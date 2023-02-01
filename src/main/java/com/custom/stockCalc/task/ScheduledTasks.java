package com.custom.stockCalc.task;

import com.custom.stockCalc.model.StockData;
import com.custom.stockCalc.model.config.TaskConfig;
import com.custom.stockCalc.model.config.TaskKey;
import com.custom.stockCalc.model.financial.FinancialSheet;
import com.custom.stockCalc.model.stockDayView.Company;
import com.custom.stockCalc.provider.DateProvider;
import com.custom.stockCalc.provider.WebProvider;
import com.custom.stockCalc.repo.TaskConfigRepo;
import com.custom.stockCalc.service.FinancialInfo;
import com.custom.stockCalc.service.StockInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduledTasks {

    private final Log log = LogFactory.getLog(ScheduledTasks.class);
    private final String COMPANY_URL = "https://openapi.twse.com.tw/v1/exchangeReport/TWTB4U";

    private final String FINANCIAL_URL = "https://openapi.twse.com.tw/v1/exchangeReport/BWIBBU_d";
    private final WebProvider webProvider = new WebProvider();
    private final DateProvider dateProvider = new DateProvider();

    private final String stockDataStartDate = "20221201";
    private final String financialStartDate = "2022:3";
    private final String nothingLeft = "nothing left";
    private String stockDataDate = stockDataStartDate;
    private String financialDate = financialStartDate;
    @Autowired
    private TaskConfigRepo taskConfigRepo;
    @Autowired
    private StockInfo stockInfo;
    @Autowired
    private FinancialInfo financialInfo;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    public void refreshCompanyArray() throws Exception {
        Company[] companies = webProvider.getUrlToObject(COMPANY_URL, Company[].class);
        getCompanyList(companies, true, TaskKey.companyList.toString());
        getCompanyList(companies, false, TaskKey.stockCodes.toString());

        companies = webProvider.getUrlToObject(FINANCIAL_URL, Company[].class);
        getCompanyList(companies, true, TaskKey.companyList_financial.toString());
        getCompanyList(companies, false, TaskKey.stockCodes_financial.toString());
    }

    private List<String> getCompanyList(Company[] companies, boolean isFullName, String key) {
        List<String> companyList = new ArrayList<>();
        for (Company company : companies) {
            if (isFullName) {
                companyList.add(company.getCode() + ":" + company.getName());
            } else {
                companyList.add(company.getCode());
            }
        }

        if (taskConfigRepo.findById(key).isPresent()) {
            if (taskConfigRepo.findById(key).get().getConfigValue().equals(companyList)) {
                return companyList;
            }
        }
        return taskConfigRepo.save(new TaskConfig(key, companyList)).getConfigValue();
    }

    @Scheduled(fixedDelay = 1000 * 60)
    public void getStockData() throws Exception {
        stockDataDate = dateProvider.getPreMonthDate(stockDataDate);
        String next = getNextStockDataCompany(stockDataDate.contains(TaskKey.changeNewStockCode.toString()));

        if (next.equals(nothingLeft)) {
            log.info(nothingLeft);
            return;
        }

        List<StockData> stockDataList = stockInfo.getStockInfo(next, stockDataDate, false);
        logData("getStockData", next, stockDataDate, stockDataList);
    }

    @Scheduled(fixedDelay = 1000 * 60 * 2)
    public void getFinancialData() throws Exception {
        financialDate = dateProvider.getPreSeasonDate(financialDate);
        String next = getNextFinancialCompany(financialDate.contains(TaskKey.changeNewStockCode.toString()));

        if (next.equals(nothingLeft)) {
            log.info(nothingLeft);
            return;
        }

        FinancialSheet financialSheet = financialInfo.getFinancial(next, financialDate.split(":")[0], financialDate.split(":")[1]);
        logData("getFinancialData", next, financialDate, financialSheet);
    }

    private String getNextStockDataCompany(boolean nextStock) {
        List<String> remainStockDataList = taskConfigRepo.findById(TaskKey.remainStockDataList.toString()).orElse(new TaskConfig()).getConfigValue();
        if (null == remainStockDataList) {
            remainStockDataList = new ArrayList<>(taskConfigRepo.findById(TaskKey.stockCodes.toString()).get().getConfigValue());
            taskConfigRepo.save(new TaskConfig(TaskKey.remainStockDataList.toString(), remainStockDataList));
        }

        if (remainStockDataList.isEmpty()) {
            return nothingLeft;
        }

        String next;
        if (nextStock) {
            next = remainStockDataList.get(1);
            this.stockDataDate = stockDataStartDate;

            remainStockDataList.remove(0);
            updateConfigDataList(TaskKey.remainStockDataList.toString(), remainStockDataList);
        } else {
            next = remainStockDataList.get(0);
        }

        return next;
    }

    private String getNextFinancialCompany(boolean nextStock) {
        List<String> remainFinancialList = taskConfigRepo.findById(TaskKey.remainFinancialList.toString()).orElse(new TaskConfig()).getConfigValue();
        if (null == remainFinancialList) {
            remainFinancialList = new ArrayList<>(taskConfigRepo.findById(TaskKey.stockCodes_financial.toString()).get().getConfigValue());
            taskConfigRepo.save(new TaskConfig(TaskKey.remainFinancialList.toString(), remainFinancialList));
        }

        if (remainFinancialList.isEmpty()) {
            return nothingLeft;
        }

        String next;

        if (nextStock) {
            next = remainFinancialList.get(1);
            this.financialDate = financialStartDate;

            remainFinancialList.remove(0);
            updateConfigDataList(TaskKey.remainFinancialList.toString(), remainFinancialList);
        } else {
            next = remainFinancialList.get(0);
        }

        return next;
    }

    private void updateConfigDataList(String key, List<String> dataList) {
        if (taskConfigRepo.findById(key).isPresent()) {
            taskConfigRepo.deleteById(key);
        }
        log.info("remain list " + key + " : " + dataList.toString());
        taskConfigRepo.save(new TaskConfig(key, dataList));
    }

    private void logData(String where, String next, String date, Object data) {
        String baseStr = "%s : { code:%s, date:%s, %s }";
        log.info(String.format(baseStr, where, next, date, data));
    }

}
