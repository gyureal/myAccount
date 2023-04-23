package com.example.myaccount.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfo {      // Client - Controller 간 전송
    private String accountNumber;
    private Long balance;

}
