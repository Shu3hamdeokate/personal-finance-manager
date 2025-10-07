package com.finance.manager.service;

import com.finance.manager.dto.AccountDTO;
import com.finance.manager.entity.Account;
import com.finance.manager.entity.User;
import com.finance.manager.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public List<AccountDTO> getAllAccounts(User user) {
        return accountRepository.findByUser(user)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AccountDTO createAccount(Account account, User user) {
        account.setUser(user);
        Account saved = accountRepository.save(account);
        return toDTO(saved);
    }

    private AccountDTO toDTO(Account account) {
        return AccountDTO.builder()
                .id(account.getId())
                .name(account.getName())
                .type(account.getType().name())
                .balance(account.getBalance())
                .build();
    }
}

