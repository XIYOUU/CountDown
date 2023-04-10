package com.example.countdown.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
/**自定义电池*/
public class BatteryView extends View {
    private float percent = 1.0f;

    public BatteryView(Context context, AttributeSet set) {
        super(context, set);
    }

    @Override
    // 重写该方法,进行绘图
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 电池电量外面的大白框
        Paint paint = new Paint();
        // 电池电量里面的绿色
        Paint paint1 = new Paint();
        // 电池头部
        Paint paint2 = new Paint();
        // 去锯齿
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        // 大于百分之20时绿色，否则为红色
        if (percent > 0.2f) {
            paint.setColor(Color.GREEN);
        } else {
            paint.setColor(Color.RED);
        }

        paint1.setAntiAlias(true);
        paint1.setStyle(Paint.Style.STROKE);
        paint1.setStrokeWidth(4);
        paint1.setColor(Color.WHITE);
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setColor(Color.WHITE);
        int a = getWidth() - 4;
        int b = getHeight() - 4;
        // 根据电量百分比画图
        float d = a * percent;
        RectF re1 = new RectF(4, 4, d - 10, b);
        RectF re2 = new RectF(0, 0, a - 6, b + 4);
        RectF re3 = new RectF(a - 8, b / 2 - 8, a, b - 6);
        // 绘制圆角矩形
        canvas.drawRect(re1, paint);
        canvas.drawRect(re2, paint1);
        canvas.drawRect(re3, paint2);
    }

    // 每次检测电量都重绘，在检测电量的地方调用
    public synchronized void setProgress(int percent) {
        this.percent = (float) (percent / 100.0);
        postInvalidate();
    }

}
