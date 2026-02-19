package com.votingsystem.Voting.System.exception;

public class CandidateNotExistException extends RuntimeException {
    public CandidateNotExistException(String message) {
        super(message);
    }
}
