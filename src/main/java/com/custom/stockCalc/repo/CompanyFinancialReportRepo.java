package com.custom.stockCalc.repo;

import com.custom.stockCalc.model.stockDayView.CompanyFinancialReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyFinancialReportRepo extends JpaRepository<CompanyFinancialReport, String> {
}
