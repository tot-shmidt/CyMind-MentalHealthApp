package cymind.service;

import cymind.model.AbstractUser;
import cymind.model.JournalEntry;
import cymind.model.MoodEntry;
import cymind.model.Student;
import cymind.repository.JournalEntryRepository;
import cymind.repository.MoodEntryRepository;
import cymind.repository.StudentRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public List<MoodEntry> findAllByStudent(int num) {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student authedStudent = studentRepository.findByAbstractUser(authedUser);
        if (authedStudent == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No entries found");
        }

        List<MoodEntry> entries = moodEntryRepository.findAllByStudentOrderByIdDesc(authedStudent);
        return entries.subList(0, Math.min(num, entries.size()));
    }

    @Transactional
    public MoodEntry getMoodEntryById(long id) throws AuthorizationDeniedException, ResponseStatusException {
        MoodEntry moodEntry = moodEntryRepository.findById(id);
        if (moodEntry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }

        checkAuth(moodEntry);

        return moodEntry;
    }

    @Transactional
    public MoodEntry createMoodEntry(MoodEntry moodEntry) throws AuthorizationDeniedException {
        checkAuth(moodEntry);

        return moodEntryRepository.save(moodEntry);
    }

    @Transactional
    public MoodEntry updateMoodEntry(long id, MoodEntry request) throws AuthorizationDeniedException, ResponseStatusException {
        MoodEntry moodEntry = moodEntryRepository.findById(id);
        if (moodEntry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }

        checkAuth(moodEntry);

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

        checkAuth(moodEntry);

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

        checkAuth(moodEntry);

        moodEntryRepository.deleteById(id);
    }

    private void checkAuth(MoodEntry moodEntry) {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != moodEntry.getStudent().getAbstractUser().getId()) {
            throw new AuthorizationDeniedException("Attempting to access a mood entry for different user");
        }
    }
}
