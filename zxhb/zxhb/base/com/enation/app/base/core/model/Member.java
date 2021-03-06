package com.enation.app.base.core.model;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * 会员实体
 * @author kanon 2015-9-21 添加会员实体注释
 *
 */
public class Member implements java.io.Serializable {
	private Integer member_id;		//会员ID
	private Integer lv_id;			//会员级别
	private String username;		//会员用户名
	private String email;			//电子邮箱
	private String password;		//密码
	private Long regtime;			//注册时间
	private String pw_answer;		//问题答案
	private String pw_question;		//问题
	private String name;			//会员姓名
	private Integer sex;			//性别
	private Long birthday;			//出生日期
	private Double advance;			//预存款
	private Integer province_id;	//省ID
	private Integer city_id;		//市ID
	private Integer region_id;		//区ID
	private String province;		//省	
	private String city;			//市
	private String region;			//区
	private String address;			//联系地址
	private String zip;				//邮编
	private String mobile;			//手机
	private String tel;				//电话

	private Integer info_full;		//是否完善了资料
	private Integer recommend_point_state;	//是否已经完成了推荐积分

	private Integer point;			//积分
	private String qq;				//QQ
	private String msn;				//msn
	private String remark;			//备注
	private Long lastlogin;			//最后登录时间
	private Integer logincount;		//当月登录次数
	private Integer mp; 			// 消费积分
	
	private String lvname;			// 会员等级名称，非数据库字段
	private Integer parentid; 		// 父代理商id
	private Integer agentid; 		// 代理商id
	private Integer is_cheked; 		// 是否已验证
	private String registerip; 		// 注册IP
 
	private String nickname;		//昵称
	private String face;			//头像
	private Integer midentity;		//身份
	private Long last_send_email; 	//最后发送激活邮件的时间
	private String find_code;		//找回密码代码
	
	//add from chopper 2016-02-28
	private int status;				//0 开启 1 关闭
	private Double consume;			//消费总量
	private Long lastdate;			//最后时间
	private String detail;			//维护内容 
	private Long outtime;			//过期时间
	
	private Integer belong_store_id;
	
	private String fullname;		//会员姓名
	private String idno;
	private String drivingyears;	//驾龄
	private String annualincome;	//年收入
	
	private Integer is_store; 		//是否有店铺（1：店铺会员，0：app会员）
	private Integer store_id; 		//店铺Id
	
	private String gesturelock; 	//手势锁
	private Integer rankingflag; 	//排名标识:0:close 1:open
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * =======================================================
	 * GETTER AND SETTER
	 */
	@PrimaryKeyField
	public Integer getMember_id() {
		return member_id;
	}

	public void setMember_id(Integer memberId) {
		member_id = memberId;
	}

	public Integer getLv_id() {
		// lv_id = lv_id==null?0:lv_id;
		return lv_id;
	}

	public void setLv_id(Integer lvId) {
		lv_id = lvId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getRegtime() {
		return regtime;
	}

	public void setRegtime(Long regtime) {
		this.regtime = regtime;
	}

	public String getPw_answer() {
		return pw_answer;
	}

	public void setPw_answer(String pwAnswer) {
		pw_answer = pwAnswer;
	}

	public String getPw_question() {
		return pw_question;
	}

	public void setPw_question(String pwQuestion) {
		pw_question = pwQuestion;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getSex() {
		sex= sex==null?-1:sex;
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Long getBirthday() {
		return birthday;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public Double getAdvance() {
		return advance;
	}

	public void setAdvance(Double advance) {
		this.advance = advance;
	}

	public Integer getProvince_id() {
		
		return province_id;
	}

	public void setProvince_id(Integer provinceId) {
		province_id = provinceId;
	}

	public Integer getCity_id() {
		return city_id;
	}

	public void setCity_id(Integer cityId) {
		city_id = cityId;
	}

	public Integer getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Integer regionId) {
		region_id = regionId;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public Integer getPoint() {
		if (point == null)
			point = 0;
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public Integer getMidentity() {
		return midentity;
	}

	public void setMidentity(Integer midentity) {
		this.midentity = midentity;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@NotDbField
	public String getLvname() {
		return lvname;
	}

	public void setLvname(String lvname) {
		this.lvname = lvname;
	}

	public Long getLastlogin() {
		return lastlogin;
	}

	public void setLastlogin(Long lastlogin) {
		this.lastlogin = lastlogin;
	}

	public Integer getMp() {
		return mp;
	}

	public void setMp(Integer mp) {
		this.mp = mp;
	}

	public Integer getParentid() {
		return parentid;
	}

	public void setParentid(Integer parentid) {
		this.parentid = parentid;
	}

	public Integer getIs_cheked() {
		return is_cheked;
	}

	public void setIs_cheked(Integer is_cheked) {
		this.is_cheked = is_cheked;
	}

	public Integer getLogincount() {
		return logincount;
	}

	public void setLogincount(Integer logincount) {
		this.logincount = logincount;
	}

	public Integer getAgentid() {
		return agentid;
	}

	public void setAgentid(Integer agentid) {
		this.agentid = agentid;
	}

	public String getRegisterip() {
		return registerip;
	}

	public void setRegisterip(String registerip) {
		this.registerip = registerip;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}


	public String getFind_code() {
		return find_code;
	}

	public void setFind_code(String find_code) {
		this.find_code = find_code;
	}



	public Long getLast_send_email() {
		return last_send_email;
	}

	public void setLast_send_email(Long last_send_email) {
		this.last_send_email = last_send_email;
	}

	public Integer getInfo_full() {
		return info_full;
	}

	public void setInfo_full(Integer info_full) {
		this.info_full = info_full;
	}

	public Integer getRecommend_point_state() {
		return recommend_point_state;
	}

	public void setRecommend_point_state(Integer recommend_point_state) {
		this.recommend_point_state = recommend_point_state;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Double getConsume() {
		return consume;
	}

	public void setConsume(Double consume) {
		this.consume = consume;
	}

	public Long getLastdate() {
		return lastdate;
	}

	public void setLastdate(Long lastdate) {
		this.lastdate = lastdate;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public Long getOuttime() {
		return outtime;
	}

	public void setOuttime(Long outtime) {
		this.outtime = outtime;
	}

	public Integer getBelong_store_id() {
		return belong_store_id;
	}

	public void setBelong_store_id(Integer belong_store_id) {
		this.belong_store_id = belong_store_id;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getIdno() {
		return idno;
	}

	public void setIdno(String idno) {
		this.idno = idno;
	}

	public String getDrivingyears() {
		return drivingyears;
	}

	public void setDrivingyears(String drivingyears) {
		this.drivingyears = drivingyears;
	}

	public String getAnnualincome() {
		return annualincome;
	}

	public void setAnnualincome(String annualincome) {
		this.annualincome = annualincome;
	}

	public Integer getIs_store() {
		return is_store;
	}

	public void setIs_store(Integer is_store) {
		this.is_store = is_store;
	}

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}

	public String getGesturelock() {
		return gesturelock;
	}

	public void setGesturelock(String gesturelock) {
		this.gesturelock = gesturelock;
	}

	public Integer getRankingflag() {
		return rankingflag;
	}

	public void setRankingflag(Integer rankingflag) {
		this.rankingflag = rankingflag;
	}
	
	
}