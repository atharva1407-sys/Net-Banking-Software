package com.securebank.beneficiary.controller;

import com.securebank.beneficiary.dto.BeneficiaryRequest;
import com.securebank.beneficiary.dto.BeneficiaryResponse;
import com.securebank.beneficiary.service.BeneficiaryService;
import com.securebank.common.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/beneficiaries")
@Tag(name = "Beneficiaries", description = "Beneficiary management for fund transfers")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping
    @Operation(summary = "List saved beneficiaries")
    public ResponseEntity<ApiResponse<List<BeneficiaryResponse>>> getBeneficiaries() {
        return ResponseEntity.ok(ApiResponse.ok(beneficiaryService.getBeneficiaries()));
    }

    @PostMapping
    @Operation(summary = "Add a new beneficiary")
    public ResponseEntity<ApiResponse<BeneficiaryResponse>> addBeneficiary(@Valid @RequestBody BeneficiaryRequest request) {
        BeneficiaryResponse res = beneficiaryService.addBeneficiary(request);
        return ResponseEntity.ok(ApiResponse.ok("Beneficiary added successfully.", res));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a beneficiary")
    public ResponseEntity<ApiResponse<Void>> deleteBeneficiary(@PathVariable Long id) {
        beneficiaryService.deleteBeneficiary(id);
        return ResponseEntity.ok(ApiResponse.ok("Beneficiary removed successfully.", null));
    }
}
