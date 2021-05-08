package com.openpayd.exchange.gateway.domain.conversion_transaction.service;


import com.openpayd.exchange.gateway.domain.conversion_transaction.entity.ConversionTransaction;
import com.openpayd.exchange.gateway.dto.ConversionResultDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;

public interface IConversionService {
    ConversionResultDto convert(String sourceCurrency, String targetCurrency, BigDecimal sourceCurrencyAmount);

    Page<ConversionTransaction> findConversionTransactionByTransactionId(Pageable pageable, Long transactionId);

    Page<ConversionTransaction> findConversionTransactionByTimestamp(Pageable pageable, Date transactionDate);
}
