package com.brian.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //底部导航栏
        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_setting)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //新建清单按钮 FAB 定义,过段时间可能会换掉
        ImageView fab = findViewById(R.id.add_main);
        //FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            //页面跳转
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, AddListActivity.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.no_anim);
        });
    }

}