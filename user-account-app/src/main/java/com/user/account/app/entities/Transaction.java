package com.user.account.app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(precision = 19, scale = 2)
    private BigDecimal balanceBefore;

    @Column(precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column
    private String description;

    @Column
    private String referenceNumber;

    // For transfer transactions
    @Column
    private String toAccountNumber;

    @Column
    private String fromAccountNumber;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER_IN,
        TRANSFER_OUT
    }
}
