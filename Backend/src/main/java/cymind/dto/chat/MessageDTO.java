package cymind.dto.chat;

import cymind.enums.MessageType;
import cymind.model.ChatMessage;

import java.time.LocalDateTime;

public record MessageDTO(Long chatId, Long senderId, String content, LocalDateTime timestamp, MessageType messageType) {
    public MessageDTO(ChatMessage message) {
        this(
                message.getId(),
                message.getSender().getId(),
                message.getContent(),
                message.getTimestamp(),
                MessageType.MESSAGE
        );
    }
}
