package com.wanyou.aframe.http;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class FormAgent {
	private String url = "";
	private int connectTimeOut = 30 * 1000; // 连接主机超时时间
    private int readTimeout = 30 * 1000; // 从主机读取数据超时时间
    private boolean doOutput = true; // 是否输出
    private boolean doInput = true; // 是否输入
    private boolean userCaches = false; //是否使用缓存
    private String charSet = "utf-8"; // 字符编码格式
    private String connect = "Keep-Alive";
	private String contentType = "application/x-www-form-urlencoded;characterSet:utf-8";
	private String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	private String accept_lan = "zh-cn,zh;q=0.8,en-us;q=0.5,en;q=0.3";
	private String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.1) Gecko/20090715 Firefox/3.5.1";
	private static CookieManager cookieContainer = CookieManager.getInstance();
	
	private Map<String, String> postParams = null;
	private Map<String, String> getParams = null;
	
	private int  statusCode;
	private String responseContent = "";
	
	/**
	 * Http Post表单请求
	 * @author Moyaofen
	 * @return String
	 * @throws
	 */
	public String doPost() throws MalformedURLException, ProtocolException, ConnectException, IOException, IllegalArgumentException, 
	IllegalStateException,SocketTimeoutException,Exception{
		String postDataStr = "";
		if (null != postParams) {
			StringBuilder str = new StringBuilder();
	        Iterator<String> it = postParams.keySet().iterator();
	        while (it.hasNext()) {
	        	String key = (String) it.next();
	        	String value;
	            try {
	                value = URLEncoder.encode(String.valueOf(postParams.get(key)), "utf-8");
	            } catch (UnsupportedEncodingException e) {
	                value = String.valueOf(postParams.get(key));
	            }
	            str.append(key).append("=").append(value).append("&");
	        }
	        postDataStr = str.toString();
		}
		
		URL urlObj = new URL(this.getUrl());
        HttpURLConnection  conn =  (HttpURLConnection)urlObj.openConnection();
    
        //设置cookie
        cookieContainer.setCookies(conn);
        
        /**
         * 设置关键值
         */
        conn.setRequestMethod("POST");
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false); // post方式不能使用缓存
        conn.setReadTimeout(this.getReadTimeout());
        conn.setConnectTimeout(this.getConnectTimeOut());
        
        // 设置请求头信息
        conn.setRequestProperty("Connection", this.getConnect());
        conn.setRequestProperty("Charset", this.getCharSet());
        conn.setRequestProperty("Content-Type", this.getContentType());
        conn.setRequestProperty("user-agent", this.getUserAgent());
        
        // 获得输出流
        byte[] tempBuffer = postDataStr.getBytes(this.getCharSet());
        OutputStream requestStream = new DataOutputStream(conn.getOutputStream());
        requestStream.write(tempBuffer, 0, tempBuffer.length);
        requestStream.flush();
        requestStream.close();
     
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        InputStream inStream = conn.getInputStream();
        byte[] buff = new byte[100]; //buff用于存放循环读取的临时数据
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
        	outStream.write(buff, 0, rc);
        } 
        String result =  outStream.toString();
        outStream.close();
        inStream.close();
        
       this.statusCode = conn.getResponseCode();
       this.responseContent = result;
       cookieContainer.storeCookies(conn);
       conn.disconnect();
		
		return  this.responseContent ;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getConnectTimeOut() {
		return connectTimeOut;
	}
	public void setConnectTimeOut(int connectTimeOut) {
		this.connectTimeOut = connectTimeOut;
	}
	public int getReadTimeout() {
		return readTimeout;
	}
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	public boolean isDoOutput() {
		return doOutput;
	}
	public void setDoOutput(boolean doOutput) {
		this.doOutput = doOutput;
	}
	public boolean isDoInput() {
		return doInput;
	}
	public void setDoInput(boolean doInput) {
		this.doInput = doInput;
	}
	public boolean isUserCaches() {
		return userCaches;
	}
	public void setUserCaches(boolean userCaches) {
		this.userCaches = userCaches;
	}
	public String getCharSet() {
		return charSet;
	}
	public void setCharSet(String charSet) {
		this.charSet = charSet;
	}
	public String getConnect() {
		return connect;
	}
	public void setConnect(String connect) {
		this.connect = connect;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public String getAccept() {
		return accept;
	}
	public void setAccept(String accept) {
		this.accept = accept;
	}
	public String getAccept_lan() {
		return accept_lan;
	}
	public void setAccept_lan(String accept_lan) {
		this.accept_lan = accept_lan;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public static CookieManager getCookieContainer() {
		return cookieContainer;
	}
	public static void setCookieContainer(CookieManager cookieContainer) {
		FormAgent.cookieContainer = cookieContainer;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public String getResponseContent() {
		return responseContent;
	}
	public void setResponseContent(String responseContent) {
		this.responseContent = responseContent;
	}
	public Map<String, String> getPostParams() {
		return postParams;
	}
	public void setPostParams(Map<String, String> postParams) {
		this.postParams = postParams;
	}
	public Map<String, String> getGetParams() {
		return getParams;
	}
	public void setGetParams(Map<String, String> getParams) {
		this.getParams = getParams;
	}
}
