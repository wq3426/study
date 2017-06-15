/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：购物车api  
 * 修改人：  
 * 修改时间：
 * 修改内容：
 */
package com.enation.app.shop.mobile.action.cart;

import com.enation.app.b2b2c.core.service.IStoreDlyTypeManager;
import com.enation.app.b2b2c.core.service.IStoreMemberAddressManager;
import com.enation.app.b2b2c.core.service.IStoreTemplateManager;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.support.InsureRepairSpec;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.impl.OrderManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonUtil;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车Api 
 * 提供购物车增删改查、结算
 * @author Sylow
 * @version v1.0 2015-08-24
 * @since v1.0
 */
@SuppressWarnings("serial")
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("cart")
public class MobileCartApiAction extends WWAction {
	private int productid;
	private int num;//要向购物车中活加的货品数量
	//在向购物车添加货品时，是否在返回的json串中同时显示购物车数据。  0为否,1为是
	private int showCartData;
	private int isInsureOrRepair;//是否为保险/保养 0 普通商品  1 保险 2 保养
	private IProductManager productManager;
	private ICartManager cartManager;
	private IStoreCartManager storeCartManager;
	private IStoreMemberAddressManager storeMemberAddressManager;
	private IStoreTemplateManager storeTemplateManager;
	private IStoreDlyTypeManager storeDlyTypeManager;
	private IOrderManager orderManager;

