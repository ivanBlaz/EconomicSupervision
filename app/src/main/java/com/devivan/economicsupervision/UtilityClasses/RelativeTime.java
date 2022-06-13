package com.devivan.economicsupervision.UtilityClasses;

import android.app.Application;
import android.content.Context;

import com.devivan.economicsupervision.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RelativeTime extends Application {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return ctx.getString(R.string.A_moment_ago);
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return ctx.getString(R.string.A_moment_ago);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return ctx.getString(R.string.A_minute_ago);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return ctx.getString(R.string.X_minutes_ago).replace("X", String.valueOf(diff / MINUTE_MILLIS));
        } else if (diff < 90 * MINUTE_MILLIS) {
            return ctx.getString(R.string.An_hour_ago);
        } else if (diff < 24 * HOUR_MILLIS) {
            return ctx.getString(R.string.X_hours_ago).replace("X", String.valueOf(diff / HOUR_MILLIS));
        } else if (diff < 48 * HOUR_MILLIS) {
            return ctx.getString(R.string.Yesterday);
        } else {
            return ctx.getString(R.string.X_days_ago).replace("X", String.valueOf(diff / DAY_MILLIS));
        }
    }


    public static String timeFormatAMPM(long time, Context ctx) {

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");


        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            String dateString = formatter.format(new Date(time));
            return dateString;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < 24 * HOUR_MILLIS) {
            String dateString = formatter.format(new Date(time));
            return dateString;
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Ayer";
        } else {
            return "Hace " + diff / DAY_MILLIS + " dias";
        }

    }

}