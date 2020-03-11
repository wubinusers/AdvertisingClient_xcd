package com.worldchip.advertising.client.utils;

import android.content.Context;

public class SettingsXmlBean {

	private int presentEquip;
	private int copyMode;
	private int playMode;

	private int videoSize;
	private int imageSize;
	private int imageTrans;
	private int imageTime;

	private int timeSwitch;
	private int timeSet;
	private int timeColor;
	private int timeSize;
	private int timeLocation;

	private int adSwitch;
	private int adMode;
	private int adNumber;

	private int captionSwitch;
	private int captionSize;
	private int captionColor;
	private int captionBackgroundColor;
	private int captionSpeed;
	private int captionLocation;

	private int windowMode;
	private int backgroundMusic;

	private int showLogo;

	private int languageSet;
	private int idSet;

	private final static String BASIC_MENU = "basic_menu";
	private final static String TIME_SET = "time_set";
	private final static String CAPTION_MENU = "caption_menu";
	private final static String FUNCTION_MENU = "function_menu";
	private final static String AD_SET = "ad_set";

	//menu keys
	private final static String LANGUAGE_SET_KEY = "language_set_index";
	private final static String PRESENT_EQUIP_KEY = "present_equip_index";
	private final static String COPY_MODE_KEY = "copy_mode_index";
	private final static String PLAY_MODE_KEY = "play_mode_index";

	private final static String TIME_SWITCH_KEY = "time_switch_index";
	private final static String TIME_COLOR_KEY = "time_color_index";
	private final static String TIME_SIZE_KEY = "time_size_index";
	private final static String TIME_LOCATION_KEY = "time_location_index";

	private final static String VIDEO_SIZE_KEY = "video_size_index";
	private final static String IMAGE_SIZE_KEY = "image_size_index";

	private final static String IMAGE_TRANS_KEY = "image_trans_index";
	private final static String IMAGE_TIME_KEY = "image_time_value";

	private final static String CAPTION_SWITCH_KEY = "caption_switch_index";
	private final static String CAPTION_SIZE_KEY = "caption_size_index";
	private final static String CAPTION_COLOR_KEY = "caption_color_index";
	private final static String CAPTION_BACKGROUND_COLOR_KEY = "caption_background_color_index";
	private final static String CAPTION_SPEED_KEY = "caption_speed_index";
	private final static String CAPTION_LOCATION_KEY = "caption_location_index";

	private final static String WINDOW_MODE_KEY = "window_mode_index";
	private final static String BACKGROUND_MUSIC_KEY = "background_music_index";
	private final static String SHOW_LOGO_KEY = "show_logo_index";

	//  AD_SET
	private final static String AD_SWITCH_KEY = "ad_switch_index";
	private final static String AD_MODE_KEY = "ad_mode_index";
	private final static String AD_NUMBER_KEY = "ad_number";

	WriteSettingPrefer writeSettingPrefer;

	public SettingsXmlBean(Context context) {
		writeSettingPrefer = new WriteSettingPrefer(context);
	}

	public int getPresentEquip() {
		return presentEquip;
	}

	public void setPresentEquip(int presentEquip) {
		if (presentEquip == 0 || presentEquip == 1 || presentEquip == 2) {
			this.presentEquip = presentEquip;
		} else {
			this.presentEquip = 0;
		}
		writeSettingPrefer.setMenuPreference(BASIC_MENU, PRESENT_EQUIP_KEY, this.presentEquip);
	}

	public int getCopyMode() {
		return copyMode;
	}

	public void setCopyMode(int copyMode) {
		if (copyMode == 0 || copyMode == 1) {
			this.copyMode = copyMode;
		} else {
			this.copyMode = 0;
		}
		writeSettingPrefer.setMenuPreference(BASIC_MENU, COPY_MODE_KEY, this.copyMode);
	}

	public int getPlayMode() {
		return playMode;
	}

	public void setPlayMode(int playMode) {
		if (playMode == 0 || playMode == 1) {
			this.playMode = playMode;
		} else {
			this.playMode = 0;
		}
		writeSettingPrefer.setMenuPreference(BASIC_MENU, PLAY_MODE_KEY, this.playMode);
	}

	public int getVideoSize() {
		return videoSize;
	}

