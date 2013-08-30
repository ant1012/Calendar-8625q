package edu.bupt.calendar.attendee;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * add attendeePhone
     * 
     * @param attendeePhones
     */
    public void add(List<AttendeePhone> attendeePhones) {
        db.beginTransaction();
        try {
            for (AttendeePhone attendeePhone : attendeePhones) {
                db.execSQL("INSERT INTO AttendeePhone VALUES(null, ?, ?, ?)",
                        new Object[] { attendeePhone.event_id,
                                attendeePhone.name, attendeePhone.phoneNumber });
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * delete
     * 
     * @param attendeePhone
     */
    public void deleteOldPerson(AttendeePhone attendeePhone) {
        db.delete("AttendeePhone", "phoneNumber = ?",
                new String[] { String.valueOf(attendeePhone.phoneNumber) });
    }

    /**
     * query all AttendeePhones, return list
     * 
     * @return List<Person>
     */
    public List<AttendeePhone> query() {
        ArrayList<AttendeePhone> attendeePhones = new ArrayList<AttendeePhone>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            AttendeePhone attendeePhone = new AttendeePhone();
            attendeePhone._id = c.getInt(c.getColumnIndex("_id"));
            attendeePhone.event_id = c.getInt(c.getColumnIndex("event_id"));
            attendeePhone.name = c.getString(c.getColumnIndex("name"));
            attendeePhone.phoneNumber = c.getString(c.getColumnIndex("phoneNumber"));
            attendeePhones.add(attendeePhone);
        }
        c.close();
        return attendeePhones;
    }

    /**
     * query all AttendeePhones, return cursor
     * 
     * @return Cursor
     */
    public Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM AttendeePhone", null);
        return c;
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
