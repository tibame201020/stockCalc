package com.custom.stockCalc.model.stockDayView;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 上市公司營益分析查詢彙總表(全體公司彙總報表)
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CompanySummaryReport implements Serializable {
    /**
     * 出表日期
     */
    @JsonProperty(value = "出表日期")
    private String reportDate;
    /**
     * 年度
     */
    @JsonProperty(value = "年度")
    private String reportYear;
    /**
     * 季別
     */
    @JsonProperty(value = "季別")
    private String reportSeason;
    /**
     * 公司代號
     */
    @Id
    @JsonProperty(value = "公司代號")
    private String companyCode;
    /**
     * 公司名稱
     */
    @JsonProperty(value = "公司名稱")
    private String companyName;
    /**
     * 營業收入(百萬元)
     */
    @JsonProperty(value = "營業收入(百萬元)")
    private BigDecimal income;
    /**
     * 毛利率
     */
    @JsonProperty(value = "毛利率(%)(營業毛利)/(營業收入)")
    private BigDecimal grossProfitMargin;
    /**
     * 營業利益率
     */
    @JsonProperty(value = "營業利益率(%)(營業利益)/(營業收入)")
    private BigDecimal operatingProfitRatio;
    /**
     * 稅前純益率
     */
    @JsonProperty(value = "稅前純益率(%)(稅前純益)/(營業收入)")
    private BigDecimal preTaxIncomeMargin;
    /**
     * 稅後純益率
     */
    @JsonProperty(value = "稅後純益率(%)(稅後純益)/(營業收入)")
    private BigDecimal netProfitMargin;
    /**
     * 更新資料日期
     */
    private LocalDate updateDate;

}
