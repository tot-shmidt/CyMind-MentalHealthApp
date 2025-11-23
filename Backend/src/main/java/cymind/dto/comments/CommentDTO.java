package cymind.dto.comments;

import java.time.LocalDateTime;

public record CommentDTO(
        Long id,
        String content,
        String authorName,
        Long authorId,
        LocalDateTime createdAt
) {
}
