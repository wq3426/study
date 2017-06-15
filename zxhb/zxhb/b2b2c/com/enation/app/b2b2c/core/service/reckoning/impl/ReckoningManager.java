package com.enation.app.b2b2c.core.service.reckoning.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.record.formula.eval.StringValueEval;
import org.apache.tools.ant.types.CommandlineJava.SysProperties;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.model.reckoning.Reckoning;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningDetail;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeStatus;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeType;
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.eop.processor.core.Request;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONObject;

@Component
public class ReckoningManager implements IReckoningManager {
	private IDaoSupport daoSupport;
	private IStoreManager storeManager;
	private StoreOrder order;

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer addDetail(ReckoningDetail reckoning_detail) {
		try {
			daoSupport.insert("es_reckoning_detail", reckoning_detail);
			return daoSupport.getLastId("es_reckoning_detail");
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(Reckoning reckoning) {
		try {
			daoSupport.insert("es_reckoning", reckoning);
		} catch (Exception e) {
			throw e;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public List<Reckoning> getReckoningBySettlementTime(long time) {
		try{
			//类型为结算
			int settle_type =ReckoningTradeType.settle_accounts.getIndex();
			//状态为未结算
			int no_settle_accounts = ReckoningTradeStatus.no_settle_accounts.getIndex();
			
			String sql = "select *from es_reckoning r  where r.trade_type=? and r.trade_status=?  and  "
					+ "EXISTS(select *from es_reckoning_detail  rd where rd.id = r.reckoning_detail_id  and rd.settlement_time <= ? and rd.order_type in (0,3)) order by r.trade_time asc";
			return daoSupport.queryForList(sql, Reckoning.class, settle_type,no_settle_accounts,time);
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void orderSettlement(Reckoning reckoning,Double balance) {
		try{
			if(reckoning!=null && reckoning.getTrade_status() == ReckoningTradeStatus.no_settle_accounts.getIndex()){
				int yet_settle_accounts = ReckoningTradeStatus.yet_settle_accounts.getIndex();
				//更新状态和结算时间
				String sql = "update es_reckoning r set r.trade_status = ?,r.balance=r.trade_money+?,r.trade_time=? where r.id  = ? ";
				daoSupport.execute(sql,yet_settle_accounts,balance,DateUtil.getDateline(),daoSupport.getLastId("es_reckoning"));
				//更新店铺余额
				sql = "update es_store_balance set balance = balance + ? where store_id = ?";
				daoSupport.execute(sql,reckoning.getTrade_money(),reckoning.getStore_id());
				
				sql = "update es_reckoning_detail set settlement_time=? where id = ?";
				daoSupport.execute(sql, DateUtil.getDateline(),reckoning.getReckoning_detail_id());
			}
		}catch(Exception e){
			throw e;
		}
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void timeOutOrderSettlement(Reckoning reckoning,Double balance) {
		try{
			if(reckoning!=null && reckoning.getTrade_status() == ReckoningTradeStatus.no_settle_accounts.getIndex()){
				int yet_settle_accounts = ReckoningTradeStatus.yet_settle_accounts.getIndex();
				//更新状态和结算时间
				String sql = "update es_reckoning r set r.trade_status = ?,r.balance=r.trade_money+?,r.trade_time=? where r.id  = ? ";
				daoSupport.execute(sql,yet_settle_accounts,balance,DateUtil.getDateline(),reckoning.getId());
				//更新店铺余额
				sql = "update es_store_balance set balance = balance + ? where store_id = ?";
				System.out.println("2:"+sql + "getTrade_money : " +reckoning.getTrade_money());
				daoSupport.execute(sql,reckoning.getTrade_money(),reckoning.getStore_id());
				
				
				sql = "update es_reckoning_detail set settlement_time=? where id = ?";
				daoSupport.execute(sql, DateUtil.getDateline(),reckoning.getReckoning_detail_id());
			}
		}catch(Exception e){
			throw e;
		}
	}
	
	
	@Override
	public Reckoning getReckoningByOrder(String orderSn) {
		try{
			String sql = "select r.* from es_reckoning r,es_reckoning_detail rd where r.reckoning_detail_id = rd.id and rd.order_sn = ?";
			return (Reckoning)daoSupport.queryForObject(sql,Reckoning.class, orderSn);
		}catch(Exception e){
			return null;
		}
		
	}

	
	
	@Override
	public double getSettlementCount(Integer reckoningType,Integer reckoningStatus, Integer store_id) {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("select sum(trade_money) count from es_reckoning  r where 1=1  and r.store_id = ?");
			if(reckoningType!=null){
				sql.append(" and  r.trade_type = " + reckoningType);
			}
			if(reckoningStatus!=null){
				sql.append(" and   r.trade_status = " +reckoningStatus);
			}
 			Map map = daoSupport.queryForMap(sql.toString(),  store_id);
 			if(map != null){
 				return (double)map.get("count");
 			}
		}catch(Exception e){
			return 0;
		}
		return 0;
	}

	
	
	@Override
	public Page getReckoningList(int pageNo, int pageSize, Integer store_id, Map result) {
		try{
			String trade_type = String.valueOf(result.get("trade_type"));
			String trade_status =  String.valueOf(result.get("trade_status"));
			String startTime =  String.valueOf(result.get("startTime"));
			String endTime =  String.valueOf(result.get("endTime"));
			String sn = String.valueOf(result.get("sn"));
			StringBuffer sql  =  new StringBuffer();
			sql.append("SELECT"
			+"	r.*, rd.order_sn,"
			+"	rd.service_time,"
			+"	rd.order_price,"
			+"	rd.use_gain,"
			+"	rd.use_coupon,"
			+"	rd.use_repair_coin,"
			+"	rd.paymoney,"
			+"	rd.order_type,"
			+"	rd.handling_charge,"
			+"	rd.service_charge,"
			+"	rd.settlement_money,"
			+"	rd.settlement_time,"
			+"	s.bank_account_name,"
			+"	s.bank_account_number,"
			+"	s.weichat_account,"
			+"	s.alipay_account,"
			+"	ads.trade_time deal_time,"
			+"  o.ship_name,o.complete_time "
			+"  FROM"
			+"	((("
			+"		es_reckoning r LEFT JOIN es_reckoning_detail rd ON r.reckoning_detail_id = rd.id"
			+"	   )"
			+"	   INNER JOIN es_store s ON r.store_id = s.store_id"
			+"	 )"
			+"	 LEFT JOIN es_admin_settlement ads ON ads.sn = r.sn "
			+"  )LEFT JOIN es_order o on rd.order_sn = o.sn"
			+"  WHERE"
			+"  r.store_id = ?");
			if(!StringUtil.isNull(sn)){
				sql.append(" and  r.sn like  '%").append(sn).append("%'");
			}
			if(!StringUtil.isNull(trade_type)&&Integer.parseInt(trade_type)!=0){
				sql.append(" and  r.trade_type = " + trade_type);
			}
			if(!StringUtil.isNull(trade_status)){
				if(Integer.parseInt(trade_status)==0){//全部不显示未结算
					sql.append(" and  trade_status!=1 ");
				}else{
					sql.append(" and   r.trade_status =  " + trade_status);
				}
			}else{
				sql.append(" and  trade_status!=1 ");
			}
			if(!StringUtil.isNull(startTime)){
				sql.append(" and  r.trade_time  >=  ").append(DateUtil.getDateline(startTime+" 00:00:00","yyyy-MM-dd HH:mm:ss") );
			}
			if(!StringUtil.isNull(endTime)){
				sql.append(" and r.trade_time <= ").append(DateUtil.getDateline(endTime+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
			}
			sql.append(" ORDER BY r.trade_time desc,r.balance desc");
	 		Page page = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, store_id );
			return page;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	@Override
	public Reckoning getReckoningBySn(String sn) {
		try{
		String sql  = "select * from es_reckoning where sn = ? ";
		return (Reckoning)daoSupport.queryForObject(sql, Reckoning.class, sn);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	
	@Override
	public List<Map> getReckonings(int store_id, String trade_type, String trade_status, String startTime, String endTime ,String sn) {
		StringBuffer sql  =  new StringBuffer();
		sql.append("SELECT"
				+"	r.*, rd.order_sn,"
				+"	rd.service_time,"
				+"	rd.order_price,"
				+"	rd.use_gain,"
				+"	rd.use_coupon,"
				+"	rd.paymoney,"
				+"	rd.order_type,"
				+"	rd.handling_charge,"
				+"	rd.service_charge,"
				+"	rd.settlement_money,"
				+"	rd.settlement_time,"
				+"	s.bank_account_name,"
				+"	s.bank_account_number,"
				+"	ads.trade_time deal_time"
				+"  FROM"
				+"	(("
				+"		es_reckoning r LEFT JOIN es_reckoning_detail rd ON r.reckoning_detail_id = rd.id"
				+"	 )"
				+"	INNER JOIN es_store s ON r.store_id = s.store_id"
				+"	)"
				+"	LEFT JOIN es_admin_settlement ads ON ads.sn = r.sn "
				+"  WHERE"
				+"  r.store_id = ?");
		if(!StringUtil.isNull(sn)){
			sql.append(" and  r.sn like '%").append(sn).append("%'");
		}
		if(!StringUtil.isNull(trade_type)&&Integer.parseInt(trade_type)!=0){
			sql.append(" and  r.trade_type = " + trade_type);
		}
		if(!StringUtil.isNull(trade_status)){
			if(Integer.parseInt(trade_status)==0){//全部不显示未结算
				sql.append(" and  r.trade_status!=1 ");
			}else{
				sql.append(" and   r.trade_status =  " + trade_status);
			}
		}else{
			sql.append(" and  r.trade_status!=1 ");
		}
		if(!StringUtil.isNull(startTime)){
			sql.append(" and  r.trade_time  >=  ").append(DateUtil.getDateline(startTime+" 00:00:00","yyyy-MM-dd HH:mm:ss") );
		}
		if(!StringUtil.isNull(endTime)){
			sql.append(" and r.trade_time <= ").append(DateUtil.getDateline(endTime+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
		}
		sql.append(" ORDER BY r.trade_time DESC");
		try{
			List<Map> list = daoSupport.queryForList(sql.toString(),store_id);
			return list;
		}catch(Exception e){
			e.printStackTrace();
		}
		return Collections.EMPTY_LIST;
	}
	

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveStageno(String sn, Integer store_id) {
		try{
            StringBuffer sql = new StringBuffer();
		    sql.append("    SELECT");
		    sql.append("	id");
		    sql.append("	FROM");
		    sql.append("		es_reckoning er");
		    sql.append("	WHERE");
		    sql.append("	er.stage_no IS NULL");
		    sql.append("	AND er.store_id=").append(store_id);
		    sql.append("	AND er.trade_type=").append(ReckoningTradeType.settle_accounts.getIndex());
		    sql.append("	AND er.trade_status=").append(ReckoningTradeStatus.yet_settle_accounts.getIndex());
		    List list = daoSupport.queryForList(sql.toString());
		    if(list!=null && list.size()>0){
			    for(int i = 0 ; i <list.size() ; i++){
			    	JSONObject obj= JSONObject.fromObject(list.get(i));
			    	int reckoning_id = obj.getInt("id");
			    	daoSupport.execute("update es_reckoning set stage_no=? where id = ?",sn, reckoning_id);
			    }
		    }else{
		    	throw new RuntimeException("没有可提现的结算账单");
		    }
		}catch (Exception e) {
			throw e;
		}
	}
	
	

	@Override
	public Page getThisPeriodReckoning(int pageNo, int pageSize, Integer store_id, Map result) {
		String order_type = String.valueOf(result.get("order_type"));
		String userInfo =  String.valueOf(result.get("userInfo"));
		String startTime =  String.valueOf(result.get("startTime"));
		String endTime =  String.valueOf(result.get("endTime"));
		String sn  = String.valueOf(result.get("sn"));
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT   r.sn,rd.order_sn,");
		sql.append(" eas.trade_time,rd.order_type,rd.paymoney,rd.settlement_money,");
		sql.append(" rd.handling_charge,rd.service_charge,m.fullname,m.username,o.order_id,o.ship_name,o.status");
		sql.append(" FROM");
		sql.append("(((	es_reckoning r	LEFT JOIN es_reckoning_detail rd ON r.reckoning_detail_id = rd.id	");
		sql.append("  )	LEFT JOIN es_order o ON o.sn = rd.order_sn");
		sql.append(" )LEFT JOIN es_member m ON o.member_id = m.member_id");
		sql.append(")LEFT JOIN es_admin_settlement eas on eas.order_sn = o.sn");
		sql.append(" where r.store_id = ?  ");
		//sql.append(" and r.trade_type = ").append(ReckoningTradeType.settle_accounts.getIndex());
		sql.append(" and r.trade_status = ").append(ReckoningTradeStatus.yet_settle_accounts.getIndex());
		sql.append(" and r.stage_no is null");
		if(!StringUtil.isNull(order_type)){
			sql.append(" and  rd.order_type = " + order_type);
		}
		if(!StringUtil.isNull(sn)){
			sql.append(" and r.sn like '%").append(sn).append("%'");
		}
		if(!StringUtil.isNull(userInfo)){
			sql.append(" and   (m.fullname like '%").append(userInfo).append("%' or m.username like '%").append(userInfo).append("%')");
		}
		if(!StringUtil.isNull(startTime)){
			sql.append(" and  eas.trade_time >=  ").append(DateUtil.getDateline(startTime+" 00:00:00","yyyy-MM-dd HH:mm:ss") );
		}
		if(!StringUtil.isNull(endTime)){
			sql.append(" and eas.trade_time <= ").append(DateUtil.getDateline(endTime+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
		}
		
		sql.append(" order by eas.trade_time  desc");
		Page page = daoSupport.queryForPage(sql.toString(), pageNo, pageSize,store_id);
		return page;
	}

	@Override
	public Page getSettledList(int pageNo, int pageSize, Integer store_id, Map result) {
		String order_type = String.valueOf(result.get("order_type"));
		String userInfo =  String.valueOf(result.get("userInfo"));
		String startTime =  String.valueOf(result.get("startTime"));
		String endTime =  String.valueOf(result.get("endTime"));
		String sn = String.valueOf(result.get("sn"));
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT   r.id,r.sn,rd.order_sn,");
		sql.append(" eas.trade_time,rd.order_type,rd.paymoney,rd.settlement_money,");
		sql.append(" rd.handling_charge,rd.service_charge,m.fullname,m.username,r.trade_type,r.stage_no,r.trade_status,");
		sql.append(" r.trade_time r_trade_time,r.trade_money,o.order_id");
		sql.append(" FROM");
		sql.append("(((	es_reckoning r	LEFT JOIN es_reckoning_detail rd ON r.reckoning_detail_id = rd.id	");
		sql.append("  )	LEFT JOIN es_order o ON o.sn = rd.order_sn");
		sql.append(" )LEFT JOIN es_member m ON o.member_id = m.member_id");
		sql.append(")LEFT JOIN es_admin_settlement eas on eas.order_sn = o.sn");
		sql.append(" where r.store_id = ?  ");
		sql.append(" and r.trade_status != ").append(ReckoningTradeStatus.no_settle_accounts.getIndex());
		sql.append(" and r.stage_no is not null");
		if(!StringUtil.isNull(sn)){
			sql.append(" and  r.sn like '%").append(sn).append("%'");
		}
		if(!StringUtil.isNull(order_type)){
			sql.append(" and  rd.order_type = " + order_type);
		}
		if(!StringUtil.isNull(userInfo)){
			sql.append(" and   (m.fullname like '%").append(userInfo).append("%' or m.username like '%").append(userInfo).append("%')");
		}
		if(!StringUtil.isNull(startTime)){
			sql.append(" and  eas.trade_time >=  ").append(DateUtil.getDateline(startTime+" 00:00:00","yyyy-MM-dd HH:mm:ss") );
		}
		if(!StringUtil.isNull(endTime)){
			sql.append(" and eas.trade_time <= ").append(DateUtil.getDateline (endTime+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
		}
		sql.append(" order by r.id DESC,eas.trade_time  DESC");
		
		Page page = daoSupport.queryForPage(sql.toString(), pageNo, pageSize,store_id);
		return page;
	}

	@Override
	public List getSettledListByNo(Integer store_id, Map result) {
		try{
			String stage_no = String.valueOf(result.get("stage_no"));
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT   r.id,r.sn,rd.order_sn,");
			sql.append(" eas.trade_time,rd.order_type,rd.paymoney,rd.settlement_money,");
			sql.append(" rd.handling_charge,rd.service_charge,m.fullname,m.username,r.trade_type,r.stage_no,r.trade_status,");
			sql.append(" r.trade_time r_trade_time,r.trade_money");
			sql.append(" FROM");
			sql.append("(((	es_reckoning r	LEFT JOIN es_reckoning_detail rd ON r.reckoning_detail_id = rd.id	");
			sql.append("  )	LEFT JOIN es_order o ON o.sn = rd.order_sn");
			sql.append(" )LEFT JOIN es_member m ON o.member_id = m.member_id");
			sql.append(")LEFT JOIN es_admin_settlement eas on eas.order_sn = o.sn");
			sql.append(" where r.store_id = ?  ");
			sql.append(" and r.trade_status != ").append(ReckoningTradeStatus.no_settle_accounts.getIndex());
			sql.append(" and r.stage_no is not null");
			sql.append(" and r.stage_no = ?");
			sql.append(" order by r.id DESC,eas.trade_time  DESC");
			return  daoSupport.queryForList(sql.toString(), store_id,stage_no);			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	@Override
	public Map getReckoningByIncome(Map result) {
		String startTime =  String.valueOf(result.get("startTime"));
		String endTime =  String.valueOf(result.get("endTime"));
		String pageType = String.valueOf(result.get("pageType"));
		String store_id = String.valueOf(result.get("store_id"));
		StringBuffer sql  =  new StringBuffer();
		if(!StringUtil.isNull(pageType) && pageType.equals("transactionDetail")){//账户交易流水收入支出
			String trade_type = String.valueOf(result.get("trade_type"));
			String trade_status =  String.valueOf(result.get("trade_status"));
			sql.append("select sum(trade_money) income ,count(*) income_count from es_reckoning r where r.trade_type =   ").append(ReckoningTradeType.settle_accounts.getIndex());;
			sql.append(" and store_id = '").append(store_id).append("'");
			if(!StringUtil.isNull(trade_type)&&Integer.parseInt(trade_type)!=0){
				sql.append(" and  r.trade_type = " + trade_type);
			}
			if(!StringUtil.isNull(trade_status)){
				if(Integer.parseInt(trade_status)==0){//全部不显示未结算
					sql.append(" and  trade_status!=1 ");
				}else{
					sql.append(" and   r.trade_status =  " + trade_status);
				}
			}else{
				sql.append(" and  trade_status!=1 ");
			}
			if(!StringUtil.isNull(startTime)){
				sql.append(" and  r.trade_time  >=  ").append(DateUtil.getDateline(startTime+" 00:00:00","yyyy-MM-dd HH:mm:ss") );
			}
			if(!StringUtil.isNull(endTime)){
				sql.append(" and r.trade_time <= ").append(DateUtil.getDateline(endTime+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
			}
		}
		else if(!StringUtil.isNull(pageType) && pageType.equals("settled")){//往期
			String order_type = String.valueOf(result.get("order_type"));
			String userInfo =  String.valueOf(result.get("userInfo"));
			sql.append("SELECT   sum(r.trade_money) income,count(*) income_count");
			sql.append(" FROM");
			sql.append("(((	es_reckoning r	LEFT JOIN es_reckoning_detail rd ON r.reckoning_detail_id = rd.id	");
			sql.append("  )	LEFT JOIN es_order o ON o.sn = rd.order_sn");
			sql.append(" )LEFT JOIN es_member m ON o.member_id = m.member_id");
			sql.append(")LEFT JOIN es_admin_settlement eas on eas.order_sn = o.sn");
			sql.append(" where r.store_id =   ").append(store_id);
			sql.append(" and r.trade_status != ").append(ReckoningTradeStatus.no_settle_accounts.getIndex());
			sql.append(" and r.stage_no is not null");
			sql.append(" and r.trade_type = ").append(ReckoningTradeType.settle_accounts.getIndex());
			if(!StringUtil.isNull(order_type)){
				sql.append(" and  rd.order_type = " + order_type);
			}
			if(!StringUtil.isNull(userInfo)){
				sql.append(" and   (m.fullname like '%").append(userInfo).append("%' or m.username like '%").append(userInfo).append("%')");
			}
			if(!StringUtil.isNull(startTime)){
				sql.append(" and  eas.trade_time >=  ").append(DateUtil.getDateline(startTime+" 00:00:00","yyyy-MM-dd HH:mm:ss") );
			}
			if(!StringUtil.isNull(endTime)){
				sql.append(" and eas.trade_time <= ").append(DateUtil.getDateline (endTime+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
			}
		}else if(!StringUtil.isNull(pageType) && pageType.equals("thisPeriod")){
			String order_type = String.valueOf(result.get("order_type"));
			String userInfo =  String.valueOf(result.get("userInfo"));
			sql.append("SELECT   sum(r.trade_money) income,count(*) income_count");
			sql.append(" FROM");
			sql.append("(((	es_reckoning r	LEFT JOIN es_reckoning_detail rd ON r.reckoning_detail_id = rd.id	");
			sql.append("  )	LEFT JOIN es_order o ON o.sn = rd.order_sn");
			sql.append(" )LEFT JOIN es_member m ON o.member_id = m.member_id");
			sql.append(")LEFT JOIN es_admin_settlement eas on eas.order_sn = o.sn");
			sql.append(" where r.store_id = ").append(store_id);
			sql.append(" and r.trade_type = ").append(ReckoningTradeType.settle_accounts.getIndex());
			sql.append(" and r.trade_status = ").append(ReckoningTradeStatus.yet_settle_accounts.getIndex());
			sql.append(" and r.stage_no is null");
			if(!StringUtil.isNull(order_type)){
				sql.append(" and  rd.order_type = " + order_type);
			}
			if(!StringUtil.isNull(userInfo)){
				sql.append(" and   (m.fullname like '%").append(userInfo).append("%' or m.username like '%").append(userInfo).append("%')");
			}
			if(!StringUtil.isNull(startTime)){
				sql.append(" and  eas.trade_time >=  ").append(DateUtil.getDateline(startTime+" 00:00:00","yyyy-MM-dd HH:mm:ss") );
			}
			if(!StringUtil.isNull(endTime)){
				sql.append(" and eas.trade_time <= ").append(DateUtil.getDateline(endTime+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
			}
		}
		return daoSupport.queryForMap(sql.toString());
		
	}

	@Override
	public Map getReckoningByPay(Map result) {
		String startTime =  String.valueOf(result.get("startTime"));
		String endTime =  String.valueOf(result.get("endTime"));
		String pageType = String.valueOf(result.get("pageType"));
		String store_id = String.valueOf(result.get("store_id"));
		StringBuffer sql  =  new StringBuffer();
		if(!StringUtil.isNull(pageType) && pageType.equals("transactionDetail")){
			sql.append("select sum(trade_money) pay,count(*) pay_count from es_reckoning r where (r.trade_type =   ").append(ReckoningTradeType.draw_money.getIndex());
			sql.append(" or r.trade_type = ").append(ReckoningTradeType.service_buy.getIndex());
			sql.append(") and store_id = '").append(store_id).append("'");
			String trade_type = String.valueOf(result.get("trade_type"));
			String trade_status =  String.valueOf(result.get("trade_status"));
			if(!StringUtil.isNull(trade_type)&&Integer.parseInt(trade_type)!=0){
				sql.append(" and  r.trade_type = " + trade_type);
			}
			if(!StringUtil.isNull(trade_status)){
				if(Integer.parseInt(trade_status)==0){//全部不显示未结算
					sql.append(" and  r.trade_status!=1 ");
				}else{
					sql.append(" and   r.trade_status =  " + trade_status);
				}
			}else{
				sql.append(" and  r.trade_status!=1 ");
			}
			if(!StringUtil.isNull(startTime)){
				sql.append(" and  r.trade_time  >=  ").append(DateUtil.getDateline(startTime+" 00:00:00","yyyy-MM-dd HH:mm:ss") );
			}
			if(!StringUtil.isNull(endTime)){
				sql.append(" and r.trade_time <= ").append(DateUtil.getDateline(endTime+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
			}
		}else if(!StringUtil.isNull(pageType) && pageType.equals("settled")){
			String order_type = String.valueOf(result.get("order_type"));
			String userInfo =  String.valueOf(result.get("userInfo"));
			sql.append("SELECT   sum(r.trade_money) pay,count(*) pay_count");
			sql.append(" FROM");
			sql.append("(((	es_reckoning r	LEFT JOIN es_reckoning_detail rd ON r.reckoning_detail_id = rd.id	");
			sql.append("  )	LEFT JOIN es_order o ON o.sn = rd.order_sn");
			sql.append(" )LEFT JOIN es_member m ON o.member_id = m.member_id");
			sql.append(")LEFT JOIN es_admin_settlement eas on eas.order_sn = o.sn");
			sql.append(" where r.store_id =  ").append(store_id);
			sql.append(" and r.trade_status != ").append(ReckoningTradeStatus.no_settle_accounts.getIndex());
			sql.append(" and r.stage_no is not null");
			sql.append(" and r.trade_type = ").append(ReckoningTradeType.draw_money.getIndex());
			if(!StringUtil.isNull(order_type)){
				sql.append(" and  rd.order_type = " + order_type);
			}
			if(!StringUtil.isNull(userInfo)){
				sql.append(" and   (m.fullname like '%").append(userInfo).append("%' or m.username like '%").append(userInfo).append("%')");
			}
			if(!StringUtil.isNull(startTime)){
				sql.append(" and  eas.trade_time >=  ").append(DateUtil.getDateline(startTime+" 00:00:00","yyyy-MM-dd HH:mm:ss") );
			}
			if(!StringUtil.isNull(endTime)){
				sql.append(" and eas.trade_time <= ").append(DateUtil.getDateline (endTime+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
			}
			
		}else if(!StringUtil.isNull(pageType) && pageType.equals("thisPeriod")){
			String order_type = String.valueOf(result.get("order_type"));
			String userInfo =  String.valueOf(result.get("userInfo"));
			sql.append("SELECT   sum(r.trade_money) pay,count(*) pay_count");
			sql.append(" FROM");
			sql.append("(((	es_reckoning r	LEFT JOIN es_reckoning_detail rd ON r.reckoning_detail_id = rd.id	");
			sql.append("  )	LEFT JOIN es_order o ON o.sn = rd.order_sn");
			sql.append(" )LEFT JOIN es_member m ON o.member_id = m.member_id");
			sql.append(")LEFT JOIN es_admin_settlement eas on eas.order_sn = o.sn");
			sql.append(" where r.store_id = ").append(store_id);
			sql.append(" and (r.trade_type = ").append(ReckoningTradeType.other.getIndex());
			sql.append(" or r.trade_type = ").append(ReckoningTradeType.service_buy.getIndex());
			sql.append(") and r.stage_no is null");
			if(!StringUtil.isNull(order_type)){
				sql.append(" and  rd.order_type = " + order_type);
			}
			if(!StringUtil.isNull(userInfo)){
				sql.append(" and   (m.fullname like '%").append(userInfo).append("%' or m.username like '%").append(userInfo).append("%')");
			}
			if(!StringUtil.isNull(startTime)){
				sql.append(" and  eas.trade_time >=  ").append(DateUtil.getDateline(startTime+" 00:00:00","yyyy-MM-dd HH:mm:ss") );
			}
			if(!StringUtil.isNull(endTime)){
				sql.append(" and eas.trade_time <= ").append(DateUtil.getDateline(endTime+" 23:59:59","yyyy-MM-dd HH:mm:ss"));
			}
		}
		return daoSupport.queryForMap(sql.toString());
	}

	
	
	@Override
	public Map getTransactionDetailById(String id) {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT");
			sql.append(" r.*, rd.order_sn,rd.service_time,rd.order_price,rd.use_gain,rd.use_coupon,rd.use_repair_coin,rd.paymoney,rd.order_type,");
			sql.append(" rd.handling_charge,rd.service_charge,rd.settlement_money,rd.settlement_time,s.bank_account_name,");
			sql.append(" s.bank_account_number,s.weichat_account,s.alipay_account,ads.trade_time deal_time,o.ship_name");
			sql.append("	FROM");
			sql.append(" (((es_reckoning r");
			sql.append("     LEFT JOIN es_reckoning_detail rd ON r.reckoning_detail_id = rd.id");
			sql.append("    )INNER JOIN es_store s ON r.store_id = s.store_id");
			sql.append("   )LEFT JOIN es_admin_settlement ads ON ads.sn = r.sn");
			sql.append(" )LEFT JOIN es_order o ON rd.order_sn = o.sn WHERE r.id=?");
			return daoSupport.queryForMap(sql.toString(), id);
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	@Override
	public List<Map> getThisPeriodList(int store_id, Map result) {
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT   r.sn,rd.order_sn,");
		sql.append(" eas.trade_time,rd.order_type,rd.paymoney,rd.settlement_money,");
		sql.append(" rd.handling_charge,rd.service_charge,m.fullname,m.username,o.order_id,o.ship_name,o.status");
		sql.append(" FROM");
		sql.append("(((	es_reckoning r	LEFT JOIN es_reckoning_detail rd ON r.reckoning_detail_id = rd.id	");
		sql.append("  )	LEFT JOIN es_order o ON o.sn = rd.order_sn");
		sql.append(" )LEFT JOIN es_member m ON o.member_id = m.member_id");
		sql.append(")LEFT JOIN es_admin_settlement eas on eas.order_sn = o.sn");
		sql.append(" where r.store_id = ?  ");
		sql.append(" and r.trade_type = ").append(ReckoningTradeType.settle_accounts.getIndex());
		sql.append(" and r.trade_status = ").append(ReckoningTradeStatus.yet_settle_accounts.getIndex());
		sql.append(" and r.stage_no is null");
		sql.append(" order by eas.trade_time  desc");
		return daoSupport.queryForList(sql.toString(), store_id);
	}

	
	@Override
	public int countDrawMoneyByStoreId(Integer store_id) {
		String sql = "select count(*) count from es_reckoning  where trade_type = " + ReckoningTradeType.draw_money.getIndex() 
		+ " and store_id = ? " ;
		return daoSupport.queryForInt(sql, store_id);
		 
	}

	
	@Override
	public List<Map> getSettledStageNoList(Integer store_id) {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("select stage_no from es_reckoning  where stage_no is not null and store_id=? group by stage_no");
			return daoSupport.queryForList(sql.toString(), store_id);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}



	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}

	public StoreOrder getOrder() {
		return order;
	}

	public void setOrder(StoreOrder order) {
		this.order = order;
	}



	

}
