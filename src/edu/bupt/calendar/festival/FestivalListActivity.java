package edu.bupt.calendar.festival;

import static android.provider.CalendarContract.EXTRA_EVENT_ALL_DAY;
import static android.provider.CalendarContract.EXTRA_EVENT_BEGIN_TIME;
import static android.provider.CalendarContract.EXTRA_EVENT_END_TIME;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.bupt.calendar.AllInOneActivity;
import edu.bupt.calendar.CalendarController;
import edu.bupt.calendar.R;
import edu.bupt.calendar.CalendarController.EventType;
import edu.bupt.calendar.CalendarController.ViewType;
import edu.bupt.calendar.R.id;
import edu.bupt.calendar.R.layout;
import edu.bupt.calendar.agenda.AgendaListView;
import edu.bupt.calendar.event.EditEventActivity;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FestivalListActivity extends Activity {

    ListView lv;
    List<Map<String, Object>> data;
    int currentPosition;
    Time currentTime;
    int counter = 62;
    Time currentTime2;
    public TextView mFooterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        data = getData();
        lv = new ListView(this);
        final MyAdapter adapter = new MyAdapter(this, lv);

        lv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                    int position, long id) {
                sendEditEventByTime(position);
            }

        });

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mFooterView = (TextView) inflater.inflate(
                R.layout.agenda_header_footer, null);

        String curTime = currentTime2.format("%Y-%m-%d");

        /** zzz */
        mFooterView.setText(getString(R.string.show_newer_events, curTime));

        mFooterView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                counter += 62;
                data = getFestivals(counter);

                currentTime2.set(currentTime2.monthDay + 62,
                        currentTime2.month, currentTime2.year);
                currentTime2.normalize(true);
                String curTime = currentTime2.format("%Y-%m-%d");
                Log.v("FestivalListActivity", "current Time: " + curTime);

                /** zzz */
                mFooterView.setText(getString(R.string.show_newer_events,
                        curTime));

                adapter.notifyDataSetChanged();

                // currentTime.set(currentTime.monthDay+62, currentTime.month,
                // currentTime.year);
                // currentTime.normalize(true);
                //
                //
                // data=getFestivals(currentTime);
                // List<Map<String, Object>> list=getFestivals(a);

                // for(int i=0;i<list.size();i++){
                // String name = (String) list.get(i).get("title");
                // Time time = (Time) list.get(i).get("info");
                // String s=time.format("%Y-%m-%d");
                // Log.v("FestivalListActivity", "festival: "+name+" date: "+s);
                //
                // }
                // Time b = (Time) list.get(1).get("info");
                //
                // String c = a.format("%Y-%m-%d");
                // Toast.makeText(getApplicationContext(), c,
                // Toast.LENGTH_SHORT).show();
                //
            }

        });

        lv.addFooterView(mFooterView);

        lv.setAdapter(adapter);
        setContentView(lv);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;

        currentTime = new Time();
        currentTime.setToNow();

        currentTime2 = new Time();
        currentTime2.setToNow();
        currentTime2.set(currentTime2.monthDay + 62, currentTime2.month,
                currentTime2.year);
        currentTime2.normalize(true);

        list = getFestivals(counter);

        // map = new HashMap<String, Object>();
        //
        // map.put("title", "劳动节");
        // Time time1 = new Time();
        // time1.set(1, 4, 2013);
        // map.put("info", time1);
        //
        // list.add(map);
        //
        //
        // map = new HashMap<String, Object>();
        // map.put("title", "国庆节");
        // Time time2 = new Time();
        // time2.set(1, 9, 2013);
        // map.put("info", time2);
        // list.add(map);
        //
        //
        //
        //
        // map = new HashMap<String, Object>();
        // map.put("title", "妇女节");
        // Time time3 = new Time();
        // time3.set(8, 2, 2013);
        // map.put("info", time3);
        // list.add(map);

        return list;
    }

    static class ViewHolder {

        public TextView title;
        public TextView info;

    }

    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater = null;
        // public TextView mFooterView;
        public ListView lv;

        private MyAdapter(Context context, ListView lv) {
            // 根据context上下文加载布局，这里的是Demo17Activity本身，即this
            this.mInflater = LayoutInflater.from(context);
            this.lv = lv;

        }

        @Override
        public int getCount() {
            // How many items are in the data set represented by this Adapter.
            // 在此适配器中所代表的数据集中的条目数
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            // Get the data item associated with the specified position in the
            // data set.
            // 获取数据集中与指定索引对应的数据项
            return position;
        }

        @Override
        public long getItemId(int position) {
            // Get the row id associated with the specified position in the
            // list.
            // 获取在列表中与指定索引对应的行id
            return position;
        }

        // Get a View that displays the data at the specified position in the
        // data set.
        // 获取一个在数据集中指定索引的视图来显示数据
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            // 如果缓存convertView为空，则需要创建View
            if (convertView == null) {
                holder = new ViewHolder();
                // 根据自定义的Item布局加载布局
                convertView = mInflater.inflate(R.layout.test_layout_items,
                        null);

                holder.title = (TextView) convertView.findViewById(R.id.tv);
                holder.info = (TextView) convertView.findViewById(R.id.info);
                // 将设置好的布局保存到缓存中，并将其设置在Tag里，以便后面方便取出Tag
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.title.setText((String) data.get(position).get("title"));
            Time time = (Time) data.get(position).get("info");

            String s = time.format("%Y-%m-%d");

            holder.info.setText(s);

            return convertView;
        }

    }

    public void sendEditEvent(Time startTime, Time endTime) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setClass(FestivalListActivity.this, EditEventActivity.class);
        intent.putExtra(EXTRA_EVENT_BEGIN_TIME, startTime.toMillis(false));
        intent.putExtra(EXTRA_EVENT_END_TIME, endTime.toMillis(false));
        intent.putExtra(EXTRA_EVENT_ALL_DAY, true);

        startActivity(intent);
    }

    public void sendEditEventByTime(int position) {

        String name = (String) data.get(position).get("title");
        Time startTime = (Time) data.get(position).get("info");
        Time endTime = new Time();
        endTime.set(startTime.monthDay + 1, startTime.month, startTime.year);
        endTime.normalize(true);
        sendEditEvent(startTime, endTime);
        Toast.makeText(getApplicationContext(), name, 8000).show();

    }

    public List<Map<String, Object>> getFestivals(Time startTime) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        int[] mothdays;
        int counter = 0;

        // if(ReadDays.isLeapYear(startTime.year)){
        // mothdays=CalendarDays.noOfMothLeap;
        //
        //
        // }
        //
        // else{
        // mothdays=CalendarDays.noOfMothEven;
        //
        //
        // }

        Time time = new Time();
        time.set(startTime);

        for (int i = 0; i < 62; i++) {

            Lunar.setLunar(this, time.year, time.month + 1, time.monthDay);
            if (Lunar.isFestival()) {

                String festival = Lunar.getFestival();
                Time festivalTime = new Time();
                festivalTime.set(time);

                map = new HashMap<String, Object>();

                map.put("title", festival);

                map.put("info", festivalTime);
                Time festivalTime2 = (Time) map.get("info");
                String s = festivalTime2.format("%Y-%m-%d");

                list.add(map);
                Log.v("FestivalListActivity", " method: festival: " + festival
                        + " date: " + s);
            }

            time.set(time.monthDay + 1, time.month, time.year);
            time.normalize(true);

        }

        return list;

    }

    public List<Map<String, Object>> getFestivals(int counter) {

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> map;
        int[] mothdays;
        // int counter = 0;

        // if(ReadDays.isLeapYear(startTime.year)){
        // mothdays=CalendarDays.noOfMothLeap;
        //
        //
        // }
        //
        // else{
        // mothdays=CalendarDays.noOfMothEven;
        //
        //
        // }

        Time time = new Time();
        time.set(currentTime);

        for (int i = 0; i < counter; i++) {

            Lunar.setLunar(this, time.year, time.month + 1, time.monthDay);
            if (Lunar.isFestival()) {

                String festival = Lunar.getFestival();
                Time festivalTime = new Time();
                festivalTime.set(time);

                map = new HashMap<String, Object>();

                map.put("title", festival);

                map.put("info", festivalTime);
                Time festivalTime2 = (Time) map.get("info");
                String s = festivalTime2.format("%Y-%m-%d");

                list.add(map);
                Log.v("FestivalListActivity", " method: festival: " + festival
                        + " date: " + s);
            }

            time.set(time.monthDay + 1, time.month, time.year);
            time.normalize(true);

        }

        return list;

    }

}
