package ru.fpv.deposit.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.fpv.deposit.model.CbRates;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CbRatesRepository extends CrudRepository<CbRates,Long> {
    @Query("""
        SELECT RATE
        FROM CB_RATES
        WHERE START_PERIOD <= CURRENT_DATE
          AND (END_PERIOD IS NULL OR END_PERIOD > CURRENT_DATE)
        ORDER BY START_PERIOD DESC
        LIMIT 1
    """)
    Optional<BigDecimal> findCurrentRate();


}
