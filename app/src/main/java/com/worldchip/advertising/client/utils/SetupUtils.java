package com.worldchip.advertising.client.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;

import com.worldchip.advertising.client.entity.Caption;

public class SetupUtils {

	private static final String TAG = "--SetupUtils--";
	public static final int CAPTION_TEXT_SLOW_SPEED = 3;
	public static final int CAPTION_TEXT_NROMAL_SPEED = 4;
	public static final int CAPTION_TEXT_FAST_SPEED = 5;

	// 时间字体大小
	public static final int TIME_TEXT_SMALL_SIZE = 38;
	public static final int TIME_TEXT_NORMAL_SIZE = 48;
	public static final int TIME_TEXT_LARGE_SIZE = 58;

	// 字幕字体大小
	public static final float CAPTION_TEXT_SMALL_SIZE = 48.0f;
	public static final float CAPTION_TEXT_NROMAL_SIZE = 68.0f;
	public static final float CAPTION_TEXT_LARGE_SIZE = 90.0f;

	// 字幕文字颜色
	public static final String CAPTION_TEXT_COLOR_YELLO = "#ffff00";
	public static final String CAPTION_TEXT_COLOR_WHITE = "#ffffff";
	public static final String CAPTION_TEXT_COLOR_BLACK = "#000000";
	public static final String CAPTION_TEXT_COLOR_RED = "#ff0000";
	public static final String CAPTION_TEXT_COLOR_BLUE = "#0000ff";

	// 字幕背景颜色
	public static final String CAPTION_BG_COLOR_YELLO = "#88ffff00";
	public static final String CAPTION_BG_COLOR_WHITE = "#88ffffff";
	public static final String CAPTION_BG_COLOR_BLACK = "#88000000";
	public static final String CAPTION_BG_COLOR_RED = "#88ff0000";
	public static final String CAPTION_BG_COLOR_BLUE = "#880000ff";
	public static final String CAPTION_BG_COLOR_TRANSPARENT = "#00000000";

	public static SharedPreferences menuPref;

	// 1111111111
	public static void setupSystemLangue(Context context) {
		menuPref = context.getSharedPreferences("basic_menu", 0);
		int index = menuPref.getInt("language_set_index", 0);

		// Log.e(TAG,
		// "setupSystemLangue...menuItem.SelectedIndex="+menuItem.SelectedIndex);
		Configuration config = context.getResources().getConfiguration();
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		if (index == 0) {
			config.locale = Locale.SIMPLIFIED_CHINESE;
		} else {
			config.locale = Locale.ENGLISH;
		}
		context.getResources().updateConfiguration(config, dm);
	}

	public static int getMenuIndex(Context context) {
		menuPref = context.getSharedPreferences("id_prefer", 0);
		int index = menuPref.getInt("menuIn", 0);
		return index;
	}

	// 设置当前的根目录
	public static int setCurrentRootPath(Context context) {
		menuPref = context.getSharedPreferences("basic_menu", 0);
		int index = menuPref.getInt("present_equip_index", 0);
		switch (index) {
			case 0:
				Utils.setCurrentRootPath(Utils.NAND_PATH);
				break;
			case 1:
				Utils.setCurrentRootPath(Utils.SD_PATH);
				break;
			case 2:
				Utils.setCurrentRootPath(Utils.USB_PATH);
				break;
			default:
				Utils.setCurrentRootPath(Utils.NAND_PATH);
				//	Utils.setCurrentRootPath(Utils.USB_PATH);
				break;
		}
		return index;
	}

	// 拷贝
	public static int getCopyMode(Context context) {
		menuPref = context.getSharedPreferences("basic_menu", 0);
		int index = menuPref.getInt("copy_mode_index", 0);
		return index;
	}

	// 单曲播放
	public static boolean playSingleMedia(Context context) {
		menuPref = context.getSharedPreferences("basic_menu", 0);
		int index = menuPref.getInt("play_mode_index", 0);
		// Log.e(TAG, "playSingleMedia..index="+index);
		return index == 1;
	}

	// 时间开关
	public static boolean getTimeSwitch(Context context) {
		menuPref = context.getSharedPreferences("time_set", 0);
		int index = menuPref.getInt("time_switch_index", 1);
		return index == 0;
	}

	// 时间格式
	public static SimpleDateFormat getDateFormatForLangue(Context context, String pattern) {
		menuPref = context.getSharedPreferences("basic_menu", 0);
		int index = menuPref.getInt("language_set_index", 0);
		// java.utils.Locale 这个主要是在软件本地化时候用得到。
		Locale localeLangue = null;

		if (index == 0) {
			localeLangue = Locale.CHINA;
		} else if (index == 2) {
			localeLangue = Locale.JAPANESE;
		} else {
			localeLangue = Locale.ENGLISH;
		}

		return new SimpleDateFormat(pattern, localeLangue);
	}

