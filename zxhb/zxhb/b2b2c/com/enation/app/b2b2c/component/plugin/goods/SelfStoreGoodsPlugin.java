package com.enation.app.b2b2c.component.plugin.goods;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.B2b2cApp;
import com.enation.app.shop.ShopApp;
import com.enation.app.shop.core.plugin.goods.IGetGoodsAddHtmlEvent;
import com.enation.app.shop.core.plugin.goods.IGetGoodsEditHtmlEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsBeforeAddEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsBeforeEditEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsSearchFilter;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;


/**
 * 自营店商品插件<br>
 * 在商品添加时给store_id赋值
 * @author [kingapex]
 * @version [1.0]
 * @since [5.1]
 * 2015年10月17日下午8:40:43
 */
@Component
public class SelfStoreGoodsPlugin extends AutoRegisterPlugin implements IGoodsBeforeAddEvent ,IGoodsBeforeEditEvent,IGoodsSearchFilter,IGetGoodsAddHtmlEvent,IGetGoodsEditHtmlEvent{
	
	
	/**
	 * 响应商品的添加事件，为商品的store_id字段赋值<br>
	 * 判断如果是管理员在添加再执行赋值操作,以确保是自营的商品
	 */
	@Override
	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request) throws RuntimeException {
		
		AdminUser adminUser = UserConext.getCurrentAdminUser();
		if(adminUser!=null && "yes".equals( request.getParameter("self_store") )){
			
			//为商品表赋店铺名
			goods.put("store_name","平台自营");
			
			//为商品表赋自营店的id
			goods.put("store_id", ShopApp.self_storeid); 
			String goods_transfee_charge = request.getParameter("goods_transfee_charge");
			goods.put("goods_transfee_charge",StringUtil.toInt(goods_transfee_charge, 0));
			
		}
		
	}
	
	
	@Override
	public void onBeforeGoodsEdit(Map goods, HttpServletRequest request) {
		AdminUser adminUser = UserConext.getCurrentAdminUser();
		if(adminUser!=null){
			
			String goods_transfee_charge = request.getParameter("goods_transfee_charge");
			goods.put("goods_transfee_charge",StringUtil.toInt(goods_transfee_charge, 0));
			
		}
	}
	
	
	

	@Override
	public String getEditHtml(Map goods, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getAddHtml(HttpServletRequest request) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		freeMarkerPaser.putData("show_ship_free","yes");
		return "";
	}
	
	
	
	/**
	 * 拼装后台商品查询的sql中的Where部分<br>
	 * 加入自营店的店铺id过滤条件
	 */
	@Override
	public void filter(StringBuffer sql) {
		
		//判断如果是自营店的商品列表查询才执行此逻辑
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		
		//如果是传递了self_store参数 代表自营店商品查询
		String selfStore = request.getParameter("self_store");
		if(  !"yes".equals( selfStore )  ){
			return ;
		}
		
		sql.append(" AND g.store_id="+ShopApp.self_storeid);
		
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.goods.IGoodsSearchFilter#getSelector()
	 */
	@Override
	public String getSelector() {
		
		return "";
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.goods.IGoodsSearchFilter#getFrom()
	 */
	@Override
	public String getFrom() {
	 
		return "";
	}


	
	
	
}
