package com.finance.manager.dto;

import com.finance.manager.entity.AccountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String name;
    private AccountType type;
    private BigDecimal balance;
}