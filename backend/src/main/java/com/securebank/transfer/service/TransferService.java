package com.securebank.transfer.service;

import com.securebank.account.entity.BankAccount;
import com.securebank.account.repository.BankAccountRepository;
import com.securebank.auth.service.AuthService;
import com.securebank.common.exception.BusinessException;
import com.securebank.customer.entity.Customer;
import com.securebank.notification.entity.Notification;
import com.securebank.notification.repository.NotificationRepository;
import com.securebank.transaction.entity.TransactionEntry;
import com.securebank.transaction.repository.TransactionEntryRepository;
import com.securebank.transfer.dto.TransferRequest;
import com.securebank.transfer.dto.TransferResponse;
import com.securebank.transfer.entity.Transfer;
import com.securebank.transfer.repository.TransferRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class TransferService {

    private final BankAccountRepository bankAccountRepository;
    private final TransferRepository transferRepository;
    private final TransactionEntryRepository transactionEntryRepository;
    private final NotificationRepository notificationRepository;
    private final AuthService authService;
    private final SecureRandom secureRandom = new SecureRandom();

    public TransferService(BankAccountRepository bankAccountRepository,
                           TransferRepository transferRepository,
                           TransactionEntryRepository transactionEntryRepository,
                           NotificationRepository notificationRepository,
                           AuthService authService) {
        this.bankAccountRepository = bankAccountRepository;
        this.transferRepository = transferRepository;
        this.transactionEntryRepository = transactionEntryRepository;
        this.notificationRepository = notificationRepository;
        this.authService = authService;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TransferResponse executeTransfer(TransferRequest req) {
        Customer customer = authService.getAuthenticatedCustomer();

        // 1. Idempotency verification if key provided
        if (req.getIdempotencyKey() != null && !req.getIdempotencyKey().isBlank()) {
            var existing = transferRepository.findByIdempotencyKey(req.getIdempotencyKey());
            if (existing.isPresent()) {
                Transfer t = existing.get();
                return new TransferResponse(
                        t.getTransferReference(),
                        t.getSourceAccount().getAccountNumber(),
                        t.getDestinationAccount().getAccountNumber(),
                        t.getAmount(),
                        t.getStatus(),
                        t.getDescription(),
                        t.getSourceAccount().getBalance(),
                        t.getCreatedAt()
                );
            }
        }

        // 2. Fetch & Validate Source Account (ownership & status)
        BankAccount sourceAccount = bankAccountRepository.findByIdAndCustomerId(req.getSourceAccountId(), customer.getId())
                .orElseThrow(() -> new BusinessException("Source account not found or unauthorized.", HttpStatus.FORBIDDEN));

        if (!"ACTIVE".equalsIgnoreCase(sourceAccount.getStatus())) {
            throw new BusinessException("Source account is " + sourceAccount.getStatus() + " and cannot initiate transfers.");
        }

        // 3. Fetch & Validate Destination Account
        BankAccount destinationAccount = bankAccountRepository.findByAccountNumber(req.getDestinationAccountNumber().trim())
                .orElseThrow(() -> new BusinessException("Beneficiary account number does not exist."));

        if (!"ACTIVE".equalsIgnoreCase(destinationAccount.getStatus())) {
            throw new BusinessException("Destination account is currently inactive or frozen.");
        }

        // 4. Cannot transfer to same account
        if (sourceAccount.getId().equals(destinationAccount.getId())) {
            throw new BusinessException("Source and destination accounts cannot be identical.");
        }

        // 5. Balance check
        BigDecimal amount = req.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Transfer amount must be strictly greater than zero.");
        }

        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new BusinessException("Insufficient funds. Available balance: ₹" + sourceAccount.getBalance());
        }

        // 6. Update balances atomically
        BigDecimal newSourceBalance = sourceAccount.getBalance().subtract(amount);
        sourceAccount.setBalance(newSourceBalance);
        sourceAccount.setUpdatedAt(LocalDateTime.now());
        bankAccountRepository.save(sourceAccount);

        BigDecimal newDestBalance = destinationAccount.getBalance().add(amount);
        destinationAccount.setBalance(newDestBalance);
        destinationAccount.setUpdatedAt(LocalDateTime.now());
        bankAccountRepository.save(destinationAccount);

        // 7. Record Transfer entity
        String txRef = "TRF" + System.currentTimeMillis() + (1000 + secureRandom.nextInt(9000));
        String desc = (req.getDescription() != null && !req.getDescription().isBlank())
                ? req.getDescription().trim()
                : "Fund transfer to A/C " + mask(destinationAccount.getAccountNumber());

        Transfer transfer = new Transfer(
                txRef,
                sourceAccount,
                destinationAccount,
                amount,
                desc,
                "SUCCESS",
                req.getIdempotencyKey()
        );
        transfer = transferRepository.save(transfer);

        // 8. Balanced Double-Entry Ledger
        // Debit source account
        TransactionEntry debitEntry = new TransactionEntry(
                transfer,
                sourceAccount,
                txRef,
                "DEBIT",
                amount,
                newSourceBalance,
                desc,
                "COMPLETED"
        );
        transactionEntryRepository.save(debitEntry);

        // Credit destination account
        TransactionEntry creditEntry = new TransactionEntry(
                transfer,
                destinationAccount,
                txRef,
                "CREDIT",
                amount,
                newDestBalance,
                "Fund received from A/C " + mask(sourceAccount.getAccountNumber()),
                "COMPLETED"
        );
        transactionEntryRepository.save(creditEntry);

        // 9. Notifications
        Notification senderNotification = new Notification(
                customer,
                "Fund Transfer Successful",
                String.format("₹%.2f transferred to A/C %s. Ref: %s", amount, mask(destinationAccount.getAccountNumber()), txRef),
                "TRANSFER_SUCCESS"
        );
        notificationRepository.save(senderNotification);

        if (!destinationAccount.getCustomer().getId().equals(customer.getId())) {
            Notification recipientNotification = new Notification(
                    destinationAccount.getCustomer(),
                    "Fund Received",
                    String.format("₹%.2f received from %s into A/C %s. Ref: %s", amount, customer.getFullName(), mask(destinationAccount.getAccountNumber()), txRef),
                    "INFO"
            );
            notificationRepository.save(recipientNotification);
        }

        return new TransferResponse(
                txRef,
                sourceAccount.getAccountNumber(),
                destinationAccount.getAccountNumber(),
                amount,
                "SUCCESS",
                desc,
                newSourceBalance,
                transfer.getCreatedAt()
        );
    }

    private String mask(String num) {
        if (num == null || num.length() < 4) return "••••";
        return "••••" + num.substring(num.length() - 4);
    }
}
