package com.finance.manager.repository;

import com.finance.manager.entity.Account;
import com.finance.manager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);
}

