package com.example.countdown.index;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.countdown.Bean.ActivityNameItem;
import com.example.countdown.R;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddItemActivity extends AppCompatActivity {

    private ImageView back;
    private TextView finish;
    private RelativeLayout rl_date;
    private Calendar cale;
    private TextView time_pick;
    private EditText activity_title;
    private EditText activity_tip;
    String position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        initView();
        initData();

        /*如果是修改Item，则将数据传进来并显示*/
        getItemDataAndSend();

        rl_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(AddItemActivity.this,3,time_pick,cale);
            }
        });
        //返回按钮
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //点击完成退回到主界面
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*点击完成的时候完成创建一个活动*/
                String activity_title = AddItemActivity.this.activity_title.getText().toString();
                String activity_tip = AddItemActivity.this.activity_tip.getText().toString();
                String time_pick = AddItemActivity.this.time_pick.getText().toString();

                Intent intent=new Intent();
                intent.putExtra("activity_title",activity_title);
                intent.putExtra("activity_tip",activity_tip);
                intent.putExtra("time_pick",time_pick);
                intent.putExtra("position",position);
                setResult(1,intent);

                finish();
            }
        });
    }

    public void initView() {
        back = (ImageView) findViewById(R.id.back);
        finish = (TextView) findViewById(R.id.finish);
        rl_date = (RelativeLayout) findViewById(R.id.rl_date);
        //cale = (CalendarView) findViewById(R.id.cale);
        cale = Calendar.getInstance();
        time_pick = (TextView) findViewById(R.id.time_pick);
        activity_title = (EditText) findViewById(R.id.activity_title);
        activity_tip = (EditText) findViewById(R.id.activity_tip);
    }

    public void initData(){
        /*如果是添加Item，则初始化时间，初始的时间是系统时间*/
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String format = df.format(new Date());
        time_pick.setText(format);
    }

    public void showDatePickerDialog(Activity activity, int themeResId, final TextView tvTime, Calendar calendar) {
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(activity, themeResId, new DatePickerDialog.OnDateSetListener() {
                // 绑定监听器(How the parent is notified that the date is set.)

            private String times;

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // 此处得到选择的时间，可以进行你想要的操作
                times = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                tvTime.setText(times);
            }
        }
                // 设置初始日期
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
    }


    //当用户点击一个Item时，将数据传进来,并将数据发送回去
    public void getItemDataAndSend(){
        Intent intent=getIntent();


        String setting_day = intent.getStringExtra("setting_day");
        activity_title.setText(intent.getStringExtra("activity_name"));
        activity_tip.setText(intent.getStringExtra("activity_tip"));
        time_pick.setText(intent.getStringExtra("setting_day"));
//        showDatePickerDialog(AddItemActivity.this,3,time_pick,cale);
        position = intent.getStringExtra("position");
    }
}