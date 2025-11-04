package cymind.service;

import cymind.dto.chat.ChatGroupDTO;
import cymind.dto.chat.CreateChatGroupDTO;
import cymind.enums.UserType;
import cymind.model.AbstractUser;
import cymind.model.ChatGroup;
import cymind.model.MentalHealthProfessional;
import cymind.model.Student;
import cymind.repository.ChatGroupRepository;
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
        if (!chatGroupRepository.existsById(id)) {
            throw new NoResultException("No such chat group");
        }

        return new ChatGroupDTO(chatGroupRepository.findById(id));
    }

    @Transactional
    public ChatGroupDTO update(long id, CreateChatGroupDTO chatGroupDTO) {
        ChatGroup chatGroup = chatGroupRepository.findById(id);
        if (chatGroup == null) {
            throw new NoResultException("No such chat group");
        }

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
        if (!chatGroupRepository.existsById(id)) {
            throw new NoResultException("No such chat group");
        }

        chatGroupRepository.deleteById(id);
    }
}
