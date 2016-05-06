package com.sixin.nearpeople.dialog;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hogan.walkbynear.R;
import com.sixin.nearpeople.entity.People;

public class DataAdapter extends BaseAdapter {
	private List<People> list;
	private Context context;
	private People people;

	public DataAdapter(List<People> list, Context context) {
		this.list = list;
		this.context = context;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (list.size() > 0) {
			return list.size();
		}
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub

		return list.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		people = list.get(position);

		ViewHolder mHolder;
		if (convertView == null) {

			convertView = LayoutInflater.from(context).inflate(R.layout.item,
					null);
			mHolder = new ViewHolder();
			mHolder.name = (TextView) convertView.findViewById(R.id.name);
			mHolder.address = (TextView) convertView.findViewById(R.id.address);
			mHolder.phone_item = (TextView) convertView
					.findViewById(R.id.phone_item);
			mHolder.distance = (TextView) convertView
					.findViewById(R.id.distance);
			convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		mHolder.name.setText(people.getName());
		mHolder.address.setText(people.getCity());
		mHolder.distance.setText(String.valueOf(people.getDistance() + "米"));
		mHolder.phone_item.setText(people.getPhoneNum());
	
		return convertView;
	}

	class ViewHolder {
		private TextView name, address, distance, phone_item;
	}

}
