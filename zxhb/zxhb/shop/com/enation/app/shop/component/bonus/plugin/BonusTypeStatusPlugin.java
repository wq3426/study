package com.enation.app.shop.component.bonus.plugin;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.job.IErverDayTwlveExecuteEvent;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 
 *自动任务每天晚上12点修改失效的优惠券信息 
 * @author yunbs
 *
 */
@Component
@Scope("prototype")
public class BonusTypeStatusPlugin extends AutoRegisterPlugin  implements IErverDayTwlveExecuteEvent{
	private IDaoSupport daoSupport;
	
	
	@Override
	public void everyDayTwlve() {
		long nowTime = System.currentTimeMillis();
		this.daoSupport.execute("update es_bonus_type set status=3 where use_end_date < ? and status = 1", nowTime);
	}


	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	
}
