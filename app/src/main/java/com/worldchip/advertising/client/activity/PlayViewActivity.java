package com.worldchip.advertising.client.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.worldchip.advertising.client.application.ExitApplication;
import com.worldchip.advertising.client.entity.Caption;
import com.worldchip.advertising.client.entity.DetailsInfo;
import com.worldchip.advertising.client.utils.AdSetting;
import com.worldchip.advertising.client.utils.AdSetting.ADType;
import com.worldchip.advertising.client.utils.Common;
import com.worldchip.advertising.client.utils.IPlayFinished;
import com.worldchip.advertising.client.utils.ISetupCallback;
import com.worldchip.advertising.client.utils.IconState;
import com.worldchip.advertising.client.utils.Loger;
import com.worldchip.advertising.client.utils.MediaType;
import com.worldchip.advertising.client.utils.PlayModel;
import com.worldchip.advertising.client.utils.PlayWhat;
import com.worldchip.advertising.client.utils.RecorderLoger;
import com.worldchip.advertising.client.utils.SerialPort;
import com.worldchip.advertising.client.utils.SetupUtils;
import com.worldchip.advertising.client.utils.Utils;
import com.worldchip.advertising.client.utils.XmlReader;
import com.worldchip.advertising.client.view.AdvertisingView;
import com.worldchip.advertising.client.view.AutoMusicView;
import com.worldchip.advertising.client.view.AutoScrollTextView;
import com.worldchip.advertising.client.view.AutoViewPager;
import com.worldchip.advertising.client.view.SXImageView;
import com.worldchip.advertising.client.view.SwitchNextImageCallback;
import com.worldchip.advertising.client.view.VideoView;
import com.worldchip.advertisingclient.R;
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
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.os.PowerManager;
import android.os.SystemClock;


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
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;


//import android.view.DisplayManagerAw;
//import android.os.IPowerManager;
import android.os.RemoteException;
//import android.os.ServiceManager;
import android.content.ContentResolver;
import android.provider.Settings;

//import android.os.SystemProperties;

import android.media.AudioManager;
//import com.softwinner.Gpio;


@SuppressLint("DefaultLocale")
@SuppressWarnings("deprecation")
public class PlayViewActivity extends Activity implements ISetupCallback, IPlayFinished {


	private static final String PROP_DISPLAY_ROTATE = "persist.sys.displayrot";

	private static final String TAG = "--PlayViewActivity--";

	private static final int PLAY_IMAGE = 0;
	private static final int PLAY_MUSIC = 1;
	private static final int PLAY_AD_IMAGE = 2;
	private static final int PLAY_BG_MUSIC_IMAGE = 3;

	private static final int TIMER_RUNNING = 10;
	private static final int SYSTEM_TIMER_RUNNING = 11;
	private static final int SAVE_CURRENT_STATE = 12;
	private static final int RECEIVE_SERIAL_PORT_VIDEO_PATH = 13;
	private static final int RECEIVE_SERIAL_PORT_IMAGE_PATH = 14;
	private static final int START_SEND_PATH = 17;
	private static final int SEND_PATH_AGAIN = 18;
	private static final long CHECK_AD_PLAY_DELAY = 2 * 1000;
	private static final long CHECK_AD_PLAY_TIMEING = 1 * 1000;

	private static final int PRESS_UP = 190;
	private static final int PRESS_DOWN = 200;
	private static final int PRESS_LEFT = 210;
	private static final int PRESS_RIGHT = 220;
	private static final int PRESS_DEL = 230;
	private static final int AUTO_HIDE_POP = 250;

	private static final boolean DEBUG = false;

	private AdvertisingView mAdvertisingView = null;
	private VideoView mVideoView = null;
	private AutoMusicView mAutoMusicView = null;

	private AutoViewPager mAutoViewPager;
	private AutoViewPager mAdAutoViewPager;
	private AutoViewPager mNormalAutoViewPager;

	private List<DetailsInfo> mAdMediaList = new ArrayList<DetailsInfo>();
	private List<DetailsInfo> mBGMusicList = new ArrayList<DetailsInfo>();
	private List<DetailsInfo> mMediaList = new ArrayList<DetailsInfo>();
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Integer> mAdMediaPagerMap = new HashMap<Integer, Integer>();
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Integer> mMediaPagerMap = new HashMap<Integer, Integer>();
	// private String mLastMediaFormat =

	private MediaPlayer mMediaPlayer = null;

	// 系统时间View
	private TextView mTimerIconView = null;
	// 系统logo
	private SXImageView mLogoView = null;
	// 滚动字幕
	// private AutoCustomTextView mAutoScrollTextView = null;
	private AutoScrollTextView mAutoScrollTextView = null;
	// 播放状态：暂停等图标
	private TextView mPlayIconTextView = null;
	/* 音乐曲目 */
	private int mMusicPosition = 0;
	/* 音乐已播放时间 */
	private int mMusicDuration = 0;
	/* 视频曲目 */
	private int mMediaPosition = 0;
	private int mMediaDuration = 0;

	private SharedPreferences mPrefs = null;
	// 是否插卡播放
	private boolean mMediaMounted = false;

	// 插播类型
	private ADType mAdType = null;
	// 间隔插播
	private int mIntervalValue = 0; // 间隔时间，秒
	private int mIntervalCount = 0; // 计时，秒

	// 最后播放的插播视频的位置
	private int mLastAdPosition = -1;
	// 每次插播允许的个数
	private int mMaxAdCount;
	// 当前插播的个数
	private int mCurrentAdIndex = 0;
	// 图片切场动画
	private int mSetupAnimationIndex = 0;

	private boolean mFirst = true;
	// 退出时使用
	// private long mExitTime = 0;

	// 图片切换时间间隔
	private int mPlayPhotoTime = 5;
	private boolean mPlayPhotoFullScreen = true;

	// 是否背景音乐模式
	private boolean mBackgroundMusic = false;
	// 背景音乐
	private MediaPlayer mBackgroundMusicPlayer = null;
	// 插播计时器
	private Timer mTimer = null;
	// 系统时间
	private Timer mSystemTime = null;
	// 保存播放状态，每5秒保存一次。
	private Timer mSavePlayStateTime = null;

	// 当前正在播放的类型
	private PlayWhat mPlayWhat;
	// 前一个状态
	private PlayWhat mPrePlayWhat = PlayWhat.NULL_STATE;
	// Next是否为前一个媒体
	private boolean mPreviousMedia = false;
	private ADTimerTask mAdTimerTask;

	private int mPreWindowModel = -1;
	private int mPreRootIndex = -1;

	// timer
	private String mTimeFormatPattern = "";
	private SimpleDateFormat mDateFormat = null;
	private String mTimerColor;
	private int mTimerSize;

	// 串口同步相关
	private SharedPreferences mSetupSp;
	// 接收到的路径，包含视频和图片的路径。
	private String mReceiveSerialPortPath;
	// 发送的视频路径
	private String mSendSerialPortPath;
	// mSerialPathList 用于存储收到的所有格式，用于判断是否重绘布局。
	private ArrayList<String> mSerialFormatList = new ArrayList<String>();
	// 上一个节目格式
	private String mLastFormat;

	private String lastReceivePath;
	// 串口对象
	SerialPort mSerialPort;
	// 读取串口数据线程
	ReadThread mReadThread;
	// 输出串口数据流
	FileOutputStream mSerialPortOutputStream;
	// 输入串口数据流
	FileInputStream mSerialPortIntputStream;
	// 配置波特率9600
	private static final int PORT_RATE = 9600;
	// 配串口路径/dev/ttyS4，不同的设备不一定。
	private static final String SERIAL_PORT_DEVICE_PATH = "/dev/ttyS4";
	// 是否只有一个同步节目
	private boolean isSingleMedia = false;

	// remote control paw
	private PopupWindow popupWindow;
	private EditText checkEditText;
	private View layout;
	private boolean isInput = false;
	private boolean isRight = false;
	private long currentTime;
	private SharedPreferences mSharedPre = null;
	private boolean canKeyUp = false;
	private boolean timeErr = false;


	private final static int ROATION = 0xff;
	private Timer checkIOTimer;
	private boolean isRoationChange = false;
	private static final char PORT_TYPE = 'B';
	private static final int PORT_NUM_20 = 20;
	private static final int PORT_NUM_21 = 21;

