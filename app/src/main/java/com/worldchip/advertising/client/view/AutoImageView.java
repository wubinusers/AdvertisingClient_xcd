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
import com.worldchip.advertising.client.utils.IPlayFinished;
import com.worldchip.advertising.client.utils.PlayModel;
import com.worldchip.advertising.client.utils.Utils;
import com.worldchip.advertisingclient.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

public class AutoImageView extends LinearLayout {

	private static final int SWITCH_PICTURE = 0;
	protected static final int LOAD_IMAGE = 1;
	private static final int SWITCH_TO_NEXT_PROGRAM = 4;
	private static final String TAG = "-AutoImageView--";

	private ViewPager mViewPager;
	private ArrayList<View> mPageViewList = new ArrayList<View>();
	private MarqueeAdapter marqueeAdapter;// 适配器
	public int mCurrentIndex = 0;// 当前显示View页面的序
	private int mPreIndex = -1;// 默认不指向任何view
	private FixedSpeedScroller scroller = null;
	private Context mContext;
	private int mSetupAnimationIndex = 0;
	private SXImageView mImageView = null;
	private ABaseTransformer mDefaultTransformer = null;
	private boolean mHasVideo = false;
	private IPlayFinished mPlayFinished;
	// add by lee begin
	private ImageLoader mImageLoader;

	// add by lee end
	public AutoImageView(Context context) {
		super(context);
		init(context);
	}

	public AutoImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		this.mPlayFinished = (IPlayFinished) mContext;
		this.mDefaultTransformer = new NoAnimationTransformer();// ZoomInTransformer();

		View view = LayoutInflater.from(mContext).inflate(R.layout.marquee_image, this);
		mViewPager = (ViewPager) view.findViewById(R.id.marquee_image_viewpager);
		marqueeAdapter = new MarqueeAdapter();
		mViewPager.setAdapter(marqueeAdapter);
		// add by lee begin
		mImageLoader = ImageLoader.getInstance(context.getApplicationContext(), 3, false);
		// add by lee end
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SWITCH_PICTURE:
				switchNextPicture();
				break;
			case LOAD_IMAGE:
				loadImage();
				break;
				
		    case SWITCH_TO_NEXT_PROGRAM:
			     mPlayFinished.playFinished(PlayModel.P);
			    break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	// 切换下一张图片
	public void switchNextPicture() {
		if (mPageViewList == null || mPageViewList.size() == 0) {
			mPlayFinished.playFinished(PlayModel.P);
			return;
		}
		
		if (mCurrentIndex >= mPageViewList.size()) {
			// 播放完成，回调
			Log.e(TAG, "finish...switchNextPicture..mPageViewList.size=" + mPageViewList.size()
					+ "; ...will call back..mHasVideo=" + mHasVideo);
			if (mHasVideo) {
			   mCurrentIndex = 0;
			} else if (!Utils.isSplitOnlyPic() && !Utils.isFreeWinMode){
			   mCurrentIndex = 0;
			}  else {
			   mPlayFinished.playFinished(PlayModel.P);
			   return;
			}
		}
		
		mImageView = (SXImageView) mPageViewList.get(mCurrentIndex);
		mImageLoader.loadImage(mImageView.getPath(), mImageView, false, new ImageLoaderCallback() {
			@Override
			public void onLoadCompleted(Bitmap bitmap) {
				mHandler.removeMessages(LOAD_IMAGE);
				// Message msg = mHandler.obtainMessage();
				// msg.what = LOAD_IMAGE;
				// mHandler.sendMessage(msg);
			if (mPageViewList.size() > 1) {
					loadImage();
					emptyPreImageDrawble(mPreIndex);
				}
				
			if (mCurrentIndex == 0 && mPageViewList.size() == 1 && Utils.isFreeWinMode && !mHasVideo) {
			       mHandler.sendEmptyMessageDelayed(SWITCH_TO_NEXT_PROGRAM, mImageView.getTime() * 1000);
			   }
			}
		});
	}

	// 此方法的作用就是主动清理垃圾，但是会造成图片切换有短暂的黑屏
	// 目前广告机还是以性能为主，此时可以不进行垃圾回收，将回收的任务交给系统虚拟机来自动处理。
	private void emptyPreImageDrawble(int index) {
	  //  Log.d("2222","currentIndex=== recycle==" + index);
		int pre = index - 1;
		if (index >= 0 && mPageViewList != null && index < mPageViewList.size()) {
			if (pre < 0) {
				View view = mPageViewList.get(mPageViewList.size() - 1);
				if (view != null && view instanceof SXImageView) {
					((SXImageView) view).setImageDrawable(null);
					System.gc();
				}
			} else {
				View view = mPageViewList.get(pre);
				if (view != null && view instanceof SXImageView) {
					((SXImageView) view).setImageDrawable(null);
					System.gc();
				}
			}
		}
	}

	private void loadImage() {
	//	Log.d(TAG, "currentIndex777======" + mCurrentIndex);
		if (mImageView != null) {
			// modify by lee end
			if (mCurrentIndex >= mPageViewList.size()) {
				mCurrentIndex = 0;
			}
		//	if (mCurrentIndex == 0) {
			//	Log.d(TAG, "currentIndex======" + mCurrentIndex);
			//	mViewPager.setCurrentItem(mCurrentIndex, false);
				mViewPager.setCurrentItem(mCurrentIndex);
		//	} else {
			//	mViewPager.setCurrentItem(mCurrentIndex);
		//	}

			// 动画
			if (mSetupAnimationIndex > 0) {
				setScrollerTime(500);
				setScrollerAnimation();
			} else {

				setScrollerTime(50);
				mViewPager.setPageTransformer(true, mDefaultTransformer);
			}
			mPreIndex = mCurrentIndex;
			mCurrentIndex++;

			mHandler.removeMessages(SWITCH_PICTURE);
			mHandler.sendEmptyMessageDelayed(SWITCH_PICTURE, mImageView.getTime() * 1000);
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
	public void setData(ArrayList<View> pageViewList, boolean hasVideo) {
		clearData();
		if (pageViewList != null) {
			Log.e(TAG, "setData...mHasVideo=" + mHasVideo + "; hasVideo=" + hasVideo);
			mHasVideo = hasVideo;
			this.mPageViewList = pageViewList;
			marqueeAdapter.setData(mPageViewList);// 添加数据
			marqueeAdapter.notifyDataSetChanged();// 通知数据发生改变
		}
	}

	/**
	 * 清理全部数据
	 */
	public void clearData() {
		mHasVideo = false;
		mHandler.removeMessages(SWITCH_PICTURE);
		mHandler.removeMessages(LOAD_IMAGE);
		if (this.mPageViewList != null && !mPageViewList.isEmpty()) {
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
		// 随机0~15
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
