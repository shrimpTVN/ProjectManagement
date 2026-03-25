package com.app.src.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgoHelper {
    public static String toTimeAgo(Date date) {
        if (date == null) return "";

        long now = new Date().getTime();
        long past = date.getTime();
        long diffInMs = now - past;

        // Guard: future timestamps or too small difference
        if (diffInMs < 0 || TimeUnit.MILLISECONDS.toSeconds(diffInMs) < 60) {
            return "Just now";
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMs);
        if (minutes < 60) return minutes + " minutes ago";

        long hours = TimeUnit.MILLISECONDS.toHours(diffInMs);
        if (hours < 24) return hours + " hours ago";

        long days = TimeUnit.MILLISECONDS.toDays(diffInMs);
        if (days < 30) return days + " days ago";

        long months = days / 30;
        return months + " months ago";
    }
}