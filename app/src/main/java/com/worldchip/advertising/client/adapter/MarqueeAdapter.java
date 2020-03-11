package com.worldchip.advertising.client.adapter;

import java.util.ArrayList;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class MarqueeAdapter extends PagerAdapter {

	private ArrayList<View> mPageViews = new ArrayList<View>();

	/**
	 * 添加数据
	 * 
	 * @param mPageViews
	 */
	public void setData(ArrayList<View> mPageViews) {
		this.mPageViews = mPageViews;
	}

	@Override
	public int getCount() {
		return mPageViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}

	@Override
	public void destroyItem(ViewGroup view, int position, Object object) {
		view.removeView(mPageViews.get(position));
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(mPageViews.get(arg1));
		return mPageViews.get(arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}

	@Override
	public void finishUpdate(View arg0) {

	}
}