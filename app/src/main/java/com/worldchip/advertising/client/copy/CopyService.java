package com.worldchip.advertising.client.copy;

import com.worldchip.advertising.client.activity.CopyActivity;
import com.worldchip.advertising.client.activity.IdleActivity;
import com.worldchip.advertising.client.activity.MainActivity;
import com.worldchip.advertising.client.activity.PlayViewActivity;
import com.worldchip.advertising.client.application.ExitApplication;
import com.worldchip.advertising.client.utils.SetupUtils;
import com.worldchip.advertising.client.utils.Utils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

public class CopyService extends Service {

	private static final String TAG = "-------CopyService----------";

	@Override
	public void onCreate() {
		Log.e(TAG, "Service onCreate--->");

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.setPriority(2147483647);
		intentFilter.addDataScheme("file");
		registerReceiver(CardMountedReceiver, intentFilter);
		super.onCreate();
	}

	private BroadcastReceiver CardMountedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				// Toast.makeText(CopyService.this, "检测到外部TF卡插入，准备拷贝,
				// 请勿拔掉外部设备或中断拷贝！", Toast.LENGTH_SHORT).show();
				String path = intent.getData().getPath();

				boolean isMediaCard = false;
				Intent i = new Intent(CopyService.this, CopyActivity.class);

				int copyModeIndex = SetupUtils.getCopyMode(CopyService.this);
				SharedPreferences mSharedPre = getSharedPreferences("basic_menu", 0);
				int storageIndex = mSharedPre.getInt("present_equip_index", 0);
				Log.e(TAG, "CardMountedReceiver..path=" + path + "; valueIndex=" + copyModeIndex + "; storageIndex="
						+ storageIndex);
				// 拷贝模式为0表示禁止拷贝
				if (copyModeIndex != 0) {
					if (path.contains("extsd")) {
						if (storageIndex == 1) { // 当前媒体是TF卡，插入的又是TF卡，防止再删除
							startPlayerActivity();
							return;
						}
						isMediaCard = true;
						i.putExtra("from_root_path", Utils.SD_PATH);
					} else if (path.contains("usbhost")) {
						if (storageIndex == 2) { // 当前媒体是TF卡，插入的又是TF卡，防止再删除
							startPlayerActivity();
							return;
						}
						isMediaCard = true;
						i.putExtra("from_root_path", Utils.USB_PATH);
					}

					if (isMediaCard) {
						ExitApplication.getInstance().removeActivity("playview");
						ExitApplication.getInstance().removeActivity("main");
						i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(i);
					}
				} else { // 插卡如果不进行拷贝，则立马播放

					if (path.contains("extsd") && storageIndex == 1) {
						startPlayerActivity();
					} else if (path.contains("usbhost") && storageIndex == 2) {
						startPlayerActivity();
					}

				}
			} else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED) || action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)) {
				String path = intent.getData().getPath();
				SharedPreferences mSharedPre = getSharedPreferences("basic_menu", 0);
				int storageIndex = mSharedPre.getInt("present_equip_index", 0);
				Log.e(TAG, "media removal ....path=" + path + "; storageIndex=" + storageIndex);
				if (path.contains("extsd") && storageIndex == 1) {
					startIdleActivity();
				} else if (path.contains("usbhost") && storageIndex == 2) {
					startIdleActivity();
				}
			}
		}
	};

	// 启动空闲Activity
	private void startIdleActivity() {
		Intent i = new Intent(this, IdleActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}

	private void startPlayerActivity() {
		int windowMode = SetupUtils.windowModeIndex(CopyService.this);
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		Log.e(TAG, "startPlayerActivity...windowMode=" + windowMode);
		if (windowMode == 0) { // 分屏关
			intent.setClass(this, PlayViewActivity.class);
			ExitApplication.getInstance().removeActivity("playview");
		} else {
			intent.setClass(this, MainActivity.class);
			ExitApplication.getInstance().removeActivity("main");
		}

		intent.putExtra("media_mounted", true);
		startActivity(intent);
	}

	@Override
	public void onDestroy() {
		Log.i(TAG, "Service onDestroy--->");
		if (CardMountedReceiver != null) {
			unregisterReceiver(CardMountedReceiver);
		}

		// 确保service退出时，重启启动service
		Intent intent = new Intent("com.worldchipo.advertisingclient.reboot.copyservice");
		sendBroadcast(intent);

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
