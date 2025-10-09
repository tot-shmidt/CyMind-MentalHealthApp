package cymind.dto.journal;

import cymind.model.JournalEntry;
import java.util.Date;

public record JournalEntryDTO(Long id, Date date, String entryName, String content, Long moodId) {
    public JournalEntryDTO(JournalEntry journalEntry) {
        this(
                journalEntry.getId(),
                journalEntry.getDate(),
                journalEntry.getEntryName(),
                journalEntry.getContent(),
                journalEntry.getMoodEntry().getId());
    }
}
