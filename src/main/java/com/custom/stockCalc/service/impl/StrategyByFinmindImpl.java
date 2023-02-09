package com.custom.stockCalc.service.impl;

import com.custom.stockCalc.model.config.TaskConfig;
import com.custom.stockCalc.model.config.TaskKey;
import com.custom.stockCalc.model.finmind.backTesting.BackTesting;
import com.custom.stockCalc.model.finmind.backTesting.BackTestingStatus;
import com.custom.stockCalc.model.finmind.strategyResult.StrategyResult;
import com.custom.stockCalc.model.finmind.strategyResult.StrategyResultStatus;
import com.custom.stockCalc.model.finmind.strategyResultByCode.StrategyResultByCode;
import com.custom.stockCalc.model.finmind.strategyResultByCode.StrategyResultStatusByCode;
import com.custom.stockCalc.model.finmind.strategySummary.StrategySummary;
import com.custom.stockCalc.model.finmind.strategySummary.StrategySummaryStatus;
import com.custom.stockCalc.provider.DateProvider;
import com.custom.stockCalc.provider.WebProvider;
import com.custom.stockCalc.repo.*;
import com.custom.stockCalc.service.StrategyByFinmind;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StrategyByFinmindImpl implements StrategyByFinmind {

    @Autowired
    private StrategySummaryRepo strategySummaryRepo;
    @Autowired
    private StrategyResultRepo strategyResultRepo;
    @Autowired
    private StrategyResultByCodeRepo strategyResultByCodeRepo;
    @Autowired
    private BackTestingRepo backTestingRepo;

    @Autowired
    private TaskConfigRepo taskConfigRepo;

    private WebProvider webProvider = new WebProvider();
    private DateProvider dateProvider = new DateProvider();

    private String strategySummaryUrl = "https://api.web.finmindtrade.com/v2/strategy_summary";
    private String strategyResultUrl = "https://api.web.finmindtrade.com/v2/strategy_analysis%s";
    private String backTestingUrl = "https://api.web.finmindtrade.com/v2/backtesting%s";

    @Override
    public List<StrategySummary> getStrategySummary() throws Exception {
        List<StrategySummary> strategySummaryList = strategySummaryRepo.findAll();

        if (strategySummaryList.isEmpty()) {
            strategySummaryList = getStrategySummaryFromUrl();
            return strategySummaryRepo.saveAll(strategySummaryList);
        }

        boolean isUpdateDateToday = dateProvider.isUpdateDateToday(strategySummaryList.get(0).getUpdateDate());

        if (isUpdateDateToday) {
            return strategySummaryList;
        }

        strategySummaryList = getStrategySummaryFromUrl();
        return strategySummaryRepo.saveAll(strategySummaryList);
    }

    @Override
    public List<StrategyResult> getStrategyResult(String strategyName) throws Exception {
        List<StrategyResult> strategyResultList = strategyResultRepo.findByStrategyName(strategyName);

        if (strategyResultList.isEmpty()) {
            strategyResultList = getStrategyResultFromUrl(strategyName);
            return strategyResultRepo.saveAll(strategyResultList);
        }

        boolean isUpdateDateToday = dateProvider.isUpdateDateToday(strategyResultList.get(0).getUpdateDate());
        if (isUpdateDateToday) {
            return strategyResultList;
        }

        strategyResultList = getStrategyResultFromUrl(strategyName);
        return strategyResultRepo.saveAll(strategyResultList);
    }

    @Override
    public List<StrategyResultByCode> getStrategyResultByCode(String stockCode) throws Exception {
        if (!stockCodeIsValid(stockCode)) {
            return null;
        }

        List<StrategyResultByCode> strategyResultByCodeList = strategyResultByCodeRepo.findByStockId(stockCode);

        if (strategyResultByCodeList.isEmpty()) {
            strategyResultByCodeList = getStrategyResultByCodeFromUrl(stockCode);
            return strategyResultByCodeRepo.saveAll(strategyResultByCodeList);
        }

        boolean isUpdateDateToday = dateProvider.isUpdateDateToday(strategyResultByCodeList.get(0).getUpdateDate());
        if (isUpdateDateToday) {
            return strategyResultByCodeList;
        }


        strategyResultByCodeList = getStrategyResultByCodeFromUrl(stockCode);
        return strategyResultByCodeRepo.saveAll(strategyResultByCodeList);
    }

    @Override
    public BackTesting getBackTesting(String strategyName, String stockCode, long userFunds, String beginDate, String endDate) throws Exception {
        if (!stockCodeIsValid(stockCode)) {
            return null;
        }

        beginDate = dateProvider.parseDate(beginDate, "yyyy-MM-dd");
        endDate = dateProvider.parseDate(endDate, "yyyy-MM-dd");
        String backTestingId = stockCode + strategyName + userFunds + beginDate + endDate;
        String finalEndDate = endDate;
        String finalBeginDate = beginDate;
        BackTesting backTesting = backTestingRepo.findById(backTestingId).orElseGet(() -> {
            try {
                return getBackTestingFromUrl(strategyName, stockCode, userFunds, finalBeginDate, finalEndDate);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        boolean isUpdateDateToday = dateProvider.isUpdateDateToday(backTesting.getUpdateDate());
        if (isUpdateDateToday) {
            return backTesting;
        }

        return getBackTestingFromUrl(strategyName, stockCode, userFunds, finalBeginDate, finalEndDate);
    }

    private BackTesting getBackTestingFromUrl(String strategyName, String stockCode, long userFunds, String beginDate, String endDate) throws Exception {
        String param = String.format("?stock_id=%s&strategy_name=%s&user_funds=%s&start_date=%s&end_date=%s",
                stockCode,
                strategyName,
                userFunds,
                beginDate,
                endDate);
        String url = String.format(backTestingUrl, param);
        String backTestingId = stockCode + strategyName + userFunds + beginDate + endDate;

        BackTestingStatus backTestingStatus = webProvider.getUrlToObject(url, BackTestingStatus.class);
        BackTesting backTesting = backTestingStatus.getData();
        backTesting.setBackTestingId(backTestingId);
        backTesting.setUpdateDate(LocalDate.now());

        return backTestingRepo.save(backTesting);
    }

    private List<StrategyResultByCode> getStrategyResultByCodeFromUrl(String stockCode) throws Exception {
        String param = String.format("?stock_id=%s", stockCode);
        String url = String.format(strategyResultUrl, param);
        StrategyResultStatusByCode strategyResultStatusByCode = webProvider.getUrlToObject(url, StrategyResultStatusByCode.class);
        StrategyResultByCode[] strategyResultByCodes = strategyResultStatusByCode.getData().getStrategyResultByCodes();
        return Arrays.stream(strategyResultByCodes).peek(strategyResultByCode -> {
            strategyResultByCode.setStrategyId(strategyResultByCode.getStockId() + strategyResultByCode.getStrategyName());
            strategyResultByCode.setUpdateDate(LocalDate.now());
        }).collect(Collectors.toList());
    }

    private List<StrategyResult> getStrategyResultFromUrl(String strategyName) throws Exception {
        String param = String.format("?strategy=%s", strategyName);
        String url = String.format(strategyResultUrl, param);
        StrategyResultStatus strategyResultStatus = webProvider.getUrlToObject(url, StrategyResultStatus.class);
        StrategyResult[] strategyResults = strategyResultStatus.getData().getStrategyResults();

        return Arrays.stream(strategyResults).peek(strategyResult -> {
            strategyResult.setStrategyId(strategyResult.getStockId() + strategyResult.getStrategyName());
            strategyResult.setUpdateDate(LocalDate.now());
        }).collect(Collectors.toList());
    }

    private List<StrategySummary> getStrategySummaryFromUrl() throws Exception {
        StrategySummaryStatus strategySummaryStatus = webProvider.getUrlToObject(strategySummaryUrl, StrategySummaryStatus.class);
        StrategySummary[] strategySummaries = strategySummaryStatus.getData().getStrategySummaries();
        return Arrays.stream(strategySummaries).peek(strategySummary -> strategySummary.setUpdateDate(LocalDate.now())).collect(Collectors.toList());
    }

    private boolean stockCodeIsValid(String stockCode) {
        List<String> stockCodes = taskConfigRepo.findById(TaskKey.stockCodes.toString()).orElseGet(TaskConfig::new).getConfigValue();
        return stockCodes.contains(stockCode);
    }
}
