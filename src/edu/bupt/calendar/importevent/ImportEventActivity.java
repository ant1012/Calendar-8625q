package edu.bupt.calendar.importevent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.android.calendarcommon.ICalendar;
import com.android.calendarcommon.ICalendar.FormatException;
import com.android.calendarcommon.ICalendar.Property;

import edu.bupt.calendar.CalendarEventModel;
import edu.bupt.calendar.CalendarEventModel.Attendee;
import edu.bupt.calendar.GeneralPreferences;
import edu.bupt.calendar.R;
import edu.bupt.calendar.Utils;
import edu.bupt.calendar.attendee.AttendeePhone;
import edu.bupt.calendar.attendee.DBManager;
import edu.bupt.calendar.event.AttendeesView;
import edu.bupt.calendar.event.EditEventHelper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract.Events;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 导入日程的Activity，由ACTION_VIEW调起，解析vcard文件并显示内容，提供保存方法(功能14)
 * 
 * */
public class ImportEventActivity extends Activity {
    private String TAG = "ImportEventActivity";
    private View view;
    private TextView textviewTitle;
    private TextView textViewDatetime;
    private TextView textViewDateendtime;
    private TextView textViewRepeat;
    private TextView textViewWhere;
    private TextView textViewDisc;
    private AttendeesView attendeesView;
    private ICalendar.Component parent;
    private ICalendar.Component child;
    private Context context = this;
    private CalendarEventModel model;
    private int event_id = 1;

    // zzz 用来保存日程解析结果的全局变量
    private String event_title = null;
    private long event_datetime = 0;
    private String event_where = null;
    private String event_disc = null;
    private String event_tz = null;
    private long event_dateendtime;

