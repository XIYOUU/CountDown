package com.example.countdown.Timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.countdown.R;
import com.example.countdown.Utils.BatteryView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DigitalTubeClockActivity extends AppCompatActivity {
    // tv1用来显示时间，tv2是用来做个背景的
    private TextView tv1, tv2;

    // Handler里用
    private static final int SHOW_TIME = 1;

    private Timer timer = new Timer();
    private TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            // 调用控件不能在子线程，所以这里需要在handler中调用
            handler.sendEmptyMessage(SHOW_TIME);
        }
    };

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SHOW_TIME:
                    Calendar calendar = Calendar.getInstance();
                    // 显示时间
                    if (calendar.get(Calendar.SECOND) % 2 == 0) {     //偶数，第二个：显示
                        tv1.setText(String.format("%02d:%02d:%02d",
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                calendar.get(Calendar.SECOND)));

                    } else {                                      //奇数，第二个：不显示
                        tv1.setText(String.format("%02d:%02d %02d",
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                calendar.get(Calendar.SECOND)));
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private TextClock dateText;
    private TextView monday;
    private TextView tuesday;
    private TextView wednesday;
    private TextView thursday;
    private TextView friday;
    private TextView saturday;
    private TextView sunday;
    private int mDay;
    private List<TextView> mWeekday;
    private Typeface typeface;
    private TextView battery_percent;
    private BatteryView battery_view_icon;
    private Handler mHandler;
    private RelativeLayout bg;
    private TextView solar_terms;
    private TextView solar_terms_name;
    private int year;
    private int month;
    private int day;
    private TextView day_of_week;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        /*竖屏*/
        if (ori == mConfiguration.ORIENTATION_PORTRAIT) {

            /**取消状态栏*/
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            Toast.makeText(DigitalTubeClockActivity.this, "竖屏模式", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_digital_tube_clock);
            initView();
            getWeedDayIsWhite();
            // 准备字体
            typeface = Typeface.createFromAsset(getAssets(), "fonts/Digital-7/digital-7-mono-3.ttf");
            dateText.setTypeface(typeface);

            //获取电量
            getBatteryPresent();
            initFestival();
        /*横屏*/
        } else if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            /**取消状态栏*/
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            Toast.makeText(DigitalTubeClockActivity.this, "横屏模式", Toast.LENGTH_LONG).show();
            setContentView(R.layout.activity_digital_tube_clock_landscape);

            initView();
            get_day_of_week();
            typeface = Typeface.createFromAsset(getAssets(), "fonts/Digital-7/digital-7-mono-3.ttf");
            dateText.setTypeface(typeface);


            getBatteryPresent();
        }


        /**电子管时间*/
        tv1 = (TextView) findViewById(R.id.ledview_clock_time);
        tv2 = (TextView) findViewById(R.id.ledview_clock_bg);
        dateText = (TextClock) findViewById(R.id.dateText);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/Digital-7/digital-7-mono-3.ttf");
        // 设置字体
        tv1.setTypeface(typeface);
        tv2.setTypeface(typeface);
        tv2.setText("88:88:88");

        // 0毫秒后执行timerTask，并且以后每隔1000毫秒执行一次timerTask
        timer.schedule(timerTask, 0, 1000);





        /*沉浸式状态栏*/
        getWindow().setStatusBarColor(0xff);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public void initView() {
        monday = (TextView) findViewById(R.id.Monday);
        tuesday = (TextView) findViewById(R.id.Tuesday);
        wednesday = (TextView) findViewById(R.id.Wednesday);
        thursday = (TextView) findViewById(R.id.Thursday);
        friday = (TextView) findViewById(R.id.Friday);
        saturday = (TextView) findViewById(R.id.Saturday);
        sunday = (TextView) findViewById(R.id.Sunday);
        dateText = (TextClock) findViewById(R.id.dateText);
        battery_percent = (TextView) findViewById(R.id.battery_percent);
        battery_view_icon = (BatteryView) findViewById(R.id.battery_view_icon);
        bg = (RelativeLayout) findViewById(R.id.bg);
        solar_terms = (TextView) findViewById(R.id.solar_terms);
        solar_terms_name = (TextView) findViewById(R.id.solar_terms_name);
        day_of_week = (TextView) findViewById(R.id.day_of_week);


        mWeekday = new ArrayList<>();
        mWeekday.add(monday);
        mWeekday.add(tuesday);
        mWeekday.add(wednesday);
        mWeekday.add(thursday);
        mWeekday.add(friday);
        mWeekday.add(saturday);
        mWeekday.add(sunday);
    }

    /**
     * 当前星期
     */
    /*当前星期全部为灰色*/
    public void changeWeekGrey() {
        for (int i = 0; i < mWeekday.size(); i++) {
            mWeekday.get(i).setTextColor(Color.parseColor("#757575"));
        }
    }

    /*当前星期几变为白色*/
    public void getWeedDayIsWhite() {
        Calendar c = Calendar.getInstance();
        mDay = c.get(Calendar.DAY_OF_WEEK)-1;
        System.out.println(mDay);
        //周日为0，周一为1
        for (int i = 0; i < 7; i++) {
            if(mDay==0){
                changeWeekGrey();
                mWeekday.get(6).setTextColor(Color.parseColor("#FFFFFF"));
            }
            if (mDay - 1 == i) {
                changeWeekGrey();
                mWeekday.get(i).setTextColor(Color.parseColor("#FFFFFF"));
            }
        }
    }

    /*横屏模式，获取当前星期几*/
    public void get_day_of_week(){
        Calendar calendar= Calendar.getInstance();
        int dayOfWeek=calendar.get(Calendar.DAY_OF_WEEK)+1;
        switch (dayOfWeek){
            case 1:{
                day_of_week.setText("周一");
                break;
            }
            case 2:{
                day_of_week.setText("周二");
                break;
            }
            case 3:{
                day_of_week.setText("周三");
                break;
            }
            case 4:{
                day_of_week.setText("周四");
                break;
            }
            case 5:{
                day_of_week.setText("周五");
                break;
            }
            case 6:{
                day_of_week.setText("周六");
                break;
            }
            case 7:{
                day_of_week.setText("周日");
                break;
            }
        }
    }


    /**实时获取电池电量*/
    public void getBatteryPresent(){
        /*设置电池电量*/
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // (1) 使用handler发送消息
                Message message = new Message();
                message.what = 0;
                mHandler.sendMessage(message);
            }
        }, 0, 1000);//每隔一秒使用handler发送一下消息,也就是每隔一秒执行一次,一直重复执行
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    BatteryManager manager = (BatteryManager) getSystemService(BATTERY_SERVICE);
                    int intProperty = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
