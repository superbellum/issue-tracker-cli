package com.gohealth.issuetracker.service;

import com.gohealth.issuetracker.model.Issue;
import com.gohealth.issuetracker.model.IssueStatus;

import java.util.List;

public interface IIssueService {
    Issue createIssue(String description, String parentId);

    void updateStatus(String issueId, IssueStatus status);

    List<Issue> listByStatus(IssueStatus status);
}
