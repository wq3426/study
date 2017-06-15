package com.enation.app.b2b2c.core.model.store;

/**
 * @Description 消息推送类型实体类
 *
 * @createTime 2016年11月1日 下午5:32:43
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
public class PushMsgType {
	private Integer id;
	private String push_type;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getPush_type() {
		return push_type;
	}
	public void setPush_type(String push_type) {
		this.push_type = push_type;
	}
	
}