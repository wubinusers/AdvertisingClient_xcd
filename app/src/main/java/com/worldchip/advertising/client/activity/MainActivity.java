package com.worldchip.advertising.client.activity;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsoluteLayout;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.worldchip.advertising.client.application.ExitApplication;
import com.worldchip.advertising.client.entity.Caption;
import com.worldchip.advertising.client.entity.Clip;
import com.worldchip.advertising.client.entity.DetailsInfo;
import com.worldchip.advertising.client.entity.Media;
import com.worldchip.advertising.client.entity.Playlist;
import com.worldchip.advertising.client.entity.Program;
import com.worldchip.advertising.client.utils.Common;
import com.worldchip.advertising.client.utils.IPlayFinished;
import com.worldchip.advertising.client.utils.ISetupCallback;
import com.worldchip.advertising.client.utils.Loger;
import com.worldchip.advertising.client.utils.MediaType;
import com.worldchip.advertising.client.utils.PlayModel;
import com.worldchip.advertising.client.utils.SetupUtils;
import com.worldchip.advertising.client.utils.Utils;
import com.worldchip.advertising.client.utils.XmlReader;
import com.worldchip.advertising.client.view.AdvertisingView;
import com.worldchip.advertising.client.view.AutoImageView;
import com.worldchip.advertising.client.view.AutoScrollTextView;
import com.worldchip.advertising.client.view.SXImageView;
import com.worldchip.advertising.client.view.VideoView;
import com.worldchip.advertisingclient.R;


//import android.view.DisplayManagerAw;
//import android.os.IPowerManager;
import android.os.RemoteException;
//import android.os.ServiceManager;
import android.content.ContentResolver;
import android.provider.Settings;

import android.os.SystemProperties;

import android.media.AudioManager;
//import com.softwinner.Gpio;

/**
 * mainActivity是关于分屏播放界面的
 *
 * @author CQ
 *
 */
