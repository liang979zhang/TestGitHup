package com.zdl.swipdelete;

import android.app.Fragment;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * Created by Administrator on 2015/12/14 0014.
 */
public class SwipeLayout extends FrameLayout {
    private View contentView, menuView;
    private int contentWidth, menuWidth, viewHeight;
    private Scroller scroller;

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
    }

    //得到视图
    @Override
    protected void onFinishInflate() {//加载布局完成获得子试图
        super.onFinishInflate();
        contentView = getChildAt(0);
        menuView = getChildAt(1);
    }

    //测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        contentWidth = contentView.getMeasuredWidth();
        menuWidth = menuView.getMeasuredWidth();
        viewHeight = contentView.getMeasuredHeight();
    }
    //对menuview布局

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        menuView.layout(contentWidth, 0, contentWidth + menuWidth, viewHeight);
    }


    private int lastX, lastY, downX, downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventX = (int) event.getRawX();
        int eventY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = eventX;
                downX = eventX;
                lastY = eventY;
                downY = eventY;

                break;
            case MotionEvent.ACTION_MOVE:
                int dx = eventX - lastX;


                int toScrollX = getScrollX() - dx;
                //toScrollX[0,menuWidth]
                if (toScrollX < 0) {
                    toScrollX = 0;
                } else if (toScrollX > menuWidth) {
                    toScrollX = menuWidth;
                }

                scrollTo(toScrollX, getScrollY());
                int totalDx = Math.abs(eventX - downX);
                int totalDy = Math.abs(eventY - downY);
                if (totalDx > 10 && totalDx > totalDy) {
                    //反拦截
                    getParent().requestDisallowInterceptTouchEvent(true);
                }


                lastX = eventX;
                lastY = eventY;
                break;
            case MotionEvent.ACTION_UP:
                int topScrollX = getScrollX();
                if (topScrollX < menuWidth / 2) {
                    close();
                } else {
                    open();
                }

                break;
        }

        return true;
    }

    //平滑的打开
    private void open() {
        scroller.startScroll(getScrollX(), getScrollY(), menuWidth - getScrollX(), 0);
        invalidate();

        if (onswipelistener != null) {
            onswipelistener.onOpen(this);
        }

    }

    //平滑的关闭
    public void close() {
        scroller.startScroll(getScrollX(), getScrollY(), -getScrollX(), 0);
        invalidate();
        if (onswipelistener != null) {
            onswipelistener.onClose(this);
        }
    }

    //scroller调用必须写的方法
    @Override
    public void computeScroll() {

        super.computeScroll();

        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }


    //拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercept = false;//默认不拦截
        int eventX = (int) ev.getRawX();
        int eventY = (int) ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = eventX;
                downX = eventX;
                lastY = eventY;
                downY = eventY;

                if (onswipelistener != null) {
                    onswipelistener.onDown(this);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                int totalDx = Math.abs(eventX - downX);
                if (totalDx > 10) {//拦截
                    intercept = true;
                }
                break;

        }

        return intercept;
    }

    //自定义拦截器
    private OnSwipeListener onswipelistener;

    public void setOnswipelistener(OnSwipeListener onswipelistener) {
        this.onswipelistener = onswipelistener;
    }

    interface OnSwipeListener {
        public void onOpen(SwipeLayout swipeLayout); //当打开时调用

        public void onClose(SwipeLayout swipeLayout); //当关闭时调用

        public void onDown(SwipeLayout swipeLayout);//当按下时调用

    }

}
