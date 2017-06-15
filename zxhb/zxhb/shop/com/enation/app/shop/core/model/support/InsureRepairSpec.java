package com.enation.app.shop.core.model.support;

/**
 * @Description 保险/保养订单详情model类
 *
 * @createTime 2016年9月12日 下午7:39:18
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
public class InsureRepairSpec {

	private Integer spec_id;
	
	//保险
	private Integer insure_company_id;//下单的保险公司id
	private String select_insure_typeids;//选择的险种类型id字符串
	private String insure_params;//险种类型参数字符串
	private String applicant;//投保人
	private String applicant_id;//投保人身份证号
	private Long insure_starttime;//保险起始时间
	private Long insure_endtime;//保险结束时间
	private String policy_no;//保单号
	private Long repair_coin;//保养币
	
	//保养
	private String repair_item_ids;//保养预约的项目内容
	private Long order_date;//保养预约日期
	private Integer time_region_id;//保养时间段
	private Long repair_mile;//保养里程
	private Double repair_price;//保养价格
	private String repair_source;//保养地点
	private Double service_timelength;//保养时长
	private String engineer;//技师名称
	private Long repair_time;//服务时间
	
	//公共参数
	private String carplate;//保险、保养预约车牌号
	private Integer store_id;//保险、保养预约店铺id
	private Integer order_id;//订单id
	private Double price;//订单价格
	private Double rewards_limit;//奖励使用额度
	private Long create_time;//订单生成时间
	private Long update_time;//更新时间
	
	private Long order_time;//旧版本字段

	public Integer getSpec_id() {
		return spec_id;
	}

	public void setSpec_id(Integer spec_id) {
		this.spec_id = spec_id;
	}

	public Integer getInsure_company_id() {
		return insure_company_id;
	}

	public void setInsure_company_id(Integer insure_company_id) {
		this.insure_company_id = insure_company_id;
	}

	public String getSelect_insure_typeids() {
		return select_insure_typeids;
	}

	public void setSelect_insure_typeids(String select_insure_typeids) {
		this.select_insure_typeids = select_insure_typeids;
	}

	public String getInsure_params() {
		return insure_params;
	}

	public void setInsure_params(String insure_params) {
		this.insure_params = insure_params;
	}

	public String getApplicant() {
		return applicant;
	}

	public void setApplicant(String applicant) {
		this.applicant = applicant;
	}

	public String getApplicant_id() {
		return applicant_id;
	}

	public void setApplicant_id(String applicant_id) {
		this.applicant_id = applicant_id;
	}

	public Long getInsure_starttime() {
		return insure_starttime;
	}

	public void setInsure_starttime(Long insure_starttime) {
		this.insure_starttime = insure_starttime;
	}

	public Long getInsure_endtime() {
		return insure_endtime;
	}

	public void setInsure_endtime(Long insure_endtime) {
		this.insure_endtime = insure_endtime;
	}

	public String getPolicy_no() {
		return policy_no;
	}

	public void setPolicy_no(String policy_no) {
		this.policy_no = policy_no;
	}

	public Long getRepair_coin() {
		return repair_coin;
	}

	public void setRepair_coin(Long repair_coin) {
		this.repair_coin = repair_coin;
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

	public String getCarplate() {
		return carplate;
	}

	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
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

	public Long getOrder_time() {
		return order_time;
	}

	public void setOrder_time(Long order_time) {
		this.order_time = order_time;
	}

	@Override
	public String toString() {
		return "InsureRepairSpec [spec_id=" + spec_id + ", insure_company_id=" + insure_company_id
				+ ", select_insure_typeids=" + select_insure_typeids + ", insure_params=" + insure_params
				+ ", applicant=" + applicant + ", applicant_id=" + applicant_id + ", insure_starttime="
				+ insure_starttime + ", insure_endtime=" + insure_endtime + ", policy_no=" + policy_no
				+ ", repair_coin=" + repair_coin + ", repair_item_ids=" + repair_item_ids + ", order_date=" + order_date
				+ ", time_region_id=" + time_region_id + ", repair_mile=" + repair_mile + ", repair_price="
				+ repair_price + ", repair_source=" + repair_source + ", service_timelength=" + service_timelength
				+ ", engineer=" + engineer + ", repair_time=" + repair_time + ", carplate=" + carplate + ", store_id="
				+ store_id + ", order_id=" + order_id + ", price=" + price + ", rewards_limit=" + rewards_limit
				+ ", create_time=" + create_time + ", update_time=" + update_time + ", order_time=" + order_time + "]";
	}
	
}
