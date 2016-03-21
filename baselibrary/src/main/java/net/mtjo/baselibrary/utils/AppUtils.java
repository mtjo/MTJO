package com.wanyou.aframe.utils;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;

/**
 * 系统工具类，如获取版本信息等
 */
public class AppUtils {
	/**
	 * 获取当前版本名称
     * @throws NameNotFoundException 
	 */
	public static String getVersion(Activity aty) throws NameNotFoundException,Exception{
		String version="";
		if(null != aty){
			PackageManager manager = aty.getPackageManager();
			PackageInfo info = manager.getPackageInfo(aty.getPackageName(), 0);
			version = info.versionName;
		}
		return null==version?"":version; 
	}
	
	/**
	 * 拨打号码
	 */
	public static void doCall(Activity aty, String mobile) throws Exception{
		if(null != aty && null != mobile){
			Intent phoneIntent = new Intent("android.intent.action.CALL",Uri.parse("tel:" + mobile));
			phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			aty.startActivity(phoneIntent);
		}
	}
	
	/**
	 * 判断gps是否开通
	 */
	public static boolean openGPSSettings(Activity aty) {
		LocationManager alm = (LocationManager) aty.getSystemService(Context.LOCATION_SERVICE);     
		if (alm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {     
			return true;     
		} 
		return false;
	}
	
	/**
	 * 获取定位
	 */
	public static Location getLocation(Activity aty) 
	{ 
	    // 获取位置管理服务 
	    LocationManager locationManager; 
	    String serviceName = Context.LOCATION_SERVICE; 
	    locationManager = (LocationManager) aty.getSystemService(serviceName);
	    
	    // 查找到服务信息 
	    Criteria c = new Criteria();  
		c.setAccuracy(Criteria.ACCURACY_LOW); //精度高  
		c.setPowerRequirement(Criteria.POWER_LOW); //电量消耗低  
		c.setAltitudeRequired(false); //不需要海拔  
		c.setSpeedRequired(false); //不需要速度  
		c.setCostAllowed(false); //不需要费用
	    String provider = locationManager.getBestProvider(c , true); // 获取GPS信息 
	    Location location = null;
	    if(null != provider)
	    	location = locationManager.getLastKnownLocation(provider); // 通过GPS获取位置 
	    return location;
	}
	
	/**
	 * 写本地缓存，使用SharedPreference
	 * @param ctx
	 * @param key
	 * @param value
	 */
	public static void saveLocalCache(Context ctx, String key, String value) {
		if (ctx == null) {
			return;
		}
		try {
			SharedPreferences sp = ctx.getSharedPreferences("LocalCache", Activity.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(key, value);
			editor.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 读取本地缓存，使用SharedPreference
	 * @param ctx
	 * @param key
	 * @return
	 */
	public static String getLocalCache(Context ctx, String key) {
		if (ctx == null) {
			return null;
		}
		try {
			SharedPreferences sp = ctx.getSharedPreferences("LocalCache", Activity.MODE_PRIVATE);
			return sp.getString(key, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 写本地缓存，使用SharedPreference
	 * @param ctx
	 * @param key
	 * @param value
	 */
	public static void saveCacheBoolean(Context ctx, String key, boolean value) {
		if (ctx == null) {
			return;
		}
		try {
			SharedPreferences sp = ctx.getSharedPreferences("LocalCache", Activity.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putBoolean(key, value);
			editor.commit();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 读取本地缓存，使用SharedPreference
	 * @param ctx
	 * @param key
	 * @return
	 */
	public static boolean getCacheBoolean(Context ctx, String key) {
		if (ctx == null) {
			return false;
		}
		try {
			SharedPreferences sp = ctx.getSharedPreferences("LocalCache", Activity.MODE_PRIVATE);
			return sp.getBoolean(key, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 清除本地缓存，使用SharedPreference
	 * @param ctx
	 * @param key
	 * @return
	 */
	public static void cleanLocalCache(Context ctx, String key) {
		if (ctx == null) {
			return;
		}
		try {
			SharedPreferences sp = ctx.getSharedPreferences("LocalCache", Activity.MODE_PRIVATE);
			Editor ed = sp.edit();
			ed.remove(key);
			ed.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取已保存的专长   找法网
	 * @param context
	 * @return
	 */
	public static String getPublicSkill(Context context){
		if(context == null) return "";
		SharedPreferences sp = context.getSharedPreferences("CACHESKILL", Activity.MODE_PRIVATE);
		String public_skill = sp.getString("public_skill", "");
		return public_skill;
	}
	
	/**
	 * 保存已选的专长   找法网
	 * @param context
	 * @param skill 格式：3,6,7,8   (逗号分隔)
	 */
	public static boolean savePublicSkill(Context context,String skill){
		if(context == null || skill == null || skill.trim().length() == 0) return false;
		SharedPreferences sp = context.getSharedPreferences("CACHESKILL", Activity.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString("public_skill", skill);
		return edit.commit();
	}
	
	/**
	 * 获取已保存的专长   法律快车
	 * @param context
	 * @return
	 */
	public static String getLT_PublicSkill(Context context){
		if(context == null) return "";
		SharedPreferences sp = context.getSharedPreferences("CACHESKILL", Activity.MODE_PRIVATE);
		String public_skill = sp.getString("lt_public_skill", "");
		return public_skill;
	}
	
	/**
	 * 保存已选的专长   法律快车
	 * @param context
	 * @param skill 格式：3,6,7,8   (逗号分隔)
	 */
	public static boolean saveLT_PublicSkill(Context context,String skill){
		if(context == null || skill == null || skill.trim().length() == 0) return false;
		SharedPreferences sp = context.getSharedPreferences("CACHESKILL", Activity.MODE_PRIVATE);
		Editor edit = sp.edit();
		edit.putString("lt_public_skill", skill);
		return edit.commit();
	}
	
	/**
	 * 判断是否有内存卡
	 */
	public static boolean hasSDCard() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}
	
	/**
	 * 获取api
	 */
	public static int getSdk_int(){
		return android.os.Build.VERSION.SDK_INT;
	}
	
	/**
	 * 判断gps是否开启
	 */
	public static boolean isGpsOpen(Context context){
		LocationManager locationManager  
        = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE); 
		// 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快） 
		boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		locationManager = null;
		return gps;
	}
	
	/**
     * 用来判断服务是否运行.
     * @param context
     * @param className 判断的服务名字
     * @return true 在运行 false 不在运行
     */
    public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
        		mContext.getSystemService(Context.ACTIVITY_SERVICE); 
        List<ActivityManager.RunningServiceInfo> serviceList 
        = activityManager.getRunningServices(100);
       if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0,l=serviceList.size(); i<l; i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }
}
