package com.openpayd.exchange.gateway.exception;

import com.openpayd.exchange.gateway.enumeration.ErrorCode;
import lombok.Getter;

@Getter
public class BusinessRuleException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessRuleException(ErrorCode errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
    }

}