	// 时间颜色
	public static String getTimerColor(Context context) {
		menuPref = context.getSharedPreferences("time_set", 0);
		int index = menuPref.getInt("time_color_index", 1);

		switch (index) {
			case 0:
				return CAPTION_TEXT_COLOR_YELLO;
			case 1:
				return CAPTION_TEXT_COLOR_WHITE;
			case 2:
				return CAPTION_TEXT_COLOR_BLACK;
			case 3:
				return CAPTION_TEXT_COLOR_RED;
			case 4:
				return CAPTION_TEXT_COLOR_BLUE;
		}

		return CAPTION_TEXT_COLOR_BLACK;
	}

	// 时间大小
	public static int getTimerSize(Context context) {
		menuPref = context.getSharedPreferences("time_set", 0);
		int index = menuPref.getInt("time_size_index", 1);
		switch (index) {
			case 0:
				return TIME_TEXT_SMALL_SIZE;
			case 1:
				return TIME_TEXT_NORMAL_SIZE;
			case 2:
				return TIME_TEXT_LARGE_SIZE;
		}

		return TIME_TEXT_NORMAL_SIZE;
	}

	// 视频播放大小
	public static int getVideoScale(Context context) {
		menuPref = context.getSharedPreferences("basic_menu", 0);
		int index = menuPref.getInt("video_size_index", 0);
		return index;
	}

	// 图片全屏显示
	public static boolean playPhotoFullScreen(Context context) {
		menuPref = context.getSharedPreferences("basic_menu", 0);
		int index = menuPref.getInt("image_size_index", 0);
		return index == 0;
	}

	// 图片切场
	public static int getSwitchPhotoAnimationIndex(Context context) {
		menuPref = context.getSharedPreferences("basic_menu", 0);
		int index = menuPref.getInt("image_trans_index", 0);
		return index;
	}

	// 图片时间间隔
	public static int playPhotoDuration(Context context) {
		menuPref = context.getSharedPreferences("basic_menu", 0);
		int index = menuPref.getInt("image_time_value", 5);
		return index;
	}

	// 字幕开关
	public static boolean isCaptionOpen(Context context) {
		menuPref = context.getSharedPreferences("caption_menu", 0);
		int index = menuPref.getInt("caption_switch_index", 0);
		return index == 0;
	}

	// 获取滚动字幕的设置
	public static Caption getCaptionSetting(Context context) {
		Caption captionSetting = new Caption();
		menuPref = context.getSharedPreferences("caption_menu", 0);
		int index = menuPref.getInt("caption_switch_index", 0);

		// 字幕开关
		captionSetting.setShow(index == 0);

		// 字体大小
		int captionSize = menuPref.getInt("caption_size_index", 1);

		switch (captionSize) {
			case 0:
				captionSetting.setSize(CAPTION_TEXT_SMALL_SIZE);
				break;
			case 1:
				captionSetting.setSize(CAPTION_TEXT_NROMAL_SIZE);
				break;
			case 2:
				captionSetting.setSize(CAPTION_TEXT_LARGE_SIZE);
				break;
		}

		// 字体颜色
		int captionColor = menuPref.getInt("caption_color_index", 2);

		switch (captionColor) {
			case 0:
				captionSetting.setFgcolor(CAPTION_TEXT_COLOR_YELLO);
				break;
			case 1:
				captionSetting.setFgcolor(CAPTION_TEXT_COLOR_WHITE);
				break;
			case 2:
				captionSetting.setFgcolor(CAPTION_TEXT_COLOR_BLACK);
				break;
			case 3:
				captionSetting.setFgcolor(CAPTION_TEXT_COLOR_RED);
				break;
			case 4:
				captionSetting.setFgcolor(CAPTION_TEXT_COLOR_BLUE);
				break;
		}

		// 背景颜色
		int captionBackColor = menuPref.getInt("caption_background_color_index", 2);

		switch (captionBackColor) {
			case 0:
				captionSetting.setBgcolor(CAPTION_BG_COLOR_TRANSPARENT);
				break;
			case 1:
				captionSetting.setBgcolor(CAPTION_BG_COLOR_YELLO);
				break;
			case 2:
				captionSetting.setBgcolor(CAPTION_BG_COLOR_WHITE);
				break;
			case 3:
				captionSetting.setBgcolor(CAPTION_BG_COLOR_BLACK);
				break;
			case 4:
				captionSetting.setBgcolor(CAPTION_BG_COLOR_RED);
				break;
			case 5:
				captionSetting.setBgcolor(CAPTION_BG_COLOR_BLUE);
				break;
		}

		// 滚动速度
		int speed = menuPref.getInt("caption_speed_index", 1);

		switch (speed) {
			case 0:
				captionSetting.setSpeed("slow");
				break;
			case 1:
				captionSetting.setSpeed("normal");
				break;
			case 2:
				captionSetting.setSpeed("fast");
				break;
		}

		// 显示位置: 底部：0； 顶部1：
		//	captionSetting.setShowLocation(0);

		// 字幕位置，底部：0； 顶部1：
		int location = menuPref.getInt("caption_location_index", 0);

		switch (location) {
			case 0:
				captionSetting.setShowLocation(0);
				break;
			case 1:
				captionSetting.setShowLocation(1);
				break;
		}

		return captionSetting;
	}

