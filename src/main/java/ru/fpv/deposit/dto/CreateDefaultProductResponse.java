package ru.fpv.deposit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
public class CreateDefaultProductResponse {

    private boolean created;
    private BigDecimal cbRate;
    private List<Long> createProductIds;
    private int expiredCount;

    public CreateDefaultProductResponse(boolean created, Optional<BigDecimal> cbRate, List<Long> createdIds, int expiredCount) {
    }
}
