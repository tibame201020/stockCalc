package com.custom.stockCalc.service;

import com.custom.stockCalc.model.StockData;
import com.custom.stockCalc.model.StockImmediateInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;

public interface StockInfo {

    Log log = LogFactory.getLog(StockInfo.class);
    List<StockData> getStockData(String code, String beginDate, String endDate) throws Exception;

    Map<String, StockImmediateInfo> getImmediateStock(String code) throws Exception;

    List<String> getCodeNmList(String key) throws Exception;

    void getFinancial(String code, String year, String season) throws Exception;
}
