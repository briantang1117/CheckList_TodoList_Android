package com.brian.checklist;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class archive extends AppCompatActivity {
    private ListView listView;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ListViewAdapterArchive adapter;
    private List<Map<String, Object>> datalist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        dbHelper = new MyDatabaseHelper(this, "ListDatabase.db", null, 1);
        db = dbHelper.getWritableDatabase();

        listView = findViewById(R.id.list_view_trash);

        datalist = getData();
        adapter = new ListViewAdapterArchive(this, datalist);
        listView.setAdapter(adapter);
    }

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        @SuppressLint("Recycle") TypedArray load_icon = getResources().obtainTypedArray(R.array.load_icon);

        Cursor listList = db.query("List", null, "status = 2", null, null, null, null);
        if (listList.moveToFirst()) {
            do {
                //然后通过Cursor的getColumnIndex()获取某一列中所对应的位置的索引
                String name = listList.getString(listList.getColumnIndex("listname"));
                int listid = listList.getInt((listList.getColumnIndex("id")));
                int countAll = listList.getInt(listList.getColumnIndex("countAll"));
                int countFinish = listList.getInt(listList.getColumnIndex("countFinish"));
                Map<String, Object> map = new HashMap<>();
                map.put("id", listid);
                map.put("title", name);
                map.put("info", countFinish + "完成  " + (countAll - countFinish) + "待办");
                list.add(map);
            } while (listList.moveToNext());
        }
        listList.close();
        return list;
    }

    public void backviewonClick(View view) {
        archive.this.finish();
        overridePendingTransition(R.anim.no_anim,R.anim.trans_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            archive.this.finish();
            overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
        }
        return super.onKeyUp(keyCode, event);
    }
}