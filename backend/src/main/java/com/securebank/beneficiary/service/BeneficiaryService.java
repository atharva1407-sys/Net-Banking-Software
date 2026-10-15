package com.securebank.beneficiary.service;

import com.securebank.auth.service.AuthService;
import com.securebank.beneficiary.dto.BeneficiaryRequest;
import com.securebank.beneficiary.dto.BeneficiaryResponse;
import com.securebank.beneficiary.entity.Beneficiary;
import com.securebank.beneficiary.repository.BeneficiaryRepository;
import com.securebank.common.exception.BusinessException;
import com.securebank.customer.entity.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BeneficiaryService {

    private final BeneficiaryRepository beneficiaryRepository;
    private final AuthService authService;

    public BeneficiaryService(BeneficiaryRepository beneficiaryRepository, AuthService authService) {
        this.beneficiaryRepository = beneficiaryRepository;
        this.authService = authService;
    }

    public List<BeneficiaryResponse> getBeneficiaries() {
        Customer customer = authService.getAuthenticatedCustomer();
        return beneficiaryRepository.findByCustomerIdAndStatus(customer.getId(), "ACTIVE")
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BeneficiaryResponse addBeneficiary(BeneficiaryRequest req) {
        Customer customer = authService.getAuthenticatedCustomer();

        if (beneficiaryRepository.existsByCustomerIdAndAccountNumber(customer.getId(), req.getAccountNumber())) {
            throw new BusinessException("A beneficiary with this account number already exists in your account.");
        }

        Beneficiary beneficiary = new Beneficiary(
                customer,
                req.getBeneficiaryName().trim(),
                req.getAccountNumber().trim(),
                req.getBankName(),
                req.getIfscCode(),
                req.getNickname()
        );
        beneficiary = beneficiaryRepository.save(beneficiary);
        return toResponse(beneficiary);
    }

    @Transactional
    public void deleteBeneficiary(Long id) {
        Customer customer = authService.getAuthenticatedCustomer();
        Beneficiary beneficiary = beneficiaryRepository.findByIdAndCustomerId(id, customer.getId())
                .orElseThrow(() -> new BusinessException("Beneficiary not found or unauthorized.", HttpStatus.NOT_FOUND));
        beneficiary.setStatus("INACTIVE");
        beneficiaryRepository.save(beneficiary);
    }

    private BeneficiaryResponse toResponse(Beneficiary b) {
        return new BeneficiaryResponse(
                b.getId(),
                b.getBeneficiaryName(),
                b.getAccountNumber(),
                b.getBankName(),
                b.getIfscCode(),
                b.getNickname(),
                b.getCreatedAt()
        );
    }
}
