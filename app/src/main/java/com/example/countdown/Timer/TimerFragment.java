package com.example.countdown.Timer;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.countdown.Bean.ActivityNameItem;
import com.example.countdown.R;
import com.example.countdown.index.AddItemActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;


public class TimerFragment extends Fragment {

    private View v;
    private TextView phone_brand;
    private ImageView gif_pic;
    private NumberPicker num_picker;
    private TextView age_picker;
    private Calendar cale;
    private TextView exist_time;

    private TextView residue_time;
    private Button electronic_clock;
    private ProgressBar progress_bar;
    private TextView percent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_timer, container, false);

        initView();
        initAgeDate();
        age_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                showDatePickerDialog(getActivity(), 3, age_picker, cale);
            }
        });

        electronic_clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), DigitalTubeClockActivity.class));
            }
        });
        return v;
    }

    public void initView() {
        cale = Calendar.getInstance();
        age_picker = (TextView) v.findViewById(R.id.age_picker);
        exist_time = (TextView) v.findViewById(R.id.exist_time);
//        residue_time = (TextView) v.findViewById(R.id.residue_time);
        electronic_clock = (Button) v.findViewById(R.id.electronic_clock);
        progress_bar = (ProgressBar) v.findViewById(R.id.progress_bar);
        percent = (TextView) v.findViewById(R.id.percent);
    }

    /*时间选择对话框*/
    public void showDatePickerDialog(Activity activity, int themeResId, final TextView tvTime, Calendar calendar) {
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(activity, themeResId
                // 绑定监听器(How the parent is notified that the date is set.)
                , new DatePickerDialog.OnDateSetListener() {
            private int age;

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // 此处得到选择的时间，可以进行你想要的操作
                monthOfYear = monthOfYear + 1;      //默认为0开始

                //当前系统时间减去出生日期
                int cat_age = calendar.get(Calendar.YEAR) - year;
                int cat_month = calendar.get(Calendar.MONTH) + 1 - monthOfYear;        //获得相减后的月，月份从0开始
                int cat_day = calendar.get(Calendar.DAY_OF_MONTH) - dayOfMonth;         //获得相减后的日
//                System.out.println(cat_month);
                //如果相减的日小于0
                if (cat_day < 0) {
                    cat_month = cat_month - 1;
//                    System.out.println(cat_month);
                    cat_day = cat_day + calendar.getActualMaximum(Calendar.DATE);
                }
                //如果相减的月小于0
                if (cat_month < 0) {
                    cat_age = cat_age - 1;
                    cat_month = cat_month + 12;
//                    System.out.println(cat_month);
                }


                if (cat_age < 0) {                      //输入的年不合法
                    //设置了年龄
                    tvTime.setText(0 + "");
                    /*设置已存在年月日*/
                    exist_time.setText("0年0月0日");
                    /*设置年龄进度条*/
                    percent.setText("0%");
                    initProgress(0);
                    Toast.makeText(getActivity(), "年龄不合法", Toast.LENGTH_SHORT).show();
                } else if (cat_age == 0 && cat_month < 0) {//输入的月不合法
                    //设置了年龄
                    tvTime.setText(0 + "");
                    /*设置已存在年月日*/
                    exist_time.setText("0年0月0日");
                    /*设置年龄进度条*/
                    percent.setText("0%");
                    initProgress(0);
                    Toast.makeText(getActivity(), "年龄不合法", Toast.LENGTH_SHORT).show();
                } else if (cat_age == 0 && cat_month == 0 && cat_day < 0) {//输入的日不合法
                    //设置了年龄
                    tvTime.setText(0 + "");
                    /*设置已存在年月日*/
                    exist_time.setText("0年0月0日");
                    /*设置年龄进度条*/
                    percent.setText("0%");
                    initProgress(0);
                    Toast.makeText(getActivity(), "年龄不合法", Toast.LENGTH_SHORT).show();
                } else {
                    //设置了年龄
                    tvTime.setText(cat_age + "");
                    /*设置已存在年月日*/
                    exist_time.setText(cat_age + "年" + cat_month + "月" + cat_day + "日");
                    /*设置年龄进度条*/
                    percent.setText(cat_age * 100 / 77 + "%");
                    initProgress(cat_age);

                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myAge", getActivity().MODE_PRIVATE);
                    /*提交*/
                    SharedPreferences.Editor edit = sharedPreferences.edit();
                    edit.putInt("cat_age", cat_age);
                    edit.putInt("cat_month", cat_month);
                    edit.putInt("cat_day", cat_day);
                    edit.commit();
                }
            }
        }
                // 设置初始日期
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    //默认的年龄数据
    public void initAgeDate() {
        /*如果没有存储，存储过就读取*/
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("myAge", getActivity().MODE_PRIVATE);
        int mDay = sharedPreferences.getInt("cat_day", -1);
        if (mDay == -1) {           //没有存储
            Calendar calendar = Calendar.getInstance();
            //默认年龄
            int def_age = 20;         //出生年份
            int def_month = 5;          //出生月
            int def_day = 14;           //出生日

            //当前系统时间减去出生日期
            int cat_age = def_age;
            int cat_day = calendar.get(Calendar.DAY_OF_MONTH) - def_day;         //获得相减后的日
            int cat_month = calendar.get(Calendar.MONTH) + 1 - def_month;        //获得相减后的月，月份从0开始
//            System.out.println(cat_month);
            //如果相减的日小于0
            if (cat_day < 0) {
                cat_month = cat_month - 1;
//                System.out.println(cat_month);
                cat_day = cat_day + calendar.getActualMaximum(Calendar.DATE);
            }
            //如果相减的月小于0
            if (cat_month < 0) {
                cat_age = cat_age - 1;
                cat_month = cat_month + 12;
//                System.out.println(cat_month);
            }
            /*提交*/
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putInt("cat_age", cat_age);
            edit.putInt("cat_month", cat_month);
            edit.putInt("cat_day", cat_day);
            edit.commit();

            age_picker.setText(cat_age + "");
            percent.setText(cat_age * 100 / 77 + "%");
            exist_time.setText(def_age + "年" + cat_month + "月" + cat_day + "日");
            initProgress(cat_age);
        } else {            //有数据，直接读取
            int cat_day = sharedPreferences.getInt("cat_day", -1);
            int cat_month = sharedPreferences.getInt("cat_month", -1);
            int cat_age = sharedPreferences.getInt("cat_age", -1);

            age_picker.setText(cat_age + "");
            percent.setText(cat_age * 100 / 77 + "%");
            exist_time.setText(cat_age + "年" + cat_month + "月" + cat_day + "日");
            initProgress(cat_age);
        }
    }

    public void initProgress(int curr_age) {
        progress_bar.setProgress(curr_age * 100 / 77);
    }

}