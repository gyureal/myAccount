package com.example.myaccount.service;

import com.example.myaccount.domain.Account;
import com.example.myaccount.domain.AccountStatus;
import com.example.myaccount.domain.AccountUser;
import com.example.myaccount.dto.AccountDto;
import com.example.myaccount.repository.AccountRepository;
import com.example.myaccount.repository.AccountUserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    void 계좌_생성에_성공한다() {
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

}