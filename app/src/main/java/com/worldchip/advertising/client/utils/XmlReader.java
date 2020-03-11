package com.worldchip.advertising.client.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import com.worldchip.advertising.client.entity.Caption;
import com.worldchip.advertising.client.entity.Clip;
import com.worldchip.advertising.client.entity.Date;
import com.worldchip.advertising.client.entity.Friday;
import com.worldchip.advertising.client.entity.Group;
import com.worldchip.advertising.client.entity.Media;
import com.worldchip.advertising.client.entity.MenuInfo;
import com.worldchip.advertising.client.entity.MenuItemInfo;
import com.worldchip.advertising.client.entity.Monday;
import com.worldchip.advertising.client.entity.Playlist;
import com.worldchip.advertising.client.entity.PowerDayTime;
import com.worldchip.advertising.client.entity.PowerWeekTime;
import com.worldchip.advertising.client.entity.Program;
import com.worldchip.advertising.client.entity.Saturday;
import com.worldchip.advertising.client.entity.Setting;
import com.worldchip.advertising.client.entity.Sunday;
import com.worldchip.advertising.client.entity.System;
import com.worldchip.advertising.client.entity.Thursday;
import com.worldchip.advertising.client.entity.Time;
import com.worldchip.advertising.client.entity.Tuesday;
import com.worldchip.advertising.client.entity.Wednesday;

public class XmlReader {
	private static final String TAG = "--XmlReader--";
	private static final boolean DEBUG = false;
	private static final String USB_ROOT_PATH = "/mnt/usb_storage/USB_DISK2/udisk0/";
	private static final String INNER_ROOT_PATH = "/mnt/sdcard/";

