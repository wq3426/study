package com.enation.app.b2b2c.core.service.cart;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.cart.StoreCartItem;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.StoreCartKeyEnum;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
/**
 * 店铺购物车管理类
 * @author LiFenLong
 *
 */
public interface IStoreCartManager {
	public static final String FILTER_KEY = "cartFilter"; 
	
	
	/**（0.1版本旧逻辑,现在使用putCartListToSession方法）
	 * 根据session信息计算费用<br>
	 * 计算结果要通过{@link StoreCartContainer#getStoreCartListFromSession()} 获取到
	 * @param isCountShip 是否计算费用
	 */
	@Deprecated
	public void countPrice(String isCountShip );
	
	
	/**
	 * 根据购物车里的ids
	 * @param cartIds
	 */
	public void putCartListToSession(String cartIds);
	
	
	/**（0.1版本旧逻辑,现在使用listGoodsByCartids方法）
	 * 获取购物车商品列表
	 * @param sessionid
	 * @return List<StoreCartItem>
	 */
	@Deprecated
	public List<StoreCartItem> listGoods(String sessionid);
	
	/**
	 * 获取购物车商品列表
	 * @param sessionid
	 * @return List<StoreCartItem>
	 */
	public List<StoreCartItem> listGoodsByCartids(String cartIds);
	
	/**
	 * （0.1版本旧逻辑使用，现在使用storeGoodsList方法）
	 * 获取分店铺购物车列表<br>
	 * @param sessionid
	 * @return 返回的list中map结构如下：<br>
	 * <li>key为store_id的值为店铺id</li>
	 * <li>key为store_name的值为店铺名称</li>
	 * <li>key为goodslist为此店铺的购物车列表</li>
	 * <li>key为storeprice为此店铺的价格对像 {@link OrderPrice}</li>
	 * @see StoreCartKeyEnum
	 */
	@Deprecated
	public List<Map> storeListGoods(String sessionid);
	
	
	
	/**
	 * 获取店铺购物车列表 
	 * @param cartIds
	 * @return
	 */
	public List<Map> storeGoodsList(String cartIds);
	
	
	/**
	 * 清除购物车
	 * @param sessionid
	 */
	public void  clean(String sessionid);
	
	/**
	 * 修改购物车中价格，根据货品id
	 * @param product_id
	 */
	public void updatePriceByProductid(Integer product_id,Double price);








	
	
}
