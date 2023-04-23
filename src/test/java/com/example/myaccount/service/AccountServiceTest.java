package com.example.myaccount.service;

import com.example.myaccount.domain.Account;
import com.example.myaccount.domain.AccountStatus;
import com.example.myaccount.domain.AccountUser;
import com.example.myaccount.dto.AccountDto;
import com.example.myaccount.exception.AccountException;
import com.example.myaccount.repository.AccountRepository;
import com.example.myaccount.repository.AccountUserRepository;
import com.example.myaccount.type.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountUserRepository accountUserRepository;

    @InjectMocks
    private AccountService accountService;  // 위에 모킹한 라이브러리 삽입

    @Test
    void createAccount_계좌_생성에_성공한다() {
        // given
        Optional<AccountUser> userData = Optional.of(AccountUser.builder()
                .id(12345L).build());

        given(accountUserRepository.findById(anyLong()))
                .willReturn(userData);
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1110").build())
                );
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(userData.get())
                        .accountNumber("1119").build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);


        // when
        AccountDto accountDto = accountService.createAccount(
                1L, 100L);

        // then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12345L, accountDto.getUserId());
        assertEquals("1111", captor.getValue().getAccountNumber());
    }

    @Test
    void createAccount_계좌가_없을때_초기_계좌번호로_생성한다() {
        String INITIAL_ACCOUNT_NUMBER = "1000000000";

        // given
        Optional<AccountUser> userData = Optional.of(AccountUser.builder()
                .id(12345L).build());

        given(accountUserRepository.findById(anyLong()))
                .willReturn(userData);
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.empty()
                );
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(userData.get())
                        .accountNumber("1119").build());

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);


        // when
        AccountDto accountDto = accountService.createAccount(
                1L, 100L);

        // then
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12345L, accountDto.getUserId());
        assertEquals(INITIAL_ACCOUNT_NUMBER, captor.getValue().getAccountNumber());
    }

    @Test
    void createAccount_유저_정보가_없을때_예외를_던진다() {
        String INITIAL_ACCOUNT_NUMBER = "1000000000";

        // given
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());


        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 100L));

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createAccount_계좌_수가_10개_이상일때_예외를_던진다() {
        String INITIAL_ACCOUNT_NUMBER = "1000000000";

        // given
        Optional<AccountUser> userData = Optional.of(AccountUser.builder()
                .id(12345L).build());

        given(accountUserRepository.findById(anyLong()))
                .willReturn(userData);

        given(accountRepository.countByAccountUser(any()))
                .willReturn(11);


        // when
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 100L));

        // then
        assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER, exception.getErrorCode());
    }

    @Test
    void deleteAccount_성공() {
        // given
        Optional<AccountUser> userData = Optional.of(AccountUser.builder()
                .id(12345L).build());

        given(accountUserRepository.findById(anyLong()))
                .willReturn(userData);
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(userData.get())
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(0L)
                        .accountNumber("1110").build())
                );

        // when
        AccountDto accountDto = accountService.deleteAccount(
                1L, "1110");

        // then
        assertEquals(12345L, accountDto.getUserId());
    }

    @Test
    void deleteAccount_실패_유저_없음() {
        // given

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());


        // when
        AccountException exception = assertThrows(AccountException.class
                , () -> accountService.deleteAccount(1L, "1110"));

        // then
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void deleteAccount_실패_미등록_계좌() {
        // given

        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(new AccountUser()));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());


        // when
        AccountException exception = assertThrows(AccountException.class
                , () -> accountService.deleteAccount(1L, "1110"));

        // then
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void deleteAccount_실패_계좌_소유주_다름() {
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
                , () -> accountService.deleteAccount(1L, "1110"));

        // then
        assertEquals(ErrorCode.USER_ACCOUNT_NOT_MATCH, exception.getErrorCode());
    }

    @Test
    void deleteAccount_실패_계좌_잔액이_없어야_한다() {
        // given
        Optional<AccountUser> userData = Optional.of(AccountUser.builder()
                .id(12345L).build());

        given(accountUserRepository.findById(anyLong()))
                .willReturn(userData);
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(userData.get())
                        .accountStatus(AccountStatus.IN_USE)
                        .balance(111L)
                        .accountNumber("1110").build())
                );

        // when
        AccountException exception = assertThrows(AccountException.class
                , () -> accountService.deleteAccount(1L, "1110"));

        // then
        assertEquals(ErrorCode.BALANCE_IS_NOT_EMPTY, exception.getErrorCode());
    }

}