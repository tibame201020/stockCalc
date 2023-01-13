package com.custom.stockCalc.repo;

import com.custom.stockCalc.model.finmind.strategyResult.StrategyResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StrategyResultRepo extends JpaRepository<StrategyResult, String> {
    List<StrategyResult> findByStrategyName(String strategyName);
}
