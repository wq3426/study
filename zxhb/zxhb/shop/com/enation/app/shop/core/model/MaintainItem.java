package com.enation.app.shop.core.model;


/**
 * 保养项目信息实体
 * @author wangqiang 2016年4月14日 下午2:11:00
 *
 */
@SuppressWarnings("serial")
public class MaintainItem implements java.io.Serializable {
	private int id;//主键id
	private String itemname;//保养项目名
	private String itemstatus;//保养状态  1 是 0 否
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getItemname() {
		return itemname;
	}
	public void setItemname(String itemname) {
		this.itemname = itemname;
	}
	public String getItemstatus() {
		return itemstatus;
	}
	public void setItemstatus(String itemstatus) {
		this.itemstatus = itemstatus;
	}
}