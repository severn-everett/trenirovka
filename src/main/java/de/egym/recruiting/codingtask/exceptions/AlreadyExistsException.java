package de.egym.recruiting.codingtask.exceptions;

import java.util.Date;

public class AlreadyExistsException extends Exception {
    
    public AlreadyExistsException(Long userId, Date startTime, Date endTime, String description) {
        super(String.format("An exercise already exists for user %s between %s and %s: '%s'", userId, startTime, endTime, description));
    }
    
}
