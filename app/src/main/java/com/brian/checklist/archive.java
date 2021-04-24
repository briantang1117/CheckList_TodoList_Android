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

public class archive extends AppCompatActivity implements View.OnClickListener {

    private ListView listView;
    private MyDatabaseDAO db;
    private ListViewAdapterArchive adapter;
    private List<Map<String, Object>> datalist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);

        db = new MyDatabaseDAO(archive.this);
        listView = findViewById(R.id.list_view_archive);
        View emptyView = findViewById(R.id.empty);
        listView.setEmptyView(emptyView);
        datalist = getData();
        adapter = new ListViewAdapterArchive(archive.this, datalist);
        listView.setAdapter(adapter);
    }

    public List<Map<String, Object>> getData() {
        return db.queryList(2);
    }

    //OnClick
    @Override
    public void onClick(View v) {
        int btn_id = v.getId();
        HashMap<String, Object> listinfo = (HashMap<String, Object>) listView.getItemAtPosition((int) v.getTag());//SimpleAdapter返回Map
        int listId = (int) listinfo.get("id");
        String listName = listinfo.get("title").toString();
        //点击删除
        if (btn_id == R.id.btn_trash_archive_delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(archive.this);
            builder.setTitle("确认将 " + listName + " 移至回收站？");
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.setPositiveButton("确定", (dialog, which) -> {
                db.updateList(listId, 1);
                refresh();
            });
            builder.show();
        }
        //点击还原
        else if (btn_id == R.id.btn_trash_archive_recover) {
            AlertDialog.Builder builder = new AlertDialog.Builder(archive.this);
            builder.setTitle("确认将 " + listName + " 移出归档?");
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.setPositiveButton("确定", (dialog, which) -> {
                db.updateList(listId, 0);
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