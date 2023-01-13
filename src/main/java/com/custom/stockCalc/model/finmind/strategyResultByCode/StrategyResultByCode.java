package com.custom.stockCalc.model.finmind.strategyResultByCode;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 交易策略結果
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class StrategyResultByCode implements Serializable {

    @Id
    private String strategyId;
    @JsonProperty(value = "stock_id")
    private String stockId;
    @JsonProperty(value = "start_date")
    private String startDate;
    @JsonProperty(value = "end_date")
    private String endDate;
    @JsonProperty(value = "StrategyName")
    private String strategyName;
    @JsonProperty(value = "MaxLossPer")
    private String maxLossPer;
    @JsonProperty(value = "MaxLossPer0050")
    private String maxLossPer0050;
    @JsonProperty(value = "FinalProfitPer")
    private String finalProfitPer;
    @JsonProperty(value = "FinalProfitPer0050")
    private String finalProfitPer0050;
    /**
     * 更新資料日期
     */
    private LocalDate updateDate;
}
