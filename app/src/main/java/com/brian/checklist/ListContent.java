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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListContent extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private int listid;
    private String name;
    private ListView listView;
    private ListViewAdapterContent adapter;
    private List<Map<String, Object>> datalist;
    private int countAll;
    private int countFinish;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_content);

        //解析传递过来的listid
        listid = Integer.parseInt(getIntent().getStringExtra("listid"));
        //Log.d("ListContent",getIntent().getStringExtra("listid"));

        //数据库初始化
        dbHelper = new MyDatabaseHelper(ListContent.this, "ListDatabase.db", null, 1);
        db = dbHelper.getWritableDatabase();

        //使用 listid 从数据库中查询list名称并设置到title，id有且仅有1条
        Cursor listname = db.query("List", null, "id=" + listid, null, null, null, null);
        if (listname.getCount() == 1) {
            Log.d("main", "check");
            listname.moveToFirst();
            name = listname.getString(listname.getColumnIndex("listname"));
            countAll = listname.getInt(listname.getColumnIndex("countAll"));
            countFinish = listname.getInt(listname.getColumnIndex("countFinish"));
            TextView title = findViewById(R.id.listtitle);
            title.setText(name);
        } else {
            ListContent.this.finish();
            overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
            Toast.makeText(ListContent.this, "发生严重错误！", Toast.LENGTH_SHORT).show();
        }
        listname.close();

        //监听回收站与归档按钮
        findViewById(R.id.btn_MoveToTrash).setOnClickListener(this::onClick);
        findViewById(R.id.btn_MoveToArchive).setOnClickListener(this::onClick);

        //
        listView = (ListView) findViewById(R.id.listview);
        datalist = getData();
        adapter = new ListViewAdapterContent(ListContent.this, datalist);

        //
        View addView = getLayoutInflater().inflate(R.layout.content_item_add, null);
        ImageView addbtn = addView.findViewById(R.id.add_icon);
        listView.addFooterView(addView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, Object> contentinfo = (HashMap<String, Object>) listView.getItemAtPosition(position);
            int contentid = (int) contentinfo.get("id");
            int contentstatus = (int) contentinfo.get("status");
            if (contentstatus == 0) {
                //点击改为完成
                countFinish++;
                ContentValues values_0to1 = new ContentValues();
                ContentValues values_sync = new ContentValues();
                values_sync.put("countFinish", countFinish);
                values_0to1.put("isFinish", 1);

                db.update("Content", values_0to1, "id=" + contentid, null);
                db.update("List", values_sync, "id=" + listid, null);

                values_sync.clear();
                values_0to1.clear();
                //刷新list
                refresh();
            } else if (contentstatus == 1) {
                //点击改为不完成
                countFinish--;
                ContentValues values_1to0 = new ContentValues();
                ContentValues values_sync = new ContentValues();
                values_1to0.put("isFinish", 0);
                values_sync.put("countFinish", countFinish);

                db.update("Content", values_1to0, "id=" + contentid, null);
                db.update("List", values_sync, "id=" + listid, null);

                values_sync.clear();
                values_1to0.clear();
                //刷新list
                refresh();
            }
        });
        addbtn.setOnClickListener(v -> {
            Log.d("Add", "onClick: add");
            final EditText add_content_text = new EditText(ListContent.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(ListContent.this);
            builder.setTitle("请添加内容");
            builder.setView(add_content_text);
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.setPositiveButton("确定", (dialog, which) -> {
                ContentValues values = new ContentValues();//new一个存放写入数据的value
                if (add_content_text.getText().toString().length() != 0) {
                    values.put("content", add_content_text.getText().toString());
                    values.put("listid", listid);
                    values.put("isFinish", 0);
                    db.insert("Content", null, values);
                    values.clear();
                    countAll++;
                    ContentValues values_sync = new ContentValues();
                    values_sync.put("countAll", countAll);
                    db.update("List", values_sync, "id=" + listid, null);
                    refresh();
                }
                else {
                    Toast.makeText(ListContent.this, "请输入内容", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        });
    }


    public List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<>();
        @SuppressLint("Recycle") TypedArray checked_icon = getResources().obtainTypedArray(R.array.checked_icon);

        Cursor listcontent = db.query("Content", null, "listid=" + listid, null, null, null, null);
        if (listcontent.moveToFirst()) {
            do {
                String name = listcontent.getString(listcontent.getColumnIndex("content"));
                int id = listcontent.getInt(listcontent.getColumnIndex("id"));
                int status = listcontent.getInt(listcontent.getColumnIndex("isFinish"));

                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("title", name);
                map.put("image", checked_icon.getResourceId(status, 0));
                map.put("status", status);
                list.add(map);
            } while (listcontent.moveToNext());
        }
        listcontent.close();
        return list;
    }

    public void syncdb() {
        Cursor listALL = db.query("Content", null, "listid=" + listid, null, null, null, null);
        countAll = listALL.getCount();
        listALL.close();
        Cursor listFinish = db.query("Content", null, "listid=" + listid + " AND isFinish=1", null, null, null, null);
        countFinish = listFinish.getCount();
        listFinish.close();
        ContentValues values_sync = new ContentValues();
        values_sync.put("countFinish", countFinish);
        values_sync.put("countAll", countAll);
        db.update("List", values_sync, "id=" + listid, null);
    }


    //返回键
    public void backviewonClick(View view) {
        syncdb();
        ListContent.this.finish();
        overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ListContent.this.finish();
            overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
        }
        return super.onKeyUp(keyCode, event);
    }

    //回收站与归档按钮操作
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_MoveToTrash) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ListContent.this);
            builder.setTitle("确认将 " + name + " 移至回收站？");
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.setPositiveButton("确定", (dialog, which) -> {
                ContentValues values_trash = new ContentValues();
                values_trash.put("status", 1);
                db.update("List", values_trash, "id=" + listid, null);
                values_trash.clear();
                //Toast.makeText(ListContent.this, "已移动至回收站", Toast.LENGTH_SHORT).show();
                ListContent.this.finish();
                overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
            });
            builder.show();

        } else if (viewId == R.id.btn_MoveToArchive) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ListContent.this);
            builder.setTitle("确认将 " + name + " 移至归档？");
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.setPositiveButton("确定", (dialog, which) -> {
                ContentValues values_archive = new ContentValues();
                values_archive.put("status", 2);
                db.update("List", values_archive, "id=" + listid, null);
                values_archive.clear();
                //Toast.makeText(ListContent.this, "已归档", Toast.LENGTH_SHORT).show();
                ListContent.this.finish();
                overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
            });
            builder.show();
        } else {
            Toast.makeText(ListContent.this, "发生严重错误！", Toast.LENGTH_SHORT).show();
        }
    }

    public void refresh() {
        //刷新list
        datalist.clear();
        datalist.addAll(getData());
        adapter.notifyDataSetChanged();
    }
}