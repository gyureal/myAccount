package com.example.myaccount.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateAccount {
    // 한 요청에 대한 요청과 응답을 하나의 클래스에 명시적으로 사용가능하다.
    @Getter
    @Setter
    public static class Request {   // static
        @NotNull
        @Min(1)
        private Long userId;
        @NotNull
        @Min(100)
        private Long initialBalance;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long userId;
        private Long accountNumber;
        private LocalDateTime registeredAt;
    }
}
