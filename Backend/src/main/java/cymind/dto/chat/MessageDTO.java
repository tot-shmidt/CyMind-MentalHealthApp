package cymind.dto.chat;

import cymind.enums.MessageType;
import cymind.model.ChatMessage;

import java.time.LocalDateTime;

public record MessageDTO(Long messageId, Long senderId, String name, String content, LocalDateTime timestamp, MessageType messageType) {
    public MessageDTO(ChatMessage message, String name) {
        this(
                message.getId(),
                message.getSender().getId(),
                name,
                message.getContent(),
                message.getTimestamp(),
                MessageType.MESSAGE
        );
    }
}
