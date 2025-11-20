package com.user.account.app.services;

import com.user.account.app.entities.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByAccountId(Long accountId, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId " +
           "AND (CAST(:startDate AS timestamp) IS NULL OR t.createdAt >= :startDate) " +
           "AND (CAST(:endDate AS timestamp) IS NULL OR t.createdAt <= :endDate)")
    Page<Transaction> findByAccountIdAndDateRange(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
