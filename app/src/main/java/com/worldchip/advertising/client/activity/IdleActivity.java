package com.worldchip.advertising.client.activity;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.worldchip.advertising.client.application.ExitApplication;
import com.worldchip.advertising.client.entity.DetailsInfo;
import com.worldchip.advertising.client.utils.Common;
import com.worldchip.advertising.client.utils.ISetupCallback;
import com.worldchip.advertising.client.utils.Loger;
import com.worldchip.advertising.client.utils.MediaType;
import com.worldchip.advertising.client.utils.SetupUtils;
import com.worldchip.advertising.client.utils.Utils;
import com.worldchip.advertisingclient.R;

import android.app.admin.DevicePolicyManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

//import android.view.DisplayManagerAw;
//import android.os.IPowerManager;
import android.os.RemoteException;
//import android.os.ServiceManager;
import android.os.SystemClock;
import android.content.ContentResolver;
import android.provider.Settings;
//import android.os.SystemProperties;

public class IdleActivity extends Activity implements ISetupCallback {

	private static final String TAG = "--IdleActivity--";

	protected static final int SHOW_TOAST_FOR_TEST = 8;
	private static final int SETUP_REQUEST = 9;
	private static final int START_PLAYER_ACTIVITY = 11;
	protected static final int SYSTEM_TIMER_RUNNING = 12;
	/* 从idleActivity进入播放，有0.05秒的延时，如果觉得时间太短，可以稍微延长 */
	private static final int DELAY_TIME = 5;

	private static final int PRESS_UP = 19;
	private static final int PRESS_DOWN = 20;
	private static final int PRESS_LEFT = 21;
	private static final int PRESS_RIGHT = 22;
	private static final int PRESS_DEL = 23;
	private static final int AUTO_HIDE_POP = 888;

	private Timer mSystemTime = null;

	protected static final int SET_BACKGROUD = 0;
	private static final int NET_CONNECT = SET_BACKGROUD + 1;
	private static final int NET_UNCONNECT = NET_CONNECT + 1;
	private List<DetailsInfo> mMediaDetailsInfos = new ArrayList<DetailsInfo>();
	/* 默认分屏模式为不分屏 */
	private int mWindowMode = 0;

	private RelativeLayout mMainLayout;

	private SharedPreferences mPrefSetup = null;
	// 存密码
	private SharedPreferences mSharedPre = null;
	private boolean mRunning = true;

	// 时间日期格式
	private SimpleDateFormat mTimeFormat = null;
	private SimpleDateFormat mDateFormat = null;

	/**
	 * mTvSystemTime 显示当前系统时间 mTvSystemDate 显示系统当前日期
	 */
	private TextView mTvSystemTime;
	private TextView mTvSystemDate;

	/**
	 * mTvCurrentStorage 在idleActivity界面上显示当前存取其名称 mTvStorageSize 当前存储器的总大小
	 * mTvStorageUse 当前存储器的使用大小
	 */
	private TextView mTvCurrentStorage;
	private TextView mTvStorageSize;
	private TextView mTvStorageUse;

	/* 显示设备ID */
	private TextView mTvDeviceId;
	/* 显示软件版本号 */
	private TextView mTvSystemVersion;
	private TextView mTvDisplayGpu;

	private PopupWindow popupWindow;
	// EditText checkEditText =
	// (EditText)popupWindow.getContentView().findViewById(R.id.check_password_pop_edittext);
	EditText checkEditText;
	private View layout;
	boolean isInput = false;
	boolean isRight = false;
	long currentTime;
	private boolean timeErr;
	private Resources mRes;


	private static final String PROP_DISPLAY_ROTATE = "persist.sys.displayrot";


	private final static int ROATION = 0xfe;
	private Timer checkIOTimer;
	private boolean isRoationChange = false;
	private static final char PORT_TYPE = 'B';
	private static final int PORT_NUM_20 = 20;
	private static final int PORT_NUM_21 = 21;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SetupUtils.setupSystemLangue(this);
		setContentView(R.layout.idle_layout);
		// 开机将音量调成最大。
		//	AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
		//	if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) < audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC))
		//		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

