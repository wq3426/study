package com.enation.app.shop.core.model;


/**
 * 不同车型、不同保养里程的保养项目信息实体
 * @author wangqiang 2016年4月14日 下午2:18:16
 *
 */
@SuppressWarnings("serial")
public class MaintainIntervalItem implements java.io.Serializable {
	private Integer id;//主键id
	private Integer carmodelid;//车型id
	private Long repairinterval;//保养间隔里程
	private String repaircontent;//保养内容
	private Long price;//价格
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getCarmodelid() {
		return carmodelid;
	}
	public void setCarmodelid(Integer carmodelid) {
		this.carmodelid = carmodelid;
	}
	public Long getRepairinterval() {
		return repairinterval;
	}
	public void setRepairinterval(Long repairinterval) {
		this.repairinterval = repairinterval;
	}
	public String getRepaircontent() {
		return repaircontent;
	}
	public void setRepaircontent(String repaircontent) {
		this.repaircontent = repaircontent;
	}
	public Long getPrice() {
		return price;
	}
	public void setPrice(Long price) {
		this.price = price;
	}
}