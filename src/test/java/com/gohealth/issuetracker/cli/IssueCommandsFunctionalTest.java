package com.gohealth.issuetracker.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.gohealth.issuetracker.model.IssueStatus.IN_PROGRESS;
import static com.gohealth.issuetracker.model.IssueStatus.OPEN;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class IssueCommandsFunctionalTest {
    @Autowired
    private IssueCommands issueCommands;

    @Value("${issues.storage.path}")
    private String storagePath;

    @AfterEach
    void cleanStorage() throws IOException {
        Files.deleteIfExists(Path.of(storagePath));
    }

    @Test
    void create_shouldReturnCreatedIssue() {
        String description = "description";
        String parentId = "parentId";

        String createOutput = issueCommands.create(description, parentId);

        assertThat(createOutput).startsWith("Created issue");
    }

    @Test
    void updateStatus_shouldSetNewIssueStatus() {
        String description = "description";

        String createOutput = issueCommands.create(description, null);

        String issueId = createOutput.replace("Created issue ", "").trim();
        String updateOutput = issueCommands.update(issueId, IN_PROGRESS.name());

        assertThat(updateOutput).isEqualTo("Updated issue status to " + IN_PROGRESS.name());
    }

    @Test
    void listIssues_shouldOnlyContainFilteredIssues() {
        String description1 = "desc1";
        String description2 = "desc2";

        issueCommands.create(description1, null);
        String createOutput = issueCommands.create(description2, null);

        String issueId = createOutput.replace("Created issue ", "").trim();
        issueCommands.update(issueId, IN_PROGRESS.name());

        String inProgressList = issueCommands.list(IN_PROGRESS.name());
        assertThat(inProgressList.lines().count()).isEqualTo(1);
        assertThat(inProgressList)
                .contains(description2)
                .contains(IN_PROGRESS.name());

        String openList = issueCommands.list(OPEN.name());
        assertThat(openList).contains(description1);
    }

    @Test
    void updateIssueWithWrongStatus_shouldReturnExceptionMessage() {
        String wrongStatus = "INVALID_STATUS";
        String errorOutput = issueCommands.update("issueId", wrongStatus);
        assertThat(errorOutput).startsWith("Wrong status ");
    }

    @Test
    void updateIssueWithWrongId_shouldReturnExceptionMessage() {
        String wrongId = "INVALID_ID";
        String errorOutput = issueCommands.update(wrongId, IN_PROGRESS.name());
        assertThat(errorOutput).startsWith("Issue not found");
    }
}
