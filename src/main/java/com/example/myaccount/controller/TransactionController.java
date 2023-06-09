package com.example.myaccount.controller;

import com.example.myaccount.aop.AccountLock;
import com.example.myaccount.dto.CancelBalance;
import com.example.myaccount.dto.QueryTransactionResponse;
import com.example.myaccount.dto.TransactionDto;
import com.example.myaccount.dto.UseBalance;
import com.example.myaccount.exception.AccountException;
import com.example.myaccount.service.TransactionService;
import com.example.myaccount.type.TransactionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 잔액 관련 컨트롤러
 * 1. 잔액 사용
 * 2. 잔액 사용 취소
 * 3. 거래 확인
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transaction/use")
    @AccountLock
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request request) {

        TransactionDto transactionDto;
        try {
            Thread.sleep(5000L);
            transactionDto = transactionService.useBalance(request.getUserId(),
                    request.getAccountNumber(), request.getAmount());
            return UseBalance.Response.from(transactionDto);
        } catch (AccountException e) {
            log.error("Failed to use balance. ");

            transactionService.saveFailedTransaction(
                    TransactionType.USE,
                    request.getAccountNumber(),
                    request.getAmount()
            );
            throw e;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/transaction/cancel")
    @AccountLock
    public CancelBalance.Response cancelBalance(
            @Valid @RequestBody CancelBalance.Request request) {

        TransactionDto transactionDto;
        try {
            transactionDto = transactionService.cancelBalance(request.getTransactionId(),
                    request.getAccountNumber(), request.getAmount());
            return CancelBalance.Response.from(transactionDto);
        } catch (AccountException e) {
            log.error("Failed to use balance. ");

            transactionService.saveFailedTransaction(
                    TransactionType.CANCEL,
                    request.getAccountNumber(),
                    request.getAmount()
            );
            throw e;
        }
    }

    @GetMapping("/transaction/{transactionId}")
    public QueryTransactionResponse queryTransaction(
            @PathVariable String transactionId
    ) {
        return QueryTransactionResponse.from(transactionService.queryTransaction(transactionId));
    }

}
