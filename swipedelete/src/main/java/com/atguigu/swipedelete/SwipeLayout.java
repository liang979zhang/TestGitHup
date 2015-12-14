package com.atguigu.swipedelete;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

/**
 * 1. 正常的初始化显示
 * 1). 得到子View对象:  onFinishInflate()
 * 2). 得到子View的宽高: onMeasure()
 * 3). 对子View进行重新布局: onLayout()
 * 2. 滑动打开/关闭
 * 2.1). 响应用户的操作: onTouchEvent(), 返回true
 * 2.2). 在move中, 计算事件的偏移量, 来对View进行对应的滚动
 * 2.3). 在up中, 根据总的偏移量, 来确定是打开/关闭
 * 2.4). 实现平滑的打开/关闭的效果
 * 3. 解决当前视图与父视图(ListView)的事件冲突
 * 解决办法: 反拦截
 * 3.1). 计算反拦截的条件: 在onTouchEvent中的move
 * totalDx>totalDy
 * totalDx>10
 * 3.2). 一旦满足条件, 反拦截 :
 * 4. 解决当前视图与子视图的事件冲突
 * 解决办法: 拦截
 * 4.1). 重写onIntercept(), 不能直接返回true
 * 4.2). 在down/move记录一下事件坐标
 * 4.3). 在move中计算拦截的条件
 * totalDx>10
 * 4.4). 如果满足了条件, 返回true
 * <p/>
 * 5. 自定义监听器实现限制只能打开一个
 */
public class SwipeLayout extends FrameLayout {

    private View contentView, menuView;
    private int contentWidth, menuWidth, viewHeight;
    private Scroller scroller;

    public SwipeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        scroller = new Scroller(context);
    }

    //1.1). 得到子View对象:  onFinishInflate()
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contentView = getChildAt(0);
        menuView = getChildAt(1);
    }

    //1.2). 得到子View的宽高: onMeasure()
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        contentWidth = contentView.getMeasuredWidth();
        menuWidth = menuView.getMeasuredWidth();
        viewHeight = getMeasuredHeight();
    }

    //1.3). 对子View进行重新布局: onLayout()
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //对menu子View进行重新布局
        menuView.layout(contentWidth, 0, contentWidth + menuWidth, viewHeight);
    }

    /*
     *  2.1). 响应用户的操作: onTouchEvent(), 返回true
     *  2.2). 在move中, 计算事件的偏移量, 来对View进行对应的滚动
     *  2.3). 在up中, 根据总的偏移量, 来确定是打开/关闭
     *  2.4). 实现平滑的打开/关闭的效果
     */

    private int lastX;
    private int downX, downY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //return super.onTouchEvent(event);
        int eventX = (int) event.getRawX();
        int eventY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = eventX;
                downX = eventX;
                downY = eventY;

                break;
            case MotionEvent.ACTION_MOVE:
                int dx = eventX - lastX;
                //滚动视图
                int toScrollX = getScrollX() - dx;
                //toScrollX的范围[0,menuWidth]
                if (toScrollX < 0) {
                    toScrollX = 0;
                } else if (toScrollX > menuWidth) {
                    toScrollX = menuWidth;
                }
                scrollTo(toScrollX, 0);
                lastX = eventX;

                int totalDx = Math.abs(eventX - downX);
                int totalDy = Math.abs(eventY - downY);
                if (totalDx > totalDy && totalDx > 10) {//满足条件了
                    //反拦截
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
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

    /**
     * 平滑的打开
     */
    public void open() {//--->(menuwidth,0)
        scroller.startScroll(getScrollX(), getScrollY(), menuWidth - getScrollX(), 0);
        invalidate();

        if (onSwipeListener != null) {
            onSwipeListener.onOpen(this);
        }
    }

    /**
     * 平滑的关闭
     */
    public void close() {//--->(0,0)
        scroller.startScroll(getScrollX(), getScrollY(), -getScrollX(), 0);
        invalidate();

        if (onSwipeListener != null) {
            onSwipeListener.onClose(this);
        }
    }

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
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercept = false;//默认不拦截
        int eventX = (int) event.getRawX();
        int eventY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = eventX;
                downX = eventX;
                downY = eventY;

                if (onSwipeListener != null) {
                    onSwipeListener.onDown(this);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                int totalDx = Math.abs(eventX - downX);
                if (totalDx > 10) {//满足条件了
                    //拦截
                    intercept = true;
                }
                break;
        }

        return intercept;
    }

    //自定义监听器
    private OnSwipeListener onSwipeListener;

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        this.onSwipeListener = onSwipeListener;
    }

    interface OnSwipeListener {
        public void onOpen(SwipeLayout swipeLayout); //当打开时调用

        public void onClose(SwipeLayout swipeLayout); //当关闭时调用

        public void onDown(SwipeLayout swipeLayout);//当按下时调用
    }
}
