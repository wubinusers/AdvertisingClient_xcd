package com.worldchip.advertising.client.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.http.util.EncodingUtils;
import com.worldchip.advertisingclient.R;
import com.worldchip.advertising.client.utils.ScreenUtils;



import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.text.Editable;
import android.text.method.KeyListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.view.KeyEvent;
import android.app.AlertDialog;
import android.content.DialogInterface;
//import android.view.DisplayManagerAw;
//import android.os.IPowerManager;
import android.os.RemoteException;
//import android.os.ServiceManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.provider.Settings;
import android.media.AudioManager;

//import android.os.SystemProperties;

import android.media.AudioManager;
//import com.softwinner.Gpio;

//import com.softwinner.SecureFile;

public class OtherListAdapter extends BaseAdapter {
	private Holder mHolder;
	SharedPreferences menuPref;
	SharedPreferences idPref;
	private LayoutInflater inflater;
	private Context mContext;

	private String[] otherMenuArray;
	private String[] bootArray;
	private String[] changeVolumeArray;
	private String[] powerDelayTimeArray;

	private final static String POWER_DELAY_TIME = "/data/misc/user/0/delay_boot.txt";

	private Handler mHandler;
	public final static int OPEN_FILE_MANAGER = 3342;
	public final static int SET_ID = 6575;
	public final static int REFRESH_LIST = 6545;

	private EditText inputIdText;
	private PopupWindow inputIdWindow;
	AudioManager mAudioManager;

	public OtherListAdapter(Context context, Handler handler) {
		this.mHandler = handler;
		idPref = context.getSharedPreferences("id_prefer", 0);
		menuPref = context.getSharedPreferences("other_menu", 0);
		initArray(context);
		inflater = LayoutInflater.from(context);
		mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
	}

	// 加载数组资源文件。
	private void initArray(Context context) {
		mContext = context;
		otherMenuArray = context.getResources().getStringArray(R.array.other_set_array);
		bootArray = context.getResources().getStringArray(R.array.boot);
		powerDelayTimeArray = context.getResources().getStringArray(R.array.power_delay_time_array);
	}

