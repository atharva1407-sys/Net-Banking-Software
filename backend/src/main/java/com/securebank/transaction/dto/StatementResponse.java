package com.securebank.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class StatementResponse {
    private String accountNumber;
    private String accountType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;
    private List<TransactionResponse> transactions;

    public StatementResponse() {}

    public StatementResponse(String accountNumber, String accountType, LocalDate startDate, LocalDate endDate,
                             BigDecimal openingBalance, BigDecimal closingBalance, BigDecimal totalDebits,
                             BigDecimal totalCredits, List<TransactionResponse> transactions) {
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.openingBalance = openingBalance;
        this.closingBalance = closingBalance;
        this.totalDebits = totalDebits;
        this.totalCredits = totalCredits;
        this.transactions = transactions;
    }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }

    public BigDecimal getClosingBalance() { return closingBalance; }
    public void setClosingBalance(BigDecimal closingBalance) { this.closingBalance = closingBalance; }

    public BigDecimal getTotalDebits() { return totalDebits; }
    public void setTotalDebits(BigDecimal totalDebits) { this.totalDebits = totalDebits; }

    public BigDecimal getTotalCredits() { return totalCredits; }
    public void setTotalCredits(BigDecimal totalCredits) { this.totalCredits = totalCredits; }

    public List<TransactionResponse> getTransactions() { return transactions; }
    public void setTransactions(List<TransactionResponse> transactions) { this.transactions = transactions; }
}
