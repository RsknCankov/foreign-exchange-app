package com.openpayd.exchange.gateway.enumeration;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    UNHANDLED(-1),

    INVALID_SYMBOL(100),
    INVALID_EXCHANGE_PAIR(101),
    INVALID_ARGUMENTS(102),
    INVALID_TRANSACTION_ID(103),
    NO_TRANSACTIONS_FOR_PERIOD(104),
    INTERNAL_ERROR(200),
    FIXER_API_FAIL(300);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public static HttpStatus getHttpStatus(ErrorCode errorCode) {
        if (errorCode.code >= 100 && errorCode.code < 200) {
            return HttpStatus.BAD_REQUEST;
        } else if (errorCode.code >= 200 && errorCode.code < 300) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }
}
