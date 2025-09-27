package cymind.controller;

import cymind.model.JournalEntry;
import cymind.model.MoodEntry;
import cymind.repository.JournalEntryRepository;
import cymind.repository.MoodEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class MoodEntryController {
    @Autowired
    private MoodEntryRepository moodEntryRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @GetMapping(path = "/entries/mood")
    List<MoodEntry> getAllMoodEntries() {
        return moodEntryRepository.findAll();
    }

    @GetMapping(path = "/entries/mood/{id}")
    MoodEntry getMoodEntryById(@PathVariable long id) {
        return moodEntryRepository.findById(id);
    }

    @PostMapping(path = "/entries/mood")
    @ResponseStatus(HttpStatus.CREATED)
    MoodEntry createMoodEntry(@RequestBody MoodEntry moodEntry) {
        if (moodEntry == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mood entry is invalid");
        moodEntryRepository.save(moodEntry);
        return moodEntry;
    }

    @PutMapping("/entries/mood/{id}")
    MoodEntry updateMoodEntry(@PathVariable long id, @RequestBody MoodEntry moodEntry) {
        MoodEntry entry = moodEntryRepository.findById(id);
        if (entry == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mood entry does not exist");
        moodEntryRepository.save(moodEntry);
        return moodEntryRepository.findById(id);
    }

    @PutMapping("/entries/mood/{moodId}/journal/{journalId}")
    MoodEntry assignJournalEntryToMoodEntry(@PathVariable long moodId, @PathVariable long journalId) {
        MoodEntry moodEntry = moodEntryRepository.findById(moodId);
        JournalEntry journalEntry = journalEntryRepository.findById(journalId);
        if (moodEntry == null || journalEntry == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entry does not exist");
        journalEntry.setMoodEntry(moodEntry);
        moodEntry.addJournalEntry(journalEntry);
        moodEntryRepository.save(moodEntry);
        return moodEntryRepository.findById(moodId);
    }

    @DeleteMapping(path = "/entries/mood/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteMoodEntry(@PathVariable long id) {
        moodEntryRepository.deleteById(id);
    }
}
