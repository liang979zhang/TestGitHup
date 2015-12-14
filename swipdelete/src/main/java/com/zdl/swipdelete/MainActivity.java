package com.zdl.swipdelete;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private ListView lv_main;
    private List<MyBean> data = new ArrayList<>();
    private MyAdapter adapter;
    private SwipeLayout openedSwipeLayout;
    private SwipeLayout.OnSwipeListener onSwipeListener = new SwipeLayout.OnSwipeListener() {
        @Override
        public void onOpen(SwipeLayout swipeLayout) {
            openedSwipeLayout = swipeLayout;
        }

        @Override
        public void onClose(SwipeLayout swipeLayout) {
            if (swipeLayout == openedSwipeLayout) {
                swipeLayout = null;
            }
        }

        @Override
        public void onDown(SwipeLayout swipeLayout) {
            if (openedSwipeLayout != null && openedSwipeLayout != swipeLayout) {
                openedSwipeLayout.close();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_main = (ListView) findViewById(R.id.lv_main);
        initData();
        adapter = new MyAdapter();

        lv_main.setAdapter(adapter);


    }

    //获取数据
    private void initData() {
        for (int i = 0; i < 50; i++) {
            data.add(new MyBean("AA" + i));
        }
    }

    //填充数据
    class MyAdapter extends BaseAdapter implements View.OnClickListener {

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(getApplicationContext(), R.layout.item_swipe, null);
                holder.contentTV = (TextView) convertView.findViewById(R.id.tv_item_content);
                holder.delTV = (TextView) convertView.findViewById(R.id.tv_item_menu);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            //保存当前的position
            holder.contentTV.setTag(position);
            holder.delTV.setTag(position);
            holder.contentTV.setOnClickListener(this);
            holder.delTV.setOnClickListener(this);


            MyBean myBean = data.get(position);
            holder.contentTV.setText(myBean.getName());

            SwipeLayout swipeLayout = (SwipeLayout) convertView;
            swipeLayout.setOnswipelistener(onSwipeListener);
            return convertView;
        }

        @Override
        public void onClick(View v) {
            int position = (int) v.getTag();
            switch (v.getId()) {
                case R.id.tv_item_content:
                    Toast.makeText(MainActivity.this, "点击了" + data.get(position), Toast.LENGTH_SHORT).show();
                    break;
                case R.id.tv_item_menu:
                    data.remove(position);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    class ViewHolder {
        private TextView contentTV;
        private TextView delTV;
    }
}
