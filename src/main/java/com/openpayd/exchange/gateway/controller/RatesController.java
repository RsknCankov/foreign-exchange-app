package com.openpayd.exchange.gateway.controller;

import com.openpayd.exchange.gateway.domain.fxrates.service.FxRatesService;
import com.openpayd.exchange.gateway.enumeration.ErrorCode;
import com.openpayd.exchange.gateway.exception.BusinessRuleException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping(consumes = "application/json", produces = "application/json")
public class RatesController {

    private final FxRatesService fxRatesService;

    public RatesController(FxRatesService fxRatesService) {
        this.fxRatesService = fxRatesService;
    }

    /**
     * Controller for obtaining exchange rate for a given pair
     *
     * @param pair case insensitive String (e.g. EUR-USD)
     * @return the exchange rate for the requested pair
     * @throws BusinessRuleException if pair delimiter is different than '-'
     */
    @GetMapping(path = "rates")
    public BigDecimal getExchangeRate(@RequestParam(value = "pair") String pair) {
        if (pair.contains("-")) {
            String[] currencies = pair.split("-");
            return fxRatesService.getPairExchangeRate(currencies[0], currencies[1]);
        } else {
            throw new BusinessRuleException(ErrorCode.INVALID_EXCHANGE_PAIR, "Invalid exchange pair format");
        }
    }
}
