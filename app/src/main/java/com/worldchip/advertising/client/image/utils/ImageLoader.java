package com.worldchip.advertising.client.image.utils;

import java.io.File;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import com.worldchip.advertising.client.image.utils.ImageSizeUtil.ImageSize;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

/**
 * 图片加载类
 * 
 */
public class ImageLoader
{
	private static ImageLoader mInstance;

	/**
	 * 图片缓存的核心对象
	 */
	private LruCache<String, Bitmap> mLruCache;
	/**
	 * 线程池
	 */
	private ExecutorService mThreadPool;
	private static final int DEAFULT_THREAD_COUNT = 1;
	/**
	 * 队列的调度方式
	 */
	private Type mType = Type.LIFO;
	/**
	 * 任务队列
	 */
	private LinkedList<Runnable> mTaskQueue;
	/**
	 * 后台轮询线程
	 */
	private Thread mPoolThread;
	private Handler mPoolThreadHandler;
	/**
	 * UI线程中的Handler
	 */
	private Handler mUIHandler;
	private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
	private Semaphore mSemaphoreThreadPool;

	private boolean isDiskCacheEnable = true;

	private static final String TAG = "ImageLoader";

	//private static final String ActivityManager = null;

	private boolean isAddCach = true;
	/**
	 * FIFO 先入先出队列
	 * LIFO 后入先出队列
	 *
	 */
	public enum Type
	{
		FIFO, LIFO;
	}

	public ImageLoader(Context context, int threadCount, Type type, boolean addCache)
	{
		init(context, threadCount, type, addCache);
	}

