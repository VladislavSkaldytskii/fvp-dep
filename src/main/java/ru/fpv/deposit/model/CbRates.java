package ru.fpv.deposit.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@Table("CB_RATES")
public class CbRates {

    @Id
    private Long id;
    @Column("Rate")
    private BigDecimal rate;
    @Column("Start_period")
    private LocalDate start_period;
    @Column("End_period")
    private LocalDate end_period;
}
