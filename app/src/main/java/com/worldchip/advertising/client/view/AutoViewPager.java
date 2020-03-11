package com.worldchip.advertising.client.view;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.worldchip.advertising.client.adapter.MarqueeAdapter;
import com.worldchip.advertising.client.image.utils.ImageLoader;
import com.worldchip.advertising.client.image.utils.ImageLoaderCallback;
import com.worldchip.advertising.client.transformer.AccordionTransformer;
import com.worldchip.advertising.client.transformer.BackgroundToForegroundTransformer;
import com.worldchip.advertising.client.transformer.CubeInTransformer;
import com.worldchip.advertising.client.transformer.CubeOutTransformer;
import com.worldchip.advertising.client.transformer.DefaultTransformer;
import com.worldchip.advertising.client.transformer.DepthPageTransformer;
import com.worldchip.advertising.client.transformer.FlipHorizontalTransformer;
import com.worldchip.advertising.client.transformer.FlipVerticalTransformer;
import com.worldchip.advertising.client.transformer.ForegroundToBackgroundTransformer;
import com.worldchip.advertising.client.transformer.NoAnimationTransformer;
import com.worldchip.advertising.client.transformer.RotateDownTransformer;
import com.worldchip.advertising.client.transformer.RotateUpTransformer;
import com.worldchip.advertising.client.transformer.StackTransformer;
import com.worldchip.advertising.client.transformer.TabletTransformer;
import com.worldchip.advertising.client.transformer.ZoomInTransformer;
import com.worldchip.advertising.client.transformer.ZoomOutSlideTransformer;
import com.worldchip.advertising.client.transformer.ZoomOutTranformer;
import com.worldchip.advertising.client.utils.ABaseTransformer;
import com.worldchip.advertising.client.utils.MediaType;
import com.worldchip.advertisingclient.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.util.Log;

public class AutoViewPager extends LinearLayout {

	private ViewPager mViewPager;
	private ArrayList<View> mPageViewList = new ArrayList<View>();
	private MarqueeAdapter marqueeAdapter;// 适配器

	private FixedSpeedScroller scroller = null;
	private Context mContext;
	private int mSetupAnimationIndex = 0;
	private SXImageView mImageView = null;
	private int mIndex;
	private int mPreIndex = -1;// 默认不指向任何view
	private int mLastIndex = 0;
	protected static final int LOAD_IMAGE = 1;
	private ImageLoader mImageLoader;
	private long lastHideTiem;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_IMAGE:
				loadImage();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public AutoViewPager(Context context) {
		super(context);
		init(context);
	}

	public AutoViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		View view = LayoutInflater.from(mContext).inflate(R.layout.marquee_image, this);
		new NoAnimationTransformer();

