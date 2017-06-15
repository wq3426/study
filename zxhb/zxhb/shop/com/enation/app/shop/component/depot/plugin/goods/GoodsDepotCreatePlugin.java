package com.enation.app.shop.component.depot.plugin.goods;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.IShortMsgManager;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IRoleManager;
import com.enation.app.shop.core.model.Depot;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.plugin.goods.IGoodsAfterAddEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsDeleteEvent;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 生成商品仓库库存
 * @author kingapex
 *
 */
@Component
public class GoodsDepotCreatePlugin extends AutoRegisterPlugin implements
		IGoodsAfterAddEvent,IGoodsDeleteEvent {
	private IDepotManager depotManager;
	private IDaoSupport baseDaoSupport;
	private IShortMsgManager shortMsgManager ;
	private IAdminUserManager adminUserManager;
	private IRoleManager roleManager;
	private IProductManager productManager;
 
	@Override
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request)
			throws RuntimeException {
		Integer goodsid  = (Integer) goods.get("goods_id");
		List<Depot> depotList = this.depotManager.list();
		for(Depot depot:depotList){
			this.baseDaoSupport.execute("insert into goods_depot(goodsid,depotid,iscmpl)values(?,?,?)", goodsid,depot.getId(),0);
		}
		
		Product product=productManager.getByGoodsId(goodsid);
		Integer productid=product.getProduct_id();
				
		this.baseDaoSupport.execute("insert into es_product_store(goodsid,productid,depotid,store,enable_store)values(?,?,?,?,?)", 
									goodsid, productid, 1,Integer.valueOf("" + goods.get("store")),Integer.valueOf("" + goods.get("enable_store")));
		
	}
	 
	
	@Override
	public void onGoodsDelete(Integer[] goodsid) {
		if(goodsid==null || goodsid.length==0) return ;
		String goodsidstr = StringUtil.arrayToString(goodsid, ",");
		this.baseDaoSupport.execute("delete from goods_depot where goodsid in ("+goodsidstr +")");
	}

 
	public IDepotManager getDepotManager() {
		return depotManager;
	}

	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}


	public IDaoSupport getBaseDaoSupport() {
		return baseDaoSupport;
	}


	public void setBaseDaoSupport(IDaoSupport baseDaoSupport) {
		this.baseDaoSupport = baseDaoSupport;
	}


	public IShortMsgManager getShortMsgManager() {
		return shortMsgManager;
	}

	public void setShortMsgManager(IShortMsgManager shortMsgManager) {
		this.shortMsgManager = shortMsgManager;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public IRoleManager getRoleManager() {
		return roleManager;
	}

	public void setRoleManager(IRoleManager roleManager) {
		this.roleManager = roleManager;
	}


	public IProductManager getProductManager() {
		return productManager;
	}


	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

}
