package com.finance.manager.dto;

import com.finance.manager.entity.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class TransactionRequest {

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required (INCOME/EXPENSE)")
    private TransactionType type;

    @NotBlank(message = "Category is required")
    @Size(max = 50, message = "Category name too long")
    private String category;

    @NotBlank(message = "Description is required")
    @Size(max = 255, message = "Description too long")
    private String description;

    @NotNull(message = "Date is required")
    private LocalDate date;
}