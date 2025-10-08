package cymind.service;

import cymind.model.AbstractUser;
import cymind.model.JournalEntry;
import cymind.model.MoodEntry;
import cymind.model.Student;
import cymind.repository.JournalEntryRepository;
import cymind.repository.MoodEntryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MoodEntryService {
    @Autowired
    private MoodEntryRepository moodEntryRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Transactional
    public List<MoodEntry> findAllByStudent(int num) {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<MoodEntry> entries = moodEntryRepository.findAllByStudentOrderByIdDesc((Student) authedUser);
        return entries.subList(0, Math.min(num, entries.size()));
    }

    @Transactional
    public MoodEntry getMoodEntryById(long id) throws AuthorizationDeniedException, ResponseStatusException {
        MoodEntry moodEntry = moodEntryRepository.findById(id);
        if (moodEntry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }

        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != moodEntry.getStudent().getId()) {
            throw new AuthorizationDeniedException("Attempting to get a mood entry for a different user");
        }

        return moodEntry;
    }

    @Transactional
    public MoodEntry createMoodEntry(MoodEntry moodEntry) throws AuthorizationDeniedException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != moodEntry.getStudent().getId()) {
            throw new AuthorizationDeniedException("Attempting to add a mood entry for a different user");
        }

        return moodEntryRepository.save(moodEntry);
    }

    @Transactional
    public MoodEntry updateMoodEntry(long id, MoodEntry request) throws AuthorizationDeniedException, ResponseStatusException {
        MoodEntry moodEntry = moodEntryRepository.findById(id);
        if (moodEntry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }

        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != moodEntry.getStudent().getId()) {
            throw new AuthorizationDeniedException("Attempting to update a mood entry for a different user");
        }

        MoodEntry moodEntryToUpdate = moodEntryRepository.findById(id);
        moodEntryToUpdate.updateMoodRating(request);

        return moodEntryRepository.save(moodEntryToUpdate);
    }

    @Transactional
    public MoodEntry setJournalEntry(long moodId, long journalId) throws AuthorizationDeniedException {
        MoodEntry moodEntry = moodEntryRepository.findById(moodId);
        JournalEntry journalEntry = journalEntryRepository.findById(journalId);
        if (moodEntry == null || journalEntry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }

        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != moodEntry.getStudent().getId()) {
            throw new AuthorizationDeniedException("Attempting to update a mood entry for a different user");
        }

        journalEntry.setMoodEntry(moodEntry);
        journalEntryRepository.save(journalEntry);

        moodEntry.setJournalEntry(journalEntry);
        return moodEntryRepository.save(moodEntry);
    }

    @Transactional
    public void deleteMoodEntry(long id) throws AuthorizationDeniedException {
        MoodEntry moodEntry = moodEntryRepository.findById(id);
        if  (moodEntry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }

        AbstractUser abstractUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (abstractUser.getId() != moodEntry.getStudent().getId()) {
            throw new AuthorizationDeniedException("Attempting to delete a mood entry for different user");
        }

        moodEntryRepository.deleteById(id);
    }
}
