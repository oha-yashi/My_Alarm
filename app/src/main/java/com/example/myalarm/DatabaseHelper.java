package com.example.myalarm;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AlarmDatabase.db";
    private static final String[] DATABASE_COLUMNS = {
            "_id INTEGER PRIMARY KEY AUTOINCREMENT",
            "ring_time", "note", "tag DEFAULT 0"
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
        try{ sqLiteDatabase.execSQL("DROP TABLE alarm_list"); }
        catch(SQLException e){ e.printStackTrace(); }
        onCreate(sqLiteDatabase);
    }

    public static SQLiteDatabase newDatabase(Context context){
        SQLiteDatabase sqLiteDatabase = new DatabaseHelper(context).getWritableDatabase();
        return sqLiteDatabase;
    }

    public static void insertAlarm(Calendar c, String note, int tag){
        String timestamp = Tools.toTimestamp(c);
    }
}
