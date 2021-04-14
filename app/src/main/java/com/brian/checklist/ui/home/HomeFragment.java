package com.brian.checklist.ui.home;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.brian.checklist.MyDatabaseHelper;
import com.brian.checklist.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {
    private ListView listView;
    private MyDatabaseHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home , container, false);

        listView = (ListView)view.findViewById(R.id.list_view);
        List<Map<String, Object>> list=getData();
        listView.setAdapter(new ListViewAdapter(getActivity(), list));
        return view;
    }




    public List<Map<String, Object>> getData(){
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
        dbHelper = new MyDatabaseHelper(getContext(),"ListDatabase.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor listList = db.query("List",null,"status = 0",null,null,null,null);
        if (listList.moveToFirst()){
            do {
                //然后通过Cursor的getColumnIndex()获取某一列中所对应的位置的索引
                String name = listList.getString(listList.getColumnIndex("listname"));
                int status = listList.getInt(listList.getColumnIndex("status"));
                int countAll = listList.getInt(listList.getColumnIndex("countAll"));
                int countFinish = listList.getInt(listList.getColumnIndex("countFinish"));
                int load = countAll/countFinish;
              //  Log.d("MainActivity","list name is "+name);
              //  Log.d("MainActivity","list ststus is "+status);

                Map<String, Object> map=new HashMap<String, Object>();
                map.put("image", "@drawable/ic_load"+String.valueOf(load));
                map.put("title", name);
                map.put("info", String.valueOf(countFinish)+"完成  "+String.valueOf(countAll-countFinish)+"待办");
                list.add(map);

            }while(listList.moveToNext());
        }
        listList.close();
        return list;
    }
}
