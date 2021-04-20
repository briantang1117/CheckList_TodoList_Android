package com.brian.checklist;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ListViewAdapterArchive extends BaseAdapter {

    private final View.OnClickListener listener;
    private final List<Map<String, Object>> data;

    public ListViewAdapterArchive(View.OnClickListener listener, List<Map<String, Object>> data) {
        this.listener = listener;
        this.data = data;
    }

    /**
     * 组件集合，对应list.xml中的控件
     *
     * @author Administrator
     */
    public final class Zujian {
        public TextView title;
        public TextView info;
        public ImageView btn_delete_a;
        public ImageView btn_recover_a;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * 获得某一位置的数据
     */
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    /**
     * 获得唯一标识
     */
    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Zujian zujian;
        if (convertView == null) {
            zujian = new Zujian();
            //获得组件，实例化组件
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.archive_list_item, parent, false);
            zujian.title = convertView.findViewById(R.id.title);
            zujian.info = convertView.findViewById(R.id.info);
            zujian.btn_delete_a = convertView.findViewById(R.id.btn_delete_a);
            zujian.btn_recover_a = convertView.findViewById(R.id.btn_recover_a);
            convertView.setTag(zujian);
        } else {
            zujian = (Zujian) convertView.getTag();
        }
        //绑定数据
        zujian.title.setText((String) data.get(position).get("title"));
        zujian.info.setText((String) data.get(position).get("info"));

        //监听
        zujian.btn_delete_a.setOnClickListener(listener);
        zujian.btn_recover_a.setOnClickListener(listener);
        zujian.btn_delete_a.setTag(position);
        zujian.btn_recover_a.setTag(position);
        return convertView;
    }

}

