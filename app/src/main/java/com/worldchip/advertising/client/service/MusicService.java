package com.worldchip.advertising.client.service;

import java.io.IOException;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class MusicService extends Service {

	MediaPlayer mMusicPlayer = null;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// mMusicPlayer = MediaPlayer.create(this,R.raw.yueguang);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		if (mMusicPlayer != null) {
			mMusicPlayer.release();
		}
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int command = intent.getIntExtra("command", 0);
		switch (command) {
		case 0:
			if (mMusicPlayer != null) {
				mMusicPlayer.start();
			}
			break;
		case 1:
			if (mMusicPlayer != null) {
				mMusicPlayer.pause();
			}
			break;
		case 3:
			if (mMusicPlayer != null) {
				mMusicPlayer.stop();
				try {
					mMusicPlayer.prepare();
					mMusicPlayer.seekTo(0);
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
		return super.onStartCommand(intent, flags, startId);
	}
}