	public void setVideoSize(int videoSize) {
		if (videoSize == 0 || videoSize == 1) {
			this.videoSize = videoSize;
		} else {
			this.videoSize = 0;
		}
		writeSettingPrefer.setMenuPreference(BASIC_MENU, VIDEO_SIZE_KEY, this.videoSize);
	}

	public int getImageSize() {
		return imageSize;
	}

	public void setImageSize(int imageSize) {
		if (imageSize == 0 || imageSize == 1) {
			this.imageSize = imageSize;
		} else {
			this.imageSize = 0;
		}
		writeSettingPrefer.setMenuPreference(BASIC_MENU, IMAGE_SIZE_KEY, this.imageSize);
	}

	public int getImageTrans() {
		return imageTrans;
	}

	public void setImageTrans(int imageTrans) {
		if (imageTrans >= 0 && imageTrans <= 16) {
			this.imageTrans = imageTrans;
		} else {
			this.imageTrans = 0;
		}
		writeSettingPrefer.setMenuPreference(BASIC_MENU, IMAGE_TRANS_KEY, this.imageTrans);
	}

	//////////////////////////////
	public int getImageTime() {
		return imageTime;
	}

	public void setImageTime(int imageTime) {
		this.imageTime = imageTime;

		writeSettingPrefer.setMenuPreference(BASIC_MENU, IMAGE_TIME_KEY, this.imageTime);
	}
	/////////////////////////////

	public int getTimeSwitch() {
		return timeSwitch;
	}

	public void setTimeSwitch(int timeSwitch) {

		if (timeSwitch == 0 || timeSwitch == 1) {
			this.timeSwitch = timeSwitch;
		} else {
			this.timeSwitch = 0;
		}
		writeSettingPrefer.setMenuPreference(TIME_SET, TIME_SWITCH_KEY, this.timeSwitch);
	}

	///////////////////////////////////// 时间正则表达式。   不用这样来设置。
	public int getTimeSet() {
		return timeSet;
	}

	public void setTimeSet(int timeSet) {
		this.timeSet = timeSet;
	}
	////////////////////////////////////

	public int getTimeColor() {
		return timeColor;
	}

	public void setTimeColor(int timeColor) {

		if (timeColor >= 0 && timeColor <= 4) {
			this.timeColor = timeColor;
		} else {
			this.timeColor = 1;
		}
		writeSettingPrefer.setMenuPreference(TIME_SET, TIME_COLOR_KEY, this.timeColor);
	}

	public int getTimeSize() {
		return timeSize;
	}

	public void setTimeSize(int timeSize) {
		if (timeSize >= 0 && timeSize <= 2) {
			this.timeSize = timeSize;
		} else {
			this.timeSize = 1;
		}
		writeSettingPrefer.setMenuPreference(TIME_SET, TIME_SIZE_KEY, this.timeSize);
	}

	public int getTimeLocation() {
		return timeLocation;
	}

	public void setTimeLocation(int timeLocation) {
		if (timeLocation >= 0 && timeLocation <= 3) {
			this.timeLocation = timeLocation;
		} else {
			this.timeLocation = 0;
		}
		writeSettingPrefer.setMenuPreference(TIME_SET, TIME_LOCATION_KEY, this.timeLocation);
	}

	public int getAdSwitch() {
		return adSwitch;
	}

	public void setAdSwitch(int adSwitch) {
		if (adSwitch == 0 || adSwitch == 1) {
			this.adSwitch = adSwitch;
		} else {
			this.adSwitch = 0;
		}
		writeSettingPrefer.setMenuPreference(AD_SET, AD_SWITCH_KEY, this.adSwitch);
	}

	public int getAdMode() {
		return adMode;
	}

	public void setAdMode(int adMode) {
		if (adMode >= 0 && adMode <= 61) {
			this.adMode = adMode;
		} else {
			this.adMode = 3;
		}
		writeSettingPrefer.setMenuPreference(AD_SET, AD_MODE_KEY, this.adMode);
	}

	public int getAdNumber() {
		return adNumber;
	}

	public void setAdNumber(int adNumber) {
		if (adNumber >= 1 && adNumber <= 30) {
			this.adNumber = adNumber;
		} else {
			this.adNumber = 2;
		}
		writeSettingPrefer.setMenuPreference(AD_SET, AD_NUMBER_KEY, this.adNumber);
	}

	public int getCaptionSwitch() {
		return captionSwitch;
	}

