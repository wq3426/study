package com.enation.app.base.core.model;

/**
 * @Description 车辆收益历史
 *
 * @createTime 2016年10月20日 下午2:39:18
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public class CarGainHiatory {

	private int id;                 //id
	private int type;    			//收支项目
	private int query_id;           //查询id
	private String carplate;    	//车牌号
	private double reward;   		//收支
	private long timeline;          //截至时间
	private String detail;         	//详情
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * ===================================================================
	 * SETTER AND GETTER
	 */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getQuery_id() {
		return query_id;
	}
	public void setQuery_id(int query_id) {
		this.query_id = query_id;
	}
	public String getCarplate() {
		return carplate;
	}
	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}
	public double getReward() {
		return reward;
	}
	public void setReward(double reward) {
		this.reward = reward;
	}
	public long getTimeline() {
		return timeline;
	}
	public void setTimeline(long timeline) {
		this.timeline = timeline;
	}
	public String getDetail() {
		return detail;
	}
	public void setDetail(String detail) {
		this.detail = detail;
	}
	
	
	
	
	
	
	
	
}
