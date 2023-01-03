package com.custom.stockCalc.service.impl;

import com.custom.stockCalc.model.*;
import com.custom.stockCalc.provider.DateProvider;
import com.custom.stockCalc.provider.WebProvider;
import com.custom.stockCalc.repo.HistoryStockDataRepo;
import com.custom.stockCalc.repo.TaskConfigRepo;
import com.custom.stockCalc.repo.TempStockDataRepo;
import com.custom.stockCalc.service.StockInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockInfoImpl implements StockInfo {
    // need 西元年月日 & stockCode
    String STOCK_INFO_URL = "https://www.twse.com.tw/en/exchangeReport/STOCK_DAY?response=json&date=%s&stockNo=%s";
    // need stockCode
    String IMMEDIATE_STOCK_URL = "https://mis.twse.com.tw/stock/api/getStockInfo.jsp?ex_ch=%s.tw";
    // need stockCode
    String IMMEDIATE_STOCK_URL_ODD = "https://mis.twse.com.tw/stock/api/getOddInfo.jsp?ex_ch=%s.tw";
    // otc or tse & need stockCode for IMMEDIATE_STOCK
    String companyCategory = "%s_%s";

    @Autowired
    private HistoryStockDataRepo historyStockDataRepo;
    @Autowired
    private TempStockDataRepo tempStockDataRepo;
    @Autowired
    private TaskConfigRepo taskConfigRepo;
    private WebProvider webProvider = new WebProvider();
    private DateProvider dateProvider = new DateProvider();

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
        List<String> companyList = taskConfigRepo.getReferenceById("companyList").getConfigValue();
        return companyList.stream().filter(s -> s.contains(key)).collect(Collectors.toList());
    }

    private boolean stockCodeIsValid(String stockCode) {
        List<String> stockCodes = taskConfigRepo.getReferenceById("stockCodes").getConfigValue();
        return stockCodes.contains(stockCode);
    }

    @Override
    public List<StockData> getStockInfo(String code, String dateStr, boolean isThisMonth) throws Exception {
        String yearMonthCode = code + ":" + dateProvider.getYearMonth(dateStr);
        List<StockData> stockDataList = isThisMonth ?
                tempStockDataRepo.findByYearMonthCode(yearMonthCode) :
                historyStockDataRepo.findByYearMonthCode(yearMonthCode);

        if (stockDataList != null && !stockDataList.isEmpty()) {
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

    public List<StockData> getStockInfoFromUrl(String code, String dateStr, boolean isThisMonth) throws Exception {
        String url = String.format(STOCK_INFO_URL, dateStr, code);
        StockBasicInfo stockBasicInfo = webProvider.getUrlToObject(url, StockBasicInfo.class);

        List<? extends StockData> stockDataListFromUrl = translateJsonData(stockBasicInfo.getData(), code, isThisMonth);
        if (stockDataListFromUrl == null || stockDataListFromUrl.isEmpty()) {
            return null;
        }
        if (isThisMonth) {
            tempStockDataRepo.saveAll((List<TempStockData>) stockDataListFromUrl);
        } else {
            historyStockDataRepo.saveAll((List<HistoryStockData>) stockDataListFromUrl);
        }
        return (List<StockData>) stockDataListFromUrl;
    }

    private List<? extends StockData> translateJsonData(String[][] data, String code, boolean isThisMonth) {
        try {
            List<StockData> stockDataList = new ArrayList<>();
            for (String[] dataInfo : data) {
                StockData stockData = translateStockData(dataInfo, code, isThisMonth);
                stockDataList.add(stockData);
            }
            return stockDataList;
        } catch (Exception e) {
            return null;
        }
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
