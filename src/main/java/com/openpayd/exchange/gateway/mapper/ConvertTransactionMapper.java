package com.openpayd.exchange.gateway.mapper;

import com.openpayd.exchange.gateway.domain.conversion_transaction.entity.ConversionTransaction;
import com.openpayd.exchange.gateway.dto.ConversionResultDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ConvertTransactionMapper {
    @Mapping(source = "resultAmount", target = "amount")
    ConversionResultDto conversionEntityToResult(ConversionTransaction conversionTransactionEntity);
}
