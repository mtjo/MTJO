package com.wanyou.aframe.ui.widget;

import com.wanyou.wylibrary.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

@SuppressLint("NewApi")
public class CusTextView extends TextView {
	private Context mContext;
	private PopupWindow mPopupWindow; 
	
	 //长按的runnable  
    private Runnable mLongPressRunnable; 
    
    private int mLastMotionX, mLastMotionY;  
    private boolean isMoved; 
    //移动的阈值  
    private static final int TOUCH_SLOP = 10; 

	public CusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		
		mLongPressRunnable = new Runnable() {  
            @Override  
            public void run() { 
                performLongClick(); // 执行长按事件（如果有定义的话）  
            }  
        };
	}
	
	@Override
	public boolean performLongClick() {
		try{
			getPopupWindowsInstance(0,0);
			mPopupWindow.showAsDropDown(this,getWidth()/2-50,
					0-(getHeight()+60));
		}catch(Exception e){
			e.printStackTrace();
		}
		return super.performLongClick();
	}
	
	public boolean dispatchTouchEvent(MotionEvent event) {  
        int x = (int) event.getX();  
        int y = (int) event.getY();  
          
        switch(event.getAction()) {  
        case MotionEvent.ACTION_DOWN:  
              
            mLastMotionX = x;  
            mLastMotionY = y;  
            isMoved = false;  
            /* 
             * 将mLongPressRunnable放进任务队列中，到达设定时间后开始执行 
             * 这里的长按时间采用系统标准长按时间 
             */  
            postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());  
            break;  
        case MotionEvent.ACTION_MOVE:  
              
            if( isMoved ) break;  
              
            if( Math.abs(mLastMotionX-x) > TOUCH_SLOP   
                    || Math.abs(mLastMotionY-y) > TOUCH_SLOP ) {  
                //移动超过阈值，则表示移动了  
                isMoved = true;  
                removeCallbacks(mLongPressRunnable);  
            }  
            break;  
        case MotionEvent.ACTION_UP:  
            //释放了  
            removeCallbacks(mLongPressRunnable);  
            break;  
        }  
        return true;  
    }

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			getPopupWindowsInstance(event.getX(),event.getY());
//			mPopupWindow.showAsDropDown(this,getWidth()/2-50,
//					0-(getHeight()+60));	
//			break;
//		}
//		return super.onTouchEvent(event);
//	}
    /**
     * 销毁 PopupWindow
     */
    private void dismissPopupWindowInstance(){
    	  if (null != mPopupWindow) { 
              mPopupWindow.dismiss(); 
    	  }
    }
    /**
     * 获得 PopupWindow 实例
     */
    private void getPopupWindowsInstance(float x, float y){
    	if(mPopupWindow!=null){
    		mPopupWindow.dismiss();
    	}else{
    		initPopuptWindow(x, y);
    	}
    }
    
    static final int ver = Build.VERSION.SDK_INT;
	/*
     * 创建PopupWindow
     */ 
    private void initPopuptWindow(float x, float y) { 
        LayoutInflater layoutInflater = LayoutInflater.from(getContext()); 
        View popupWindow = layoutInflater.inflate(R.layout.popup_window, null); 
        Button btnCopy = (Button) popupWindow.findViewById(R.id.btnCopy); 
        btnCopy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismissPopupWindowInstance();
				
				if(ver<11){
					android.text.ClipboardManager cmb = (android.text.ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);  
					cmb.setText(getText());  
				}else{
					android.content.ClipboardManager cmb = (android.content.ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);  
					cmb.setText(getText()); 
				}
			}
		});
        //  此处 之所以  给了 PopupWindow  一个 固定的宽度  是因为 我要让 PopupWindow 的中心位置对齐  TextView的中心位置
        //  一开始 写成了ViewGroup.LayoutParams.WRAP_CONTENT  但是  各种尝试之后 我没办法 得到 PopupWindow 的宽度 如果你能获得的话 麻烦留言 告诉我 
		mPopupWindow = new PopupWindow(popupWindow, ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);//dipTopx(mContext, 50)
		// 这行代码 很重要
		mPopupWindow.setBackgroundDrawable(getDrawable());
		mPopupWindow.setOutsideTouchable(true);
    } 
    /**
     * 生成一个 透明的背景图片
     * @return
     */
    private Drawable getDrawable(){
    	ShapeDrawable bgdrawable =new ShapeDrawable(new OvalShape());
    	bgdrawable.getPaint().setColor(getResources().getColor(android.R.color.transparent));
    	return   bgdrawable;
    }
}