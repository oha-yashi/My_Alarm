package com.example.myalarm;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Handler handler;
    LinearLayout scrollBody;
    FloatingActionButton addButton;

    Calendar today_calendar;

    Intent intent;
    PendingIntent sender;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(getMainLooper());
        scrollBody = findViewById(R.id.scrollBody);
        addButton = findViewById(R.id.addButton);

        today_calendar = Calendar.getInstance();

        intent = new Intent(MainActivity.this, AlarmReceiver.class);
        sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

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
//                if(day == 1) tv_day.setText(String.format(Locale.JAPAN,"%d/%d", month_this, day));
//                else tv_day.setText(String.valueOf(day));
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

                handler.post(()->{
                    scrollBody.addView(v);
                    Calendar c_copy = Calendar.getInstance();
                    c_copy.setTime(calendar.getTime());
                    refresh(c_copy);
                });

                calendar.add(Calendar.DATE,1);
            }
        }).start();
    }

    private View.OnClickListener addButtonClickListener = view -> {
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
                setAlarmDialog(pickedCalendar);
            }
        }, hour, minute, true);
        timePickerDialog.setTitle(String.format(Locale.JAPAN,"%d/%d/%d",
                pickedCalendar.get(Calendar.YEAR),pickedCalendar.get(Calendar.MONTH)+1,pickedCalendar.get(Calendar.DAY_OF_MONTH)));
        timePickerDialog.show();
    }

    private void setAlarmDialog(Calendar pickedCalendar){
        if(BuildConfig.DEBUG) {
            String s = "picked " + Tools.toTimestamp(pickedCalendar);
            Toast.makeText(this,s,Toast.LENGTH_LONG).show();
            View v = getLayoutInflater().inflate(R.layout.dialog_alarm_set,null);
            EditText et_note = v.findViewById(R.id.set_note);
            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch sw_isRing = v.findViewById(R.id.set_isRing);
            Spinner sp_tag = v.findViewById(R.id.set_tag);
            new AlertDialog.Builder(this).setTitle(s).setView(v)
                    .setPositiveButton("設定",(dialogInterface, i) -> {
                        setAlarm(pickedCalendar,et_note.getText().toString(), sw_isRing.isChecked(),sp_tag.getSelectedItem().toString());
                    })
                    .setNeutralButton("閉じる",null).show();
        }
    }

    private void setAlarm(Calendar pickedCalendar,String note,boolean isRing,String tag){
        DatabaseHelper.insertAlarm(this,pickedCalendar,note,isRing,tag);
//        Calendar nextRing = DatabaseHelper.getNextRingTime(this);
//        if( pickedCalendar.before(nextRing) ){
//            alarmManager.cancel(sender);
//            alarmManager.set(AlarmManager.RTC_WAKEUP,nextRing.getTimeInMillis(),sender);
//        }
        refresh(pickedCalendar);
    }

    private void refresh(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        View v = scrollBody.getChildAt(day-1); //OK!!
            if (v == null) {
                Tools.MyLog.d(Tools.toTimestamp(calendar) + "v==null");
            } else {
                LinearLayout wrapping = v.findViewById(R.id.alarm_wrapping);

                try (Cursor c = DatabaseHelper.newDatabase(this).rawQuery(
                        "SELECT * FROM alarm_list WHERE strftime('%m%d', ring_time) = strftime('%m%d', '" + Tools.toTimestamp(calendar) + "') ORDER BY ring_time",null)) {
                    c.moveToFirst();
                    for(int i=0; i<c.getCount(); i++){
                        ScheduleButton scheduleButton = new ScheduleButton(this,c.getString(1),c.getInt(0));
                        wrapping.addView(scheduleButton);
                        c.moveToNext();
                    }
                }
            }
    }
}