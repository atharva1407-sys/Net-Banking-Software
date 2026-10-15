package com.securebank.account.repository;

import com.securebank.account.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByCustomerId(Long customerId);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    Optional<BankAccount> findByIdAndCustomerId(Long id, Long customerId);
    boolean existsByAccountNumber(String accountNumber);
}
