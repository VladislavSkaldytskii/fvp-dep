package ru.fpv.deposit.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.fpv.deposit.enums.PERIOD_TYPE;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDepositProductRequest {
    @NotNull(message = "Id is required")
    private Long id;

    @DecimalMin(value = "0.0", inclusive = false, message = "minRate must be positive")
    private BigDecimal minRate;

    @DecimalMin(value = "0.0", inclusive = false, message = "maxRate must be positive")
    private BigDecimal maxRate;

    @DecimalMin(value = "0.0", inclusive = false, message = "minDepositAmount must be positive")
    private BigDecimal minDepositAmount;

    @DecimalMin(value = "0.0", inclusive = false, message = "maxDepositAmount must be positive")
    private BigDecimal maxDepositAmount;

    private PERIOD_TYPE periodType;
    private Integer minPeriod;
    private Integer limitPeriod;
}
