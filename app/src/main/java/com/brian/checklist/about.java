package com.brian.checklist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class about extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
    }

    public void backviewonClick(View view) {
        about.this.finish();
        overridePendingTransition(R.anim.no_anim,R.anim.trans_out);
    }
}