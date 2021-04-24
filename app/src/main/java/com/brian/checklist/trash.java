package com.brian.checklist;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class trash extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private MyDatabaseDAO db;
    private ListViewAdapterTrash adapter;
    private List<Map<String, Object>> datalist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        db = new MyDatabaseDAO(trash.this);
        listView = findViewById(R.id.list_view_trash);
        View emptyView = findViewById(R.id.empty);
        listView.setEmptyView(emptyView);
        datalist = getData();
        adapter = new ListViewAdapterTrash(trash.this, datalist);
        listView.setAdapter(adapter);
    }

    //获取数据库中回收站数据
    public List<Map<String, Object>> getData() {
        return db.queryList(1);
    }


    //清空回收站
    public void ClearTrash(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(trash.this);
        builder.setTitle("确认清空回收站？");
        builder.setNegativeButton("取消", (dialog, which) -> {
        });
        builder.setPositiveButton("确定", (dialog, which) -> {
            db.clearTrash();
            trash.this.finish();
            overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
        });
        builder.show();
    }

    //点击事件
    @Override
    public void onClick(View v) {
        int btn_id = v.getId();
        HashMap<String, Object> listinfo = (HashMap<String, Object>) listView.getItemAtPosition((int) v.getTag());//SimpleAdapter返回Map
        int listId = (int) listinfo.get("id");
        String listName = listinfo.get("title").toString();
        //点击删除
        if (btn_id == R.id.btn_trash_archive_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(trash.this);
            builder.setTitle("确认将 " + listName + " 永久删除?");
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.setPositiveButton("确定", (dialog, which) -> {
                db.deleteList(listId);
                refresh();
            });
            builder.show();
        }
        //点击还原
        else if (btn_id == R.id.btn_trash_archive_recover) {
            //点击删除按钮之后，给出dialog提示
            AlertDialog.Builder builder = new AlertDialog.Builder(trash.this);
            builder.setTitle("确认将 " + listName + " 移出回收站?");
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.setPositiveButton("确定", (dialog, which) -> {
                db.updateList(listId, 0);
                refresh();
            });
            builder.show();
        }
    }

    public void refresh() {
        //刷新list
        datalist.clear();
        datalist.addAll(getData());
        adapter.notifyDataSetChanged();
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
}