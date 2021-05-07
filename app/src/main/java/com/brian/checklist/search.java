package com.brian.checklist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class search extends AppCompatActivity {
    //全局变量声明 没有初始化
    private MyDatabaseDAO db;//数据库DAO
    private ListView listView;//lv
    private ListViewAdapter adapter;//lv的是配置
    private List<Map<String ,Object>> datalist;//datalist数据集，也就是lv的内容
    private EditText searchList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//重写oncreate
        setContentView(R.layout.activity_search);//设置layout
        db = new MyDatabaseDAO(search.this);//新建数据库dao实例
        listView = findViewById(R.id.resultList);//指定listview
        datalist = new ArrayList<>();//初始化datalist为空list 不是null java中空和null不一样
        adapter = new ListViewAdapter(search.this,datalist);//初始化lv的adapter，datalist为上面的空
        View search_foot = getLayoutInflater().inflate(R.layout.search_foot, null);
        listView.addFooterView(search_foot, null, false);
        listView.setAdapter(adapter);//设置lv的adapter
        searchList = findViewById(R.id.search_text);//指定et
        searchList.setFocusable(true);
        searchList.setFocusableInTouchMode(true);
        searchList.requestFocus();
        searchList.setOnEditorActionListener((v, actionId, event) -> {//设置键盘搜索键和回车的监听
            if(actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {//如果有按下搜索/回车的order
                String listName = searchList.getText().toString().trim();//获得et中的文字
                datalist.clear();//datalist设定为空
                if(listName.length()!=0){//如果et中有内容
                    datalist.addAll(getData(listName));//从数据库中查找并把结果添加到datalist
                }
                adapter.notifyDataSetChanged();//刷新listview
                hidekeyboard(v);
                return true;//结束
            }
            return false;//结束
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String,Object> listinfo = (HashMap<String,Object>)listView.getItemAtPosition(position);
                Log.d("aaa",listinfo.toString());
                String listId = listinfo.get("id").toString();
                Intent intent = new Intent();
                intent.setClass(search.this,ListContent.class);
                intent.putExtra("listid",listId);
                startActivity(intent);
                overridePendingTransition(R.anim.trans_in,R.anim.no_anim);
            }
        });
    }

    public List<Map<String ,Object>> getData(String name){
        return db.queryList(1,0,name);
    }//从db中查找

    //back
    public void backviewonClick(View view) {
        search.this.finish();
        overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            search.this.finish();
            overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
        }
        return super.onKeyUp(keyCode, event);
    }

    public void hidekeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        DisplayUtils.hideInputWhenTouchOtherView(this, ev, null);
        Log.d("TAG", "dispatchTouchEvent: 1");
        return super.dispatchTouchEvent(ev);
    }
}