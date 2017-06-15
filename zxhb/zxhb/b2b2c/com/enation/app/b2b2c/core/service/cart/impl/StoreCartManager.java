package com.enation.app.b2b2c.core.service.cart.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.cart.StoreCartItem;
import com.enation.app.b2b2c.core.service.IStoreDlyTypeManager;
import com.enation.app.b2b2c.core.service.IStoreTemplateManager;
import com.enation.app.b2b2c.core.service.StoreCartContainer;
import com.enation.app.b2b2c.core.service.StoreCartKeyEnum;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberAddress;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IMemberAddressManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;

@Component
public class StoreCartManager extends BaseSupport implements IStoreCartManager {
	private CartPluginBundle cartPluginBundle;
	private IDlyTypeManager dlyTypeManager;
	private IPromotionManager promotionManager;
	private IStoreGoodsManager storeGoodsManager;
	private IStoreMemberManager storeMemberManager;
	private IStoreDlyTypeManager storeDlyTypeManager;
	private IStoreTemplateManager storeTemplateManager;
	private IMemberAddressManager memberAddressManager;
	private ICartManager cartManager;
	

	@Override
	public void putCartListToSession(String cartIds) {
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		//获取对应cardIds对应各个商铺购物车列表
		List<Map> storeGoodsList = storeGoodsList(cartIds);
		//循环每个店铺，计算费用
		
		for (Map map : storeGoodsList) {
			List list = (List) map.get(StoreCartKeyEnum.goodslist.toString());

			// 计算店铺价格，不计算运费
			OrderPrice orderPrice = this.cartManager.countPrice(list, null, null);

			// 为店铺信息压入店铺的各种价格
			map.put(StoreCartKeyEnum.storeprice.toString(), orderPrice);

		}
		// 向session中压入购物车列表
		StoreCartContainer.putStoreCartListToSession(storeGoodsList);
	}


	@Override
	@Deprecated
	public void countPrice(String isCountShip) {

		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		//sessionid
		String sessionid = request.getSession().getId();
		
		//获取各个店铺购物车列表
		List<Map> storeGoodsList=storeListGoods(sessionid);
		//用户的默认地区（自己建立的或者根据ip得到的）
		Integer regionid = this.memberAddressManager.getMemberDefaultRegionId();
		
		//如果计算价格说明进入结算页了，那么要将用户的默认地址压入session
		if("yes".equals(isCountShip)){
			
			/**
			 * 获取结算时的用户地址，优先级为：
			 * 1.用户选择过的
			 * 2.用户建立的默认地址
			 * 3.根据用户所在ip的到
			 */
			MemberAddress address=this.getCheckoutAddress();
			if(address!=null){
				regionid=address.getRegion_id();
			}
			 
		}
		
		
		
		//循环每个店铺，计算各种费用，如果要求计算运费还要根据配送方式和地区计算运费
		for(Map map : storeGoodsList){ 
			List list = (List) map.get(StoreCartKeyEnum.goodslist.toString());
			
			//计算店铺价格，不计算运费
			OrderPrice orderPrice =   this.cartManager.countPrice(list, null, null);
			
			//为店铺信息压入店铺的各种价格
			map.put(StoreCartKeyEnum.storeprice.toString(), orderPrice);
			
			//如果指定计算运费，则计算每个店的的运费，并设置好配送方式列表，在结算页中此参数应该设置为yes
			if("yes".equals(isCountShip)){
				orderPrice =this.countShipPrice(map,regionid);
			} 
		} 
		//向session中压入购物车列表
		StoreCartContainer.putStoreCartListToSession(storeGoodsList);
	}
	
	
	/**
	 * 获取结算时的用户地址，优先级为：
	 * 1.用户选择过的
	 * 2.用户建立的默认地址
	 * @return 用戶地址
	 */
	private MemberAddress getCheckoutAddress(){
		Member member = UserConext.getCurrentMember();
		if(member!=null){
			
			
			/**
			 * 先检查是否选择过地区，有选择过，则使用用户选择的地址。
			 */
			MemberAddress address= StoreCartContainer.getUserSelectedAddress();
			if(address!=null){
				return address;
			}
			
			
			/**
			 * 如果用户没有选择过收货地址，则使用用户的默认地址
			 */
			address= memberAddressManager.getMemberDefault(member.getMember_id());
			if(address!=null){
				
				/**
				 * 同时将默认地址设置为用户的选择过的地址
				 */
				if(StoreCartContainer.getUserSelectedAddress()==null){
					StoreCartContainer.putSelectedAddress(address);
				}
				
				return address;
				
			}
			
			
		}
		
		return null;
	}
	
	
	