		mRes = getResources();
		// checkEditText = (EditText)popupWindow.findviewbyid
		try {

			mMainLayout = (RelativeLayout) findViewById(R.id.main_layout);
			this.getSharedPreferences("advertis_setting", 0);
			mPrefSetup = this.getSharedPreferences("advertis_setup", 0);
			initView();
			// added by guofq 20151006
			// startPlayerActivity();
			// startAutoUpdateService(this);

			ExitApplication.getInstance().addActivity(this);

			configurationOrientation();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			writeFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	// 每隔5s监测IO口。
	private void startCheckRotion() {
		checkIOTimer = new Timer();
		checkIOTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				//checkAndAutoRotation();
                Log.d("zhang","Not support rotation!!!");
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

	// 写文件
	public void writeFile() throws IOException {
		String write_str = "0";
		File file = new File("/data/misc/user/0/delay_boot.txt");
		if (!file.exists()) {

			FileOutputStream fos = new FileOutputStream(file);
			byte[] bytes = write_str.getBytes();
			fos.write(bytes);
			fos.close();
		}

	}


	private void initView() {
		mTvCurrentStorage = (TextView) findViewById(R.id.storage_name);
		mTvStorageSize = (TextView) findViewById(R.id.storage_size);
		mTvStorageUse = (TextView) findViewById(R.id.storage_user_size);

		mTvSystemTime = (TextView) findViewById(R.id.current_time);
		mTvSystemDate = (TextView) findViewById(R.id.current_date);
		mTimeFormat = SetupUtils.getDateFormatForLangue(this, "HH:mm");
		mDateFormat = SetupUtils.getDateFormatForLangue(this, "yyyy/MM/dd E");

		mTvDeviceId = (TextView) findViewById(R.id.device_id);
		mTvDeviceId.setText("ID: " + getSharedPreferences("id_prefer", 0).getString("id_text", "000000"));

		/*String version = "";
		PackageManager pm = this.getPackageManager();// context为当前Activity上下文
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(this.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		version = pi.versionName;*/

		mTvSystemVersion = (TextView) findViewById(R.id.system_version);
		mTvSystemVersion.setText(getResources().getString(R.string.soft_version) + getResources().getString(R.string.version));
		//Display verion by zhang
		mTvDisplayGpu = (TextView) findViewById(R.id.system_gpu);
		mTvDisplayGpu.setText(getResources().getString(R.string.firmware_version) + Build.VERSION.INCREMENTAL.substring(0,16));
	}

	// private void startAutoUpdateService(Context context) {
	// Intent i= new Intent(context, AutoUpdateService.class);
	// context.startService(i);
	// }

	@Override
	protected void onResume() {
		SetupUtils.setupSystemLangue(this);
		super.onResume();
		if (SetupUtils.isAutoRotation(this)) {
			startCheckRotion();
		}
		Loger.openPrint(this);

		Utils.AUTO_UPDATING = false;
		mRunning = true;

		loadData();
	}

	private void loadData() {
		long sumSize = 0;
		long useSize = 0;
		// 设置根目录（哪个存储器优先）
		int storageIndex = SetupUtils.setCurrentRootPath(this);
		if (storageIndex == 0) {
			mTvCurrentStorage.setText(getResources().getString(R.string.menu_storage_text));
			sumSize = Common.readBlockSize("/mnt/sdcard", 0);
			useSize = Common.readBlockSize("/mnt/sdcard", 2);
			Loger.print("当前优先播放设备: 内部存储。");
		} else if (storageIndex == 1) {
			mTvCurrentStorage.setText(getResources().getString(R.string.menu_sd_text));
			File f1 = new File(Utils.SD_ROOT_PATH);
			//by zhang
			if (f1.exists()) {
				sumSize = Common.readBlockSize(Utils.SD_ROOT_PATH, 0);
				useSize = Common.readBlockSize(Utils.SD_ROOT_PATH, 2);
			}
			Loger.print("当前优先播放设备: 外部TF卡存储。");
		} else {
			mTvCurrentStorage.setText(getResources().getString(R.string.menu_usb_text));
			Log.d("zhang",android.os.Build.MODEL);
			File f1 = new File(Utils.USB_ROOT_PATH);
            //by zhang
			if (f1.exists()){
				sumSize = Common.readBlockSize(Utils.USB_ROOT_PATH, 0);
				useSize = Common.readBlockSize(Utils.USB_ROOT_PATH, 2);
			}
			Loger.print("当前优先播放设备: 外部USB存储。");
		}

		mTvStorageSize.setText(
				String.format(getResources().getString(R.string.storage_sum_size), (sumSize / 1024 / 1024) + "GB", 1));
		mTvStorageUse.setText(
				String.format(getResources().getString(R.string.storage_user_size), (useSize / 1024 / 1024) + "GB", 1));
		// 从preference里面获取ID号
		//mTvDeviceId.setText("ID: " + mPrefSetup.getString("id_value", "000000"));

		//mTvDeviceId.setText("ID: "+ mPrefSetup.getString("id_value", "000000"));

		//mTvSystemVersion.setText(getResources().getString(R.string.version));

		startSystemTime();
		setScreenOrientation();
		startMediaUnMountedReceiver();
		setBackground(Utils.IS_PORT);
		mHandler.sendEmptyMessageDelayed(SET_BACKGROUD, 2 * 1000);

		// MediaType.ALL
		mMediaDetailsInfos = Common.loadData(this, MediaType.ALL);
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);  ///     BY chenqiang 20160704


		/* DELAY_TIME=1*1000，延时1秒进入 */
		if (mMediaDetailsInfos != null && mMediaDetailsInfos.size() > 0 &&  pm.isScreenOn()) {   // by chenqiang 20160704
			mHandler.sendEmptyMessageDelayed(START_PLAYER_ACTIVITY, DELAY_TIME);
		}
	}

	/* 通过比较横竖分辨率，来判断是否转屏 */
	private void setScreenOrientation() {
		DisplayMetrics mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		Utils.WIDTH_PIXELS = mDisplayMetrics.widthPixels;
		Utils.HEIGHT_PIXELS = mDisplayMetrics.heightPixels;
		if (Utils.WIDTH_PIXELS > Utils.HEIGHT_PIXELS) {
			Utils.IS_PORT = false;
		} else {
			Utils.IS_PORT = true;
		}
	}

	/* 启动媒体卸载监听 */
	private void startMediaUnMountedReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.setPriority(2147483647);
		intentFilter.addDataScheme("file");
		registerReceiver(mMediaMountedReceiver, intentFilter);
	}

