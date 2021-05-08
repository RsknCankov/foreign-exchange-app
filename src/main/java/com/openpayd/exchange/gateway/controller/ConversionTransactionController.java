package com.openpayd.exchange.gateway.controller;

import com.openpayd.exchange.gateway.domain.conversion_transaction.entity.ConversionTransaction;
import com.openpayd.exchange.gateway.domain.conversion_transaction.service.ConversionServiceImpl;
import com.openpayd.exchange.gateway.dto.ConversionRequestDto;
import com.openpayd.exchange.gateway.dto.ConversionResultDto;
import com.openpayd.exchange.gateway.enumeration.ErrorCode;
import com.openpayd.exchange.gateway.exception.BusinessRuleException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;

@RestController
@RequestMapping(consumes = "application/json", produces = "application/json")
public class ConversionTransactionController {

    private final ConversionServiceImpl conversionService;

    public ConversionTransactionController(ConversionServiceImpl conversionService) {
        this.conversionService = conversionService;
    }

    /**
     * Controller used for obtaining list of conversions based either on transactionId or transactionDate
     *
     * @param pageable config object for page size and page number
     * @param transactionId parameter (optional)
     * @param transactionDate parameter (optional)
     * @return list of transactions along with the Pageable object configuration
     * @throws BusinessRuleException if both transactionId and transactionDate are provided
     *                               if neither transactionId no transactionDate is provided
     */
    @GetMapping("transactions")
    public @ResponseBody
    Page<ConversionTransaction> getConversionTransactions(Pageable pageable,
                                                          @RequestParam(required = false) Long transactionId,
                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date transactionDate) {
        if (transactionId != null && transactionDate != null)
            throw new BusinessRuleException(ErrorCode.INVALID_ARGUMENTS, "Only transactionId OR transactionDate must be provided!");
        if (transactionId != null)
            return conversionService.findConversionTransactionByTransactionId(pageable, transactionId);
        if (transactionDate != null)
            return conversionService.findConversionTransactionByTimestamp(pageable, transactionDate);
        throw new BusinessRuleException(ErrorCode.INVALID_ARGUMENTS, "transactionId OR transactionDate must be provided!");
    }


    /**
     * Controller for conversion execution
     * @see ConversionRequestDto
     * @see ConversionResultDto
     *
     * @param request model containing all fields needed for the conversion:
     *                -amount
     *                -source currency
     *                -target currency
     * @return transactionId and amount converted
     */
    @PostMapping(path = "exchange")
    public ConversionResultDto exchangeCurrency(@RequestBody ConversionRequestDto request) {
        String sourceCurrency = request.getSourceCurrency();
        String targetCurrency = request.getTargetCurrency();
        BigDecimal sourceCurrencyAmount = request.getAmount();
        return conversionService.convert(sourceCurrency, targetCurrency, sourceCurrencyAmount);

    }
}
