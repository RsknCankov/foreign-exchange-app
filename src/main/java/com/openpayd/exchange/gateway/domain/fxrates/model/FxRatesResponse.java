package com.openpayd.exchange.gateway.domain.fxrates.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
public class FxRatesResponse {
    private Map<String, BigDecimal> rates;
}
