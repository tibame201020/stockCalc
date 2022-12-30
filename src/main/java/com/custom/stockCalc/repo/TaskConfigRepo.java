package com.custom.stockCalc.repo;

import com.custom.stockCalc.model.config.TaskConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskConfigRepo extends JpaRepository<TaskConfig, String> {
}
