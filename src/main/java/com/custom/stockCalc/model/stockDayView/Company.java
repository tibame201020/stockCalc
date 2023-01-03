package com.custom.stockCalc.model.stockDayView;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
@Setter
//https://openapi.twse.com.tw/v1/exchangeReport/TWTB4U
public class Company {
    @JsonProperty(value = "Code")
    private String code;
    @JsonProperty(value = "Name")
    private String name;
}
