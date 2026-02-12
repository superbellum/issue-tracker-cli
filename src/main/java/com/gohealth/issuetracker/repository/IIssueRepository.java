package com.gohealth.issuetracker.repository;

import com.gohealth.issuetracker.model.Issue;
import com.gohealth.issuetracker.model.IssueStatus;

import java.util.List;
import java.util.Optional;

public interface IIssueRepository {
    Issue save(Issue issue);

    Optional<Issue> findById(String id);

    List<Issue> findByStatus(IssueStatus status);

    void update(Issue issue);
}
