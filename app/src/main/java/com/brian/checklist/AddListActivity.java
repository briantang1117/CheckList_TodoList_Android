package com.brian.checklist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddListActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    private EditText listNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        listNameInput = findViewById(R.id.listName);
        listNameInput.setFocusable(true);
        listNameInput.setFocusableInTouchMode(true);
        listNameInput.requestFocus();

        dbHelper = new MyDatabaseHelper(this, "ListDatabase.db", null, 1);
        //获取数据库
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


    public void addlist(View view) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取数据库

        ContentValues values = new ContentValues();//new一个存放写入数据的value

        //确保list名字不为空
        if (listNameInput.getText().toString().length() != 0) {
            values.put("listname", listNameInput.getText().toString());
            values.put("countAll", 0);
            values.put("countFinish", 0);
            values.put("status", 0);
            db.insert("List", null, values);
            values.clear();

            //返回数据库最新插入行的真实id，有且仅有一个
            Cursor cursor = db.rawQuery("select last_insert_rowid() from List", null);
            int strid;
            if (cursor.moveToFirst()) {
                strid = cursor.getInt(0);
                cursor.close();
                //按下完成键跳转到新建list详情页，附带id
                Intent intent = new Intent();
                intent.setClass(AddListActivity.this, ListContent.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
                intent.putExtra("listid", String.valueOf(strid));
                startActivity(intent);
                overridePendingTransition(R.anim.trans_in, R.anim.no_anim);
                AddListActivity.this.finish();
            }
            cursor.close();
        } else {
            //空list名称报Toast
            Toast.makeText(AddListActivity.this, "请输入名称", Toast.LENGTH_SHORT).show();
        }
    }
}