package com.custom.stockCalc.model.finmind.strategySummary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * 交易策略種類
 * https://api.web.finmindtrade.com/v2/strategy_summary
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class StrategySummary implements Serializable {
    @Id
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "zh_name")
    private String zh_name;
    /**
     * 描述
     */
    @JsonProperty(value = "description")
    private String description;
    /**
     * 進出場策略
     */
    @JsonProperty(value = "formula")
    private String formula;
    @JsonProperty(value = "link")
    private String link;
    /**
     * 更新資料日期
     */
    private LocalDate updateDate;
}
