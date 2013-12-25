/** zzz */
package edu.bupt.calendar.attendee;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 日程参与者的ContentProvider，使其他应用(通讯录)可以获取日程参与者的信息
 * 
 * (功能8) (通讯录功能24)
 * 
 * */

public class MyContentProvider extends ContentProvider {
    // private DBManager mgr;

    private static SQLiteDatabase database;
    public static final Uri CONTENT_URI = Uri
            .parse("content://edu.bupt.calendar.attendee"); // zzz 通过Uri来调用

    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public boolean onCreate() {
        // mgr = new DBManager(getContext());
        database = new DBHelper(getContext()).getReadableDatabase();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {

        return database.query("AttendeePhone", projection, selection,
                selectionArgs, null, null, sortOrder); // 主要提供查询功能，其他功能暂时没有需求

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        return 0;
    }

}
