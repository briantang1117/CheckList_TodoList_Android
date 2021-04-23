package com.brian.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_2);

        //底部导航栏
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_setting)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        //新建清单按钮 FAB 定义,果断时间可能会换掉
        ImageView fab = findViewById(R.id.add_main);
        fab.setOnClickListener(view -> {
            //页面跳转
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, AddListActivity.class);//this前面为当前activty名称，class前面为要跳转到得activity名称
            startActivity(intent);
            overridePendingTransition(R.anim.trans_in, R.anim.no_anim);
        });

        //数据库初始化
        dbHelper = new MyDatabaseHelper(this, "ListDatabase.db", null, 1);
        dbHelper.getWritableDatabase();
    }

}