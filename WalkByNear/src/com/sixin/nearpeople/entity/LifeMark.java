package com.sixin.nearpeople.entity;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;
import com.lidroid.xutils.db.annotation.Transient;
import com.lidroid.xutils.db.annotation.Unique;
import com.sixin.nearpeople.gaode.GaoDeMapManager;

@Table(name = "lifemark")
public class LifeMark {

	public final static int TYPE_FOOD = 0; // 美食
	public final static int TYPE_ENTERTAINMENT = 1; // 娱乐
	public final static int TYPE_SHOPPING = 2; // 购物
	public final static int TYPE_HOTEL = 3; // 酒店
	public final static int TYPE_LIFE = 4; // 生活
	public final static int TYPE_MYTYPE = 5; // 自定义
	public final static int SELECT = 1; // 选中
	public final static int UNSELECT = 0;// 为选中
	private int _id;
	@Transient
	public final static String[] Categorys = { "美食", "娱乐", "购物", "酒店", "生活",
			"自定义" };
	@Transient
	private Integer imageId;
	@Column(column = "_type")
	private int type;
	@Column(column = "_name")
	@Unique
	private String name;
	@Column(column = "_isselect")
	private int isSelect; // 0为未选中，1为选中
	public Integer getImageId() {

		this.imageId = GaoDeMapManager.getImages().get(this.name);
		if (this.imageId == null) {
			this.imageId = GaoDeMapManager.getImages().get("自定义");
		}
		return this.imageId;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIsSelect() {
		return isSelect;
	}

	public void setIsSelect(int isSelect) {
		this.isSelect = isSelect;
	}

	public LifeMark(int type, String name, int isSelect) {
		super();
		this.type = type;
		this.name = name;
		this.isSelect = isSelect;
	}

	public LifeMark() {
	}
	
}
