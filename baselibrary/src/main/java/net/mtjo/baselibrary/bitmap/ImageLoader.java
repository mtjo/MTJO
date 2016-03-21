package com.wanyou.aframe.bitmap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

import com.wanyou.aframe.bitmap.utils.ImageOperUtils;
import com.wanyou.aframe.bitmap.utils.MyThumbnailUtils;
import com.wanyou.aframe.core.WYTaskExecutor;
import com.wanyou.wylibrary.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;

/**
 * 图片加载类，本地图片采用并行线程，网络图片采用串行阻塞方法
 * 对于有Activity有ListView等在onStop后，请调用stop()方法，不会有残余请求。
 * @author Moyaofen
 */
public class ImageLoader {
	private volatile static ImageLoader instance;
	
	private ImageMemoryCache memoryCache;  //图片内存缓存
    private ImageFileCache fileCache;	//图片sd卡缓存
	
	/** 请求图片 */
	private static final int MSG_REQUEST = 1;
	/** 图片加载完成 */
	private static final int MSG_REPLY = 2;
	/** 中止图片加载线程 */
	private static final int MSG_STOP = 3;
	/** 加载网络图片失败*/
	private static final int MSG_ERROR = 5;
	/** 加载网络图片过程*/
	private static final int MSG_PROCESS = 6;
	
	/** 图片加载队列，后进先出 */
	private Stack<ImageRef> mImageQueue = new Stack<ImageRef>();
	/** 图片请求队列，先进先出，用于存放已发送的请求。 */
	private Queue<ImageRef> mRequestQueue = new LinkedList<ImageRef>();
	/** 图片加载线程消息处理器 */
	private Handler mImageLoaderHandler;
	/** 图片加载线程是否就绪 */
	private boolean mImageLoaderIdle = true;

	/** Returns singleton class instance */
	public static ImageLoader getInstance(Context context) {
		// 如果不在ui线程中，则抛出异常
		if (Looper.myLooper() != Looper.getMainLooper()) {
			throw new RuntimeException("Cannot instantiate outside UI thread.");
		}
		
		if (instance == null) {
			synchronized (ImageLoader.class) {
				if (instance == null) {
					instance = new ImageLoader(context);
				}
			}
		}
		return instance;
	}

	private ImageLoader(Context context) {
		memoryCache=new ImageMemoryCache(context);  
        fileCache=new ImageFileCache();
	}
	
	/**
	 * 加载图片
	 */
	public void displayImage(ImageView imageView, String url,ImageCallBack callback){
		displayImage(imageView,url,callback,false,0);
	}
	
	/**
	 * 加载图片
	 * 先从缓存中读取，再从sd卡，如果都没有则加载，并写入到缓存和sd卡中
	 */
	public void displayImage(ImageView imageView, String url,
			ImageCallBack callback,boolean isround,int roundPx){
		displayImage(imageView,url,callback,isround,roundPx,0,0);
	}
	
	/**
	 * 显示图片固定大小图片的缩略图，一般用于显示列表的图片，可以大大减小内存使用
	 * 
	 * @param imageView 加载图片的控件
	 * @param url 加载地址
	 * @param resId 默认图片
	 * @param width 指定宽度
	 * @param height 指定高度
	 */
	public void displayImage(ImageView imageView, String url,ImageCallBack listener,
			boolean isround,int roundPx,int width, int height) {
		if (imageView == null) {
			return;
		}
		if (url == null || url.equals("")) {
			if(null != listener){
				listener.onFailed(url, imageView, null);
			}else{
				imageView.setImageResource(R.drawable.default_picture);
			}
			return;
		}

		// 添加url tag
		imageView.setTag(url);
		// 读取map缓存
		Bitmap tBitmap = memoryCache.getBitmapFromCache((width != 0 && height != 0)? 
				(url + width + height):url);
		Bitmap bitmap = null;
		if(isround)
			bitmap = ImageOperUtils.getRoundedCornerBitmap(tBitmap, roundPx);
		else 
			bitmap = tBitmap;
		if (bitmap != null) {
			if(null != listener)
				listener.onComplete(url, imageView, bitmap);
			else 
				setImageBitmap(imageView, bitmap, false);
			return;
		}
		if(null != listener)
			listener.onStarted(url, imageView);
		queueImage(new ImageRef(imageView, url, width, height,listener,isround,roundPx));
	}
	
	/**
	 * 存放图片信息
	 */
	class ImageRef {

		/** 图片对应ImageView控件 */
		ImageView imageView;
		/** 图片URL地址 */
		String url;
		int width = 0;
		int height = 0;
		ImageCallBack callback;
		boolean isround;//是否圆角
		int roundPx;//圆角弧度

