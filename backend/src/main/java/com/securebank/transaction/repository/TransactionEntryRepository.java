package com.securebank.transaction.repository;

import com.securebank.transaction.entity.TransactionEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionEntryRepository extends JpaRepository<TransactionEntry, Long> {

    Page<TransactionEntry> findByAccountIdOrderByCreatedAtDesc(Long accountId, Pageable pageable);

    List<TransactionEntry> findByAccountIdAndCreatedAtBetweenOrderByCreatedAtAsc(
            Long accountId, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT te FROM TransactionEntry te JOIN te.account acc WHERE acc.customer.id = :customerId ORDER BY te.createdAt DESC",
           countQuery = "SELECT count(te) FROM TransactionEntry te JOIN te.account acc WHERE acc.customer.id = :customerId")
    Page<TransactionEntry> findByCustomerIdOrderByCreatedAtDesc(@Param("customerId") Long customerId, Pageable pageable);
}
