package com.custom.stockCalc.task;

import com.custom.stockCalc.provider.WebProvider;
import lombok.Getter;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
public class ScheduledTasks {
    private final String TSE_URL = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=2";
    private final String OTC_URL = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=4";
    private String [] COMPANY_URL = new String[] { TSE_URL, OTC_URL };

    private static List<String> companyList;

    private static List<String> stockCodes;

    private WebProvider webProvider = new WebProvider();

    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    public void refreshCompanyArray() throws Exception {
        try {
            List<String> refreshCompanyList = new ArrayList<>();
            List<String> refreshStockCodes = new ArrayList<>();
            for (String url:
                    COMPANY_URL) {
                Elements elements = webProvider.getHtmlDoc(TSE_URL, false).select("tr").select("td:first-child");
                for (Element element:
                        elements) {
                    refreshCompanyList.add(element.text().trim());
                    refreshStockCodes.add(element.text().split(" ")[0].trim());
                }
            }
            companyList = refreshCompanyList;
            stockCodes = refreshStockCodes;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getCompanyList() {
        return companyList;
    }

    public static List<String> getStockCodes() {
        return stockCodes;
    }

}
