package com.securebank;

import com.securebank.account.dto.AccountResponse;
import com.securebank.account.entity.BankAccount;
import com.securebank.account.repository.BankAccountRepository;
import com.securebank.account.service.AccountService;
import com.securebank.auth.dto.AuthResponse;
import com.securebank.auth.dto.LoginRequest;
import com.securebank.auth.dto.RegisterRequest;
import com.securebank.auth.service.AuthService;
import com.securebank.common.exception.BusinessException;
import com.securebank.customer.entity.Customer;
import com.securebank.customer.entity.UserCredential;
import com.securebank.customer.repository.CustomerRepository;
import com.securebank.customer.repository.UserCredentialRepository;
import com.securebank.security.JwtService;
import com.securebank.transaction.repository.TransactionEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private TransactionEntryRepository transactionEntryRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    private Customer testCustomer;
    private UserCredential testCredential;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer("Aarav Sharma", "aarav@example.com", "9876543210");
        testCustomer.setId(1L);
        testCredential = new UserCredential(testCustomer, "customer101", "hashedPass", "ROLE_CUSTOMER");
    }

    @Test
    void testSuccessfulLogin() {
        LoginRequest req = new LoginRequest("customer101", "Password@123");
        when(userCredentialRepository.findByUsername("customer101")).thenReturn(Optional.of(testCredential));
        when(passwordEncoder.matches("Password@123", "hashedPass")).thenReturn(true);

        UserDetails userDetails = new User("customer101", "hashedPass", Collections.emptyList());
        when(userDetailsService.loadUserByUsername("customer101")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mock.jwt.token");

        AuthResponse res = authService.login(req);

        assertNotNull(res);
        assertEquals("mock.jwt.token", res.getToken());
        assertEquals("customer101", res.getCustomerId());
        assertEquals("Aarav Sharma", res.getFullName());
    }

    @Test
    void testInvalidPasswordThrowsException() {
        LoginRequest req = new LoginRequest("customer101", "WrongPassword");
        when(userCredentialRepository.findByUsername("customer101")).thenReturn(Optional.of(testCredential));
        when(passwordEncoder.matches("WrongPassword", "hashedPass")).thenReturn(false);

        assertThrows(BusinessException.class, () -> authService.login(req));
        assertEquals(1, testCredential.getFailedAttempts());
    }

    @Test
    void testDuplicateUsernameRegistrationThrowsException() {
        RegisterRequest req = new RegisterRequest();
        req.setCustomerId("customer101");
        req.setFullName("Aarav Sharma");
        req.setEmail("aarav@example.com");
        req.setMobile("9876543210");
        req.setPassword("Password@123");

        when(userCredentialRepository.existsByUsername("customer101")).thenReturn(true);

        assertThrows(BusinessException.class, () -> authService.register(req));
    }
}
