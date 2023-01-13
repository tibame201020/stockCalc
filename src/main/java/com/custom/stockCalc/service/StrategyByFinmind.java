package com.custom.stockCalc.service;


import com.custom.stockCalc.model.finmind.backTesting.BackTesting;
import com.custom.stockCalc.model.finmind.strategyResult.StrategyResult;
import com.custom.stockCalc.model.finmind.strategyResultByCode.StrategyResultByCode;
import com.custom.stockCalc.model.finmind.strategySummary.StrategySummary;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public interface StrategyByFinmind {
    Log log = LogFactory.getLog(StrategyByFinmind.class);

    /**
     * 取得策略種類與描述
     *
     * @return List<StrategySummary>
     * @throws Exception
     */
    List<StrategySummary> getStrategySummary() throws Exception;

    /**
     * 取得策略結果
     *
     * @param strategyName 策略種類
     * @return List<StrategyResult>
     * @throws Exception
     */
    List<StrategyResult> getStrategyResult(String strategyName) throws Exception;

    /**
     * 取得策略結果By股票代碼
     *
     * @param stockCode 個股
     * @return List<StrategyResultByCode>
     * @throws Exception
     */
    List<StrategyResultByCode> getStrategyResultByCode(String stockCode) throws Exception;

    /**
     * 取得回測結果
     *
     * @param strategyName 策略
     * @param stockCode    個股
     * @param userFunds    資金
     * @param beginDate    開始日期
     * @param endDate      結束日期
     * @return BackTesting
     * @throws Exception
     */
    BackTesting getBackTesting(String strategyName, String stockCode, long userFunds, String beginDate, String endDate) throws Exception;
}
