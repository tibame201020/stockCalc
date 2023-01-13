package com.custom.stockCalc.model.finmind.backTesting;

import lombok.*;

import java.io.Serializable;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BackTestingParam implements Serializable {
    private String strategyName;
    private String stockCode;
    private long userFunds;
    private String beginDate;
    private String endDate;
}
