package com.custom.stockCalc.model;

import lombok.*;

import java.io.Serializable;

/**
 * 查詢參數bean
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CodeParam implements Serializable {
    /**
     * 股票代號
     */
    private String code;
    /**
     * 起始日期
     */
    private String beginDate;
    /**
     * 結束日期
     */
    private String endDate;
    /**
     * 年度
     */
    private String year;
    /**
     * 季度
     */
    private String season;
}
