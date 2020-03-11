package com.worldchip.advertising.client.view;

import android.content.Context;
import android.util.AttributeSet;

public class SXImageView extends android.widget.ImageView {

	private int mTime = 5;
	private String path;
	private int layoutWidth;
	private int layoutHeight;

	public SXImageView(Context context) {
		super(context);
	}

	public SXImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SXImageView(Context context, AttributeSet attrs, int defStyle) {
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

	public int getLayoutWidth() {
		return layoutWidth;
	}

	public void setLayoutWidth(int layoutWidth) {
		this.layoutWidth = layoutWidth;
	}

	public int getLayoutHeight() {
		return layoutHeight;
	}

	public void setLayoutHeight(int layoutHeight) {
		this.layoutHeight = layoutHeight;
	}

}