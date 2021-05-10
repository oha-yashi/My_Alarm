package com.example.myalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

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
        Tools.MyLog.d();

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
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int DoW = calendar.get(Calendar.DAY_OF_WEEK);
                tv_day.setText(String.valueOf(day));
                if(DoW == Calendar.SUNDAY) tv_day.setTextColor(Color.RED);
                if(DoW == Calendar.SATURDAY)tv_day.setTextColor(Color.BLUE);
                if(Tools.isSameDay(today_calendar,calendar)) tv_day.setBackgroundColor(Color.rgb(0,255,255));

                tv_day.setOnClickListener(view -> {
                    Calendar pickedCalendar = Calendar.getInstance();
                    pickedCalendar.set(year_this,month_this,day);
                    pickTimeDialog(pickedCalendar);
                });

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

        Calendar pickedCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this);
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatePicker datePicker = datePickerDialog.getDatePicker();
                pickedCalendar.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
                pickTimeDialog(pickedCalendar);
            }
        });
        datePickerDialog.show();
    };

    private void pickTimeDialog(Calendar pickedCalendar){
//        MyLog.format("picked %d/%d/%d", year, month+1, day);
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                pickedCalendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                pickedCalendar.set(Calendar.MINUTE, timePicker.getMinute());
                pickedCalendar.set(Calendar.SECOND, 0);
                setAlarm(pickedCalendar);
            }
        }, hour, minute, true);
        timePickerDialog.setTitle(String.format(Locale.JAPAN,"%d/%d/%d",
                pickedCalendar.get(Calendar.YEAR),pickedCalendar.get(Calendar.MONTH)+1,pickedCalendar.get(Calendar.DAY_OF_MONTH)));
        timePickerDialog.show();
    }

    private void setAlarm(Calendar pickedCalendar){
        if(BuildConfig.DEBUG) {
            String s = "picked " + Tools.toTimestamp(pickedCalendar);
            Toast.makeText(this,s,Toast.LENGTH_LONG).show();
        }
    }
}