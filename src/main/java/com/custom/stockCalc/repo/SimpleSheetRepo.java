package com.custom.stockCalc.repo;

import com.custom.stockCalc.model.financial.SimpleSheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimpleSheetRepo extends JpaRepository<SimpleSheet, String> {
}
