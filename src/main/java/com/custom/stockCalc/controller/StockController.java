package com.custom.stockCalc.controller;

import com.custom.stockCalc.model.CodeParam;
import com.custom.stockCalc.model.StockData;
import com.custom.stockCalc.model.StockImmediateInfo;
import com.custom.stockCalc.model.financial.SimpleSheet;
import com.custom.stockCalc.model.stockDayView.Bwibbu;
import com.custom.stockCalc.model.stockDayView.CompanyFinancialReport;
import com.custom.stockCalc.model.stockDayView.CompanySummaryReport;
import com.custom.stockCalc.model.stockDayView.StockDayAvg;
import com.custom.stockCalc.service.FinancialInfo;
import com.custom.stockCalc.service.StockDayView;
import com.custom.stockCalc.service.StockInfo;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * wrapper opendata twse
 */
@RestController
@RequestMapping("/api/stock")
public class StockController {
    @Autowired
    private StockInfo stockInfo;
    @Autowired
    private FinancialInfo financialInfo;
    @Autowired
    private StockDayView stockDayView;

    /**
     * 取得日期區間內股價資訊
     *
     * @param codeParam 查詢參數
     * @return List<StockData>
     * @throws Exception
     */
    @RequestMapping("/getStockData")
    public List<StockData> getStockData(@RequestBody CodeParam codeParam) throws Exception {
        return stockInfo.getStockData(codeParam.getCode(), codeParam.getBeginDate(), codeParam.getEndDate());
    }

    /**
     * 取得盤中即時股價資訊
     *
     * @param code 股票代碼
     * @return Map<String, StockImmediateInfo> 整股與零股資訊
     * @throws Exception
     */
    @RequestMapping("/getImmediateStock")
    public Map<String, StockImmediateInfo> getImmediateStock(@RequestBody String code) throws Exception {
        return stockInfo.getImmediateStock(code);
    }

    /**
     * 取得完整財報
     *
     * @param codeParam 查詢參數
     * @return List<Map> 資產負債表、綜合損益表、現金流量表
     * @throws Exception
     */
    @RequestMapping("/getFinancial")
    public List<Map> getFinancial(@RequestBody CodeParam codeParam) throws Exception {
        return Arrays.stream(financialInfo.getFinancial(codeParam.getCode(), codeParam.getYear(), codeParam.getSeason()).getSheets())
                .map(jsonStr -> new Gson().fromJson(jsonStr, Map.class))
                .collect(Collectors.toList());
    }

    /**
     * 取得日期區間內簡易財報資料
     *
     * @param codeParam 查詢參數
     * @return List<SimpleSheet> 簡易財報資料
     * @throws Exception
     */
    @RequestMapping("/getSheetByCodeAndDateRange")
    public List<SimpleSheet> getSheetByCodeAndDateRange(@RequestBody CodeParam codeParam) throws Exception {
        return financialInfo.getSheetByCodeAndDateRange(codeParam.getCode(), codeParam.getBeginDate(), codeParam.getEndDate());
    }

    /**
     * 取得上市公司營益分析查詢彙總表(全體公司彙總報表)
     *
     * @return List<CompanySummaryReport>
     * @throws Exception
     */
    @RequestMapping("getCompanySummaryReport")
    public List<CompanySummaryReport> getCompanySummaryReport() throws Exception {
        return stockDayView.getCompanySummaryReport();
    }

    /**
     * 取得當季上市公司報表
     *
     * @return List<CompanyFinancialReport>
     * @throws Exception
     */
    @RequestMapping("getCompanyFinancialReport")
    public List<CompanyFinancialReport> getCompanyFinancialReport() throws Exception {
        return stockDayView.getCompanyFinancialReport();
    }

    /**
     * 取得昨收與月均價格
     *
     * @return List<StockDayAvg>
     * @throws Exception
     */
    @RequestMapping("getStockDayAvg")
    public List<StockDayAvg> getStockDayAvg() throws Exception {
        return stockDayView.getStockDayAvg();
    }

    /**
     * 取得上市個股日本益比、殖利率及股價淨值比
     *
     * @return List<Bwibbu>
     * @throws Exception
     */
    @RequestMapping("getBwibbu")
    public List<Bwibbu> getBwibbu() throws Exception {
        return stockDayView.getBwibbu();
    }

    /**
     * 取得有關鍵字的股票代碼List，以利使用者查詢
     *
     * @param key 關鍵字
     * @return 包含關鍵字的股票代碼List
     * @throws Exception
     */
    @RequestMapping("getCodeNmList")
    public List<String> getCodeNmList(@RequestBody String key) throws Exception {
        return stockInfo.getCodeNmList(key);
    }

    /**
     * 取得有關鍵字的公司代碼List，以利使用者查詢
     *
     * @param key 關鍵字
     * @return 包含關鍵字的公司代碼List
     * @throws Exception
     */
    @RequestMapping("getCompanyNmList")
    public List<String> getCompanyNmList(@RequestBody String key) throws Exception {
        return financialInfo.getCompanyNmList(key);
    }

}