	private OrderPrice countShipPrice(Map map,int regionid){
		
		//是否免运费
		boolean free_ship=false;
		int storeid= (Integer)map.get(StoreCartKeyEnum.store_id.toString());
		List<StoreCartItem> list = (List) map.get(StoreCartKeyEnum.goodslist.toString());
		
		//先检测购物商品中是否有免运费的
		for (StoreCartItem storeCartItem : list) {
			//如果有一个商品免运费则此店铺订单免运费
			if (1== storeCartItem.getGoods_transfee_charge()){
				free_ship=true;
				break;
			}
		}
		
		//取出之前计算好的订单价格
		OrderPrice orderPrice = (OrderPrice)map.get(StoreCartKeyEnum.storeprice.toString());
		
		//得到商品总计和重量，以便计算运费之用
		Double goodsprice = orderPrice.getGoodsPrice();
		Double weight = orderPrice.getWeight();
		
		
		
		//生成配送方式列表，此map中已经含有计算后的运费
		List<Map> shipList  = this.getShipTypeList(storeid, regionid, weight, goodsprice);
		
		//向店铺信息中压入配送方式列表
		map.put(StoreCartKeyEnum.shiptype_list.toString(),shipList);
		
		//如果免运费，向配送方式列表中加入免运费项
		if(free_ship ||shipList.isEmpty()){
			
			 //生成免运费项
			 Map freeType = new HashMap();
			 freeType.put("type_id", 0);
			 freeType.put("name", "免运费");
			 freeType.put("shipPrice", 0);
			 
			 //将免运费项加入在第一项
			 shipList.add(0, freeType);
			 
			 //设置运费价格和配送方式id
			 orderPrice.setShippingPrice(0d);
			 map.put(StoreCartKeyEnum.shiptypeid.toString(), 0);
			 
		}else{
		 
			
			//如果不免运费用第一个配送方式 设置运费价格和配送方式id
			Map firstShipType =shipList.get(0);
			Double shipprice = (Double)firstShipType.get("shipPrice");
			orderPrice.setShippingPrice(shipprice);
			
			//订单总费用为商品价格+运费
			double orderTotal = CurrencyUtil.add(goodsprice, shipprice);
			orderPrice.setOrderPrice(orderTotal);
			
		    map.put(StoreCartKeyEnum.shiptypeid.toString(), firstShipType.get("type_id"));
			
		}
		
		orderPrice.setNeedPayMoney(orderPrice.getOrderPrice());
		return orderPrice;
	}
	
	

