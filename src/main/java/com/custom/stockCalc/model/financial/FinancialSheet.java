package com.custom.stockCalc.model.financial;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 財報完整資料
 */
@ToString
@Setter
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSheet implements Serializable {
    @Id
    private String financialSheetId;
    @Lob
    @ElementCollection
    @OrderColumn
    private String[] sheets;

}
