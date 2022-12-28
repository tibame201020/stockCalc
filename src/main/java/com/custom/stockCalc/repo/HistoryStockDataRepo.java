package com.custom.stockCalc.repo;

import com.custom.stockCalc.model.HistoryStockData;
import com.custom.stockCalc.model.StockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryStockDataRepo extends JpaRepository<HistoryStockData, String> {
    List<StockData> findByYearMonthCode(String yearMonthCode);
}
