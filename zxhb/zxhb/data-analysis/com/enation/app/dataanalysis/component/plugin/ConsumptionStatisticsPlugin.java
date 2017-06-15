package com.enation.app.dataanalysis.component.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.job.IErverDayZeroThirtyExecuteEvent;
import com.enation.app.dataanalysis.service.UpdateConsumptionDataService;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * @Description 油耗数据统计定时任务（每天执行一次）
 *
 * @createTime 2016年9月26日 下午2:24:47
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
@Component
public class ConsumptionStatisticsPlugin extends AutoRegisterPlugin implements IErverDayZeroThirtyExecuteEvent {
	
	@Autowired
	private  UpdateConsumptionDataService updateConsumptionDataService;

	@Override
	public void everyDayZeroThirty() {
		
		//统计所有车的油耗数据
		updateConsumptionDataService.handleAllCarConsumptionData();
		
	}

}
