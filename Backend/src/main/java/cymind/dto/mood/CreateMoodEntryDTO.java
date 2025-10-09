package cymind.dto.mood;

public record CreateMoodEntryDTO(
        int moodRating,
        long userId,

        // Can be null or empty if no journal is being created
        String journalName,
        // This can be null or empty if no journal is being created
        String journalContent
) {
}
