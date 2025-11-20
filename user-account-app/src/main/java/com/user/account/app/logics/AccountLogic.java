package com.user.account.app.logics;

import com.user.account.app.dto.AccountResponse;
import com.user.account.app.dto.CreateAccountRequest;
import com.user.account.app.entities.Account;
import com.user.account.app.entities.User;
import com.user.account.app.exceptions.ResourceNotFoundException;
import com.user.account.app.services.AccountRepository;
import com.user.account.app.services.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountLogic {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    @SneakyThrows
    public AccountResponse createAccount(String username, CreateAccountRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accountNumber = generateAccountNumber();

        Account account = Account.builder()
                .accountNumber(accountNumber)
                .accountName(request.getAccountName())
                .balance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO)
                .user(user)
                .build();

        account = accountRepository.save(account);

        return mapToResponse(account);
    }

    @SneakyThrows
    public List<AccountResponse> getAccountList(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return accountRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public AccountResponse getAccountDetail(String username, String accountNumber) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Account not found");
        }

        return mapToResponse(account);
    }

    @Transactional
    @SneakyThrows
    public void deleteAccount(String username, String accountNumber) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Account not found");
        }

        accountRepository.delete(account);
    }

    private String generateAccountNumber() {
        Random random = new Random();
        String accountNumber;
        do {
            accountNumber = String.format("%010d", random.nextInt(1000000000));
        } while (accountRepository.existsByAccountNumber(accountNumber));
        return accountNumber;
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountName(account.getAccountName())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
