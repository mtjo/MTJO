package com.wanyou.aframe.ui.widget.filtermenu;

import java.util.ArrayList;

import com.wanyou.aframe.utils.AppUtils;
import com.wanyou.wylibrary.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;
import android.widget.PopupWindow.OnDismissListener;

public class FilterMenuView extends LinearLayout implements OnDismissListener{
	
	private int displayWidth;
	private int displayHeight;
	private int selectPosition;
	
	private Context mContext;
	private PopupWindow popupWindow;
	private ToggleButton selectedButton;
	
	private ArrayList<String> mTextArray = new ArrayList<String>();
	private ArrayList<View> mViewArray = new ArrayList<View>();
	private ArrayList<ToggleButton> mToggleButton = new ArrayList<ToggleButton>();
	
	private OnButtonClickListener mOnButtonClickListener;

	/**
	 * 自定义tabitem点击回调接口
	 */
	public interface OnButtonClickListener {
		public void onClick(int selectPosition);
	}
	
	public FilterMenuView(Context context) {
		super(context);
		init(context);
	}
	
	public FilterMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public FilterMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}
	
	@SuppressLint("NewApi")
	public FilterMenuView(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context);
	}

	@Override
	public void onDismiss() {
		showPopup(selectPosition);
		popupWindow.setOnDismissListener(null);
	}
	
	/**
	 * 设置显示菜单及其页面
	 */
	public void setMenuArray(ArrayList<String> textArray, ArrayList<View> viewArray){
		if (mContext == null) {
			return;
		}
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mTextArray = textArray;
		for (int i = 0; i < viewArray.size(); i++) {
			final RelativeLayout r = new RelativeLayout(mContext);
			int maxHeight = (int) (displayHeight * 0.6);
			if(null != viewArray.get(i)){
				RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, maxHeight);
				r.addView(viewArray.get(i), rl);
				mViewArray.add(r);
				
				r.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onPressBack();
					}
				});
//				r.setBackgroundColor(mContext.getResources().getColor(R.color.popup_main_background));
			} else {
				mViewArray.add(null);
			}
			
			ToggleButton tButton = (ToggleButton) inflater.inflate(R.layout.filter_menu_btn, this, false);
			addView(tButton);
//			View line = new TextView(mContext);
//			line.setBackgroundResource(R.drawable.choosebar_line);
//			if (i < viewArray.size() - 1) {
//				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.MATCH_PARENT);
//				addView(line, lp);
//			}
			mToggleButton.add(tButton);
			tButton.setTag(i);
			tButton.setText(mTextArray.get(i));
			
			tButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					// initPopupWindow();
					ToggleButton tButton = (ToggleButton) view;

					if (selectedButton != null && selectedButton != tButton) {
						selectedButton.setChecked(false);
					}
					selectedButton = tButton;
					selectPosition = (Integer) selectedButton.getTag();
					startAnimation();
					if (mOnButtonClickListener != null && tButton.isChecked()) {
						mOnButtonClickListener.onClick(selectPosition);
					}
				}
			});
		}
	}
	
	/**
	 * 根据选择的位置设置tabitem显示的值
	 */
	public void setTitle(String valueText, int position) {
		if (position < mToggleButton.size()) {
			mToggleButton.get(position).setText(valueText);
		}
	}
	
	/**
	 * 根据选择的位置设置tabitem显示的值
	 */
	public void setTitleEnable(boolean enabled, int position) {
		if (position < mToggleButton.size()) {
			mToggleButton.get(position).setEnabled(enabled);
		}
	}
	
	/**
	 * 如果菜单成展开状态，则让菜单收回去
	 */
	public boolean onPressBack() {
		if (popupWindow != null && popupWindow.isShowing()) {
			popupWindow.dismiss();
			hideView();
			if (selectedButton != null) {
				selectedButton.setChecked(false);
			}
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 设置tabitem的点击监听事件
	 */
	public void setOnButtonClickListener(OnButtonClickListener l) {
		mOnButtonClickListener = l;
	}
	
	private void init(Context context){
		mContext = context;
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay(); 
		if(AppUtils.getSdk_int() < 13){
			displayWidth = display.getWidth();
			displayHeight = display.getHeight();
		} else {
			Point point = new Point();
			display.getSize(point);
			displayWidth = point.x;
			displayHeight = point.y;
		}
		wm = null;
		display = null;
		
		setOrientation(LinearLayout.HORIZONTAL);
	}
	
	private void startAnimation() {
		if (popupWindow == null) {
			popupWindow = new PopupWindow(mViewArray.get(selectPosition), displayWidth, displayHeight);
			popupWindow.setAnimationStyle(R.style.FilterMenuPopupAnimation);
			popupWindow.setFocusable(false);
			popupWindow.setOutsideTouchable(true);
			ColorDrawable bgdraw = new ColorDrawable(
					mContext.getResources().getColor(R.color.popup_main_background));
			popupWindow.setBackgroundDrawable(bgdraw);
		}

		if (selectedButton.isChecked()) {
			if (!popupWindow.isShowing()) {
				showPopup(selectPosition);
			} else {
				popupWindow.setOnDismissListener(this);
				popupWindow.dismiss();
				hideView();
			}
		} else {
			if (popupWindow.isShowing()) {
				popupWindow.dismiss();
				hideView();
			}
		}
	}

	private void showPopup(int position) {
//		View tView = mViewArray.get(selectPosition).getChildAt(0);
//		if (tView instanceof ViewBaseAction) {
//			ViewBaseAction f = (ViewBaseAction) tView;
//			f.showMenu();
//		}
		if(null == mViewArray.get(position))return;
		if (popupWindow.getContentView() != mViewArray.get(position)) {
			popupWindow.setContentView(mViewArray.get(position));
		}
		popupWindow.showAsDropDown(this, 0, 0);
	}

	private void hideView() {
//		View tView = mViewArray.get(selectPosition).getChildAt(0);
//		if (tView instanceof ViewBaseAction) {
//			ViewBaseAction f = (ViewBaseAction) tView;
//			f.hideMenu();
//		}
	}
}
