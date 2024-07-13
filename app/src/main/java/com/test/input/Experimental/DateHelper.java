package com.test.input.Experimental;

import android.graphics.Color;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateHelper {
    public static String convertToRelativeDate(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH.mm.ss", Locale.US);
        try {
            Date date = sdf.parse(dateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            Calendar today = Calendar.getInstance();
            long diffInMillis = today.getTimeInMillis() - calendar.getTimeInMillis();
            long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);

            if (diffInDays == 0) {
                long diffInHours = diffInMillis / (1000 * 60 * 60);
                if (diffInHours > 0) {
                    return diffInHours + " jam yang lalu";
                } else {
                    return "Baru saja";
                }
            } else if (diffInDays == 1) {
                return "Kemarin";
            } else if (diffInDays < 7) {
                return diffInDays + " hari yang lalu";
            } else if (diffInDays < 30) {
                long diffInWeeks = diffInDays / 7;
                if (diffInWeeks == 1) {
                    return "1 minggu yang lalu";
                } else {
                    return diffInWeeks + " minggu yang lalu";
                }
            } else if (diffInDays < 365) {
                long diffInMonths = diffInDays / 30;
                if (diffInMonths == 1) {
                    return "1 bulan yang lalu";
                } else {
                    return diffInMonths + " bulan yang lalu";
                }
            } else {
                return sdf.format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString;
        }
    }
}
