package com.enation.app.shop.core.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 会员冻结积分 
 * @author kingapex
 * @date 2011-10-27 上午10:53:10 
 * @version V1.0
 */
public class FreezePoint {

	private int id;
	private int memberid;
	private Integer childid;
	private int point;
	private int mp;
	private Integer orderid;
	private Long dateline;
	private String type;
	
	/**
	 * --------------
	 *  非数据库字段
	 * --------------
	 */
	private int order_status; //订单状态 
	

	@PrimaryKeyField
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	} 

	public int getMp() {
		return mp;
	}

	public void setMp(int mp) {
		this.mp = mp;
	}


	public Long getDateline() {
		return dateline;
	}

	public void setDateline(Long dateline) {
		this.dateline = dateline;
	}

	public int getMemberid() {
		return memberid;
	}

	public void setMemberid(int memberid) {
		this.memberid = memberid;
	}

 
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@NotDbField
	public int getOrder_status() {
		return order_status;
	}

	public void setOrder_status(int order_status) {
		this.order_status = order_status;
	}

	public Integer getChildid() {
		return childid;
	}

	public void setChildid(Integer childid) {
		this.childid = childid;
	}

	public Integer getOrderid() {
		return orderid;
	}

	public void setOrderid(Integer orderid) {
		this.orderid = orderid;
	}
	
	
}
