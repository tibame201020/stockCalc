package com.custom.stockCalc.controller;

import com.custom.stockCalc.model.CodeParam;
import com.custom.stockCalc.model.StockData;
import com.custom.stockCalc.model.StockImmediateInfo;
import com.custom.stockCalc.model.financial.SimpleSheet;
import com.custom.stockCalc.service.FinancialInfo;
import com.custom.stockCalc.service.StockInfo;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/stock")
public class StockController {
    @Autowired
    private StockInfo stockInfo;
    @Autowired
    private FinancialInfo financialInfo;

    @RequestMapping("/getStockData")
    public List<StockData> getStockData(@RequestBody CodeParam codeParam) throws Exception {
        return stockInfo.getStockData(codeParam.getCode(), codeParam.getBeginDate(), codeParam.getEndDate());
    }

    @RequestMapping("/getImmediateStock")
    public Map<String, StockImmediateInfo> getImmediateStock(@RequestBody String code) throws Exception {
        return stockInfo.getImmediateStock(code);
    }

    @RequestMapping("/getFinancial")
    public List<Map> getFinancial(@RequestBody CodeParam codeParam) throws Exception {
        return Arrays.stream(financialInfo.getFinancial(codeParam.getCode(), codeParam.getYear(), codeParam.getSeason()).getSheets())
                .map(jsonStr -> new Gson().fromJson(jsonStr, Map.class))
                .collect(Collectors.toList());
    }

    @RequestMapping("/getSheetByCodeAndDateRange")
    public List<SimpleSheet> getSheetByCodeAndDateRange(@RequestBody CodeParam codeParam) throws Exception {
        return financialInfo.getSheetByCodeAndDateRange(codeParam.getCode(), codeParam.getBeginDate(), codeParam.getEndDate());
    }

}
