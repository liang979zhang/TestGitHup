package com.example.ss.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ss.R;

/**
 * Created by xch on 2014/6/14.
 */
public class NewsListAdapter extends BaseAdapter {
    private Context mContext;
    private List<User> mUsers;
    Drawable defaultDrawable;


    public NewsListAdapter(Context context, List<User> users) {
        this.mContext = context;
        this.mUsers = users;
        defaultDrawable = mContext.getResources().getDrawable(R.drawable.ic_launcher);
    }

    @Override
    public int getCount() {
        return mUsers.size();
    }

    @Override
    public Object getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        final ViewHolder holder;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(
                    R.layout.item_new, null);
            holder = new ViewHolder();
            holder.headImage = (ImageView) view.findViewById(R.id.new_it_iv_head);
            holder.nameText = (TextView) view.findViewById(R.id.new_it_tv_name);
            holder.signText = (TextView) view.findViewById(R.id.new_it_tv_sign);
            holder.timeText = (TextView) view.findViewById(R.id.new_it_tv_time);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        User user = mUsers.get(position);
        holder.headImage.setImageDrawable(defaultDrawable);
        holder.nameText.setText(user.name);
        holder.signText.setText(user.sign);
        holder.timeText.setText(user.time);
        return view;
    }

    public class ViewHolder {
        ImageView headImage;
        TextView nameText;
        TextView signText;
        TextView timeText;
       public String status;
    }
}
