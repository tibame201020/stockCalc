package com.custom.stockCalc.model.financial;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.math.BigDecimal;

//資產負債表
@ToString
@Setter
@Getter
@Entity
public class BalanceSheet {
    @Id
    @Lob
    private String originData;
    private Asset asset;

}

class Asset {
    //現金及約當現金
    private BigDecimal cashQquivalents;
}
