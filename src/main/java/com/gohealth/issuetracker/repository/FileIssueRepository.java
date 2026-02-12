package com.gohealth.issuetracker.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gohealth.issuetracker.exception.FileException;
import com.gohealth.issuetracker.model.Issue;
import com.gohealth.issuetracker.model.IssueStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class FileIssueRepository implements IIssueRepository {
    private final ObjectMapper objectMapper;
    private final File file;
    private final Map<String, Issue> storage;

    public FileIssueRepository(ObjectMapper objectMapper, @Value("${issues.storage.path}") String filePath) throws IOException {
        this.objectMapper = objectMapper;
        this.file = new File(filePath);

        if (!file.exists()) {
            file.createNewFile();
            storage = new HashMap<>();
            saveToFile();
        } else {
            storage = loadFromFile();
        }
    }

    private synchronized Map<String, Issue> loadFromFile() throws IOException {
        if (file.length() == 0) {
            return new HashMap<>();
        }

        return objectMapper.readValue(file, new TypeReference<>() {
        });
    }

    private synchronized void saveToFile() throws IOException {
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, storage);
    }

    @Override
    public synchronized Issue save(Issue issue) {
        storage.put(issue.id(), issue);
        try {
            saveToFile();
        } catch (IOException e) {
            throw new FileException("Failed to save issue to file", e);
        }
        return issue;
    }

    @Override
    public synchronized Optional<Issue> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public synchronized List<Issue> findByStatus(IssueStatus status) {
        return storage.values()
                .stream()
                .filter(issue -> issue.status() == status)
                .toList();
    }

    @Override
    public synchronized void update(Issue issue) {
        storage.put(issue.id(), issue);
        try {
            saveToFile();
        } catch (IOException e) {
            throw new FileException("Failed to update issue in file", e);
        }
    }
}
