package com.openpayd.exchange.gateway.domain.fxrates.service;

import com.openpayd.exchange.gateway.domain.fxrates.entity.FxRateEntity;
import com.openpayd.exchange.gateway.domain.fxrates.repository.FxRateRepository;
import com.openpayd.exchange.gateway.enumeration.ErrorCode;
import com.openpayd.exchange.gateway.exception.BusinessRuleException;
import com.openpayd.exchange.gateway.util.IFixerCommunicator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Service
public class FxRatesService {

    FxRateRepository fxRateRepository;
    IFixerCommunicator iFixerCommunicator;
    Integer ratesScale;

    public FxRatesService(FxRateRepository fxRateRepository, IFixerCommunicator iFixerCommunicator,
                          @Value("${rates.default-scale}") Integer ratesScale) {
        this.fxRateRepository = fxRateRepository;
        this.iFixerCommunicator = iFixerCommunicator;
        this.ratesScale = ratesScale;
    }

    /**
     * Persists the rates from fixer.io
     */
    public void persistFxRates() {
        iFixerCommunicator.getFxRates()
                .forEach((symbol, rate) -> fxRateRepository.save(FxRateEntity.builder().symbol(symbol).rate(rate).build()));
    }

    /**
     * @param sourceCurrency
     * @param targetCurrency
     * @return  exchange rate for the given source and target currency
     */
    public BigDecimal getPairExchangeRate(String sourceCurrency, String targetCurrency) {
        BigDecimal sourceCurrencyRate = getExchangeRateForSymbol(sourceCurrency);
        BigDecimal targetCurrencyRate = getExchangeRateForSymbol(targetCurrency);
        BigDecimal fxRate = targetCurrencyRate.divide(sourceCurrencyRate, ratesScale, RoundingMode.DOWN);
        log.trace("FX rate obtained {}/{} -> {}", sourceCurrency, targetCurrency, fxRate);
        return fxRate;
    }

    /**
     *
     * @param symbol of the currency (case insensitive)
     * @return rate for the given symbol
     * @throws BusinessRuleException if the symbol does not exist
     */
    private BigDecimal getExchangeRateForSymbol(String symbol) {
        return fxRateRepository.findBySymbol(symbol.toUpperCase())
                .orElseThrow(() -> new BusinessRuleException(ErrorCode.INVALID_SYMBOL, "Invalid symbol"))
                .getRate();
    }
}
