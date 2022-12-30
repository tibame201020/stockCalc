package com.custom.stockCalc.model.config;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TaskConfig {
    @Id
    private String configName;
    @Lob
    @ElementCollection
    @OrderColumn
    private List<String> configValue;
}