//                    System.out.println(intProperty);
                    battery_percent.setText(intProperty + "%");
                    battery_view_icon.setProgress(intProperty);
                    battery_percent.setTypeface(typeface);
                }
            }
        };
    }

    /*获得节气*/
    public void initFestival(){
        Calendar c = Calendar.getInstance();
        month = c.get(Calendar.MONTH)+1;        //月份从0开始
        day = c.get(Calendar.DAY_OF_MONTH);
//        System.out.println(month);
//        System.out.println(day);
        if(month==2 && day>=2 && day<=5){
            solar_terms_name.setText("立春");
            solar_terms.setText("    2月3日-2月4日：表示严冬已逝，春季到来，气温回升，万物复苏。");
        }else if(month==2 && day>=18 && day<=19){
            solar_terms_name.setText("雨水");
            solar_terms.setText("    2月18日-2月19日：由于气温转暖，冰消雪化，雨水增多，故取名为雨水。");
        }

        else if(month==3 && day>=5 && day<=6){
            solar_terms_name.setText("惊蛰");
            solar_terms.setText("    3月5日-3月6日：蛰的本意为藏，动物冬眠称“入蛰”。古人认为冬眠的昆虫被春雷惊醒，故称惊蛰。");
        }else if(month==3 && day>=20 && day<=21){
            solar_terms_name.setText("春分");
            solar_terms.setText("    3月20日-3月21日：这一天正当春季九十日之半，故曰“春分”。昼夜长度各半，冷热均衡，一些越冬作物开始进入春季生长阶段。");
        }

        else if(month==4 && day>=4 && day<=6){
            solar_terms_name.setText("清明");
            solar_terms.setText("    4月4日-4月6日：含有天气晴朗、草木萌发之意。此时气温渐暖，草木发芽，大地返青，也是春耕春种的好时节。");
        }
        else if(month==4 && day>=19 && day<=20){
            solar_terms_name.setText("谷雨");
            solar_terms.setText("    4月19日-4月20日：由于雨水增多，滋润田野，有利于农作物的生长，故有“雨生百谷”之说。");
        }

        else if(month==5 && day>=5 && day<=6){
            solar_terms_name.setText("立夏");
            solar_terms.setText("    5月5日-5月6日：标志着夏季的开始，视为气温升高的开端。此时万物生长旺盛，欣欣向荣。");
        }
        else if(month==5 && day>=20 && day<=22){
            solar_terms_name.setText("小满");
            solar_terms.setText("    5月20日-5月22日：其含义是夏熟作物籽料已经开始灌浆饱满，但尚未成熟，故称“小满”。");
        }

        else if(month==6 && day>=5 && day<=6){
            solar_terms_name.setText("芒种");
            solar_terms.setText("    6月5日-6月6日：芒,指某些禾本植物籽实的外壳上长的针状物。芒种指小麦等有芒作物即将成熟，可以采收留种了，也预示着农民开始了忙碌的田间生活。");
        }
        else if(month==6 && day>=21 && day<=22){
            solar_terms_name.setText("夏至");
            solar_terms.setText("    6月21日-6月22日：是全年中白昼最长、黑夜最短的一天，也说明即将进入炎热的夏季。");
        }

        else if(month==7 && day>=7 && day<=8){
            solar_terms_name.setText("小暑");
            solar_terms.setText("    7月7日-7月8日：属于“三伏”中的初伏，天气炎热、蒸闷。气温虽高，但还不是最热的时候，故称小暑。");
        }
        else if(month==7 && day>=22 && day<=23){
            solar_terms_name.setText("大暑");
            solar_terms.setText("   7月22日-7月23日：正值“中伏”前后，也是我国大部分地区一年中最热的时期，气温最高。");
        }

        else if(month==8 && day>=6 && day<=9){
            solar_terms_name.setText("立秋");
            solar_terms.setText("    8月6日-8月9日：预示着秋季即将开始，天气逐渐转凉。不过暑气并未尽散，还有气温较热的“秋老虎”之说。");
        }
        else if(month==8 && day>=22 && day<=24){
            solar_terms_name.setText("处暑");
            solar_terms.setText("    8月22日-8月24日：代表暑天即将结束，天气由炎热向凉爽过渡。");
        }

        else if(month==9 && day>=7 && day<=8){
            solar_terms_name.setText("白露");
            solar_terms.setText("    9月7日-9月8日：由于昼夜温差加大，水汽在草木上凝结成白色露珠，故称白露。");
        }
        else if(month==9 && day>=22 && day<=24){
            solar_terms_name.setText("秋分");
            solar_terms.setText("    9月22日-9月24日：与春分相同，昼夜几乎等长，处于整个秋天的中间。");
        }

        else if(month==10 && day>=7 && day<=9){
            solar_terms_name.setText("寒露");
            solar_terms.setText("    10月7日-10月9日：冷空气渐强，雨季结束，气温由凉转冷，开始出现露水，早晨和夜间会有地冷露凝的现象。");
        }
        else if(month==10 && day>=23 && day<=24){
            solar_terms_name.setText("霜降");
            solar_terms.setText("    10月23日-10月24日：由秋季过渡到冬季的节气，开始有霜冻的现象出现。");
        }

        else if(month==11 && day>=7 && day<=8){
            solar_terms_name.setText("立冬");
            solar_terms.setText("    11月7日-11月8日：标志着冬季的开始。田间的操作也随之结束，作物在收割后进行贮藏。");
        }
        else if(month==11 && day>=22 && day<=23){
            solar_terms_name.setText("小雪");
            solar_terms.setText("    11月22日-11月23日：大地呈现初冬的景象，但还没到大雪纷飞的时节。");
        }

        else if(month==12 && day>=7 && day<=8){
            solar_terms_name.setText("大雪");
            solar_terms.setText("    12月7日-12月8日：此时天气较冷，不仅降雪量增大，降雪范围也更广。");
        }
        else if(month==12 && day>=21 && day<=23){
            solar_terms_name.setText("冬至");
            solar_terms.setText("    12月21日-12月23日：与夏至相反，白昼最短，黑夜最长，开始“数九”，过了冬至,白昼就一天天地增长了。");
        }

        else if(month==1 && day>=5 && day<=6){
            solar_terms_name.setText("小寒");
            solar_terms.setText("    1月5日-1月6日：此时正值“三九”前后，大部分地区开始天寒地冻，但还没有到达寒冷的极点。");
        }
        else if(month==1 && day>=19 && day<=21){
            solar_terms_name.setText("大寒");
            solar_terms.setText("    1月19日-1月21日：一年当中最冷的一段时间，相对于小寒来说，标志着严寒的加剧。");
        }

        else{
            solar_terms_name.setText("24节气");
            solar_terms.setText("    24节气是民间反应天气变化的一种节令，划分为24个是为了更好的区分每个节气表现出来的天气特点，从立春到大寒，然后一个轮回一个轮回的交替。");
        }
    }
}