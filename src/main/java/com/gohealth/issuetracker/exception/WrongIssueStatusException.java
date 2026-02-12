package com.gohealth.issuetracker.exception;

public class WrongIssueStatusException extends RuntimeException {

    public WrongIssueStatusException(String message) {
        super(message);
    }
}
