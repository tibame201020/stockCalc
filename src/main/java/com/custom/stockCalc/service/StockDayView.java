package com.custom.stockCalc.service;

import com.custom.stockCalc.model.stockDayView.Bwibbu;
import com.custom.stockCalc.model.stockDayView.CompanyFinancialReport;
import com.custom.stockCalc.model.stockDayView.CompanySummaryReport;
import com.custom.stockCalc.model.stockDayView.StockDayAvg;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public interface StockDayView {

    Log log = LogFactory.getLog(StockDayView.class);

    /**
     * 取得上市公司營益分析查詢彙總表(全體公司彙總報表)
     *
     * @return List<CompanySummaryReport>
     * @throws Exception
     */
    List<CompanySummaryReport> getCompanySummaryReport() throws Exception;

    /**
     * 取得當季上市公司報表
     *
     * @return List<CompanyFinancialReport>
     * @throws Exception
     */
    List<CompanyFinancialReport> getCompanyFinancialReport() throws Exception;

    /**
     * 取得昨收與月均價格
     *
     * @return List<StockDayAvg>
     * @throws Exception
     */
    List<StockDayAvg> getStockDayAvg() throws Exception;

    /**
     * 取得上市個股日本益比、殖利率及股價淨值比
     *
     * @return List<Bwibbu>
     * @throws Exception
     */
    List<Bwibbu> getBwibbu() throws Exception;
}
