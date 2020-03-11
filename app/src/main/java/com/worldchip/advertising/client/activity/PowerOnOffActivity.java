package com.worldchip.advertising.client.activity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.Date;
import java.util.TimerTask;

import com.worldchip.advertising.client.adapter.EveryDayAdapter;
import com.worldchip.advertising.client.adapter.FriAdapter;
import com.worldchip.advertising.client.adapter.MonAdapter;
import com.worldchip.advertising.client.adapter.SatAdapter;
import com.worldchip.advertising.client.adapter.SunAdapter;
import com.worldchip.advertising.client.adapter.ThuAdapter;
import com.worldchip.advertising.client.adapter.TueAdapter;
import com.worldchip.advertising.client.adapter.WenAdapter;
import com.worldchip.advertising.client.service.DetectTimeService;
import com.worldchip.advertisingclient.R;

import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PowerOnOffActivity extends Activity {

	private ListView mList;
	private EveryDayAdapter everyDayAdapter;
	private MonAdapter monAdapter;
	private TueAdapter tueAdapter;
	private WenAdapter wenAdapter;
	private ThuAdapter thuAdapter;
	private FriAdapter friAdapter;
	private SatAdapter satAdapter;
	private SunAdapter sunAdapter;

	private ImageView eveBtn;
	private ImageView monBtn;
	private ImageView tueBtn;
	private ImageView wenBtn;
	private ImageView thuBtn;
	private ImageView friBtn;
	private ImageView satBtn;
	private ImageView sunBtn;

	private ImageView eveSelect;
	private ImageView monSelect;
	private ImageView tueSelect;
	private ImageView wenSelect;
	private ImageView thuSelect;
	private ImageView friSelect;
	private ImageView satSelect;
	private ImageView sunSelect;

	private SharedPreferences eveShare;
	private SharedPreferences monShare;
	private SharedPreferences tueShare;
	private SharedPreferences wenShare;
	private SharedPreferences thuShare;
	private SharedPreferences friShare;
	private SharedPreferences satShare;
	private SharedPreferences sunShare;

	private boolean eveChecked;
	private boolean monChecked;
	private boolean tueChecked;
	private boolean wenChecked;
	private boolean thuChecked;
	private boolean friChecked;
	private boolean satChecked;
	private boolean sunChecked;

	private int weekIndex = 0;
	private int mSelectPosition;

	private TextView currentTimeText;

	private final static int REFRESH_TIME = 0x555;
	private Timer mSystemTime = new Timer();

	private ArrayList<String> allUsefulTime = new ArrayList<String>();

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case EveryDayAdapter.RECREATE:
					// 此时也要向服务发送开关机数据。
					everyDayAdapter = null;
					everyDayAdapter = new EveryDayAdapter(PowerOnOffActivity.this, mHandler);
					mList.setAdapter(everyDayAdapter);
					mList.setSelection(mSelectPosition);
					addAllUsefulTime();
					break;

				case MonAdapter.RECREATE:
					monAdapter = null;
					monAdapter = new MonAdapter(PowerOnOffActivity.this, mHandler);
					mList.setAdapter(monAdapter);
					mList.setSelection(mSelectPosition);
					addAllUsefulTime();
					break;

				case TueAdapter.RECREATE:
					tueAdapter = null;
					tueAdapter = new TueAdapter(PowerOnOffActivity.this, mHandler);
					mList.setAdapter(tueAdapter);
					mList.setSelection(mSelectPosition);
					addAllUsefulTime();
					break;

				case WenAdapter.RECREATE:
					wenAdapter = null;
					wenAdapter = new WenAdapter(PowerOnOffActivity.this, mHandler);
					mList.setAdapter(wenAdapter);
					mList.setSelection(mSelectPosition);
					addAllUsefulTime();
					break;

				case ThuAdapter.RECREATE:
					thuAdapter = null;
					thuAdapter = new ThuAdapter(PowerOnOffActivity.this, mHandler);
					mList.setAdapter(thuAdapter);
					mList.setSelection(mSelectPosition);
					addAllUsefulTime();
					break;

				case FriAdapter.RECREATE:
					friAdapter = null;
					friAdapter = new FriAdapter(PowerOnOffActivity.this, mHandler);
					mList.setAdapter(friAdapter);
					mList.setSelection(mSelectPosition);
					addAllUsefulTime();
					break;

				case SatAdapter.RECREATE:
					satAdapter = null;
					satAdapter = new SatAdapter(PowerOnOffActivity.this, mHandler);
					mList.setAdapter(satAdapter);
					mList.setSelection(mSelectPosition);
					addAllUsefulTime();
					break;

				case SunAdapter.RECREATE:
					sunAdapter = null;
					sunAdapter = new SunAdapter(PowerOnOffActivity.this, mHandler);
					mList.setAdapter(sunAdapter);
					mList.setSelection(mSelectPosition);
					addAllUsefulTime();
					break;

				case REFRESH_TIME:
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd E HH:mm:ss");
					df.format(new Date());
					currentTimeText.setText(df.format(new Date()));
					break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.power_on_off_main);
		initAllShare();
		initAllAdapter();
		initList();
		initAllBtn();
		startSystemTimer();
	}

	private void startSystemTimer() {
		mSystemTime.schedule(new TimerTask() {
			@Override
			public void run() {
				mHandler.sendEmptyMessage(REFRESH_TIME);
			}
		}, 0, 1 * 1000);
	}

	@Override
	public boolean bindService(Intent service, ServiceConnection conn, int flags) {
		return super.bindService(service, conn, flags);
	}

	// 初始化所有的Adapter
	private void initAllAdapter() {
		everyDayAdapter = new EveryDayAdapter(this, mHandler);
		monAdapter = new MonAdapter(this, mHandler);
		tueAdapter = new TueAdapter(this, mHandler);
		wenAdapter = new WenAdapter(this, mHandler);
		thuAdapter = new ThuAdapter(this, mHandler);
		friAdapter = new FriAdapter(this, mHandler);
		satAdapter = new SatAdapter(this, mHandler);
		sunAdapter = new SunAdapter(this, mHandler);
	}

	// 初始化share
	private void initAllShare() {
		eveShare = getSharedPreferences("eve", 0);
		monShare = getSharedPreferences("mon", 0);
		tueShare = getSharedPreferences("tue", 0);
		wenShare = getSharedPreferences("wen", 0);
		thuShare = getSharedPreferences("thu", 0);
		friShare = getSharedPreferences("fri", 0);
		satShare = getSharedPreferences("sat", 0);
		sunShare = getSharedPreferences("sun", 0);
	}

	private void initList() {
		mList = (ListView) findViewById(R.id.on_off_list);
		mList.setAdapter(everyDayAdapter);
		mList.setOnKeyListener(new MyListKeyListener());
		mList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				mSelectPosition = position;
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
		});
	}

	private void initAllBtn() {
		eveChecked = eveShare.getBoolean("isEnable", false);
		monChecked = monShare.getBoolean("isEnable", false);
		tueChecked = tueShare.getBoolean("isEnable", false);
		wenChecked = wenShare.getBoolean("isEnable", false);
		thuChecked = thuShare.getBoolean("isEnable", false);
		friChecked = friShare.getBoolean("isEnable", false);
		satChecked = satShare.getBoolean("isEnable", false);
		sunChecked = sunShare.getBoolean("isEnable", false);

		// 右上角显示时间的。
		currentTimeText = (TextView) findViewById(R.id.current_time_text);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd E HH:mm:ss");
		df.format(new Date());
		currentTimeText.setText(df.format(new Date()));

		eveBtn = (ImageView) findViewById(R.id.everyday_btn);
		monBtn = (ImageView) findViewById(R.id.mon_btn);
		tueBtn = (ImageView) findViewById(R.id.tue_btn);
		wenBtn = (ImageView) findViewById(R.id.wen_btn);
		thuBtn = (ImageView) findViewById(R.id.thu_btn);
		friBtn = (ImageView) findViewById(R.id.fri_btn);
		satBtn = (ImageView) findViewById(R.id.sat_btn);
		sunBtn = (ImageView) findViewById(R.id.sun_btn);

		eveBtn.setOnFocusChangeListener(new MyButtonFocusListener());
		monBtn.setOnFocusChangeListener(new MyButtonFocusListener());
		tueBtn.setOnFocusChangeListener(new MyButtonFocusListener());
		wenBtn.setOnFocusChangeListener(new MyButtonFocusListener());
		thuBtn.setOnFocusChangeListener(new MyButtonFocusListener());
		friBtn.setOnFocusChangeListener(new MyButtonFocusListener());
		satBtn.setOnFocusChangeListener(new MyButtonFocusListener());
		sunBtn.setOnFocusChangeListener(new MyButtonFocusListener());

		eveBtn.setOnClickListener(new MyWeekBtnClickListener());
		monBtn.setOnClickListener(new MyWeekBtnClickListener());
		tueBtn.setOnClickListener(new MyWeekBtnClickListener());
		wenBtn.setOnClickListener(new MyWeekBtnClickListener());
		thuBtn.setOnClickListener(new MyWeekBtnClickListener());
		friBtn.setOnClickListener(new MyWeekBtnClickListener());
		satBtn.setOnClickListener(new MyWeekBtnClickListener());
		sunBtn.setOnClickListener(new MyWeekBtnClickListener());

		eveSelect = (ImageView) findViewById(R.id.everyday_select);
		if (eveChecked) {
			eveSelect.setVisibility(View.VISIBLE);
		}

		monSelect = (ImageView) findViewById(R.id.mon_select);
		if (monChecked) {
			monSelect.setVisibility(View.VISIBLE);
		}

		tueSelect = (ImageView) findViewById(R.id.tue_select);
		if (tueChecked) {
			tueSelect.setVisibility(View.VISIBLE);
		}

		wenSelect = (ImageView) findViewById(R.id.wen_select);
		if (wenChecked) {
			wenSelect.setVisibility(View.VISIBLE);
		}

		thuSelect = (ImageView) findViewById(R.id.thu_select);
		if (thuChecked) {
			thuSelect.setVisibility(View.VISIBLE);
		}

		friSelect = (ImageView) findViewById(R.id.fri_select);
		if (friChecked) {
			friSelect.setVisibility(View.VISIBLE);
		}

		satSelect = (ImageView) findViewById(R.id.sat_select);
		if (satChecked) {
			satSelect.setVisibility(View.VISIBLE);
		}

		sunSelect = (ImageView) findViewById(R.id.sun_select);
		if (sunChecked) {
			sunSelect.setVisibility(View.VISIBLE);
		}

	}

	private class MyListKeyListener implements View.OnKeyListener {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				handleKeyEvent(keyCode);

				if (keyCode == KeyEvent.KEYCODE_MENU) {
					weekIndex++;
					if (weekIndex > 7) {
						weekIndex = 0;
					}
					switchWeek(weekIndex);
					setWeekFocusImg(weekIndex);
				}
			}

			return false;
		}
	}

	private void handleKeyEvent(int keycode) {
		switch (keycode) {
			case 22:
				// 方向右键。
				showPopupWindow();
				break;
			case 23:
				// enter键。
				setChecked();
				break;
			case 120:
				// 快进键 90。
				// COPY键 120.
				addItem();
				break;
			case 67:
				// del键。
				removeItem();
				break;
			default:
				break;
		}
	}

	private void removeItem() {

		if (weekIndex == 0) {
			everyDayAdapter.removeItem();
			everyDayAdapter.notifyDataSetChanged();
			return;
		}

		if (weekIndex == 1) {
			monAdapter.removeItem();
			monAdapter.notifyDataSetChanged();
			return;
		}
		if (weekIndex == 2) {
			tueAdapter.removeItem();
			tueAdapter.notifyDataSetChanged();
			return;
		}
		if (weekIndex == 3) {
			wenAdapter.removeItem();
			wenAdapter.notifyDataSetChanged();
			return;
		}
		if (weekIndex == 4) {
			thuAdapter.removeItem();
			thuAdapter.notifyDataSetChanged();
			return;
		}
		if (weekIndex == 5) {
			friAdapter.removeItem();
			friAdapter.notifyDataSetChanged();
			return;
		}
		if (weekIndex == 6) {
			satAdapter.removeItem();
			satAdapter.notifyDataSetChanged();
			return;
		}
		if (weekIndex == 7) {
			sunAdapter.removeItem();
			sunAdapter.notifyDataSetChanged();
			return;
		}
	}

	private void addItem() {

		if (weekIndex == 0) {
			everyDayAdapter.addItem(mSelectPosition);
			everyDayAdapter.notifyDataSetChanged();
			return;
		}

		if (weekIndex == 1) {
			monAdapter.addItem(mSelectPosition);
			monAdapter.notifyDataSetChanged();
			return;
		}
		if (weekIndex == 2) {
			tueAdapter.addItem(mSelectPosition);
			tueAdapter.notifyDataSetChanged();
			return;
		}
		if (weekIndex == 3) {
			wenAdapter.addItem(mSelectPosition);
			wenAdapter.notifyDataSetChanged();
			return;
		}
		if (weekIndex == 4) {
			thuAdapter.addItem(mSelectPosition);
			thuAdapter.notifyDataSetChanged();
			return;
		}
		if (weekIndex == 5) {
			friAdapter.addItem(mSelectPosition);
			friAdapter.notifyDataSetChanged();
			return;
		}
		if (weekIndex == 6) {
			satAdapter.addItem(mSelectPosition);
			satAdapter.notifyDataSetChanged();
			return;
		}
		if (weekIndex == 7) {
			sunAdapter.addItem(mSelectPosition);
			sunAdapter.notifyDataSetChanged();
			return;
		}
	}

	private void showPopupWindow() {
		if (weekIndex == 0) {
			everyDayAdapter.initTimePopupWindow(PowerOnOffActivity.this, mSelectPosition);
			return;
		}

		if (weekIndex == 1) {
			monAdapter.initTimePopupWindow(PowerOnOffActivity.this, mSelectPosition);
			return;
		}
		if (weekIndex == 2) {
			tueAdapter.initTimePopupWindow(PowerOnOffActivity.this, mSelectPosition);
			return;
		}
		if (weekIndex == 3) {
			wenAdapter.initTimePopupWindow(PowerOnOffActivity.this, mSelectPosition);
			return;
		}
		if (weekIndex == 4) {
			thuAdapter.initTimePopupWindow(PowerOnOffActivity.this, mSelectPosition);
			return;
		}
		if (weekIndex == 5) {
			friAdapter.initTimePopupWindow(PowerOnOffActivity.this, mSelectPosition);
			return;
		}
		if (weekIndex == 6) {
			satAdapter.initTimePopupWindow(PowerOnOffActivity.this, mSelectPosition);
			return;
		}
		if (weekIndex == 7) {
			sunAdapter.initTimePopupWindow(PowerOnOffActivity.this, mSelectPosition);
			return;
		}
	}

	private void setChecked() {
		getAllGroupChecked();
		if (weekIndex == 0) {
			if (monChecked || tueChecked || wenChecked || thuChecked || friChecked || satChecked || sunChecked) {
				Toast.makeText(PowerOnOffActivity.this, getString(R.string.toast_select_week), Toast.LENGTH_LONG)
						.show();
			} else {
				everyDayAdapter.setChecked(mSelectPosition);
				everyDayAdapter.notifyDataSetChanged();
			}
		}

		if (weekIndex == 1) {
			if (eveChecked) {
				Toast.makeText(PowerOnOffActivity.this, getString(R.string.toast_select_every), Toast.LENGTH_LONG)
						.show();
			} else {
				monAdapter.setChecked(mSelectPosition);
				monAdapter.notifyDataSetChanged();
			}
		}

		if (weekIndex == 2) {
			if (eveChecked) {
				Toast.makeText(PowerOnOffActivity.this, getString(R.string.toast_select_every), Toast.LENGTH_LONG)
						.show();
			} else {
				tueAdapter.setChecked(mSelectPosition);
				tueAdapter.notifyDataSetChanged();
			}
		}

		if (weekIndex == 3) {
			if (eveChecked) {
				Toast.makeText(PowerOnOffActivity.this, getString(R.string.toast_select_every), Toast.LENGTH_LONG)
						.show();
			} else {
				wenAdapter.setChecked(mSelectPosition);
				wenAdapter.notifyDataSetChanged();
			}
		}

		if (weekIndex == 4) {
			if (eveChecked) {
				Toast.makeText(PowerOnOffActivity.this, getString(R.string.toast_select_every), Toast.LENGTH_LONG)
						.show();
			} else {
				thuAdapter.setChecked(mSelectPosition);
				thuAdapter.notifyDataSetChanged();
			}
		}

		if (weekIndex == 5) {
			if (eveChecked) {
				Toast.makeText(PowerOnOffActivity.this, getString(R.string.toast_select_every), Toast.LENGTH_LONG)
						.show();
			} else {
				friAdapter.setChecked(mSelectPosition);
				friAdapter.notifyDataSetChanged();
			}
		}

		if (weekIndex == 6) {
			if (eveChecked) {
				Toast.makeText(PowerOnOffActivity.this, getString(R.string.toast_select_every), Toast.LENGTH_LONG)
						.show();
			} else {
				satAdapter.setChecked(mSelectPosition);
				satAdapter.notifyDataSetChanged();
			}
		}

		if (weekIndex == 7) {
			if (eveChecked) {
				Toast.makeText(PowerOnOffActivity.this, getString(R.string.toast_select_every), Toast.LENGTH_LONG)
						.show();
			} else {
				sunAdapter.setChecked(mSelectPosition);
				sunAdapter.notifyDataSetChanged();
			}
		}

		afterClickedCheck();
	}

	private void getAllGroupChecked() {
		everyDayAdapter.getGroupChecked();
		monAdapter.getGroupChecked();
		tueAdapter.getGroupChecked();
		wenAdapter.getGroupChecked();
		thuAdapter.getGroupChecked();
		friAdapter.getGroupChecked();
		satAdapter.getGroupChecked();
		sunAdapter.getGroupChecked();
	}

	private void addAllUsefulTime() {
		// 长度35的数组存放我有要发送的数据。
		String[] strings = new String[35];
		allUsefulTime.clear();
		allUsefulTime.addAll(everyDayAdapter.getGroupChecked());
		allUsefulTime.addAll(monAdapter.getGroupChecked());
		allUsefulTime.addAll(tueAdapter.getGroupChecked());
		allUsefulTime.addAll(wenAdapter.getGroupChecked());
		allUsefulTime.addAll(thuAdapter.getGroupChecked());
		allUsefulTime.addAll(friAdapter.getGroupChecked());
		allUsefulTime.addAll(satAdapter.getGroupChecked());
		allUsefulTime.addAll(sunAdapter.getGroupChecked());

		// 发送机制有问题，万一发送了就只能第二天开
		for (int i = 0; i < allUsefulTime.size(); i++) {
			strings[i] = allUsefulTime.get(i);
		}
		Intent i = new Intent();
		i.setClass(PowerOnOffActivity.this, DetectTimeService.class);
		i.putExtra("useful_time", strings);
		i.addFlags(99999);
		startService(i);
	}

	private void afterClickedCheck() {
		addAllUsefulTime();
		eveChecked = eveShare.getBoolean("group_check", false);
		if (eveChecked) {
			eveShare.edit().putBoolean("isEnable", true).commit();
			eveSelect.setVisibility(View.VISIBLE);
		} else {
			eveShare.edit().putBoolean("isEnable", false).commit();
			eveSelect.setVisibility(View.INVISIBLE);
		}

		monChecked = monShare.getBoolean("group_check", false);
		if (monChecked) {
			monShare.edit().putBoolean("isEnable", true).commit();
			monSelect.setVisibility(View.VISIBLE);
		} else {
			monShare.edit().putBoolean("isEnable", false).commit();
			monSelect.setVisibility(View.INVISIBLE);
		}

		tueChecked = tueShare.getBoolean("group_check", false);
		if (tueChecked) {
			tueShare.edit().putBoolean("isEnable", true).commit();
			tueSelect.setVisibility(View.VISIBLE);
		} else {
			tueShare.edit().putBoolean("isEnable", false).commit();
			tueSelect.setVisibility(View.INVISIBLE);
		}

		wenChecked = wenShare.getBoolean("group_check", false);
		if (wenChecked) {
			wenShare.edit().putBoolean("isEnable", true).commit();
			wenSelect.setVisibility(View.VISIBLE);
		} else {
			wenShare.edit().putBoolean("isEnable", false).commit();
			wenSelect.setVisibility(View.INVISIBLE);
		}

		thuChecked = thuShare.getBoolean("group_check", false);
		if (thuChecked) {
			thuShare.edit().putBoolean("isEnable", true).commit();
			thuSelect.setVisibility(View.VISIBLE);
		} else {
			thuShare.edit().putBoolean("isEnable", false).commit();
			thuSelect.setVisibility(View.INVISIBLE);
		}

		friChecked = friShare.getBoolean("group_check", false);
		if (friChecked) {
			friShare.edit().putBoolean("isEnable", true).commit();
			friSelect.setVisibility(View.VISIBLE);
		} else {
			friShare.edit().putBoolean("isEnable", false).commit();
			friSelect.setVisibility(View.INVISIBLE);
		}

		satChecked = satShare.getBoolean("group_check", false);
		if (satChecked) {
			satShare.edit().putBoolean("isEnable", true).commit();
			satSelect.setVisibility(View.VISIBLE);
		} else {
			satShare.edit().putBoolean("isEnable", false).commit();
			satSelect.setVisibility(View.INVISIBLE);
		}

		sunChecked = sunShare.getBoolean("group_check", false);
		if (sunChecked) {
			sunShare.edit().putBoolean("isEnable", true).commit();
			sunSelect.setVisibility(View.VISIBLE);
		} else {
			sunShare.edit().putBoolean("isEnable", false).commit();
			sunSelect.setVisibility(View.INVISIBLE);
		}

	}

	private void resetAllWeekImg() {
		eveBtn.setImageDrawable(getResources().getDrawable(R.drawable.eve));
		monBtn.setImageDrawable(getResources().getDrawable(R.drawable.mon));
		tueBtn.setImageDrawable(getResources().getDrawable(R.drawable.tue));
		wenBtn.setImageDrawable(getResources().getDrawable(R.drawable.wen));
		thuBtn.setImageDrawable(getResources().getDrawable(R.drawable.thu));
		friBtn.setImageDrawable(getResources().getDrawable(R.drawable.fri));
		satBtn.setImageDrawable(getResources().getDrawable(R.drawable.sat));
		sunBtn.setImageDrawable(getResources().getDrawable(R.drawable.sun));
	}

	// 设置顶部的
	private void setWeekFocusImg(int weekIndex) {
		resetAllWeekImg();
		if (weekIndex == 0) {
			eveBtn.setImageDrawable(getResources().getDrawable(R.drawable.eve_sel));
			return;
		}

		if (weekIndex == 1) {
			monBtn.setImageDrawable(getResources().getDrawable(R.drawable.mon_sel));
			return;
		}

		if (weekIndex == 2) {
			tueBtn.setImageDrawable(getResources().getDrawable(R.drawable.tue_sel));
			return;
		}

		if (weekIndex == 3) {
			wenBtn.setImageDrawable(getResources().getDrawable(R.drawable.wen_sel));
			return;
		}

		if (weekIndex == 4) {
			thuBtn.setImageDrawable(getResources().getDrawable(R.drawable.thu_sel));
			return;
		}

		if (weekIndex == 5) {
			friBtn.setImageDrawable(getResources().getDrawable(R.drawable.fri_sel));
			return;
		}

		if (weekIndex == 6) {
			satBtn.setImageDrawable(getResources().getDrawable(R.drawable.sat_sel));
			return;
		}

		if (weekIndex == 7) {
			sunBtn.setImageDrawable(getResources().getDrawable(R.drawable.sun_sel));
			return;
		}
	}

	private class MyButtonFocusListener implements View.OnFocusChangeListener {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				v.setBackgroundColor(Color.parseColor("#FF00AEFF"));
				setAdapter(v);
			} else {
				v.setBackgroundColor(Color.TRANSPARENT);
			}
		}
	}

	private void switchWeek(int weekIndex) {
		switch (weekIndex) {
			case 0:
				mList.setAdapter(everyDayAdapter);
				break;
			case 1:
				mList.setAdapter(monAdapter);
				break;
			case 2:
				mList.setAdapter(tueAdapter);
				break;
			case 3:
				mList.setAdapter(wenAdapter);
				break;
			case 4:
				mList.setAdapter(thuAdapter);
				break;
			case 5:
				mList.setAdapter(friAdapter);
				break;
			case 6:
				mList.setAdapter(satAdapter);
				break;
			case 7:
				mList.setAdapter(sunAdapter);
				break;
		}
	}

	private void setAdapter(View v) {
		switch (v.getId()) {
			case R.id.everyday_btn:
				mList.setAdapter(everyDayAdapter);
				break;
			case R.id.mon_btn:
				mList.setAdapter(monAdapter);
				break;
			case R.id.tue_btn:
				mList.setAdapter(tueAdapter);
				break;
			case R.id.wen_btn:
				mList.setAdapter(wenAdapter);
				break;
			case R.id.thu_btn:
				mList.setAdapter(thuAdapter);
				break;
			case R.id.fri_btn:
				mList.setAdapter(friAdapter);
				break;
			case R.id.sat_btn:
				mList.setAdapter(satAdapter);
				break;
			case R.id.sun_btn:
				mList.setAdapter(sunAdapter);
				break;
		}
	}

	// 顶部的星期点击事件，供触摸点击时使用。
	private class MyWeekBtnClickListener implements View.OnClickListener {
		@Override
		public void onClick(View v) {
			setAdapter(v);
		}
	}

}
