package com.user.account.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    @NotBlank(message = "Account name is required")
    private String accountName;

    @NotNull(message = "Initial balance is required")
    @PositiveOrZero(message = "Initial balance must be zero or positive")
    private BigDecimal initialBalance;
}
