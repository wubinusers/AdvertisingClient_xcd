package com.worldchip.advertising.client.adapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.widget.PopupWindow.OnDismissListener;
import com.worldchip.advertisingclient.R;
import com.worldchip.advertising.client.utils.ScreenUtils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.SystemClock;
import android.os.Handler;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class TimeListAdapter extends BaseAdapter {
	private Holder mHolder;
	private LayoutInflater inflater;
	private Context mContext;
	SharedPreferences menuPref;
	private String[] timeMenuArray;
	private String[] timeSwitchArray;
	private String[] timeStyleArray;
	private String[] timeLocationArray;
	private String[] timeSizeArray;
	private String[] timeColorArray;

	PopupWindow changeTimeWindow;

	private Handler mHandler;

	EditText yearText;
	EditText monthText;
	EditText dayText;
	EditText hourText;
	EditText minText;
	EditText secText;
	Button timeSaveButton;

	int year2Int = 0;
	String year = "";

	int mon2Int = 0;
	String mon = "";


	int day2Int = 0;
	String day = "";



	int hour2Int = 0;
	String hour = "";


	int min2Int = 0;
	String min = "";


	int sec2Int = 0;
	String sec = "";


	View layout;

	public final static int RESET_TIME_MENU = 27723;

	public TimeListAdapter(Context context, Handler handler) {
		this.mHandler = handler;
		this.mContext = context;
		menuPref = context.getSharedPreferences("time_set", 0);
		initArray(context);
		inflater = LayoutInflater.from(context);

		year = getCurrentTime().substring(0, 4);
		year2Int = Integer.parseInt(year);
		mon = getCurrentTime().substring(5, 7);
		mon2Int = Integer.parseInt(mon);
		day = getCurrentTime().substring(8, 10);
		day2Int = Integer.parseInt(mon);
		hour = getCurrentTime().substring(11, 13);
		hour2Int = Integer.parseInt(hour);
		min = getCurrentTime().substring(14, 16);
		min2Int = Integer.parseInt(min);
		sec = getCurrentTime().substring(17, 19);
		sec2Int = Integer.parseInt(sec);

	}

	// 加载数组资源文件。
	private void initArray(Context context) {

		timeMenuArray = context.getResources().getStringArray(R.array.time_set_left_array);

		timeSwitchArray = context.getResources().getStringArray(R.array.time_switch);
		timeStyleArray = context.getResources().getStringArray(R.array.time_style);
		timeLocationArray = context.getResources().getStringArray(R.array.time_location);
		timeSizeArray = context.getResources().getStringArray(R.array.time_size);
		timeColorArray = context.getResources().getStringArray(R.array.time_color);
	}

	@Override
	public int getCount() {
		return timeMenuArray.length;
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
		holder.menuText.setText(timeMenuArray[position]);
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

		switch (position) {
			case 0:
				menuItemName = timeSwitchArray[menuPref.getInt("time_switch_index", 1)];
				break;
			case 1:
				// menuItemName = mContext.getString(R.string.set_system_time);
				menuItemName = getCurrentTime().substring(11, 16);
				break;
			case 2:
				menuItemName = timeStyleArray[menuPref.getInt("time_style_index", 2)];
				break;
			case 3:
				menuItemName = timeLocationArray[menuPref.getInt("time_location_index", 0)];
				break;
			case 4:
				menuItemName = timeSizeArray[menuPref.getInt("time_size_index", 1)];
				break;
			case 5:
				menuItemName = timeColorArray[menuPref.getInt("time_color_index", 1)];
			default:
				break;
		}

		return menuItemName;
	}

	public void handleKeyEvent (int position, boolean isRightKey) {
		Holder holder = mHolder;
		int witch = 0;
		if(isRightKey) {
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
					int timeSwitchIndex = changeIndex(index, witch, timeSwitchArray);
					menuPref.edit().putInt("time_switch_index", timeSwitchIndex).commit();
					holder.itemText.setText(timeSwitchArray[timeSwitchIndex]);
					break;
				case 1:
					// 调整时间
					initTimePopupWindow(mContext, holder);
					break;
				case 2:
					int timeStyleIndex = changeIndex(index, witch, timeStyleArray);
					menuPref.edit().putInt("time_style_index", timeStyleIndex).commit();
					holder.itemText.setText(timeStyleArray[timeStyleIndex]);
					break;
				case 3:
					int timeLocationIndex = changeIndex(index, witch, timeLocationArray);
					menuPref.edit().putInt("time_location_index", timeLocationIndex).commit();
					holder.itemText.setText(timeLocationArray[timeLocationIndex]);
					break;
				case 4:
					int timeSizeIndex = changeIndex(index, witch, timeSizeArray);
					menuPref.edit().putInt("time_size_index", timeSizeIndex).commit();
					holder.itemText.setText(timeSizeArray[timeSizeIndex]);
					break;
				case 5:
					int timeColorIndex = changeIndex(index, witch, timeColorArray);
					menuPref.edit().putInt("time_color_index", timeColorIndex).commit();
					holder.itemText.setText(timeColorArray[timeColorIndex]);
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
				int timeSwitchIndex = menuPref.getInt("time_switch_index", 1);
				index = timeSwitchIndex;
				break;
			case 1:

				break;
			case 2:
				int timeStyleIndex = menuPref.getInt("time_style_index", 0);
				index = timeStyleIndex;
				break;
			case 3:
				int timeLocationIndex = menuPref.getInt("time_location_index", 0);
				index = timeLocationIndex;
				break;
			case 4:
				int timeSizeIndex = menuPref.getInt("time_size_index", 0);
				index = timeSizeIndex;
				break;
			case 5:
				int timeColorIndex = menuPref.getInt("time_color_index", 0);
				index = timeColorIndex;
				break;
			default:
				break;
		}

		return index;
	}

	// 显示时间设置
	public void initTimePopupWindow(Context context, Holder holder) {
		year = getCurrentTime().substring(0, 4);
		year2Int = Integer.parseInt(year);
		mon = getCurrentTime().substring(5, 7);
		mon2Int = Integer.parseInt(mon);
		day = getCurrentTime().substring(8, 10);
		day2Int = Integer.parseInt(mon);
		hour = getCurrentTime().substring(11, 13);
		hour2Int = Integer.parseInt(hour);
		min = getCurrentTime().substring(14, 16);
		min2Int = Integer.parseInt(min);
		sec = getCurrentTime().substring(17, 19);
		sec2Int = Integer.parseInt(sec);
		//holder.itemText.setVisibility(INVISIBLE);
		int xOffset = 0;
		if (ScreenUtils.getScreenWidth(mContext) > ScreenUtils.getScreenHeight(mContext)) {
			xOffset = 280;
		} else {
			xOffset = 180;
		}
		int yOffset = -170;
		LayoutInflater inflater = LayoutInflater.from(context);
		layout = inflater.inflate(R.layout.change_time_layout, null);
		initTimeText();


		changeTimeWindow = new PopupWindow(layout, android.view.WindowManager.LayoutParams.WRAP_CONTENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT);
		changeTimeWindow.setFocusable(true);
		changeTimeWindow.setBackgroundDrawable(new BitmapDrawable());
		changeTimeWindow.showAtLocation(layout, Gravity.CENTER, xOffset, yOffset);

		changeTimeWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				setSystemDateTime();
				//changeTimeWindow.dismiss();
				mHandler.sendEmptyMessage(RESET_TIME_MENU);
			}
		});
	}


	public class TimeSetKeyListener implements  KeyListener{

		@Override
		public int getInputType() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
				case R.id.year_text:
					if(keyCode == 19) {
						year2Int++;
						if(year2Int > 2050) {
							year2Int = 1970;
						}
						yearText.requestFocus();
						text.clear();
						text.append(year2Int + "");
					}

					if (keyCode == 20) {
						year2Int--;
						if(year2Int < 1970) {
							year2Int = 2050;
						}
						text.clear();
						text.append(year2Int + "");
						yearText.requestFocus();
					}

					if (keyCode == 21) {

					}

					if (keyCode == 22) {
						monthText.requestFocus();
					}

					break;

				case R.id.month_text:
					if(keyCode == 19) {
						mon2Int++;
						if(mon2Int > 12){
							mon2Int = 1;
						}
						monthText.requestFocus();
						text.clear();
						//String curOnHour = String.format("%02d", mon2Int);
						text.append(String.format("%02d", mon2Int));
					}

					if (keyCode == 20) {
						mon2Int--;
						if(mon2Int < 1) {
							mon2Int = 12;
						}
						monthText.requestFocus();
						text.clear();
						text.append(String.format("%02d", mon2Int));
					}

					if (keyCode == 21) {
						yearText.requestFocus();
					}

					if (keyCode == 22) {
						dayText.requestFocus();
					}
					break;

				case R.id.day_text:
					if(keyCode == 19) {
						day2Int++;
						if(day2Int > getDayNumber(year2Int, mon2Int)){
							day2Int = 1;
						}
						dayText.requestFocus();
						text.clear();
						text.append(String.format("%02d", day2Int));
					}

					if (keyCode == 20) {
						day2Int--;
						if(day2Int < 1) {
							day2Int = getDayNumber(year2Int, mon2Int);
						}
						dayText.requestFocus();
						text.clear();
						text.append(String.format("%02d", day2Int));
					}

					if (keyCode == 21) {
						monthText.requestFocus();
					}

					if (keyCode == 22) {
						hourText.requestFocus();
					}
					break;

				case R.id.hour_text:
					if(keyCode == 19) {
						hour2Int++;
						if(hour2Int > 23){
							hour2Int = 0;
						}
						hourText.requestFocus();
						text.clear();
						text.append(String.format("%02d", hour2Int));
					}

					if (keyCode == 20) {
						hour2Int--;
						if(hour2Int < 0) {
							hour2Int = 23;
						}
						hourText.requestFocus();
						text.clear();
						text.append(String.format("%02d", hour2Int));
					}

					if (keyCode == 21) {
						dayText.requestFocus();
					}

					if (keyCode == 22) {
						minText.requestFocus();
					}
					break;

				case R.id.min_text:
					if(keyCode == 19) {
						min2Int++;
						if(min2Int > 59){
							min2Int = 0;
						}
						minText.requestFocus();
						text.clear();
						text.append(String.format("%02d", min2Int));
					}

					if (keyCode == 20) {
						min2Int--;
						if(min2Int < 0) {
							min2Int = 59;
						}
						minText.requestFocus();
						text.clear();
						text.append(String.format("%02d", min2Int));
					}

					if (keyCode == 21) {
						hourText.requestFocus();
					}

					if (keyCode == 22) {
						secText.requestFocus();
					}
					break;

				case R.id.sec_text:
					if(keyCode == 19) {
						sec2Int++;
						if(sec2Int > 59){
							sec2Int = 0;
						}
						secText.requestFocus();
						text.clear();
						text.append(String.format("%02d", sec2Int));
					}

					if (keyCode == 20) {
						sec2Int--;
						if(sec2Int < 0) {
							sec2Int = 59;
						}
						secText.requestFocus();
						text.clear();
						text.append(String.format("%02d", sec2Int));
					}

					if (keyCode == 21) {
						minText.requestFocus();
					}

					if (keyCode == 22) {
						//timeSaveButton.requestFocus();
					}
					break;

				default:
					break;
			}

			return true;
		}

		private int getDayNumber(int year, int month) {
			int days[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
			if (2 == month && 0 == (year % 4) && (0 != (year % 100) || 0 == (year % 400))) {
				days[1] = 29;
			}
			return (days[month - 1]);
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


	public class TimeSetFoucusChangeListener implements OnFocusChangeListener{

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if (hasFocus) {
				v.setBackgroundColor(Color.parseColor("#234567"));
			} else {
				v.setBackgroundColor(Color.WHITE);
			}
		}
	}


	private void initTimeText() {
		yearText = (EditText) layout.findViewById(R.id.year_text);
		yearText.setText(year);
		yearText.setKeyListener(new TimeSetKeyListener());
		yearText.setOnFocusChangeListener(new TimeSetFoucusChangeListener());

		monthText = (EditText) layout.findViewById(R.id.month_text);
		monthText.setText(mon);
		monthText.setKeyListener(new TimeSetKeyListener());
		monthText.setOnFocusChangeListener(new TimeSetFoucusChangeListener());

		dayText = (EditText) layout.findViewById(R.id.day_text);
		dayText.setText(day);
		dayText.setKeyListener(new TimeSetKeyListener());
		dayText.setOnFocusChangeListener(new TimeSetFoucusChangeListener());

		hourText = (EditText) layout.findViewById(R.id.hour_text);
		hourText.setText(hour);
		hourText.setKeyListener(new TimeSetKeyListener());
		hourText.setOnFocusChangeListener(new TimeSetFoucusChangeListener());

		minText = (EditText) layout.findViewById(R.id.min_text);
		minText.setText(min);
		minText.setKeyListener(new TimeSetKeyListener());
		minText.setOnFocusChangeListener(new TimeSetFoucusChangeListener());

		secText = (EditText) layout.findViewById(R.id.sec_text);
		secText.setText(sec);
		secText.setKeyListener(new TimeSetKeyListener());
		secText.setOnFocusChangeListener(new TimeSetFoucusChangeListener());
	}

	private String getCurrentTime() {
		String date = "";
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		date = sDateFormat.format(new java.util.Date());
		return date;
	}

	// 设定系统时间,要make编译才能有效
	private void setSystemDateTime() {

		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(yearText.getText().toString()));
		c.set(Calendar.MONTH, Integer.parseInt(monthText.getText().toString()) - 1);
		c.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dayText.getText().toString()));
		c.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hourText.getText().toString()));
		c.set(Calendar.MINUTE, Integer.parseInt(minText.getText().toString()));

		long when = c.getTimeInMillis();

		if (when / 1000 < Integer.MAX_VALUE) {
			SystemClock.setCurrentTimeMillis(when);
		}
	}

}
