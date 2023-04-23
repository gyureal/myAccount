package com.example.myaccount.controller;

import com.example.myaccount.domain.Account;
import com.example.myaccount.domain.AccountStatus;
import com.example.myaccount.dto.AccountDto;
import com.example.myaccount.dto.CreateAccount;
import com.example.myaccount.dto.DeleteAccount;
import com.example.myaccount.service.AccountService;
import com.example.myaccount.service.RedisTestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import redis.embedded.RedisServer;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @MockBean
    private RedisTestService redisTestService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void 계좌를_생성하는데_성공한다() throws Exception {
        // given
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                        .userId(12345L)
                        .accountNumber("1234567")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build()
                );

        // when
        // then
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateAccount.Request(12345L, 100L))
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(12345L))
                .andExpect(jsonPath("$.accountNumber").value("1234567"))
                .andDo(print());
    }

    @Test
    void deleteAccount_계좌를_삭제하는데_성공한다() throws Exception {
        // given
        given(accountService.deleteAccount(anyLong(), anyString()))
                .willReturn(AccountDto.builder()
                        .userId(12345L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build()
                );

        // when
        // then
        mockMvc.perform(delete("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DeleteAccount.Request(12345L, "1123456789"))
                        ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(12345L))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());
    }

    @Test
    void getAccountsByUserId_계좌_조회_성공() throws Exception {

        List<AccountDto> accountDtos =
                Arrays.asList(AccountDto.builder()
                                .accountNumber("1111111111")
                                .balance(1000L)
                                .build(),
                        AccountDto.builder()
                                .accountNumber("2222222222")
                                .balance(2222L)
                                .build(),
                        AccountDto.builder()
                                .accountNumber("3333333333")
                                .balance(3333L)
                                .build()
                );

        // given
        given(accountService.getAccountsByUserId(anyLong()))
                .willReturn(accountDtos);

        // when
        // then
        mockMvc.perform(get("/account?user_id=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("1111111111"))
                .andExpect(jsonPath("$[0].balance").value(1000L))
                .andExpect(jsonPath("$[1].accountNumber").value("2222222222"))
                .andExpect(jsonPath("$[1].balance").value(2222L))
                .andExpect(jsonPath("$[2].accountNumber").value("3333333333"))
                .andExpect(jsonPath("$[2].balance").value(3333L))
                .andDo(print());
    }

}