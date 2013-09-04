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
     * query all AttendeePhones, return list
     * 
     */
    public List<AttendeePhone> query() {
        ArrayList<AttendeePhone> attendeePhones = new ArrayList<AttendeePhone>();
        Cursor c = queryTheCursor();
        while (c.moveToNext()) {
            AttendeePhone attendeePhone = new AttendeePhone();
            attendeePhone._id = c.getInt(c.getColumnIndex("_id"));
            attendeePhone.event_id = c.getInt(c.getColumnIndex("event_id"));
            attendeePhone.name = c.getString(c.getColumnIndex("name"));
            attendeePhone.phoneNumber = c.getString(c
                    .getColumnIndex("phoneNumber"));
            attendeePhones.add(attendeePhone);
        }
        c.close();
        return attendeePhones;
    }

    /**
     * query for event id
     * 
     */
    public List<AttendeePhone> query(String s) {
        ArrayList<AttendeePhone> attendeePhones = new ArrayList<AttendeePhone>();
        Cursor c = queryTheCursor(s);
        while (c.moveToNext()) {
            AttendeePhone attendeePhone = new AttendeePhone();
            attendeePhone._id = c.getInt(c.getColumnIndex("_id"));
            attendeePhone.event_id = c.getInt(c.getColumnIndex("event_id"));
            attendeePhone.name = c.getString(c.getColumnIndex("name"));
            attendeePhone.phoneNumber = c.getString(c
                    .getColumnIndex("phoneNumber"));
            attendeePhones.add(attendeePhone);
        }
        c.close();
        return attendeePhones;
    }

    /**
     * query all AttendeePhones, return cursor
     * 
     */
    private Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM AttendeePhone", null);
        return c;
    }

    /**
     * query all AttendeePhones, return cursor
     * 
     */
    private Cursor queryTheCursor(String s) {
        Cursor c = db.rawQuery("SELECT * FROM AttendeePhone WHERE event_id="
                + s, null);
        return c;
    }

    /**
     * delete one attendee
     * 
     */
    public void deleteAttendee(String event_id, String attendee) {
        db.execSQL("delete from AttendeePhone where event_id=" + event_id
                + " and phoneNumber=" + attendee);
    }

    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
