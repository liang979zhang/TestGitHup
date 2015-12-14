package com.zf.pulldown_listview.cusview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.zf.pulldown_listview.R;

/**
 * �Զ�������ˢ��ListView���Զ�����������лص�����
 * @author 
 * @Date 2014-4-28
 * @ClassInfo com.zf.pulldown_listview.cusview-MyCusListView3.java
 * @Description
 */
public class MyCusListView3 extends ListView implements OnScrollListener {
	private static final String TAG = "MyCusListView===>";
	
	/** ����״̬:�����տ�ʼ�����˵�����һ��ˢ�½��� */
	private static final int DONE = 0x1;
	/** ����״̬:�ɿ�����ˢ�� */
	private final static int RELEASE_TO_REFRESH = 0x2;
	/** ����״̬:��������ˢ�� */
	private final static int PULL_TO_REFRESH = 0x3;
	/** ����״̬:����ˢ�� */
	private final static int REFRESHING = 0x4;
	/** �Զ���ListViewͷ���� */
	private LinearLayout headView;
	/** ˢ����ʾ�ı� */
	private TextView txtHeadTip;
	/** Բ�ν�����*/
	private MyCusRingProgressBar pb;
	/** headView�� */
	private int headContentWidth;
	/** headView�� */
	private int headContentHeight;
	/** ��ʶ����ˢ��״̬ */
	private int refreshState;
	/** �״δ�����Ļ��Ϊtrue,����ʱ��Ϊfalse,����һ�δ����¼��ļ�¼״̬ */
	private boolean isRecored = false;
	/** ��ָ�״δ�����ĻʱYλ�� */
	private int startY;
	/** ��ָ�ƶ��ľ����headView��padding����ı���,��ֹ�ƶ�ʱheadView�������� */
	private final static int RATIO = 3;
	/** ��ʾ�Ѿ�����������ˢ��״̬,�������� */
	private boolean isBack = false;
	/** ˢ�¼����ص��ӿ� */
	private OnRefreshListener refreshListener;
	/** �б�����Ļ���˵�һ�������ɼ����position */
	private int firstItemIndex;

	/** ���ڼ�¼һ������������ʵʱPaddingTopֵ */
	private int lessDisPadding;
	private LessPaddingSetRunnable lessPaddingSetRunnable;

	/** ���ڼ�¼ˢ��״̬��ִ�������������ʵʱPaddingTopֵ */
	private int moreDisPadding;
	private MorePaddingSetRunnable morePaddingSetRunnable;

	public MyCusListView3(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/** ��ʼ�� */
	private void init(Context context) {
		Log.i(TAG, "init()...");
		// ��ȡ�Զ���ͷview
		headView = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.head_cus_listview3, null);
		// ��ȡheadView�пؼ�
		pb = (MyCusRingProgressBar) headView.findViewById(R.id.pb);
		txtHeadTip = (TextView) headView.findViewById(R.id.txtHeadTip);
		// Ԥ��headView���
		measureView(headView);
		// ��ȡheadView���
		headContentWidth = headView.getMeasuredWidth();
		headContentHeight = headView.getMeasuredHeight();
		Log.i(TAG, "headView��:[" + headContentWidth + "],��:["
				+ headContentHeight + "]");
		// ����headView��paddingֵ,topPaddingΪ�䱾��ĸ�ֵ,�ﵽ����Ļ�����ص�Ч��
		headView.setPadding(0, -headContentHeight, 0, 0);
		// �ػ�headView
		headView.invalidate();
		// ��headView��ӵ��Զ����ListViewͷ��
		this.addHeaderView(headView, null, false);
		// ����ListView�Ļ�������
		this.setOnScrollListener(this);
		AnimationUtils.loadAnimation(context,
				R.anim.cus_progress_rotate);
		// ��ʼˢ��״̬
		refreshState = DONE;
	}

