package com.gohealth.issuetracker.service;

import com.gohealth.issuetracker.model.Issue;
import com.gohealth.issuetracker.repository.IIssueRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.gohealth.issuetracker.model.IssueStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IssueServiceTest {

    @Mock
    private IIssueRepository repository;

    @InjectMocks
    private IssueServiceImpl service;

    @Test
    void createIssue_shouldSaveIssue() {
        String description = "description";
        String parentId = "parentId1";

        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Issue issue = service.createIssue(description, parentId);

        assertNotNull(issue.id());
        assertEquals(description, issue.description());
        assertEquals(parentId, issue.parentId());
        assertEquals(OPEN, issue.status());
        verify(repository, times(1)).save(issue);
    }

    @Test
    void updateStatus_shouldChangeStatus() {
        Issue existingIssue = Issue.create("description", null);

        when(repository.findById("1")).thenReturn(Optional.of(existingIssue));

        service.updateStatus("1", IN_PROGRESS);

        ArgumentCaptor<Issue> captor = ArgumentCaptor.forClass(Issue.class);
        verify(repository).update(captor.capture());
        Issue updatedIssue = captor.getValue();
        assertEquals(IN_PROGRESS, updatedIssue.status());
    }

    @Test
    void updateStatus_shouldThrowException_whenIssueNotFound() {
        String invalidId = "invalidId";

        when(repository.findById(invalidId)).thenReturn(Optional.empty());

        var ex = assertThrows(IllegalArgumentException.class, () -> service.updateStatus(invalidId, IN_PROGRESS));
        assertTrue(ex.getMessage().contains(invalidId));
    }

    @Test
    void updateStatus_shouldUpdateDate() throws InterruptedException {
        Instant createdDate = Instant.now();
        Issue existingIssue = new Issue("id1", "desc1", null, OPEN, createdDate, createdDate);

        when(repository.findById("id1")).thenReturn(Optional.of(existingIssue));

        Thread.sleep(5);

        service.updateStatus("id1", IN_PROGRESS);

        ArgumentCaptor<Issue> captor = ArgumentCaptor.forClass(Issue.class);
        verify(repository).update(captor.capture());
        Issue updatedIssue = captor.getValue();

        assertEquals(IN_PROGRESS, updatedIssue.status());
        assertEquals(createdDate, updatedIssue.createdDate());
        assertTrue(updatedIssue.updatedDate().isAfter(createdDate));
    }

    @Test
    void listByStatus_shouldReturnFilteredIssues() {
        Instant now = Instant.now();
        Issue issue1 = new Issue("id1", "desc1", null, CLOSED, now, now);
        Issue issue2 = new Issue("id2", "desc2", null, IN_PROGRESS, now, now);
        Issue issue3 = new Issue("id3", "desc3", null, IN_PROGRESS, now, now);

        when(repository.findByStatus(IN_PROGRESS)).thenReturn(List.of(issue2, issue3));

        List<Issue> issues = service.listByStatus(IN_PROGRESS);

        assertEquals(2, issues.size());
        assertTrue(issues.containsAll(List.of(issue2, issue3)));
        assertFalse(issues.contains(issue1));
    }
}
