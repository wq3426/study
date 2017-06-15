package com.enation.app.b2b2c.component.plugin.reckoning;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.reckoning.Reckoning;
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.plugin.job.IErverDayZeroThirtyExecuteEvent;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

@Component
public class TimeOutSettlementPlugin extends AutoRegisterPlugin implements IErverDayZeroThirtyExecuteEvent {

	private IReckoningManager reckoningManager;
	
	private IStoreManager storeManager;
	
	@Override
	public void everyDayZeroThirty() {
		ComfirmOrderSettlement();
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void ComfirmOrderSettlement() {// T+7日后结算(保险和保养为立即结算)

		try {
			String today = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
			Long todayTime =DateUtil.getDateline(today, "yyyy-MM-dd HH:mm:ss");
			//只获取普通商品和中安商品结算信息
			List<Reckoning> reckonings = reckoningManager.getReckoningBySettlementTime(todayTime);
			
			if(reckonings!=null && reckonings.size()>0){
				for(int i = 0 ; i<reckonings.size() ; i++){
					double balance = storeManager.getBalance(reckonings.get(i).getStore_id());
					//结算订单状态改变
					reckoningManager.timeOutOrderSettlement(reckonings.get(i),balance);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IReckoningManager getReckoningManager() {
		return reckoningManager;
	}

	public void setReckoningManager(IReckoningManager reckoningManager) {
		this.reckoningManager = reckoningManager;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}

}
