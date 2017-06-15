package com.enation.app.b2b2c.core.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreBonusManager;
import com.enation.app.b2b2c.core.service.IStorePromotionManager;
import com.enation.app.base.SaleTypeSetting;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IStoreCostManager;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;

/**
 * 店铺促销管理 manager
 * @author xulipeng
 * 2015年1月12日23:14:29
 */
@Component
public class StorePromotionManager extends BaseSupport implements IStorePromotionManager {

	private IBonusTypeManager bonusTypeManager;
	private IStoreBonusManager storeBonusManager;
	
	@Override
	public void add_FullSubtract(StoreBonus bonus) {
		this.baseDaoSupport.insert("es_bonus_type", bonus);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void receive_bonus(Integer member_id,Integer store_id,Integer type_id) {
		StoreBonus bonus =	this.getBonus(type_id);
		StoreMember member= this.getStoreMember(member_id);
		int limit = bonus.getLimit_num();	//限领数量
		int createNum = bonus.getCreate_num();//总数量
		int useNum = bonus.getUse_num();//已领取数量
		String queryBonusCreate="select count(0) from es_member_bonus where bonus_type_id = "+type_id;
		if(this.baseDaoSupport.queryForInt(queryBonusCreate)>=createNum){
			throw new RuntimeException("此优惠劵已经被领完了.");
		}
		int num = this.getmemberBonus(type_id,member_id);
		if(num<limit){
			String sn =this.createSn(bonus.getType_id()+"");
			//int c= this.baseDaoSupport.queryForInt("select count(0) from es_member_bonus where bonus_sn=?", sn);
			this.baseDaoSupport.execute("insert into es_member_bonus(bonus_type_id,bonus_sn,type_name,bonus_type,create_time,member_id) values(?,?,?,?,?,?)", type_id,sn,bonus.getType_name(),bonus.getSend_type(),DateUtil.getDateline(),member.getMember_id());
			bonus.setUse_num(useNum+1);
			this.baseDaoSupport.update("es_bonus_type", bonus, "type_id="+ bonus.getType_id());
		}else if(num==limit){
			throw new RuntimeException("您已经领取过优惠券了.");
		}else{
			throw new RuntimeException("系统异常");
		}
	}
	
	private String createSn(String prefix){
		
		StringBuffer sb = new StringBuffer();
		sb.append(prefix);
		sb.append( DateUtil.toString(new Date(), "yyMMddhhmmss"));
		sb.append( createRandom() );
		
		return sb.toString();
	}
	
	private String createRandom(){
		Random random  = new Random();
		StringBuffer pwd=new StringBuffer();
		for(int i=0;i<6;i++){
			pwd.append(random.nextInt(9));
			 
		}
		return pwd.toString();
	}
	
	@Override
	public StoreBonus getBonus(Integer type_id) {
		String sql ="select * from es_bonus_type  where type_id =?";
		return (StoreBonus) this.daoSupport.queryForObject(sql, StoreBonus.class, type_id);
	}
	
	
	@Override
	public StoreMember getStoreMember(Integer member_id) {
		String sql ="select * from es_member  where member_id =?";
		return (StoreMember) this.daoSupport.queryForObject(sql, StoreMember.class, member_id);
	}
	
	@Override
	public int getmemberBonus(Integer type_id,Integer memberid) {
		String sql = "select count(0) from es_member_bonus where bonus_type_id=? and member_id=?";
		int num = this.daoSupport.queryForInt(sql, type_id,memberid);
		return num;
	}
	
	@Override
	public void edit_FullSubtract(StoreBonus bonus) {
		this.daoSupport.update("es_bonus_type", bonus, "type_id="+bonus.getType_id());
	}
	
	@Override
	public void deleteBonus(Integer type_id,Integer bonus_id) {
		StoreBonus bonus =	this.getBonus(type_id);
		int useNum = bonus.getUse_num();
		String sql ="select use_end_date from es_bonus_type where type_id="+type_id;
		long use_end_date = this.daoSupport.queryForLong(sql);
		//判断用户删除的优惠券信息是否过期，如果过期不作处理，如果没有过期，修改优惠券数量
		if(DateUtil.getDateline()<use_end_date){
			this.daoSupport.execute("delete from es_member_bonus where bonus_id="+bonus_id);
			if(bonus.getUse_num() > 0){
				bonus.setUse_num(useNum-1);
			}
			this.daoSupport.update("es_bonus_type", bonus, "type_id="+ bonus.getType_id());
		}else{
			this.daoSupport.execute("delete from es_member_bonus where bonus_id="+bonus_id);
		}
	}
	
	@Override
	public List<StoreBonus> getBonusByMemberId(int memberId) {
		String sql = "SELECT * FROM es_member_bonus WHERE member_id = ? AND used=0";
		return this.daoSupport.queryForList(sql, StoreBonus.class, memberId);
	}
	
	@Override
	public void changeStatus(StoreBonus storeBonus) throws Exception {
		if(storeBonus.getStatus() == 1){//发布
			storeBonusManager.updateBonusStoreCost(storeBonus,SaleTypeSetting.BONUS_NUM_TYPE);
		}
		this.daoSupport.update("es_bonus_type", storeBonus,"type_id = " + storeBonus.getType_id());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<StoreBonus> getBonusByStoreId(int storeId,Member member) {
		Long nowTime=DateUtil.getDateline();
		List<StoreBonus> storeBonusList = null;
		//如果用户没有绑定4s店的话查询中安已经发布的优惠券，并且在有效期的,并且用户没有领取的,并且数量领取数量。
		if(storeId == 1 || storeId == 0){
			String sql = "SELECT ebt.*,es.store_name FROM es_bonus_type ebt,es_store es WHERE ebt.type_id NOT IN( SELECT emb.bonus_type_id FROM es_member_bonus emb WHERE emb.member_id = ?)"
					   + " AND ebt.store_id = 1 AND ebt.status = 1 AND ebt.use_end_date > ? and ebt.store_id = es.store_id AND ebt.create_num > ebt.use_num";
			storeBonusList = this.daoSupport.queryForList(sql, StoreBonus.class,member.getMember_id(),nowTime);
		}else{
			String sql = "SELECT ebt.*,es.store_name FROM es_bonus_type ebt,es_store es WHERE ebt.type_id NOT IN( SELECT emb.bonus_type_id FROM es_member_bonus emb WHERE emb.member_id = ?) "
					   + "AND (ebt.store_id = 1 or ebt.store_id = ? ) AND ebt.status = 1 AND ebt.use_end_date > ? and ebt.store_id = es.store_id AND ebt.create_num > ebt.use_num";
			storeBonusList = this.daoSupport.queryForList(sql, StoreBonus.class,member.getMember_id(),storeId,nowTime);
		}
		return storeBonusList;
	}
	@Override
	public Page getBonusByStoreIdAndPage(int storeId, Member member, Integer bonusPage, int pageSize) {
		Long nowTime=DateUtil.getDateline();
		Page rpage;
		String  sql;
		if(storeId == 1 || storeId == 0){
			sql = "SELECT ebt.*,es.store_name FROM es_bonus_type ebt,es_store es WHERE ebt.type_id NOT IN( "
				+ "SELECT emb.bonus_type_id FROM es_member_bonus emb WHERE emb.member_id = "+member.getMember_id()+")"
				+ " AND ebt.store_id = 1 AND ebt.status = 1 AND ebt.use_end_date > "+nowTime+" "
				+ "and ebt.store_id = es.store_id AND ebt.create_num > ebt.use_num";
			rpage =this.daoSupport.queryForPage(sql.toString(),bonusPage, pageSize);
		}else{
			sql = "SELECT ebt.*,es.store_name FROM es_bonus_type ebt,es_store es WHERE ebt.type_id NOT IN( "
				+ "SELECT emb.bonus_type_id FROM es_member_bonus emb WHERE emb.member_id = "+member.getMember_id()+") "
				+ "AND (ebt.store_id = 1 or ebt.store_id = "+storeId+" ) AND ebt.status = 1 AND ebt.use_end_date > "+nowTime+" "
				+ "and ebt.store_id = es.store_id AND ebt.create_num > ebt.use_num";
			rpage =this.daoSupport.queryForPage(sql.toString(),bonusPage, pageSize);
		}
		return rpage;
	}

	
	@Override
	public Page getBonusBymemberId(Integer member_id, int page, int pageSize) {
		StringBuffer sql =new StringBuffer("SELECT ebt.*,es.store_name,emb.bonus_id FROM es_bonus_type ebt,es_member_bonus emb,es_store es "
							+ "WHERE  ebt.type_id = emb.bonus_type_id AND emb.member_id = "+member_id+" AND emb.used = 0 "
							+ "AND ebt.store_id=es.store_id order by ebt.use_end_date desc");
		Page rpage = this.daoSupport.queryForPage(sql.toString(),page, pageSize);
		return rpage;
	}

	@Override
	public List<Map> getBonusInPay(Integer member_id, Integer store_id, double payMoney,long nowTime) {
		String sql = "SELECT ebt.*,emb.bonus_id,used_time FROM es_bonus_type ebt,es_member_bonus emb,es_store es" 
					+" WHERE  ebt.type_id = emb.bonus_type_id AND emb.member_id = ? AND emb.used = 0 "
					+" AND ebt.store_id=es.store_id AND ebt.store_id=? AND ebt.min_goods_amount <= ?"
					+ "AND ebt.use_start_date <= ? AND ebt.use_end_date >= ? order by type_money desc";
		List<Map> storeBonusList = this.daoSupport.queryForList(sql, member_id,store_id,payMoney,nowTime,nowTime);
		return storeBonusList;
	}

	@Override
	public Map getStoreBonusByOrder(int member_id, int store_id, int order_id) {
		String sql = "select ebt.* from es_bonus_type ebt,es_member_bonus emb where ebt.type_id = emb.bonus_type_id and  emb.order_id = ? and ebt.store_id = ? and emb.member_id = ? and emb.used = 1";
		List<Map> storeBonusList = this.daoSupport.queryForList(sql,order_id,store_id,member_id);
		Map storeBonus=null; 
		if(storeBonusList!=null&&storeBonusList.size()>0){
			storeBonus = storeBonusList.get(0);
		}
		return storeBonus;
	}
	public IBonusTypeManager getBonusTypeManager() {
		return bonusTypeManager;
	}

	public void setBonusTypeManager(IBonusTypeManager bonusTypeManager) {
		this.bonusTypeManager = bonusTypeManager;
	}

	public IStoreBonusManager getStoreBonusManager() {
		return storeBonusManager;
	}

	public void setStoreBonusManager(IStoreBonusManager storeBonusManager) {
		this.storeBonusManager = storeBonusManager;
	}

	
}