		mViewPager = (ViewPager) view.findViewById(R.id.marquee_image_viewpager);
		marqueeAdapter = new MarqueeAdapter();
		mViewPager.setAdapter(marqueeAdapter);
		mImageLoader = ImageLoader.getInstance(context.getApplicationContext(), 3, false);
	}

	public int getViewsCount() {
		if (mPageViewList == null) {
			return 0;
		}
		return mPageViewList.size();
	}

	public View getView(int index) {
		if (mPageViewList == null || mPageViewList.size() <= index) {
			return null;
		}
		return mPageViewList.get(index);
	}

	public MediaType getViewMideaType(int index) {
		View view = getView(index);
		if (view == null || view.getTag() == null) {
			return MediaType.NULL;
		}

		if (view.getTag().toString().equals("image")) {
			return MediaType.PHOTO;
		} else if (view.getTag().toString().equals("video")) {
			return MediaType.VIDEO;
		} else if (view.getTag().toString().equals("music")) {
			return MediaType.MUSIC;
		}
		return MediaType.NULL;
	}

	public void setIndex(int index) {
		this.mIndex = index;
	}

	// 切换下一个试图
	public void switchNextView(boolean isImageview, final SwitchNextImageCallback callback) {
		mHandler.removeMessages(LOAD_IMAGE);
		if (isImageview) {
			mImageView = null;
			if (mPageViewList != null && mPageViewList.size() > 0) {
				mLastIndex = mIndex;
				mImageView = (SXImageView) mPageViewList.get(mIndex);
				mImageLoader.loadImage(mImageView.getPath(), mImageView, false, new ImageLoaderCallback() {
					@Override
					public void onLoadCompleted(Bitmap bitmap) {
						// TODO Auto-generated method stub
						Message msg = mHandler.obtainMessage();
						msg.what = LOAD_IMAGE;
						mHandler.sendMessage(msg);
						if (mPageViewList != null && mPageViewList.size() > 1 && mPageViewList.size() != 2) {
							recyclePreImage(mPreIndex);
						}
						if (callback != null) {
							callback.onSwitchCompleted();
						}
					}

				});
			}
		} else {
			mViewPager.setCurrentItem(mIndex, false);

			recyclePreImage(mPreIndex);
		}
	}

	private void recyclePreImage(int index) {
	    lastHideTiem = System.currentTimeMillis();
		int pre = index - 1;
		int backPre = index + 1;
		if (index >= 0 && mPageViewList != null && index < mPageViewList.size() && pre != mLastIndex
				|| (pre - mLastIndex) > 1) {
			if (pre < 0) {
				View view = mPageViewList.get(mPageViewList.size() - 1);
				if (view != null && view instanceof SXImageView) {
					((SXImageView) view).setImageDrawable(null);
				}
			} else {
				View view = mPageViewList.get(pre);
				if (view != null && view instanceof SXImageView) {
					((SXImageView) view).setImageDrawable(null);
				}
			}
		} else {
			if (backPre >= mPageViewList.size()) {
				View view = mPageViewList.get(0);
				if (view != null && view instanceof SXImageView) {
					((SXImageView) view).setImageDrawable(null);
				}
			} else {
				View view = mPageViewList.get(backPre);
				if (view != null && view instanceof SXImageView) {
					((SXImageView) view).setImageDrawable(null);
				}
			}
		}
	}

	public void hideViewPager(int hide, boolean isAd) {
		long presentTime = System.currentTimeMillis();
		setVisibility(hide);
		if (hide == View.GONE) {
			recyclePreImage(mPreIndex);
		}
		// 后来加的。
		if (isAd) {	
			if (presentTime - lastHideTiem > 50000) {
				View view = mPageViewList.get(mIndex);
				if (view != null && view instanceof SXImageView) {
					((SXImageView) view).setImageDrawable(null);
				}
			}	
		}
	}

	public void setFirstImage() {
		mImageView = (SXImageView) mPageViewList.get(mIndex);
		Bitmap bitmap = BitmapFactory.decodeFile(mImageView.getPath());
		mImageView.setImageBitmap(bitmap);
		mViewPager.setCurrentItem(mIndex);
	}

	public void loadImage() {
		if (mImageView != null) {
			// mImageView.setImageBitmap(mBitmap);
			mPreIndex = mIndex;
		//	Log.d("chanson", "mmmmmm--mIndex === "  + mIndex);
			if (mIndex == 0) {
				//mViewPager.setCurrentItem(mIndex, false);
				if (mSetupAnimationIndex == 0) {
			    	mViewPager.setCurrentItem(mIndex, false);
				} else {
				    setScrollerTime(500);
					setScrollerAnimation();
					mViewPager.setCurrentItem(mIndex);
				}
					
			} else {
				if (mSetupAnimationIndex == 0) {
				    // 没设定动画时避免viewpager默认动画干扰。
					mViewPager.setCurrentItem(mIndex, false);
				} else {
					setScrollerTime(500);
					setScrollerAnimation();
					mViewPager.setCurrentItem(mIndex);
				}
			}

		}
	}

	/**
	 * 设置滑动时间
	 */
	public void setScrollerTime(int scrollerTime) {
		try {
			if (scroller != null) {
				scroller.setTime(scrollerTime);
			} else {
				Field mScroller;
				mScroller = ViewPager.class.getDeclaredField("mScroller");
				mScroller.setAccessible(true);
				scroller = new FixedSpeedScroller(mViewPager.getContext(), new AccelerateInterpolator());
				scroller.setTime(scrollerTime);
				mScroller.set(mViewPager, scroller);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置数据
	 * 
	 * @param mPageViews
	 */
	public void setData(ArrayList<View> pageViewList) {
		clearData();
		if (pageViewList != null) {
			this.mPageViewList = pageViewList;
			marqueeAdapter.setData(mPageViewList);// 添加数据
			marqueeAdapter.notifyDataSetChanged();// 通知数据发生改变
		}
	}

	/**
	 * 清理全部数据
	 */
	public void clearData() {
		mHandler.removeMessages(LOAD_IMAGE);
		if (this.mPageViewList != null) {
			this.mPageViewList.clear();
		}
		mViewPager.removeAllViews();
	}

	// 转场下标
	public void setAnimationIndex(int setupAnimationIndex) {
		this.mSetupAnimationIndex = setupAnimationIndex;
	}

	public void setScrollerAnimation() {
		ABaseTransformer transformer = null;
		int index = 0;
		// 随机0~13
		if (mSetupAnimationIndex == 1) {
			index = (int) Math.round(Math.random() * 15);
		} else {
			index = mSetupAnimationIndex - 2;
		}
		switch (index) {
		case 0:
			transformer = new AccordionTransformer();
			break;
		case 1:
			transformer = new BackgroundToForegroundTransformer();
			break;
		case 2:
			transformer = new CubeInTransformer();
			break;
		case 3:
			transformer = new CubeOutTransformer();
			break;
		case 4:
			transformer = new DepthPageTransformer();
			break;
		case 5:
			transformer = new FlipHorizontalTransformer();
			break;
		case 6:
			transformer = new FlipVerticalTransformer();
			break;
		case 7:
			transformer = new ForegroundToBackgroundTransformer();
			break;
		case 8:
			transformer = new RotateDownTransformer();
			break;
		case 9:
			transformer = new RotateUpTransformer();
			break;
		case 10:
			transformer = new StackTransformer();
			break;
		case 11:
			transformer = new TabletTransformer();
			break;
		case 12:
			transformer = new ZoomInTransformer();
			break;
		case 13:
			transformer = new ZoomOutSlideTransformer();
			break;
		case 14:
			transformer = new ZoomOutTranformer();
			break;
		default:
			transformer = new DefaultTransformer();
			break;
		}
		mViewPager.setPageTransformer(true, transformer);
	}
}
