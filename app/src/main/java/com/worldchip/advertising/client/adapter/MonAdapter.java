package com.worldchip.advertising.client.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import java.util.ArrayList;

import com.worldchip.advertising.client.utils.ChangeHourOrMinUtil;
import com.worldchip.advertising.client.utils.ChangeYPosition;
import com.worldchip.advertising.client.utils.ScreenUtils;
import com.worldchip.advertisingclient.R;

/**
 * Created by RYX on 2016/4/27.
 */
public class MonAdapter extends BaseAdapter {
    private SharedPreferences mShare;
    private LayoutInflater mInflate;
    private View layout;
    private int count = 1;
    private int mPosition;
    private int popPosition = 0;
    private Handler mHandler;
    private Context mContext;
    public final static int RECREATE = 0x223;

    private PopupWindow setTimePop;

    private EditText onHourPopEdit;
    private EditText onMinPopEdit;
    private EditText offHourPopEdit;
    private EditText offMinPopEdit;

    private String onShareHour;
    private String onShareMin;
    private String offShareHour;
    private String offShareMin;

    private int groups = 1;

    private boolean isGroupChecked = false;

    private ArrayList<String> groupDetails = new ArrayList<String>();

    private ArrayList<String> groupNames = new ArrayList<String>();

    private ArrayList<String> usefulGroup = new ArrayList<String>();


    public MonAdapter(Context context, Handler handler) {
	    mContext = context;
        this.mHandler = handler;
        mInflate = LayoutInflater.from(context);
        mShare = context.getSharedPreferences("mon", 0);
        // 淇濆瓨缁勬暟銆�
        groups = mShare.getInt("groups", 1);
        groupDetails = getGroupsFromShare(groups);
    }

    @Override
    public int getCount() {
        return groups;
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
            view = mInflate.inflate(R.layout.time_setting_list, null);
            holder.checkBox = (CheckBox) view.findViewById(R.id.check_box);
            holder.onHourEdit = (EditText) view.findViewById(R.id.on_hour_edit);
            holder.onMinEdit = (EditText) view.findViewById(R.id.on_min_edit);
            holder.offHourEdit = (EditText) view.findViewById(R.id.off_hour_edit);
            holder.offMinEdit = (EditText) view.findViewById(R.id.off_min_edit);
            holder.addItemBtn = (ImageButton) view.findViewById(R.id.add_item);
            holder.delItemBtn = (ImageButton) view.findViewById(R.id.del_item);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        mPosition = position;

        if (position == 0) {
            holder.addItemBtn.setVisibility(View.INVISIBLE);
        }

        if (position == groups - 1) {
            holder.addItemBtn.setVisibility(View.VISIBLE);
        }

        holder.checkBox.setChecked(getChecked(position));
        holder.onHourEdit.setText(getText(position, 2, 4));
        holder.onMinEdit.setText(getText(position, 5, 7));
        holder.offHourEdit.setText(getText(position, 7, 9));
        holder.offMinEdit.setText(getText(position, 10, 12));
        holder.checkBox.setOnFocusChangeListener(new MyImageButtonFocusListener());
        holder.addItemBtn.setOnFocusChangeListener(new MyImageButtonFocusListener());
        holder.delItemBtn.setOnFocusChangeListener(new MyImageButtonFocusListener());
        holder.delItemBtn.setOnClickListener(new MyButtonClickListener());
        holder.addItemBtn.setOnClickListener(new MyButtonClickListener());
        return view;
    }

    public static class Holder {
        CheckBox checkBox;
        EditText onHourEdit;
        EditText onMinEdit;
        EditText offHourEdit;
        EditText offMinEdit;
        ImageButton addItemBtn;
        ImageButton delItemBtn;
    }

