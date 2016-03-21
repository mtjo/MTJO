package com.wanyou.aframe.http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import com.wanyou.aframe.core.WYTaskExecutor;

import android.app.Activity;
import android.app.ProgressDialog;
/**
 * 文件上传类
 * 分两种，一种是多参数post
 * 一种是直接post数据流
 */
public class DataPost {
	private static String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.1.1) Gecko/20090715 Firefox/3.5.1";
	private static String characterSet = "utf-8";
	
	private static DataPost instance = new DataPost();
	
	private DataPost(){};
	
	public static DataPost getInstancts(){
		return instance;
	}
	
	/**
     * Http Post请求
     * 
     * @param url 地址
     * @param params 参数
     * @param callback 请求中的回调方法，可选类型：StringCallBack
     */
	public void urlPost(String url,Map<String,Object> params, StringCallBack callback,
			Activity aty,String msg){
		new HttpDataPostTask(url,params,callback,aty, msg).execute();
	}
	
	/**
     * 实现HttpUrlPost请求的任务
     */
    private class HttpDataPostTask extends
            WYTaskExecutor<Void, Object, Object>{
    	private String url;
    	private Map<String,Object> params;
    	private StringCallBack callback;
    	private Activity aty;
    	private String msg;
    	private ProgressDialog dialog = null;
    	
    	public HttpDataPostTask(String url, Map<String,Object> params, StringCallBack callback,
    			Activity aty, String msg){
    		this.url = url;
    		this.params = params;
    		this.callback = callback;
    		this.aty = aty;
    		this.msg = msg;
    	}
    
    	@Override
    	protected void onPreExecute() {
    		
    	}
    	
    	@Override
		protected void onProgressUpdate(Object... values) {
    		if(null != callback)
    			callback.onLoading((Long) values[0], (Long) values[1]);
		}

		@Override
		protected Object doInBackground(Void... params) {
			callHttp();
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
		}
		
		private void callHttp(){
			try{
				 URL urlObj = new URL(url);
				 HttpURLConnection  conn =  (HttpURLConnection)urlObj.openConnection();
		
				 // 边界符
				 // var boundary = "---------------" + DateTime.Now.Ticks.ToString("x");
				 String boundary = "---------------7d4a6d158c9";
		
				 // 设置属性
				 conn.setRequestMethod("POST"); // 以Post方式提交表单，默认post方式
				 conn.setDoInput(true);
				 conn.setDoOutput(true);
				 conn.setUseCaches(false); // post方式不能使用缓存
				 conn.setReadTimeout(0);
				 conn.setChunkedStreamingMode(0);
		
				 // 设置请求头信息
				 conn.setRequestProperty("Connection", "Keep-Alive");
				 conn.setRequestProperty("Charset", characterSet);
				 conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);	            
				 conn.setRequestProperty("user-agent", userAgent);
				 
				// 获得输出流
				 OutputStream requestStream = new DataOutputStream(conn.getOutputStream());
				 doWritePost(requestStream, boundary);
				 
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
				 
				 if(conn.getResponseCode()==200){
					 callback.setContent(result);
	        	 }
	        	 else{
					if (callback != null) {
						callback.setState(conn.getResponseCode());
						callback.setDesc("服务访问出错！请检测服务是否可用！");
					}
	        	 }
				 conn.disconnect();
			 }catch (MalformedURLException e) {
					System.out.println("=========MalformedURLException===========");
					if (callback != null) {
						callback.setState(-997);
						callback.setDesc("请检测URL地址是否正确!");
					}
				} catch (ProtocolException e) {
					System.out.println("=========ProtocolException===========");
					if (callback != null) {
						callback.setState(-997);
						callback.setDesc("请检测协议是否正确!");
					}
				}catch(UnsupportedEncodingException e){
					System.out.println("=========ProtocolException===========");
					if (callback != null) {
						callback.setState(-997);
						callback.setDesc("请检测协议是否正确!");
					}
				} catch (ConnectException e) {
					System.out.println("=========ConnectException===========");
					if (callback != null) {
						callback.setState(-998);
						callback.setDesc("无法连接服务器，请检测网络服务是否可用！");
					}
				}catch (IOException e) {
					System.out.println("=========IOException===========");
					if (callback != null) {
						callback.setState(-999);
						callback.setDesc("无法连接服务器，请检测服务是否可用，及本机网络是否正常！");
					}
				} catch (IllegalArgumentException e) {
					System.out.println("=========IllegalArgumentException===========");
					if (callback != null) {
						callback.setState(-997);
						callback.setDesc("调用参数错误!");
					}
					e.printStackTrace();
				} catch (IllegalStateException e) {
					System.out.println("=========IllegalStateException===========");
					if (callback != null) {
						callback.setState(-997);
						callback.setDesc("状态非法! ");
					}
				} catch (Exception e) {
					System.out.println("=========Exception===========");
					if (callback != null) {
						callback.setState(-997);
						callback.setDesc("服务调用未知错误!");
					}
				}
		}
		
		/**
		  * 直接写出
		 * @param requestStream
		 * @param boundary
		 * @throws IOException 
		 */
		private void doWritePost(OutputStream requestStream,
				String boundary) throws IOException {
			 //先求出总的输出数据长度（不准确，仅用于大概参考）
			 long totalSize = 2; //总长
			 long totalOutSize = 0; //已经发送出去的总长度
			 
			 if (params != null) {
					for (Iterator iterator = params.keySet().iterator(); iterator
							.hasNext();) {
						String key = (String) iterator.next();
						Object param =  params.get(key);
						if(param.getClass().equals(FileParameter.class)){
							FileParameter fileParameter = (FileParameter)param;
							totalSize += fileParameter.getFileSize();
						}else{
							String postData = String.format("--%1$s\r\nContent-Disposition: form-data; name=\"%2$s\"\r\n\r\n%3$s",
									 boundary,
									 key,
									 String.valueOf(param));
							 totalSize += postData.getBytes(characterSet).length;
						}
					}
				}
			 
			 //开始循环将内容写到输入数据流中
			 boolean needsCLRF = false;
			 byte[] tempBytes = null;
			 
			 if (params != null) {
					for (Iterator iterator = params.keySet().iterator(); iterator
							.hasNext();) {
						if (needsCLRF){
							 tempBytes = "\r\n".getBytes(characterSet);
							 requestStream.write(tempBytes, 0,  tempBytes.length);
						 }
						 
						 needsCLRF = true;
						 
						 String key = (String) iterator.next();
						 Object param = params.get(key);
						 if(param.getClass().equals(FileParameter.class)){
							 FileParameter fileParameter = (FileParameter) param;
							 String header = String.format("--%1$s\r\nContent-Disposition: form-data; name=\"%2$s\"; filename=\"%3$s\";\r\nContent-Type: %4$s\r\n\r\n",
									 boundary,
									 fileParameter.getName(),
									 fileParameter.getFileName(),
									 fileParameter.getContenttype());
							 
							 tempBytes = header.getBytes(characterSet);
							 requestStream.write(tempBytes, 0, tempBytes.length);
							 
							 try {
								 InputStream inStream = null;
								 if(fileParameter.getDataType() ==1){
									 //直接输入data
									 inStream = new ByteArrayInputStream(fileParameter.getFileBytes());
								 }else{
									 //读取文件输出data
									 File file = new File(fileParameter.getOriginalFileName());
									 inStream = new FileInputStream(file); 
								 }
								 
								 byte[] b = new byte[1000];  
								 int n;  
								 while ((n = inStream.read(b)) != -1) {
									 requestStream.write(b, 0, n);
									 totalOutSize += n;
									 requestStream.flush();
									 publishProgress(totalOutSize, totalSize);
								 }  
								 inStream.close();
								 
							 } catch (FileNotFoundException e) {  
								 e.printStackTrace();  
							 } catch (IOException e) {  
								 e.printStackTrace();  
							 }
						 }else{
							 String postData = String.format("--%1$s\r\nContent-Disposition: form-data; name=\"%2$s\"\r\n\r\n%3$s",
									 boundary,
									 key,
									 String.valueOf(params));
							 tempBytes = postData.getBytes(characterSet);
							 requestStream.write(tempBytes, 0, tempBytes.length);
							 totalOutSize += tempBytes.length;
							 requestStream.flush();
							 publishProgress(totalSize,totalOutSize);
						 }
					}
			 }
			 // Add the end of the request.  Start with a newline
			 String footer = "\r\n--" + boundary + "--\r\n";
			 tempBytes = footer.getBytes(characterSet);
			 requestStream.write(tempBytes, 0, tempBytes.length);
		}
		
//		 /**
//		  * 
//		  * 计算进度
//		 * @param totalOutSize
//		 * @param totalSize
//		 */
//		private double getPostProgress(long totalOutSize, long totalSize) {
//			 double percent = (double) (1.0 * totalOutSize / totalSize * 100);
//			 return percent;
//		}
    }
}
