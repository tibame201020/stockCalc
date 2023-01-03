package com.custom.stockCalc.service;

import com.custom.stockCalc.model.financial.FinancialSheet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public interface FinancialInfo {
    Log log = LogFactory.getLog(FinancialInfo.class);
    FinancialSheet getFinancial(String code, String year, String season) throws Exception;
}
