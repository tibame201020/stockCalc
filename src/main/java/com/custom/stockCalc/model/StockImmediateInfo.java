package com.custom.stockCalc.model;

import com.google.gson.JsonObject;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 即時股價資訊
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockImmediateInfo implements Serializable {
    private String code;
    private String openToday;
    private String closeYesterday;
    private String high;
    private String low;
    private String totalVolumes;
    private PriceVolume deal;

    private PriceVolume[] askToSells;
    private PriceVolume[] askToBuys;

    private String company;

    public StockImmediateInfo(JsonObject jsonObject) {
        this.code = jsonObject.get("c").getAsString();
        this.openToday = jsonObject.get("o").getAsString();
        this.closeYesterday = jsonObject.get("y").getAsString();
        this.high = jsonObject.get("h").getAsString();
        this.low = jsonObject.get("l").getAsString();
        this.totalVolumes = jsonObject.get("v").getAsString();
        this.company = jsonObject.get("nf").getAsString();
        this.deal = new PriceVolume(jsonObject.get("z").getAsString(), jsonObject.get("s").getAsString());
        this.askToSells = transPriceVolumeArray(jsonObject.get("a").getAsString(), jsonObject.get("f").getAsString());
        this.askToBuys = transPriceVolumeArray(jsonObject.get("b").getAsString(), jsonObject.get("g").getAsString());
    }

    private PriceVolume[] transPriceVolumeArray(String priceArrayStr, String volumeArrayStr) {
        String[] priceArray = priceArrayStr.split("_");
        if (CollectionUtils.isEmpty(Arrays.asList(priceArray))) {
            return null;
        }
        String[] volumeArray = volumeArrayStr.split("_");
        PriceVolume[] priceVolumes = new PriceVolume[priceArray.length];
        for (int i = 0; i < priceArray.length; i++) {
            priceVolumes[i] = new PriceVolume(priceArray[i], volumeArray[i]);
        }

        return priceVolumes;
    }

}

@ToString
@Setter
@Getter
@AllArgsConstructor
class PriceVolume implements Serializable {
    private String price;
    private String volume;
}
