package com.enation.app.b2b2c.core.model.note;

/**
 * 短信服务
 * @author yunbs
 *
 */
public class NoteServer {
	private Integer note_id;    //短信id
	private Integer store_id;	//店铺id
	private String note_name;	//短信名称
	private String note_detail; //短信内容
	private Integer send_count; //发送数量
	private long create_time;	//创建时间
	private long send_time;     //发短信时间
	private String status;		//短信状态
	
	public Integer getNote_id() {
		return note_id;
	}
	public void setNote_id(Integer note_id) {
		this.note_id = note_id;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public String getNote_name() {
		return note_name;
	}
	public void setNote_name(String note_name) {
		this.note_name = note_name;
	}
	public String getNote_detail() {
		return note_detail;
	}
	public void setNote_detail(String note_detail) {
		this.note_detail = note_detail;
	}
	
	public Integer getSend_count() {
		return send_count;
	}
	public void setSend_count(Integer send_count) {
		this.send_count = send_count;
	}
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	public long getSend_time() {
		return send_time;
	}
	public void setSend_time(long send_time) {
		this.send_time = send_time;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}


}
