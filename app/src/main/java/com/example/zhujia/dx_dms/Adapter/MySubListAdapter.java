package com.example.zhujia.dx_dms.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zhujia.dx_dms.Data.Data;
import com.example.zhujia.dx_dms.R;

import java.util.List;

public class MySubListAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    private List<Data> datas;
    public int categoryPoition;
    private int selectedPosition = -1;
    public MySubListAdapter(Context context, List<Data>data, int position) {
        this.context = context;
        this.datas = data;
        Log.e("TAG", "MySubListAdapter: "+data.size() );
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.categoryPoition = position;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.mysublist_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView
                    .findViewById(R.id.subTextView);
            viewHolder.layout = (LinearLayout) convertView
                    .findViewById(R.id.colorlayout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (selectedPosition == position) {
            viewHolder.textView.setTextColor(Color.WHITE);
            viewHolder.layout.setBackgroundColor(Color.TRANSPARENT);
        } else {
            viewHolder.textView.setTextColor(Color.BLUE);
            viewHolder.layout.setBackgroundColor(Color.LTGRAY);
        }
        viewHolder.textView.setText(datas.get(position).getLabel());
        viewHolder.textView.setTextColor(Color.BLACK);
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

