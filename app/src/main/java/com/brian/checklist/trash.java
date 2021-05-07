package com.brian.checklist;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

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
        return db.queryList(0,1,null);
    }


    //清空回收站
    public void ClearTrash(View view) {
        final CommonDialog dialog = new CommonDialog(trash.this);
        dialog.setTitle("您确认要清空回收站吗？")
                .setPositive("清空").setPositiveColor(Color.parseColor("#ff2d55"))
                .setNegtive("取消")
                .setMessage("将立即清空回收站，不能撤销此操作.")
                .setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                    @Override
                    public void onPositiveClick() {
                        dialog.dismiss();
                        db.clearTrash();
                        trash.this.finish();
                        overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
                    }

                    @Override
                    public void onNegtiveClick() {
                        dialog.dismiss();
                    }
                }).show();
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
            final CommonDialog dialog = new CommonDialog(trash.this);
            dialog.setTitle("您确认要删除 " + listName + " 吗？")
                    .setPositive("删除").setPositiveColor(Color.parseColor("#ff2d55"))
                    .setNegtive("取消")
                    .setMessage("将立即清除此清单，不能撤销此操作.")
                    .setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                        @Override
                        public void onPositiveClick() {
                            dialog.dismiss();
                            db.deleteList(listId);
                            refresh();
                        }

                        @Override
                        public void onNegtiveClick() {
                            dialog.dismiss();
                        }
                    }).show();
        }
        //点击还原
        else if (btn_id == R.id.btn_trash_archive_recover) {
            db.updateList(listId, 0);
            refresh();
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