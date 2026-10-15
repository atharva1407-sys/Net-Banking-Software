package com.securebank.auth.service;

import com.securebank.account.entity.BankAccount;
import com.securebank.account.repository.BankAccountRepository;
import com.securebank.auth.dto.AuthResponse;
import com.securebank.auth.dto.LoginRequest;
import com.securebank.auth.dto.RegisterRequest;
import com.securebank.common.exception.BusinessException;
import com.securebank.customer.entity.Customer;
import com.securebank.customer.entity.UserCredential;
import com.securebank.customer.repository.CustomerRepository;
import com.securebank.customer.repository.UserCredentialRepository;
import com.securebank.security.JwtService;
import com.securebank.transaction.entity.TransactionEntry;
import com.securebank.transaction.repository.TransactionEntryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final UserCredentialRepository userCredentialRepository;
    private final BankAccountRepository bankAccountRepository;
    private final TransactionEntryRepository transactionEntryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final SecureRandom secureRandom = new SecureRandom();

    public AuthService(CustomerRepository customerRepository,
                       UserCredentialRepository userCredentialRepository,
                       BankAccountRepository bankAccountRepository,
                       TransactionEntryRepository transactionEntryRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       UserDetailsService userDetailsService) {
        this.customerRepository = customerRepository;
        this.userCredentialRepository = userCredentialRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.transactionEntryRepository = transactionEntryRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userCredentialRepository.existsByUsername(req.getCustomerId())) {
            throw new BusinessException("User ID / Customer ID is already registered. Please choose another.");
        }
        if (customerRepository.existsByEmail(req.getEmail())) {
            throw new BusinessException("Email address is already registered.");
        }
        if (customerRepository.existsByMobile(req.getMobile())) {
            throw new BusinessException("Mobile number is already registered.");
        }

        // 1. Create Customer
        Customer customer = new Customer(req.getFullName().trim(), req.getEmail().trim(), req.getMobile().trim());
        customer = customerRepository.save(customer);

        // 2. Create Credential
        String encodedPassword = passwordEncoder.encode(req.getPassword());
        UserCredential credential = new UserCredential(customer, req.getCustomerId().trim(), encodedPassword, "ROLE_CUSTOMER");
        userCredentialRepository.save(credential);

        // 3. Automatically provision primary Savings Account with simulated opening bonus (₹10,000.00)
        String accountNumber = generateUniqueAccountNumber();
        BigDecimal initialBalance = new BigDecimal("10000.00");
        BankAccount account = new BankAccount(customer, accountNumber, "SAVINGS", initialBalance);
        account = bankAccountRepository.save(account);

        // 4. Record initial ledger entry
        String txRef = "TXN" + System.currentTimeMillis() + (1000 + secureRandom.nextInt(9000));
        TransactionEntry initialEntry = new TransactionEntry(
                null,
                account,
                txRef,
                "CREDIT",
                initialBalance,
                initialBalance,
                "Initial Welcome Deposit",
                "COMPLETED"
        );
        transactionEntryRepository.save(initialEntry);

        // 5. Generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(credential.getUsername());
        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                token,
                credential.getUsername(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getMobile(),
                credential.getRole(),
                customer.getId()
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest req) {
        UserCredential credential = userCredentialRepository.findByUsername(req.getCustomerId())
                .orElseThrow(() -> new BusinessException("Invalid Customer ID or password.", HttpStatus.UNAUTHORIZED));

        if (!credential.isEnabled()) {
            throw new BusinessException("Account is disabled. Please contact bank support.", HttpStatus.FORBIDDEN);
        }

        if (credential.getLockedUntil() != null && credential.getLockedUntil().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Account is temporarily locked due to multiple failed login attempts. Try again later.", HttpStatus.FORBIDDEN);
        }

        if (!passwordEncoder.matches(req.getPassword(), credential.getPasswordHash())) {
            int attempts = credential.getFailedAttempts() + 1;
            credential.setFailedAttempts(attempts);
            if (attempts >= 5) {
                credential.setLockedUntil(LocalDateTime.now().plusMinutes(15));
                userCredentialRepository.save(credential);
                throw new BusinessException("Account locked for 15 minutes due to 5 consecutive failed attempts.", HttpStatus.FORBIDDEN);
            }
            userCredentialRepository.save(credential);
            throw new BusinessException("Invalid Customer ID or password.", HttpStatus.UNAUTHORIZED);
        }

        // Reset failed attempts on success
        credential.setFailedAttempts(0);
        credential.setLockedUntil(null);
        userCredentialRepository.save(credential);

        UserDetails userDetails = userDetailsService.loadUserByUsername(credential.getUsername());
        String token = jwtService.generateToken(userDetails);
        Customer customer = credential.getCustomer();

        return new AuthResponse(
                token,
                credential.getUsername(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getMobile(),
                credential.getRole(),
                customer.getId()
        );
    }

    public Customer getAuthenticatedCustomer() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BusinessException("Unauthorized request.", HttpStatus.UNAUTHORIZED);
        }
        String username = auth.getName();
        UserCredential credential = userCredentialRepository.findByUsernameWithCustomer(username)
                .orElseThrow(() -> new BusinessException("Customer profile not found.", HttpStatus.NOT_FOUND));
        return credential.getCustomer();
    }

    private String generateUniqueAccountNumber() {
        String num;
        do {
            long randomNineDigits = 100000000L + secureRandom.nextLong(900000000L);
            num = "100" + randomNineDigits; // 12-digit netbanking account number
        } while (bankAccountRepository.existsByAccountNumber(num));
        return num;
    }
}
