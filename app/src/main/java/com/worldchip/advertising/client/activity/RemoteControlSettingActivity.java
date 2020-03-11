package com.worldchip.advertising.client.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.worldchip.advertisingclient.R;

public class RemoteControlSettingActivity extends Activity {
	private EditText mEditText;
	private Button mButton;
	private TextView mTextView;
	private ImageView mImageView;
	private Drawable mRemoteControlPic;
	private Toast mToast;
	private SharedPreferences mSharedPre;
	private Resources mRes;

	private final static int KEY_UP = 1;
	private final static int KEY_DOWN = 2;
	private final static int KEY_LEFT = 3;
	private final static int KEY_RIGHT = 4;

	private final static int REFRESH_TEXT_VIEW = 7;
	private final static int DEL_NUM = 11;
	private final static int CHECK_PAW = 12;

	private Handler mHandler = new Handler() {
		@SuppressWarnings("static-access")
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				mEditText.append(mRes.getString(R.string.remote_key_up));
				mImageView.setImageDrawable(getDrawbleFromRes(R.drawable.press_up));
				break;
			case 2:
				mEditText.append(mRes.getString(R.string.remote_key_down));
				mImageView.setImageDrawable(getDrawbleFromRes(R.drawable.press_down));
				break;
			case 3:
				mEditText.append(mRes.getString(R.string.remote_key_left));
				mImageView.setImageDrawable(getDrawbleFromRes(R.drawable.press_left));
				break;
			case 4:
				mEditText.append(mRes.getString(R.string.remote_key_right));
				mImageView.setImageDrawable(getDrawbleFromRes(R.drawable.press_right));
				break;

			case 7:
				mSharedPre = getSharedPreferences("paw", 0);
				if (mSharedPre.getString("final_paw", "").equals("")) {
					if (mEditText.getText().length() == 6) {
						mSharedPre.edit().putString("first_paw", mEditText.getText().toString()).commit();
						mEditText.setText("");
						mTextView.setText(mRes.getString(R.string.input_password_again));
						mButton.setText(mRes.getString(R.string.set_finish));
					} else {
						mToast = new Toast(RemoteControlSettingActivity.this);
						mToast.makeText(RemoteControlSettingActivity.this, mRes.getString(R.string.toast_only_six),
								Toast.LENGTH_LONG).show();
					}
				} else {
					mToast = new Toast(RemoteControlSettingActivity.this);
					mToast.makeText(RemoteControlSettingActivity.this, mRes.getString(R.string.toast_aready_setted),
							Toast.LENGTH_LONG).show();
					RemoteControlSettingActivity.this.finish();
				}
				break;

			case 11:
				mImageView.setImageDrawable(getDrawbleFromRes(R.drawable.press_del));
				try {
					mEditText.getText().delete(mEditText.getText().length() - 1, mEditText.getText().length());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;

			case 12:
				if (mSharedPre.getString("first_paw", "").equals(mEditText.getText().toString())) {
					mToast = new Toast(RemoteControlSettingActivity.this);
					mToast.makeText(RemoteControlSettingActivity.this, mRes.getString(R.string.toast_set_success),
							Toast.LENGTH_LONG).show();
					mSharedPre.edit().putString("final_paw", mEditText.getText().toString()).commit();
					RemoteControlSettingActivity.this.finish();
				} else {
					mToast = new Toast(RemoteControlSettingActivity.this);
					mToast.makeText(RemoteControlSettingActivity.this, mRes.getString(R.string.toast_try_again),
							Toast.LENGTH_LONG).show();
					mEditText.setText("");
				}
				break;
			}

			super.handleMessage(msg);
		}
	};

	private Drawable getDrawbleFromRes(int res) {
		mRemoteControlPic = getResources().getDrawable(res);
		return mRemoteControlPic;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_remote_control_setting);
		mRes = getResources();
		mEditText = (EditText) findViewById(R.id.set_password_edittext);
		mButton = (Button) findViewById(R.id.set_password_button);
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mButton.getText().equals(mRes.getString(R.string.next_step))) {
					mHandler.sendEmptyMessage(REFRESH_TEXT_VIEW);
				} else if (mButton.getText().equals(mRes.getString(R.string.set_finish))) {
					mHandler.sendEmptyMessage(CHECK_PAW);
				}
			}

		});

		mTextView = (TextView) findViewById(R.id.set_password_textview);
		mImageView = (ImageView) findViewById(R.id.set_password_imageview);
	}

	@SuppressWarnings("static-access")
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == 67) {
			mHandler.sendEmptyMessage(DEL_NUM);
		}
		if (mEditText.getText().length() < 6) {
			if (event.getAction() == KeyEvent.ACTION_DOWN)
				switch (keyCode) {
				case 19:
					mHandler.sendEmptyMessage(KEY_UP);
					break;
				case 20:
					mHandler.sendEmptyMessage(KEY_DOWN);
					break;
				case 21:
					mHandler.sendEmptyMessage(KEY_LEFT);
					break;
				case 22:
					mHandler.sendEmptyMessage(KEY_RIGHT);
					break;

				default:
					break;
				}
		} else if (mEditText.getText().length() >= 6
				&& (keyCode == 19 || keyCode == 20 || keyCode == 21 || keyCode == 22)) {
			mToast = new Toast(RemoteControlSettingActivity.this);
			mToast.makeText(this, mRes.getString(R.string.toast_only_six), Toast.LENGTH_LONG).show();
		}

		return super.onKeyDown(keyCode, event);
	}

}
