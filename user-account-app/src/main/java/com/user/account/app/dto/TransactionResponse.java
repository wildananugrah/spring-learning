package com.user.account.app.dto;

import com.user.account.app.entities.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long id;
    private String accountNumber;
    private Transaction.TransactionType type;
    private BigDecimal amount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private String description;
    private String referenceNumber;
    private String toAccountNumber;
    private String fromAccountNumber;
    private LocalDateTime createdAt;
}
