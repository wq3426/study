package com.enation.app.b2b2c.core.model;

/**
 * @Description 保养时间段设置对象
 *
 * @createTime 2016年8月31日 下午7:07:34
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
public class RepairTimeRegion {
	private Integer time_region_id;
	private Integer store_id;//店铺id
	private String starttime;//保养起始时间
	private String endtime;//保养结束时间
	private Integer station;//工位数
	private Double ratio;//费率
	
	public Integer getTime_region_id() {
		return time_region_id;
	}
	public void setTime_region_id(Integer time_region_id) {
		this.time_region_id = time_region_id;
	}
	public Integer getStore_id() {
		return store_id;
	}
	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	public Integer getStation() {
		return station;
	}
	public void setStation(Integer station) {
		this.station = station;
	}
	public Double getRatio() {
		return ratio;
	}
	public void setRatio(Double ratio) {
		this.ratio = ratio;
	}
	
}
