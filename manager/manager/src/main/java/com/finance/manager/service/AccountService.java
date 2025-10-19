package com.finance.manager.service;

import com.finance.manager.dto.AccountRequest;
import com.finance.manager.dto.AccountResponse;
import com.finance.manager.entity.Account;
import com.finance.manager.entity.User;
import com.finance.manager.exception.ResourceAccessException;
import com.finance.manager.repository.AccountRepository;
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
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public AccountResponse createAccount(AccountRequest request) {
        User user = getCurrentUser();

        Account account = Account.builder()
                .name(request.getName())
                .type(request.getType())
                .balance(request.getBalance())
                .user(user)
                .build();

        Account saved = accountRepository.save(account);
        return mapToResponse(saved);
    }

    public List<AccountResponse> getAccountsForCurrentUser() {
        User user = getCurrentUser();
        return accountRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccountById(Long accountId) {
        User user = getCurrentUser();
        Account account = getAccountAndVerifyOwnership(accountId, user);
        return mapToResponse(account);
    }

    @Transactional
    public AccountResponse updateAccount(Long accountId, AccountRequest request) {
        User user = getCurrentUser();
        Account account = getAccountAndVerifyOwnership(accountId, user);

        account.setName(request.getName());
        account.setType(request.getType());
        account.setBalance(request.getBalance());

        Account updated = accountRepository.save(account);
        return mapToResponse(updated);
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        User user = getCurrentUser();
        Account account = getAccountAndVerifyOwnership(accountId, user);
        accountRepository.delete(account);
    }

    private User getCurrentUser() {
        String email = SecurityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user not found: " + email));
    }

    private Account getAccountAndVerifyOwnership(Long accountId, User currentUser) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found with id: " + accountId));

        if (!account.getUser().getId().equals(currentUser.getId())) {
            throw new ResourceAccessException("Access denied: Account does not belong to the current user.");
        }
        return account;
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType())
                .balance(account.getBalance())
                .build();
    }
}