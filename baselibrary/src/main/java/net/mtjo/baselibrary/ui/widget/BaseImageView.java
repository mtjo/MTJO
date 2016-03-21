package com.wanyou.aframe.ui.widget;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class BaseImageView extends ImageView {
    private static final String TAG = BaseImageView.class.getSimpleName();

    protected Context mContext;

    private static final Xfermode sXfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
    private Bitmap mMaskBitmap;
    private Paint mPaint;
    private WeakReference<Bitmap> mWeakBitmap;
    
    private int MASK_ID = 0;
    private int MASK_PRESS_ID = 0;

    public BaseImageView(Context context) {
        super(context);
        sharedConstructor(context);
    }

    public BaseImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        sharedConstructor(context);
    }

    public BaseImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        sharedConstructor(context);
    }

    private void sharedConstructor(Context context) {
        mContext = context;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public void invalidate() {
        mWeakBitmap = null;
        super.invalidate();
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if (!isInEditMode()) {
            int i = canvas.saveLayer(0.0f, 0.0f, getWidth(), getHeight(),
                    null, Canvas.ALL_SAVE_FLAG);
            try {
                Bitmap bitmap = mWeakBitmap != null ? mWeakBitmap.get() : null;
                // Bitmap not loaded.
                if (bitmap == null || bitmap.isRecycled()) {
                    Drawable drawable = getDrawable();
                    if (drawable != null) {
                        // Allocation onDraw but it's ok because it will not always be called.
                        bitmap = Bitmap.createBitmap(getWidth(),
                                getHeight(), Bitmap.Config.ARGB_8888);
                    	
                        Canvas bitmapCanvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, getWidth(), getHeight());
                        drawable.draw(bitmapCanvas);

                        mMaskBitmap = getBitmap();

                        // Draw Bitmap.
                        mPaint.reset();
                        mPaint.setFilterBitmap(false);
                        mPaint.setXfermode(sXfermode);
                        
                        bitmapCanvas.drawBitmap(mMaskBitmap, 0.0f, 0.0f, mPaint);
                        mWeakBitmap = new WeakReference<Bitmap>(bitmap);
                    }
                }

                // Bitmap already loaded.
                if (bitmap != null) {
                    mPaint.setXfermode(null);
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, mPaint);
                    
                    //点击效果
                    if (isPressed()){
                    	Bitmap bgmap = getBackgroud();
                    	canvas.drawBitmap(bgmap, 0.0f, 0.0f, mPaint);
                    }
                    return;
                }
                
	             
            
            } catch (Exception e) {
                System.gc();

                Log.e(TAG, String.format("Failed to draw, Id :: %s. Error occurred :: %s", getId(), e.toString()));
            } finally {
                canvas.restoreToCount(i);
            }
        } else {
            super.onDraw(canvas);
        }
    }

    public Bitmap getBitmap(){
    	Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        if(MASK_ID > 0){
        	Bitmap tmap = BitmapFactory.decodeResource(getResources(), MASK_ID);
            NinePatch np = new NinePatch(tmap, tmap.getNinePatchChunk(), null);
            RectF rectf = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
            //9妹png放大
            np.draw(canvas, rectf);
        }
        
        return bitmap;
    }
    
    public Bitmap getBackgroud(){
    	Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        
        if(MASK_PRESS_ID > 0){
        	Bitmap tmap = BitmapFactory.decodeResource(getResources(), MASK_PRESS_ID);
            NinePatch np = new NinePatch(tmap, tmap.getNinePatchChunk(), null);
            RectF rectf = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
            //9妹png放大
            np.draw(canvas, rectf);
        }
        return bitmap;
    }
    
    @Override
	protected void dispatchSetPressed(boolean pressed) {
		super.dispatchSetPressed(pressed);
		invalidate();
	}

	public void setMASK_ID(int mASK_ID) {
		MASK_ID = mASK_ID;
	}

	public void setMASK_PRESS_ID(int mASK_PRESS_ID) {
		MASK_PRESS_ID = mASK_PRESS_ID;
	}
}
