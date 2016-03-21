package com.wanyou.aframe.selectimg;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class ImageService {
	private Context context;

	public ImageService(Context context) {
		this.context = context;
	}

	public LinkedHashMap<String, List<String>> getImages() {
		
		LinkedHashMap<String, List<String>> mGruopMap = new LinkedHashMap<String, List<String>>();
		
		Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		
		ContentResolver contentResolver = context.getContentResolver();
		
		String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";		// 排序
		
		Cursor cursor = contentResolver.query(uri, null,MediaStore.Images.Media.MIME_TYPE + "=? or "+ MediaStore.Images.Media.MIME_TYPE + "=?",new String[] { "image/jpeg", "image/png" },sortOrder );
				
		if (cursor != null) {
			while (cursor.moveToNext()) {
				// 获取图片的路径
				String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
				// 获取该图片的父路径名
				String parentName = new File(path).getParentFile().getName();
				// 根据父路径名将图片放入到mGruopMap中
				if (!mGruopMap.containsKey(parentName)) {
					List<String> chileList = new ArrayList<String>();
					chileList.add(path);
					mGruopMap.put(parentName, chileList);
				} else {
					mGruopMap.get(parentName).add(path);
				}
			}
			cursor.close();
		}
		return mGruopMap;
	}
}