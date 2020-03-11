package com.worldchip.advertising.client.adapter;

import com.worldchip.advertisingclient.R;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BasicListAdapter extends BaseAdapter {
    private Holder mHolder;
    private Context mContext;
    private LayoutInflater inflater;
    SharedPreferences menuPref;
    private String[] basicMenuArray;
    private String[] languageArray;
    private String[] equipArray;
    private String[] copyModeArray;
    private String[] playModeArray;
    private String[] videoModeArray;
    private String[] imgModeArray;
    private String[] imgTransArray;

    public final static int SET_TIME = 1123;
    public final static int RESET_MENU = 2123;
    public final static int CLICK_LANGUAGE = 0x334;

    private Handler mHandler;


    public BasicListAdapter(Context context, Handler handler) {
        this.mContext = context;
        menuPref = context.getSharedPreferences("basic_menu", 0);
        initArray(context);
        inflater = LayoutInflater.from(context);
        this.mHandler = handler;
    }

    // 加载数组资源文件。
    private void initArray(Context context) {
        basicMenuArray = context.getResources().getStringArray(R.array.basic_set_array);

        languageArray = context.getResources().getStringArray(R.array.language);
        equipArray = context.getResources().getStringArray(R.array.priority_mode);
        copyModeArray = context.getResources().getStringArray(R.array.copy_mode);
        playModeArray = context.getResources().getStringArray(R.array.repeat_mode);
        videoModeArray = context.getResources().getStringArray(R.array.video_mode);
        imgModeArray = context.getResources().getStringArray(R.array.image_mode);
        imgTransArray = context.getResources().getStringArray(R.array.image_transition);

    }

    @Override
    public int getCount() {
        return basicMenuArray.length;
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
        holder.menuText.setText(basicMenuArray[position]);
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

        switch (position) {
            case 0:
                menuItemName = languageArray[menuPref.getInt("language_set_index", 0)];
                break;
            case 1:
                menuItemName = equipArray[menuPref.getInt("present_equip_index", 0)];
                break;
            case 2:
                menuItemName = copyModeArray[menuPref.getInt("copy_mode_index", 0)];
                break;
            case 3:
                menuItemName = playModeArray[menuPref.getInt("play_mode_index", 0)];
                break;
            case 4:
                menuItemName = mContext.getString(R.string.time_set);
                break;
            case 5:
                menuItemName = mContext.getString(R.string.auto_on_off);
                break;
            case 6:
                menuItemName = videoModeArray[menuPref.getInt("video_size_index", 0)];
                break;
            case 7:
                menuItemName = imgModeArray[menuPref.getInt("image_size_index", 0)];
                break;
            case 8:
                menuItemName = imgTransArray[menuPref.getInt("image_trans_index", 0)];
                break;
            case 9:
                menuItemName = menuPref.getInt("image_time_value", 5) + "" + mContext.getResources().getString(R.string.image_time_sec);
                break;
            case 10:
                menuItemName = mContext.getString(R.string.reset_menu);
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
                    int languageIndex = changeIndex(index, witch, languageArray);
                    menuPref.edit().putInt("language_set_index", languageIndex).commit();
                    holder.itemText.setText(languageArray[languageIndex]);
                    mHandler.sendEmptyMessage(CLICK_LANGUAGE);
                    break;
                case 1:
                    int presentEquipIndex = changeIndex(index, witch, equipArray);
                    menuPref.edit().putInt("present_equip_index", presentEquipIndex).commit();
                    holder.itemText.setText(equipArray[presentEquipIndex]);
                    break;
                case 2:
                    int copyModeIndex = changeIndex(index, witch, copyModeArray);
                    menuPref.edit().putInt("copy_mode_index", copyModeIndex).commit();
                    holder.itemText.setText(copyModeArray[copyModeIndex]);
                    break;
                case 3:
                    int playModeIndex = changeIndex(index, witch, playModeArray);
                    menuPref.edit().putInt("play_mode_index", playModeIndex).commit();
                    holder.itemText.setText(playModeArray[playModeIndex]);
                    break;
                case 4:
                    mHandler.sendEmptyMessage(SET_TIME);
                    break;
                case 5:
                    // 定时开关机
                    startActivity("com.adtv", "com.adtv.setting.PowerOnOffSettings");
                    break;
                case 6:
                    int videoSizeIndex = changeIndex(index, witch, videoModeArray);
                    menuPref.edit().putInt("video_size_index", videoSizeIndex).commit();
                    holder.itemText.setText(videoModeArray[videoSizeIndex]);
                    break;
                case 7:
                    int imageSizeIndex = changeIndex(index, witch, imgModeArray);
                    menuPref.edit().putInt("image_size_index", imageSizeIndex).commit();
                    holder.itemText.setText(imgModeArray[imageSizeIndex]);
                    break;
                case 8:
                    int imageTransIndex = changeIndex(index, witch, imgTransArray);
                    menuPref.edit().putInt("image_trans_index", imageTransIndex).commit();
                    holder.itemText.setText(imgTransArray[imageTransIndex]);
                    break;
                case 9:
                    int imageTime = menuPref.getInt("image_time_value", 5);
                    if (witch == R.id.right_img) {
                        imageTime++;
                        if (imageTime > 99) {
                            imageTime = 1;
                        }
                    } else {
                        imageTime--;
                        if (imageTime < 1) {
                            imageTime = 99;
                        }
                    }
                    menuPref.edit().putInt("image_time_value", imageTime).commit();
                    holder.itemText.setText((menuPref.getInt("image_time_value", 5) + "") + mContext.getResources().getString(R.string.image_time_sec));
                    break;
                case 10:
                    mHandler.sendEmptyMessage(RESET_MENU);
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
                int languageIndex = menuPref.getInt("language_set_index", 0);
                index = languageIndex;
                break;
            case 1:
                int presentEquipIndex = menuPref.getInt("present_equip_index", 0);
                index = presentEquipIndex;
                break;
            case 2:
                int copyModeIndex = menuPref.getInt("copy_mode_index", 0);
                index = copyModeIndex;
                break;
            case 3:
                int playModeIndex = menuPref.getInt("play_mode_index", 0);
                index = playModeIndex;
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                int videoSizeIndex = menuPref.getInt("video_size_index", 0);
                index = videoSizeIndex;
                break;
            case 7:
                int imageSizeIndex = menuPref.getInt("image_size_index", 0);
                index = imageSizeIndex;
                break;
            case 8:
                int imageTransIndex = menuPref.getInt("image_trans_index", 0);
                index = imageTransIndex;
                break;
            case 9:
                break;
            case 10:
                break;
            default:
                break;
        }

        return index;
    }


    // 通过包名和类名来开启活动。
    private void startActivity(String packageName, String className) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, className));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        } catch (Exception err) {
            Toast.makeText(mContext, "The App not found!", Toast.LENGTH_LONG).show();
            err.printStackTrace();
        }
    }


}

