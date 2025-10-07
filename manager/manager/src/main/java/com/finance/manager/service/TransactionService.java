package com.finance.manager.service;

import com.finance.manager.dto.TransactionDTO;
import com.finance.manager.entity.Transaction;
import com.finance.manager.entity.User;
import com.finance.manager.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public List<TransactionDTO> getAllTransactions(User user) {
        return transactionRepository.findByUser(user)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TransactionDTO createTransaction(Transaction transaction, User user) {
        transaction.setUser(user);
        Transaction saved = transactionRepository.save(transaction);
        return toDTO(saved);
    }

    private TransactionDTO toDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccount().getId())
                .amount(transaction.getAmount())
                .type(transaction.getType().name())
                .category(transaction.getCategory())
                .description(transaction.getDescription())
                .date(transaction.getDate())
                .build();
    }
}

