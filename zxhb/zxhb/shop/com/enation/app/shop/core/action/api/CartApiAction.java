package com.enation.app.shop.core.action.api;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 购物车api
 * @author kingapex
 *2013-7-19下午12:58:43
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("cart")
public class CartApiAction extends WWAction {
	private ICartManager cartManager;
	private IPromotionManager promotionManager ;
	private int goodsid;
	private int productid;
	private int num;//要向购物车中活加的货品数量
	private IProductManager productManager ;
	
	//在向购物车添加货品时，是否在返回的json串中同时显示购物车数据。
	//0为否,1为是
	private int showCartData;
	
	/**
	 * 将一个货品添加至购物车。
	 * 需要传递productid和num参数
	 * 
	 * @param productid 货品id，int型
	 * @num 数量，int 型
	 * 
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 */
	public String addProduct() {
		try {
			Product product = productManager.get(productid);
			Integer cartid  = this.cartManager.addProductToCart(product, num,false);
			if(cartid == -1){
				this.showErrorJson("抱歉！您所选选择的货品库存不足。");
				return this.JSON_MESSAGE;
			}
			this.showSuccessJson("添加购物车成功");
		} catch (Exception e) {
			this.logger.error("添加购物车出错", e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	
	
	
	/**
	 * 将一个商品添加到购物车
	 * 需要传递goodsid 和num参数
	 * @param goodsid 商品id，int型
	 * @param num 数量，int型
	 * 
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败
	 * message 为提示信息
	 */
	public String addGoods(){
		try {
			Product product = productManager.getByGoodsId(goodsid);

			Integer cartid  = this.cartManager.addProductToCart(product, num ,false);
			if(cartid == -1){
				this.showErrorJson("抱歉！您所选选择的货品库存不足。");
				return this.JSON_MESSAGE;
			}
			this.showSuccessJson("添加购物车成功");

			this.showSuccessJson("添加购物车成功");
		} catch (Exception e) {
			this.logger.error("创建订单出错", e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	
	/**
	 * 获取购物车数据
	 * @param 无
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败
	 * data.count：购物车的商品总数,int 型
	 * data.total:购物车总价，int型
	 * 
	 */
	public String getCartData(){
		
		try{
			
			String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
			
			Double goodsTotal  =cartManager.countGoodsTotal( sessionid );
			int count = this.cartManager.countItemNum(sessionid);
			
			java.util.Map<String, Object> data =new HashMap();
			data.put("count", count);//购物车中的商品数量
			data.put("total", goodsTotal);//总价
			
			this.json = JsonMessageUtil.getObjectJson(data);
			
		}catch(Throwable e ){
			this.logger.error("获取购物车数据出错",e);
			this.showErrorJson("获取购物车数据出错["+e.getMessage()+"]");
		}
		
		return this.JSON_MESSAGE;
	}
	
	
	
	/**
	 * 删除购物车一项
	 * @param cartid:要删除的购物车id,int型,即 CartItem.item_id
	 * 
	 * @return 返回json字串
	 * result  为1表示调用成功0表示失败
	 * message 为提示信息
	 * 
	 * {@link CartItem }
	 */
	public String delete(){
		try{
			HttpServletRequest request =ThreadContextHolder.getHttpRequest();
			String cartid= request.getParameter("cartid");
			cartManager.delete(request.getSession().getId(), Integer.valueOf(cartid));
			this.showSuccessJson("删除成功");
		}catch(RuntimeException e){
			this.logger.error("删除购物项失败",e);
			this.showErrorJson("删除购物项失败");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 更新购物车的数量
	 * @param cartid:要更新的购物车项id，int型，即 CartItem.item_id
	 * @param num:要更新数量,int型
	 * @return 返回json字串
	 * result： 为1表示调用成功0表示失败 int型
	 * store: 此商品的库存 int型
	 */
	public String updateNum(){
		try{
			HttpServletRequest request =ThreadContextHolder.getHttpRequest();
			String cartid= request.getParameter("cartid");
			String num= request.getParameter("num");
			num = StringUtil.isEmpty(num)?"1":num;//lzf add 20110113
			String productid= request.getParameter("productid");
			Product product=productManager.get(Integer.valueOf(productid));
			Integer store=product.getEnable_store();
			if(store==null)
				store=0;
			if(store >=Integer.valueOf(num)){
				cartManager.updateNum(request.getSession().getId(),  Integer.valueOf(cartid),  Integer.valueOf(num));
			}
			json=JsonMessageUtil.getNumberJson("store",store);
		}catch(RuntimeException e){
			this.logger.error("更新购物车数量出现意外错误", e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE; 
	}
	
	
	
	
	/**
	 * 购物车的价格总计信息
	 * @param 无
	 * @return 返回json字串
	 * result： 为1表示调用成功0表示失败 int型
	 * orderprice: 订单价格，OrderPrice型
	 * {@link OrderPrice}  
	 */
	public String getTotal(){
		HttpServletRequest request =ThreadContextHolder.getHttpRequest();
		String sessionid  = request.getSession().getId();
		OrderPrice orderprice  =this.cartManager.countPrice(cartManager.listGoods(sessionid), null, null);
		this.json = JsonMessageUtil.getObjectJson(orderprice);
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 清空购物车
	 */
	
	public String clean(){	
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		try{
			cartManager.clean(request.getSession().getId());
			this.showSuccessJson("清空购物车成功");
		}catch(RuntimeException e){
			this.logger.error("清空购物车",e);
			this.showErrorJson(e.getMessage());
		}
		return this.JSON_MESSAGE; 
	}
	
	public ICartManager getCartManager() {
		return cartManager;
	}
	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}


	public int getGoodsid() {
		return goodsid;
	}


	public void setGoodsid(int goodsid) {
		this.goodsid = goodsid;
	}


	public int getProductid() {
		return productid;
	}


	public void setProductid(int productid) {
		this.productid = productid;
	}


	public IProductManager getProductManager() {
		return productManager;
	}


	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}


	public int getNum() {
		return num;
	}


	public void setNum(int num) {
		this.num = num;
	}




	public int getShowCartData() {
		return showCartData;
	}




	public void setShowCartData(int showCartData) {
		this.showCartData = showCartData;
	}
	
	
	  
}
