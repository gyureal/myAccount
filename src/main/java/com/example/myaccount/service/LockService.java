package com.example.myaccount.service;

import com.example.myaccount.exception.AccountException;
import com.example.myaccount.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {
    private final RedissonClient redissonClient;

    public void lock(String accountNumber) {
        RLock lock = redissonClient.getLock(getLockKey(accountNumber)); // lock key
        log.debug("Trying lock for accountNumber : {}", accountNumber);

        try {
            // 3초 동안 락을 건다.
            boolean isLock = lock.tryLock(1, 5, TimeUnit.SECONDS); // 락 획득 시도

            if (!isLock) {
                log.error("=========Lock acquisition failed");
                throw new AccountException(ErrorCode.ACCOUNT_TRANSACTION_LOCK);
            }
        } catch (Exception e) {
            log.error("Redis lock failed");
        }
    }

    public void unLock(String accountNumber) {
        log.debug("Unlock for accountNumber : {}", accountNumber);
        redissonClient.getLock(getLockKey(accountNumber)).unlock();
    }

    private static String getLockKey(String accountNumber) {
        return "ACLK:" + accountNumber;
    }
}
