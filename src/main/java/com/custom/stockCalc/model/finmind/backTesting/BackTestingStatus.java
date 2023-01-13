package com.custom.stockCalc.model.finmind.backTesting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BackTestingStatus implements Serializable {
    @JsonProperty(value = "msg")
    private String msg;
    @JsonProperty(value = "status")
    private String status;
    @JsonProperty(value = "data")
    private BackTesting data;
}
