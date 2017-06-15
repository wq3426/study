package com.enation.app.shop.core.model;


/**
 * 车辆信息实体
 * 
 * @author kingapex 2010-4-25下午09:40:24
 */
@SuppressWarnings("serial")
public class CarModel implements java.io.Serializable {
	private Integer id;//主键id
	private String capital;//首字母
	private Integer brand_id;//品牌id，对应表es_brand的id
	private String brand;//品牌名称
	private String brandimage;//品牌LOGO
	private String model;//汽车年款
	private String modelimage;//车型图片
	private String type;//品牌类型 进口/国产
	private String series;//车系
	private String nk;//年款
	private String sales_name;//销售名称
	private Integer seats;//核定载客数
	private String discharge;//排量
	private Integer gearboxtype;//变速箱类型 1 手动  2 自动
	private Double price;//新车购置价格
	private Long repairinterval;//保养间隔里程
	private Long repairintervaltime;//保养间隔时间 
	
	public static final long max_mile = 300000l;//车辆行驶最大公里数
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getCapital() {
		return capital;
	}
	public void setCapital(String capital) {
		this.capital = capital;
	}
	public Integer getBrand_id() {
		return brand_id;
	}
	public void setBrand_id(Integer brand_id) {
		this.brand_id = brand_id;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getBrandimage() {
		return brandimage;
	}
	public void setBrandimage(String brandimage) {
		this.brandimage = brandimage;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getModelimage() {
		return modelimage;
	}
	public void setModelimage(String modelimage) {
		this.modelimage = modelimage;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSeries() {
		return series;
	}
	public void setSeries(String series) {
		this.series = series;
	}
	public String getNk() {
		return nk;
	}
	public void setNk(String nk) {
		this.nk = nk;
	}
	public String getSales_name() {
		return sales_name;
	}
	public void setSales_name(String sales_name) {
		this.sales_name = sales_name;
	}
	public Integer getSeats() {
		return seats;
	}
	public void setSeats(Integer seats) {
		this.seats = seats;
	}
	public String getDischarge() {
		return discharge;
	}
	public void setDischarge(String discharge) {
		this.discharge = discharge;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public Long getRepairinterval() {
		return repairinterval;
	}
	public void setRepairinterval(Long repairinterval) {
		this.repairinterval = repairinterval;
	}
	public Long getRepairintervaltime() {
		return repairintervaltime;
	}
	public void setRepairintervaltime(Long repairintervaltime) {
		this.repairintervaltime = repairintervaltime;
	}
	public Integer getGearboxtype() {
		return gearboxtype;
	}
	public void setGearboxtype(Integer gearboxtype) {
		this.gearboxtype = gearboxtype;
	}
	@Override
	public String toString() {
		return "CarModel [id=" + id + ", capital=" + capital + ", brand_id=" + brand_id + ", brand=" + brand
				+ ", brandimage=" + brandimage + ", model=" + model + ", modelimage=" + modelimage + ", type=" + type
				+ ", series=" + series + ", nk=" + nk + ", sales_name=" + sales_name + ", seats=" + seats
				+ ", discharge=" + discharge + ", gearboxtype=" + gearboxtype + ", price=" + price + ", repairinterval="
				+ repairinterval + ", repairintervaltime=" + repairintervaltime + "]";
	}
}