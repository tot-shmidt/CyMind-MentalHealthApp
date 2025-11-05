package cymind.dto.chat;

import java.time.LocalDateTime;

public record MessageDTO(Long groupId, Long senderId, String content, LocalDateTime timestamp) {
}
