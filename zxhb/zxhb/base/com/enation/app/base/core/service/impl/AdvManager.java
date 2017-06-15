package com.enation.app.base.core.service.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.SaleTypeSetting;
import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.model.AdvMapper;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.app.base.core.service.IStoreCostManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.Page;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * 广告管理
 * 
 * @author 李志富 lzf<br/>
 *         2010-2-4 下午03:55:33<br/>
 *         version 1.0<br/>
 * <br/>
 * @author LiFenLong 2014-4-1;4.0版本改造,修改deadvs方法参数为integer[]
 */
public class AdvManager extends BaseSupport<Adv> implements IAdvManager {
	
	private IStoreCostManager storeCostManager;
	
	public void addAdv(Adv adv) {
		this.baseDaoSupport.insert("adv", adv);

	}

	
	public void delAdvs(Integer[] ids) {
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete from adv where aid in (" + id_str
				+ ")";
		this.baseDaoSupport.execute(sql);
	}

	public void delAdv(Integer aid) {
		try {
			Adv adv = this.baseDaoSupport.queryForObject("select * from adv where aid = ?", Adv.class, aid);
			String pic  = adv.getAtturl();
			if(pic!=null){
				String file_path = SystemSetting.getFile_path();
				String file = pic.replace(EopSetting.FILE_STORE_PREFIX+"/files", file_path);
				FileUtil.delete(file);
			}
			
			String sql = "delete from adv where aid = ? ";
			this.baseDaoSupport.execute(sql,aid);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	public Adv getAdvDetail(Long advid) {
		Adv adv = this.baseDaoSupport.queryForObject("select * from adv where aid = ?", Adv.class, advid);
		String pic  = adv.getAtturl();
		if(pic!=null){
			pic  =UploadUtil.replacePath(pic); 
			adv.setAtturl(pic);
		}
		return adv;
	}
	
	@Override
	public Adv getAdv(Integer aid) {
		Adv adv = this.baseDaoSupport.queryForObject("select * from adv where aid = ?", Adv.class, aid);
		return adv;
	}

	
	public Page pageAdv(String order, int page, int pageSize) {
		order = order == null ? " aid desc" : order;
		String sql = "select v.*, c.cname   cname from " + this.getTableName("adv") + " v left join " + this.getTableName("adcolumn") + " c on c.acid = v.acid";
		sql += " order by " + order; 
		Page rpage = this.daoSupport.queryForPage(sql, page, pageSize,new AdvMapper());
		return rpage;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void updateAdv(Adv adv) throws Exception{
			if(adv.getIsclose()==1){
				storeCostManager.updateStoreCost(adv.getStore_id(),SaleTypeSetting.ADV_PUBLISH_NUM_TYPE);
			}
			this.baseDaoSupport.update("adv", adv, "aid = " + adv.getAid());
	}
	
	
	public List listAdv(Long acid) {
		Long nowtime = (new Date()).getTime();
		
		List<Adv> list = this.baseDaoSupport.queryForList("select a.*,'' cname from adv a where acid = ? and isclose = 0", new AdvMapper(), acid);
		return list;
	}


	@Override
	public Page search(String cname,int pageNo,int pageSize,String order) {
		StringBuffer term  = new StringBuffer();
		StringBuffer sql = new StringBuffer( "select v.*, c.cname  cname from " + this.getTableName("adv") + " v left join " + this.getTableName("adcolumn") + " c on c.acid = v.acid");
		
		if(!StringUtil.isEmpty(cname)){
			if(term.length()>0){
				term.append(" and ");
			}
			else
			{
				term.append(" where ");
			}
			
			term.append(" aname like'%"+cname+"%'");
		}
		sql.append(term);
		
		order = order == null ? " aid desc" : order;
		sql.append(" order by " + order );
		
		Page page = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize,Adv.class);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdvManager#getAdvDetail(int)
	 */
	@Override
	public List<Adv> getAdvDetail(int aId) {
		String sql = "SELECT * FROM es_adv WHERE aid = ?";
		List<Adv> advs = this.daoSupport.queryForList(sql, Adv.class, aId);
		for(Adv adv : advs){
			String pic  = adv.getAtturl();
			if(pic!=null){
				pic  =UploadUtil.replacePath(pic); 
				adv.setAtturl(pic);
			}
		}
		return advs;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdvManager#delByStore(int)
	 */
	@Override
	public void delByStore(int storeId) {
		String sql = "DELETE FROM es_adv WHERE store_id = ?";
		this.daoSupport.execute(sql, storeId);
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdvManager#getStoreAdvList(int)
	 */
	@Override
	public List<Adv> getStoreAdvList(int storeId) {
		String sql = "SELECT * FROM es_adv WHERE store_id = ?";
		List<Adv> advs = this.daoSupport.queryForList(sql, Adv.class, storeId);
		for(Adv adv : advs){
			String pic  = adv.getAtturl();
			if(pic!=null){
				pic  =UploadUtil.replacePath(pic); 
				adv.setAtturl(pic);
			}
		}
		return advs;
	}
	
	@Override
	public List<Adv> getStoreAdvListForMobile(int storeId) {
		List<Adv> advList =  new LinkedList<Adv>();
		if(storeId == 1 || storeId == 0){
			String sql2 = "SELECT * FROM es_adv WHERE store_id = 1 and isclose = 1 ORDER BY begintime DESC";
			List<Adv> advs2 = this.daoSupport.queryForList(sql2, Adv.class);
			if(advs2.size()>2){
				for(int i=0;i<=2;i++){
					advList.add(advs2.get(i));
				}
			}else{
				for(Adv adv: advs2){
					advList.add(adv);
				}
			}
		}else{
			String sql = "SELECT * FROM es_adv WHERE store_id = ? and isclose = 1 ORDER BY begintime DESC";
			List<Adv> advs = this.daoSupport.queryForList(sql, Adv.class, storeId);//店铺发布信息
			String sql2 = "SELECT * FROM es_adv WHERE store_id = 1 and isclose = 1 ORDER BY begintime DESC";
			List<Adv> advs2 = this.daoSupport.queryForList(sql2, Adv.class);//中安发布信息
			if(advs.size()>0){//店铺发布信息 不为空
				if(advs2.size()>2){
					for(int i=0;i<2;i++){
						advList.add(advs2.get(i));
					}
				}else{
					for(Adv adv: advs2){
						advList.add(adv);
					}
				}
				advList.add(advs.get(0));
			}else{//店铺发布信息为空
				if(advs2.size()>2){
					for(int i=0;i<=2;i++){
						advList.add(advs2.get(i));
					}
				}else{
					for(Adv adv: advs2){
						advList.add(adv);
					}
				}
			}
		}
		if(advList.size() >0){
			for(Adv adv : advList){
				String pic  = adv.getAtturl();
				if(pic!=null){
					pic  =UploadUtil.replacePath(pic); 
					adv.setAtturl(pic);
				}
			}
		}
		return advList;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdvManager#disableStoreAdv(int)
	 */
	@Override
	public void disableStoreAdv(int storeId) {
		String sql = "UPDATE es_adv SET disabled='true' WHERE store_id = ?";
		this.daoSupport.execute(sql, storeId);
	}


	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IAdvManager#startAdv(int)
	 */
	@Override
	public void startAdv(int advId) {
		// TODO Auto-generated method stub
		String sql = "UPDATE es_adv SET disabled='false' WHERE aid = ?";
		this.daoSupport.execute(sql, advId);
	}


	public IStoreCostManager getStoreCostManager() {
		return storeCostManager;
	}


	public void setStoreCostManager(IStoreCostManager storeCostManager) {
		this.storeCostManager = storeCostManager;
	}

}
