package com.securebank;

import com.securebank.account.entity.BankAccount;
import com.securebank.account.repository.BankAccountRepository;
import com.securebank.auth.service.AuthService;
import com.securebank.common.exception.BusinessException;
import com.securebank.customer.entity.Customer;
import com.securebank.notification.repository.NotificationRepository;
import com.securebank.transaction.repository.TransactionEntryRepository;
import com.securebank.transfer.dto.TransferRequest;
import com.securebank.transfer.dto.TransferResponse;
import com.securebank.transfer.entity.Transfer;
import com.securebank.transfer.repository.TransferRepository;
import com.securebank.transfer.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private TransactionEntryRepository transactionEntryRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private TransferService transferService;

    private Customer sender;
    private Customer recipient;
    private BankAccount sourceAccount;
    private BankAccount destAccount;

    @BeforeEach
    void setUp() {
        sender = new Customer("Aarav Sharma", "aarav@example.com", "9876543210");
        sender.setId(1L);

        recipient = new Customer("Priya Patel", "priya@example.com", "9876543211");
        recipient.setId(2L);

        sourceAccount = new BankAccount(sender, "1001002345", "SAVINGS", new BigDecimal("5000.00"));
        sourceAccount.setId(10L);

        destAccount = new BankAccount(recipient, "1001004826", "CURRENT", new BigDecimal("1000.00"));
        destAccount.setId(20L);
    }

    @Test
    void testSuccessfulTransfer() {
        when(authService.getAuthenticatedCustomer()).thenReturn(sender);
        when(bankAccountRepository.findByIdAndCustomerId(10L, 1L)).thenReturn(Optional.of(sourceAccount));
        when(bankAccountRepository.findByAccountNumber("1001004826")).thenReturn(Optional.of(destAccount));
        when(transferRepository.save(any(Transfer.class))).thenAnswer(i -> i.getArguments()[0]);

        TransferRequest req = new TransferRequest();
        req.setSourceAccountId(10L);
        req.setDestinationAccountNumber("1001004826");
        req.setAmount(new BigDecimal("2000.00"));
        req.setDescription("Rent payment");

        TransferResponse res = transferService.executeTransfer(req);

        assertNotNull(res);
        assertEquals("SUCCESS", res.getStatus());
        assertEquals(new BigDecimal("3000.00"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("3000.00"), destAccount.getBalance());
        assertEquals(new BigDecimal("3000.00"), res.getUpdatedSourceBalance());
    }

    @Test
    void testInsufficientBalanceThrowsException() {
        when(authService.getAuthenticatedCustomer()).thenReturn(sender);
        when(bankAccountRepository.findByIdAndCustomerId(10L, 1L)).thenReturn(Optional.of(sourceAccount));
        when(bankAccountRepository.findByAccountNumber("1001004826")).thenReturn(Optional.of(destAccount));

        TransferRequest req = new TransferRequest();
        req.setSourceAccountId(10L);
        req.setDestinationAccountNumber("1001004826");
        req.setAmount(new BigDecimal("9999.00")); // More than 5000

        assertThrows(BusinessException.class, () -> transferService.executeTransfer(req));
        assertEquals(new BigDecimal("5000.00"), sourceAccount.getBalance());
    }

    @Test
    void testTransferToSameAccountThrowsException() {
        when(authService.getAuthenticatedCustomer()).thenReturn(sender);
        when(bankAccountRepository.findByIdAndCustomerId(10L, 1L)).thenReturn(Optional.of(sourceAccount));
        when(bankAccountRepository.findByAccountNumber("1001002345")).thenReturn(Optional.of(sourceAccount));

        TransferRequest req = new TransferRequest();
        req.setSourceAccountId(10L);
        req.setDestinationAccountNumber("1001002345");
        req.setAmount(new BigDecimal("500.00"));

        assertThrows(BusinessException.class, () -> transferService.executeTransfer(req));
    }
}
