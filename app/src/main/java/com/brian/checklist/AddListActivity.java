package com.brian.checklist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddListActivity extends AppCompatActivity {

    private EditText listNameInput;
    private TextView listTime;
    private MyDatabaseDAO db;
    Calendar calendar = Calendar.getInstance(Locale.CHINA);
    DateFormat format = DateFormat.getDateInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);
        db = new MyDatabaseDAO(AddListActivity.this);
        listTime = findViewById(R.id.listTime);
        listNameInput = findViewById(R.id.listName);
        listNameInput.setFocusable(true);
        listNameInput.setFocusableInTouchMode(true);
        listNameInput.requestFocus();
        listNameInput = findViewById(R.id.listName);
        listNameInput.setOnEditorActionListener((v, actionId, event) -> {
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

        listTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(AddListActivity.this,0,listTime,calendar);
            }
        });

    }

    public static void showDatePickerDialog(Activity activity,int themeResId,final TextView tv,Calendar calendar){
        new DatePickerDialog(activity, themeResId, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                tv.setText(year+"年"+(month+1)+"月"+dayOfMonth+"日");
            }
        }
            ,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
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