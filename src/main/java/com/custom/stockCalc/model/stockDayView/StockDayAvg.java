package com.custom.stockCalc.model.stockDayView;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

@ToString
@NoArgsConstructor
@Getter
@Setter
//https://openapi.twse.com.tw/v1/exchangeReport/STOCK_DAY_AVG_ALL
public class StockDayAvg implements Serializable {
    @JsonProperty(value = "Code")
    private String code;
    @JsonProperty(value = "Name")
    private String name;
    //昨收價格
    @JsonProperty(value = "ClosingPrice")
    private BigDecimal closingPrice;
    //月均價格
    @JsonProperty(value = "MonthlyAveragePrice")
    private BigDecimal monthlyAveragePrice;
}
