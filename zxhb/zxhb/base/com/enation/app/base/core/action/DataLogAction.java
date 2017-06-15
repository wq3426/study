package com.enation.app.base.core.action;

import com.enation.eop.resource.IDataLogManager;
import com.enation.framework.action.WWAction;

/**
 * 数据日志管理Action
 * @author Kanon 2015-9-24 version 1.1 添加注释
 *
 */
public class DataLogAction extends WWAction {

	private IDataLogManager dataLogManager;
	private String start;
	private String end;
	private Integer[] ids;
	
	/**
	 * 数据日志列表
	 * @param start 开始时间
	 * @param end 结束时间
	 * @return 数据日志列表页面
	 */
	public String list() {
		this.webpage = this.dataLogManager.list(start, end, getPage(), getPageSize());
		return "list";
	}
	
	/**
	 * 删除数据日志
	 * @param ids 日志Id
	 * @return 删除状态
	 */
	public String delete() {
		try {
			this.dataLogManager.delete(ids);
			this.showSuccessJson("删除成功");
			this.json = "{result:0,message:'删除成功'}";
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败");
			this.logger.error("数据日志删除失败",e);
		}
		return WWAction.JSON_MESSAGE;
	}

	public IDataLogManager getDataLogManager() {
		return dataLogManager;
	}

	public void setDataLogManager(IDataLogManager dataLogManager) {
		this.dataLogManager = dataLogManager;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public Integer[] getIds() {
		return ids;
	}

	public void setIds(Integer[] ids) {
		this.ids = ids;
	}

}
