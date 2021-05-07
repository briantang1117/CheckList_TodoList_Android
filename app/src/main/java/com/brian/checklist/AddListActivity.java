package com.brian.checklist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddListActivity extends AppCompatActivity {

    private EditText listNameInput;
    private TextView listDatePicker;
    private MyDatabaseDAO db;
    private Calendar calendar;
    private String listName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);
        db = new MyDatabaseDAO(AddListActivity.this);
        calendar = Calendar.getInstance();
        listDatePicker = findViewById(R.id.listTime);
        listNameInput = findViewById(R.id.listName);
        listNameInput.setFocusable(true);
        listNameInput.setFocusableInTouchMode(true);
        listNameInput.requestFocus();
        listNameInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                listName = listNameInput.getText().toString().trim();
                //确保list名字不为空
                if (listName.length() != 0) {
                    showDatePickerDialog(AddListActivity.this,0);
                    //选择日期
                } else {
                    Toast.makeText(AddListActivity.this, "请输入名称", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            return false;
        });
        listDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listName = listNameInput.getText().toString().trim();
                //确保list名字不为空
                if (listName.length() != 0) {
                    showDatePickerDialog(AddListActivity.this,0);
                    //选择日期
                } else {
                    Toast.makeText(AddListActivity.this, "请先输入名称", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showDatePickerDialog(Context context,int themeResId){
        new DatePickerDialog(context, themeResId, (view, year, month, dayOfMonth) -> {
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            calendar.set(year,month,dayOfMonth);
            long timestamp=0;
            String listDate = sdf.format(calendar.getTime());
            try {
                timestamp = sdf.parse(listDate).getTime() / 1000;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            listDatePicker.setText(listDate);
            long rowid = (db.addList(listName,timestamp));
            Intent intent = new Intent();
            intent.setClass(AddListActivity.this, ListContent.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
            intent.putExtra("listid", String.valueOf(rowid));
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.no_anim);
            AddListActivity.this.finish();
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
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