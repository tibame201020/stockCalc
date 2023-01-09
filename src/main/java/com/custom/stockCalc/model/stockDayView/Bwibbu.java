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
//https://openapi.twse.com.tw/v1/exchangeReport/BWIBBU_ALL
public class Bwibbu implements Serializable {
    @Id
    @JsonProperty(value = "Code")
    private String code;
    @JsonProperty(value = "Name")
    private String name;
    /**
     * 日本益比
     */
    @JsonProperty(value = "PEratio")
    private BigDecimal pEratio;
    /**
     * 殖利率
     */
    @JsonProperty(value = "DividendYield")
    private BigDecimal dividendYield;
    /**
     * 股價淨值比
     */
    @JsonProperty(value = "PBratio")
    private BigDecimal pBratio;

    /**
     * 更新資料日期
     */
    private LocalDate updateDate;
}
