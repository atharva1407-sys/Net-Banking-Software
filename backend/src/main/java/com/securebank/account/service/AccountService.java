package com.securebank.account.service;

import com.securebank.account.dto.AccountResponse;
import com.securebank.account.entity.BankAccount;
import com.securebank.account.repository.BankAccountRepository;
import com.securebank.auth.service.AuthService;
import com.securebank.common.exception.BusinessException;
import com.securebank.customer.entity.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    private final BankAccountRepository bankAccountRepository;
    private final AuthService authService;
    private final SecureRandom secureRandom = new SecureRandom();

    public AccountService(BankAccountRepository bankAccountRepository, AuthService authService) {
        this.bankAccountRepository = bankAccountRepository;
        this.authService = authService;
    }

    public List<AccountResponse> getCustomerAccounts() {
        Customer customer = authService.getAuthenticatedCustomer();
        return bankAccountRepository.findByCustomerId(customer.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccountById(Long accountId) {
        Customer customer = authService.getAuthenticatedCustomer();
        BankAccount account = bankAccountRepository.findByIdAndCustomerId(accountId, customer.getId())
                .orElseThrow(() -> new BusinessException("Account not found or unauthorized.", HttpStatus.FORBIDDEN));
        return toResponse(account);
    }

    public BigDecimal getAccountBalance(Long accountId) {
        Customer customer = authService.getAuthenticatedCustomer();
        BankAccount account = bankAccountRepository.findByIdAndCustomerId(accountId, customer.getId())
                .orElseThrow(() -> new BusinessException("Account not found or unauthorized.", HttpStatus.FORBIDDEN));
        return account.getBalance();
    }

    @Transactional
    public AccountResponse createAccount(String type) {
        Customer customer = authService.getAuthenticatedCustomer();
        String accountType = "CURRENT".equalsIgnoreCase(type) ? "CURRENT" : "SAVINGS";

        String accountNumber = generateUniqueAccountNumber();
        BankAccount account = new BankAccount(customer, accountNumber, accountType, BigDecimal.ZERO);
        account = bankAccountRepository.save(account);
        return toResponse(account);
    }

    public AccountResponse toResponse(BankAccount account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getCurrency(),
                account.getBalance(),
                account.getStatus(),
                account.getCreatedAt()
        );
    }

    private String generateUniqueAccountNumber() {
        String num;
        do {
            long randomNineDigits = 100000000L + secureRandom.nextLong(900000000L);
            num = "100" + randomNineDigits;
        } while (bankAccountRepository.existsByAccountNumber(num));
        return num;
    }
}
