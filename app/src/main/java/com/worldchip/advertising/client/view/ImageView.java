package com.worldchip.advertising.client.view;

import android.content.Context;
import android.util.AttributeSet;

public class ImageView extends android.widget.ImageView {

	private int mTime = 5;
	private String path;

	public ImageView(Context context) {
		super(context);
	}

	public ImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setTime(int time) {
		mTime = time;
	}

	public int getTime() {
		return mTime;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
}