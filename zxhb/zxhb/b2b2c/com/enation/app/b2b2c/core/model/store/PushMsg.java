package com.enation.app.b2b2c.core.model.store;

/**
 * @Description 消息推送实体类
 *
 * @createTime 2016年8月29日 上午9:37:23
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
public class PushMsg {
	private Integer id;
	private Integer storeId;
	private Integer push_id;
	private String title;
	private String msg;
	private String  create_time;
	private char flag;//0:待查看；1:已查看
	private char status;//0:待推送；1:已推送
	private String remarks;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getStoreId() {
		return storeId;
	}
	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}
	public Integer getPush_id() {
		return push_id;
	}
	public void setPush_id(Integer push_id) {
		this.push_id = push_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public char getFlag() {
		return flag;
	}
	public void setFlag(char flag) {
		this.flag = flag;
	}
	public char getStatus() {
		return status;
	}
	public void setStatus(char status) {
		this.status = status;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
}