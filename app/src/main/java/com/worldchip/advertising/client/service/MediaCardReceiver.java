package com.worldchip.advertising.client.service;

import com.worldchip.advertising.client.utils.ReadXmlSettings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MediaCardReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ReadXmlSettings readXmlSettings = new ReadXmlSettings(context);
		if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")) {
		//	if (readXmlSettings.fileIsExists()) {
			//	readXmlSettings.getItem();
		//	}
		}
	}

}
