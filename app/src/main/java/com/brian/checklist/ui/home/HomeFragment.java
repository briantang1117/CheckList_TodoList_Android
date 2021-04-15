package com.brian.checklist.ui.home;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.brian.checklist.AddListActivity;
import com.brian.checklist.MainActivity;
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

        listView = view.findViewById(R.id.list_view);

        return view;
    }

    public List<Map<String, Object>> getData(){
        List<Map<String, Object>> list= new ArrayList<>();
        @SuppressLint("Recycle") TypedArray load_icon = getResources().obtainTypedArray(R.array.load_icon);
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
                int load = 0;
                if (countAll != 0) {
                    load = countFinish*100/countAll;
                }

                Map<String, Object> map= new HashMap<>();
                map.put("image", load_icon.getResourceId(load/10, 0));
                map.put("title", name);
                map.put("info", countFinish +"完成  "+ (countAll - countFinish) +"待办 "+ load +"%");
                list.add(map);

            }while(listList.moveToNext());
        }
        listList.close();
        return list;
    }

    @Override
    public void onStart() {
        super.onStart();
        List<Map<String, Object>> list=getData();
        listView.setAdapter(new ListViewAdapter(getActivity(), list));
    }
}
