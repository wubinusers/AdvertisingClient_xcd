package com.worldchip.advertising.client.adapter;

import java.util.ArrayList;

import com.worldchip.advertisingclient.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


//import android.view.DisplayManagerAw;
//import android.os.IPowerManager;
import android.os.RemoteException;
//import android.os.ServiceManager;
import android.content.ContentResolver;
import android.provider.Settings;

//import android.os.SystemProperties;

import android.media.AudioManager;
//import com.softwinner.Gpio;

public class FunctionListAdapter extends BaseAdapter {
	private Holder mHolder;
	SharedPreferences menuPref;
	private LayoutInflater inflater;
	private Context mContext;
	private String[] functionMenuArray;
	private String[] rotationArray;
	private String[] windowModeArray;
	private String[] backgroundMusicArray;
	private String[] showLogoArray;
	private String[] serialPortArray;
	private String[] audioModeArray;
	private String[] playEncodeArray;
	private String[] autoRotationArray;
	private String[] sensorSwitchArray;

	PopupWindow windowModePop;
	// 自动隐藏分屏预览
	public final static int AUTO_HIDE_POP = 123;
	private final static int AUTO_HIDE_POP_TIME = 4 * 1000;

	public final static int AD_SET = 155;
	public final static int REMOTE_CONTROL = 185;

