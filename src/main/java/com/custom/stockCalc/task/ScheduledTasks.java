package com.custom.stockCalc.task;

import com.custom.stockCalc.model.config.TaskConfig;
import com.custom.stockCalc.provider.WebProvider;
import com.custom.stockCalc.repo.TaskConfigRepo;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ScheduledTasks {
    private final String TSE_URL = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=2";
    private final String OTC_URL = "https://isin.twse.com.tw/isin/C_public.jsp?strMode=4";
    private String[] COMPANY_URL = new String[]{TSE_URL, OTC_URL};
    private WebProvider webProvider = new WebProvider();

    @Autowired
    private TaskConfigRepo taskConfigRepo;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    public void refreshCompanyArray() throws Exception {
        try {
            List<String> refreshCompanyList = new ArrayList<>();
            List<String> refreshStockCodes = new ArrayList<>();
            for (String url : COMPANY_URL) {
                Elements elements = webProvider.getHtmlDoc(url, false).select("tr").select("td:first-child");
                for (Element element : elements) {
                    refreshCompanyList.add(element.text().trim());
                    refreshStockCodes.add(element.text().split("　")[0].trim());
                }
            }
            taskConfigRepo.save(new TaskConfig("companyList", refreshCompanyList));
            taskConfigRepo.save(new TaskConfig("stockCodes", refreshStockCodes));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
