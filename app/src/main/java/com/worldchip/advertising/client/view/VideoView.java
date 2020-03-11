package com.worldchip.advertising.client.view;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.MediaController;
import android.widget.MediaController.MediaPlayerControl;

import java.io.IOException;

import com.worldchip.advertising.client.utils.SetupUtils;
import com.worldchip.advertising.client.utils.ScreenUtils;
import com.worldchip.advertising.client.utils.Utils;

public class VideoView extends SurfaceView implements MediaPlayerControl {

	private static final String TAG = "---VideoView---";

	public final static int A_DEFALT = 1; // 全屏（按视频比例）
	public final static int A_ALL = 0; // 全屏（按屏幕比例）
	public final static int A_4X3 = 2; // 4:3
	public final static int A_16X9 = 3; // 16:9
	public final static int A_RAW = 4; // 原始大小

	public final static int VIDEO_ERROR = 0x564;

	private static final boolean DEBUG = true;

	private Context mContext;
	private Uri mUri;
	private int mDuration;
	private SurfaceHolder mSurfaceHolder = null;
	private MediaPlayer mMediaPlayer = null;
	private boolean mIsPrepared;
	private int mVideoWidth;
	private int mVideoHeight;
	private int mSurfaceWidth;
	private int mSurfaceHeight;
	private MediaController mMediaController;
	private int mCurrentBufferPercentage;
	private OnErrorListener mOnErrorListener;
	private boolean mStartWhenPrepared;
	private int mSeekWhenPrepared;
	private MySizeChangeLinstener mMyChangeLinstener;
	private Handler mHandler;

	public int getVideoWidth() {
		return mVideoWidth;
	}

	public int getVideoHeight() {
		return mVideoHeight;
	}

	public void setVideoScale(int width, int height) {
		LayoutParams lp = getLayoutParams();
		lp.height = height;
		lp.width = width;
		setLayoutParams(lp);
	}

	public interface MySizeChangeLinstener {
		public void doMyThings();
	}

	public void setMySizeChangeLinstener(MySizeChangeLinstener l) {
		this.mMyChangeLinstener = l;
	}

	public VideoView(Context context, Handler handler) {
		super(context);
		this.mContext = context;
		this.mHandler = handler;
		initVideoView();
	}