	/**
	 * 初始化
	 * 
	 * @param threadCount
	 * @param type
	 */
	private void init(Context context, int threadCount, Type type, boolean addCache)
	{
		this.isAddCach = addCache;
		initBackThread();

		// 获取我们应用的最大可用内存
		//int maxMemory = (int) Runtime.getRuntime().maxMemory();
		//int cacheMemory = maxMemory / 8;
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		int memoryClass = am.getMemoryClass();
		final int cacheSize = 1024 * 1024 * memoryClass / 8;
		if (addCache) {
			mLruCache = new LruCache<String, Bitmap>(cacheSize) {
				@Override
				protected int sizeOf(String key, Bitmap bitmap) {
					return bitmap.getRowBytes() * bitmap.getHeight();
				}
			};
		}

		// 创建线程池
		mThreadPool = Executors.newFixedThreadPool(threadCount);
		mTaskQueue = new LinkedList<Runnable>();
		mType = type;
		mSemaphoreThreadPool = new Semaphore(threadCount);
		
		if (mUIHandler == null)
		{
			mUIHandler = new Handler()
			{
				public void handleMessage(Message msg)
				{
					// 获取得到图片，为imageview回调设置图片
					ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
					Bitmap bm = holder.bitmap;
					ImageView imageview = holder.imageView;
					String path = holder.path;
					// 将path与getTag存储路径进行比较
					try {
						if (imageview.getTag().toString().equals(path))
						{
							imageview.setImageBitmap(bm);
							if (holder.callback != null) {
								holder.callback.onLoadCompleted(bm);
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				};
			};
		}
	}

	/**
	 * 初始化后台轮询线程
	 */
	private void initBackThread()
	{
		// 后台轮询线程
		mPoolThread = new Thread()
		{
			@Override
			public void run()
			{
				Looper.prepare();
				mPoolThreadHandler = new Handler()
				{
					@Override
					public void handleMessage(Message msg)
					{
						
						try
						{
							// 线程池去取出一个任务进行执行
							mThreadPool.execute(getTask());
							mSemaphoreThreadPool.acquire();
						} catch (InterruptedException e)
						{
							e.printStackTrace();
						}catch(Exception err){
							err.printStackTrace();
						}
					}
				};
				// 释放一个信号量
				mSemaphorePoolThreadHandler.release();
				Looper.loop();
			};
		};

		mPoolThread.start();
	}

	public static ImageLoader getInstance(Context context)
	{
		if (mInstance == null)
		{
			synchronized (ImageLoader.class)
			{
				if (mInstance == null)
				{
					mInstance = new ImageLoader(context, DEAFULT_THREAD_COUNT, 
							Type.LIFO,true);
				}
			}
		}
		return mInstance;
	}

	public static ImageLoader getInstance(Context context, int threadCount,
			boolean addCache)
	{
		if (mInstance == null)
		{
			synchronized (ImageLoader.class)
			{
				if (mInstance == null)
				{
					mInstance = new ImageLoader(context, threadCount, Type.LIFO,
							addCache);
				}
			}
		}
		return mInstance;
	}

	/**
	 * 根据path为imageview设置图片
	 * 
	 * @param path
	 * @param imageView
	 */
	
	//此处可以设置播放制定路径的图片。
	public void loadImage(final String path, final ImageView imageView,
			final boolean isFromNet, final ImageLoaderCallback callback)
	{   //设定标签有什么作用？
		imageView.setTag(path);
		// 根据path在缓存中获取bitmap
		Bitmap bm = getBitmapFromLruCache(path);
		if (bm != null)
		{
			refreashBitmap(path, imageView, bm, callback);
		} else
		{
			addTask(buildTask(path, imageView, isFromNet, callback));
		}
	}

	
	/**
	 * 根据传入的参数，新建一个任务
	 * 
	 * @param path
	 * @param imageView
	 * @param isFromNet
	 * @return
	 */
	private Runnable buildTask(final String path, final ImageView imageView,
			final boolean isFromNet, final ImageLoaderCallback callback)
	{
		return new Runnable()
		{
			@Override
			public void run()
			{
				Bitmap bm = null;
				if (isFromNet)
				{
					File file = getDiskCacheDir(imageView.getContext(),
							md5(path));
					if (file.exists())// 如果在缓存文件中发现
					{
						Log.e(TAG, "find image :" + path + " in disk cache .");
						bm = loadImageFromLocal(file.getAbsolutePath(),
								imageView);
					} else
					{
						if (isDiskCacheEnable)// 检测是否开启硬盘缓存
						{
							boolean downloadState = DownloadImgUtils
									.downloadImgByUrl(path, file);
							if (downloadState)// 如果下载成功
							{
								Log.e(TAG,
										"download image :" + path
												+ " to disk cache . path is "
												+ file.getAbsolutePath());
								bm = loadImageFromLocal(file.getAbsolutePath(),
										imageView);
							}
						} else
						// 直接从网络加载
						{
							Log.e(TAG, "load image :" + path + " to memory.");
							bm = DownloadImgUtils.downloadImgByUrl(path,
									imageView);
						}
					}
				} else
				{
					bm = loadImageFromLocal(path, imageView);
				}
				// 3、把图片加入到缓存
				if (isAddCach) {
					addBitmapToLruCache(path, bm);
				}
				refreashBitmap(path, imageView, bm, callback);
				mSemaphoreThreadPool.release();
			}

			
		};
	}
	
	private Bitmap loadImageFromLocal(final String path,
			final ImageView imageView)
	{
		Bitmap bm = null;
		// 加载图片
		// 图片的压缩
		// 1、获得图片需要显示的大小
		ImageSize imageSize = ImageSizeUtil.getImageViewSize(imageView);
		// 2、压缩图片
		bm = decodeSampledBitmapFromPath(path, imageSize.width,
					imageSize.height);
		return bm;
	}

	
	/**
	 * 从任务队列取出一个方法
	 * 
	 * @return
	 */
	private Runnable getTask()
	{
		if (mType == Type.FIFO)
		{
			return mTaskQueue.removeFirst();//移除并返回此列表的第一个元素。
		} else if (mType == Type.LIFO)
		{
			return mTaskQueue.removeLast();
		}
		return null;
	}

	/**
	 * 利用签名辅助类，将字符串字节数组
	 * 
	 * @param str
	 * @return
	 */
	public String md5(String str)
	{
		byte[] digest = null;
		try
		{
			MessageDigest md = MessageDigest.getInstance("md5");
			digest = md.digest(str.getBytes());
			return bytes2hex02(digest);

		} catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 方式二
	 * 
	 * @param bytes
	 * @return
	 */
	public String bytes2hex02(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder();
		String tmp = null;
		for (byte b : bytes)
		{
			// 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
			tmp = Integer.toHexString(0xFF & b);
			if (tmp.length() == 1)// 每个字节8为，转为16进制标志，2个16进制位
			{
				tmp = "0" + tmp;
			}
			sb.append(tmp);
		}

		return sb.toString();

	}

	private void refreashBitmap(final String path, final ImageView imageView,
			Bitmap bm,ImageLoaderCallback callback)
	{
		Message message = Message.obtain();
		ImgBeanHolder holder = new ImgBeanHolder();
		holder.bitmap = bm;
		holder.path = path;
		holder.imageView = imageView;
		holder.callback = callback;
		message.obj = holder;
		mUIHandler.sendMessage(message);
	}

	/**
	 * 将图片加入LruCache
	 * 
	 * @param path
	 * @param bm
	 */
	protected void addBitmapToLruCache(String path, Bitmap bm)
	{
		if (getBitmapFromLruCache(path) == null)
		{
			if (bm != null)
				
				mLruCache.put(path, bm);
		}
	}

	
	/**
	 * 根据图片需要显示的宽和高对图片进行压缩
	 * 
	 * @param resId
	 * @param width
	 * @param height
	 * @return
	 */
	protected Bitmap decodeSampledBitmapFromRes(Resources resources, int resId, int width,
			int height)
	{
		// 获得图片的宽和高，并不把图片加载到内存中
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(resources, resId, options);

		options.inSampleSize = ImageSizeUtil.caculateInSampleSize(options,
				width, height);

		// 使用获得到的InSampleSize再次解析图片
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeResource(resources, resId, options);
		return bitmap;
	}
	
	/**
	 * 根据图片需要显示的宽和高对图片进行压缩
	 * 
	 * @param path
	 * @param width
	 * @param height
	 * @return
	 */
	protected Bitmap decodeSampledBitmapFromPath(String path, int width,
			int height)
	{
		// 获得图片的宽和高，并不把图片加载到内存中
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		options.inSampleSize = ImageSizeUtil.caculateInSampleSize(options,
				width, height);
		// 使用获得到的InSampleSize再次解析图片
		//使用RGB_565色彩模式压缩图片，红色5位，绿色6位，蓝色5位，会造成颜色略微失真，特别是颜色过渡区域
		//优势是占用的内存相比ARGB_8888要小很多。可以根据具体机器的内存大小来设置。
		//options.inPreferredConfig = Bitmap.Config.RGB_565;
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(path, options);
		return bitmap;
	}


	private synchronized void addTask(Runnable runnable)
	{
		mTaskQueue.add(runnable);
		// if(mPoolThreadHandler==null)wait();
		try
		{
			if (mPoolThreadHandler == null)
				mSemaphorePoolThreadHandler.acquire();
		} catch (InterruptedException e)
		{
		}
		mPoolThreadHandler.sendEmptyMessage(0x110);
	}

	/**
	 * 获得缓存图片的地址
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	public File getDiskCacheDir(Context context, String uniqueName)
	{
		String cachePath;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()))
		{
			cachePath = context.getExternalCacheDir().getPath();
		} else
		{
			cachePath = context.getCacheDir().getPath();
		}
		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 根据path在缓存中获取bitmap
	 * 
	 * @param key
	 * @return
	 */
	private Bitmap getBitmapFromLruCache(String key)
	{
		if (mLruCache != null) {
			return mLruCache.get(key);
		}
		return null;
	}

	private class ImgBeanHolder
	{
		Bitmap bitmap;
		ImageView imageView;
		String path;
		ImageLoaderCallback callback;
	}
	
}
