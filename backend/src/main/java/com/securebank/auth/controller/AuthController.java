package com.securebank.auth.controller;

import com.securebank.auth.dto.AuthResponse;
import com.securebank.auth.dto.LoginRequest;
import com.securebank.auth.dto.RegisterRequest;
import com.securebank.auth.service.AuthService;
import com.securebank.common.dto.ApiResponse;
import com.securebank.customer.entity.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Customer registration, login, and session identity")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new netbanking customer", description = "Creates a customer profile, user credential, primary bank account, and returns JWT.")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.ok("Registration successful. Welcome to SecureBank NetBanking!", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Customer login", description = "Authenticates using Customer ID and password. Returns JWT token.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful.", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated customer", description = "Returns customer profile derived directly from security context.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentUser() {
        Customer customer = authService.getAuthenticatedCustomer();
        Map<String, Object> data = Map.of(
                "id", customer.getId(),
                "fullName", customer.getFullName(),
                "email", customer.getEmail(),
                "mobile", customer.getMobile(),
                "status", customer.getStatus(),
                "createdAt", customer.getCreatedAt()
        );
        return ResponseEntity.ok(ApiResponse.ok(data));
    }

    @PostMapping("/logout")
    @Operation(summary = "Customer logout", description = "Acknowledges customer logout.")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully.", null));
    }
}
