package com.worldchip.advertising.client.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class Loger {

	private LogerImp instance;

	// 日志名称
	private String logerName;

	// 调试时可以设置为true；发布时需要设置为false；
	protected static boolean isOpen = false;

	private static SharedPreferences mSharedPref;
	private static String Id = "000000";

	/**
	 * 开始输入日志信息<br\> （只作为程序日志开关，在个人设置中开启，其他应用中不得调用）
	 */
	public static void openPrint(Context context) {

		mSharedPref = context.getSharedPreferences("advertis_setup", 0);
		Id = mSharedPref.getString("id_value", "000000");
		File root = new File(Utils.CURRENT_ROOT_PATH);
		if (root != null && !root.exists()) {
			isOpen = false;
			return;
		}

		// isOpen = SetupUtils.isLogcat(context);
		if (isOpen) {// logcat是乱码，目前将其功能屏蔽掉。
			// LogerImp.instance.startRun();
		}
	}

	/**
	 * 关闭日志打印 <br\> （只作为程序日志开关，在个人设置中开启，其他应用中不得调用）
	 */
	public static void closePrint() {
		if (isOpen) {
			// LogerImp.instance.stopRun();
		}
	}

	private static Loger loger = new Loger("[Logcat]");

	/**
	 * 输出日志信息
	 *
	 * @param msg
	 *            String 日志
	 */
	public synchronized static void print(String msg) {
		if (isOpen) {
			// loger.output(msg);
		}
	}

	/**
	 * 输出日志信息及异常发生的详细信息
	 *
	 * @param msg
	 *            String 日志
	 * @param e
	 *            Exception
	 */
	public synchronized static void print(String msg, Exception e) {
		if (isOpen) {
			// loger.output(msg, e);
		}
	}

	/**
	 * 构造函数
	 *
	 * @param name
	 *            String
	 */
	public Loger(String name) {

		logerName = "<" + Id + "> " + name;
		instance = LogerImp.getInstance();
	}

	/**
	 * 输出日志信息
	 *
	 * @param msg
	 *            String 日志
	 */
	public synchronized void output(String msg) {
		if (isOpen) {
			Log.i(logerName, msg);
			// instance.submitMsg(logerName + " < Msg > " + msg);
		}
	}

	/**
	 * 输出日志信息及异常发生的详细信息
	 *
	 * @param msg
	 *            String 日志
	 * @param e
	 *            Exception
	 */
	public synchronized void output(String msg, Exception e) {
		if (isOpen) {
			Log.i(logerName, msg, e);
			StringBuffer buf = new StringBuffer(msg);
			buf.append(logerName).append(" : ").append(msg).append("\n");
			buf.append(e.getClass()).append(" : ");
			buf.append(e.getLocalizedMessage());
			buf.append("\n");
			StackTraceElement[] stack = e.getStackTrace();
			for (StackTraceElement trace : stack) {
				buf.append("\t at ").append(trace.toString()).append("\n");
			}
			instance.submitMsg(buf.toString());
		}
	}

	/**
	 * 打印当前的内存信息
	 */
	public void printCurrentMemory() {
		if (isOpen) {
			StringBuilder logs = new StringBuilder();
			long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
			long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
			long maxMemory = Runtime.getRuntime().maxMemory() / 1024;
			logs.append("\t[Memory_free]: ").append(freeMemory).append(" kb");
			logs.append("\t[Memory_total]: ").append(totalMemory).append(" kb");
			logs.append("\t[Memory_max]: ").append(maxMemory).append(" kb");
			Log.i(logerName, logs.toString());
			instance.submitMsg(logerName + " " + logs.toString());
		}
	}
}

/**
 * 日志输出的具体实现类
 *
 * @author Administrator
 *
 */
class LogerImp implements Runnable {

	private Loger log = new Loger("[Logcat]");

	static LogerImp instance = new LogerImp();

	// 日志存放的队列
	private List<String> printOutList = new ArrayList<String>();

	// 日志文件
	private FileOutputStream fos = null;

	// 日志输出流
	private PrintStream print = null;

	// 时间格式
	private DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	// 线程轮询标识
	private boolean runFlag = false;

	// 当前天，每天生成一个日志文件
	private int currDay = -1;

	private GcCheck gcRun = new GcCheck();

	class GcCheck implements Runnable {
		boolean flag = true;

