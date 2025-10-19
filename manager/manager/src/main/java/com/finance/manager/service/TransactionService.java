package com.finance.manager.service;

import com.finance.manager.dto.TransactionRequest;
import com.finance.manager.dto.TransactionResponse;
import com.finance.manager.entity.Account;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.TransactionType;
import com.finance.manager.entity.User;
import com.finance.manager.exception.ResourceAccessException;
import com.finance.manager.repository.AccountRepository;
import com.finance.manager.repository.TransactionRepository;
import com.finance.manager.repository.UserRepository;
import com.finance.manager.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    /** * Creates a new transaction and atomically updates the associated account balance.
     */
    @Transactional
    public TransactionResponse createTransaction(TransactionRequest request) {
        User user = getCurrentUser();
        Account account = getAccountAndVerifyOwnership(request.getAccountId(), user);

        BigDecimal newBalance;
        if (request.getType() == TransactionType.INCOME) {
            newBalance = account.getBalance().add(request.getAmount());
        } else if (request.getType() == TransactionType.EXPENSE) {
            newBalance = account.getBalance().subtract(request.getAmount());
        } else {
            throw new IllegalArgumentException("Invalid transaction type.");
        }

        account.setBalance(newBalance);
        accountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .account(account)
                .user(user)
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .description(request.getDescription())
                .date(request.getDate())
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return mapToResponse(saved);
    }

    /** * Get all transactions for the current user.
     */
    public List<TransactionResponse> getAllTransactions() {
        User user = getCurrentUser();
        return transactionRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /** * Get all transactions for a specific account, verifying ownership.
     */
    public List<TransactionResponse> getTransactionsByAccount(Long accountId) {
        User user = getCurrentUser();
        Account account = getAccountAndVerifyOwnership(accountId, user);

        return transactionRepository.findByAccount(account)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found."));
    }

    private Account getAccountAndVerifyOwnership(Long accountId, User currentUser) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));

        if (!account.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceAccessException("Access denied: Account does not belong to the current user.");
        }
        return account;
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