package com.worldchip.advertising.client.utils;


/**
 * Created by RYX on 2016/4/27.
 */
public class ChangeHourOrMinUtil {
    static int mHour;
    static int mMin;
    public static int getHour(int keyCode, int hour) {
        mHour = hour;
        if (keyCode == 19) {
            mHour++;
            if (mHour > 23) {
                mHour = 0;
            }
        }

        if (keyCode == 20) {
            mHour--;
            if (mHour < 0) {
                mHour = 23;
            }
        }

        return mHour;
    }

    public static int getMin(int keyCode, int min) {
        mMin = min;
        if (keyCode == 19) {
            mMin++;
            if (mMin > 59) {
                mMin = 0;
            }
        }

        if (keyCode == 20) {
            mMin--;
            if(mMin < 0) {
                mMin = 59;
            }
        }

        return mMin;
    }

}
