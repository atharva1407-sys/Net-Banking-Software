package com.securebank.transfer.controller;

import com.securebank.common.dto.ApiResponse;
import com.securebank.transfer.dto.TransferRequest;
import com.securebank.transfer.dto.TransferResponse;
import com.securebank.transfer.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transfers")
@Tag(name = "Fund Transfers", description = "Instant account-to-account simulated money transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    @Operation(summary = "Transfer money", description = "Executes an atomic debit-and-credit transfer between bank accounts.")
    public ResponseEntity<ApiResponse<TransferResponse>> transfer(@Valid @RequestBody TransferRequest request) {
        TransferResponse response = transferService.executeTransfer(request);
        return ResponseEntity.ok(ApiResponse.ok("Transfer completed successfully.", response));
    }
}
