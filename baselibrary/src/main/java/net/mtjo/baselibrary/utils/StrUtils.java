package com.wanyou.aframe.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Collator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;

import com.wanyou.aframe.utils.HanziToPinyin.Token;

public class StrUtils {
	
	public static String longToStr(long str){
		Date date = new Date(str);
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}
	
	/**
	 * 判断两个时间相隔的天，小时，分钟
	 * @param pBeginTime
	 * @param pEndTime
	 * @return
	 * @throws Exception
	 */
	 public static String TimeDiff(String timeStr){
		return TimeDiff(StrUtils.strToLong(timeStr)*1000);
	 }
	 
	 public static String TimeDiff(long t){
		if(t == 0) return "";
		
	    long time = System.currentTimeMillis() - t;
	    long minute = (long) Math.floor(time/60/1000.0f);// 分钟前 
	    long hour = (long) Math.floor(time/60/60/1000.0f);// 小时  
	    long day = (long) Math.floor(time/24/60/60/1000.0f);// 天前
	    
	    if (day > 0 && day < 7) {
	        if (day == 1) {
	        	return "昨天";
			}else if(day == 2){
				return "前天";
			}else{
				return getWeek(t);
			}
	    }else if(day >= 7){
	    	return getDay(t);
		} else if (hour > 0) {  
			return hour + "小时前";  
	    }else{
	    	if(minute < 1) 
	    		return "刚刚";
			  return minute + " 分钟前";
	    }
	 }
	 
	 public static String getWeek(Long l){
		 final String dayNames[] = { "周日", "周一", "周二", "周三", "周四", "周五","周六" };
		 Calendar calendar = Calendar.getInstance();
		 Date date = new Date(l);
		 calendar.setTime(date);
		 int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;
		 if(dayOfWeek<0)dayOfWeek=0;
		 return dayNames[dayOfWeek];
	 }
	 
	// 格式到天
	public static String getDay(long time) {
		return new SimpleDateFormat("yyyy-MM-dd").format(time);
	}
	
	/**
	 * 判断字符是否为空字符串
	 * @param o
	 * @return
	 */
	public static boolean isEmpty(String o){
		return o == null || "".equals(o.toString().trim())
				|| "null".equalsIgnoreCase(o.toString().trim())
				|| "undefined".equalsIgnoreCase(o.toString().trim());
	}
	
	/**
	 * 字符串转int
	 */
	public static int strToInt(String sInt) {
		int dReturn =  0;
		try {
			dReturn = Integer.parseInt(sInt);
		} catch(Exception e) {
			dReturn = 0;
		}
		
		return dReturn;
	}
	
	/**
	 * 字符串转long
	 */
	public static long strToLong(String sLong) {
		long dReturn =  0;
		try {
			dReturn = Long.parseLong(sLong);
		} catch(Exception e) {
			dReturn = 0;
		}
		
		return dReturn;
	}
	
	/**
	 * 字符串转double
	 */
	public static double strToDounle(String db){
		double dReturn = 0;
		try{
			dReturn = Double.parseDouble(db);
		}catch(Exception e){
			dReturn = 0;
		}
		return dReturn;
	}
	
	/**
	 * 去除空字符串
	 */
	public static String strToString(String str){
		return null == str ? "" : (str.equals("null")?"":str);
	}
	
	/**
	 * 时间戳字符串转时间并格式化
	 * @param   time  
	 * 				时间戳字符串
	 * @param   format
	 * 				时间格式,默认格式yyyy-MM-dd HH:mm:ss
	 */
	public static String strToDataFormat(String time, String format){
		String rStr = "";
		long datatime = strToLong(time)*1000;
		if(datatime >0){
			Date date = new Date(datatime);
			rStr = dateToFromat(date, format);
		}
		return rStr;
	}
	
	/**
	 * 时间格式化
	 */
	@SuppressLint("SimpleDateFormat")
	public static String dateToFromat(Date date,String format){
		if(null == date)return null;
		SimpleDateFormat sdf = new SimpleDateFormat(null == format?"yyyy-MM-dd HH:mm:ss":format);
		String strDate = sdf.format(date);
		return strDate;
	}
	
