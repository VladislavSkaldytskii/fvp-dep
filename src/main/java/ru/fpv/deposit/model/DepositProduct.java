package ru.fpv.deposit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import ru.fpv.deposit.enums.DEPOSIT_TYPE;
import ru.fpv.deposit.enums.PERIOD_TYPE;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("Deposit_product")
public class DepositProduct {
    @Id
    private long id;
    @Column("Max_rate")
    private BigDecimal maxRate;
    @Column("Min_rate")
    private BigDecimal minRate;
    @Column("Min_deposit_amount")
    private BigDecimal minDepositAmount;
    @Column("Max_deposit_amount")
    private BigDecimal maxDepositAmount;
    @Column("Deposit_type")
    private DEPOSIT_TYPE depositType;
    @Column("Is_capitalization")
    private boolean isCapitalization;
    @Column("Period_type")
    private PERIOD_TYPE periodType;
    @Column("Min_period")
    private int minPeriod;
    @Column("Limit_period")
    private Integer limitPeriod;
    @Column("Is_expired")
    private boolean isExpired;
    @Column("Created")
    private LocalDateTime created;
    @Column("Modified")
    private LocalDateTime modified;

}