	public VideoView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.mContext = context;
		initVideoView();
	}

	public VideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		initVideoView();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
		int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	public int resolveAdjustedSize(int desiredSize, int measureSpec) {
		int result = desiredSize;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		switch (specMode) {
			case MeasureSpec.UNSPECIFIED:
				result = desiredSize;
				break;
			case MeasureSpec.AT_MOST:
				result = Math.min(desiredSize, specSize);
				break;
			case MeasureSpec.EXACTLY:
				result = specSize;
				break;
		}
		return result;
	}

	@SuppressWarnings("deprecation")
	private void initVideoView() {
		mVideoWidth = 0;
		mVideoHeight = 0;
		getHolder().addCallback(mSHCallback);
		getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		setFocusable(false);
		setFocusableInTouchMode(false);
		setZOrderOnTop(false);
	}

	public void setVideoPath(String path) {
		setVideoURI(Uri.parse(path));
	}

	public void setVideoURI(Uri uri) {
		mUri = uri;
		mStartWhenPrepared = false;
		mSeekWhenPrepared = 0;
		setZOrderMediaOverlay(false);
		openVideo();
		requestLayout();
		invalidate();
	}

	public void stopPlayback() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	private void openVideo() {
		if (mUri == null || mSurfaceHolder == null) {
			return;
		}
		Intent i = new Intent("com.android.music.musicservicecommand");
		// 暂停音乐
		i.putExtra("command", "pause");
		mContext.sendBroadcast(i);

		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		try {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnPreparedListener(mPreparedListener);
			mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
			mIsPrepared = false;
			mDuration = -1;
			mMediaPlayer.setOnCompletionListener(mCompletionListener);
			mMediaPlayer.setOnErrorListener(mErrorListener);
			mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
			mCurrentBufferPercentage = 0;
			mMediaPlayer.setDataSource(mContext, mUri);
			mMediaPlayer.setDisplay(mSurfaceHolder);
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			// 播放时不会自动熄屏
			mMediaPlayer.setScreenOnWhilePlaying(true);
			mMediaPlayer.prepareAsync();
			attachMediaController();
		} catch (IOException ex) {
			return;
		} catch (IllegalArgumentException ex) {
			return;
		}
	}

	// 媒体控制
	public void setMediaController(MediaController controller) {
		if (mMediaController != null) {
			mMediaController.hide();
		}
		mMediaController = controller;
		attachMediaController();
	}

	private void attachMediaController() {
		if (mMediaPlayer != null && mMediaController != null) {
			mMediaController.setMediaPlayer(this);
			View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
			mMediaController.setAnchorView(anchorView);
			mMediaController.setEnabled(mIsPrepared);
		}
	}

	private MediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
		public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();

			if (mMyChangeLinstener != null) {
				mMyChangeLinstener.doMyThings();
			}

			if (mVideoWidth != 0 && mVideoHeight != 0) {
				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
			}
		}
	};

	private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
		public void onPrepared(MediaPlayer mp) {
			mIsPrepared = true;
			if (mOnPreparedListener != null) {
				mOnPreparedListener.onPrepared(mMediaPlayer);
			}

			if (mMediaController != null) {
				mMediaController.setEnabled(true);
			}

			mVideoWidth = mp.getVideoWidth();
			mVideoHeight = mp.getVideoHeight();
			if (mVideoWidth != 0 && mVideoHeight != 0) {
				getHolder().setFixedSize(mVideoWidth, mVideoHeight);
				if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
					if (mSeekWhenPrepared != 0) {
						mMediaPlayer.seekTo(mSeekWhenPrepared);
						mSeekWhenPrepared = 0;
					}
					if (mStartWhenPrepared) {
						mMediaPlayer.start();
						mStartWhenPrepared = false;
						if (mMediaController != null) {
							mMediaController.show();
						}
					} else if (!isPlaying() && (mSeekWhenPrepared != 0 || getCurrentPosition() > 0)) {
						if (mMediaController != null) {
							mMediaController.show(0);
						}
					}
				}
			} else {
				if (mSeekWhenPrepared != 0) {
					mMediaPlayer.seekTo(mSeekWhenPrepared);
					mSeekWhenPrepared = 0;
				}
				if (mStartWhenPrepared) {
					mMediaPlayer.start();
					mStartWhenPrepared = false;
				}
			}
		}
	};

	private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			if (mMediaController != null) {
				mMediaController.hide();
			}
			if (mOnCompletionListener != null) {
				mOnCompletionListener.onCompletion(mMediaPlayer);
			}
		}
	};

	private MediaPlayer.OnErrorListener mErrorListener = new MediaPlayer.OnErrorListener() {
		public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
			if (mMediaController != null) {
				mMediaController.hide();
			}

			if (mOnErrorListener != null) {
				if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
					return true;
				}
			}

			mHandler.sendEmptyMessage(VIDEO_ERROR);
			return true;
		}
	};

	private MediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			mCurrentBufferPercentage = percent;
		}
	};

	private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mediaPlayer) {
			start();
			setScales(SetupUtils.getVideoScale(mContext));
		}
	};

	private OnCompletionListener mOnCompletionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
			Log.e(TAG, "mOnCompletionListener---------播放完成");
			mHandler.sendEmptyMessage(Utils.PLAY_VIDEO_COMPLETION);
		}
	};
	@Override
	public int getAudioSessionId(){
             return 0;
    };
	public void setOnErrorListener(OnErrorListener onErrorListener) {
		mOnErrorListener = onErrorListener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mIsPrepared && mMediaPlayer != null && mMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent ev) {
		if (mIsPrepared && mMediaPlayer != null && mMediaController != null) {
			toggleMediaControlsVisiblity();
		}
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mIsPrepared && keyCode != KeyEvent.KEYCODE_BACK && keyCode != KeyEvent.KEYCODE_VOLUME_UP
				&& keyCode != KeyEvent.KEYCODE_VOLUME_DOWN && keyCode != KeyEvent.KEYCODE_MENU
				&& keyCode != KeyEvent.KEYCODE_CALL && keyCode != KeyEvent.KEYCODE_ENDCALL && mMediaPlayer != null
				&& mMediaController != null) {
			if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
				if (mMediaPlayer.isPlaying()) {
					pause();

					mMediaController.show();
				} else {
					start();
					mMediaController.hide();
				}
				return true;
			} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP && mMediaPlayer.isPlaying()) {
				pause();
				mMediaController.show();
			} else {
				toggleMediaControlsVisiblity();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void toggleMediaControlsVisiblity() {
		if (mMediaController.isShowing()) {
			mMediaController.hide();
		} else {
			mMediaController.show();
		}
	}

	public void start() {
		if (mMediaPlayer != null && mIsPrepared) {
			mMediaPlayer.start();
			mStartWhenPrepared = false;
		} else {
			mStartWhenPrepared = true;
		}
	}

	public void pause() {
		if (mMediaPlayer != null && mIsPrepared) {
			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.pause();
			}
		}
		mStartWhenPrepared = false;
	}

	public int getDuration() {
		if (mMediaPlayer != null && mIsPrepared) {
			if (mDuration > 0) {
				return mDuration;
			}
			mDuration = mMediaPlayer.getDuration();
			return mDuration;
		}
		mDuration = -1;
		return mDuration;
	}

	public int getCurrentPosition() {
		if (mMediaPlayer != null && mIsPrepared) {
			return mMediaPlayer.getCurrentPosition();
		}
		return 0;
	}

	public void seekTo(int msec) {
		if (mMediaPlayer != null && mIsPrepared) {
			mMediaPlayer.seekTo(msec);
		} else {
			mSeekWhenPrepared = msec;
		}
	}

	public boolean isPlaying() {
		if (mMediaPlayer != null && mIsPrepared) {
			return mMediaPlayer.isPlaying();
		}
		return false;
	}

	public int getBufferPercentage() {
		if (mMediaPlayer != null) {
			return mCurrentBufferPercentage;
		}
		return 0;
	}

	@Override
	public boolean canPause() {
		return false;
	}

	@Override
	public boolean canSeekBackward() {
		return false;
	}

	@Override
	public boolean canSeekForward() {
		return false;
	}

	// 设置视频大小
	public void setScales(int scale) {
		if (getWindowToken() != null) {
			// 屏蔽的代码适合全屏播放的情况
			// Rect rect = new Rect();
			// getWindowVisibleDisplayFrame(rect);
			// Log.e(TAG, "setScales: Rect = " + rect.top + ":" + rect.bottom +
			// ":"
			// + rect.left + ":" + rect.right);

			//double height = getHeight(); // rect.bottom - rect.top;
			//double width = getWidth(); // rect.right - rect.left;

			double height = ScreenUtils.getScreenHeight(mContext);
			double width = ScreenUtils.getScreenWidth(mContext);
			if (DEBUG)
				Log.d(TAG, "setScales: scale=" + scale + "; diplay = " + width + ":" + height + "; mVideoHeight="
						+ mVideoHeight + "; mVideoWidth=" + mVideoWidth);

			if (height <= 0.0 || width <= 0.0 || mVideoHeight <= 0.0 || mVideoWidth <= 0.0) {
				return;
			}

			ViewGroup.LayoutParams param = getLayoutParams();
			switch (scale) { // 这下面的一些比例并没有完全用到
				// 4:3
				case A_4X3:
					if (width / height >= 4.0 / 3.0) {
						param.height = (int) height;
						param.width = (int) (4 * height / 3);
					} else {
						param.width = (int) width;
						param.height = (int) (3 * width / 4);
					}
					if (DEBUG)
						Log.e(TAG, "A_4X3 === " + param.width + ":" + param.height);
					setLayoutParams(param);
					break;
				case A_16X9: // 16:9
					if (width / height >= 16.0 / 9.0) {
						param.height = (int) height;
						param.width = (int) (16 * height / 9);
					} else {
						param.width = (int) width;
						param.height = (int) (9 * width / 16);
					}
					if (DEBUG)
						Log.e(TAG, "A_16X9 === " + param.width + ":" + param.height);
					setLayoutParams(param);
					break;
				case A_ALL: // 全屏（按屏幕比例）
					param.height = (int) height;
					param.width = (int) width;
					if (DEBUG)
						Log.e(TAG, "A_ALL === " + param.width + ":" + param.height);
					setLayoutParams(param);
					break;
				case A_DEFALT: // 全屏（按视频比例）
					if (width / height >= mVideoWidth / mVideoHeight) {
						param.height = (int) height;
						param.width = (int) (mVideoWidth * height / mVideoHeight);
					} else {
						param.width = (int) width;
						param.height = (int) (mVideoHeight * width / mVideoWidth);
					}
					if (DEBUG)
						Log.e(TAG, "A_DEFALT === " + param.width + ":" + param.height);
					setLayoutParams(param);
					break;
			}
			invalidate();
		}
	}

	private SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			mSurfaceWidth = w;
			mSurfaceHeight = h;
			if (mMediaPlayer != null && mIsPrepared && mVideoWidth == w && mVideoHeight == h) {
				if (mSeekWhenPrepared != 0) {
					mMediaPlayer.seekTo(mSeekWhenPrepared);
					mSeekWhenPrepared = 0;
				}
				mMediaPlayer.start();
				if (mMediaController != null) {
					mMediaController.show();
				}
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			mSurfaceHolder = holder;
			openVideo();
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			mSurfaceHolder = null;
			if (mMediaController != null)
				mMediaController.hide();
			if (mMediaPlayer != null) {
				mMediaPlayer.reset();
				mMediaPlayer.release();
				mMediaPlayer = null;
			}
		}
	};
}