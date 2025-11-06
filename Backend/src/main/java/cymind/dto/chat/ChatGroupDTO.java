package cymind.dto.chat;

import cymind.dto.appointment.AppointmentDTO;
import cymind.model.ChatGroup;
import cymind.model.ChatMessage;

import java.util.Date;
import java.util.List;

public record ChatGroupDTO(Long id, List<Long> professionalIds, List<Long> studentIds, String groupName, List<Long> messageIds, Date createdOn) {
    public ChatGroupDTO(ChatGroup group) {
        this(
                group.getId(),
                // List of MentalHealthProfessionals -> List of AbstractUser ids
                group.getProfessionals().stream().map(p -> p.getAbstractUser().getId()).toList(),
                // List of Students -> List of AbstractUser ids
                group.getStudents().stream().map(s -> s.getAbstractUser().getId()).toList(),
                group.getGroupName(),
                // If messages are not null, List of ChatMessages -> List of ids
                group.getChatMessages() != null ? group.getChatMessages().stream().map(ChatMessage::getId).toList() : null,
                group.getCreatedOn()
        );
    }
}
