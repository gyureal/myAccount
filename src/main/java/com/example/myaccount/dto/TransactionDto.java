package com.example.myaccount.dto;

import com.example.myaccount.domain.Account;
import com.example.myaccount.domain.Transaction;
import com.example.myaccount.type.TransactionResultType;
import com.example.myaccount.type.TransactionType;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {

    private String accountNumber; // account 에서 accountNumber 만 반환
    //private Account account;
    private TransactionType transactionType;
    private TransactionResultType transactionResultType;
    private Long amount;
    private Long balanceSnapShot;
    private String transactionId;
    private LocalDateTime transactionAt;

    public TransactionDto fromEntity(Transaction transaction) {
        return TransactionDto.builder()
                .accountNumber(transaction.getAccount().getAccountNumber())
                .transactionType(transaction.getTransactionType())
                .transactionResultType(transaction.getTransactionResultType())
                .amount(transaction.getAmount())
                .balanceSnapShot(transaction.getBalanceSnapShot())
                .transactionId(transaction.getTransactionId())
                .transactionAt(transaction.getTransactionAt())
                .build();
    }
}
