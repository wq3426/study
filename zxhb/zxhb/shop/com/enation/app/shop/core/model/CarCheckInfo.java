package com.enation.app.shop.core.model;

/**
 * 汽车体检相关信息
 * @author yunbs 2016年6月16日
 *
 */
@SuppressWarnings("serial")
public class CarCheckInfo implements java.io.Serializable{
	
	private int id;//主键id
	private String codes;//编号
	private int sum;//总数量
	private String carplate;//车牌
	private Long check_time;//体检时间
	private String back_filed;//备用字段
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCodes() {
		return codes;
	}
	public void setCodes(String codes) {
		this.codes = codes;
	}
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	public String getCarplate() {
		return carplate;
	}
	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}
	public Long getCheck_time() {
		return check_time;
	}
	public void setCheck_time(Long check_time) {
		this.check_time = check_time;
	}
	public String getBack_filed() {
		return back_filed;
	}
	public void setBack_filed(String back_filed) {
		this.back_filed = back_filed;
	}
	
}
