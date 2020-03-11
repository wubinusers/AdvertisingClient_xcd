package com.worldchip.advertising.client.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.worldchip.advertising.client.activity.IdleActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.storage.StorageManager;
import android.util.Log;
import android.util.Xml;

public class ReadXmlSettings {

	private Context context;

	public ReadXmlSettings(Context con) {
		this.context = con;
	}

	public SettingsXmlBean parseXml(InputStream is) throws XmlPullParserException {
		SettingsXmlBean settingsXmlBean = null;
		XmlPullParser xmlPullParser = Xml.newPullParser();
		xmlPullParser.setInput(is, "utf-8");
		int eventType = xmlPullParser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					settingsXmlBean = new SettingsXmlBean(context);
					break;
				case XmlPullParser.START_TAG:
					if (xmlPullParser.getName().equals("present_equip")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setPresentEquip(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("copy_mode")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setCopyMode(string2Int(xmlPullParser.getText()));
					} else if (xmlPullParser.getName().equals("play_mode")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setPlayMode(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("video_size")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setVideoSize(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("image_size")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setImageSize(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("image_trans")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setImageTrans(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("image_time")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setImageTime(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("time_switch")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setTimeSwitch(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("time_set")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//settingsXmlBean.setTimeSet(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("time_color")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setTimeColor(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("time_size")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setTimeSize(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("time_location")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setTimeLocation(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("ad_switch")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setAdSwitch(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("ad_mode")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setAdMode(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("ad_number")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setAdNumber(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("caption_switch")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setCaptionSwitch(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("caption_size")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setCaptionSize(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("caption_color")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setCaptionColor(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("caption_background_color")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setCaptionBackgroundColor(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("caption_speed")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setCaptionSpeed(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("caption_location")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setCaptionLocation(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("window_mode")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setWindowMode(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("background_music")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setBackgroundMusic(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("show_logo")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setShowLogo(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("language_set")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setLanguageSet(string2Int(xmlPullParser.getText()));

					} else if (xmlPullParser.getName().equals("id_set")) {
						try {
							eventType = xmlPullParser.next();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						settingsXmlBean.setIdSet(string2Int(xmlPullParser.getText()));
					}

					break;
				case XmlPullParser.END_TAG:
					// 解析到结束标签。然后要执行。
					if (xmlPullParser.getName().equals("settings")) {
						// xmlBeanList.add(xmlBean);
						// xmlBean = null;
						ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
						//  List<RunningTaskInfo> tasks = am.getRunningTasks(1);
						//   if (!tasks.isEmpty()) {
						//    ComponentName topActivity = tasks.get(0).topActivity;
						// if (!topActivity.getPackageName().equals(context.getPackageName())) {
						// }
						Intent intent = new Intent();
						intent.setClass(context, IdleActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);

						//   }
					}
					break;

				default:
					break;
			}

			try {
				eventType = xmlPullParser.next();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// return xmlBeanList;
		return settingsXmlBean;
	}

	// 将String转化为int
	private static int string2Int(String str) {
		return Integer.parseInt(str);
	}

	// 获取本地的settings.xml文件
	public void getItem() {
		try {
			String pathString = "";
			if (fileIsExists()) {
				pathString = "/mnt/usbhost1/settings.xml";
			}
			InputStream is = new FileInputStream(pathString);
			ReadXmlSettings readXmlSettings = new ReadXmlSettings(context);
			try {
				readXmlSettings.parseXml(is);
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 获取sd卡路径。
	@SuppressLint("NewApi")
	public String sdfile() {
		// Tool tool=new Tool(this.getContext)
		String sdfiles = "";
		StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
		try {
			Class<?>[] paramClasses = {};
			Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths", paramClasses);
			getVolumePathsMethod.setAccessible(true);
			Object[] params = {};
			Object invoke = getVolumePathsMethod.invoke(storageManager, params);
			for (int i = 0; i < ((String[]) invoke).length; i++) {
				if (i == 2) {
					sdfiles = ((String[]) invoke)[i].toString();
				}
			}
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return sdfiles;
	}

	// 判断是否存在文件。
	public boolean fileIsExists() {
		File f = new File(sdfile() + "/settings.xml");
		if (!f.exists()) {
			return false;
		}
		return true;
	}

}
