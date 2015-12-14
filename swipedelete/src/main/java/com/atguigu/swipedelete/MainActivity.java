package com.atguigu.swipedelete;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.atguigu.swipedelete.base.CommonBaseAdapter;
import com.atguigu.swipedelete.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    private ListView lv_main;
    private CommonBaseAdapter<String> adapter;
    private List<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_main = (ListView) findViewById(R.id.lv_main);
        initData();
        adapter = new CommonBaseAdapter<String>(this, data, R.layout.item_main) {
            @Override
            public void convert(ViewHolder holder, int position) {
                String content = data.get(position);
                holder.setText(R.id.tv_item_content, content);
                holder.setOnclickListener(R.id.tv_item_content, MainActivity.this)
                        .setOnclickListener(R.id.tv_item_menu, MainActivity.this)
                        .setTag(R.id.tv_item_content, position)
                        .setTag(R.id.tv_item_menu, position);


                SwipeLayout swipeLayout = (SwipeLayout) holder.getConvertView();
                swipeLayout.setOnSwipeListener(onSwipeListener);
            }
        };

        lv_main.setAdapter(adapter);
    }

    private void initData() {
        for(int i=0;i<30;i++) {
            data.add("Content "+i);
        }
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        switch(v.getId()) {
            case R.id.tv_item_content:
                Toast.makeText(this, data.get(position), 0).show();
            break;
            case R.id.tv_item_menu:
                data.remove(position);
                adapter.notifyDataSetChanged();
            break;
        }
    }

    private SwipeLayout openedSwipeLayout;
    private SwipeLayout.OnSwipeListener onSwipeListener = new SwipeLayout.OnSwipeListener() {
        @Override
        public void onOpen(SwipeLayout swipeLayout) {
            openedSwipeLayout = swipeLayout;
        }

        @Override
        public void onClose(SwipeLayout swipeLayout) {
            if(swipeLayout==openedSwipeLayout) {
                openedSwipeLayout = null;
            }
        }

        @Override
        public void onDown(SwipeLayout swipeLayout) {
            if(openedSwipeLayout!=null && openedSwipeLayout!=swipeLayout) {
                openedSwipeLayout.close();
            }
        }
    };
}
