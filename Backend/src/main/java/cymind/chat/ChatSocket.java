package cymind.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cymind.dto.chat.MessageDTO;
import cymind.model.AbstractUser;
import cymind.model.ChatGroup;
import cymind.model.ChatMessage;
import cymind.repository.AbstractUserRepository;
import cymind.repository.ChatGroupRepository;
import cymind.repository.ChatMessageRepository;
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

    private static Map<Session, Long> sessionUserIdMap = new Hashtable<>();
    private static Map<Session, Long> sessionGroupIdMap = new Hashtable<>();
    private static Map<Long, List<Session>> groupIdSessionsMap = new Hashtable<>();

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
        AbstractUser user = abstractUserRepository.findById(userId.longValue());
        ChatGroup chatGroup = chatGroupRepository.findById(groupId.longValue());
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
    public void onMessage(Session session, MessageDTO messageDTO) {
        log.info("[onMessage] chatMessageJson: {}", messageDTO);

        Long userId = sessionUserIdMap.get(session);
        AbstractUser user = abstractUserRepository.findById(userId.longValue());
        Long groupId = sessionGroupIdMap.get(session);
        ChatGroup chatGroup = chatGroupRepository.findById(groupId.longValue());

        ChatMessage chatMessage = new ChatMessage(user, chatGroup, messageDTO.content(), messageDTO.timestamp());
        chatMessageRepository.save(chatMessage);

        sendToGroup(messageDTO, groupId);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("[onError] id: {} - {}", session.getId(), error.getMessage());
    }

    private void sendToGroup(MessageDTO messageDTO, Long groupId) {
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
}
