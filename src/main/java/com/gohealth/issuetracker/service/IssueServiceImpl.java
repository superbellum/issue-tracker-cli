package com.gohealth.issuetracker.service;

import com.gohealth.issuetracker.model.Issue;
import com.gohealth.issuetracker.model.IssueStatus;
import com.gohealth.issuetracker.repository.IIssueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IssueServiceImpl implements IIssueService {
    private final IIssueRepository repository;

    public IssueServiceImpl(IIssueRepository repository) {
        this.repository = repository;
    }

    @Override
    public Issue createIssue(String description, String parentId) {
        Issue issue = Issue.create(description, parentId);
        return repository.save(issue);
    }

    @Override
    public void updateStatus(String issueId, IssueStatus status) {
        Issue issue = repository
                .findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("Issue not found: " + issueId));

        repository.update(issue.withStatus(status));
    }

    @Override
    public List<Issue> listByStatus(IssueStatus status) {
        return repository.findByStatus(status);
    }
}
