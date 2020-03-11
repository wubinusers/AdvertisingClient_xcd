package com.worldchip.advertising.client.application;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

public class ExitApplication extends Application {

	private static final String TAG = "--ExitApplication--";
	private List<Activity> activityList=new LinkedList<Activity>();
	private static ExitApplication instance;
	//私有化构造方法
	private ExitApplication(){}

	//单例模式中获取唯一的ExitApplication 实例
	//提供静态获取方法
	public static ExitApplication getInstance() {
		if (null == instance) {
			instance = new ExitApplication();
		}
		return instance;
	}

	//添加Activity 到容器中
	public void addActivity(Activity activity)
	{
		activityList.add(activity);
	}
	//将Activity移出容器。
	public void removeActivity(String title){
		Log.e(TAG, "removeActivity....");
		try{
			for(Activity activity:activityList){
				Log.e(TAG, "activity.getTitle()="+activity.getTitle());
				if(title.equals(activity.getTitle())){
					activity.finish();
				}
			}
		}catch(Exception err){
			Log.e(TAG, "removeActivity err....err:"+err.getMessage());
			err.printStackTrace();
		}
	}

	//遍历所有Activity 并finish
	public void exit(){
		Log.e(TAG, "exit....");
		for(Activity activity:activityList)
		{
			activity.finish();
		}
		System.exit(0);
	}

}
