package com.securebank.transaction.service;

import com.securebank.account.entity.BankAccount;
import com.securebank.account.repository.BankAccountRepository;
import com.securebank.auth.service.AuthService;
import com.securebank.common.exception.BusinessException;
import com.securebank.customer.entity.Customer;
import com.securebank.transaction.dto.StatementResponse;
import com.securebank.transaction.dto.TransactionResponse;
import com.securebank.transaction.entity.TransactionEntry;
import com.securebank.transaction.repository.TransactionEntryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionEntryRepository transactionEntryRepository;
    private final BankAccountRepository bankAccountRepository;
    private final AuthService authService;

    public TransactionService(TransactionEntryRepository transactionEntryRepository,
                              BankAccountRepository bankAccountRepository,
                              AuthService authService) {
        this.transactionEntryRepository = transactionEntryRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.authService = authService;
    }

    public Page<TransactionResponse> getTransactions(Long accountId, int page, int size) {
        Customer customer = authService.getAuthenticatedCustomer();
        Pageable pageable = PageRequest.of(page, size);

        if (accountId != null) {
            // Validate ownership
            BankAccount account = bankAccountRepository.findByIdAndCustomerId(accountId, customer.getId())
                    .orElseThrow(() -> new BusinessException("Account not found or unauthorized.", HttpStatus.FORBIDDEN));
            return transactionEntryRepository.findByAccountIdOrderByCreatedAtDesc(account.getId(), pageable)
                    .map(this::toResponse);
        } else {
            return transactionEntryRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId(), pageable)
                    .map(this::toResponse);
        }
    }

    public StatementResponse generateStatement(Long accountId, LocalDate startDate, LocalDate endDate) {
        Customer customer = authService.getAuthenticatedCustomer();

        BankAccount account = bankAccountRepository.findByIdAndCustomerId(accountId, customer.getId())
                .orElseThrow(() -> new BusinessException("Account not found or unauthorized.", HttpStatus.FORBIDDEN));

        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<TransactionEntry> entries = transactionEntryRepository
                .findByAccountIdAndCreatedAtBetweenOrderByCreatedAtAsc(account.getId(), startDateTime, endDateTime);

        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (TransactionEntry e : entries) {
            if ("DEBIT".equalsIgnoreCase(e.getDirection())) {
                totalDebits = totalDebits.add(e.getAmount());
            } else {
                totalCredits = totalCredits.add(e.getAmount());
            }
        }

        // Calculate opening and closing balance
        BigDecimal closingBalance = entries.isEmpty() ? account.getBalance() : entries.get(entries.size() - 1).getBalanceAfter();
        BigDecimal netChange = totalCredits.subtract(totalDebits);
        BigDecimal openingBalance = closingBalance.subtract(netChange);

        List<TransactionResponse> txDtos = entries.stream().map(this::toResponse).collect(Collectors.toList());

        return new StatementResponse(
                account.getAccountNumber(),
                account.getAccountType(),
                startDate,
                endDate,
                openingBalance,
                closingBalance,
                totalDebits,
                totalCredits,
                txDtos
        );
    }

    public String generateCsvStatement(Long accountId, LocalDate startDate, LocalDate endDate) {
        StatementResponse statement = generateStatement(accountId, startDate, endDate);
        StringBuilder sb = new StringBuilder();
        sb.append("SecureBank Account Statement\n");
        sb.append("Account Number:,").append(statement.getAccountNumber()).append("\n");
        sb.append("Account Type:,").append(statement.getAccountType()).append("\n");
        sb.append("Period:,").append(statement.getStartDate()).append(" to ").append(statement.getEndDate()).append("\n");
        sb.append("Opening Balance:,").append(statement.getOpeningBalance()).append("\n");
        sb.append("Closing Balance:,").append(statement.getClosingBalance()).append("\n");
        sb.append("Total Debits:,").append(statement.getTotalDebits()).append("\n");
        sb.append("Total Credits:,").append(statement.getTotalCredits()).append("\n\n");

        sb.append("Date,Reference,Description,Direction,Amount,Balance After,Status\n");
        for (TransactionResponse tx : statement.getTransactions()) {
            sb.append(tx.getCreatedAt()).append(",")
              .append(tx.getTransactionReference()).append(",")
              .append("\"").append(tx.getDescription().replace("\"", "\"\"")).append("\",")
              .append(tx.getDirection()).append(",")
              .append(tx.getAmount()).append(",")
              .append(tx.getBalanceAfter()).append(",")
              .append(tx.getStatus()).append("\n");
        }
        return sb.toString();
    }

    public TransactionResponse toResponse(TransactionEntry entry) {
        return new TransactionResponse(
                entry.getId(),
                entry.getTransactionReference(),
                entry.getAccount().getAccountNumber(),
                entry.getDirection(),
                entry.getAmount(),
                entry.getBalanceAfter(),
                entry.getDescription(),
                entry.getStatus(),
                entry.getCreatedAt()
        );
    }
}