    private class MyImageButtonFocusListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                if (v.getId() == R.id.add_item || v.getId() == R.id.del_item || v.getId() == R.id.check_box) {
                    v.setBackgroundColor(Color.parseColor("#FFFFB300"));
                } else {
                    v.setBackgroundColor(Color.parseColor("#FF00AEFF"));
                }

            } else {
                v.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    private class MyButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.del_item) {
                removeItem();
            }

            if (v.getId() == R.id.add_item) {
                addItem(mPosition);
            }
        }
    }

    // 鍒犻櫎鏈�鍚庝竴缁勩��
    public void removeItem() {
        groups = mShare.getInt("groups", 1);
        if (groups > 1) {
            groups--;
            mShare.edit().putInt("groups", groups).commit();
            mShare.edit().remove("group" + groups).commit();
            // groupDetails.remove(groupDetails.size() - 1);
            mHandler.sendEmptyMessage(RECREATE);
            return;
        }
    }

    public void addItem(int position) {
        if (groups < 5) {
            groups++;
            mShare.edit().putInt("groups", groups).commit();
            mShare.edit().putString(autoNextGroup(groups), "1009:0021:00").commit();
            groupDetails.add(position + 1, "1009:0021:00");
            mHandler.sendEmptyMessage(RECREATE);
        }
    }

    public void resetAllChecked() {
        String str = "";
        String afterStr = "";
        for (int i = 0; i <= groups; i++) {
            str = mShare.getString("group" + i, "1009:0021:00");
            afterStr = str.substring(0, 1) + 0 + str.substring(2, 12);
            mShare.edit().putString("group" + i, afterStr).commit();
        }
    }

    public void setChecked(int position) {
        if (getChecked(position)) {
            mShare.edit().putString("group" + position, "10" + getText(position, 2, 12)).commit();
        } else {
            mShare.edit().putString("group" + position, "11" + getText(position, 2, 12)).commit();
        }
    }

    public boolean getChecked(int position) {
        String str = mShare.getString("group" + position, "1009:0021:00").substring(1, 2);
        if (str.equals("0")) {
            return false;
        }

        if (str.equals("1")) {
            return true;
        }

        return false;
    }

    private String getCheckedString(int position) {
        String str = mShare.getString("group" + position, "1009:0021:00").substring(1, 2);
        return str;
    }

    private String getText(int position, int start, int end) {
        String str = groupDetails.get(position).substring(start, end);
        return str;
    }

    // 鑷姩鐢熸垚涓�涓暟缁勶紝
    private ArrayList<String> autoNameGroups() {
        for (int i = groups; i < count; i++) {
            groupNames.add("group" + i);
        }
        return groupNames;
    }

    private String autoNextGroup(int groups) {
        return "group" + (groups - 1);
    }

    // 浠巗hare閲岄潰鏉ヨ鍙栧垎缁勮缁嗕俊鎭��
    private ArrayList<String> getGroupsFromShare(int groups) {
        String str = "";
        String str1 = "";
        usefulGroup.clear();
        ArrayList<String> checkStringList = new ArrayList<String>();
        for (int i = 0; i <= groups; i++) {
            str = mShare.getString("group" + i, "1009:0021:00");
            str1 = str.substring(1, 2);
            if (str1.equals("1")) {
                usefulGroup.add(str);
            }
            checkStringList.add(str1);
            groupDetails.add(str);
        }

        if (checkStringList.contains("1")) {
            isGroupChecked = true;
        } else {
            isGroupChecked = false;
        }

        mShare.edit().putBoolean("group_check", isGroupChecked).commit();

        return groupDetails;
    }

    // 鑾峰彇姣忎釜鏄熸湡鏄惁鏈夊凡缁廋hecked 鐨勩��
    public ArrayList<String> getGroupChecked() {
        getGroupsFromShare(groups);
        return usefulGroup;
    }


    public void refreshGroup() {
        groups = mShare.getInt("groups", 1);
        groupDetails = getGroupsFromShare(groups);
    }


    // 鏄剧ず鏃堕棿璁剧疆
    public void initTimePopupWindow(Context context, int position) {
    	popPosition = position;
        onShareHour = getText(position, 2, 4);
        onShareMin = getText(position, 5, 7);
        offShareHour = getText(position, 7, 9);
        offShareMin = getText(position, 10, 12);
		int xOffset = 0;
        int yOffset = 0;
        if (ScreenUtils.getScreenWidth(mContext) > ScreenUtils.getScreenHeight(mContext)) {
 		 xOffset = -60;	
 		 yOffset = 198 + popPosition * 74 ;
 		} else {
 		 xOffset = 50;
 		 yOffset = 178 + popPosition * 61 ;
		}
        LayoutInflater inflater = LayoutInflater.from(context);
        layout = inflater.inflate(R.layout.power_set_pop_layout, null);
        initAllTimeText();
        setTimePop = new PopupWindow(layout, android.view.WindowManager.LayoutParams.WRAP_CONTENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT);
        setTimePop.setFocusable(true);
        setTimePop.setBackgroundDrawable(new BitmapDrawable());
        setTimePop.showAtLocation(layout, Gravity.TOP, xOffset, yOffset);
        setTimePop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                // TODO Auto-generated method stub
                saveSharePre(popPosition);
                mHandler.sendEmptyMessage(RECREATE);
            }
        });
    }


    private void initAllTimeText() {
        onHourPopEdit = (EditText) layout.findViewById(R.id.on_hour_edit);
        onHourPopEdit.setKeyListener(new MyEditKeyListener());
        onHourPopEdit.setOnFocusChangeListener(new TimeSetFocusChangeListener());
        onHourPopEdit.setText(getText(popPosition, 2, 4));

        onMinPopEdit = (EditText) layout.findViewById(R.id.on_min_edit);
        onMinPopEdit.setKeyListener(new MyEditKeyListener());
        onMinPopEdit.setOnFocusChangeListener(new TimeSetFocusChangeListener());
        onMinPopEdit.setText(getText(popPosition, 5, 7));

        offHourPopEdit = (EditText) layout.findViewById(R.id.off_hour_edit);
        offHourPopEdit.setKeyListener(new MyEditKeyListener());
        offHourPopEdit.setOnFocusChangeListener(new TimeSetFocusChangeListener());
        offHourPopEdit.setText(getText(popPosition, 7, 9));

        offMinPopEdit = (EditText) layout.findViewById(R.id.off_min_edit);
        offMinPopEdit.setKeyListener(new MyEditKeyListener());
        offMinPopEdit.setOnFocusChangeListener(new TimeSetFocusChangeListener());
        offMinPopEdit.setText(getText(popPosition, 10, 12));
    }

    private class MyEditKeyListener implements KeyListener {

        @Override
        public int getInputType() {
            return 0;
        }

        @Override
        public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
            switch (view.getId()) {
                case R.id.on_hour_edit:
                    if (keyCode == 19 || keyCode == 20) {
                        int onHour = Integer.parseInt(onShareHour);
                        String curOnHour = String.format("%02d", ChangeHourOrMinUtil.getHour(keyCode, onHour));
                        text.clear();
                        text.append(curOnHour);
                        onShareHour = curOnHour;
                    }

                    if (keyCode == 21) {

                    }

                    if (keyCode == 22) {
                        onMinPopEdit.requestFocus();
                    }
                    break;

                case R.id.on_min_edit:
                    if (keyCode == 19 || keyCode == 20) {
                        int onMin = Integer.parseInt(onShareMin);
                        String curOnMin = String.format("%02d", ChangeHourOrMinUtil.getMin(keyCode, onMin));
                        text.clear();
                        text.append(curOnMin);
                        onShareMin = curOnMin;
                    }

                    if (keyCode == 21) {
                        onHourPopEdit.requestFocus();
                    }

                    if (keyCode == 22) {
                        offHourPopEdit.requestFocus();
                    }

                    break;
                case R.id.off_hour_edit:
                    if (keyCode == 19 || keyCode == 20) {
                        int offHour = Integer.parseInt(offShareHour);
                        String curOffHour = String.format("%02d", ChangeHourOrMinUtil.getHour(keyCode, offHour));
                        text.clear();
                        text.append(curOffHour);
                        offShareHour = curOffHour;
                    }

                    if (keyCode == 21) {
                        onMinPopEdit.requestFocus();
                    }

                    if (keyCode == 22) {
                        offMinPopEdit.requestFocus();
                    }

                    break;
                case R.id.off_min_edit:
                    if (keyCode == 19 || keyCode == 20) {
                        int offMin = Integer.parseInt(offShareMin);
                        String curOffMin = String.format("%02d", ChangeHourOrMinUtil.getMin(keyCode, offMin));
                        text.clear();
                        text.append(curOffMin);
                        offShareMin = curOffMin;
                    }


                    if (keyCode == 21) {
                        offHourPopEdit.requestFocus();
                    }

                    if (keyCode == 22) {
                        //offMinPopEdit.requestFocus();
                    }

                    break;
            }
            return true;
        }

        @Override
        public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
            return false;
        }

        @Override
        public boolean onKeyOther(View view, Editable text, KeyEvent event) {
            return false;
        }

        @Override
        public void clearMetaKeyState(View view, Editable content, int states) {

        }
    }

    public class TimeSetFocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            // TODO Auto-generated method stub
            if (hasFocus) {
                v.setBackgroundColor(Color.parseColor("#f71f75"));
            } else {
                v.setBackgroundColor(Color.WHITE);
            }
        }
    }

    private void saveSharePre(int position) {

        mShare.edit().putString("group" + position, 1 + getCheckedString(position) + onShareHour + ":" + onShareMin + offShareHour + ":" + offShareMin).commit();
    }

}
