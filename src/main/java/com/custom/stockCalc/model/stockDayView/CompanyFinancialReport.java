package com.custom.stockCalc.model.stockDayView;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 當季上市公司報表
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class CompanyFinancialReport implements Serializable {
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
     * 產業別
     */
    @JsonProperty(value = "產業別")
    private String companyCategory;
    /**
     * 基本每股盈餘(元)
     */
    @JsonProperty(value = "基本每股盈餘(元)")
    private BigDecimal eps;
    /**
     * 營業收入
     */
    @JsonProperty(value = "營業收入")
    private BigDecimal income;
    /**
     * 營業利益
     */
    @JsonProperty(value = "營業利益")
    private String operatingExpenses;
    /**
     * 營業外收入及支出
     */
    @JsonProperty(value = "營業外收入及支出")
    private String nonOperatingIncomeAndExpenses;
    /**
     * 稅後淨利
     */
    @JsonProperty(value = "稅後淨利")
    private BigDecimal netIncome;
    /**
     * 更新資料日期
     */
    private LocalDate updateDate;
}
