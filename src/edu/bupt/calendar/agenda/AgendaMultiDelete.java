package edu.bupt.calendar.agenda;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.android.calendarcommon.ICalendar;

import edu.bupt.calendar.R;
import edu.bupt.calendar.Utils;
import edu.bupt.calendar.agenda.AgendaMultiDeleteAdapter.ViewHolder;

import android.R.menu;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
	private ArrayList<Map<String, String>> list;
	private Button bt_delete;
	private int checkNum; // 记录选中的条目数量
	private ArrayList<Long> idlist;
	private Map<String, String> map;
	ActionBar actionBar;
	Context context;

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.agendamultidelete);
		actionBar = getActionBar();
		actionBar.show();
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle("多条删除日程");
		actionBar.setDisplayHomeAsUpEnabled(true);

		/* 实例化各个控件 */
		lv = (ListView) findViewById(R.id.lv);
		bt_delete = (Button) findViewById(R.id.bt_delete);
		list = new ArrayList<Map<String, String>>();
		idlist = new ArrayList<Long>();

		// 为Adapter准备数据
		initDate();
		// 实例化自定义的MyAdapter
		mAdapter = new AgendaMultiDeleteAdapter(list, this);
		// 绑定Adapter
		lv.setAdapter(mAdapter);

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
				AgendaMultiDeleteAdapter.getIsSelected().put(arg2,
						holder.cb.isChecked());
				// 调整选定条目
				if (holder.cb.isChecked() == true) {
					checkNum++;

				} else {
					checkNum--;
				}
			}
		});
		// 绑定删除按钮的监听器
		bt_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkNum == 0){
					
				}
				else{
					new AlertDialog.Builder(AgendaMultiDelete.this)
					.setTitle("多条删除日程")
					.setMessage("确定要删除这" + checkNum +"条日程么？")
					.setNegativeButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Set<Integer> key = AgendaMultiDeleteAdapter
											.getIsSelected().keySet();
									Iterator<Integer> keySetIterator = key
											.iterator();
									long id = 0;
									int len = 0;
									while (keySetIterator.hasNext()) {
										Integer e = keySetIterator.next();
										if (AgendaMultiDeleteAdapter
												.getIsSelected().get(e) == true) {
											list.remove(e - len);
											id = idlist.get(e - len);
											Log.i("e", Long.toString(e));
											Log.i("id", Long.toString(id));
											calendarDelete(id);
											idlist.remove(e - len);
											AgendaMultiDeleteAdapter
													.getIsSelected().put(e,
															false);
											checkNum--;
											len++;

										}
									}
									// Log.i("list", list.toString());
									// Log.i("getIsSelected",
									// mAdapter.getIsSelected().toString());
									// tv_show.setText("已选中0项");
									dataChanged();

								}
							}).setPositiveButton("取消", null).show();
				} 


			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		super.onCreateOptionsMenu(menu);
		// 添加菜单项
		MenuItem add = menu.add(0, 0, 0, "全选");
		MenuItem del = menu.add(0, 1, 1, "反选");
		add.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		del.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			break;
		case 0:
			Log.i("0", "000000000000000");
			for (int i = 0; i < list.size(); i++) {
				AgendaMultiDeleteAdapter.getIsSelected().put(i, true);
			}
			// 数量设为list的长度
			checkNum = list.size();
			// 刷新listview和TextView的显示
			dataChanged();
			break;
		case 1:
			Log.i("1", "1111111111111111");
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
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void calendarDelete(long id) {
		// TODO Auto-generated method stub
		Cursor curde = getContentResolver().query(Events.CONTENT_URI, null,
				null, null, null);
		while (curde.moveToNext()) {
			Uri deleteUri = null;
			deleteUri = ContentUris.withAppendedId(Events.CONTENT_URI, id);
			getContentResolver().delete(deleteUri, null, null);
		}
	}

	// 初始化数据
	private void initDate() {
		Cursor cur = getContentResolver().query(Events.CONTENT_URI, null, null,
				null, null);
		// Use the cursor to step through the returned records
		while (cur.moveToNext()) {
			long calID = 0;
			String title = null;
			String temp, temp1 = null;
			StringBuffer sb1 = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			map = new HashMap<String, String>();
			long stime = 0;
			long etime = 0;

			long rruletime = 0;
			String year;

			String rrule = new String();
			char rrulechar;

			// Get the field values
			calID = cur.getLong(cur
					.getColumnIndexOrThrow(CalendarContract.Events._ID));
			Log.i("calID", String.valueOf(calID));
			idlist.add(calID);

			title = cur.getString(cur
					.getColumnIndexOrThrow(CalendarContract.Events.TITLE));
			Log.i("title", title);
			map.put("title", title);

			stime = cur.getLong(cur
					.getColumnIndexOrThrow(CalendarContract.Events.DTSTART));
			temp = String
					.valueOf(new SimpleDateFormat("a hh:mm").format(stime));
			sb1.append(temp);
			sb1.append(" - ");
			Log.i("temp", temp);

			etime = cur.getLong(cur
					.getColumnIndexOrThrow(CalendarContract.Events.DTEND));
			temp = String
					.valueOf(new SimpleDateFormat("a hh:mm").format(etime));
			sb1.append(temp);
			map.put("time", sb1.toString());
			try {
				rrule = cur.getString(cur
						.getColumnIndexOrThrow(CalendarContract.Events.RRULE));
				rrulechar = rrule.charAt(5);
				switch (rrulechar) {
				case 'D':
					sb2.append(getResources().getString(R.string.daily));
					map.put("year", sb2.toString());
					break;
				case 'W':
					if (rrule.length() > 30) {
						sb2.append(getResources().getString(
								R.string.every_weekday));
						map.put("year", sb2.toString());
					} else {

						temp = String.valueOf(new SimpleDateFormat("E")
								.format(stime));
						sb2.append(String
								.format(getResources().getString(
										R.string.weekly), temp));
						map.put("year", sb2.toString());
					}
					break;
				case 'M':
					if (rrule.length() > 32) {
						String a = rrule.substring(rrule.length() - 2,
								rrule.length());
						sb2.append(String.format(
								getResources().getString(
										R.string.monthly_on_day), a));
						map.put("year", sb2.toString());
					} else {
						// sb2.append(getResources().getString(R.string.monthly));
						// sb2.append("  (每月的第");
						// sb2.append(a);
						// sb2.append("个");
						temp = String.valueOf(new SimpleDateFormat("E")
								.format(stime));
						// sb2.append(temp);
						// sb2.append(")");
						sb2.append(String.format(
								getResources().getString(
										R.string.monthly_on_day_count),
								getResources().getStringArray(
										R.array.ordinal_labels)[Integer
										.parseInt(String.valueOf(rrule
												.charAt(rrule.length() - 3)))],
								temp));
						map.put("year", sb2.toString());

					}

					break;
				case 'Y':

					temp = String.valueOf(new SimpleDateFormat("MM月dd日")
							.format(stime));
					sb2.append(String.format(
							getResources().getString(R.string.yearly), temp));
					map.put("year", sb2.toString());
					break;

				default:
					break;
				}
				Log.i("rrule", rrule);
			} catch (Exception e) {
				// TODO: handle exception
				temp = String.valueOf(new SimpleDateFormat("yyyy年MM月dd日")
						.format(etime));
				map.put("year", temp);
				Log.i("rrule", "rrule is null");
			}

			list.add(map);
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
	}

}
