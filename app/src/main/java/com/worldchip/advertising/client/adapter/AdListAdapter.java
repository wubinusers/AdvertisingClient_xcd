package com.worldchip.advertising.client.adapter;

import com.worldchip.advertisingclient.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdListAdapter extends BaseAdapter {
	private Holder mHolder;
	SharedPreferences menuPref;
	private LayoutInflater inflater;
	private String[] adMenuArray;
	private String[] adSwitchArray;
	private String[] adModeArray;


	@Override
	public int getCount() {
		return adMenuArray.length;
	}

	public AdListAdapter(Context context) {
		menuPref = context.getSharedPreferences("ad_set", 0);
		initArray(context);

		inflater = LayoutInflater.from(context);

	}

	// 加载数组资源文件。
	private void initArray(Context context) {

		adMenuArray = context.getResources().getStringArray(R.array.ad_set_left_array);

		adSwitchArray = context.getResources().getStringArray(R.array.ad_switch);
		adModeArray = context.getResources().getStringArray(R.array.ad_mode);
	}

	@Override
	public Object getItem(int position) {
		return adMenuArray.length;
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
		holder.menuText.setText(adMenuArray[position]);
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
				menuItemName = adSwitchArray[menuPref.getInt("ad_switch_index", 0)];
				break;
			case 1:
				menuItemName = menuPref.getInt("ad_number", 2) + "";
				break;
			case 2:
				menuItemName = adModeArray[menuPref.getInt("ad_mode_index", 3)];
				break;
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
					int adSwitchIndex = changeIndex(index, witch, adSwitchArray);
					menuPref.edit().putInt("ad_switch_index", adSwitchIndex).commit();
					holder.itemText.setText(adSwitchArray[adSwitchIndex]);
					break;
				case 1:
					int adNumber = menuPref.getInt("ad_number", 2);
					if (witch == R.id.right_img) {
						adNumber++;
						if (adNumber > 30) {
							adNumber = 1;
						}
					} else {
						adNumber--;
						if (adNumber < 1) {
							adNumber = 30;
						}
					}
					menuPref.edit().putInt("ad_number", adNumber).commit();
					holder.itemText.setText(menuPref.getInt("ad_number", 2) + "");
					break;
				case 2:
					int adModeIndex = changeIndex(index, witch, adModeArray);
					menuPref.edit().putInt("ad_mode_index", adModeIndex).commit();
					holder.itemText.setText(adModeArray[adModeIndex]);
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
				int adSwitchIndex = menuPref.getInt("ad_switch_index", 0);
				index = adSwitchIndex;
				break;
			case 1:

				break;
			case 2:
				int adModeIndex = menuPref.getInt("ad_mode_index", 3);
				index = adModeIndex;
				break;

			default:
				break;
		}

		return index;
	}


}
