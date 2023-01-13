package com.custom.stockCalc.controller;

import com.custom.stockCalc.model.finmind.backTesting.BackTesting;
import com.custom.stockCalc.model.finmind.backTesting.BackTestingParam;
import com.custom.stockCalc.model.finmind.strategyResult.StrategyResult;
import com.custom.stockCalc.model.finmind.strategyResultByCode.StrategyResultByCode;
import com.custom.stockCalc.model.finmind.strategySummary.StrategySummary;
import com.custom.stockCalc.service.StrategyByFinmind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * wrapper opendata finmind
 */
@RestController
@RequestMapping("api/finmind/")
public class StrategyController {

    @Autowired
    private StrategyByFinmind strategyByFinmind;

    /**
     * 取得策略種類與描述
     *
     * @return List<StrategySummary>
     * @throws Exception
     */
    @RequestMapping("getStrategySummary")
    public List<StrategySummary> getStrategySummary() throws Exception {
        return strategyByFinmind.getStrategySummary();
    }

    /**
     * 取得策略結果
     *
     * @param strategyName 策略種類
     * @return List<StrategyResult>
     * @throws Exception
     */
    @RequestMapping("getStrategyResult")
    public List<StrategyResult> getStrategyResult(@RequestBody String strategyName) throws Exception {
        return strategyByFinmind.getStrategyResult(strategyName);
    }

    /**
     * 取得策略結果By股票代碼
     *
     * @param stockCode
     * @return List<StrategyResultByCode>
     * @throws Exception
     */
    @RequestMapping("getStrategyResultByCode")
    public List<StrategyResultByCode> getStrategyResultByCode(@RequestBody String stockCode) throws Exception {
        return strategyByFinmind.getStrategyResultByCode(stockCode);
    }

    /**
     * 取得回測結果(策略、個股、資金、開始日期、結束日期)
     *
     * @param backTestingParam 查詢bean
     * @return BackTesting
     * @throws Exception
     */
    @RequestMapping("getBackTesting")
    public BackTesting getBackTesting(@RequestBody BackTestingParam backTestingParam) throws Exception {
        return strategyByFinmind.getBackTesting(backTestingParam.getStrategyName(), backTestingParam.getStockCode(), backTestingParam.getUserFunds(), backTestingParam.getBeginDate(), backTestingParam.getEndDate());
    }


}
