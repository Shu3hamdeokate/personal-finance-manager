package com.finance.manager.dto;

import com.finance.manager.entity.AccountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class AccountRequest {

    @NotBlank(message = "Account name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Account type is required")
    private AccountType type;

    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.00", inclusive = false, message = "Balance must be a positive number")
    private BigDecimal balance; // Using BigDecimal for precision
}