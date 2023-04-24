package com.example.myaccount.type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND("사용자가 없습니다."),
    MAX_ACCOUNT_PER_USER("한 유저의 최대 계좌 수는 10개 입니다."),
    ACCOUNT_NOT_FOUND("계좌 정보가 없습니다."),
    USER_ACCOUNT_NOT_MATCH("사용자와 계좌의 소유주가 다릅니다."),
    BALANCE_IS_NOT_EMPTY("잔액이 있는 계좌는 해지할 수 없습니다."),
    AMOUNT_EXCEED_BALANCE("거래 금액이 계좌 보다 큽니다."),
    ACCOUNT_ALREADY_UNREGISTERED("계좌가 이미 해지되었습니다.");



    private final String description;
}
