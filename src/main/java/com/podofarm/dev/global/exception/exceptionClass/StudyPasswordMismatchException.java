package com.podofarm.dev.global.exception.exceptionClass;

import org.springframework.security.authentication.BadCredentialsException;

public class StudyPasswordMismatchException extends BadCredentialsException {

    public StudyPasswordMismatchException(String msg) {
        super(msg);
    }

    public StudyPasswordMismatchException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