	/**
	 * Ԥ��headView�Ŀ��
	 * 
	 * @param child
	 */
	private void measureView(View child) {
		Log.i(TAG, "measureView()...");
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// ��¼����ʱ�б��һ�������ɼ����position
		firstItemIndex = firstVisibleItem;
	}

	/** ���������¼� */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:// ��һ�δ���ʱ
			if (firstItemIndex == 0) {
				// ��ʼ��¼
				isRecored = true;
				// ��ȡ�״�Yλ��
				startY = (int) ev.getY();
				Log.i(TAG, "���״δ�����Ϳ�ʼ��¼...");
			} else {
				Log.i(TAG, "�״δ���ʱfirstItemIndex��Ϊ0,��ִ������ˢ��");
			}
			Log.i(TAG, "��¼״̬isRecored:" + isRecored);
			break;
		case MotionEvent.ACTION_UP:// �ɿ���Ļʱ
			// �Ƴ���¼
			isRecored = false;
			Log.i(TAG, "ֹͣ��¼..." + ",isRecored:" + isRecored);
			if (refreshState == PULL_TO_REFRESH) {
				Log.i(TAG, "PULL_TO_REFRESH״̬����,�ص�ԭʼ״̬");
				refreshState = DONE;
				changeHeadView();
			} else if (refreshState == RELEASE_TO_REFRESH) {
				Log.i(TAG, "RELEASE_TO_REFRESH״̬����,����REFRESHING״̬");
				refreshState = REFRESHING;
				changeHeadView();

				// morePadding�ص�
				if (morePaddingSetRunnable != null) {
					morePaddingSetRunnable.stop();
				}
				morePaddingSetRunnable = new MorePaddingSetRunnable(
						moreDisPadding);
				post(morePaddingSetRunnable);

				// ִ��ˢ�·���
				onRefreshing();
			} else if (refreshState == REFRESHING) {
				if (firstItemIndex == 0) {
					Log.i(TAG, "REFRESHING״̬����,���ָ�״̬,headView���ڶ���");

					// morePadding�ص�
					if (morePaddingSetRunnable != null) {
						morePaddingSetRunnable.stop();
					}
					if (lessPaddingSetRunnable != null) {
						lessPaddingSetRunnable.stop();
					}
					morePaddingSetRunnable = new MorePaddingSetRunnable(
							moreDisPadding);
					post(morePaddingSetRunnable);
				} else {
					//NO-OP
					Log.i(TAG, "REFRESHING״̬����,���ָ�״̬,headView���Ƴ�����");
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:// �����ƶ�ʱ
			// ��¼ʵʱ����ָ�ƶ�ʱ����Ļ��Yλ��,���ں�startY�Ƚ�
			int curY = (int) ev.getY();

			if (!isRecored && firstItemIndex == 0) {
				isRecored = true;
				Log.i(TAG, "���ƶ�״ִ̬������ˢ��,��ʼ��¼..." + ",isRecored:" + isRecored);
				startY = curY;
			}

			if (isRecored) {
				// ��ʼ�����״̬
				if (refreshState == DONE) {
					if (curY - startY > 0) {// ��ʾ��������
						// ״̬��Ϊ����ˢ��
						refreshState = PULL_TO_REFRESH;
						changeHeadView();
					}
				}

				// ����ˢ��״̬
				if (refreshState == PULL_TO_REFRESH) {
					setSelection(0);
					// ���ϸı�headView�ĸ߶�
					headView.setPadding(0, (curY - startY) / RATIO
							- headContentHeight, 0, 0);

					// ��תԲ�ν�����
					changeProgress(curY - startY);

					// ʵʱ��¼lessDisPaddingƫ����������С����
					lessDisPadding = (curY - startY) / RATIO
							- headContentHeight;
					if (lessDisPadding <= -headContentHeight) {
						lessDisPadding = -headContentHeight;
					}

					// ������RELEASE_TO_REFRESH״̬
					if ((curY - startY) / RATIO >= headContentHeight) {
						refreshState = RELEASE_TO_REFRESH;
						isBack = true;
						changeHeadView();
					} else if ((curY - startY) <= 0) {
						// ���Ƶ���
						refreshState = DONE;
						changeHeadView();
					}
				}

				// ���ֿ���ˢ��״̬
				if (refreshState == RELEASE_TO_REFRESH) {
					setSelection(0);
					// ���ϸı�headView�ĸ߶�
					headView.setPadding(0, (curY - startY) / RATIO
							- headContentHeight, 0, 0);

					// ʵʱ��¼lessDisPaddingƫ����������С����
					lessDisPadding = (curY - startY) / RATIO
							- headContentHeight;
					if (lessDisPadding <= -headContentHeight) {
						lessDisPadding = -headContentHeight;
					}

					// ʵʱ��¼moreDisPaddingƫ����������С����
					moreDisPadding = (curY - startY) / RATIO
							- headContentHeight;
					if (moreDisPadding <= 0) {
						moreDisPadding = 0;
					}

					// ��������
					if ((curY - startY) / RATIO < headContentHeight) {
						refreshState = PULL_TO_REFRESH;
						changeHeadView();
					}
				}

				// ����ˢ��״̬
				if (refreshState == REFRESHING) {
					if (curY - startY > 0) {
						// ֻ�ı�paddingֵ,�������ദ��
						headView.setPadding(0, (curY - startY) / RATIO, 0, 0);

						// ʵʱ��¼lessDisPaddingƫ����������С����
						lessDisPadding = (curY - startY) / RATIO;
						if (lessDisPadding <= -headContentHeight) {
							lessDisPadding = -headContentHeight;
						}

						// ʵʱ��¼moreDisPaddingƫ����������С����
						moreDisPadding = (curY - startY) / RATIO;
						if (moreDisPadding <= 0) {
							moreDisPadding = 0;
						}
					}
				}
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	/** ����ˢ�µķ��� */
	private void onRefreshing() {
		// ���ûص��ӿ��е�ˢ�·���
		if (refreshListener != null) {
			refreshListener.toRefresh();
		}
	}

	/** ʹ�ý��洫�ݸ���ListView�Ļص��ӿ�,�������߼�ͨ�� */
	public interface OnRefreshListener {
		public void toRefresh();
	}

	/**
	 * ע��һ������ˢ�µĻص��ӿ�
	 * 
	 * @param refreshListener
	 */
	public void setOnRefreshListener(OnRefreshListener refreshListener) {
		// ��ȡ���ݹ����Ļص��ӿ�
		this.refreshListener = refreshListener;
	}

	/** ʹ�ý���ִ����ˢ�²���ʱ���ô˷��� */
	public void onRefreshFinished() {
		refreshState = DONE;
		changeHeadView();
	}

	/** ���������н��ȱ仯 */
	private void changeProgress(int del) {
		if(del < 90){
			pb.setProgress(0);
		}else{
			int progress = del -90 ;
			if(progress >=90){
				progress = 90;
			}
			pb.setProgress(progress);
		}
	}

	/** ����������״̬�ı�headView */
	private void changeHeadView() {
		switch (refreshState) {
		case DONE:// ��ʼ�����״̬
			Log.i(TAG, "��ǰ״̬:DONE");

			if (isRecored) {// ����ˢ��״̬������������ֱ��ˢ���껹δ����
				// ��ǲ��ټ�¼
				isRecored = false;
			} else {// ����״̬�½���ˢ�����
				// �ص�������������
				if (lessDisPadding >= 0) {
					lessDisPadding = 0;
				}
			}

			// ����״̬���
			isBack = false;
			// �����������ת����
			pb.stopCycleAnim();
			txtHeadTip.setText("����ˢ��...");

			// lessPadding�ص�
			if (lessPaddingSetRunnable != null) {
				lessPaddingSetRunnable.stop();
			}
			if (morePaddingSetRunnable != null) {
				morePaddingSetRunnable.stop();
			}
			lessPaddingSetRunnable = new LessPaddingSetRunnable(lessDisPadding);
			post(lessPaddingSetRunnable);
			break;
		case PULL_TO_REFRESH:// ����ˢ��״̬
			Log.i(TAG, "��ǰ״̬:PULL_TO_REFRESH");
			// ��RELEASE_TO_REFRESH�ص�PULL_TO_REFRESH״̬
			Log.i(TAG, "�Ƿ���ɿ�ˢ�»ص�����ˢ��...isBack:" + isBack);
			if (isBack) {
				// NO-OP
			}
			txtHeadTip.setText("����ˢ��...");
			break;
		case RELEASE_TO_REFRESH:
			Log.i(TAG, "��ǰ״̬:RELEASE_TO_REFRESH");
			txtHeadTip.setText("�ɿ�ˢ��...");
			break;
		case REFRESHING:
			Log.i(TAG, "��ǰ״̬:REFRESHING");
			// ������������ת����
			pb.startCycleAnim();
			txtHeadTip.setText("����ˢ��...");
			break;
		}
	}

	/**
	 * ͷ���ص����������÷�ʱ�������Padding�ķ�ʽ
	 * <ul>
	 * <strong>�������̰�����Ч����</strong>
	 * <li>��������δ�ﵽˢ��λ�ú�����</li>
	 * <li>������������headview�߶Ⱥ�����</li>
	 * </ul>
	 * 
	 * <p>
	 * �����߼�����<em>Chris Banes</em>��<code>PullToRefreshListView</code>
	 * <p>
	 */
	private class LessPaddingSetRunnable implements Runnable {
		private Interpolator interpolator;
		private int disP;
		private long startTime = -1;
		private int currentPadding = -1;
		private boolean canRunning = true;

		public LessPaddingSetRunnable(int disPadding) {
			interpolator = new DecelerateInterpolator();
			this.disP = disPadding;
		}

		@Override
		public void run() {
			if (startTime == -1) {
				startTime = System.currentTimeMillis();
			} else {
				long normalizedTime = (1000 * (System.currentTimeMillis() - startTime)) / 200;
				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);
				int deltaP = Math
						.round((disP + headContentHeight)
								* interpolator
										.getInterpolation(normalizedTime / 1000f));
				currentPadding = disP - deltaP;
				headView.setPadding(0, currentPadding, 0, 0);
			}

			if (canRunning && (currentPadding > -headContentHeight)) {
				MyCusListView3.this.postDelayed(this, 16);
			} else {
				stop();
			}
		}

		public void stop() {
			canRunning = false;
			removeCallbacks(this);
		}
	}

	/**
	 * ͷ���ص����������÷�ʱ�������Padding�ķ�ʽ
	 * <ul>
	 * <strong>һ�����̰�����Ч����</strong>
	 * <li>����ˢ��״̬����ִ������������</li>
	 * </ul>
	 * 
	 * <p>
	 * �����߼�����<em>Chris Banes</em>��<code>PullToRefreshListView</code>
	 * <p>
	 */
	private class MorePaddingSetRunnable implements Runnable {
		private Interpolator interpolator;
		private int disP;
		private long startTime = -1;
		private int currentPadding = 1;
		private boolean canRunning = true;

		public MorePaddingSetRunnable(int disPadding) {
			interpolator = new DecelerateInterpolator();
			this.disP = disPadding;
		}

		@Override
		public void run() {
			if (startTime == -1) {
				startTime = System.currentTimeMillis();
			} else {
				long normalizedTime = (1000 * (System.currentTimeMillis() - startTime)) / 200;
				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);
				int deltaP = Math
						.round((disP)
								* interpolator
										.getInterpolation(normalizedTime / 1000f));
				currentPadding = disP - deltaP;
				headView.setPadding(0, currentPadding, 0, 0);
			}

			if (canRunning && (currentPadding > 0)) {
				MyCusListView3.this.postDelayed(this, 16);
			} else {
				stop();
			}
		}

		public void stop() {
			canRunning = false;
			removeCallbacks(this);
		}
	}
}
