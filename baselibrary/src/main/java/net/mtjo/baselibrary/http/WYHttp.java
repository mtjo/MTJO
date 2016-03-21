package com.wanyou.aframe.http;

import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.util.Map;

import com.wanyou.aframe.core.WYTaskExecutor;
import com.wanyou.aframe.http.cache.HttpCache;
import com.wanyou.aframe.ui.ViewInject;
import com.wanyou.aframe.ui.widget.CustomProgressDialog;

import android.app.Activity;

/**
 * http post请求入口
 * 
 * <b>创建时间</b> 2014-9-26
 * 
 * @author moyaofen
 */
public class WYHttp {
	private static WYHttp instance = new WYHttp();
	
	private WYHttp(){};
	
	public static WYHttp getInstancts(){
		return instance;
	}
	
	/**
     * Http Post请求
     * 
     * @param url 地址
     * @param params 参数
     * @param callback 请求中的回调方法，可选类型：StringCallBack
     */
	public void urlPost(String url, Map<String,String> params, StringCallBack callback,
			Activity aty,String msg){
		new HttpUrlPostTask(url,params,callback,aty, msg).execute();
	}
	
	/**
     * Http Post请求
     * 
     * @param url 地址
     * @param params 参数
     * @param callback 请求中的回调方法，可选类型：StringCallBack
     * @param iscash  是否缓存
     */
	public void urlPost(String url, Map<String,String> params, StringCallBack callback,
			Activity aty,String msg, boolean iscash){
		String cash = HttpCache.create().get(url);
		if(null != cash){
			callback.setContent(cash);
			callback.onSuccess(null);
		}else{
			new HttpUrlPostTask(url,params,callback,aty, msg, iscash).execute();
		}
	}
	
	/**
     * 实现HttpUrlPost请求的任务
     */
    private class HttpUrlPostTask extends
            WYTaskExecutor<Void, Object, Object>{
    	private String url;
    	private Map<String, String> params;
    	private StringCallBack callback;
    	private Activity aty;
    	private String msg;
    	private CustomProgressDialog dialog = null;
    	private boolean iscash;
    	
    	public HttpUrlPostTask(String url, Map<String,String> params, StringCallBack callback,
    			Activity aty, String msg){
    		this.url = url;
    		this.params = params;
    		this.callback = callback;
    		this.aty = aty;
    		this.msg = msg;
    	}
    	
    	public HttpUrlPostTask(String url, Map<String,String> params, StringCallBack callback,
    			Activity aty, String msg, boolean iscash){
    		this.url = url;
    		this.params = params;
    		this.callback = callback;
    		this.aty = aty;
    		this.msg = msg;
    		this.iscash = iscash;
    	}
    	
    	@Override
    	protected void onPreExecute() {
    		dialog = ViewInject.getprogress(aty, msg, true);
    		if(null != dialog){
    			dialog.show();
    		}
    	}

		@Override
		protected Object doInBackground(Void... params) {
			try {
				FormAgent form = new FormAgent();
				form.setUrl(this.url);
				form.setPostParams(this.params);
				form.doPost();
				
				if(form.getStatusCode()== 200){
					this.callback.setContent(form.getResponseContent());
					//判断是否缓存
					if(iscash){
						if(this.callback.getState() == 0)
							HttpCache.create().add(this.url, form.getResponseContent());
					}
				}else{
				}
			} catch (ConnectException e) {
				this.callback.setState(-998);
				this.callback.setDesc("无法连接服务器！");
			} catch (SocketTimeoutException e) {
				this.callback.setState(-998);
				this.callback.setDesc("连接服务器超时！");
			} catch (MalformedURLException e) {
				this.callback.setState(-997);
				this.callback.setDesc("请检测url是否正确！");
			} catch (ProtocolException e) {
				this.callback.setState(-997);
				this.callback.setDesc("请检测协议是否正确");
			} catch (IllegalArgumentException e) {
				this.callback.setState(-997);
				this.callback.setDesc("参数错误！");
			} catch (IllegalStateException e) {
				this.callback.setState(-997);
				this.callback.setDesc("非法状态");
			} catch (IOException e) {
				this.callback.setState(-999);
				this.callback.setDesc("本地网络未打开，请检查网络配置！");
			} catch (Exception e) {
				this.callback.setState(-997);
				this.callback.setDesc("未知服务错误！");
			}
			return null;
		}
    	
		@Override
        protected void onPostExecute(Object result){
			if(null != callback){
				if(callback.getState() == 0){
					callback.onSuccess(null);
				}else{
					callback.onFailure(null, callback.getState(), callback.getDesc());
				}
			}
			
			if(null != dialog && dialog.isShowing()){
				dialog.dismiss();
			}
			dialog = null;
		}
    }
}
