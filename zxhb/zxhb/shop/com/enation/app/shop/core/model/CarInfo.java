package com.enation.app.shop.core.model;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 汽车相关信息
 * @author linsen 2016年4月5日
 *
 */
@SuppressWarnings("serial")
public class CarInfo implements java.io.Serializable {
	private int id;//主键id
	
	private String carusecharacter;//使用性质  
	private Long carfirstbuytime;//首次购买时间
	
	//保险
	private String insuresetid;//在保险表(es_insurances)中对应的表项
	
	
	private String origin;//车主来源
	private Long contracttime;//签约时间
	private String contract_name;//签约人
	private String insure_source;//保险购买来源
	private String repair_source;//保养购买来源
	
	
	private String carplate;//车牌			
	private String obdmacaddr; //OBD 物理地址
	private String carowner; //车主账号
	private Integer carmodelid; //在车型表中对应的表项
	private String carmodelimage;//车型图片
	private String cartype;//汽车类型   国产、进口
	private String carvin;  //车架号			
	private String carengineno;//发动机号	
	private String brand_type_no;//品牌型号
	private String caroiltype; //油耗类型	
	private Long caravgyearmile; //年平均行驶里程	
	private String cardriveregion; //车辆属地
	//保险
	private Long car_register_time;//车辆行驶证注册时间
	private Double car_age;//车龄	
	private Integer car_use_type;//车辆使用性质   1 家庭自用车   2  企业非营业客车  3  机关非营业客车  4  营业客车-出租  5  营业客车-城市公交  6 营业客车-公路客运  7 非营业货车  8 营业货车
	private Integer car_kind;//车辆类别   1 9座以下客车  2 10座以上客车  3 微型载货汽车  4 载货汽车(带拖挂) 5 低速货车/三轮汽车  6 其他车辆
	private Integer original_storeid;//初装4s店
	private Double insureestimatedmaxgain; //预计最大收益
	private Double insureestimatedfee;//预计保费
	private Long insurenextbuytime; //下次购买时间
	private Long estimated_repair_coin;//预估能获得的保养币
	//保养
	private Integer repair4sstoreid;//在保养表(es_store)中对应的表项
	private Long repairlastmile; //上次保养里程
	private Long repairlasttime; //上次保养时间
	private Long repairnexttime;//下次保养时间
	private Double repairlastprice; //上次保养价格
	private String repairlastcontent; //上次保养内容
	private Double repairestimatedmaxgain; //预计最大保养收益
	private Double repairestimatedfee;//预计保费
	private String repairtrack;//保养营销跟踪
	private Long repairlastmile_updatetime;//上次保养里程更新时间	
	//收益
	private Double insuretotalgain; //保险总收益
	private Double repairtotalgain; //保养总收益
	private Double totalgain; //总收益
	private Long repair_total_coin;//车辆保养币总数
	private Long totalmile; //总里程
	private Long create_time;//创建时间
	
	private Integer has_set_repair_info;//是否设置了保养信息 1 是 0 否
	
	public static Map<Integer, String> carUseTypeMap;//车辆使用性质map
	public static Map<Integer, String> carKindMap;//车辆类别map
	
	public static final int DRIVING_INCOME = 1;//驾驶收益
	public static final int ORDER_INCOME_USE = 2;//订单支付使用收益 
	public static final int INSURE_PAY_REPAIR_COIN_GET = 3;//保险订单支付后获得的保养币
	public static final int ORDER_REPAIR_COIN_USE = 4;//订单支付使用保养币
	
	static {
		carUseTypeMap = new HashMap<Integer, String>();
		
		carUseTypeMap.put(1, "家庭自用车");
		carUseTypeMap.put(2, "企业非营业客车");
		carUseTypeMap.put(3, "机关非营业客车");
//		carUseTypeMap.put(4, "营业客车-出租");
//		carUseTypeMap.put(5, "营业客车-城市公交");
//		carUseTypeMap.put(6, "营业客车-公路客运");
//		carUseTypeMap.put(7, "非营业货车");
//		carUseTypeMap.put(8, "营业货车");
		
		carKindMap = new HashMap<Integer, String>();
		
		carKindMap.put(1, "9座以下客车");
//		carKindMap.put(2, "10座以上客车");
//		carKindMap.put(3, "微型载货汽车");
//		carKindMap.put(4, "载货汽车(带拖挂)");
//		carKindMap.put(5, "低速货车/三轮汽车");
//		carKindMap.put(6, "其他车辆");
	}
	
