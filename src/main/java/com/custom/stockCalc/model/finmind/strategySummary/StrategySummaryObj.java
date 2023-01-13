package com.custom.stockCalc.model.finmind.strategySummary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StrategySummaryObj implements Serializable {
    @JsonProperty(value = "StrategySummary")
    private StrategySummary[] strategySummaries;
}
