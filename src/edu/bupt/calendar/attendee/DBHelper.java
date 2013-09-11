package edu.bupt.calendar.attendee;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "attendeePhone.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase arg0) {
        arg0.execSQL("CREATE TABLE IF NOT EXISTS AttendeePhone"//
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "//
                + "event_id INTEGER, "//
                + "name VARCHAR, "//
                + "phoneNumber VARCHAR)");
        arg0.execSQL("CREATE TABLE IF NOT EXISTS MsgAlert"//
                + "(_id INTEGER PRIMARY KEY AUTOINCREMENT, "//
                + "event_id INTEGER, "//
                + "alert_time INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE AttendeePhone ADD COLUMN other VARCHAR");
    }

}
