package com.custom.stockCalc.model.finmind.strategyResultByCode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StrategyResultObjByCode implements Serializable {
    @JsonProperty(value = "StrategyAnalysis")
    private StrategyResultByCode[] strategyResultByCodes;
}
