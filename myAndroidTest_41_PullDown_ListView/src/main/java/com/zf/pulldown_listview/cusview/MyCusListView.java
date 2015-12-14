package com.zf.pulldown_listview.cusview;

import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zf.pulldown_listview.R;

/**
 * �Զ�������ˢ��ListView�������ͷ��ת��ϵͳ���������޻ص�����
 * @author 
 * @Date 2014-4-28
 * @ClassInfo com.zf.pulldown_listview.cusview-MyCusListView.java
 * @Description
 */
public class MyCusListView extends ListView implements OnScrollListener {
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
	/** ���ˢ��ʱ���ı� */
	private TextView txtLastRefresh;
	/** ������ͷͼ�� */
	private ImageView imgRefreshArrow;
	/** ˢ�½�����ͼ�� */
	private ProgressBar pbRefreshRound;
	/** headView�� */
	private int headContentWidth;
	/** headView�� */
	private int headContentHeight;
	/** ����ʱ��ͷ��ת���� */
	private Animation pullAnim;
	/** ȡ��ʱ��ͷ��ת���� */
	private Animation reserveAnim;
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

	public MyCusListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/** ��ʼ�� */
	private void init(Context context) {
		Log.i(TAG, "init()...");
		// ��ȡ�Զ���ͷview
		headView = (LinearLayout) LayoutInflater.from(context).inflate(
				R.layout.head_cus_listview, null);
		// ��ȡheadView�пؼ�
		imgRefreshArrow = (ImageView) headView
				.findViewById(R.id.imgRefreshArrow);
		pbRefreshRound = (ProgressBar) headView
				.findViewById(R.id.pbRefreshRound);
		txtHeadTip = (TextView) headView.findViewById(R.id.txtHeadTip);
		txtLastRefresh = (TextView) headView.findViewById(R.id.txtLastRefresh);
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
		// ��ȡ��ͷ��ת����
		pullAnim = AnimationUtils.loadAnimation(context, R.anim.arrow_rotate);
		reserveAnim = AnimationUtils.loadAnimation(context,
				R.anim.arrow_rotate_reverse);
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
				refreshState = DONE;
				changeHeadView();
				Log.i(TAG, "PULL_TO_REFRESH״̬����,�ص�ԭʼ״̬");
			} else if (refreshState == RELEASE_TO_REFRESH) {
				refreshState = REFRESHING;
				changeHeadView();
				onRefreshing();
				Log.i(TAG, "RELEASE_TO_REFRESH״̬����,����REFRESHING״̬");
			} else if (refreshState == REFRESHING) {
				if (firstItemIndex == 0) {
					// ����ˢ��״̬
					headView.setPadding(0, 0, 0, 0);
					Log.i(TAG, "REFRESHING״̬����,���ָ�״̬,headView���ڶ���");
				} else {
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
					// ������RELEASE_TO_REFRESH״̬
					if ((curY - startY) / RATIO >= headContentHeight * 1.5) {
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
					// ��������
					if ((curY - startY) / RATIO < headContentHeight* 1.5) {
						refreshState = PULL_TO_REFRESH;
						changeHeadView();
					}
				}

				// ����ˢ��״̬
				if (refreshState == REFRESHING) {
					if (curY - startY > 0) {
						// ֻ�ı�paddingֵ,�������ദ��
						headView.setPadding(0, (curY - startY) / RATIO, 0, 0);
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
		// ��ʾ�������
		txtLastRefresh.setText("�������:" + new Date().toLocaleString());
	}

	/** ����������״̬�ı�headView */
	private void changeHeadView() {
		switch (refreshState) {
		case DONE:// ��ʼ�����״̬
			Log.i(TAG, "��ǰ״̬:DONE");
			// ����״̬���
			isBack = false;
			// �ظ�ԭʼ�߶�
			headView.setPadding(0, -headContentHeight, 0, 0);
			// ����������
			pbRefreshRound.setVisibility(View.GONE);
			// ����ԭʼ��ͷͼƬ
			imgRefreshArrow.setImageResource(R.drawable.indicator_arrow);
			imgRefreshArrow.setVisibility(View.VISIBLE);
			txtHeadTip.setText("��������ˢ��...");
			break;
		case PULL_TO_REFRESH:// ����ˢ��״̬
			Log.i(TAG, "��ǰ״̬:PULL_TO_REFRESH");
			// ��RELEASE_TO_REFRESH�ص�PULL_TO_REFRESH״̬
			Log.i(TAG, "�Ƿ���ɿ�ˢ�»ص�����ˢ��...isBack:" + isBack);
			if (isBack) {
				// ���ü�ͷ��ת����
				imgRefreshArrow.startAnimation(reserveAnim);
			}
			txtHeadTip.setText("��������ˢ��...");
			break;
		case RELEASE_TO_REFRESH:
			Log.i(TAG, "��ǰ״̬:RELEASE_TO_REFRESH");
			// ���ü�ͷ��ת����
			imgRefreshArrow.startAnimation(pullAnim);
			txtHeadTip.setText("�ɿ�����ˢ��...");
			break;
		case REFRESHING:
			// ����headView����Ļ������ʾ
			headView.setPadding(0, 0, 0, 0);
			// ��ʾ��������
			pbRefreshRound.setVisibility(View.VISIBLE);
			// ���ؼ�ͷͼ��
			imgRefreshArrow.clearAnimation();
			imgRefreshArrow.setVisibility(View.GONE);
			txtHeadTip.setText("����ˢ��...");
			Log.i(TAG, "��ǰ״̬:REFRESHING");
			break;
		}
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		txtLastRefresh.setText("�������:" + new Date().toLocaleString());
	}
}
