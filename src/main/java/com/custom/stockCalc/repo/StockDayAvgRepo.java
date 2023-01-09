package com.custom.stockCalc.repo;

import com.custom.stockCalc.model.stockDayView.StockDayAvg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockDayAvgRepo extends JpaRepository<StockDayAvg, String> {
}