	/**
	 * 获取第一个汉字的拼音的第一个字母
	 */
	public static String getFirstPinYin(String source){ 
		if (!Arrays.asList(Collator.getAvailableLocales()).contains(Locale.CHINA)) { 
			return source; 
		}  
		ArrayList<Token> tokens = HanziToPinyin.getInstance().get(source); 
		if (tokens == null || tokens.size() == 0) { 
			return source; 
		} 
		StringBuffer result = new StringBuffer(); 
		for (Token token : tokens) { 
			if (token.type == Token.PINYIN) { 
				result.append(token.target.charAt(0)); 
			} else { 
//				result.append("#");
				result.append(getSortKey(source));
			} 
			return result.toString(); 
		}
		return "#";
	}
	
	public static String getSortKey(String sortKeyString) {
		if(null == sortKeyString || sortKeyString.length()==0)return "#";
        String key = sortKeyString.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        }
        return "#";
    }
	
	/*** 
     * 获取汉字串拼音首字母，英文及特殊字符字符不变
     * @param chinese 汉字串* 
     * @return 汉语拼音首字母 
     */ 
	public static String cn2FirstSpell(String chinese) {
		return getFirstPinYin(chinese);
	}
	
	/**
	 * GUID/UUID生成
	 * @return
	 */
	public static String getGUID() {
		return newID();
	}
	public static String newID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
	
	
	/**
	 * 匹配移动号码
	 */
	public static boolean isMobileNO(String mobiles){
		if(null == mobiles)return false;
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");  
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}
	
	/**
	 * 匹配密码
	 */
	public static boolean isPassword(String password){
		if(null == password)return false;
		Pattern p = Pattern.compile("\\w{6,30}");  
		Matcher m = p.matcher(password);
		return m.matches();
	}
	
	/**
	 * 整秒数转hh:mm:ss格式
	 */
	public static String secToTime(long time) {  
        String timeStr = null;  
        int hour = 0;  
        int minute = 0;  
        int second = 0;  
        if (time <= 0)  
            return "00:00:00";  
        else {  
            minute = (int) (time / 60);  
            if (minute < 60) {  
                second = (int) (time % 60);  
                timeStr ="00:" + unitFormat(minute) + ":" + unitFormat(second);  
            } else {  
                hour = minute / 60;  
//                if (hour > 99)  
//                    return "99:59:59";  
                minute = minute % 60;  
                second = (int) (time - hour * 3600 - minute * 60);  
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);  
            }  
        }  
        return timeStr;  
    }  
  
    public static String unitFormat(int i) {  
        String retStr = null;  
        if (i >= 0 && i < 10)  
            retStr = "0" + Integer.toString(i);  
        else  
            retStr = "" + i;  
        return retStr;  
    }
    
    /**
     * 涉案金额正则表达式
     */
	public static boolean isAmount(String samount){
		if(null == samount)return false;
		String repx = "";
		if(samount.indexOf(".") != -1){
			repx = "^[0-9]+\\.{0,1}[0-9]{0,2}$";
		}else{
			repx = "^[1-9]+\\.{0,1}[0-9]{0,2}$";
		}
		Pattern p = Pattern.compile(repx);  
		Matcher m = p.matcher(samount);
		return m.matches();
	}   
	
	/**
     * 16进制表示的字符串转换为字节数组。
     * 
     * @param s
     *            16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] d = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            d[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return d;
    }
    
    /**
     * byte[]数组转换为16进制的字符串。
     * 
     * @param data
     *            要转换的字节数组。
     * @return 转换后的结果。
     */
    public static final String byteArrayToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        for (byte b : data) {
            int v = b & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.getDefault());
    }
    
    /**
     * 判断是否是新年
     */
    public static boolean isNewYear(){
    	Calendar cal=Calendar.getInstance();
    	cal.set(Calendar.YEAR, 2015);
    	cal.set(Calendar.MONTH, 2);
    	cal.set(Calendar.DAY_OF_MONTH, 6);
    	Date date=cal.getTime();
    	long time = date.getTime();
    	long current = new Date().getTime();
    	return current <= time;
    }
    
    /**
     * md5加密
     */
    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
