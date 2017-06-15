
package com.enation.app.b2b2c.core.action.api.cart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.UploadContext;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.objectweb.asm.tree.IntInsnNode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreProduct;
import com.enation.app.b2b2c.core.model.cart.StoreCart;
import com.enation.app.b2b2c.core.model.cart.StoreCartItem;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.cart.IStoreProductManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.eop.processor.core.Request;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 店铺购物车API
 * @author LiFenLong
 *
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/store")
@Action("storeCart")
@InterceptorRef(value="apiRightStack")
public class StoreCartApiAction extends WWAction{

	private ICartManager cartManager;
	private IStoreProductManager storeProductManager;
	private IGoodsManager goodsManager;
	private IProductManager productManager;
	private int num;//要向购物车中活加的货品数量
	private int goodsid;
	private int showCartData;	//在向购物车添加货品时，是否在返回的json串中同时显示购物车数据。0为否,1为是
	private Integer productid;
	
	/**
	 * 添加货品的购物车
	 * @param product
	 * @return
	 */
	private boolean addProductToCart(StoreProduct product){
		String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
		
		if(product!=null){
			try{
				if(product.getStore()==null || product.getStore()<num){
					throw new RuntimeException("抱歉！您所选选择的货品库存不足。");
				}
				StoreCart cart = new StoreCart();
				cart.setGoods_id(product.getGoods_id());
				cart.setProduct_id(product.getProduct_id());
				cart.setSession_id(sessionid);
				cart.setNum(num);
				cart.setItemtype(0); //0为product和产品 ，当初是为了考虑有赠品什么的，可能有别的类型。
				cart.setWeight(product.getWeight());
				cart.setPrice( product.getPrice() );
				cart.setName(product.getName());
				cart.setStore_id(product.getStore_id());
				this.cartManager.add(cart); 
				this.showSuccessJson("货品成功添加到购物车");

				//需要同时显示购物车信息
				if(showCartData==1){
					this.getCartData();
				}
				
				return true;
			}catch(RuntimeException e){
				this.logger.error("将货品添加至购物车出错",e);
				this.showErrorJson("将货品添加至购物车出错["+e.getMessage()+"]");
				return false;
			}
			
		}else{
			this.showErrorJson("该货品不存在，未能添加到购物车");
			return false;
		}
	}
	
