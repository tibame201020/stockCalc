package com.custom.stockCalc.model.financial;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.jsoup.Jsoup;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@ToString
@Setter
@Getter
@Entity
public class FinancialSheet implements Serializable {
    @Id
    private String financialSheetId;
    @Lob @ElementCollection @OrderColumn
    private String[] sheets;

}
