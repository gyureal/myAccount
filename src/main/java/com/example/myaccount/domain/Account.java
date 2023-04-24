package com.example.myaccount.domain;

import com.example.myaccount.exception.AccountException;
import com.example.myaccount.type.ErrorCode;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Account {
    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private AccountUser accountUser;
    private String accountNumber;
    @Enumerated(EnumType.STRING)    // 문자가 DB에 저장됨
    private AccountStatus accountStatus;
    private Long balance;
    private LocalDateTime registeredAt;
    private LocalDateTime unRegisteredAt;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void unregister() {
        this.accountStatus = AccountStatus.UNREGISTERED;
        this.unRegisteredAt = LocalDateTime.now();
    }

    public void useBalance(Long amount) {
        if (amount > balance) {
            throw new AccountException(ErrorCode.AMOUNT_EXCEED_BALANCE);
        }
        balance -= amount;
    }

}