	private List<Map> getShipTypeList(int storeid,int regionid,double weight,double goodsprice){
		
		List<Map> newList  = new ArrayList();
		if(Double.valueOf(weight)!=0d){
			Integer tempid = this.storeTemplateManager.getDefTempid(storeid);
			List<Map> list =   this.storeDlyTypeManager.getDlyTypeList(tempid);
			
			for(Map maps:list){
				Map newMap = new HashMap();
				String name = (String)maps.get("name");
				Integer typeid = (Integer) maps.get("type_id");
				Double[] priceArray = this.dlyTypeManager.countPrice(typeid, Double.valueOf(weight), goodsprice, regionid+"");
				Double dlyPrice = priceArray[0];//配送费用
				newMap.put("name", name);
				newMap.put("type_id", typeid);
				newMap.put("shipPrice", dlyPrice);
				newList.add(newMap);
			}
		}
		
		return newList;
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.cart.IStoreCartManager#listGoods(java.lang.String)
	 */
	@Override
	@Deprecated
	public List<StoreCartItem> listGoods(String sessionid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select s.store_id as store_id,s.store_name as store_name,p.weight AS weight,c.cart_id as id,g.goods_id,g.thumbnail as image_default,c.name ,  p.sn, p.specs  ,g.mktprice,g.unit,g.point,p.product_id,c.price,c.cart_id as cart_id,c.num as num,c.itemtype,c.addon,  c.price  as coupPrice , c.rewards_limit as rewards_limit from "+ this.getTableName("cart") +" c,"+ this.getTableName("product") +" p,"+ this.getTableName("goods")+" g ,"+this.getTableName("store")+" s ");//g.goods_transfee_charge as goods_transfee_charge,
		sql.append("where c.itemtype=0 and c.product_id=p.product_id and p.goods_id= g.goods_id and c.session_id=?  AND c.store_id=s.store_id");
		List list  =this.daoSupport.queryForList(sql.toString(), StoreCartItem.class, sessionid);
		cartPluginBundle.filterList(list, sessionid);
		return list;
	}
	
	
	@Override
	public List<StoreCartItem> listGoodsByCartids(String cartIds) {
		StringBuffer sql = new StringBuffer();
		sql.append("select s.store_id as store_id,s.store_name as store_name,p.weight AS weight,c.cart_id as id,g.goods_id,g.thumbnail as image_default,c.name ,  p.sn, p.specs  ,g.mktprice,g.unit,g.point,p.product_id,c.price,c.cart_id as cart_id,c.num as num,c.itemtype,c.addon,  c.price  as coupPrice , c.rewards_limit as rewards_limit, c.insure_repair_specid from "+ this.getTableName("cart") +" c,"+ this.getTableName("product") +" p,"+ this.getTableName("goods")+" g ,"+this.getTableName("store")+" s ");//g.goods_transfee_charge as goods_transfee_charge,
		sql.append("where c.itemtype=0 and c.product_id=p.product_id and p.goods_id= g.goods_id AND c.cart_id in("+cartIds+")  AND c.store_id=s.store_id");
		List list  =this.daoSupport.queryForList(sql.toString(), StoreCartItem.class);
		cartPluginBundle.filterList(list,null);//session可以为空
		return list;
	}
	
	
	
	@Override
	@Deprecated
	public List<Map> storeListGoods(String sessionid) {
		List<Map> storeGoodsList= new ArrayList<Map>();
		List<StoreCartItem> goodsList =new ArrayList();
		
		goodsList= this.listGoods(sessionid); //商品列表
		
		for (StoreCartItem item : goodsList) {
			findStoreMap(storeGoodsList, item);
		}
		return storeGoodsList;
	}
	
	@Override
	public List<Map> storeGoodsList(String cartIds) {
		
		List<Map> storeGoodsList= new ArrayList<Map>();
		List<StoreCartItem> goodsList =new ArrayList();
		
		goodsList= this.listGoodsByCartids(cartIds); //根据cartid获得商品列表
		
		for (StoreCartItem item : goodsList) {
			findStoreMap(storeGoodsList, item);
		}
		return storeGoodsList;
	}
	
	
	/**
	 * 获取店铺商品列表
	 * @param storeGoodsList
	 * @param map
	 * @param StoreCartItem
	 * @return list<Map>
	 */
	private void findStoreMap(List<Map> storeGoodsList,StoreCartItem item){
		int is_store=0;
		if (storeGoodsList.isEmpty()){
			addGoodsList(item, storeGoodsList);
		}else{
			for (Map map: storeGoodsList) {
				if(map.containsValue(item.getStore_id())){
					List list=(List) map.get(StoreCartKeyEnum.goodslist.toString());
					list.add(item);
					is_store=1;
				}
			}
			if(is_store==0){
				addGoodsList(item, storeGoodsList);
			}
		}
	}
	
	/**
	 * 添加至店铺列表
	 * @param item
	 * @param storeGoodsList
	 */
	private void addGoodsList(StoreCartItem item,List<Map> storeGoodsList){
		Map map=new HashMap();
		List list=new ArrayList();
		list.add(item);
		map.put(StoreCartKeyEnum.store_id.toString(), item.getStore_id());
		map.put(StoreCartKeyEnum.store_name.toString(), item.getStore_name());
		map.put(StoreCartKeyEnum.goodslist.toString(), list);
		storeGoodsList.add(map);
	}
	@Override
	public void  clean(String sessionid){
		String sql ="delete from cart where session_id=?";
		
		this.baseDaoSupport.execute(sql, sessionid);
	}
	
	
	
	@Override
	public void updatePriceByProductid(Integer product_id,Double price) {
		String sql = "update es_cart set price=? where product_id=?";
		this.daoSupport.execute(sql, price,product_id);
	}

	//set get
	
	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}
	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}
	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}
	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}
	public IStoreGoodsManager getStoreGoodsManager() {
		return storeGoodsManager;
	}
	public void setStoreGoodsManager(IStoreGoodsManager storeGoodsManager) {
		this.storeGoodsManager = storeGoodsManager;
	}
	public CartPluginBundle getCartPluginBundle() {
		return cartPluginBundle;
	}
	public void setCartPluginBundle(CartPluginBundle cartPluginBundle) {
		this.cartPluginBundle = cartPluginBundle;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}




	public IStoreDlyTypeManager getStoreDlyTypeManager() {
		return storeDlyTypeManager;
	}




	public void setStoreDlyTypeManager(IStoreDlyTypeManager storeDlyTypeManager) {
		this.storeDlyTypeManager = storeDlyTypeManager;
	}




	public IStoreTemplateManager getStoreTemplateManager() {
		return storeTemplateManager;
	}




	public void setStoreTemplateManager(IStoreTemplateManager storeTemplateManager) {
		this.storeTemplateManager = storeTemplateManager;
	}




	public IMemberAddressManager getMemberAddressManager() {
		return memberAddressManager;
	}




	public void setMemberAddressManager(IMemberAddressManager memberAddressManager) {
		this.memberAddressManager = memberAddressManager;
	}




	public ICartManager getCartManager() {
		return cartManager;
	}


	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}


	
}
