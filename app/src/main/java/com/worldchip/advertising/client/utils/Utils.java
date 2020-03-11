package com.worldchip.advertising.client.utils;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.os.Environment;
import android.util.Log;

public class Utils {

	public static final CharSequence APP_VERISON = "XcdPlayer3.0";

	public static final String PACKAGE_NAME = "com.worldchip.advertisingclient";
	public static final String REBOOT_SERVICE = "com.worldchipo.advertisingclient.reboot.monitorservice";
	public static final String REBOOT_AUTO_UPDATE_SERVICE = "com.worldchip.advertising.client.service.AutoUpdateService";
	public static final String NAND_PATH = Environment.getExternalStorageDirectory() + "/Playlist" + File.separator;
	public static final String SD_ROOT_PATH = "/mnt/external_sd/";
	public static final String SD_PATH = "/mnt/external_sd/Playlist/";
	public static String USB_ROOT_PATH = getUsbRoot();
	public static String USB_PATH = getUsbStorage();
	public static final String PLAY_LIST_DIR = "Playlist/";
	public static final String VIDEO_PATH = "video" + File.separator;
	public static final String MEDIA_PATH = "media" + File.separator;
	public static final String CAPTION_PATH = "caption" + File.separator;
	public static final String AD_PATH = "AD" + File.separator;
	public static final String LOGO_PATH = "logo" + File.separator;
	public static final String LOG_PATH = "log" + File.separator;

	public static final String LAND_BACKGROUND = "background.jpg";
	public static final String PORT_BACKGROUND = "port_background.jpg";
	public static final String LOGO_FILE = "logo.png";

	public static String CURRENT_ROOT_PATH = NAND_PATH;
	public static String CURRENT_SCROLL_TEXT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static boolean AUTO_UPDATING = false;

	public static final long UPDATE_DATETIME_BETWEEN = 60 * 60 * 1000; // 1小时
	public static final int PLAY_VIDEO_COMPLETION = 2000;

	public static final int CHECK_PLAYLIST_SETTING = 0;
	public static final int CHECK_PLAYLIST_SETTING_TIME = 5 * 1000;

	// 定义的关闭POPWINDOE的时间12秒
	public static final int CLOSE_SETUP_POPUP = 100;
	public static final long CLOSE_SETUP_POPUP_TIME = 12 * 1000;

	// 播放状态图标相关的
	public static final int HIDE_PLAY_STATE_ICON = 101;
	public static final long HIDE_PLAY_STATE_ICON_TIME = 5 * 1000;

	// 快进和快退前进或后退5秒
	public static int FAST_FORWARD_OR_BACK_STEP = 5 * 1000;

	public static final String PLAYLIST_FILE = "Playlist.xml";
	public static final String PLAYLIST_FILE_PORT = "Playlist_port.xml";
	public static final String SETTING_FILE = "Setting.xml";

	public static boolean IS_PORT = false;
	public static int WIDTH_PIXELS = 1920;
	public static int HEIGHT_PIXELS = 1080;

	public static final int PLAY_ICON_WIDTH_STEP = 160;
	public static final int PLAY_ICON_HEIGHT_STEP = 400;

	public static final int SCOLL_LARGE_TEXT_HEIGHT_STEP = 135;
	public static final int SCOLL_NORMAL_TEXT_HEIGHT_STEP = 110;
	public static final int SCOLL_SMALL_TEXT_HEIGHT_STEP = 85;

	public static final int SETUP_RESULT = 500;

	// 记录的文件名
	public static final String RECORDER_FILE = "playlog.txt";
	// 拷贝的文件
	public static final String COPY_FILE = "COPY.txt";
	// 最大滚动字幕的长度
	public static final int MAX_SCROLL_TEXT_LENGTH = 500;
	// 支持最大的图片播放大小5M
	public static final int MAX_DISPLAY_IMAGE_SIZE = 5 * 1024 * 1024;

	private static final String TAG = "--Utils--";

	/////add for tans only pic
	public static boolean isSplitOnlyPic = false;

	public static boolean isFreeWinMode = false;

	public static boolean isSplitOnlyPic() {
		return isSplitOnlyPic;
	}

	public static void setSplitOnlyPic(boolean isSplitOnlyPic) {
		Utils.isSplitOnlyPic = isSplitOnlyPic;
	}


	private static String getUsbRoot() {
		File file ;
		file = new File("/mnt/usb_storage/USB_DISK2/udisk0/");
		if (file.exists()) {
			Log.d(TAG, "/mnt/usb_storage/USB_DISK2/udisk0/" );
			return   "/mnt/usb_storage/USB_DISK2/udisk0/";
		}
		return   "/mnt/usb_storage/USB_DISK2/udisk0/";
	}

	private static String getUsbStorage() {
		File file ;
		file = new File("/mnt/usb_storage/USB_DISK2/udisk0/");
		if (file.exists()) {
			Log.d(TAG, "/mnt/usb_storage/USB_DISK2/udisk0/" );
			return   "/mnt/usb_storage/USB_DISK2/udisk0/Playlist/";
		}
		return   "/mnt/usb_storage/USB_DISK2/udisk0/Playlist/";

	}

	public static void setCurrentRootPath(String rootPath) {
		CURRENT_ROOT_PATH = rootPath;
	}

	public static String getCurrentRootPath() {
		return CURRENT_ROOT_PATH;
	}


	public static Set<String> Array2Set(String[] strings) {
		//数组-->Set
		Set<String> set = new HashSet<String>(Arrays.asList(strings));
		return set;
	}

	public static String[]  Set2Array(Set<String> set) {

		String[] arr = new String[set.size()];
		//Set-->数组
		set.toArray(arr);
		return arr;
	}

}