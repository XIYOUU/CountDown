package com.example.countdown.Bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/*创建一个活动的实体*/
public class ActivityNameItem {
    @SerializedName("activity_name")
    String activity_name;
    @SerializedName("setting_day")
    String setting_day;
    @SerializedName("activity_tip")
    String activity_tip;

    public ActivityNameItem() {
    }

    public ActivityNameItem(String activity_name, String setting_day, String activity_tip) {
        this.activity_name = activity_name;
        this.setting_day = setting_day;
        this.activity_tip = activity_tip;
    }

    public String getActivity_name() {
        return activity_name;
    }

    public void setActivity_name(String activity_name) {
        this.activity_name = activity_name;
    }

    public String getSetting_day() {
        return setting_day;
    }

    public void setSetting_day(String setting_day) {
        this.setting_day = setting_day;
    }

    public String getActivity_tip() {
        return activity_tip;
    }

    public void setActivity_tip(String activity_tip) {
        this.activity_tip = activity_tip;
    }
}
