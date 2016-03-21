package com.wanyou.aframe.bitmap;

import android.graphics.Bitmap;
import android.view.View;

/**
 * 图片加载监听
 * @author Moyaofen
 */
public interface ImageCallBack {
	/**
	 * 加载前初始化工作
	 */
	void onStarted(String imageUri, View view);
	
	/**
	 * 加载失败
	 */
	void onFailed(String imageUri, View view, FailReason failReason);
	
	/**
	 * 加载进度
	 */
	void onProcess(String imageUri,View view,int percent);
	
	/**
	 * 加载完成
	 */
	void onComplete(String imageUri, View view, Bitmap loadedImage);
}
