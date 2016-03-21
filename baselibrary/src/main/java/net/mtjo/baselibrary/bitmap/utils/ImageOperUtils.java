package com.wanyou.aframe.bitmap.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.widget.ImageView;

/**
 * 图片操作处理，如放大缩小，圆角等
 * @author Moyaofen
 */
public class ImageOperUtils {
	/**
	 * 获得圆角图片的方法
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */ 
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
		if(null == bitmap)return null;
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap 
	            .getHeight(), Config.ARGB_8888); 
	    Canvas canvas = new Canvas(output); 
	    final int color = 0xff424242; 
	    final Paint paint = new Paint(); 
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()); 
	    final RectF rectF = new RectF(rect); 
	    paint.setAntiAlias(true); 
	    canvas.drawARGB(0, 0, 0, 0); 
	    paint.setColor(color); 
	    canvas.drawRoundRect(rectF, roundPx, roundPx, paint); 
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN)); 
	    canvas.drawBitmap(bitmap, rect, rect, paint); 
	    return output; 
	}
	
	/**
     * 转换图片成圆形
     * 
     * @param bitmap
     *            传入Bitmap对象
     * @return
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap)
    {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height)
        {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        }
        else
        {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right,
                (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top,
                (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }
	
	/**
	 * 读取本地图片
	 */
	public static void getLocalImage(String path,ImageView img_view,int width,int height,boolean iscorner, float roundPx){
		try{
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inSampleSize = 1;
			opt.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opt);
			int bitmapSize = opt.outHeight * opt.outWidth * 4;
			opt.inSampleSize = bitmapSize / (1000 * 2000);
			opt.inJustDecodeBounds = false;
			Bitmap tBitmap = BitmapFactory.decodeFile(path, opt);
			
			int degree = readPictureDegree(path);
	        Bitmap image = rotaingImageView(degree, tBitmap);
	        Bitmap newimg = null;
	        
	        if(width != 0 && height != 0){
	        	newimg = MyThumbnailUtils.extractThumbnail(image,width, height,
						ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
	        }else{
	        	newimg = image;
	        }
	        
	        Bitmap eimg = null;
	        if(null != newimg)
	        if(iscorner){
	        	eimg  = getRoundedCornerBitmap(newimg, roundPx);
	        }else{
	        	eimg = newimg;
	        }
	        if(null != img_view && null != eimg)
	        	img_view.setImageBitmap(eimg);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * 读取图片属性：旋转的角度
	 * @param path 图片绝对路径
	 * @return degree旋转的角度
	 */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
                ExifInterface exifInterface = new ExifInterface(path);
                int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }
        } catch (IOException e) {
                e.printStackTrace();
        }
        return degree;
    }
    
    /*
     * 旋转图片 
     * @param angle 
     * @param bitmap 
     * @return Bitmap 
     */  
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
        //旋转图片 动作   
        Matrix matrix = new Matrix();;  
        matrix.postRotate(angle);  
        // 创建新的图片   
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
        return resizedBitmap;  
    }
    
    /**
	 * 压缩图片上传
	 */
	public static byte[] compressBmpFromBmp(String filePath,long maxsize) {
         final BitmapFactory.Options options = new BitmapFactory.Options();
         options.inJustDecodeBounds = true;
         BitmapFactory.decodeFile(filePath, options);
         options.inSampleSize = calculateInSampleSize(options, 480, 800);
         options.inJustDecodeBounds = false;
         Bitmap temp_image = BitmapFactory.decodeFile(filePath, options);
         
         int degree = readPictureDegree(filePath);
         Bitmap image = rotaingImageView(degree, temp_image); 
         
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        int compress = 100;  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
        while (baos.toByteArray().length > maxsize) {   
            baos.reset();  
            compress -= 10;  
            image.compress(Bitmap.CompressFormat.JPEG, compress, baos);  
        }
        return baos.toByteArray(); 
    }
	
	//计算图片的缩放值
		public static int calculateInSampleSize(BitmapFactory.Options options,int reqWidth, int reqHeight) {
		    final int height = options.outHeight;
		    final int width = options.outWidth;
		    int inSampleSize = 1;

		    if (height > reqHeight || width > reqWidth) {
		             final double heightRatio = Math.ceil((float) height/ (float) reqHeight);
		             final double widthRatio = Math.ceil((float) width / (float) reqWidth);
		             inSampleSize = (int) Math.ceil(Math.max(heightRatio, widthRatio));
		    }
		        return inSampleSize;
		}
}
