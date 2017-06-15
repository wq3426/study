package com.enation.app.dataanalysis.response.vo;

public class RankingRespVO {

	private String face;		//用户头像URL
	private String carplate;	//用户头像URL
	private String nickname;	//用户昵称
	private double income_consumption;	//油耗或者收益的数据值
	private int likecount;		//点赞数量
	private String brand;		//车辆品牌

	

	
	
	/*
	 * ------------------------------------------------------------
	 * GETTER AND SETTER
	 */
	
	

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

	public int getLikecount() {
		return likecount;
	}

	public void setLikecount(int likecount) {
		this.likecount = likecount;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getCarplate() {
		return carplate;
	}

	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}

	public double getIncome_consumption() {
		return income_consumption;
	}

	public void setIncome_consumption(double income_consumption) {
		this.income_consumption = income_consumption;
	}
	

	
	
	
}
