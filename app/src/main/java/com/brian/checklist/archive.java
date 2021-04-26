package com.brian.checklist;

import android.graphics.Color;
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
            final CommonDialog dialog = new CommonDialog(archive.this);
            dialog.setTitle("您确认要删除 "+listName+" 吗？")
                    .setPositive("删除")
                    .setPositiveColor(Color.parseColor("#ff2d55"))
                    .setNegtive("取消")
                    .setMessage("将移到回收站，可从回收站恢复此清单.")
                    .setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                        @Override
                        public void onPositiveClick() {
                            dialog.dismiss();
                            db.updateList(listId, 1);
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