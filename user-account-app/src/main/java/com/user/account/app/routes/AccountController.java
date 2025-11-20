package com.user.account.app.routes;

import com.user.account.app.dto.AccountResponse;
import com.user.account.app.dto.ApiResponse;
import com.user.account.app.dto.CreateAccountRequest;
import com.user.account.app.logics.AccountLogic;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountLogic accountLogic;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(
            Authentication authentication,
            @Valid @RequestBody CreateAccountRequest request) {
        AccountResponse response = accountLogic.createAccount(authentication.getName(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Account created successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAccountList(Authentication authentication) {
        List<AccountResponse> response = accountLogic.getAccountList(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Accounts retrieved successfully", response));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountDetail(
            Authentication authentication,
            @PathVariable String accountNumber) {
        AccountResponse response = accountLogic.getAccountDetail(authentication.getName(), accountNumber);
        return ResponseEntity.ok(ApiResponse.success("Account retrieved successfully", response));
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(
            Authentication authentication,
            @PathVariable String accountNumber) {
        accountLogic.deleteAccount(authentication.getName(), accountNumber);
        return ResponseEntity.ok(ApiResponse.success("Account deleted successfully", null));
    }
}
