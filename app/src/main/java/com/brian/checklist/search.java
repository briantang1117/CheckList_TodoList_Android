package com.brian.checklist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class search extends AppCompatActivity {
    private MyDatabaseDAO db;
    private ListView listView;
    private ListViewAdapter adapter;
    private List<Map<String, Object>> datalist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        db = new MyDatabaseDAO(search.this);
        listView = findViewById(R.id.resultList);
        EditText searchList = findViewById(R.id.search_text);
        searchList.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String listName = searchList.getText().toString().trim();
                    if (listName.length() != 0) {

                        datalist = getData(listName);
                        if (datalist != null) {
                            adapter = new ListViewAdapter(search.this, datalist);
                            listView.setAdapter(adapter);
                        } else {
                            Log.v("111", "null");
                        }
                    }
                }
                return false;
            }
        });
    }

    public List<Map<String, Object>> getData(String name) {
        return db.searchList(name);
    }

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
}