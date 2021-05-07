package com.brian.checklist;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Map;

public class ListViewAdapter extends BaseAdapter {

    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public ListViewAdapter(Context context, List<Map<String, Object>> data) {
        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
    }

    //组件集合，对应list.xml中的控件
    public final class Zujian {
        public ImageView image;
        public ImageView image2;
        public TextView title;
        public Button view;
        public TextView info;
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
            convertView = layoutInflater.inflate(R.layout.home_list_item, null);
            zujian.image = convertView.findViewById(R.id.image);
            zujian.image2 = convertView.findViewById(R.id.image2);
            zujian.title = convertView.findViewById(R.id.title);
            zujian.info = convertView.findViewById(R.id.info);
            convertView.setTag(zujian);
        } else {
            zujian = (Zujian) convertView.getTag();
        }
        //绑定数据

        zujian.image.setImageResource((int) data.get(position).get("image"));
        //图片tint设置
        int ddlstatus = (int) data.get(position).get("ddl");
        int isFinish = (int) data.get(position).get("isFinish");
        if (ddlstatus == 1 && isFinish == 0) {
            zujian.image.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.trash)));
            //设置感叹号
            zujian.image2.setImageResource(R.drawable.ic_load_err);

        }
        else if (isFinish == 1) {
            zujian.image.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.green)));
            zujian.image2.setImageDrawable(null);
        }
        else {
            zujian.image.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context,R.color.classicalBlue)));
            zujian.image2.setImageDrawable(null);
        }

        zujian.title.setText((String) data.get(position).get("title"));
        zujian.info.setText((String) data.get(position).get("info"));
        return convertView;
    }

}

