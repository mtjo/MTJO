package com.wanyou.aframe.ui;

import com.wanyou.aframe.ui.widget.CustomDialog;
import com.wanyou.aframe.ui.widget.CustomProgressDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.Toast;

/**
 * 侵入式View的调用工具类,如弹出确认窗，显示提示，进度条等
 * 
 * @author Moyaofen
 */
public class ViewInject {
	
	/**
     * 返回一个等待信息弹窗
     * 
     * @param aty
     *            要显示弹出窗的Activity
     * @param msg
     *            弹出窗上要显示的文字
     * @param cancel
     *            dialog是否可以被取消
     */
    public static CustomProgressDialog getprogress(Activity aty,
            String msg, boolean cancel) {
    	if(null != aty && null != msg){
    		CustomProgressDialog progressDialog = new CustomProgressDialog(aty, msg); 
    		progressDialog.setCancelable(false);
    	    progressDialog.show();  
    	    return progressDialog;
    	}
        return null;
    }
    
    /**
     * 返回一个信息警告框
     * @param  aty   
     * 				要显示警告框的Activity
     * @param msg
     * 				警告框上要显示的文字
     */
	public static Dialog getWarnDialog(Activity aty, String msg){
		if(null == aty || null == msg){
			return null;
		}
		CustomDialog.Builder dialogBuilder = new CustomDialog.Builder(aty);
		dialogBuilder.setMessage(msg);
		dialogBuilder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				dialog = null;
			}}
		);
		final Dialog dialog = dialogBuilder.create();
		dialog.show();
		return dialog;
	}
	
	/**
	 * 返回一个确认弹出框
	 * @param  aty   
	 * 				要显示警告框的Activity
	 * @param msg
	 * 				警告框上要显示的文字
	 */
	public static Dialog getConfirmDialog(Activity aty, String msg,
			android.content.DialogInterface.OnClickListener confimClick, 
			android.content.DialogInterface.OnClickListener cancleClick){
		if(null == aty || null == msg){
			return null;
		}
		CustomDialog.Builder dialogBuilder = new CustomDialog.Builder(aty);
		dialogBuilder.setMessage(msg);
		dialogBuilder.setPositiveButton("确定", null != confimClick ? confimClick
						: new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						dialog = null;
					}
				}
		);
		dialogBuilder.setNegativeButton("取消", null != cancleClick ? cancleClick
						: new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						dialog = null;
					}
				}
		);
		final Dialog dialog = dialogBuilder.create();
		dialog.show();
		return dialog;
	}
	
	/**
	 * 返回一个确认弹出框
	 * @param  aty   
	 * 				要显示警告框的Activity
	 * @param msg
	 * 				警告框上要显示的文字
	 */
	public static Dialog getConfirmDialog(Activity aty, String msg,String yes, String no,
			android.content.DialogInterface.OnClickListener confimClick, 
			android.content.DialogInterface.OnClickListener cancleClick){
		if(null == aty || null == msg){
			return null;
		}
		CustomDialog.Builder dialogBuilder = new CustomDialog.Builder(aty);
		dialogBuilder.setMessage(msg);
		dialogBuilder.setPositiveButton(yes,null != confimClick ? confimClick 
				: new android.content.DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				dialog = null;
			}}
				);
		dialogBuilder.setNegativeButton(no,null != cancleClick ? cancleClick 
				: new android.content.DialogInterface.OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				dialog = null;
			}}
				);
		final Dialog dialog = dialogBuilder.create();
		dialog.show();
		return dialog;
	}

	/**
	 * 返回一个确认弹出框
	 * @param  aty
	 * 				要显示警告框的Activity
	 * @param msg
	 * 				警告框上要显示的文字
	 */
	public static Dialog getConfirmDialog_textToLeft(Activity aty, String msg,String yes, String no,
										  android.content.DialogInterface.OnClickListener confimClick,
										  android.content.DialogInterface.OnClickListener cancleClick){
		if(null == aty || null == msg){
			return null;
		}
		CustomDialog.Builder dialogBuilder = new CustomDialog.Builder(aty);
		dialogBuilder.setMessage(msg);
		dialogBuilder.setPositiveButton(yes,null != confimClick ? confimClick
						: new android.content.DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						dialog = null;
					}}
		);
		dialogBuilder.setNegativeButton(no,null != cancleClick ? cancleClick
						: new android.content.DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
						dialog = null;
					}}
		);
		final Dialog dialog = dialogBuilder.create(true);
		dialog.show();
		return dialog;
	}
	
	/**
	 * 显示Toast提示
	 * @param aty 
	 * 				要显示Toast的Activity
	 * @param msg
	 * 				要Toast的消息
	 */
	public static void showToast(Activity aty, String msg){
		if(null == aty || null == msg){
			return;
		}
		Toast.makeText(aty, msg, Toast.LENGTH_SHORT).show();
	}
}
