package com.ncu.tianyan;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

/**
 * 侧滑菜单
 * Created by BeanYong on 2015/8/10.
 */
public class SlidingMenu extends HorizontalScrollView {

    private final int STATE_CLOSE = 0;
    private final int STATE_OPEN = 1;

    /**
     * 菜单的当前状态
     */
    private int mCurrentStatus = STATE_CLOSE;

    /**
     * 当前屏幕宽度
     */
    private int mScreenWidth = 0;

    /**
     * 菜单的宽度
     */
    private int mMenuWidth = 0;

    /**
     * 菜单打开后距离屏幕右侧的距离
     */
    private int mRightMargin = 0;

    public SlidingMenu(Context context) {
        this(context, null);
    }

    public SlidingMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingMenu, 0, defStyle);
        mRightMargin = (int) ta.getDimension(R.styleable.SlidingMenu_right_margin,
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                        getResources().getDisplayMetrics()));
        ta.recycle();

        //获取屏幕宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ViewGroup root = (ViewGroup) getChildAt(0);
        ViewGroup menu = (ViewGroup) root.getChildAt(0);
        ViewGroup content = (ViewGroup) root.getChildAt(1);

        mMenuWidth = mScreenWidth - mRightMargin;
        menu.getLayoutParams().width = mMenuWidth;
        content.getLayoutParams().width = mScreenWidth;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            scrollTo(mMenuWidth, 0);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
                int x = getScrollX();
                if (mCurrentStatus == STATE_OPEN && x > 0.3 * mMenuWidth) {//关闭菜单
                    closeMenu();
                } else if (mCurrentStatus == STATE_CLOSE && x < 0.7 * mMenuWidth) {//打开菜单
                    openMenu();
                } else {
                    recoverMenu();
                }
                return true;
        }

        return super.onTouchEvent(ev);
    }

    /**
     * 关闭菜单
     */
    private void closeMenu() {
        smoothScrollTo(mMenuWidth, 0);
        mCurrentStatus = STATE_CLOSE;
    }

    /**
     * 打开菜单
     */
    private void openMenu() {
        smoothScrollTo(0, 0);
        mCurrentStatus = STATE_OPEN;
    }

    /**
     * 改变菜单状态
     */
    public void toggleMenu() {
        if (mCurrentStatus == STATE_CLOSE) {
            openMenu();
        } else {
            closeMenu();
        }
    }

    /**
     * 恢复到菜单当前所处的状态
     */
    public void recoverMenu() {
        if (mCurrentStatus == STATE_OPEN) {
            smoothScrollTo(0, 0);
        } else {
            smoothScrollTo(mMenuWidth, 0);
        }
    }
}
