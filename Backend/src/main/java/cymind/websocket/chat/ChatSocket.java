package cymind.websocket.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cymind.dto.chat.MessageDTO;
import cymind.enums.MessageType;
import cymind.model.AbstractUser;
import cymind.model.ChatGroup;
import cymind.model.ChatMessage;
import cymind.repository.AbstractUserRepository;
import cymind.repository.ChatGroupRepository;
import cymind.repository.ChatMessageRepository;
import cymind.service.ChatGroupService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@ServerEndpoint(value = "/chat/{groupId}/{userId}", decoders = {MessageDecoder.class}, encoders = {MessageEncoder.class})
public class ChatSocket {

    private static AbstractUserRepository abstractUserRepository;
    private static ChatGroupRepository chatGroupRepository;
    private static ChatMessageRepository chatMessageRepository;

    private static final Map<Session, Long> sessionUserIdMap = new Hashtable<>();
    private static final Map<Session, Long> sessionGroupIdMap = new Hashtable<>();
    private static final Map<Long, List<Session>> groupIdSessionsMap = new Hashtable<>();

    @Autowired
    public void setAbstractUserRepository(AbstractUserRepository repo) {
        abstractUserRepository = repo;
    }

    @Autowired
    public void setChatGroupRepository(ChatGroupRepository repo) {
        chatGroupRepository = repo;
    }

    @Autowired
    public void setChatMessageRepository(ChatMessageRepository repo) {
        chatMessageRepository = repo;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("groupId") Long groupId, @PathParam("userId") Long userId) throws EncodeException, IOException {
        ChatGroup chatGroup = chatGroupRepository.findById(groupId.longValue());
        if (chatGroup == null) {
            sendError(session, "Group not found");
            session.close();
            return;
        }

        AbstractUser user = abstractUserRepository.findById(userId.longValue());
        if (user == null) {
            sendError(session, "User not found");
            session.close();
            return;
        }

        if (!chatGroup.containsUser(user)) {
            sendError(session, "User not part of group");
            session.close();
            return;
        };

        log.info("[onOpen] groupId: {} groupName: {} userId: {} name: {} {}", groupId, chatGroup.getGroupName(), userId, user.getFirstName(), user.getLastName());

        sessionUserIdMap.put(session, userId);
        sessionGroupIdMap.put(session, chatGroup.getId());

        List<Session> groupSession = groupIdSessionsMap.get(groupId);
        if (groupSession == null) {
            groupSession = new ArrayList<>();
            groupSession.add(session);
            groupIdSessionsMap.put(groupId, groupSession);
        } else {
            groupSession.add(session);
        }

        sendGroupHistory(session);
    }

    @OnClose
    public void onClose(Session session) {
        Long userId = sessionUserIdMap.get(session);
        log.info("[onClose] Removing: {}", userId);

        sessionUserIdMap.remove(session);
        Long groupId = sessionGroupIdMap.remove(session);
        List<Session> groupSession = groupIdSessionsMap.get(groupId);
        if (groupSession != null) {
            groupSession.remove(session);
        }
    }

    @OnMessage
    public void onMessage(Session session, MessageDTO messageDTO) throws EncodeException, IOException {
        log.info("[onMessage] chatMessageJson: {}", messageDTO);

        Long groupId = sessionGroupIdMap.get(session);
        Long userId = sessionUserIdMap.get(session);
        if (userId != messageDTO.senderId()) {
            sendError(session, "Sender id does not match sender");
            return;
        }

        MessageDTO response = null;
        if (messageDTO.messageType() == MessageType.MESSAGE) {
            AbstractUser user = abstractUserRepository.findById(userId.longValue());
            ChatGroup chatGroup = chatGroupRepository.findById(groupId.longValue());

            ChatMessage chatMessage = new ChatMessage(user, chatGroup, messageDTO.content(), messageDTO.timestamp());
            response = new MessageDTO(chatMessageRepository.save(chatMessage));
        } else if (messageDTO.messageType() == MessageType.DELETE) {
            ChatMessage chatMessage = chatMessageRepository.findById(messageDTO.messageId().longValue());
            if (chatMessage == null) {
                sendError(session, "Message not found");
                return;
            }
            if (userId != chatMessage.getSender().getId()) {
                sendError(session, "Current id does not match sender");
                return;
            }

            chatMessageRepository.deleteById(messageDTO.messageId());
            response = new MessageDTO(messageDTO.messageId(), messageDTO.senderId(), null, messageDTO.timestamp(), MessageType.DELETE);
        } else if (messageDTO.messageType() == MessageType.EDIT) {
            ChatMessage chatMessage = chatMessageRepository.findById(messageDTO.messageId().longValue());
            if (chatMessage == null) {
                sendError(session, "Message not found");
                return;
            }
            if (userId != chatMessage.getSender().getId()) {
                sendError(session, "Current id does not match sender");
                return;
            }

            chatMessage.setContent(messageDTO.content());
            chatMessageRepository.save(chatMessage);

            response = new MessageDTO(messageDTO.messageId(), messageDTO.senderId(), messageDTO.content(), messageDTO.timestamp(), MessageType.EDIT);
        } else {
            sendError(session, "Invalid message type");
        }

        sendToGroup(response, groupId);
    }

    @OnError
    public void onError(Session session, Throwable error) throws EncodeException, IOException {
        sendError(session, error.getMessage());
        log.error("[onError] id: {} - {}", session.getId(), error.getMessage());
    }

    private void sendToGroup(MessageDTO messageDTO, Long groupId) {
        if (messageDTO == null) {
            return;
        }

        groupIdSessionsMap.get(groupId).forEach(session -> {
            try {
                session.getBasicRemote().sendObject(messageDTO);
            } catch (IOException | EncodeException e) {
                log.error("[sendToGroup] id: {} - {}", session.getId(), e.getMessage());
            }
        });
    }

    private void sendGroupHistory(Session session) throws IOException {
        Long groupId = sessionGroupIdMap.get(session);
        List<MessageDTO> messages = chatMessageRepository.findAllByChatGroup_IdOrderByTimestampDesc(groupId).stream()
                .map(MessageDTO::new)
                .toList();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        session.getBasicRemote().sendText(mapper.writeValueAsString(messages));
    }

    private void sendError(Session session, String message) throws EncodeException, IOException {
        session.getBasicRemote().sendObject(new MessageDTO(null, null, message, null, MessageType.ERROR));
    }
}
