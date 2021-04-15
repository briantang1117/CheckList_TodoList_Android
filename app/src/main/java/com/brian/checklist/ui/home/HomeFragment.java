package com.brian.checklist.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.brian.checklist.AddListActivity;
import com.brian.checklist.ListContent;
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
    private ListViewAdapter adapter;
    private List<Map<String, Object>> datalist;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        listView = view.findViewById(R.id.list_view);

        return view;
    }

    //从数据库获取data
    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        @SuppressLint("Recycle") TypedArray load_icon = getResources().obtainTypedArray(R.array.load_icon);
        dbHelper = new MyDatabaseHelper(getContext(), "ListDatabase.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor listList = db.query("List", null, "status = 0", null, null, null, null);
        if (listList.moveToFirst()) {
            do {
                //然后通过Cursor的getColumnIndex()获取某一列中所对应的位置的索引
                String name = listList.getString(listList.getColumnIndex("listname"));
                int listid = listList.getInt((listList.getColumnIndex("id")));
                int countAll = listList.getInt(listList.getColumnIndex("countAll"));
                int countFinish = listList.getInt(listList.getColumnIndex("countFinish"));
                int load = 0;
                if (countAll != 0) {
                    load = countFinish * 100 / countAll;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("id", listid);
                map.put("image", load_icon.getResourceId(load / 10, 0));
                map.put("title", name);
                map.put("info", countFinish + "完成  " + (countAll - countFinish) + "待办 " + load + "%");
                list.add(map);
            } while (listList.moveToNext());
        }
        listList.close();
        return list;
    }


    @Override
    public void onStart() {
        super.onStart();

        //从数据库请求list
        datalist = getData();
        adapter = new ListViewAdapter(getActivity(), datalist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView lv = (ListView) parent;
                HashMap<String, Object> listinfo = (HashMap<String, Object>) lv.getItemAtPosition(position);//SimpleAdapter返回Map
                String listid = listinfo.get("id").toString();

                Intent intent = new Intent();
                intent.setClass(getActivity(), ListContent.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
                intent.putExtra("listid",listid);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.trans_in,R.anim.no_anim);
            }
        });

        //Toast.makeText(getActivity(), "onStart", Toast.LENGTH_SHORT).show();
    }
}
