package com.brian.checklist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class ListContent extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private int listid;


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
            String name = listname.getString(listname.getColumnIndex("listname"));
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

    }

    //返回键
    public void backviewonClick(View view) {
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
            builder.setTitle("确认移动至回收站?");
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
            builder.setTitle("确认归档?");
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
}