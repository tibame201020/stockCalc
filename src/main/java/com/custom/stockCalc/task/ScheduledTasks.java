package com.custom.stockCalc.task;

import com.custom.stockCalc.model.StockData;
import com.custom.stockCalc.model.config.TaskConfig;
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

import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduledTasks {

    private final Log log = LogFactory.getLog(ScheduledTasks.class);
    private final String COMPANY_URL = "https://openapi.twse.com.tw/v1/exchangeReport/TWTB4U";

    private final String FINANCIAL_URL = "https://openapi.twse.com.tw/v1/exchangeReport/BWIBBU_d";
    private final WebProvider webProvider = new WebProvider();
    private final DateProvider dateProvider = new DateProvider();

    private String stockDataDate = "20221201";
    private String financialDate = "2022:3";

    @Autowired
    private TaskConfigRepo taskConfigRepo;
    @Autowired
    private StockInfo stockInfo;
    @Autowired
    private FinancialInfo financialInfo;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    public void refreshCompanyArray() throws Exception {
        Company[] companies = webProvider.getUrlToObject(COMPANY_URL, Company[].class);
        getCompanyList(companies, true, "companyList");
        getCompanyList(companies, false, "stockCodes");

        companies = webProvider.getUrlToObject(FINANCIAL_URL, Company[].class);
        getCompanyList(companies, true, "companyList_financial");
        getCompanyList(companies, false, "stockCodes_financial");
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

    @Scheduled(fixedRate = 1000 * 60)
    public void getStockData() throws Exception {
        stockDataDate = dateProvider.getPreMonthDate(stockDataDate);
        String next = getNextStockDataCompany(stockDataDate.contains("changeNewStockCode"));

        if (next.equals("nothing left")) {
            log.info("nothing left");
            return;
        }

        List<StockData> stockDataList = stockInfo.getStockInfo(next, stockDataDate, false);
        log.info("getStockData : {" + next + " ," + stockDataDate + " ," + stockDataList + " }");
    }

    @Scheduled(fixedRate = 1000 * 60)
    public void getFinancialData() throws Exception {
        financialDate = dateProvider.getPreSeasonDate(financialDate);
        String next = getNextFinancialCompany(financialDate.contains("changeNewStockCode"));

        if (next.equals("nothing left")) {
            log.info("nothing left");
            return;
        }

        FinancialSheet financialSheet = financialInfo.getFinancial(next, financialDate.split(":")[0], financialDate.split(":")[1]);
        log.info("getFinancialData args = " + next + " : " + financialDate + " : " + financialSheet);
    }

    private String getNextStockDataCompany(boolean nextStock) {
        List<String> remainStockDataList = taskConfigRepo.findById("remainStockDataList").orElse(new TaskConfig()).getConfigValue();
        if (null == remainStockDataList) {
            remainStockDataList = new ArrayList<>(taskConfigRepo.findById("stockCodes").get().getConfigValue());
            taskConfigRepo.save(new TaskConfig("remainStockDataList", remainStockDataList));
        }

        if (remainStockDataList.isEmpty()) {
            return "nothing left";
        }

        String next;
        if (nextStock) {
            next = remainStockDataList.get(1);
            this.stockDataDate = "20221201";

            remainStockDataList.remove(0);
            updateConfigDataList("remainStockDataList", remainStockDataList);
        } else {
            next = remainStockDataList.get(0);
        }

        return next;
    }

    private String getNextFinancialCompany(boolean nextStock) {
        List<String> remainFinancialList = taskConfigRepo.findById("remainFinancialList").orElse(new TaskConfig()).getConfigValue();
        if (null == remainFinancialList) {
            remainFinancialList = new ArrayList<>(taskConfigRepo.findById("stockCodes_financial").get().getConfigValue());
            taskConfigRepo.save(new TaskConfig("remainFinancialList", remainFinancialList));
        }

        if (remainFinancialList.isEmpty()) {
            return "nothing left";
        }

        String next;

        if (nextStock) {
            next = remainFinancialList.get(1);
            this.financialDate = "2022:3";

            remainFinancialList.remove(0);
            updateConfigDataList("remainFinancialList", remainFinancialList);
        } else {
            next = remainFinancialList.get(0);
        }


        if (financialDate.contains("changeNewStockCode")) {
            this.financialDate = "2022:3";
            next = remainFinancialList.get(1);
            remainFinancialList.remove(0);
            if (taskConfigRepo.findById("remainFinancialList").isPresent()) {
                taskConfigRepo.deleteById("remainFinancialList");
            }
            taskConfigRepo.save(new TaskConfig("remainFinancialList", remainFinancialList));
        }

        return next;
    }

    private void updateConfigDataList(String key, List<String> dataList) {
        if (taskConfigRepo.findById(key).isPresent()) {
            taskConfigRepo.deleteById(key);
        }
        taskConfigRepo.save(new TaskConfig(key, dataList));
    }

}