	// 屏幕旋转
	public static int getScreenRotation(Context context) {
		menuPref = context.getSharedPreferences("function_menu", 0);
		int value = menuPref.getInt("rotation_index", 0);
		return value;
	}

	// 分屏模式
	public static int windowModeIndex(Context context) {
		menuPref = context.getSharedPreferences("function_menu", 0);
		int value = menuPref.getInt("window_mode_index", 0);
		return value;
	}

	// 是否背景音乐模式
	public static boolean isBackgroundMuiscModel(Context context) {
		menuPref = context.getSharedPreferences("function_menu", 0);
		int index = menuPref.getInt("background_music_index", 0);
		return index == 1;
	}

	// 显示图标
// 显示图标
	public static int showLogo(Context context) {
		menuPref = context.getSharedPreferences("function_menu", 0);
		int value = menuPref.getInt("show_logo_index", 0);
		Log.e(TAG, "showLogo..value=" + value);
		return value;
	}

	// 串口同步播放
	public static int serialPortIndex(Context context) {
		menuPref = context.getSharedPreferences("function_menu", 0);
		int value = menuPref.getInt("serial_port_index", 0);
		return value;
	}

	// 开机守护
	public static boolean isBootProtect(Context context) {
		menuPref = context.getSharedPreferences("other_menu", 0);
		int index = menuPref.getInt("boot_index", 0);
		return index == 0;
	}

	// 播放加密
	public static boolean isPlayEncode(Context context) {
		menuPref = context.getSharedPreferences("function_menu", 0);
		int index = menuPref.getInt("play_encode_index", 0);
		return index == 1;
	}

	// 自动旋转
	public static boolean isAutoRotation(Context context) {
		menuPref = context.getSharedPreferences("function_menu", 0);
		int index = menuPref.getInt("auto_rotation_index", 0);
		return index == 1;
	}

	// 获取感应开关选项。
	public static int getSensorSwitch(Context context) {
		menuPref = context.getSharedPreferences("function_menu", 0);
		int index = menuPref.getInt("sensor_switch_index", 0);
		return index;
	}

	/*
	 * // 日志 public static boolean isLogcat(Context context) { int index =
	 * getSetupValueIndex(context, LOGCAT); return index == 0; }
	 *
	 * // 播放记录 public static boolean isPlayRecordOpen(Context context) { int
	 * index = getSetupValueIndex(context, RECORDER_FILE); return index == 0; }
	 */

	@SuppressLint("SimpleDateFormat")
	public static long getCurrentSystemTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String dateTime = formatter.format(new java.util.Date());
		try {
			return formatter.parse(dateTime).getTime();
		} catch (ParseException e) {
			// Log.e(TAG, "getCurrentSystemTime..err: "+e.getMessage());
			e.printStackTrace();
		}
		return -1;
	}

	// 当图片和音乐同时播放时，以哪个优先切换
	// 0:图片，1：音乐
	public static int getPhotoOrMusicFinished(Context context) {
		return 0;
	}

	/*
	 * public static void switchToNand(Context context) {
	 * SetupData.updateMenuItemInfo(context, "priority_mode", 0);
	 * Utils.setCurrentRootPath(Utils.NAND_PATH); }
	 */

	// 这个是标准获取版本号的方法
	public static String getVersionCode(Context context) {

		try {
			return String.valueOf(
					context.getPackageManager().getPackageInfo("com.worldchip.advertisingclient", 0).versionCode);
		} catch (NameNotFoundException e) {
			return "V2.00";
		}
	}

	public static String getVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo("com.worldchip.advertisingclient", 0).versionName;
		} catch (NameNotFoundException e) {
			return "V2.00";
		}
	}

}