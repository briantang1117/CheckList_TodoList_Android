package com.brian.checklist;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyDatabaseDAO {
    private final MyDatabaseHelper dbHelper;
    private final Context mcontext;

    public MyDatabaseDAO(Context context) {
        dbHelper = new MyDatabaseHelper(context);
        mcontext = context;
    }

    public long addList(String listName, long listDate) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取数据库
        ContentValues values = new ContentValues();
        values.put("listname", listName);
        values.put("countAll", 0);
        values.put("countFinish", 0);
        values.put("status", 0);
        values.put("deadline", listDate);
        long rowid = db.insert("List", null, values);
        values.clear();
        return rowid;
    }

    public void clearTrash() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取数据库
        db.delete("List", "status = 1", null);
        db.delete("Content", "status = 1", null);
        db.close();
    }

    public void deleteList(int listId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取数据库
        db.delete("List", "id=" + listId, null);
        db.delete("Content", "listid=" + listId, null);
    }

    public void updateList(int listId, int status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取数据库
        ContentValues values = new ContentValues();
        values.put("status", status);
        db.update("List", values, "id=" + listId, null);
        db.update("Content", values, "listid=" + listId, null);
        values.clear();
    }

    public List<Map<String, Object>> queryList(int method, int status, String listname) {
        //method 0 = id,1 = name
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取数据库
        List<Map<String, Object>> result = new ArrayList<>();
        Cursor listList;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        @SuppressLint("Recycle") TypedArray load_icon = mcontext.getResources().obtainTypedArray(R.array.load_icon);
        if (method == 0) {
            listList = db.query("List", null, "status=" + status, null, null, null, null);
        } else {
            listList = db.rawQuery("select * from List where status = 0 and listname like '%" + listname + "%'", null);
        }
        if (listList.moveToFirst()) {
            do {
                //然后通过Cursor的getColumnIndex()获取某一列中所对应的位置的索引
                String ExtraString = "";
                int ddlstatus = 0, isFinish = 0;
                Map<String, Object> map = new HashMap<>();
                String name = listList.getString(listList.getColumnIndex("listname"));
                int listid = listList.getInt((listList.getColumnIndex("id")));
                map.put("id", listid);
                map.put("title", name);
                int countAll = listList.getInt(listList.getColumnIndex("countAll"));
                int countFinish = listList.getInt(listList.getColumnIndex("countFinish"));
                int load = 0;
                if (countAll != 0) {
                    load = countFinish * 100 / countAll;
                    if (load == 100) {
                        isFinish = 1;
                    }
                }
                map.put("image", load_icon.getResourceId(load / 10, 0));
                //时间计算
                Long ddl = listList.getLong(listList.getColumnIndex("deadline"));//截止时间戳
                Date nowTime = new Date(System.currentTimeMillis());
                String todayDate = sdf.format(nowTime);
                long todayTimeStamp = 0;
                try {
                    todayTimeStamp = sdf.parse(todayDate).getTime() / 1000;//今天的时间戳
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (ddl > todayTimeStamp) {//没过期 返回剩余天数
                    long diff = ddl - todayTimeStamp;
                    long days = diff / (60 * 60 * 24);
                    ExtraString = String.valueOf(days) + "天后";

                } else if (ddl == todayTimeStamp) {//当天 返回"今天"
                    ExtraString = "今天";
                } else {//过期了 返回不一样的图片，暂无
                    ExtraString = "已过期";
                    ddlstatus = 1;
                }
                //时间结束
                map.put("ddl", ddlstatus);
                map.put("isFinish", isFinish);
                map.put("info", countFinish + "完成  " + (countAll - countFinish) + "待办 " + ExtraString);
                result.add(map);
            } while (listList.moveToNext());
        }
        listList.close();
        db.close();
        return result;
    }

    public String queryListInfo(int listId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取数据库
        Cursor listname = db.query("List", null, "id=" + listId, null, null, null, null);
        listname.moveToFirst();
        String listName = listname.getString(listname.getColumnIndex("listname"));
        listname.close();
        return listName;
    }

    public List<Map<String, Object>> queryContent(int listId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取数据库
        List<Map<String, Object>> result = new ArrayList<>();
        @SuppressLint("Recycle") TypedArray checked_icon = mcontext.getResources().obtainTypedArray(R.array.checked_icon);
        Cursor listContent = db.query("Content", null, "listid=" + listId, null, null, null, null);
        if (listContent.moveToFirst()) {
            do {
                String contentName = listContent.getString(listContent.getColumnIndex("content"));
                int id = listContent.getInt(listContent.getColumnIndex("id"));
                int status = listContent.getInt(listContent.getColumnIndex("isFinish"));

                Map<String, Object> map = new HashMap<>();
                map.put("id", id);
                map.put("title", contentName);
                map.put("image", checked_icon.getResourceId(status, 0));
                map.put("status", status);
                result.add(map);
            } while (listContent.moveToNext());
        }
        listContent.close();
        db.close();
        return result;
    }

    public void addContent(String contentName, int listId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取数据库
        ContentValues values = new ContentValues();
        values.put("content", contentName);
        values.put("listid", listId);
        values.put("isFinish", 0);
        values.put("status", 0);
        db.insert("Content", null, values);
        values.clear();
        syncdblite(db, listId);
    }

    public void deleteContent(int contentId, int listId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取数据库
        db.delete("Content", "id=" + contentId, null);
        syncdblite(db, listId);
    }

    public void updateContent(int contentId, int contentStatus, int listId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取数据库
        ContentValues values = new ContentValues();
        values.put("isFinish", 1 ^ contentStatus);
        db.update("Content", values, "id=" + contentId, null);
        syncdblite(db, listId);
    }

    private void syncdblite(SQLiteDatabase db, int listId) {
        Cursor countALL = db.query("Content", null, "listid=" + listId, null, null, null, null);
        int countAll = countALL.getCount();
        countALL.close();
        Cursor countFINISH = db.query("Content", null, "listid=" + listId + " AND isFinish=1", null, null, null, null);
        int countFinish = countFINISH.getCount();
        countFINISH.close();
        ContentValues values_sync = new ContentValues();
        values_sync.put("countFinish", countFinish);
        values_sync.put("countAll", countAll);
        db.update("List", values_sync, "id=" + listId, null);
        values_sync.clear();
    }
}