	private static final String PROP_DISPLAY_ROTATE = "persist.sys.displayrot";

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (msg.what == AUTO_HIDE_POP) {
				//	if (windowModePop != null && windowModePop.isShowing()) {
				try {
					if (msg.what == AUTO_HIDE_POP) {
						if (windowModePop != null && windowModePop.isShowing()) {
							windowModePop.dismiss();
							windowModePop = null;
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//	windowModePop = null;
			}
		}
	};

	private Handler activityHandler;

	@Override
	public int getCount() {
		return functionMenuArray.length;
	}

	public FunctionListAdapter(Context context, Handler handler) {
		this.activityHandler = handler;
		mContext = context;
		menuPref = context.getSharedPreferences("function_menu", 0);
		initArray(context);
		inflater = LayoutInflater.from(context);

	}

	// 加载数组资源文件。
	private void initArray(Context context) {
		functionMenuArray = context.getResources().getStringArray(R.array.function_set_array);

		rotationArray = context.getResources().getStringArray(R.array.screen_rotation);
		windowModeArray = context.getResources().getStringArray(R.array.window_mode);
		backgroundMusicArray = context.getResources().getStringArray(R.array.background_music);
		showLogoArray = context.getResources().getStringArray(R.array.logo_setting);
		serialPortArray = context.getResources().getStringArray(R.array.serial_port);
		audioModeArray = context.getResources().getStringArray(R.array.audio_out_mode);
		playEncodeArray = context.getResources().getStringArray(R.array.play_encode);
		autoRotationArray = context.getResources().getStringArray(R.array.auto_rotation_screen);
		sensorSwitchArray = context.getResources().getStringArray(R.array.sensor_switch);
	}


	@Override
	public Object getItem(int position) {
		return functionMenuArray.length;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		Holder holder = null;
		if (view == null) {
			holder = new Holder();
			view = inflater.inflate(R.layout.setup_list_detail, null);
			holder.menuText = (TextView) view.findViewById(R.id.menu_text);
			holder.leftImage = (ImageView) view.findViewById(R.id.left_img);
			holder.itemText = (TextView) view.findViewById(R.id.item_text);
			holder.rightImage = (ImageView) view.findViewById(R.id.right_img);
			view.setTag(holder);
		} else {
			holder = (Holder) view.getTag();
		}
		holder.menuText.setText(functionMenuArray[position]);
		holder.itemText.setText(getMenuItem(position));
		holder.leftImage.setTag(position);
		holder.rightImage.setTag(position);

		final int fPosition = position;
		final Holder fHolder = holder;
		mHolder = holder;
		holder.leftImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int which = v.getId();
				handleClick(mContext, fPosition, which, fHolder);
			}
		});

		holder.rightImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int which = v.getId();
				handleClick(mContext, fPosition, which, fHolder);
			}
		});

		return view;
	}

	public static class Holder {
		TextView menuText;
		ImageView leftImage;
		TextView itemText;
		ImageView rightImage;
	}

	private String getMenuItem(int position) {

		String menuItemName = "";

		switch (position) {
			case 0:
				menuItemName = rotationArray[menuPref.getInt("rotation_index", 0)];
				break;
			case 1:
				menuItemName = windowModeArray[menuPref.getInt("window_mode_index", 0)];
				break;
			case 2:
				menuItemName = mContext.getString(R.string.ad_set);
				break;
			case 3:
				menuItemName = backgroundMusicArray[menuPref.getInt("background_music_index", 0)];
				break;
			case 4:
				menuItemName = showLogoArray[menuPref.getInt("show_logo_index", 0)];
				break;
			case 5:
				menuItemName = mContext.getString(R.string.remote_control);
				break;
			case 6:
				menuItemName = serialPortArray[menuPref.getInt("serial_port_index", 0)];
				break;
			case 7:
				menuItemName = audioModeArray[menuPref.getInt("audio_mode_index", 0)];
				break;
			case 8:
				menuItemName = playEncodeArray[menuPref.getInt("play_encode_index", 0)];
				break;
			case 9:
				menuItemName = autoRotationArray[menuPref.getInt("auto_rotation_index", 0)];
				break;
			case 10:
				menuItemName = sensorSwitchArray[menuPref.getInt("sensor_switch_index", 0)];
				break;
			default:
				break;
		}

		return menuItemName;
	}

	public void handleKeyEvent(Context context, int position, boolean isRightKey) {
		Holder holder = mHolder;
		int witch = 0;
		if (isRightKey) {
			witch = R.id.right_img;
		} else {
			witch = 0;
		}

		handleClick(context, position, witch, holder);
	}

	private void handleClick(Context context, int index, int witch, Holder holder) {
		if (holder != null) {

			switch (index) {
				case 0:
					int rotationIndex = changeIndex(index, witch, rotationArray);
					menuPref.edit().putInt("rotation_index", rotationIndex).commit();
					holder.itemText.setText(rotationArray[rotationIndex]);
					rotationScreen(rotationIndex);
					break;
				case 1:
					int windowModeIndex = changeIndex(index, witch, windowModeArray);
					menuPref.edit().putInt("window_mode_index", windowModeIndex).commit();
					holder.itemText.setText(windowModeArray[windowModeIndex]);
					if (windowModeIndex == 1) {
						if (windowModePop != null && windowModePop.isShowing()) {
							windowModePop.dismiss();
							windowModePop = null;
						}
					} else {
						initIdPopupWindow(context, windowModeIndex);
					}
					break;
				case 2:
					// 广告插播
					activityHandler.sendEmptyMessage(AD_SET);
					break;
				case 3:
					int backgroundMusicIndex = changeIndex(index, witch, backgroundMusicArray);
					menuPref.edit().putInt("background_music_index", backgroundMusicIndex).commit();
					holder.itemText.setText(backgroundMusicArray[backgroundMusicIndex]);
					break;
				case 4:
					int showLogoIndex = changeIndex(index, witch, showLogoArray);
					menuPref.edit().putInt("show_logo_index", showLogoIndex).commit();
					holder.itemText.setText(showLogoArray[showLogoIndex]);
					break;
				case 5:
					// 遥控加密
					activityHandler.sendEmptyMessage(REMOTE_CONTROL);
					break;
				case 6:
					int serialPortIndex = changeIndex(index, witch, serialPortArray);
					menuPref.edit().putInt("serial_port_index", serialPortIndex).commit();
					holder.itemText.setText(serialPortArray[serialPortIndex]);
					break;
				case 7:
					int audioModeIndex = changeIndex(index, witch, audioModeArray);
					menuPref.edit().putInt("audio_mode_index", audioModeIndex).commit();
					holder.itemText.setText(audioModeArray[audioModeIndex]);
					changeAudioMode(audioModeIndex);
					break;
				case 8:
					int playEncodeIndex = changeIndex(index, witch, playEncodeArray);
					menuPref.edit().putInt("play_encode_index", playEncodeIndex).commit();
					holder.itemText.setText(playEncodeArray[playEncodeIndex]);
					break;
				case 9:
					int autoRotationIndex = changeIndex(index, witch, autoRotationArray);
					menuPref.edit().putInt("auto_rotation_index", autoRotationIndex).commit();
					holder.itemText.setText(autoRotationArray[autoRotationIndex]);
					System.exit(0);
					break;
				case 10:
					int sensorSwitchIndex = changeIndex(index, witch, sensorSwitchArray);
					menuPref.edit().putInt("sensor_switch_index", sensorSwitchIndex).commit();
					holder.itemText.setText(sensorSwitchArray[sensorSwitchIndex]);
					break;
				default:
					break;
			}

		}

	}

	private int changeIndex(int position, int witch, String[] array) {
		int index = getIndexArray(position);
		if (witch == R.id.right_img) {
			index++;
			if (index > array.length - 1) {
				index = 0;
			}
		} else {
			index--;
			if (index < 0) {
				index = array.length - 1;
			}
		}
		return index;
	}

	private int getIndexArray(int textViewId) {
		int index = 0;
		switch (textViewId) {
			case 0:
				int rotationIndex = menuPref.getInt("rotation_index", 0);
				index = rotationIndex;
				break;
			case 1:
				int windowModeIndex = menuPref.getInt("window_mode_index", 0);
				index = windowModeIndex;
				break;
			case 2:

				break;
			case 3:
				int backgroundMusicIndex = menuPref.getInt("background_music_index", 0);
				index = backgroundMusicIndex;
				break;
			case 4:
				int showLogoIndex = menuPref.getInt("show_logo_index", 0);
				index = showLogoIndex;
				break;
			case 5:
				break;
			case 6:
				int serialPortIndex = menuPref.getInt("serial_port_index", 0);
				index = serialPortIndex;
				break;
			case 7:
				int audioModeIndex = menuPref.getInt("audio_mode_index", 0);
				index = audioModeIndex;
				break;
			case 8:
				int playEncodeIndex = menuPref.getInt("play_encode_index", 0);
				index = playEncodeIndex;
				break;
			case 9:
				int autoRotationIndex = menuPref.getInt("auto_rotation_index", 0);
				index = autoRotationIndex;
				break;
			case 10:
				int sensorSwitchIndex = menuPref.getInt("sensor_switch_index", 0);
				index = sensorSwitchIndex;
				break;
			default:
				break;
		}
		return index;
	}

	// 显示分屏预览。
	private void initIdPopupWindow(Context context, int windowModeIndex) {
		mHandler.removeMessages(AUTO_HIDE_POP);
		mHandler.sendEmptyMessageDelayed(AUTO_HIDE_POP, AUTO_HIDE_POP_TIME);
		if (windowModePop != null && windowModePop.isShowing()) {
			windowModePop.dismiss();
			windowModePop = null;
		}
		// 偏移量。
		int xOffset = -20;
		int yOffset = -15;
		LayoutInflater inflater = LayoutInflater.from(context);
		View layout = inflater.inflate(R.layout.window_mode_pop, null);
		ImageView imageView = (ImageView) layout.findViewById(R.id.window_mode_pic);
		switch (windowModeIndex) {
			case 0:
				imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.nowindow));
				break;
			case 1:
				break;
			case 2:
				imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.mode1));
				break;
			case 3:
				imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.mode2));
				break;
			case 4:
				imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.mode3));
				break;
			case 5:
				imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.mode4));
				break;
			case 6:
				imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.mode5));
				break;
			case 7:
				imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.mode6));
				break;
			case 8:
				imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.mode7));
				break;

			default:
				break;
		}

		windowModePop = new PopupWindow(layout, android.view.WindowManager.LayoutParams.WRAP_CONTENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT);
		windowModePop.setBackgroundDrawable(new BitmapDrawable());
		windowModePop.showAtLocation(layout, Gravity.CENTER, xOffset, yOffset);
	}

	// 屏幕旋转。
	private void rotationScreen(int rotationIndex) {
		int angle = rotationIndex * 90;
		//Settings.System.putInt(mContext.getContentResolver(), Settings.System.BD_FOLDER_PLAY_MODE, rotationIndex);
		//SystemProperties.set(PROP_DISPLAY_ROTATE, String.valueOf(angle));
		Activity act = (Activity) mContext;
		act.recreate();
	}

	// 调节音频输出模式。
	private void changeAudioMode(int audioModeIndex) {

		AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		if (audioManager == null) {
			return;
		}
		ArrayList<String> audioOutputChannels = new ArrayList<String>();
		audioOutputChannels.clear();
		/*if (audioModeIndex == 0) {
			audioOutputChannels.add(AudioManager.AUDIO_NAME_CODEC);
		} else if (audioModeIndex == 1) {
			audioOutputChannels.add(AudioManager.AUDIO_NAME_HDMI);
		} else if (audioModeIndex == 2) {
			audioOutputChannels.add(AudioManager.AUDIO_NAME_HDMI);
			audioOutputChannels.add(AudioManager.AUDIO_NAME_CODEC);
		} */

		/*audioManager.setAudioDeviceActive(audioOutputChannels, AudioManager.AUDIO_OUTPUT_ACTIVE);
		String st = null;
		for (int i = 0; i < audioOutputChannels.size(); i++) {
			if (st == null) {
				st = audioOutputChannels.get(i);
			} else {
				st = st + "," + audioOutputChannels.get(i);
			}
		}
		Settings.System.putString(mContext.getContentResolver(), Settings.System.AUDIO_OUTPUT_CHANNEL, st);*/

	}

}
