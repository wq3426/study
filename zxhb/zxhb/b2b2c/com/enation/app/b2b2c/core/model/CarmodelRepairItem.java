package com.enation.app.b2b2c.core.model;

/**
 * @Description 车型保养项目对象
 *
 * @createTime 2016年8月27日 下午5:37:32
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
public class CarmodelRepairItem {
	private Integer id;
	private Integer carmodel_id;//车型id
	private Integer repair_item_id;//保养项目id
	private Long repair_interval;//保养间隔里程
	private Long repair_interval_time;//保养间隔时间
	
	private String itemname;//保养项目名称
	private String brand;//品牌
	private String type;//类型
	private String series;//车系
	private String nk;//年款
	private String sales_name;//车型销售名称
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCarmodel_id() {
		return carmodel_id;
	}
	public void setCarmodel_id(Integer carmodel_id) {
		this.carmodel_id = carmodel_id;
	}
	public Integer getRepair_item_id() {
		return repair_item_id;
	}
	public void setRepair_item_id(Integer repair_item_id) {
		this.repair_item_id = repair_item_id;
	}
	public Long getRepair_interval() {
		return repair_interval;
	}
	public void setRepair_interval(Long repair_interval) {
		this.repair_interval = repair_interval;
	}
	public Long getRepair_interval_time() {
		return repair_interval_time;
	}
	public void setRepair_interval_time(Long repair_interval_time) {
		this.repair_interval_time = repair_interval_time;
	}
	public String getItemname() {
		return itemname;
	}
	public void setItemname(String itemname) {
		this.itemname = itemname;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public String getNk() {
		return nk;
	}
	public void setNk(String nk) {
		this.nk = nk;
	}
	public String getSales_name() {
		return sales_name;
	}
	public void setSales_name(String sales_name) {
		this.sales_name = sales_name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "CarmodelRepairItem [id=" + id + ", carmodel_id=" + carmodel_id + ", repair_item_id=" + repair_item_id
				+ ", repair_interval=" + repair_interval + ", repair_interval_time=" + repair_interval_time
				+ ", itemname=" + itemname + ", brand=" + brand + ", type=" + type + ", series=" + series + ", nk=" + nk
				+ ", sales_name=" + sales_name + "]";
	}
}
