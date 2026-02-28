package ru.fpv.deposit.repository;

import jakarta.validation.constraints.NotNull;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.fpv.deposit.model.DepositProduct;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface DepositsProductsRepository  extends CrudRepository<DepositProduct,Integer> {

    @Query("""
    SELECT DISTINCT CB_rate
    FROM Deposit_product
    WHERE Is_expired = false
    LIMIT 1
""")
    BigDecimal findCurrentActiveCbRate();

    @Query("""
        UPDATE Deposit_product
        SET Is_expired = true,
            Modified = CURRENT_DATE
        WHERE Is_expired = false
    """)
    int expireAllActive();

    Optional<Object> findById(@NotNull(message = "Id is required") Long id);
}
