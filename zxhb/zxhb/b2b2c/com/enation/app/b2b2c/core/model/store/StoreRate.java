package com.enation.app.b2b2c.core.model.store;

import java.io.Serializable;

import com.enation.framework.database.PrimaryKeyField;

public class StoreRate implements Serializable{
	
	private Integer rate_id;
	
	private Integer order_type;
	
	private Integer level_id;
	
	private Double original_service_rate;
	
	private Double flow_service_rate;
	
	private Double original_handling_rate;
	
	private Double flow_handling_rate;
	
	@PrimaryKeyField
	public Integer getRate_id() {
		return rate_id;
	}

	public void setRate_id(Integer rate_id) {
		this.rate_id = rate_id;
	}

	public Integer getOrder_type() {
		return order_type;
	}

	public void setOrder_type(Integer order_type) {
		this.order_type = order_type;
	}

	public Integer getLevel_id() {
		return level_id;
	}

	public void setLevel_id(Integer level_id) {
		this.level_id = level_id;
	}

	public Double getOriginal_service_rate() {
		return original_service_rate;
	}

	public void setOriginal_service_rate(Double original_service_rate) {
		this.original_service_rate = original_service_rate;
	}

	public Double getFlow_service_rate() {
		return flow_service_rate;
	}

	public void setFlow_service_rate(Double flow_service_rate) {
		this.flow_service_rate = flow_service_rate;
	}

	public Double getOriginal_handling_rate() {
		return original_handling_rate;
	}

	public void setOriginal_handling_rate(Double original_handling_rate) {
		this.original_handling_rate = original_handling_rate;
	}

	public Double getFlow_handling_rate() {
		return flow_handling_rate;
	}

	public void setFlow_handling_rate(Double flow_handling_rate) {
		this.flow_handling_rate = flow_handling_rate;
	}



	

}
