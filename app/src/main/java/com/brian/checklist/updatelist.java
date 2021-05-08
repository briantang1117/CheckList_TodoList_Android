package com.brian.checklist;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class updatelist extends AppCompatActivity {
    private int listId;
    private MyDatabaseDAO db;
    private String listName;
    private EditText listNameInput;
    private TextView listDatePicker;
    private Calendar calendar;
    private Date date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_list);

        listId = Integer.parseInt(getIntent().getStringExtra("listId"));
        db = new MyDatabaseDAO(updatelist.this);
        TextView titleText = findViewById(R.id.title);
        ImageView icon = findViewById(R.id.imageView2);
        titleText.setText(R.string.update_list);
        icon.setImageResource(R.drawable.ic_updatelogo);
        listNameInput = findViewById(R.id.listName);
        listDatePicker = findViewById(R.id.listTime);

        Map<String, Object> map = new HashMap<>();
        map = db.queryListInfo(listId);
        listName=(String) map.get("listName");
        Long ddl = (Long)map.get("ddl");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        date = new Date(ddl*1000);
        calendar = Calendar.getInstance();
        calendar.setTime(date);
        String DDL = sdf.format(date);//ddl

        listNameInput.setText(listName);
        listDatePicker.setText(DDL);

        listNameInput.setFocusable(true);
        listNameInput.setFocusableInTouchMode(true);
        listNameInput.requestFocus();
        listNameInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                listName = listNameInput.getText().toString().trim();
                //确保list名字不为空
                if (listName.length() != 0) {
                    showDatePickerDialog(updatelist.this,0);
                    //选择日期
                } else {
                    Toast.makeText(updatelist.this, "请输入名称", Toast.LENGTH_SHORT).show();
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
                    showDatePickerDialog(updatelist.this,0);
                    //选择日期
                } else {
                    Toast.makeText(updatelist.this, "请先输入名称", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void showDatePickerDialog(Context context, int themeResId){
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
            //数据库操作
            db.updateListNamedate(listId,listName,timestamp);
            finish();
            overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
            //
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    //back
    public void backviewonClick(View view) {
        finish();
        overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
        }
        return super.onKeyUp(keyCode, event);
    }
}