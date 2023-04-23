package com.example.myaccount.domain;

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


}
