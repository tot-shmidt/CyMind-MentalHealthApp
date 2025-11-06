package cymind.service;

import cymind.dto.chat.ChatGroupDTO;
import cymind.dto.chat.CreateChatGroupDTO;
import cymind.dto.chat.MessageDTO;
import cymind.enums.UserType;
import cymind.model.*;
import cymind.repository.ChatGroupRepository;
import cymind.repository.ChatMessageRepository;
import cymind.repository.MentalHealthProfessionalRepository;
import cymind.repository.StudentRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ChatGroupService {
    @Autowired
    ChatGroupRepository chatGroupRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MentalHealthProfessionalRepository mentalHealthProfessionalRepository;
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Transactional
    public ChatGroupDTO create(CreateChatGroupDTO chatGroupDTO) {
        List<Student> studentList = studentRepository.findAllByAbstractUserIdIn(chatGroupDTO.studentIds());
        if (studentList.isEmpty()) {
            throw new NoResultException("No specified student user are students");
        }

        List<MentalHealthProfessional> professionalList = mentalHealthProfessionalRepository.findAllByAbstractUserIdIn(chatGroupDTO.professionalIds());
        if (professionalList.isEmpty()) {
            throw new NoResultException("No specified professional users are professionals");
        }

        ChatGroup chatGroup = new ChatGroup(professionalList, studentList, chatGroupDTO.groupName());

        checkAuth(chatGroup);

        return new ChatGroupDTO(chatGroupRepository.save(chatGroup));
    }

    @Transactional
    public List<ChatGroupDTO> getByUserPrincipal() {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getUserType() != UserType.PROFESSIONAL && authedUser.getUserType() != UserType.STUDENT) {
            throw new AuthorizationDeniedException("Invalid user type");
        }

        return chatGroupRepository.findAllByUserId(authedUser.getId()).stream()
                .map(ChatGroupDTO::new)
                .toList();
    }

    @Transactional
    public ChatGroupDTO get(long id) {
        ChatGroup chatGroup = chatGroupRepository.findById(id);
        if (chatGroup == null) {
            throw new NoResultException("No chat group found");
        }
        checkAuth(chatGroup);

        return new ChatGroupDTO(chatGroup);
    }

    @Transactional
    public MessageDTO getMessageByMessageId(long groupId, long messageId) {
        ChatGroup chatGroup = chatGroupRepository.findById(groupId);
        if (chatGroup == null) {
            throw new NoResultException("No chat group found");
        }
        checkAuth(chatGroup);

        ChatMessage chatMessage = chatMessageRepository.findById(messageId);
        if (chatMessage == null) {
            throw new NoResultException("No message found");
        }

        if (chatGroup.getChatMessages() == null || !chatGroup.getChatMessages().contains(chatMessage)) {
            throw new AuthorizationDeniedException("Attempting to access a message using a different group");
        }

        return new MessageDTO(chatMessage);
    }

    @Transactional
    public List<MessageDTO> getMessages(long groupId, String search) {
        ChatGroup chatGroup = chatGroupRepository.findById(groupId);
        if (chatGroup == null) {
            throw new NoResultException("No chat group found");
        }
        checkAuth(chatGroup);

        if (search != null && !search.isEmpty()) {
            return chatMessageRepository.findAllByChatGroup_IdAndContentContainsOrderByTimestampDesc(groupId, search).stream()
                    .map(MessageDTO::new)
                    .toList();
        } else {
            return chatMessageRepository.findAllByChatGroup_IdOrderByTimestampDesc(groupId).stream()
                    .map(MessageDTO::new)
                    .toList();
        }
    }

    @Transactional
    public ChatGroupDTO update(long id, CreateChatGroupDTO chatGroupDTO) {
        ChatGroup chatGroup = chatGroupRepository.findById(id);
        if (chatGroup == null) {
            throw new NoResultException("No such chat group");
        }
        checkAuth(chatGroup);

        List<Student> studentList = studentRepository.findAllByAbstractUserIdIn(chatGroupDTO.studentIds());
        if (studentList.isEmpty()) {
            throw new NoResultException("No specified student user are students");
        }

        List<MentalHealthProfessional> professionalList = mentalHealthProfessionalRepository.findAllByAbstractUserIdIn(chatGroupDTO.professionalIds());
        if (professionalList.isEmpty()) {
            throw new NoResultException("No specified professional users are professionals");
        }

        chatGroup.setStudents(studentList);
        chatGroup.setProfessionals(professionalList);
        chatGroup.setGroupName(chatGroupDTO.groupName());

        return new ChatGroupDTO(chatGroupRepository.save(chatGroup));
    }

    @Transactional
    public void delete(long id) {
        ChatGroup chatGroup = chatGroupRepository.findById(id);
        if (chatGroup == null) {
            throw new NoResultException("No such chat group");
        }
        checkAuth(chatGroup);

        chatGroupRepository.deleteById(id);
    }

    private void checkAuth(ChatGroup chatGroup) {
        Object o = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(o instanceof AbstractUser authedUser)) {
            throw new AuthorizationDeniedException("Invalid user");
        }

        if (!chatGroup.containsUser(authedUser)) {
            throw new AuthorizationDeniedException("Attempting to access a group without user");
        }
    }

}
