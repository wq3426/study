package com.enation.app.shop.core.service.impl;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.core.model.Cart;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.mapper.CartItemMapper;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.CartPluginBundle;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IDlyTypeManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IPromotionManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.DoubleMapper;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.StringUtil;

/**
 * 购物车业务实现
 * 
 * @author kingapex 2010-3-23下午03:30:50
 * edited by lzf 2011-10-08
 */

public class CartManager extends BaseSupport implements ICartManager {
	private IDlyTypeManager dlyTypeManager;

	private CartPluginBundle cartPluginBundle;
	private IMemberLvManager memberLvManager;
	private IPromotionManager promotionManager;
	private IGoodsManager goodsManager;
	
	
	
	@Override
	public Integer addProductToCart(Product product,int num,boolean isImmediately) {
		String sessionid =ThreadContextHolder.getHttpRequest().getSession().getId();
		String carplate = ThreadContextHolder.getHttpRequest().getParameter("carplate");
		Member member =  UserConext.getCurrentMember();
		if(product!=null){
			int enableStore = product.getEnable_store();
			if (enableStore < num) {
//				throw new RuntimeException("抱歉！您所选选择的货品库存不足。");
				return -1;//货品不足返回 -1再外面校验,不抛出异常
			}
			//根据productid获取goods获取store_id
			Goods goods = goodsManager.getGoods(product.getGoods_id());
			
			try {
				Cart cart = null;
				if (!isImmediately) {// 如果是添加购物车
					// 查询已经存在购物车里的商品,如果购物车有则数量叠加
					cart = this.getCartByProductId(product.getProduct_id(), member.getMember_id(), carplate);
					if (cart != null) {
						int tempNum = cart.getNum();
						if (enableStore < num + tempNum) {
							// throw new RuntimeException("抱歉！您所选选择的货品库存不足。");
							return -1;// 货品不足返回 -1再外面校验,不抛出异常
						}
						cart.setNum(num);
					} else {
						cart = new Cart();
						cart.setGoods_id(product.getGoods_id());
						cart.setProduct_id(product.getProduct_id());
						cart.setSession_id(sessionid);
						cart.setNum(num);
						cart.setItemtype(0); // 0为product和产品
												// ，当初是为了考虑有赠品什么的，可能有别的类型。
						cart.setWeight(product.getWeight());
						cart.setPrice(product.getPrice());
						cart.setName(product.getName());
						cart.setRewards_limit(product.getRewards_limit());
						cart.setMember_id(member.getMember_id());
						cart.setCarplate(carplate);
						cart.setStore_id(goods.getStore_id());
					}
					cart.isImmediately = isImmediately;
				}else{//如果为立即下单，购物车也有相同商品则保留购物车商品
					cart = new Cart();
					cart.setGoods_id(product.getGoods_id());
					cart.setProduct_id(product.getProduct_id());
					cart.setSession_id(sessionid);
					cart.setNum(num);
					cart.setItemtype(0); // 0为product和产品// ，当初是为了考虑有赠品什么的，可能有别的类型。
					cart.setWeight(product.getWeight());
					cart.setPrice(product.getPrice());
					cart.setName(product.getName());
					cart.setRewards_limit(product.getRewards_limit());
					cart.setMember_id(member.getMember_id());
					cart.setCarplate(carplate);
					cart.setStore_id(goods.getStore_id());
					cart.isImmediately = isImmediately;
					
				}
				return this.add(cart);
			}catch(Exception e){
				e.printStackTrace();
				this.logger.error("将货品添加至购物车出错。",e);
				throw new RuntimeException("将货品添加至购物车出错。");
			}
			
		}else{
			throw new RuntimeException("该货品不存在，未能添加到购物车");
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public int add(Cart cart) {
		//HttpCacheManager.sessionChange();
		/*
		 * 触发购物车添加事件
		 */
		try {
			this.cartPluginBundle.onAdd(cart);
			
			if(!cart.isImmediately){
				String sql = "";
				int count = 0;
				if(cart.getInsure_repair_specid() == null){
					sql ="select count(0) from es_cart where  product_id=? and member_id=? and carplate=? and itemtype=?";
					count = this.baseDaoSupport.queryForInt(sql, cart.getProduct_id(),cart.getMember_id(),cart.getCarplate(),cart.getItemtype());
				}else{
					sql ="select count(0) from es_cart where  product_id=? and member_id=? and carplate=? and itemtype=? and insure_repair_specid=?";
					count = daoSupport.queryForInt(sql, cart.getProduct_id(),cart.getMember_id(),cart.getCarplate(),cart.getItemtype(), cart.getInsure_repair_specid());
				}
				if(count>0){
					daoSupport.execute("update es_cart set num=num+? where  product_id=? and member_id=? and carplate=? and itemtype=? ", cart.getNum(),cart.getProduct_id(),cart.getMember_id(),cart.getCarplate(),cart.getItemtype());
//					return 0;
					Cart cart2 = this.getCartByProductId(cart.getProduct_id(), cart.getMember_id(),cart.getCarplate());
//					this.cartPluginBundle.onAfterAdd(cart);
					return cart2.getCart_id();
					
				}else{
					daoSupport.insert("es_cart", cart);
					Integer cartid  = daoSupport.getLastId("es_cart");
//					this.cartPluginBundle.onAfterAdd(cart);
					return cartid;
				}
			}
			
			
			else{
				daoSupport.insert("es_cart", cart);
				Integer cartid  = daoSupport.getLastId("es_cart");
//				this.cartPluginBundle.onAfterAdd(cart);
				return cartid;
			}
			
			
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
	}
	
	/**
	 * 
	 */
	public Cart get(int cart_id){
		return (Cart)this.baseDaoSupport.queryForObject("SELECT * FROM es_cart WHERE cart_id=?", Cart.class, cart_id);
	}
	
	public Cart getCartByProductId(int productId, String sessionid){
		return (Cart)this.baseDaoSupport.queryForObject("SELECT * FROM es_cart WHERE product_id=? AND session_id=?", Cart.class, productId,sessionid);
	}
	
	public Cart getCartByProductId(int productId, String sessionid, String addon){
		return (Cart)this.baseDaoSupport.queryForObject("SELECT * FROM es_cart WHERE product_id=? AND session_id=? AND addon=?", Cart.class, productId, sessionid, addon);
	}
	
	public Cart getCartByProductId(int productId, Integer member_id, String carplate){
		return (Cart)this.baseDaoSupport.queryForObject("SELECT * FROM es_cart WHERE product_id=? AND member_id=? AND carplate=?", Cart.class, productId, member_id, carplate);
	}
	
	public Integer countItemNum(String sessionid) {
		String sql = "select count(0) from es_cart where session_id =?";
		return this.baseDaoSupport.queryForInt(sql, sessionid);
	}
	
	public Integer countItemNum(String carpalte,Integer member_id,String cartIds) {
		String sql = "select count(0) from es_cart where carplate =? and member_id = ? ";
		if(!StringUtil.isNull(cartIds)){
		 sql  = sql + " and cart_id in ("+cartIds+")";
		}
		return this.baseDaoSupport.queryForInt(sql,carpalte,member_id);
	}
	
	@Deprecated
	public List<CartItem> listGoods(String sessionid) {
		StringBuffer sql = new StringBuffer();

		sql.append("select g.cat_id as catid,g.goods_id,g.thumbnail,c.name ,  p.sn, p.specs  ,g.mktprice,g.unit,g.point,p.product_id,c.price,c.cart_id as cart_id,c.num as num,c.itemtype,c.addon,c.weight  from "+ this.getTableName("cart") +" c,"+ this.getTableName("product") +" p,"+ this.getTableName("goods")+" g ");
		sql.append("where c.itemtype=0 and c.product_id=p.product_id and p.goods_id= g.goods_id and c.session_id=?");
		List<CartItem>  list  =this.daoSupport.queryForList(sql.toString(), new CartItemMapper(), sessionid);
		cartPluginBundle.filterList(list, sessionid);
		return list;
	}
	
	
	public List<CartItem> listGoodsByCartids(String cartids) {
		StringBuffer sql = new StringBuffer();

		sql.append("select g.cat_id as catid,g.goods_id,g.thumbnail,c.name ,  p.sn, p.specs  ,g.mktprice,g.unit,g.point,p.product_id,c.price,c.cart_id as cart_id,c.num as num,c.itemtype,c.addon,c.weight  from "+ this.getTableName("cart") +" c,"+ this.getTableName("product") +" p,"+ this.getTableName("goods")+" g ");
		sql.append("where c.itemtype=0 and c.product_id=p.product_id and p.goods_id= g.goods_id and c.cart_id in ("+cartids+")");
		List<CartItem>  list  =this.daoSupport.queryForList(sql.toString(), new CartItemMapper());
		cartPluginBundle.filterList(list,null);//sessionId可以为null
		return list;
	}
	
 
	

	public void  clean(String sessionid){
		String sql ="delete from es_cart where session_id=?";
 
		this.baseDaoSupport.execute(sql, sessionid);
//		HttpCacheManager.sessionChange();
	}

	public void  clean(Integer member_id){
		String sql ="delete from es_cart where member_id=?";
		this.baseDaoSupport.execute(sql, member_id);
//		HttpCacheManager.sessionChange();
	}
	
	@Override
	public void  clean(Integer member_id,String carplate){
		String sql ="delete from es_cart where member_id = ? and carplate = ?";
		this.baseDaoSupport.execute(sql,member_id,carplate);
//		HttpCacheManager.sessionChange();
	}
	
	
	@Override
	public void cleanCart(String cardIds,Member member) {
		String sql = "delete from es_cart where cart_id in ("+cardIds+") ";
		if(member != null && member.getMember_id() > -1){
			sql += "and member_id = " + member.getMember_id(); 
		}
		this.baseDaoSupport.execute(sql);
	}

	public void clean(String sessionid, Integer userid, Integer siteid) {

			String sql = "delete from es_cart where session_id=?";
			this.baseDaoSupport.execute(sql, sessionid);

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("clean es_cart sessionid[" + sessionid + "]");
		}
//		HttpCacheManager.sessionChange();
	}
	
	

	public void delete(String sessionid, Integer cartid) {
		String sql = "delete from es_cart where session_id=? and cart_id=?";
		this.baseDaoSupport.execute(sql, sessionid, cartid);
		this.cartPluginBundle.onDelete(sessionid, cartid);
//		HttpCacheManager.sessionChange();
	}

	public void updateNum(String sessionid, Integer cartid, Integer num) {
		try {
			//执行购物车数量修改前事件
			cartPluginBundle.onBeforeUpdate(sessionid, cartid, num);
		
			//修改购物车数量
			String sql = "update es_cart set num=? where session_id =? and cart_id=?";
			this.baseDaoSupport.execute(sql, num, sessionid, cartid);
			
			//执行修改购物车修改后事件
			this.cartPluginBundle.onUpdate(sessionid, cartid);
		}catch (RuntimeException e) {
			throw e;
		}
		
	}
	
	public void updateNum(Integer member_id, Integer cartid, Integer num) {
		try {
			//执行购物车数量修改前事件
//			cartPluginBundle.onBeforeUpdate(null, cartid, num);
		
			//修改购物车数量
			String sql = "update es_cart set num=? where member_id =? and cart_id=?";
			this.baseDaoSupport.execute(sql, num, member_id, cartid);
			
			//执行修改购物车修改后事件
//			this.cartPluginBundle.onUpdate(null, cartid);
		}catch (RuntimeException e) {
			throw e;
		}
		
	}
	
	
	public Double countGoodsTotal(String sessionid) {
		StringBuffer sql = new StringBuffer();
		sql.append("select sum( c.price * c.num ) as num from es_cart c ");
		sql.append("where  c.session_id=? and c.itemtype=0 ");
		Double price = (Double) this.baseDaoSupport.queryForObject(sql
				.toString(), new DoubleMapper(), sessionid);
		return price;
	}
	
	
	public Double countGoodsTotal(String carplate,Integer member_id) {
		StringBuffer sql = new StringBuffer();
		sql.append("select sum( c.price * c.num ) as num from es_cart c ");
		sql.append("where  c.carplate = ? and c.member_id = ? and c.itemtype=0 ");
		Double price = (Double) this.baseDaoSupport.queryForObject(sql
				.toString(), new DoubleMapper(), carplate,member_id);
		return price;
	}
	
	public Double countGoodsTotal(String carplate,Integer member_id,String cartIds) {
		StringBuffer sql = new StringBuffer();
		sql.append("select sum( c.price * c.num ) as num from es_cart c ");
		sql.append("where  c.itemtype=0  ");
		if(StringUtil.isNull(carplate)){
			sql.append(" and  c.carplate = " + carplate);
		}
		if(member_id != null && member_id > 0){
			sql.append(" and  c.member_id = " + member_id);
		}
		if(!StringUtil.isNull(cartIds)){
			sql.append(" and  c.cart_id in (" + cartIds + ")");
		}
		Double price = (Double) this.baseDaoSupport.queryForObject(sql
				.toString(), new DoubleMapper());
		return price;
	}




	
	public Double  countGoodsDiscountTotal(String sessionid){
		

		List<CartItem> itemList = this.listGoods(sessionid);

		double price = 0; // 计算商品促销规则优惠后的总价
		for (CartItem item : itemList) {
			// price+=item.getSubtotal();
			price = CurrencyUtil.add(price, item.getSubtotal());
		}

		return price;
	}

	
	public Integer countPoint(String sessionid) {

//		Member member = UserServiceFactory.getUserService().getCurrentMember();
//		if (member != null) {
//			Integer memberLvId = member.getLv_id();
//			StringBuffer sql = new StringBuffer();
//			sql
//					.append("select c.*, g.goods_id, g.point from "
//							+ this.getTableName("cart")
//							+ " c,"
//							+ this.getTableName("goods")
//							+ " g, "
//							+ this.getTableName("product")
//							+ " p where p.product_id = c.product_id and g.goods_id = p.goods_id and c.session_id = ?");
//			List<Map> list = this.daoSupport.queryForList(sql.toString(),
//					sessionid);
//			Integer result = 0;
//			for (Map map : list) {
//				Integer goodsid = StringUtil.toInt(map.get("goods_id")
//						.toString());
//				List<Promotion> pmtList = new ArrayList();
//				
//				if(memberLvId!=null){
//					pmtList = promotionManager.list(goodsid, memberLvId);
//				}
//				
//				for (Promotion pmt : pmtList) {
//
//					// 查找相应插件
//					String pluginBeanId = pmt.getPmts_id();
//					IPromotionPlugin plugin = promotionManager
//							.getPlugin(pluginBeanId);
//
//					if (plugin == null) {
//						logger.error("plugin[" + pluginBeanId + "] not found ");
//						throw new ObjectNotFoundException("plugin["
//								+ pluginBeanId + "] not found ");
//					}
//
//					// 查找相应优惠方式
//					String methodBeanName = plugin.getMethods();
//					if (this.logger.isDebugEnabled()) {
//						this.logger.debug("find promotion method["
//								+ methodBeanName + "]");
//					}
//					IPromotionMethod promotionMethod = SpringContextHolder
//							.getBean(methodBeanName);
//					if (promotionMethod == null) {
//						logger.error("plugin[" + methodBeanName
//								+ "] not found ");
//						throw new ObjectNotFoundException("promotion method["
//								+ methodBeanName + "] not found ");
//					}
//
//					// 翻倍积分方式
//					if (promotionMethod instanceof ITimesPointBehavior) {
//						Integer point = StringUtil.toInt(map.get("point")
//								.toString());
//						ITimesPointBehavior timesPointBehavior = (ITimesPointBehavior) promotionMethod;
//						point = timesPointBehavior.countPoint(pmt, point);
//						result += point;
//					}
//
//				}
//			}
//			return result;
//		} else {
			StringBuffer sql = new StringBuffer();
			sql.append("select  sum(g.point * c.num) from "
					+ this.getTableName("cart") + " c,"
					+ this.getTableName("product") + " p,"
					+ this.getTableName("goods") + " g ");
			sql
					.append("where (c.itemtype=0  or c.itemtype=1)  and c.product_id=p.product_id and p.goods_id= g.goods_id and c.session_id=?");

			return this.daoSupport.queryForInt(sql.toString(), sessionid);
//		}
	}

	public Double countGoodsWeight(String sessionid) {
		StringBuffer sql = new StringBuffer(
				"select sum( c.weight * c.num )  from es_cart c where c.session_id=?");
		Double weight = (Double) this.baseDaoSupport.queryForObject(sql
				.toString(), new DoubleMapper(), sessionid);
		return weight;
	}
	

 
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.ICartManager#countPrice(java.util.List, java.lang.Integer, java.lang.String)
	 */
	@Override
	public OrderPrice countPrice(List<CartItem> cartItemList, Integer shippingid,String regionid) {
		
		OrderPrice orderPrice = new OrderPrice();
		
		//计算商品重量
		Double weight=0.0;
 
		//订单总价格
		Double  orderTotal = 0d;
		
		//配送费用
		Double dlyPrice = 0d;  
		
		//优惠后的订单价格,默认为商品原始价格
		Double goodsPrice =0.0; 
		
		//计算商品总数
		Integer goods_num = 0;
		//计算商品重量及商品价格
		for (CartItem cartItem : cartItemList) {
			
			// 计算商品重量
			weight = CurrencyUtil.add(weight, CurrencyUtil.mul(cartItem.getWeight(), cartItem.getNum()));
			
		 
			//计算商品优惠后的价格小计
			Double itemTotal = CurrencyUtil.mul(cartItem.getCoupPrice(), cartItem.getNum());
			goodsPrice=CurrencyUtil.add(goodsPrice, itemTotal);
			
			goods_num += cartItem.getNum();
			
		}
		
		 
		//如果传递了配送信息，计算配送费用
		if(regionid!=null &&shippingid!=null ){
			if(shippingid!=0){
				//计算原始配置送费用
				Double[] priceArray = this.dlyTypeManager.countPrice(shippingid, weight, goodsPrice, regionid);
				
				//费送费用
				dlyPrice = priceArray[0];
			}
			
		}

		
		//商品金额 
		orderPrice.setGoodsPrice(goodsPrice); 
		
		//配送费用
		orderPrice.setShippingPrice(dlyPrice);
				
		//订单总金额:商品金额+运费
		orderTotal = CurrencyUtil.add(goodsPrice, dlyPrice); 
		
		//订单总金额
		orderPrice.setOrderPrice(orderTotal);
		
		
		//应付金额为订单总金额
		orderPrice.setNeedPayMoney(orderTotal); 
		
		//订单总的优惠金额
		orderPrice.setDiscountPrice( CurrencyUtil.add(0d,BonusSession.getUseMoney())); 
		
		orderPrice.setNeedPayMoney(CurrencyUtil.add(orderTotal,-orderPrice.getDiscountPrice())); 
		
		//如果优惠金额后订单价格小于0
		if(orderPrice.getNeedPayMoney()<=0){
			orderPrice.setNeedPayMoney(0d);
		}
		
		//订单可获得积分
		orderPrice.setPoint(0); 
		
		//商品总重量
		orderPrice.setWeight(weight);
		
		//商品总数
		orderPrice.setGoods_num(goods_num);
		
		return orderPrice;
		 
	 
	}
	
	@Override
	public List showCarts(String carplate, Integer member_id) {
		String sql = 
		"SELECT s.store_id,s.store_name,p.`name` as product_name,p.price,p.rewards_limit,g.thumbnail,c.addon,c.num,c.goods_id,c.product_id,c.cart_id,g.market_enable FROM es_store s,es_cart c,es_product p,es_goods g"
		+" WHERE s.store_id = c.store_id AND c.product_id = p.product_id AND c.goods_id = g.goods_id AND c.carplate = ? AND c.member_id = ? ORDER BY c.store_id,c.cart_id DESC" ;
		return this.daoSupport.queryForList(sql, carplate,member_id);
	}

	@Override
	public void updatePriceByProductid(Integer product_id,Double price) {
		String sql = "update es_cart set price=? where product_id=?";
		this.daoSupport.execute(sql, price,product_id);
	}
	

	@Override
	public boolean checkGoodsInCart(Integer goodsid) {
		String sql ="select count(0) from es_cart where goods_id=?";
		return this.baseDaoSupport.queryForInt(sql, goodsid)>0;
	}

	
	
	//set get 

	public IPromotionManager getPromotionManager() {
		return promotionManager;
	}
 

	public CartPluginBundle getCartPluginBundle() {
		return cartPluginBundle;
	}

	public void setCartPluginBundle(CartPluginBundle cartPluginBundle) {
		this.cartPluginBundle = cartPluginBundle;
	}

 
	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public IDlyTypeManager getDlyTypeManager() {
		return dlyTypeManager;
	}

	public void setDlyTypeManager(IDlyTypeManager dlyTypeManager) {
		this.dlyTypeManager = dlyTypeManager;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setPromotionManager(IPromotionManager promotionManager) {
		this.promotionManager = promotionManager;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	
	
}
