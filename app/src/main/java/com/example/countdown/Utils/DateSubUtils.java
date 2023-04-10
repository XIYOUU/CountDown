package com.example.countdown.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**计算2个年月日的相减后的剩余天数*/
public class DateSubUtils {

    /**获得剩余天数*/
    public static String getTwoDateSub(String s1,String s2) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Date d1 = format.parse(s1);
        Date d2 = format.parse(s2);

        long ms = d1.getTime() - d2.getTime();//这样得到的差值是毫秒级别
        long days = ms / (1000 * 60 * 60 * 24);
        return String.valueOf(days);
    }

//    public static void
}
