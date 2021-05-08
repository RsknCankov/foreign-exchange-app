package com.openpayd.exchange.gateway.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ConversionRequestDto {
    BigDecimal amount;
    String sourceCurrency;
    String targetCurrency;
}
