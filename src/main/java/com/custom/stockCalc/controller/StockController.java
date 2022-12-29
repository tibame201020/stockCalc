package com.custom.stockCalc.controller;

import com.custom.stockCalc.model.CodeParam;
import com.custom.stockCalc.model.StockData;
import com.custom.stockCalc.model.StockImmediateInfo;
import com.custom.stockCalc.model.financial.FinancialSheet;
import com.custom.stockCalc.service.StockInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stock")
public class StockController {
    @Autowired
    private StockInfo stockInfo;

    @RequestMapping("/getStockData")
    public List<StockData> getStockData(@RequestBody CodeParam codeParam) throws Exception {
        return stockInfo.getStockData(codeParam.getCode(), codeParam.getBeginDate(), codeParam.getEndDate());
    }

    @RequestMapping("/getImmediateStock")
    public Map<String, StockImmediateInfo> getImmediateStock(String code) throws Exception {
        return stockInfo.getImmediateStock(code);
    }

    @RequestMapping("/getFinancial")
    public FinancialSheet getImmediateStock(@RequestBody CodeParam codeParam) throws Exception {
        return stockInfo.getFinancial(codeParam.getCode(), codeParam.getYear(), codeParam.getSeason());
    }
}
