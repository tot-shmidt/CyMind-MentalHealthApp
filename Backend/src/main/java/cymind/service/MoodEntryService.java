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
    public List<MoodEntryDTO> findAllByStudent() throws ResponseStatusException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student authedStudent = studentRepository.findByAbstractUserId(authedUser.getId());
        if (authedStudent == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No entries found");
        }

        return moodEntryRepository.findAllByStudentOrderByIdDesc(authedStudent).stream().map(MoodEntryDTO::new).toList();
    }

    @Transactional
    public List<MoodEntryDTO> findAllByStudent(int num) throws ResponseStatusException {
        List<MoodEntryDTO> entries = findAllByStudent();
        return entries.subList(0, Math.min(num, entries.size()));
    }

    @Transactional
    public MoodEntryDTO getMoodEntryById(long id) throws AuthorizationDeniedException, ResponseStatusException {
        MoodEntry moodEntry = moodEntryRepository.findById(id);
        if (moodEntry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }

        checkAuth(moodEntry);

        return new MoodEntryDTO(moodEntry);
    }

    @Transactional
    public MoodEntryDTO createMoodEntry(CreateMoodEntryDTO createMoodEntryDTO) throws AuthorizationDeniedException {
        Student student = studentRepository.findByAbstractUserId(createMoodEntryDTO.userId());
        if (student == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }

        // Create and save the MoodEntry first, so we get an ID.
        MoodEntry moodEntry = new MoodEntry(createMoodEntryDTO.moodRating(), student);
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
    public MoodEntryDTO updateMoodEntry(long id, CreateMoodEntryDTO request) throws AuthorizationDeniedException, ResponseStatusException {
        MoodEntry moodEntry = moodEntryRepository.findById(id);
        if (moodEntry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }

        checkAuth(moodEntry);

        moodEntry.setMoodRating(request.moodRating());

        return new MoodEntryDTO(moodEntryRepository.save(moodEntry));
    }

    @Transactional
    public MoodEntryDTO setJournalEntry(long moodId, long journalId) throws AuthorizationDeniedException {
        MoodEntry moodEntry = moodEntryRepository.findById(moodId);
        JournalEntry journalEntry = journalEntryRepository.findById(journalId);
        if (moodEntry == null || journalEntry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Entry not found");
        }

        checkAuth(moodEntry);

        journalEntry.setMoodEntry(moodEntry);
        journalEntryRepository.save(journalEntry);

        moodEntry.setJournalEntry(journalEntry);
        return new MoodEntryDTO(moodEntryRepository.save(moodEntry));
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
