package com.custom.stockCalc.provider;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateProvider {

    public boolean isThisMonth(String dateStr){
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd")).withDayOfMonth(1);
        return LocalDate.now().withDayOfMonth(1).isEqual(date);
    }

    public boolean isUpdateDateToday(LocalDate updateDate) {
        return LocalDate.now().isEqual(updateDate);
    }

    public String getYearMonth(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

}
