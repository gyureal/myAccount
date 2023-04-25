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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountUserRepository accountUserRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;  // 위에 모킹한 라이브러리 삽입

    @Test
    void useBalance_성공() {
        // given
        AccountUser userData = AccountUser.builder()
                .id(12345L).build();

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(userData));

        Account account = Account.builder()
                .accountNumber("1110000000")
                .accountUser(userData)
                .accountStatus(AccountStatus.IN_USE)
                .balance(1000L)
                .build();
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account)
                );
        given(transactionRepository.save(any()))
                .willReturn(Transaction.builder()
                        .account(account)
                        .transactionType(TransactionType.USE)
                        .transactionResultType(TransactionResultType.S)
                        .amount(100L)
                        .balanceSnapShot(900L)
                        .build());
        ArgumentCaptor<Transaction> captor
                = ArgumentCaptor.forClass(Transaction.class);

        // when
        TransactionDto transactionDto = transactionService.useBalance(
                userData.getId(), "111111111", 100L);

        // then
        verify(transactionRepository, times(1))
                .save(captor.capture());
        assertEquals(100L, captor.getValue().getAmount());
        assertEquals(900L, captor.getValue().getBalanceSnapShot());
        assertEquals(TransactionType.USE, transactionDto.getTransactionType());
        assertEquals(TransactionResultType.S
                , transactionDto.getTransactionResultType());
    }

    @Test
    void useBalance_유저_정보가_없을때_예외를_던진다() {
        // given
        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> transactionService.useBalance(1L,
                        "1000000001", 100L));

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void useBalance_실패_계좌_소유주_다름() {
        // given
        Optional<AccountUser> userData = Optional.of(AccountUser.builder()
                .id(12345L).build());

        Optional<AccountUser> otherUser = Optional.of(AccountUser.builder()
                .id(2222L).build());

        given(accountUserRepository.findById(anyLong()))
                .willReturn(userData);
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(otherUser.get())
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(111L)
                        .accountNumber("1110").build())
                );

        // when
        AccountException exception = assertThrows(AccountException.class
                , () -> transactionService.useBalance(1L,
                        "1000000001", 100L));

        // then
        assertEquals(ErrorCode.USER_ACCOUNT_NOT_MATCH, exception.getErrorCode());
    }

    @Test
    void useBalance_실패_계좌_해지상태() {
        // given
        Optional<AccountUser> userData = Optional.of(AccountUser.builder()
                .id(12345L).build());

        given(accountUserRepository.findById(anyLong()))
                .willReturn(userData);
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(userData.get())
                        .accountStatus(AccountStatus.UNREGISTERED)
                        .balance(111L)
                        .accountNumber("1110").build())
                );

        // when
        AccountException exception = assertThrows(AccountException.class
                , () -> transactionService.useBalance(1L,
                        "1000000001", 100L));

        // then
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
    }

    @Test
    void useBalance_실패_계좌잔액이_부족한_경우() {
        // given
        Optional<AccountUser> userData = Optional.of(AccountUser.builder()
                .id(12345L).build());

        given(accountUserRepository.findById(anyLong()))
                .willReturn(userData);
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(userData.get())
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(99L)
                        .accountNumber("1110").build())
                );

        // when
        AccountException exception = assertThrows(AccountException.class
                , () -> transactionService.useBalance(1L,
                        "1000000001", 100L));

        // then
        assertEquals(ErrorCode.AMOUNT_EXCEED_BALANCE, exception.getErrorCode());
    }

    @Test
    void cancelBalance_성공() {
        // given
        AccountUser userData = AccountUser.builder()
                .id(12345L).build();

        Account account = Account.builder()
                .accountNumber("1110000000")
                .accountUser(userData)
                .accountStatus(AccountStatus.IN_USE)
                .balance(1000L)
                .build();
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.CANCEL)
                .transactionResultType(TransactionResultType.S)
                .amount(100L)
                .balanceSnapShot(900L)
                .transactionAt(LocalDateTime.now())
                .build();

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account)
                );
        given(transactionRepository.findByTransactionId(any()))
                .willReturn(Optional.of(transaction));
        given(transactionRepository.save(any()))
                .willReturn(transaction);
        ArgumentCaptor<Transaction> captor
                = ArgumentCaptor.forClass(Transaction.class);


        // when
        TransactionDto transactionDto = transactionService.cancelBalance(
                "transactionId", "111111111",
                100L);

        // then
        verify(transactionRepository, times(1))
                .save(captor.capture());
        assertEquals(100L, captor.getValue().getAmount());
        assertEquals(1100L, captor.getValue().getBalanceSnapShot());
        assertEquals(TransactionType.CANCEL, transactionDto.getTransactionType());
        assertEquals(TransactionResultType.S
                , transactionDto.getTransactionResultType());
    }

    @Test
    void cancelBalance_실패_미등록_계좌() {
        // given
        given(transactionRepository.findByTransactionId(any()))
                .willReturn(Optional.of(new Transaction()));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class
                , () -> transactionService.cancelBalance("tId",
                        "1110", 1000L));

        // then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void cancelBalance_실패_거래_정보_없음() {
        // given
        given(transactionRepository.findByTransactionId(any()))
                .willReturn(Optional.empty());

        // when
        AccountException exception = assertThrows(AccountException.class
                , () -> transactionService.cancelBalance("tId",
                        "1110", 1000L));

        // then
        assertEquals(ErrorCode.TRANSACTION_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void cancelBalance_실패_거래와_계좌의_사용자가_다름() {
        // given
        Account oneAccount = Account.builder()
                .id(1111L)
                .build();
        Account otherAccount = Account.builder()
                .id(1112L)
                .build();


        given(transactionRepository.findByTransactionId(any()))
                .willReturn(Optional.of(Transaction.builder()
                        .account(oneAccount)
                        .build()));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(otherAccount));

        // when
        AccountException exception = assertThrows(AccountException.class
                , () -> transactionService.cancelBalance("tId",
                        "1110", 1000L));

        // then
        assertEquals(ErrorCode.TRANSACTION_ACCOUNT_NOT_MATCH, exception.getErrorCode());
    }

    @Test
    void cancelBalance_실패_전체_취소가_아닐때() {
        // given
        AccountUser userData = AccountUser.builder()
                .id(12345L).build();
        Account account = Account.builder()
                .accountNumber("1110000000")
                .accountUser(userData)
                .accountStatus(AccountStatus.IN_USE)
                .balance(1000L)
                .build();
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.CANCEL)
                .transactionResultType(TransactionResultType.S)
                .amount(100L)
                .balanceSnapShot(900L)
                .transactionAt(LocalDateTime.now())
                .build();

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account)
                );
        given(transactionRepository.findByTransactionId(any()))
                .willReturn(Optional.of(transaction));

        // when
        AccountException exception = assertThrows(AccountException.class
                , () -> transactionService.cancelBalance("tId",
                        "1110", 1000L));

        // then
        assertEquals(ErrorCode.CANCEL_MUST_FULLY, exception.getErrorCode());
    }

    @Test
    void cancelBalance_거래가_너무_과거_일때() {
        // given
        AccountUser userData = AccountUser.builder()
                .id(12345L).build();
        Account account = Account.builder()
                .accountNumber("1110000000")
                .accountUser(userData)
                .accountStatus(AccountStatus.IN_USE)
                .balance(1000L)
                .build();
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.CANCEL)
                .transactionResultType(TransactionResultType.S)
                .amount(100L)
                .balanceSnapShot(900L)
                .transactionAt(LocalDateTime.now().minusYears(1))
                .build();

        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(account)
                );
        given(transactionRepository.findByTransactionId(any()))
                .willReturn(Optional.of(transaction));

        // when
        AccountException exception = assertThrows(AccountException.class
                , () -> transactionService.cancelBalance("tId",
                        "1110", 100L));

        // then
        assertEquals(ErrorCode.TOO_OLD_ORDER_TO_CANCEL, exception.getErrorCode());
    }

    @Test
    public void queryTransaction_성공() {
        // given
        Account account = Account.builder()
                .accountNumber("1110000000")
                .accountUser(new AccountUser())
                .accountStatus(AccountStatus.IN_USE)
                .balance(1000L)
                .build();
        Transaction transaction = Transaction.builder()
                .account(account)
                .transactionType(TransactionType.CANCEL)
                .transactionResultType(TransactionResultType.S)
                .amount(100L)
                .balanceSnapShot(900L)
                .transactionAt(LocalDateTime.now().minusYears(1))
                .build();
        given(transactionRepository.findByTransactionId(any()))
                .willReturn(Optional.of(transaction));

        // when
        TransactionDto transactionDto = transactionService.queryTransaction("tId");

        // then
        assertEquals(TransactionType.CANCEL, transactionDto.getTransactionType());
        assertEquals(TransactionResultType.S, transactionDto.getTransactionResultType());
        assertEquals(100L, transactionDto.getAmount());
    }
}