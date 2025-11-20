package com.user.account.app.routes;

import com.user.account.app.dto.ApiResponse;
import com.user.account.app.dto.TransactionRequest;
import com.user.account.app.dto.TransactionResponse;
import com.user.account.app.logics.TransactionLogic;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionLogic transactionLogic;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            Authentication authentication,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionLogic.deposit(authentication.getName(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deposit successful", response));
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdrawal(
            Authentication authentication,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionLogic.withdrawal(authentication.getName(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Withdrawal successful", response));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            Authentication authentication,
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionLogic.transfer(authentication.getName(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transfer successful", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactionHistory(
            Authentication authentication,
            @RequestParam String accountNumber,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        Page<TransactionResponse> response = transactionLogic.getTransactionHistory(
                authentication.getName(),
                accountNumber,
                startDate,
                endDate,
                page,
                size,
                sortBy,
                sortDirection
        );

        return ResponseEntity.ok(ApiResponse.success("Transaction history retrieved successfully", response));
    }
}
