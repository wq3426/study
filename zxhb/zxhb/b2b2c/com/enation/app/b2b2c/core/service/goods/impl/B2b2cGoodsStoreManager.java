package com.enation.app.b2b2c.core.service.goods.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.goods.IB2b2cGoodsStoreManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.impl.PermissionConfig;
import com.enation.app.shop.ShopApp;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * b2b2c商品库存管理
 * @author [kingapex]
 * @version [1.0]
 * @since [5.1]
 * 2015年10月23日下午5:05:39
 */
@Component
public class B2b2cGoodsStoreManager implements IB2b2cGoodsStoreManager {
	
	private IPermissionManager permissionManager;
	private IDaoSupport daoSupport;
	
	/*
	 * 
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IB2b2cGoodsStoreManager#listSelfStore(java.util.Map, int, int, java.lang.String, java.lang.String)
	 */
	@Override
	public Page listSelfStore(Map map, int page, int pageSize, String sortField, String sortType) {
		
		Integer stype = (Integer) map.get("stype");
		String keyword = (String) map.get("keyword");
		String name = (String) map.get("name");
		String sn = (String) map.get("sn");
		
		int depotid  = (Integer)map.get("depotid") ;
		
		if( StringUtil.isEmpty( sortField)){
			sortField=" g.goods_id";
		}
		
		if( StringUtil.isEmpty( sortType)){
			sortType=" desc";
		}
		
		boolean isSuperAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("super_admin"));// 超级管理员权限
		boolean isDepotAdmin = this.permissionManager.checkHaveAuth(PermissionConfig.getAuthId("depot_admin"));// 库存管理权限
		
		if(!isDepotAdmin&&!isSuperAdmin){
			throw new RuntimeException("没有操作库存的权限");
		}
		StringBuffer sql = new StringBuffer();
		sql.append("select g.goods_id,g.sn,g.name,g.store,c.name cname,g.enable_store from es_goods g,es_goods_cat c where g.cat_id=c.cat_id and g.store_id="+ShopApp.self_storeid);
		
		if(stype!=null && keyword!=null){//基本搜索	
			if(stype==0){
				sql.append(" and ( g.name like '%"+keyword.trim()+"%'");
				sql.append(" or g.sn like '%"+keyword.trim()+"%')");
			}
		}else{ //高级搜索
		
			if(!StringUtil.isEmpty(name)){
				sql.append(" and g.name like '%"+name+"%'");
			}
			
			if(!StringUtil.isEmpty(sn)){
				sql.append(" and g.sn like '%"+sn+"%'");
			}
		}
		
		sql.append(" order by "+sortField+" "+sortType);
		Page webPage = this.daoSupport.queryForPage(sql.toString(), page, pageSize);
		
		List<Map>goodslist = (List<Map>) webPage.getResult();
		
		StringBuffer goodsidstr = new  StringBuffer();
		for (Map goods : goodslist) {
			Integer goodsid  = (Integer)goods.get("goods_id");
			if(goodsidstr.length()!=0){
				goodsidstr.append(",");
			}
			goodsidstr.append(goodsid);
		}
		
		if(goodsidstr.length()!=0){
			
			String ps_sql ="select ps.* from  es_product_store ps where  ps.goodsid in("+goodsidstr+") ";
			if(depotid!=0 ){
				ps_sql=ps_sql+" and depotid="+depotid;
			}else{
				//判断是否为总库存
				if(isDepotAdmin){
					AdminUser adminUser = UserConext.getCurrentAdminUser();
					String depotsql = "select d.* from es_depot d inner join es_depot_user du on du.depotid=d.id where du.userid=?";
					List<Map> depotList=this.daoSupport.queryForList(depotsql,adminUser.getUserid());
					Integer depot_id=0;
					if(depotList.size()!=0){
						for (Map map1:depotList) {
							depot_id=Integer.parseInt(map1.get("id").toString());
						}
						ps_sql=ps_sql+" and depotid="+depot_id;
					}
				}
			}
			ps_sql=ps_sql+" order by goodsid,depotid ";
			List<Map> storeList  = this.daoSupport.queryForList(ps_sql);
			
			for (Map goods : goodslist) {
				Integer goodsid  = (Integer)goods.get("goods_id");
				if(depotid!=0 ||isDepotAdmin){
					goods.put("d_store", 0);
					goods.put("enable_store", 0);
					for (Map store : storeList) {
						Integer store_goodsid  = (Integer)store.get("goodsid");
						if(store_goodsid.compareTo(goodsid)==0){
							Integer d_store = (Integer.valueOf(goods.get("d_store").toString()))+(Integer.valueOf(store.get("store").toString()));
							Integer enable_store = (Integer.valueOf(goods.get("enable_store").toString()))+(Integer.valueOf(store.get("enable_store").toString()));
							goods.put("d_store", d_store);
							goods.put("enable_store", enable_store);
						}
					}
				}else{
					goods.put("d_store", goods.get("store"));
					goods.put("enable_store", goods.get("enable_store"));
					 
				}
			}
			 
		}
		
		return webPage;
		
	}

	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	
	

}
