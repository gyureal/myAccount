package com.example.myaccount.service;

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

    public String getLock(String id) {
        RLock lock = redissonClient.getLock("sample-lock");

        try {
            // 3초 동안 락을 건다.
            boolean isLock = lock.tryLock(1, 3, TimeUnit.SECONDS); // 락 획득 시도

            if (!isLock) {
                log.error("=========Lock acquisition failed");
                return "Lock failed";
            }
        } catch (Exception e) {
            log.error("Redis lock failed");
        }

        return "get lock success";
    }
}
