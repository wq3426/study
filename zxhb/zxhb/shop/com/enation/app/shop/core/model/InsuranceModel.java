package com.enation.app.shop.core.model;


/**
 * 保险套餐信息实体
 * 
 * @author kingapex 2010-4-25下午09:40:24
 */
@SuppressWarnings("serial")
public class InsuranceModel implements java.io.Serializable {
	private int id;//主键id
	private String company;//保险公司
	private String companyimage;//保险公司Logo
	private String telephone;//电话
	private String insurance;//套餐内容
	private Integer mandatory;//是否必选 1 必选 0 可选
	private Double insurediscount;//保险公司折扣
	private Double zadiscount;//中安折扣
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getCompanyimage() {
		return companyimage;
	}
	public void setCompanyimage(String companyimage) {
		this.companyimage = companyimage;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getInsurance() {
		return insurance;
	}
	public void setInsurance(String insurance) {
		this.insurance = insurance;
	}
	public Integer getMandatory() {
		return mandatory;
	}
	public void setMandatory(Integer mandatory) {
		this.mandatory = mandatory;
	}
	public Double getInsurediscount() {
		return insurediscount;
	}
	public void setInsurediscount(Double insurediscount) {
		this.insurediscount = insurediscount;
	}
	public Double getZadiscount() {
		return zadiscount;
	}
	public void setZadiscount(Double zadiscount) {
		this.zadiscount = zadiscount;
	}
}