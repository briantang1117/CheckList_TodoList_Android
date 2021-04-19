package com.brian.checklist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddListActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        dbHelper = new MyDatabaseHelper(this,"ListDatabase.db",null,1);
    }

    public void backviewonClick(View view) {
        AddListActivity.this.finish();
        overridePendingTransition(R.anim.no_anim,R.anim.trans_out);
    }


    public void addlist(View view) {
        EditText listNameInput = findViewById(R.id.listName);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (listNameInput.getText().toString().length() != 0) {
            values.put("listname",listNameInput.getText().toString());
            values.put("countAll",5);
            values.put("countFinish",5);
            values.put("status",0);
            db.insert("List",null,values);
            values.clear();

            Cursor cursor = db.rawQuery("select last_insert_rowid() from List",null);
            int strid;
            if(cursor.moveToFirst()) {
                strid = cursor.getInt(0);
                cursor.close();
                Log.i("testAuto", String.valueOf(strid));
                Intent intent = new Intent();
                intent.setClass(AddListActivity.this, ListContent.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
                intent.putExtra("listid",String.valueOf(strid));
                startActivity(intent);
                overridePendingTransition(R.anim.trans_in,R.anim.no_anim);
                AddListActivity.this.finish();
            }
            cursor.close();
        }
        else {
            Toast.makeText(AddListActivity.this,"请输入名称",Toast.LENGTH_SHORT).show();
        }
    }
}