@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements IPlayFinished, ISetupCallback {

	private static final String PROP_DISPLAY_ROTATE = "persist.sys.displayrot";

	private static final String TAG = "--MainActivity--";

	private static final int SYSTEM_TIMER_RUNNING = 11;
	private static final boolean DEBUG = true;

	private static final int PRESS_UP = 19;
	private static final int PRESS_DOWN = 20;
	private static final int PRESS_LEFT = 21;
	private static final int PRESS_RIGHT = 22;
	private static final int PRESS_DEL = 23;
	private static final int AUTO_HIDE_POP = 29;

	/* mSetupAnimationIndex 图片切换效果 */
	private int mSetupAnimationIndex = 0;
	/* mAdvertisingView 分屏播放布局 */
	private AdvertisingView mAdvertisingView = null;
	/* 视频View */
	private VideoView mVideoView;
	/* 视频播放路径列表 */
	private List<String> mVideoPathList = new ArrayList<String>();
	/* 视频当前播放的下标 */
	private int mCurrentVideoIndex = 0;
	/* 背景音乐下标 */
	private int mMusicPosition = 0;
	/* 背景音乐 */
	private MediaPlayer mBackgroundMusicPlayer = null;
	private List<Clip> mBackgroundMusicList = null;

	// private AutoImageView mAutoImageView = null;
	/* 图片 */
	private List<AutoImageView> mAutoImageViewList = null;
	/* 图片显示模式，对否全屏 */
	private boolean mIsPlayPhotoFullScreen = true;
	// 系统配置文件,暂时不需要。
	// private Setting mSetting = null;
	// 系统播放列表文件
	private List<Playlist> mPlaylists = null;

	/* 是否为自由分屏模式 */
	private boolean mIsFreedomMode;
	/* 图片切换时间，单位：秒 */
	private int mPlayPhotoTime = 5;
	// 退出时使用
	// private long mExitTime = 0;
	/* 当前播放的Program的下标 */
	private int mCurrentProgramIndex;
	/* 当前播放的下标 */
	private Playlist mCurrentPlaylist = null;
	/* 当前播放的Program id */
	private int mCurrentProgramId = -1;
	/* 系统时间 */
	private Timer mSystemTime = null;
	/* 系统时间View */
	private TextView mTimerIconView = null;
	/* 系统logo */
	private SXImageView mLogoView = null;
	// 滚动字幕
	// private AutoCustomTextView mAutoScrollTextView = null;
	private AutoScrollTextView mAutoScrollTextView = null;

	private Resources mRes = null;

	// 为什么要把这两个的初始值设置成-1呢？
	private int mPreWindowModel = -1;
	private int mPreRootIndex = -1;

	// timer
	private String mTimeFormatPattern = "";
	private boolean mHasVideo = false;
	private SimpleDateFormat mDateFormat = null;
	private String mTimerColor;
	private int mTimerSize;

	// remote control paw
	private PopupWindow popupWindow;
	private EditText checkEditText;
	private View layout;
	private boolean isInput = false;
	private boolean isRight = false;
	private long currentTime;
	private SharedPreferences mSharedPre = null;
	private boolean timeErr;


	private final static int ROATION = 0xff;
	private Timer checkIOTimer;
	private boolean isRoationChange = false;
	private static final char PORT_TYPE = 'B';
	private static final int PORT_NUM_20 = 20;
	private static final int PORT_NUM_21 = 21;

	private Media singleProgramMediaVideo;

	protected void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		mIsFreedomMode = false;
		mAutoImageViewList = new ArrayList<AutoImageView>();

		mAdvertisingView = new AdvertisingView(this);
		mAdvertisingView.setFocusable(false);
		setContentView(mAdvertisingView);

		startMediaUnMountedReceiver();

		ExitApplication.getInstance().addActivity(this);
		mRes = getResources();
	}

	public void onResume() {
		super.onResume();

		try {
			reloadData();

			Thread.sleep(500);
			setRootViews();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (SetupUtils.isAutoRotation(this)) {
			startCheckRotion();
		}
	}


	// 每隔3s监测IO口。
	private void startCheckRotion() {
		checkIOTimer = new Timer();
		checkIOTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				//checkAndAutoRotation();
				Log.d("zhang","Not support rotation !!!");
			}
		}, 0, 5000);

	}

	/*private void checkAndAutoRotation() {
		// io20 状态
		int io20 = Gpio.readGpio(PORT_TYPE, PORT_NUM_20);
		// io21 状态
		int io21 = Gpio.readGpio(PORT_TYPE, PORT_NUM_21);
		isRoationChange = checkRoation(io20, io21);

		if (!isRoationChange) {
			if (io20 == 1 && io21 == 1) {
				getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", 0).commit();
				Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, 0);
				SystemProperties.set(PROP_DISPLAY_ROTATE, "0");
			} else if (io20 == 0 && io21 == 1) {
				getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", 1).commit();
				Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, 1);
				SystemProperties.set(PROP_DISPLAY_ROTATE, "90");
			} else if (io20 == 0 && io21 == 0) {
				getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", 2).commit();
				Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, 2);
				SystemProperties.set(PROP_DISPLAY_ROTATE, "180");
			} else if (io20 == 1 && io21 == 0) {
				getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", 3).commit();
				Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, 3);
				SystemProperties.set(PROP_DISPLAY_ROTATE, "270");
			}

			mHandler.sendEmptyMessage(ROATION);
		}
	}*/

	private boolean checkRoation(int PB20, int PB21) {

		int roation = 0;

		if (PB20 == 1 && PB21 == 1) {
			roation = 0;
		}

		if (PB20 == 0 && PB21 == 1) {
			roation = 1;
		}

		if (PB20 == 0 && PB21 == 0) {
			roation = 2;
		}

		if (PB20 == 1 && PB21 == 0) {
			roation = 3;
		}

		return roation == getSharedPreferences("function_menu", 0).getInt("rotation_index", 0);
	}

	private void reloadData() {
		mCurrentProgramId = -1;
		Loger.openPrint(this);
		SharedPreferences mSharedPre = getSharedPreferences("function_menu", 0);
		int windowMode = mSharedPre.getInt("window_mode_index", 0);
		Log.e(TAG, "onResume...windowMode=" + windowMode + "; mPreWindowModel=" + mPreWindowModel + "; mPreWindowModel="
				+ mPreWindowModel);
		if (windowMode == 0) {
			startIdleViewActivity();
			return;
		}
		if (mPreWindowModel != -1 && mPreWindowModel != windowMode) {

			// 当前选择的分屏模式为0或1，或者之前的分屏模式为0,1且当前选择的分屏模式为2，3,4,5,6...
			if (windowMode < 2 || (mPreWindowModel < 2 && windowMode >= 2)) {
				startIdleViewActivity();
				return;
			}
		}
		int rootPathIndex = SetupUtils.setCurrentRootPath(this);

		if (mPreRootIndex != -1 && rootPathIndex != mPreRootIndex) {
			startIdleViewActivity();
			return;
		}

		mPreWindowModel = rootPathIndex;
		this.removeAdvertingViews();
		this.removeAutoScollViews();

		/* 下面这条取消注释可以解决转屏的问题。*BY 陈强 */
		setScreenOrientation();

		if (loadConfigXml()) {
			// 设置播放器的设置
			setPlayerSettings();
			// 显示Root层
			// setRootViews();
			// 自由分屏模式，需要实时监测配置文件。
			if (DEBUG)
				Log.e(TAG, "onResume...mIsFreedomMode=" + mIsFreedomMode);
			if (mIsFreedomMode) {
				// 检测播放列表
				chooseCurrentPlayList();
			} else {
				if (mPlaylists != null && mPlaylists.size() > 0) {
					mCurrentPlaylist = mPlaylists.get(0);
					runCurrentPlaylist();
				}
			}
		}
	}

	// 媒体卸载监听
	private void startMediaUnMountedReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.setPriority(2147483647);
		intentFilter.addDataScheme("file");
		registerReceiver(mMediaMountedReceiver, intentFilter);
	}

	/* 滚动字幕、时钟、Logo. */
	private void setRootViews() {
		// 时钟
		startSystemTime();
		// 台标
		//	if (SetupUtils.showLogo(this)) {
		//		if (mLogoView == null) {
		//			mLogoView = mAdvertisingView.CreateLogoImageView();
		//		}
		//		mLogoView.setVisibility(View.VISIBLE);
		//	} else {
		//		if (mLogoView != null) {
		//			mLogoView.setVisibility(View.INVISIBLE);
		//		}
		//	}


		// 台标
		if (SetupUtils.showLogo(this) != 0) {
			if (mLogoView == null) {
				mLogoView = mAdvertisingView.CreateLogoImageView(SetupUtils.showLogo(this));
			}
			mLogoView.setVisibility(View.VISIBLE);
		} else {
			if (mLogoView != null) {
				mLogoView.setVisibility(View.INVISIBLE);
			}
		}
		// 滚动字幕
		if (!mIsFreedomMode) {
			if (SetupUtils.isCaptionOpen(this)) {
				// 滚动字幕
				if (mAutoScrollTextView == null) {
					try {
						createAutoScrollTextView();
					} catch (NullPointerException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					resetCaptionSetting();
				}
			} else {
				removeAutoScollViews();
			}
		} else {
			if (mAutoScrollTextView != null) {
				// resetCaptionSetting();
				// 注释掉可以解决字幕强行拉到下面的问题,但是不能强行注释，会引起字幕大小无法调节问题

			}
		}
	}

	// 重置设置滚动属性
	private void resetCaptionSetting() {
		Caption captionSetting = SetupUtils.getCaptionSetting(this);
		AbsoluteLayout.LayoutParams params = null;
		if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_LARGE_SIZE) {
			params = new AbsoluteLayout.LayoutParams(Utils.WIDTH_PIXELS, Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP, 0,
					Utils.HEIGHT_PIXELS - Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP);
		} else if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_SMALL_SIZE) {
			params = new AbsoluteLayout.LayoutParams(Utils.WIDTH_PIXELS, Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP, 0,
					Utils.HEIGHT_PIXELS - Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP);
		} else if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_NROMAL_SIZE) {
			params = new AbsoluteLayout.LayoutParams(Utils.WIDTH_PIXELS, Utils.SCOLL_NORMAL_TEXT_HEIGHT_STEP, 0,
					Utils.HEIGHT_PIXELS - Utils.SCOLL_NORMAL_TEXT_HEIGHT_STEP);
		}
		mAutoScrollTextView.setLayoutParams(params);
		mAutoScrollTextView.setTextSize(SetupUtils.getCaptionSetting(this).getSize());
		mAutoScrollTextView.setCaptionSetting(SetupUtils.getCaptionSetting(this));
	}

	// 设置屏幕分辨率
	private void setScreenOrientation() {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		Utils.WIDTH_PIXELS = mDisplayMetrics.widthPixels;
		Utils.HEIGHT_PIXELS = mDisplayMetrics.heightPixels;

		/*
		 * if(Utils.WIDTH_PIXELS > Utils.HEIGHT_PIXELS ){ Utils.IS_PORT =false;
		 * }else{ Utils.IS_PORT = true; }
		 */

		/* 下面的一部分是关于转屏切换的代码，先进入idleActivity,然后继续播放。 */
		boolean isPort = false;
		// 判断是否屏幕旋转了。
		if (Utils.WIDTH_PIXELS > Utils.HEIGHT_PIXELS) {
			isPort = false;
		} else {
			isPort = true;
		}

		if (Utils.IS_PORT != isPort) {
			Utils.IS_PORT = isPort;
			Intent i = new Intent(this, IdleActivity.class);
			startActivity(i);
			this.finish();
			return;
		}
	}

	// 设置播放器设置
	private void setPlayerSettings() {
		mCurrentProgramIndex = -1;

		// 系统语言
		SetupUtils.setupSystemLangue(MainActivity.this);
		// 播放图片时间
		mPlayPhotoTime = SetupUtils.playPhotoDuration(this);
		// 是否全屏
		mIsPlayPhotoFullScreen = SetupUtils.playPhotoFullScreen(this);
		// 动画效果
		mSetupAnimationIndex = SetupUtils.getSwitchPhotoAnimationIndex(this);
	}

	// 启动空闲Activity
	private void startIdleViewActivity() {
		savePlayState();

		clearImageViewList();

		removeAdvertingViews();
		removeAutoScollViews();
		setResult(1000);
		Intent i = new Intent(this, IdleActivity.class);
		startActivity(i);
		this.finish();
	}

	private void clearImageViewList() {
		for (AutoImageView imageView : mAutoImageViewList) {
			if (imageView != null) {
				imageView.clearData();
				imageView = null;
			}
		}
		mAutoImageViewList.clear();
	}

	// 保存状态
	private void savePlayState() {
		if (mSystemTime != null) {
			mSystemTime.cancel();
			mSystemTime = null;
		}

		if (mBackgroundMusicPlayer != null) {
			mBackgroundMusicPlayer.stop();
		}
		if (mVideoView != null && mVideoView.isPlaying()) {
			mVideoView.stopPlayback();
		}

		mHandler.removeMessages(Utils.CHECK_PLAYLIST_SETTING);
		mHandler.removeMessages(SYSTEM_TIMER_RUNNING);

		this.removeAdvertingViews();
	}

	// 加载系统配置文件及播放列表文件
	private boolean loadConfigXml() {
		if (mPlaylists != null) {
			mPlaylists.clear();
			mPlaylists = null;
		}

		if (mCurrentPlaylist != null) {
			mCurrentPlaylist = null;
		}
		SharedPreferences mSharedPre = getSharedPreferences("function_menu", 0);
		int windowMode = mSharedPre.getInt("window_mode_index", 0);
		mPreWindowModel = windowMode;
		Log.e(TAG, "loadConfigXml...windowMode =" + windowMode);
		if (windowMode == 1) {
			mIsFreedomMode = true;
		}

		if (mIsFreedomMode) {
			Utils.isFreeWinMode = true;
			mPlaylists = XmlReader.pullParseXmlPlaylist(Utils.CURRENT_ROOT_PATH);
		} else {
			Utils.isFreeWinMode = false;
			mPlaylists = XmlReader.pullParseXmlPlaylist(this, windowMode - 1);
		}

		if (mPlaylists == null) {
			if (mIsFreedomMode) {
				mAdvertisingView.CreateTextView(Utils.WIDTH_PIXELS, Utils.HEIGHT_PIXELS,
						mRes.getString(R.string.no_playlist_file), Gravity.CENTER);
			}
			return false;
		}
		return true;
	}

	// 时钟显示
	private void startSystemTime() {
		if (mSystemTime != null) {
			mSystemTime.cancel();
			mSystemTime = null;
		}

		if (mTimerIconView != null) {
			mAdvertisingView.removeView(mTimerIconView);
			mTimerIconView = null;
		}
		SharedPreferences mSharedPre = getSharedPreferences("time_set", 0);
		boolean start = SetupUtils.getTimeSwitch(this);
		if (start) {
			int location = mSharedPre.getInt("time_location_index", 0);
			int timeStyleIndex = mSharedPre.getInt("time_style_index", 2);
			String[] timeStyleArr = getResources().getStringArray(R.array.time_style);
			if (timeStyleArr.length > 0 && timeStyleIndex < timeStyleArr.length) {
				mTimeFormatPattern = getTimeFormat(timeStyleIndex);
			} else {
				mTimeFormatPattern = getString(R.string.default_time_pattern);
			}
			mDateFormat = SetupUtils.getDateFormatForLangue(this, mTimeFormatPattern);
			mTimerSize = SetupUtils.getTimerSize(this);
			mTimerColor = SetupUtils.getTimerColor(this);

			if (mSystemTime == null) {
				mSystemTime = new Timer();
			}

			if (mTimerIconView == null) {
				mTimerIconView = mAdvertisingView.CreateTimerTextView(location);
			}

			startSystemTimer();
		} else {
			// 时钟关
			if (mTimerIconView != null) {
				mTimerIconView.setVisibility(View.GONE);
			}
			mTimerIconView = null;
		}
	}

	private static String getTimeFormat(int selectedIndex) {
		switch (selectedIndex) {
			case 0:
				return "yyyy-MM-dd E HH:mm:ss";
			case 1:
				return "yyyy-MM-dd HH:mm:ss";
			case 2:
				return "HH:mm:ss";
			case 3:
				return "HH:mm";
			default:
				return "HH:mm";
		}
	}

	private void startSystemTimer() {
		mSystemTime.schedule(new TimerTask() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(SYSTEM_TIMER_RUNNING);
			}
		}, 0, 1 * 1000);
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case Utils.CHECK_PLAYLIST_SETTING:
					chooseCurrentPlayList();
					break;
				case Utils.PLAY_VIDEO_COMPLETION:
					playVideoCompletion();
					break;
				case SYSTEM_TIMER_RUNNING:
					updateSystemTimer();
					break;
				case PRESS_UP:
					checkEditText.append(getResources().getString(R.string.remote_key_up));
					autoCheck();
					break;
				case PRESS_DOWN:
					checkEditText.append(getResources().getString(R.string.remote_key_down));
					autoCheck();
					break;
				case PRESS_LEFT:
					checkEditText.append(getResources().getString(R.string.remote_key_left));
					autoCheck();
					break;
				case PRESS_RIGHT:
					checkEditText.append(getResources().getString(R.string.remote_key_right));
					autoCheck();
					break;
				case AUTO_HIDE_POP:
					autoHidePop();
					break;
				case PRESS_DEL:
					try {
						checkEditText.getText().delete(checkEditText.getText().length() - 1,
								checkEditText.getText().length());
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;

				case ROATION:
					//	MainActivity.this.recreate();
					//Intent i = new Intent();
					//	i.setClass(MainActivity.this, IdleActivity.class);
					//	startActivity(i);
					MainActivity.this.onDestroy();
					System.exit(0);
					break;
				default:
					break;
			}
			super.handleMessage(msg);
		}
	};

	@SuppressWarnings("static-access")
	private void autoCheck() {
		if (checkEditText.getText().length() == 6) {
			if (checkPassword()) {
				isRight = true;
				popupWindow.dismiss();
				currentTime = System.currentTimeMillis();
				mSharedPre.edit().putLong("main_crt_tim", currentTime).commit();
				isInput = false;
				Toast t = new Toast(MainActivity.this);
				t.makeText(MainActivity.this, getResources().getString(R.string.unlock_success), Toast.LENGTH_LONG).show();
			} else {
				Toast t = new Toast(MainActivity.this);
				t.makeText(MainActivity.this, getResources().getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
				checkEditText.setText("");
			}
		}
	}

	// 时钟显示
	@SuppressLint("SimpleDateFormat")
	private void updateSystemTimer() {
		if (mDateFormat != null) {
			String dateTime = mDateFormat.format(new java.util.Date());
			if (mTimerIconView != null) {
				mTimerIconView.setText(dateTime);
				mTimerIconView.setTextSize(mTimerSize);
				mTimerIconView.setTextColor(Color.parseColor(mTimerColor));
			}
		}
	}

	// 重置playlist
	private void resetPlaylist() {
		mCurrentPlaylist = null;
		mCurrentProgramIndex = -1;
		// showToast(mRes.getString(R.string.switch_next_playlist));
	}

	/*
	 * 选择当前的播放列表
	 */
	@SuppressLint("SimpleDateFormat")
	private void chooseCurrentPlayList() {

		// 每30秒检测设置及配置列表
		mHandler.removeMessages(Utils.CHECK_PLAYLIST_SETTING);
		mHandler.sendEmptyMessageDelayed(Utils.CHECK_PLAYLIST_SETTING, Utils.CHECK_PLAYLIST_SETTING_TIME);

		/**
		 * SimpleDateFormat函数语法： G 年代标志符 y 年 M 月 d 日 h 时 在上午或下午 (1~12) H 时 在一天中
		 * (0~23) m 分 s 秒 S 毫秒 E 星期 D 一年中的第几天 F 一月中第几个星期几 w 一年中第几个星期 W 一月中第几个星期
		 * a 上午 / 下午 标记符 k 时 在一天中 (1~24) K 时 在上午或下午 (0~11) z 时区
		 */
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		long currentSystemTime = SetupUtils.getCurrentSystemTime();

		boolean isFound = false;
		Playlist playlist = null;

		if (DEBUG)
			Log.e(TAG, "chooseCurrentPlayList..mPlaylists=" + mPlaylists.size());



		// 如果只有一个list
		if (mPlaylists.size() == 1) {
			playlist = mPlaylists.get(0);
			try {
				String playListStart = playlist.getStart();// 获取Playlist的开始时间
				String playListEnd = playlist.getEnd();// 获取Playlist的结束时间
				long playListStartTime = formatter.parse(playListStart).getTime();
				long playListEndTime = formatter.parse(playListEnd).getTime();
				Log.e(TAG, "yy   playListStart : " + playListStart + ", playListEnd : " + playListEnd);
				if (currentSystemTime < playListStartTime || currentSystemTime > playListEndTime) {
					removeAdvertingViews();
					removeAutoScollViews();
					// 在此可以添加不在播放时间内的逻辑
					if (mBackgroundMusicPlayer != null) {
						mBackgroundMusicPlayer.stop();
					}
					clearImageViewList();

					if (mIsFreedomMode) {
						mAdvertisingView.CreateMessageTextView(Utils.WIDTH_PIXELS, Utils.HEIGHT_PIXELS,
								mRes.getString(R.string.no_playlist_file), Gravity.CENTER);
					}
					return;
				}
				choosePlaylist(playlist);
			} catch (ParseException e) {
				Log.e(TAG, "find err...e=" + e.getMessage());
				e.printStackTrace();
			}
			return;
		}

		// 遍历N-1个list
		for (int i = 0; i < mPlaylists.size(); i++) {
			playlist = mPlaylists.get(i);
			try { // <List start="08:00:00" end="09:00:00">
				String playListStart = playlist.getStart();// 获取Playlist的开始时间
				String playListEnd = playlist.getEnd();// 获取Playlist的结束时间
				long playListStartTime = formatter.parse(playListStart).getTime();
				long playListEndTime = formatter.parse(playListEnd).getTime();
				Log.e(TAG, "xx   playListStart : " + playListStart + ", playListEnd : " + playListEnd);
				if (currentSystemTime < playListStartTime || currentSystemTime > playListEndTime) {
					continue;
				}
				Log.e(TAG, "playListStart : " + playListStart + ", playListEnd : " + playListEnd + ", week = "
						+ playlist.getWeek() + ", month = " + playlist.getMonth());
				if (DEBUG)
					Log.e(TAG, "is found");
				isFound = true;
				choosePlaylist(playlist);
				break;
			} catch (ParseException e) {
				Log.e(TAG, "find err...e=" + e.getMessage());
				e.printStackTrace();
			}
		}

		if (!isFound) {
			Log.e(TAG, "chooseCurrentPlayList...not found...will get last config");
			if (mPlaylists == null || mPlaylists.size() <= 0) {
				if (mIsFreedomMode) {
					mAdvertisingView.CreateMessageTextView(Utils.WIDTH_PIXELS, Utils.HEIGHT_PIXELS,
							mRes.getString(R.string.no_playlist_file), Gravity.CENTER);
				}
				return;
			}

			removeAdvertingViews();
			removeAutoScollViews();
			if (mBackgroundMusicPlayer != null) {
				mBackgroundMusicPlayer.stop();
			}
			clearImageViewList();
			if (mIsFreedomMode) {
				mAdvertisingView.CreateMessageTextView(Utils.WIDTH_PIXELS, Utils.HEIGHT_PIXELS,
						mRes.getString(R.string.no_playlist_file), Gravity.CENTER);
			}

			/*
			 * playlist = mPlaylists.get(mPlaylists.size()-1); if (playlist !=
			 * null && playlist.getStart().equals("-1") &&
			 * playlist.getEnd().equals("-1")){ if (mCurrentPlaylist != null &&
			 * mCurrentPlaylist.getStart().equals(playlist.getStart()) &&
			 * mCurrentPlaylist.getEnd().equals(playlist.getEnd())) { return; }
			 * resetPlaylist(); mCurrentPlaylist = playlist;
			 * runCurrentPlaylist(); }
			 */
		}
	}

	private void choosePlaylist(Playlist playlist) {
		if (mCurrentPlaylist == null) // first
		{
			mCurrentPlaylist = playlist;
			runCurrentPlaylist();
		} else {
			if (mCurrentPlaylist.getStart().equals(playlist.getStart())
					&& mCurrentPlaylist.getEnd().equals(playlist.getEnd())) {
				return;
			} else {
				resetPlaylist();
				mCurrentPlaylist = playlist;
				runCurrentPlaylist();
			}
		}
	}

	// 运行当前playlist
	private void runCurrentPlaylist() {
		if (mCurrentPlaylist == null) {
			return;
		}
		mCurrentProgramIndex = 0;

		// 获取当前播放列表中的所有Program
		List<Program> programList = mCurrentPlaylist.getPrograms();
		if (programList == null || programList.size() < 1) {
			return;
		}

		// 播放当前的program
		playNextProgram(programList.get(mCurrentProgramIndex));
	}

	// 切换下一个Program
	private void switchNextProgram() {
		if (DEBUG)
			Log.e(TAG, "switchNextProgram...");

		if (mCurrentPlaylist == null) {
			if (DEBUG)
				Log.e(TAG, "switchNextProgram...mCurrentPlaylist is empty!");
			return;
		}

		// 获取当前播放列表中的所有Program
		List<Program> programList = mCurrentPlaylist.getPrograms();
		if (programList == null || programList.size() < 1) {
			if (DEBUG)
				Log.e(TAG, "switchNextProgram...programList is empty!");
			return;
		}

		if (mIsFreedomMode) {
			// 播放当前播放列表中的下一个Program
			mCurrentProgramIndex++;
			Log.e(TAG, "switchNextProgram...programList.size(): " + programList.size() + "; mCurrentProgramIndex="
					+ mCurrentProgramIndex);
			if (mCurrentProgramIndex >= programList.size()) {
				mCurrentProgramIndex = 0;
			}
		}

		if (programList.size() > 1) {
			if (!Utils.isSplitOnlyPic()) playNextProgram(programList.get(mCurrentProgramIndex));
		} else {
			if (!Utils.isSplitOnlyPic()) {
				removeVideoViews();
				createVideoView(singleProgramMediaVideo);
			}
		}
	}

	// 播放当前Program
	@SuppressLint({ "DefaultLocale", "NewApi" })
	private void playNextProgram(Program program) {

		mHasVideo = false;

		if (DEBUG)
			Log.e(TAG,
					"playNextProgram...mCurrentProgramId = " + mCurrentProgramId + ", programId = " + program.getId());

		// 移除当前布局，根据当前Program重新进行分配设置
		removeAdvertingViews();

		// mAdvertisingView.setBackground(null);

		clearImageViewList();

		List<Media> medias = program.getMedias();
		if (medias == null || medias.size() < 1) {
			return;
		}

		List<Media> CaptionList = new ArrayList<Media>();
		CaptionList.clear();

		for (Media media : medias) {
			if (media.getType().toUpperCase().equals("V")) {
				mHasVideo = true;
				Utils.setSplitOnlyPic(false);
				break;
			}
		}

		for (Media media : medias) {
			String type = media.getType();
			if (type.toUpperCase().equals("V")) {
				if (mBackgroundMusicPlayer != null) {
					mBackgroundMusicPlayer.stop();
					mBackgroundMusicPlayer = null;
				}
				singleProgramMediaVideo = media;
				createVideoView(media);
			} else if (type.equals("P")) {
				createImageView(media);
			} else if (type.equals("M")) {
				if (program.getId() != mCurrentProgramId) {
					createMusicView(program, media);
				}
			} else if (type.equals("C")) {
				CaptionList.add(media);
			}

			// 设置自由模式下的背景图片
			String path = program.getBackground();
			Log.e(TAG, "playNextProgram.....path" + path);
			if (path != null && !path.equals("") && mIsFreedomMode) {
				if (!path.contains(Utils.CURRENT_ROOT_PATH)) {
					path = Utils.CURRENT_ROOT_PATH + Utils.LOGO_PATH + path;
				}
				Bitmap image = Common.loadImageFromLocal(path, mAdvertisingView);
				if (image != null) {
					mAdvertisingView.setBackgroundDrawable(new BitmapDrawable(image));
				} else {
					mAdvertisingView.setBackgroundDrawable(null);
				}

			} else {
				SharedPreferences mSharedPre = getSharedPreferences("function_menu", 0);
				int windowMode = mSharedPre.getInt("window_mode_index", 0);
				setAdvertisingViewBackground(windowMode);
			}
		}

		if (mIsFreedomMode) {
			if (program.getId() != mCurrentProgramId) {
				// 移除滚动字幕
				removeAutoScollViews();
				createCaptionView(CaptionList);
			}

		}
		mCurrentProgramId = program.getId();
	}

	private void setAdvertisingViewBackground(int windowMode) {
		Resources res = getResources();
		switch (windowMode) {
			case 7:
				if (Utils.WIDTH_PIXELS > Utils.HEIGHT_PIXELS) {
					Bitmap bm = BitmapFactory.decodeResource(res, R.drawable.normal6);
					if (bm != null) {
						mAdvertisingView.setBackgroundDrawable(new BitmapDrawable(bm));
					} else {
						mAdvertisingView.setBackgroundDrawable(null);
					}
				} else {
					Bitmap bm = BitmapFactory.decodeResource(res, R.drawable.port6);
					if (bm != null) {
						mAdvertisingView.setBackgroundDrawable(new BitmapDrawable(bm));
					} else {
						mAdvertisingView.setBackgroundDrawable(null);
					}
				}
				break;
			case 8:
				if (Utils.WIDTH_PIXELS > Utils.HEIGHT_PIXELS) {
					Bitmap bm = BitmapFactory.decodeResource(res, R.drawable.normal7);
					if (bm != null) {
						mAdvertisingView.setBackgroundDrawable(new BitmapDrawable(bm));
					} else {
						mAdvertisingView.setBackgroundDrawable(null);
					}
				} else {
					Bitmap bm = BitmapFactory.decodeResource(res, R.drawable.port7);
					if (bm != null) {
						mAdvertisingView.setBackgroundDrawable(new BitmapDrawable(bm));
					} else {
						mAdvertisingView.setBackgroundDrawable(null);
					}
				}
				break;
			default:
				break;
		}
	}

	private void createMusicView(Program program, Media media) {
		if (mBackgroundMusicList != null) {
			mBackgroundMusicList.clear();
		}
		if (mBackgroundMusicPlayer != null) {
			mBackgroundMusicPlayer.stop();
		}

		if (mBackgroundMusicList == null) {
			mBackgroundMusicList = new ArrayList<Clip>();
		}
		for (Clip clip : media.getClips()) {
			mBackgroundMusicList.add(clip);
		}
		if (mBackgroundMusicList == null || mBackgroundMusicList.size() < 1) {
			Log.e(TAG, "playNextProgram: play music, clips is empty!");
			return;
		}

		mMusicPosition = -1;
		playNextBackgroundMusic();
	}

	// 播放下一个背景音乐
	private void playNextBackgroundMusic() {
		if (mBackgroundMusicList == null || mBackgroundMusicList.size() <= 0) {
			return;
		}

		mMusicPosition++;
		if (mMusicPosition < 0 || mMusicPosition >= mBackgroundMusicList.size()) {
			mMusicPosition = 0;
		}
		// Log.e(TAG, "playNextBackgroundMusic " + mMusicPosition);
		try {
			Clip clip = mBackgroundMusicList.get(mMusicPosition);
			mBackgroundMusicPlayer = new MediaPlayer();
			Log.e(TAG, "playNextBackgroundMusic...clip.getPath()=" + clip.getPath());
			String path = clip.getPath();
			if (!path.contains(Utils.MEDIA_PATH)) {
				path = Utils.MEDIA_PATH + path;
			}
			Uri mediaUri = Uri.parse(Utils.CURRENT_ROOT_PATH + path);
			mBackgroundMusicPlayer.stop();
			mBackgroundMusicPlayer.reset();
			// 设置播放路径
			mBackgroundMusicPlayer.setDataSource(this, mediaUri);
			mBackgroundMusicPlayer.prepare();
			initBackgroundMusicListener();
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	// 加载图片滚动字幕区域
	private void createCaptionView(List<Media> CaptionList) {
		if (CaptionList.size() <= 0)
			return;
		for (Media captionMedia : CaptionList) {
			if (DEBUG)
				Log.e(TAG,
						"playNextProgram: play text: width=" + captionMedia.getWidth() + "; height="
								+ captionMedia.getHeight() + "; startX=" + captionMedia.getX() + "; startY="
								+ captionMedia.getY());
			List<Clip> clips = captionMedia.getClips();
			if (clips == null || clips.size() < 1) {
				return;
			}
			try {
				Caption captionSetting = SetupUtils.getCaptionSetting(this);
				if (captionSetting == null) {
					return;
				}
				if (!captionSetting.isShow()) {
					return;
				}
				if (DEBUG)
					Log.e(TAG, "playNextProgram: play text: root path=" + Utils.CURRENT_ROOT_PATH + "; path="
							+ clips.get(0).getPath());
				StringBuffer content = XmlReader.getTextString(Utils.CURRENT_ROOT_PATH, clips.get(0).getPath());
				if (content == null || content.toString().equals("")) {
					if (DEBUG)
						Log.e(TAG, "playNextProgram: play text, content is null!");
					return;
				}

				if (mAutoScrollTextView != null) {
					mAutoScrollTextView.setTextVules(content.toString());
					mAutoScrollTextView.setCaptionSetting(captionSetting);
				} else {
					mAutoScrollTextView = mAdvertisingView.CreateAutoScrollTextView(Utils.WIDTH_PIXELS,
							captionMedia.getHeight(), captionMedia.getX(), captionMedia.getY(), captionSetting,
							Common.subMaxScrollText(content.toString()));
				}
			} catch (Exception err) {
				continue;
			}
		}
	}

	// 播放滚动字幕，区别于自定义滚动字幕
	private void createAutoScrollTextView() throws NullPointerException {

		StringBuffer buffer = XmlReader.getTextString();
		if (buffer == null) {
			return;
		}

		Caption captionSetting = SetupUtils.getCaptionSetting(this);

		if (captionSetting == null) {
			return;
		}
		if (!captionSetting.isShow()) {
			return;
		}


		//	mAutoScrollTextView = mAdvertisingView.CreateAutoScrollTextView(Utils.WIDTH_PIXELS, LayoutParams.WRAP_CONTENT,
		//		0, -1, captionSetting, Common.subMaxScrollText(buffer.toString()));

		///++++++++++++++++++++++++++++++++
		if (SetupUtils.getCaptionSetting(this).getShowLocation() == 0) {
			mAutoScrollTextView = mAdvertisingView.CreateAutoScrollTextView(Utils.WIDTH_PIXELS, LayoutParams.WRAP_CONTENT,
					0, -1, captionSetting, Common.subMaxScrollText(buffer.toString()));
		} else {
			mAutoScrollTextView = mAdvertisingView.CreateAutoScrollTextView(Utils.WIDTH_PIXELS, LayoutParams.WRAP_CONTENT,
					0, -2, captionSetting, Common.subMaxScrollText(buffer.toString()));
		}
		///+++++++++++++++++++++++++++++++++
	}

	// 加载图片区域视图
	private void createImageView(Media media) {

		AutoImageView autoImageView = mAdvertisingView.CreateImageView(media.getWidth(), media.getHeight(),
				media.getX(), media.getY());
		mAutoImageViewList.add(autoImageView);
		// Clip is a program.Like a picture,a video both is a clip.

		List<Clip> clips = new ArrayList<Clip>();
		for (Clip clip : media.getClips()) {
			clips.add(clip);
		}

		Log.d("chanson", "clip size is " + clips.size());
		boolean allPhoto = false;
		if (clips != null && clips.size() > 0) {
			for (Clip clip : clips) {
				if (clip.getPath() != null) {
					if (clip.getPath().trim().equals("-1")) {
						allPhoto = true;
						break;
					}
				}
			}
		}

		if (allPhoto) {
			List<DetailsInfo> mAllPhotos = Common.loadData(this, MediaType.PHOTO);
			if (mAllPhotos != null && mAllPhotos.size() > 0) {
				if (!mIsFreedomMode) { // added by guofq
					// 不是自由分屏模式，shuffle打乱随机播放
					Collections.shuffle(mAllPhotos);
				}

				Clip clip = null;
				if (clips != null) {
					clips.clear();
				}
				for (DetailsInfo detail : mAllPhotos) {
					clip = new Clip();
					clip.setPath(detail.getPath());
					clip.setTime(-1);
					clips.add(clip);
				}
			}
		}

		ArrayList<View> views = new ArrayList<View>();
		String path = "";
		int layoutWidth = media.getWidth();
		int layoutHeight = media.getHeight();
		for (Clip clip : clips) {
			SXImageView imageView = new SXImageView(getApplicationContext());
			path = clip.getPath();
			if (!path.contains(Utils.MEDIA_PATH)) {
				path = Utils.MEDIA_PATH + path;
			}
			if (mIsFreedomMode) {
				if (!path.contains(Utils.CURRENT_ROOT_PATH)) {
					path = Utils.CURRENT_ROOT_PATH + path;
				}
				if (clip.getTime() <= 0) {
					imageView.setTime(mPlayPhotoTime);
				} else {
					imageView.setTime(clip.getTime());
				}
			} else {
				imageView.setTime(mPlayPhotoTime);
			}

			// 设置图片播放的路径。这个路径就影响着分屏根目录图片播放不出来的问题
			// 尝试着仿照playViewActivity里面图片路径的处理方法，但是图片会只停留在第一张，不能轮播。
			// 尝试着仿照视频播放的路径播放的处理方式，但是视频和音乐是用mediaPlayer播放的，而图片不是，mediaPlayer提供了setDataSourc方法。
			if (mIsFreedomMode || path.contains(Utils.PLAY_LIST_DIR)) {
				imageView.setPath(path);
			} else {
				List<DetailsInfo> mAllPhotos = Common.loadData(this, MediaType.PHOTO);
				if (mAllPhotos != null && mAllPhotos.size() > 0) {
					if (!mIsFreedomMode) { // added by guofq
						// 不是自由模式，shuffle打乱随机播放
						Collections.shuffle(mAllPhotos);
					}
					DetailsInfo detailsInfo = null;
					// 这个location 的值为0 ，会有很小的几率造成图片循环失效
					int location = 0;
					detailsInfo = mAllPhotos.get(location);
					imageView.setPath(detailsInfo.getPath());
				}
			}

			// 设置图片布局宽度以及高度
			imageView.setLayoutWidth(layoutWidth);
			imageView.setLayoutHeight(layoutHeight);

			if (mIsPlayPhotoFullScreen) {
				imageView.setScaleType(ScaleType.FIT_XY);
			} else {
				imageView.setScaleType(ScaleType.CENTER);
			}
			views.add(imageView);
		}

		Log.e(TAG, "createImageView...size = " + views.size() + "; mHasVideo=" + mHasVideo);

		autoImageView.setData(views, mHasVideo);
		if (mSetupAnimationIndex > 0) {
			autoImageView.setAnimationIndex(mSetupAnimationIndex);
		}
		autoImageView.switchNextPicture();
	}

	private void createVideoView(Media media) {
		mVideoPathList.clear();
		mVideoView = mAdvertisingView.CreateVideoView(mHandler, media.getWidth(), media.getHeight(), media.getX(),
				media.getY(), -1);
		boolean allVideo = false;
		String path = "";
		for (Clip clip : media.getClips()) {
			if (clip.getPath() != null) {
				if (clip.getPath().trim().equals("-1")) {
					allVideo = true;
					break;
				}
			}
			path = clip.getPath();
			if (!path.contains(Utils.MEDIA_PATH)) {
				path = Utils.MEDIA_PATH + path;
			}
			mVideoPathList.add(Utils.CURRENT_ROOT_PATH + path);
		}

		if (DEBUG)
			Log.e(TAG, "createVideoView...allVideo=" + allVideo);
		if (allVideo) { // 所有视频
			mVideoPathList.clear();
			List<DetailsInfo> mAllVideos = Common.loadData(this, MediaType.VIDEO);
			if (DEBUG)
				Log.e(TAG, "createVideoView...mAllPhotos.size=" + mAllVideos.size());
			if (mAllVideos != null && mAllVideos.size() > 0) {
				for (DetailsInfo detail : mAllVideos) {
					mVideoPathList.add(detail.getPath());
				}
			}
		}
		if (mVideoPathList.size() > 0) {// 设置视频播放路径。
			mVideoView.setVideoPath(mVideoPathList.get(0));
		}
	}

	/**
	 * 删除布局重新加载
	 */
	private void removeAdvertingViews() {
		if (mAdvertisingView != null) {
			mAdvertisingView.emptyLayout();
		}
	}

	private void removeVideoViews() {

		mVideoView.setVisibility(View.GONE);
		mVideoView = null;
	}

	private void removeTimerViews(String tag) {
		if (mAdvertisingView != null) {
			for (int i = 0; i < mAdvertisingView.getChildCount(); i++) {
				if (mAdvertisingView.getChildAt(i) instanceof TextView
						&& mAdvertisingView.getChildAt(i).getTag() != null
						&& mAdvertisingView.getChildAt(i).getTag().equals(tag)) {
					mAdvertisingView.removeViewAt(i);
					break;
				}
			}
		}
	}

	private void removeLogViews(String tag) {
		if (mAdvertisingView != null) {
			for (int i = 0; i < mAdvertisingView.getChildCount(); i++) {
				if (mAdvertisingView.getChildAt(i) instanceof SXImageView
						&& mAdvertisingView.getChildAt(i).getTag() != null
						&& mAdvertisingView.getChildAt(i).getTag().equals(tag)) {
					mAdvertisingView.removeViewAt(i);
					this.mLogoView = null;
					break;
				}
			}
		}
	}

	// 删除滚动字幕，有问题。？？？什么问题
	// 这里面并没问题，只是在playViewActivity里面有问题
	private void removeAutoScollViews() {
		if (mAdvertisingView != null) {
			for (int i = 0; i < mAdvertisingView.getChildCount(); i++) {
				if (mAdvertisingView.getChildAt(i) instanceof AutoScrollTextView) {// instanceof...类的实例。
					Log.e(TAG, "removeAutoScollViews-----PlayActivity");
					mAdvertisingView.removeViewAt(i);
					mAutoScrollTextView = null;
					break;
				}
			}
		}
	}

	/**
	 * 监听处理
	 */
	private void playVideoCompletion() {

		if (mVideoView == null)
			return;
		int n = mVideoPathList.size();
		if (++mCurrentVideoIndex < n) {
			mVideoView.setVideoPath(mVideoPathList.get(mCurrentVideoIndex));
			mVideoView.setScales(SetupUtils.getVideoScale(MainActivity.this));
		} else {
			// 切换下一个播放Program
			Log.e(TAG, "setOnCompletionListener..mCurrentVideoIndex=" + mCurrentVideoIndex);
			mCurrentVideoIndex = 0;
			if (mIsFreedomMode) {
				switchNextProgram();
			} else { // 非自由分屏下不用切换下一个program
				mVideoView.setVideoPath(mVideoPathList.get(mCurrentVideoIndex));
				mVideoView.setScales(SetupUtils.getVideoScale(MainActivity.this));
			}
		}
	}

	// 背景音乐
	private void initBackgroundMusicListener() {
		if (mBackgroundMusicPlayer != null) {
			mBackgroundMusicPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer arg0) {
					mBackgroundMusicPlayer.start();
				}
			});

			mBackgroundMusicPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer player) {
					playNextBackgroundMusic();
				}
			});
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		Loger.closePrint();

		savePlayState();

		if (mVideoView != null) {
			mVideoView.pause();
		}
		if (mBackgroundMusicPlayer != null) {
			mBackgroundMusicPlayer.stop();
		}

		mAdvertisingView.setBackground(null);

		this.finish();
	}

	@Override
	protected void onDestroy() {

		if (mVideoView != null) {
			if (mVideoView.isPlaying()) {
				mVideoView.stopPlayback();
			}
		}
		if (mBackgroundMusicPlayer != null) {
			mBackgroundMusicPlayer.stop();
		}
		mAdvertisingView.setBackground(null);
		super.onDestroy();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e(TAG, "onActivityResult...requestCode=" + requestCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		//if (DEBUG)
		//Log.e(TAG, "onKeyDown..keyCode=" + keyCode);
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			mSharedPre = getSharedPreferences("paw", 0);
			if ((System.currentTimeMillis() - mSharedPre.getLong("main_crt_tim", 0L)) < 0L && !timeErr) {
				checkStep1();
				timeErr = true;
			}

			if (!mSharedPre.getString("final_paw", "").equals("") && !isInput
					&& (System.currentTimeMillis() - mSharedPre.getLong("main_crt_tim", 0L)) >= 60 * 1000) {
				checkStep1();
			} else if (isInput) {
				if (!popupWindow.isShowing()) {
					popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
					mHandler.sendEmptyMessageDelayed(AUTO_HIDE_POP, 15 * 1000);
				}
				switch (event.getKeyCode()) {
					case 19:
						mHandler.sendEmptyMessage(PRESS_UP);
						break;
					case 20:
						mHandler.sendEmptyMessage(PRESS_DOWN);
						break;
					case 21:
						mHandler.sendEmptyMessage(PRESS_LEFT);
						break;
					case 22:
						mHandler.sendEmptyMessage(PRESS_RIGHT);
						break;
					case 67:
						mHandler.sendEmptyMessage(PRESS_DEL);
						break;
					default:
						break;
				}
			} else if (isRight || mSharedPre.getString("final_paw", "").equals("")
					|| ((System.currentTimeMillis() - mSharedPre.getLong("main_crt_tim", 0L)) < 60 * 1000)
					&& System.currentTimeMillis() - mSharedPre.getLong("main_crt_tim", 0L) > 0) {
				switch (event.getKeyCode()) {
					case 0:
						break;
					case 67:
						// 定义DEL键为转屏键；在此添加转屏逻辑
						int i = SetupUtils.getScreenRotation(this);
						i++;
						if (i > 3) {
							i = 0;
						}
						//by zhang
						//getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", i).commit();
						//Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, i);
						//SystemProperties.set(PROP_DISPLAY_ROTATE, 90 * i + "");
						this.recreate();
						break;
					case KeyEvent.KEYCODE_MENU:
						Intent intent = new Intent(MainActivity.this, SetupActivity.class);
						startActivity(intent);
						break;
					case KeyEvent.KEYCODE_MEDIA_STOP:
						// Intent intent = new Intent(MainActivity.this,
						// SetupActivity.class);
						// startActivity(intent);
						break;
					case KeyEvent.KEYCODE_BACK:
						/*
						 * if ((System.currentTimeMillis() - mExitTime) > 2000){
						 * showToast(getResources().getString(R.string.back_key_again));
						 * mExitTime = System.currentTimeMillis(); }else{
						 * ExitApplication.getInstance().exit(); System.exit(0); }
						 */
						// back 键退出apk.
						//showExitDialog();
						break;
					case 23: // enter
					case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
					case KeyEvent.KEYCODE_MEDIA_NEXT:
					case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
					case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
					case KeyEvent.KEYCODE_MEDIA_REWIND:
						break;
					default:
						return false;
				}
			}
		}
		return true;
	}

	/*
	@SuppressLint("NewApi")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (DEBUG)
			Log.e(TAG, "onKeyDown..keyCode=" + keyCode);
		mSharedPre = getSharedPreferences("paw", 0);
		if ((System.currentTimeMillis() - mSharedPre.getLong("main_crt_tim", 0L)) < 0L && !timeErr) {
			checkStep1();
			timeErr = true;
		}

		if (!mSharedPre.getString("final_paw", "").equals("") && !isInput
				&& (System.currentTimeMillis() - mSharedPre.getLong("main_crt_tim", 0L)) >= 60 * 1000) {
			checkStep1();
		} else if (isInput) {
			if (!popupWindow.isShowing()) {
				popupWindow.showAtLocation(layout, Gravity.CENTER, 0, 0);
				mHandler.sendEmptyMessageDelayed(AUTO_HIDE_POP, 15 * 1000);
			}
			switch (keyCode) {
			case 19:
				mHandler.sendEmptyMessage(PRESS_UP);
				break;
			case 20:
				mHandler.sendEmptyMessage(PRESS_DOWN);
				break;
			case 21:
				mHandler.sendEmptyMessage(PRESS_LEFT);
				break;
			case 22:
				mHandler.sendEmptyMessage(PRESS_RIGHT);
				break;
			case 67:
				mHandler.sendEmptyMessage(PRESS_DEL);
				break;
			default:
				break;
			}
		} else if (isRight || mSharedPre.getString("final_paw", "").equals("")
				|| ((System.currentTimeMillis() - mSharedPre.getLong("main_crt_tim", 0L)) < 60 * 1000)
						&& System.currentTimeMillis() - mSharedPre.getLong("main_crt_tim", 0L) > 0) {
			switch (keyCode) {
			case 0:
				break;
			case 67:
				// 定义DEL键为转屏键；在此添加转屏逻辑
				int i = SetupUtils.getScreenRotation(this);
				i++;
				if (i > 3) {
					i = 0;
				}
				getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", i).commit();
				Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, i);
				SystemProperties.set(PROP_DISPLAY_ROTATE, 90 * i + "");
				this.recreate();
				break;
			case KeyEvent.KEYCODE_MENU:
				Intent intent = new Intent(MainActivity.this, SetupActivity.class);
				startActivity(intent);
				break;
			case KeyEvent.KEYCODE_MEDIA_STOP:
				// Intent intent = new Intent(MainActivity.this,
				// SetupActivity.class);
				// startActivity(intent);
				break;
			case KeyEvent.KEYCODE_BACK:
				/*
				 * if ((System.currentTimeMillis() - mExitTime) > 2000){
				 * showToast(getResources().getString(R.string.back_key_again));
				 * mExitTime = System.currentTimeMillis(); }else{
				 * ExitApplication.getInstance().exit(); System.exit(0); }
				 */
	//	showExitDialog();
	//	break;
	//	case 23: //// enter
	//	case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
	//	case KeyEvent.KEYCODE_MEDIA_NEXT:
	//	case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
	//	case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
	//	case KeyEvent.KEYCODE_MEDIA_REWIND:
	//		break;
	//	default:
	//		return false;
	//	}
	//	}

	//	return true;
