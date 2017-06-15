package com.enation.app.b2b2c.core.model.zxhb;

/**
 * @Description 用户地址实体类
 *
 * @createTime 2016年12月7日 下午9:08:44
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
public class OrderAddress {
	private Integer address_id;//地址id
	private Integer user_id;//用户id
	private String address_content;//地址内容
	private Integer is_default;//是否默认地址 1 是 0 否
	
	public Integer getAddress_id() {
		return address_id;
	}
	public void setAddress_id(Integer address_id) {
		this.address_id = address_id;
	}
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	public String getAddress_content() {
		return address_content;
	}
	public void setAddress_content(String address_content) {
		this.address_content = address_content;
	}
	public Integer getIs_default() {
		return is_default;
	}
	public void setIs_default(Integer is_default) {
		this.is_default = is_default;
	}
}
