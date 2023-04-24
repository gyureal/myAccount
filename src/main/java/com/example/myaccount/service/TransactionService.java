package com.example.myaccount.service;

import com.example.myaccount.domain.Account;
import com.example.myaccount.domain.AccountStatus;
import com.example.myaccount.domain.AccountUser;
import com.example.myaccount.domain.Transaction;
import com.example.myaccount.dto.TransactionDto;
import com.example.myaccount.exception.AccountException;
import com.example.myaccount.repository.AccountRepository;
import com.example.myaccount.repository.AccountUserRepository;
import com.example.myaccount.repository.TransactionRepository;
import com.example.myaccount.type.ErrorCode;
import com.example.myaccount.type.TransactionResultType;
import com.example.myaccount.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static com.example.myaccount.type.ErrorCode.*;
import static com.example.myaccount.type.TransactionResultType.F;
import static com.example.myaccount.type.TransactionResultType.S;
import static com.example.myaccount.type.TransactionType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountUserRepository accountUserRepository;
    private final AccountRepository accountRepository;

    /**
     * 사용자 없는 경우, 사용자 아이디와 계좌 소유주가 다른 경우,
     * 계좌가 이미 해지 상태인 경우, 거래금액이 잔액보다 큰 경우,
     * 거래 금액이 너무 작거나 큰 경우 실패 응답
     */
    @Transactional
    public TransactionDto useBalance(Long userId, String accountNumber,
                                     Long amount) {
        AccountUser user = accountUserRepository.findById(userId)
                .orElseThrow(() -> new AccountException(USER_NOT_FOUND));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        validateUseBalance(user, account, amount);

        account.useBalance(amount);

        Transaction transaction =
                saveAndGetTransaction(S, account, amount);

        return TransactionDto.fromEntity(transaction);
    }

    private void validateUseBalance(AccountUser user, Account account, Long amount) {
        if (!Objects.equals(user.getId(), account.getAccountUser().getId())) {
            throw new AccountException(USER_ACCOUNT_NOT_MATCH);
        }

        if (account.getAccountStatus() != AccountStatus.IN_USE) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);
        }

        if (account.getBalance() < amount) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        }
    }

    @Transactional
    public void saveFailedUseTransaction(String accountNumber, Long amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ACCOUNT_NOT_FOUND));

        saveAndGetTransaction(F, account, amount);
    }

    private Transaction saveAndGetTransaction(
            TransactionResultType transactionResultType
            , Account account, Long amount) {
        return transactionRepository.save(Transaction.builder()
                .transactionType(USE)
                .transactionResultType(transactionResultType)
                .account(account)
                .amount(amount)
                .balanceSnapShot(account.getBalance())
                .transactionId(UUID.randomUUID().toString()
                        .replace("-", ""))    // 랜덤값
                .transactionAt(LocalDateTime.now())
                .build());
    }
}
