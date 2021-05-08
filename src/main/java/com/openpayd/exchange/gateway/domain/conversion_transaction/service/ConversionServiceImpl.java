package com.openpayd.exchange.gateway.domain.conversion_transaction.service;

import com.openpayd.exchange.gateway.domain.conversion_transaction.entity.ConversionTransaction;
import com.openpayd.exchange.gateway.domain.conversion_transaction.repository.ConversionTransactionRepository;
import com.openpayd.exchange.gateway.domain.fxrates.service.FxRatesService;
import com.openpayd.exchange.gateway.dto.ConversionResultDto;
import com.openpayd.exchange.gateway.mapper.ConvertTransactionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Slf4j
@Service
public class ConversionServiceImpl implements IConversionService {

    Integer ratesScale;
    FxRatesService fxRatesService;
    ConversionTransactionRepository conversionTransactionRepository;
    ConvertTransactionMapper transactionMapper;

    public ConversionServiceImpl(FxRatesService fxRatesService, ConversionTransactionRepository conversionTransactionRepository, ConvertTransactionMapper transactionMapper,
                                 @Value("${rates.default-scale}") Integer ratesScale) {
        this.fxRatesService = fxRatesService;
        this.conversionTransactionRepository = conversionTransactionRepository;
        this.transactionMapper = transactionMapper;
        this.ratesScale = ratesScale;
    }

    /**
     * Executes and persists conversion transaction
     *
     * @param sourceCurrency
     * @param targetCurrency
     * @param sourceCurrencyAmount
     * @return internal transactionId and converted amount (amount in target currency)
     */
    @Override
    public ConversionResultDto convert(String sourceCurrency, String targetCurrency, BigDecimal sourceCurrencyAmount) {
        BigDecimal exchangeRate = fxRatesService.getPairExchangeRate(sourceCurrency, targetCurrency);
        BigDecimal targetCurrencyAmount = sourceCurrencyAmount.multiply(exchangeRate).setScale(ratesScale, RoundingMode.DOWN);
        log.trace("Conversion Saved: {} {} --> {} {}", sourceCurrencyAmount, sourceCurrency, targetCurrencyAmount, targetCurrency);
        ConversionTransaction transactionEntity = conversionTransactionRepository.save(ConversionTransaction.builder()
                .sourceCurrency(sourceCurrency)
                .sourceAmount(sourceCurrencyAmount)
                .targetCurrency(targetCurrency)
                .exchangeRate(exchangeRate)
                .resultAmount(targetCurrencyAmount)
                .build());

        return transactionMapper.conversionEntityToResult(transactionEntity);
    }

    /**
     * @param pageable
     * @param transactionId
     * @return conversion transaction by its ID
     */
    @Override
    public Page<ConversionTransaction> findConversionTransactionByTransactionId(Pageable pageable, Long transactionId) {
        return conversionTransactionRepository.findConversionTransactionByTransactionId(transactionId, pageable);
    }

    /**
     * @param pageable
     * @param transactionDate
     * @return conversion transactions for the given date
     */
    @Override
    public Page<ConversionTransaction> findConversionTransactionByTimestamp(Pageable pageable, Date transactionDate) {
        return conversionTransactionRepository.findConversionTransactionByTimestampEquals(transactionDate, pageable);
    }
}
