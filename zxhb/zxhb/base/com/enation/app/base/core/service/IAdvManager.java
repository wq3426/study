package com.enation.app.base.core.service;

import java.util.List;

import com.enation.app.base.core.model.Adv;
import com.enation.framework.database.Page;

/**
 * 广告接口
 * 
 * @author 李志富 lzf<br/>
 *         2010-2-4 下午03:25:36<br/>
 *         version 1.0<br/>
 * <br/>
 */
public interface IAdvManager {

	/**
	 * 广告信息修改
	 * 
	 * @param adv
	 * @throws Exception 
	 */
	public void updateAdv(Adv adv) throws Exception;

	/**
	 * 获取广告详细
	 * 
	 * @param advid
	 * @return
	 */
	public Adv getAdvDetail(Long advid);

	/**
	 * 广告新增
	 * 
	 * @param adv
	 */
	public void addAdv(Adv adv);

	/**
	 * 广告删除
	 * 
	 * @param advid
	 */
	public void delAdvs(Integer[] ids);

	/**
	 * 单条广告删除
	 * 
	 * @param advid
	 */
	public void delAdv(Integer aid);
	
	/**
	 * 分页读取广告
	 * 
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page pageAdv(String order, int page, int pageSize);
	
	/**
	 * 获取对应acid的所有广告列表
	 * @param acid
	 * @return
	 */
	public List listAdv(Long acid);
	
	
	/**
	 * 搜索关键字
	 * @param cname
	 * @return
	 */
	public Page search(String advname,int pageNo,int pageSize,String order);
	
	/**
	 * 根据商家id 获取广告详细
	 * @param storeId 商家id
	 * @return
	 */
	public List<Adv> getAdvDetail(int aId);
	
	/**
	 * 根据商家id 删除广告
	 * @param storeId 商家id
	 */
	public void delByStore(int storeId);
	
	/**
	 * 获取商家广告列表
	 * @param storeId
	 * @return
	 */
	public List<Adv> getStoreAdvList(int storeId);
	/**
	 * 获取商家广告列表
	 * @param storeId
	 * @return
	 */
	public List<Adv> getStoreAdvListForMobile(int storeId);
	/**
	 * 禁用商家广告
	 * @param storeId
	 */
	public void disableStoreAdv(int storeId);
	
	
	/**
	 * 启用商家某广告
	 * @param advId
	 * @param storeId
	 */
	public void startAdv(int advId);

	/**
	 * 根据aid获取广告
	 * @param aid
	 * @return
	 */
	public Adv getAdv(Integer aid);


	
}
