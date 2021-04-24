package com.brian.checklist;

import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListContent extends AppCompatActivity {
    private MyDatabaseDAO db;
    private int listId;
    private String listName;
    private ListView listView;
    private ListViewAdapterContent adapter;
    private List<Map<String, Object>> datalist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_content);

        //解析传递过来的listid
        listId = Integer.parseInt(getIntent().getStringExtra("listid"));

        //数据库初始化
        db = new MyDatabaseDAO(ListContent.this);

        //使用 listid 从数据库中查询list名称并设置到title，id有且仅有1条
        listName = db.queryListInfo(listId);
        TextView title = findViewById(R.id.listtitle);
        title.setText(listName);

        //监听回收站与归档按钮
        findViewById(R.id.btn_MoveToTrash).setOnClickListener(this::onClick);
        findViewById(R.id.btn_MoveToArchive).setOnClickListener(this::onClick);

        //listview
        listView = findViewById(R.id.listview);
        datalist = getData();
        adapter = new ListViewAdapterContent(ListContent.this, datalist);
        View addView = getLayoutInflater().inflate(R.layout.content_item_add, null);
        ImageView addbtn = addView.findViewById(R.id.add_icon);
        listView.addFooterView(addView, null, false);
        listView.setAdapter(adapter);

        //listview点击监听
        listView.setOnItemClickListener((parent, view, position, id) -> {
            HashMap<String, Object> contentinfo = (HashMap<String, Object>) listView.getItemAtPosition(position);
            int contentId = (int) contentinfo.get("id");
            int contentStatus = (int) contentinfo.get("status");
            db.updateContent(contentId, contentStatus, listId);
            refresh();
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            HashMap<String, Object> contentinfo = (HashMap<String, Object>) listView.getItemAtPosition(position);
            int contentId = (int) contentinfo.get("id");
            //Toast.makeText(ListContent.this, String.valueOf(contentid), Toast.LENGTH_SHORT).show();
            db.deleteContent(contentId, listId);
            refresh();
            return true;
        });

        addbtn.setOnClickListener(v -> {
            final EditText add_content_text = new EditText(ListContent.this);
            AlertDialog.Builder builder = new AlertDialog.Builder(ListContent.this);
            builder.setTitle("请添加内容");
            builder.setView(add_content_text);
            builder.setNegativeButton("取消", (dialog, which) -> {
            });
            builder.setPositiveButton("确定", (dialog, which) -> {
                String contentName = add_content_text.getText().toString().trim();
                if (contentName.length() != 0) {
                    db.addContent(contentName, listId);
                    refresh();
                } else {
                    Toast.makeText(ListContent.this, "请输入内容", Toast.LENGTH_SHORT).show();
                }
            });
            builder.show();
        });
    }


    public List<Map<String, Object>> getData() {
        return db.queryContent(listId);
    }

    //回收站与归档按钮操作
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_MoveToTrash) {
            final CommonDialog dialog = new CommonDialog(ListContent.this);
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
                            ListContent.this.finish();
                            overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
                        }
                        @Override
                        public void onNegtiveClick() {
                            dialog.dismiss();
                        }
                    }).show();
        } else if (viewId == R.id.btn_MoveToArchive) {
            final CommonDialog dialog = new CommonDialog(ListContent.this);
            dialog.setTitle("您确认要归档 "+listName+" 吗？")
                    .setPositive("归档")
                    .setPositiveColor(Color.parseColor("#ff9500"))
                    .setNegtive("取消")
                    .setMessage("将移到归档，可从归档恢复此清单.")
                    .setOnClickBottomListener(new CommonDialog.OnClickBottomListener() {
                        @Override
                        public void onPositiveClick() {
                            dialog.dismiss();
                            db.updateList(listId, 2);
                            ListContent.this.finish();
                            overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
                        }
                        @Override
                        public void onNegtiveClick() {
                            dialog.dismiss();
                        }
                    }).show();

        } else {
            Toast.makeText(ListContent.this, "发生严重错误！", Toast.LENGTH_SHORT).show();
        }
    }

    //刷新listview
    public void refresh() {
        //刷新list
        datalist.clear();
        datalist.addAll(getData());
        adapter.notifyDataSetChanged();
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

}