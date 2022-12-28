package com.custom.stockCalc.model;

import lombok.*;

import java.io.Serializable;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CodeParam implements Serializable {
    private String code;
    private String beginDate;
    private String endDate;
}
