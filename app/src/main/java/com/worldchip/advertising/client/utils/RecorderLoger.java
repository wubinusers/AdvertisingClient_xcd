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
import android.util.Log;

public class RecorderLoger {

	private RecorderLogerImp instance;

	// 日志名称
	private String logerName;

	// 调试时可以设置为true；发布时需要设置为false；

	/**
	 * 开始输入日志信息<br\> （只作为程序日志开关，在个人设置中开启，其他应用中不得调用）
	 */
	public static void openPrint(Context context) {
		RecorderLogerImp.instance.startRun();
	}

	/**
	 * 关闭日志打印 <br\> （只作为程序日志开关，在个人设置中开启，其他应用中不得调用）
	 */
	public static void closePrint() {
		RecorderLogerImp.instance.stopRun();
	}

	private static RecorderLoger loger = new RecorderLoger("[播放记录日志]");

	/**
	 * 输出日志信息
	 * 
	 * @param msg
	 *            String 日志
	 */
	public synchronized static void print(String msg) {
		loger.output(msg);
	}

	/**
	 * 构造函数
	 * 
	 * @param name
	 *            String
	 */
	public RecorderLoger(String name) {
		logerName = name;
		instance = RecorderLogerImp.getInstance();
	}

	/**
	 * 输出日志信息
	 * 
	 * @param msg
	 *            String 日志
	 */
	public synchronized void output(String msg) {
		Log.i(logerName, msg);
		instance.submitMsg(logerName + " <类型-文件>  " + msg);
	}

}

/**
 * 日志输出的具体实现类
 * 
 * @author Administrator
 *
 */
class RecorderLogerImp implements Runnable {

	private RecorderLoger log = new RecorderLoger("[日志]");

	static RecorderLogerImp instance = new RecorderLogerImp();

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

	/**
	 * 得到单例对象 [url=home.php?mod=space&uid=309376]@return[/url] LogerImp
	 */
	public static RecorderLogerImp getInstance() {
		if (instance != null) {
			instance.printOutList.clear();
		}
		return instance;
	}

	/**
	 * 私有方法，单例
	 */
	private RecorderLogerImp() {
	}

	// 初始化文件输出流
	private boolean initPrint() {
		try {
			if (null != print) {
				close();
			}

			String path = Utils.CURRENT_ROOT_PATH + Utils.RECORDER_FILE;
			File file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(path, true);
			print = new PrintStream(fos, true);
		} catch (Exception e) {
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
			log.output("[日志] < 警告 > 日志已经在运行 !");
		}
	}

	/**
	 * 线程停止
	 */
	public void stopRun() {
		if (runFlag) {
			runFlag = false;
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
				return;
			}
			while (runFlag) {
				runMethod();
			}
			runFlag = false;
		} catch (Exception e) {
			if (runFlag) {
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
		if (print == null) {
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
