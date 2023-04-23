package com.example.myaccount.controller;

import com.example.myaccount.domain.Account;
import com.example.myaccount.dto.CreateAccount;
import com.example.myaccount.service.AccountService;
import com.example.myaccount.service.RedisTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;
    private final RedisTestService redisTestService;

    @GetMapping("/get-lock")
    public String getLock() {
        return redisTestService.getLock("sample-lock");
    }

    @PostMapping("/create-account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request) {
        accountService.createAccount(request.getUserId()
                , request.getInitialBalance());
        return null;
    }

    @GetMapping("/account/{id}")
    public Account getAccount(
            @PathVariable Long id){
        return accountService.getAccount(id);
    }
}
