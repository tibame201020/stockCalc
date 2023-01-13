package com.custom.stockCalc.model.finmind.strategyResult;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;


@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StrategyResultStatus implements Serializable {
    @JsonProperty(value = "msg")
    private String msg;
    @JsonProperty(value = "status")
    private String status;
    @JsonProperty(value = "data")
    private StrategyResultObj data;
}
