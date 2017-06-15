package com.enation.app.shop.core.model;

import java.util.Map;

import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;
import com.enation.framework.util.StringUtil;

/**
 * 订单货物实体类
 * @author kingapex
 *2010-4-6下午04:11:42
 */
public class OrderItem  implements java.io.Serializable {

      
	
	private static final long serialVersionUID = 8531790664258169824L;
	
	
	 private Integer item_id; //订单货物id
     private Integer order_id; //多对一订单实体类，即一个订单大类很多item子类
     private Integer goods_id; //对应货物
     private Integer product_id; //对应实际商品

     private Integer num;  //销售量
     private Integer ship_num; //
     private String name;
     private String sn;
     private String image;  //对应图片
     private Integer store; //对应货品的库存
     private String addon;
     private Integer cat_id; // 此商品的分类
     private Double price;  //该item的金额
     private int gainedpoint;  //获得积分
     private int state;//订单货物状态
     private String change_goods_name;//换到的货物名称
     private Integer change_goods_id;//换到的货物ID
     
     private String unit;
     
     private String depotId; //库房Id 2014-6-8 李奋龙
     
     private Double rewards_limit; //该订单商品最高可用奖励限制
     
     private Double used_rewards; //用户使用收益
     
     private String other;
     
     private Integer insure_repair_specid;//保险/保养订单特殊属性
    
	public Integer getInsure_repair_specid() {
		return insure_repair_specid;
	}
	public void setInsure_repair_specid(Integer insure_repair_specid) {
		this.insure_repair_specid = insure_repair_specid;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getChange_goods_name() {
		return change_goods_name;
	}
	public void setChange_goods_name(String change_goods_name) {
		this.change_goods_name = change_goods_name;
	}
	public Integer getChange_goods_id() {
		return change_goods_id;
	}
	public void setChange_goods_id(Integer change_goods_id) {
		this.change_goods_id = change_goods_id;
	}
	public Integer getGoods_id() {
		return goods_id;
	}
	public void setGoods_id(Integer goods_id) {
		this.goods_id = goods_id;
	}
	@PrimaryKeyField
	public Integer getItem_id() {
		return item_id;
	}
	public void setItem_id(Integer item_id) {
		this.item_id = item_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getNum() {
		return num;
	}
	public void setNum(Integer num) {
		this.num = num;
	}
	public Integer getOrder_id() {
		return order_id;
	}
	public void setOrder_id(Integer order_id) {
		this.order_id = order_id;
	}
	public Integer getShip_num() {
		return ship_num;
	}
	public void setShip_num(Integer ship_num) {
		this.ship_num = ship_num;
	}

 
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public int getGainedpoint() {
		return gainedpoint;
	}
	public void setGainedpoint(int gainedpoint) {
		this.gainedpoint = gainedpoint;
	}
	public String getSn() {
		return sn;
	}
	public void setSn(String sn) {
		this.sn = sn;
	}
	
	@NotDbField
	public Integer getStore() {
		return store;
	}
	public void setStore(Integer store) {
		this.store = store;
	}
	public String getAddon() {
		return addon;
	}
	
 
	public void setAddon(String addon) {
		this.addon = addon;
	}

	public int getCat_id() {
		return cat_id;
	}
	public void setCat_id(int cat_id) {
		this.cat_id = cat_id;
	}
 
	@NotDbField
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public Integer getProduct_id() {
		return product_id;
	}
	public void setProduct_id(Integer product_id) {
		this.product_id = product_id;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public String getDepotId() {
		return depotId;
	}
	public void setDepotId(String depotId) {
		this.depotId = depotId;
	}
	public Double getRewards_limit() {
		return rewards_limit;
	}
	public void setRewards_limit(Double rewards_limit) {
		this.rewards_limit = rewards_limit;
	}
	public Double getUsed_rewards() {
		return used_rewards;
	}
	public void setUsed_rewards(Double used_rewards) {
		this.used_rewards = used_rewards;
	}
	
}