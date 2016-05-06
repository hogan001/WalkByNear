package com.sixin.nearpeople;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.hogan.walkbynear.R;
import com.sixin.nearpeople.dialog.DataAdapter;
import com.sixin.nearpeople.entity.People;
import com.sixin.nearpeople.gaode.GaoDeMapManager;

public class ListViewActivity extends Activity {
	private ListView listView;
	private DataAdapter adapter;
	private List<People> myPeoples;
	private TextView tv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_list_view);
		getDate();
	}

	/**
	 * @描述 数据初始化
	 * @时间 2015-1-19 下午4:29:07
	 */

	private void getDate() {
		myPeoples = GaoDeMapManager.getInstance(this).getPeopleList();
		listView = (ListView) findViewById(R.id.listView);
		tv = (TextView) findViewById(R.id.tv);
		adapter = new DataAdapter(myPeoples, this);
		listView.setAdapter(adapter);
		// 透明点击让本页面消失
		tv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				People people = (People) arg0.getItemAtPosition(position);
				Intent intent = new Intent();

				intent.putExtra("selectPosition", position);
				intent.putExtra("name", people.getName());
				intent.putExtra("address", people.getAddress());
				int distance = (int) people.getDistance();
				intent.putExtra("disance", distance);

				if (people.getPhoneNum() == null
						|| people.getPhoneNum().equals("")) {
					intent.putExtra("phone", "暂无电话");
				} else {

					intent.putExtra("phone", people.getPhoneNum());
				}

				setResult(0x102, intent);
				finish();
			}
		});

	}
}
