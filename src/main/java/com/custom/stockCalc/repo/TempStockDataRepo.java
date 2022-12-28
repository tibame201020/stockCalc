package com.custom.stockCalc.repo;

import com.custom.stockCalc.model.StockData;
import com.custom.stockCalc.model.TempStockData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TempStockDataRepo extends JpaRepository<TempStockData, String> {
    List<StockData> findByYearMonthCode(String yearMonthCode);
}
