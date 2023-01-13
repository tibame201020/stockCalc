package com.custom.stockCalc.repo;

import com.custom.stockCalc.model.finmind.strategySummary.StrategySummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StrategySummaryRepo extends JpaRepository<StrategySummary, String> {
}
