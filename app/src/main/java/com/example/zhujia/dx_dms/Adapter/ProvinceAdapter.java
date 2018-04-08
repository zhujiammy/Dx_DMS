package com.example.zhujia.dx_dms.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zhujia.dx_dms.Data.Data;
import com.example.zhujia.dx_dms.R;

import java.util.List;

public class ProvinceAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    private List<Data> datas;
    int last_item;
    private int selectedPosition = -1;

    public ProvinceAdapter(Context context, List<Data>datas) {
        this.context = context;
        this.datas = datas;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.mylist_item, null);
            holder = new ViewHolder();
            holder.textView = (TextView) convertView
                    .findViewById(R.id.textView);
            holder.layout = (LinearLayout) convertView
                    .findViewById(R.id.colorlayout);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (selectedPosition == position) {
            holder.textView.setTextColor(Color.WHITE);
            holder.layout.setBackgroundColor(Color.TRANSPARENT);
        } else {
            holder.textView.setTextColor(Color.BLUE);
            holder.layout.setBackgroundColor(Color.LTGRAY);
        }
        holder.textView.setText(datas.get(position).getValue());
        holder.textView.setTextColor(Color.BLACK);
        return convertView;
    }

    public static class ViewHolder {
        public TextView textView;
        public LinearLayout layout;
    }

    public void setSelectedPosition(int position) {
        selectedPosition = position;
    }

}
