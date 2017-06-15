package com.enation.app.base.core.model;

/**
 * @Description 店铺实体类
 *
 * @createTime 2016年9月14日 下午5:18:12
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public class Store {

	private int store_id;				//店铺Id
	private String store_name;			//店铺名称
	private String short_store_name;	//店铺名称缩写
	private Integer  store_provinceid;	//省
	private Integer  store_cityid;		//市
	private Integer  store_regionid;	//区
	
	private String  store_province;		//省
	private String  store_city;			//市
	private String  store_region;		//区
	
	private String attr;				//详细地址
	private String zip;					//邮编
	private String tel;					//联系方式
	private String contacts_name;		//联系人姓名
	private String office_phone;		//办公电话
	private String dept_position;		//部门与职位
	private Integer store_level;		//店铺等级
	private Integer  member_id;			//会员Id
	private String  member_name;		//会员名称
	private String  id_number;			//联系人身份证号
	private String  id_img;				//联系人身份证照片（正面）
	private String  contact_idimg_back;	//联系人身份证照片（背面）
	private String  license_img;		//执照照片
	private Integer  disabled;			//店铺状态（0：关闭，1：开启）
	private Long  create_time;			//创建时间
	private Long  end_time;				//关闭时间
	private String  store_logo;			//店铺logo
	private String store_banner;		//店铺横幅
	private String  description;		//店铺简介
	private Integer  store_recommend;	//是否推荐
	private Integer  store_theme;		//店铺主题
	private Integer store_credit;		//店铺信用
	private Double  praise_rate;		//店铺好评率
	private Double  store_desccredit;	//描述相符度
	private Double  store_servicecredit;//服务态度分数
	private Double store_deliverycredit;//发货速度分数
	private Integer  store_collect;		//店铺收藏数量
	private Integer store_auth;			//店铺认证
	private Integer name_auth;			//店主认证
	private Integer goods_num; 			//店铺商品数量
	private String qq;					//店铺客服QQ
	
	private Double 	commission;			//店铺佣金比例
	private String 	bank_account_name;	//银行开户名   
	private String 	bank_account_number;//公司银行账号
	private String  bank_name;			//开户银行支行名称
	private String  bank_code;			//支行联行号
	private String  bank_license_img;	//银行开户许可证
	private Integer bank_provinceid;	//开户银行所在省Id
	private Integer bank_cityid;		//开户银行所在市Id
	private Integer bank_regionid;		//开户银行所在区Id
	private String  bank_province;		//开户银行所在省
	private String  bank_city;			//开户银行所在市
	private String  bank_region;		//开户银行所在区
	private String  bank_attr;			//开户银行详细地址
	
	
	//2015-8-6 author:TALON 新增字段 小区、坐标
	private Integer community_id;		//小区Id
	private String community;			//小区
	private Double longitude;			//经度
	private Double latitude;			//纬度
	
	
	private Double comment_grade; 		//店铺评分
	//2016-02-27 add by _ Sylow
	/**客服电话*/
	private String customer_phone;
	/**售后电话*/
	private String after_phone;
	/**电子邮件*/
	private String email;
	/**资质*/
	private String qualification;
	/**商家简介*/
	private String store_desc;
	/**支付相关（企业帐号）*/
	private String payment_account;
	/**品牌*/
	private Integer brand_id;
	private String brand_name;
	
	private Double discountcontract;		//签约折扣
	private Double discountnoncontract;		//非签约折扣
	private String corporation;				//企业法人姓名
	private String corporation_id_number;	//法人身份证号
	private String corporation_id_img;		//法人身份证照片（正面）
	private String corporate_idimg_back;	//法人身份证照片（背面）
	private String bussiness_license_number;//营业执照注册号
	private String alipay_account;			//支付宝账号
	private String weichat_account;			//微信账号
	
	private Integer settlement_period ;		//店铺结算周期
	private Integer repair_time_range;		//店铺保养预约时间范围
	
	private String cashingout_password;		//提现密码
	private String cashingouter_phone;		//提现人手机
	private String auditstatus;				//审核状态（0：待修改 1：待审核 2：已审核）
	
	private String docker;					//对接人姓名
	private String docker_job_number;		//对接人工号
	private String docker_tel;				//对接人联系方式

	
	
	
	
	/*
	 * ============================================================================
	 * GETTER AND SETTER
	 */
	public int getStore_id() {
		return store_id;
	}
	public String getBrand_name() {
		return brand_name;
	}
	public void setBrand_name(String brand_name) {
		this.brand_name = brand_name;
	}
	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}
	public String getStore_name() {
		return store_name;
	}
	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	public Integer getStore_provinceid() {
		return store_provinceid;
	}
	public void setStore_provinceid(Integer store_provinceid) {
		this.store_provinceid = store_provinceid;
	}
	public Integer getStore_cityid() {
		return store_cityid;
	}
	public void setStore_cityid(Integer store_cityid) {
		this.store_cityid = store_cityid;
	}
	public Integer getStore_regionid() {
		return store_regionid;
	}
	public void setStore_regionid(Integer store_regionid) {
		this.store_regionid = store_regionid;
	}
	public String getStore_province() {
		return store_province;
	}
	public void setStore_province(String store_province) {
		this.store_province = store_province;
	}
	public String getStore_city() {
		return store_city;
	}
	public void setStore_city(String store_city) {
		this.store_city = store_city;
	}
	public String getStore_region() {
		return store_region;
	}
	public void setStore_region(String store_region) {
		this.store_region = store_region;
	}
	public String getAttr() {
		return attr;
	}
	public void setAttr(String attr) {
		this.attr = attr;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getContacts_name() {
		return contacts_name;
	}
	public void setContacts_name(String contacts_name) {
		this.contacts_name = contacts_name;
	}
	public Integer getStore_level() {
		return store_level;
	}
	public void setStore_level(Integer store_level) {
		this.store_level = store_level;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public String getMember_name() {
		return member_name;
	}
	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}
	public String getId_number() {
		return id_number;
	}
	public void setId_number(String id_number) {
		this.id_number = id_number;
	}
	public String getId_img() {
		return id_img;
	}
	public void setId_img(String id_img) {
		this.id_img = id_img;
	}
	public String getLicense_img() {
		return license_img;
	}
	public void setLicense_img(String license_img) {
		this.license_img = license_img;
	}
	public Integer getDisabled() {
		return disabled;
	}
	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public Long getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Long end_time) {
		this.end_time = end_time;
	}
	public String getStore_logo() {
		return store_logo;
	}
	public void setStore_logo(String store_logo) {
		this.store_logo = store_logo;
	}
	public String getStore_banner() {
		return store_banner;
	}
	public void setStore_banner(String store_banner) {
		this.store_banner = store_banner;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getStore_recommend() {
		return store_recommend;
	}
	public void setStore_recommend(Integer store_recommend) {
		this.store_recommend = store_recommend;
	}
	public Integer getStore_theme() {
		return store_theme;
	}
	public void setStore_theme(Integer store_theme) {
		this.store_theme = store_theme;
	}
	public Integer getStore_credit() {
		return store_credit;
	}
	public void setStore_credit(Integer store_credit) {
		this.store_credit = store_credit;
	}
	public Double getPraise_rate() {
		return praise_rate;
	}
	public void setPraise_rate(Double praise_rate) {
		this.praise_rate = praise_rate;
	}
	public Double getStore_desccredit() {
		return store_desccredit;
	}
	public void setStore_desccredit(Double store_desccredit) {
		this.store_desccredit = store_desccredit;
	}
	public Double getStore_servicecredit() {
		return store_servicecredit;
	}
	public void setStore_servicecredit(Double store_servicecredit) {
		this.store_servicecredit = store_servicecredit;
	}
	public Double getStore_deliverycredit() {
		return store_deliverycredit;
	}
	public void setStore_deliverycredit(Double store_deliverycredit) {
		this.store_deliverycredit = store_deliverycredit;
	}
	public Integer getStore_collect() {
		return store_collect;
	}
	public void setStore_collect(Integer store_collect) {
		this.store_collect = store_collect;
	}
	public Integer getStore_auth() {
		return store_auth;
	}
	public void setStore_auth(Integer store_auth) {
		this.store_auth = store_auth;
	}
	public Integer getName_auth() {
		return name_auth;
	}
	public void setName_auth(Integer name_auth) {
		this.name_auth = name_auth;
	}
	public Integer getGoods_num() {
		return goods_num;
	}
	public void setGoods_num(Integer goods_num) {
		this.goods_num = goods_num;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
	}
	public Double getCommission() {
		return commission;
	}
	public void setCommission(Double commission) {
		this.commission = commission;
	}
	public String getBank_account_name() {
		return bank_account_name;
	}
	public void setBank_account_name(String bank_account_name) {
		this.bank_account_name = bank_account_name;
	}
	public String getBank_account_number() {
		return bank_account_number;
	}
	public void setBank_account_number(String bank_account_number) {
		this.bank_account_number = bank_account_number;
	}
	public String getBank_name() {
		return bank_name;
	}
	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}
	public String getBank_code() {
		return bank_code;
	}
	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}
	public Integer getBank_provinceid() {
		return bank_provinceid;
	}
	public void setBank_provinceid(Integer bank_provinceid) {
		this.bank_provinceid = bank_provinceid;
	}
	public Integer getBank_cityid() {
		return bank_cityid;
	}
	public void setBank_cityid(Integer bank_cityid) {
		this.bank_cityid = bank_cityid;
	}
	public Integer getBank_regionid() {
		return bank_regionid;
	}
	public void setBank_regionid(Integer bank_regionid) {
		this.bank_regionid = bank_regionid;
	}
	public String getBank_province() {
		return bank_province;
	}
	public void setBank_province(String bank_province) {
		this.bank_province = bank_province;
	}
	public String getBank_city() {
		return bank_city;
	}
	public void setBank_city(String bank_city) {
		this.bank_city = bank_city;
	}
	public String getBank_region() {
		return bank_region;
	}
	public void setBank_region(String bank_region) {
		this.bank_region = bank_region;
	}
	public Integer getCommunity_id() {
		return community_id;
	}
	public void setCommunity_id(Integer community_id) {
		this.community_id = community_id;
	}
	public String getCommunity() {
		return community;
	}
	public void setCommunity(String community) {
		this.community = community;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public String getCustomer_phone() {
		return customer_phone;
	}
	public void setCustomer_phone(String customer_phone) {
		this.customer_phone = customer_phone;
	}
	public String getAfter_phone() {
		return after_phone;
	}
	public void setAfter_phone(String after_phone) {
		this.after_phone = after_phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getQualification() {
		return qualification;
	}
	public void setQualification(String qualification) {
		this.qualification = qualification;
	}
	public String getStore_desc() {
		return store_desc;
	}
	public void setStore_desc(String store_desc) {
		this.store_desc = store_desc;
	}
	public String getPayment_account() {
		return payment_account;
	}
	public void setPayment_account(String payment_account) {
		this.payment_account = payment_account;
	}
	public Integer getBrand_id() {
		return brand_id;
	}
	public void setBrand_id(Integer brand_id) {
		this.brand_id = brand_id;
	}
	public Double getDiscountcontract() {
		return discountcontract;
	}
	public void setDiscountcontract(Double discountcontract) {
		this.discountcontract = discountcontract;
	}
	public Double getDiscountnoncontract() {
		return discountnoncontract;
	}
	public void setDiscountnoncontract(Double discountnoncontract) {
		this.discountnoncontract = discountnoncontract;
	}
	public String getCorporation() {
		return corporation;
	}
	public void setCorporation(String corporation) {
		this.corporation = corporation;
	}
	public String getCorporation_id_number() {
		return corporation_id_number;
	}
	public void setCorporation_id_number(String corporation_id_number) {
		this.corporation_id_number = corporation_id_number;
	}
	public String getCorporation_id_img() {
		return corporation_id_img;
	}
	public void setCorporation_id_img(String corporation_id_img) {
		this.corporation_id_img = corporation_id_img;
	}
	public String getBussiness_license_number() {
		return bussiness_license_number;
	}
	public void setBussiness_license_number(String bussiness_license_number) {
		this.bussiness_license_number = bussiness_license_number;
	}
	public String getAlipay_account() {
		return alipay_account;
	}
	public void setAlipay_account(String alipay_account) {
		this.alipay_account = alipay_account;
	}
	public String getWeichat_account() {
		return weichat_account;
	}
	public void setWeichat_account(String weichat_account) {
		this.weichat_account = weichat_account;
	}
	public String getShort_store_name() {
		return short_store_name;
	}
	public void setShort_store_name(String short_store_name) {
		this.short_store_name = short_store_name;
	}
	public Double getComment_grade() {
		return comment_grade;
	}
	public void setComment_grade(Double comment_grade) {
		this.comment_grade = comment_grade;
	}
	public Integer getSettlement_period() {
		return settlement_period;
	}
	public void setSettlement_period(Integer settlement_period) {
		this.settlement_period = settlement_period;
	}
	public String getDept_position() {
		return dept_position;
	}
	public void setDept_position(String dept_position) {
		this.dept_position = dept_position;
	}
	public Integer getRepair_time_range() {
		return repair_time_range;
	}
	public void setRepair_time_range(Integer repair_time_range) {
		this.repair_time_range = repair_time_range;
	}
	public String getCashingout_password() {
		return cashingout_password;
	}
	public void setCashingout_password(String cashingout_password) {
		this.cashingout_password = cashingout_password;
	}
	public String getCashingouter_phone() {
		return cashingouter_phone;
	}
	public void setCashingouter_phone(String cashingouter_phone) {
		this.cashingouter_phone = cashingouter_phone;
	}
	public String getAuditstatus() {
		return auditstatus;
	}
	public void setAuditstatus(String auditstatus) {
		this.auditstatus = auditstatus;
	}
	public String getDocker() {
		return docker;
	}
	public void setDocker(String docker) {
		this.docker = docker;
	}
	public String getDocker_job_number() {
		return docker_job_number;
	}
	public void setDocker_job_number(String docker_job_number) {
		this.docker_job_number = docker_job_number;
	}
	public String getDocker_tel() {
		return docker_tel;
	}
	public void setDocker_tel(String docker_tel) {
		this.docker_tel = docker_tel;
	}
	public String getOffice_phone() {
		return office_phone;
	}
	public void setOffice_phone(String office_phone) {
		this.office_phone = office_phone;
	}
	public String getBank_attr() {
		return bank_attr;
	}
	public void setBank_attr(String bank_attr) {
		this.bank_attr = bank_attr;
	}
	public String getBank_license_img() {
		return bank_license_img;
	}
	public void setBank_license_img(String bank_license_img) {
		this.bank_license_img = bank_license_img;
	}
	public String getContact_idimg_back() {
		return contact_idimg_back;
	}
	public void setContact_idimg_back(String contact_idimg_back) {
		this.contact_idimg_back = contact_idimg_back;
	}
	public String getCorporate_idimg_back() {
		return corporate_idimg_back;
	}
	public void setCorporate_idimg_back(String corporate_idimg_back) {
		this.corporate_idimg_back = corporate_idimg_back;
	}
	
	
	
	
}
