package com.votingsystem.Voting.System.exception;

public class CandidateExistException extends RuntimeException {
    public CandidateExistException(String message) {
        super(message);
    }
}
