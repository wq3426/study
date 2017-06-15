package com.enation.app.base.core.model;

/**
 * @Description 商户信息实体类
 *
 * @createTime 2016年8月26日 下午3:57:02
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public class MerchantInfo {

	private int id;                 //商户id
	private String company_name;    //公司名称
	private String brand;           //主营品牌
	private String contact_name;    //联系人
	private String contact_phone;   //联系电话
	private String email;           //邮箱
	private String address;         //详细地址
	private String signstatus;      //注册状态
	private long create_time;       //商户添加时间
	
	
	
	
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getContact_name() {
		return contact_name;
	}
	public void setContact_name(String contact_name) {
		this.contact_name = contact_name;
	}
	public String getContact_phone() {
		return contact_phone;
	}
	public void setContact_phone(String contact_phone) {
		this.contact_phone = contact_phone;
	}
	public long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(long create_time) {
		this.create_time = create_time;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getSignstatus() {
		return signstatus;
	}
	public void setSignstatus(String signstatus) {
		this.signstatus = signstatus;
	}	
	
	
}
