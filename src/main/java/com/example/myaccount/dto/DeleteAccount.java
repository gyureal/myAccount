package com.example.myaccount.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class DeleteAccount {
    // 한 요청에 대한 요청과 응답을 하나의 클래스에 명시적으로 사용가능하다.
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request {   // static
        @NotNull
        @Min(1)
        private Long userId;
        @NotBlank
        @Size(min = 10, max = 10)
        private String accountNumber;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long userId;
        private String accountNumber;
        private LocalDateTime unRegisteredAt;

        public static Response from(AccountDto accountDto) {
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .unRegisteredAt(accountDto.getUnRegisteredAt())
                    .build();
        }
    }
}