	/**
	 * 通过Handler处理一些消息 CLOSE_SETUP_POPUP 关闭弹出的菜单 public static final int
	 * CLOSE_SETUP_POPUP = 100;
	 */
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case START_PLAYER_ACTIVITY:
					if (mRunning) {
						startPlayerActivity();
					}
					break;
				case SYSTEM_TIMER_RUNNING:
					updateSystemTimer();
					break;
				case SET_BACKGROUD:
					setBackground(Utils.IS_PORT);
					mHandler.sendEmptyMessageDelayed(SET_BACKGROUD, 2 * 1000);
					break;
				case NET_CONNECT:
					Toast.makeText(IdleActivity.this, "NetWork Connected!", Toast.LENGTH_LONG).show();
					break;
				case NET_UNCONNECT:
					Toast.makeText(IdleActivity.this, "NetWork Un Connected!", Toast.LENGTH_LONG).show();
					break;
				case SHOW_TOAST_FOR_TEST:
					Toast.makeText(IdleActivity.this, (msg.obj == null ? "" : msg.obj.toString()), Toast.LENGTH_LONG)
							.show();
					break;
				case PRESS_UP:
					checkEditText.append(mRes.getString(R.string.remote_key_up));
					autoCheck();
					break;
				case PRESS_DOWN:
					checkEditText.append(mRes.getString(R.string.remote_key_down));
					autoCheck();
					break;
				case PRESS_LEFT:
					checkEditText.append(mRes.getString(R.string.remote_key_left));
					autoCheck();
					break;
				case PRESS_RIGHT:
					checkEditText.append(mRes.getString(R.string.remote_key_right));
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

