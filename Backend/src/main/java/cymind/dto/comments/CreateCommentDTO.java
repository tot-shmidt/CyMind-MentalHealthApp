package cymind.dto.comments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCommentDTO(
        @NotBlank String content,
        @NotNull Long userId
) {
}
