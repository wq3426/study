package com.enation.app.b2b2c.core.action.backend.saleType;

import java.text.DecimalFormat;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.reckoning.Reckoning;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningDetail;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeStatus;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeType;
import com.enation.app.b2b2c.core.model.settlement.AdminSettlement;
import com.enation.app.b2b2c.core.model.store.StoreLevel;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.b2b2c.core.service.saleType.ISaleAuditManager;
import com.enation.app.b2b2c.core.service.saleType.ISaleFreeManager;
import com.enation.app.b2b2c.core.service.settlement.impl.IAdminSettlementManager;
import com.enation.app.b2b2c.core.service.store.IStoreLevelManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.SaleTypeSetting;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.SaleFree;
import com.enation.app.base.core.model.Store;
import com.enation.app.base.core.service.IStoreCostManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.service.AdminSettlementType;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.OrderType;
import com.enation.app.shop.core.utils.SerailNumberUtils;
import com.enation.framework.action.WWAction;
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("saleaudit")
public class SaleAuditAction extends WWAction{
	
	private IStoreMemberManager storeMemberManager;
	private ISaleAuditManager saleAuditManager;
	private ISaleFreeManager saleFreeManager;
	private IStoreManager storeManager;
	private IOrderManager orderManager;
	private IStoreCostManager storeCostManager;
	private IStoreLevelManager storeLevelManager;
	private IReckoningManager reckoningManager;
	private String typeId;
	private Integer auditId;
	private Integer saleFreeId;
	private Integer auditStatus = 0;
	private IAdminSettlementManager adminSettlementManager;
	/**
	 * @description 免费申请优惠券
	 * @date 2016年8月30日 下午3:25:36
	 * @return
	 * @return String
	 */
	public String apply_freeBonus(){
		try {
			Member member=storeMemberManager.getStoreMember();
			SaleFree saleFree = saleFreeManager.getSaleFree(auditId);
			if(saleFree != null){
				saleAuditManager.addSaleAudit(member.getStore_id(),typeId,saleFree,auditStatus);
				this.showSuccessJson("申请成功!");
			}else{
				this.showSuccessJson("数据异常,申请失败!");
			}
		} catch (RuntimeException e) {
			this.showErrorJson(e.getMessage());
		} catch (Exception e) {
			this.showErrorJson("申请失败!");
		}
		return this.JSON_MESSAGE;
	}