					IdleActivity.this.recreate();
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
				mSharedPre.edit().putLong("idle_crt_tim", currentTime).commit();
				isInput = false;
				Toast t = new Toast(IdleActivity.this);
				t.makeText(IdleActivity.this, getResources().getString(R.string.unlock_success), Toast.LENGTH_LONG).show();
			} else {
				Toast t = new Toast(IdleActivity.this);
				t.makeText(IdleActivity.this, getResources().getString(R.string.wrong_password), Toast.LENGTH_SHORT).show();
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

	private void configurationOrientation() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setBackground(false);
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setBackground(true);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		configurationOrientation();
	}

	/* 设置背景，为什么要设置背景呢？ */
	/* 因为要考虑到屏幕旋转。 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void setBackground(boolean isPort) {
		try {
			// Log.e(TAG,"setBackground...current
			// path="+Utils.CURRENT_ROOT_PATH);
			String filePath = Utils.CURRENT_ROOT_PATH + Utils.LOGO_PATH + Utils.LAND_BACKGROUND;
			if (isPort) {
				filePath = Utils.CURRENT_ROOT_PATH + Utils.LOGO_PATH + Utils.PORT_BACKGROUND;
			}
			// Log.e(TAG, "setBackground : " + filePath);
			File file = new File(filePath);
			if (file.exists()) {
				Loger.print("用户自定义背景图片。");
				Drawable drawable = Drawable.createFromPath(filePath);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
					mMainLayout.setBackground(drawable);
				} else {
					mMainLayout.setBackgroundDrawable(drawable);
				}
			} else {
				Loger.print("用户自定义背景图不存在，将设置为默认背景图。");
				setDefaultBackground(isPort);
			}
		} catch (Exception err) {
			// Log.e(TAG, "setBackground...err="+err.getMessage());
			Loger.print("设置背景图出现异常：", err);
			setDefaultBackground(isPort);
		}
	}

	private void startSystemTime() {
		if (mSystemTime != null) {
			mSystemTime.cancel();
			mSystemTime = null;
		}

		mSystemTime = new Timer();

		startSystemTimer();
	}

	private void startSystemTimer() {
		mSystemTime.schedule(new TimerTask() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(SYSTEM_TIMER_RUNNING);
			}
		}, 0, 1 * 60000);   // by chenqiang 20160704  只用60秒更新一下时间。
	}

	/* 设置默认背景 */
	private void setDefaultBackground(boolean isPort) {
		try {
			if (isPort) {
				// 转屏背景
				mMainLayout.setBackgroundResource(R.drawable.port_background);
			} else {
				// 横屏背景
				mMainLayout.setBackgroundResource(R.drawable.background);
			}
		} catch (Exception err) {
			return;
		}
	}

	private void updateSystemTimer() {
		if (mTimeFormat != null) {
			String dateTime = mTimeFormat.format(new java.util.Date());
			mTvSystemTime.setText(dateTime);
		}

		if (mDateFormat != null) {
			String dateTime = mDateFormat.format(new java.util.Date());
			mTvSystemDate.setText(dateTime);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Loger.closePrint();
		mHandler.removeMessages(SET_BACKGROUD);
		if (mMediaMountedReceiver != null) {
			try {
				this.unregisterReceiver(mMediaMountedReceiver);
			} catch (IllegalArgumentException err) {
				return;
			} catch (Exception err) {
				return;
			}
		}
	}

	@Override
	protected void onDestroy() {
		mHandler.removeMessages(PRESS_DEL);
		mHandler.removeMessages(PRESS_UP);
		mHandler.removeMessages(PRESS_RIGHT);
		mHandler.removeMessages(PRESS_LEFT);
		mHandler.removeMessages(PRESS_DOWN);
		super.onDestroy();
	}

	/**
	 * 处理按键事件 KEYCODE_MENU 菜单键 KEYCODE_BACK 返回键
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		mSharedPre = getSharedPreferences("paw", 0);

		if ((System.currentTimeMillis() - mSharedPre.getLong("idle_crt_tim", 0L)) < 0L && !timeErr) {
			checkStep1();
			timeErr = true;
		}

		if (!mSharedPre.getString("final_paw", "").equals("") && !isInput
				&& (System.currentTimeMillis() - mSharedPre.getLong("idle_crt_tim", 0L)) >= 60 * 1000) {
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
				case 23:
					break;
				case 67:
					mHandler.sendEmptyMessage(PRESS_DEL);
					break;
				default:
					break;
			}
		} else if (isRight || mSharedPre.getString("final_paw", "").equals("")
				|| ((System.currentTimeMillis() - mSharedPre.getLong("idle_crt_tim", 0L)) < 60 * 1000)
				&& System.currentTimeMillis() - mSharedPre.getLong("idle_crt_tim", 0L) > 0) {
			switch (keyCode) {
				case 67:
					// 定义DEL键为转屏键；在此添加转屏逻辑
					int i = SetupUtils.getScreenRotation(this);
					i++;
					if (i > 3) {
						i = 0;
					}
					//Removal by zhang
					/*getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", i).commit();
					Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, i);
					SystemProperties.set(PROP_DISPLAY_ROTATE, 90 * i + "");*/
					this.recreate();
					break;
				case 0:
					break;
				case 21:
					///////////SLEEP_START BY CHEN
					//	PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
					//	pm.goToSleep(SystemClock.uptimeMillis());
					//
					// DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
					//      devicePolicyManager.lockNow();

					// PowerManager.WakeLock  wakeLock = pm.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "TAG");
					//     wakeLock.acquire();
					//    wakeLock.release();
					/////////////SLEEP_END  BY CHEN
					break;
				case KeyEvent.KEYCODE_MENU:
					Intent intent = new Intent(IdleActivity.this, SetupActivity.class);
					startActivity(intent);
					break;
				case KeyEvent.KEYCODE_MEDIA_STOP:
					// Intent intent = new Intent(IdleActivity.this,
					// SetupActivity.class);
					// startActivity(intent);
					break;
				case KeyEvent.KEYCODE_BACK:
					// back 键退出apk
					//	Log.e(TAG, "onKeyDown...keyCode=" + keyCode);
					//	ExitApplication.getInstance().exit();
					//	this.finish();
					//	System.exit(0);
					break;
				default:
					break;
			}
		}
		return false;
	}

	private boolean checkPassword() {
		String inputString = checkEditText.getText().toString();
		return mSharedPre.getString("final_paw", "").equals(inputString);
	}

	private void checkStep1() {
		initPopupWindow(R.layout.check_password_pop_layout);
		mHandler.sendEmptyMessageDelayed(AUTO_HIDE_POP, 15 * 1000);
		isInput = true;
		checkEditText.setFocusable(true);
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

	@Override
	public void setupCallback(int index) {
		// Log.e(TAG, "startSetupActivity...setupIndex="+index);
		mHandler.removeMessages(START_PLAYER_ACTIVITY);
		mRunning = false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e(TAG, "onActivityResult...requestCode=" + requestCode + "; resultCode=" + resultCode);
		if (requestCode == SETUP_REQUEST && resultCode == Utils.SETUP_RESULT) {
			// DELAY_TIME = 1*1000
			mHandler.sendEmptyMessageDelayed(START_PLAYER_ACTIVITY, DELAY_TIME);
		} else if (resultCode == 1000) {
			mHandler.sendEmptyMessageDelayed(START_PLAYER_ACTIVITY, DELAY_TIME);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 开启播放 全屏就打开PlayViewActivity 分屏就打开MainActivity
	 */
	private void startPlayerActivity() {
		mWindowMode = SetupUtils.windowModeIndex(this);
		Log.e(TAG, "startPlayerActivity...mWindowMode=" + mWindowMode);
		Intent intent = new Intent();
		if (mWindowMode == 0) {
			/* 分屏关时启动PlayViewActivity */
			intent.setClass(this, PlayViewActivity.class);
		} else {
			/* 分屏开时启动MainActivity */
			intent.setClass(this, MainActivity.class);
		}
		intent.putExtra("window_model", mWindowMode);
		startActivity(intent);
		// finish();
	}

	/**
	 * 媒体插入监听 监听SD卡和U盘的插入，并且判断是否需要拷贝，以及拷贝的文件夹
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
					msg.what = SHOW_TOAST_FOR_TEST;
					mHandler.sendMessage(msg);
					if (path.endsWith("/")) {// 根目录
						Utils.USB_ROOT_PATH = path;
					} else {
						Utils.USB_ROOT_PATH = path + "/";
					}
					// USB播放文件目录
					Utils.USB_PATH = Utils.USB_ROOT_PATH + Utils.PLAY_LIST_DIR;
					Log.d(TAG, "usb root path = " + Utils.USB_ROOT_PATH + "; usb path =" + Utils.USB_PATH);
				}

				boolean isMediaCard = false;
				Intent i = new Intent(IdleActivity.this, CopyActivity.class);

				SharedPreferences mSharedPre = getSharedPreferences("basic_menu", 0);
				int storageIndex = mSharedPre.getInt("present_equip_index", 0);

				if (path.contains("extsd") && storageIndex == 1) {
					// startPlayerActivity();
					loadData();
					return;
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
					// nothing

				} else if (path.contains("usbhost") && storageIndex == 2) {
					// nothing
				}
			}
		}
	};
}