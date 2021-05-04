package com.brian.checklist;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
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

    public long addList(String listName) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取数据库
        ContentValues values = new ContentValues();
        values.put("listname", listName);
        values.put("countAll", 0);
        values.put("countFinish", 0);
        values.put("status", 0);
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

    public List<Map<String, Object>> queryList(int status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();//获取数据库
        List<Map<String, Object>> result = new ArrayList<>();
        @SuppressLint("Recycle") TypedArray load_icon = mcontext.getResources().obtainTypedArray(R.array.load_icon);
        Cursor listList = db.query("List", null, "status=" + status, null, null, null, null);
        if (listList.moveToFirst()) {
            do {
                //然后通过Cursor的getColumnIndex()获取某一列中所对应的位置的索引
                String name = listList.getString(listList.getColumnIndex("listname"));
                int listid = listList.getInt((listList.getColumnIndex("id")));
                int countAll = listList.getInt(listList.getColumnIndex("countAll"));
                int countFinish = listList.getInt(listList.getColumnIndex("countFinish"));
                int load = 0;
                if (countAll != 0) {
                    load = countFinish * 100 / countAll;
                }
                Map<String, Object> map = new HashMap<>();
                map.put("id", listid);
                map.put("image", load_icon.getResourceId(load / 10, 0));
                map.put("title", name);
                map.put("info", countFinish + "完成  " + (countAll - countFinish) + "待办");
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

    public List<Map<String,Object>> searchList(String listname) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        List<Map<String,Object>> searchResult = new ArrayList<>();
        @SuppressLint("Recycle") TypedArray load_icon = mcontext.getResources().obtainTypedArray(R.array.load_icon);
        Cursor ListList = db.rawQuery("select * from List where status = 0 and listname like '%"+listname+"%'",null);
        if(ListList.moveToFirst()){
            do{
                String name = ListList.getString(ListList.getColumnIndex("listname"));
                int listid = ListList.getInt(ListList.getColumnIndex("id"));
                int countAll = ListList.getInt(ListList.getColumnIndex("countAll"));
                int countFinish = ListList.getInt(ListList.getColumnIndex("countFinish"));
                int load = 0;
                if(countAll != 0){
                    load = countFinish * 100 / countAll;
                }
                Map<String,Object> map = new HashMap<>();
                map.put("id",listid);
                map.put("title",name);
                map.put("info",countFinish + "完成  " + (countAll-countFinish) + "待办");
                map.put("image",load_icon.getResourceId(load / 10 , 0));
                searchResult.add(map);
            } while (ListList.moveToNext());
        }
        ListList.close();
        db.close();
        return searchResult;
    }
}
