package cymind.controller;

import cymind.model.MoodEntry;
import cymind.service.MoodEntryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MoodEntryController {
    @Autowired
    private MoodEntryService moodEntryService;

    @GetMapping(path = "/entries/mood", params = "{num}")
    ResponseEntity<List<MoodEntry>> getAllMoodEntries(@RequestParam int num) {
        List<MoodEntry> entries;
        if (num <= 0) {
             entries = moodEntryService.findAllByStudent(1);
        } else {
            entries = moodEntryService.findAllByStudent(num);
        }

        return new ResponseEntity<>(entries, HttpStatus.OK);
    }

    @GetMapping(path = "/entries/mood/{id}")
    MoodEntry getMoodEntryById(@PathVariable long id) {
        return moodEntryService.getMoodEntryById(id);
    }

    @PostMapping(path = "/entries/mood")
    ResponseEntity<MoodEntry> createMoodEntry(@Valid @RequestBody MoodEntry moodEntry) {
        MoodEntry newUser = moodEntryService.createMoodEntry(moodEntry);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/entries/mood/{id}")
    ResponseEntity<MoodEntry> updateMoodEntry(@PathVariable long id, @Valid @RequestBody MoodEntry moodEntry) {
        MoodEntry updatedUser = moodEntryService.updateMoodEntry(id, moodEntry);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PutMapping("/entries/mood/{moodId}/journal/{journalId}")
    ResponseEntity<MoodEntry> assignJournalEntryToMoodEntry(@PathVariable long moodId, @PathVariable long journalId) {
        MoodEntry updatedUser = moodEntryService.setJournalEntry(moodId, journalId);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping(path = "/entries/mood/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<?> deleteMoodEntry(@PathVariable long id) {
        moodEntryService.deleteMoodEntry(id);
        return ResponseEntity.ok().build();
    }
}
