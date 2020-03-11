package com.worldchip.advertising.client.utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class AdSetting {

	public static SharedPreferences menuPref;
	private static final String TAG = "--AdSetting--";

	@SuppressLint("SimpleDateFormat")
	private static final SimpleDateFormat mFormatter = new SimpleDateFormat("HH:mm:ss");
	private static final List<String> mPointTimeList = new ArrayList<String>();
	private static final List<String> mHalfPointTimeList = new ArrayList<String>();
	private static final List<String> mFixedTimeList = new ArrayList<String>();

	public enum ADType {// 广告插播模式枚举
		POINT, INTERVAL, FIXED
	}

	// 插播是否打开
	public static boolean isAdPlayOpen(Context context) {
		menuPref = context.getSharedPreferences("ad_set", 0);
		int isAdOpen = menuPref.getInt("ad_switch_index", 0);
		return isAdOpen == 1;
	}

	// 插播类型
	public static ADType getSettingADType(Context context) {
		menuPref = context.getSharedPreferences("ad_set", 0);
		int selectAdType = menuPref.getInt("ad_mode_index", 3);
		Log.d("33333", "selectAdType2222=="+selectAdType);
		Log.e(TAG, "getSettingADType...selectAdType=" + selectAdType);

		if (selectAdType <= 1 ) {
			return ADType.POINT;
		} else if (selectAdType > 1) {
			return ADType.INTERVAL;
		}

		// 获取设置值 默认
		return ADType.POINT;

	}

	// 插播数量
	public static int getAdMaxCount(Context context) {
		menuPref = context.getSharedPreferences("ad_set", 0);
		int adMaxCount = menuPref.getInt("ad_number", 2);
		Log.e(TAG, "adMaxCount...adMaxCount=" + adMaxCount);
		return adMaxCount;
	}

	// 是否整点(半点）
	public static boolean isPointTime(Context context) {
		Date currentDate = new Date(System.currentTimeMillis());
		String strCurrentDate = mFormatter.format(currentDate);

		// SharedPreferences mSetupSp =
		// context.getSharedPreferences("advertis_setup", 0);

		menuPref = context.getSharedPreferences("ad_set", 0);
		int adHourTimeIndex = menuPref.getInt("ad_mode_index", 0);
		if (adHourTimeIndex == 0) {// 获取整点插播广告列表
			getPointTimeList();
			if (mPointTimeList.contains(strCurrentDate)) {
				Log.e(TAG, "isPointTime...is point time");
				return true;
			}
		} else {// 获取半点插播广告列表。
			getHalfPointTimeList();
			if (mHalfPointTimeList.contains(strCurrentDate)) {
				Log.e(TAG, "isPointTime...is half point time");
				return true;
			}
		}

		return false;
	}

	// 是否定点时间
	public static boolean isFixedTime() {
		Date currentDate = new Date(System.currentTimeMillis());
		String strCurrentDate = mFormatter.format(currentDate);
		if (mFixedTimeList.contains(strCurrentDate)) {
			Log.e(TAG, "isFixedTime...is fixed time:" + strCurrentDate);
			return true;
		}
		return false;

	}

	// 获取间隔时间，秒
	public static int getIntervalValue(Context context) {
		// SharedPreferences mSetupSp =
		// context.getSharedPreferences("advertis_setup", 0);

		menuPref = context.getSharedPreferences("ad_set", 0);
		int interval = menuPref.getInt("ad_mode_index", 3);
		Log.e(TAG, "getIntervalValue...interval=" + interval);
		// if (interval != 0) {
		return (interval - 1) * 60;
		// }
		// 默认30分钟
		// return 30 * 60;
	}

	// 定点时间，格式化后的字符串"00:00:00"
	public static void resetFixedTimeList(Context context) {
		mFixedTimeList.clear();

		SharedPreferences mSetupSp = context.getSharedPreferences("advertis_setup", 0);
		int whichFixedTime = mSetupSp.getInt("ad_fixed_which", 1);
		if (whichFixedTime < 1 || whichFixedTime > 5) {
			whichFixedTime = 1;
		}

		int mSetTimeMinCount = mSetupSp.getInt("ad_fixed_min_time_" + whichFixedTime, 12);
		int mSetTimeSecCount = mSetupSp.getInt("ad_fixed_sec_time_" + whichFixedTime, 30);
		Log.e(TAG, "resetFixedTimeList...whichFixedTime=" + whichFixedTime + "; mSetTimeMinCount=" + mSetTimeMinCount
				+ "; mSetTimeSecCount=" + mSetTimeSecCount);
		String minStr;// 分
		String secStr;// 秒
		if (mSetTimeMinCount < 0 || mSetTimeMinCount > 23) {
			mSetTimeMinCount = 12;
		}
		if (mSetTimeMinCount < 10) {
			minStr = "0" + mSetTimeMinCount;
		} else {
			minStr = String.valueOf(mSetTimeMinCount);
		}

		if (mSetTimeSecCount < 0 || mSetTimeSecCount > 59) {
			mSetTimeSecCount = 12;
		}
		if (mSetTimeSecCount < 10) {
			secStr = "0" + mSetTimeSecCount;
		} else {
			secStr = String.valueOf(mSetTimeSecCount);
		}

		String fixedTime = minStr + ":" + secStr + ":" + "00";
		Log.e(TAG, "resetFixedTimeList...fixedTime=" + fixedTime);
		mFixedTimeList.add(fixedTime);

		// 需要从设置中读取
		// mFixedTimeList.add("12:42:30");
		// mFixedTimeList.add("12:52:30");
		// mFixedTimeList.add("12:55:00");
		// mFixedTimeList.add("13:00:00");
		// mFixedTimeList.add("14:00:00");
	}

	// 整点时间
	private static void getPointTimeList() {
		if (mPointTimeList.size() <= 0) {
			mPointTimeList.add("00:00:00");
			mPointTimeList.add("01:00:00");
			mPointTimeList.add("02:00:00");
			mPointTimeList.add("03:00:00");
			mPointTimeList.add("04:00:00");
			mPointTimeList.add("05:00:00");
			mPointTimeList.add("06:00:00");
			mPointTimeList.add("07:00:00");
			mPointTimeList.add("08:00:00");
			mPointTimeList.add("09:00:00");
			mPointTimeList.add("10:00:00");
			mPointTimeList.add("12:00:00");
			mPointTimeList.add("13:00:00");
			mPointTimeList.add("14:00:00");
			mPointTimeList.add("15:00:00");
			mPointTimeList.add("16:00:00");
			mPointTimeList.add("17:00:00");
			mPointTimeList.add("18:00:00");
			mPointTimeList.add("19:00:00");
			mPointTimeList.add("20:00:00");
			mPointTimeList.add("21:00:00");
			mPointTimeList.add("22:00:00");
			mPointTimeList.add("23:00:00");
		}
	}

	// 半点时间
	private static void getHalfPointTimeList() {
		if (mHalfPointTimeList.size() <= 0) {
			mHalfPointTimeList.add("00:30:00");
			mHalfPointTimeList.add("01:30:00");
			mHalfPointTimeList.add("02:30:00");
			mHalfPointTimeList.add("03:30:00");
			mHalfPointTimeList.add("04:30:00");
			mHalfPointTimeList.add("05:30:00");
			mHalfPointTimeList.add("06:30:00");
			mHalfPointTimeList.add("07:30:00");
			mHalfPointTimeList.add("08:30:00");
			mHalfPointTimeList.add("09:30:00");
			mHalfPointTimeList.add("10:30:00");
			mHalfPointTimeList.add("12:30:00");
			mHalfPointTimeList.add("13:30:00");
			mHalfPointTimeList.add("14:30:00");
			mHalfPointTimeList.add("15:30:00");
			mHalfPointTimeList.add("16:30:00");
			mHalfPointTimeList.add("17:30:00");
			mHalfPointTimeList.add("18:30:00");
			mHalfPointTimeList.add("19:30:00");
			mHalfPointTimeList.add("20:30:00");
			mHalfPointTimeList.add("21:30:00");
			mHalfPointTimeList.add("22:30:00");
			mHalfPointTimeList.add("23:30:00");
		}
	}
}
