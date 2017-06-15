package com.enation.app.b2b2c.component.plugin.settlement;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.settlement.AdminSettlement;
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.b2b2c.core.service.settlement.impl.IAdminSettlementManager;
import com.enation.app.b2b2c.core.service.store.impl.StoreManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.plugin.order.IOrderConfirmPayEvent;
import com.enation.app.shop.core.service.AdminSettlementType;
import com.enation.app.shop.core.service.impl.PaymentManager;
import com.enation.app.shop.core.utils.SerailNumberUtils;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;

@Component
public class ComfirmPayPlugin extends AutoRegisterPlugin implements IOrderConfirmPayEvent {
	private IAdminSettlementManager adminSettlementManager;

	private PaymentManager paymentManager;

	private StoreManager storeManager;
	
	private IDaoSupport daoSupport;
	
	private IReckoningManager reckoningManager;
	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void confirmPay(Order order,String tradeno) {
		try{
		// 确认支付后生成流水号和admin结算单
		PayCfg payCfg = paymentManager.get(order.getPayment_id());
		double pay_fee = 5.5; //支付宝手续费写死为千分之5.5
	/*	if (payCfg.getPay_fee() != null) {
			pay_fee = payCfg.getPay_fee();
		}else{
			pay_fee = 5.5;//支付宝手续费写死为千分之5.5
		}*/

		double admin_balance = storeManager.getBalance(0);// 获取admin的余额
		double bank_handling_charge = CurrencyUtil.mul(order.getPaymoney(), (pay_fee/1000d), true,true);// 获取手续费
		bank_handling_charge = -bank_handling_charge;//手续费为负数
		AdminSettlement adminSettlement = new AdminSettlement();
		adminSettlement.setTrade_big_type(AdminSettlementType.BIG_BUY_SERVICE);
		adminSettlement.setTrade_small_type(order.getOrder_type());
		adminSettlement.setBank_handling_charge(bank_handling_charge);
		adminSettlement.setReal_settlement_money(CurrencyUtil.add(order.getPaymoney(), bank_handling_charge));
		adminSettlement.setBalance_record(CurrencyUtil.add(admin_balance, adminSettlement.getReal_settlement_money()));
		adminSettlement.setTrade_money(order.getPaymoney());
		// 流水号 获取此次交易流水
		int sequence = adminSettlementManager.countTodayAdminTradeBigType(adminSettlement.getTrade_big_type());
		String sn = SerailNumberUtils.getSerailNumber(adminSettlement.getTrade_big_type(),
				adminSettlement.getTrade_small_type(), sequence);
		adminSettlement.setSn(sn);
		adminSettlement.setOrder_sn(order.getSn());
		adminSettlement.setMember_id(order.getMember_id());
		adminSettlement.setObject_name(order.getShip_name());
		adminSettlement.setTrade_time(DateUtil.getDateline());
		adminSettlement.setAdmin_pay_type(order.getPayment_id());
		adminSettlement.setPay_sn(tradeno);
		adminSettlementManager.add(adminSettlement);
		//更新admin余额
		daoSupport.execute("update es_store_balance set balance=balance+? where store_id = 0 ", adminSettlement.getReal_settlement_money());
		}catch(Exception e){
			throw e;
		}
	}

	public IAdminSettlementManager getAdminSettlementManager() {
		return adminSettlementManager;
	}

	public void setAdminSettlementManager(IAdminSettlementManager adminSettlementManager) {
		this.adminSettlementManager = adminSettlementManager;
	}

	public PaymentManager getPaymentManager() {
		return paymentManager;
	}

	public void setPaymentManager(PaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

	public StoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(StoreManager storeManager) {
		this.storeManager = storeManager;
	}

	public IReckoningManager getReckoningManager() {
		return reckoningManager;
	}

	public void setReckoningManager(IReckoningManager reckoningManager) {
		this.reckoningManager = reckoningManager;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	
	

}
