package cymind.dto.mood;

public record CreateMoodEntryDTO(int moodRating, long userId, Long journalId) {
}
