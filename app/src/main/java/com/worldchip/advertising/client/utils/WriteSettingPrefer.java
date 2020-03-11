package com.worldchip.advertising.client.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class WriteSettingPrefer {

	private Context mContext;

	public WriteSettingPrefer(Context context) {
		mContext = context;
	}

	// 根据名称来获取相应的sharePre。
	public SharedPreferences getSharePreference(String preferName) {
		return mContext.getSharedPreferences(preferName, 0);
	}

	// 写入相应的值。
	public void setMenuPreference(String preferName, String key, int value) {
		getSharePreference(preferName).edit().putInt(key, value).commit();
	}
}
