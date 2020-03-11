package com.worldchip.advertising.client.view;

import java.io.File;

import com.worldchip.advertising.client.entity.Caption;
import com.worldchip.advertising.client.utils.DefiTrans;
import com.worldchip.advertising.client.utils.SetupUtils;
import com.worldchip.advertising.client.utils.ScreenUtils;
import com.worldchip.advertising.client.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class AdvertisingView extends AbsoluteLayout {

	private static final boolean DEBUG = false;
	private Context mContext;
	private AbsoluteLayout mCustomLayout;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					invalidate();
					mHandler.sendEmptyMessageDelayed(0, 50);
					break;
			}
			super.handleMessage(msg);
		}
	};

	public AdvertisingView(Context context) {
		super(context);
		this.mContext = context;
		initView();
	}

	public AdvertisingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		initView();
	}

	public AdvertisingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView();
	}

	// 控件初始化
	private void initView() {
		mCustomLayout = new AbsoluteLayout(mContext);
		LayoutParams layoutParams = new LayoutParams(Utils.WIDTH_PIXELS, Utils.HEIGHT_PIXELS, 0, 0);
		mCustomLayout.setLayoutParams(layoutParams);
		mCustomLayout.setBackgroundColor(Color.parseColor("#00000000"));

		addView(mCustomLayout);
		// mHandler.sendEmptyMessageDelayed(0, 50);
	}

	/**
	 * 清空布局
	 */
	public void emptyLayout() {
		if (mCustomLayout != null) {
			mCustomLayout.removeAllViews();
		}
	}

	/**
	 * 清空视频
	 */
	public void emptyVideoView() {
		if (mCustomLayout != null) {
			for (int n = 0; n < mCustomLayout.getChildCount(); n++) {
				if (mCustomLayout.getChildAt(n) instanceof RelativeLayout) {
					RelativeLayout relativeLayout = (RelativeLayout) mCustomLayout.getChildAt(n);
					if (relativeLayout.getChildAt(0) instanceof VideoView) {
						if (DEBUG)
							Log.e("emptyVideoView", "emptyVideoView-----视频删除了");
						mCustomLayout.removeViewAt(n);
					}
					break;
				}
			}
		}
	}

	/**
	 * 添加音乐到 mCustomLayout中
	 *
	 * @param view
	 */
	public void addMusicLayout(View view) {
		emptyLayout();
		mCustomLayout.addView(view);
	}

	/**
	 * 创建播放状态，显示暂停等信息
	 *
	 * @return
	 */
	public TextView CreatePlayIconTextView() {
		TextView textView = new TextView(mContext);
		LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				Utils.PLAY_ICON_WIDTH_STEP, Utils.HEIGHT_PIXELS - Utils.PLAY_ICON_HEIGHT_STEP);
		textView.setLayoutParams(params);
		textView.getPaint().setFakeBoldText(true);// 设置粗体
		textView.setBackgroundColor(Color.parseColor(SetupUtils.CAPTION_BG_COLOR_TRANSPARENT));
		textView.setTextColor(Color.parseColor(SetupUtils.CAPTION_TEXT_COLOR_WHITE));
		textView.setTextSize(56);
		textView.setTag("play_icon");
		addView(textView);
		return textView;
	}

	/**
	 * 创建一个时间
	 *
	 * @return
	 */

	public TextView CreateTimerTextView(int location) {
		TextView textView = new TextView(mContext);
		LinearLayout.LayoutParams params = null;
		/**
		 * 重新调整过的时间位置显示，完美解决时间显示问题，
		 */
		switch (location) {
			case 1: // left upper
				params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				textView.setPadding(DefiTrans.dip2px(mContext, 30), DefiTrans.dip2px(mContext, 19), 0, 0);
				break;
			case 0: // right upper
				params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.CENTER_HORIZONTAL;
				textView.setGravity(Gravity.RIGHT);
				textView.setPadding(0, DefiTrans.dip2px(mContext, 19), DefiTrans.dip2px(mContext, 40), 0);
				break;
		}
		textView.setLayoutParams(params);
		textView.setBackgroundColor(Color.parseColor(SetupUtils.CAPTION_BG_COLOR_TRANSPARENT));
		textView.setTextColor(Color.parseColor(SetupUtils.CAPTION_TEXT_COLOR_WHITE));
		//textView.setTextSize(42);
		textView.setTag("timing");
		addView(textView);
		return textView;
	}

	/**
	 * 创建一个LOGO
	 *
	 * @return
	 */
	@SuppressLint("NewApi")
	public SXImageView CreateLogoImageView(int location ) {
		SXImageView imageView = new SXImageView(mContext);
		LayoutParams params = null;
		Drawable drawable = null;
		//	LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 40, 40);

		//  如果台标要在根目录下可以从此处修改。
		try {
			String filePath = Utils.CURRENT_ROOT_PATH + Utils.LOGO_PATH + Utils.LOGO_FILE;
			File file = new File(filePath);
			if (file.exists()) {
				drawable = Drawable.createFromPath(filePath);
				imageView.setBackgroundDrawable(drawable);
			} else {
				imageView.setBackgroundDrawable(null);
			}
		} catch (Exception err) {
			imageView.setBackground(null);
		}

		///////////
		switch (location) {
			case 1:// 左上
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 40, 40);
				break;
			case 2:// 右上
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, ScreenUtils.getScreenWidth(mContext) - 40 - drawable.getIntrinsicWidth(), 40);
				break;
			case 3:// 左下
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 40, ScreenUtils.getScreenHeight(mContext) - 40 - drawable.getIntrinsicHeight());
				break;
			case 4:// 右下
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, ScreenUtils.getScreenWidth(mContext) - 40 - drawable.getIntrinsicWidth(),
						ScreenUtils.getScreenHeight(mContext) - 40 - drawable.getIntrinsicHeight());
				break;

			default:
				params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 40, 40);
				break;
		}
		imageView.setLayoutParams(params);
		imageView.setScaleType(ScaleType.CENTER);
		imageView.setTag("logo_img");
		addView(imageView);
		return imageView;
	}

	/**
	 * 创建一个TextView
	 *
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param x
	 *            X轴坐标
	 * @param y
	 *            Y轴坐标
	 * @param text
	 *            显示内容
	 * @param flag
	 *            内容是否滚动
	 * @param displayMode
	 *            显示方式
	 */
	public TextView CreateTextView(int width, int height, String text, int displayMode) {
		TextView textView = new TextView(mContext);
		LayoutParams params = new LayoutParams(width, height, 0, 0);
		textView.setLayoutParams(params);
		textView.setBackgroundColor(Color.parseColor(SetupUtils.CAPTION_TEXT_COLOR_BLACK));
		textView.setTextColor(Color.parseColor(SetupUtils.CAPTION_TEXT_COLOR_WHITE));
		textView.setTextSize(54);
		textView.setText(text);
		textView.setSingleLine(true);
		textView.setGravity(displayMode);
		addView(textView);
		return textView;
	}

	/**
	 * 创建一个提示语的TextView
	 *
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param x
	 *            X轴坐标
	 * @param y
	 *            Y轴坐标
	 * @param text
	 *            显示内容
	 * @param flag
	 *            内容是否滚动
	 * @param displayMode
	 *            显示方式
	 */
	public TextView CreateMessageTextView(int width, int height, String text, int displayMode) {
		TextView textView = new TextView(mContext);
		LayoutParams params = new LayoutParams(width, height, 0, 0);
		textView.setLayoutParams(params);
		textView.setBackgroundColor(Color.parseColor(SetupUtils.CAPTION_TEXT_COLOR_BLACK));
		textView.setTextColor(Color.parseColor(SetupUtils.CAPTION_TEXT_COLOR_WHITE));
		textView.setTextSize(54);
		textView.setText(text);
		textView.setSingleLine(true);
		textView.setGravity(displayMode);
		this.mCustomLayout.addView(textView);
		return textView;
	}

	/**
	 * 创建一个滚动的TextView
	 *
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param x
	 *            X轴坐标
	 * @param y
	 *            Y轴坐标
	 * @param text
	 *            显示内容
	 */
	@SuppressLint("DefaultLocale")
	public AutoCustomTextView CreateCustomTextView(int width, int height, int x, int y, Caption captionSetting,
												   String text) {
		AutoCustomTextView autoCustomTextView = new AutoCustomTextView(mContext);
		LayoutParams params = null;
		if (y == -1) {
			if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_LARGE_SIZE) {
				params = new LayoutParams(width, Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP, x,
						Utils.HEIGHT_PIXELS - Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP);
			} else if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_SMALL_SIZE) {
				params = new LayoutParams(width, Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP, x,
						Utils.HEIGHT_PIXELS - Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP);
			} else if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_NROMAL_SIZE) {
				params = new LayoutParams(width, Utils.SCOLL_NORMAL_TEXT_HEIGHT_STEP, x,
						Utils.HEIGHT_PIXELS - Utils.SCOLL_NORMAL_TEXT_HEIGHT_STEP);
			}
		} else {
			if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_LARGE_SIZE) {
				if (y >= (Utils.HEIGHT_PIXELS - Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP)) {
					params = new LayoutParams(width, Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP, x,
							Utils.HEIGHT_PIXELS - Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP);
				} else {
					params = new LayoutParams(width, Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP, x, y);
				}
			} else if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_SMALL_SIZE) {
				if (y >= (Utils.HEIGHT_PIXELS - Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP)) {
					params = new LayoutParams(width, Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP, x,
							Utils.HEIGHT_PIXELS - Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP);
				} else {
					params = new LayoutParams(width, Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP, x, y);
				}
			} else if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_NROMAL_SIZE) {
				if (y >= (Utils.HEIGHT_PIXELS - Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP)) {
					params = new LayoutParams(width, Utils.SCOLL_NORMAL_TEXT_HEIGHT_STEP, x,
							Utils.HEIGHT_PIXELS - Utils.SCOLL_NORMAL_TEXT_HEIGHT_STEP);
				} else {
					params = new LayoutParams(width, Utils.SCOLL_NORMAL_TEXT_HEIGHT_STEP, x, y);
				}
			}
		}
		autoCustomTextView.setLayoutParams(params);
		autoCustomTextView.setTextSize(captionSetting.getSize());
		autoCustomTextView.setTextVules(text);
		autoCustomTextView.setCaptionSetting(captionSetting);
		addView(autoCustomTextView);
		return autoCustomTextView;
	}

	@SuppressLint("DefaultLocale")
	public AutoScrollTextView CreateAutoScrollTextView(int width, int height, int x, int y, Caption captionSetting,
													   String text) {
		AutoScrollTextView autoScrollTextView = new AutoScrollTextView(mContext);
		LayoutParams params = null;
		if (y == -1) {
			if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_LARGE_SIZE) {
				params = new LayoutParams(width, Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP, x,
						Utils.HEIGHT_PIXELS - Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP);
			} else if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_SMALL_SIZE) {
				params = new LayoutParams(width, Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP, x,
						Utils.HEIGHT_PIXELS - Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP);
			} else if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_NROMAL_SIZE) {
				Log.d("534534543", "6456464654iiiiiiii");
				params = new LayoutParams(width, Utils.SCOLL_NORMAL_TEXT_HEIGHT_STEP, x,
						Utils.HEIGHT_PIXELS - Utils.SCOLL_NORMAL_TEXT_HEIGHT_STEP);
			}
		} else if (y == -2) {
			//	Log.d("534534543", "6456464654pppppp");
			//	Log.d("534534543", "6456464654yyyyyyyy=====" + y);
			if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_LARGE_SIZE) {
				params = new LayoutParams(width, Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP, x, 0);
			} else if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_SMALL_SIZE) {
				params = new LayoutParams(width, Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP, x, 0);
			} else if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_NROMAL_SIZE) {
				params = new LayoutParams(width, Utils.SCOLL_NORMAL_TEXT_HEIGHT_STEP, x, 0);
			}

		} else {

			//	Log.d("534534543", "6456464654bbbbbbbb");
			//	Log.d("534534543", "6456464654yyyyyyyy=====" + y);
			if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_LARGE_SIZE) {
				if (y > (Utils.HEIGHT_PIXELS - Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP)) {
					params = new LayoutParams(width, Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP, x,
							Utils.HEIGHT_PIXELS - Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP);
				} else {
					params = new LayoutParams(width, Utils.SCOLL_LARGE_TEXT_HEIGHT_STEP, x, y);
				}
			} else if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_SMALL_SIZE) {
				if (y > (Utils.HEIGHT_PIXELS - Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP)) {
					params = new LayoutParams(width, Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP, x,
							Utils.HEIGHT_PIXELS - Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP);
				} else {
					params = new LayoutParams(width, Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP, x, y);
				}
			} else if (captionSetting.getSize() == SetupUtils.CAPTION_TEXT_NROMAL_SIZE) {
				if (y > (Utils.HEIGHT_PIXELS - Utils.SCOLL_SMALL_TEXT_HEIGHT_STEP)) {
					params = new LayoutParams(width, Utils.SCOLL_NORMAL_TEXT_HEIGHT_STEP, x,
							Utils.HEIGHT_PIXELS - Utils.SCOLL_NORMAL_TEXT_HEIGHT_STEP);
				} else {
					params = new LayoutParams(width, Utils.SCOLL_NORMAL_TEXT_HEIGHT_STEP, x, y);
				}
			}

		}

		autoScrollTextView.setLayoutParams(params);
		autoScrollTextView.setTextSize(captionSetting.getSize());
		autoScrollTextView.setTextVules(text);
		autoScrollTextView.setCaptionSetting(captionSetting);
		addView(autoScrollTextView);
		return autoScrollTextView;
	}

	/**
	 * 创建一个PageView
	 *
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param x
	 *            X轴坐标
	 * @param y
	 *            Y轴坐标
	 */
	// 自动播放图片的ViewPager.
	public AutoViewPager CreateAutoPager(int width, int height, int x, int y) {
		AutoViewPager viewPage = new AutoViewPager(mContext);
		LayoutParams params = new LayoutParams(width, height, x, y);
		viewPage.setLayoutParams(params);
		this.mCustomLayout.addView(viewPage);
		return viewPage;
	}

	/**
	 * 创建一个ImageView
	 *
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param x
	 *            X轴坐标
	 * @param y
	 *            Y轴坐标
	 */
	/*
	 * public AutoImageView CreateImageView(int width,int height,int x,int y) {
	 * AutoImageView imageView = new AutoImageView(mContext); LayoutParams
	 * params = new LayoutParams(width, height, x, y);
	 * imageView.setLayoutParams(params); this.mCustomLayout.addView(imageView);
	 * return imageView; }
	 */
	/**
	 * 创建一个ImageView
	 *
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param x
	 *            X轴坐标
	 * @param y
	 *            Y轴坐标
	 */
	public AutoImageView CreateImageView(int width, int height, int x, int y) {
		AutoImageView imageView = new AutoImageView(mContext);
		LayoutParams params = new LayoutParams(width, height, x, y);
		imageView.setLayoutParams(params);
		this.mCustomLayout.addView(imageView);
		return imageView;
	}

	/**
	 * 添加一个视频
	 *
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param x
	 *            X轴坐标
	 * @param y
	 *            Y轴坐标
	 * @return
	 */
	public VideoView CreateVideoView(Handler handler, int width, int height, int x, int y, int backgroundResId) {
		RelativeLayout relativeLayout = new RelativeLayout(mContext);
		LayoutParams layoutParams = new LayoutParams(width, height, x, y);
		relativeLayout.setLayoutParams(layoutParams);
		relativeLayout.setGravity(Gravity.CENTER);
		VideoView videoView = new VideoView(mContext, handler);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
		videoView.setLayoutParams(params);
		relativeLayout.addView(videoView);
		if (backgroundResId != -1) {
			relativeLayout.setBackgroundResource(backgroundResId);
		}
		this.mCustomLayout.addView(relativeLayout);
		return videoView;
	}
}