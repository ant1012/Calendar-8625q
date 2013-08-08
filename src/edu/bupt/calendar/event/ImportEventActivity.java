package edu.bupt.calendar.event;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.android.calendarcommon.ICalendar;
import com.android.calendarcommon.ICalendar.FormatException;

import edu.bupt.calendar.AbstractCalendarActivity;
import edu.bupt.calendar.R;
import edu.bupt.calendar.Utils;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;

public class ImportEventActivity extends AbstractCalendarActivity {
    private static final String TAG = "EditEventActivity";
    private ImportEventFragment mImportFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_frame_layout);

        getDataFromFile();

        mImportFragment = new ImportEventFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.main_frame, mImportFragment).commit();
    }

    private void getDataFromFile() {
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

                    ICalendar.Component parent = ICalendar.parseCalendar(vcal);

                    Log.i(TAG, parent.getName());
                    Log.i(TAG, parent.getPropertyNames().toString());
                    for (ICalendar.Component child : parent.getComponents()) {
                        Log.i(TAG, child.toString());

                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (FormatException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utils.returnToCalendarHome(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
