package com.worldchip.advertising.client.view;

import java.io.File;

import com.worldchip.advertising.client.entity.MusicInfo;
import com.worldchip.advertising.client.utils.Common;
import com.worldchip.advertisingclient.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AutoMusicView extends RelativeLayout {

	private static final String TAG = null;
	private Context mContext;
	private TextView mMusicName, mMusicSingeName, mMusicTime;
	private ImageView mMusicImage;

	private String mPath;

	public AutoMusicView(Context context) {
		super(context);
		this.mContext = context;
		initView();
	}

	public AutoMusicView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		initView();
	}

	public AutoMusicView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		initView();
	}

	private void initView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.music_main, null);
		this.mMusicName = (TextView) view.findViewById(R.id.music_name);
		this.mMusicSingeName = (TextView) view.findViewById(R.id.music_singer_name);
		this.mMusicImage = (ImageView) view.findViewById(R.id.music_image);
		this.mMusicTime = (TextView) view.findViewById(R.id.music_time);
		addView(view);
	}

	public String getPath() {
		return mPath;
	}

	public void setMusicInfo(String path) {
		mPath = path;
		MusicInfo musicInfo = Common.getMusicInfo(mContext, path);
		Log.e(TAG, "createMusicView...musicInfo=" + musicInfo);
		if (musicInfo != null) {
			String title = musicInfo.getTitle();
			this.mMusicName.setText(getResources().getString(R.string.media_music_name) + ": " + title);
			String artist = musicInfo.getArtist();
			if (artist != null && !artist.equals("<unknown>")) {
				this.mMusicSingeName.setText(getResources().getString(R.string.media_music_singer) + ": " + artist);
			} else {
				this.mMusicSingeName.setText(getResources().getString(R.string.media_music_singer) + ": "
						+ getResources().getString(R.string.media_unknown));
			}
			Bitmap bitmap = BitmapFactory.decodeFile(musicInfo.getAlbum());
			if (bitmap != null) {
				this.mMusicImage.setImageBitmap(bitmap);
			} else {
				this.mMusicImage.setImageResource(R.drawable.music_icon);
			}
		} else {
			File file = new File(path);
			String name = getResources().getString(R.string.media_unknown);
			if (file != null && file.exists() && file.getName() != null && file.getName().contains(".")) {
				name = file.getName().substring(0, file.getName().lastIndexOf("."));
			}
			// Log.e(TAG, "setMusicInfo...file.getName()=" + file.getName()+ ";
			// name=" + name);
			this.mMusicName.setText(getResources().getString(R.string.media_music_name) + ": " + name);
			this.mMusicSingeName.setText(getResources().getString(R.string.media_music_singer) + ": "
					+ getResources().getString(R.string.media_unknown));
		}
	}

	public void updateMusicTime(String timer) {
		mMusicTime.setText(timer);
	}
}