	/**
	 * 将一个货品添加至购物车。
	 * 需要传递productid和num参数
	 * 
	 * @param productid 货品id，int型
	 * @num 数量，int 型
	 * 
	 * 保险product入参：{"product_id":308,"isInsureOrRepair":1,"num":1,"applicant":"hhh","applicant_id":"37292219910929035X","price":"￥7867.85"}
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 */
	public String addProduct(){
		try {
			String info = getBody(getRequest());
			JSONObject obj = JSONObject.fromObject(info);
			isInsureOrRepair = obj.getInt("isInsureOrRepair");
			productid = obj.getInt("product_id");
			num = obj.getInt("num");
			int store_id = obj.getInt("repair4sstoreid");
			String carplate = obj.getString("carplate");
			Product product = productManager.get(productid);
			InsureRepairSpec insureOrRepairSpec = null;
			if(isInsureOrRepair == 1){//添加保险产品到购物车
				productManager.update(product);
				double insureestimatedmaxgain = Double.valueOf(getRequest().getParameter("insureestimatedmaxgain"));
				String applicant = getRequest().getParameter("applicant");
				String applicant_id = getRequest().getParameter("applicant_id");
				double price = Double.valueOf(getRequest().getParameter("price"));
				insureOrRepairSpec = orderManager.getInsureOrRepairSpec(1, carplate, applicant, applicant_id);
				product.setEnable_store(1);
				productManager.update(product);
			}
			if(isInsureOrRepair == 2){//添加保养产品到购物车
				int time_region_id = Integer.valueOf(getRequest().getParameter("time_region_id"));
				double price = Double.valueOf(getRequest().getParameter("price"));
				double repairestimatedmaxgain = Double.valueOf(getRequest().getParameter("repairestimatedmaxgain"));
				insureOrRepairSpec = orderManager.getInsureOrRepairSpec(2, carplate, time_region_id, store_id);
				product.setEnable_store(1);
				productManager.update(product);
			}
			int cart_id = this.addProductToCart(product, insureOrRepairSpec, store_id, isInsureOrRepair, carplate);
			if(cart_id == -1){
				this.showErrorJson("抱歉！您所选择的货品库存不足。");
				return JSON_MESSAGE;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 添加货品的购物车
	 * @param product
	 * @param isInsureOrRepair 
	 * @return
	 */
	private int addProductToCart(Product product, InsureRepairSpec insureOrRepairSpec, int store_id, int isInsureOrRepair, String carplate){
		String sessionid = ThreadContextHolder.getHttpRequest().getSession().getId();
		
		if(product!=null){
			try{
				/**
				 * 保险、保养商品去掉库存校验
				 */
//				int enableStore = product.getEnable_store();
//				if (enableStore < num) {
//					throw new RuntimeException("抱歉！您所选选择的货品库存不足。");
//				}
				//查询已经存在购物车里的商品
//				Cart tempCart = cartManager.getCartByProductId(product.getProduct_id(), sessionid);
//				if(tempCart != null){
//					int tempNum = tempCart.getNum();
//					if (enableStore < num + tempNum) {
//						throw new RuntimeException("抱歉！您所选选择的货品库存不足。");
//					}
//				}
				Member member = UserConext.getCurrentMember();
				
				Cart cart = new Cart();
				cart.setGoods_id(product.getGoods_id());
				cart.setProduct_id(product.getProduct_id());
				cart.setSession_id(sessionid);
				cart.setNum(num);
				cart.setItemtype(0); //0为product和产品 ，当初是为了考虑有赠品什么的，可能有别的类型。
				cart.setWeight(product.getWeight());
				cart.setPrice(insureOrRepairSpec.getPrice());
				cart.setName(product.getName());
//				if(isInsureOrRepair == 1){//保险下单是在中安下单
//					cart.setStore_id(1);
//				}else{
//					cart.setStore_id(store_id);
//				}
				cart.setStore_id(store_id);
				cart.setRewards_limit(insureOrRepairSpec.getRewards_limit());
				cart.setCarplate(carplate);
				cart.setMember_id(member.getMember_id());
				cart.setInsure_repair_specid(insureOrRepairSpec.getSpec_id());
				
				return this.cartManager.add(cart);
				
			}catch(RuntimeException e){
				this.logger.error("将货品添加至购物车出错",e);
				this.showErrorJson(e.getMessage());
				e.printStackTrace();
				throw new RuntimeException("将货品添加至购物车出错。");
			}
		}else{
			this.showErrorJson("该货品不存在，未能添加到购物车");
			throw new RuntimeException("该货品不存在，未能添加到购物车");
		}
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
	 * 获取购物车中的商品列表
	 * 
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public String list() {
		try {
			
			this.storeCartManager.countPrice(null);
			List<Map> list = StoreCartContainer.getStoreCartListFromSession();

			this.json = JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			this.logger.error("获取购物车列表出错", e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 删除购物车一项(原有逻辑不使用)
	 * 
	 * @param cartid
	 *            :要删除的购物车id,int型,即 CartItem.item_id
	 * @return 返回json字串 result 为1表示调用成功0表示失败 message 为提示信息
	 *         <p/>
	 *         {@link com.enation.app.shop.core.model.support.CartItem }
	 */
	public String delete() {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String cartid = request.getParameter("cartid");
			cartManager.delete(request.getSession().getId(),
					Integer.valueOf(cartid));

			Integer count = this.cartManager.countItemNum(request.getSession()
					.getId());
			this.json = JsonMessageUtil.getNumberJson("count", count);
		} catch (RuntimeException e) {
			this.logger.error("删除购物项失败", e);
			this.showErrorJson("删除购物项失败:" + e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	

	/**
	 * 更新购物车的数量
	 * 
	 * @param cartid
	 *            :要更新的购物车项id，int型，即 CartItem.item_id
	 * @param num
	 *            :要更新数量,int型
	 * @return 返回json字串 result： 为1表示调用成功0表示失败 int型 store: 此商品的库存 int型
	 */
	public String updateNum() {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String cartid = request.getParameter("cartid");
			int num = NumberUtils.toInt(request.getParameter("num"), 1);
			String productid = request.getParameter("productid");
			Product product = productManager.get(Integer.valueOf(productid));
			Integer store = product.getEnable_store();

			if (store == null)
				store = 0;

			if (store >= num) {
				cartManager.updateNum(request.getSession().getId(),
						Integer.valueOf(cartid), Integer.valueOf(num));
				this.storeCartManager.countPrice("no");
				List<Map> list = StoreCartContainer.getStoreCartListFromSession();
				String listStr = JsonUtil.ListToJson(list);
				this.json =  "{\"result\":1,\"data\":"+listStr+"}";
			} else {
				this.showErrorJson("要购买的商品数量超出库存！");
			}
		} catch (RuntimeException e) {
			this.logger.error("更新购物车数量出现意外错误", e);
			this.showErrorJson("更新购物车数量出现意外错误" + e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 购物车的价格总计信息
	 * 
	 * @param 无
	 * @return 返回json字串 result： 为1表示调用成功0表示失败 int型 orderprice: 订单价格，OrderPrice型
	 *         {@link com.enation.app.shop.core.model.support.OrderPrice}
	 */
	public String total() {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			OrderPrice orderprice = this.cartManager.countPrice(
					cartManager.listGoods(request.getSession().getId()), null,
					null);
			this.json = JsonMessageUtil.getObjectJson(orderprice);
		} catch (RuntimeException e) {
			this.logger.error("计算总价出错", e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 计算购物车货物总数
	 * 
	 * @return
	 */
	public String count() {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			Integer count = this.cartManager.countItemNum(request.getSession()
					.getId());
			this.json = JsonMessageUtil.getNumberJson("count", count);
		} catch (RuntimeException e) {
			this.logger.error("计算货物总数出错", e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}

	public IStoreCartManager getStoreCartManager() {
		return storeCartManager;
	}

	public void setStoreCartManager(IStoreCartManager storeCartManager) {
		this.storeCartManager = storeCartManager;
	}

	public IStoreMemberAddressManager getStoreMemberAddressManager() {
		return storeMemberAddressManager;
	}

	public void setStoreMemberAddressManager(
			IStoreMemberAddressManager storeMemberAddressManager) {
		this.storeMemberAddressManager = storeMemberAddressManager;
	}

	public IStoreTemplateManager getStoreTemplateManager() {
		return storeTemplateManager;
	}

	public void setStoreTemplateManager(IStoreTemplateManager storeTemplateManager) {
		this.storeTemplateManager = storeTemplateManager;
	}

	public IStoreDlyTypeManager getStoreDlyTypeManager() {
		return storeDlyTypeManager;
	}

	public void setStoreDlyTypeManager(IStoreDlyTypeManager storeDlyTypeManager) {
		this.storeDlyTypeManager = storeDlyTypeManager;
	}

	public int getProductid() {
		return productid;
	}

	public void setProductid(int productid) {
		this.productid = productid;
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

	public int getIsInsureOrRepair() {
		return isInsureOrRepair;
	}

	public void setIsInsureOrRepair(int isInsureOrRepair) {
		this.isInsureOrRepair = isInsureOrRepair;
	}
	
}
