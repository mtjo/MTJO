package com.wanyou.aframe.json.parse;
/**
 * json解析
 * @author zxp 
 * 
 *
 */

import java.util.List;

import com.alibaba.fastjson.JSON;

public class ParseJson {
	
	/**
	 *  返回解析对象 
	 * @param jsonString
	 * @param clazz
	 * @return
	 */
	public static <T> T getEntity(String jsonString, Class<T> clazz){
		T t = null;
		try {
			t = JSON.parseObject(jsonString,clazz);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return t;
	}
	
	/**
	 * 解析返回list
	 * @param jsonString
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> getEntityList(String jsonString, Class<T> clazz){
		List<T> t = null;
		try {
			 t = JSON.parseArray(jsonString, clazz);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return t;
	}

}
