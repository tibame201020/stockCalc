package com.custom.stockCalc.service.impl;

import com.custom.stockCalc.model.config.TaskConfig;
import com.custom.stockCalc.model.config.TaskKey;
import com.custom.stockCalc.model.financial.FinancialOriginal;
import com.custom.stockCalc.model.financial.FinancialSheet;
import com.custom.stockCalc.provider.WebProvider;
import com.custom.stockCalc.repo.FinancialOriginalRepo;
import com.custom.stockCalc.repo.FinancialSheetRepo;
import com.custom.stockCalc.repo.TaskConfigRepo;
import com.custom.stockCalc.service.FinancialInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FinancialInfoImpl implements FinancialInfo {

    // need stockCode 西元年月日 season
    String FINANCIAL_URL = "https://mops.twse.com.tw/server-java/t164sb01?step=1&CO_ID=%s&SYEAR=%s&SSEASON=%s&REPORT_ID=C#BalanceSheet";
    @Autowired
    private FinancialSheetRepo financialSheetRepo;
    @Autowired
    private FinancialOriginalRepo financialOriginalRepo;
    @Autowired
    private TaskConfigRepo taskConfigRepo;

    private WebProvider webProvider = new WebProvider();

    @Override
    public FinancialSheet getFinancial(String code, String year, String season) throws Exception {
        if (!stockCodeIsValid(code)) {
            return null;
        }
        return financialSheetRepo.findById(code + ":" + year + ":" + season).orElse(getFinancialFromUrl(code, year, season));
    }

    private boolean stockCodeIsValid(String stockCode) {
        List<String> stockCodes = taskConfigRepo.findById(TaskKey.stockCodes_financial.toString()).orElse(new TaskConfig()).getConfigValue();
        return stockCodes.contains(stockCode);
    }

    private FinancialSheet getFinancialFromUrl(String code, String year, String season) throws Exception {
        String financialSheetId = code + ":" + year + ":" + season;
        FinancialSheet financialSheet = new FinancialSheet();
        financialSheet.setFinancialSheetId(financialSheetId);

        String url = String.format(FINANCIAL_URL, code, year, season);
        Elements elements = webProvider.getHtmlDoc(url, false).select(".rptidx").next().select("tbody");

        financialOriginalRepo.save(new FinancialOriginal(financialSheetId, elements.toString(), url));

        List<JsonObject> sheets = new ArrayList<>();

        for (Element element : elements) {
            Element stop = element.select("tr th").first();
            if (stop == null || stop.text().contains("當期權益變動表")) {
                break;
            }

            JsonObject jsonObject = new JsonObject();
            List<String> props = new ArrayList<>();
            int preValue = -1;

            Elements dataElements = element.select("tr");
            String sheetName = "";
            for (Element dataElement :
                    dataElements) {
                Element thElement = dataElement.select("th").first();
                if (thElement != null && !thElement.text().contains("代號Code")) {
                    sheetName = thElement.firstElementChild().text();
                    jsonObject.addProperty("sheetName", sheetName);
                }
                if (thElement == null) {
                    preValue = handleData(jsonObject, dataElement, props, preValue);
                }
            }

            sheets.add(jsonObject);
        }
        financialSheet.setSheets(
                sheets.stream()
                        .map(jsonObject -> new Gson().toJson(jsonObject))
                        .toArray(String[]::new)
        );
        financialSheetRepo.save(financialSheet);

        return financialSheet;
    }

    private int handleData(JsonObject jsonObject, Element dataElement, List<String> props, int preValue) {
        Elements dataElements = dataElement.select("td");

        String code = dataElements.eq(0).text();
        String name = dataElements.eq(1).select("span").first().text();
        String value = dataElements.eq(2).text();

        JsonObject children = generateChildJsonObject(code, value);
        int elementPos = chargeElementPos(name);
        props = handleProps(props, name, preValue, elementPos);
        addProp(jsonObject, children, new ArrayList<>(props));

        return elementPos;

    }

    private void addProp(JsonObject jsonObject, JsonObject children, List<String> props) {
        if (children.get("value").getAsString().equals("")) {
            return;
        }

        String key = props.get(0);
        props.remove(0);
        if (null == jsonObject.getAsJsonObject(key)) {
            jsonObject.add(key, new JsonObject());
        }
        if (props.size() > 0) {
            addProp(jsonObject.getAsJsonObject(key), children, props);
        } else {
            jsonObject.add(key, children);
        }
    }

    private List<String> handleProps(List<String> props, String name, int preValue, int elementPos) {
        String category = name.replaceAll("　", "");

        if (preValue < elementPos) {
            List<String> temp;
            try {
                temp = props.subList(0, elementPos);
            } catch (Exception e) {
                temp = new ArrayList<>();
            }
            temp.add(category);
            return temp.subList(0, elementPos + 1);
        }

        if (preValue > elementPos) {
            List<String> temp = props.subList(0, elementPos);
            temp.add(category);
            return temp.subList(0, elementPos + 1);
        }

        props.set(elementPos, category);
        return props.subList(0, elementPos + 1);
    }

    private JsonObject generateChildJsonObject(String code, String value) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("code", code.replaceAll("　", "").replaceAll("\n", "").replaceAll("\r", "").replaceAll(" ", "").trim());
        jsonObject.addProperty("value", value.replaceAll("　", "").replaceAll("\n", "").replaceAll("\r", "").replaceAll(" ", "").trim());
        return jsonObject;
    }

    private int chargeElementPos(String chargeName) {
        int blankCnt = 0;
        for (Character c : chargeName.toCharArray()) {
            if (c.equals('　')) {
                blankCnt++;
            } else {
                break;
            }
        }
        return blankCnt;
    }
}