		/**
		 * 构造函数
		 * 
		 * @param imageView
		 * @param url
		 * @param resId
		 */
		ImageRef(ImageView imageView, String url,
				ImageCallBack callback,boolean isround,int roundPx) {
			this.imageView = imageView;
			this.url = url;
			this.callback = callback;
			this.isround = isround;
			this.roundPx = roundPx;
		}
		
		ImageRef(ImageView imageView, String url,int width, int height,
				ImageCallBack callback,boolean isround,int roundPx) {
			this.imageView = imageView;
			this.url = url;
			this.width = width;
			this.height = height;
			this.callback = callback;
			this.isround = isround;
			this.roundPx = roundPx;
		}
	}
	
	/**
	 * 入队，后进先出
	 * 
	 * @param imageRef
	 */
	public void queueImage(ImageRef imageRef) {
		if(null != imageRef && null != imageRef.url){
			//网络图片
			if(imageRef.url.indexOf("sdcard") < 0){
				// 删除已有ImageView
				Iterator<ImageRef> iterator = mImageQueue.iterator();
				while (iterator.hasNext()) {
					if (iterator.next().imageView == imageRef.imageView) {
						iterator.remove();
					}
				}

				// 添加请求
				mImageQueue.push(imageRef);
				sendRequest();
			}else{
				//网络图片本地图片
				new LocalImageLoadTask(imageRef).execute();			
			}
		}
		
	}
	
	/**
	 * 发送请求
	 */
	private void sendRequest() {
		// 开启图片加载线程
		if (mImageLoaderHandler == null) {
			HandlerThread imageLoader = new HandlerThread("image_loader");
			imageLoader.start();
			mImageLoaderHandler = new ImageLoaderHandler(
					imageLoader.getLooper());
		}

		// 发送请求
		if (mImageLoaderIdle && mImageQueue.size() > 0) {
			ImageRef imageRef = mImageQueue.pop();
			Message message = mImageLoaderHandler.obtainMessage(MSG_REQUEST,
					imageRef);
			mImageLoaderHandler.sendMessage(message);
			mImageLoaderIdle = false;
			mRequestQueue.add(imageRef);
		}
	}
	
	
	/**
	 * 图片加载线程
	 */
	class ImageLoaderHandler extends Handler {

		public ImageLoaderHandler(Looper looper) {
			super(looper);
		}

		public void handleMessage(Message msg) {
			if (msg == null)
				return;

			switch (msg.what) {

			case MSG_REQUEST: // 收到请求
				Bitmap bitmap = null;
				Bitmap tBitmap = null;
				Bitmap endmap = null;
				if (msg.obj != null && msg.obj instanceof ImageRef) {

					ImageRef imageRef = (ImageRef) msg.obj;
					String url = imageRef.url;
					if (url == null)
						return;
					
					// 文件缓存中获取  
					try{
						bitmap = imageRef.width != 0 && imageRef.height != 0 ? fileCache.getImage(url+ imageRef.width+ imageRef.height):fileCache.getImage(url);
					}catch(OutOfMemoryError e){
						if (mImageManagerHandler != null) {
							FailReason failReason = new FailReason(FailReason.FailType.OUT_OF_MEMORY, e);
							Message message = mImageManagerHandler.obtainMessage(
									MSG_ERROR, failReason);
							mImageManagerHandler.sendMessage(message);
						}
					}catch(Exception e){
						if (mImageManagerHandler != null) {
							FailReason failReason = new FailReason(FailReason.FailType.UNKNOWN, e);
							Message message = mImageManagerHandler.obtainMessage(
									MSG_ERROR, failReason);
							mImageManagerHandler.sendMessage(message);
						}
					}
					
					if (bitmap != null) {
						// 写入map缓存
						if (imageRef.width != 0 && imageRef.height != 0) {
							if(memoryCache.getBitmapFromCache(url+ imageRef.width+ imageRef.height)==null){
								memoryCache.addBitmapToCache(url + imageRef.width + imageRef.height, bitmap);
							}
						} else {
							if(memoryCache.getBitmapFromCache(url)==null)
								memoryCache.addBitmapToCache(url,bitmap);
						}

					} else {
						try {
							byte[] data = loadImageFromNetWork(url);

							if (data != null) {
								BitmapFactory.Options opt = new BitmapFactory.Options();
								opt.inSampleSize = 1;

								opt.inJustDecodeBounds = true;
								BitmapFactory.decodeByteArray(data, 0,
										data.length, opt);
								int bitmapSize = opt.outHeight * opt.outWidth* 4;
								if (bitmapSize > 1000 * 1200)
									opt.inSampleSize = 2;
								
								opt.inJustDecodeBounds = false;
								tBitmap = BitmapFactory.decodeByteArray(data,
										0, data.length, opt);
								if (imageRef.width != 0 && imageRef.height != 0) {
									bitmap = MyThumbnailUtils
											.extractThumbnail(
													tBitmap,
													imageRef.width,
													imageRef.height,
													ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
								} else {
									bitmap = tBitmap;
									tBitmap = null;
								}
								if (bitmap != null && url != null) {
									// 写入SD卡
									if (imageRef.width != 0 && imageRef.height != 0) {
										memoryCache.addBitmapToCache(url+ imageRef.width+ imageRef.height, bitmap); 
										fileCache.saveBitmap(bitmap, url+ imageRef.width+ imageRef.height);
									} else {
										memoryCache.addBitmapToCache(url, bitmap); 
										fileCache.saveBitmap(bitmap, url);
									}
								}
							}
							
						} catch (OutOfMemoryError e) {
							if (mImageManagerHandler != null) {
								FailReason failReason = new FailReason(FailReason.FailType.OUT_OF_MEMORY, e);
								Message message = mImageManagerHandler.obtainMessage(
										MSG_ERROR, failReason);
								mImageManagerHandler.sendMessage(message);
							}
						}catch(MalformedURLException e){
							if (mImageManagerHandler != null) {
								FailReason failReason = new FailReason(FailReason.FailType.NETWORK_DENIED, e);
								Message message = mImageManagerHandler.obtainMessage(
										MSG_ERROR, failReason);
								mImageManagerHandler.sendMessage(message);
							}
						}catch(IOException e){
							if (mImageManagerHandler != null) {
								FailReason failReason = new FailReason(FailReason.FailType.IO_ERROR, e);
								Message message = mImageManagerHandler.obtainMessage(
										MSG_ERROR, failReason);
								mImageManagerHandler.sendMessage(message);
							}
						}catch(Exception e){
							if (mImageManagerHandler != null) {
								FailReason failReason = new FailReason(FailReason.FailType.UNKNOWN, e);
								Message message = mImageManagerHandler.obtainMessage(
										MSG_ERROR, failReason);
								mImageManagerHandler.sendMessage(message);
							}
						}
					}
					
					if(imageRef.isround)
						endmap = ImageOperUtils.getRoundedCornerBitmap(bitmap, imageRef.roundPx);
					else
						endmap = bitmap;
				}

				if (mImageManagerHandler != null) {
					Message message = mImageManagerHandler.obtainMessage(
							MSG_REPLY, endmap);
					mImageManagerHandler.sendMessage(message);
				}
				break;

			case MSG_STOP: // 收到终止指令
				Looper.myLooper().quit();
				break;

			}
		}
	}
	
