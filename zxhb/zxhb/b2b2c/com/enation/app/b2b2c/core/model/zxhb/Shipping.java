package com.enation.app.b2b2c.core.model.zxhb;

/** @Description (物流实体类)
 *
 * @createTime 2016年12月14日 上午10:38:30
 *
 * @author <a href="mailto:huashixin@trans-it.cn">huashixin</a>
 */
public class Shipping {
	
	private Integer id;
	
	private String shipping_name;
	
	private String shipping_type;
	
	private String shipping_tel;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getShipping_name() {
		return shipping_name;
	}

	public void setShipping_name(String shipping_name) {
		this.shipping_name = shipping_name;
	}

	public String getShipping_type() {
		return shipping_type;
	}

	public void setShipping_type(String shipping_type) {
		this.shipping_type = shipping_type;
	}

	public String getShipping_tel() {
		return shipping_tel;
	}

	public void setShipping_tel(String shipping_tel) {
		this.shipping_tel = shipping_tel;
	}
	
	
}
