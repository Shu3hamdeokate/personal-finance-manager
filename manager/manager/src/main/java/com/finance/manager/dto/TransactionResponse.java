package com.finance.manager.dto;

import com.finance.manager.entity.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Builder
public class TransactionResponse {
    private Long id;
    private Long accountId;
    private String accountName;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private String description;
    private LocalDate date;
}