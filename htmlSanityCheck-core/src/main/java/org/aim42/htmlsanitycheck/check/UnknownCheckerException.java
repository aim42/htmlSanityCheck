package org.aim42.htmlsanitycheck.check;

public class UnknownCheckerException extends RuntimeException {
    public UnknownCheckerException(String message) {
        super(message);
    }

    public UnknownCheckerException(String message, String checkerName) {
        super(message + ": " + checkerName);
    }
}
