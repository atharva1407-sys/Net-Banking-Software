package com.securebank.transfer.repository;

import com.securebank.transfer.entity.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long> {
    Optional<Transfer> findByTransferReference(String transferReference);
    Optional<Transfer> findByIdempotencyKey(String idempotencyKey);
}
