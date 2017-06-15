package com.enation.app.base.core.service;

/**
 * 任务执行器 
 * @author kingapex
 *
 */
public interface IJobExecuter {
	
	public void everyHour();
	public void everyDay();
	public void everyMonth();
	public void everyMinutes();
	public void everyDayZeroThirty();
	public void everyDayTwlve();
}
