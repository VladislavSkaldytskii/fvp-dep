package ru.fpv.deposit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import ru.fpv.deposit.dto.CreateDefaultProductResponse;
import ru.fpv.deposit.dto.DepositProductResponse;
import ru.fpv.deposit.dto.UpdateDepositProductRequest;
import ru.fpv.deposit.enums.DEPOSIT_TYPE;
import ru.fpv.deposit.enums.PERIOD_TYPE;
import ru.fpv.deposit.model.DepositProduct;
import ru.fpv.deposit.repository.CbRatesRepository;
import ru.fpv.deposit.repository.DepositsProductsRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.fpv.deposit.enums.DEPOSIT_TYPE.*;

@Service
@RequiredArgsConstructor
public class DepositsProductsService {
    private final DepositsProductsRepository depositsProductsRepository;
    private final CbRatesRepository cbRatesRepository;
    private final JdbcTemplate jdbcTemplate;

    private static final BigDecimal MIN_DEPOSIT_AMOUNT = BigDecimal.valueOf(1000);
    private static final BigDecimal MAX_DEPOSIT_AMOUNT = BigDecimal.valueOf(10_000_000);
    private static final int MIN_PERIOD = 3;
    private static final int MAX_PERIOD_DEMAND = 1;

   @Transactional
    public CreateDefaultProductResponse createDefaultProductResponse() {

       Optional<BigDecimal> cbRate = cbRatesRepository.findCurrentRate();

       if (cbRate == null) {
           throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No active CB rate ");
       }

       BigDecimal activeRate = depositsProductsRepository.findCurrentActiveCbRate();

       if (activeRate != null && activeRate.compareTo(cbRate.get()) == 0) {
           return new CreateDefaultProductResponse(false, cbRate.orElseThrow(), List.of(), 0);

       }

       int expiredCount = depositsProductsRepository.expireAllActive();

       List<Long> createdIds = new ArrayList<>();

       for (DEPOSIT_TYPE depositType : DEPOSIT_TYPE.values()) {
           for (PERIOD_TYPE periodType : PERIOD_TYPE.values()) {
               if ((depositType == DEPOSIT_TYPE.DEMAND && periodType != PERIOD_TYPE.DAY) ||
                       ((depositType == DEPOSIT_TYPE.TERM || depositType == DEPOSIT_TYPE.TERM_CAPITALIZATION)
                               && periodType != PERIOD_TYPE.MONTH)) {
                   continue;
               }

               DepositProduct product = DepositProduct.builder()
                       .depositType(depositType)
                       .periodType(periodType)
                       .limitPeriod(depositType == DEPOSIT_TYPE.DEMAND ? null : 36)
                       .minPeriod(depositType == DEPOSIT_TYPE.DEMAND ? 1 : 3)
                       .minRate(round(calculateMinRate(depositType, cbRate)))
                       .maxRate(round(calculateMaxRate(depositType, cbRate)))
                       .minDepositAmount(MIN_DEPOSIT_AMOUNT)
                       .maxDepositAmount(MAX_DEPOSIT_AMOUNT)
                       .isCapitalization(depositType == DEPOSIT_TYPE.TERM_CAPITALIZATION)
                       .isExpired(false)
                       .created(LocalDateTime.now())
                       .modified(LocalDateTime.now())
                       .build();

               createdIds.add(depositsProductsRepository.save(product).getId());
           }
       }

           return new CreateDefaultProductResponse(true, cbRate, createdIds, expiredCount);
       }

    private BigDecimal calculateMinRate(DEPOSIT_TYPE depositType, Optional<BigDecimal> cbRate) {
        return null;
    }

    private BigDecimal calculateMinRate (DEPOSIT_TYPE depositType, BigDecimal cbRate){
           return switch (depositType) {
               case DEMAND -> cbRate.multiply(BigDecimal.valueOf(0.3));
               case TERM -> cbRate.multiply(BigDecimal.valueOf(0.6));
               case TERM_CAPITALIZATION -> cbRate.multiply(BigDecimal.valueOf(0.55));
           };
       }

    private BigDecimal calculateMaxRate(DEPOSIT_TYPE depositType, Optional<BigDecimal> cbRateOptional) {
        BigDecimal cbRate = cbRateOptional.orElseThrow(() ->
                new IllegalArgumentException("CB rate is missing")
        );

        return switch (depositType) {
            case DEMAND -> cbRate.multiply(BigDecimal.valueOf(0.5));
            case TERM -> cbRate.multiply(BigDecimal.valueOf(0.9));
            case TERM_CAPITALIZATION -> cbRate.multiply(BigDecimal.valueOf(0.85));
        };
       }

