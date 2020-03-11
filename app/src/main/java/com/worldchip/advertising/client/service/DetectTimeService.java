package com.worldchip.advertising.client.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.worldchip.advertising.client.utils.TimeChangeUtils;
import com.worldchip.advertising.client.utils.Utils;

/**
 * Created by RYX on 2016/4/29.
 */
public class DetectTimeService extends Service {

	private Timer mTimer;
	private Timer mmTimer;

	private List<String> mSendList = new ArrayList<String>();
	private Date mData;

	private String lastIndexString;
	private int lastIndexInt;

	private String resendLastIndexString;
	private int resendLastIndexInt;

	private Intent mIntent;
	private Intent mmIntent;

	//	private Set<String> lastTimesSet = new HashSet<String>();
	private String[] lastTimesStrs = new String[35];
	private SharedPreferences mShare;
	private SharedPreferences mLastSetShare;

	boolean isEverydayEnable;
	String hourAndMin = "";
	String mReceiveStr = "";
	int hour;
	int minute;
	int week;

	private final static int START_SEND = 56743;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == START_SEND) {
				//	Log.d("64564646464", "1111111111111111111111115");
				mmIntent = new Intent();
				mmIntent.setAction("com.signway.PowerOnOff");
				String resendCurrentIndexString = "";
				for (int i = 0; i < lastTimesStrs.length; i++) {
					if (lastTimesStrs[i] != null) {
						resendCurrentIndexString = lastTimesStrs[i].substring(0, 1);
						if (resendCurrentIndexString.equals(resendLastIndexString)) {
							resendLastIndexInt++;
						} else {
							resendLastIndexInt = 0;
						}
						mmIntent.putExtra("group" + resendLastIndexInt, lastTimesStrs[i]);
						resendLastIndexString = lastTimesStrs[i].substring(0, 1);
						sendBroadcast(mmIntent);
					}
				}
				DetectTimeService.this.stopSelf();
			}
		};
	};

	@Override
	public IBinder onBind(Intent intent) {
		mIntent = intent;
		return null;
	}


	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mLastSetShare = getSharedPreferences("last_set", 0);
		Set<String> lastTimesSet = new HashSet<String>();
		String intentString = "";
		if (intent != null && intent.getAction() != null) {
			intentString = intent.getAction();
		}

		if (intentString.equals("recent_power")) {
//	Log.d("64564646464", "1111111111111111111111112");
			lastTimesSet = mLastSetShare.getStringSet("useful_set", lastTimesSet);
			// 将空值删除，下面排序有空值会报错。
			lastTimesSet.remove(null);
			if (lastTimesSet.size() > 0) {
				lastTimesStrs = Set2Array(lastTimesSet);
				Arrays.sort(lastTimesStrs);
				//	Log.d("64564646464", "1111111111111111111111113");
			}
			mHandler.sendEmptyMessageDelayed(START_SEND, 180000);
			/*mmTimer = new Timer();
			mmTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
				//	mmIntent = new Intent();
				//	mmIntent.setAction("com.signway.PowerOnOff");
					Log.d("64564646464", "1111111111111111111111115");
					String resendCurrentIndexString = "";
					for (int i = 0; i < lastTimesStrs.length; i++) {
						if (lastTimesStrs[i] != null) {
							Log.d("64564646464", "1111111111111111111111114");
							Intent mmIntent = new Intent();
					        mmIntent.setAction("com.signway.PowerOnOff");
							resendCurrentIndexString = lastTimesStrs[i].substring(0, 1);
							if (resendCurrentIndexString.equals(resendLastIndexString)) {
								resendLastIndexInt++;
							} else {
								resendLastIndexInt = 0;
							}
							mmIntent.putExtra("group" + resendLastIndexInt, lastTimesStrs[i]);
							resendLastIndexString = lastTimesStrs[i].substring(0, 1);
							{
								Log.d("64564646464", "1111111111111111111111110");
								sendBroadcast(mmIntent);
							}
						}
					}
				}
			}, 18000);*/

			//	return;
		}

		if (intent != null) {
			if (intent.getFlags() == 99999) {

				String[] strings = new String[35];


				strings = intent.getStringArrayExtra("useful_time");


				// 当strings的长度大于0的时候才会保存。
				//if (strings.length > 0) {
				//	Log.d("64564646464", "1111111strings.length ===" + strings.length);
				mLastSetShare.edit().remove("useful_set").commit();
				mLastSetShare.edit().putStringSet("useful_set", Array2Set(strings)).commit();
				mSendList = Arrays.asList(strings);
				//	}
				mShare = getSharedPreferences("eve", 0);
				isEverydayEnable = mShare.getBoolean("isEnable", false);
				mTimer = new Timer();
				mTimer.schedule(new TimerTask() {
					@Override
					public void run() {

						Calendar calendar = Calendar.getInstance();
						hour = calendar.get(Calendar.HOUR_OF_DAY);
						minute = calendar.get(Calendar.MINUTE);
						week = calendar.get(Calendar.DAY_OF_WEEK);
						hourAndMin = String.format("%02d", hour) + ":" + String.format("%02d", minute);
						if (isEverydayEnable && mSendList.size() > 0) {

							for (int i = 0; i < mSendList.size(); i++) {
								if (mSendList.get(i) != null) {
									mReceiveStr = mSendList.get(i);
									if (TimeChangeUtils.getTwoMinAgo(mReceiveStr.substring(7, 12)).equals(hourAndMin)) {
										// String currentIndexString = "";
										for (int ii = 0; ii < mSendList.size(); ii++) {
											if (mSendList.get(ii) != null) {
												Intent intent = new Intent();
												intent.setAction("com.signway.PowerOnOff");
												intent.putExtra("group" + ii, mSendList.get(ii));
												sendBroadcast(intent);
											}
										}
									}
								}
							}
						} else if (mSendList.size() > 0) {
							for (int i = 0; i < mSendList.size(); i++) {
								if (mSendList.get(i) != null) {
									mReceiveStr = mSendList.get(i);
									if ((TimeChangeUtils.getTwoMinAgo(mReceiveStr.substring(7, 12)).equals(hourAndMin))
											&& TimeChangeUtils.changeWeeKToNormal(week).equals(mReceiveStr.substring(0, 1))) {

										String currentIndexString = "";
										for (int ii = 0; ii < mSendList.size(); ii++) {
											if (mSendList.get(ii) != null) {
												Intent mIntent = new Intent();
												mIntent.setAction("com.signway.PowerOnOff");
												currentIndexString = mSendList.get(ii).substring(0, 1);
												if (currentIndexString.equals(lastIndexString)) {
													lastIndexInt++;
												} else {
													lastIndexInt = 0;
												}
												mIntent.putExtra("group" + lastIndexInt, mSendList.get(ii));
												lastIndexString = mSendList.get(ii).substring(0, 1);
												sendBroadcast(mIntent);
											}
										}
									}
								}
							}
						}
					}
				}, 0, 59000);}}

		return START_REDELIVER_INTENT;
	}

