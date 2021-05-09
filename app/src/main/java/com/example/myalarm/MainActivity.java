package com.example.myalarm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    Handler handler;
    LinearLayout scrollBody;
    FloatingActionButton addButton;

    Calendar today_calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(getMainLooper());
        scrollBody = findViewById(R.id.scrollBody);
        addButton = findViewById(R.id.addButton);

        today_calendar = Calendar.getInstance();

        setLayout(Calendar.getInstance());
        MyLog.d();

        addButton.setOnClickListener(addButtonClickListener);
    }

    private void setLayout(Calendar calendar){
        new Thread(()->{
            calendar.set(Calendar.DATE,1);
            int year_this = calendar.get(Calendar.YEAR);
            int month_this = calendar.get(Calendar.MONTH);
            while(calendar.get(Calendar.MONTH) == month_this){
                View v = getLayoutInflater().inflate(R.layout.table_row,null);

                TextView tv_day = v.findViewById(R.id.day);
                int day = calendar.get(Calendar.DATE);
                int DoW = calendar.get(Calendar.DAY_OF_WEEK);
                tv_day.setText(String.valueOf(day));
                if(DoW == Calendar.SUNDAY) tv_day.setTextColor(Color.RED);
                if(DoW == Calendar.SATURDAY)tv_day.setTextColor(Color.BLUE);
                if(isSameDay(today_calendar,calendar)) tv_day.setBackgroundColor(Color.rgb(0,255,255));

                tv_day.setOnClickListener(view -> setAlarm(year_this,month_this,day));

                /**
                 * アラームデータを追加
                 */

                handler.post(()->scrollBody.addView(v));

                calendar.add(Calendar.DATE,1);
            }
        }).start();
    }

    private View.OnClickListener addButtonClickListener = view -> {
//            View v = scrollBody.getChildAt(new Random().nextInt(31)); //OK!!
//            if (v == null) {
//                MyLog.d("v==null");
//            } else {
//                LinearLayout wrapping = v.findViewById(R.id.alarm_wrapping);
//
//                Button button = new Button(this);
//                button.setText("test_button");
//
//                wrapping.addView(button);
//            }

        final int[] date = {0,0,0};
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                date[0] = datePickerDialog.getDatePicker().getYear();
                date[1] = datePickerDialog.getDatePicker().getMonth();
                date[2] = datePickerDialog.getDatePicker().getDayOfMonth();
                setAlarm(date[0],date[1],date[2]);
            }
        });
        datePickerDialog.show();
    };

    private void setAlarm(int year, int month, int day){
//        MyLog.format("picked %d/%d/%d", year, month+1, day);
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        Context context_this = this;
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                int getH = timePicker.getHour();
                int getM = timePicker.getMinute();
                String s = String.format(Locale.JAPAN, "picked %d/%d/%d %2d:%02d",year,month+1,day,getH,getM);
                Toast.makeText(context_this,s,Toast.LENGTH_LONG).show();
            }
        }, hour, minute, true);
        timePickerDialog.setTitle(String.format(Locale.JAPAN,"%d/%d/%d",year,month+1,day));
        timePickerDialog.show();
    }

    public static class MyLog {
        public static void d(String msg){
            if(!BuildConfig.DEBUG)return;
            StackTraceElement[] elements = Thread.currentThread().getStackTrace();
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

    private boolean isSameDay(Calendar c1, Calendar c2){
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String s1 = timestampFormat.format(c1.getTime());
        String s2 = timestampFormat.format(c2.getTime());
        return Objects.equals(s1,s2);
    }
}