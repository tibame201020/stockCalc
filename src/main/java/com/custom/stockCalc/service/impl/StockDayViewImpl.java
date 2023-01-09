package com.custom.stockCalc.service.impl;

import com.custom.stockCalc.model.stockDayView.Bwibbu;
import com.custom.stockCalc.model.stockDayView.CompanyFinancialReport;
import com.custom.stockCalc.model.stockDayView.CompanySummaryReport;
import com.custom.stockCalc.model.stockDayView.StockDayAvg;
import com.custom.stockCalc.provider.DateProvider;
import com.custom.stockCalc.provider.WebProvider;
import com.custom.stockCalc.repo.BwibbuRepo;
import com.custom.stockCalc.repo.CompanyFinancialReportRepo;
import com.custom.stockCalc.repo.CompanySummaryReportRepo;
import com.custom.stockCalc.repo.StockDayAvgRepo;
import com.custom.stockCalc.service.StockDayView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockDayViewImpl implements StockDayView {

    private String companySummaryReportUrl = "https://openapi.twse.com.tw/v1/opendata/t187ap17_L";

    private String companyFinancialReportUrl = "https://openapi.twse.com.tw/v1/opendata/t187ap14_L";

    private String bwibbuUrl = "https://openapi.twse.com.tw/v1/exchangeReport/BWIBBU_ALL";

    private String stockAvgUrl = "https://openapi.twse.com.tw/v1/exchangeReport/STOCK_DAY_AVG_ALL";


    @Autowired
    private CompanySummaryReportRepo companySummaryReportRepo;
    @Autowired
    private CompanyFinancialReportRepo companyFinancialReportRepo;

    @Autowired
    private StockDayAvgRepo stockDayAvgRepo;
    @Autowired
    private BwibbuRepo bwibbuRepo;

    private WebProvider webProvider = new WebProvider();
    private DateProvider dateProvider = new DateProvider();

    @Override
    public List<CompanySummaryReport> getCompanySummaryReport() throws Exception {
        List<CompanySummaryReport> companySummaryReportList = companySummaryReportRepo.findAll();

        if (companySummaryReportList.isEmpty()) {
            log.info("get companySummaryReportList from url");
            companySummaryReportList = getCompanySummaryReportFromUrl();
            return companySummaryReportRepo.saveAll(companySummaryReportList);
        }

        boolean isUpdateDateToday = dateProvider.isUpdateDateToday(companySummaryReportList.get(0).getUpdateDate());
        if (isUpdateDateToday) {
            log.info("get companySummaryReportList from db");
            return companySummaryReportList;
        }

        log.info("update companySummaryReportList from url");
        companySummaryReportList = getCompanySummaryReportFromUrl();
        return companySummaryReportRepo.saveAll(companySummaryReportList);
    }

    @Override
    public List<CompanyFinancialReport> getCompanyFinancialReport() throws Exception {
        List<CompanyFinancialReport> companyFinancialReportList = companyFinancialReportRepo.findAll();

        if (companyFinancialReportList.isEmpty()) {
            log.info("get companyFinancialReportList from url");
            companyFinancialReportList = getCompanyFinancialReportFromUrl();
            return companyFinancialReportRepo.saveAll(companyFinancialReportList);
        }

        boolean isUpdateDateToday = dateProvider.isUpdateDateToday(companyFinancialReportList.get(0).getUpdateDate());
        if (isUpdateDateToday) {
            log.info("get companyFinancialReportList from db");
            return companyFinancialReportList;
        }

        log.info("update companyFinancialReportList from url");
        companyFinancialReportList = getCompanyFinancialReportFromUrl();
        return companyFinancialReportRepo.saveAll(companyFinancialReportList);
    }

    @Override
    public List<StockDayAvg> getStockDayAvg() throws Exception {
        List<StockDayAvg> stockDayAvgList = stockDayAvgRepo.findAll();
        if (stockDayAvgList.isEmpty()) {
            log.info("get stockDayAvgList from url");
            stockDayAvgList = getStockDayAvgFromUrl();
            return stockDayAvgRepo.saveAll(stockDayAvgList);
        }
        boolean isUpdateDateToday = dateProvider.isUpdateDateToday(stockDayAvgList.get(0).getUpdateDate());
        if (isUpdateDateToday) {
            log.info("get stockDayAvgList from db");
            return stockDayAvgList;
        }

        log.info("update stockDayAvgList from url");
        stockDayAvgList = getStockDayAvgFromUrl();
        return stockDayAvgRepo.saveAll(stockDayAvgList);
    }

    @Override
    public List<Bwibbu> getBwibbu() throws Exception {
        List<Bwibbu> bwibbuList = bwibbuRepo.findAll();
        if (bwibbuList.isEmpty()) {
            log.info("get bwibbuList from url");
            bwibbuList = getBwibbuFromUrl();
            return bwibbuRepo.saveAll(bwibbuList);
        }
        boolean isUpdateDateToday = dateProvider.isUpdateDateToday(bwibbuList.get(0).getUpdateDate());
        if (isUpdateDateToday) {
            log.info("get bwibbuList from db");
            return bwibbuList;
        }

        log.info("update bwibbuList from url");
        bwibbuList = getBwibbuFromUrl();
        return bwibbuRepo.saveAll(bwibbuList);
    }

    private List<StockDayAvg> getStockDayAvgFromUrl() throws Exception {
        StockDayAvg[] stockDayAvgs = webProvider.getUrlToObject(stockAvgUrl, StockDayAvg[].class);
        return Arrays.stream(stockDayAvgs)
                .peek(stockDayAvg -> stockDayAvg.setUpdateDate(LocalDate.now()))
                .collect(Collectors.toList());
    }

    private List<Bwibbu> getBwibbuFromUrl() throws Exception {
        Bwibbu[] bwibbus = webProvider.getUrlToObject(bwibbuUrl, Bwibbu[].class);
        return Arrays.stream(bwibbus)
                .peek(bwibbu -> bwibbu.setUpdateDate(LocalDate.now()))
                .collect(Collectors.toList());
    }


    private List<CompanySummaryReport> getCompanySummaryReportFromUrl() throws Exception {
        CompanySummaryReport[] companySummaryReports = webProvider.getUrlToObject(companySummaryReportUrl, CompanySummaryReport[].class);
        return Arrays.stream(companySummaryReports)
                .peek(companySummaryReport -> companySummaryReport.setUpdateDate(LocalDate.now()))
                .collect(Collectors.toList());
    }

    private List<CompanyFinancialReport> getCompanyFinancialReportFromUrl() throws Exception {
        CompanyFinancialReport[] companySummaryReports = webProvider.getUrlToObject(companyFinancialReportUrl, CompanyFinancialReport[].class);
        return Arrays.stream(companySummaryReports)
                .peek(companySummaryReport -> companySummaryReport.setUpdateDate(LocalDate.now()))
                .collect(Collectors.toList());
    }
}
