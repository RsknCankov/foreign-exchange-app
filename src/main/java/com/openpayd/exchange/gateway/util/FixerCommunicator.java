package com.openpayd.exchange.gateway.util;

import com.openpayd.exchange.gateway.domain.fxrates.model.FxRatesResponse;
import com.openpayd.exchange.gateway.enumeration.ErrorCode;
import com.openpayd.exchange.gateway.exception.BusinessRuleException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Service
public class FixerCommunicator implements IFixerCommunicator {
    private static final String FIXER_RESOURCE_URI = "http://data.fixer.io/api/latest?access_key=e38330757c53ebc83f53114b8c63fe69&format=1\"";

    /**
     * Obtains the rates for all supported currencies from fixer.io
     *
     * @return Map containing exchange rates information for each currency (Currency Symbol-Rate)
     * @throws BusinessRuleException if the api returns empty rates object
     */
    @Override
    public Map<String, BigDecimal> getFxRates() {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<FxRatesResponse> response = restTemplate.getForEntity(FIXER_RESOURCE_URI, FxRatesResponse.class);
        return Optional.ofNullable(response.getBody().getRates())
                .orElseThrow(() -> new BusinessRuleException(ErrorCode.FIXER_API_FAIL, "Could not get fixer rates"));

    }
}
