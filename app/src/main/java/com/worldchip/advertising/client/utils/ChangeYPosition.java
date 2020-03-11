package com.worldchip.advertising.client.utils;

/**
 * Created by RYX on 2016/4/27.
 */
public class ChangeYPosition {
    public static int getOffSetY(int position) {
        int i = 0;

        switch (position) {
            case 0:
                i = -334;
                break;
            case 1:
                i = -260;
                break;
            case 2:
                i = -186;
                break;
            case 3:
                i = -113;
                break;
            case 4:
                i = -38;
                break;
            case 5:
                i = 36;
                break;
            case 6:
                i = 110;
                break;
            case 7:
                i = 184;
                break;
            case 8:
                i = 259;
                break;
            case 9:
                i = 333;
                break;
        }

        return i + 30;
    }
}
