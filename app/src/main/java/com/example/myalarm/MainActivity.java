package com.example.myalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Handler handler;
    LinearLayout scrollBody;
    FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(getMainLooper());
        scrollBody = findViewById(R.id.scrollBody);
        addButton = findViewById(R.id.addButton);

        setLayout(Calendar.getInstance());
        MyLog.d();

        addButton.setOnClickListener(view -> {
            View v = scrollBody.getChildAt(new Random().nextInt(31)); //OK!!
            if (v == null) {
                MyLog.d("v==null");
            } else {
                LinearLayout wrapping = v.findViewById(R.id.alarm_wrapping);

                Button button = new Button(this);
                button.setText("test_button");

                wrapping.addView(button);
            }
        });
    }

    private void setLayout(Calendar calendar){
        new Thread(()->{
            calendar.set(Calendar.DATE,1);
            int month_this = calendar.get(Calendar.MONTH);
            while(calendar.get(Calendar.MONTH) == month_this){
                View v = getLayoutInflater().inflate(R.layout.table_row,null);

                TextView tv_day = v.findViewById(R.id.day);
                int day = calendar.get(Calendar.DATE);
                int DoW = calendar.get(Calendar.DAY_OF_WEEK);
                tv_day.setText(String.valueOf(day));
                if(DoW == Calendar.SUNDAY) tv_day.setTextColor(Color.RED);
                if(DoW == Calendar.SATURDAY)tv_day.setTextColor(Color.BLUE);

                handler.post(()->scrollBody.addView(v));

                calendar.add(Calendar.DATE,1);
            }
        }).start();
    }


    public static class MyLog {
        public static void d(String msg){
            if(!BuildConfig.DEBUG)return;
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
//            for(StackTraceElement sTE:elements) Log.d("MyLog",sTE.toString());
            StackTraceElement calledClass = elements[3];
            String tag = "MyLog " + calledClass.getMethodName() + " - (" + calledClass.getFileName() + ":" + calledClass.getLineNumber() + ")";
            Log.d(tag, msg);
        }

        public static void d(){
            // 上とは別々にgetStackTraceしないと、MyTool.MyLogで実行したことになってしまう
            if(!BuildConfig.DEBUG)return;
            StackTraceElement calledClass = Thread.currentThread().getStackTrace()[3];
            String tag = "MyLog " + calledClass.getMethodName() + " - (" + calledClass.getFileName() + ":" + calledClass.getLineNumber() + ")";
            Log.d(tag, "Logging!!");
        }

        public static void format(String format, Object... args){
            if(!BuildConfig.DEBUG)return;
            StackTraceElement calledClass = Thread.currentThread().getStackTrace()[3];
            String tag = "MyLog " + calledClass.getMethodName() + " - (" + calledClass.getFileName() + ":" + calledClass.getLineNumber() + ")";
            String msg = String.format(Locale.JAPAN,format,args);
            Log.d(tag, msg);
        }
    }
}