package cymind.dto.journal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotNull;

public record CreateJournalEntryDTO(@NotBlank String entryName, @NotBlank String content, @NotNull Long moodId) {}