	// 读取默认盘符下的滚动字幕
	public static StringBuffer getTextString() {
		if (DEBUG)
			Log.e(TAG, "getTextString..path=" + Utils.CURRENT_ROOT_PATH + "caption" + File.separator);
		File file = null;
		String mRootPath = "";
		File f = new File(Utils.CURRENT_ROOT_PATH);
		if (f.exists()) {// 如果包含"Playlist"文件夹。
			mRootPath = Utils.CURRENT_ROOT_PATH;
			file = new File(Utils.CURRENT_ROOT_PATH + "caption" + File.separator);
		} else {
			if (Utils.getCurrentRootPath().contains("external")) {
				mRootPath = Utils.SD_ROOT_PATH;
				file = new File(Utils.SD_ROOT_PATH);
			} else if (Utils.getCurrentRootPath().contains("usbhost")) {
				mRootPath = USB_ROOT_PATH;
				file = new File(USB_ROOT_PATH);
			} else {
				mRootPath = INNER_ROOT_PATH;
				file = new File(INNER_ROOT_PATH);
			}
		}
		// 字幕路径的列表
		List<String> pathList = new ArrayList<String>();
		try {
			Common.getCaptionFileList(pathList, file);
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (pathList == null || pathList.size() < 1) {
			return null;
		}
		return getTextString(mRootPath, pathList.get(0));
	}

	public static StringBuffer getTextString(final String rootPath, String txtFileName) {
		InputStreamReader inputStreamReader = null;
		// 根据传入的rootPath来判断，如果包含"Playlist"文件夹，那么久去加载Playlist文件夹下面的字幕文件。
		if (rootPath.contains("Playlist/")) {
			try {
				if (!txtFileName.contains(Utils.CAPTION_PATH)) {
					txtFileName = Utils.CAPTION_PATH + txtFileName;
				}
				InputStream inputStream = getInputStream(rootPath, txtFileName);
				inputStreamReader = new InputStreamReader(inputStream, "UTF-8");

			} catch (UnsupportedEncodingException e1) {
				Log.e(TAG, "UnsupportedEncodingException..err: " + e1.getMessage());
			} catch (Exception err) {
				Log.e(TAG, "Exception..err: " + err.getMessage());
			}
		} else {// 如果传入的rootPath里面没有"Playlist"则就从根目录下面来读取名称为“SCROLL.TXT"的滚动字幕文件。
			try {

				InputStream inputStream = getInputStreamFromRootPath(rootPath, txtFileName);
				inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			} catch (UnsupportedEncodingException e1) {
				Log.e(TAG, "UnsupportedEncodingException..err: " + e1.getMessage());
			} catch (Exception err) {
				Log.e(TAG, "Exception..err: " + err.getMessage());
			}
		}
		BufferedReader reader = new BufferedReader(inputStreamReader);
		StringBuffer sb = new StringBuffer("");
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb;
	}

	public static Setting pullParseXmlSetting(Context context) {
		try {
			InputStream inputStream = context.getAssets().open("Setting.xml");
			if (inputStream == null)
				return null;

			return pullParseXmlSetting(inputStream);
		} catch (Exception err) {
			Log.e(TAG, "pullParseXmlSetting..err=" + err.getMessage());
			err.printStackTrace();
		}
		return null;
	}

	public static Setting pullParseXmlSetting(final String rootPath) {
		try {
			InputStream inputStream = getInputStream(rootPath, Utils.SETTING_FILE);
			if (inputStream == null)
				return null;

			return pullParseXmlSetting(inputStream);

		} catch (Exception err) {
			Log.e(TAG, "pullParseXmlSetting..err=" + err.getMessage());
			err.printStackTrace();
		}
		return null;
	}

	// 分屏模板
	public static List<Playlist> pullParseXmlPlaylist(Context context, int windowModeIndex) {
		try {
			String portName = "";
			if (Utils.IS_PORT) {
				portName = "port_";
			}
			String fileName = "Playlist_" + portName + windowModeIndex + ".xml";
			InputStream inputStream = context.getAssets().open(fileName);
			if (inputStream == null)
				return null;

			List<Playlist> allPlayList = pullParseXmlPlaylist(inputStream);
			if (allPlayList == null || allPlayList.size() < 1)
				return allPlayList;

			return allPlayList;

		} catch (IOException err) {

			Log.e(TAG, "pullParseXmlPlaylist..err=" + err.getMessage());
			err.printStackTrace();
		}
		return null;
	}

	public static List<Playlist> pullParseXmlPlaylist(final String rootPath) {
		try {
			InputStream inputStream = null;
			if (DEBUG)
				Log.e(TAG, "pullParseXmlPlaylist..rootPath=" + rootPath + "; Utils.IS_PORT=" + Utils.IS_PORT);
			if (Utils.IS_PORT) {
				inputStream = getInputStream(rootPath, Utils.PLAYLIST_FILE_PORT);
			} else {
				inputStream = getInputStream(rootPath, Utils.PLAYLIST_FILE);
			}
			if (inputStream == null)
				return null;

			List<Playlist> allPlayList = pullParseXmlPlaylist(inputStream);

			Log.e(TAG, "pullParseXmlPlaylist..allPlayList=" + allPlayList.size());
			if (allPlayList == null || allPlayList.size() < 1) {
				return allPlayList;
			}
			return getTodayPlaylist(allPlayList);
		} catch (Exception err) {
			Log.e(TAG, "pullParseXmlPlaylist..err=" + err.getMessage());
			err.printStackTrace();
		}
		return null;
	}

	// 获取当天的playlist
	@SuppressLint("SimpleDateFormat")
	private static List<Playlist> getTodayPlaylist(List<Playlist> allPlayList) {
		List<Playlist> userPlayList = new ArrayList<Playlist>();

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String today = formatter.format(new java.util.Date());

		final Calendar c = Calendar.getInstance();
		int week = c.get(Calendar.DAY_OF_WEEK) - 1;
		String month = String.valueOf(c.get(Calendar.MONTH) + 1);

		if (week == 0) {
			week = 7;
		}
		Log.e(TAG, "get playlist..x.taday=" + today + "; week=" + week + "; month=" + month);
		// 找当天的playlist
		for (int i = 0; i < allPlayList.size(); i++) {
			Playlist playList = allPlayList.get(i);
			Log.e(TAG, "playlist..x.xxx + " + (playList.getMonth() != null && playList.getMonth().equals(month)));
			if (playList.getDay() != null && playList.getDay().equals("-1")
					|| playList.getDay() != null && playList.getDay().equals(today)) // day
			// //
			// model
			{
				userPlayList.add(playList);
			} else if (playList.getMonth() != null && playList.getMonth().contains(month)) {
				userPlayList.add(playList);
			} else if (playList.getWeek() != null && playList.getWeek().contains(week + "")) {
				userPlayList.add(playList);
			}
		}

		// 添加最后一个playlist
		/*
		 * Playlist entList = allPlayList.get(allPlayList.size() - 1); if
		 * (entList != null && entList.getStart().equals("-1") &&
		 * entList.getEnd().equals("-1")) {
		 * userPlayList.add(allPlayList.get(allPlayList.size() - 1)); }
		 */
		Log.e(TAG, "playlist...userPlaylist.size=" + userPlayList.size());
		return userPlayList;
	}

	/**
	 * 解析指定目录中的Playlist.XML文件
	 */
	public static List<Playlist> pullParseXmlPlaylist(InputStream inputStream) {
		List<Playlist> playlists = null;
		try {
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
			pullParserFactory.setNamespaceAware(true);
			XmlPullParser xmlPullParser = pullParserFactory.newPullParser();

			Playlist playlist = null;
			Program program = null;
			Media media = null;
			Clip clip = null;
			int id = 0;

			xmlPullParser.setInput(inputStream, "UTF-8");
			// 开始解析电脑上生成 的XML文件
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {

				String nodeName = xmlPullParser.getName();
				switch (eventType) {
					case XmlPullParser.START_DOCUMENT: {
						playlists = new ArrayList<Playlist>();
					}
					break;
					case XmlPullParser.START_TAG: {
						if (nodeName.equals("List")) {
							playlist = new Playlist();
							// 设置开始时间和结束时间
							// 天
							String day = xmlPullParser.getAttributeValue("", "day");
							Log.e("XmlPullParser", "XmlPullParser---day = " + day);

							if (day == null || day.equals("null") || day.equals("")) {
								playlist.setDay("");
							} else {
								playlist.setDay(day.trim());
							}
							// 月
							String month = xmlPullParser.getAttributeValue("", "month");
							Log.e(TAG, "get playlist...month=" + month);
							if (month == null || month.equals("null") || month.equals("") || month.equals("-1")) {
								playlist.setMonth("-1");
							} else {
								playlist.setMonth(month.trim());
							}
							// 周
							String week = xmlPullParser.getAttributeValue("", "week");
							if (week == null || week.equals("null") || week.equals("") || week.equals("-1")) {
								playlist.setWeek("-1");
							} else {
								playlist.setWeek(week.trim());
							}

							// 开始时间
							String startTime = xmlPullParser.getAttributeValue("", "start");
							if (startTime == null || startTime.equals("null") || startTime.equals("")
									|| startTime.equals("-1")) {
								playlist.setStart("-1");
							} else {
								playlist.setStart(startTime.trim());
							}

							// 结束时间
							String endTime = xmlPullParser.getAttributeValue("", "end");
							if (endTime == null || endTime.equals("null") || endTime.equals("") || endTime.equals("-1")) {
								playlist.setEnd("-1");
							} else {
								playlist.setEnd(endTime.trim());
							}
						} else if (nodeName.equals("Program")) {
							program = new Program();
							program.setId(id++);
							program.setType(xmlPullParser.getAttributeValue("", "type"));
							program.setTime(xmlPullParser.getAttributeValue("", "time"));
							program.setBackground(xmlPullParser.getAttributeValue("", "background"));
						} else if (nodeName.equals("Media")) {
							media = new Media();
							if (DEBUG)
								Log.e("Media----START_TAG",
										xmlPullParser.getAttributeValue("", "type") + "; " + "Media.X="
												+ xmlPullParser.getAttributeValue("", "x") + "; Y="
												+ xmlPullParser.getAttributeValue("", "y") + "; " + "w="
												+ xmlPullParser.getAttributeValue("", "w") + "; " + "H="
												+ xmlPullParser.getAttributeValue("", "h"));
							media.setType(xmlPullParser.getAttributeValue("", "type"));
							String x = xmlPullParser.getAttributeValue("", "x");
							if (x != null && !x.equals("null") && x != "" && !x.equals("")) {
								if (x.contains("/")) {
									String[] values = x.split("/");
									if (values != null && values.length > 1) {
										int x1 = Integer.parseInt(values[0]);
										int x2 = Integer.parseInt(values[1]);
										x1 = Utils.WIDTH_PIXELS * x1;
										if (DEBUG)
											Log.e(TAG, "setX: x1=" + x1 + "; x2=" + x2 + "; (int)x1/x2=" + (int) x1 / x2);
										if (x2 != 0) {
											media.setX((int) x1 / x2);
										}
									}
								} else if (x.equals("-1")) {
									media.setX(Utils.WIDTH_PIXELS);
								} else {
									media.setX(Integer.parseInt(x));
								}
							}
							String y = xmlPullParser.getAttributeValue("", "y");
							if (y != null && !y.equals("null") && y != "" && !y.equals("")) {
								if (y.contains("/")) {
									String[] values = y.split("/");
									if (values != null && values.length > 1) {
										int y1 = Integer.parseInt(values[0]);
										int y2 = Integer.parseInt(values[1]);
										y1 = Utils.HEIGHT_PIXELS * y1;
										if (DEBUG)
											Log.e(TAG, "SetY: y1=" + y1 + "; y2=" + y2 + "; (int)y1/y2=" + (int) y1 / y2);
										if (y2 != 0) {
											media.setY((int) y1 / y2);
										}
									}
								} else if (y.equals("-1")) {
									media.setY(Utils.HEIGHT_PIXELS);
								} else {
									media.setY(Integer.parseInt(y));
								}

							}
							String width = xmlPullParser.getAttributeValue("", "w");
							if (width != null && !width.equals("null") && width != "" && !width.equals("")) {
								if (width.contains("/")) {
									String[] values = width.split("/");
									if (values != null && values.length > 1) {
										int width1 = Integer.parseInt(values[0]);
										int width2 = Integer.parseInt(values[1]);
										width1 = Utils.WIDTH_PIXELS * width1;
										if (DEBUG)
											Log.e(TAG, "setWidth: width1=" + width1 + "; width2=" + width2 + ";"
													+ " (int)width1/width2=" + (int) width1 / width2);
										if (width2 != 0) {
											media.setWidth((int) width1 / width2);
										}
									}
								} else if (width.equals("-1")) {
									media.setWidth(Utils.WIDTH_PIXELS);
								} else {
									media.setWidth(Integer.parseInt(width));
								}

							}
							String height = xmlPullParser.getAttributeValue("", "h");
							if (height != null && !height.equals("null") && height != "" && !height.equals("")) {
								if (height.contains("/")) {
									String[] values = height.split("/");
									if (values != null && values.length > 1) {
										int height1 = Integer.parseInt(values[0]);
										int height2 = Integer.parseInt(values[1]);
										height1 = Utils.HEIGHT_PIXELS * height1;
										if (DEBUG)
											Log.e(TAG, "setY: height1=" + height1 + "; height2=" + height2 + ";"
													+ " (int)height1/height2=" + (int) height1 / height2);
										if (height2 != 0) {
											media.setHeight((int) height1 / height2);
										}
									}
								} else if (height.equals("-1")) {
									media.setHeight(Utils.HEIGHT_PIXELS);
								} else {
									media.setHeight(Integer.parseInt(height));
								}
							}
						} else if (nodeName.equals("Clip")) {
							clip = new Clip();
							String time = xmlPullParser.getAttributeValue("", "time");
							if (time != null && !time.equals("null") && time != "" && !time.equals("")) {
								clip.setTime(Integer.parseInt(time));
							}
							String nextText = xmlPullParser.nextText();
							clip.setPath(nextText);
							media.addClip(clip);
							clip = null;
						}
					}
					break;
					case XmlPullParser.END_TAG: {
						if (nodeName.equals("List")) {
							playlists.add(playlist);
						} else if (nodeName.equals("Program")) {
							playlist.addProgram(program);
							program = null;
						} else if (nodeName.equals("Media")) {
							program.addMedia(media);
							media = null;
						}
					}
					break;
					default:
						break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			Log.e(TAG, "XmlPullParserException err! " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "IOException err! " + e.getMessage());
			e.printStackTrace();
		}
		Log.i("pullParseXmlPlaylist", "size---" + playlists.size());
		return playlists;
	}

	/**
	 * 解析指定默认设置
	 */
	public static List<MenuInfo> pullDefaultSetting(Context context) {
		List<MenuInfo> menuList = null;

		try {
			XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
			pullParserFactory.setNamespaceAware(true);
			XmlPullParser xmlPullParser = pullParserFactory.newPullParser();
			InputStream inputStream = context.getResources().getAssets().open("default_setting.xml");
			if (inputStream == null)
				return menuList;

			MenuInfo menuInfo = null;
			MenuItemInfo menuItemInfo = null;

			xmlPullParser.setInput(inputStream, "UTF-8");
			// 开始
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String nodeName = xmlPullParser.getName();
				switch (eventType) {
					case XmlPullParser.START_DOCUMENT: {
						menuList = new ArrayList<MenuInfo>();
					}
					break;
					case XmlPullParser.START_TAG: {
						if (DEBUG)
							Log.e(TAG, "START_TAG: nodeName---" + nodeName);
						if (nodeName.equals("menu")) {
							menuInfo = new MenuInfo();
							menuInfo.Id = Integer.parseInt(xmlPullParser.getAttributeValue("", "id"));
							menuInfo.Name = xmlPullParser.getAttributeValue("", "name");
							if (DEBUG)
								Log.e(TAG, "menuInfo: id:" + menuInfo.Id + "; name=" + menuInfo.Name);
						} else if (nodeName.equals("sub_menu")) {
							menuItemInfo = new MenuItemInfo();
							menuItemInfo.Id = Integer.parseInt(xmlPullParser.getAttributeValue("", "id"));
							menuItemInfo.Key = xmlPullParser.getAttributeValue("", "key");
							menuItemInfo.Name = xmlPullParser.getAttributeValue("", "name");
							menuItemInfo.SelectedIndex = Integer.parseInt(xmlPullParser.getAttributeValue("", "value"));
							if (DEBUG)
								Log.e(TAG, "menuItemInfo: id:" + menuItemInfo.Id + "; name=" + menuItemInfo.Name);
							menuInfo.mSubMenuList.add(menuItemInfo);
						}
					}
					break;
					case XmlPullParser.END_TAG: {
						if (DEBUG)
							Log.e(TAG, "END_TAG: nodeName---" + nodeName);
						if (nodeName.equals("menu")) {
							menuList.add(menuInfo);
						}
					}
					break;
					default:
						break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			Log.e(TAG, "XmlPullParserException err! " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "IOException err! " + e.getMessage());
			e.printStackTrace();
		}
		if (DEBUG)
			Log.e("pullDefaultSetting", "size---" + menuList.size());

		return menuList;
	}

	/**
	 * 读取SD卡中指定文件夹中的XML文件
	 *
	 * @param fileName
	 * @return 返回XML文件的inputStream
	 */
	public static InputStream getInputStream(String rootPath, String fileName) {
		InputStream inputStream = null;
		try {
			// path路径根据实际项目修改，此次获取SDcard根目录
			String path = rootPath + fileName;
			if (DEBUG)
				Log.e(TAG, "getInputStream...path : " + path);
			File xmlFlie = new File(path);
			if (xmlFlie.exists()) {
				inputStream = new FileInputStream(xmlFlie);
			}
		} catch (IOException e) {
			if (DEBUG)
				Log.e(TAG, "getInputStream...IOException : " + e.getMessage());
			e.printStackTrace();
		}
		return inputStream;
	}

	/**
	 * 读取SD卡中指定文件夹中的XML文件
	 *
	 * @param fileName
	 * @return 返回XML文件的inputStream
	 */
	public static InputStream getInputStreamFromRootPath(String rootPath, String fileName) {
		InputStream inputStream = null;
		try {
			// path路径根据实际项目修改，此次获取SDcard根目录
			String path = rootPath + fileName;
			inputStream = new FileInputStream(path);

		} catch (IOException e) {
			if (DEBUG)
				Log.e(TAG, "getInputStream...IOException : " + e.getMessage());
			e.printStackTrace();
		}
		return inputStream;
	}

	/**
	 * 解析指定目录中的Setting.XML文件
	 */
	public static Setting pullParseXmlSetting(InputStream inputStream) {

		Setting setting = null;

		try {
			if (inputStream == null)
				return setting;

			XmlPullParserFactory pullParserFactory = XmlPullParserFactory.newInstance();
			pullParserFactory.setNamespaceAware(true);
			XmlPullParser xmlPullParser = pullParserFactory.newPullParser();

			System system = null;
			Date date = null;
			Time time = null;
			Caption caption = null;
			PowerDayTime powerDayTime = null;
			PowerWeekTime powerWeekTime = null;

			ArrayList<Group> groups = null;
			Group group = null;

			Monday mon = null;
			Tuesday tues = null;
			Wednesday wed = null;
			Thursday thur = null;
			Friday fri = null;
			Saturday sat = null;
			Sunday sun = null;

			xmlPullParser.setInput(inputStream, "UTF-8");
			// 开始
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String nodeName = xmlPullParser.getName();
				switch (eventType) {
					case XmlPullParser.START_DOCUMENT: {
						setting = new Setting();
					}
					break;
					case XmlPullParser.START_TAG: {
						if (DEBUG)
							Log.i("nodeName----START_TAG", "nodeName---" + nodeName);
						if (nodeName.equals("System")) {
							system = new System();
						} else if (nodeName.equals("Date")) {
							date = new Date();
							date.setYear(xmlPullParser.getAttributeValue("", "year"));
							date.setMonth(xmlPullParser.getAttributeValue("", "month"));
							date.setDay(xmlPullParser.getAttributeValue("", "day"));
						} else if (nodeName.equals("Time")) {
							time = new Time();
							time.setHour(xmlPullParser.getAttributeValue("", "hour"));
							time.setMin(xmlPullParser.getAttributeValue("", "min"));
							time.setSec(xmlPullParser.getAttributeValue("", "sec"));
						} else if (nodeName.equals("Vol")) {
							String vol = xmlPullParser.nextText();
							if (vol != null && !vol.equals("null") && vol != "" && !vol.equals("")) {
								system.setVol(Integer.parseInt(vol));
							}
						} else if (nodeName.equals("Angle")) {
							String angle = xmlPullParser.nextText();
							if (angle != null && !angle.equals("null") && angle != "" && !angle.equals("")) {
								system.setAngle(Integer.parseInt(angle));
							}
						} else if (nodeName.equals("Caption")) {
							caption = new Caption();
							caption.setFgcolor(xmlPullParser.getAttributeValue("", "fgcolor"));
							caption.setBgcolor(xmlPullParser.getAttributeValue("", "bgcolor"));
							String size = xmlPullParser.getAttributeValue("", "size");
							if (size != null && !size.equals("null") && size != "" && !size.equals("")) {
								caption.setSize(Integer.parseInt(size));
							}
							caption.setSpeed(xmlPullParser.getAttributeValue("", "speed"));
						} else if (nodeName.equals("PowerDayTime")) {
							powerDayTime = new PowerDayTime();
							groups = new ArrayList<Group>();
							powerDayTime.setGroup(xmlPullParser.getAttributeValue("", "group"));
						} else if (nodeName.equals("Group")) {
							group = new Group();
							if (DEBUG)
								Log.e("Group", xmlPullParser.getAttributeValue("", "On") + "-----Group----"
										+ xmlPullParser.getAttributeValue("", "Off"));
							group.setOn(xmlPullParser.getAttributeValue("", "On"));
							group.setOff(xmlPullParser.getAttributeValue("", "Off"));
						} else if (nodeName.equals("PowerWeekTime")) {
							powerWeekTime = new PowerWeekTime();
						} else if (nodeName.equals("MON")) {
							mon = new Monday();
							groups = new ArrayList<Group>();
							mon.setGroup(xmlPullParser.getAttributeValue("", "group"));
						} else if (nodeName.equals("TUE")) {
							tues = new Tuesday();
							groups = new ArrayList<Group>();
							tues.setGroup(xmlPullParser.getAttributeValue("", "group"));
						} else if (nodeName.equals("WED")) {
							wed = new Wednesday();
							groups = new ArrayList<Group>();
							wed.setGroup(xmlPullParser.getAttributeValue("", "group"));
						} else if (nodeName.equals("TUS")) {
							thur = new Thursday();
							groups = new ArrayList<Group>();
							thur.setGroup(xmlPullParser.getAttributeValue("", "group"));
						} else if (nodeName.equals("FRI")) {
							fri = new Friday();
							groups = new ArrayList<Group>();
							fri.setGroup(xmlPullParser.getAttributeValue("", "group"));
						} else if (nodeName.equals("SAT")) {
							sat = new Saturday();
							groups = new ArrayList<Group>();
							sat.setGroup(xmlPullParser.getAttributeValue("", "group"));
						} else if (nodeName.equals("SUN")) {
							sun = new Sunday();
							groups = new ArrayList<Group>();
							sun.setGroup(xmlPullParser.getAttributeValue("", "group"));
						} else if (nodeName.equals("Weather")) {
							String weather = xmlPullParser.nextText();
							setting.setWeather(weather);
						}
					}
					break;
					case XmlPullParser.END_TAG: {
						if (DEBUG)
							Log.i("nodeName----END_TAG", "nodeName---" + nodeName);
						if (nodeName.equals("System")) {
							if (DEBUG)
								Log.i("System----END_TAG", "END_TAG");
							system.setDate(date);
							system.setTime(time);
							setting.setSystem(system);
							system = null;
						} else if (nodeName.equals("Caption")) {
							if (DEBUG)
								Log.i("Caption-----END_TAG", "END_TAG");
							setting.setCaption(caption);
							caption = null;
						} else if (nodeName.equals("PowerDayTime")) {
							if (DEBUG)
								Log.w("PowerDayTime----END_TAG", "END_TAG" + groups.size());
							powerDayTime.setGroups(groups);
							setting.setPowerDayTime(powerDayTime);
							groups = null;
						} else if (nodeName.equals("Group")) {
							groups.add(group);
							group = null;
						} else if (nodeName.equals("MON")) {
							mon.setGroups(groups);
							groups = null;
						} else if (nodeName.equals("TUE")) {
							if (DEBUG)
								Log.e("TUE", "------------TUE");
							tues.setGroups(groups);
							groups = null;
						} else if (nodeName.equals("WED")) {
							wed.setGroups(groups);
							groups = null;
						} else if (nodeName.equals("TUS")) {
							if (DEBUG)
								Log.e("TUS", "------------TUS");
							thur.setGroups(groups);
							groups = null;
						} else if (nodeName.equals("FRI")) {
							fri.setGroups(groups);
							groups = null;
						} else if (nodeName.equals("SAT")) {
							sat.setGroups(groups);
							groups = null;
						} else if (nodeName.equals("SUN")) {
							sun.setGroups(groups);
							groups = null;
						} else if (nodeName.equals("PowerWeekTime")) {
							powerWeekTime.setMon(mon);
							powerWeekTime.setTues(tues);
							powerWeekTime.setWed(wed);
							powerWeekTime.setThur(thur);
							powerWeekTime.setFri(fri);
							powerWeekTime.setSat(sat);
							powerWeekTime.setSun(sun);
							setting.setPowerWeekTime(powerWeekTime);
							powerWeekTime = null;
						}
					}
					break;
					default:
						break;
				}

				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) {
			Log.e(TAG, "XmlPullParserException err! " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e(TAG, "IOException err! " + e.getMessage());
			e.printStackTrace();
		}
		return setting;
	}
}