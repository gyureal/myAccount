package com.example.myaccount.repository;

import com.example.myaccount.domain.Account;
import com.example.myaccount.domain.AccountUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findFirstByOrderByIdDesc(); // 명에 맞게 쿼리 만들어짐
    Integer countByAccountUser(AccountUser accountUser);
    Optional<Account> findByAccountNumber(String accountNumber);
    List<Account> findByAccountUser(AccountUser accountUser);
}
