package com.worldchip.advertising.client.service;

import com.worldchip.advertising.client.activity.IdleActivity;
import com.worldchip.advertising.client.utils.SetupUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 *
 * @author guofq 20130528
 */
public class BootReceiver extends BroadcastReceiver {

	private static final String TAG = "--------MainLauncher--------BootReceiver-------------------";

	private Intent mIntent;


	@Override
	public void onReceive(Context context, Intent intent) {
		this.mIntent = intent;

		cancelPowerOnOff(context);
		String action = intent.getAction();

		//	if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {

		// startAutoUpdateService(context);
		//	if (SetupUtils.isBootProtect(context)) {
		Log.e(TAG, "ACTION_BOOT_COMPLETED...will start idle activity!");
		startIdleActivity(context);
		//	} else {
		//		return;
		//	}

		//	}
	}

	// 自动启动
	private void startIdleActivity(Context context) {
		Intent i = new Intent(context, IdleActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}


	private void cancelPowerOnOff (Context context) {

		//mIntent.setAction("com.signway.PowerOnOff");
		// 开机后需再次发送。
		mIntent.setAction("recent_power");
		//mIntent.setAction("android.action.reset_power");
		mIntent.setClass(context, DetectTimeService.class);
		context.startService(mIntent);
		//context.sendBroadcast(mIntent);

	}
}