package com.custom.stockCalc.repo;

import com.custom.stockCalc.model.finmind.strategyResultByCode.StrategyResultByCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrategyResultByCodeRepo extends JpaRepository<StrategyResultByCode, String> {
    List<StrategyResultByCode> findByStockId(String stockId);
}
