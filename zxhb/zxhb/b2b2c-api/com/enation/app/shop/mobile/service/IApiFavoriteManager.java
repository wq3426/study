package com.enation.app.shop.mobile.service;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Favorite;
import com.enation.framework.database.Page;

/**
 * 收藏Manager接口
 * @author Sylow
 * @version v1.0 , 2015-08-24
 * @since v1.0
 */
@Component
public interface IApiFavoriteManager  {

	 /**
     * 根据ID获取收藏信息
     * @param favorite_id
     * @return
     */
	public Favorite get(int favorite_id);

	/**
     * 根据商品ID和会员ID获取收藏信息
     * @param goodsid
     * @param memberid
     * @return
     */
	public Favorite get(int goodsid, int memberid);

	 /**
     * 根据商品ID和会员ID删除收藏信息
     * @param goodsid
     * @param memberid
     */
	public void delete(int goodsid, int memberid);

	 /**
     * 是否已经收藏某个商品
     * @param goodsid
     * @param memeberid
     * @return
     */
	public boolean isFavorited(int goodsid, int memeberid);

	/**
     * 添加收藏
     * @param goodsid
     */
	public void add(Integer goodsid, int memberid);
	
	/**
	 * 删除收藏
	 * @param favorite_id
	 */
	public void delete(int favorite_id);
	
	/**
     * 添加收藏
     * @param goodsid
     */
	public void add(Integer goodsid);

	/**
	 * 获取分页信息
	 * @param memberid
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page list(int memberid, int pageNo, int pageSize);

	
	
	
}
