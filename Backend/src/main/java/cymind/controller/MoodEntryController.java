package cymind.controller;

import cymind.dto.mood.CreateMoodEntryDTO;
import cymind.dto.mood.MoodEntryDTO;
import cymind.model.MoodEntry;
import cymind.service.MoodEntryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class MoodEntryController {
    @Autowired
    private MoodEntryService moodEntryService;

    @GetMapping(path = "/entries/mood")
    ResponseEntity<List<MoodEntryDTO>> getAllMoodEntries() {
        List<MoodEntryDTO> entries = moodEntryService.findAllByStudent();
        return new ResponseEntity<>(entries, HttpStatus.OK);
    }

    @GetMapping(path = "/entries/mood", params = "num")
    ResponseEntity<List<MoodEntryDTO>> getAllMoodEntries(@RequestParam(required = false, defaultValue = "1") Optional<Integer> num) {
        List<MoodEntryDTO> entries;
        if (num.isPresent()) {
            entries = moodEntryService.findAllByStudent(num.get());
        } else {
            entries = moodEntryService.findAllByStudent(1);
        }

        return new ResponseEntity<>(entries, HttpStatus.OK);
    }

    @GetMapping(path = "/entries/mood/{id}")
    ResponseEntity<MoodEntryDTO> getMoodEntryById(@PathVariable long id) {
        return new ResponseEntity<>(moodEntryService.getMoodEntryById(id), HttpStatus.OK);
    }

    @PostMapping(path = "/entries/mood")
    ResponseEntity<MoodEntryDTO> createMoodEntry(@Valid @RequestBody CreateMoodEntryDTO createMoodEntryDTO) {
        MoodEntryDTO newUser = moodEntryService.createMoodEntry(createMoodEntryDTO);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PutMapping("/entries/mood/{id}")
    ResponseEntity<MoodEntryDTO> updateMoodEntry(@PathVariable long id, @Valid @RequestBody MoodEntry moodEntry) {
        MoodEntryDTO updatedUser = moodEntryService.updateMoodEntry(id, moodEntry);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PutMapping("/entries/mood/{moodId}/journal/{journalId}")
    ResponseEntity<MoodEntryDTO> assignJournalEntryToMoodEntry(@PathVariable long moodId, @PathVariable long journalId) {
        MoodEntryDTO updatedUser = moodEntryService.setJournalEntry(moodId, journalId);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @DeleteMapping(path = "/entries/mood/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    ResponseEntity<?> deleteMoodEntry(@PathVariable long id) {
        moodEntryService.deleteMoodEntry(id);
        return ResponseEntity.ok().build();
    }
}
