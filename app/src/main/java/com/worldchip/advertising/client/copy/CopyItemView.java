package com.worldchip.advertising.client.copy;

/**
 * 显示copy界面相关
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import com.worldchip.advertising.client.utils.Common;
import com.worldchip.advertising.client.utils.Utils;
import com.worldchip.advertisingclient.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

@SuppressLint({ "ViewConstructor", "DefaultLocale" })
public class CopyItemView extends FrameLayout {

	private static final boolean DEBUG = false;

	protected static final int COPY_FAILED = -1;
	protected static final int COPY_SUCCESS = -2;
	private final static int UPPDATE_PRORGRESS = 1;
	protected static final String TAG = "--CopyItemView--";

	private String mFromRootPath;
	private Boolean mAppendCopy;

	private CopyFile mCopyFile;
	private int mCopyTotal;
	private String mCopyInfo;

	private ImageView mImgMediaTypeIcon;
	private ProgressBar mProgressBar;
	private TextView mTvFileName;
	private TextView mTvTooltip;

	private ICopyEnd mCopyEnd;

	private CopyThread mThread;

	@SuppressWarnings("unused")
	private Context mContext;
	@SuppressWarnings("unused")
	private Resources mRes;

	public CopyItemView(Context context, String fromRootPath, boolean appendCopy, CopyFile copyFile) {
		super(context);
		mCopyEnd = (ICopyEnd) context;

		mAppendCopy = appendCopy;
		mContext = context;
		mRes = getResources();

		mFromRootPath = fromRootPath;
		mCopyFile = copyFile;

		LayoutInflater.from(context).inflate(R.layout.copy_item_layout, this);

		initView();
		if (mCopyFile != null) {
			setIcon();
		}
	}

	private void initView() {
		mImgMediaTypeIcon = (ImageView) findViewById(R.id.img_meida_type_icon);
		// 显示进度条，怎么动态更新呢？
		// 通过这个方法 mProgressBar.setProgress(mCopyTotal);
		mProgressBar = (ProgressBar) findViewById(R.id.progessbar);
		// 拷贝的文件名
		mTvFileName = (TextView) findViewById(R.id.file_name);
		// ？
		mTvTooltip = (TextView) findViewById(R.id.tool_tip);
	}

	public void start() {
		String fileName = mCopyFile.mFileName;
		if (fileName.length() > 20) {
			// 只取前20位名字显示。
			fileName = fileName.substring(0, 20) + "...";
		}
		mTvFileName.setText(fileName + mCopyFile.mFileExtend);

		// 显示百分比
		mTvTooltip.setText(mCopyTotal + "%");
		mThread = new CopyThread();
		mThread.start();
	}

	private void setIcon() {
		if (Common.getVideoExtens().contains(mCopyFile.mFileExtend.toUpperCase())) {
			mImgMediaTypeIcon.setImageResource(R.drawable.icon_video);
		} else if (Common.getMusicExtens().contains(mCopyFile.mFileExtend.toUpperCase())) {
			mImgMediaTypeIcon.setImageResource(R.drawable.icon_music);
		} else if (Common.getImageExtens().contains(mCopyFile.mFileExtend.toUpperCase())) {
			mImgMediaTypeIcon.setImageResource(R.drawable.icon_image);
		}
	}

	// 拷贝线程
	private class CopyThread extends Thread {
		@SuppressLint("SimpleDateFormat")
		@SuppressWarnings("resource")
		@Override
		public void run() {
			try {
				int length = 2097152;
				String fromFile;
				String toFile;
				// 从哪里拷贝
				fromFile = mCopyFile.mFilePath;
				// 拷贝到哪里
				toFile = Utils.CURRENT_ROOT_PATH + mCopyFile.mCategory + mCopyFile.mFileName + mCopyFile.mFileExtend;
				File targetDir = new File(Utils.CURRENT_ROOT_PATH + mCopyFile.mCategory);
				if (!targetDir.exists()) {
					targetDir.mkdirs();
				}
				if (mAppendCopy) { // 追加拷贝时，需要确认目标文件是否存在。同名覆盖。
					File targetFile = new File(toFile);
					if (targetFile.exists()) {
						mCopyHandler.sendEmptyMessage(COPY_SUCCESS);
						return;
					}
				}

				if (DEBUG)
					Log.e(TAG, "start copy...fromFile=" + fromFile + "; toFile=" + toFile);

				FileInputStream in = new FileInputStream(fromFile);
				FileOutputStream out = new FileOutputStream(toFile);

				// java.nio.channels
				FileChannel inC = in.getChannel();
				FileChannel outC = out.getChannel();
				ByteBuffer b = null;
				// 对输入输出流获得其管道,然后分批次的从f1的管道中向f2的管道中输入数据每次输入的数据最大为2097152byte
				while (true) {
					if (inC.position() == inC.size()) {
						inC.close();
						outC.close();
					}
					if ((inC.size() - inC.position()) < length) { // 最后一次拷贝，如果多于2097152byte，就只拷贝剩下的。
						length = (int) (inC.size() - inC.position());
					} else
						length = 2097152;
					b = ByteBuffer.allocateDirect(length);

					// 复制进度百分比
					mCopyTotal = (int) (((inC.position() + length) * 100) / inC.size());
					mCopyInfo = "(" + Common.convertFileSize(inC.position() + length) + "/"
							+ Common.convertFileSize(inC.size()) + ")";
					mCopyHandler.sendEmptyMessage(UPPDATE_PRORGRESS);

					inC.read(b);
					b.flip();
					outC.write(b);
					outC.force(false);
				}
			} catch (Exception e) {
				e.printStackTrace();
				mCopyHandler.sendEmptyMessage(COPY_FAILED);
			}
		}
	};

	@SuppressLint("HandlerLeak")
	private Handler mCopyHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == UPPDATE_PRORGRESS) {// 其他线程更新UI
				mProgressBar.setProgress(mCopyTotal);
				//
				mTvTooltip.setText(mCopyTotal + "% " + mCopyInfo);
				if (mCopyTotal >= 100) {
					Log.d(TAG, "COPY_END ... mCopyTotal=" + mCopyTotal + "; mFileName=" + mCopyFile.mFileName);
					mCopyEnd.copyEnd(CopyItemView.this);
				}
			} else if (msg.what == COPY_SUCCESS) {
				mCopyEnd.copyEnd(CopyItemView.this);
			}
		}
	};

	public String getFileName() {
		return mCopyFile.mFileName + mCopyFile.mFileExtend;
	}
}