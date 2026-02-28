package ru.fpv.deposit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fpv.deposit.enums.DEPOSIT_TYPE;
import ru.fpv.deposit.enums.PERIOD_TYPE;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositProductResponse {
    private Long id;
    private DEPOSIT_TYPE depositType;
    private PERIOD_TYPE periodType;
    private Integer limitPeriod;
    private Integer minPeriod;
    private BigDecimal minRate;
    private BigDecimal maxRate;
    private BigDecimal minDepositAmount;
    private BigDecimal maxDepositAmount;
    private Boolean isCapitalization;
    private Boolean isExpired;
    private LocalDateTime created;
    private LocalDateTime modified;
}
