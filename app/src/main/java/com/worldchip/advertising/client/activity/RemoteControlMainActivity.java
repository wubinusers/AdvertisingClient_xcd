package com.worldchip.advertising.client.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import com.worldchip.advertisingclient.R;

public class RemoteControlMainActivity extends Activity {

	private Button mSetPawBtn;
	private Button mResetPawBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_remote_control_main);
		mResetPawBtn = (Button) findViewById(R.id.reset_password_btn);
		mResetPawBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(RemoteControlMainActivity.this, RemoteControlResetActivity.class);
				startActivity(i);
			}
		});

		mSetPawBtn = (Button) findViewById(R.id.set_password_btn);
		mSetPawBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(RemoteControlMainActivity.this, RemoteControlSettingActivity.class);
				startActivity(i);
			}
		});
	}
}