package com.custom.stockCalc.provider;


import com.custom.stockCalc.model.config.TaskKey;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateProvider {

    public boolean isThisMonth(String dateStr) {
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

    public String getPreMonthDate(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        date = date.plusMonths(-1);
        if (date.getYear() == 2009) {
            return TaskKey.changeNewStockCode.toString();
        }
        return date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public String getPreSeasonDate(String yearSeason) {
        String[] array = yearSeason.split(":");
        String year = array[0];
        String season = array[1];
        int preYear = Integer.parseInt(year) - 1;
        if (preYear == 2018) {
            return TaskKey.changeNewStockCode.toString();
        }
        switch (season) {
            case "1":
                return preYear + ":4";
            case "2":
                return year + ":1";
            case "3":
                return year + ":2";
            case "4":
                return year + ":3";
            default:
                return "";
        }
    }

    public String parseDate(String date, String targetPattern) {
        DateTimeFormatter formatter;
        if (date.contains("/")) {
            formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        } else if (date.contains("-")) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        }


        LocalDate localDate = LocalDate.parse(date, formatter);
        return localDate.format(DateTimeFormatter.ofPattern(targetPattern));

    }

    public LocalDate parseDate(String date) {
        DateTimeFormatter formatter;
        if (date.contains("/")) {
            formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        } else if (date.contains("-")) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        } else {
            formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        }

        return LocalDate.parse(date, formatter);
    }

}
