package cymind.service;

import cymind.dto.journal.CreateJournalEntryDTO;
import cymind.dto.journal.JournalEntryDTO;
import cymind.exceptions.AuthorizationDeniedException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private MoodEntryRepository moodEntryRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public JournalEntryDTO createJournalEntry(CreateJournalEntryDTO dto) throws AuthorizationDeniedException, NoResultException, ResponseStatusException {
        MoodEntry moodEntry = moodEntryRepository.findById(dto.moodId().longValue());
        if (moodEntry == null) {
            throw new NoResultException("Mood entry not found");
        }

        checkAuth(moodEntry);

        if (moodEntry.getJournalEntry() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This mood entry is already linked with a journal entry.");
        }

        JournalEntry journalEntry = new JournalEntry(dto.entryName(), dto.content(), moodEntry);
        JournalEntry savedJournalEntry = journalEntryRepository.save(journalEntry);

        moodEntry.setJournalEntry(savedJournalEntry);
        moodEntryRepository.save(moodEntry);

        return new JournalEntryDTO(savedJournalEntry);
    }

    @Transactional
    public JournalEntryDTO getJournalEntryById(long id) throws AuthorizationDeniedException, NoResultException {
        JournalEntry journalEntry = journalEntryRepository.findById(id);
        if (journalEntry == null) {
            throw new NoResultException("Journal entry not found");
        }

        checkAuth(journalEntry.getMoodEntry());

        return new JournalEntryDTO(journalEntry);
    }

    @Transactional
    public List<JournalEntryDTO> getAllJournalEntriesForUser() throws NoResultException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student authedStudent = studentRepository.findByAbstractUserId(authedUser.getId());
        if (authedStudent == null) {
            throw new NoResultException("Requested user is not a student");
        }

        List<MoodEntry> moodEntries = moodEntryRepository.findAllByStudentOrderByIdDesc(authedStudent);
        List<JournalEntryDTO> journalEntryDTOs = new ArrayList<>();

        for (MoodEntry moodEntry : moodEntries) {
            if (moodEntry.getJournalEntry() != null) {
                journalEntryDTOs.add(new JournalEntryDTO(moodEntry.getJournalEntry()));
            }
        }

        return journalEntryDTOs;
    }

    @Transactional
    public JournalEntryDTO updateJournalEntry(long id, CreateJournalEntryDTO dto) throws AuthorizationDeniedException, NoResultException {
        JournalEntry journalEntry = journalEntryRepository.findById(id);
        if (journalEntry == null) {
            throw new NoResultException("Journal entry not found");
        }

        checkAuth(journalEntry.getMoodEntry());

        journalEntry.setEntryName(dto.entryName());
        journalEntry.setContent(dto.content());

        JournalEntry updatedJournalEntry = journalEntryRepository.save(journalEntry);

        return new JournalEntryDTO(updatedJournalEntry);
    }

    @Transactional
    public void deleteJournalEntry(long id) throws AuthorizationDeniedException, NoResultException {
        JournalEntry journalEntry = journalEntryRepository.findById(id);
        if (journalEntry == null) {
            throw new NoResultException("Journal entry not found");
        }

        checkAuth(journalEntry.getMoodEntry());

        MoodEntry moodEntry = journalEntry.getMoodEntry();
        moodEntry.setJournalEntry(null);
        moodEntryRepository.save(moodEntry);

        journalEntryRepository.deleteById(id);
    }

    private void checkAuth(MoodEntry moodEntry) throws AuthorizationDeniedException {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != moodEntry.getStudent().getAbstractUser().getId()) {
            throw new AuthorizationDeniedException("Attempting to access a journal entry for different user");
        }
    }
}