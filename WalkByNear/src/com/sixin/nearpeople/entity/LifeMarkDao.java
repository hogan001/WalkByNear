package com.sixin.nearpeople.entity;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;

import android.content.Context;
import android.util.Log;

public class LifeMarkDao {
	private Context context;
	private DbUtils db;
	public LifeMarkDao(Context context){
		this.context=context;
		this.db = DbUtils.create(context);
	}
	public void initData(){
		Object obj=null;
		try {
			obj=db.findFirst(LifeMark.class);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(obj==null){ //是第一次使用此应用
			Log.e("hongliang", "第一次进入！");
			try {
				db.saveBindingId(new LifeMark(0, "美食", 1));
				db.saveBindingId(new LifeMark(1, "KTV", 1));
				db.saveBindingId(new LifeMark(1, "景点", 1));
				db.saveBindingId(new LifeMark(3, "酒店", 1));
				db.saveBindingId(new LifeMark(2, "超市", 1));
				db.saveBindingId(new LifeMark(4, "公交车", 1));
				db.saveBindingId(new LifeMark(4, "ATM", 1));
				db.saveBindingId(new LifeMark(1, "电影院", 1));
				db.saveBindingId(new LifeMark(4, "药店", 1));
				db.saveBindingId(new LifeMark(4, "地铁", 1));
				db.saveBindingId(new LifeMark(0, "快餐", 1));
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			Log.e("hongliang", "不是第一次进入！");
			return ;
		}
	}
	public List<LifeMark> getMyLifeMark() throws DbException{
		return db.findAll(Selector.from(LifeMark.class).where("_isselect", "=", 1));
	}
	public List<LifeMark>getLifeMarkByType(int _type) throws DbException{
		return db.findAll(Selector.from(LifeMark.class).where("_type", "=", _type));
	}
	public List<List<LifeMark>>getLifeMarkGroupByType()throws DbException{
		List<List<LifeMark>> lifeMarks=new ArrayList<List<LifeMark>>();
		lifeMarks.add(getLifeMarkByType(LifeMark.TYPE_FOOD));
		lifeMarks.add(getLifeMarkByType(LifeMark.TYPE_ENTERTAINMENT));
		lifeMarks.add(getLifeMarkByType(LifeMark.TYPE_SHOPPING));
		lifeMarks.add(getLifeMarkByType(LifeMark.TYPE_HOTEL));
		lifeMarks.add(getLifeMarkByType(LifeMark.TYPE_LIFE));
		lifeMarks.add(getLifeMarkByType(LifeMark.TYPE_MYTYPE));
		return lifeMarks;
	}
	public void UpdateLifeMark(LifeMark entity) throws DbException{
		db.saveOrUpdate(entity);
	}
	public void saveLifeMarks(List<List<LifeMark>> lifeMarks) throws DbException {
		for (List<LifeMark> entities : lifeMarks) {
			db.saveOrUpdateAll(entities);
		}
	}
	public void saveLifeMark(LifeMark entity) throws DbException{
		db.saveBindingId(entity);
	}
	public void deleteLifeMarks(List<LifeMark> entities) throws DbException{
		db.deleteAll(entities);
	}

}
