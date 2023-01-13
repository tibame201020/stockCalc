package com.custom.stockCalc.repo;

import com.custom.stockCalc.model.finmind.backTesting.BackTesting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BackTestingRepo extends JpaRepository<BackTesting, String> {
}
