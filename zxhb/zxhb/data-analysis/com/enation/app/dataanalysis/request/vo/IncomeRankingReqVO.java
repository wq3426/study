package com.enation.app.dataanalysis.request.vo;

import com.enation.framework.util.StringUtil;

/**
 * @Description 收益排名请求VO
 *
 * @createTime 2016年9月10日 上午10:25:45
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public class IncomeRankingReqVO extends BusinessReqVO{

	private String brand;			//车辆品牌
	private String cardriveregion;	//车辆行驶区域
	private String gender;			//用户性别
	private String starttime;		//起始时间
	private String endtime;			//结束时间

	
	
	/*
	 * -----------------------------------------------------------------
	 * GETTER AND SETTER
	 */

	public String getBrand() {
		return brand == null ? "" : brand.trim();
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCardriveregion() {
		return cardriveregion == null ? "" : cardriveregion.trim();
	}

	public void setCardriveregion(String cardriveregion) {
		this.cardriveregion = cardriveregion;
	}

	public int getGender() {
		
		if(StringUtil.isEmpty(gender)) {
			return 2;	//性别未知
		}
		gender = gender.trim();
		if(gender.equals("male")) {
			return 1;	//男性
		}
		if(gender.equals("female")) {
			return 0;	//女性
		}
		return 2;		
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getStarttime() {
		return starttime == null ? "" : starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime == null ? "" : endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	
	
}
