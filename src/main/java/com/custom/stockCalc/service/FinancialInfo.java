package com.custom.stockCalc.service;

import com.custom.stockCalc.model.financial.FinancialSheet;
import com.custom.stockCalc.model.financial.SimpleSheet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;

public interface FinancialInfo {
    Log log = LogFactory.getLog(FinancialInfo.class);

    /**
     * 取得財報 資產負債表 綜合損益表 現金流量表
     *
     * @param code   公司代碼
     * @param year   年度
     * @param season 季
     * @return FinancialSheet
     * @throws Exception
     */
    FinancialSheet getFinancial(String code, String year, String season) throws Exception;

    /**
     * 取得簡易財報資料
     *
     * @param code   公司代碼
     * @param year   年度
     * @param season 季
     * @return BalanceSheet
     * @throws Exception
     */
    SimpleSheet getSimpleSheet(String code, String year, String season) throws Exception;

    /**
     * 取得日期range內 季度簡易財報資料
     *
     * @param code
     * @param beginDate
     * @param endDate
     * @return SimpleSheets
     * @throws Exception
     */
    List<SimpleSheet> getSheetByCodeAndDateRange(String code, String beginDate, String endDate) throws Exception;
}
