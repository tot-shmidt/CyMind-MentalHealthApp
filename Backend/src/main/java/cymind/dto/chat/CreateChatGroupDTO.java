package cymind.dto.chat;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateChatGroupDTO(@NotNull List<Long> professionalIds, @NotNull List<Long> studentIds, String groupName) {
}