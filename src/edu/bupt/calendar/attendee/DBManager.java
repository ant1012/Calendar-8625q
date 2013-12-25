package edu.bupt.calendar.attendee;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 数据库Helper
 * 
 * (功能8) (功能9)
 * 
 * */

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        db = helper.getWritableDatabase();
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 添加参与者
     * 
     * (功能8)
     * 
     * */
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
     * 北邮ANT实验室
     * zzz
     * 
     * 添加短信提醒
     * 
     * (功能9)
     * 
     * */
    /**
     * add MsgAlert
     * 
     */
    public void add(MsgAlert msgAlert) {
        db.beginTransaction();
        try {
            db.execSQL("INSERT INTO MsgAlert VALUES(null, ?, ?)", new Object[] {
                    msgAlert.event_id, msgAlert.alert_time });
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 查询参与人的电话号码列表
     * 
     * (功能8)
     * 
     * */
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
     * 北邮ANT实验室
     * zzz
     * 
     * 查询参与人列表
     * 
     * (功能8)
     * 
     * */
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
     * 北邮ANT实验室
     * zzz
     * 
     * 查询某号码是否参与了某日程
     * 
     * (功能8)
     * 
     * */
    /**
     * query if a phone number is a attendee of event
     * 
     */
    public int queryPhone(String s) {
        int event_id = 0;
        AttendeePhone attendeePhone = new AttendeePhone();
        Cursor c = queryTheCursorOfPhoneNumber(s);
        c.moveToNext();
        event_id = c.getInt(c.getColumnIndex("event_id"));
        c.close();
        return event_id;
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 查询参与者数据表
     * 
     * (功能8)
     * 
     * */
    /**
     * query all AttendeePhones, return cursor
     * 
     */
    private Cursor queryTheCursor() {
        Cursor c = db.rawQuery("SELECT * FROM AttendeePhone", null);
        return c;
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 根据id查询参与者数据表
     * 
     * (功能8)
     * 
     * */
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
     * 北邮ANT实验室
     * zzz
     * 
     * 根据号码查询参与者数据表
     * 
     * (功能8)
     * 
     * */
    /**
     * query all AttendeePhones, return cursor
     * 
     */
    private Cursor queryTheCursorOfPhoneNumber(String s) {
        Cursor c = db.rawQuery("SELECT * FROM AttendeePhone WHERE phoneNumber="
                + s, null);
        return c;
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 删除参与者信息
     * 
     * (功能8)
     * 
     * */
    /**
     * delete one attendee
     * 
     */
    public void deleteAttendee(String event_id, String attendee) {
        db.execSQL("delete from AttendeePhone where event_id=" + event_id
                + " and phoneNumber=\"" + attendee + '\"'); // zzz 加引号 否则当号码中有空格时会引起sql语句错误
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 根据id查询短信提醒信息
     * 
     * (功能9)
     * 
     * */
    /**
     * query all AttendeePhones, return cursor
     * 
     */
    private Cursor queryTheCursorMsgalert(String s) {
        Cursor c = db.rawQuery("SELECT * FROM MsgAlert WHERE event_id=" + s,
                null);
        return c;
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 查询某日程在某时间是否要发送短信提醒
     * 
     * (功能9)
     * 
     * */
    /**
     * query for event id
     * 
     */
    public boolean queryMsgAlert(long event_id, long alert_time) {
        Cursor c = queryTheCursorMsgalert(String.valueOf(event_id));
        while (c.moveToNext()) {
            if (c.getInt(c.getColumnIndex("alert_time")) == alert_time) {
                c.close();
                return true;
            }
        }
        c.close();
        return false;
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 删除短信提醒信息
     * 
     * (功能8)
     * 
     * */
    /**
     * delete one msg alert
     * 
     */
    public void deleteMsgAlert(long event_id) {
        db.execSQL("delete from MsgAlert where event_id=" + event_id);
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 关闭数据库
     * 
     * */
    /**
     * close database
     */
    public void closeDB() {
        db.close();
    }
}