       private BigDecimal round (BigDecimal value){
           return value.setScale(2, RoundingMode.HALF_UP);
       }

       private PERIOD_TYPE resolvePeriodType (DEPOSIT_TYPE type){
           return switch (type) {
               case DEMAND -> PERIOD_TYPE.DAY;
               case TERM, TERM_CAPITALIZATION -> PERIOD_TYPE.MONTH;
           };
       }

       private Integer resolveLimitPeriod (DEPOSIT_TYPE type){
           if (type == DEMAND) return null;
           return 36;
       }

       private int resolveMinPeriod (DEPOSIT_TYPE type){
           return switch (type) {
               case DEMAND -> MIN_PERIOD;
               case TERM, TERM_CAPITALIZATION -> MIN_PERIOD;
           };
       }

    public List<DepositProductResponse> getDepositProducts(
            Boolean isExpired,
            LocalDateTime createdFrom,
            LocalDateTime createdTo,
            LocalDateTime modifiedFrom,
            LocalDateTime modifiedTo
    ) {
        String sql = "SELECT * FROM DEPOSIT_PRODUCT WHERE 1=1";
        List<Object> params = new ArrayList<>();

        if (isExpired != null) {
            sql += " AND IS_EXPIRED = ?";
            params.add(isExpired);
        }
        if (createdFrom != null) {
            sql += " AND CREATED >= ?";
            params.add(Timestamp.valueOf(createdFrom));
        }
        if (createdTo != null) {
            sql += " AND CREATED <= ?";
            params.add(Timestamp.valueOf(createdTo));
        }
        if (modifiedFrom != null) {
            sql += " AND MODIFIED >= ?";
            params.add(Timestamp.valueOf(modifiedFrom));
        }
        if (modifiedTo != null) {
            sql += " AND MODIFIED <= ?";
            params.add(Timestamp.valueOf(modifiedTo));
        }

        sql += " ORDER BY MODIFIED DESC";

        return jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) -> mapRowToDTO(rs));
    }

    public DepositProductResponse getDepositProductById(Long id) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM DEPOSIT_PRODUCT WHERE ID = ?",
                    new Object[]{id},
                    (rs, rowNum) -> mapRowToDTO(rs)
            );
        } catch (EmptyResultDataAccessException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Deposit product not found");
        }
    }

    private DepositProductResponse mapRowToDTO(ResultSet rs) throws SQLException {
        return new DepositProductResponse(
                rs.getLong("ID"),
                DEPOSIT_TYPE.valueOf(rs.getString("DEPOSIT_TYPE")),
                PERIOD_TYPE.valueOf(rs.getString("PERIOD_TYPE")),
                rs.getObject("LIMIT_PERIOD") != null ? rs.getInt("LIMIT_PERIOD") : null,
                rs.getInt("MIN_PERIOD"),
                rs.getBigDecimal("MIN_RATE"),
                rs.getBigDecimal("MAX_RATE"),
                rs.getBigDecimal("MIN_DEPOSIT_AMOUNT"),
                rs.getBigDecimal("MAX_DEPOSIT_AMOUNT"),
                rs.getBoolean("IS_CAPITALIZATION"),
                rs.getBoolean("IS_EXPIRED"),
                rs.getTimestamp("CREATED").toLocalDateTime(),
                rs.getTimestamp("MODIFIED").toLocalDateTime()
        );
    }

    @Transactional
    public DepositProduct updateDepositProduct(Long id, UpdateDepositProductRequest request) {
        DepositProduct product = (DepositProduct) depositsProductsRepository.findById(request.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Deposit product not found"));

        if (request.getMinRate().compareTo(request.getMaxRate()) > 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minRate cannot be greater than maxRate");

        if (request.getMinDepositAmount().compareTo(request.getMaxDepositAmount()) > 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minDepositAmount cannot be greater than maxDepositAmount");

        if (request.getLimitPeriod() != null && request.getMinPeriod() > request.getLimitPeriod())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minPeriod cannot be greater than limitPeriod");

        product.setMinRate(request.getMinRate());
        product.setMaxRate(request.getMaxRate());
        product.setMinDepositAmount(request.getMinDepositAmount());
        product.setMaxDepositAmount(request.getMaxDepositAmount());
        product.setPeriodType(request.getPeriodType());
        product.setMinPeriod(request.getMinPeriod());
        product.setLimitPeriod(request.getLimitPeriod());
        product.setModified(LocalDateTime.now());

        return depositsProductsRepository.save(product);
    }

}





