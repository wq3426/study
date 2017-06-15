package com.enation.app.b2b2c.component.plugin.reckoning;

import java.util.List;

import org.jsoup.select.Evaluator.Id;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.model.reckoning.Reckoning;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningDetail;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeStatus;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeType;
import com.enation.app.b2b2c.core.model.settlement.AdminSettlement;
import com.enation.app.b2b2c.core.model.store.StoreLevel;
import com.enation.app.b2b2c.core.model.store.StoreRate;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.b2b2c.core.service.settlement.impl.IAdminSettlementManager;
import com.enation.app.b2b2c.core.service.store.IStoreLevelManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.b2b2c.core.service.store.IStoreRateManager;
import com.enation.app.base.core.model.Store;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.plugin.order.IOrderConfirmServiceEvent;
import com.enation.app.shop.core.service.ICarInfoManager;
import com.enation.app.shop.core.service.OrderType;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.CurrencyUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Component
public class ComfirmServicePlugin extends AutoRegisterPlugin implements IOrderConfirmServiceEvent {

	private IStoreRateManager storeRateManager;

	private IBonusManager bonusManager;

	private IStoreManager storeManager;

	private ICarInfoManager carInfoManager;

	private IReckoningManager reckoningManager;
	
	private IAdminSettlementManager adminSettlementManager;
	
	private IStoreOrderManager storeOrderManager;
	
