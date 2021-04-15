package com.brian.checklist;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class ListContent extends AppCompatActivity {
    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_content);

        //解析传递过来的listid
        int listid = Integer.parseInt(getIntent().getStringExtra("listid"));
        //Log.d("ListContent",getIntent().getStringExtra("listid"));

        //数据库初始化
        dbHelper = new MyDatabaseHelper(ListContent.this, "ListDatabase.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //使用 listid 从数据库中查询list名称并设置到title，id有且仅有1条
        Cursor listname = db.query("List", null, "id="+listid, null, null, null, null);
        if (listname.getCount()==1) {
            Log.d("main","check");
            listname.moveToFirst();
            String name = listname.getString(listname.getColumnIndex("listname"));
            TextView title = (TextView) findViewById(R.id.listtitle);
            title.setText(name);
        }
        else {
            ListContent.this.finish();
            overridePendingTransition(R.anim.no_anim,R.anim.trans_out);
            Toast.makeText(ListContent.this, "发生严重错误！", Toast.LENGTH_SHORT).show();
        }



        /*if (listname.moveToFirst()) {
            do {
                //然后通过Cursor的getColumnIndex()获取某一列中所对应的位置的索引
                String name = listname.getString(listname.getColumnIndex("listname"));
                int countAll = listname.getInt(listname.getColumnIndex("countAll"));
                int countFinish = listname.getInt(listname.getColumnIndex("countFinish"));
                int load = 0;
                if (countAll != 0) {
                    load = countFinish * 100 / countAll;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("id", listid);
                map.put("image", load_icon.getResourceId(load / 10, 0));
                map.put("title", name);
                map.put("info", countFinish + "完成  " + (countAll - countFinish) + "待办 " + load + "%");
                list.add(map);
            } while (listname.moveToNext());
        }*/
        listname.close();
    }

    //返回键
    public void backviewonClick(View view) {
        ListContent.this.finish();
        overridePendingTransition(R.anim.no_anim,R.anim.trans_out);
    }
}