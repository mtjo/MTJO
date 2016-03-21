package com.wanyou.aframe.net;

import com.wanyou.aframe.net.NetWorkUtil.netType;

/**
 * 监听网络是否改变的观察者
 */
public class NetChangeObserver {
	/**
	 * 网络连接连接时调用
	 */
	public void onConnect(netType type)
	{
		
	}

	/**
	 * 当前没有网络连接
	 */
	public void onDisConnect()
	{

	}
}
