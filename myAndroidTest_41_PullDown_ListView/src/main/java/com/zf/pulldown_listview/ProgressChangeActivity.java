package com.zf.pulldown_listview;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import com.zf.pulldown_listview.cusview.MyCusListView3;
import com.zf.pulldown_listview.cusview.MyCusListView3.OnRefreshListener;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.app.Activity;

public class ProgressChangeActivity extends Activity {
	protected static final int REF = 0x1;
	private MyCusListView3 myCuLv;
	private MyCusAdapter mAdapter;
	private List<String> mList;
	private MyHandler handler = new MyHandler(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_progress);
		myCuLv = (MyCusListView3) findViewById(R.id.myCuLv);
		mList = new ArrayList<String>();
		mList.add("origin item 1");
		mList.add("origin item 2");
		mList.add("origin item 3");
		mList.add("origin item 4");
		mList.add("origin item 5");
		mList.add("origin item 6");
		mList.add("origin item 7");
		mList.add("origin item 8");
		mList.add("origin item 9");
		mList.add("origin item 10");
		mList.add("origin item 11");
		mList.add("origin item 12");
		mList.add("origin item 13");
		mList.add("origin item 14");
		mList.add("origin item 15");
		mList.add("origin item 16");
		mList.add("origin item 17");
		mList.add("origin item 18");
		mList.add("origin item 19");
		mList.add("origin item 20");
		mAdapter = new MyCusAdapter();
		myCuLv.setAdapter(mAdapter);
		myCuLv.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void toRefresh() {
				upDateList();
			}
		});
	}

	private void upDateList() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					mList.add(0, "new item " + (int) (Math.random() * 100));
					handler.sendEmptyMessage(REF);
				} catch (InterruptedException e) {
				}
			}
		}).start();
	}

	class MyCusAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {  
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textView = new TextView(getApplicationContext());
			textView.setText(mList.get(position));
			textView.setTextColor(getResources()
					.getColor(android.R.color.black));
			textView.setBackgroundColor(getResources()
					.getColor(android.R.color.white));
			textView.setTextSize(25);
			return textView;
		}
	}
	
	static class MyHandler extends Handler{
		WeakReference<ProgressChangeActivity> mActivity;
		
		public MyHandler(ProgressChangeActivity activity) {
			mActivity = new WeakReference<ProgressChangeActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ProgressChangeActivity mainActivity = mActivity.get();
			switch (msg.what) {
			case REF:
				mainActivity.mAdapter.notifyDataSetChanged();
				mainActivity.myCuLv.onRefreshFinished();
				break;  
			}
		}
	}
}
