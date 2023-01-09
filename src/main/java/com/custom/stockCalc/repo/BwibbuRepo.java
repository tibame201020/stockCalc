package com.custom.stockCalc.repo;

import com.custom.stockCalc.model.stockDayView.Bwibbu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BwibbuRepo extends JpaRepository<Bwibbu, String> {
}
