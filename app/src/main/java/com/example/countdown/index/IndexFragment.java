package com.example.countdown.index;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.countdown.Bean.ActivityNameItem;
import com.example.countdown.R;
import com.example.countdown.Utils.DateSubUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yanzhenjie.recyclerview.OnItemMenuClickListener;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuBridge;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.touch.OnItemMoveListener;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class IndexFragment extends Fragment {

    private View v;
    private TextView add_item;
    private SwipeRecyclerView rv_slide;
    private SlideAdapter mAdapter;
    private List<String> stringList;
    private List<ActivityNameItem> mRecycleViewList;
    private Button save_data;
    private Button read_date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_index, container, false);

        initView();
        isFirstUse();
        initSlideRecycleView();


        /*跳转到活动添加页面*/
        add_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddItemActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        return v;
    }

    public void initView() {
        add_item = (TextView) v.findViewById(R.id.add_item);
        rv_slide = (SwipeRecyclerView) v.findViewById(R.id.rv_slide);
        mRecycleViewList = new ArrayList<>();
    }

    /**
     * RecycleView的初始化和数据创建
     */
    /*初始化数据*/
    public void initSlideRecycleView() {
        /*初始化数据*/
        ReadData();

        /*布局和适配器*/
        rv_slide.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new SlideAdapter();

        rv_slide.setSwipeMenuCreator(mSwipeMenuCreator);        //菜单
        rv_slide.setOnItemMenuClickListener(mItemMenuClickListener);    //菜单点击删除
        //rv_slide.setOnItemMoveListener(mItemMoveListener);  // 监听拖拽，更新UI。

        /*设置间隔*/
        int space = 20;
        rv_slide.addItemDecoration(new SpacesItemDecoration(space));

        /*设置适配器和刷新数据*/
        rv_slide.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    /*从AddItemActivity中返回创建的活动的数据*/
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //这里是一个switch()方法，内置的变量是requestCode,即请求码
        switch (requestCode) {
            //第一个case是1，即对应之前startActivityResult()方法当中的请求码
            case 1:
                //如果里面的处理结果是和后面一个Activity的setResult()方法当中的第一个参数相同
                if (resultCode == 1) {
                    //那么就从返回时获取返回的数据
                    //这里使用getStringExtra(0方法获取数据，里面的参数对应下一个Activity中putExtra()方法中的第一个参数
                    String activity_title = data.getStringExtra("activity_title");
                    String activity_tip = data.getStringExtra("activity_tip");
                    String time_pick = data.getStringExtra("time_pick");
                    InsertDate(activity_title, time_pick, activity_tip);
//                    Toast.makeText(getActivity(), "activity_title:" + activity_title, Toast.LENGTH_SHORT).show();
                }
        }
    }


    /**
     * 可滑动的RecycleView
     */
    /*滑动item的适配器*/
    public class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.SlideViewHolder> {
        public class SlideViewHolder extends RecyclerView.ViewHolder {

            private final TextView activity_name;
            private final TextView setting_day;
            private final TextView tip;
            private final TextView residue_day;
            private final TextView residue;

            public SlideViewHolder(@NonNull View itemView) {
                super(itemView);
                activity_name = (TextView) itemView.findViewById(R.id.activity_name);
                setting_day = (TextView) itemView.findViewById(R.id.setting_day);
                tip = (TextView) itemView.findViewById(R.id.tip);
                residue_day = (TextView) itemView.findViewById(R.id.residue_day);
                residue = (TextView) itemView.findViewById(R.id.residue);
            }
        }


        @NonNull
        @Override
        public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.layout_add_item, null);
            return new SlideViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
            holder.activity_name.setText(mRecycleViewList.get(position).getActivity_name());
            holder.setting_day.setText(mRecycleViewList.get(position).getSetting_day());

            /*设置剩余时间*/
            try {
                //获取当前系统时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String format = df.format(new Date());
                int day = Integer.parseInt(DateSubUtils.getTwoDateSub(mRecycleViewList.get(position).getSetting_day(), format));
                /*如果天数小于0，则文字置灰*/
                if (day < 0) {
                    //System.out.println("第" + position + "项置灰：");
                    holder.residue_day.setText(-day + "");
                    holder.residue_day.setTextColor(Color.parseColor("#77666666"));
                    holder.residue.setText("天前");
                    holder.residue.setTextColor(Color.parseColor("#77666666"));
                    holder.residue.setBackgroundResource(R.drawable.style_radiu_line_grey);
                    holder.activity_name.setTextColor(Color.parseColor("#77666666"));
                    holder.setting_day.setTextColor(Color.parseColor("#77666666"));
                    holder.tip.setTextColor(Color.parseColor("#77666666"));
                }
                /*如果天数大于0*/
                else {
                    holder.residue_day.setText(day + "");
                    holder.residue_day.setTextColor(Color.parseColor("#8A000000"));
                    holder.residue.setText("天后");
                    holder.residue.setTextColor(Color.parseColor("#f0f0f4"));
                    holder.activity_name.setTextColor(Color.parseColor("#e9e7ef"));
                    holder.setting_day.setTextColor(Color.parseColor("BLACK"));
                    holder.tip.setTextColor(Color.parseColor("#8A000000"));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            /*设置备注*/
            if (mRecycleViewList.get(position).getActivity_tip().isEmpty()) {
                holder.tip.setText("备注：无备注");
            } else {
                holder.tip.setText("备注：" + mRecycleViewList.get(position).getActivity_tip());
            }


        }

        @Override
        public int getItemCount() {
            return mRecycleViewList.size();
        }
    }

    /*设置item间隔*/
    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view,
                                   RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            // Add top margin only for the first item to avoid double space between items
            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }

    // 创建删除菜单：
    SwipeMenuCreator mSwipeMenuCreator = new SwipeMenuCreator() {
        @Override
        public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
            //SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity());
            // 1. MATCH_PARENT 自适应高度，保持和Item一样高;
            // 2. 指定具体的高，比如80;
            // 3. WRAP_CONTENT，自身高度，不推荐;
            int height = 170;
            int width = 120;
            // 添加右侧的，如果不添加，则右侧不会出现菜单。
            {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getActivity()).setBackgroundColor(Color.parseColor("#FF0000"))
                        .setText("删除")
                        .setTextColor(Color.WHITE)
                        .setWidth(width)
                        .setHeight(height);
                rightMenu.addMenuItem(deleteItem);// 添加菜单到右侧。
            }
        }
    };
    // 菜单点击监听。
    OnItemMenuClickListener mItemMenuClickListener = new OnItemMenuClickListener() {
        @Override
        public void onItemClick(SwipeMenuBridge menuBridge, int position) {
            // 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
            menuBridge.closeMenu();

            // 左侧还是右侧菜单：
            int direction = menuBridge.getDirection();
            // 菜单在Item中的Position：
            int menuPosition = menuBridge.getPosition();
            /*删除第position项*/
            mRecycleViewList.remove(position);
            SaveData(mRecycleViewList);
            mAdapter.notifyItemRemoved(position);
        }
    };
    /*侧滑和删除*/
    OnItemMoveListener mItemMoveListener = new OnItemMoveListener() {


        @Override
        public boolean onItemMove(RecyclerView.ViewHolder srcHolder, RecyclerView.ViewHolder targetHolder) {
            // 此方法在Item拖拽交换位置时被调用。
            // 第一个参数是要交换为之的Item，第二个是目标位置的Item。

            // 交换数据，并更新adapter。
            int fromPosition = srcHolder.getAdapterPosition();
            int toPosition = targetHolder.getAdapterPosition();
            Collections.swap(stringList, fromPosition, toPosition);
            mAdapter.notifyItemMoved(fromPosition, toPosition);


            // 返回true，表示数据交换成功，ItemView可以交换位置。
            return true;
        }

        @Override
        public void onItemDismiss(RecyclerView.ViewHolder srcHolder) {
            // 此方法在Item在侧滑删除时被调用。

            // 从数据源移除该Item对应的数据，并刷新Adapter。
            int position = srcHolder.getAdapterPosition();
            stringList.remove(position);
            mAdapter.notifyItemRemoved(position);

        }
    };

    /**
     * 文件操作
     */
    /*保存文件数据*/
    public void SaveData(List<ActivityNameItem> list) {
        try {
            //用MODE_PRIVAT模式，创建一个hxy.txt文件
            FileOutputStream outputStream = getActivity().openFileOutput("myActivityItem.txt", getActivity().MODE_PRIVATE);
            //写入方法和冲刷方法
            Gson gson = new Gson();
            String s = gson.toJson(list);
            outputStream.write(s.getBytes());
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*读取文件数据*/
    public void ReadData() {
        try {
            // 打开文件得到一个只读的输入流，
            FileInputStream fis = getActivity().openFileInput("myActivityItem.txt");
            // 将文件内容存放到byte数组中
            byte[] buffer = new byte[fis.available()];

            // 读取数组中的内容
            fis.read(buffer);
            fis.close();

            // 将数组内容存放到字符串中，并显示出来
            String s = new String(buffer);
            Gson gson = new Gson();
            List<ActivityNameItem> list = gson.fromJson(s, new TypeToken<List<ActivityNameItem>>() {
            }.getType());
            mRecycleViewList = list;
            /*重新排序*/
            AscSort();
            mAdapter.notifyDataSetChanged();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*插入文件数据*/
    public void InsertDate(String activity_title, String time_pick, String activity_tip) {
        ActivityNameItem act = new ActivityNameItem(activity_title, time_pick, activity_tip);
        mRecycleViewList.add(act);
        AscSort();
        SaveData(mRecycleViewList);
        if (mAdapter != null) {

            mAdapter.notifyDataSetChanged();
        }

    }

    /*重新排序集合*/
    public void AscSort() {
        /**根据剩余天数按升序排序*/
        Collections.sort(mRecycleViewList, new Comparator<ActivityNameItem>() {

            @Override
            public int compare(ActivityNameItem a1, ActivityNameItem a2) {

                //获取当前系统时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                int day1 = 0;
                int day2 = 0;
                String format = df.format(new Date());
                try {
                    day1 = Integer.parseInt(DateSubUtils.getTwoDateSub(a1.getSetting_day(), format));
                    day2 = Integer.parseInt(DateSubUtils.getTwoDateSub(a2.getSetting_day(), format));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return day1 - day2;
            }
        });

        /**小于0的排在后面*/
        /*找出几个小于0的*/
        int j = 0;                /*记录有几个日期是小于0的*/
        for (int i = 0; i < mRecycleViewList.size(); i++) {
            try {
                //获取当前系统时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                String format = df.format(new Date());
                if (Integer.parseInt(DateSubUtils.getTwoDateSub(mRecycleViewList.get(i).getSetting_day(), format)) < 0) {
                    j++;
                } else {
                    break;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        //System.out.println(j);
        /*将小于0的日期按升序放到最后面，同时剩余天数文本颜色置灰*/
        while (j != 0) {
            ActivityNameItem a = mRecycleViewList.get(j - 1);
            mRecycleViewList.remove(j - 1);
            mRecycleViewList.add(a);
            j--;
        }
    }


    /**
     * 是否第一次使用app如果是第一次使用app，则添加一条引导信息
     */
    public void isFirstUse() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("isFirstUse", getActivity().MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", false);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        System.out.println(isFirstRun);
        if (isFirstRun == false) {  //第一次运行
            InsertDate("使用指南", "2022-4-22", "左滑试试");
            ReadData();

            edit.putBoolean("isFirstRun", true);
            edit.commit();
        } else {                      //不是第一次运行

        }
    }

}