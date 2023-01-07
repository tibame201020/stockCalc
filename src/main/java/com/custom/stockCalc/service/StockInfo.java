package com.custom.stockCalc.service;

import com.custom.stockCalc.model.StockData;
import com.custom.stockCalc.model.StockImmediateInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;

public interface StockInfo {

    Log log = LogFactory.getLog(StockInfo.class);

    /**
     * 取得股價資訊 單位:天
     * @param code 股票代碼
     * @param beginDate 開始日期
     * @param endDate 結束日期
     * @return 開始至結束區間股價資訊
     * @throws Exception
     */
    List<StockData> getStockData(String code, String beginDate, String endDate) throws Exception;

    /**
     * 即時股價資訊
     * @param code 股票代碼
     * @return 盤中整股與盤中零股交易資訊
     * @throws Exception
     */
    Map<String, StockImmediateInfo> getImmediateStock(String code) throws Exception;

    /**
     * 取得有關鍵字的股票代碼，以利使用者查詢
     * @param key 關鍵字
     * @return 包含關鍵字的股票代碼List
     * @throws Exception
     */
    List<String> getCodeNmList(String key) throws Exception;

    /**
     * for task爬歷史股價使用
     * @param code 股票代碼
     * @param dateStr 目標日期
     * @param isThisMonth 是否與當下同年月份
     * @return
     * @throws Exception
     */
    List<StockData> getStockInfo(String code, String dateStr, boolean isThisMonth) throws Exception;
}
