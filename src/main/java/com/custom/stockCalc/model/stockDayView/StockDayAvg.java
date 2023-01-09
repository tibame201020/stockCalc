package com.custom.stockCalc.model.stockDayView;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
//https://openapi.twse.com.tw/v1/exchangeReport/STOCK_DAY_AVG_ALL
public class StockDayAvg implements Serializable {
    @Id
    @JsonProperty(value = "Code")
    private String code;
    @JsonProperty(value = "Name")
    private String name;
    /**
     * 昨收價格
     */
    @JsonProperty(value = "ClosingPrice")
    private BigDecimal closingPrice;
    /**
     * 月均價格
     */
    @JsonProperty(value = "MonthlyAveragePrice")
    private BigDecimal monthlyAveragePrice;
    /**
     * 更新資料日期
     */
    private LocalDate updateDate;
}
