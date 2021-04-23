package com.brian.checklist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class archive extends AppCompatActivity implements View.OnClickListener {

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
        listView = findViewById(R.id.list_view_archive);
        datalist = getData();
        adapter = new ListViewAdapterArchive(archive.this, datalist);
        listView.setAdapter(adapter);
    }

    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
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

    //OnClick
    @Override
    public void onClick(View v) {
        int btn_id = v.getId();
        //点击删除
        if (btn_id == R.id.btn_trash_archive_delete) {
            final int position = (int) v.getTag(); //获取被点击的控件所在item 的位置，setTag 存储的object，所以此处要强转
            HashMap<String, Object> listinfo = (HashMap<String, Object>) listView.getItemAtPosition(position);//SimpleAdapter返回Map
            int listid = (int) listinfo.get("id");
            String listname = listinfo.get("title").toString();

            //点击删除按钮之后，给出dialog提示
            AlertDialog.Builder builder = new AlertDialog.Builder(archive.this);
            builder.setTitle("确认将 " + listname + " 移至回收站？");
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.setPositiveButton("确定", (dialog, which) -> {
                //移动至回收站
                ContentValues values_trash = new ContentValues();
                values_trash.put("status", 1);
                db.update("List", values_trash, "id=" + listid, null);
                db.update("Content", values_trash, "listid=" + listid, null);
                values_trash.clear();
                refresh();
            });
            builder.show();
        }
        //点击还原
        else if (btn_id == R.id.btn_trash_archive_recover) {
            final int position = (int) v.getTag(); //获取被点击的控件所在item 的位置，setTag 存储的object，所以此处要强转
            HashMap<String, Object> listinfo = (HashMap<String, Object>) listView.getItemAtPosition(position);//SimpleAdapter返回Map
            int listid = (int) listinfo.get("id");
            String listname = listinfo.get("title").toString();

            //点击删除按钮之后，给出dialog提示
            AlertDialog.Builder builder = new AlertDialog.Builder(archive.this);
            builder.setTitle("确认将 " + listname + " 移出归档?");
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.setPositiveButton("确定", (dialog, which) -> {
                //数据库更新
                ContentValues values_recover = new ContentValues();
                values_recover.put("status", 0);
                db.update("List", values_recover, "id=" + listid, null);
                values_recover.clear();
                refresh();
            });
            builder.show();
        }
    }

    //back
    public void backviewonClick(View view) {
        archive.this.finish();
        overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            archive.this.finish();
            overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
        }
        return super.onKeyUp(keyCode, event);
    }

    public void refresh() {
        //刷新list
        datalist.clear();
        datalist.addAll(getData());
        adapter.notifyDataSetChanged();
    }
}