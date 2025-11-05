package cymind.dto.chat;

import cymind.model.ChatMessage;

import java.time.LocalDateTime;

public record MessageDTO(Long groupId, Long senderId, String content, LocalDateTime timestamp) {
    public MessageDTO(ChatMessage message) {
        this(
                message.getChatGroup().getId(),
                message.getSender().getId(),
                message.getContent(),
                message.getTimestamp()
        );
    }
}
