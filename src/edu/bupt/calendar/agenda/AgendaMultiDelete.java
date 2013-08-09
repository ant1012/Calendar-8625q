package edu.bupt.calendar.agenda;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.bupt.calendar.R;
import edu.bupt.calendar.agenda.AgendaMultiDeleteAdapter.ViewHolder;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Calendars;
import android.provider.CalendarContract.Events;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class AgendaMultiDelete extends Activity {  
    private ListView lv;  
    private AgendaMultiDeleteAdapter mAdapter;  
    private ArrayList<String> list;  
    private Button bt_selectall;  
    private Button bt_cancel;  
    private Button bt_deselectall;  
    private Button bt_delete;  
    private int checkNum; // 记录选中的条目数量  
    private TextView tv_show;// 用于显示选中的条目数量  
    private ArrayList<Long> idlist;
    private ArrayList<String> starttime = new ArrayList<String>();
    private ArrayList<String> endtime = new ArrayList<String>();
  
    /** Called when the activity is first created. */  
  
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.agendamultidelete);  
        /* 实例化各个控件 */  
        lv = (ListView) findViewById(R.id.lv);  
        bt_selectall = (Button) findViewById(R.id.bt_selectall);  
        bt_cancel = (Button) findViewById(R.id.bt_cancelselectall);  
        bt_deselectall = (Button) findViewById(R.id.bt_deselectall);  
        bt_delete = (Button) findViewById(R.id.bt_delete);  
        tv_show = (TextView) findViewById(R.id.tv);  
        list = new ArrayList<String>();  
        idlist  = new ArrayList<Long>();
        // 为Adapter准备数据  
        initDate();  
        // 实例化自定义的MyAdapter  
        mAdapter = new AgendaMultiDeleteAdapter(list, this);  
        // 绑定Adapter  
        lv.setAdapter(mAdapter);  
  
        // 全选按钮的回调接口  
        bt_selectall.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                // 遍历list的长度，将MyAdapter中的map值全部设为true  
                for (int i = 0; i < list.size(); i++) {  
                    AgendaMultiDeleteAdapter.getIsSelected().put(i, true);  
                }  
                // 数量设为list的长度  
                checkNum = list.size();  
                // 刷新listview和TextView的显示  
                dataChanged();  
            }  
        });  
  
        // 反选按钮的回调接口  
        bt_cancel.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                // 遍历list的长度，将已选的设为未选，未选的设为已选  
                for (int i = 0; i < list.size(); i++) {  
                    if (AgendaMultiDeleteAdapter.getIsSelected().get(i)) {  
                        AgendaMultiDeleteAdapter.getIsSelected().put(i, false);  
                        checkNum--;  
                    } else {  
                        AgendaMultiDeleteAdapter.getIsSelected().put(i, true);  
                        checkNum++;  
                    }  
                }  
                // 刷新listview和TextView的显示  
                dataChanged();  
            }  
        });  
  
        // 取消按钮的回调接口  
        bt_deselectall.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                // 遍历list的长度，将已选的按钮设为未选  
                for (int i = 0; i < list.size(); i++) {  
                    if (AgendaMultiDeleteAdapter.getIsSelected().get(i)) {  
                        AgendaMultiDeleteAdapter.getIsSelected().put(i, false);  
                        checkNum--;// 数量减1  
                    }  
                }  
                // 刷新listview和TextView的显示  
                dataChanged();  
            }  
        });  
  
        // 绑定listView的监听器  
        lv.setOnItemClickListener(new OnItemClickListener() {  
            @Override  
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,  
                    long arg3) {  
                // 取得ViewHolder对象，这样就省去了通过层层的findViewById去实例化我们需要的cb实例的步骤  
                ViewHolder holder = (ViewHolder) arg1.getTag();  
                // 改变CheckBox的状态  
                holder.cb.toggle();  
                // 将CheckBox的选中状况记录下来  
                AgendaMultiDeleteAdapter.getIsSelected().put(arg2, holder.cb.isChecked());  
                // 调整选定条目  
                if (holder.cb.isChecked() == true) {  
                    checkNum++;  

                } else {  
                	checkNum--;  
                }  
                // 用TextView显示  
                tv_show.setText("已选中" + checkNum + "项");  
            }  
        });  
        // 绑定删除按钮的监听器  
        bt_delete.setOnClickListener(new OnClickListener() {  
            @Override  
            public void onClick(View v) {                 	
            	
            	Set<Integer> key =  AgendaMultiDeleteAdapter.getIsSelected().keySet();
            	Iterator<Integer> keySetIterator = key.iterator();
            	long id = 0;
            	int len = 0;
            	int i = 0;
            	while(keySetIterator.hasNext()){
            	    Integer e = keySetIterator.next();
            	    if(AgendaMultiDeleteAdapter.getIsSelected().get(e) == true){ 
            	    	list.remove(e-len);
            	    	id = idlist.get(e-len);
            	    	Log.i("e", Long.toString(e));
            			Log.i("id", Long.toString(id)); 
            	    	calendarDelete(id);     
            	    	idlist.remove(e-len);
            	    	AgendaMultiDeleteAdapter.getIsSelected().put(e, false);
            	    	checkNum--;
            	    	len++;

            	    }
            	}
//            	Log.i("list", list.toString());
//            	Log.i("getIsSelected", mAdapter.getIsSelected().toString());
            	tv_show.setText("已选中0项");
            	dataChanged();
            }  
        });  
    }    
	private void calendarDelete(long id) {
		// TODO Auto-generated method stub
		Cursor curde = getContentResolver().query(Events.CONTENT_URI, null,null , null, null);
		while(curde.moveToNext()){
			
			ContentResolver cr = getContentResolver();
			ContentValues values = new ContentValues();
			Uri deleteUri = null;
			deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, id);
			getContentResolver().delete(deleteUri, null, null);
		}
	}
    // 初始化数据  
    private void initDate() {  
//        for (int i = 0; i < 15; i++) {  
//            list.add("data" + " " + i);  
//        }  
    	int i = 0;
	    Cursor cur = getContentResolver().query(Events.CONTENT_URI, null,null , null, null);
	 // Use the cursor to step through the returned records
	    while (cur.moveToNext()) {
	        long calID = 0;
	        String title = null;
	        String temp = null;
	        StringBuffer sb = new StringBuffer();
	        long stime = 0;
	        long etime = 0;
	          
	        // Get the field values
	        calID = cur.getLong(cur.getColumnIndexOrThrow(CalendarContract.Events._ID));
	        Log.i("calID", String.valueOf(calID));
	        idlist.add(calID);

	        title = cur.getString(cur.getColumnIndexOrThrow(CalendarContract.Events.TITLE));
	        sb.append(title);
	        Log.i("title", title);   
	        stime = cur.getLong(cur.getColumnIndexOrThrow(CalendarContract.Events.DTSTART));
	        temp = String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(stime));
	        sb.append(temp);
	        Log.i("temp", temp); 
	        etime = cur.getLong(cur.getColumnIndexOrThrow(CalendarContract.Events.DTEND));
	        temp = String.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(etime));
	        sb.append(temp);
	        list.add(sb.toString());
	        Log.i("temp", temp); 
	        Log.i("segmentation", "---------------------------------------"); 
	    }
	    cur.close();	
	    Log.i("list", list.toString());
	    Log.i("segmentation", "---------------------------------------");
        Log.i("idlist", String.valueOf(idlist));
    }  
    // 刷新listview和TextView的显示  
    private void dataChanged() {  
        // 通知listView刷新  
        mAdapter.notifyDataSetChanged();  
        // TextView显示最新的选中数目  
        tv_show.setText("已选中" + checkNum + "项");  
    };  
}
