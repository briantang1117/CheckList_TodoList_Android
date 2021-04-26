package com.brian.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddListActivity extends AppCompatActivity {

    private EditText listNameInput;
    private MyDatabaseDAO db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);
        db = new MyDatabaseDAO(AddListActivity.this);
        listNameInput = findViewById(R.id.listName);
        listNameInput.setFocusable(true);
        listNameInput.setFocusableInTouchMode(true);
        listNameInput.requestFocus();

        EditText addlist = findViewById(R.id.listName);
        addlist.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                String listNmae = listNameInput.getText().toString().trim();
                //确保list名字不为空
                if (listNmae.length() != 0) {
                    long rowid = (db.addList(listNmae));
                    Intent intent = new Intent();
                    intent.setClass(AddListActivity.this, ListContent.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
                    intent.putExtra("listid", String.valueOf(rowid));
                    startActivity(intent);
                    overridePendingTransition(R.anim.trans_in, R.anim.no_anim);
                    AddListActivity.this.finish();
                } else {
                    Toast.makeText(AddListActivity.this, "请输入名称", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });


        //获取数据库DAO
    }

    public void backviewonClick(View view) {
        AddListActivity.this.finish();
        overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            AddListActivity.this.finish();
            overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
        }
        return super.onKeyUp(keyCode, event);
    }
}