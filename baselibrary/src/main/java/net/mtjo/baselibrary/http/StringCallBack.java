package com.wanyou.aframe.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wanyou.aframe.Loger;
import com.wanyou.aframe.utils.StrUtils;

/**
 * 用于处理JSON的http请求回调类<br>
 * 
 * <b>创建时间</b> 2014-9-26
 * 
 * @author moyaofen
 */
public class StringCallBack implements IHttpRespond {
	/**
	 * 0:返回成功！
	 * -999: Android 本地网络未打开
	 * -998: Android 无法连接服务器
	 * -997: Android 其它错误
	 * -996: 服务器返回数据格式错误
	 * */
	private int state;
	
	/**
	 * 1.结果成功为空，
	 * 2.报错信息
	 * */
	private String desc;
	
	/**
	 * 返回结果是jsonObjce时，直接解析在此对象
	 */
	private JSONObject jsonContent=null;
	
	/**
	 * 返回结果是jsonArray时，直接解析在此对象
	 */
	private JSONArray jsonArray = null;
	
	/**
	 * 返回结果是字符串或者是一个标记
	 */
	private String strContent;
	
	/**
	 * 请求源对象
	 */
	private Object resourceObj;
	
	/**
	 * http请求结果，在此做简单的解析
	 * @param String content http请求结果
	 */
	public void setContent(String content){
		this.jsonContent = null;
		this.jsonArray = null;
		this.strContent = null;
		try{
			if (content != null && content.length() > 0 ) {
				JSONObject json = new JSONObject(content);
				if(json.has("code"))
					this.setState(StrUtils.strToInt(json.getString("code")));
				if(json.has("message"))
					this.setDesc(json.getString("message"));
				if(json.has("data") && json.get("data")instanceof JSONObject)
					this.jsonContent = json.getJSONObject("data");	
				else if(json.has("data") && json.get("data") instanceof JSONArray)
					this.jsonArray = json.getJSONArray("data");
				else if(json.has("data"))
					this.strContent = json.get("data").toString();
			}
		}catch(JSONException e){
			this.state = 996;
			this.desc = "服务端返回数据格式错误";
			Loger.debug(getClass().getName() + ":解析出错", e);
		}
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public JSONObject getJsonContent() {
		return jsonContent;
	}

	public void setJsonContent(JSONObject jsonContent) {
		this.jsonContent = jsonContent;
	}

	public JSONArray getJsonArray() {
		return jsonArray;
	}

	public void setJsonArray(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	public String getStrContent() {
		return strContent;
	}

	public void setStrContent(String strContent) {
		this.strContent = strContent;
	}

	public Object getResourceObj() {
		return resourceObj;
	}

	public void setResourceObj(Object resourceObj) {
		this.resourceObj = resourceObj;
	}

	@Override
	public boolean isProgress() {
		return false;
	}

	@Override
	public void setProgress(boolean open) {
	}

	@Override
	public void onLoading(long count, long current) {
	}

	@Override
	public void onSuccess(Object t) {
	}

	@Override
	public void onFailure(Throwable t, int errorNo, String strMsg) {
	}
}
