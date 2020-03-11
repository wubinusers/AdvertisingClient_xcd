package com.worldchip.advertising.client.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by RYX on 2016/4/28.
 */
public class TimeChangeUtils {

   
    public static int Hm2min(String Hm) {
        int min = 0;
        min = Integer.parseInt(Hm.substring(0, 2)) * 60 + Integer.parseInt(Hm.substring(3, 5));
        return min;
    }

    
    public static String min2Hm(int min) {
        String Hm = "";
        Hm = String.format("%02d", min / 60) + ":" + String.format("%02d", min % 60);
        return Hm;
    }

    
    public static String getTwoMinAgo(String Hm) {
        String nHM = min2Hm(Hm2min(Hm) - 2);
        return nHM;
    }

    
    public static Date getData(String Hm) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 39);
        Date time = calendar.getTime();
        return time;
    }

    
    public static String changeWeeKToNormal(int week) {
        int normalWeek = 0;
        String str = "";
        normalWeek = week - 1;
        {
            if (normalWeek == 0) {
                normalWeek = 7;
            }
        }
        str = normalWeek + "";
        return str;
    }
}
