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

public class CaptionListAdapter extends BaseAdapter {
    private Holder mHolder;
    SharedPreferences menuPref;
    private LayoutInflater inflater;
    private String[] captionMenuArray;
    private String[] captionSwitchArray;
    private String[] captionSizeArray;
    private String[] captionColorArray;
    private String[] captionBackColorArray;
    private String[] captionSpeedArray;

    private String[] captionLocationArray;
    public CaptionListAdapter(Context context) {
        menuPref = context.getSharedPreferences("caption_menu", 0);
        initArray(context);
        inflater = LayoutInflater.from(context);
    }


    // 加载数组资源文件。
    private void initArray(Context context) {

        captionMenuArray = context.getResources().getStringArray(R.array.caption_set_array);

        captionSwitchArray = context.getResources().getStringArray(R.array.caption_switch);
        captionSizeArray = context.getResources().getStringArray(R.array.caption_size);
        captionColorArray = context.getResources().getStringArray(R.array.caption_color);
        captionBackColorArray = context.getResources().getStringArray(R.array.caption_background_color);
        captionSpeedArray = context.getResources().getStringArray(R.array.rolling_speed);
        captionLocationArray = context.getResources().getStringArray(R.array.caption_location);
    }

    @Override
    public int getCount() {
        return captionMenuArray.length;
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
        holder.menuText.setText(captionMenuArray[position]);
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
                menuItemName = captionSwitchArray[menuPref.getInt("caption_switch_index", 0)];
                break;
            case 1:
                menuItemName = captionSizeArray[menuPref.getInt("caption_size_index", 1)];
                break;
            case 2:
                menuItemName = captionColorArray[menuPref.getInt("caption_color_index", 2)];
                break;
            case 3:
                menuItemName = captionBackColorArray[menuPref.getInt("caption_background_color_index", 2)];
                break;
            case 4:
                menuItemName = captionSpeedArray[menuPref.getInt("caption_speed_index", 1)];
                break;
            case 5:
                menuItemName = captionLocationArray[menuPref.getInt("caption_location_index", 0)];
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
                    int captionSwitchIndex = changeIndex(index, witch, captionSwitchArray);
                    menuPref.edit().putInt("caption_switch_index", captionSwitchIndex).commit();
                    holder.itemText.setText(captionSwitchArray[captionSwitchIndex]);
                    break;
                case 1:
                    int captionSizeIndex = changeIndex(index, witch, captionSizeArray);
                    menuPref.edit().putInt("caption_size_index", captionSizeIndex).commit();
                    holder.itemText.setText(captionSizeArray[captionSizeIndex]);
                    break;
                case 2:
                    int captionColorIndex = changeIndex(index, witch, captionColorArray);
                    menuPref.edit().putInt("caption_color_index", captionColorIndex).commit();
                    holder.itemText.setText(captionColorArray[captionColorIndex]);
                    break;
                case 3:
                    int captionBackColorIndex = changeIndex(index, witch, captionBackColorArray);
                    menuPref.edit().putInt("caption_background_color_index", captionBackColorIndex).commit();
                    holder.itemText.setText(captionBackColorArray[captionBackColorIndex]);
                    break;
                case 4:
                    int captionSpeedIndex = changeIndex(index, witch, captionSpeedArray);
                    menuPref.edit().putInt("caption_speed_index", captionSpeedIndex).commit();
                    holder.itemText.setText(captionSpeedArray[captionSpeedIndex]);
                    break;

                case 5:
                    int captionLocationIndex = changeIndex(index, witch, captionLocationArray);
                    menuPref.edit().putInt("caption_location_index", captionLocationIndex).commit();
                    holder.itemText.setText(captionLocationArray[captionLocationIndex]);
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
                int captionSwitchIndex = menuPref.getInt("caption_switch_index", 0);
                index = captionSwitchIndex;
                break;
            case 1:
                int captionSizeIndex = menuPref.getInt("caption_size_index", 0);
                index = captionSizeIndex;
                break;
            case 2:
                int captionColorIndex = menuPref.getInt("caption_color_index", 0);
                index = captionColorIndex;
                break;
            case 3:
                int captionBackColorIndex = menuPref.getInt("caption_background_color_index", 0);
                index = captionBackColorIndex;
                break;
            case 4:
                int captionSpeedIndex = menuPref.getInt("caption_speed_index", 0);
                index = captionSpeedIndex;
                break;
            case 5:
                int captionLocationIndex = menuPref.getInt("caption_location_index", 0);
                index = captionLocationIndex;
                break;
            default:
                break;
        }

        return index;
    }

}
