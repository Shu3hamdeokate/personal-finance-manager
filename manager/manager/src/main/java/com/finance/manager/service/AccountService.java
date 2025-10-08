package com.finance.manager.service;

import com.finance.manager.dto.AccountRequest;
import com.finance.manager.dto.AccountResponse;
import com.finance.manager.entity.Account;
import com.finance.manager.entity.User;
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

        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user email not found: " + email));

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

        String email = SecurityUtils.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Authenticated user email not found: " + email));

        return accountRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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