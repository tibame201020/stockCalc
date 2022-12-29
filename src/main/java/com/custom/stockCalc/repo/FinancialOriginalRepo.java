package com.custom.stockCalc.repo;

import com.custom.stockCalc.model.financial.FinancialOriginal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FinancialOriginalRepo extends JpaRepository<FinancialOriginal,String> {
}
