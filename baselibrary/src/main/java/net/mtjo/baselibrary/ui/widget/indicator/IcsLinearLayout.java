package com.wanyou.aframe.ui.widget.indicator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

/**
 * A simple extension of a regular linear layout that supports the divider API
 * of Android 4.0+. The dividers are added adjacent to the children by changing
 * their layout params. If you need to rely on the margins which fall in the
 * same orientation as the layout you should wrap the child in a simple
 * {@link android.widget.FrameLayout} so it can receive the margin.
 */
class IcsLinearLayout extends LinearLayout {
    private static final int[] LL = new int[] {
        /* 0 */ android.R.attr.divider,
        /* 1 */ android.R.attr.showDividers,
        /* 2 */ android.R.attr.dividerPadding,
    };
    private static final int LL_DIVIDER = 0;
    private static final int LL_SHOW_DIVIDER = 1;
    private static final int LL_DIVIDER_PADDING = 2;

    private Drawable mDivider;
    private int mDividerWidth;
    private int mDividerHeight;
    private int mShowDividers;
    private int mDividerPadding;
    
    private Paint paint;
    private RectF curRectF;
	private RectF tarRectF;


    public IcsLinearLayout(Context context, int themeAttr) {
        super(context);

        paint = new Paint();
		paint.setAntiAlias(true);
		
        TypedArray a = context.obtainStyledAttributes(null, LL, themeAttr, 0);
        setDividerDrawable(a.getDrawable(IcsLinearLayout.LL_DIVIDER));
        mDividerPadding = a.getDimensionPixelSize(LL_DIVIDER_PADDING, 0);
        mShowDividers = a.getInteger(LL_SHOW_DIVIDER, SHOW_DIVIDER_NONE);
        a.recycle();
        
        curRectF = null;
		tarRectF = null;
    }

    public void setDividerDrawable(Drawable divider) {
        if (divider == mDivider) {
            return;
        }
        mDivider = divider;
        if (divider != null) {
            mDividerWidth = divider.getIntrinsicWidth();
            mDividerHeight = divider.getIntrinsicHeight();
        } else {
            mDividerWidth = 0;
            mDividerHeight = 0;
        }
        setWillNotDraw(divider == null);
        requestLayout();
    }

    @Override
    protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        final int index = indexOfChild(child);
        final int orientation = getOrientation();
        final LayoutParams params = (LayoutParams) child.getLayoutParams();
        if (hasDividerBeforeChildAt(index)) {
            if (orientation == VERTICAL) {
                //Account for the divider by pushing everything up
                params.topMargin = mDividerHeight;
            } else {
                //Account for the divider by pushing everything left
                params.leftMargin = mDividerWidth;
            }
        }

        final int count = getChildCount();
        if (index == count - 1) {
            if (hasDividerBeforeChildAt(count)) {
                if (orientation == VERTICAL) {
                    params.bottomMargin = mDividerHeight;
                } else {
                    params.rightMargin = mDividerWidth;
                }
            }
        }
        super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed, parentHeightMeasureSpec, heightUsed);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mDivider != null) {
            if (getOrientation() == VERTICAL) {
                drawDividersVertical(canvas);
            } else {
                drawDividersHorizontal(canvas);
            }
        }
        
        int step = getWidth()/30;
		canvas.drawColor(Color.BLACK);
		paint.setColor(Color.LTGRAY);
		
		if (curRectF == null)
			curRectF = new RectF(this.getChildAt(0).getLeft()+10, this.getChildAt(0).getTop()-2,
					this.getChildAt(0).getRight()-10, this.getChildAt(0).getBottom()+2);
		if (tarRectF == null)
			tarRectF = new RectF(this.getChildAt(0).getLeft()+10, this.getChildAt(0).getTop()-2, 
					this.getChildAt(0).getRight()-10, this.getChildAt(0).getBottom()+2);
		
		if (Math.abs(curRectF.left - tarRectF.left) < step) {
			curRectF.left = tarRectF.left;
			curRectF.right = tarRectF.right;
		}
		if (curRectF.left > tarRectF.left) {
			curRectF.left -= step;
			curRectF.right -= step;
			invalidate();
		}
		else if (curRectF.left < tarRectF.left) {
			curRectF.left += step;
			curRectF.right += step;
			invalidate();
		}
		canvas.drawRoundRect(curRectF, 5, 5, paint);
		
        super.onDraw(canvas);
    }

    private void drawDividersVertical(Canvas canvas) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child != null && child.getVisibility() != GONE) {
                if (hasDividerBeforeChildAt(i)) {
                    final android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) child.getLayoutParams();
                    final int top = child.getTop() - lp.topMargin/* - mDividerHeight*/;
                    drawHorizontalDivider(canvas, top);
                }
            }
        }

        if (hasDividerBeforeChildAt(count)) {
            final View child = getChildAt(count - 1);
            int bottom = 0;
            if (child == null) {
                bottom = getHeight() - getPaddingBottom() - mDividerHeight;
            } else {
                //final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                bottom = child.getBottom()/* + lp.bottomMargin*/;
            }
            drawHorizontalDivider(canvas, bottom);
        }
    }

    private void drawDividersHorizontal(Canvas canvas) {
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child != null && child.getVisibility() != GONE) {
                if (hasDividerBeforeChildAt(i)) {
                    final android.widget.LinearLayout.LayoutParams lp = (android.widget.LinearLayout.LayoutParams) child.getLayoutParams();
                    final int left = child.getLeft() - lp.leftMargin/* - mDividerWidth*/;
                    drawVerticalDivider(canvas, left);
                }
            }
        }

        if (hasDividerBeforeChildAt(count)) {
            final View child = getChildAt(count - 1);
            int right = 0;
            if (child == null) {
                right = getWidth() - getPaddingRight() - mDividerWidth;
            } else {
                //final LayoutParams lp = (LayoutParams) child.getLayoutParams();
                right = child.getRight()/* + lp.rightMargin*/;
            }
            drawVerticalDivider(canvas, right);
        }
    }

    private void drawHorizontalDivider(Canvas canvas, int top) {
        mDivider.setBounds(getPaddingLeft() + mDividerPadding, top,
                getWidth() - getPaddingRight() - mDividerPadding, top + mDividerHeight);
        mDivider.draw(canvas);
    }

    private void drawVerticalDivider(Canvas canvas, int left) {
        mDivider.setBounds(left, getPaddingTop() + mDividerPadding,
                left + mDividerWidth, getHeight() - getPaddingBottom() - mDividerPadding);
        mDivider.draw(canvas);
    }

    private boolean hasDividerBeforeChildAt(int childIndex) {
        if (childIndex == 0 || childIndex == getChildCount()) {
          return false;
        }
        if ((mShowDividers & SHOW_DIVIDER_MIDDLE) != 0) {
            boolean hasVisibleViewBefore = false;
            for (int i = childIndex - 1; i >= 0; i--) {
                if (getChildAt(i).getVisibility() != GONE) {
                    hasVisibleViewBefore = true;
                    break;
                }
            }
            return hasVisibleViewBefore;
        }
        return false;
    }
}