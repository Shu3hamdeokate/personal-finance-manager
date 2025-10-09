package com.finance.manager.dto;

import com.finance.manager.entity.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TransactionRequest {
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private String description;
    private LocalDateTime date;
    private Long accountId;
}