		@Override
		public void run() {
			int count = 40;
			StringBuffer logs = new StringBuffer();
			while (flag) {
				if (count >= 50) {
					long freeMemory = Runtime.getRuntime().freeMemory() / 1024;
					long totalMemory = Runtime.getRuntime().totalMemory() / 1024;
					long maxMemory = Runtime.getRuntime().maxMemory() / 1024;
					logs.append("\t[Memory_free]:").append(freeMemory).append(" kb");
					logs.append("\t[Memory_total]:").append(totalMemory).append(" kb");
					logs.append("\t[Memory_max]:").append(maxMemory).append(" kb");
					synchronized (printOutList) {
						printOutList.add(logs.toString());
					}
					Log.i("Memory", logs.toString());
					logs.setLength(0);
					if (freeMemory < 400) {
						System.gc();
						count = 40;
						logs.append("<GC>");
					} else {
						count = 0;
					}
				}
				try {
					count++;
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	/**
	 * 得到单例对象 [url=home.php?mod=space&uid=309376]@return[/url] LogerImp
	 */
	public static LogerImp getInstance() {
		if (instance != null) {
			instance.printOutList.clear();
		}
		return instance;
	}

	/**
	 * 私有方法，单例
	 */
	private LogerImp() {
	}

	// 初始化文件输出流
	private boolean initPrint() {
		Calendar date = Calendar.getInstance();
		currDay = date.get(Calendar.DAY_OF_YEAR);
		DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd");
		String fileName = new String(dfm.format(date.getTime()) + ".txt");
		String path = null;
		try {
			if (null != print) {
				close();
			}

			path = Utils.CURRENT_ROOT_PATH + Utils.LOG_PATH;
			File dir = new File(path);
			if (!dir.exists()) {
				dir.mkdir();
			}
			fos = new FileOutputStream(path + fileName, true);
			print = new PrintStream(fos, true);
		} catch (Exception e) {
			log.output(
					"[Msg] Can not open file:" + path + " name " + fileName + " details: " + e.getLocalizedMessage());
			runFlag = false;
			stopRun();
			close();
			return false;
		}

		return true;
	}

	/**
	 * 线程开启
	 */
	public void startRun() {
		if (!initPrint()) {
			return;
		}
		if (!runFlag) {
			runFlag = true;
			new Thread(this).start();
		} else {
			log.output("[msg] < warning > log running !");
		}
	}

	/**
	 * 线程停止
	 */
	public void stopRun() {
		if (runFlag) {
			gcRun.flag = false;
			runFlag = false;
			Log.i("Thread", "queue list：" + printOutList.size());
			printToFile("[msg]  end !\n\n");
			close();
		}
	}

	private void close() {
		try {
			print.flush();
			print.close();
			print = null;
			try {
				fos.close();
				fos = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception err) {
			return;
		}
	}

	/**
	 * 向队列中增加日志数据
	 *
	 * @param msg
	 *            String 日志数据
	 */
	protected synchronized void submitMsg(String msg) {
		synchronized (printOutList) {
			printOutList.add(msg);
		}
	}

	public void run() {
		try {
			if (!initPrint()) {
				runFlag = false;
				gcRun.flag = false;
				return;
			}
			printToFile("[Log] < msg > start... ");
			while (runFlag) {
				runMethod();
			}
			runFlag = false;
		} catch (Exception e) {
			printToFile("[Log] <Warning> log err!: " + e.getLocalizedMessage());
			if (runFlag) {
				printToFile("[Log] thread error! " + e.getLocalizedMessage());
				new Thread(this).start();
			}
		}
	}

	// 线程需要重复执行的操作
	private void runMethod() throws Exception {
		String line = null;
		synchronized (printOutList) {
			if (!printOutList.isEmpty()) {
				line = printOutList.remove(0);
			}
		}
		if (null != line) {
			printToFile(line);
		} else {
			Thread.sleep(10);
		}
	}

	// 把数据持久到文件
	private void printToFile(String line) {
		Calendar date = Calendar.getInstance();
		int day = date.get(Calendar.DAY_OF_YEAR);
		if (day != currDay) {
			if (!initPrint()) {
				return;
			}
		}
		if (null == print) {
			return;
		}

		print.println(">>> " + format.format(date.getTime()) + " -- " + line);
		print.flush();
	}
}