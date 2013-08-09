package edu.bupt.calendar.importevent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import edu.bupt.calendar.R;
import edu.bupt.calendar.R.layout;
import edu.bupt.calendar.R.menu;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class ImportEventActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_event);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.import_event, menu);
        return true;
    }

}
