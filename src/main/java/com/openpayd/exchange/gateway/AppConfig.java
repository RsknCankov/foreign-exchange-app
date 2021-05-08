package com.openpayd.exchange.gateway;

import com.openpayd.exchange.gateway.domain.fxrates.service.FxRatesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AppConfig {

    @Bean
    CommandLineRunner initDatabase(FxRatesService fxRatesService) {

        return args -> {
            log.trace("Persisting FX rates....");
            fxRatesService.persistFxRates();
            log.trace("Done!");
        };
    }
}
