package com.app.src.utils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeAgoHelper {
    public static String toTimeAgo(Date pastDate) {
        if (pastDate == null) return "Không rõ";
        long duration = new Date().getTime() - pastDate.getTime();
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);
        long diffInHours = TimeUnit.MILLISECONDS.toHours(duration);

        if (diffInMinutes < 1) return "Vừa xong";
        if (diffInMinutes < 60) return diffInMinutes + " phút trước";
        if (diffInHours < 24) return diffInHours + " giờ trước";
        return new java.text.SimpleDateFormat("dd/MM/yyyy").format(pastDate);
    }
}