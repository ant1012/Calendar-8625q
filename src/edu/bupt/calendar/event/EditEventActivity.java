/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.bupt.calendar.event;

import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_ALL_DAY;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.People.Phones;
import android.provider.ContactsContract;
import android.text.format.Time;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import edu.bupt.calendar.AbstractCalendarActivity;
import edu.bupt.calendar.CalendarController;
import edu.bupt.calendar.CalendarController.EventInfo;
import edu.bupt.calendar.R;
import edu.bupt.calendar.Utils;

/**
 * 北邮ANT实验室
 * zzz
 * 
 * 日程编辑的Activity
 * 
 * 此文件取自codeaurora提供的适用于高通8625Q的android 4.1.2源码，有修改
 * 
 * */

public class EditEventActivity extends AbstractCalendarActivity {
    private static final String TAG = "EditEventActivity";

    private static final boolean DEBUG = false;

    private static final String BUNDLE_KEY_EVENT_ID = "key_event_id";

    private static boolean mIsMultipane;

    private EditEventFragment mEditFragment;

    private EventInfo mEventInfo;

    /** zzz */
    // zzz 参与者的信息
    public static String number;
    public static String name;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.simple_frame_layout);

        mEventInfo = getEventInfoFromIntent(icicle);

        mEditFragment = (EditEventFragment) getFragmentManager().findFragmentById(R.id.main_frame);

        mIsMultipane = Utils.getConfigBool(this, R.bool.multiple_pane_config);

        if (mIsMultipane) {
            getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE,
                    ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
            getActionBar().setTitle(mEventInfo.id == -1 ? R.string.event_create : R.string.event_edit);
        } else {
            getActionBar().setDisplayOptions(
                    ActionBar.DISPLAY_SHOW_CUSTOM,
                    ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE
                            | ActionBar.DISPLAY_SHOW_CUSTOM);
        }

        if (mEditFragment == null) {
            Intent intent = null;
            if (mEventInfo.id == -1) {
                intent = getIntent();
            }

            mEditFragment = new EditEventFragment(mEventInfo, false, intent);

            mEditFragment.mShowModifyDialogOnLaunch = getIntent().getBooleanExtra(
                    CalendarController.EVENT_EDIT_ON_LAUNCH, false);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, mEditFragment);
            ft.show(mEditFragment);
            ft.commit();
        }
    }

    private EventInfo getEventInfoFromIntent(Bundle icicle) {
        EventInfo info = new EventInfo();
        long eventId = -1;
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            try {
                eventId = Long.parseLong(data.getLastPathSegment());
            } catch (NumberFormatException e) {
                if (DEBUG) {
                    Log.d(TAG, "Create new event");
                }
            }
        } else if (icicle != null && icicle.containsKey(BUNDLE_KEY_EVENT_ID)) {
            eventId = icicle.getLong(BUNDLE_KEY_EVENT_ID);
        }

        boolean allDay = intent.getBooleanExtra(EXTRA_EVENT_ALL_DAY, false);

        long begin = intent.getLongExtra(EXTRA_EVENT_BEGIN_TIME, -1);
        long end = intent.getLongExtra(EXTRA_EVENT_END_TIME, -1);
        if (end != -1) {
            info.endTime = new Time();
            if (allDay) {
                info.endTime.timezone = Time.TIMEZONE_UTC;
            }
            info.endTime.set(end);
        }
        if (begin != -1) {
            info.startTime = new Time();
            if (allDay) {
                info.startTime.timezone = Time.TIMEZONE_UTC;
            }
            info.startTime.set(begin);
        }
        info.id = eventId;

        if (allDay) {
            info.extraLong = CalendarController.EXTRA_CREATE_ALL_DAY;
        } else {
            info.extraLong = 0;
        }
        return info;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utils.returnToCalendarHome(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** zzz */
    /**
     * 北邮ANT实验室
     * zzz
     * 
     * 如果在EditEventFragment中点击了选择参与人的Button，选择完成后返回会调起此Acitivity
     * 需要重写onActivityResult方法拿到选择的参与者的信息
     * 
     * */
    // back from choosing a attendee phone number from system cantact app, and
    // then send it to EditEventFragment
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
        case RESULT_OK:
            if (resultCode == Activity.RESULT_OK) { // zzz 选择了参与者

                Uri contactData = data.getData();
                Cursor c = getContentResolver().query(contactData, null, null, null, null);
                // zzz 应该只有一条，先查看此联系人是否有电话
                if (c.moveToFirst()) {
                    String id = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts._ID)); // zzz
                                                                                                     // id查询电话号码时会用到
                    String hasPhone = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)); // zzz
                                                                                                                 // 是否存在号码
                    c.close();

                    if (hasPhone.equalsIgnoreCase("1")) { // 选择的联系人的电话
                        ArrayList<HashMap<String, String>> numbersArray = new ArrayList<HashMap<String, String>>();
                        Cursor phoneCur = getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null); // zzz
                                                                                                             // 根据id查号码
                        if (phoneCur.getCount() > 1) {
                            Log.v(TAG, "phoneCur.getCount() > 1");
                            while (phoneCur.moveToNext()) {
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("phone", phoneCur.getString(phoneCur.getColumnIndex("data1")));
                                numbersArray.add(map);
                            }

                            ListView listView = new ListView(this);// zzz
                                                                   // 显示在选择号码对话框中的ListView
                            SimpleAdapter adapter = new SimpleAdapter(this, numbersArray,
                                    android.R.layout.simple_list_item_1, new String[] { "phone" },
                                    new int[] { android.R.id.text1 });
                            listView.setAdapter(adapter);
                            final AlertDialog dialog = new AlertDialog.Builder(this).setView(listView).create(); // zzz
                                                                                                                 // 弹出选择号码的对话框
                            dialog.show();

                            listView.setOnItemClickListener(new OnItemClickListener() { // zzz
                                                                                        // 选择号码后的响应

                                @Override
                                public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                    Log.v(TAG, "arg2 - " + arg2);
                                    TextView tv = (TextView) arg1.findViewById(android.R.id.text1); // zzz
                                                                                                    // 取得每条item中的TextView控件
                                    number = tv.getText().toString();
                                    mEditFragment.mView.onContactsChoosed(number, name); // zzz
                                                                                         // 附加到参与人中
                                    Log.v(TAG, "number - " + number);
                                    dialog.dismiss();

                                    // name =
                                    // numbersArray.get(arg2).get("phone");
                                    // // zzz 这需要numbersArray是final的，因此不能
                                }

                            });
                        } else {
                            Log.v(TAG, "phoneCur.getCount() == 1");
                            if (phoneCur.moveToFirst()) {
                                number = phoneCur.getString(phoneCur.getColumnIndex("data1"));
                                mEditFragment.mView.onContactsChoosed(number, name);
                            }
                        }

                        phoneCur.close();
                    }
                    // name =
                    // c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    //
                    // Log.i(TAG, "name - " + name);
                }
            }
            break;
        default:
            break;
        }
    }
}
