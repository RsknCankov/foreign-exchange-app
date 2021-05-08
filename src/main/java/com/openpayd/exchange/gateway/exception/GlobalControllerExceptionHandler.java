package com.openpayd.exchange.gateway.exception;

import com.openpayd.exchange.gateway.dto.ErrorResponse;
import com.openpayd.exchange.gateway.enumeration.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     *This method is used to handle the custom BusinessRuleException that is used to
     * standardise our error codes and messages in order to help easier integration from third parties
     * with our application
     */
    @ExceptionHandler(value = BusinessRuleException.class)
    protected ResponseEntity<Object> handleBusinessRuleException(BusinessRuleException ex, WebRequest request) {

        ErrorResponse body = new ErrorResponse(ex.getMessage(), ex.getErrorCode().getCode());
        HttpStatus httpStatus = ErrorCode.getHttpStatus(ex.getErrorCode());
        log.error(ex.getMessage(), ex);
        return handleExceptionInternal(ex, body, new HttpHeaders(), httpStatus, request);
    }

    /**
     * This method is used to handle a generalized RuntimeException that could be
     * thrown in the application
     */
    @ExceptionHandler(value = RuntimeException.class)
    protected ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request) {
        ErrorResponse body = new ErrorResponse(ex.getMessage(), ErrorCode.UNHANDLED.getCode());
        HttpStatus httpStatus = ErrorCode.getHttpStatus(ErrorCode.UNHANDLED);
        log.error(ex.getMessage(), ex);
        return handleExceptionInternal(ex, body, new HttpHeaders(), httpStatus, request);
    }
}
