package com.enation.app.base.core.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.SaleFree;
import com.enation.app.base.core.model.Store;
import com.enation.app.base.core.model.StoreCost;
import com.enation.app.base.core.service.IStoreCostManager;
import com.enation.eop.sdk.database.BaseSupport;
@Component
public class StoreCostManager  extends BaseSupport implements IStoreCostManager{
	
	@Override
	public StoreCost getStoreCost(Integer store_id, Integer saleType) {
		long currenTime = System.currentTimeMillis();
		String sql = "select * from es_store_cost where store_id = "+store_id+" AND type_id = " + saleType + " AND isFree = '0'";
		
		if(saleType == 2 || saleType == 4 || saleType == 6){
			sql  += " AND valid_start_date < "+ currenTime + " AND valid_end_date > "+ currenTime ;
		}
		StoreCost storeCost = (StoreCost) this.baseDaoSupport.queryForObject(sql, StoreCost.class);
		return storeCost;
	}
	
	@Override
	public StoreCost getStoreCostForBuy(Integer store_id, int saleType) {
		String sql = "select id,store_id,type_id,valid_start_date,valid_end_date,SUM(surp_num) surp_num,SUM(used_num) used_num,isFree"
					+ "	from es_store_cost where store_id = "+store_id+" AND type_id = " + saleType + " AND isFree = '1'";
		StoreCost storeCost = (StoreCost) this.baseDaoSupport.queryForObject(sql, StoreCost.class);
		return storeCost;
	}

	@Override
	public void updateStoreCost(Integer store_id, Integer publishNumType) throws Exception {
			long currenTime = System.currentTimeMillis();
			StoreCost storeCostForFree = this.getStoreCost(store_id,publishNumType);
			String sql = "";
			if(storeCostForFree != null){
				if(storeCostForFree.getSurp_num() > 0){
					sql = "update es_store_cost set used_num = "+(storeCostForFree.getUsed_num()+1)+",surp_num = "+(storeCostForFree.getSurp_num()-1)+
							" where  store_id = "+store_id+" AND type_id = "+publishNumType+ " AND isFree = '0'";
					if(publishNumType == 2 || publishNumType == 4){
						sql += " AND valid_start_date < "+ currenTime + " AND valid_end_date > "+ currenTime; 
					}
				}else{
					StoreCost storeCostForBuy = this.getStoreCostForBuy(store_id,publishNumType);
					if(storeCostForBuy != null){
						if(storeCostForBuy.getSurp_num() > 0){
							sql = "update es_store_cost set used_num = "+(storeCostForBuy.getUsed_num()+1)+",surp_num = "+(storeCostForBuy.getSurp_num()-1)+
									" where  store_id = "+store_id+" AND type_id = "+publishNumType+ " AND isFree = '1'";
						}
					}
				}
				this.daoSupport.execute(sql);
			}else{
				throw new Exception("发布次数已经使用完，请免费申请或者购买发布次数！");
			}
	} 
	
	@Override
	public void updateNoteStoreCost(int store_id, Integer noteNumType, int count) {
		// TODO Auto-generated method stub
		try {
			long currenTime = System.currentTimeMillis();
			StoreCost storeCostForFree = this.getStoreCost(store_id,noteNumType);
			String sql = "";
			String sql1 = "";
			if(storeCostForFree != null){
				if(count <= storeCostForFree.getSurp_num()){
					sql = "update es_store_cost set used_num = "+(storeCostForFree.getUsed_num()+count)+
						  ",surp_num = "+(storeCostForFree.getSurp_num()-count)+" where  store_id = " +store_id+
						  " AND type_id = "+noteNumType+ " AND isFree = '0'";
				}else{
					StoreCost storeCostForBuy = this.getStoreCostForBuy(store_id,noteNumType);
					if(storeCostForBuy != null){
						if(count <= (storeCostForFree.getSurp_num()+storeCostForBuy.getSurp_num())){
							sql1 = "update es_store_cost set used_num = "+(storeCostForFree.getUsed_num()+storeCostForFree.getSurp_num())+
								      ",surp_num = 0 where  store_id = " +store_id+" AND type_id = "+noteNumType+" AND isFree = '0'";
							sql = "update es_store_cost set used_num = "+(storeCostForBuy.getUsed_num()+count-storeCostForFree.getSurp_num())+
									  ",surp_num = "+(storeCostForBuy.getSurp_num()+storeCostForFree.getSurp_num()-count)+" where  store_id = " +
									  store_id+ " AND type_id = "+noteNumType+ " AND isFree = '1'";
							this.daoSupport.execute(sql1);
						}
					}else{
						throw new Exception("短信发放数量已经使用完，请免费申请或者购买发放数量！");
					}
				}
				this.daoSupport.execute(sql);
			}else{
				throw new Exception("短信发放数量已经使用完，请免费申请或者购买发放数量！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addStoreCostForBuySale(SaleFree saleFree,String typeId,  Integer store_id) {
		String sql1 = "select * from es_store where store_id = "+store_id;
		Store store = (Store) this.daoSupport.queryForObject(sql1, Store.class);
		String sql = "";
		if(store != null){
			String sqlCost = "select * from es_store_cost where store_id = "+store_id+" AND type_id ="+typeId+" AND isFree = 1";
			StoreCost storeCost = (StoreCost) this.daoSupport.queryForObject(sqlCost, StoreCost.class);
			if(storeCost != null){
				sql ="update es_store_cost set surp_num = "+ (saleFree.getUse_num()+storeCost.getSurp_num())+ " where id = "+storeCost.getId();
			}else{
				sql = "insert into es_store_cost(store_id,type_id,level_id,valid_start_date,valid_end_date,used_num,surp_num,isFree) values("+
						store_id+","+typeId+","+store.getStore_level()+","+0+","+0+","+0+","+saleFree.getUse_num()+","+'1'+")";
			}
			this.daoSupport.execute(sql);
		}
	}

}
