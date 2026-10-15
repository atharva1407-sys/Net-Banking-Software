package com.securebank.config;

import com.securebank.account.entity.BankAccount;
import com.securebank.account.repository.BankAccountRepository;
import com.securebank.beneficiary.entity.Beneficiary;
import com.securebank.beneficiary.repository.BeneficiaryRepository;
import com.securebank.customer.entity.Customer;
import com.securebank.customer.entity.UserCredential;
import com.securebank.customer.repository.CustomerRepository;
import com.securebank.customer.repository.UserCredentialRepository;
import com.securebank.notification.entity.Notification;
import com.securebank.notification.repository.NotificationRepository;
import com.securebank.transaction.entity.TransactionEntry;
import com.securebank.transaction.repository.TransactionEntryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CustomerRepository customerRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionEntryRepository transactionEntryRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final NotificationRepository notificationRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CustomerRepository customerRepository,
                           UserCredentialRepository userCredentialRepository,
                           BankAccountRepository bankAccountRepository,
                           TransactionEntryRepository transactionEntryRepository,
                           BeneficiaryRepository beneficiaryRepository,
                           NotificationRepository notificationRepository,
                           PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionEntryRepository = transactionEntryRepository;
        this.beneficiaryRepository = beneficiaryRepository;
        this.notificationRepository = notificationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (customerRepository.count() > 0) {
            return; // Already initialized
        }

        // Demo User 1: Aarav Sharma (customer101)
        Customer c1 = new Customer("Aarav Sharma", "aarav.sharma@example.com", "9876543210");
        c1 = customerRepository.save(c1);

        UserCredential cred1 = new UserCredential(c1, "customer101", passwordEncoder.encode("Password@123"), "ROLE_CUSTOMER");
        userCredentialRepository.save(cred1);

        BankAccount acc1 = new BankAccount(c1, "1001002345", "SAVINGS", new BigDecimal("50000.00"));
        acc1 = bankAccountRepository.save(acc1);

        // Demo User 2: Priya Patel (customer102)
        Customer c2 = new Customer("Priya Patel", "priya.patel@example.com", "9876543211");
        c2 = customerRepository.save(c2);

        UserCredential cred2 = new UserCredential(c2, "customer102", passwordEncoder.encode("Password@123"), "ROLE_CUSTOMER");
        userCredentialRepository.save(cred2);

        BankAccount acc2 = new BankAccount(c2, "1001004826", "CURRENT", new BigDecimal("120000.00"));
        acc2 = bankAccountRepository.save(acc2);

        // Initial Ledger Entries for Aarav
        TransactionEntry t1 = new TransactionEntry(
                null, acc1, "TXN10001001", "CREDIT",
                new BigDecimal("50000.00"), new BigDecimal("50000.00"),
                "Opening Salary Deposit", "COMPLETED"
        );
        t1.setCreatedAt(LocalDateTime.now().minusDays(5));
        transactionEntryRepository.save(t1);

        // Saved Beneficiary for Aarav -> Priya
        Beneficiary b1 = new Beneficiary(c1, "Priya Patel", "1001004826", "SecureBank", "SECU0001001", "Priya (Consultant)");
        beneficiaryRepository.save(b1);

        // Saved Beneficiary for Priya -> Aarav
        Beneficiary b2 = new Beneficiary(c2, "Aarav Sharma", "1001002345", "SecureBank", "SECU0001001", "Aarav Personal");
        beneficiaryRepository.save(b2);

        // Welcome Notification for Aarav
        Notification n1 = new Notification(
                c1,
                "Welcome to SecureBank",
                "Your NetBanking profile and Everyday Savings account (A/C •••• 2345) are active.",
                "INFO"
        );
        notificationRepository.save(n1);

        System.out.println("==================================================================");
        System.out.println(">>> SecureBank Synthetic Seed Data Initialized Successfully! <<<");
        System.out.println("Demo Customer 1: ID = customer101 | Password = Password@123");
        System.out.println("Demo Customer 2: ID = customer102 | Password = Password@123");
        System.out.println("==================================================================");
    }
}
