package com.securebank.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountResponse {
    private Long id;
    private String accountNumber;
    private String maskedAccountNumber;
    private String accountType;
    private String currency;
    private BigDecimal balance;
    private String status;
    private LocalDateTime createdAt;

    public AccountResponse() {}

    public AccountResponse(Long id, String accountNumber, String accountType, String currency,
                           BigDecimal balance, String status, LocalDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.maskedAccountNumber = maskNumber(accountNumber);
        this.accountType = accountType;
        this.currency = currency;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
    }

    private static String maskNumber(String num) {
        if (num == null || num.length() < 4) return "••••";
        return "•••• •••• " + num.substring(num.length() - 4);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getMaskedAccountNumber() { return maskedAccountNumber; }
    public void setMaskedAccountNumber(String maskedAccountNumber) { this.maskedAccountNumber = maskedAccountNumber; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
