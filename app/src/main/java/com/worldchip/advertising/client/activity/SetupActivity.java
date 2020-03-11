package com.worldchip.advertising.client.activity;

import java.util.Timer;
import java.util.TimerTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import com.worldchip.advertising.client.adapter.AdListAdapter;
import com.worldchip.advertising.client.adapter.BasicListAdapter;
import com.worldchip.advertising.client.adapter.CaptionListAdapter;
import com.worldchip.advertising.client.adapter.FunctionListAdapter;
import com.worldchip.advertising.client.adapter.OtherListAdapter;
import com.worldchip.advertising.client.adapter.TimeListAdapter;
import com.worldchip.advertising.client.utils.SetupUtils;
import com.worldchip.advertisingclient.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


//import android.view.DisplayManagerAw;
//import android.os.IPowerManager;
import android.os.RemoteException;
//import android.os.ServiceManager;
import android.content.ContentResolver;
import android.provider.Settings;

//import android.os.SystemProperties;

import android.media.AudioManager;
//import com.softwinner.Gpio;

public class SetupActivity extends Activity {


	private static final String PROP_DISPLAY_ROTATE = "persist.sys.displayrot";
	private ListView menuListView;

	private BasicListAdapter basicListAdapter;
	private CaptionListAdapter captionListAdapter;
	private FunctionListAdapter functionListAdapter;
	private OtherListAdapter otherListAdapter;
	private TimeListAdapter timeListAdapter;
	private AdListAdapter adListAdapter;

	private ImageView menu0Img;
	private ImageView menu1Img;
	private ImageView menu2Img;
	private ImageView menu3Img;
	private TextView topMenuText;

	private int menuIndex = 0;
	int selectedPosition = 0;

