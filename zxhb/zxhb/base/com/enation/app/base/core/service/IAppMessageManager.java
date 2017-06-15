package com.enation.app.base.core.service;
 

import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.AppMessage; 
import com.enation.framework.database.Page;


public interface IAppMessageManager {

	/**
	 * 获取一个消息详情
	 * @return
	 */
	public AppMessage getMessage(int messageid);
	
	public Map get(Integer amid);
	/**
	 * 添加
	 * @return
	 */
	public void add(AppMessage ms);
	
	/**
	 * 修改
	 * @return
	 * @throws Exception 
	 */
	public void edit(AppMessage ms) throws Exception;
	

	/**
	 * 获取分页的消息集合
	 * @return
	 */
	public Page listPage(int page,int pagesize,int store_id);

	/**
	 * 删除一个消息
	 * @return
	 */
	public void delete(int messageid);
	
	/**
	 * 根据会员分页获取消息
	 * @param page 当前页
	 * @param pageSize 一页的条数
	 * @param memberId 会员id
	 * @return
	 */
	public Page listPageByMemberId(int page, int pageSize, int memberId);
	/**
	 * 根据storeId获取消息
	 * @return
	 */
	public List<AppMessage> getMessageListByStoreId(int storeId);
	/**
	 * 根据page,pageSize,storeId获取消息
	 * @return
	 */
	public Page getAllMessageList(Integer page, int pageSize, int storeId);

	
}
