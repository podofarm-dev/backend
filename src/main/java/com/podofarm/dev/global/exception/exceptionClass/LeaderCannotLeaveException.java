package com.podofarm.dev.global.exception.exceptionClass;

public class LeaderCannotLeaveException extends IllegalStateException {

    public LeaderCannotLeaveException() {
        super();
    }

    public LeaderCannotLeaveException(String s) {
        super(s);
    }
}