	public void setCaptionSwitch(int captionSwitch) {
		if (captionSwitch == 0 || captionSwitch == 1) {
			this.captionSwitch = captionSwitch;
		} else {
			this.captionSwitch = 0;
		}
		writeSettingPrefer.setMenuPreference(CAPTION_MENU, CAPTION_SWITCH_KEY, this.captionSwitch);
	}

	public int getCaptionSize() {
		return captionSize;
	}

	public void setCaptionSize(int captionSize) {
		if (captionSize >= 0 && captionSize <= 2) {
			this.captionSize = captionSize;
		} else {
			this.captionSize = 1;
		}
		writeSettingPrefer.setMenuPreference(CAPTION_MENU, CAPTION_SIZE_KEY, this.captionSize);
	}

	public int getCaptionColor() {
		return captionColor;
	}

	public void setCaptionColor(int captionColor) {
		if (captionColor >= 0 && captionColor <= 4) {
			this.captionColor = captionColor;
		} else {
			this.captionColor = 2;
		}
		writeSettingPrefer.setMenuPreference(CAPTION_MENU, CAPTION_COLOR_KEY, this.captionColor);
	}

	public int getCaptionBackgroundColor() {
		return captionBackgroundColor;
	}

	public void setCaptionBackgroundColor(int captionBackgroundColor) {
		if (captionBackgroundColor >= 0 && captionBackgroundColor <= 5) {
			this.captionBackgroundColor = captionBackgroundColor;
		} else {
			this.captionBackgroundColor = 2;
		}
		writeSettingPrefer.setMenuPreference(CAPTION_MENU, CAPTION_BACKGROUND_COLOR_KEY, this.captionBackgroundColor);
	}

	public int getCaptionSpeed() {
		return captionSpeed;
	}

	public void setCaptionSpeed(int captionSpeed) {
		if (captionSpeed >= 0 && captionSpeed <= 2) {
			this.captionSpeed = captionSpeed;
		} else {
			this.captionSpeed = 1;
		}
		writeSettingPrefer.setMenuPreference(CAPTION_MENU, CAPTION_SPEED_KEY, this.captionSpeed);
	}

	public int getCaptionLocation() {
		return captionLocation;
	}

	public void setCaptionLocation(int captionLocation) {
		if (captionLocation == 0 || captionLocation == 1) {
			this.captionLocation = captionLocation;
		} else {
			this.captionLocation = 0;
		}
		writeSettingPrefer.setMenuPreference(CAPTION_MENU, CAPTION_LOCATION_KEY, this.captionLocation);
	}

	public int getWindowMode() {
		return windowMode;
	}

	public void setWindowMode(int windowMode) {
		if (windowMode >= 0 && windowMode <= 8) {
			this.windowMode = windowMode;
		} else {
			this.windowMode = 0;
		}
		writeSettingPrefer.setMenuPreference(FUNCTION_MENU, WINDOW_MODE_KEY, this.windowMode);
	}

	public int getBackgroundMusic() {
		return backgroundMusic;
	}

	public void setBackgroundMusic(int backgroundMusic) {
		if (backgroundMusic == 0 || backgroundMusic == 1) {
			this.backgroundMusic = backgroundMusic;
		} else {
			this.backgroundMusic = 0;
		}
		writeSettingPrefer.setMenuPreference(FUNCTION_MENU, BACKGROUND_MUSIC_KEY, this.backgroundMusic);
	}

	public int getShowLogo() {
		return showLogo;
	}

	public void setShowLogo(int showLogo) {
		if (showLogo >= 0 && showLogo <= 4) {
			this.showLogo = showLogo;
		} else {
			this.showLogo = 0;
		}
		writeSettingPrefer.setMenuPreference(FUNCTION_MENU, SHOW_LOGO_KEY, this.showLogo);
	}

	public int getLanguageSet() {
		return languageSet;
	}

	public void setLanguageSet(int languageSet) {
		if (languageSet == 0 || languageSet == 1) {
			this.languageSet = languageSet;
		} else {
			this.languageSet = 0;
		}
		writeSettingPrefer.setMenuPreference(BASIC_MENU, LANGUAGE_SET_KEY, this.languageSet);
	}

	///////////////////////////////////////////// 设置ID
	public int getIdSet() {
		return idSet;
	}

	public void setIdSet(int idSet) {
		this.idSet = idSet;
	}
	////////////////////////////////////////////
}
