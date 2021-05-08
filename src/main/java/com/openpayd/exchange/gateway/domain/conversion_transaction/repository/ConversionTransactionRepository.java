package com.openpayd.exchange.gateway.domain.conversion_transaction.repository;

import com.openpayd.exchange.gateway.domain.conversion_transaction.entity.ConversionTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface ConversionTransactionRepository extends JpaRepository<ConversionTransaction, Long> {
    Page<ConversionTransaction> findConversionTransactionByTransactionId(Long transactionId, Pageable pageable);

    Page<ConversionTransaction> findConversionTransactionByTimestampEquals(Date transactionDate, Pageable pageable);

}
