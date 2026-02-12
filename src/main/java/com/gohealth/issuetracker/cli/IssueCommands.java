package com.gohealth.issuetracker.cli;

import com.gohealth.issuetracker.exception.WrongIssueStatusException;
import com.gohealth.issuetracker.model.Issue;
import com.gohealth.issuetracker.model.IssueStatus;
import com.gohealth.issuetracker.service.IIssueService;
import org.springframework.shell.core.command.annotation.Command;
import org.springframework.shell.core.command.annotation.Option;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class IssueCommands {
    private final IIssueService issueService;

    public IssueCommands(IIssueService issueService) {
        this.issueService = issueService;
    }

    @Command(name = "create", group = "Issues", description = "Create new issue",
            help = "Create new issue with description and optional parent id. " +
                    "Usage: create [-d | --description]=<description> --parent-id=<parent id>")
    public String create(
            @Option(shortName = 'd', longName = "description", required = true) String description,
            @Option(longName = "parent-id") String parentId
    ) {
        Issue issue = issueService.createIssue(description, parentId);
        return "Created issue " + issue.id();
    }

    @Command(name = "update-status", group = "Issues", description = "Update issue status",
            help = "Update the status of an issue identified by id. " +
                    "Usage: update-status --id=<id> --status=<status>")
    public String update(
            @Option(longName = "id", required = true) String issueId,
            @Option(longName = "status", required = true) String status
    ) {
        try {
            validateIssueStatus(status);
            issueService.updateStatus(issueId, IssueStatus.valueOf(status));
            return "Updated issue status to " + status;
        } catch (WrongIssueStatusException | IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage();
        }
    }

    @Command(name = "list", group = "Issues", description = "List issues",
            help = "List all issues with the given status. " +
                    "Usage: list [-s | --status]=<status>")
    public String list(@Option(shortName = 's', longName = "status", required = true) String status) {
        try {
            validateIssueStatus(status);
            List<Issue> issues = issueService.listByStatus(IssueStatus.valueOf(status));
            StringBuilder builder = new StringBuilder();
            issues.forEach(issue -> builder.append(issue.toString()).append("\n"));
            return builder.toString();
        } catch (WrongIssueStatusException | IllegalArgumentException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage();
        }
    }

    private void validateIssueStatus(String status) {
        List<String> statusNames = Arrays.stream(IssueStatus.values()).map(Enum::name).toList();

        if (statusNames.stream().noneMatch(s -> s.equals(status))) {
            throw new WrongIssueStatusException("Wrong status '" + status + "' used. " +
                    "Available statuses: " + String.join(", ", statusNames));
        }
    }
}
