package com.securebank.transaction.controller;

import com.securebank.common.dto.ApiResponse;
import com.securebank.transaction.dto.StatementResponse;
import com.securebank.transaction.dto.TransactionResponse;
import com.securebank.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Transactions & Statements", description = "Transaction history and account statement generation")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    @Operation(summary = "Get transaction history", description = "Paginated list of transactions with optional account filter.")
    public ResponseEntity<ApiResponse<Page<TransactionResponse>>> getTransactions(
            @RequestParam(required = false) Long accountId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok(transactionService.getTransactions(accountId, page, size)));
    }

    @GetMapping("/statements")
    @Operation(summary = "Generate account statement", description = "Computes opening balance, transactions, and closing balance.")
    public ResponseEntity<ApiResponse<StatementResponse>> getStatement(
            @RequestParam Long accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(ApiResponse.ok(transactionService.generateStatement(accountId, startDate, endDate)));
    }

    @GetMapping(value = "/statements/export", produces = "text/csv")
    @Operation(summary = "Export statement as CSV file")
    public ResponseEntity<String> exportStatementCsv(
            @RequestParam Long accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        String csv = transactionService.generateCsvStatement(accountId, startDate, endDate);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"statement-" + accountId + ".csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
