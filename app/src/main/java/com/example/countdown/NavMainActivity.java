package com.example.countdown;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.countdown.databinding.ActivityNavMainBinding;

public class NavMainActivity extends AppCompatActivity {

    private ActivityNavMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**取消状态栏*/
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = ActivityNavMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        BottomNavigationView navView = findViewById(R.id.nav_view);
        /*去掉bottom_navigation_bar自带的阴影效果*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            navView.setOutlineProvider(null);
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                navView.getMenu())
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_nav_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        /*设置显示第一个Fragment*/
        navController.navigate(R.id.indexFragment);

        /*点击底部导航栏标签修改文字颜色*/
        Resources resource = getResources();
        @SuppressLint("ResourceType")
        ColorStateList csl = resource.getColorStateList(R.drawable.bottom_btn_selected_color);
        navView.setItemTextColor(csl);

        /**沉浸式状态栏*/
        getWindow().setStatusBarColor(0xff);

        if (Build.VERSION.SDK_INT >= 21){
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }


        /*判断app是否第一次使用*/
        SharedPreferences sharedPreferences = getSharedPreferences("isFirstUse", MODE_PRIVATE);
        //默认false,false代表第一次使用，true代表不是第一次使用
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", false);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (isFirstRun) {
            //不是第一次运行
        } else {
            //第一次运行
//            editor.putBoolean("isFirstRun", true);
//            editor.commit();
        }

    }


}