package com.custom.stockCalc.model.finmind.strategyResult;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StrategyResultObj implements Serializable {
    @JsonProperty(value = "StrategyAnalysis")
    private StrategyResult[] strategyResults;
}
