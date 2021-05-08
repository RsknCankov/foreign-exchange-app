package com.openpayd.exchange.gateway.domain.conversion_transaction.service;

import com.openpayd.exchange.gateway.domain.conversion_transaction.entity.ConversionTransaction;
import com.openpayd.exchange.gateway.domain.conversion_transaction.repository.ConversionTransactionRepository;
import com.openpayd.exchange.gateway.domain.fxrates.service.FxRatesService;
import com.openpayd.exchange.gateway.dto.ConversionResultDto;
import com.openpayd.exchange.gateway.enumeration.ErrorCode;
import com.openpayd.exchange.gateway.exception.BusinessRuleException;
import com.openpayd.exchange.gateway.mapper.ConvertTransactionMapper;
import com.openpayd.exchange.gateway.mapper.ConvertTransactionMapperImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "rates.default-scale=2",
})
class ConversionServiceImplTest {

    @Value("${rates.default-scale}")
    Integer ratesScale;

    @Mock
    FxRatesService fxRatesService;
    @Mock
    ConversionTransactionRepository conversionTransactionRepository;
    @Mock
    ConvertTransactionMapper convertTransactionMapper = new ConvertTransactionMapperImpl();

    @Test
    void convertSuccess() {
        final BigDecimal sourceAmount = new BigDecimal(100);
        final BigDecimal exchangeRate = new BigDecimal("2.5");
        final BigDecimal resultAmount = sourceAmount.multiply(exchangeRate).setScale(ratesScale, RoundingMode.DOWN);
        final long transactionId = 1L;
        final String sourceCurrency = "USD";
        final String targetCurrency = "EUR";
        ConversionTransaction mockSavedTransaction = ConversionTransaction.builder()
                .sourceCurrency(sourceCurrency)
                .targetCurrency(targetCurrency)
                .sourceAmount(sourceAmount)
                .exchangeRate(exchangeRate)
                .resultAmount(resultAmount)
                .transactionId(transactionId)
                .timestamp(new Date())
                .build();
        ConversionResultDto mockResultDto = new ConversionResultDto(transactionId, resultAmount);

        when(fxRatesService.getPairExchangeRate(sourceCurrency, targetCurrency)).thenReturn(exchangeRate);
        when(conversionTransactionRepository.save(any())).thenReturn(mockSavedTransaction);
        when(convertTransactionMapper.conversionEntityToResult(mockSavedTransaction)).thenReturn(mockResultDto);

        ConversionResultDto result = new ConversionServiceImpl(fxRatesService, conversionTransactionRepository, convertTransactionMapper, ratesScale).convert(sourceCurrency, targetCurrency, sourceAmount);

        assertEquals(result.getTransactionId(), transactionId);
        assertEquals(result.getAmount(), resultAmount);
    }

    @Test
    void convertErrInvalidSymbol() {
        final BigDecimal sourceAmount = new BigDecimal(100);
        final String sourceCurrency = "USD";
        final String targetCurrency = "EUR";
        final String errMsg = "Invalid symbol";

        when(fxRatesService.getPairExchangeRate(any(), any())).thenThrow(new BusinessRuleException(ErrorCode.INVALID_SYMBOL, errMsg));

        final ConversionServiceImpl conversionService = new ConversionServiceImpl(fxRatesService, conversionTransactionRepository, convertTransactionMapper, ratesScale);
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            conversionService.convert(sourceCurrency, targetCurrency, sourceAmount);
        });

        assertTrue(exception.getMessage().contains(errMsg));
        assertEquals(ErrorCode.INVALID_SYMBOL, exception.getErrorCode());
    }

}
