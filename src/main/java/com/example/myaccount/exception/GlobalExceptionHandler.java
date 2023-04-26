package com.example.myaccount.exception;

import com.example.myaccount.dto.ErrorResponse;
import com.example.myaccount.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.myaccount.type.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ErrorResponse handleAccountException(AccountException e) {
        log.error("{} is occurred", e.getErrorCode());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    // Exception 종류에 따라 handler를 만들어주면, 더 친절한 에러 코드가 될 수 있다.

    @ExceptionHandler(Exception.class)
    public ErrorResponse handleAccountException(Exception e) {
        log.error("{} is occurred", e);

        return new ErrorResponse(INTERNAL_SERVER_ERROR,
                INTERNAL_SERVER_ERROR.getDescription());
        // 내부의 에러메세지를 외부로 표출하지 않는다.
    }
}
