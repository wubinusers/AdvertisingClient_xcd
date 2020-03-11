package com.worldchip.advertising.client.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class AutoTextView extends TextView implements Runnable {

	private float textLength = 0f;// 文本长度
	private float viewWidth = 0f;
	private float step = 1920.0f;// 文字的横坐标
	@SuppressWarnings("unused")
	private float y = 0f;// 文字的纵坐标
	private float temp_view_plus_text_length = 0.0f;// 用于计算的临时变量
	private float temp_view_plus_two_text_length = 0.0f;// 用于计算的临时变量
	public boolean isStarting = false;// 是否开始滚动
	private Paint mPaint = null;// 绘图样式
	private CharSequence text = "";// 文本内容
	private float speed = 0.5f;
	private int textColor = Color.YELLOW;
	private int desY = 15;

	public AutoTextView(Context context) {
		super(context);
		super.setSingleLine(true);
		super.setMarqueeRepeatLimit(-1);
	}

	public AutoTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		super.setSingleLine(true);
		super.setMarqueeRepeatLimit(-1);
	}

	public AutoTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		super.setSingleLine(true);
		super.setMarqueeRepeatLimit(-1);
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int color) {
		this.textColor = color;
		mPaint.setColor(textColor);
	}

	public float getSpeed() {
		return speed;
	}

	public void setScrollSpeed(float speed) {
		this.speed = speed;
	}

	public void setDes(int des) {
		this.desY = des;
	}

	/**
	 * 文本初始化，每次更改文本内容或者文本效果等之后都需要重新初始化一下!
	 */
	public void init(float width) {
		text = super.getText();
		mPaint = super.getPaint();
		text = getText().toString();
		textLength = mPaint.measureText(text.toString());

		viewWidth = width;
		step = textLength;
		temp_view_plus_text_length = viewWidth + textLength;
		temp_view_plus_two_text_length = viewWidth + textLength * 2;
		y = getTextSize() + getPaddingTop();
		mPaint.setColor(textColor);
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);
		ss.step = step;
		ss.isStarting = isStarting;
		return ss;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		if (!(state instanceof SavedState)) {
			super.onRestoreInstanceState(state);
			return;
		}
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		step = ss.step;
		isStarting = ss.isStarting;
	}

	public static class SavedState extends BaseSavedState {
		public boolean isStarting = false;
		public float step = 0.0f;

		SavedState(Parcelable superState) {
			super(superState);
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeBooleanArray(new boolean[] { isStarting });
			out.writeFloat(step);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}

			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}
		};

		@SuppressWarnings("unused")
		private SavedState(Parcel in) {
			super(in);
			boolean[] b = null;
			in.readBooleanArray(b);
			if (b != null && b.length > 0) {
				isStarting = b[0];
			}
			step = in.readFloat();
		}
	}

	/**
	 * 开始滚动
	 */
	public void startScroll() {
		isStarting = true;
		new Thread(this).start();
	}

	/**
	 * 停止滚动
	 */
	public void stopScroll() {
		isStarting = false;
	}

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		FontMetrics metrics = mPaint.getFontMetrics();
		float x = temp_view_plus_text_length - step;
		int des = (int) (Math.ceil(metrics.descent - metrics.ascent) + 2) - desY;
		canvas.drawText(text, 0, text.length(), x, des, mPaint);
		if (!isStarting) {
			return;
		}
		step += speed;

		if (step > temp_view_plus_two_text_length) {
			step = textLength;
		}
	}

	@Override
	public void run() {
		while (isStarting) {
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.e("lee", "AutoTextView --- run postInvalidate() ----------------");
			postInvalidate();
		}
	}

}