package com.finance.manager.service;

import com.finance.manager.dto.TransactionRequest;
import com.finance.manager.dto.TransactionResponse;
import com.finance.manager.entity.*;
import com.finance.manager.repository.AccountRepository;
import com.finance.manager.repository.TransactionRepository;
import com.finance.manager.repository.UserRepository;
import com.finance.manager.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    /** Create a new transaction for the current user */
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + request.getAccountId()));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied: account does not belong to current user");
        }

        Transaction transaction = Transaction.builder()
                .account(account)
                .user(user)
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .description(request.getDescription())
                .date(request.getDate())
                .build();

        // Adjust account balance
        if (request.getType() == TransactionType.INCOME) {
            account.setBalance(account.getBalance().add(request.getAmount()));
        } else if (request.getType() == TransactionType.EXPENSE) {
            account.setBalance(account.getBalance().subtract(request.getAmount()));
        }

        accountRepository.save(account);
        Transaction saved = transactionRepository.save(transaction);

        return mapToResponse(saved);
    }

    /** Get all transactions for the current user */
    public List<TransactionResponse> getAllTransactions() {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return transactionRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /** Get all transactions for a specific account */
    public List<TransactionResponse> getTransactionsByAccount(Long accountId) {
        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Access denied: account does not belong to current user");
        }

        return transactionRepository.findByAccount(account)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse mapToResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccount().getId())
                .accountName(transaction.getAccount().getName())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .category(transaction.getCategory())
                .description(transaction.getDescription())
                .date(transaction.getDate())
                .build();
    }
}
