package com.securebank.beneficiary.repository;

import com.securebank.beneficiary.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {
    List<Beneficiary> findByCustomerIdAndStatus(Long customerId, String status);
    Optional<Beneficiary> findByIdAndCustomerId(Long id, Long customerId);
    boolean existsByCustomerIdAndAccountNumber(Long customerId, String accountNumber);
}
