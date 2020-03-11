package com.worldchip.advertising.client.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.worldchip.advertising.client.copy.CopyFile;
import com.worldchip.advertising.client.copy.CopyItemView;
import com.worldchip.advertising.client.copy.ICopyEnd;
import com.worldchip.advertising.client.utils.Common;
import com.worldchip.advertising.client.utils.SetupUtils;
import com.worldchip.advertising.client.utils.Utils;
import com.worldchip.advertisingclient.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CopyActivity extends Activity implements ICopyEnd {

	private static final String TAG = "--CopyActivity--";
	// private static final boolean DEBUG = false;
	private String mFromRootPath;

	private boolean mDeleteAllCopy;
	private boolean mAppendCopy;

	private List<CopyFile> mCopyFileList;
	private LinearLayout mCopyViewList;

	private TextView mInfo;
	private TextView mCopyInfo;
	private Resources mRes;

	private SharedPreferences mPrefs;
	private boolean mCopyRootFiles;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mRes = getResources();
		mPrefs = this.getSharedPreferences("advertis_setting", 0);

		int valueIndex = SetupUtils.getCopyMode(this);
		if (valueIndex == 0) { // 全删全拷
			mDeleteAllCopy = true;// 先删除所有
			mAppendCopy = false;
		} else { // 追加拷贝
			mDeleteAllCopy = false;
			mAppendCopy = true;
		}

		Log.e(TAG, "onCreate..mCopyDeleteAll: " + mDeleteAllCopy);
		mFromRootPath = getIntent().getStringExtra("from_root_path");

		Log.e(TAG, "onCreate...mFromRootPath=" + mFromRootPath);
		if (mFromRootPath == null || mFromRootPath.equals("")) {
			startIdActivity();
			this.finish();
		}

		mCopyRootFiles = true;
		if (mFromRootPath.contains(Utils.PLAY_LIST_DIR)) {
			mCopyRootFiles = false;
		}

		Log.e(TAG, "onCreate..mCopyRootFiles: " + mCopyRootFiles);
		startMediaUnMountedReceiver();
		Toast.makeText(CopyActivity.this, mRes.getString(R.string.no_devices), Toast.LENGTH_SHORT).show();
		if (mDeleteAllCopy) {
			deleteAllTargetFiles();
		}

		setContentView(R.layout.copy_main);

		initView();
		copyFile();
	}

	private void deleteAllTargetFiles() {
		Log.e(TAG, "deleteAllTargetFiles...targetRootPath=" + Utils.CURRENT_ROOT_PATH);
		File targetRootPath = new File(Utils.CURRENT_ROOT_PATH);
		if (targetRootPath.exists()) {
			recursionDeleteFile(targetRootPath);
		}
	}

	// 递归删除所有文件
	public static void recursionDeleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}
		if (file.isDirectory()) {
			File[] childFile = file.listFiles();
			if (childFile == null || childFile.length == 0) {
				file.delete();
				return;
			}
			for (File f : childFile) {
				recursionDeleteFile(f);
			}
			file.delete();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.e(TAG, "onResume..mCopyDeleteAll: " + mDeleteAllCopy);
	};

	private void initView() {
		mCopyFileList = new ArrayList<CopyFile>();

		mCopyViewList = (LinearLayout) findViewById(R.id.copy_view);
		mInfo = (TextView) findViewById(R.id.info);
		mCopyInfo = (TextView) findViewById(R.id.copy_info);
	}

	private void startIdActivity() {
		try {
			this.setResult(1000);
			Intent i = new Intent(CopyActivity.this, IdleActivity.class);
			CopyActivity.this.startActivity(i);
			this.finish();
		} catch (Exception err) {
			Log.e(TAG, "startMainActivity..Exception: " + err.getMessage());
			System.exit(0);
		}
	}

	/**
	 * 拷贝文件
	 *
	 * @return boolean
	 */
	public void copyFile() {
		// 拷贝源
		getCopyFileList();

		if (mCopyFileList.size() < 1) {
			Toast.makeText(this, mRes.getString(R.string.external_storage) + "  " + mFromRootPath + "  "
					+ mRes.getString(R.string.no_folder), Toast.LENGTH_LONG).show();
			startIdActivity();
			return;
		}

		mInfo.setText(mRes.getString(R.string.total) + "  " + mCopyFileList.size() + "  "
				+ mRes.getString(R.string.file_synchronized_copy));
		mCopyInfo.setText(
				mRes.getString(R.string.Copying) + "  " + mCopyFileList.size() + "  " + mRes.getString(R.string.file));

		// 目标目录
		File targetRootPath = new File(Utils.CURRENT_ROOT_PATH);
		if (!targetRootPath.exists()) {
			targetRootPath.mkdirs();
		}

		// 判断目标路径及目标文件是否存在
		File targetFile;
		for (CopyFile copyFile : mCopyFileList) {

			Log.e(TAG, "copyFile...copyFile =" + copyFile.mFilePath + "; " + "copyFile.mFileExtend="
					+ copyFile.mFileExtend + "; " + "copyFile.mCategory=" + copyFile.mCategory);
			File fromFile = new File(copyFile.mFilePath);
			targetFile = new File(
					Utils.CURRENT_ROOT_PATH + copyFile.mCategory + copyFile.mFileName + copyFile.mFileExtend);
			if (fromFile.exists() && targetFile.exists()) {
				if (copyFile.mCategory == null || copyFile.mCategory.equals("")) {
					targetFile.delete();
					CopyItemView copyItemView = new CopyItemView(this, mFromRootPath, mAppendCopy, copyFile);
					this.mCopyViewList.addView(copyItemView);
					copyItemView.start();
				}
			} else {
				CopyItemView copyItemView = new CopyItemView(this, mFromRootPath, mAppendCopy, copyFile);
				this.mCopyViewList.addView(copyItemView);
				copyItemView.start();
			}
		}

		if (this.mCopyViewList.getChildCount() <= 0) {
			resetPlayState();
			Toast.makeText(CopyActivity.this, mRes.getString(R.string.file_update_success), Toast.LENGTH_LONG).show();
			startIdActivity();
			return;
		}
	}

	private void getCopyFileList() {
		mCopyFileList.clear();
		// 配置文件
		File file = new File(mFromRootPath);

		if (mCopyRootFiles) {
			Common.getCopyRootFileList(mCopyFileList, file);
		} else {
			Common.getCopyConfigFileList(mCopyFileList, file);
			// 媒体文件
			file = new File(mFromRootPath + Utils.MEDIA_PATH);
			Common.getCopyFileList(mCopyFileList, file);
			// 滚动字幕
			file = new File(mFromRootPath + Utils.CAPTION_PATH);
			Common.getCopyCaptionFileList(mCopyFileList, file);
			// 插播文件
			file = new File(mFromRootPath + Utils.AD_PATH);
			Common.getCopyADFileList(mCopyFileList, file);
			// 背景文件
			file = new File(mFromRootPath + Utils.LOGO_PATH);
			Common.getCopyLogoFileList(mCopyFileList, file);
		}
	}

	// 媒体卸载监听，动态注册广播。
	private void startMediaUnMountedReceiver() {
		IntentFilter intentFilter = new IntentFilter();
		// ACTION_MEDIA_BAD_REMOVAL
		// ACTION_MEDIA_UNMOUNTED
		intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.setPriority(2147483647);
		intentFilter.addDataScheme("file");
		registerReceiver(mMediaMountedReceiver, intentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
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
		super.onDestroy();
	}

	@Override
	public void copyEnd(CopyItemView copyItemView) {

		mCopyInfo.setText(copyItemView.getFileName() + mRes.getString(R.string.copy_success) + "\n"
				+ mRes.getString(R.string.copies_remaining) + (mCopyViewList.getChildCount() - 1)
				+ mRes.getString(R.string.file));
		this.mCopyViewList.removeView(copyItemView);

		if (this.mCopyViewList.getChildCount() < 1) {
			resetPlayState();
			Toast.makeText(CopyActivity.this, mRes.getString(R.string.file_update_success), Toast.LENGTH_LONG).show();
			startIdActivity();
			return;
		}
	};

	// 拷贝发生后，对一些播放记录复位。并且清空播放记录
	private void resetPlayState() {
		try {
			// 利用preference的PUT方法 向里面写入初始值，来重置。
			mPrefs.edit().putInt("media_play_duration", 0).commit();
			mPrefs.edit().putInt("media_position", 0).commit();
			mPrefs.edit().putInt("music_play_duration", 0).commit();
			mPrefs.edit().putInt("music_position", 0).commit();
		} catch (Exception err) {
			return;
		}
	}

	// 为什么在copyActivity里面也有关于媒体插入的监听。

	private BroadcastReceiver mMediaMountedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 获取动态注册的广播的ACTION。
			if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED) || action.equals(Intent.ACTION_MEDIA_BAD_REMOVAL)) {
				String path = intent.getData().getPath();

				// int storageIndex =
				// SetupUtils.getSetupValueIndex(CopyActivity.this,
				// SetupUtils.PRIORITY_MODE);
				int storageIndex = 0;
				Log.e(TAG, "mMediaMountedReceiver..unmounted...path=" + path + "; storageIndex=" + storageIndex);
				if (path.contains("extsd") && storageIndex > 0) {
					resetPlayState();
					Toast.makeText(CopyActivity.this, mRes.getString(R.string.copy_sd), Toast.LENGTH_LONG).show();
					startIdActivity();
				} else if (path.contains("usbhost") && storageIndex > 0) {
					resetPlayState();
					Toast.makeText(CopyActivity.this, mRes.getString(R.string.copy_usb), Toast.LENGTH_LONG).show();
					startIdActivity();
				}
			}
		}
	};

	private void showToast(int msg) {
		Toast.makeText(this, mRes.getString(msg), Toast.LENGTH_LONG).show();
	}

	private long mExitTime = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if ((System.currentTimeMillis() - mExitTime) > 2000) {
					showToast(R.string.copy_exit);
					mExitTime = System.currentTimeMillis();
				} else {
					this.finish();
				}
				break;
		}
		return true;
	}
}