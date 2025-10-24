package cymind.service;

import cymind.dto.mood.CreateMoodEntryDTO;
import cymind.dto.mood.MoodEntryDTO;
import cymind.model.AbstractUser;
import cymind.model.JournalEntry;
import cymind.model.MoodEntry;
import cymind.model.Student;
import cymind.repository.JournalEntryRepository;
import cymind.repository.MoodEntryRepository;
import cymind.repository.StudentRepository;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class MoodEntryService {
    @Autowired
    private MoodEntryRepository moodEntryRepository;

    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public List<MoodEntryDTO> findAllByStudent() throws NoResultException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student authedStudent = studentRepository.findByAbstractUserId(authedUser.getId());
        if (authedStudent == null) {
            throw new NoResultException("Requested user is not a student");
        }

        return moodEntryRepository.findAllByStudentOrderByIdDesc(authedStudent).stream().map(MoodEntryDTO::new).toList();
    }

    @Transactional
    public List<MoodEntryDTO> findAllByStudent(int num) throws NoResultException {
        List<MoodEntryDTO> entries = findAllByStudent();
        return entries.subList(0, Math.min(num, entries.size()));
    }

    @Transactional
    public MoodEntryDTO getMoodEntryById(long id) throws AuthorizationDeniedException, NoResultException {
        MoodEntry moodEntry = moodEntryRepository.findById(id);
        if (moodEntry == null) {
            throw new NoResultException("Mood entry not found");
        }

        checkAuth(moodEntry);

        return new MoodEntryDTO(moodEntry);
    }

    @Transactional
    public MoodEntryDTO createMoodEntry(CreateMoodEntryDTO createMoodEntryDTO) throws AuthorizationDeniedException, NoResultException {
        Student student = studentRepository.findByAbstractUserId(createMoodEntryDTO.userId());
        if (student == null) {
            throw new NoResultException("Requested user is not a student");
        }

        // Create and save the MoodEntry first, so we get an ID.
        MoodEntry moodEntry = new MoodEntry(createMoodEntryDTO.moodRating(), student);
        checkAuth(moodEntry);

        MoodEntry savedMoodEntry = moodEntryRepository.save(moodEntry);

        // Check if journal content was provided in the request.
        String journalName = createMoodEntryDTO.journalName();
        String journalContent = createMoodEntryDTO.journalContent();

        if (journalContent != null && !journalContent.isBlank()) {
            // If yes, create a new JournalEntry.
            JournalEntry journalEntry = new JournalEntry();
            // Use the provided name or a default if it's blank
            journalEntry.setEntryName(journalName != null && !journalName.isBlank() ? journalName : "Journal Entry");
            journalEntry.setContent(journalContent);
            journalEntry.setDate(new Date());
            journalEntry.setMoodEntry(savedMoodEntry); // Link it to the mood we just saved.
            JournalEntry savedJournalEntry = journalEntryRepository.save(journalEntry);

            // Link the new journal back to the mood entry and save again.
            savedMoodEntry.setJournalEntry(savedJournalEntry);
            moodEntryRepository.save(savedMoodEntry);
        }

        // Return the DTO for the mood entry, which will now include the new journalId if it was created.
        return new MoodEntryDTO(savedMoodEntry);
    }

    @Transactional
    public MoodEntryDTO updateMoodEntry(long id, CreateMoodEntryDTO request) throws AuthorizationDeniedException, NoResultException {
        MoodEntry moodEntry = moodEntryRepository.findById(id);
        if (moodEntry == null) {
            throw new NoResultException("Mood entry not found");
        }

        checkAuth(moodEntry);

        moodEntry.setMoodRating(request.moodRating());

        return new MoodEntryDTO(moodEntryRepository.save(moodEntry));
    }

    @Transactional
    public MoodEntryDTO setJournalEntry(long moodId, long journalId) throws AuthorizationDeniedException, NoResultException {
        MoodEntry moodEntry = moodEntryRepository.findById(moodId);
        if (moodEntry == null) {
            throw new NoResultException("Mood entry not found");
        }

        JournalEntry journalEntry = journalEntryRepository.findById(journalId);
        if (journalEntry == null) {
            throw new NoResultException("Journal entry not found");
        }

        checkAuth(moodEntry);

        journalEntry.setMoodEntry(moodEntry);
        journalEntryRepository.save(journalEntry);

        moodEntry.setJournalEntry(journalEntry);
        return new MoodEntryDTO(moodEntryRepository.save(moodEntry));
    }

    @Transactional
    public void deleteMoodEntry(long id) throws AuthorizationDeniedException, NoResultException {
        MoodEntry moodEntry = moodEntryRepository.findById(id);
        if  (moodEntry == null) {
            throw new NoResultException("Mood entry not found");
        }

        checkAuth(moodEntry);

        moodEntryRepository.deleteById(id);
    }

    private void checkAuth(MoodEntry moodEntry) throws AuthorizationDeniedException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != moodEntry.getStudent().getAbstractUser().getId()) {
            throw new AuthorizationDeniedException("Attempting to access a mood entry for different user");
        }
    }
}