	// 处理发送的消息。
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("static-access")
		@Override
		public void handleMessage(Message msg) {


			switch (msg.what) {
				case PLAY_IMAGE:
					if (mPlayWhat == PlayWhat.NORMAL_IAMGE) {
						playNextNormalMedia(false, false);
					}
					break;
				case PLAY_AD_IMAGE:
					if (mPlayWhat == PlayWhat.AD_IAMGE) {
						playNextAd();
					}
					break;
				case RECEIVE_SERIAL_PORT_VIDEO_PATH:
					playVideoByReceivePath(mReceiveSerialPortPath);
					break;
				case RECEIVE_SERIAL_PORT_IMAGE_PATH:
					getBitmapByPath(mReceiveSerialPortPath, false);
					break;
				case START_SEND_PATH:
					outPutSerialPortStream(mSendSerialPortPath);
					break;
				case SEND_PATH_AGAIN:
					outPutSerialPortStream(mSendSerialPortPath);
					break;
				case SAVE_CURRENT_STATE:
					saveCurrentPlayState();
					break;
				case PLAY_BG_MUSIC_IMAGE:
					if (mPlayWhat == PlayWhat.BG_MUSIC_IMAGE) {
						playNextBgMusicImage(false);
					}
					break;
				case Utils.PLAY_VIDEO_COMPLETION:
					playVideoComletion();
					break;
				case VideoView.VIDEO_ERROR:
					playNext(false);
					break;
				case PLAY_MUSIC:
					updateMusicTime();
					break;
				case SYSTEM_TIMER_RUNNING:
					updateSystemTimer();
					break;
				case TIMER_RUNNING:
					mIntervalCount++;
					checkAdPlayTime();
					break;
				case Utils.HIDE_PLAY_STATE_ICON:
					showPlayStateIcon(IconState.NULL);
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
					checkEditText.setText("");
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
					//	Intent i = new Intent();
					//	i.setClass(PlayViewActivity.this, IdleActivity.class);
					//	startActivity(i);
					PlayViewActivity.this.onDestroy();
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
				mSharedPre.edit().putLong("play_crt_tim", currentTime).commit();
				isInput = false;
				Toast t = new Toast(PlayViewActivity.this);
				t.makeText(PlayViewActivity.this, getResources().getString(R.string.unlock_success), Toast.LENGTH_LONG).show();
			} else {
				Toast t = new Toast(PlayViewActivity.this);
				t.makeText(PlayViewActivity.this, getResources().getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
				checkEditText.setText("");
			}
		}
	}

	private void autoHidePop() {
		if (popupWindow.isShowing()) {
			popupWindow.dismiss();
			mHandler.removeMessages(AUTO_HIDE_POP);
		}
	}

	protected void onCreate(Bundle paramBundle) {

		super.onCreate(paramBundle);
		mAdvertisingView = new AdvertisingView(this);
		setContentView(mAdvertisingView);
		initSerialPort();
		if (SetupUtils.serialPortIndex(this) == 2) {
			mReadThread = new ReadThread();
			mReadThread.start();
		}

		ExitApplication.getInstance().addActivity(this);
		mMediaDuration = 0;
		mMusicDuration = 0;
		mPrefs = this.getSharedPreferences("advertis_setting", 0);
		mMediaMounted = getIntent().getBooleanExtra("media_mounted", false);
		if (DEBUG)
			Log.e(TAG, "onCreate..mMediaMounted=" + mMediaMounted + "; this.title=" + this.getTitle());

		//	reloadData();  ///by chenqiang 20160719
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 是否打开播放记录
		//RecorderLoger.openPrint(this);
		reloadData();

		if (SetupUtils.isAutoRotation(this)) {
			startCheckRotion();
		}
		// loadData();
		if (mPrePlayWhat == PlayWhat.NULL_STATE) {
			return;
		}

		int windowMode = SetupUtils.windowModeIndex(this);
		Log.e(TAG, "onResume...windowMode=" + windowMode + "; mPreWindowModel=" + mPreWindowModel + "; mPreWindowModel="
				+ mPreWindowModel);
		if (windowMode != 0) {
			startIdleActivity();
			return;
		}

		int rootPathIndex = SetupUtils.setCurrentRootPath(this);
		if (mPreRootIndex != -1 && rootPathIndex != mPreRootIndex) {
			startIdleActivity();
			return;
		}

		// 这一句注释，看能不能解决拷贝混乱的问题。
		// mPreWindowModel = rootPathIndex;
		setScreenOrientation();
		// *把这以大段注释掉可以解决断点记忆记忆不了的问题，
		// 因为下面的代码会强行将position置零，会造成播放记忆失效，从头开始。
		try {
			mPlayWhat = mPrePlayWhat;
			//	getLastPlayState();  /// 后面又重复调用了。  BY CHENQIANG 20160801
			// removeAutoScollViews();
			removeTimerViews("play_icon");
			removeTimerViews("timing");
			removeLogViews("logo_img");
			Thread.sleep(500);
			setRootViews();

			if (mPlayWhat == PlayWhat.AD_IAMGE) {
				mHandler.removeMessages(PLAY_AD_IMAGE);
				mHandler.sendEmptyMessageDelayed(PLAY_AD_IMAGE, mPlayPhotoTime * 1000);
			} else if (mPlayWhat == PlayWhat.NORMAL_IAMGE) {
				mHandler.removeMessages(PLAY_IMAGE);
				mHandler.sendEmptyMessageDelayed(PLAY_IMAGE, mPlayPhotoTime * 1000);
			} else {
				mHandler.sendEmptyMessageDelayed(PLAY_IMAGE, mPlayPhotoTime * 1000);
				if (mMediaList != null && mMediaPosition >= mMediaList.size()) {
					mMediaPosition = 0;
				}
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 每隔3s监测IO口。
	private void startCheckRotion() {
		checkIOTimer = new Timer();
		checkIOTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				checkAndAutoRotation();
			}
		}, 0, 5000);

	}

	private void checkAndAutoRotation() {
		// io20 状态
		int io20 = 1; //Gpio.readGpio(PORT_TYPE, PORT_NUM_20);
		// io21 状态
		int io21 = 1; //Gpio.readGpio(PORT_TYPE, PORT_NUM_21);
		isRoationChange = checkRoation(io20, io21);

		if (!isRoationChange) {
			if (io20 == 1 && io21 == 1) {
				getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", 0).commit();
				//Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, 0);
				//SystemProperties.set(PROP_DISPLAY_ROTATE, "0");
			} else if (io20 == 0 && io21 == 1) {
				getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", 1).commit();
				//Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, 1);
				//SystemProperties.set(PROP_DISPLAY_ROTATE, "90");
			} else if (io20 == 0 && io21 == 0) {
				getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", 2).commit();
				//Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, 2);
				//SystemProperties.set(PROP_DISPLAY_ROTATE, "180");
			} else if (io20 == 1 && io21 == 0) {
				getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", 3).commit();
				//Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, 3);
				//SystemProperties.set(PROP_DISPLAY_ROTATE, "270");
			}

			mHandler.sendEmptyMessage(ROATION);
		}
	}

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
		//	Loger.openPrint(this);
		int windowMode = SetupUtils.windowModeIndex(this);
		Log.e(TAG, "onResume...windowMode=" + windowMode);
		if (windowMode != 0) {
			startIdleActivity();
			return;
		}

		// this.removeAutoScollViews();
		try {
			mAutoScrollTextView.setVisibility(View.INVISIBLE);
		} catch (Exception e) {
			// TODO: handle exception
		}
		setScreenOrientation();
		startMediaUnMountedReceiver();

		loadData();

