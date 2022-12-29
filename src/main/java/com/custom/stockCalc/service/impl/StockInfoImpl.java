package com.custom.stockCalc.service.impl;

import com.custom.stockCalc.model.*;
import com.custom.stockCalc.model.financial.FinancialOriginal;
import com.custom.stockCalc.model.financial.FinancialSheet;
import com.custom.stockCalc.provider.DateProvider;
import com.custom.stockCalc.provider.WebProvider;
import com.custom.stockCalc.repo.FinancialOriginalRepo;
import com.custom.stockCalc.repo.FinancialSheetRepo;
import com.custom.stockCalc.repo.HistoryStockDataRepo;
import com.custom.stockCalc.repo.TempStockDataRepo;
import com.custom.stockCalc.service.StockInfo;
import com.custom.stockCalc.task.ScheduledTasks;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockInfoImpl implements StockInfo {
    @Autowired
    private HistoryStockDataRepo historyStockDataRepo;
    @Autowired
    private TempStockDataRepo tempStockDataRepo;
    @Autowired
    private FinancialSheetRepo financialSheetRepo;
    @Autowired
    private FinancialOriginalRepo financialOriginalRepo;

    private WebProvider webProvider = new WebProvider();
    private DateProvider dateProvider = new DateProvider();
    // need 西元年月日 & stockCode
    String STOCK_INFO_URL = "https://www.twse.com.tw/en/exchangeReport/STOCK_DAY?response=json&date=%s&stockNo=%s";
    // need stockCode
    String IMMEDIATE_STOCK_URL = "https://mis.twse.com.tw/stock/api/getStockInfo.jsp?ex_ch=tse_%s.tw";
    // need stockCode
    String IMMEDIATE_STOCK_URL_ODD = "https://mis.twse.com.tw/stock/api/getOddInfo.jsp?ex_ch=%s.tw";
    // otc or tse & need stockCode for IMMEDIATE_STOCK
    String companyCategory = "%s_%s";
    // need stockCode 西元年月日 season
    String FINANCIAL_URL = "https://mops.twse.com.tw/server-java/t164sb01?step=1&CO_ID=%s&SYEAR=%s&SSEASON=%s&REPORT_ID=C#BalanceSheet";

    @Override
    public List<StockData> getStockData(String code, String beginDate, String endDate) throws Exception {
        if (!stockCodeIsValid(code)) {
            return null;
        }

        List<StockData> stockDataList = new ArrayList<>();

        DateTimeFormatter formatter;
        if (beginDate.contains("/")) {
            formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        }
        LocalDate begin = LocalDate.parse(beginDate, formatter).withDayOfMonth(1);
        LocalDate end = LocalDate.parse(endDate, formatter).withDayOfMonth(1).plusMonths(1);
        for (LocalDate date = begin; date.isBefore(end); date = date.plusMonths(1)) {
            String dateFormat = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            boolean isThisMonth = dateProvider.isThisMonth(dateFormat);
            stockDataList.addAll(getStockInfo(code, dateFormat, isThisMonth));
        }
        return stockDataList;
    }

    @Override
    public Map<String, StockImmediateInfo> getImmediateStock(String code) throws Exception {
        if (!stockCodeIsValid(code)) {
            return null;
        }

        Map<String, StockImmediateInfo> immediateInfoMap = new LinkedHashMap<>();
        StockImmediateInfo stockImmediateInfo;
        StockImmediateInfo stockImmediateInfoOdd;

        try {
            String url = String.format(IMMEDIATE_STOCK_URL, String.format(companyCategory, "tse", code));
            Document document = webProvider.getHtmlDoc(url, true);
            JsonObject jsonObject = new Gson().fromJson(document.body().text(), JsonObject.class);
            stockImmediateInfo = new StockImmediateInfo(new Gson().fromJson(jsonObject.getAsJsonArray("msgArray").get(0), JsonObject.class));
        } catch (Exception e) {
            String url = String.format(IMMEDIATE_STOCK_URL, String.format(companyCategory, "otc", code));
            Document document = webProvider.getHtmlDoc(url, true);
            JsonObject jsonObject = new Gson().fromJson(document.body().text(), JsonObject.class);
            stockImmediateInfo = new StockImmediateInfo(new Gson().fromJson(jsonObject.getAsJsonArray("msgArray").get(0), JsonObject.class));
        }

        try {
            String oddUrl = String.format(IMMEDIATE_STOCK_URL_ODD, String.format(companyCategory, "tse", code));
            Document document = webProvider.getHtmlDoc(oddUrl, true);
            JsonObject jsonObject = new Gson().fromJson(document.body().text(), JsonObject.class);
            stockImmediateInfoOdd = new StockImmediateInfo(new Gson().fromJson(jsonObject.getAsJsonArray("msgArray").get(0), JsonObject.class));
        } catch (Exception e) {
            String oddUrl = String.format(IMMEDIATE_STOCK_URL_ODD, String.format(companyCategory, "otc", code));
            Document document = webProvider.getHtmlDoc(oddUrl, true);
            JsonObject jsonObject = new Gson().fromJson(document.body().text(), JsonObject.class);
            stockImmediateInfoOdd = new StockImmediateInfo(new Gson().fromJson(jsonObject.getAsJsonArray("msgArray").get(0), JsonObject.class));
        }

        immediateInfoMap.put("stockImmediateInfo", stockImmediateInfo);
        immediateInfoMap.put("stockImmediateInfoOdd", stockImmediateInfoOdd);

        return immediateInfoMap;
    }

    @Override
    public List<String> getCodeNmList(String key) throws Exception {
        return ScheduledTasks.getCompanyList().stream().filter(s -> s.contains(key)).collect(Collectors.toList());
    }

    @Override
    public FinancialSheet getFinancial(String code, String year, String season) throws Exception {
        String financialSheetId = code + ":" + year + ":" + season;
        return financialSheetRepo.findById(financialSheetId).orElse(getFromUrl(code, year, season));
    }

    private FinancialSheet getFromUrl(String code, String year, String season) throws Exception {
        String financialSheetId = code + ":" + year + ":" + season;
        FinancialSheet financialSheet = new FinancialSheet();
        FinancialOriginal financialOriginal = new FinancialOriginal();

        financialSheet.setFinancialSheetId(financialSheetId);
        financialOriginal.setFinancialSheetId(financialSheetId);

        String url = String.format(FINANCIAL_URL, code, year, season);
        Elements elements = webProvider.getHtmlDoc(url, false).select(".rptidx").next().select("tbody");
        financialOriginal.setOriginalData(elements.toString());
        financialOriginalRepo.save(financialOriginal);
        for (Element element:
                elements) {

            Element stop = element.select("tr th").first();
            if (stop == null || stop.text().contains("當期權益變動表")) {
                break;
            }

            JsonObject jsonObject = new JsonObject();
            List<String> props = new ArrayList<>();
            int preValue = -1;

            Elements dataElements = element.select("tr");
            String sheetName = "";
            for (Element dataElement:
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

            if (sheetName.equals("資產負債表")) {
                financialSheet.setBalanceSheet(new Gson().toJson(jsonObject));
            }
            if (sheetName.equals("綜合損益表")) {
                financialSheet.setComprehensiveIncome(new Gson().toJson(jsonObject));
            }
            if (sheetName.equals("現金流量表")) {
                financialSheet.setCashFlows(new Gson().toJson(jsonObject));
            }

        }
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

    public static void main(String[] args) throws Exception {
        new StockInfoImpl().getFinancial("2303", "2022", "3");

    }

    private boolean stockCodeIsValid (String stockCode) {
        return ScheduledTasks.getStockCodes().contains(stockCode);
    }

    private List<StockData> getStockInfo(String code, String dateStr, boolean isThisMonth) throws Exception {
        String yearMonthCode = code + ":" + dateProvider.getYearMonth(dateStr);
        List<StockData> stockDataList = isThisMonth?
                tempStockDataRepo.findByYearMonthCode(yearMonthCode):
                historyStockDataRepo.findByYearMonthCode(yearMonthCode);

        if (!stockDataList.isEmpty()) {
            if (isThisMonth) {
                LocalDate updateDate = stockDataList.get(0).getUpdateDate();
                if (dateProvider.isUpdateDateToday(updateDate)) {
                    return stockDataList;
                } else {
                    return getStockInfoFromUrl(code, dateStr, true);
                }
            }
            return stockDataList;
        }

        return getStockInfoFromUrl(code, dateStr, isThisMonth);
    }

    private List<StockData> getStockInfoFromUrl(String code, String dateStr, boolean isThisMonth) throws Exception {
        String url = String.format(STOCK_INFO_URL, dateStr, code);
        StockBasicInfo stockBasicInfo = webProvider.getUrlToObject(url, StockBasicInfo.class);

        List<? extends StockData> stockDataListFromUrl = translateJsonData(stockBasicInfo.getData(), code, isThisMonth);
        if (isThisMonth) {
            tempStockDataRepo.saveAll((List<TempStockData>)stockDataListFromUrl);
        } else {
            historyStockDataRepo.saveAll((List<HistoryStockData>)stockDataListFromUrl);
        }
        return (List<StockData>) stockDataListFromUrl;
    }

    private List<? extends StockData> translateJsonData(String[][] data, String code, boolean isThisMonth) {
        List<StockData> stockDataList = new ArrayList<>();
        for (String[] dataInfo : data) {
            StockData stockData = translateStockData(dataInfo, code, isThisMonth);
            stockDataList.add(stockData);
        }
        return stockDataList;
    }

    private StockData translateStockData(String[] dataInfo, String code, boolean isThisMonth) {
        StockData stockData;
        if (isThisMonth) {
            stockData = new TempStockData();
        } else {
            stockData = new HistoryStockData();
        }
        stockData.setCodeDate(code + ":" + dataInfo[0].replace("/", "-"));
        stockData.setTradeVolume(dataInfo[1]);
        stockData.setTradeValue(dataInfo[2]);
        stockData.setOpeningPrice(dataInfo[3]);
        stockData.setHighestPrice(dataInfo[4]);
        stockData.setLowestPrice(dataInfo[5]);
        stockData.setClosingPrice(dataInfo[6]);
        stockData.setChange(dataInfo[7]);
        stockData.setTransaction(dataInfo[8]);
        stockData.setYearMonthCode(code + ":" + dataInfo[0].substring(0, 7).replace("/", "-"));
        stockData.setUpdateDate(LocalDate.now());
        return stockData;
    }
}
