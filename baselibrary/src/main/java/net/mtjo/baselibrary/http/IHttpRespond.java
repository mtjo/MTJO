package com.wanyou.aframe.http;

/**
 * http请求规范接口协议<br>
 * 
 * <b>创建时间</b> 2014-9-26
 * 
 * @author moyaofen
 */
public interface IHttpRespond {
	/**
     * 开发者必须实现该方法，供外部调用（启动进度回调）
     */
    boolean isProgress();

    /**
     * 开发者必须实现该方法，供外部调用（启动进度回调）
     */
    void setProgress(boolean open);

    /**
     * 进度回调
     * 
     * @param count
     *            总数
     * @param current
     *            当前进度
     */
    void onLoading(long count, long current);

    /**
     * http请求成功时回调
     * 
     * @param t
     */
    void onSuccess(Object t);

    /**
     * http请求失败时回调
     * 
     * @param t
     * @param errorNo
     *            错误码
     * @param strMsg
     *            错误原因
     */
    void onFailure(Throwable t, int errorNo, String strMsg);
}
