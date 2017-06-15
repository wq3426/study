package com.enation.app.shop.core.model;

/**
 * @Description 保险险种实体类
 *
 * @createTime 2016年9月8日 下午8:45:24
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
public class InsureTypeModel {
	private int id;//主键id
	private String company_id;//保险公司id
	private String insure_name;//险种名称
	private Integer insure_type;//险种类型
	private Integer sort;//排序号
	private Long create_time;//创建时间
	private Long update_time;//更新时间
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCompany_id() {
		return company_id;
	}
	public void setCompany_id(String company_id) {
		this.company_id = company_id;
	}
	public String getInsure_name() {
		return insure_name;
	}
	public void setInsure_name(String insure_name) {
		this.insure_name = insure_name;
	}

	public Integer getInsure_type() {
		return insure_type;
	}
	public void setInsure_type(Integer insure_type) {
		this.insure_type = insure_type;
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
		return "InsureTypeModel [id=" + id + ", company_id=" + company_id + ", insure_name=" + insure_name
				+ ", insure_type=" + insure_type + ", sort=" + sort + ", create_time=" + create_time + ", update_time="
				+ update_time + "]";
	}
}
