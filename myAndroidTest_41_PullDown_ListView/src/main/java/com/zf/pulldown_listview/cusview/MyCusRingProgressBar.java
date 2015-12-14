package com.zf.pulldown_listview.cusview;

import com.zf.pulldown_listview.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * �Զ��廷�ν�����
 * @author 
 * @Date 2014-4-28
 * @ClassInfo com.zf.pulldown_listview.cusview-MyCusRingProgressBar.java
 * @Description
 */
public class MyCusRingProgressBar extends View {
	/** Բ������ */
	private Paint ringPaint;
	/** Բ���뾶 */
	private int ringRadius;
	/** Բ����� */
	private int strokeWidth = 2;
	/** �ܽ��� */
	private int tolProgress = 100;
	/** ��ǰ���� */
	private int curProgress;
	/** ������Բ������ */
	private RectF oval;
	/** Բ������ */
	private int center;
	/** ��ת���� */
	private Animation cycleAnim;

	public MyCusRingProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		// ��ȡ��ת����
		cycleAnim = AnimationUtils.loadAnimation(context,
				R.anim.cus_progress_rotate);
		// ����Բ������
		ringPaint = new Paint();
		ringPaint.setStyle(Paint.Style.STROKE);
		ringPaint.setStrokeWidth(strokeWidth);
		ringPaint.setAntiAlias(true);
		ringPaint.setColor(getResources().getColor(R.color.red));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		center = getWidth() / 2;
		ringRadius = center - strokeWidth / 2;

		if (curProgress > 0) {
			if (oval == null) {
				oval = new RectF();
				oval.left = center - ringRadius;
				oval.top = center - ringRadius;
				oval.right = center + ringRadius;
				oval.bottom = center + ringRadius;
			}
			// ��0�ȿ�ʼ�������ݵ�ǰ����ռ�ܽ��ȵİٷֱ������Ƕ�����
			canvas.drawArc(oval, 0, ((float) curProgress / tolProgress) * 360,
					false, ringPaint);
		}
	}

	/**���õ�ǰ����*/
	public void setProgress(int progress) {
		curProgress = progress;
		postInvalidate();
	}

	/**
	 * ��ʼ��ת����
	 * <ul>
	 * <strong>��XML������layout_width��layout_height��ֵʱ����ת���ܴ������⣬δ�����</strong>
	 * </ul>
	 */
	public void startCycleAnim() {
		this.startAnimation(cycleAnim);
	}

	/**ֹͣ��ת����*/
	public void stopCycleAnim() {
		this.clearAnimation();
	}
}
