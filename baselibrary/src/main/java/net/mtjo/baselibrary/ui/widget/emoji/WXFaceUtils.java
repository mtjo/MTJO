package com.wanyou.aframe.ui.widget.emoji;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;

import com.wanyou.wylibrary.R;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class WXFaceUtils {
	public static List<String> mEmoticons = new ArrayList<String>();
    public static Map<String, Integer> mEmoticonsId = new LinkedHashMap<String,Integer>();
    public static Map<String, Integer> mEmoticons_CN_Id = new LinkedHashMap<String, Integer>();
    public static List<String> mEmoticons_CN = new ArrayList<String>();
    public static Map<String, SoftReference<Bitmap>> mEmoticonsChche = new HashMap<String, SoftReference<Bitmap>>();
	
    public static void init(Context context){
    	mEmoticonsId = parser(R.xml.face, context);
    	mEmoticons_CN_Id = parser(R.xml.face_cn, context);
    }
    
	public static LinkedHashMap<String, Integer> parser(int xml, Context context) {
        XmlResourceParser parser = context.getResources().getXml(xml);
        LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
        try {
            int event = parser.getEventType();
            String key = "";
            while(event != XmlResourceParser.END_DOCUMENT){
            	switch(event){
	            	case XmlPullParser.START_TAG:
	            		if("key".equals(parser.getName())) {
	                        key = parser.nextText();
	                    } else if("string".equals(parser.getName())) {
	                        String id = new StringBuilder("smiley_").
	                        		append(parser.nextText()).toString();
	                        int emoticonsId = context.getResources().getIdentifier(id, "drawable", context.getPackageName());
	                        SoftReference<Bitmap> reference = new SoftReference<Bitmap>(BitmapFactory.decodeResource(context.getResources(), emoticonsId));
	                        mEmoticonsChche.put(key, reference);
	                        map.put(key, Integer.valueOf(emoticonsId));
	                    }
	            		break;
            	}
            	event = parser.next();
            }
            parser.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return map;
    }
	
	public static void destory(){
		mEmoticons.clear();
		mEmoticonsId.clear();
		mEmoticons_CN_Id.clear();
		mEmoticons_CN.clear();
		mEmoticonsChche.clear();
	}
}
