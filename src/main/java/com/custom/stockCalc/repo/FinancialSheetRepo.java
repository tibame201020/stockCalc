package com.custom.stockCalc.repo;

import com.custom.stockCalc.model.financial.FinancialSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialSheetRepo extends JpaRepository<FinancialSheet, String> {
}
