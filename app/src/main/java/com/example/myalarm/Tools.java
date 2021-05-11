package com.example.myalarm;

import android.util.Log;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class Tools {
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

    public static boolean isSameDay(Calendar c1, Calendar c2){
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        String s1 = timestampFormat.format(c1.getTime());
        String s2 = timestampFormat.format(c2.getTime());
        return Objects.equals(s1,s2);
    }

    public static SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    public static String toTimestamp(@NonNull Calendar calendar) {
        return timestampFormat.format(calendar.getTime());
    }
    public static Calendar toCalendar(@NonNull String timestamp){
        Calendar rtn = Calendar.getInstance();
        try {
            rtn.setTime(timestampFormat.parse(timestamp));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return rtn;
    }

    public static String toOnlyTimeStamp(String timestamp){
        return timestamp.substring(11,19);
    }
    public static String toOnlyTimeStamp(Calendar calendar){
        return toOnlyTimeStamp(toTimestamp(calendar));
    }
}
