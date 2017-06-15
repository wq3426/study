package com.enation.app.b2b2c.core.model;

import com.enation.app.shop.component.bonus.model.BonusType;

/**
 * 店铺促销实体类
 * @author xulipeng
 *
 */
public class StoreBonus extends BonusType {
	private Integer store_id;	//店铺id
	private String img_bonus;	//图片地址
	private int limit_num;		//限领
	private int is_given;		//是否允许转增、此版本未使用此字段  xlp
	private String store_name;
	private long create_date;   //创建时间

	public String getImg_bonus() {
		return img_bonus;
	}

	public void setImg_bonus(String img_bonus) {
		this.img_bonus = img_bonus;
	}

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}

	public int getLimit_num() {
		return limit_num;
	}

	public void setLimit_num(int limit_num) {
		this.limit_num = limit_num;
	}

	public int getIs_given() {
		return is_given;
	}

	public void setIs_given(int is_given) {
		this.is_given = is_given;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}

	public long getCreate_date() {
		return create_date;
	}

	public void setCreate_date(long create_date) {
		this.create_date = create_date;
	}

}
