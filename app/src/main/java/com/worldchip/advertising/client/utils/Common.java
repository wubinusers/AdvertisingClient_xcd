package com.worldchip.advertising.client.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.worldchip.advertising.client.copy.CopyFile;
import com.worldchip.advertising.client.entity.DetailsInfo;
import com.worldchip.advertising.client.entity.MusicInfo;
import com.worldchip.advertising.client.image.utils.ImageSizeUtil;
import com.worldchip.advertisingclient.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.os.Build;
import android.os.StatFs;
import android.provider.MediaStore.Audio.Media;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class Common {

	private static Context mContext;

	protected static final String TAG = "--Common--";
	private static final boolean DEBUG = false;

	public static List<DetailsInfo> loadData(Context context, MediaType mediaType) {

		return getList(context, mediaType);
	}

	public static String getDeviceId(Context context) {
		String deviceID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
		Log.e(TAG, "getDeviceId=" + deviceID);
		return deviceID;
	}

	public static String getBuildString(String property) {
		// String propertyValue = SystemProperties.get(property, "unknown");
		// Log.e(TAG, "getBuildString...property="+property+";
		// propertyValue="+propertyValue);
		// return propertyValue;
		return "";
	}

	// 获取存储空间相关
	public static long readBlockSize(String path, int flag) {
		StatFs sf = new StatFs(path);

		long blockSize = sf.getBlockSize();
		long blockCount = sf.getBlockCount();
		long availCount = sf.getAvailableBlocks();

		Log.e(TAG, "block大小:" + blockSize + ",block数目:" + blockCount + ",总大小:" + blockSize * blockCount / 1024 + "KB");
		// Log.d("", "可用的block数目：:"+ availCount+",剩余空间:"+
		// availCount*blockSize/1024+"KB");
		if (flag == 0) { // sum
			return blockSize * blockCount / 1024;
		} else if (flag == 1) { // avail
			return blockSize * availCount / 1024;
		} else {
			return (blockSize * blockCount / 1024) - (blockSize * availCount / 1024);
		}
	}

	// 获取列表，存储绝对路径
	private static List<DetailsInfo> getList(Context context, MediaType type) {
		mContext = context;
		List<DetailsInfo> detailsInfos = new ArrayList<DetailsInfo>();

		File mediaFile = new File(Utils.CURRENT_ROOT_PATH + Utils.MEDIA_PATH);
		if (!mediaFile.exists()) {
			// added by guofq 20151006
			String rootPath = Utils.CURRENT_ROOT_PATH.replace("Playlist/", "");
			Log.e(TAG, "getList...rootPath =" + rootPath);
			mediaFile = new File(rootPath);
			/////////////////
			File mediaFile1 = new File("/mnt/usbhost2");
			//	if (android.os.Build.MODEL.contains("A83")) {
			//	mediaFile1 = new File("/mnt/usbhost");
			//	} else {
			//		mediaFile1 = new File("/mnt/usbhost0");
			//	}
//////////////////////////
			if (!mediaFile.exists()) {
				return detailsInfos;
			}  else {

				if (type == MediaType.VIDEO) {
					filterMediaFiles(MediaType.VIDEO, 2, detailsInfos, mediaFile);
				} else if (type == MediaType.PHOTO) {
					filterMediaFiles(MediaType.PHOTO, 2, detailsInfos, mediaFile);
				} else if (type == MediaType.MUSIC) {
					filterMediaFiles(MediaType.MUSIC, 2, detailsInfos, mediaFile);
				} else if (type == MediaType.ALL) {
					filterMediaFiles(MediaType.ALL, 0, detailsInfos, mediaFile);
				}

			}

			if (mediaFile1.exists()) {
				if (type == MediaType.VIDEO) {
					filterMediaFiles(MediaType.VIDEO, 2, detailsInfos, mediaFile1);
				} else if (type == MediaType.PHOTO) {
					filterMediaFiles(MediaType.PHOTO, 2, detailsInfos, mediaFile1);
				} else if (type == MediaType.MUSIC) {
					filterMediaFiles(MediaType.MUSIC, 2, detailsInfos, mediaFile1);
				} else if (type == MediaType.ALL) {
					filterMediaFiles(MediaType.ALL, 0, detailsInfos, mediaFile1);
				}

				return detailsInfos;

			}

		}


		if (type == MediaType.VIDEO) {
			filterMediaFiles(MediaType.VIDEO, 2, detailsInfos, mediaFile);
		} else if (type == MediaType.PHOTO) {
			filterMediaFiles(MediaType.PHOTO, 2, detailsInfos, mediaFile);
		}else if (type == MediaType.MUSIC) {
			filterMediaFiles(MediaType.MUSIC, 2, detailsInfos, mediaFile);
		}else if (type == MediaType.ALL) {
			filterMediaFiles(MediaType.ALL, 0, detailsInfos, mediaFile);
		}

		return detailsInfos;
	}
	// 获取插播数据
	public static List<DetailsInfo> getAllVideoAndImageList(Context context, String path) {
		final List<DetailsInfo> AllList = new ArrayList<DetailsInfo>();

		File file = new File(path);
		if (file.exists()) {
			filterMediaFiles(MediaType.ALL, 1, AllList, file);
		}
		return AllList;
	}

	// 过滤所有媒体文件
	@SuppressLint("DefaultLocale")
	private static void filterMediaFiles(MediaType type, int filterType, final List<DetailsInfo> detailsInfos,
										 File file) {
		File[] files = file.listFiles();
		if (files == null || files.length <= 0)
			return;

		ArrayList<String> items = new ArrayList<String>();
		HashMap<String, File> hashMap = new HashMap<String, File>();

		// 在此添加文件过滤。
		for (int i = 0; i < files.length; i++) {
			File item = files[i];
			if (SetupUtils.isPlayEncode(mContext)) {
				try {
					if (readLastLine(item)) {
						items.add(item.getName());
						if (!hashMap.containsKey(item.getName())) {
							hashMap.put(item.getName(), item);
						}
						if (DEBUG)
							Log.e(TAG, ".filterAllMediaFile..before sort...name=" + item.getName());
					} else {
						// nothing
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}  else {
				items.add(item.getName());
				if (!hashMap.containsKey(item.getName())) {
					hashMap.put(item.getName(), item);
				}
				if (DEBUG)
					Log.e(TAG, ".filterAllMediaFile..before sort...name=" + item.getName());
			}

		}

		// 排序
		Collections.sort(items, String.CASE_INSENSITIVE_ORDER);

		File mediaFile = null;
		for (String name : items) {
			if (DEBUG)
				Log.e(TAG, ".filterAllMediaFile..after sort...name=" + name);
			if (hashMap.containsKey(name)) {
				mediaFile = hashMap.get(name);
			} else {
				continue;
			}
			int i = name.lastIndexOf('.');
			if (i > -1) {
				name = name.substring(i).toUpperCase();
				if (filterType == 0) {
					addAllMedia(detailsInfos, mediaFile, name);
				} else if (filterType == 1) {
					addAdMedia(detailsInfos, mediaFile, name);
				} else if (filterType == 2) {
					addSingleTypeMedia(type, detailsInfos, mediaFile, name);
				}
			}
		}
	}

	public static boolean readLastLine(File file) throws IOException {

		boolean isEncodeFile = false;
		//File file = new File(fileString);
		if (!file.exists() || file.isDirectory() || !file.canRead()) {
			return false;
		}

		RandomAccessFile randomFile = null;
		try {
			System.out.println("随机读取一段文件内容：");
			// 打开一个随机访问文件流，按只读方式
			randomFile = new RandomAccessFile(file, "r");
			// 文件长度，字节数
			long start = randomFile.length() - 6;
			// 将读文件的开始位置移到beginIndex位置。
			randomFile.seek(start);
			byte[] bytes = new byte[6];
			int byteread = 0;
			// 一次读6个字节，如果文件内容不足6个字节，则读剩下的字节。
			// 将一次读取的字节数赋给byteread
			while ((byteread = randomFile.read(bytes)) != -1) {
				for (int i = 0; i < bytes.length; i++) {
					//Log.d("4345345", "byte" + i + "=========" + bytes[i]);

					if (bytes[0] == -96 && bytes[1] == -80 && bytes[2] == -64 && bytes[3] == -48 && bytes[4] == -32
							&& bytes[5] == -16) {
						isEncodeFile = true;
					}
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (randomFile != null) {
				try {
					randomFile.close();
				} catch (IOException e1) {
				}
			}
		}

		return isEncodeFile;
	}

	private static void addAdMedia(final List<DetailsInfo> detailsInfos, File mediaFile, String name) {
		if (getVideoExtens().contains(name)) {
			detailsInfos.add(new DetailsInfo(MediaType.VIDEO, mediaFile.getAbsolutePath(), null));
		} else if (getImageExtens().contains(name)) {
			detailsInfos.add(new DetailsInfo(MediaType.PHOTO, mediaFile.getAbsolutePath(), null));
		}
	}

	private static void addAllMedia(final List<DetailsInfo> detailsInfos, File mediaFile, String name) {
		// Log.e(TAG, "addAllMedia...name="+name);
		if (getVideoExtens().contains(name)) {
			detailsInfos.add(new DetailsInfo(MediaType.VIDEO, mediaFile.getAbsolutePath(), null));
		} else if (getMusicExtens().contains(name)) {
			detailsInfos.add(new DetailsInfo(MediaType.MUSIC, mediaFile.getAbsolutePath(), null));
		} else if (getImageExtens().contains(name)) {
			detailsInfos.add(new DetailsInfo(MediaType.PHOTO, mediaFile.getAbsolutePath(), null));
		}
	}

	// singleTypeMedia 表示什么？
	private static void addSingleTypeMedia(final MediaType type, final List<DetailsInfo> detailsInfos, File mediaFile,
										   String name) {
		switch (type) {
			case VIDEO:
				if (getVideoExtens().contains(name)) {
					detailsInfos.add(new DetailsInfo(type, mediaFile.getAbsolutePath(), null));
				}
				break;
			case MUSIC:
				if (getMusicExtens().contains(name)) {
					detailsInfos.add(new DetailsInfo(type, mediaFile.getAbsolutePath(), null));
				}
				break;
			case PHOTO:
				if (getImageExtens().contains(name)) {
					detailsInfos.add(new DetailsInfo(type, mediaFile.getAbsolutePath(), null));
				}
				break;
			default:
				break;
		}
	}

	// 获取音乐数据
	public static MusicInfo getMusicInfo(Context context, String path) {
		MusicInfo music = null;
		String[] columns = { Media.ALBUM, Media.DURATION, Media.DISPLAY_NAME, Media.TITLE, Media.ARTIST, Media.DATA,
				Media.IS_MUSIC };
		String selection = Media.DATA + "=?";
		String[] selectionArgs = { path.trim() };
		Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, columns, selection,
				selectionArgs, null);
		if (cursor != null && cursor.moveToNext()) {
			music = new MusicInfo();

			// trim()方法返回调用字符串对象的一个副本，但是所有起始和结尾的空格都被删除了
			music.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(Media.ARTIST)).trim());
			music.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(Media.ALBUM)).trim());
			music.setData(cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA)).trim());
			music.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(Media.TITLE)).trim());
			music.setDisplay_name(cursor.getString(cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME)).trim());
			try {
				music.setDuration(
						Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Media.DURATION)).trim()));
			} catch (Exception err) {
				music.setDuration(0);
			}
			return music;
		}
		return music;
	}

	public static List<DetailsInfo> getMusicMedias(Context context, final List<DetailsInfo> AllList,
												   final MediaType type, final String rootPath) {
		String[] columns = { Media.ALBUM, Media.DURATION, Media.DISPLAY_NAME, Media.TITLE, Media.ARTIST, Media.DATA,
				Media.IS_MUSIC };
		String selection = Media.IS_MUSIC + "=?";
		String[] selectionArgs = { "1" };
		Cursor cursor = context.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, columns, selection,
				selectionArgs, null);
		DetailsInfo item = null;
		while (cursor.moveToNext()) {
			String path = cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA)).trim();
			if (!path.contains(rootPath)) {
				continue;
			}
			MusicInfo music = new MusicInfo();
			music.setAlbum(cursor.getString(cursor.getColumnIndexOrThrow(Media.ALBUM)).trim());
			music.setData(cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA)).trim());
			music.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(Media.TITLE)).trim());
			music.setDisplay_name(cursor.getString(cursor.getColumnIndexOrThrow(Media.DISPLAY_NAME)).trim());
			try {
				music.setDuration(
						Long.parseLong(cursor.getString(cursor.getColumnIndexOrThrow(Media.DURATION)).trim()));
			} catch (Exception err) {
				music.setDuration(0);
			}
			music.setArtist(cursor.getString(cursor.getColumnIndexOrThrow(Media.ARTIST)).trim());
			item = new DetailsInfo(type, path, music);
			AllList.add(item);
		}
		return AllList;
	}

	private static List<String> ImageExtens = null;

	public static List<String> getImageExtens() {
		if (ImageExtens == null) {
			ImageExtens = new ArrayList<String>();
		}

		if (ImageExtens.size() < 1) { // 添加.gif文件的支持
			ImageExtens.add(".GIF");
			ImageExtens.add(".JPEG");
			ImageExtens.add(".JPG");
			ImageExtens.add(".PNG");
			ImageExtens.add(".BMP");
		}
		return ImageExtens;
	}

	private static List<String> VideoExtens = null;

	public static List<String> getVideoExtens() {
		if (VideoExtens == null) {
			VideoExtens = new ArrayList<String>();
		}
		if (VideoExtens.size() < 1) { // 添加对.rm文件的支持
			// 实际支持.dat格式的视频，但是考虑到一些数据文件也是.dat格式，就暂且没有将其加入。
			VideoExtens.add(".RM");
			VideoExtens.add(".MP4");
			VideoExtens.add(".AVI");
			VideoExtens.add(".RMB");
			VideoExtens.add(".RMVB");
			VideoExtens.add(".FLV");
			VideoExtens.add(".MKV");
			VideoExtens.add(".MOV");
			VideoExtens.add(".TS");
			VideoExtens.add(".MPG");
			VideoExtens.add(".VOB");
			VideoExtens.add(".WMV");
			VideoExtens.add(".TP");
		}
		return VideoExtens;
	}

	private static List<String> MusicExtens = null;

	public static List<String> getMusicExtens() {
		if (MusicExtens == null) {
			MusicExtens = new ArrayList<String>();
		}
		if (MusicExtens.size() < 1) { // 添加对.wav .aac .arm .ogg文件的支持
			MusicExtens.add(".WAV");
			MusicExtens.add(".AAC");
			MusicExtens.add(".OGG");
			MusicExtens.add(".ARM");
			MusicExtens.add(".MP3");
			MusicExtens.add(".WAVE");
			MusicExtens.add(".WMA");
			MusicExtens.add(".APE");
			MusicExtens.add(".FLAC");
		}
		return MusicExtens;
	}

	private static List<String> TextExtens = null;

	public static List<String> getTextExtens() {
		if (TextExtens == null) {
			TextExtens = new ArrayList<String>();
		}
		if (TextExtens.size() < 1) {
			TextExtens.add(".TXT");

		}
		return TextExtens;
	}

	// 过滤插播文件
	public static void getCopyADFileList(final List<CopyFile> copyFileList, File file) {
		file.listFiles(new FileFilter() {
			@SuppressLint("DefaultLocale")
			public boolean accept(File file) {
				String name = file.getName();
				int i = name.lastIndexOf('.');
				if (i > -1) {
					CopyFile copyFile = new CopyFile();
					String fileName = name.substring(0, i);
					String extend = name.substring(i);
					if (DEBUG)
						Log.e(TAG, "fileName=" + fileName + "; extend=" + extend);
					if (getVideoExtens().contains(extend.toUpperCase())) {
						copyFile.mCategory = Utils.AD_PATH;
						copyFile.mFileExtend = extend;
						copyFile.mFileName = fileName;
						copyFile.mFilePath = file.getAbsolutePath();
						copyFileList.add(copyFile);
					} else if (getImageExtens().contains(extend.toUpperCase())) {
						copyFile.mCategory = Utils.AD_PATH;
						copyFile.mFileExtend = extend;
						copyFile.mFileName = fileName;
						copyFile.mFilePath = file.getAbsolutePath();
						copyFileList.add(copyFile);
					}
				}
				return false;
			}
		});
	}

	// 过滤配置文件
	public static void getCopyConfigFileList(final List<CopyFile> copyFileList, File file) {
		file.listFiles(new FileFilter() {
			@SuppressLint("DefaultLocale")
			public boolean accept(File file) {
				String name = file.getName();
				int i = name.lastIndexOf('.');
				if (i > -1) {
					CopyFile copyFile = new CopyFile();
					String fileName = name.substring(0, i);
					String extend = name.substring(i);
					if (DEBUG)
						Log.e(TAG, "fileName=" + fileName + "; extend=" + extend);
					if (extend.toUpperCase().equals(".XML")) {
						copyFile.mCategory = "";
						copyFile.mFileExtend = extend;
						copyFile.mFileName = fileName;
						copyFile.mFilePath = file.getAbsolutePath();
						copyFileList.add(copyFile);
					}
				}
				return false;
			}
		});
	}

	// 过滤背景文件及台标
	public static void getCopyLogoFileList(final List<CopyFile> copyFileList, File file) {
		file.listFiles(new FileFilter() {
			@SuppressLint("DefaultLocale")
			public boolean accept(File file) {
				String name = file.getName();
				int i = name.lastIndexOf('.');
				if (i > -1) {
					CopyFile copyFile = new CopyFile();
					String fileName = name.substring(0, i);
					String extend = name.substring(i);
					if (DEBUG)
						Log.e(TAG, "fileName=" + fileName + "; extend=" + extend);
					if (getImageExtens().contains(extend.toUpperCase())) {
						copyFile.mCategory = Utils.LOGO_PATH;
						copyFile.mFileExtend = extend;
						copyFile.mFileName = fileName;
						copyFile.mFilePath = file.getAbsolutePath();
						copyFileList.add(copyFile);
					}
				}
				return false;
			}
		});
	}

	// 过滤背景文件及台标
	public static void getCopyCaptionFileList(final List<CopyFile> copyFileList, File file) {
		file.listFiles(new FileFilter() {
			@SuppressLint("DefaultLocale")
			public boolean accept(File file) {
				String name = file.getName();
				int i = name.lastIndexOf('.');
				if (i > -1) {
					CopyFile copyFile = new CopyFile();
					String fileName = name.substring(0, i);
					String extend = name.substring(i);
					if (DEBUG)
						Log.e(TAG, "fileName=" + fileName + "; extend=" + extend);
					if (!name.toUpperCase().contains("COPY") && getTextExtens().contains(extend.toUpperCase())) {
						copyFile.mCategory = Utils.CAPTION_PATH;
						copyFile.mFileExtend = extend;
						copyFile.mFileName = fileName;
						copyFile.mFilePath = file.getAbsolutePath();
						copyFileList.add(copyFile);
					}
				}
				return false;
			}
		});
	}

	// added by guofq
	public static void getCopyRootFileList(final List<CopyFile> copyFileList, File file) {
		if (file == null)
			return;

		file.listFiles(new FileFilter() {
			@SuppressLint("DefaultLocale")
			public boolean accept(File file) {
				String name = file.getName();
				int i = name.lastIndexOf('.');
				if (i > -1) {
					CopyFile copyFile = new CopyFile();
					String fileName = name.substring(0, i);
					String extend = name.substring(i);
					Log.e(TAG,
							"getCopyRootFileList...file name=" + fileName + "; file path =" + file.getAbsolutePath());
					if (extend == null)
						return false;
					if (getVideoExtens().contains(extend.toUpperCase())) {
						copyFile.mCategory = Utils.MEDIA_PATH;
						copyFile.mFileExtend = extend;
						copyFile.mFileName = fileName;
						copyFile.mFilePath = file.getAbsolutePath();
						copyFileList.add(copyFile);
					} else if (getMusicExtens().contains(extend.toUpperCase())) {
						copyFile.mCategory = Utils.MEDIA_PATH;
						copyFile.mFileExtend = extend;
						copyFile.mFileName = fileName;
						copyFile.mFilePath = file.getAbsolutePath();
						copyFileList.add(copyFile);
					} else if (getImageExtens().contains(extend.toUpperCase())) {
						if (fileName.equals(Utils.LOGO_FILE) || fileName.equals(Utils.LAND_BACKGROUND)
								|| fileName.equals(Utils.PORT_BACKGROUND)) {
							copyFile.mCategory = Utils.LOGO_PATH;
						} else {
							copyFile.mCategory = Utils.MEDIA_PATH;
						}

						copyFile.mFilePath = file.getAbsolutePath();
						copyFile.mFileExtend = extend;
						copyFile.mFileName = fileName;
						copyFileList.add(copyFile);
					} else if (!name.toUpperCase().contains("COPY") && getTextExtens().contains(extend.toUpperCase())) {
						copyFile.mCategory = Utils.CAPTION_PATH;
						copyFile.mFileExtend = extend;
						copyFile.mFileName = fileName;
						copyFile.mFilePath = file.getAbsolutePath();
						copyFileList.add(copyFile);
					} else if (extend.toUpperCase().equals(".XML")) {
						copyFile.mCategory = "";
						copyFile.mFileExtend = extend;
						copyFile.mFileName = fileName;
						copyFile.mFilePath = file.getAbsolutePath();
						copyFileList.add(copyFile);
					}

				} else if (file.isDirectory()) {
					getCopyRootFileList(copyFileList, file);
				}
				return false;
			}
		});
	}

	// added by guofq
	public static void getCopyFileList(final List<CopyFile> copyFileList, File file) {
		if (file == null)
			return;

		file.listFiles(new FileFilter() {
			@SuppressLint("DefaultLocale")
			public boolean accept(File file) {
				String name = file.getName();
				if (DEBUG)
					Log.e(TAG, "name=" + name);
				int i = name.lastIndexOf('.');
				if (i > -1) {
					CopyFile copyFile = new CopyFile();
					String fileName = name.substring(0, i);
					String extend = name.substring(i);
					if (DEBUG)
						Log.e(TAG, "fileName=" + fileName + "; extend=" + extend);
					if (extend == null)
						return false;
					if (getVideoExtens().contains(extend.toUpperCase())) {
						copyFile.mCategory = Utils.MEDIA_PATH;
						copyFile.mFileExtend = extend;
						copyFile.mFileName = fileName;
						copyFile.mFilePath = file.getAbsolutePath();
						copyFileList.add(copyFile);
					} else if (getMusicExtens().contains(extend.toUpperCase())) {
						copyFile.mCategory = Utils.MEDIA_PATH;
						copyFile.mFileExtend = extend;
						copyFile.mFileName = fileName;
						copyFile.mFilePath = file.getAbsolutePath();
						copyFileList.add(copyFile);
					} else if (getImageExtens().contains(extend.toUpperCase())) {
						copyFile.mCategory = Utils.MEDIA_PATH;
						copyFile.mFileExtend = extend;
						copyFile.mFileName = fileName;
						copyFile.mFilePath = file.getAbsolutePath();
						copyFileList.add(copyFile);
					}

				} else if (file.isDirectory() && !file.getName().toUpperCase().equals("AD")
						&& !file.getName().toUpperCase().equals("LOGO")) {
					getCopyFileList(copyFileList, file);
				}
				return false;
			}
		});
	}

	// added by guofq
	public static void getCaptionFileList(final List<String> captionList, File file) {
		if (DEBUG)
			Log.e(TAG, "getCaptionFileList...file=" + file);

		// 传入文件为空，返回。
		if (file == null)
			return;

		file.listFiles(new FileFilter() {
			@SuppressLint("DefaultLocale")
			// 是否接收某个文件作为字幕文件。
			public boolean accept(File file) {

				String name = file.getName();
				if (DEBUG)
					Log.e(TAG, "name=" + name);
				int i = name.lastIndexOf('.');
				if (i > -1) {
					// 截取.txt前面的字符，作为文件名列表。
					String fileName = name.substring(0, i);
					// 截取文件类型 “txt”
					String extend = name.substring(i);
					if (DEBUG)
						Log.e(TAG, "fileName=" + fileName + "; extend=" + extend);
					if (extend == null)
						return false;
					// txt
					if (!name.toUpperCase().contains("COPY") && getTextExtens().contains(extend.toUpperCase())) {
						captionList.add(name);
					}
				} else if (file.isDirectory()) {
					getCaptionFileList(captionList, file);
				}
				return false;
			}
		});
	}

	// 初始化动画
	public static List<Animation> initAniamtion(Context context) {
		List<Animation> mAnimationList = new ArrayList<Animation>();
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.transition_in));
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.to_large));
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.to_small));
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.alpha_rotate_in));
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.alpha_rotate_out));
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.alpha_scale_translate_in));
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.alpha_scale_translate_out));
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.bounce_in));
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.bounce_out));
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.load_photo));
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.slide_in));
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.slide_out));
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.slide_in_vertical));
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.slide_out_vertical));
		mAnimationList.add(AnimationUtils.loadAnimation(context, R.anim.load_photo));
		return mAnimationList;
	}

	public static int getPixels(int pixels, int scale) {
		return (int) pixels * scale;
	}

	/// <summary>
	/// 计算文件大小函数(保留两位小数),Size为字节大小
	/// </summary>
	/// <param name="Size">初始文件大小</param>
	/// <returns></returns>
	public static String convertFileSize(long filesize) {
		String strUnit = "Bytes";
		String strAfterComma = "";
		int intDivisor = 1;
		if (filesize >= 1024 * 1024) {
			strUnit = "MB";
			intDivisor = 1024 * 1024;
		} else if (filesize >= 1024) {
			strUnit = "KB";
			intDivisor = 1024;
		}
		if (intDivisor == 1)
			return filesize + " " + strUnit;
		strAfterComma = "" + 100 * (filesize % intDivisor) / intDivisor;
		if (strAfterComma == "")
			strAfterComma = ".0";
		return filesize / intDivisor + "." + strAfterComma + " " + strUnit;
	}

	/**
	 * 根据路径获得圆角图片的方法
	 *
	 * @param path
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(String path) {
		Bitmap bitmap = null;
		File mFile = new File(path);
		// 若该文件存在
		if (mFile.exists()) {
			bitmap = BitmapFactory.decodeFile(path);
			if (bitmap != null) {
				Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
				Canvas canvas = new Canvas(output);
				// hex颜色
				final int color = 0xff424242;
				final Paint paint = new Paint();
				final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
				final RectF rectF = new RectF(rect);

				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);
				paint.setColor(color);
				canvas.drawRoundRect(rectF, 15, 15, paint);

				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
				canvas.drawBitmap(bitmap, rect, rect, paint);
				bitmap = output;
			}
		}
		return bitmap;
	}

	// 判断当前设置的时间内是否有需要播放的列表
	@SuppressWarnings("unused")
	private boolean checkCurrentPlayList(long currentSystemTime, long playListStartTime, long playListEndTime,
										 long currentGroupOnTime, long currentGroupOffTime) {

		// long currentGroupOnTime =
		// formatter.parse(mCurrentGroup.getOn()+":00").getTime();
		// long currentGroupOffTime =
		// formatter.parse(mCurrentGroup.getOff()+":00").getTime();
		// 判断设置当前时间允许播放的列表
		// if(!checkCurrentPlayList(currentSystemTime, playListStartTime,
		// playListEndTime, currentGroupOnTime, currentGroupOffTime)){
		// continue;
		// }

		// if(!checkCurrentPlayList(currentSystemTime, playListStartTime,
		// playListEndTime)){
		// continue;
		// }

		if (DEBUG)
			Log.e(TAG,
					"checkCurrentPlayList; " + "; currentSystemTime=" + currentSystemTime + "; playListStartTime:"
							+ playListStartTime + "; playListEndTime=" + playListEndTime + "; currentGroupOnTime="
							+ currentGroupOnTime + "; currentGroupOffTime=" + currentGroupOffTime);

		if (currentSystemTime < playListStartTime || currentSystemTime > playListEndTime) {
			return false;
		}

		// etc: group time: 13:00~18:00
		// 1.12:30~19:00; 2. 13:30 ~ 17:30; 3. 17:30 ~18:30; 4.12:30~17:30
		if (playListStartTime <= currentGroupOnTime && playListEndTime >= currentGroupOnTime || // 1.end
				// time
				// between
				// group
				// on
				// and
				// off
				playListStartTime >= currentGroupOnTime && playListEndTime <= currentGroupOffTime
				|| playListStartTime >= currentGroupOnTime && playListStartTime <= currentGroupOffTime
				&& playListEndTime >= currentGroupOffTime
				|| playListStartTime <= currentGroupOnTime && playListEndTime >= currentGroupOnTime
				&& playListEndTime <= currentGroupOffTime) {
			if (DEBUG)
				Log.e(TAG, "checkCurrentPlayList...check current play list right!");
			return true;
		}
		if (DEBUG)
			Log.e(TAG, "checkCurrentPlayList...check current play list wrong!");
		return false;
	}

	// 获取图片截图
	public static Bitmap createVideoThumbnail(String filePath) {
		// MediaMetadataRetriever is available on API Level 8
		// but is hidden until API Level 10
		Class<?> clazz = null;
		Object instance = null;
		try {
			clazz = Class.forName("android.media.MediaMetadataRetriever");
			instance = clazz.newInstance();

			Method method = clazz.getMethod("setDataSource", String.class);
			method.invoke(instance, filePath);

			// The method name changes between API Level 9 and 10.
			if (Build.VERSION.SDK_INT <= 9) {
				return (Bitmap) clazz.getMethod("captureFrame").invoke(instance);
			} else {
				byte[] data = (byte[]) clazz.getMethod("getEmbeddedPicture").invoke(instance);
				if (data != null) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
					if (bitmap != null)
						return bitmap;
				}
				return (Bitmap) clazz.getMethod("getFrameAtTime").invoke(instance);
			}
		} catch (IllegalArgumentException ex) {
			// Assume this is a corrupt video file
		} catch (RuntimeException ex) {
			// Assume this is a corrupt video file.
		} catch (InstantiationException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} catch (InvocationTargetException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} catch (NoSuchMethodException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} catch (IllegalAccessException e) {
			Log.e(TAG, "createVideoThumbnail", e);
		} finally {
			try {
				if (instance != null) {
					clazz.getMethod("release").invoke(instance);
				}
			} catch (Exception ignored) {
			}
		}
		return null;
	}

	public static Bitmap compressImageFromFile(Activity activity, String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		WindowManager windowManager = activity.getWindowManager();
		DisplayMetrics displayMetrics = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(displayMetrics);
		float hh = displayMetrics.widthPixels;
		float ww = displayMetrics.heightPixels;
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0) {
			be = 1;
		}
		newOpts.inSampleSize = be;// 设置采样率
		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return bitmap;
	}

	public static String subMaxScrollText(String text) {
		if (!TextUtils.isEmpty(text)) {
			if (text.length() > Utils.MAX_SCROLL_TEXT_LENGTH) {
				return text.substring(0, Utils.MAX_SCROLL_TEXT_LENGTH);
			}
		}
		return text;
	}

	public static class ViewSize {
		int width;
		int height;
	}

	public static Bitmap loadImageFromLocal(final String path, final View view) {
		Bitmap bm = null;
		// 加载图片
		// 图片的压缩
		// 1、获得图片需要显示的大小
		ViewSize viewSize = getViewSize(view);
		// 2、压缩图片
		bm = decodeSampledBitmapFromPath(path, viewSize.width, viewSize.height);
		return bm;
	}

	public static Bitmap loadImageFromLocal(Context context, int resId, final View view) {
		Bitmap bm = null;
		// 加载图片
		// 图片的压缩
		// 1、获得图片需要显示的大小
		ViewSize viewSize = getViewSize(view);
		// 2、压缩图片
		bm = decodeSampledBitmapFromPath(context, resId, viewSize.width, viewSize.height);
		return bm;
	}

	/**
	 * 根据View获适当的压缩的宽和高
	 *
	 * @param imageView
	 * @return
	 */
	public static ViewSize getViewSize(View view) {

		ViewSize imageSize = new ViewSize();
		DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();

		LayoutParams lp = view.getLayoutParams();

		int width = view.getWidth();// 获取imageview的实际宽度
		if (width <= 0 && lp != null) {
			width = lp.width;// 获取imageview在layout中声明的宽度
		}

		if (width <= 0) {
			// width = imageView.getMaxWidth();// 检查最大值
			width = ImageSizeUtil.getImageViewFieldValue(view, "mMaxWidth");
		}
		if (width <= 0) {
			width = displayMetrics.widthPixels;
		}

		int height = view.getHeight();// 获取imageview的实际高度
		if (height <= 0 && lp != null) {
			height = lp.height;// 获取imageview在layout中声明的宽度
		}

		if (height <= 0) {
			height = ImageSizeUtil.getImageViewFieldValue(view, "mMaxHeight");// 检查最大值
		}

		if (height <= 0) {
			height = displayMetrics.heightPixels;
		}
		imageSize.width = width;
		imageSize.height = height;
		return imageSize;
	}

	/**
	 * 根据图片需要显示的宽和高对图片进行压缩
	 *
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	private static Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
		// 获得图片的宽和高，并不把图片加载到内存中
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = ImageSizeUtil.caculateInSampleSize(options, width, height);
		// 使用获得到的InSampleSize再次解析图片
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}

	/**
	 * 根据图片需要显示的宽和高对图片进行压缩
	 *
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	private static Bitmap decodeSampledBitmapFromPath(Context context, int resid, int width, int height) {
		// 获得图片的宽和高，并不把图片加载到内存中
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(context.getResources(), resid, options);
		options.inSampleSize = ImageSizeUtil.caculateInSampleSize(options, width, height);
		// 使用获得到的InSampleSize再次解析图片
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resid, options);
		return bitmap;
	}
}
