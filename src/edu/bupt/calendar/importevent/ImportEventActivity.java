package edu.bupt.calendar.importevent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.android.calendarcommon.ICalendar;
import com.android.calendarcommon.ICalendar.FormatException;

import edu.bupt.calendar.R;
import edu.bupt.calendar.Utils;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ImportEventActivity extends Activity {
    private View view;
    private TextView textviewTitle;
    private TextView textViewDatetime;
    private TextView textViewRepeat;
    private TextView textViewWhere;
    private TextView textViewDisc;
    private ICalendar.Component parent;
    private ICalendar.Component child;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_event);

        textviewTitle = (TextView) findViewById(R.id.title);
        textViewDatetime = (TextView) findViewById(R.id.when_datetime);
        textViewWhere = (TextView) findViewById(R.id.where);
        textViewDisc = (TextView) findViewById(R.id.disc);

        parent = getEventFromString(getStringFromFile());
        // for (ICalendar.Component child : parent.getComponents()) {
        // }

        // just test first event
        child = parent.getComponents().get(0);
        textviewTitle.setText(child.getFirstProperty("SUMMARY").getValue());
        textViewDatetime.setText(child.getFirstProperty("DTSTART").getValue());
        textViewWhere.setText(child.getFirstProperty("LOCATION").getValue());
        textViewDisc.setText(child.getFirstProperty("DISCRIPTION").getValue());
    }

    /** zzz */
    private String getStringFromFile() {
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            Uri uri = Uri.parse(data.toString());
            String filename = uri.getPath();
            File file = new File(filename);
            if (file.exists()) {
                FileInputStream fin;
                try {
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
                    return vcal;

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String getSummary(ICalendar.Component c) {
        return null;
    }

    private ICalendar.Component getEventFromString(String s) {
        ICalendar.Component c = null;
        try {
            c = ICalendar.parseCalendar(s);
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.import_event, menu);
        return true;
    }

}