//	}*/

	private void autoHidePop() {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
			mHandler.removeMessages(AUTO_HIDE_POP);
		}
	}

	private void checkStep1() {
		initPopupWindow(R.layout.check_password_pop_layout);
		mHandler.sendEmptyMessageDelayed(AUTO_HIDE_POP, 15 * 1000);
		isInput = true;
		checkEditText.setFocusable(true);
	}

	private boolean checkPassword() {
		String inputString = checkEditText.getText().toString();
		return mSharedPre.getString("final_paw", "").equals(inputString);
	}

	private void initPopupWindow(int toastLayoutId) {
		int xOffset = 0;
		int yOffset = 0;
		LayoutInflater inflater = getLayoutInflater();
		layout = inflater.inflate(toastLayoutId, null);
		checkEditText = (EditText) layout.findViewById(R.id.check_password_pop_edittext);
		checkEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		popupWindow = new PopupWindow(layout, android.view.WindowManager.LayoutParams.WRAP_CONTENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT);
		popupWindow.showAtLocation(layout, Gravity.CENTER, xOffset, yOffset);
	}

	// 根据不同的播放模式来设定结束后的相关方法。
	@Override
	public void playFinished(PlayModel model) {
		Log.d("3423424234", "535345346456745745333333333");
		if (mCurrentPlaylist == null) {
			Log.e(TAG, "playFinished...is null");
			return;
		}
		List<Program> programList = mCurrentPlaylist.getPrograms();

		if (programList == null || programList.size() < 1) {
			Log.e(TAG, "playFinished...programList is empty!");
			return;
		}
		if (mCurrentProgramIndex < 0) {
			mCurrentProgramIndex = 0;
		}
		Program currentProgram = programList.get(mCurrentProgramIndex);

		Log.e(TAG, "playFinished...mCurrentProgramIndex=" + mCurrentProgramIndex + "; currentProgram.getPlayModel()="
				+ currentProgram.getPlayModel() + "; ");
		/**
		 * P stands for Picture M stands for Media C stands for Caption
		 */
		switch (currentProgram.getPlayModel()) {
			case P:
			case PC:
				if (mIsFreedomMode && model == PlayModel.P) {
					switchNextProgram();
				}
				break;
			case PM:
			case PMC:
				// int switchFlag = SetupUtils.getPhotoOrMusicFinished(this);
				int switchFlag = 0;

				if (mIsFreedomMode && switchFlag == 0 && model == PlayModel.P) { // 如果是图片结束，并且以图片结束为标识，则切换
					switchNextProgram();
				} else if (mIsFreedomMode && switchFlag == 1 && model == PlayModel.M) { // 如果是音乐结束，并且以音乐结束为标识，则切换
					switchNextProgram();
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void setupCallback(int index) {
		Log.e(TAG, "startSetupActivity...setupIndex=" + index);
		savePlayState();
	}

	/**
	 * 媒体插入监听 监听SD卡和U盘的插入，并且判断是否需要拷贝，以及要拷贝的文件夹
	 */

	private BroadcastReceiver mMediaMountedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				String path = intent.getData().getPath();

				Log.d("zhang","BroadcastReceiver path"+path);

				if (path.contains("usbhost")) {
					Log.e(TAG, "Media mounted! path = " + path);
					Message msg = mHandler.obtainMessage();
					msg.obj = "Media mounted!  " + path;

					mHandler.sendMessage(msg);
					if (path.endsWith("/")) {// 根目录
						Utils.USB_ROOT_PATH = path;
					} else {
						Utils.USB_ROOT_PATH = path + "/";
					}
					// USB播放文件目录
					Utils.USB_PATH = Utils.USB_ROOT_PATH + Utils.PLAY_LIST_DIR;
					Log.e(TAG, "usb root path = " + Utils.USB_ROOT_PATH + "; usb path =" + Utils.USB_PATH);
				}

				boolean isMediaCard = false;
				Intent i = new Intent(MainActivity.this, CopyActivity.class);
				SharedPreferences mSharedPre = getSharedPreferences("basic_menu", 0);
				int storageIndex = mSharedPre.getInt("present_equip_index", 0);

				if (path.contains("extsd") && storageIndex == 1) {
					// startPlayerActivity();
					// loadData();
					return;
				} else if (path.contains("usbhost") && storageIndex == 2) {
					// startPlayerActivity();
					// loadData();
					return;
				}

				File copyFile = null;
				File playListDir = null;
				if (path.contains("extsd")) {
					isMediaCard = true;
					copyFile = new File(Utils.SD_ROOT_PATH + Utils.COPY_FILE);
					playListDir = new File(Utils.SD_ROOT_PATH + Utils.PLAY_LIST_DIR);
					if (playListDir != null && playListDir.exists()) {
						Log.e(TAG, "mMediaMountedReceiver playlist exist! from path =" + Utils.SD_PATH);
						i.putExtra("from_root_path", Utils.SD_PATH);
					} else {
						Log.e(TAG, "mMediaMountedReceiver playlist not exist! from path =" + Utils.SD_ROOT_PATH);
						i.putExtra("from_root_path", Utils.SD_ROOT_PATH);
					}
				} else if (path.contains("usbhost")) {
					isMediaCard = true;
					copyFile = new File(Utils.USB_ROOT_PATH + Utils.COPY_FILE);
					playListDir = new File(Utils.USB_ROOT_PATH + Utils.PLAY_LIST_DIR);
					if (playListDir != null && playListDir.exists()) {
						Log.e(TAG, "mMediaMountedReceiver playlist exist! from path =" + Utils.USB_PATH);
						i.putExtra("from_root_path", Utils.USB_PATH);
					} else {
						Log.e(TAG, "mMediaMountedReceiver playlist not exist! from path =" + Utils.USB_ROOT_PATH);
						i.putExtra("from_root_path", Utils.USB_ROOT_PATH);
					}
				}

				Log.e(TAG, "mMediaMountedReceiver..mounted...path=" + path + "; copyFile=" + copyFile);
				if (isMediaCard && copyFile != null && copyFile.exists()) {
					ExitApplication.getInstance().removeActivity("main");
					ExitApplication.getInstance().removeActivity("playview");
					i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				}
			} else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED) || action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)) {
				String path = intent.getData().getPath();

				SharedPreferences mSharedPre = getSharedPreferences("basic_menu", 0);
				int storageIndex = mSharedPre.getInt("present_equip_index", 0);

				// Log.e(TAG, "mMediaMountedReceiver..unmounted...path="+path+";
				// storageIndex="+storageIndex);
				if (path.contains("external_sd") && storageIndex == 1) {
					// sd卡拔出，跳转到idleActivity
					startIdleActivity();
				} else if (path.contains("usbhost") && storageIndex == 2) {
					// U盘卡拔出，跳转到idleActivity
					startIdleActivity();
				}
			}
		}
	};

	// 启动空闲Activity
	private void startIdleActivity() {
		// 将现在的Activity的List中移除。
		// appName = Hdplayer
		ExitApplication.getInstance().removeActivity("Hdplayer");
		setResult(1000);
		Intent i = new Intent(this, IdleActivity.class);
		startActivity(i);
		this.finish();
	}

	// 显示退出对话框
	protected void showExitDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setMessage(R.string.back_key_again);
		builder.setIcon(R.drawable.exit);
		builder.setPositiveButton(this.getResources().getString(R.string.determine),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						ExitApplication.getInstance().exit();
						MainActivity.this.finish();
					}
				});

		builder.setNegativeButton(this.getResources().getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

}
