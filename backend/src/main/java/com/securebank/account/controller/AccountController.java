package com.securebank.account.controller;

import com.securebank.account.dto.AccountResponse;
import com.securebank.account.service.AccountService;
import com.securebank.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Accounts", description = "Customer bank account listing, balances, and opening")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    @Operation(summary = "Get all customer accounts", description = "Returns active bank accounts for the authenticated customer.")
    public ResponseEntity<ApiResponse<List<AccountResponse>>> getAccounts() {
        return ResponseEntity.ok(ApiResponse.ok(accountService.getCustomerAccounts()));
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get account details by ID", description = "Returns single account details, enforcing customer authorization.")
    public ResponseEntity<ApiResponse<AccountResponse>> getAccountById(@PathVariable Long accountId) {
        return ResponseEntity.ok(ApiResponse.ok(accountService.getAccountById(accountId)));
    }

    @GetMapping("/{accountId}/balance")
    @Operation(summary = "Get real-time balance", description = "Authoritative balance check on customer account.")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getBalance(@PathVariable Long accountId) {
        BigDecimal balance = accountService.getAccountBalance(accountId);
        return ResponseEntity.ok(ApiResponse.ok(Map.of("accountId", accountId, "balance", balance, "currency", "INR")));
    }

    @PostMapping
    @Operation(summary = "Open a new bank account", description = "Allows opening a secondary savings or current account.")
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@RequestParam(defaultValue = "SAVINGS") String type) {
        return ResponseEntity.ok(ApiResponse.ok("Account opened successfully", accountService.createAccount(type)));
    }
}
