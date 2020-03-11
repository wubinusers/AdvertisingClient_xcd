package com.worldchip.advertising.client.view;

import com.worldchip.advertising.client.entity.Caption;
import com.worldchip.advertising.client.utils.SetupUtils;
import com.worldchip.advertising.client.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;


public class AutoCustomTextView extends TextView {

	private int mSpeed = 2;
	boolean mIsfirst = true;
	private int mViewWidth = 0;
	private boolean mShortScroll = true; // 当文字长度小于view宽度时是否滚动,默认不滚动
	private int mScrollLength = Utils.WIDTH_PIXELS; // 总共需要滚动的长度
	private int mScrolledLength = 0; // 已经滚动的长度
	private boolean mIsRunning = false;
	private boolean mScrollCompleted = false; // 显示结束
	private int mDefaultWidth = Utils.WIDTH_PIXELS; // TextView默认宽度
	private static final int REFRESH = 0;
	int mTextLength = 0;
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		// 接收到消息后处理
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case REFRESH:
					//invalidate(); // 刷新界面
					break;
			}
			super.handleMessage(msg);
		}
	};
	
	public AutoCustomTextView(Context context) {
		super(context);
		this.mDefaultWidth = Utils.WIDTH_PIXELS;
	}

	public AutoCustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mDefaultWidth = Utils.WIDTH_PIXELS;
	}

	public AutoCustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mDefaultWidth = Utils.WIDTH_PIXELS;
	}
	
	/**
	 * 设置当文字长度小于 view宽度时是否滚动,默认不滚动
	 * @param shortScroll
	 */
	public void setShortScroll(boolean shortScroll) {
		mShortScroll = shortScroll;
		if(!mShortScroll)
		{
			invalidate();
		}
	}
	
	/**
	 * 当文字长度小雨view宽度时是否滚动
	 * @return
	 */
	public boolean ifShortScroll(){
		return mShortScroll;
	}
	
	/**
	 * 设置滚动速度,默认是2
	 * @param speed
	 */
	public void setSpeed(int speed) {
		if(speed > 0)
		{	
			mSpeed = speed;
		}
	}
	
	/**
	 * 当文字长度小于view宽度时是否滚动
	 * @return
	 */
	public int getSpeed() {
		return mSpeed;
	}
	
	/**
	 * 开始滚动
	 * @param text 需要滚动显示的字符串
	 */
	public void setTextVules(String text) 
	{
		mTextLength = (int) getPaint().measureText(text);
		mViewWidth = getWidth();
		if(mViewWidth <= 0)
		{
			mViewWidth = mDefaultWidth;
		}
		mScrollLength = mTextLength + 30; //当尾部完全滚完了再开始滚
		mScrolledLength = -mViewWidth;
		mScrollCompleted = false;
		mIsRunning = true;
		mIsfirst = true;
		setGravity(Gravity.CENTER_VERTICAL);
		setSingleLine(true);
		setText(text);
		
		
		//setEllipsize(TruncateAt.MARQUEE);
		//setMarqueeRepeatLimit(-1);
		//setFocusable(true);
	}
	
	/**
	 * 停止滚动,隐藏Textview内容
	 */
	public void stopMarquee(){
		scrollTo(0, 0);
		mScrolledLength = -mViewWidth;
		mScrollCompleted = true;
		mIsRunning = false;
	}
	
	/**
	 * 暂停滚动,内容依然显示
	 */
	public void pauseMarquee() {
		mIsRunning = false;
	}
	
	/**
	 * 继续滚动
	 */
	public void resumeMarquee() {
		mIsRunning = true;
		invalidate();
	}

	private synchronized void scrollOnce() {
		if (mIsfirst) {
			mIsfirst = false;
			scrollTo(-mViewWidth, 0);
		} else {
			if (mIsRunning && !mScrollCompleted) {
				mScrolledLength += mSpeed;
				if (mScrolledLength >= mScrollLength) {
					mScrolledLength = -mViewWidth;
					scrollTo(-mViewWidth, 0);
				} else {
					scrollTo(mScrolledLength, 0);
				}
			}
		}
		invalidate();
	}
	
	protected void onDraw(Canvas canvas) {
		Log.e("lee", "onDraw....");
		if(mShortScroll) {
			scrollOnce();
		} else {
			int textLength = (int) getPaint().measureText((String) getText());
			if(textLength >= mViewWidth) {
				scrollOnce();
			}
		}
		super.onDraw(canvas);
	}
	
	public void setCaptionSetting(Caption captionSetting) {
		try{
			//setTextSize(captionSetting.getSize());
			String speed = captionSetting.getSpeed();
			if (speed != null && !speed.equals(""))
			{
				if(speed.toUpperCase().equals("NORMAL"))
				{
					setSpeed(SetupUtils.CAPTION_TEXT_NROMAL_SPEED);
				}else if(speed.toUpperCase().equals("SLOW")){
					setSpeed(SetupUtils.CAPTION_TEXT_SLOW_SPEED);
				}else if(speed.toUpperCase().equals("FAST")){
					setSpeed(SetupUtils.CAPTION_TEXT_FAST_SPEED);
				}
			}
			
			String color = captionSetting.getFgcolor();
			if (color != null && !color.equals(""))
			{
				if (color.startsWith("#")) 
				{
					setTextColor(Color.parseColor(color));
				} else {
					setTextColor(Color.parseColor("#" + color));
				}
			}
			
			String bgColor = captionSetting.getBgcolor();
			if(bgColor !=null && !bgColor.equals("")){
				if(color.startsWith("#")){
					setBackgroundColor(Color.parseColor(bgColor));
				}else{
					setBackgroundColor(Color.parseColor("#"+bgColor));
				}
			}
		}catch(Exception err){
			err.printStackTrace();
		}
	}

}