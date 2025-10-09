package cymind.controller;

import org.springframework.web.bind.annotation.*;
import cymind.dto.journal.CreateJournalEntryDTO;
import cymind.dto.journal.JournalEntryDTO;
import cymind.service.JournalEntryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/entries/journal")
public class JournalEntryController {

    @Autowired
    private JournalEntryService journalEntryService;

    /**
     * Create a new journal entry
     * @param createDto
     */
    @PostMapping("/entries/journal")
    public ResponseEntity<JournalEntryDTO> createJournalEntry(@Valid @RequestBody CreateJournalEntryDTO createDto) {
        JournalEntryDTO newJournalEntry = journalEntryService.createJournalEntry(createDto);
        return new ResponseEntity<>(newJournalEntry, HttpStatus.CREATED);
    }

    /**
     * Get a journal entry by its id
     * @param id
     */
    @GetMapping("/entries/journal/{id}")
    public ResponseEntity<JournalEntryDTO> getJournalEntryById(@PathVariable long id) {
        JournalEntryDTO journalEntry = journalEntryService.getJournalEntryById(id);
        return ResponseEntity.ok(journalEntry);
    }

    /**
     * Get all journal entries for current user
     */
    @GetMapping("/entries/journal")
    public ResponseEntity<List<JournalEntryDTO>> getAllJournalEntries() {
        List<JournalEntryDTO> entries = journalEntryService.getAllJournalEntriesForUser();
        return ResponseEntity.ok(entries);
    }

    /**
     * Updates an existing journal entry.
     */
    @PutMapping("/entries/journal/{id}")
    public ResponseEntity<JournalEntryDTO> updateJournalEntry(@PathVariable long id, @Valid @RequestBody CreateJournalEntryDTO updateDto) {
        JournalEntryDTO updatedJournalEntry = journalEntryService.updateJournalEntry(id, updateDto);
        return ResponseEntity.ok(updatedJournalEntry);
    }

    /**
     * Deletes a journal entry.
     */
    @DeleteMapping("/entries/journal/{id}")
    public ResponseEntity<?> deleteJournalEntry(@PathVariable long id) {
        journalEntryService.deleteJournalEntry(id);
        return ResponseEntity.noContent().build();
    }
}
