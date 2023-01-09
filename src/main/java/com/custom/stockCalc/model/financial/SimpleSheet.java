package com.custom.stockCalc.model.financial;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 簡易財報資料
 */
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SimpleSheet implements Serializable {

    @Id
    private String financialSheetId;
    /**
     * 流動資產
     */
    private BigDecimal currentAssets;
    /**
     * 非流動資產
     */
    private BigDecimal nonCurrentAssets;
    /**
     * 總資產
     */
    private BigDecimal totalAssets;
    /**
     * 流動負債
     */
    private BigDecimal currentLiabilities;
    /**
     * 非流動負債
     */
    private BigDecimal nonCurrentLiabilities;
    /**
     * 總負債
     */
    private BigDecimal totalLiabilities;
    /**
     * 權益
     */
    private BigDecimal equity;
    /**
     * 負債及權益總計
     */
    private BigDecimal liabilitiesAndEquity;
    /**
     * 股數
     */
    private BigDecimal ordinaryCnt;
    /**
     * 每股淨額
     */
    private BigDecimal navps;

    /**
     * 本業營收
     */
    private BigDecimal operatingRevenue;
    /**
     * 本業成本
     */
    private BigDecimal operatingCosts;
    /**
     * 毛利(本業營收 - 本業成本)
     */
    private BigDecimal grossProfit;
    /**
     * 毛利率(毛利/本業營收)
     */
    private BigDecimal grossProfitPercent;
    /**
     * 營業費用(ex人事)
     */
    private BigDecimal operatingExpenses;
    /**
     * 本業收入(本業營收 - 本業成本 - 營業費用)
     */
    private BigDecimal operatingIncome;
    /**
     * 外業收入
     */
    private BigDecimal nonOperatingIncome;
    /**
     * 基本每股盈餘
     */
    private BigDecimal eps;
    /**
     * 稀釋每股盈餘
     */
    private BigDecimal dEps;

    /**
     * 營業活動之淨現金流入（流出）
     * 營業活動現金流，指的是所有跟公司本業的營運有關的現金流入 / 流出。由於是記錄了跟「本業營運」相關的現金流，營業活動現金流可以讓投資朋友檢視公司所得的品質——去判斷賺到的「錢」，是否真的有以「現金」的方式流入。營業現金流越大，代表公司資金越充裕；如果可以長年維持正數又持續增長，可以初步判斷公司的本業營運體質很健康、也具成長性。
     */
    private BigDecimal operatingActivities;

    /**
     * 投資活動之淨現金流入（流出）
     * 投資活動現金流，指的是公司以「投資」為目的所發生的現金流。就公司的角度而言，所謂的「投資」，可能是像各位投資朋友一樣以交易、賺價差為目的買賣金融商品（例如：股、債等），也有另外一種可能是投資「自己」，也就是買賣營運所需的固定資產（例如：機械設備、廠房、不動產等）。
     */
    private BigDecimal investingActivities;

    /**
     * 籌資活動之淨現金流入（流出）
     * 籌資活動現金流，指的是公司為不同目的而償還、籌措、發放資金等等活動，所造成的現金流入或流出。可以視為「本業以外」的現金流。
     */
    private BigDecimal financingActivities;

    public SimpleSheet(String financialSheetId, String[] sheets) {
        this.financialSheetId = financialSheetId;
        for (String sheet : sheets) {
            JsonObject jsonObject = new Gson().fromJson(sheet, JsonObject.class);
            String sheetName = jsonObject.get("sheetName").getAsString();

            switch (sheetName) {
                case "資產負債表":
                    setBalanceSheetProp(jsonObject);
                    break;
                case "綜合損益表":
                    setComprehensiveIncomeProp(jsonObject);
                    break;
                case "現金流量表":
                    setCashFlowsProp(jsonObject);
                    break;
                default:
                    break;
            }
        }
    }

    private void setBalanceSheetProp(JsonObject jsonObject) {
        this.currentAssets = getFromJsonObject(jsonObject, new String[]{"資產", "流動資產", "流動資產合計"});
        this.nonCurrentAssets = getFromJsonObject(jsonObject, new String[]{"資產", "非流動資產", "非流動資產合計"});
        this.totalAssets = getFromJsonObject(jsonObject, new String[]{"資產", "資產總計"});
        this.currentLiabilities = getFromJsonObject(jsonObject, new String[]{"負債及權益", "負債", "流動負債", "流動負債合計"});
        this.nonCurrentLiabilities = getFromJsonObject(jsonObject, new String[]{"負債及權益", "負債", "非流動負債", "非流動負債合計"});
        this.totalLiabilities = getFromJsonObject(jsonObject, new String[]{"負債及權益", "負債", "負債總計"});
        try {
            this.equity = getFromJsonObject(jsonObject, new String[]{"負債及權益", "權益", "權益總額"});
        } catch (Exception e) {
            this.equity = getFromJsonObject(jsonObject, new String[]{"負債及權益", "權益", "權益總計"});
        }
        this.liabilitiesAndEquity = getFromJsonObject(jsonObject, new String[]{"負債及權益", "負債及權益總計"});
        // 普通股股本
        BigDecimal ordinary = getFromJsonObject(jsonObject, new String[]{"負債及權益", "權益", "歸屬於母公司業主之權益", "股本", "普通股股本"});
        this.ordinaryCnt = BigDecimal.valueOf(Math.floor(ordinary.divide(BigDecimal.valueOf(10.0)).doubleValue()));
        this.navps = this.equity.divide(this.ordinaryCnt, 3, BigDecimal.ROUND_FLOOR);
    }

    private void setComprehensiveIncomeProp(JsonObject jsonObject) {
        this.operatingRevenue = getFromJsonObject(jsonObject, new String[]{"營業收入", "營業收入合計"});
        this.operatingCosts = getFromJsonObject(jsonObject, new String[]{"營業成本", "營業成本合計"});
        this.grossProfit = this.operatingRevenue.subtract(this.operatingCosts);
        this.grossProfitPercent = this.grossProfit.divide(this.operatingRevenue, 5, BigDecimal.ROUND_FLOOR).multiply(BigDecimal.valueOf(100));
        this.operatingExpenses = getFromJsonObject(jsonObject, new String[]{"營業費用", "營業費用合計"});
        this.operatingIncome = getFromJsonObject(jsonObject, new String[]{"營業利益（損失）"});
        this.nonOperatingIncome = getFromJsonObject(jsonObject, new String[]{"營業外收入及支出", "營業外收入及支出合計"});
        this.eps = getFromJsonObject(jsonObject, new String[]{"基本每股盈餘", "基本每股盈餘合計"});
        try {
            this.dEps = getFromJsonObject(jsonObject, new String[]{"稀釋每股盈餘", "稀釋每股盈餘合計"});
        } catch (Exception e) {
            this.dEps = null;
        }
    }

    private void setCashFlowsProp(JsonObject jsonObject) {
        this.operatingActivities = getFromJsonObject(jsonObject, new String[]{"營業活動之淨現金流入（流出）"});
        this.investingActivities = getFromJsonObject(jsonObject, new String[]{"投資活動之現金流量", "投資活動之淨現金流入（流出）"});
        this.financingActivities = getFromJsonObject(jsonObject, new String[]{"籌資活動之現金流量", "籌資活動之淨現金流入（流出）"});
    }

    private BigDecimal getFromJsonObject(JsonObject jsonObject, String[] props) {
        for (String prop : props) {
            jsonObject = jsonObject.getAsJsonObject(prop);
        }
        String valueStr = jsonObject.get("value").getAsString().replaceAll(",", "");
        //負數
        if (valueStr.contains("(") && valueStr.contains(")")) {
            return new BigDecimal(valueStr.replaceAll("\\(", "").replaceAll("\\)", "")).negate();
        }

        return new BigDecimal(valueStr);
    }
}
