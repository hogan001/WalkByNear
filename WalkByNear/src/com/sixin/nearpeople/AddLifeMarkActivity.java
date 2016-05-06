package com.sixin.nearpeople;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hogan.walkbynear.R;
import com.lidroid.xutils.exception.DbException;
import com.sixin.nearpeople.dialog.AddLifeMarkDialog;
import com.sixin.nearpeople.dialog.DeleteLifeMarkDialog;
import com.sixin.nearpeople.entity.LifeMark;
import com.sixin.nearpeople.entity.LifeMarkDao;

public class AddLifeMarkActivity extends Activity {
	private ListView left_lv;
	private ListView right_lv;
	private List<List<LifeMark>> lifeMarks;
	private LifeMarkDao markDao = null;
	private int selectType = 0;
	private Button submit;
	private List<LifeMark> deletes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_life_mark);

		init();
	}

	private void init() {
		deletes = new ArrayList<LifeMark>();
		left_lv = (ListView) findViewById(R.id.add_lifeMark_left_lv);
		right_lv = (ListView) findViewById(R.id.add_lifeMark_right_lv);
		submit = (Button) findViewById(R.id.add_lifeMark_submit);
		final LeftBaseAdapter leftAdapter = new LeftBaseAdapter(this);
		final RightBaseAdapter rightAdapter = new RightBaseAdapter(this);
		markDao = new LifeMarkDao(this);
		try {
			lifeMarks = markDao.getLifeMarkGroupByType();
		} catch (DbException e1) {
			e1.printStackTrace();
		}
		left_lv.setAdapter(leftAdapter);
		right_lv.setAdapter(rightAdapter);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					markDao.saveLifeMarks(lifeMarks);
					markDao.deleteLifeMarks(deletes);
				} catch (DbException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				setResult(0x102);
				finish();
			}
		});
		left_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				selectType = position;
				leftAdapter.notifyDataSetChanged();
				rightAdapter.notifyDataSetChanged();
			}
		});
		// 设置短按事件
		right_lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 判断是否是添加
				if (position == lifeMarks.get(selectType).size()) {
					// Toast.makeText(AddLifeMarkActivity.this, "添加", 0).show();

					final AddLifeMarkDialog dialog = new AddLifeMarkDialog(
							AddLifeMarkActivity.this);
					dialog.setView(new EditText(AddLifeMarkActivity.this));

					dialog.setOnPositiveClickListenner(new AddLifeMarkDialog.OnPositiveClickListenner() {

						@Override
						public void onPositiveClick(AddLifeMarkDialog dialog) {
							dialog.dismiss();
							String lifeMarkName = dialog.getLifeMarkName();
							if (lifeMarkName == null || lifeMarkName.equals("")) {
								Toast.makeText(AddLifeMarkActivity.this,
										"添加失败：标签名为空！", 0).show();
							} else if (isUnique(lifeMarkName)) {
								// 添加到数据库
								LifeMark entity = new LifeMark(
										LifeMark.TYPE_MYTYPE, lifeMarkName,
										LifeMark.UNSELECT);
								try {
									markDao.saveLifeMark(entity);
								} catch (DbException e) {
									e.printStackTrace();
								}
								lifeMarks.get(selectType).add(entity);
								rightAdapter.notifyDataSetChanged();
							} else {
								Toast.makeText(AddLifeMarkActivity.this,
										"添加失败：标签名已存在！", 0).show();
							}
						}

						private boolean isUnique(String lifeMarkName) {
							for (List<LifeMark> lists : lifeMarks) {
								for (LifeMark lifeMark : lists) {
									if (lifeMark.getName().equals(lifeMarkName)) {
										return false;
									}
								}
							}
							return true;
						}
					});
					dialog.show();
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							dialog.showKeyboard();
						}
					}, 200);
					return;
				}
				LifeMark lifeMark = lifeMarks.get(selectType).get(position);
				Holder holder = (Holder) view.getTag();
				if (lifeMark.getIsSelect() == 0) {
					lifeMark.setIsSelect(1);
					holder.select.setSelected(true);
				} else {
					lifeMark.setIsSelect(0);
					holder.select.setSelected(false);
				}
			}
		});
		// 设置长按事件
		right_lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int pos = position;
				if (selectType == LifeMark.TYPE_MYTYPE
						&& position != lifeMarks.get(selectType).size()) {
					DeleteLifeMarkDialog dialog = new DeleteLifeMarkDialog(
							AddLifeMarkActivity.this);
					dialog.show();
					dialog.setOnPositiveClickListenner(new DeleteLifeMarkDialog.OnPositiveClickListenner() {

						@Override
						public void onPositiveClick(DeleteLifeMarkDialog dialog) {

							LifeMark entity = lifeMarks.get(selectType)
									.get(pos);
							// 添加到删除列表
							deletes.add(entity);
							// 移除listView中的数据，并更新数据
							lifeMarks.get(selectType).remove(pos);
							rightAdapter.notifyDataSetChanged();
							dialog.dismiss();
						}
					});
				}
				return true;
			}
		});
	}

	private class LeftBaseAdapter extends BaseAdapter {
		private Context context;

		public LeftBaseAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {

			return LifeMark.Categorys.length;
		}

		@Override
		public Object getItem(int position) {

			return LifeMark.Categorys[position];
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HolderBtn holderBtn = null;
			if (convertView == null) {
				convertView = View.inflate(context,
						R.layout.item_add_life_mark_left, null);
				holderBtn = new HolderBtn();
				holderBtn.tv = (TextView) convertView
						.findViewById(R.id.add_lifeMark_left_tv);
				convertView.setTag(holderBtn);
			} else {
				holderBtn = (HolderBtn) convertView.getTag();
			}
			holderBtn.tv.setText(LifeMark.Categorys[position]);
			if (selectType == position) {
				holderBtn.tv.setSelected(true);
			} else {
				holderBtn.tv.setSelected(false);
			}
			return convertView;
		}
	}

	class HolderBtn {
		TextView tv;
	}

	private class RightBaseAdapter extends BaseAdapter {
		private Context context;

		public RightBaseAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			int extra = 0;
			if (selectType == LifeMark.TYPE_MYTYPE) {// 若为自定义则需添加一个添加的按钮
				extra = 1;
			}
			if (lifeMarks.get(selectType) == null) {
				return extra;
			}
			return lifeMarks.get(selectType).size() + extra;
		}

		@Override
		public Object getItem(int position) {

			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			Holder holder = null;
			if (convertView == null || convertView.getTag() == null) {
				convertView = View.inflate(context,
						R.layout.item_add_life_mark_right, null);
				holder = new Holder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.add_lifeMark_right_image);
				holder.name = (TextView) convertView
						.findViewById(R.id.add_lifeMark_right_name);
				holder.select = (ImageView) convertView
						.findViewById(R.id.add_lifeMark_right_select_iv);
				holder.add = (ImageView) convertView
						.findViewById(R.id.add_lifeMark_right_add_iv);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			if (position == lifeMarks.get(selectType).size()) {
				holder.add.setVisibility(View.VISIBLE);
				holder.image.setVisibility(View.GONE);
				holder.name.setVisibility(View.GONE);
				holder.select.setVisibility(View.GONE);
				return convertView;
			} else {
				holder.add.setVisibility(View.GONE);
				holder.image.setVisibility(View.VISIBLE);
				holder.name.setVisibility(View.VISIBLE);
				holder.select.setVisibility(View.VISIBLE);
			}
			LifeMark lifeMark = lifeMarks.get(selectType).get(position);

			holder.image.setBackgroundResource(lifeMark.getImageId());
			holder.name.setText(lifeMark.getName());
			if (lifeMark.getIsSelect() == 1) {
				holder.select.setSelected(true);
			} else {
				holder.select.setSelected(false);
			}
			return convertView;
		}

	}

	class Holder {
		ImageView image;
		TextView name;
		ImageView select;
		ImageView add;

	}

}
