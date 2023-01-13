package com.custom.stockCalc.model.finmind.backTesting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class BackTesting implements Serializable {
    @Id
    private String backTestingId;
    @Lob
    @JsonProperty(value = "StrategyResult")
    private _StrategyResult strategyResults;
    @Lob
    @JsonProperty(value = "Plot")
    private _Plot plot;
    @Lob
    @JsonProperty(value = "TradeDetailList")
    private _TradeDetailList tradeDetailList;
    @Lob
    @JsonProperty(value = "PlotCompare")
    private _PlotCompare plotCompare;
    /**
     * 更新資料日期
     */
    private LocalDate updateDate;
}

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class _StrategyResult implements Serializable {
    @JsonProperty(value = "stock_id")
    private String stockId;
    @JsonProperty(value = "trader_fund")
    private BigDecimal traderFund;
    @JsonProperty(value = "MeanProfitPer")
    private BigDecimal meanProfitPer;
    @JsonProperty(value = "FinalProfit")
    private BigDecimal finalProfit;
    @JsonProperty(value = "FinalProfitPer")
    private BigDecimal finalProfitPer;
    @JsonProperty(value = "MaxLoss")
    private BigDecimal maxLoss;
    @JsonProperty(value = "MaxLossPer")
    private BigDecimal maxLossPer;
    @JsonProperty(value = "AnnualReturnPer")
    private BigDecimal annualReturnPer;
    @JsonProperty(value = "AnnualSharpRatio")
    private BigDecimal annualSharpRatio;
}

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class _Plot implements Serializable {
    @JsonProperty(value = "labels")
    private LocalDate[] labels;
    @JsonProperty(value = "series")
    private int[] series;
    @JsonProperty(value = "signal")
    private int[] signal;
    @JsonProperty(value = "trade_price")
    private BigDecimal[] tradePrice;
}

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class _TradeDetailList implements Serializable {
    @JsonProperty(value = "TradeDetail")
    private _TradeDetail[] tradeDetail;
}

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class _TradeDetail implements Serializable {
    @JsonProperty(value = "labels")
    private LocalDate labels;
    @JsonProperty(value = "EverytimeProfit")
    private BigDecimal everytimeProfit;
    @JsonProperty(value = "RealizedProfit")
    private BigDecimal realizedProfit;
    @JsonProperty(value = "UnrealizedProfit")
    private BigDecimal unrealizedProfit;
    @JsonProperty(value = "trader_fund")
    private BigDecimal traderFund;
    @JsonProperty(value = "hold_cost")
    private BigDecimal holdCost;
    @JsonProperty(value = "hold_volume")
    private BigDecimal holdVolume;
}

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
class _PlotCompare implements Serializable {
    @JsonProperty(value = "labels")
    private LocalDate[] labels;
    @JsonProperty(value = "series")
    private int[] series;
    @JsonProperty(value = "market_series")
    private BigDecimal[] marketSeries;
}


