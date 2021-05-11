package com.example.myalarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "AlarmDatabase.db";
    /**
     * ring_time - 鳴動時間
     * set_time - 設定時間
     * note - メモ
     * isRing - 0/1 鳴動するか
     * tag - @Nullable タグ
     */
    private static final String[] DATABASE_COLUMNS = {
            "_id INTEGER PRIMARY KEY AUTOINCREMENT",
            "ring_time", "set_time", "note", "isRing", "tag"
    };
    private static final String TABLE_NAME = "alarm_list";

    private static String QUERY_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" + String.join(", ", DATABASE_COLUMNS) + ")";

    private DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(QUERY_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Tools.MyLog.d();
        try{ sqLiteDatabase.execSQL("DROP TABLE alarm_list"); }
        catch(SQLException e){ e.printStackTrace(); }
        onCreate(sqLiteDatabase);
    }

    public static SQLiteDatabase newDatabase(Context context){
        return new DatabaseHelper(context).getWritableDatabase();
    }

    public static void insertAlarm(Context context, Calendar ring_time, @Nullable String note, boolean isRing, @NonNull String tag){
        try(SQLiteDatabase db = newDatabase(context)){
            ContentValues cv = new ContentValues();
            cv.put("ring_time", Tools.toTimestamp(ring_time));
            cv.put("set_time", Tools.toTimestamp(Calendar.getInstance()));
            cv.put("note", note);
            cv.put("isRing", isRing ? 1 : 0);
            cv.put("tag", tag);
            db.insert(TABLE_NAME,null,cv);
        }
    }

    public static Calendar getNextRingTime(Context context){
        try(SQLiteDatabase db = newDatabase(context)){
            try (Cursor c = db.rawQuery("SELECT ring_time FROM alarm_list WHERE isRing=1 ORDER BY ring_time LIMIT 1", null)) {
                if (c.getCount() == 0) return null;
                return Tools.toCalendar(c.getString(0));
            }
        }
    }

    public static String toString(Context context, int _id){
        StringBuilder rtn = new StringBuilder("null");
        try(Cursor cursor = newDatabase(context).rawQuery("SELECT * FROM alarm_list WHERE _id="+_id,null)){
            rtn.delete(0,rtn.length());
            for(int i=0; i<cursor.getColumnCount(); i++){
                rtn.append(cursor.getString(i));
                rtn.append(",");
            }
        }
        return rtn.toString();
    }
}
