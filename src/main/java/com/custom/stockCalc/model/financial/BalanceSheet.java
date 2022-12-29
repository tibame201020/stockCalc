package com.custom.stockCalc.model.financial;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Id;
import javax.persistence.Lob;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@ToString
@Setter
@Getter
//資產負債表
public class BalanceSheet {
    @Id
    @Lob
    private String originData;
    //資產
    private Asset asset;
    //負債及權益
    private LiabilitiesAndEquity liabilitiesAndEquity;

}

@ToString
@Setter
@Getter
class Asset {
    //流動資產
    private List<Map<String, BigDecimal>> currentAssets;
    //流動資產總計
    private BigDecimal totalCurrentAssets;
    //非流動資產
    private List<Map<String, BigDecimal>> nonCurrentAssets;
    //非流動資產總計
    private BigDecimal totalNonCurrentAssets;
    //資產總計
    private BigDecimal totalAssets;
}

@ToString
@Setter
@Getter
class LiabilitiesAndEquity {
    //流動負債
    private List<Map<String, BigDecimal>> currentLiabilities;
    //流動負債總計
    private BigDecimal totalCurrentLiabilities;
    //非流動負債
    private List<Map<String, BigDecimal>> nonCurrentLiabilities;
    //非流動負債總計
    private BigDecimal totalNonCurrentLiabilities;

    private BigDecimal totalLiabilities;

}
