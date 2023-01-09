package com.custom.stockCalc.repo;

import com.custom.stockCalc.model.stockDayView.CompanySummaryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanySummaryReportRepo extends JpaRepository<CompanySummaryReport, String> {

}