	@Override
	public int getCount() {
		return otherMenuArray.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
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
		holder.menuText.setText(otherMenuArray[position]);
		holder.itemText.setText(getMenuItem(position));
		holder.leftImage.setTag(position);
		holder.rightImage.setTag(position);

		final int fPosition = position;
		final Holder fHolder = holder;
		mHolder = fHolder;
		holder.leftImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int which = v.getId();
				handleClick(fPosition, which, fHolder);
			}
		});

		holder.rightImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int which = v.getId();
				handleClick(fPosition, which, fHolder);
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
		String str = "";
		switch (position) {
			case 0:
				menuItemName = bootArray[menuPref.getInt("boot_index", 0)];
				break;
			case 1:
				menuItemName = mContext.getString(R.string.file_manager);
				break;
			case 2:
				menuItemName = menuPref.getInt("brightness_value", 50) + "";
				break;
			case 3:
				menuItemName = menuPref.getInt("contrast_value", 50) + "";
				break;
			case 4:
				menuItemName = menuPref.getInt("saturation_value", 50) + "";
				break;
			case 6:
				menuItemName = idPref.getString("id_text", "000000");
				break;
			case 7:
				// 延时开机时间。
				try {
					str = readFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//	menuItemName = powerDelayTimeArray[menuPref.getInt("power_delay_index", 0)] + mContext.getResources().getString(R.string.power_delay_sec);
				menuItemName = str + mContext.getResources().getString(R.string.power_delay_sec);
				break;
			case 8:
				menuItemName = mContext.getString(R.string.version);
				break;
			case 9:
				menuItemName = mContext.getString(R.string.reset_system);
				break;
			default:
				break;
		}

		return menuItemName;
	}

	public void handleKeyEvent(int position, boolean isRightKey) {
		Holder holder = mHolder;
		int witch = 0;
		if (isRightKey) {
			witch = R.id.right_img;
		} else {
			witch = 0;
		}

		handleClick(position, witch, holder);
	}

	private void handleClick(int index, int witch, Holder holder) {
		if (holder != null) {

			switch (index) {
				case 0:
					int bootIndex = changeIndex(index, witch, bootArray);
					menuPref.edit().putInt("boot_index", bootIndex).commit();
					holder.itemText.setText(bootArray[bootIndex]);
					break;
				case 1:
					// 文件管理
					mHandler.sendEmptyMessage(OPEN_FILE_MANAGER);
					break;
				case 2:
					int brightnessValue = menuPref.getInt("brightness_value", 50);
					if (witch == R.id.right_img) {
						brightnessValue++;
						if (brightnessValue > 99) {
							brightnessValue = 1;
						}
					} else {
						brightnessValue--;
						if (brightnessValue < 1) {
							brightnessValue = 99;
						}
					}
					menuPref.edit().putInt("brightness_value", brightnessValue).commit();
					holder.itemText.setText(menuPref.getInt("brightness_value", 50) + "");
					changeBrightness(brightnessValue);
					break;
				case 3:
					int contrastValue = menuPref.getInt("contrast_value", 50);
					if (witch == R.id.right_img) {
						contrastValue++;
						if (contrastValue > 99) {
							contrastValue = 1;
						}
					} else {
						contrastValue--;
						if (contrastValue < 1) {
							contrastValue = 99;
						}
					}
					menuPref.edit().putInt("contrast_value", contrastValue).commit();
					holder.itemText.setText(menuPref.getInt("contrast_value", 50) + "");
					changeContrast(contrastValue);
					break;
				case 4:
					int saturationValue = menuPref.getInt("saturation_value", 50);
					if (witch == R.id.right_img) {
						saturationValue++;
						if (saturationValue > 99) {
							saturationValue = 1;
						}
					} else {
						saturationValue--;
						if (saturationValue < 1) {
							saturationValue = 99;
						}
					}
					menuPref.edit().putInt("saturation_value", saturationValue).commit();
					holder.itemText.setText(menuPref.getInt("saturation_value", 50) + "");
					changeSaturation(saturationValue);
					break;

				case 5:
					if (witch == R.id.right_img) {
						mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_RAISE,
								AudioManager.FX_FOCUS_NAVIGATION_UP);
					} else {
						mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,AudioManager.ADJUST_LOWER,
								AudioManager.FX_FOCUS_NAVIGATION_UP);
					}

					break;

				case 6:
					showIdSettingPop(holder);
					break;
				case 7:
					// 延时开机
					// int powerDelaySec = menuPref.getInt("power_delay_sec", 0);
					int powerDelayIndex = changeIndex(index, witch, powerDelayTimeArray);
					menuPref.edit().putInt("power_delay_index", powerDelayIndex).commit();
					holder.itemText.setText(powerDelayTimeArray[powerDelayIndex] + mContext.getResources().getString(R.string.power_delay_sec));
					try {
						writeFile(powerDelayIndex);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 8:
					// 版本号
					break;
				case 9:
					// 系统重置
					//	mContext.sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
					showResetMenuDialog();
					break;
				default:
					break;
			}
		}
	}

	// 显示重置对话框
	protected void showResetMenuDialog() {
		AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mContext);
		builder.setMessage(R.string.confirm_reset_system);
		/*builder.setPositiveButton(mContext.getResources().getString(R.string.confirm_reset_menu_yes),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();

						Intent intent = new Intent(ExternalStorageFormatter.FORMAT_AND_FACTORY_RESET);
						intent.setComponent(ExternalStorageFormatter.COMPONENT_NAME);
						mContext.startService(intent);
					}
				});*/

		builder.setNegativeButton(mContext.getResources().getString(R.string.confirm_reset_menu_no),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						dialog.dismiss();
					}
				});
		builder.create().show();
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
				int bootIndex = menuPref.getInt("boot_index", 0);
				index = bootIndex;
				break;
			case 6:
				int powerDelayIndex = menuPref.getInt("power_delay_index", 0);
				index = powerDelayIndex;
				break;
			default:
				break;
		}

		return index;
	}

	// 显示ID设置
	private void showIdSettingPop(final Holder holder) {
		int xOffset = 0;
		if (ScreenUtils.getScreenWidth(mContext) > ScreenUtils.getScreenHeight(mContext)) {
			xOffset = 270;
		} else {
			xOffset = 190;
		}
		int yOffset = 60;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View layout = inflater.inflate(R.layout.input_id_layout, null);
		inputIdText = (EditText) layout.findViewById(R.id.input_id_text);
		inputIdText.setText(idPref.getString("id_text", "000000"));
		inputIdText.setKeyListener(new MyIdInputKeyListener());
		//	inputIdText.setInputType(InputType.TYPE_CLASS_NUMBER);
		inputIdWindow = new PopupWindow(layout, android.view.WindowManager.LayoutParams.WRAP_CONTENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT);
		inputIdWindow.setFocusable(true);
		inputIdWindow.setBackgroundDrawable(new BitmapDrawable());
		inputIdWindow.showAtLocation(layout, Gravity.CENTER, xOffset, yOffset);
		inputIdWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				//mContext.getSharedPreferences("id_prefer", 0).edit()
				//	.putString("id_text", inputIdText.getText().toString()).commit();
				// inputIdWindow = null;
				// handleLeftRightKey(3, true);
				// id设置
				holder.itemText.setText(idPref.getString("id_text", "000000"));
				mHandler.sendEmptyMessage(REFRESH_LIST);
			}
		});
	}

	private class MyIdInputKeyListener implements KeyListener {

		@Override
		public int getInputType() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			int currentID = Integer.parseInt(idPref.getString("id_text", "000000"));
			if (keyCode == 19) {
				currentID++;
				if (currentID > 999999) {
					currentID = 0;
				}
				idPref.edit().putString("id_text", String.format("%06d", currentID)).commit();

				//inputIdText.setText(String.format("%06d", currentID));
				text.clear();
				text.append(String.format("%06d", currentID));
				//inputIdText.requestFocus();
			}


			if (keyCode == 20) {
				currentID--;
				if (currentID < 0) {
					currentID = 999999;
				}

				idPref.edit().putString("id_text", String.format("%06d", currentID)).commit();

				//inputIdText.setText(String.format("%06d", currentID));
				text.clear();
				text.append(String.format("%06d", currentID));
				//inputIdText.requestFocus();
			}
			return true;
		}

		@Override
		public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onKeyOther(View view, Editable text, KeyEvent event) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void clearMetaKeyState(View view, Editable content, int states) {
			// TODO Auto-generated method stub

		}
	}



	// 写文件
	public void writeFile(int index) throws IOException {
		String write_str = powerDelayTimeArray[index];
		File file = new File(POWER_DELAY_TIME);
		FileOutputStream fos = new FileOutputStream(file);

		byte[] bytes = write_str.getBytes();
		fos.write(bytes);
		fos.close();
	}

	//读文件
	public String readFile() throws IOException {


		File file = new File(POWER_DELAY_TIME);

		if (!file.exists()) {
			return "0";
		}
		FileInputStream fis = new FileInputStream(file);
		int length = fis.available();
		byte [] buffer = new byte[length];
		fis.read(buffer);
		String res = EncodingUtils.getString(buffer, "UTF-8");
		fis.close();
		return res;
	}


	// 调节亮度
	private void changeBrightness(int brightnessValue) {

		// 系统的亮度范围为1-255，但是菜单的亮度范围是1-99

		if (brightnessValue < 40 ) brightnessValue = 40;
		if (brightnessValue > 70) brightnessValue = 70;

		int i = brightnessValue * 255 / 100;
		/*try {
			IPowerManager power = IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
			if (power != null) {
				power.setTemporaryScreenBrightnessSettingOverride(i);
			}
			Settings.System.putInt(mContext.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, i);

		} catch (RemoteException doe) {
		}*/

	}

	// 调节对比度
	private void changeContrast(int contrastValue) {

		// 系统的对比度范围为20 - 80，但是菜单的对比度范围是1-99

		int i = contrastValue * 60 / 100 + 20;
		/*DisplayManagerAw mDisplayManagerAw = (DisplayManagerAw) mContext.getSystemService(Context.DISPLAY_SERVICE_AW);
		mDisplayManagerAw.setDisplayContrast(0, contrastValue);
		Settings.System.putInt(mContext.getContentResolver(), Settings.System.COLOR_CONTRAST, i);*/
	}

	// 调节饱和度
	private void changeSaturation(int saturationValue) {

		// 系统的饱和度范围为20 - 80，但是菜单的饱和度范围是1-99

		int i = saturationValue * 60 / 100 + 20;
		/*DisplayManagerAw mDisplayManagerAww = (DisplayManagerAw) mContext.getSystemService(Context.DISPLAY_SERVICE_AW);
		mDisplayManagerAww.setDisplaySaturation(0, saturationValue);
		Settings.System.putInt(mContext.getContentResolver(), Settings.System.COLOR_SATURATION, i);*/

	}

}
