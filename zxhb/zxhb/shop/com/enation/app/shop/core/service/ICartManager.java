package com.enation.app.shop.core.service;

import java.util.List;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;

import net.sf.json.JSONArray;

/**
 * 购物车业务接口
 * @author kingapex
 * @see com.enation.test.shop.cart.CartTest#testAdd
 *2010-3-23下午03:26:12
 */
public interface ICartManager {
	
	
	
	
	/**添加商品到购物车,这里区分是立即下单还是从购物车下单，如果立即下单的话：保留购物车原来商品的项，只对指定商品下单。
	 * @param product
	 * @param num
	 * @param is
	 * @return
	 */
	public Integer addProductToCart(Product product,int num,boolean isImmediately);	
	
	/**
	 * 添加一个购物项
	 * @param cart
	 * @return cart_id
	 */
	public int add(Cart cart);
	
	/**
	 * 根据购物车ID来获取购物车信息
	 * @param cart_id
	 * @return 购物车
	 */
	public Cart get(int cart_id);
	
	
	/**
	 * 计算购物车中货物总数
	 * @param sessionid
	 * @return 货物总数
	 */
	public Integer countItemNum(String sessionid);
	
	/**
	 * 计算购物车中货物总数
	 * @param carplate,member_id
	 * @return 货物总数
	 */
	public Integer countItemNum(String carplate,Integer member_id,String cartIds);
	
	/**
	 * 检测某个货品是否有购物车使用
	 * @param goodsid
	 * @return
	 */
	public boolean checkGoodsInCart(Integer goodsid);
	
	/**
	 * 根据productId和sessionId来判断购物车中是否已经存在了一个物品
	 * @param productId
	 * @param sessionid
	 * @return
	 */
	public Cart getCartByProductId(int productId, String sessionid);
	
	/**
	 * 根据productId和sessionId以及addon来判断购物车中是否已经存在了一个物品
	 * @param productId
	 * @param sessionid
	 * @param addon
	 * @return
	 */
	public Cart getCartByProductId(int productId, String sessionid, String addon);
	
	/**
	 * 根据productId和member_id以及carplate来判断购物车中是否已经存在了一个物品
	 * @param productId
	 * @param sessionid
	 * @param addon
	 * @return
	 */
	public Cart getCartByProductId(int productId, Integer member_id, String carplate);
	
	/**
	 * 读取某用户的购物车中项列表(0.1版本,现使用listGoodsByCartids)
	 * @param sessionid
	 * @return
	 */
	public List<CartItem> listGoods(String sessionid);
	
	/**
	 * 读取某用户的购物车中项列表
	 * @param sessionid
	 * @return
	 */
	public List<CartItem> listGoodsByCartids(String cartIds);
	
	
	
	/**
	 * 清空某用户的购物车
	 * @param sessionid
	 */
	public void  clean(String sessionid);
	
 
	/**清空某用户的购物车
	 * @param member_id
	 */
	public void  clean(Integer member_id);
	
	
	/**清空某用户对应车牌的购物车
	 * @param member_id
	 */
	public void  clean(Integer member_id,String carplate);
	
	
	/**
	 * 清除对应的购物车选项
	 */
	public void cleanCart(String cardIds,Member member);
	
	/**
	 * 更新购物数量
	 * @param sessionid
	 * @param cartid
	 */
	public void updateNum(String sessionid,Integer cartid,Integer num);
	
	
	/**
	 * 更新购物数量
	 * @param member_id
	 * @param cartid
	 */
	public void updateNum(Integer member_id,Integer cartid,Integer num);
	
	/**
	 * 删除购物车中的一项
	 * @param cartid
	 */
	public void delete(String sessionid,Integer cartid);
	
 
	/**
	 * 计算购买商品重量，包括商品、捆绑商品、赠品
	 * @param sessionid
	 * @return
	 */
	public Double countGoodsWeight(String sessionid);
	
 
	/**
	 * 计算购物车中货物的总积分
	 * @param sessionid
	 * @return
	 */
	public  Integer countPoint(String sessionid);
	
	/**
	 * 计算订单价格<br>
	 * 根据购物车列表、配送方式，及收货地区id来计算订单价格。
	 * 负责订单创建流程中价格的处理<br>
	 * 包括：购物车中的总价计算，结算页的总价计算，订单创建时的总价计算。<br>
	 * @param cartItemList 购物车列表，在订单价格计算时，商品总价会根据 CartItem 中的coupPrice（优惠后的价格）计算
	 * @see CartItem
	 * @param shippingid 配送方式id，用于计算配送费用，不同的配置方式运费价格可能不一样
	 * @param regionid  地区id，配送地区不同，运费可能不同
	 * @return 订单价格
	 * @see OrderPrice
	 */
	public OrderPrice countPrice(List<CartItem> cartItemList,Integer shippingid,String regionid);
	
	
	
	
	/**
	 * 计算购物商品货物总价(原始的，未处理优惠数据的)
	 * @param carplate,integer
	 * @param integer 
	 * @return
	 */
	public Double countGoodsTotal(String carplate, Integer member_id);
	
	
	/**
	 * 计算选中购物项的价格,带条件查询,可以只查cartIds
	 * @param 
	 * @param integer 
	 * @return
	 */
	public Double countGoodsTotal(String carplate, Integer member_id ,String cartIds);
	
	/**
	 * 计算购物商品货物总价(原始的，未处理优惠数据的)
	 * @param sessionid
	 * @param integer 
	 * @return
	 */
	public Double countGoodsTotal(String sessionid);
	
	/**
	 * 修改购物车中价格，根据货品id
	 * @param product_id
	 */
	public void updatePriceByProductid(Integer product_id,Double price);

	
	/**
	 * 按店铺显示，购物车商品数据用于展示
	 * @param carplate
	 * @param member_id
	 */
	public List showCarts(String carplate, Integer member_id);

}