	/** UI线程消息处理器 */
	@SuppressLint("HandlerLeak")
	private Handler mImageManagerHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg != null) {
				switch (msg.what) {
					case MSG_REPLY: // 收到应答
						do {
							ImageRef imageRef = mRequestQueue.remove();
	
							if (imageRef == null)
								break;
	
							if (imageRef.imageView == null
									|| imageRef.imageView.getTag() == null
									|| imageRef.url == null)
								break;
	
							if (!(msg.obj instanceof Bitmap) || msg.obj == null) {
								if(null != imageRef.callback)
									imageRef.callback.onFailed(imageRef.url, imageRef.imageView, null);
								else
									imageRef.imageView.setImageResource(R.drawable.default_picture);
								break;
							}
							Bitmap bitmap = (Bitmap) msg.obj;
	
							// 非同一ImageView
							if (imageViewReused(imageRef)) {
								break;
							}
							if(imageRef.callback != null)
								imageRef.callback.onComplete(imageRef.url, imageRef.imageView, bitmap);
							else 
								setImageBitmap(imageRef.imageView, bitmap, false);
	
						} while (false);
	
						break;
						
					case MSG_PROCESS://图片加载过程
						do{
							ImageRef imageRef = mRequestQueue.peek();
							if (imageRef == null)
								break;

							if (imageRef.imageView == null
									|| imageRef.imageView.getTag() == null
									|| imageRef.url == null)
								break;
							// 非同一ImageView
							if (imageViewReused(imageRef)) {
								break;
							}
							if(null != imageRef.callback)
								imageRef.callback.onProcess(imageRef.url, imageRef.imageView, ((Integer) msg.obj).intValue());
						}while(false);
						
						break;
					case MSG_ERROR: //图片加载失败
						do{
							ImageRef imageRef = mRequestQueue.peek();
							if (imageRef == null)
								break;

							if (imageRef.imageView == null
									|| imageRef.imageView.getTag() == null
									|| imageRef.url == null)
								break;
							// 非同一ImageView
							if (imageViewReused(imageRef)) {
								break;
							}
							if(null != imageRef.callback)
								imageRef.callback.onFailed(imageRef.url, imageRef.imageView, (FailReason) msg.obj);
							else
								imageRef.imageView.setImageResource(R.drawable.default_picture);
						}while(false);
						break;
				}
			}
			// 设置闲置标志
			mImageLoaderIdle = true;

