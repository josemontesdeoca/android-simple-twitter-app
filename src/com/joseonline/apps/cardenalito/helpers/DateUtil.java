
package com.joseonline.apps.cardenalito.helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.text.format.DateUtils;

public class DateUtil {
    public static final String TWITTER_TIME_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

    // getRelativeTimeAgo("Mon Apr 01 21:16:23 +0000 2014");
    public static String getRelativeTimeAgo(String rawDate, String timeFormat) {
        SimpleDateFormat sf = new SimpleDateFormat(timeFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS,
                    DateUtils.FORMAT_ABBREV_RELATIVE).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static String getFullDate(Context context, String rawDate, String timeFormat) {
        SimpleDateFormat sf = new SimpleDateFormat(timeFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String fullDate = "";
        try {
            long dateMillis = sf.parse(rawDate).getTime();
            fullDate = DateUtils.formatDateTime(context, dateMillis, DateUtils.FORMAT_SHOW_TIME
                    | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fullDate;
    }
}
