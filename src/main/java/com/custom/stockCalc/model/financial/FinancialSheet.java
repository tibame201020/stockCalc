package com.custom.stockCalc.model.financial;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;

@ToString
@Setter
@Getter
@Entity
public class FinancialSheet implements Serializable {
    @Id
    private String financialSheetId;
    @Lob
    private String balanceSheet;//資產負債表
    @Lob
    private String comprehensiveIncome;//綜合損益表
    @Lob
    private String cashFlows; //現金流量表

}
