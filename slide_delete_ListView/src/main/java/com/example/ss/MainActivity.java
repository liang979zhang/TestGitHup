package com.example.ss;

import java.util.ArrayList;

import android.app.ListActivity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Toast;

import com.example.ss.adapters.NewsListAdapter;
import com.example.ss.adapters.NewsListAdapter.ViewHolder;
import com.example.ss.adapters.User;

/**
 * @title 模仿android 4.0 通知栏动画
 * @description listview 滑动删除item
 */

/**
 * 添加滑动删除按钮
 * 更新事件的处理
 */
public class MainActivity extends ListActivity {
	private ArrayList<User> array;
	private NewsListAdapter adapter;
	PopupWindow pop;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ListView listView = getListView();
		array = getData();
		adapter = new NewsListAdapter(this, array);
		listView.setAdapter(adapter);
		pop = new PopupWindow();
		pop.setTouchable(true);
		pop.setWidth(LayoutParams.WRAP_CONTENT);
		pop.setHeight(LayoutParams.WRAP_CONTENT);
		pop.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0x00000000);
		pop.setBackgroundDrawable(dw);
		pop.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				unDelete(CurrentView, -2);
			}
		});
		/**
		 * 添加listview滑动接听
		 */
		listView.setOnTouchListener(new OnTouchListener() {
			float x, y, upx, upy;

			public boolean onTouch(View view, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					x = event.getX();
					y = event.getY();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					upx = event.getX();
					upy = event.getY();
					int position1 = ((ListView) view).pointToPosition((int) x,
							(int) y);
					int position2 = ((ListView) view).pointToPosition(
							(int) upx, (int) upy);

					if (position1 == position2 && Math.abs(x - upx) > 10) {
						View v = ((ListView) view).getChildAt(position1);
						if ((x - upx) > 0) {
							prepareDelete(v, position1);
						} else {
							unDelete(v, position1);
						}
//					} else {
//						listView.performItemClick(listView, position1,
//								position1);
					}
				}
				return false;
			}

		});

		/**
		 * listview 的item 点击事件
		 */
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View rowView,
					int positon, long id) {
				Toast.makeText(rowView.getContext(),
						"你点击了第" + positon + "位置的item", Toast.LENGTH_SHORT)
						.show();
				// removeListItem(rowView, positon);
				if (CurrentView != null) {
					unDelete(CurrentView, positon);
				}
			}
		});
	}

	private View CurrentView;

	/**
	 * 删除item，并播放动画
	 *
	 * @param rowView
	 *            播放动画的view
	 * @param positon
	 *            要删除的item位置
	 */
	protected void prepareDelete(final View rowView, final int positon) {
		if (rowView == null) {
			return;
		}
		if (CurrentView != null && CurrentView != rowView) {
			unDelete(CurrentView, positon);
		}
		if (!"half_delete".equals(((ViewHolder) rowView.getTag()).status)) {
			Log.e("222", "2333333333");
			final Animation animation = (Animation) AnimationUtils
					.loadAnimation(rowView.getContext(), R.anim.item);
			animation.setFillAfter(true);
			((ViewHolder) rowView.getTag()).status = "half_delete";
			CurrentView = rowView;
			addDeleteButton(rowView, positon);
			rowView.startAnimation(animation);
		}
	}

	protected void unDelete(final View rowView, final int positon) {
		if (rowView == null) {
			Log.e("111111", "3333333");
			return;
		}
		if ("half_delete".equals(((ViewHolder) rowView.getTag()).status)) {
			final Animation animation = (Animation) AnimationUtils
					.loadAnimation(rowView.getContext(), R.anim.item2);
			animation.setFillAfter(true);
			((ViewHolder) rowView.getTag()).status = "normal";
			CurrentView = null;
			animation.setAnimationListener(new AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}
				public void onAnimationRepeat(Animation animation) {
				}
				public void onAnimationEnd(Animation animation) {
					rowView.clearAnimation();
				}
			});

			rowView.startAnimation(animation);
		}

	}

	protected void doDelete(final View rowView, final int positon) {
		if (rowView == null) {
			return;
		}
		if ("half_delete".equals(((ViewHolder) rowView.getTag()).status)) {
			final Animation animation = (Animation) AnimationUtils
					.loadAnimation(rowView.getContext(), R.anim.item2);
			animation.setDuration(0);
			animation.setFillAfter(true);
			((ViewHolder) rowView.getTag()).status = "normal";
			CurrentView = null;
			animation.setAnimationListener(new AnimationListener() {
				public void onAnimationStart(Animation animation) {
				}

				public void onAnimationRepeat(Animation animation) {
				}

				public void onAnimationEnd(Animation animation) {
					rowView.clearAnimation();
					array.remove(positon);
					adapter.notifyDataSetChanged();
				}
			});

			rowView.startAnimation(animation);
		}

	}

	private void addDeleteButton(final View view, final int position) {

		Button button = new Button(MainActivity.this);
		button.setText("删除");
		button.setVisibility(View.VISIBLE);
		ViewGroup.LayoutParams Params = new ViewGroup.LayoutParams(-2, -2);
		button.setLayoutParams(Params);
		pop.setContentView(button);
		pop.showAsDropDown(view, 560, -130);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				doDelete(view, position);
				pop.dismiss();
				CurrentView = null;

			}
		});

	}

	private ArrayList<User> getData() {
		ArrayList<User> users = new ArrayList<User>();
		User user = new User();
		user.logo = "http://t10.baidu.com/it/u=3485469777,1638135480&fm=56";
		user.name = "1111";
		user.time = "11:11";
		user.sign = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
		users.add(user);
		user = new User();
		user.name = "222222";
		users.add(user);
		user = new User();
		user.name = "3333";
		users.add(user);
		user = new User();
		user.name = "44444";
		users.add(user);
		user = new User();
		user.name = "5555";
		users.add(user);
		user = new User();
		user.name = "6666";
		users.add(user);
		user = new User();
		user.name = "77777";
		users.add(user);
		user = new User();
		user.name = "88888";
		users.add(user);
		user = new User();
		user.name = "99999";
		users.add(user);
		user = new User();
		user.name = "aaaaa";
		users.add(user);
		return users;
	}
}