/*	@Override
	public void onStart(Intent intent, int startId) {
       super.onStart(intent, startId);
		// this.mmIntent = intent;
		mLastSetShare = getSharedPreferences("last_set", 0);
		Set<String> lastTimesSet = new HashSet<String>();
		String intentString = "";
	    if (intent != null && intent.getAction() != null) {
	    	intentString = intent.getAction();
	    }

		if (intentString.equals("recent_power")) {
	Log.d("64564646464", "1111111111111111111111112");
			lastTimesSet = mLastSetShare.getStringSet("useful_set", lastTimesSet);
			// 将空值删除，下面排序有空值会报错。
			lastTimesSet.remove(null);
			if (lastTimesSet.size() > 0) {
				lastTimesStrs = Set2Array(lastTimesSet);
				Arrays.sort(lastTimesStrs);
					Log.d("64564646464", "1111111111111111111111113");
			}
			mHandler.sendEmptyMessageDelayed(START_SEND, 180000);
			/*mmTimer = new Timer();
			mmTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
				//	mmIntent = new Intent();
				//	mmIntent.setAction("com.signway.PowerOnOff");
					Log.d("64564646464", "1111111111111111111111115");
					String resendCurrentIndexString = "";
					for (int i = 0; i < lastTimesStrs.length; i++) {
						if (lastTimesStrs[i] != null) {
							Log.d("64564646464", "1111111111111111111111114");
							Intent mmIntent = new Intent();
					        mmIntent.setAction("com.signway.PowerOnOff");
							resendCurrentIndexString = lastTimesStrs[i].substring(0, 1);
							if (resendCurrentIndexString.equals(resendLastIndexString)) {
								resendLastIndexInt++;
							} else {
								resendLastIndexInt = 0;
							}
							mmIntent.putExtra("group" + resendLastIndexInt, lastTimesStrs[i]);
							resendLastIndexString = lastTimesStrs[i].substring(0, 1);
							{
								Log.d("64564646464", "1111111111111111111111110");
								sendBroadcast(mmIntent);
							}
						}
					}
				}
			}, 18000);*/

	//	return;
	/*	}

if (intent != null) {
	if (intent.getFlags() == 99999) {

		String[] strings = new String[50];


			strings = intent.getStringArrayExtra("useful_time");


		// 当strings的长度大于0的时候才会保存。
		//if (strings.length > 0) {
		Log.d("64564646464", "1111111strings.length ===" + strings.length);
			mLastSetShare.edit().remove("useful_set").commit();
			mLastSetShare.edit().putStringSet("useful_set", Array2Set(strings)).commit();
			mSendList = Arrays.asList(strings);
	//	}
		mShare = getSharedPreferences("eve", 0);
		isEverydayEnable = mShare.getBoolean("isEnable", false);
		mTimer = new Timer();
		mTimer.schedule(new TimerTask() {
			@Override
			public void run() {

				Calendar calendar = Calendar.getInstance();
				hour = calendar.get(Calendar.HOUR_OF_DAY);
				minute = calendar.get(Calendar.MINUTE);
				week = calendar.get(Calendar.DAY_OF_WEEK);
				hourAndMin = String.format("%02d", hour) + ":" + String.format("%02d", minute);
				if (isEverydayEnable && mSendList.size() > 0) {

					for (int i = 0; i < mSendList.size(); i++) {
						if (mSendList.get(i) != null) {
							mReceiveStr = mSendList.get(i);
							if (TimeChangeUtils.getTwoMinAgo(mReceiveStr.substring(7, 12)).equals(hourAndMin)) {
								// String currentIndexString = "";
								for (int ii = 0; ii < mSendList.size(); ii++) {
									if (mSendList.get(ii) != null) {
									    Intent intent = new Intent();
										intent.setAction("com.signway.PowerOnOff");
										intent.putExtra("group" + ii, mSendList.get(ii));
										sendBroadcast(intent);
									}
								}
							}
						}
					}
				} else if (mSendList.size() > 0) {
					for (int i = 0; i < mSendList.size(); i++) {
						if (mSendList.get(i) != null) {
							mReceiveStr = mSendList.get(i);
							if ((TimeChangeUtils.getTwoMinAgo(mReceiveStr.substring(7, 12)).equals(hourAndMin))
									&& TimeChangeUtils.changeWeeKToNormal(week).equals(mReceiveStr.substring(0, 1))) {

								String currentIndexString = "";
								for (int ii = 0; ii < mSendList.size(); ii++) {
									if (mSendList.get(ii) != null) {
										Intent mIntent = new Intent();
							         	mIntent.setAction("com.signway.PowerOnOff");
										currentIndexString = mSendList.get(ii).substring(0, 1);
										if (currentIndexString.equals(lastIndexString)) {
											lastIndexInt++;
										} else {
											lastIndexInt = 0;
										}
										mIntent.putExtra("group" + lastIndexInt, mSendList.get(ii));
										lastIndexString = mSendList.get(ii).substring(0, 1);
										sendBroadcast(mIntent);
									}
								}
							}
						}
					}
				}
			}
		}, 0, 59000);}}


	}*/


	public Set<String> Array2Set(String[] strings) {
		//数组-->Set
		Set<String> set = new HashSet<String>(Arrays.asList(strings));
		return set;
	}

	public String[]  Set2Array(Set<String> set) {

		String[] arr = new String[set.size()];
		//Set-->数组
		set.toArray(arr);
		return arr;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}


}
