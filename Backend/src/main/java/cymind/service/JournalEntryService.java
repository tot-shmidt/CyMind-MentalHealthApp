package cymind.service;

import cymind.dto.journal.CreateJournalEntryDTO;
import cymind.dto.journal.JournalEntryDTO;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private MoodEntryRepository moodEntryRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Transactional
    public JournalEntryDTO createJournalEntry(CreateJournalEntryDTO dto) {
        MoodEntry moodEntry = moodEntryRepository.findById(dto.moodId().longValue());
        if (moodEntry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mood entry not found");
        }

        checkAuth(moodEntry);

        if (moodEntry.getJournalEntry() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This mood entry is already linked with a journal entry.");
        }

        JournalEntry journalEntry = new JournalEntry();
        journalEntry.setEntryName(dto.entryName());
        journalEntry.setContent(dto.content());
        journalEntry.setDate(new Date());
        journalEntry.setMoodEntry(moodEntry);
        JournalEntry savedJournalEntry = journalEntryRepository.save(journalEntry);

        moodEntry.setJournalEntry(savedJournalEntry);
        moodEntryRepository.save(moodEntry);

        return new JournalEntryDTO(savedJournalEntry);
    }

    @Transactional
    public JournalEntryDTO getJournalEntryById(long id) {
        JournalEntry journalEntry = journalEntryRepository.findById(id);
        if (journalEntry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Journal entry was not found");
        }

        checkAuth(journalEntry.getMoodEntry());

        return new JournalEntryDTO(journalEntry);
    }

    @Transactional
    public List<JournalEntryDTO> getAllJournalEntriesForUser() {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Student authedStudent = studentRepository.findByAbstractUserId(authedUser.getId());

        if (authedStudent == null) {
            return new ArrayList<>();
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
    public JournalEntryDTO updateJournalEntry(long id, CreateJournalEntryDTO dto) {
        JournalEntry journalEntry = journalEntryRepository.findById(id);
        if (journalEntry == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Journal entry not found");
        }

        checkAuth(journalEntry.getMoodEntry());

        journalEntry.setEntryName(dto.entryName());
        journalEntry.setContent(dto.content());

        JournalEntry updatedJournalEntry = journalEntryRepository.save(journalEntry);

        return new JournalEntryDTO(updatedJournalEntry);
    }

    @Transactional
    public void deleteJournalEntry(long id) {
        JournalEntry journalEntry = journalEntryRepository.findById(id);

        if (journalEntry != null) {
            checkAuth(journalEntry.getMoodEntry());

            MoodEntry moodEntry = journalEntry.getMoodEntry();
            moodEntry.setJournalEntry(null);
            moodEntryRepository.save(moodEntry);

            journalEntryRepository.deleteById(id);
        }
    }

    private void checkAuth(MoodEntry moodEntry) {
        AbstractUser authedUser = (AbstractUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authedUser.getId() != moodEntry.getStudent().getAbstractUser().getId()) {
            throw new AuthorizationDeniedException("You are not authorized.");
        }
    }
}