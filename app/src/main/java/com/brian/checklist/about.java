package com.brian.checklist;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class about extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    //左上角返回键，其他页面相同
    public void backviewonClick(View view) {
        about.this.finish();
        overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
    }

    //重写手机返回键，添加切换动画，其他页面相同
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            about.this.finish();
            overridePendingTransition(R.anim.no_anim, R.anim.trans_out);
        }
        return super.onKeyUp(keyCode, event);
    }
}