package cymind.dto.chat;

import cymind.model.ChatMessage;

import java.util.Date;

public record MessageDTO(long chatId, String sender, long userId, Date timestamp, String content) {
    public MessageDTO(ChatMessage message, String sender) {
        this(
                message.getId(),
                message.getSender().getFirstName() + " " + message.getSender().getLastName(),
                message.getSender().getId(),
                message.getTimestamp(),
                message.getContent()
        );
    }
}
