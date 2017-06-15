package com.enation.app.b2b2c.core.model;

/**
 *  保养订单详情model类
 * @author wangqiang 2016年7月14日 下午5:59:46
 *
 */
public class RepairOrderDetail {
	private Integer spec_id;
	
	private Integer store_id;//保养预约店铺id
	private String repair_item_ids;//保养预约的项目内容
	private Long order_date;//保养预约日期
	private Integer time_region_id;//保养时间段
	private String carplate;//保养预约车牌号
	private Long repair_mile;//保养里程
	private Double repair_price;//保养价格
	private String repair_source;//保养地点
	private Double service_timelength;//保养时长
	private String engineer;//技师名称
	private Long repair_time;//服务时间
	private String repair_remarks;//保养备注
	private Long create_time;//订单生成时间
	private Long update_time;//更新时间
	
	private Integer order_id;//订单id
	private Double price;//订单价格
	private Double rewards_limit;//奖励使用额度
	
	public Integer getSpec_id() {
		return spec_id;
	}
	public void setSpec_id(Integer spec_id) {
		this.spec_id = spec_id;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public String getRepair_item_ids() {
		return repair_item_ids;
	}
	public void setRepair_item_ids(String repair_item_ids) {
		this.repair_item_ids = repair_item_ids;
	}
	public Long getOrder_date() {
		return order_date;
	}
	public void setOrder_date(Long order_date) {
		this.order_date = order_date;
	}
	public Integer getTime_region_id() {
		return time_region_id;
	}
	public void setTime_region_id(Integer time_region_id) {
		this.time_region_id = time_region_id;
	}
	public String getCarplate() {
		return carplate;
	}
	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}
	public Long getRepair_mile() {
		return repair_mile;
	}
	public void setRepair_mile(Long repair_mile) {
		this.repair_mile = repair_mile;
	}
	public Double getRepair_price() {
		return repair_price;
	}
	public void setRepair_price(Double repair_price) {
		this.repair_price = repair_price;
	}
	public String getRepair_source() {
		return repair_source;
	}
	public void setRepair_source(String repair_source) {
		this.repair_source = repair_source;
	}
	public Double getService_timelength() {
		return service_timelength;
	}
	public void setService_timelength(Double service_timelength) {
		this.service_timelength = service_timelength;
	}
	public String getEngineer() {
		return engineer;
	}
	public void setEngineer(String engineer) {
		this.engineer = engineer;
	}
	public Long getRepair_time() {
		return repair_time;
	}
	public void setRepair_time(Long repair_time) {
		this.repair_time = repair_time;
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
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Double getRewards_limit() {
		return rewards_limit;
	}
	public void setRewards_limit(Double rewards_limit) {
		this.rewards_limit = rewards_limit;
	}
	
	public String getRepair_remarks() {
		return repair_remarks;
	}
	public void setRepair_remarks(String repair_remarks) {
		this.repair_remarks = repair_remarks;
	}

}
