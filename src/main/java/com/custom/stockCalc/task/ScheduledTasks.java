package com.custom.stockCalc.task;

import com.custom.stockCalc.model.StockData;
import com.custom.stockCalc.model.config.TaskConfig;
import com.custom.stockCalc.model.financial.FinancialSheet;
import com.custom.stockCalc.provider.DateProvider;
import com.custom.stockCalc.provider.WebProvider;
import com.custom.stockCalc.repo.TaskConfigRepo;
import com.custom.stockCalc.service.StockInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduledTasks {

    private final Log log = LogFactory.getLog(ScheduledTasks.class);
    private final String TSE_URL = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=2";
    private final String OTC_URL = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=4";
    private String[] COMPANY_URL = new String[]{TSE_URL, OTC_URL};
    private WebProvider webProvider = new WebProvider();
    private DateProvider dateProvider = new DateProvider();

    private String stockDataDate = "20221101";
    private String financialDate = "2022:3";

    @Autowired
    private TaskConfigRepo taskConfigRepo;
    @Autowired
    private StockInfo stockInfo;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    public void refreshCompanyArray() throws Exception {
        List<String> refreshCompanyList = new ArrayList<>();
        List<String> refreshStockCodes = new ArrayList<>();
        for (String url : COMPANY_URL) {
            String html = webProvider.getUrlToObject(url, String.class);
            Elements elements = Jsoup.parse(html).select("tr").select("td:first-child");
            for (Element element : elements) {
                refreshCompanyList.add(element.text().trim());
                refreshStockCodes.add(element.text().split("　")[0].trim());
            }
        }
        if (taskConfigRepo.findById("companyList").isPresent()) {
            boolean isSame = taskConfigRepo.findById("companyList").get().getConfigValue().equals(refreshCompanyList);
            if (isSame) {
                return;
            }
            taskConfigRepo.deleteById("companyList");
        }
        taskConfigRepo.save(new TaskConfig("companyList", refreshCompanyList));
        if (taskConfigRepo.findById("stockCodes").isPresent()) {
            taskConfigRepo.deleteById("stockCodes");
        }
        taskConfigRepo.save(new TaskConfig("stockCodes", refreshStockCodes));
    }

    @Scheduled(fixedRate = 1000 * 60 * 6)
    public void getStockData() throws Exception {
        stockDataDate = dateProvider.getPreMonthDate(stockDataDate);
        String next = getNextStockDataCompany(stockDataDate);
        if (stockDataDate.contains("stockDataDate")) {
            stockDataDate = stockDataDate.split(":")[1];
        }

        if (next.equals("nothing left")) {
            System.out.println("nothing left");
            return;
        }
        List<StockData> stockDataList = stockInfo.getStockInfo(next, stockDataDate, false);
        if (stockDataList == null || stockDataList.isEmpty()) {
            next = getNextStockDataCompany("changeNewStockCode");
            stockDataList = stockInfo.getStockInfo(next, stockDataDate, false);
        }
        log.info("getStockData : {" + next + " ," + stockDataDate + " ," + stockDataList + " }");
    }

    @Scheduled(fixedRate = 1000 * 60 * 6)
    public void getFinancialData() throws Exception {
        financialDate = dateProvider.getPreSeasonDate(financialDate);
        String next = getNextFinancialCompany(financialDate);

        if (next.equals("nothing left")) {
            System.out.println("nothing left");
            return;
        }

        FinancialSheet financialSheet = stockInfo.getFinancial(next, financialDate.split(":")[0], financialDate.split(":")[1]);
        log.info("getFinancialData args = " + next + " : " + financialDate + " : " + financialSheet);
    }


    private String getNextStockDataCompany(String stockDataDate) {
        List<String> remainStockDataList= getRemainStockDataCompany();
        if (remainStockDataList.isEmpty()) {
            return "nothing left";
        }
        String next = remainStockDataList.get(0);
        if (stockDataDate.contains("changeNewStockCode")) {
            this.stockDataDate = "20221201";
            next= remainStockDataList.get(1);
            remainStockDataList.remove(0);
            if (taskConfigRepo.findById("remainStockDataList").isPresent()) {
                taskConfigRepo.deleteById("remainStockDataList");
            }
            taskConfigRepo.save(new TaskConfig("remainStockDataList", remainStockDataList));
        }

        return next;
    }

    private List<String> getRemainStockDataCompany() {
        List<String> remainStockDataList = taskConfigRepo.findById("remainStockDataList").orElse(new TaskConfig()).getConfigValue();
        if (null == remainStockDataList) {
            remainStockDataList = new ArrayList<>(taskConfigRepo.findById("stockCodes").get().getConfigValue());
            taskConfigRepo.save(new TaskConfig("remainStockDataList", remainStockDataList));
        }
        return remainStockDataList;
    }

    private String getNextFinancialCompany(String financialDate) {
        List<String> remainFinancialList = getRemainFinancialCompany();
        if (remainFinancialList.isEmpty()) {
            return "nothing left";
        }

        String next = remainFinancialList.get(0);
        if (financialDate.contains("changeNewStockCode")) {
            this.financialDate = "2022:4";
            next= remainFinancialList.get(1);
            remainFinancialList.remove(0);
            if (taskConfigRepo.findById("remainFinancialList").isPresent()) {
                taskConfigRepo.deleteById("remainFinancialList");
            }
            taskConfigRepo.save(new TaskConfig("remainFinancialList", remainFinancialList));
        }

        return next;
    }

    private List<String> getRemainFinancialCompany() {
        List<String> remainFinancialList = taskConfigRepo.findById("remainFinancialList").orElse(new TaskConfig()).getConfigValue();
        if (null == remainFinancialList) {
            remainFinancialList = new ArrayList<>(taskConfigRepo.findById("stockCodes").get().getConfigValue());
            taskConfigRepo.save(new TaskConfig("remainFinancialList", remainFinancialList));
        }
        return remainFinancialList;
    }

}
