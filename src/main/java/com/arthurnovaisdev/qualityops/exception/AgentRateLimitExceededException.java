package com.arthurnovaisdev.qualityops.exception;

public class AgentRateLimitExceededException extends RuntimeException {

    public AgentRateLimitExceededException(String message) {
        super(message);
    }
}