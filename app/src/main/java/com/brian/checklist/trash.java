package com.brian.checklist;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class trash extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ListViewAdapterTrash adapter;
    private List<Map<String, Object>> datalist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        dbHelper = new MyDatabaseHelper(this, "ListDatabase.db", null, 1);
        db = dbHelper.getWritableDatabase();
        datalist = getData();
        listView = findViewById(R.id.list_view_trash);
        View emptyView = findViewById(R.id.empty);
        listView.setEmptyView(emptyView);
        adapter = new ListViewAdapterTrash(trash.this, datalist);
        listView.setAdapter(adapter);
    }

    //获取数据库中回收站数据
    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        @SuppressLint("Recycle") TypedArray load_icon = getResources().obtainTypedArray(R.array.load_icon);
        Cursor listList = db.query("List", null, "status = 1", null, null, null, null);
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

    //返回键
    public void backviewonClick(View view) {
        trash.this.finish();
        overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            trash.this.finish();
            overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
        }
        return super.onKeyUp(keyCode, event);
    }

    //清空回收站
    public void ClearTrash(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(trash.this);
        builder.setTitle("确认清空回收站？");
        builder.setNegativeButton("取消", (dialog, which) -> {
        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            db.delete("List", "status = 1", null);
            db.delete("Content", "status = 1", null);
            //刷新list
            trash.this.finish();
            overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
        });
        builder.show();
    }

    //点击事件
    @Override
    public void onClick(View v) {
        int btn_id = v.getId();
        //点击删除
        if (btn_id == R.id.btn_trash_archive_delete) {
            final int position = (int) v.getTag(); //获取被点击的控件所在item 的位置，setTag 存储的object，所以此处要强转

            //通过位置查询真实id
            ListView lv = listView;
            HashMap<String, Object> listinfo = (HashMap<String, Object>) lv.getItemAtPosition(position);//SimpleAdapter返回Map
            int listid = (int) listinfo.get("id");
            String listname = listinfo.get("title").toString();

            //点击删除按钮之后，给出dialog提示
            AlertDialog.Builder builder = new AlertDialog.Builder(trash.this);
            builder.setTitle("确认将 " + listname + " 永久删除?");
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.setPositiveButton("确定", (dialog, which) -> {
                //数据库删除
                db.delete("List", "id=" + listid, null);
                db.delete("Content", "listid=" + listid, null);
                //刷新list
                datalist.clear();
                datalist.addAll(getData());
                adapter.notifyDataSetChanged();//更新数据
            });
            builder.show();
        }
        //点击还原
        else if (btn_id == R.id.btn_trash_archive_recover) {
            final int position = (int) v.getTag(); //获取被点击的控件所在item 的位置，setTag 存储的object，所以此处要强转

            ListView lv = listView;
            HashMap<String, Object> listinfo = (HashMap<String, Object>) lv.getItemAtPosition(position);//SimpleAdapter返回Map
            int listid = (int) listinfo.get("id");
            String listname = listinfo.get("title").toString();

            //点击删除按钮之后，给出dialog提示
            AlertDialog.Builder builder = new AlertDialog.Builder(trash.this);
            builder.setTitle("确认将 " + listname + " 移出回收站?");
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.setPositiveButton("确定", (dialog, which) -> {
                //数据库更新
                ContentValues values_recover = new ContentValues();
                values_recover.put("status", 0);
                db.update("List", values_recover, "id=" + listid, null);
                db.update("Content", values_recover, "listid=" + listid, null);
                values_recover.clear();
                //刷新list
                datalist.clear();
                datalist.addAll(getData());
                adapter.notifyDataSetChanged();
            });
            builder.show();
        }
    }
}