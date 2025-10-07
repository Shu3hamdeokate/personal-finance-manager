package com.finance.manager.controller;

import com.finance.manager.dto.TransactionDTO;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // NOTE: Replace with actual authenticated user retrieval
    private User getCurrentUser() {
        // TODO: integrate JWT authentication
        return new User(); // placeholder
    }

    @GetMapping
    public List<TransactionDTO> getTransactions() {
        return transactionService.getAllTransactions(getCurrentUser());
    }

    @PostMapping
    public TransactionDTO createTransaction(@RequestBody Transaction transaction) {
        return transactionService.createTransaction(transaction, getCurrentUser());
    }
}
