package com.enation.app.base.core.model;

/**
 * @Description 油耗统计实体类
 *
 * @createTime 2016年9月13日 下午4:01:53
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public class ConsumptionStatistics {
	private int id;
	private String carplate;
	private String currentdate;
	private double tital_consumption_today;
	private double tital_consumption_week;
	private double tital_consumption_month;
	private double avg_consumption_today;
	private double avg_consumption_week;
	private double avg_consumption_month;
	private double tital_mileage_today;
	private double tital_mileage_week;
	private double tital_mileage_month;
	
	

	/*
	 * --------------------------------------------------
	 * GETTER AND SETTER
	 */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCarplate() {
		return carplate;
	}
	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}
	
	public String getCurrentdate() {
		return currentdate;
	}
	public void setCurrentdate(String currentdate) {
		this.currentdate = currentdate;
	}
	public double getTital_consumption_today() {
		return tital_consumption_today;
	}
	public void setTital_consumption_today(double tital_consumption_today) {
		this.tital_consumption_today = tital_consumption_today;
	}
	public double getTital_consumption_week() {
		return tital_consumption_week;
	}
	public void setTital_consumption_week(double tital_consumption_week) {
		this.tital_consumption_week = tital_consumption_week;
	}
	public double getTital_consumption_month() {
		return tital_consumption_month;
	}
	public void setTital_consumption_month(double tital_consumption_month) {
		this.tital_consumption_month = tital_consumption_month;
	}
	public double getAvg_consumption_today() {
		return avg_consumption_today;
	}
	public void setAvg_consumption_today(double avg_consumption_today) {
		this.avg_consumption_today = avg_consumption_today;
	}
	public double getAvg_consumption_week() {
		return avg_consumption_week;
	}
	public void setAvg_consumption_week(double avg_consumption_week) {
		this.avg_consumption_week = avg_consumption_week;
	}
	public double getAvg_consumption_month() {
		return avg_consumption_month;
	}
	public void setAvg_consumption_month(double avg_consumption_month) {
		this.avg_consumption_month = avg_consumption_month;
	}
	public double getTital_mileage_today() {
		return tital_mileage_today;
	}
	public void setTital_mileage_today(double tital_mileage_today) {
		this.tital_mileage_today = tital_mileage_today;
	}
	public double getTital_mileage_week() {
		return tital_mileage_week;
	}
	public void setTital_mileage_week(double tital_mileage_week) {
		this.tital_mileage_week = tital_mileage_week;
	}
	public double getTital_mileage_month() {
		return tital_mileage_month;
	}
	public void setTital_mileage_month(double tital_mileage_month) {
		this.tital_mileage_month = tital_mileage_month;
	}
	
	
	
	
}
