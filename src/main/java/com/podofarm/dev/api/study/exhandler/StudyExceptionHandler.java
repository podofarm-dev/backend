package com.podofarm.dev.api.study.exhandler;

import com.podofarm.dev.api.study.controller.StudyController;
import com.podofarm.dev.global.exception.dto.ErrorResDto;
import com.podofarm.dev.global.exception.exceptionClass.LeaderCannotLeaveException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.CONFLICT;

@RestControllerAdvice(assignableTypes = {
        StudyController.class
})
public class StudyExceptionHandler {

    @ExceptionHandler(LeaderCannotLeaveException.class)
    public ResponseEntity<ErrorResDto> leaderLeaveExHandler(LeaderCannotLeaveException e) {
        return ResponseEntity
                .status(CONFLICT)
                .body(ErrorResDto.of(e.getMessage()));
    }

}
