package com.finance.manager.controller;

import com.finance.manager.dto.AccountDTO;
import com.finance.manager.entity.Account;
import com.finance.manager.entity.User;
import com.finance.manager.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    // NOTE: Replace with actual authenticated user retrieval
    private User getCurrentUser() {
        // TODO: integrate JWT authentication
        return new User(); // placeholder
    }

    @GetMapping
    public List<AccountDTO> getAccounts() {
        return accountService.getAllAccounts(getCurrentUser());
    }

    @PostMapping
    public AccountDTO createAccount(@RequestBody Account account) {
        return accountService.createAccount(account, getCurrentUser());
    }
}

