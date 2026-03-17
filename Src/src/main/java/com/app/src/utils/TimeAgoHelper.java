package com.app.src.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgoHelper {
    public static String toTimeAgo(Date date) {
        if (date == null) return "";

        long now = new Date().getTime();
        long past = date.getTime();
        long diffInMs = now - past;

        // Nếu thời gian ở tương lai hoặc chênh lệch quá nhỏ
        if (diffInMs < 0 || TimeUnit.MILLISECONDS.toSeconds(diffInMs) < 60) {
            return "Vừa xong";
        }

        long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMs);
        if (minutes < 60) return minutes + " phút trước";

        long hours = TimeUnit.MILLISECONDS.toHours(diffInMs);
        if (hours < 24) return hours + " giờ trước";

        long days = TimeUnit.MILLISECONDS.toDays(diffInMs);
        if (days < 30) return days + " ngày trước";

        long months = days / 30;
        return months + " tháng trước";
    }
}