	/**
	 * @description 获取车辆使用性质json
	 * @date 2016年9月13日 下午8:42:07
	 * @return
	 */
	public static JSONArray getCarUseTypeJson(){
		JSONArray returnArray = new JSONArray();
		
		for(Map.Entry<Integer, String> m : carUseTypeMap.entrySet()){
			JSONObject obj = new JSONObject();
			obj.put("type_id", m.getKey());
			obj.put("type_value", m.getValue());
			
			returnArray.add(obj);
		}
		
		return returnArray;
	}
	
	/**
	 * @description 获取车辆种类json
	 * @date 2016年9月13日 下午8:43:42
	 * @return
	 */
	public static JSONArray getCarKindMap(){
		JSONArray returnArray = new JSONArray();
		
		for(Map.Entry<Integer, String> m : carKindMap.entrySet()){
			JSONObject obj = new JSONObject();
			obj.put("type_id", m.getKey());
			obj.put("type_value", m.getValue());
			
			returnArray.add(obj);
		}
		
		return returnArray;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getCarusecharacter() {
		return carusecharacter;
	}
	public void setCarusecharacter(String carusecharacter) {
		this.carusecharacter = carusecharacter;
	}
	public Long getCarfirstbuytime() {
		return carfirstbuytime;
	}
	public void setCarfirstbuytime(Long carfirstbuytime) {
		this.carfirstbuytime = carfirstbuytime;
	}
	public String getCaroiltype() {
		return caroiltype;
	}
	public void setCaroiltype(String caroiltype) {
		this.caroiltype = caroiltype;
	}
	public Long getCaravgyearmile() {
		return caravgyearmile;
	}
	public void setCaravgyearmile(Long caravgyearmile) {
		this.caravgyearmile = caravgyearmile;
	}
	public String getCardriveregion() {
		return cardriveregion;
	}
	public void setCardriveregion(String cardriveregion) {
		this.cardriveregion = cardriveregion;
	}
	public String getInsuresetid() {
		return insuresetid;
	}
	public void setInsuresetid(String insuresetid) {
		this.insuresetid = insuresetid;
	}
	public Double getInsureestimatedmaxgain() {
		return insureestimatedmaxgain;
	}
	public void setInsureestimatedmaxgain(Double insureestimatedmaxgain) {
		this.insureestimatedmaxgain = insureestimatedmaxgain;
	}
	public Double getInsureestimatedfee() {
		return insureestimatedfee;
	}
	public void setInsureestimatedfee(Double insureestimatedfee) {
		this.insureestimatedfee = insureestimatedfee;
	}
	public Long getInsurenextbuytime() {
		return insurenextbuytime;
	}
	public void setInsurenextbuytime(Long insurenextbuytime) {
		this.insurenextbuytime = insurenextbuytime;
	}
	public Integer getRepair4sstoreid() {
		return repair4sstoreid;
	}
	public void setRepair4sstoreid(Integer repair4sstoreid) {
		this.repair4sstoreid = repair4sstoreid;
	}
	public Long getRepairlastmile() {
		return repairlastmile;
	}
	public void setRepairlastmile(Long repairlastmile) {
		this.repairlastmile = repairlastmile;
	}
	public Long getRepairlasttime() {
		return repairlasttime;
	}
	public void setRepairlasttime(Long repairlasttime) {
		this.repairlasttime = repairlasttime;
	}
	public Long getRepairnexttime() {
		return repairnexttime;
	}
	public void setRepairnexttime(Long repairnexttime) {
		this.repairnexttime = repairnexttime;
	}
	public Double getRepairlastprice() {
		return repairlastprice;
	}
	public void setRepairlastprice(Double repairlastprice) {
		this.repairlastprice = repairlastprice;
	}
	public String getRepairlastcontent() {
		return repairlastcontent;
	}
	public void setRepairlastcontent(String repairlastcontent) {
		this.repairlastcontent = repairlastcontent;
	}
	public Double getRepairestimatedmaxgain() {
		return repairestimatedmaxgain;
	}
	public void setRepairestimatedmaxgain(Double repairestimatedmaxgain) {
		this.repairestimatedmaxgain = repairestimatedmaxgain;
	}
	public Double getRepairestimatedfee() {
		return repairestimatedfee;
	}
	public void setRepairestimatedfee(Double repairestimatedfee) {
		this.repairestimatedfee = repairestimatedfee;
	}
	public String getRepairtrack() {
		return repairtrack;
	}
	public void setRepairtrack(String repairtrack) {
		this.repairtrack = repairtrack;
	}
	public Long getRepairlastmile_updatetime() {
		return repairlastmile_updatetime;
	}
	public void setRepairlastmile_updatetime(Long repairlastmile_updatetime) {
		this.repairlastmile_updatetime = repairlastmile_updatetime;
	}
	public Double getInsuretotalgain() {
		return insuretotalgain;
	}
	public void setInsuretotalgain(Double insuretotalgain) {
		this.insuretotalgain = insuretotalgain;
	}
	public Double getRepairtotalgain() {
		return repairtotalgain;
	}
	public void setRepairtotalgain(Double repairtotalgain) {
		this.repairtotalgain = repairtotalgain;
	}
	public Double getTotalgain() {
		return totalgain;
	}
	public void setTotalgain(Double totalgain) {
		this.totalgain = totalgain;
	}
	public Long getTotalmile() {
		return totalmile;
	}
	public void setTotalmile(Long totalmile) {
		this.totalmile = totalmile;
	}
	public String getOrigin() {
		return origin;
	}
	public void setOrigin(String origin) {
		this.origin = origin;
	}
	public Long getContracttime() {
		return contracttime;
	}
	public void setContracttime(Long contracttime) {
		this.contracttime = contracttime;
	}
	public String getContract_name() {
		return contract_name;
	}
	public void setContract_name(String contract_name) {
		this.contract_name = contract_name;
	}
	public String getInsure_source() {
		return insure_source;
	}
	public void setInsure_source(String insure_source) {
		this.insure_source = insure_source;
	}
	public String getRepair_source() {
		return repair_source;
	}
	public void setRepair_source(String repair_source) {
		this.repair_source = repair_source;
	}
	public String getObdmacaddr() {
		return obdmacaddr;
	}
	public void setObdmacaddr(String obdmacaddr) {
		this.obdmacaddr = obdmacaddr;
	}
	public Integer getOriginal_storeid() {
		return original_storeid;
	}
	public void setOriginal_storeid(Integer original_storeid) {
		this.original_storeid = original_storeid;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public String getCarplate() {
		return carplate;
	}
	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}
	public String getCarowner() {
		return carowner;
	}
	public void setCarowner(String carowner) {
		this.carowner = carowner;
	}
	public Integer getCarmodelid() {
		return carmodelid;
	}
	public void setCarmodelid(Integer carmodelid) {
		this.carmodelid = carmodelid;
	}
	public String getCarmodelimage() {
		return carmodelimage;
	}
	public void setCarmodelimage(String carmodelimage) {
		this.carmodelimage = carmodelimage;
	}
	public String getCartype() {
		return cartype;
	}
	public void setCartype(String cartype) {
		this.cartype = cartype;
	}
	public String getCarvin() {
		return carvin;
	}
	public void setCarvin(String carvin) {
		this.carvin = carvin;
	}
	public String getCarengineno() {
		return carengineno;
	}
	public void setCarengineno(String carengineno) {
		this.carengineno = carengineno;
	}
	public String getBrand_type_no() {
		return brand_type_no;
	}
	public void setBrand_type_no(String brand_type_no) {
		this.brand_type_no = brand_type_no;
	}
	public Long getCar_register_time() {
		return car_register_time;
	}
	public void setCar_register_time(Long car_register_time) {
		this.car_register_time = car_register_time;
	}
	public Double getCar_age() {
		return car_age;
	}
	public void setCar_age(Double car_age) {
		this.car_age = car_age;
	}
	public Integer getCar_use_type() {
		return car_use_type;
	}
	public void setCar_use_type(Integer car_use_type) {
		this.car_use_type = car_use_type;
	}
	public Integer getCar_kind() {
		return car_kind;
	}
	public void setCar_kind(Integer car_kind) {
		this.car_kind = car_kind;
	}

	public Long getEstimated_repair_coin() {
		return estimated_repair_coin;
	}

	public void setEstimated_repair_coin(Long estimated_repair_coin) {
		this.estimated_repair_coin = estimated_repair_coin;
	}

	public Long getRepair_total_coin() {
		return repair_total_coin;
	}

	public void setRepair_total_coin(Long repair_total_coin) {
		this.repair_total_coin = repair_total_coin;
	}

	public int getHas_set_repair_info() {
		return has_set_repair_info;
	}

	public void setHas_set_repair_info(Integer has_set_repair_info) {
		this.has_set_repair_info = has_set_repair_info;
	}

}