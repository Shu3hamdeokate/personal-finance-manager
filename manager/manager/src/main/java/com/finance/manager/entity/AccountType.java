package com.finance.manager.entity;

import lombok.Getter;

@Getter
public enum AccountType {

    BANK("Bank Account"),
    CASH("Cash"),
    CREDIT_CARD("Credit Card"),
    INVESTMENT("Investment");

    private final String displayName;

    // Constructor to set the display name
    AccountType(String displayName) {
        this.displayName = displayName;
    }

    // Optional: Override toString to return the display name for easy logging/API output
    @Override
    public String toString() {
        return displayName;
    }
}

