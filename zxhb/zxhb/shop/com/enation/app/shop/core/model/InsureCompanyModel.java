package com.enation.app.shop.core.model;

/**
 * @Description 保险公司信息实体类
 *
 * @createTime 2016年9月8日 下午4:35:45
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
public class InsureCompanyModel {
	private int id;//主键id
	private String company_name;//保险公司名称
	private String logo;//保险公司logo
	private String telephone;//电话
	private Integer sort;//排序号
	private Long create_time;//创建时间
	private Long update_time;//更新时间
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public Integer getSort() {
		return sort;
	}
	public void setSort(Integer sort) {
		this.sort = sort;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public Long getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Long update_time) {
		this.update_time = update_time;
	}
	
	@Override
	public String toString() {
		return "InsureCompanyModel [id=" + id + ", company_name=" + company_name + ", logo=" + logo + ", telephone="
				+ telephone + ", sort=" + sort + ", create_time=" + create_time + ", update_time=" + update_time + "]";
	}
	
}
