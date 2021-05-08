package com.openpayd.exchange.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ConversionResultDto {
    Long transactionId;
    BigDecimal amount;
}
