package com.enation.app.b2b2c.core.service.store.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.component.plugin.store.StorePluginBundle;
import com.enation.app.b2b2c.core.model.RepairTimeRegion;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.b2b2c.core.service.saleType.ISaleTypeManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.b2b2c.core.service.store.IStoreSildeManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MerchantInfo;
import com.enation.app.base.core.model.Store;
import com.enation.app.base.core.model.StoreAudit;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.model.CarModel;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.ValidateUtils;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.ExcelUtils;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
@Component
public class StoreManager  extends BaseSupport implements IStoreManager{
	private IStoreMemberManager storeMemberManager;
	private ISaleTypeManager saleTypeManager;
	private IStoreSildeManager storeSildeManager;
	private IRegionsManager regionsManager;
	private StorePluginBundle storePluginBundle;
	private IMemberManager memberManager;
	private IReckoningManager reckoningManager;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#apply(com.enation.app.b2b2c.core.model.Store)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void apply(Store store) {
		//获取当前用户信息
		Member member=storeMemberManager.getStoreMember();
		if (member != null) {
			store.setMember_id(member.getMember_id());
			store.setMember_name(member.getUsername());
		}
		if(StringUtil.isEmpty(store.getId_img())){
			store.setId_img(null);
		}if(StringUtil.isEmpty(store.getLicense_img())){
			store.setLicense_img(null);
		}
		this.getStoreRegions(store);
		this.daoSupport.insert("es_store", store);
		store.setStore_id(this.daoSupport.getLastId("es_store"));
		storePluginBundle.onAfterApply(store);
	}
	/**
	 * 获取店铺地址
	 * @param store
	 */
	private void getStoreRegions(Store store){
		store.setStore_province(regionsManager.get(store.getStore_provinceid()).getLocal_name());
		store.setStore_city(regionsManager.get(store.getStore_cityid()).getLocal_name());
		store.setStore_region(regionsManager.get(store.getStore_regionid()).getLocal_name());
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#audit_pass(java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void audit_pass(Integer member_id,Integer storeId,Integer pass,Integer name_auth,Integer store_auth,Double commission) {
		if(pass==1){
			store_auth=store_auth==null?0:store_auth;
			name_auth=name_auth==null?0:name_auth;
			this.daoSupport.execute("update es_store set create_time=?,name_auth=?,store_auth=?,commission=? where store_id=?",DateUtil.getDateline(),name_auth,store_auth,commission, storeId);
			this.editStoredis(1, storeId);
			storePluginBundle.onAfterPass(this.getStore(storeId));
		}else{
			//审核未通过
			this.daoSupport.execute("update es_store set disabled=? where store_id=?",-1,storeId);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#store_list(java.util.Map, int, int)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Page store_list(Map other,Integer disabled,int pageNo,int pageSize) {
		/*StringBuffer sql = new StringBuffer("");
		disabled = disabled == null ? 1 : disabled;
		String storeName = other.get("name") == null ? "" : other.get("name")
				.toString();*/
		/*
		String store_name = other.get("name") == null ? "" : other.get("name")
				.toString();
		String searchType = other.get("searchType") == null ? "" : other.get(
				"searchType").toString();
		
		String recently = "select count(0) from es_order eo where eo.store_id = s.store_id and status = 7 and create_time between "+((new Date().getTime()/1000)-7776000)+" and " +new Date().getTime()/1000;
		
		// 店铺状态
		if (disabled.equals(-2)) {
			sql.append("select ("+recently+") recently , s.* from es_store s   where  disabled!="//添加 查询到的列 recently 最近成交量
					+ disabled);
		} else {
			sql.append("select ("+recently+") recently ,s.* from es_store s   where  disabled="//添加 查询到的列 recently 最近成交量
					+ disabled);
		}
		
		//将自营店铺信息在后台店铺列表中去除 add by DMRain 2016-1-26
//		sql.append(" and s.store_id != 1");
		
		if (!StringUtil.isEmpty(store_name)) {
			sql.append("  and s.store_name like '%" + store_name + "%'");
		}
		if (!StringUtil.isEmpty(searchType) && !searchType.equals("default")) {
			sql.append(" order by " + searchType + " desc");
		} else {
			sql.append(" order by store_id" + " desc");
		}
		return this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);*/
		
		disabled = disabled == null ? 1 : disabled;
		String storeName = other.get("name") == null ? "" : other.get("name")
				.toString();
		
		//拼装sql
		StringBuffer sql = new StringBuffer();
		sql.append(                                                                           
			" SELECT                                                                          "+
			" 	t.store_id,                                                                   "+
			" 	t.member_name,                                                                "+
			"     t.store_name,                                                               "+
			" 	t.customer_phone,															  "+				
			" 	CASE                                                                          "+
			" 		WHEN t.disabled = 0 THEN '关闭'                                            "+
			" 		WHEN t.disabled = 1 THEN '开启'                                            "+
			" 	END as disabled,                                                              "+
			"     CASE                                                                        "+
			" 		WHEN t.auditstatus = '0' THEN '待修改'                                     "+
			" 		WHEN t.auditstatus = '1' THEN '待审核'                                     "+
			" 		WHEN t.auditstatus = '2' THEN '已审核'                                     "+
			" 	END as auditstatus,                                                           "+
			" 	CASE                                                                          "+
			" 		WHEN t1.audit_result = '0' THEN '驳回'                                    "+
			" 		WHEN t1.audit_result = '1' THEN '通过'                                    "+
			" 	END as auditresult                                                            "+
			" FROM                                                                            "+
			" 	es_store t LEFT JOIN es_store_audit t1 ON t.store_id = t1.store_id            "+                
			" WHERE 1=1                                                                       "+
			" AND t.disabled = "+ disabled +"												  ");

		if(!StringUtil.isEmpty(storeName)) {
			sql.append(" AND t.store_name like '%" + storeName.trim() + "%'");
		}
	
		sql.append (" ORDER BY t.store_id");
		Page storePage = this.daoSupport.queryForPage(sql.toString(),pageNo, pageSize);
		return storePage;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#disStore(java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void disStore(Integer storeId) {
		//关闭店铺
		this.daoSupport.execute("update es_store set  end_time=? where store_id=?",DateUtil.getDateline(), storeId);
		this.editStoredis(2, storeId);
		//修改会员店铺状态
		this.daoSupport.execute("update es_member set is_store=? where member_id=?",3,this.getStore(storeId).getMember_id());
		//更高店铺商品状态
		this.daoSupport.execute("update es_goods set disabled=? where store_id=?", 1,storeId);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#useStore(java.lang.Integer)
	 */
	@Override
	public void useStore(Integer storeId) {
		this.editStoredis(1, storeId);
		this.daoSupport.execute("update es_member set is_store="+1+" where member_id="+this.getStore(storeId).getMember_id());
		//更高店铺商品状态
		this.daoSupport.execute("update es_goods set disabled=? where store_id=?", 0,storeId);
	}
	/**
	 * 更改店铺状态
	 * @param disabled
	 * @param store_id
	 */
	private void editStoredis(Integer disabled,Integer store_id){
		this.daoSupport.execute("update es_store set disabled=? where store_id=?",disabled,store_id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#getStore(java.lang.Integer)
	 */
	@Override
	public Store getStore(Integer storeId) {
		String sql="select * from es_store where store_id="+storeId;
		List<Store> list = this.baseDaoSupport.queryForList(sql,Store.class);
		
		return (Store) list.get(0);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#editStore(com.enation.app.b2b2c.core.model.store.Store)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void editStore(Store store){
		StoreMember member=storeMemberManager.getStoreMember();
		this.daoSupport.update("es_store", store, "store_id="+member.getStore_id());
		if(store.getDisabled()==1){
			this.daoSupport.execute("update  es_member set is_store=1 where member_id=?",member.getMember_id() );
		}else{
			this.daoSupport.execute("update  es_member set is_store=2 where member_id=?",member.getMember_id() );
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#editStoreInfo(com.enation.app.b2b2c.core.model.store.Store)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void editStoreInfo(Store store){
		 this.daoSupport.update("es_store", store, " store_id="+store.getStore_id());
	}
	 public void editStore(Map store) {
		 this.daoSupport.update("es_store", store, " store_id="+store.get("store_id"));
	 }
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#checkStore()
	 */
	@Override
	public boolean checkStore() {
		Member member=storeMemberManager.getStoreMember();
		String sql="select count(store_id) from es_store where member_id=?";
		int isHas= this.daoSupport.queryForInt(sql, member.getMember_id());
		if(isHas>0)
			return true;
		else
			return false;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#save(com.enation.app.b2b2c.core.model.Store)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(Store store) {
		store.setMember_id(this.storeMemberManager.getMember(store.getMember_name()).getMember_id());
		store.setCreate_time(DateUtil.getDateline());
		this.daoSupport.insert("es_store", store);
		store.setStore_id(this.daoSupport.getLastId("es_store"));
		this.addStoreBalance(store);
		storePluginBundle.onAfterPass(store);
	}
	
	/**
	 * 注册商店，同事注册会员
	 * Navy Xue
	 * 2015-07-03
	 */
	public void registStore(Store store, Member member) {
		// 保存会员
//		this.memberManager.add(member);
		store.setMember_name(member.getUsername());
		//暂时先将店铺等级定为默认等级，以后版本升级更改此处
		store.setStore_level(1);
		// 保存商店
		save(store);
		// 申请开店
		String sql="update es_member set is_store=1,store_id=? where member_id=?";
		daoSupport.execute(sql, store.getStore_id(),store.getMember_id());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#checkIdNumber(java.lang.String)
	 */
	@Override
	public Integer checkIdNumber(String idNumber) {
		String sql = "select member_id from store where id_number=?";
		List result = this.baseDaoSupport.queryForList(sql, idNumber);
		return  result.size() > 0 ? 1 : 0;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#editStoreOnekey(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void editStoreOnekey(String key, String value) {
		StoreMember member=storeMemberManager.getStoreMember();
		Map map=new HashMap();
		map.put(key,value);
		this.daoSupport.update("es_store", map, "store_id="+member.getStore_id());
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#collect(java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addcollectNum(Integer storeid) {
		String sql = "update es_store set store_collect = store_collect+1 where store_id=?";
		this.baseDaoSupport.execute(sql, storeid);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#getStore(java.lang.String)
	 */
	@Override
	public boolean checkStoreName(String storeName) {
		String sql="select  count(store_id) from es_store where store_name=? and disabled=1";
		Integer count= this.daoSupport.queryForInt(sql, storeName);
		return count!=0?true:false;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#reduceCollectNum(java.lang.Integer)
	 */
	@Override
	public void reduceCollectNum(Integer storeid) {
		String sql = "update es_store set store_collect = store_collect-1 where store_id=?";
		this.baseDaoSupport.execute(sql, storeid);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#saveStoreLicense(java.lang.Integer, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveStoreLicense(Integer storeid, String id_img,
			String license_img, Integer store_auth, Integer name_auth) {
		if(store_auth==2){
			this.daoSupport.execute("update es_store set store_auth=?,license_img=? where store_id=?",store_auth,license_img,storeid);
		}
		if(name_auth==2){
			this.daoSupport.execute("update es_store set name_auth=?,id_img=? where store_id=?",name_auth,id_img,storeid);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#auth_list(java.util.Map, java.lang.String, int, int)
	 */
	@Override
	public Page auth_list(Map other, Integer disabled, int pageNo, int pageSize) {
		StringBuffer sql=new StringBuffer("select s.* from es_store s   where  disabled="+disabled);
		sql.append(" and (store_auth=2 or name_auth=2)");
		return this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#auth_pass(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void auth_pass(Integer store_id, Integer name_auth,
			Integer store_auth) {
		if(store_auth!=null){
				this.daoSupport.execute("update es_store set store_auth=? where store_id=?",store_auth,store_id);
		}
		if(name_auth!=null){
			this.daoSupport.execute("update es_store set name_auth=? where store_id=?",name_auth,store_id);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#getStoreByMember(java.lang.Integer)
	 */
	@Override
	public Store getStoreByMember(Integer memberId) {
		return (Store)this.daoSupport.queryForObject("select * from es_store where member_id=?", Store.class, memberId);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#reApply(com.enation.app.b2b2c.core.model.store.Store)
	 */
	@Override
	public void reApply(Store store) {
		//获取当前用户信息
		Member member=storeMemberManager.getStoreMember();
		if(StringUtil.isEmpty(store.getId_img())){
			store.setId_img(null);
			store.setName_auth(0);
		}if(StringUtil.isEmpty(store.getLicense_img())){
			store.setLicense_img(null);
			store.setStore_auth(0);
		}
		if (member != null) {
			store.setMember_id(member.getMember_id());
			store.setMember_name(member.getUsername());
		}
		this.daoSupport.update("es_store", store, "store_id="+store.getStore_id());
		storePluginBundle.onAfterApply(store);
	}
	
	
	
	@Override
	public Store getSignStore(String carplate) {
		try{
			String sql = "select *from es_store where store_id = (select repair4sstoreid from es_carinfo where carplate = ? )";
			Store store = (Store) daoSupport.queryForObject(sql, Store.class, carplate);
			return store;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateBalance(String sn,double trade_money,Integer store_id) {
		try{
			daoSupport.execute("update es_store_balance set balance = balance+? where store_id = ?",trade_money,store_id);
			reckoningManager.saveStageno(sn, store_id);
		}catch(Exception e){
			throw e;
		}
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateBalance(double trade_money,Integer store_id) {
		try{
			daoSupport.execute("update es_store_balance set balance = balance+? where store_id = ?",trade_money,store_id);
		}catch(Exception e){
			throw e;
		}
	}
	@Override
	public void addStoreBalance(Store store) {
		try{
			Map map = new HashMap<>();
			map.put("store_id", store.getStore_id());
			map.put("balance", 0.0);
			daoSupport.insert("es_store_balance", map);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public double getBalance(Integer store_id) {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append( " select *from es_store_balance where store_id = ?");
 			Map map = daoSupport.queryForMap(sql.toString(), store_id);
 			if(map != null){
 				return (double)map.get("balance");
 			}
		}catch(Exception e){
			return 0;
		}
		return 0;
	}
	
	@Override
	public void editStoreUseData(Store store) {
		 String sql = "update es_store_cost set level_id="+store.getStore_level()+" where store_id = "+store.getStore_id();
		 this.daoSupport.execute(sql);
	}
	@Override
	public List getStoreCarRepairTimeList(Integer store_id) {
		try {
			String sql = "select * from es_repair_timeregion where store_id=? order by starttime";
			return daoSupport.queryForList(sql, store_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean isExistRegion(RepairTimeRegion repairTimeRegion) {
		try {
			String sql = "select count(*) from es_repair_timeregion where store_id=? and starttime=? and endtime=?";
			int count = daoSupport.queryForInt(sql, repairTimeRegion.getStore_id(), repairTimeRegion.getStarttime(), repairTimeRegion.getEndtime());
			if(count > 0){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject addRepairTimeRegion(RepairTimeRegion repairTimeRegion) {
		JSONObject obj = new JSONObject();
		try {
			daoSupport.insert("es_repair_timeregion", repairTimeRegion);
			
			obj.put("result", 1);
			obj.put("message", "添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return obj;
	}
	
	@Override
	public RepairTimeRegion getRepairTimeRegion(String id) {
		try {
			String sql = "select * from es_repair_timeregion where time_region_id=?";
			List<RepairTimeRegion> list = daoSupport.queryForList(sql, RepairTimeRegion.class, id);
			
			if(list.size() > 0){
				return list.get(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject editRepairTimeRegionSave(RepairTimeRegion repairTimeRegion, String repair_time_range) {
		JSONObject obj = new JSONObject();
		try {
			if(repairTimeRegion != null){
				String sql = "update es_repair_timeregion set starttime=?, endtime=?, station=?, ratio=? where time_region_id=?";
				daoSupport.execute(sql, repairTimeRegion.getStarttime(), repairTimeRegion.getEndtime(), repairTimeRegion.getStation(), repairTimeRegion.getRatio(), repairTimeRegion.getTime_region_id());
			}
			
			//修改保养预约时间范围
			if(!StringUtil.isNull(repair_time_range)){
				String sql = "UPDATE es_store SET repair_time_range=? WHERE store_id = ?";
				daoSupport.execute(sql, repair_time_range, ((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id());
			}
			
			obj.put("result", 1);
			obj.put("message", "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return obj;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject delRepairTimeRegion(String id) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "delete from es_repair_timeregion where time_region_id=?";
			daoSupport.execute(sql, id);
			
			obj.put("result", 1);
			obj.put("message", "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return obj;
	}
	
	public Integer getRepairItemId(String itemname){
		String sql = "select id from es_repair_items where itemname=?";
		return daoSupport.queryForInt(sql, itemname);
	}
	
	public List<Brand> getCarBrandSelectList() {
		String sql = "SELECT NAME, brand_id FROM es_brand WHERE TYPE=0";
		List<Brand> brandList = daoSupport.queryForList(sql, Brand.class);
		
		return brandList;
	}
	
	public long[] getIntervalAndIntervalTime(String temp){
		long[] array = new long[2];
		if(!StringUtil.isEmpty(temp)){
			if(temp.indexOf(";") > 0){
				String[] tempArray = temp.split(";");
				array[0] = Long.valueOf(tempArray[0]);
				array[1] = Long.valueOf(tempArray[1]);
			}
		}
		return array;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject importStoreRepairItem(File data, String dataFileName, int store_id) throws Exception {
		JSONObject obj = new JSONObject();
		try {
			Map<String, Integer> map = new HashMap<>();
			List<Brand> brandList = getCarBrandSelectList();
			if(brandList.size() > 0){
				for(Brand b : brandList){
					map.put(b.getName(), b.getBrand_id());
				}
			}
			
			String msg = "";
			ExcelUtils eu = new ExcelUtils();
			int sheetCount = eu.getSheetNumber(new FileInputStream(data), ExcelUtils.isExcel2003(dataFileName));
			if(sheetCount > 0){
				for(int i=0; i<sheetCount; i++){
					//获取保养价格表格的行索引
					int priceRowIndex = eu.getRowIndexOfCellvalue(new FileInputStream(data), ExcelUtils.isExcel2003(dataFileName), i, "项目");
					
					List<List<String>> list = eu.read(new FileInputStream(data), ExcelUtils.isExcel2003(dataFileName), i);
					if(list.size() > 0){
						int oilId = getRepairItemId("机油");
						int oilfilterId = getRepairItemId("机油滤清器");
						int airfilterId = getRepairItemId("空气滤清器");
						int fuelfilterId = getRepairItemId("燃油滤清器");
						int cabinfilterId = getRepairItemId("空调滤清器");
						int sparkplugId = getRepairItemId("火花塞");
						int brakeoilId = getRepairItemId("刹车油");
						int transoilId = getRepairItemId("变速箱油");
						int coolingliquidId = getRepairItemId("防冻冷却液");
						int cvvtId = getRepairItemId("正时系统");
						
						List<String> titleObject = list.get(0);
						int capitalIndex = titleObject.indexOf("首字母");
						int brandIndex = titleObject.indexOf("品牌");
						int seriesIndex = titleObject.indexOf("车系");
						int typeIndex = titleObject.indexOf("类型");
						int nkIndex = titleObject.indexOf("年款");
						int salesnameIndex = titleObject.indexOf("车型销售名称");
						int gearboxIndex = titleObject.indexOf("变速箱");
						int seatsIndex = titleObject.indexOf("座位数(个)");
						int dischargeIndex = titleObject.indexOf("排量");
						int priceIndex = titleObject.indexOf("指导价格(元)");
						
						int oilIndex = titleObject.indexOf("机油");
						int oilfilterIndex = titleObject.indexOf("机油滤清器");
						int airfilterIndex = titleObject.indexOf("空气滤清器");
						int fuelfilterIndex = titleObject.indexOf("燃油滤清器");
						int cabinfilterIndex = titleObject.indexOf("空调滤清器");
						int sparkplugIndex = titleObject.indexOf("火花塞");
						int brakeoilIndex = titleObject.indexOf("刹车油");
						int transoilIndex = titleObject.indexOf("变速箱油");
						int coolingliquidIndex = titleObject.indexOf("防冻冷却液");
						int cvvtIndex = titleObject.indexOf("正时系统");
						
						if(capitalIndex >= 0 && brandIndex >= 0 && seriesIndex >= 0 && typeIndex >= 0 && nkIndex >= 0 && salesnameIndex >= 0 && dischargeIndex >= 0 && oilIndex >= 0 && oilfilterIndex >= 0){
							double oil_item_price = 0.0;//机油价格
							double oil_repair_price = 0.0;//机油保养价格
							double oilfilter_item_price = 0.0;//机油滤清器价格
							double oilfilter_repair_price = 0.0;
							double airfilter_item_price = 0.0;//空调滤清器价格
							double airfilter_repair_price = 0.0;
							double fuelfilter_item_price = 0.0;//空气滤清器价格
							double fuelfilter_repair_price = 0.0;
							double cabinfilter_item_price = 0.0;//燃油滤清器价格
							double cabinfilter_repair_price = 0.0;
							double sparkplug_item_price = 0.0;//火花塞价格
							double sparkplug_repair_price = 0.0;
							double brakeoil_item_price = 0.0;//刹车油价格
							double brakeoil_repair_price = 0.0;
							double transoil_item_price = 0.0;//变速箱油价格
							double transoil_repair_price = 0.0;
							double coolingliquid_item_price = 0.0;//防冻冷却液价格
							double coolingliquid_repair_price = 0.0;
							double cvvt_item_price = 0.0;//正时系统价格
							double cvvt_repair_price = 0.0;
							
							for(int j=priceRowIndex+1; j<list.size(); j++){
								List<String> rowObj = list.get(j);
								String itemname = rowObj.get(0);
								String item_price = rowObj.get(1);
								String repair_price = rowObj.get(2);
								
								if("机油".equals(itemname)){
									oil_item_price = "".equals(item_price) ? 0.0 : Double.valueOf(item_price);
									oil_repair_price = "".equals(repair_price) ? 0.0 : Double.valueOf(repair_price);
								}
                                if("机油滤清器".equals(itemname)){
                                	oilfilter_item_price = "".equals(item_price) ? 0.0 : Double.valueOf(item_price);
                                	oilfilter_repair_price = "".equals(repair_price) ? 0.0 : Double.valueOf(repair_price);
								}
                                if("空调滤清器".equals(itemname)){
                                	airfilter_item_price = "".equals(item_price) ? 0.0 : Double.valueOf(item_price);
                                	airfilter_repair_price = "".equals(repair_price) ? 0.0 : Double.valueOf(repair_price);
								}
                                if("空气滤清器".equals(itemname)){
                                	fuelfilter_item_price = "".equals(item_price) ? 0.0 : Double.valueOf(item_price);
                                	fuelfilter_repair_price = "".equals(repair_price) ? 0.0 : Double.valueOf(repair_price);
                                }
                                if("燃油滤清器".equals(itemname)){
                                	cabinfilter_item_price = "".equals(item_price) ? 0.0 : Double.valueOf(item_price);
                                	cabinfilter_repair_price = "".equals(repair_price) ? 0.0 : Double.valueOf(repair_price);
                                }
                                if("火花塞".equals(itemname)){
                                	sparkplug_item_price = "".equals(item_price) ? 0.0 : Double.valueOf(item_price);
                                	sparkplug_repair_price = "".equals(repair_price) ? 0.0 : Double.valueOf(repair_price);
                                }
                                if("刹车油".equals(itemname)){
                                	brakeoil_item_price = "".equals(item_price) ? 0.0 : Double.valueOf(item_price);
                                	brakeoil_repair_price = "".equals(repair_price) ? 0.0 : Double.valueOf(repair_price);
                                }
                                if("变速箱油".equals(itemname)){
                                	transoil_item_price = "".equals(item_price) ? 0.0 : Double.valueOf(item_price);
                                	transoil_repair_price = "".equals(repair_price) ? 0.0 : Double.valueOf(repair_price);
                                }
                                if("防冻冷却液".equals(itemname)){
                                	coolingliquid_item_price = "".equals(item_price) ? 0.0 : Double.valueOf(item_price);
                                	coolingliquid_repair_price = "".equals(repair_price) ? 0.0 : Double.valueOf(repair_price);
                                }
                                if("正时系统".equals(itemname)){
                                	cvvt_item_price = "".equals(item_price) ? 0.0 : Double.valueOf(item_price);
                                	cvvt_repair_price = "".equals(repair_price) ? 0.0 : Double.valueOf(repair_price);
                                }
							}
							
							for(int j=1; j<priceRowIndex - 1; j++){
								List<String> rowObj = list.get(j);
								
								long[] oilArray = getIntervalAndIntervalTime(rowObj.get(oilIndex));
								long[] oilfilterArray = getIntervalAndIntervalTime(rowObj.get(oilfilterIndex));
								long[] airfilterArray = getIntervalAndIntervalTime(rowObj.get(airfilterIndex));
								long[] fuelfilterArray = getIntervalAndIntervalTime(rowObj.get(fuelfilterIndex));
								long[] cabinfilterArray = getIntervalAndIntervalTime(rowObj.get(cabinfilterIndex));
								long[] sparkplugArray = getIntervalAndIntervalTime(rowObj.get(sparkplugIndex));
								long[] brakeoilArray = getIntervalAndIntervalTime(rowObj.get(brakeoilIndex));
								long[] transoilArray = getIntervalAndIntervalTime(rowObj.get(transoilIndex));
								long[] coolingliquidArray = getIntervalAndIntervalTime(rowObj.get(coolingliquidIndex));
								long[] cvvtArray = getIntervalAndIntervalTime(rowObj.get(cvvtIndex));
								
								if(StringUtil.isEmpty(rowObj.get(capitalIndex))){//如果该行数据为空，则跳过该行开始下一行数据的读取
									continue;
								}
								
								CarModel carmodel = new CarModel();
								carmodel.setCapital(rowObj.get(capitalIndex).toUpperCase());
								carmodel.setBrand(rowObj.get(brandIndex));
								if(map.containsKey(carmodel.getBrand())){
									carmodel.setBrand_id(map.get(carmodel.getBrand()));
								}
								carmodel.setSeries(rowObj.get(seriesIndex));
								carmodel.setType(rowObj.get(typeIndex));
								carmodel.setNk(rowObj.get(nkIndex));
								carmodel.setSales_name(rowObj.get(salesnameIndex));
								carmodel.setGearboxtype(rowObj.get(gearboxIndex).indexOf("手动") > 0 ? 1 : 2);
								carmodel.setDischarge(rowObj.get(dischargeIndex));
								int seats = StringUtil.isEmpty(rowObj.get(seatsIndex)) ? 0 : (int)Math.floor(Double.valueOf(rowObj.get(seatsIndex)));
								carmodel.setSeats(seats);
								double price = 0.0;
								String carPrice = rowObj.get(priceIndex);
								if(!StringUtil.isEmpty(carPrice)){
									price = Double.valueOf(carPrice.substring(0, carPrice.indexOf("万")));
								}
								carmodel.setPrice(price);
								carmodel.setRepairinterval(oilArray[0]);
								carmodel.setRepairintervaltime(oilArray[1]);
								
								//更新车型信息
								String sql = "select * from es_carmodels where brand=? and series=? and type=? and nk=? and sales_name=?";
								List carmodelList = daoSupport.queryForList(sql, carmodel.getBrand(), carmodel.getSeries(), carmodel.getType(), carmodel.getNk(), carmodel.getSales_name());
								if(carmodelList.size() > 0){//如果车型存在，更新车型信息，否则添加到车型表
									JSONObject carobj = JSONArray.fromObject(carmodelList).getJSONObject(0);
									carmodel.setId(carobj.getInt("id"));
									sql = "update es_carmodels set seats=?, discharge=?, gearboxtype=?, price=?, repairinterval=?, repairintervaltime=? where id=?";
									daoSupport.execute(sql, carmodel.getSeats(), carmodel.getDischarge(), carmodel.getGearboxtype(), carmodel.getPrice(), carmodel.getRepairinterval(), carmodel.getRepairintervaltime(), carmodel.getId());
								}else{
									daoSupport.insert("es_carmodels", carmodel);
									carmodel.setId(daoSupport.getLastId("es_carmodels"));
								}
								
								//更新车型保养信息
								String sql1 = "select count(*) from es_carmodel_repair_items where carmodel_id=? and repair_item_id=?";
					            String sql2 = "update es_carmodel_repair_items set repair_interval=?, repair_interval_time=? where carmodel_id=? and repair_item_id=?";
								String sql3 = "insert into es_carmodel_repair_items set repair_interval=?, repair_interval_time=?, carmodel_id=?, repair_item_id=?";
								
								int oilCount = daoSupport.queryForInt(sql1, carmodel.getId(), oilId);
								int oilfilterCount = daoSupport.queryForInt(sql1, carmodel.getId(), oilfilterId);
								int airfilterCount = daoSupport.queryForInt(sql1, carmodel.getId(), airfilterId);
								int fuelfilterCount = daoSupport.queryForInt(sql1, carmodel.getId(), fuelfilterId);
								int cabinfilterCount = daoSupport.queryForInt(sql1, carmodel.getId(), cabinfilterId);
								int sparkplugCount = daoSupport.queryForInt(sql1, carmodel.getId(), sparkplugId);
								int brakeoilCount = daoSupport.queryForInt(sql1, carmodel.getId(), brakeoilId);
								int transoilCount = daoSupport.queryForInt(sql1, carmodel.getId(), transoilId);
								int coolingliquidCount = daoSupport.queryForInt(sql1, carmodel.getId(), coolingliquidId);
								int cvvtCount = daoSupport.queryForInt(sql1, carmodel.getId(), cvvtId);
								
								if(oilCount > 0){
									daoSupport.execute(sql2, oilArray[0], oilArray[1], carmodel.getId(), oilId);
								}else{
									daoSupport.execute(sql3, oilArray[0], oilArray[1], carmodel.getId(), oilId);
								}
								if(oilfilterCount > 0){
									daoSupport.execute(sql2, oilfilterArray[0], oilfilterArray[1], carmodel.getId(), oilfilterId);
								}else{
									daoSupport.execute(sql3, oilfilterArray[0], oilfilterArray[1], carmodel.getId(), oilfilterId);
								}
								if(airfilterCount > 0){
									daoSupport.execute(sql2, airfilterArray[0], airfilterArray[1], carmodel.getId(), airfilterId);
								}else{
									daoSupport.execute(sql3, airfilterArray[0], airfilterArray[1], carmodel.getId(), airfilterId);
								}
								if(fuelfilterCount > 0){
									daoSupport.execute(sql2, fuelfilterArray[0], fuelfilterArray[1], carmodel.getId(), fuelfilterId);
								}else{
									daoSupport.execute(sql3, fuelfilterArray[0], fuelfilterArray[1], carmodel.getId(), fuelfilterId);
								}
								if(cabinfilterCount > 0){
									daoSupport.execute(sql2, cabinfilterArray[0], cabinfilterArray[1], carmodel.getId(), cabinfilterId);
								}else{
									daoSupport.execute(sql3, cabinfilterArray[0], cabinfilterArray[1], carmodel.getId(), cabinfilterId);
								}
								if(sparkplugCount > 0){
									daoSupport.execute(sql2, sparkplugArray[0], sparkplugArray[1], carmodel.getId(), sparkplugId);
								}else{
									daoSupport.execute(sql3, sparkplugArray[0], sparkplugArray[1], carmodel.getId(), sparkplugId);
								}
								if(brakeoilCount > 0){
									daoSupport.execute(sql2, brakeoilArray[0], brakeoilArray[1], carmodel.getId(), brakeoilId);
								}else{
									daoSupport.execute(sql3, brakeoilArray[0], brakeoilArray[1], carmodel.getId(), brakeoilId);
								}
								if(transoilCount > 0){
									daoSupport.execute(sql2, transoilArray[0], transoilArray[1], carmodel.getId(), transoilId);
								}else{
									daoSupport.execute(sql3, transoilArray[0], transoilArray[1], carmodel.getId(), transoilId);
								}
								if(coolingliquidCount > 0){
									daoSupport.execute(sql2, coolingliquidArray[0], coolingliquidArray[1], carmodel.getId(), coolingliquidId);
								}else{
									daoSupport.execute(sql3, coolingliquidArray[0], coolingliquidArray[1], carmodel.getId(), coolingliquidId);
								}
								if(cvvtCount > 0){
									daoSupport.execute(sql2, cvvtArray[0], cvvtArray[1], carmodel.getId(), cvvtId);
								}else{
									daoSupport.execute(sql3, cvvtArray[0], cvvtArray[1], carmodel.getId(), cvvtId);
								}
								
								//录入店铺保养项目价格
								sql1 = "select count(*) from es_store_repairitem where carmodel_id=? and repair_item_id=? and store_id=?";
								sql2 = "update es_store_repairitem set item_price=?, repair_price=? where store_id=? and carmodel_id=? and repair_item_id=?";
								sql3 = "insert into es_store_repairitem set item_price=?, repair_price=?, store_id=?, carmodel_id=?, repair_item_id=?";
								
								oilCount = daoSupport.queryForInt(sql1, carmodel.getId(), oilId, store_id);
								oilfilterCount = daoSupport.queryForInt(sql1, carmodel.getId(), oilfilterId, store_id);
								airfilterCount = daoSupport.queryForInt(sql1, carmodel.getId(), airfilterId, store_id);
								fuelfilterCount = daoSupport.queryForInt(sql1, carmodel.getId(), fuelfilterId, store_id);
								cabinfilterCount = daoSupport.queryForInt(sql1, carmodel.getId(), cabinfilterId, store_id);
								sparkplugCount = daoSupport.queryForInt(sql1, carmodel.getId(), sparkplugId, store_id);
								brakeoilCount = daoSupport.queryForInt(sql1, carmodel.getId(), brakeoilId, store_id);
								transoilCount = daoSupport.queryForInt(sql1, carmodel.getId(), transoilId, store_id);
								coolingliquidCount = daoSupport.queryForInt(sql1, carmodel.getId(), coolingliquidId, store_id);
								cvvtCount = daoSupport.queryForInt(sql1, carmodel.getId(), cvvtId, store_id);
								
								if(oilCount > 0){
									daoSupport.execute(sql2, oil_item_price, oil_repair_price, store_id, carmodel.getId(), oilId);
								}else{
									daoSupport.execute(sql3, oil_item_price, oil_repair_price, store_id, carmodel.getId(), oilId);
								}
								if(oilfilterCount > 0){
									daoSupport.execute(sql2, oilfilter_item_price, oilfilter_repair_price, store_id, carmodel.getId(), oilfilterId);
								}else{
									daoSupport.execute(sql3, oilfilter_item_price, oilfilter_repair_price, store_id, carmodel.getId(), oilfilterId);
								}
								if(airfilterCount > 0){
									daoSupport.execute(sql2, airfilter_item_price, airfilter_repair_price, store_id, carmodel.getId(), airfilterId);
								}else{
									daoSupport.execute(sql3, airfilter_item_price, airfilter_repair_price, store_id, carmodel.getId(), airfilterId);
								}
								if(fuelfilterCount > 0){
									daoSupport.execute(sql2, fuelfilter_item_price, fuelfilter_repair_price, store_id, carmodel.getId(), fuelfilterId);
								}else{
									daoSupport.execute(sql3, fuelfilter_item_price, fuelfilter_repair_price, store_id, carmodel.getId(), fuelfilterId);
								}
								if(cabinfilterCount > 0){
									daoSupport.execute(sql2, cabinfilter_item_price, cabinfilter_repair_price, store_id, carmodel.getId(), cabinfilterId);
								}else{
									daoSupport.execute(sql3, cabinfilter_item_price, cabinfilter_repair_price, store_id, carmodel.getId(), cabinfilterId);
								}
								if(sparkplugCount > 0){
									daoSupport.execute(sql2, sparkplug_item_price, sparkplug_repair_price, store_id, carmodel.getId(), sparkplugId);
								}else{
									daoSupport.execute(sql3, sparkplug_item_price, sparkplug_repair_price, store_id, carmodel.getId(), sparkplugId);
								}
								if(brakeoilCount > 0){
									daoSupport.execute(sql2, brakeoil_item_price, brakeoil_repair_price, store_id, carmodel.getId(), brakeoilId);
								}else{
									daoSupport.execute(sql3, brakeoil_item_price, brakeoil_repair_price, store_id, carmodel.getId(), brakeoilId);
								}
								if(transoilCount > 0){
									daoSupport.execute(sql2, transoil_item_price, transoil_repair_price, store_id, carmodel.getId(), transoilId);
								}else{
									daoSupport.execute(sql3, transoil_item_price, transoil_repair_price, store_id, carmodel.getId(), transoilId);
								}
								if(coolingliquidCount > 0){
									daoSupport.execute(sql2, coolingliquid_item_price, coolingliquid_repair_price, store_id, carmodel.getId(), coolingliquidId);
								}else{
									daoSupport.execute(sql3, coolingliquid_item_price, coolingliquid_repair_price, store_id, carmodel.getId(), coolingliquidId);
								}
								if(cvvtCount > 0){
									daoSupport.execute(sql2, cvvt_item_price, cvvt_repair_price, store_id, carmodel.getId(), cvvtId);
								}else{
									daoSupport.execute(sql3, cvvt_item_price, cvvt_repair_price, store_id, carmodel.getId(), cvvtId);
								}
							}
						}else{
							msg = "您选择的excel文件内容存在格式问题，请检查后重新导入";
							break;
						}
					}else{
						msg = "文件内容为空，请检查";
						break;
					}
				}
				
				if(!"".equals(msg)){
					obj.put("result", 0);
					obj.put("message", msg);
					return obj;
				}
				
				obj.put("result", 1);
				obj.put("message", "导入成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return obj;
	}
	
	@Override
	public Page storeCarModelList(int page, int pageSize, Integer store_id, Map map) {
		try {
			String series = (String) map.get("car_series");
			String nk = (String) map.get("car_nk");
			String sales_name = (String) map.get("car_sales_name");
			
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT t1.store_id, t1.carmodel_id, t2.brand, t2.series, t2.nk, t2.sales_name ");
			sql.append("FROM (SELECT store_id, carmodel_id FROM es_store_repairitem WHERE store_id=? GROUP BY carmodel_id) t1, es_carmodels t2 ");
			sql.append("WHERE t1.`carmodel_id`=t2.`id` ");
			
			if(!StringUtil.isEmpty(series)){
				sql.append("and t2.series like '%"+ series +"%' ");
			}
			if(!StringUtil.isEmpty(nk)){
				sql.append("and t2.nk like '%"+ nk +"%' ");
			}
			if(!StringUtil.isEmpty(sales_name)){
				sql.append("and t2.sales_name like '%"+ sales_name +"%' ");
			}
		
			sql.append("order by t1.carmodel_id");

			Page pageObj = this.daoSupport.queryForPage(sql.toString(), page, pageSize, store_id);
			
			return pageObj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public JSONObject storeCarRepairPriceEditList(Map map) {
		JSONObject returnObj = new JSONObject();
		try {
			String store_id = (String) map.get("store_id");
			String carmodel_id = (String) map.get("carmodel_id");
			
			String sql = "SELECT t1.id, t2.itemname, t1.item_price, t1.repair_price FROM es_store_repairitem t1, es_repair_items t2 "
					   + "WHERE t1.`repair_item_id`=t2.id AND t1.store_id=? AND t1.carmodel_id=? "
					   + "ORDER BY t1.repair_item_id";
			List store_carmodel_item_list = daoSupport.queryForList(sql, store_id, carmodel_id);
			
			returnObj.put("store_carmodel_item_list", store_carmodel_item_list);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return returnObj;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject editStoreRepairItem(String item_price, String repair_price, String id) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "update es_store_repairitem set item_price=?, repair_price=? where id=?";
			daoSupport.execute(sql, item_price, repair_price, id);
			
			obj.put("result", 1);
			obj.put("message", "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return obj;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject delStoreRepairItem(String id) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "delete from es_store_repairitem where id=?";
			daoSupport.execute(sql, id);
			
			obj.put("result", 1);
			obj.put("message", "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return obj;
	}
	
	@Override
	public JSONObject getStoreInsureCompanyInfo(Integer store_id) {
		JSONObject obj = new JSONObject();
		
		try {
			String sql = "SELECT id, company_name FROM es_insurance_company_info ORDER BY sort";
			List insure_company_list = daoSupport.queryForList(sql);
			if(insure_company_list.size() > 0){
				obj.put("insure_company_list", JSONArray.fromObject(insure_company_list));
			}
			sql = "SELECT insure_company_ids, insure_income_discount FROM es_store_insurance WHERE store_id=?";
			List store_insure_infoList = daoSupport.queryForList(sql, store_id);
			if(store_insure_infoList.size() > 0){
				JSONObject store_insure_obj = JSONObject.fromObject(store_insure_infoList.get(0));
				obj.put("insure_company_ids", store_insure_obj.getString("insure_company_ids"));
				obj.put("insure_income_discount", store_insure_obj.getString("insure_income_discount"));
				
				sql = "SELECT id, company_name FROM es_insurance_company_info WHERE id IN ("+ store_insure_obj.getString("insure_company_ids") +") ORDER BY sort";
				List store_insure_company_list = daoSupport.queryForList(sql);
				obj.put("store_insure_company_list", JSONArray.fromObject(store_insure_company_list));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return obj;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject updateStoreInsureCompanyInfo(int store_id, String company_ids, String insure_income_discount) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "SELECT COUNT(*) FROM es_store_insurance WHERE store_id=?";
			int count = daoSupport.queryForInt(sql, store_id);
			if(count > 0){
				sql = "UPDATE es_store_insurance SET ";
			}else{
				sql = "INSERT INTO es_store_insurance SET ";
			}
			
			if(!StringUtil.isEmpty(company_ids)){
				sql += "insure_company_ids='" + company_ids + "',";
			}
			if(!StringUtil.isEmpty(insure_income_discount)){
				sql += "insure_income_discount=" + insure_income_discount + ",";
			}
			sql = (sql.lastIndexOf(",") == (sql.length() - 1)) ? sql.substring(0, sql.length() - 1) : sql;

			if(count > 0){
				sql += " WHERE store_id=?";
			}else{
				sql += " ,store_id=?";
			}
			
			daoSupport.execute(sql, store_id);
			
			obj.put("result", 1);
			obj.put("message", "保存设置成功");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return obj;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String queryCashingoutPasswordByMemberName(int storeId) {
		String sql =
			" SELECT                               "+
			" 	t.cashingout_password              "+
			" FROM                                 "+
			" 	es_store t                         "+
			" WHERE 1=1                            "+
			" AND t.store_id = "+storeId+"    	   ";
			                                       
		Map<String,String> passwordMap = daoSupport.queryForMap(sql);
		if(ValidateUtils.isEmpty(passwordMap)) {
			throw new RuntimeException("数据异常");
		}
		return passwordMap.get("cashingout_password");
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public int updateCashingoutPassword(String newPassword, int storeId) {
		String sql = 
				" UPDATE es_store t            								"+
				" SET t.cashingout_password = '"+newPassword+"'         	"+
				" WHERE 1=1                     							"+
				" AND t.store_id = '"+storeId+"'          			        ";
		daoSupport.execute(sql);                
		return 1;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Page queryStoreOfUnAudit(int pageNum, int pageSize) {
		//拼装sql
		StringBuffer sql = new StringBuffer();
		sql.append(
			" SELECT                                                "+
			"   t.store_id,                                         "+
			"   t.store_name,                                       "+
			"   t.store_province,                                   "+
			"   t.store_city,                                       "+
			"   t.store_region,                                     "+
			"   t.tel,                                          	"+
			"   t.contacts_name,                                    "+
			"   t.member_name,                                      "+
			"   t.brand_name,                                       "+
			"   t.create_time                                       "+
			" FROM                                                  "+
			" 	es_store t                                          "+
			" WHERE 1=1                                             "+
			" AND t.auditstatus = '1'                               ");
		
		sql.append (" order by t.create_time desc");
		Page storePage = this.daoSupport.queryForPage(sql.toString(),pageNum, pageSize, Store.class);
		return storePage;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public int updateStoreOfpassAudit(Store store) {
		
		//获取数据
		int store_id = store.getStore_id();
		String auditstatus = store.getAuditstatus();
		String docker = store.getDocker();
		String docker_job_number = store.getDocker_job_number();
		String docker_tel = store.getDocker_tel();
		int store_level = store.getStore_level();
		Double discountcontract = store.getDiscountcontract();
		Double discountnoncontract = store.getDiscountnoncontract();
		int settlement_period = store.getSettlement_period();
		
		//拼装sql
		String sql =                                                     
				" UPDATE es_store t                                      "+
				" SET                                                    "+
				"   t.docker = '"+docker+"',                             "+
				"   t.docker_job_number = '"+docker_job_number+"',       "+
				"   t.docker_tel = '"+docker_tel+"',                     "+
				"   t.store_level = "+store_level+",                     "+
				"   t.auditstatus = "+auditstatus+",                     "+
				"   t.settlement_period = "+settlement_period+",         "+
				"   t.discountcontract = "+discountcontract+",           "+
				"   t.discountnoncontract = "+discountnoncontract+"      "+
				" WHERE 1=1                                              "+
				" AND t.store_id = "+store_id+"                          ";
		
		daoSupport.execute(sql);
		return 1;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public int saveStoreAuditResult(StoreAudit storeAudit) {
		daoSupport.insert("es_store_audit", storeAudit);
		return 1;
	}
	
	@Override
	public String queryBrandNameByBrandId(int brand) {
		String sql = "select t.name from es_brand t where 1=1 and t.brand_id = "+brand+"";
		return daoSupport.queryForString(sql);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public int updateStoreInfo(Store store) {
		daoSupport.update("es_store", store, "store_id="+store.getStore_id());
		storePluginBundle.onAfterApply(store);
		return 1;
	}
	
	@Override
	public boolean checkCashingouterPhoneIsExist(String mobile) {
		String sql= "select t.store_id from es_store t where 1=1 and t.cashingouter_phone = '"+mobile+"'";
		List list = daoSupport.queryForList(sql);
		if(ValidateUtils.isEmpty(list)) {
			return false;
		}
		return true;
	}
	
	@Override
	public boolean checkCashingouterPhoneIsCorrect(String cashingouterPhone, int store_id) {
		String sql= 
			" SELECT                                                  "+
			" 	t.store_id                                            "+
			" FROM                                                    "+
			" 	es_store t                                            "+
			" WHERE 1 = 1                                             "+
			" AND t.cashingouter_phone = '"+cashingouterPhone+"'      "+
			" AND t.store_id = "+store_id+"                           ";
			                                                       
		List list = daoSupport.queryForList(sql);                     
		if(ValidateUtils.isEmpty(list)) {
			return false;
		}
		return true;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public int modifyCashingoutPassword(Store store) {
		int store_id = store.getStore_id();
		String cashingout_password = store.getCashingout_password();
		String cashingouter_phone = store.getCashingouter_phone();
		
		String sql =                                               
				" UPDATE es_store t                                 "+
				" SET                                               "+
				"  t.cashingout_password='"+cashingout_password+"'  "+
				" WHERE 1=1                                         "+
				" AND t.store_id = "+store_id+"                     "+
				" AND t.cashingouter_phone='"+cashingouter_phone+"' ";
		daoSupport.execute(sql);
		return 1;
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public int updateStoreAuditStatus(int store_id) {
		String sql="update es_store t set t.auditstatus = '2' where 1=1 and t.store_id = "+store_id+"";
		daoSupport.execute(sql);
		return 1;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public int updateStore(Store store) {
		try{
			daoSupport.update("es_store", store, "store_id="+store.getStore_id());
			return 1;
		}catch(Exception e){
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Page queryStoreList(Map other, int disabled, int page, int pageSize) {
		
		//拼装sql
		StringBuffer sql = new StringBuffer();
		sql.append(
			" SELECT                                                                 "+
			" 	t.store_id,                                                          "+
			" 	t.member_name,                                                       "+
			" 	t.store_name,													  	 "+
			" 	CASE                                                                 "+
			" 		WHEN t.disabled = 0 THEN '关闭'                                  "+
			" 		WHEN t.disabled = 1 THEN '开启'                                  "+
			" 	END as disabled,                                                     "+
			" 	CASE                                                                 "+
			" 		WHEN t.auditstatus = '0' THEN '待修改'                           "+
			" 		WHEN t.auditstatus = '1' THEN '待审核'                           "+
			" 		WHEN t.auditstatus = '2' THEN '已审核'                           "+
			" 	END as auditstatus,                                                  "+
			" 	CASE                                                                 "+
			" 		WHEN t1.audit_result = '0' THEN '驳回'                           "+
			" 		WHEN t1.audit_result = '1' THEN '通过'                            "+
			" 	END as auditresult                                                   "+
			" FROM                                                                   "+
			" 	es_store t LEFT JOIN es_store_audit t1 ON t.store_id = t1.store_id   "+              
			" WHERE 1=1																 "+
			" AND t.t.disabled = "+disabled+""
				);

		/*if(!StringUtil.isEmpty(storeName)) {
			sql.append(" AND t.store_name like '%" + storeName.trim() + "%'");
		}*/
	
		sql.append (" ORDER BY t.store_id");
		Page storePage = this.daoSupport.queryForPage(sql.toString(),page, pageSize, MerchantInfo.class);
		return storePage;
	}
	
	@Override
	public List queryBrandList() {
		String sql = "select t.brand_id,t.name from es_brand t where 1=1 and t.type=0 and t.disabled=0";
		List brandList = daoSupport.queryForList(sql);
		return brandList;
	}
	

	@Override
	public List<Store> queryStoreList() {
		// TODO Auto-generated method stub
		String sql = "select es.store_id,es.store_name from es_store es left join es_store_audit esa ON es.store_id = esa.store_id "+
					 " where 1=1  and esa.audit_result = '1'";
		List<Store> storeList = daoSupport.queryForList(sql);
		return storeList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> queryStoreAuditResult(int store_id) {
		String sql = 
			" SELECT                        "+
			" 	t.audit_result,             "+
			" 	t.remark                    "+
			" FROM                          "+
			" 	es_store_audit t            "+
			" WHERE                         "+
			" 	1 = 1                       "+
			" AND t.store_id = "+store_id+" ";
		Map<String, String> auditResultMap = daoSupport.queryForMap(sql);
		return auditResultMap;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String,Integer> queryStoreAuditResultIsExist(int store_id) {
		String sql = "select t.id from es_store_audit t where 1=1 and t.store_id = "+store_id+"";
		List auditIdList = daoSupport.queryForList(sql);
		if(ValidateUtils.isEmpty(auditIdList)) {
			return null;
		}
		return (Map<String, Integer>) auditIdList.get(0);
	}
	
	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public int updateStoreAuditResult(StoreAudit storeAudit) {
		//获取参数
		int id = storeAudit.getId();
		int store_id = storeAudit.getStore_id();
		String audit_result = storeAudit.getAudit_result();
		String remark = storeAudit.getRemark();
		
		String sql =                                         
				"UPDATE es_store_audit t                     "+
				"SET                                         "+
				"	t.audit_result = '"+audit_result+"',     "+
				"	t.remark = '"+remark+"'                  "+
				"WHERE 1=1                                   "+
				"AND t.id = "+id+"                           "+
				"AND t.store_id = "+store_id+"               ";
		daoSupport.execute(sql);
		return 1;
	}
	
	@Override
	public String queryStoreAuditReason(int store_id) {
		String sql = "select t.remark from es_store_audit t where 1=1 and t.store_id = "+store_id+"";
		String failReason = daoSupport.queryForString(sql);
		return failReason;
	}
	
	
	
	
	/*
	 * ==================================================================
	 * GETTER AND SETTER
	 */
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
	public StorePluginBundle getStorePluginBundle() {
		return storePluginBundle;
	}
	public void setStorePluginBundle(StorePluginBundle storePluginBundle) {
		this.storePluginBundle = storePluginBundle;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	public IStoreSildeManager getStoreSildeManager() {
		return storeSildeManager;
	}

	public void setStoreSildeManager(IStoreSildeManager storeSildeManager) {
		this.storeSildeManager = storeSildeManager;
	}
	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}
	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}
	public ISaleTypeManager getSaleTypeManager() {
		return saleTypeManager;
	}
	public void setSaleTypeManager(ISaleTypeManager saleTypeManager) {
		this.saleTypeManager = saleTypeManager;
	}
	public IReckoningManager getReckoningManager() {
		return reckoningManager;
	}
	public void setReckoningManager(IReckoningManager reckoningManager) {
		this.reckoningManager = reckoningManager;
	}
	
	
	
	
	
	

}
