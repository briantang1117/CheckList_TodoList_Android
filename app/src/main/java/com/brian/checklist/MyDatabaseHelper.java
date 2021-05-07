package com.brian.checklist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_List = "create table List(" +
            //primary key 将id列设为主键    autoincrement表示id列是自增长的
            "id integer primary key autoincrement," +
            "listname text," +
            "countAll integer," +
            "countFinish integer," +
            "deadline integer,"+
            "status integer)";

    public static final String CREATE_Content = "create table Content(" +
            //primary key 将id列设为主键    autoincrement表示id列是自增长的
            "id integer primary key autoincrement," +
            "isFinish integer," +
            "content text," +
            "status integer," +
            "listid integer)";

    private final Context mContext;

    //构造方法：第一个参数Context，第二个参数数据库名，第三个参数cursor允许我们在查询数据的时候返回一个自定义的光标位置，一般传入的都是null，第四个参数表示目前库的版本号（用于对库进行升级）
    public MyDatabaseHelper(Context context) {
        super(context, "ListDatabase.db", null, 1);
        mContext = context;
    }

    //dbHelper = new MyDatabaseHelper(ListContent.this, "ListDatabase.db", null, 1);

    @Override
    public void onCreate(SQLiteDatabase db) {
        //调用SQLiteDatabase中的execSQL（）执行建表语句。
        db.execSQL(CREATE_List);
        db.execSQL(CREATE_Content);
        //创建成功
        Toast.makeText(mContext, "初始化清单成功", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists List");
        db.execSQL("drop table if exists Content");
        onCreate(db);
    }
}


