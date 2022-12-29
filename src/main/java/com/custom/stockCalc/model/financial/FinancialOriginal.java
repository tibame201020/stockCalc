package com.custom.stockCalc.model.financial;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jsoup.select.Elements;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import java.io.Serializable;

@ToString
@Setter
@Getter
@Entity
public class FinancialOriginal implements Serializable {
    @Id
    private String financialSheetId;
    @Lob
    private String originalData;

    private String dataUrl;


}