		// if (mMediaList.size() == 1) {
		// isSingleMedia = true;
		// } else {
		// isSingleMedia = false;
		// }
	}

	private void loadData() {
		stopPlay();

		if (mAdvertisingView != null) {
			mAdvertisingView.emptyLayout();
		}

		if (!mMediaMounted) {
			SetupUtils.setCurrentRootPath(this);
		}

		mBackgroundMusic = SetupUtils.isBackgroundMuiscModel(this);
		mPlayPhotoTime = SetupUtils.playPhotoDuration(this);
		mPlayPhotoFullScreen = SetupUtils.playPhotoFullScreen(this);
		mSetupAnimationIndex = SetupUtils.getSwitchPhotoAnimationIndex(this);

		clearMediaList();

		startAdPlayerTimer();
		// 加载不变的logo
		setRootViews();
		// 获取上次播放的状态
		getLastPlayState();

		/*
		 * mIsRecord = SetupUtils.isPlayRecordOpen(this); Log.e(TAG,
		 * "onResume...mIsRecord=" + mIsRecord); if (mIsRecord) { try { //
		 * 避免空指针。 RecorderLoger.openPrint(this); } catch (Exception e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } }
		 */

		startSaveUpdateTimer();

		if (mBackgroundMusic) {
			Loger.print("已启用背景音乐模式！");
			loadBackgroundMusicData();
		} else {
			Loger.print("未启用背景音乐模式，将按照正常媒体顺序播放！");
			loadNormalMediaData();
		}
	}

	// 设置屏幕分辨率？
	// Orientation是方向的意思。
	private void setScreenOrientation() {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		Utils.WIDTH_PIXELS = mDisplayMetrics.widthPixels;
		Utils.HEIGHT_PIXELS = mDisplayMetrics.heightPixels;

		boolean isPort = false;
		// 判断是否屏幕旋转了。
		if (Utils.WIDTH_PIXELS > Utils.HEIGHT_PIXELS) {
			isPort = false;
		} else {
			isPort = true;
		}

		if (Utils.IS_PORT != isPort) {
			Utils.IS_PORT = isPort;
			startIdleActivity();
			return;
		}

		Loger.print("当前设备分辨率--横屏像素:" + Utils.WIDTH_PIXELS + "; 竖屏像素: " + Utils.HEIGHT_PIXELS);
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

	// 滚动字幕、时钟、logo，不变的View
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
		// 播放状态
		if (mPlayIconTextView == null) {
			mPlayIconTextView = mAdvertisingView.CreatePlayIconTextView();
		}
		showPlayStateIcon(IconState.NULL);
		// 滚动字幕
		boolean caption = SetupUtils.isCaptionOpen(this);
		Log.e(TAG, "caption =" + caption);
		ifShowCaption();

		/*
		 * if (caption) { if (mAutoScrollTextView == null) { //
		 * 目前读取根目录下字幕出现问题了。。。 try { createAutoScrollTextView(); } catch
		 * (NullPointerException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } } else { resetCaptionSetting();
		 * mAutoScrollTextView.setVisibility(View.VISIBLE); } } else { //
		 * removeAutoScollViews(); try {
		 * mAutoScrollTextView.setVisibility(View.INVISIBLE); } catch (Exception
		 * e) { // TODO: handle exception } }
		 */
	}

	private void ifShowCaption() {
		boolean caption = SetupUtils.isCaptionOpen(this);
		if (caption) {
			if (mAutoScrollTextView == null) {
				// 目前读取根目录下字幕出现问题了。。。
				try {
					createAutoScrollTextView();
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				////resetCaptionSetting();/////////////
				mAutoScrollTextView.setVisibility(View.VISIBLE);
			}
		} else {
			// removeAutoScollViews();
			try {
				mAutoScrollTextView.setVisibility(View.INVISIBLE);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	// 重置设置滚动属性
	// 绝对布局已经不推荐使用，可以改成相对布局试试
	private void resetCaptionSetting() {
		Log.e("resetCaptionSetting", "resetCaptionSetting -------xxx");
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

	// 每5秒保存一次当前播放状态
	private void startSaveUpdateTimer() {
		if (mSavePlayStateTime != null) {
			mSavePlayStateTime.cancel();
			mSavePlayStateTime = null;
		}
		mSavePlayStateTime = new Timer();
		mSavePlayStateTime.schedule(new TimerTask() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(SAVE_CURRENT_STATE);
			}
		}, 0, 5 * 1000);// 延迟5秒保存进度
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
			Loger.print("启动时钟显示");
			int location = mSharedPre.getInt("time_location_index", 0);

			int timeStyleIndex = mSharedPre.getInt("time_style_index", 2);
			String[] timeStyleArr = getResources().getStringArray(R.array.time_style);
			if (timeStyleArr.length > 0 && timeStyleIndex < timeStyleArr.length) {
				// 从时间设置的数组来获取时间样式。
				// mTimeFormatPattern = timeStyleArr[timeStyleIndex];
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
		} else { // 时钟关
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
		}, 0, 1 * 1000);// 一秒更新一下显示的时间
	}

	// 启动插播模式
	private void startAdPlayerTimer() {
		if (!AdSetting.isAdPlayOpen(this)) {
			Loger.print("插播功能未开启！");
			return;
		}

		// 先检测是否有插播数据
		String path = Utils.CURRENT_ROOT_PATH + Utils.AD_PATH;

		mAdMediaList = Common.getAllVideoAndImageList(this, path);
		if (mAdMediaList == null || mAdMediaList.size() <= 0) {
			Loger.print("没有插播广告数据！将不启动广告插播。");
			return;
		}

		mAdType = AdSetting.getSettingADType(this);
		Loger.print("广告插播功能开，插播模式为：=" + mAdType);
		switch (mAdType) {
			case INTERVAL:
				mIntervalCount = 0;
				mIntervalValue = AdSetting.getIntervalValue(this);
				break;
			case FIXED:
				AdSetting.resetFixedTimeList(this);
				break;
			default:
				break;
		}
		startADTimer();
	}

	public void startADTimer() {
		stopADTimerTask();
		if (mTimer == null) {
			mTimer = new Timer(true);
		}
		if (mTimer != null) {
			mAdTimerTask = new ADTimerTask();
			mTimer.schedule(mAdTimerTask, CHECK_AD_PLAY_DELAY, CHECK_AD_PLAY_TIMEING);
		}
	}

	public void stopADTimerTask() {
		if (mAdTimerTask != null) {
			mAdTimerTask.cancel();
		}

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	private class ADTimerTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessage(TIMER_RUNNING);
		}
	}

	// 检测是否到插播时间，
	private void checkAdPlayTime() {
		switch (mAdType) {
			case POINT: // 整点
				if (AdSetting.isPointTime(this)) {
					startAdViews();
				}
				break;
			case INTERVAL: // 间隔
				if (mIntervalCount == mIntervalValue) {
					//Log.e(TAG, "INTERVAL..mIntervalCount=" + mIntervalCount);
					Log.d("33333", "isOpen2222==+++++++++");
					startAdViews();
				}
			case FIXED:
				/**
				 * }else if(selectAdType==2){ return ADType.FIXED; } 定点插播现在还没用到
				 */
				if (AdSetting.isFixedTime()) {
					startAdViews();
				}
				break;
			default:
				break;
		}
	}

	private void createViewPager(boolean isFromAdOrSetup, boolean isAdModel, boolean isBGMusicModel,
								 List<DetailsInfo> mediaDetailsInfos) {

		if (isAdModel) {
			//if (mAdAutoViewPager != null) {
			//	mAdAutoViewPager.clearData();
			//	mAdAutoViewPager = null;
			//	mAdAutoViewPager = mAdvertisingView.CreateAutoPager(Utils.WIDTH_PIXELS, Utils.HEIGHT_PIXELS, 0, 0);
			//	mAdAutoViewPager.setData(getViewsList(isAdModel, mediaDetailsInfos));
			//	}
			if (mAdAutoViewPager == null) {
				mAdAutoViewPager = mAdvertisingView.CreateAutoPager(Utils.WIDTH_PIXELS, Utils.HEIGHT_PIXELS, 0, 0);
				mAdAutoViewPager.setData(getViewsList(isAdModel, mediaDetailsInfos));
			}
			if (mNormalAutoViewPager != null) {
				mNormalAutoViewPager.setVisibility(View.GONE);
			}
			mAdAutoViewPager.setVisibility(View.VISIBLE);
			mAutoViewPager = mAdAutoViewPager;
		} else {
			if (mNormalAutoViewPager == null) {
				mNormalAutoViewPager = mAdvertisingView.CreateAutoPager(Utils.WIDTH_PIXELS, Utils.HEIGHT_PIXELS, 0, 0);
				mNormalAutoViewPager.setData(getViewsList(isAdModel, mediaDetailsInfos));
			}

			if (mAdAutoViewPager != null) {
				mAdAutoViewPager.setVisibility(View.GONE);
			}
			mNormalAutoViewPager.setVisibility(View.VISIBLE);
			mAutoViewPager = mNormalAutoViewPager;
		}
		mAutoViewPager.setAnimationIndex(mSetupAnimationIndex);

		if (DEBUG)
			Log.e(TAG, "createViewPager...isFromAdOrSetup=" + isFromAdOrSetup + "; isAdModel=" + isAdModel
					+ "; isBGMusicModel=" + isBGMusicModel);
		if (isAdModel) {
			playNextAd();
		} else {
			if (isBGMusicModel) {
				playNextBgMusicImage(isFromAdOrSetup);
				playNextBgMusic(isFromAdOrSetup);
			} else {
				playNextNormalMedia(isFromAdOrSetup, false);
			}
		}
	}

	private ArrayList<View> getViewsList(boolean isAdModel, List<DetailsInfo> mediaDetailsInfos) {
		ArrayList<View> views = new ArrayList<View>();
		int index = 0;
		int position = 0;
		for (DetailsInfo detailsInfo : mediaDetailsInfos) {
			if (DEBUG)
				Log.e(TAG, "getViewsList..detailsinfo=" + detailsInfo.getPath());
			switch (detailsInfo.getType()) {
				case VIDEO:
					// views.add(createVideoView(detailsInfo));
					if (isAdModel) {
						mAdMediaPagerMap.put(index, -1);
					} else {
						mMediaPagerMap.put(index, -1);
					}
					break;
				case PHOTO:
					views.add(createImageView(detailsInfo));
					if (isAdModel) {
						mAdMediaPagerMap.put(index, position);
					} else {
						mMediaPagerMap.put(index, position);
					}
					position++;
					break;
				case MUSIC:
					views.add(CreateAutoMusicView(detailsInfo));
					if (isAdModel) {
						mAdMediaPagerMap.put(index, position);
					} else {
						mMediaPagerMap.put(index, position);
					}
					position++;
					break;
				default:
					break;
			}
			index++;
		}
		return views;
	}

	/**
	 * 创建图片
	 */
	private SXImageView createImageView(DetailsInfo detailsInfo) {
		SXImageView imageView = new SXImageView(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		imageView.setLayoutParams(params);
		imageView.setTag("image");
		if (mPlayPhotoFullScreen) {
			imageView.setScaleType(ScaleType.FIT_XY);
		} else {
			imageView.setScaleType(ScaleType.FIT_CENTER);
		}
		imageView.setTime(mPlayPhotoTime);
		imageView.setPath(detailsInfo.getPath());
		return imageView;
	}

	/**
	 * 创建音乐autoMusicView
	 *
	 * @return
	 */
	private AutoMusicView CreateAutoMusicView(DetailsInfo detailsInfo) {
		AutoMusicView autoMusicView = new AutoMusicView(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		autoMusicView.setLayoutParams(params);
		autoMusicView.setTag("music");
		autoMusicView.setMusicInfo(detailsInfo.getPath());
		return autoMusicView;
	}

	/**
	 * 播放音乐
	 */
	@SuppressLint({ "SimpleDateFormat", "InflateParams" })
	private void playMusic(boolean isFromAdOrSetup, String path) {
		try {
			//
			mMediaPlayer = new MediaPlayer();
			Uri mediaUri = Uri.parse(path);
			mMediaPlayer.stop();
			mMediaPlayer.reset();
			// 设置播放路径
			mMediaPlayer.setDataSource(this, mediaUri);
			mMediaPlayer.prepare();
			mHandler.sendEmptyMessageDelayed(PLAY_MUSIC, 1000);

			initMusicListener(isFromAdOrSetup);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 加载普通媒体数据
	private void loadNormalMediaData() {
		mMediaList = Common.loadData(this, MediaType.ALL);
		if (mMediaList != null && mMediaList.size() > 0) {
			createViewPager(true, false, false, mMediaList);
		} else {
			Loger.print("没有找到任何播放媒体文件，将退出全屏播放！");
			showToast(getResources().getString(R.string.no_media_file));
			this.finish();
		}
	}

	// 背景音乐
	private void loadBackgroundMusicData() {
		mMediaList = Common.loadData(this, MediaType.PHOTO);
		mBGMusicList = Common.loadData(this, MediaType.MUSIC);

		if (mMediaList != null && mMediaList.size() > 0) {
			createViewPager(true, false, true, mMediaList);
		} else {
			loadNormalMediaData();
			Loger.print("没有找到任何播放媒体文件，将退出全屏播放！");
			// showToast(getResources().getString(R.string.no_media_file));
			// this.finish();
		}
	}

	// 启动空闲Activity
	private void startIdleActivity() {
		// 保存之前的播放状态
		saveLastPlayState();
		// 将现在的Activity的List中移除。
		// appName = Hdplayer
		ExitApplication.getInstance().removeActivity("Hdplayer");
		setResult(1000);
		Intent i = new Intent(this, IdleActivity.class);
		startActivity(i);
		this.finish();
	}

	// 启动插播
	private void startAdViews() {
		// 保存之前的播放状态
		saveLastPlayState();
		//Log.d("33333", "isOpen2222777777777777777777");
		if (mAutoScrollTextView != null) mAutoScrollTextView.setVisibility(View.INVISIBLE);
		Loger.print(getResources().getString(R.string.ad_play_start));
		// 此值为设置中读取广告插播数据
		mMaxAdCount = AdSetting.getAdMaxCount(this);
		mCurrentAdIndex = 0;
		mLastAdPosition = mPrefs.getInt("last_ad_position", 0);

		Log.e(TAG, "startAdViews...mAdMediaList=" + mAdMediaList.size());

		createViewPager(false, true, false, mAdMediaList);
	}

	/* 保存当前播放进度 */
	private void saveCurrentPlayState() {

		if (mVideoView != null && mVideoView.isPlaying()) {
			// 保存视频播放时间
			mPrefs.edit().putInt("media_play_duration", mVideoView.getCurrentPosition()).commit();
			// 保存视频播放序号
			mPrefs.edit().putInt("media_position", mMediaPosition).commit();
		} else if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			// 保存音乐播放时间
			mPrefs.edit().putInt("media_play_duration", mMediaPlayer.getCurrentPosition()).commit();
			// 保存音乐播放序号
			mPrefs.edit().putInt("media_position", mMediaPosition).commit();
		} else {
			if (mBackgroundMusicPlayer != null && mBackgroundMusicPlayer.isPlaying()) {
				// 保存背景音乐播放时间
				mPrefs.edit().putInt("music_play_duration", mBackgroundMusicPlayer.getCurrentPosition()).commit();
				// 保存背景音乐播放序号
				mPrefs.edit().putInt("music_position", mMusicPosition).commit();
			}
			mPrefs.edit().putInt("media_position", mMediaPosition).commit();
		}
	}

	// 保存状态,
	private void saveLastPlayState() {
		mPlayWhat = PlayWhat.STOP;
		saveCurrentPlayState();
		mHandler.removeMessages(SAVE_CURRENT_STATE);
		mHandler.removeMessages(TIMER_RUNNING);
		mHandler.removeMessages(SYSTEM_TIMER_RUNNING);
		mHandler.removeMessages(PLAY_IMAGE);
		mHandler.removeMessages(PLAY_AD_IMAGE);
		mHandler.removeMessages(PLAY_BG_MUSIC_IMAGE);
		mHandler.removeMessages(PLAY_MUSIC);

		if (mSavePlayStateTime != null) {
			mSavePlayStateTime.cancel();
			mSavePlayStateTime = null;
		}

		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
			mMediaPlayer = null;
		}
		if (mVideoView != null && mVideoView.isPlaying()) {
			mVideoView.stopPlayback();
		}
		if (mBackgroundMusicPlayer != null && mBackgroundMusicPlayer.isPlaying()) {
			mBackgroundMusicPlayer.stop();
			mBackgroundMusicPlayer = null;
		}
	}

	private void clearMediaList() {
		mBackgroundMusicPlayer = null;
		mAutoViewPager = null;
		mAdAutoViewPager = null;
		mNormalAutoViewPager = null;

		if (mBGMusicList != null) {
			mBGMusicList.clear();
		}
		if (mMediaList != null) {
			mMediaList.clear();
		}
		if (mAdMediaList != null) {
			mAdMediaList.clear();
		}
		if (mMediaPagerMap != null) {
			mMediaPagerMap.clear();
		}
		if (mAdMediaPagerMap != null) {
			mAdMediaPagerMap.clear();
		}
	}

	// 断点记忆
	// 可以从sharePreference里面读取media和music的播放条目和播放时间。
	private void getLastPlayState() {
		mMediaPosition = mPrefs.getInt("media_position", 0);
		mMediaDuration = mPrefs.getInt("media_play_duration", mMediaDuration);
		mMusicPosition = mPrefs.getInt("music_position", 0);
		mMusicDuration = mPrefs.getInt("music_play_duration", mMusicDuration);
		Log.e(TAG, "getLastPlayeState mMusicPosition=" + mMusicPosition + "; mMediaPosition=" + mMediaPosition
				+ "; mMediaDuration=" + mMediaDuration + "; mMusicDuration=" + mMusicDuration);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e(TAG, "onActivityResult...requestCode=" + requestCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	// 是否正在播放
	private boolean isPlaying() {
		Log.e(TAG, "isPlaying...mPlayWhat=" + mPlayWhat);
		if (mPlayWhat == PlayWhat.PAUSE || mPlayWhat == PlayWhat.STOP) {
			return false;
		}
		return true;
	}

	// 播放
	private void play() {
		Log.e(TAG, "play...mPlayWhat=" + mPlayWhat + "; mPrePlayWhat=" + mPrePlayWhat);
		pause(false);

		mPlayWhat = mPrePlayWhat;
		switch (mPlayWhat) {
			case AD_IAMGE:
				playNextAd();
				break;
			case NORMAL_IAMGE:
				playNextNormalMedia(false, true);
				break;
			case BG_MUSIC_IMAGE:
				playNextBgMusicImage(false);
				if (mBackgroundMusicPlayer != null) {
					mBackgroundMusicPlayer.start();
				}
				break;
			case NORMAL_MUSIC:
				if (mMediaPlayer != null) {
					mMediaPlayer.start();
				}
			case NORMAL_VIDEO:
			case AD_VIDEO:
				if (mVideoView != null) {
					mVideoView.start();
				}
				break;
			default:
				break;
		}
		// 显示图标
		showPlayStateIcon(IconState.PLAY);
	}

	// 停止播放
	public void stopPlay() {
		mPlayWhat = PlayWhat.STOP;

		if (mVideoView != null) {

			mVideoView.stopPlayback();
			mVideoView = null;
		}
		// 在这里emptyVideoView,并不是一个很好的做法。
		// mAdvertisingView.emptyVideoView();

		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer = null;
		}
		if (mBackgroundMusicPlayer != null) {
			mBackgroundMusicPlayer.stop();
			mBackgroundMusicPlayer = null;
		}
		mHandler.removeMessages(PLAY_AD_IMAGE);
		mHandler.removeMessages(PLAY_IMAGE);
		mHandler.removeMessages(PLAY_BG_MUSIC_IMAGE);
		mHandler.removeMessages(PLAY_MUSIC);
	}

	// 暂停
	public void pause(boolean showIcon) {

		mPlayWhat = PlayWhat.PAUSE;

		if (mVideoView != null) {
			mVideoView.pause();
		}
		if (mMediaPlayer != null) {
			mMediaPlayer.pause();
		}
		if (mBackgroundMusicPlayer != null) {
			mBackgroundMusicPlayer.pause();
		}
		// 暂停时，停止播放广告，图片，背景音乐，音乐。
		mHandler.removeMessages(PLAY_AD_IMAGE);
		mHandler.removeMessages(PLAY_IMAGE);
		mHandler.removeMessages(PLAY_BG_MUSIC_IMAGE);
		mHandler.removeMessages(PLAY_MUSIC);

		// 显示图标
		if (showIcon) {
			showPlayStateIcon(IconState.PAUSE);
		}
	}

	// 播放下一曲
	private void playNext(boolean previous) {
		Log.e(TAG, "playNext...mPlayWhat=" + mPlayWhat + "; mPrePlayWhat=" + mPrePlayWhat);
		stopPlay();

		mPlayWhat = mPrePlayWhat;
		switch (mPlayWhat) {
			case AD_IAMGE:
			case AD_VIDEO:
				playNextAd();
				break;
			case NORMAL_IAMGE:
			case NORMAL_MUSIC:
			case NORMAL_VIDEO:
				playNextNormalMedia(false, true);
				break;
			case BG_MUSIC_IMAGE:
				playNextBgMusicImage(false);
				playNextBgMusic(false);
				break;
			default:
				break;
		}

		if (!previous) {
			// 显示图标
			showPlayStateIcon(IconState.NEXT);
		}
	}

	private void playPrevious() {

		Log.e(TAG, "playPrevious...mPlayWhat=" + mPlayWhat + "; mPrePlayWhat=" + mPrePlayWhat);
		mPreviousMedia = true;
		playNext(true);
		showPlayStateIcon(IconState.PREVIOUS);
	}

	private void fastForward() {
		Log.e(TAG, "fastForward...mPlayWhat=" + mPlayWhat + "; mPrePlayWhat=" + mPrePlayWhat);
		mHandler.removeMessages(PLAY_AD_IMAGE);
		mHandler.removeMessages(PLAY_IMAGE);
		mHandler.removeMessages(PLAY_BG_MUSIC_IMAGE);
		switch (mPlayWhat) {
			case AD_IAMGE:
				// playNextAd();
				break;
			case NORMAL_IAMGE:
				break;
			case BG_MUSIC_IMAGE:
				break;
			case NORMAL_MUSIC:
				if (mMediaPlayer != null) {
					int duration = mMediaPlayer.getCurrentPosition() + Utils.FAST_FORWARD_OR_BACK_STEP;
					mMediaPlayer.pause();
					mMediaPlayer.seekTo(duration);
				}
				break;
			case NORMAL_VIDEO:
			case AD_VIDEO:
				if (mVideoView != null) {
					int duration = mVideoView.getCurrentPosition() + Utils.FAST_FORWARD_OR_BACK_STEP;
					mVideoView.pause();
					mVideoView.seekTo(duration);
				}
				break;
			default:
				break;
		}
	}

	private void fastBack() {
		mHandler.removeMessages(PLAY_AD_IMAGE);
		mHandler.removeMessages(PLAY_IMAGE);
		mHandler.removeMessages(PLAY_BG_MUSIC_IMAGE);
		switch (mPlayWhat) {
			case AD_IAMGE:
				mPreviousMedia = true;
				break;
			case NORMAL_IAMGE:
				mPreviousMedia = true;
				playMedia(true, false, false, mCurrentAdIndex);
				break;
			case BG_MUSIC_IMAGE:
				mPreviousMedia = true;
				break;
			case NORMAL_MUSIC:
				if (mMediaPlayer != null) {
					int duration = mMediaPlayer.getCurrentPosition() - Utils.FAST_FORWARD_OR_BACK_STEP;
					mMediaPlayer.pause();
					mMediaPlayer.seekTo(duration);
				}
			case NORMAL_VIDEO:
			case AD_VIDEO:
				if (mVideoView != null) {
					int duration = mVideoView.getCurrentPosition() - Utils.FAST_FORWARD_OR_BACK_STEP;
					mVideoView.pause();
					mVideoView.seekTo(duration);
				}
				break;
			default:
				break;
		}
	}

	// 下一个插播
	private void playNextAd() {
		mCurrentAdIndex++;
		if (mCurrentAdIndex > mMaxAdCount) {
			Loger.print(getResources().getString(R.string.ad_play_finish));
			getLastPlayState();
			startAdPlayerTimer(); // 启动下一个插播
			////add by cheniqang
			mAdvertisingView.emptyVideoView();
			createViewPager(true, false, mBackgroundMusic, mMediaList);
			///add by chenqiang
			ifShowCaption();
			return;
		}

		if (mPreviousMedia) {
			mLastAdPosition--;
			if (mLastAdPosition < 0) {
				mLastAdPosition = mAdMediaList.size() - 1;
			}
		} else {
			mLastAdPosition++;
			if (mLastAdPosition >= mAdMediaList.size()) {
				mLastAdPosition = 0;
			}
		}

		mPreviousMedia = false;
		// 记录最后一个广告的位置，因为广告插播是要接着上次播放的后面来的。
		mPrefs.edit().putInt("last_ad_position", mLastAdPosition).commit();
		playMedia(false, false, true, mLastAdPosition);
	}

	// 播放下一个普通媒体
	private void playNextNormalMedia(final boolean isFromAdOrSetup, final boolean keyEvent) {
		Timer timer = new Timer();
		if (mMediaList == null || mMediaList.size() <= 0) {
			showToast(getResources().getString(R.string.no_media_file));
			startIdleActivity();
			return;
		}

		if (isFromAdOrSetup) {
			// 播放
			if (mMediaPosition < 0 || mMediaPosition >= mMediaList.size()) {
				mMediaPosition = 0;
			}
			///++++++++++++++++++++++
			if (mAutoScrollTextView != null) mAutoScrollTextView.setVisibility(View.VISIBLE);
			///++++++++++++++++++++++

			playMedia(keyEvent, isFromAdOrSetup, false, mMediaPosition);
		} else {
			// 用户按键切换下一曲或者非单曲循环时播放，下标++
			if (keyEvent || !SetupUtils.playSingleMedia(this)) {
				if (mPreviousMedia) {
					mMediaPosition--;
					// 如果位置小于0 就让其跳到最后。
					if (mMediaPosition < 0) {
						mMediaPosition = mMediaList.size() - 1;
					}
				} else {
					mMediaPosition++;
					// 如果位置大于总长度，就让位置归零。
					if (mMediaPosition >= mMediaList.size()) {
						mMediaPosition = 0;
					}
				}
			}
			mPreviousMedia = false;
			if (SetupUtils.serialPortIndex(this) != 2) {
				if (SetupUtils.serialPortIndex(this) == 1) {
					mHandler.sendEmptyMessage(START_SEND_PATH);
					// 延迟1.5秒
					mHandler.sendEmptyMessageDelayed(SEND_PATH_AGAIN, 1000); }
				mSendSerialPortPath = mMediaList.get(mMediaPosition).getPath().substring(5,
						mMediaList.get(mMediaPosition).getPath().length());
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									playMedia(keyEvent, isFromAdOrSetup, false, mMediaPosition);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});
					}
				}, 50);
			}
		}
	}

	private String[] fileList(String presentPlayPath) {

		String[] files = new File(presentPlayPath).list();
		return files;
	}

	private class ReadThread extends Thread {

		@Override
		public void run() {
			super.run();
			while (!isInterrupted()) {
				int size;
				String playPath;
				String fileName;
				try {
					byte[] buffer = new byte[128];
					mSerialPortIntputStream = (FileInputStream) mSerialPort.getInputStream();
					if (mSerialPortIntputStream == null)
						return;
					size = mSerialPortIntputStream.read(buffer);
					mReceiveSerialPortPath = "/mnt/" + new String(buffer, 0, size);
					if (size > 0 && (!mReceiveSerialPortPath.equals(lastReceivePath))) {
						lastReceivePath = mReceiveSerialPortPath;
						playPath = mReceiveSerialPortPath.substring(0, mReceiveSerialPortPath.lastIndexOf("/") + 1);
						fileName = mReceiveSerialPortPath.substring(mReceiveSerialPortPath.lastIndexOf("/") + 1,
								mReceiveSerialPortPath.length());
						if (Arrays.asList(fileList(playPath)).contains(fileName)) {
							String format = mReceiveSerialPortPath
									.substring(mReceiveSerialPortPath.length() - 3, mReceiveSerialPortPath.length())
									.toUpperCase();
							mSerialFormatList.add(format);
							if (format.equals("PEG") || format.equals("JPG") || format.equals("BMP")
									|| format.equals("PNG")) {
								mHandler.sendEmptyMessage(RECEIVE_SERIAL_PORT_IMAGE_PATH);
							} else {
								mHandler.sendEmptyMessage(RECEIVE_SERIAL_PORT_VIDEO_PATH);
							}
						}
					} else if (isSingleMedia) {
						// 播放列表只有一个文件
						// if (System.currentTimeMillis() -
						// mPrefs.getLong("last_receive_time", 0) > 4000
						// || System.currentTimeMillis() -
						// mPrefs.getLong("last_receive_time", 0) < 0) {
						// mHandler.sendEmptyMessage(RECEIVE_SERIAL_PORT_VIDEO_PATH);
						// mPrefs.edit().putLong("last_receive_time",
						// System.currentTimeMillis()).commit();
						// }
					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					// TODO: handle exception
				}
			}
		}
	}

	private void initSerialPort() {

		if (SetupUtils.serialPortIndex(this) != 0) {
			try {
				mSerialPort = new SerialPort(new File(SERIAL_PORT_DEVICE_PATH), PORT_RATE);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void outPutSerialPortStream(String outPath) {
		try {
			mSerialPortOutputStream = (FileOutputStream) mSerialPort.getOutputStream();
			byte[] bytes = outPath.getBytes();
			mSerialPortOutputStream.write(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	private void playVideoByReceivePath(String path) {
		if (mSerialFormatList.size() >= 2) {
			mLastFormat = mSerialFormatList.get(mSerialFormatList.size() - 2);
		} else {
			mLastFormat = mSerialFormatList.get(mSerialFormatList.size() - 1);
		}

		// 效率有问题，并不是每次都要重绘界面。
		// 可能不用STOP,具体要测试。
		// stopPlay();
		// mMediaPlayer.release();
		if (mReceiveSerialPortPath != ""
				&& (mLastFormat == "PNG" || mLastFormat == "BMP" || mLastFormat == "JPG" || mLastFormat == "PEG")) {
			mMediaPlayer.release();

			mVideoView = mAdvertisingView.CreateVideoView(mHandler, Utils.WIDTH_PIXELS, Utils.HEIGHT_PIXELS, 0, 0, -1);
		}

		if (mVideoView == null) {
			mVideoView = mAdvertisingView.CreateVideoView(mHandler, Utils.WIDTH_PIXELS, Utils.HEIGHT_PIXELS, 0, 0, -1);
		}

		try {
			mVideoView.setVideoPath(mReceiveSerialPortPath);
			mVideoView.setScales(SetupUtils.getVideoScale(PlayViewActivity.this));
			mReceiveSerialPortPath = "";
		} catch (Exception e) {
			// TODOAuto-generated catch block
			e.printStackTrace();
		}
	}

	private void getBitmapByPath(String path, boolean isLargeFlg) {
		// 效率有问题，并不是每次都要stopPlay。
		stopPlay();
		ArrayList<View> viewss = new ArrayList<View>();
		mPlayWhat = PlayWhat.STOP;
		mAdvertisingView.emptyVideoView();
		mAdvertisingView.emptyLayout();
		SXImageView sximageView = new SXImageView(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		sximageView.setLayoutParams(params);
		BitmapFactory.Options ops = new BitmapFactory.Options();
		ops.inJustDecodeBounds = true;
		ops.inSampleSize = 2;
		ops.inPreferredConfig = Config.RGB_565;
		int oHeight = ops.outHeight;
		int oWidth = ops.outWidth;
		int contentHeight = 0;
		int contentWidth = 0;
		if (isLargeFlg) {
			contentHeight = 1080;
			contentWidth = 1920;
		} else {
			contentHeight = 1024;
			contentWidth = 768;
		}
		if (((float) oHeight / contentHeight) < ((float) oWidth / contentWidth)) {
			ops.inSampleSize = (int) Math.ceil((float) oWidth / contentWidth);
		} else {
			ops.inSampleSize = (int) Math.ceil((float) oHeight / contentHeight);
		}
		ops.inJustDecodeBounds = false;
		Bitmap bm = BitmapFactory.decodeFile(path, ops);
		sximageView.setImageBitmap(bm);
		if (mPlayPhotoFullScreen) {
			sximageView.setScaleType(ScaleType.FIT_XY);
		}
		viewss.add(sximageView);
		mNormalAutoViewPager = mAdvertisingView.CreateAutoPager(Utils.WIDTH_PIXELS, Utils.HEIGHT_PIXELS, 0, 0);
		mNormalAutoViewPager.setData(viewss);
		mNormalAutoViewPager.setVisibility(View.VISIBLE);
		mAutoViewPager = mNormalAutoViewPager;
		bm = null;
	}

	// 播放的逻辑
	private void playMedia(boolean keyEvent, boolean isFromAdOrSetup, final boolean isAd, int mediaIndex) {
		Log.e(TAG, "playMedia...isFromAdOrSetup=" + isFromAdOrSetup + "; isAd=" + isAd + "; mediaIndex=" + mediaIndex);
		// stopPlay();
		DetailsInfo detailsInfo = null;
		if (isAd) { // 获取广告的下标
			detailsInfo = mAdMediaList.get(mediaIndex);
		} else { // 获取普通媒体的下标（包括视频、图片、声音。。）
			detailsInfo = mMediaList.get(mediaIndex);
		}
		switch (detailsInfo.getType()) {

			case VIDEO:
				try {
					if (mPrePlayWhat == PlayWhat.BG_MUSIC_IMAGE || mPrePlayWhat == PlayWhat.NORMAL_MUSIC || mPrePlayWhat == PlayWhat.AD_IAMGE || mPrePlayWhat == PlayWhat.AD_VIDEO || mPrePlayWhat == PlayWhat.NORMAL_IAMGE
							|| mPrePlayWhat == PlayWhat.NULL_STATE || mVideoView == null) {
						//	Log.d("232432", "youmeiyou");
						mAutoViewPager.hideViewPager(View.INVISIBLE, false);
						mAdvertisingView.emptyVideoView();

						//	mAdvertisingView.emptyLayout();
						mVideoView = mAdvertisingView.CreateVideoView(mHandler, Utils.WIDTH_PIXELS, Utils.HEIGHT_PIXELS, 0,
								0, -1);
					}

					if (SetupUtils.serialPortIndex(this) != 2) {
						mVideoView.setVideoPath(detailsInfo.getPath());
						mVideoView.setScales(SetupUtils.getVideoScale(PlayViewActivity.this));
					} else {
						try {
							mVideoView.setVideoPath(mReceiveSerialPortPath);
							mVideoView.setScales(SetupUtils.getVideoScale(PlayViewActivity.this));
						} catch (Exception e) {
							// TODOAuto-generated catch block e.printStackTrace();
						}
					}

					if (isAd) {
						mPrePlayWhat = mPlayWhat = PlayWhat.AD_VIDEO;
						RecorderLoger.print(getString(R.string.ad_video) + mAdMediaList.get(mediaIndex).getPath());
					} else {
						mPrePlayWhat = mPlayWhat = PlayWhat.NORMAL_VIDEO;
						RecorderLoger.print(getString(R.string.play_video) + mMediaList.get(mediaIndex).getPath());
						if (isFromAdOrSetup) {
							mVideoView.seekTo(mMediaDuration);
						}
					}

					DetailsInfo info = null;
					if (isAd) {
						if (mediaIndex + 1 >= mAdMediaList.size()) {
							info = mAdMediaList.get(0);
						} else {
							info = mAdMediaList.get(mediaIndex + 1);
						}
					} else {
						if (mediaIndex + 1 >= mMediaList.size()) {
							info = mMediaList.get(0);
						} else {
							info = mMediaList.get(mediaIndex + 1);
						}
					}

					// 判断下一个是否是视频

					if (info.getType() != MediaType.VIDEO) {

						if (isAd) {
							if (mediaIndex + 1 >= mAdMediaList.size()) {
								mAutoViewPager.setIndex(mAdMediaPagerMap.get(0));
							} else {
								mAutoViewPager.setIndex(mAdMediaPagerMap.get(mediaIndex + 1));
							}
						} else {
							if (mediaIndex + 1 >= mMediaList.size()) {
								mAutoViewPager.setIndex(mMediaPagerMap.get(0));
							} else {
								mAutoViewPager.setIndex(mMediaPagerMap.get(mediaIndex + 1));
							}
						}
						mAutoViewPager.switchNextView(false, null);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case PHOTO:
				try {
					mAdvertisingView.emptyVideoView();
					mAutoViewPager.hideViewPager(View.VISIBLE, false);
					if (isAd) {// gua
						mAutoViewPager.hideViewPager(View.VISIBLE, true);
						mAutoViewPager.setIndex(mAdMediaPagerMap.get(mediaIndex));
						Log.e(TAG,
								"switch ad photo...mAdMediaPagerMap.get(mediaIndex)=" + mAdMediaPagerMap.get(mediaIndex));
						mPrePlayWhat = mPlayWhat = PlayWhat.AD_IAMGE;
						RecorderLoger.print(getString(R.string.ad_image) + mAdMediaList.get(mediaIndex).getPath());
						//mHandler.removeMessages(PLAY_AD_IMAGE);
						mHandler.sendEmptyMessageDelayed(PLAY_AD_IMAGE, mPlayPhotoTime * 1000);
					} else {

						mAutoViewPager.setIndex(mMediaPagerMap.get(mediaIndex));
						Log.e(TAG,"switch normal photo..mMediaPagerMap.get(mediaIndex)=" + mMediaPagerMap.get(mediaIndex));
						mPrePlayWhat = mPlayWhat = PlayWhat.NORMAL_IAMGE;
						RecorderLoger.print(getString(R.string.play_image) + mMediaList.get(mediaIndex).getPath());
						if(SetupUtils.playSingleMedia(PlayViewActivity.this)){
							mAutoViewPager.setFirstImage();
							break;
						}

						//mHandler.removeMessages(PLAY_IMAGE);
						//mHandler.sendEmptyMessageDelayed(PLAY_IMAGE,mPlayPhotoTime * 1000);
					}

					//if(mFirst || keyEvent){
					if(mFirst){
						mFirst = false;
						mAutoViewPager.setFirstImage();
						mHandler.sendEmptyMessageDelayed(PLAY_IMAGE, mPlayPhotoTime * 1000);
					}else{
						mAutoViewPager.setAnimationIndex(mSetupAnimationIndex);
						mAutoViewPager.switchNextView(true, new SwitchNextImageCallback() {
							@Override
							public void onSwitchCompleted() {
								// TODO Auto-generated method stub
								if (isAd) {
									mHandler.removeMessages(PLAY_AD_IMAGE);
									mHandler.sendEmptyMessageDelayed(PLAY_AD_IMAGE, mPlayPhotoTime * 1000);
								} else {
									mHandler.removeMessages(PLAY_IMAGE);
									mHandler.sendEmptyMessageDelayed(PLAY_IMAGE, mPlayPhotoTime * 1000);
								}
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case MUSIC:
				try {
					mAdvertisingView.emptyVideoView();
					mAutoViewPager.setIndex(mMediaPagerMap.get(mediaIndex));
					Log.e(TAG, "switch normal music..mMediaPagerMap.get(mediaIndex)="+mMediaPagerMap.get(mediaIndex));
					mAutoViewPager.switchNextView(false, null);
					mAutoViewPager.hideViewPager(View.VISIBLE, false);
					RecorderLoger.print(getString(R.string.play_music)+mMediaList.get(mediaIndex).getPath());
					mPrePlayWhat = mPlayWhat = PlayWhat.NORMAL_MUSIC;
					mAutoMusicView = (AutoMusicView) mAutoViewPager.getView(mMediaPagerMap.get(mediaIndex));
					playMusic(isFromAdOrSetup, mAutoMusicView.getPath());
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
		}
	}

	// 背景音乐模式：图片
	private void playNextBgMusicImage(boolean isFromAdOrSetup) {
		if (!isFromAdOrSetup) {
			if (mPreviousMedia) {
				mMediaPosition--;
				if (mMediaPosition < 0) {
					mMediaPosition = mMediaList.size() - 1;
				}
			} else {
				mMediaPosition++;
				if (mMediaPosition >= mMediaList.size()) {
					mMediaPosition = 0;
				}
			}
		}

		mPreviousMedia = false;
		mPrePlayWhat = mPlayWhat = PlayWhat.BG_MUSIC_IMAGE;

		if (mMediaPosition >= mMediaList.size() || mMediaPosition < 0) {
			mMediaPosition = 0;
		}
		mAutoViewPager.setIndex(mMediaPosition);
		mAutoViewPager.switchNextView(true, new SwitchNextImageCallback() {
			@Override
			public void onSwitchCompleted() {
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessageDelayed(PLAY_BG_MUSIC_IMAGE, mPlayPhotoTime * 1000);
			}
		});
	}

	// 播放下一个背景音乐
	private void playNextBgMusic(boolean isFromAdOrSetup) {
		if (mBGMusicList == null || mBGMusicList.size() <= 0) {
			return;
		}

		if (!isFromAdOrSetup) {
			mMusicPosition++;
			if (mMusicPosition >= mBGMusicList.size()) {
				mMusicPosition = 0;
			}
		}

		try {
			DetailsInfo detailsInfo = mBGMusicList.get(mMusicPosition);
			mBackgroundMusicPlayer = new MediaPlayer();
			Uri mediaUri = Uri.parse(detailsInfo.getPath());
			mBackgroundMusicPlayer.stop();
			mBackgroundMusicPlayer.reset();
			// 设置播放路径
			mBackgroundMusicPlayer.setDataSource(this, mediaUri);
			mBackgroundMusicPlayer.prepare();
			// mMediaBackgroundMusicPlayer.start();
			initBackgroundMusicListener(isFromAdOrSetup);
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

	// 视频播放
	private void playVideoComletion() {

		Log.e(TAG, "playVideoComletion....mPlayWhat =" + mPlayWhat + "; mPrePlayWhat=" + mPrePlayWhat);
		mPlayWhat = mPrePlayWhat;
		if (mPlayWhat == PlayWhat.AD_VIDEO) {
			playNextAd();
		} else if (mPlayWhat == PlayWhat.NORMAL_VIDEO) {
			playNextNormalMedia(false, false);
		}
	}

	// 监听音乐播放
	private void initMusicListener(final boolean isFromAdOrSetup) {
		if (mMediaPlayer != null) {
			mMediaPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer arg0) {
					if (mVideoView != null) {
						mVideoView.stopPlayback();
					}
					// Log.e(TAG,"initMusicListener...mMediaPlayer="+mMediaPlayer);
					if (mMediaPlayer == null)
						return;
					// Log.e(TAG,"isFromAdOrSetup=" + isFromAdOrSetup + ";
					// mMediaPlayer..mMusicDuration=" + mMusicDuration + "; or
					// duration=" + mMediaPlayer.getDuration());
					if (isFromAdOrSetup) {
						if (mMediaDuration < mMediaPlayer.getDuration()) {
							mMediaPlayer.start();
							mMediaPlayer.seekTo(mMediaDuration);
						} else {
							mMediaPlayer.start();
						}
					} else {
						mMediaPlayer.start();
					}
				}
			});

			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer player) {
					mMusicDuration = 0;
					if (mPlayWhat == PlayWhat.NORMAL_MUSIC) {
						playNextNormalMedia(false, false);
					}
				}
			});
		}
	}

	// 背景音乐
	private void initBackgroundMusicListener(final boolean isFromAdOrSetup) {
		if (mBackgroundMusicPlayer != null) {
			mBackgroundMusicPlayer.setOnPreparedListener(new OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer arg0) {
					Log.e(TAG,
							"initBackgroundMusicListener...isFromAdOrSetup=" + isFromAdOrSetup
									+ "; mMediaBackgroundMusicPlayer..mMediaDuration=" + mMediaDuration
									+ "; or duration=" + mBackgroundMusicPlayer.getDuration());
					if (isFromAdOrSetup) {
						if (mMusicDuration < mBackgroundMusicPlayer.getDuration()) {
							mBackgroundMusicPlayer.start();
							mBackgroundMusicPlayer.seekTo(mMusicDuration);
						} else {
							mBackgroundMusicPlayer.start();
						}
					} else {
						mBackgroundMusicPlayer.start();
					}
				}
			});

			// 背景音乐设置是否播放完毕监听。
			mBackgroundMusicPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer player) {
					mMusicDuration = 0;
					Log.e(TAG, "mMediaBackgroundMusicPlayer.setOnCompletionListener");
					playNextBgMusic(false);
				}
			});
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

	// 显示播放状态图标，包括暂停、播放、上一曲、下一曲、。。。
	private void showPlayStateIcon(IconState iconState) {
		if (mPlayIconTextView == null) {
			mPlayIconTextView = mAdvertisingView.CreatePlayIconTextView();
		}
		mPlayIconTextView.setText("");
		mPlayIconTextView.setCompoundDrawables(null, null, null, null);
		mPlayIconTextView.setVisibility(View.VISIBLE);
		Drawable drawble = null;
		mHandler.removeMessages(Utils.HIDE_PLAY_STATE_ICON);
		switch (iconState) {
			case PAUSE:
				drawble = getResources().getDrawable(R.drawable.key_pause);
				mPlayIconTextView.setText(getResources().getText(R.string.key_pause));
				mPlayIconTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawble);
				break;
			case PLAY:
				drawble = getResources().getDrawable(R.drawable.key_play);
				mPlayIconTextView.setText(getResources().getText(R.string.key_play));
				mPlayIconTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawble);
				mHandler.sendEmptyMessageDelayed(Utils.HIDE_PLAY_STATE_ICON, Utils.HIDE_PLAY_STATE_ICON_TIME);
				break;
			case NEXT:
				drawble = getResources().getDrawable(R.drawable.key_next);
				mPlayIconTextView.setText(getResources().getText(R.string.key_next));
				mPlayIconTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawble);
				mHandler.sendEmptyMessageDelayed(Utils.HIDE_PLAY_STATE_ICON, Utils.HIDE_PLAY_STATE_ICON_TIME);
				break;
			case PREVIOUS:
				drawble = getResources().getDrawable(R.drawable.key_pre);
				mPlayIconTextView.setText(getResources().getText(R.string.key_pre));
				mPlayIconTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawble);
				mHandler.sendEmptyMessageDelayed(Utils.HIDE_PLAY_STATE_ICON, Utils.HIDE_PLAY_STATE_ICON_TIME);
				break;
			case FAST_FAWORD:
				drawble = getResources().getDrawable(R.drawable.key_fast_forward);
				mPlayIconTextView.setText(getResources().getText(R.string.key_fast_word));
				mPlayIconTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawble);
				break;
			case FAST_BACK:
				drawble = getResources().getDrawable(R.drawable.key_fast_back);
				mPlayIconTextView.setText(getResources().getText(R.string.key_fast_back));
				mPlayIconTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawble);
				break;
			case NULL:
			default:
				mPlayIconTextView.setText("");
				mPlayIconTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
				mPlayIconTextView.setVisibility(View.INVISIBLE);
				break;
		}
	}

	// 播放滚动字幕，区别于自定义滚动字幕
	private void createAutoScrollTextView() {

		try {
			if (mAutoScrollTextView.getVisibility() == View.INVISIBLE) {
				mAutoScrollTextView.setVisibility(View.VISIBLE);
			} else {
				mAutoScrollTextView.setVisibility(View.VISIBLE);
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

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
		// mAutoScrollTextView =
		// mAdvertisingView.CreateCustomTextView(Utils.WIDTH_PIXELS,
		// LayoutParams.WRAP_CONTENT, 0, -1,
		// captionSetting,Common.subMaxScrollText(buffer.toString()));


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

	// 更新音乐时间。mMediaPlayer是音乐播放的View
	// 这个音乐播放的背景是我们自己定义的，所以要更新时间显示。
	@SuppressLint("SimpleDateFormat")
	private void updateMusicTime() {
		if (mMediaPlayer != null && mAutoMusicView != null) {
			SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
			String playTime = formatter.format(mMediaPlayer.getCurrentPosition());
			String countTime = formatter.format(mMediaPlayer.getDuration());
			mAutoMusicView.updateMusicTime(playTime + "/" + countTime);
			mHandler.sendEmptyMessageDelayed(PLAY_MUSIC, 1000);
		}
	}

	private void removeTimerViews(String tag) {
		if (mAdvertisingView != null) {
			for (int i = 0; i < mAdvertisingView.getChildCount(); i++) {
				if (mAdvertisingView.getChildAt(i) instanceof TextView
						&& mAdvertisingView.getChildAt(i).getTag() != null
						&& mAdvertisingView.getChildAt(i).getTag().equals(tag)) {
					mAdvertisingView.removeViewAt(i);
					this.mTimerIconView = null;
					this.mPlayIconTextView = null;
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

	/**
	 * //删除滚动字幕，有问题。 //根本就删除不了，而且点击关闭字幕后还是会显示，并且这时候字幕调节失效。 private void
	 * removeAutoScollViews() { //如果 /*if(mAdvertisingView != null) { for(int
	 * i=0; i< mAdvertisingView.getChildCount(); i++) {
	 * if(mAdvertisingView.getChildAt(i) instanceof AutoCustomTextView) {
	 * Log.e(TAG, "removeAutoScollViews-----PlayViewActivity");
	 * mAdvertisingView.removeViewAt(i); mAutoScrollTextView = null; break; } }
	 * }
	 *
	 * try { //这个并不行。只把背景颜色设置成了透明，还需要把文字设置成透明。
	 * mAutoScrollTextView.setVisibility(View.INVISIBLE);
	 *
	 *
	 * } catch (Exception e) { // TODO: handle exception }
	 *
	 * }
	 */

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Loger.closePrint();
		RecorderLoger.closePrint();
	}

	// 插播计时器
	//private Timer mTimer = null;
	// 系统时间
//	private Timer mSystemTime = null;
	// 保存播放状态，每5秒保存一次。
//	private Timer mSavePlayStateTime = null;

	@Override
	protected void onPause() {
		super.onPause();

		Loger.closePrint();
		RecorderLoger.closePrint();

		try {
			saveLastPlayState();

			if (mTimer != null) {
				mTimer.cancel();
				mTimer = null;
			}

			if (mSystemTime != null) {
				mSystemTime.cancel();
				mSystemTime = null;
			}


			if (mSavePlayStateTime != null) {
				mSavePlayStateTime.cancel();
				mSavePlayStateTime = null;
			}

			if (mAutoViewPager != null) {
				mAutoViewPager = null;
			}

			if (mMediaMountedReceiver != null) {
				try {
					this.unregisterReceiver(mMediaMountedReceiver);
				} catch (IllegalArgumentException err) {
					return;
				} catch (Exception err) {
					return;
				}
			}
		} catch (Exception err) {
			return;
		}
		//  add by chenqiang 20160707  start: for stop Background music.
		stopPlay();
		//  end

		finish();
	}

	@Override
	protected void onStop() {
		super.onStop();

		finish();
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (canKeyUp) {
			Log.e(TAG, "onKeyUp..keyCode=" + keyCode);
			switch (keyCode) {
				// 快进键弹起时候，继续播放
				case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
					play();
					break;
				// 快退键抬起时继续播放
				case KeyEvent.KEYCODE_MEDIA_REWIND:
					play();
					break;
				default:
					break;
			}
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (DEBUG)
			Log.e(TAG, "onKeyDown..keyCode=" + keyCode);
		mSharedPre = getSharedPreferences("paw", 0);
		if ((System.currentTimeMillis() - mSharedPre.getLong("play_crt_tim", 0L)) < 0L && !timeErr) {
			checkStep1();
			timeErr = true;
		}

		if (!mSharedPre.getString("final_paw", "").equals("") && !isInput
				&& (System.currentTimeMillis() - mSharedPre.getLong("play_crt_tim", 0L)) >= 60 * 1000) {
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
				|| ((System.currentTimeMillis() - mSharedPre.getLong("play_crt_tim", 0L)) < 60 * 1000)
				&& System.currentTimeMillis() - mSharedPre.getLong("play_crt_tim", 0L) > 0) {
			canKeyUp = true;
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
					//Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, i);
					//SystemProperties.set(PROP_DISPLAY_ROTATE, 90 * i + "");
					this.recreate();
					break;
				case KeyEvent.KEYCODE_MENU:
					Intent intent = new Intent(PlayViewActivity.this, SetupActivity.class);
					startActivity(intent);
					break;
				case KeyEvent.KEYCODE_MEDIA_STOP:
					// Intent intent = new Intent(PlayViewActivity.this,
					// SetupActivity.class);
					// startActivity(intent);
					break;
				case KeyEvent.KEYCODE_BACK:
					/*
					 * if ((System.currentTimeMillis() - mExitTime) > 2000){
					 * showToast(getResources().getString(R.string.back_key_again));
					 * mExitTime = System.currentTimeMillis(); }else{
					 * ExitApplication.getInstance().exit(); this.finish(); }
					 */

					/////////////////  back 键退出apk
					// 	showExitDialog();
					break;
				//	case 23: // enter
				case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
					Log.e(TAG, "play_pause...isplaying=" + isPlaying());
					if (isPlaying()) {
						pause(true);
						// 暂停广告插播
						stopADTimerTask();
					} else {
						play();
						startAdPlayerTimer(); // 启动下一个插播
					}
					break;
				case 23:  // enter 键
					///////////SLEEP_START BY CHEN
					//  PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
					//  pm.goToSleep(SystemClock.uptimeMillis());
					//	 this.finish();

					//	 AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
					//    if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
					//       audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
					//    } else {
					//       audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
					//    }

					// DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
					//      devicePolicyManager.lockNow();

					// PowerManager.WakeLock  wakeLock = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "TAG");
					//     wakeLock.acquire();
					//    wakeLock.release();
					/////////////SLEEP_END  BY CHEN
					break;
				case 120:  // copy键
					//   added by chenqiang 20160705 ------start---------
					//	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
					//	pm.goToSleep(SystemClock.uptimeMillis());
					// this.finish();
					// ---------end----------
					break;
				case KeyEvent.KEYCODE_MEDIA_NEXT:
					playNext(false);
					break;
				case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
					playPrevious();
					break;
				case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
					fastForward();
					// 显示图标
					showPlayStateIcon(IconState.FAST_FAWORD);
					break;
				case KeyEvent.KEYCODE_MEDIA_REWIND:
					fastBack();
					showPlayStateIcon(IconState.FAST_BACK);
					break;
				default:
					return false;
			}
		}

		return true;
	}

	private void checkStep1() {
		initPopupWindow(R.layout.check_password_pop_layout);
		mHandler.sendEmptyMessageDelayed(AUTO_HIDE_POP, 15 * 1000);
		isInput = true;
		canKeyUp = false;
		checkEditText.setFocusable(true);
	}

	private boolean checkPassword() {
		String inputString = checkEditText.getText().toString();
		return mSharedPre.getString("final_paw", "").equals(inputString);
	}

	private void initPopupWindow(int toastLayoutId) {
		if (popupWindow != null) {
			popupWindow.dismiss();
		}
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

	@Override
	public void setupCallback(int index) {
		Log.e(TAG, "startSetupActivity...setupIndex=" + index);
		saveLastPlayState();
		stopPlay();
	}

	@Override
	public void playFinished(PlayModel model) {
		// playBackgroundMusic();
	}

	/**
	 * SD卡或者USB插入监听 当根目录下存在COPY.txt时才拷贝
	 * 拷贝的原则是有Playlist文件夹就只拷贝Playlist文件夹，没有的话就拷贝根目录下的内容。
	 */
	private BroadcastReceiver mMediaMountedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				String path = intent.getData().getPath();
				if (path.contains("usbhost")) {
					Log.e(TAG, "Media mounted! path = " + path);
					Message msg = mHandler.obtainMessage();
					msg.obj = "Media mounted!  " + path;
					// msg.what = SHOW_TOAST_FOR_TEST;
					mHandler.sendMessage(msg);
					if (path.endsWith("/")) {
						Utils.USB_ROOT_PATH = path;
					} else {
						Utils.USB_ROOT_PATH = path + "/";
					}

					Utils.USB_PATH = Utils.USB_ROOT_PATH + Utils.PLAY_LIST_DIR;
					Log.e(TAG, "usb root path = " + Utils.USB_ROOT_PATH + "; usb path =" + Utils.USB_PATH);
				}

				boolean isMediaCard = false;
				Intent i = new Intent(PlayViewActivity.this, CopyActivity.class);

				// 获取当前播放设备目录
				SharedPreferences mSharedPre = getSharedPreferences("basic_menu", 0);
				int storageIndex = mSharedPre.getInt("present_equip_index", 0);
				// 如果插入的是SD卡，并且此时菜单里面选择的播放设备也是SD卡，就不拷贝。直接加载数据。
				if (path.contains("extsd") && storageIndex == 1) {
					// startPlayerActivity();
					loadData();
					return;
					// 如果插入的是U盘，并且此时菜单里面选择的播放设备也是U盘，就不拷贝。直接加载数据。
				} else if (path.contains("usbhost") && storageIndex == 2) {
					// startPlayerActivity();
					loadData();
					return;
				}

				File copyFile = null;
				File playListDir = null;
				if (path.contains("external_sd")) {
					isMediaCard = true;
					copyFile = new File(Utils.SD_ROOT_PATH + Utils.COPY_FILE);
					playListDir = new File(Utils.SD_ROOT_PATH + Utils.PLAY_LIST_DIR);
					if (playListDir != null && playListDir.exists()) {// playlist文件夹存在
						Log.e(TAG, "mMediaMountedReceiver playlist exist! from path =" + Utils.SD_PATH);
						i.putExtra("from_root_path", Utils.SD_PATH);
					} else {// playlist文件夹不存在
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
						PlayViewActivity.this.finish();
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