	private IStoreLevelManager storeLevelManager;
	// 确认服务插件
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void comfirmService(Order order) {
		try{
			StoreOrder storeOrder = storeOrderManager.get(order.getOrder_id());
			Reckoning checkReckoningExists = reckoningManager.getReckoningByOrder(storeOrder.getSn());
			if(checkReckoningExists!=null){//已生成结算单了
				return ;	
			}
			//生成结算单
			ReckoningDetail reckoning_detail = new ReckoningDetail();
			reckoning_detail.setOrder_sn(storeOrder.getSn());
			reckoning_detail.setOrder_type(storeOrder.getOrder_type());
			reckoning_detail.setPaymoney(storeOrder.getPaymoney());
			reckoning_detail.setService_time(storeOrder.getComplete_time());
			reckoning_detail.setUse_gain(storeOrder.getGain());
			reckoning_detail.setOrder_price(storeOrder.getNeed_pay_money());
			reckoning_detail.setUse_repair_coin(storeOrder.getRepair_coin());
			//获取服务机构店铺等级名称
			StoreLevel storeLevel= storeLevelManager.getStoreLevelByStoreId(storeOrder.getService_store_id());
			if(storeLevel!=null){
				reckoning_detail.setLevel_name(storeLevel.getLevel_name());
			}
			double price = bonusManager.getBonusPrice(storeOrder.getOrder_id(), storeOrder.getSn());
			reckoning_detail.setUse_coupon(price);
			reckoning_detail.setStore_id(storeOrder.getService_store_id());
			Store store = storeManager.getStore(storeOrder.getService_store_id());
			//获取当前订单类型，根据服务店铺等级和类型获取费率
			//这里的周期从店铺设置里拿
			int period = store.getSettlement_period()==null?7:store.getSettlement_period();
			long settlment_time = (storeOrder.getComplete_time()) + (period*24*3600);
			if(order.getOrder_type() == OrderType.ZA_INSURANCE || order.getOrder_type() == OrderType.STORE_INSURANCE  || order.getOrder_type()==OrderType.REPAIR){
				//保险保养立即结算,所以暂时设为空，结算时算为当前时间
				reckoning_detail.setSettlement_time(null);
			}else{
				reckoning_detail.setSettlement_time(settlment_time);
			}
			
			StoreRate storeRate = storeRateManager.getStoreRate(storeOrder.getOrder_type(),store.getStore_level());
			List list = carInfoManager.getCarInfoByCarplate(storeOrder.getCarplate());
			JSONObject jsonObject = JSONArray.fromObject(list).getJSONObject(0);
			/*if (jsonObject.getInt("original_storeid")== storeOrder.getService_store_id()) {// 不区分原有客户和倒流
					reckoning_detail.setHandling_charge(
							CurrencyUtil.mul(-storeOrder.getPaymoney(), storeRate.getOriginal_handling_rate(),true,false));
					reckoning_detail
							.setService_charge(CurrencyUtil.mul(storeOrder.getPaymoney(), storeRate.getOriginal_service_rate(),true,false));
			} else {//如果原有客户和服务客户不一样时
					reckoning_detail
							.setHandling_charge(-CurrencyUtil.mul(storeOrder.getPaymoney(), storeRate.getFlow_handling_rate(),true,false));
					reckoning_detail
							.setService_charge(CurrencyUtil.mul(storeOrder.getPaymoney(), storeRate.getFlow_service_rate(),true,false));
			}*/
			
			double settlement_money = 0d;
			if (storeOrder.getStore_id() == 1 && storeOrder.getService_store_id() != storeOrder.getStore_id()) {
				//如果store_id 为中安，服务id为4S店
				reckoning_detail.setHandling_charge(
						CurrencyUtil.mul(-storeOrder.getPaymoney(), storeRate.getOriginal_handling_rate(),true,false));
				reckoning_detail.setService_charge(
				CurrencyUtil.mul(storeOrder.getPaymoney(), storeRate.getOriginal_service_rate(),true,false));
				settlement_money = reckoning_detail.getService_charge();//只给服务费
			}if(storeOrder.getStore_id() == 1 && storeOrder.getService_store_id() == 1){//如果store_id 为中安，服务id为中安店
			}else if(storeOrder.getStore_id() != 1){//如果store_id为4s店，则收手续费
				reckoning_detail.setHandling_charge(
						CurrencyUtil.mul(-storeOrder.getPaymoney(), storeRate.getOriginal_handling_rate(),true,false));
				reckoning_detail.setService_charge(
						CurrencyUtil.mul(storeOrder.getPaymoney(), storeRate.getOriginal_service_rate(),true,false));
				settlement_money = CurrencyUtil.add(//
						CurrencyUtil.add(storeOrder.getPaymoney(), reckoning_detail.getService_charge())//
						, reckoning_detail.getHandling_charge());
			}
			reckoning_detail.setSettlement_money(settlement_money);
			Integer  reckoning_detail_id = reckoningManager.addDetail(reckoning_detail);
			AdminSettlement adminSettlement = adminSettlementManager.getAdminSettlementByOrder(storeOrder.getSn());
			
			Reckoning reckoning = new Reckoning();
			reckoning.setTrade_money(reckoning_detail.getSettlement_money());
			reckoning.setStore_id(store.getStore_id());
			reckoning.setReckoning_detail_id(reckoning_detail_id);
			reckoning.setSn(adminSettlement.getSn());//店铺结算流水号和admin支付流水号对应
			reckoning.setTrade_status(ReckoningTradeStatus.no_settle_accounts.getIndex());
			reckoning.setTrade_type(ReckoningTradeType.settle_accounts.getIndex());
			reckoning.setTrade_time(settlment_time);
			reckoning.setBalance(0.0);
			reckoningManager.add(reckoning);
			//判断如果是保险保养，立即结算
			if(order.getOrder_type() == OrderType.STORE_INSURANCE ||order.getOrder_type() == OrderType.ZA_INSURANCE || order.getOrder_type() == OrderType.REPAIR){
				double balance = storeManager.getBalance(reckoning.getStore_id());
				//结算订单状态改变
				reckoningManager.orderSettlement(reckoning,balance);
			}
		}catch(Exception e){
			throw e;
		}
		
	}
	public IStoreRateManager getStoreRateManager() {
		return storeRateManager;
	}
	public void setStoreRateManager(IStoreRateManager storeRateManager) {
		this.storeRateManager = storeRateManager;
	}
	public IBonusManager getBonusManager() {
		return bonusManager;
	}
	public void setBonusManager(IBonusManager bonusManager) {
		this.bonusManager = bonusManager;
	}
	public IStoreManager getStoreManager() {
		return storeManager;
	}
	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
	public ICarInfoManager getCarInfoManager() {
		return carInfoManager;
	}
	public void setCarInfoManager(ICarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}
	public IReckoningManager getReckoningManager() {
		return reckoningManager;
	}
	public void setReckoningManager(IReckoningManager reckoningManager) {
		this.reckoningManager = reckoningManager;
	}
	public IAdminSettlementManager getAdminSettlementManager() {
		return adminSettlementManager;
	}
	public void setAdminSettlementManager(IAdminSettlementManager adminSettlementManager) {
		this.adminSettlementManager = adminSettlementManager;
	}
	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}
	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}
	public IStoreLevelManager getStoreLevelManager() {
		return storeLevelManager;
	}
	public void setStoreLevelManager(IStoreLevelManager storeLevelManager) {
		this.storeLevelManager = storeLevelManager;
	}
	
	
	

}
