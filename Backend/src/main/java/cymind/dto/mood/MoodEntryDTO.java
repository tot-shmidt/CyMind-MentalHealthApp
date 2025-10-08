package cymind.dto.mood;

import cymind.model.MoodEntry;

import java.util.Date;

public record MoodEntryDTO(long id, Date date, int moodRating, long userId, Long journalId) {
    public MoodEntryDTO(MoodEntry moodEntry) {
        this(
                moodEntry.getId(),
                moodEntry.getDate(),
                moodEntry.getMoodRating(),
                moodEntry.getStudent().getAbstractUser().getId(),
                moodEntry.getJournalEntry() != null ? moodEntry.getJournalEntry().getId() : null
        );
    }
}
