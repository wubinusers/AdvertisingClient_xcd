package com.worldchip.advertising.client.view;

import com.worldchip.advertising.client.entity.Caption;
import com.worldchip.advertising.client.utils.SetupUtils;
import com.worldchip.advertising.client.utils.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class AutoScrollTextView extends SurfaceView implements SurfaceHolder.Callback {
	private int mDefaultWidth = Utils.WIDTH_PIXELS; // TextView默认宽度

	private static final String TAG = AutoScrollTextView.class.getSimpleName();
	private SurfaceHolder mHolder;
	private Thread mScrollThread;
	private Paint mPaintTxt;
	private Canvas mCanvasTxt;

	// 这个值不能太大也不能太小。太大更新太慢，字幕滚动缓慢。太小跟新太快，机器性能更不上
	// 会造成播放视频和播放图片时滚动字幕速度有明显差别。
	private int REFRESH_TIME = 50;
	private int mSpeed = 2;
	private float mTextLength = 0f;
	private float mSurfaceViewWidth = 0;
	private float mSurfaceViewHeight = 0;
	private boolean isStarting = false;
	private float mTextFontSize = 20.0f;
	private int mTextColor = Color.RED;
	private int mTextHeight = 0;
	/**
	 * 内容滚动位置起始坐标
	 */
	private float mScrollX = 0;

	/**
	 * 移动方向
	 */
	private int mScrollorientation = 0;
	/**
	 * 向左移动
	 */
	public final static int MOVE_LEFT = 0;
	/**
	 * 向右移动
	 */
	public final static int MOVE_RIGHT = 1;

	private String mScrollText = "";

	public AutoScrollTextView(Context context) {
		super(context);
		this.mDefaultWidth = Utils.WIDTH_PIXELS;
		mHolder = this.getHolder();
		mHolder.addCallback(this);
		mHolder.setFormat(PixelFormat.TRANSPARENT); // 顶层绘制SurfaceView设成透明
		this.setZOrderOnTop(true);
		mPaintTxt = new Paint();
	}

	public AutoScrollTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mDefaultWidth = Utils.WIDTH_PIXELS;
	}

	public AutoScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mDefaultWidth = Utils.WIDTH_PIXELS;
	}

	public void setTextVules(String text) {
		mSurfaceViewWidth = getWidth();
		if (mSurfaceViewWidth <= 0) {
			mSurfaceViewWidth = mDefaultWidth;
		}
		mScrollText = text;
		mTextLength = mPaintTxt.measureText(text.toString());
	}

	public void setTextSize(float textSize) {
		mTextFontSize = textSize;
		resetPaint();
	}

	public void resetPaint() {
		mPaintTxt.reset();
		// 锯齿
		mPaintTxt.setAntiAlias(true);
		// 字体
		mPaintTxt.setTypeface(Typeface.SANS_SERIF);
		// 字体大小
		mPaintTxt.setTextSize(mTextFontSize);
		// 字体颜色
		mPaintTxt.setColor(mTextColor);
		mTextLength = mPaintTxt.measureText(mScrollText.toString());
		Rect rect = new Rect();
		mPaintTxt.getTextBounds("test", 0, 1, rect);// 为了提高计算速度，自定义一个字符串来计算，由于是纯英文(占一个字符串)，只截取前1个字符即可
		mTextHeight = rect.height();
	}

	/* 自定义线程 */
	class AutoScrollRunnable implements Runnable {

		public void run() {
			mSurfaceViewWidth = getWidth();
			while (isStarting) {
				try {
					synchronized (mHolder) {
						draw();
					}
					Thread.sleep(REFRESH_TIME);
				} catch (InterruptedException e) {
					Log.e(TAG, "ScrollSurfaceView：绘制失败...\r\n" + e);
				} catch (Exception e) {
					Log.e(TAG, "ScrollSurfaceView：run...\r\n" + e);
				}
			}
		}
	}

	private void draw() {
		// 锁定画布
		mCanvasTxt = mHolder.lockCanvas();
		if (mHolder == null || mCanvasTxt == null) {
			return;
		}
		// 清屏
		mCanvasTxt.drawColor(Color.TRANSPARENT, Mode.CLEAR);
		mCanvasTxt.drawText(mScrollText, mScrollX, (mSurfaceViewHeight - mTextHeight) / 2 + mTextHeight, mPaintTxt);
		// 解锁显示
		mHolder.unlockCanvasAndPost(mCanvasTxt);
		// 方向
		if (mScrollorientation == MOVE_LEFT) {// 向左
			if (mScrollX < -mTextLength) {
				mScrollX = mSurfaceViewWidth;
			} else {
				mScrollX -= mSpeed;
			}
		} else if (mScrollorientation == MOVE_RIGHT) {// 向右
			if (mScrollX >= mSurfaceViewWidth) {
				mScrollX = -mTextLength;
			} else {
				mScrollX += mSpeed;
			}
		}
	}

	@Override
	/**
	 * 当控件创建时自动执行的方法
	 */
	public void surfaceCreated(SurfaceHolder holder) {
		// 启动自定义线程
		mScrollThread = new Thread(new AutoScrollRunnable());
		mScrollThread.start();
		isStarting = true;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mSurfaceViewHeight = height;
		Log.e("lee", "mSurfaceViewHeight == " + mSurfaceViewHeight);
		mSurfaceViewWidth = width;
	}

	@Override
	/**
	 * 当控件销毁时自动执行的方法
	 * 
	 * 
	 * 
	 * 
	 */
	public void surfaceDestroyed(SurfaceHolder holder) {
		// 终止自定义线程
		isStarting = false;
		mScrollThread.interrupt();
	}

	public void setScrollOrientation(int orientation) {
		mScrollorientation = orientation;
	}

	public void setCaptionSetting(Caption captionSetting) {
		try {
			// setTextSize(captionSetting.getSize());
			String speed = captionSetting.getSpeed();
			if (speed != null && !speed.equals("")) {
				if (speed.toUpperCase().equals("NORMAL")) {
					setSpeed(SetupUtils.CAPTION_TEXT_NROMAL_SPEED);
				} else if (speed.toUpperCase().equals("SLOW")) {
					setSpeed(SetupUtils.CAPTION_TEXT_SLOW_SPEED);
				} else if (speed.toUpperCase().equals("FAST")) {
					setSpeed(SetupUtils.CAPTION_TEXT_FAST_SPEED);
				}
			}

			String color = captionSetting.getFgcolor();
			if (color != null && !color.equals("")) {
				if (color.startsWith("#")) {
					mTextColor = Color.parseColor(color);
					mPaintTxt.setColor(mTextColor);
				} else {
					mTextColor = Color.parseColor("#" + color);
					mPaintTxt.setColor(mTextColor);
				}
			}

			String bgColor = captionSetting.getBgcolor();
			if (bgColor != null && !bgColor.equals("")) {
				if (color.startsWith("#")) {
					setBackgroundColor(Color.parseColor(bgColor));
				} else {
					setBackgroundColor(Color.parseColor("#" + bgColor));
				}
			}
		} catch (Exception err) {
			err.printStackTrace();
		}
	}

	/**
	 * 设置滚动速度,默认是2
	 * 
	 * @param speed
	 */
	public void setSpeed(int speed) {
		if (speed > 0) {
			mSpeed = speed;
		}
	}

	/**
	 * 停止滚动
	 */
	public void stopScroll() {
		isStarting = false;
	}
}