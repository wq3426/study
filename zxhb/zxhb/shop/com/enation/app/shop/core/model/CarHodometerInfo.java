package com.enation.app.shop.core.model;


/**
 * 汽车行程信息
 * @author wangqiang 2016年4月8日 下午3:04:50
 *
 */
@SuppressWarnings("serial")
public class CarHodometerInfo implements java.io.Serializable {
	private int id;//主键id
	private String carplate;//车牌
	private Long starttime;//起始时间
	private Long hotcartime;//热车时长
	private Long idlingtime;//怠速时长
	private Long drivingtime;//驾驶时长
	private Double mile;//本次行驶里程
	private Long totalmile;//总里程
	private Double idlingoilconsumption;//本次怠速耗油量
	private Double oilconsumption;//本次行驶耗油量
	private Integer maxspeed; //本次最高车速
	private Integer maxrevolution; //本次最高转速
	private Integer fastaccelarationnum; //本次急加速次数
	private Integer slambrakenum; //本次急减速次数
	private Double reward;//本次行程奖励
	private Double deduction;//危险扣除金额
	private String carstatus;//车况
	private String drivingaction;//驾驶行为
	private Integer drivingscore;//驾驶得分
	private Double insuregain; //保险收益
	private Double repairgain; //保养收益
	private Integer overdriving;//疲劳驾驶次数
	private Integer overspeed;//超速次数
	private Integer maxrevolution_overtime;//最大转速超速次数(大于5000)
	private String gpsinfo;//GPS数据（GPS: [1,2,3,4]）
	private Integer needretry;//是否为重传行程数据
	
	
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
	public Long getStarttime() {
		return starttime;
	}
	public void setStarttime(Long starttime) {
		this.starttime = starttime;
	}
	public Long getHotcartime() {
		return hotcartime;
	}
	public void setHotcartime(Long hotcartime) {
		this.hotcartime = hotcartime;
	}
	public Long getIdlingtime() {
		return idlingtime;
	}
	public void setIdlingtime(Long idlingtime) {
		this.idlingtime = idlingtime;
	}
	public Long getDrivingtime() {
		return drivingtime;
	}
	public void setDrivingtime(Long drivingtime) {
		this.drivingtime = drivingtime;
	}
	public Double getMile() {
		return mile;
	}
	public void setMile(Double mile) {
		this.mile = mile;
	}
	public Long getTotalmile() {
		return totalmile;
	}
	public void setTotalmile(Long totalmile) {
		this.totalmile = totalmile;
	}
	public Double getIdlingoilconsumption() {
		return idlingoilconsumption;
	}
	public void setIdlingoilconsumption(Double idlingoilconsumption) {
		this.idlingoilconsumption = idlingoilconsumption;
	}
	public Double getOilconsumption() {
		return oilconsumption;
	}
	public void setOilconsumption(Double oilconsumption) {
		this.oilconsumption = oilconsumption;
	}
	public Integer getMaxspeed() {
		return maxspeed;
	}
	public void setMaxspeed(Integer maxspeed) {
		this.maxspeed = maxspeed;
	}
	public Integer getMaxrevolution() {
		return maxrevolution;
	}
	public void setMaxrevolution(Integer maxrevolution) {
		this.maxrevolution = maxrevolution;
	}
	public Integer getFastaccelarationnum() {
		return fastaccelarationnum;
	}
	public void setFastaccelarationnum(Integer fastaccelarationnum) {
		this.fastaccelarationnum = fastaccelarationnum;
	}
	public Integer getSlambrakenum() {
		return slambrakenum;
	}
	public void setSlambrakenum(Integer slambrakenum) {
		this.slambrakenum = slambrakenum;
	}
	public Double getReward() {
		return reward;
	}
	public void setReward(Double reward) {
		this.reward = reward;
	}
	public Double getDeduction() {
		return deduction;
	}
	public void setDeduction(Double deduction) {
		this.deduction = deduction;
	}
	public String getCarstatus() {
		return carstatus;
	}
	public void setCarstatus(String carstatus) {
		this.carstatus = carstatus;
	}
	public String getDrivingaction() {
		return drivingaction;
	}
	public void setDrivingaction(String drivingaction) {
		this.drivingaction = drivingaction;
	}
	public Integer getDrivingscore() {
		return drivingscore;
	}
	public void setDrivingscore(Integer drivingscore) {
		this.drivingscore = drivingscore;
	}
	public Double getInsuregain() {
		return insuregain;
	}
	public void setInsuregain(Double insuregain) {
		this.insuregain = insuregain;
	}
	public Double getRepairgain() {
		return repairgain;
	}
	public void setRepairgain(Double repairgain) {
		this.repairgain = repairgain;
	}
	public String getGpsinfo() {
		return gpsinfo;
	}
	public void setGpsinfo(String gpsinfo) {
		this.gpsinfo = gpsinfo;
	}
	public Integer getOverdriving() {
		return overdriving;
	}
	public void setOverdriving(Integer overdriving) {
		this.overdriving = overdriving;
	}
	public Integer getOverspeed() {
		return overspeed;
	}
	public void setOverspeed(Integer overspeed) {
		this.overspeed = overspeed;
	}
	public Integer getMaxrevolution_overtime() {
		return maxrevolution_overtime;
	}
	public void setMaxrevolution_overtime(Integer maxrevolution_overtime) {
		this.maxrevolution_overtime = maxrevolution_overtime;
	}
	public Integer getNeedretry() {
		return needretry;
	}
	public void setNeedretry(Integer needretry) {
		this.needretry = needretry;
	}
	
	@Override
	public String toString() {
		return "CarHodometerInfo [id=" + id + ", carplate=" + carplate + ", starttime=" + starttime + ", hotcartime="
				+ hotcartime + ", idlingtime=" + idlingtime + ", drivingtime=" + drivingtime + ", mile=" + mile
				+ ", totalmile=" + totalmile + ", idlingoilconsumption=" + idlingoilconsumption + ", oilconsumption="
				+ oilconsumption + ", maxspeed=" + maxspeed + ", maxrevolution=" + maxrevolution
				+ ", fastaccelarationnum=" + fastaccelarationnum + ", slambrakenum=" + slambrakenum + ", reward="
				+ reward + ", deduction=" + deduction + ", carstatus=" + carstatus + ", drivingaction=" + drivingaction
				+ ", drivingscore=" + drivingscore + ", insuregain=" + insuregain + ", repairgain=" + repairgain
				+ ", overdriving=" + overdriving + ", overspeed=" + overspeed + ", maxrevolution_overtime="
				+ maxrevolution_overtime + ", gpsinfo=" + gpsinfo + ", needretry=" + needretry + "]";
	}
}