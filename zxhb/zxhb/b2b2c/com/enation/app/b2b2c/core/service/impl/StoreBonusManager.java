package com.enation.app.b2b2c.core.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.app.b2b2c.core.service.IStoreBonusManager;
import com.enation.app.base.core.model.StoreCost;
import com.enation.app.base.core.service.IStoreCostManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 店铺优惠卷查询
 * @author xulipeng
 *
 */
@Component
public class StoreBonusManager extends BaseSupport implements IStoreBonusManager {

	private IStoreCostManager storeCostManager;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.IStoreBonusManager#getBonusList(java.lang.Integer)
	 */
	@Override
	public List getBonusList(Integer store_id) {
		List list = this.baseDaoSupport.queryForList("select * from es_bonus_type where store_id=?", store_id);
		return list;
	}

	@Override
	public List<Map> getMemberBonusList(Integer memberid, Integer store_id,Double min_goods_amount) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String date = format.format(new Date());
		long l = DateUtil.getDateline(date+" 23:59:59");
		
		StringBuffer sql = new StringBuffer("select * from es_member_bonus m left join es_bonus_type b on b.type_id = m.bonus_type_id  where m.used=0 ");
		sql.append(" and m.member_id="+memberid);
		sql.append(" and b.store_id="+store_id);
		sql.append(" and b.min_goods_amount<="+min_goods_amount);
		sql.append(" and b.use_start_date <=" +l);
		sql.append(" and b.use_end_date>="+l);
		
		List list = this.daoSupport.queryForList(sql.toString());
		return list;
	}

	@Override
	public StoreBonus get(Integer bonusid) {
		String sql ="select * from es_bonus_type where type_id=?";
		StoreBonus bonus = (StoreBonus) this.daoSupport.queryForObject(sql, StoreBonus.class, bonusid);
		return bonus;
	}

	@Override
	public Page getBonusListBymemberid(int pageNo,int pageSize,Integer memberid) { 

		
		String sql = "select m.*,b.type_id,b.type_money,b.send_type,b.min_amount,b.max_amount,b.send_start_date,b.send_end_date,b.use_start_date,"
				+"b.use_end_date,b.min_goods_amount,b.use_num,b.create_num,b.recognition"
				+",s.store_name from es_member_bonus m left join es_bonus_type b on b.type_id = m.bonus_type_id"
				+ " left join es_store s on b.store_id=s.store_id where m.member_id="+memberid;
		Page webPage = this.daoSupport.queryForPage(sql, pageNo, pageSize);
		return webPage;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void setBonusUsed(Integer bonus_id,Integer member_id) {
		this.daoSupport.execute("update es_member_bonus set used=1 where bonus_type_id="+bonus_id+" and member_id="+member_id);
		this.daoSupport.execute("update es_bonus_type set use_num=(use_num+1) where type_id="+bonus_id);
	}
	
	
	@Override
	public Page getConditionBonusList(Integer pageNo, int pageSize,Integer store_id,Map map){
		String add_time_from=String.valueOf(map.get("add_time_from"));
		String add_time_to=String.valueOf(map.get("add_time_to"));
		String typeName = String.valueOf(map.get("typeName"));
		String status =  String.valueOf(map.get("status"));
		StringBuffer sql =new StringBuffer("select * from es_bonus_type where store_id= "+ store_id);
		
		if(!StringUtil.isEmpty(typeName)&&!typeName.equals("null")){
			sql.append(" AND type_name like '%" + typeName + "%'");
		}
		
		if(!StringUtil.isEmpty(status)&&!status.equals("null")){
			sql.append(" AND status = "+ status);
		}
		
		if(!StringUtil.isEmpty(add_time_from)&&!add_time_from.equals("null")){
			sql.append(" AND use_start_date >"+DateUtil.getDateline(add_time_from));
		}
		if(!StringUtil.isEmpty(add_time_to)&&!add_time_to.equals("null")){
			sql.append(" AND use_end_date <"+DateUtil.getDateline(add_time_to));
		}
		sql.append(" order  by create_date desc ");
		Page rpage =  this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, StoreBonus.class);
		
		
		return rpage;
	}

	@Override
	public void updateBonusStoreCost(StoreBonus storeBonus, Integer bonusNumType) throws Exception {
			long currenTime = System.currentTimeMillis();
			StoreCost storeCostForFree = storeCostManager.getStoreCost(storeBonus.getStore_id(),bonusNumType);
			String sql = "";
			String sql1 = "";
			if(storeCostForFree != null){
				if(storeBonus.getCreate_num() <= storeCostForFree.getSurp_num()){
					sql = "update es_store_cost set used_num = "+(storeCostForFree.getUsed_num()+storeBonus.getCreate_num())+
					      ",surp_num = "+(storeCostForFree.getSurp_num()-storeBonus.getCreate_num())+" where  store_id = " +storeBonus.getStore_id()+
					      " AND type_id = "+bonusNumType+ " AND isFree = '0'"+" AND valid_start_date < "+ currenTime + " AND valid_end_date > "+ currenTime;
				}else{
					StoreCost storeCostForBuy = storeCostManager.getStoreCostForBuy(storeBonus.getStore_id(),bonusNumType);
					if(storeCostForBuy != null){
						if(storeBonus.getCreate_num() <=  (storeCostForFree.getSurp_num()+storeCostForBuy.getSurp_num())){
							sql1 = "update es_store_cost set used_num = "+(storeCostForFree.getUsed_num()+storeCostForFree.getSurp_num())+
								      ",surp_num = 0 where  store_id = " +storeBonus.getStore_id()+
								      " AND type_id = "+bonusNumType+ " AND isFree = '0'"+" AND valid_start_date < "+ currenTime + " AND valid_end_date > "+ currenTime;
							sql = "update es_store_cost set used_num = "+(storeCostForBuy.getUsed_num()+storeBonus.getCreate_num()-storeCostForFree.getSurp_num())+
									  ",surp_num = "+(storeCostForBuy.getSurp_num()+storeCostForFree.getSurp_num()-storeBonus.getCreate_num())+" where  store_id = " +
										storeBonus.getStore_id()+ " AND type_id = "+bonusNumType+ " AND isFree = '1'";
							this.daoSupport.execute(sql1);
						}
					}else{
						throw new Exception("优惠券发放数量已经使用完，请免费申请或者购买发布数量！");
					}
				}
				this.daoSupport.execute(sql);
			}else{
				throw new Exception("数据异常！");
			}
	}

	public IStoreCostManager getStoreCostManager() {
		return storeCostManager;
	}

	public void setStoreCostManager(IStoreCostManager storeCostManager) {
		this.storeCostManager = storeCostManager;
	}

}
