package com.openpayd.exchange.gateway.domain.fxrates.repository;

import com.openpayd.exchange.gateway.domain.fxrates.entity.FxRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FxRateRepository extends JpaRepository<FxRateEntity, Long> {
    Optional<FxRateEntity> findBySymbol(String symbol);
}
