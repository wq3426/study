package com.enation.app.shop.core.model;


/**
 * 4s店信息实体
 * @author wangqiang 2016年4月1日 下午6:46:46
 *
 */
@SuppressWarnings("serial")
public class C4sModel implements java.io.Serializable {
	private int id;//主键id
	private String brand;//品牌
	private String city;//所在城市
	private String name;//4s店名
	private String address;//地址
	private String telephone;//电话
	private String district;//所属区域
	private Double discountcontract;//签约折扣
	private Double discountnoncontract;//一般折扣
	private String image;//品牌Logo
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public double getDiscountcontract() {
		return discountcontract;
	}
	public void setDiscountcontract(Double discountcontract) {
		this.discountcontract = discountcontract;
	}
	public double getDiscountnoncontract() {
		return discountnoncontract;
	}
	public void setDiscountnoncontract(Double discountnoncontract) {
		this.discountnoncontract = discountnoncontract;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
}