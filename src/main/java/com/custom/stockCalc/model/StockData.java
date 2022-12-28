package com.custom.stockCalc.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDate;

@ToString
@NoArgsConstructor
@MappedSuperclass
@Getter
@Setter
public class StockData implements Serializable {
    @Id
    private String codeDate;
    private String tradeVolume;
    private String tradeValue;
    private String openingPrice;
    private String highestPrice;
    private String lowestPrice;
    private String closingPrice;
    @Column(name = "change2")
    private String change;
    @Column(name = "transaction2")
    private String transaction;
    private String yearMonthCode;
    private LocalDate updateDate;

}