    private ArrayList<String> event_attendees = new ArrayList<String>();
    private Context mContext;
    private static DBManager mgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_event);

        // zzz 控件初始化
        textviewTitle = (TextView) findViewById(R.id.title);
        textViewDatetime = (TextView) findViewById(R.id.when_datetime);
        textViewWhere = (TextView) findViewById(R.id.where);
        textViewDisc = (TextView) findViewById(R.id.disc);
        attendeesView = (AttendeesView) findViewById(R.id.long_attendee_list);

        // zzz 读取打开的文件到数组，后面对此数组进行分析
        String s = getStringFromFile();
        if (s != null) {
            // zzz 读取pareant事件
            parent = getEventFromString(s);
            // just test first event
            // zzz 读取pareant事件中的第一个子事件，因为分享日程时只打包了一个
            child = parent.getComponents().get(0);
            // zzz 读取日程内容，读取结果保存在了全局变量中
            getDetails(child);
        }

        // zzz 显示标题
        textviewTitle.setText(event_title);
        // zzz 时间格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'/'MM'/'dd'-'HH':'mm");

        // zzz 显示时间
        textViewDatetime.setText(simpleDateFormat.format(new Date(event_datetime)));
        // zzz 显示地点
        textViewWhere.setText(event_where);
        // zzz 显示描述
        textViewDisc.setText(event_disc);

        mContext = getApplicationContext();
    }

    /** zzz */
    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 从打开的文件中读取数组
     * 
     * 此Activity通过打开文件的ACTION_VIEW调起，从intent中可以拿到文件的信息
     * 
     * */
    private String getStringFromFile() {
        // zzz 获取intent中的信息，包含了文件路经
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            // zzz 解析数据，拿到文件路径
            Uri uri = Uri.parse(data.toString());
            String filename = uri.getPath();
            File file = new File(filename);
            Log.i(TAG, "filename - " + filename);
            if (file.exists()) {
                FileInputStream fin;
                try { // zzz 打开文件读取数组
                    fin = new FileInputStream(file);
                    int buffersize = fin.available();
                    byte buffer[] = new byte[buffersize];
                    fin.read(buffer);
                    fin.close();
                    String vcal = new String(buffer);

                    // ICalendar.Component parent =
                    // ICalendar.parseCalendar(vcal);
                    //
                    // Log.i(TAG, parent.getName());
                    // Log.i(TAG, parent.getPropertyNames().toString());
                    // for (ICalendar.Component child : parent.getComponents())
                    // {
                    // Log.i(TAG, child.toString());
                    //
                    // }
                    Log.i(TAG, vcal);
                    return vcal;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // zzz 如果是从不合法的路径调起，可能会无法打开文件
                Log.d(TAG, "file_not_exist");
                Toast.makeText(this, R.string.file_not_exist, Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 解析日程，获取详细信息，利用ICalendar.Component类中提供的方法
     * 
     * */
    private void getDetails(ICalendar.Component c) {

        // zzz 可以直接通过ICalendar.Component类中提供的方法获取属性值
        event_title = c.getFirstProperty("SUMMARY").getValue();

        ICalendar.Property dtstart_prop = c.getFirstProperty("DTSTART");
        ICalendar.Property dtend_prop = c.getFirstProperty("DTEND");

        // zzz 根据时区格式化时间信息
        event_tz = dtstart_prop.getFirstParameter("TZID").value;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        try {
            event_datetime = simpleDateFormat.parse(dtstart_prop.getValue()).getTime();
            event_dateendtime = simpleDateFormat.parse(dtend_prop.getValue()).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        event_where = child.getFirstProperty("LOCATION").getValue();
        event_disc = child.getFirstProperty("DISCRIPTION").getValue();

        // attendee
        // zzz 参与者的电话是扩展的属性，可能存在多个
        try {
            if (!child.getProperties("X-ATTENDEE-PHONE").isEmpty()) {
                for (Property pr : child.getProperties("X-ATTENDEE-PHONE")) {
                    Log.i(TAG, pr.getValue());
                    event_attendees.add(pr.getValue());
                    attendeesView.addAttendees(pr.getValue());
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "no attendee data");
        }
        return;
    }


    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 从数组中取出ICalendar.Component类
     * 
     * */
    private ICalendar.Component getEventFromString(String s) {
        ICalendar.Component c = null;
        try {
            // zzz 利用ICalendar类提供的方法解析
            c = ICalendar.parseCalendar(s);
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return c;
    }

    // private CalendarEventModel buildTestModel() {
    // model = new CalendarEventModel();
    // model.mId = event_id;
    // model.mTitle = event_title;
    // model.mDescription = event_disc;
    // model.mLocation = event_where;
    // // model.mAllDay = true;
    // // model.mHasAlarm = false;
    // model.mCalendarId = 0;
    // model.mStart = event_datetime; // Monday, May 3rd, local Time
    // // model.mDuration = "P3652421990D";
    // // The model uses the local timezone for allday
    // model.mTimezone = "UTC";
    // // model.mRrule = "FREQ=DAILY;WKST=SU";
    // // model.mSyncId = "unique per calendar stuff";
    // model.mAvailability = 0;
    // model.mAccessLevel = 2; // This is one less than the values written if
    // // >0
    // model.mOwnerAccount = Utils.getSharedPreference(context,
    // GeneralPreferences.KEY_DEFAULT_CALENDAR, "");
    // // model.mHasAttendeeData = true;
    // // model.mEventStatus = Events.STATUS_CONFIRMED;
    // // model.mOrganizer = "organizer@gmail.com";
    // // model.mGuestsCanModify = false;
    // // model.mModelUpdatedWithEventCursor = true;
    // return model;
    // }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.import_event, menu);
        return true;
    }


    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 保存日程，菜单项被设置为showasaction作为按钮显示
     * 
     * */
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_done) {
            Log.d(TAG, "action_done");
            // save
            String calId = "";
            Cursor userCursor = getContentResolver().query(Uri.parse("content://com.android.calendar/calendars"), null,
                    null, null, null);
            if (userCursor.getCount() > 0) {
                userCursor.moveToFirst();
                calId = userCursor.getString(userCursor.getColumnIndex("_id")); // zzz 获取calendar_id用于存储

            }
            userCursor.close();

            ContentValues event = new ContentValues();
            event.put("title", event_title);
            event.put("description", event_disc);
            event.put("eventLocation", event_where);
            event.put("calendar_id", calId);
            event.put("dtstart", event_datetime);
            event.put("dtend", event_dateendtime);
            event.put("hasAlarm", 1);
            event.put("eventTimezone", event_tz);
            event.put("eventStatus", 1);
            // if (event_attendees.isEmpty()) {
            // event.put("hasAttendeeData", 0);
            // } else {
            event.put("hasAttendeeData", 1);
            // }

            // zzz 插入操作
            Uri newEvent = getContentResolver().insert(Uri.parse("content://com.android.calendar/events"), event);
            long id = Long.parseLong(newEvent.getLastPathSegment()); // zzz 拿到id用于后面的参与人的保存
            ContentValues values = new ContentValues();
            values.put("event_id", id);
            // reminder
            // values.put("minutes", 10);
            // getContentResolver().insert(
            // Uri.parse("content://com.android.calendar/reminders"),
            // values);

            Log.d(TAG, "import success");

            // attendee
            // Cursor cursor = mContext.getContentResolver().query(
            // Events.CONTENT_URI, new String[] { "MAX(_id) as max_id" },
            // null, null, "_id");
            // cursor.moveToFirst();
            // long max_val = cursor.getLong(cursor.getColumnIndex("max_id"));
            // Log.i(TAG, "max_val - " + max_val);
            // cursor.close();
            
            // zzz 保存参与人，由于不在系统提供的provider中保存，需要另外的数据库
            mgr = new DBManager(mContext);

            for (String s : event_attendees) {
                // zzz 导入操作前可以删除联系人，即isMarkAsRemoved
                // zzz 第一条是空的，从第二条开始
                if (attendeesView.isMarkAsRemoved(event_attendees.indexOf(s) + 1)) { // first
                                                                                     // one
                                                                                     // is
                                                                                     // a
                                                                                     // seperator
                    Log.i(TAG, "isMarkAsRemoved");
                    continue;
                }
                Attendee attendee = new Attendee(s, s);
                ArrayList<AttendeePhone> attendeePhones = new ArrayList<AttendeePhone>();
                AttendeePhone attendeePhone = new AttendeePhone(id, attendee.mName, attendee.mEmail);
                attendeePhones.add(attendeePhone);
                mgr.add(attendeePhones); // zzz 插入操作
                Log.d(TAG, "import attendee");
            }
            mgr.closeDB();

            // zzz 倒入成功的提示
            Toast.makeText(this, R.string.title_activity_import_event, Toast.LENGTH_SHORT).show();
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
