package com.enation.app.shop.core.model;
/**
 * 退货单
 * @author lina
 *2013-11-10上午10:16:43
 */
public class SellBackList {
	
	private Integer id;				//ID
	private String tradeno;			//退货单号
	private Integer tradestatus;	//状态 0待审核。1.审核成功代发货.6拒绝申请
	private String ordersn;			//订单号
	private String regoperator;		//操作员
	private Long regtime;			//创建时间
	private Double alltotal_pay;	//退款金额
	private String goodslist;		//商品列表
	private String seller_remark;	//客服备注
	private String warehouse_remark;//库管备注
	private String finance_remark;	//财务备注
	private Integer member_id;		//退货人会员id
	private String sndto;			//退货人
	private String tel;				//电话
	private String adr;				//地址
	private String zip;				//邮编
	private Double total;			//总数
	private Integer depotid;		//仓库Id
	private String refund_way;		//退款方式
	private String return_account;	//退款账户
	private String remark;			//备注
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTradeno() {
		return tradeno;
	}
	public void setTradeno(String tradeno) {
		this.tradeno = tradeno;
	}
	public Integer getTradestatus() {
		return tradestatus;
	}
	public void setTradestatus(Integer tradestatus) {
		this.tradestatus = tradestatus;
	}
	public String getOrdersn() {
		return ordersn;
	}
	public void setOrdersn(String ordersn) {
		this.ordersn = ordersn;
	}
	public String getRegoperator() {
		return regoperator;
	}
	public void setRegoperator(String regoperator) {
		this.regoperator = regoperator;
	}
	public Long getRegtime() {
		return regtime;
	}
	public void setRegtime(Long regtime) {
		this.regtime = regtime;
	}
	public String getGoodslist() {
		return goodslist;
	}
	public void setGoodslist(String goodslist) {
		this.goodslist = goodslist;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getSeller_remark() {
		return seller_remark;
	}
	public void setSeller_remark(String seller_remark) {
		this.seller_remark = seller_remark;
	}
	public String getWarehouse_remark() {
		return warehouse_remark;
	}
	public void setWarehouse_remark(String warehouse_remark) {
		this.warehouse_remark = warehouse_remark;
	}
	public String getFinance_remark() {
		return finance_remark;
	}
	public void setFinance_remark(String finance_remark) {
		this.finance_remark = finance_remark;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public String getSndto() {
		return sndto;
	}
	public void setSndto(String sndto) {
		this.sndto = sndto;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getAdr() {
		return adr;
	}
	public void setAdr(String adr) {
		this.adr = adr;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	public Integer getDepotid() {
		return depotid;
	}
	public void setDepotid(Integer depotid) {
		this.depotid = depotid;
	}
	public String getRefund_way() {
		return refund_way;
	}
	public void setRefund_way(String refund_way) {
		this.refund_way = refund_way;
	}
	public String getReturn_account() {
		return return_account;
	}
	public void setReturn_account(String return_account) {
		this.return_account = return_account;
	}
	public Double getAlltotal_pay() {
		return alltotal_pay;
	}
	public void setAlltotal_pay(Double alltotal_pay) {
		this.alltotal_pay = alltotal_pay;
	}
}
