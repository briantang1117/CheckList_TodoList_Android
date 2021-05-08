package com.brian.checklist;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    private EditText addcontent;
    private TextView title;
    private TextView info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_content);
        //解析传递过来的listid
        listId = Integer.parseInt(getIntent().getStringExtra("listid"));
        //数据库初始化
        db = new MyDatabaseDAO(ListContent.this);
        //使用 listid 从数据库中查询list名称并设置到title，id有且仅有1条
        title = findViewById(R.id.listtitle);


        //监听回收站与归档按钮
        findViewById(R.id.btn_MoveToTrash).setOnClickListener(this::onClick);
        findViewById(R.id.btn_MoveToArchive).setOnClickListener(this::onClick);
        findViewById(R.id.btn_updateList).setOnClickListener(this::onClick);

        //listview
        listView = findViewById(R.id.listview);
        datalist = getData();
        adapter = new ListViewAdapterContent(ListContent.this, datalist);
        View addView = getLayoutInflater().inflate(R.layout.content_item_add, null);
        addcontent = addView.findViewById(R.id.addcontent);
        info=addView.findViewById(R.id.info);
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

        addcontent.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String contentName = addcontent.getText().toString().trim();
                if (contentName.length() != 0) {
                    db.addContent(contentName, listId);
                    refresh();
                    addcontent.setText("");
                    //listView.setSelection(listView.getBottom());
                } else {
                    hidekeyboard(v);
                }
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String, Object> map = new HashMap<>();
        map = db.queryListInfo(listId);
        listName=(String) map.get("listName");
        Long ddl = (Long)map.get("ddl");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(ddl*1000);
        Date nowTime = new Date(System.currentTimeMillis());
        String DDL = sdf.format(date);//ddl
        title.setText(listName);
        info.setText("截止时间："+DDL);
        String todayDate = sdf.format(nowTime);
        long todayTimeStamp = 0;
        try {
            todayTimeStamp = sdf.parse(todayDate).getTime() / 1000;//今天的时间戳
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (ddl < todayTimeStamp) {//过期了红色
            info.setTextColor(Color.parseColor("#FF2D55"));
        } else {//没过期黑色
            info.setTextColor(Color.parseColor("#454545"));
        }

    }

    public void hidekeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        addcontent.clearFocus();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        DisplayUtils.hideInputWhenTouchOtherView(this, ev, null);
        addcontent.clearFocus();
        return super.dispatchTouchEvent(ev);
    }

    public List<Map<String, Object>> getData() {
        return db.queryContent(listId);
    }

    //回收站与归档按钮操作
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.btn_MoveToTrash) {
            final CommonDialog dialog = new CommonDialog(ListContent.this);
            dialog.setTitle("您确认要删除 " + listName + " 吗？")
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
            dialog.setTitle("您确认要归档 " + listName + " 吗？")
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

        } else if (viewId == R.id.btn_updateList){
            Intent intent = new Intent();
            intent.setClass(ListContent.this,updatelist.class);
            intent.putExtra("listId",String.valueOf(listId));
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.no_anim);
        }
        else {
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