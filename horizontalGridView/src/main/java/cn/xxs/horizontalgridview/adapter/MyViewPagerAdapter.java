package cn.xxs.horizontalgridview.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.xxs.horizontalgridview.activity.R;
import cn.xxs.horizontalgridview.control.PageControl;

/**
 * 实现ViewPager页卡切换的适配器
 * 
 * @author Administrator
 * 
 */
public class MyViewPagerAdapter extends PagerAdapter {
	private List<GridView> array;
	
//	private final int APP_PAGE_SIZE=8;
//	
//	private final int GRID_NUM_COLUMNS=4;
//	
	private PageControl control;
//	
//	
	private Map<Integer, GridView>  map;
	
	

	/**
	 * 供外部调用（new）的方法
	 * 
	 * @param context
	 *            上下文
	 * @param imageViews
	 *            添加的序列对象
	 */
	public MyViewPagerAdapter(Context context, List<GridView> array) {
		this.array = array;
	}
	public MyViewPagerAdapter(Context context,Map<Integer, GridView> map){
		this.map=map;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return map.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(map.get(arg1));
	
		return map.get(arg1);
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView((View) arg2);
		//
	}
	

}
