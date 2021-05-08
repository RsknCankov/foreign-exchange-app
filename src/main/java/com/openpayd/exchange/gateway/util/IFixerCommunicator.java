package com.openpayd.exchange.gateway.util;

import java.math.BigDecimal;
import java.util.Map;

public interface IFixerCommunicator {
    Map<String, BigDecimal> getFxRates();
}