	/**
	 * 删除多个购物项
	 * @return 
	 */
	public String delete(){
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		try{
			Member member = UserConext.getCurrentMember();
			if (member == null) {
				this.showErrorJson("您没有登录或登录过期！");
				return JSON_MESSAGE;
			}
			String cartIds = request.getParameter("cartIds");
			if(!StringUtil.isNull(cartIds) && cartIds.lastIndexOf(',')==cartIds.length()-1){
				cartIds = cartIds.substring(0,cartIds.length()-1);
			}
			if (StringUtil.isNull(cartIds)){
				this.showErrorJson("没有选中的购物项");
				return JSON_MESSAGE;
			}
			cartManager.cleanCart(cartIds,member);
			showSuccessJson("删除购物车成功！");
		}catch(Exception e){
			this.logger.error("删除购物车选项错误！",e);
			this.showErrorJson("删除购物车选项错误！");
		}
		return JSON_MESSAGE;
	}
	
	
	/**
	 * 清空购物车
	 */
	public String clean() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		try {
			Member member = UserConext.getCurrentMember();
			if (member == null) {
				this.showErrorJson("您没有登录或登录过期！");
				return JSON_MESSAGE;
			}
			String carplate = request.getParameter("carplate");
			if (StringUtil.isNull(carplate)) {
				this.showErrorJson("没有获取到用户车辆信息");
				return JSON_MESSAGE;
			}
			cartManager.clean(member.getMember_id(),carplate);
			this.showSuccessJson("清空购物车成功");
		} catch (RuntimeException e) {
			this.logger.error("清空购物车出错", e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	
	
	/**购物车列表
	 * @return
	 */
	public String list(){
		HttpServletRequest request =  this.getRequest();
		String carplate = request.getParameter("carplate");
//		String cartIds = request.getParameter("cartIds");
//		if(!StringUtil.isNull(cartIds) && cartIds.lastIndexOf(',')==cartIds.length()-1){
//			cartIds = cartIds.substring(0,cartIds.length()-1);
//		}
		Member member = UserConext.getCurrentMember();
		if (member == null) {
			this.showErrorJson("您没有登录或登录过期！");
			return JSON_MESSAGE;
		}
		try{
			List list = cartManager.showCarts(carplate,member.getMember_id());
			JSONObject carts = new JSONObject();
//			Double goodsTotal  =cartManager.countGoodsTotal(carplate,member.getMember_id());
//			int count = this.cartManager.countItemNum(carplate,member.getMember_id(),null);
//			carts.put("count", count);//购物车中的商品数量
//			carts.put("total", goodsTotal);//总价
			carts.put("carts", this.makeCartsList(list));//为了app端好显示重新构造list
			String data = UploadUtil.replacePath(carts.toString());
			this.json = "{result : 1,data : " + carts.fromObject(data) + "}";
		}catch(Exception e){
			this.showErrorJson("购物车选项展示错误！");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	
	private JSONArray makeCartsList(List list) {//外层放store列表,里层放product
		JSONArray cartsArray = new JSONArray();
		int store_id = -1;
		JSONObject storeObject = new JSONObject();
		JSONArray  productArray = null;
		for(int i = 0 ; i < list.size() ; i++){
			JSONObject cartObject = JSONObject.fromObject(list.get(i));
			if(!cartObject.get("store_id").equals(store_id)){
				if(store_id != -1 ){
					storeObject.put("productList", productArray);
					cartsArray.add(storeObject);
				}
				store_id = (int) cartObject.get("store_id");
				storeObject.put("store_id", cartObject.get("store_id"));
				storeObject.put("store_name", cartObject.get("store_name"));
				productArray = new JSONArray();
			}
			productArray.add(list.get(i));
			
			if(i == list.size()-1){
				storeObject.put("productList", productArray);
				cartsArray.add(storeObject);
			}
		}
		
		if(cartsArray.size()>1){
			int maxCart_id = cartsArray.getJSONObject(0).getJSONArray("productList").getJSONObject(0).getInt("cart_id");
			int minCart_id = cartsArray.getJSONObject(1).getJSONArray("productList").getJSONObject(0).getInt("cart_id");
			if(maxCart_id < minCart_id){
				JSONArray array = new JSONArray();
				for(int i = cartsArray.size()-1 ; i >= 0 ; i--){
					array.add(cartsArray.getJSONObject(i));
				}			
				cartsArray = array;
			}
		}
		return cartsArray;
	}

	/**
	 * 更新商品数量和获取购物车项和价格
	 * @param 无
	 * @return 返回json串
	 * result  为1表示调用成功0表示失败
	 * data.count：购物车的商品总数,int 型
	 * data.total:购物车总价，int型
	 * 
	 */
	public String getCartData(){

		HttpServletRequest request = this.getRequest();
		Member member = UserConext.getCurrentMember();
		String carplate = request.getParameter("carplate");
		String cartIds = request.getParameter("cartIds");

		if (member == null) {
			this.showErrorJson("您没有登录或登录过期！");
			return JSON_MESSAGE;
		}

		String cartid = request.getParameter("cartid");
		int num = NumberUtils.toInt(request.getParameter("num"), 1);
		String productid = request.getParameter("productid");
		
		
		try {	// 处理更新商品数量的方法
			if (!StringUtil.isNull(productid) && !StringUtil.isNull(cartid)) {
				Product product = productManager.get(Integer.valueOf(productid));
				Integer store = product.getEnable_store();
				Goods goods = goodsManager.getGoods(product.getGoods_id());
				if (goods != null && goods.getMarket_enable() == 0) {
					showErrorJson("您所购买的商品  '" + product.getName() + "' 已下架");
					return JSON_MESSAGE;
				}
				if (store == null)
					store = 0;

				if (store >= num) {
					cartManager.updateNum(member.getMember_id(), Integer.valueOf(cartid), Integer.valueOf(num));
				} else {
					this.showErrorJson("要购买的商品数量超出库存！");
					return JSON_MESSAGE;
				}
			}
		} catch (RuntimeException e) {
			this.logger.error("更新购物车数量出现意外错误", e);
			this.showErrorJson("更新购物车数量出现意外错误" + e.getMessage());
			return JSON_MESSAGE;
		}

		try {//处理得到购物车数量和价格方法
			Double goodsTotal = 0d;
			int count = 0;
			if (!StringUtil.isNull(cartIds)) {
				if (cartIds.lastIndexOf(',') == cartIds.length() - 1) {
					cartIds = cartIds.substring(0, cartIds.length() - 1);
				}
				goodsTotal = cartManager.countGoodsTotal(carplate, member.getMember_id(), cartIds);
				count = this.cartManager.countItemNum(carplate, member.getMember_id(), cartIds);
			}
			java.util.Map<String, Object> data = new HashMap();
			data.put("count", count);// 购物车中的购物项数量
			data.put("total", goodsTotal);// 总价
			this.json = JsonMessageUtil.getObjectJson(data);
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("获取购物车数据出错", e);
			this.showErrorJson("获取购物车数据出错[" + e.getMessage() + "]");
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
		Member member = UserConext.getCurrentMember();
		if (member == null) {
			this.showErrorJson("您没有登录或登录过期！");
			return JSON_MESSAGE;
		}
		StoreProduct product = null;
		if(productid!=null){
			product = storeProductManager.get(productid);
		}else{
			product = storeProductManager.getByGoodsId(goodsid);
		}
		try{
			int cartid = cartManager.addProductToCart(product, num ,false);
			if(cartid == -1){
				this.showErrorJson("抱歉！您所选选择的货品库存不足。");
				return JSON_MESSAGE;
			}
			String carplate =this.getRequest().getParameter("carplate");
			JSONObject obj = new JSONObject();
			Integer  cartCount =  cartManager.countItemNum(carplate, member.getMember_id(), null);
			obj.put("cartCount", cartCount);//用户购物车的购物项数量
			this.json = JsonMessageUtil.getObjectJson(obj);
		}catch(Exception e){
			this.logger.error("商品加入购物车出错",e);
			this.showErrorJson("商品加入购物车出错");
		}
		return JSON_MESSAGE;
	}
	

	
	
	public String invalidGoods(){
		try {
			List<Map> list= StoreCartContainer.getStoreCartListFromSession();
			String message = "";
			for (Map storemap : list) {
				List<StoreCartItem> List = (List) storemap.get("goodslist");
				for (StoreCartItem storeCartItem : List) {
					String addon = storeCartItem.getAddon();
					List<Map<String,Object>> specsList = new ArrayList();
					
					if(addon!=null){
						specsList = JsonUtil.toList(addon);
					}
					 
					
					Integer productid = storeCartItem.getProduct_id();
					Product product =  this.productManager.get(productid);
					if(product.getEnable_store().intValue()==0){
						String specs = "";
						if(specsList!=null &&  !specsList.isEmpty()){
							for(Map map:specsList){
								specs = map.get("name").toString()+":"+map.get("value").toString()+",";
							}
						}
						message+="["+product.getName()+",";
						if(!specs.equals("")){
							message+="规格:("+specs+")],";
						}
					}
				}
			}
			if(!message.equals("")){
				this.showSuccessJson(message.substring(0, message.length()-1));
			}else{
				this.showErrorJson("ok");
			}
			
		} catch (Exception e) {
			this.showErrorJson("ok");
		}
		return JSON_MESSAGE;
	}
	
	//set get
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getGoodsid() {
		return goodsid;
	}
	public void setGoodsid(int goodsid) {
		this.goodsid = goodsid;
	}
	public ICartManager getCartManager() {
		return cartManager;
	}
	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}
	public int getShowCartData() {
		return showCartData;
	}
	public void setShowCartData(int showCartData) {
		this.showCartData = showCartData;
	}
	public IStoreProductManager getStoreProductManager() {
		return storeProductManager;
	}
	public void setStoreProductManager(IStoreProductManager storeProductManager) {
		this.storeProductManager = storeProductManager;
	}
	public Integer getProductid() {
		return productid;
	}
	public void setProductid(Integer productid) {
		this.productid = productid;
	}
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	public IProductManager getProductManager() {
		return productManager;
	}
	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}
	
}