			// 若服务未关闭，则发送下一个请求。
			if (mImageLoaderHandler != null) {
				sendRequest();
			}
		}
	};
	
	/**
	 * 从网络获取图片流
	 */
	private byte[] loadImageFromNetWork(String path) throws MalformedURLException,IOException{
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setRequestMethod("GET");// 这里是不能乱写的，详看API方法  
            conn.setConnectTimeout(9*1000);// 别超过10秒。  
			conn.connect();
            int state = conn.getResponseCode();
            byte[] imgData = null;
            if (state == 200) {   
                int length = (int) conn.getContentLength();// 获取长度  
                InputStream is = conn.getInputStream();
                if (length != -1) {  
                    imgData = new byte[length];  
                    byte[] buffer = new byte[1024];  
                    int readLen = 0;  
                    int destPos = 0;  
                    while ((readLen = is.read(buffer)) > 0) {  
                        System.arraycopy(buffer, 0, imgData, destPos, readLen);  
                        destPos += readLen;
                        Double percent = (destPos * 1.0 / length * 100.0 );
                        if (mImageManagerHandler != null) {
	    					Message message = mImageManagerHandler.obtainMessage(
	    							MSG_PROCESS, percent.intValue());
	    					mImageManagerHandler.sendMessage(message);
	    				}
                    }   
                }
                
                is.close();
                conn.disconnect();
            }
        	return imgData;
	}
	
	/**
	 * 加载本地图片流
	 */
	private class LocalImageLoadTask extends WYTaskExecutor<Void, Void, Bitmap>{
		private ImageRef imageref;
		
		public LocalImageLoadTask(ImageRef imageref){
			this.imageref = imageref;
		}
		
		@Override
		protected void onPreExecute() {
			
		}
		
		@Override
		protected Bitmap doInBackground(Void... params) {
			Bitmap b = null;
			Bitmap r = null;
			if(null != imageref && null != imageref.url)
			{
				File f = new File(imageref.url);
		        if (f != null && f.exists()){
		                b = decodeFile(f);
		        }
		        if(null != b){
		        	if (imageref.width != 0 && imageref.height != 0) {
		        		r = MyThumbnailUtils
								.extractThumbnail(
										b,
										imageref.width,
										imageref.height,
										ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
						memoryCache.addBitmapToCache(imageref.url+ imageref.width+ imageref.height, r); 
					} else {
						r = b;
						memoryCache.addBitmapToCache(imageref.url, r);
					}
		        }
			}
	        return r;
		}
		
		@Override
        protected void onPostExecute(Bitmap bmp) {
			do {
				if (imageref == null)
					break;

				if (imageref.imageView == null
						|| imageref.imageView.getTag() == null
						|| imageref.url == null)
					break;

				if (null == bmp) {
					break;
				}
				// 非同一ImageView
				if (imageViewReused(imageref)) {
					break;
				}
				if(imageref.callback != null)
					imageref.callback.onComplete(imageref.url, imageref.imageView, bmp);
				else 
					setImageBitmap(imageref.imageView, bmp, false);

			} while (false);
		}
	}
	
	/**
     * 防止图片错位
     * 
     * @param photoToLoad
     * @return
     */
    boolean imageViewReused(ImageRef imageref){
    	// 非同一ImageView
		if (!(imageref.url).equals((String) imageref.imageView
				.getTag())) {
			return true;
		}
    	return false;
    }
    
    // decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
    private Bitmap decodeFile(File f) {
        try {
                // decode image size
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(f), null, o);

                // Find the correct scale value. It should be the power of 2.
                final int REQUIRED_SIZE = 100;
                int width_tmp = o.outWidth, height_tmp = o.outHeight;
                int scale = 1;
                while (true) {
                        if (width_tmp / 2 < REQUIRED_SIZE
                                        || height_tmp / 2 < REQUIRED_SIZE)
                                break;
                        width_tmp /= 2;
                        height_tmp /= 2;
                        scale *= 2;
                }

                // decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
	
	/**
	 * 添加图片显示渐现动画
	 * 
	 */
	private void setImageBitmap(ImageView imageView, Bitmap bitmap,
			boolean isTran) {
		if (isTran) {
			final TransitionDrawable td = new TransitionDrawable(
					new Drawable[] {
							new ColorDrawable(android.R.color.transparent),
							new BitmapDrawable(bitmap) });
			td.setCrossFadeEnabled(true);
			imageView.setImageDrawable(td);
			td.startTransition(300);
		} else {
			imageView.setImageBitmap(bitmap);
		}
	}

	/**
	 * Activity#onStop后，ListView不会有残余请求。
	 */
	public void stop() {
		// 清空请求队列
		mImageQueue.clear();
	}
}
