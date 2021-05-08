package com.openpayd.exchange.gateway.domain.fxrates.service;

import com.openpayd.exchange.gateway.domain.fxrates.entity.FxRateEntity;
import com.openpayd.exchange.gateway.domain.fxrates.repository.FxRateRepository;
import com.openpayd.exchange.gateway.enumeration.ErrorCode;
import com.openpayd.exchange.gateway.exception.BusinessRuleException;
import com.openpayd.exchange.gateway.util.IFixerCommunicator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {
        "rates.default-scale=2",
})
class FxRateServiceImplTest {

    @Mock
    FxRateRepository fxRateRepositoryMock;
    @Mock
    IFixerCommunicator iFixerCommunicatorMock;

    @Value("${rates.default-scale}")
    Integer ratesScale;

    @Test
    void getPairExchangeRateSuccess() {
        final String sourceSymbol = "USD";
        FxRateEntity mockedUSDEntity = FxRateEntity.builder()
                .symbol(sourceSymbol)
                .rate(new BigDecimal("1.5"))
                .id(1L).build();

        final String targetSymbol = "EUR";
        FxRateEntity mockedEUREntity = FxRateEntity.builder()
                .symbol(targetSymbol)
                .rate(new BigDecimal("1"))
                .id(2L).build();

        when(fxRateRepositoryMock.findBySymbol(sourceSymbol))
                .thenReturn(Optional.of(mockedUSDEntity));
        when(fxRateRepositoryMock.findBySymbol(targetSymbol))
                .thenReturn(Optional.of(mockedEUREntity));

        BigDecimal result = new FxRatesService(fxRateRepositoryMock, iFixerCommunicatorMock, ratesScale)
                .getPairExchangeRate(sourceSymbol, targetSymbol);

        assertEquals(BigDecimal.valueOf(0.66), result);
    }

    @Test
    void getPairExchangeRateInvalidSymbolEmpty() {

        final String targetSymbol = "EUR";
        FxRateEntity mockedEUREntity = FxRateEntity.builder()
                .symbol(targetSymbol)
                .rate(new BigDecimal("1"))
                .id(2L).build();

        when(fxRateRepositoryMock.findBySymbol("USDDD")).thenReturn(Optional.empty());
        when(fxRateRepositoryMock.findBySymbol(targetSymbol))
                .thenReturn(Optional.of(mockedEUREntity));

        final FxRatesService fxRatesService = new FxRatesService(fxRateRepositoryMock, iFixerCommunicatorMock, ratesScale);
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            fxRatesService.getPairExchangeRate("USDDD", targetSymbol);
        });

        assertEquals(ErrorCode.INVALID_SYMBOL, exception.getErrorCode());
    }

    @Test
    void persistFxRates() {

        final Map<String, BigDecimal> receivedRatesMock = Map.of(
                "USD", BigDecimal.valueOf(1.23),
                "EUR", BigDecimal.valueOf(2.14),
                "JPY", BigDecimal.valueOf(1.11)
        );

        when(iFixerCommunicatorMock.getFxRates()).thenReturn(receivedRatesMock);

        new FxRatesService(fxRateRepositoryMock, iFixerCommunicatorMock, ratesScale).persistFxRates();

        verify(iFixerCommunicatorMock, only()).getFxRates();
        verify(fxRateRepositoryMock, times(receivedRatesMock.size())).save(any());
    }
}
