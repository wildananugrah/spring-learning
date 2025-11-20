package com.user.account.app.logics;

import com.user.account.app.dto.TransactionRequest;
import com.user.account.app.dto.TransactionResponse;
import com.user.account.app.entities.Account;
import com.user.account.app.entities.Transaction;
import com.user.account.app.entities.User;
import com.user.account.app.exceptions.InsufficientBalanceException;
import com.user.account.app.exceptions.ResourceNotFoundException;
import com.user.account.app.services.AccountRepository;
import com.user.account.app.services.TransactionRepository;
import com.user.account.app.services.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionLogic {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    @SneakyThrows
    public TransactionResponse deposit(String username, TransactionRequest request) {
        validateAccountOwnership(username, request.getAccountNumber());

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balanceAfter = balanceBefore.add(request.getAmount());

        account.setBalance(balanceAfter);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .account(account)
                .type(Transaction.TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .description(request.getDescription())
                .referenceNumber(generateReferenceNumber())
                .build();

        transaction = transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    @Transactional
    @SneakyThrows
    public TransactionResponse withdrawal(String username, TransactionRequest request) {
        validateAccountOwnership(username, request.getAccountNumber());

        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        BigDecimal balanceBefore = account.getBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(request.getAmount());

        account.setBalance(balanceAfter);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .account(account)
                .type(Transaction.TransactionType.WITHDRAWAL)
                .amount(request.getAmount())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .description(request.getDescription())
                .referenceNumber(generateReferenceNumber())
                .build();

        transaction = transactionRepository.save(transaction);

        return mapToResponse(transaction);
    }

    @Transactional
    @SneakyThrows
    public TransactionResponse transfer(String username, TransactionRequest request) {
        validateAccountOwnership(username, request.getAccountNumber());

        if (request.getToAccountNumber() == null || request.getToAccountNumber().isEmpty()) {
            throw new IllegalArgumentException("Destination account number is required");
        }

        if (request.getAccountNumber().equals(request.getToAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }

        Account fromAccount = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Source account not found"));

        Account toAccount = accountRepository.findByAccountNumber(request.getToAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Destination account not found"));

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }

        String referenceNumber = generateReferenceNumber();

        // Debit from source account
        BigDecimal fromBalanceBefore = fromAccount.getBalance();
        BigDecimal fromBalanceAfter = fromBalanceBefore.subtract(request.getAmount());
        fromAccount.setBalance(fromBalanceAfter);
        accountRepository.save(fromAccount);

        Transaction transferOut = Transaction.builder()
                .account(fromAccount)
                .type(Transaction.TransactionType.TRANSFER_OUT)
                .amount(request.getAmount())
                .balanceBefore(fromBalanceBefore)
                .balanceAfter(fromBalanceAfter)
                .description(request.getDescription())
                .referenceNumber(referenceNumber)
                .toAccountNumber(toAccount.getAccountNumber())
                .fromAccountNumber(fromAccount.getAccountNumber())
                .build();

        transferOut = transactionRepository.save(transferOut);

        // Credit to destination account
        BigDecimal toBalanceBefore = toAccount.getBalance();
        BigDecimal toBalanceAfter = toBalanceBefore.add(request.getAmount());
        toAccount.setBalance(toBalanceAfter);
        accountRepository.save(toAccount);

        Transaction transferIn = Transaction.builder()
                .account(toAccount)
                .type(Transaction.TransactionType.TRANSFER_IN)
                .amount(request.getAmount())
                .balanceBefore(toBalanceBefore)
                .balanceAfter(toBalanceAfter)
                .description(request.getDescription())
                .referenceNumber(referenceNumber)
                .toAccountNumber(toAccount.getAccountNumber())
                .fromAccountNumber(fromAccount.getAccountNumber())
                .build();

        transactionRepository.save(transferIn);

        return mapToResponse(transferOut);
    }

    @SneakyThrows
    public Page<TransactionResponse> getTransactionHistory(
            String username,
            String accountNumber,
            LocalDateTime startDate,
            LocalDateTime endDate,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        validateAccountOwnership(username, accountNumber);

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Transaction> transactions;
        if (startDate != null || endDate != null) {
            transactions = transactionRepository.findByAccountIdAndDateRange(
                    account.getId(), startDate, endDate, pageable);
        } else {
            transactions = transactionRepository.findByAccountId(account.getId(), pageable);
        }

        return transactions.map(this::mapToResponse);
    }

    @SneakyThrows
    private void validateAccountOwnership(String username, String accountNumber) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Account not found");
        }
    }

    private String generateReferenceNumber() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").substring(0, 16).toUpperCase();
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .balanceBefore(transaction.getBalanceBefore())
                .balanceAfter(transaction.getBalanceAfter())
                .description(transaction.getDescription())
                .referenceNumber(transaction.getReferenceNumber())
                .toAccountNumber(transaction.getToAccountNumber())
                .fromAccountNumber(transaction.getFromAccountNumber())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
