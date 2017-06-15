package com.enation.app.base.core.model;

/**
 *   app 消息
 * @author chopper
 *
 */
public class AppMessageType {

	/**
	 * id
	 */
	public int 	id;	
	/**
	 * 标题
	 */
	public String title;
	/**
	 * 状态
	 */
	public int status; 
	/**
	 * 图片
	 */
	public String icon; 
	/**
	 * 排序
	 */
	public int sort;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	} 
}