	private final static String POWER_DELAY_TIME = "/data/misc/user/0/delay_boot.txt";
	private final static int ROATION = 0xff;
	private Timer checkIOTimer;
	private boolean isRoationChange = false;
	private static final char PORT_TYPE = 'B';
	private static final int PORT_NUM_20 = 20;
	private static final int PORT_NUM_21 = 21;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case BasicListAdapter.SET_TIME:
					switchMenu(4);
					break;
				case BasicListAdapter.RESET_MENU:
					showResetMenuDialog();
					break;
				case BasicListAdapter.CLICK_LANGUAGE:
					refreshList();
					//this.finish();
					SetupActivity.this.recreate();
					break;
				case FunctionListAdapter.AD_SET:
					switchMenu(5);
					break;
				case FunctionListAdapter.REMOTE_CONTROL:
					startActivity("com.worldchip.advertisingclient", "com.worldchip.advertising.client.activity.RemoteControlMainActivity");
					break;
				case OtherListAdapter.OPEN_FILE_MANAGER:
					startActivity("com.softwinner.TvdFileManager", "com.softwinner.TvdFileManager.MainUI");
					break;
				case OtherListAdapter.REFRESH_LIST:
					refreshList();
					break;
				case TimeListAdapter.RESET_TIME_MENU:
					switchMenu(4);
					break;
				case ROATION:
					SetupActivity.this.recreate();
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup);
		SetupUtils.setupSystemLangue(this);
		initMenuImg();
		initAllAdapter();
		recoverLastState();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		SetupUtils.setupSystemLangue(this);
		if (SetupUtils.isAutoRotation(this)) {
			startCheckRotion();
		}
		super.onResume();
	}

	// 每隔3s监测IO口。
	private void startCheckRotion() {
		checkIOTimer = new Timer();
		checkIOTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				//by zhang
				Log.d("zhang","Not suuport !!!");
				//checkAndAutoRotation();
			}
		}, 0, 5000);

	}

	/*private void checkAndAutoRotation() {
		// io20 状态
		int io20 = Gpio.readGpio(PORT_TYPE, PORT_NUM_20);
		// io21 状态
		int io21 = Gpio.readGpio(PORT_TYPE, PORT_NUM_21);
		isRoationChange = checkRoation(io20, io21);

		if (!isRoationChange) {
			if (io20 == 1 && io21 == 1) {
				getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", 0).commit();
				Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, 0);
				SystemProperties.set(PROP_DISPLAY_ROTATE, "0");
			} else if (io20 == 0 && io21 == 1) {
				getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", 1).commit();
				Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, 1);
				SystemProperties.set(PROP_DISPLAY_ROTATE, "90");
			} else if (io20 == 0 && io21 == 0) {
				getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", 2).commit();
				Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, 2);
				SystemProperties.set(PROP_DISPLAY_ROTATE, "180");
			} else if (io20 == 1 && io21 == 0) {
				getSharedPreferences("function_menu", 0).edit().putInt("rotation_index", 3).commit();
				Settings.System.putInt(getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, 3);
				SystemProperties.set(PROP_DISPLAY_ROTATE, "270");
			}

			mHandler.sendEmptyMessage(ROATION);
		}
	}*/

	private boolean checkRoation(int PB20, int PB21) {

		int roation = 0;

		if (PB20 == 1 && PB21 == 1) {
			roation = 0;
		}

		if (PB20 == 0 && PB21 == 1) {
			roation = 1;
		}

		if (PB20 == 0 && PB21 == 0) {
			roation = 2;
		}

		if (PB20 == 1 && PB21 == 0) {
			roation = 3;
		}

		return roation == getSharedPreferences("function_menu", 0).getInt("rotation_index", 0);
	}

	private void recoverLastState() {
		menuIndex = getSharedPreferences("id_prefer", 0).getInt("menuIn", 0);
		switchMenu(menuIndex);
		setFocusImgAndText(menuIndex);
	}

	private void initAllAdapter() {
		menuListView = (ListView) findViewById(R.id.menu_list);
		menuListView.setOnItemSelectedListener(new MenuListSelectListener());
		topMenuText = (TextView) findViewById(R.id.top_menu_text);
		basicListAdapter = new BasicListAdapter(this, mHandler);
		captionListAdapter = new CaptionListAdapter(this);
		functionListAdapter = new FunctionListAdapter(this, mHandler);
		otherListAdapter = new OtherListAdapter(this,mHandler);
		timeListAdapter = new TimeListAdapter(this, mHandler);
		adListAdapter = new AdListAdapter(this);
	}

	private void initMenuImg() {
		menu0Img = (ImageView) findViewById(R.id.menu0_image);
		menu0Img.setOnClickListener(new MenuImgClickListener());
		menu1Img = (ImageView) findViewById(R.id.menu1_image);
		menu1Img.setOnClickListener(new MenuImgClickListener());
		menu2Img = (ImageView) findViewById(R.id.menu2_image);
		menu2Img.setOnClickListener(new MenuImgClickListener());
		menu3Img = (ImageView) findViewById(R.id.menu3_image);
		menu3Img.setOnClickListener(new MenuImgClickListener());
	}

	private void resetAllMenuImg() {
		menu0Img.setImageDrawable(getResources().getDrawable(R.drawable.menu0));
		menu1Img.setImageDrawable(getResources().getDrawable(R.drawable.menu1));
		menu2Img.setImageDrawable(getResources().getDrawable(R.drawable.menu2));
		menu3Img.setImageDrawable(getResources().getDrawable(R.drawable.menu3));
	}

	private void setFocusImgAndText(int menuIndex) {
		resetAllMenuImg();
		if (menuIndex == 0) {
			topMenuText.setText(R.string.menu_0);
			menu0Img.setImageDrawable(getResources().getDrawable(R.drawable.menu0_focus));
		} else if (menuIndex == 1) {
			topMenuText.setText(R.string.menu_1);
			menu1Img.setImageDrawable(getResources().getDrawable(R.drawable.menu1_focus));
		} else if (menuIndex == 2) {
			topMenuText.setText(R.string.menu_2);
			menu2Img.setImageDrawable(getResources().getDrawable(R.drawable.menu2_focus));
		} else if (menuIndex == 3) {
			topMenuText.setText(R.string.menu_3);
			menu3Img.setImageDrawable(getResources().getDrawable(R.drawable.menu3_focus));
		}
	}

	public class MenuListSelectListener implements ListView.OnItemSelectedListener {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			selectedPosition = position;
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {

		}
	}

	public class MenuImgClickListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
				case R.id.menu0_image:
					setFocusImgAndText(0);
					switchMenu(0);
					// 保存本次菜单Index
					getSharedPreferences("id_prefer", 0).edit().putInt("menuIn", 0).commit();
					break;
				case R.id.menu1_image:
					setFocusImgAndText(1);
					switchMenu(1);
					// 保存本次菜单Index
					getSharedPreferences("id_prefer", 0).edit().putInt("menuIn", 1).commit();
					break;
				case R.id.menu2_image:
					setFocusImgAndText(2);
					switchMenu(2);
					// 保存本次菜单Index
					getSharedPreferences("id_prefer", 0).edit().putInt("menuIn", 2).commit();
					break;
				case R.id.menu3_image:
					setFocusImgAndText(3);
					switchMenu(3);
					// 保存本次菜单Index
					getSharedPreferences("id_prefer", 0).edit().putInt("menuIn", 3).commit();
					break;
				default:
					break;
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (menuListView.getAdapter().equals(timeListAdapter)) {
					switchMenu(0);
					menuListView.setSelection(4);
					break;
				}

				if (menuListView.getAdapter().equals(adListAdapter)) {
					switchMenu(2);
					menuListView.setSelection(2);
					break;
				}
				finish();
				break;
			case KeyEvent.KEYCODE_MENU:

				// 获取至上次菜单Index
				menuIndex = getSharedPreferences("id_prefer", 0).getInt("menuIn", 0);
				menuIndex++;
				if (menuIndex >= 4) {
					menuIndex = 0;
				}
				// 保存本次菜单Index
				getSharedPreferences("id_prefer", 0).edit().putInt("menuIn", menuIndex).commit();
				switchMenu(menuIndex);
				setFocusImgAndText(menuIndex);
				break;
			case 21:
				// 左
				handleLeftRightKey(menuIndex, false);
				break;
			case 22:
				// 右
				handleLeftRightKey(menuIndex, true);
				break;
			default:
				break;

		}
		return false;
	}

	// 切换相应的菜单。
	private void switchMenu(int menuIndex) {
		switch (menuIndex) {
			case 0:
				menuListView.setAdapter(basicListAdapter);
				break;
			case 1:
				menuListView.setAdapter(captionListAdapter);
				break;
			case 2:
				menuListView.setAdapter(functionListAdapter);
				break;
			case 3:
				menuListView.setAdapter(otherListAdapter);
				break;
			case 4:
				menuListView.setAdapter(timeListAdapter);
				break;
			case 5:
				menuListView.setAdapter(adListAdapter);
				break;
			default:
				menuListView.setAdapter(basicListAdapter);
				break;
		}
	}

	private void handleLeftRightKey(int menuIndex, boolean isRightKey) {
		switch (menuIndex) {
			case 0:
				if (menuListView.getAdapter().equals(timeListAdapter)) {
					setTimeAndAd(4, isRightKey);
					setFocusImgAndText(0);
					menuListView.setSelection(selectedPosition);
					return;
				} else {
					if (selectedPosition == 0) {
						basicListAdapter.handleKeyEvent(selectedPosition, isRightKey);
						refreshList();
						//this.finish();
						this.recreate();
						return;
					}

					if (selectedPosition == 5) {
						// 定时开关机
						//	startActivity("com.adtv", "com.adtv.setting.PowerOnOffSettings");
						startActivity("com.worldchip.advertisingclient", "com.worldchip.advertising.client.activity.PowerOnOffActivity");
						return;
					}

					if (selectedPosition == 4) {
						// 时间设置
						switchMenu(4);
						return;
					}

					if (selectedPosition == 10) {
						// 重置菜单
						showResetMenuDialog();
						return;
					}

					basicListAdapter.handleKeyEvent(selectedPosition, isRightKey);
					// 重新来加载一遍，让焦点回到上次的位置。不然的话显示会错乱的。
					refreshList();
				}

				break;
			case 1:
				captionListAdapter.handleKeyEvent(selectedPosition, isRightKey);
				// 重新来加载一遍，让焦点回到上次的位置。不然的话显示会错乱的。
				refreshList();
				break;
			case 2:
				if (menuListView.getAdapter().equals(adListAdapter)) {
					setTimeAndAd(5, isRightKey);
					// switchMenu(5);
					setFocusImgAndText(2);
					menuListView.setSelection(selectedPosition);
					return;
				} else {
					if (selectedPosition == 2) {
						setTimeAndAd(5, isRightKey);
						return;
					}

					if (selectedPosition == 5) {
						// 遥控加密。
						//com.worldchip.advertisingclient
						//RemoteControlMainActivity.class
						startActivity("com.worldchip.advertisingclient", "com.worldchip.advertising.client.activity.RemoteControlMainActivity");
						return;
					}

					functionListAdapter.handleKeyEvent(this, selectedPosition, isRightKey);
					// 重新来加载一遍，让焦点回到上次的位置。不然的话显示会错乱的。
					refreshList();
				}
				break;
			case 3:
				if (selectedPosition == 1) {
					// 打开文件管理
					startActivity("com.softwinner.TvdFileManager", "com.softwinner.TvdFileManager.MainUI");
					return;
				}

				otherListAdapter.handleKeyEvent(selectedPosition, isRightKey);
				// 重新来加载一遍，让焦点回到上次的位置。不然的话显示会错乱的。
				refreshList();
				break;
			default:
				break;
		}

	}

	private void setTimeAndAd(int index, boolean isRightKey) {
		switch (index) {
			case 4:
				switchMenu(4);
				timeListAdapter.handleKeyEvent(selectedPosition, isRightKey);
				break;
			case 5:
				switchMenu(5);
				adListAdapter.handleKeyEvent(selectedPosition, isRightKey);
				break;
			default:
				break;
		}

	}

	private void refreshList() {
		switchMenu(menuIndex);
		setFocusImgAndText(menuIndex);
		menuListView.setSelection(selectedPosition);
	}

	// 通过包名和类名来开启活动。
	private void startActivity(String packageName, String className) {
		try {
			Intent intent = new Intent();
			intent.setComponent(new ComponentName(packageName, className));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} catch (Exception err) {
			Toast.makeText(getApplicationContext(), "The App not found!", Toast.LENGTH_LONG).show();
			err.printStackTrace();
		}
	}

	// 显示重置菜单对话框
	protected void showResetMenuDialog() {
		AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
		builder.setMessage(R.string.confirm_reset_menu);
		builder.setPositiveButton(this.getResources().getString(R.string.confirm_reset_menu_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						clearSharePre();
						// mHandler.sendEmptyMessage(REFRESH_AFTER_RESET_MENU);
						try {
							writeFile();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						refreshList();
					}
				});

		builder.setNegativeButton(this.getResources().getString(R.string.confirm_reset_menu_no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}


	// 写入0
	public void writeFile() throws IOException {
		String write_str = "0";
		File file = new File(POWER_DELAY_TIME);
		FileOutputStream fos = new FileOutputStream(file);
		byte[] bytes = write_str.getBytes();
		fos.write(bytes);
		fos.close();
	}

	// 清除菜单数据。
	private void clearSharePre() {
		getSharedPreferences("basic_menu", 0).edit().clear().commit();
		getSharedPreferences("caption_menu", 0).edit().clear().commit();
		getSharedPreferences("function_menu", 0).edit().clear().commit();
		getSharedPreferences("other_menu", 0).edit().clear().commit();
		getSharedPreferences("time_set", 0).edit().clear().commit();
		getSharedPreferences("ad_set", 0).edit().clear().commit();
		getSharedPreferences("id_prefer", 0).edit().clear().commit();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mHandler.removeMessages(FunctionListAdapter.AUTO_HIDE_POP);
	}

}
