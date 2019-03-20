package com.hm60.telegramcontestchart.util;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.TimeZone;

public final class Utils {
    private static DateFormatSymbols dfs = new DateFormatSymbols();
    private static String[] shortMonths = dfs.getShortMonths();
    private static Calendar calendar = Calendar.getInstance();

    public static String toShortDateString(long millis) {
        calendar.setTimeInMillis(millis);
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        return shortMonths[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.DAY_OF_MONTH);

    }
}