	/**
	 * @description 免费申请专栏
	 * @date 2016年8月31日 下午4:08:15
	 * @return
	 * @return IStoreMemberManager
	 */
	public String apply_freeAdv(){
		try {
			Member member=storeMemberManager.getStoreMember();
			SaleFree saleFree = saleFreeManager.getSaleFree(auditId);
			if(!saleFree.toString().isEmpty()){
				saleAuditManager.addSaleAudit(member.getStore_id(),typeId,saleFree,auditStatus);
				this.showSuccessJson("申请成功!");
			}else{
				this.showSuccessJson("数据异常,申请失败!");
			}
			this.showSuccessJson("申请成功!");
		} catch (RuntimeException e) {
			this.showErrorJson(e.getMessage());
		} catch (Exception e) {
			this.showErrorJson("申请失败!");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * @description 免费申请信息服务
	 * @date 2016年9月1日 下午3:46:54
	 * @return
	 * @return String
	 */
	public String apply_freeServer(){
		try {
			Member member=storeMemberManager.getStoreMember();
			SaleFree saleFree = saleFreeManager.getSaleFree(auditId);
			if(!saleFree.toString().isEmpty()){
				saleAuditManager.addSaleAudit(member.getStore_id(),typeId,saleFree,auditStatus);
				this.showSuccessJson("申请成功!");
			}else{
				this.showSuccessJson("数据异常,申请失败!");
			}
			this.showSuccessJson("申请成功!");
		} catch (RuntimeException e) {
			this.showErrorJson(e.getMessage());
		} catch (Exception e) {
			this.showErrorJson("申请失败!");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * @description 免费申请短信服务
	 * @date 2016年9月17日 上午10:24:57
	 * @return
	 * @return String
	 */
	public String apply_freeNote(){
		try {
			Member member=storeMemberManager.getStoreMember();
			SaleFree saleFree = saleFreeManager.getSaleFree(auditId);
			if(!saleFree.toString().isEmpty()){
				saleAuditManager.addSaleAudit(member.getStore_id(),typeId,saleFree,auditStatus);
				this.showSuccessJson("申请成功!");
			}else{
				this.showSuccessJson("数据异常,申请失败!");
			}
			this.showSuccessJson("申请成功!");
		} catch (RuntimeException e) {
			this.showErrorJson(e.getMessage());
		} catch (Exception e) {
			this.showErrorJson("申请失败!");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * @description 购买优惠券数量
	 * @date 2016年9月1日 下午5:47:23
	 * @return
	 * @return IStoreMemberManager
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public String add_buyBonus(){
		try {
			StoreMember member=storeMemberManager.getStoreMember();
			SaleFree saleFree = saleFreeManager.getSaleFree(saleFreeId);
			int  auditStatus= 3;
			if(!saleFree.toString().isEmpty()){
				double yetSettlementCount = storeManager.getBalance(member.getStore_id());
				if(saleFree.getPrice() <= yetSettlementCount){
					Store store = storeManager.getStore(member.getStore_id());
					//生成订单、结算单
					this.innerCreateOrder(store,saleFree,typeId);
					//更新店铺余额
					storeManager.updateBalance(-saleFree.getPrice(), member.getStore_id());
					//更新店铺消费
					storeCostManager.addStoreCostForBuySale(saleFree,typeId,member.getStore_id());
					//更新店铺审核列表
					saleAuditManager.addSaleAudit(member.getStore_id(),typeId,saleFree,auditStatus);
					this.showSuccessJson("购买成功!");
				}else{
					this.showErrorJson("账户余额不足，购买失败！");
				}
			}
		} catch (RuntimeException e) {
			this.showErrorJson(e.getMessage());
		} catch (Exception e) {
			this.showErrorJson("购买失败!");
		}
		return this.JSON_MESSAGE;
	}
	

	private void innerCreateOrder(Store store, SaleFree saleFree, String typeId) {
		// TODO Auto-generated method stub
		String sn = orderManager.createSn()+OrderType.ZA_SERVICE;
		//生成订单
		Order order  = new Order();
		order.setSn(sn);
		order.setMember_id(store.getMember_id());
		order.setStatus(7);
		order.setPay_status(2);
		order.setCreate_time(com.enation.framework.util.DateUtil.getDateline());
		order.setComplete_time(com.enation.framework.util.DateUtil.getDateline());
		order.setGoods_amount(saleFree.getPrice());
		order.setPaymoney(saleFree.getPrice());
		order.setNeed_pay_money(saleFree.getPrice());
		order.setOrder_type(OrderType.ZA_SERVICE);
		order.setStore_name(store.getStore_name());
		order.setService_store_id(store.getStore_id());
		order.setService_store_name(store.getStore_name());
		orderManager.add(order);
		//生成结算单
		ReckoningDetail reckoning_detail = new ReckoningDetail();
		reckoning_detail.setOrder_sn(sn);
		reckoning_detail.setOrder_type(OrderType.ZA_SERVICE);
		reckoning_detail.setPaymoney(saleFree.getPrice());
		reckoning_detail.setService_time(com.enation.framework.util.DateUtil.getDateline());
		reckoning_detail.setOrder_price(saleFree.getPrice());
		//获取服务机构店铺等级名称
		StoreLevel storeLevel= storeLevelManager.getStoreLevelByStoreId(store.getStore_id());
		if(storeLevel!=null){
			reckoning_detail.setLevel_name(storeLevel.getLevel_name());
		}
		reckoning_detail.setStore_id(store.getStore_id());
		reckoning_detail.setSettlement_time(com.enation.framework.util.DateUtil.getDateline());
		reckoning_detail.setHandling_charge(0.00);
		reckoning_detail.setService_charge(0.00);
		reckoning_detail.setSettlement_money(-saleFree.getPrice());
		Integer  reckoning_detail_id = reckoningManager.addDetail(reckoning_detail);
		//AdminSettlement adminSettlement = adminSettlementManager.getAdminSettlementByOrder(sn);
		
		Reckoning reckoning = new Reckoning();
		reckoning.setTrade_money(saleFree.getPrice());
		reckoning.setStore_id(store.getStore_id());
		reckoning.setReckoning_detail_id(reckoning_detail_id);
		// 流水号 获取此次交易流水
		String snaa;
		int sequence = adminSettlementManager.countTodayBigTypeNum(ReckoningTradeType.settle_accounts.getIndex());
		if(typeId != null && typeId.equals(SaleTypeSetting.NOTE_NUM_TYPE.toString())){
			snaa = SerailNumberUtils.getSerailNumber(AdminSettlementType.BIG_STORE_BUY_SERVICE,
					AdminSettlementType.STORE_PHONE_MESSAGE, sequence);
		}else if(typeId != null && typeId.equals(SaleTypeSetting.ADV_PUBLISH_NUM_TYPE.toString())){
			snaa = SerailNumberUtils.getSerailNumber(AdminSettlementType.BIG_STORE_BUY_SERVICE,
					AdminSettlementType.STORE_ADVERTISERMENT, sequence);
		}else if(typeId != null && typeId.equals(SaleTypeSetting.ADV_NUM_TYPE.toString())){
			snaa = SerailNumberUtils.getSerailNumber(AdminSettlementType.BIG_STORE_BUY_SERVICE,
					AdminSettlementType.STORE_ADV_ERTISERMENT, sequence);
		}else if(typeId != null && typeId.equals(SaleTypeSetting.APP_MESPUBLISH_NUM_TYPE.toString())){
			snaa = SerailNumberUtils.getSerailNumber(AdminSettlementType.BIG_STORE_BUY_SERVICE,
					AdminSettlementType.STORE_APPERTISERMENT, sequence);
		}else if(typeId != null && typeId.equals(SaleTypeSetting.APP_NUM_TYPE.toString())){
			snaa = SerailNumberUtils.getSerailNumber(AdminSettlementType.BIG_STORE_BUY_SERVICE,
					AdminSettlementType.STORE_APP_ERTISERMENT, sequence);
		}else{
			snaa = SerailNumberUtils.getSerailNumber(AdminSettlementType.BIG_STORE_BUY_SERVICE,
					AdminSettlementType.STORE_BONUS_MESSAGE, sequence);
		}
		reckoning.setSn(snaa);//店铺结算流水号和admin支付流水号对应
		reckoning.setTrade_status(ReckoningTradeStatus.yet_settle_accounts.getIndex());
		reckoning.setTrade_type(ReckoningTradeType.service_buy.getIndex());
		reckoning.setTrade_time(com.enation.framework.util.DateUtil.getDateline());
		double reckoBalance = storeManager.getBalance(store.getStore_id());
		DecimalFormat formatReck = new DecimalFormat("0.00");
		double tradeMoney = Double.valueOf(formatReck.format(Double.valueOf(reckoBalance)));
		reckoning.setBalance(tradeMoney-saleFree.getPrice());
		reckoningManager.add(reckoning);
		//中安账户明细 表
		AdminSettlement adminSet = new AdminSettlement();
		adminSet.setSn(snaa);
		adminSet.setMember_id(store.getMember_id());
		adminSet.setObject_name(store.getStore_name());
		adminSet.setTrade_big_type(AdminSettlementType.BIG_STORE_BUY_SERVICE);
		
		if(typeId != null && typeId.equals(SaleTypeSetting.NOTE_NUM_TYPE.toString())){
			adminSet.setTrade_small_type(AdminSettlementType.STORE_PHONE_MESSAGE);
		}else if(typeId != null && typeId.equals(SaleTypeSetting.ADV_PUBLISH_NUM_TYPE.toString())){
			adminSet.setTrade_small_type(AdminSettlementType.STORE_ADVERTISERMENT);
		}else if(typeId != null && typeId.equals(SaleTypeSetting.ADV_NUM_TYPE.toString())){
			adminSet.setTrade_small_type(AdminSettlementType.STORE_ADV_ERTISERMENT);
		}else if(typeId != null && typeId.equals(SaleTypeSetting.APP_MESPUBLISH_NUM_TYPE.toString())){
			adminSet.setTrade_small_type(AdminSettlementType.STORE_APPERTISERMENT);
		}else if(typeId != null && typeId.equals(SaleTypeSetting.APP_NUM_TYPE.toString())){
			adminSet.setTrade_small_type(AdminSettlementType.STORE_APP_ERTISERMENT);
		}else{
			adminSet.setTrade_small_type(AdminSettlementType.STORE_BONUS_MESSAGE);
		}
		adminSet.setAdmin_pay_type(0);
		adminSet.setBank_handling_charge(0d);
		adminSet.setReal_settlement_money(0d);
		double balance = storeManager.getBalance(0);
		DecimalFormat format = new DecimalFormat("0.00");
		double trade_money = Double.valueOf(format.format(Double.valueOf(balance)));
		adminSet.setBalance_record(trade_money);
		adminSet.setTrade_time(com.enation.framework.util.DateUtil.getDateline());
		adminSet.setOrder_sn(sn);
		adminSet.setTrade_money(0d);
		adminSettlementManager.add(adminSet);
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public ISaleAuditManager getSaleAuditManager() {
		return saleAuditManager;
	}

	public void setSaleAuditManager(ISaleAuditManager saleAuditManager) {
		this.saleAuditManager = saleAuditManager;
	}

	public ISaleFreeManager getSaleFreeManager() {
		return saleFreeManager;
	}


	public void setSaleFreeManager(ISaleFreeManager saleFreeManager) {
		this.saleFreeManager = saleFreeManager;
	}
	
	public IStoreLevelManager getStoreLevelManager() {
		return storeLevelManager;
	}

	public void setStoreLevelManager(IStoreLevelManager storeLevelManager) {
		this.storeLevelManager = storeLevelManager;
	}

	public IReckoningManager getReckoningManager() {
		return reckoningManager;
	}

	public void setReckoningManager(IReckoningManager reckoningManager) {
		this.reckoningManager = reckoningManager;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IStoreCostManager getStoreCostManager() {
		return storeCostManager;
	}

	public void setStoreCostManager(IStoreCostManager storeCostManager) {
		this.storeCostManager = storeCostManager;
	}

	public Integer getAuditId() {
		return auditId;
	}

	public void setAuditId(Integer auditId) {
		this.auditId = auditId;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}

	public Integer getSaleFreeId() {
		return saleFreeId;
	}

	public void setSaleFreeId(Integer saleFreeId) {
		this.saleFreeId = saleFreeId;
	}

	public IAdminSettlementManager getAdminSettlementManager() {
		return adminSettlementManager;
	}

	public void setAdminSettlementManager(IAdminSettlementManager adminSettlementManager) {
		this.adminSettlementManager = adminSettlementManager;
	}
}
