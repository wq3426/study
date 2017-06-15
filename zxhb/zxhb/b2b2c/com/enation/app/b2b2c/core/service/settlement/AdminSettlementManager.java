package com.enation.app.b2b2c.core.service.settlement;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.reckoning.Reckoning;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeStatus;
import com.enation.app.b2b2c.core.model.settlement.AdminSettlement;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.b2b2c.core.service.settlement.impl.IAdminSettlementManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Store;
import com.enation.app.shop.core.service.AdminSettlementType;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

@Component
public class AdminSettlementManager implements IAdminSettlementManager {
	private IDaoSupport daoSupport;

	private IReckoningManager reckoningManager;

	private IStoreManager storeManager;

	private IStoreMemberManager storeMemberManager;

	@Override
	public void add(AdminSettlement adminSettlement) {
		try {
			daoSupport.insert("es_admin_settlement", adminSettlement);
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public int countTodayBigTypeNum(Integer trade_big_type) {
		//因为提现时，adminSettlement未添加，所以为例外，从es_reckoning 表中获取
		try {
			String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
			long today_zero = DateUtil.getDateline(date);
			long tomorrow_zero = DateUtil.getDateline(date, "yyyy-MM-dd HH:mm:ss");
			return daoSupport.queryForInt(
					"select count(*) from es_reckoning where trade_type = ? and trade_time BETWEEN  ? and ?",
					trade_big_type, today_zero, tomorrow_zero);
		} catch (Exception e) {
			return 0;
		}
	}
	
	
	
	@Override
	public int countTodayAdminTradeBigType(Integer trade_big_type) {
		try {
			String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd 23:59:59");
			long today_zero = DateUtil.getDateline(date);
			long tomorrow_zero = DateUtil.getDateline(date, "yyyy-MM-dd HH:mm:ss");
			return daoSupport.queryForInt(
					"select count(*) from es_admin_settlement where trade_big_type = ? and trade_time BETWEEN  ? and ?",
					trade_big_type, today_zero, tomorrow_zero);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public AdminSettlement getAdminSettlementByOrder(String order_sn) {
		try {
			return (AdminSettlement) daoSupport.queryForObject("select *from es_admin_settlement where order_sn  = ?",
					AdminSettlement.class, order_sn);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public Page apply_list(Map applyList, int pageNo, int pageSize, String sort, String order) {
		StringBuffer sql = new StringBuffer();
		sql.append(
				"SELECT 	r.*, s.store_name,s.bank_account_name,s.bank_account_number,s.weichat_account,s.alipay_account,(r.trade_money + r.balance) apply_balance");
		sql.append(" FROM es_reckoning r,es_store s WHERE 	trade_type = 1 AND r.store_id = s.store_id");
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String store_name = request.getParameter("store_name");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String trade_status = request.getParameter("trade_status");
		String sn = request.getParameter("sn");
		String keyword = request.getParameter("keyword");
		if (!StringUtil.isNull(store_name)) {
			sql.append(" and store_name like '%" + store_name + "%'");
		}
		if (!StringUtil.isNull(start_time)) {
			long time = DateUtil.getDateline(start_time);
			sql.append(" and trade_time >= " + time);
		}
		if (!StringUtil.isNull(end_time)) {
			end_time += " 23:59:59";
			long time = DateUtil.getDateline(end_time, "yyyy-MM-dd hh:mm:ss");
			sql.append(" and trade_time <= " + time);
		}
		if (!StringUtil.isNull(trade_status)) {
			sql.append(" and trade_status = " + trade_status);
		}
		if (!StringUtil.isNull(sn)) {
			sql.append(" and sn like  '%" + sn + "%'");
		}
		if (!StringUtil.isNull(keyword)) {
			sql.append(" and sn like  '%" + keyword + "%'");
		}

		sql.append(" order by r.trade_time desc");
		return this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
	}

	@Override
	public Page getSettlementList(Map settlementList, int pageNo, int pageSize, String sort, String order) {
		StringBuffer sql = new StringBuffer();
		sql.append("select  ads.*,r.trade_status from es_admin_settlement ads  LEFT JOIN  es_reckoning r on r.sn = ads.sn where 1=1 ");
		ThreadContextHolder.getHttpRequest();
		String keyword = (String) settlementList.get("keyword");
		String trade_big_type = (String) settlementList.get("trade_big_type");
		String sn = (String) settlementList.get("sn");
		String object_name = (String) settlementList.get("object_name");
		String start_time = (String) settlementList.get("start_time");
		String end_time = (String) settlementList.get("end_time");
		if (!StringUtil.isNull(keyword)) {
			sql.append(" and ads.sn like '%" + keyword + "%'");
		}
		if (!StringUtil.isNull(trade_big_type)) {
			sql.append(" and ads.trade_big_type = " + trade_big_type);
		}
		if (!StringUtil.isNull(object_name)) {
			sql.append(" and ads.object_name like  '%" + object_name + "%'");
		}
		if (!StringUtil.isNull(sn)) {
			sql.append(" and ads.sn  like '%" + sn + "%'");
		}
		if (!StringUtil.isNull(start_time)) {
			long time = DateUtil.getDateline(start_time);
			sql.append(" and ads.trade_time >= " + time);
		}
		if (!StringUtil.isNull(end_time)) {
			end_time += " 23:59:59";
			long time = DateUtil.getDateline(end_time, "yyyy-MM-dd hh:mm:ss");
			sql.append(" and ads.trade_time <= " + time);
		}
		sql.append(" order by ads.trade_time desc,balance_record desc");//优先时间，时间相同再按金额排序
		return this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
	}

	@Override
	public double getAdminBalance() {
		try {
			Map map = daoSupport.queryForMap("select *from es_store_balance where store_id = ?", 0);
			if (map != null && map.size() > 0) {
				return (double) map.get("balance");
			}
		} catch (Exception e) {
			return 0;
		}
		return 0;
	}

	@Override
	public double getSettlementCountByType(Integer reckoningType, Integer reckoningStatus, Integer store_id) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("select sum(trade_money) count from es_reckoning  r where 1=1 ");
			if (reckoningType != null) {
				sql.append(" and  r.trade_type = " + reckoningType);
			}
			if (reckoningStatus != null) {
				sql.append(" and   r.trade_status = " + reckoningStatus);
			}
			if (store_id != null) {
				sql.append(" and r.store_id = " + store_id);
			}
			Map map = daoSupport.queryForMap(sql.toString());
			if (map != null) {
				return (double) map.get("count");
			}
		} catch (Exception e) {
			return 0;
		}
		return 0;
	}

	@Override
	public double getOrderPayCount() {
		try {
			String sql = "select sum(paymoney) count from es_order  o where o.parent_id is not null and pay_status = ?";
			Map map = daoSupport.queryForMap(sql, OrderStatus.PAY_CONFIRM);
			if (map != null && map.size() > 0) {
				return (double) map.get("count");
			}
		} catch (Exception e) {
			return 0d;
		}
		return 0;

	}

	@Override
	public Map getSettlementCountByDate(long start, long end) {
		try {
			String sql = "select sum(real_settlement_money) income_count,sum(bank_handling_charge)  handling_charge_count  from es_admin_settlement where trade_time >= ? and trade_time<= ?";
			Map map = daoSupport.queryForMap(sql, start, end);
			return map;
		} catch (Exception e) {
			return Collections.EMPTY_MAP;
		}
	}

	@Override
	public Map getSettlementAllCount() {
		try {
			String sql = "select sum(real_settlement_money) income_count,sum(bank_handling_charge)  handling_charge_count  from es_admin_settlement ";
			Map map = daoSupport.queryForMap(sql);
			return map;
		} catch (Exception e) {
			return Collections.EMPTY_MAP;
		}
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addApply(String sn) {
		try {
			// 判断是否已经提交过了。
			String sql = "select *from es_admin_settlement where  sn = ?";
			AdminSettlement adminSettlement = (AdminSettlement) daoSupport.queryForObject(sql, AdminSettlement.class,
					sn);
			if (adminSettlement != null) {//
				throw new RuntimeException("已经提交过流水号为【" + sn + "】的付款单");
			}
			Reckoning reckoning = reckoningManager.getReckoningBySn(sn);
			String receipt_file = ThreadContextHolder.getHttpRequest().getParameter("receipt_file");
			daoSupport.execute("update es_reckoning set receipt_file = ? , trade_status = ? where sn = ?", receipt_file,ReckoningTradeStatus.yet_dispose.getIndex(),sn);
			adminSettlement = new AdminSettlement();
			adminSettlement.setAdmin_pay_type(0);//提现没有，用户或者经销商购买服务时可以使用银行卡，支付宝等
			double bank_handling_charge = -StringUtil
					.toDouble(ThreadContextHolder.getHttpRequest().getParameter("bank_handling_charge"), 0d);//手续费为负数
			double admin_balance = this.getAdminBalance();
			adminSettlement.setBank_handling_charge(bank_handling_charge);
			double reckningTradeMoney = -(reckoning.getTrade_money()+bank_handling_charge);//店铺承担手续费
			adminSettlement.setTrade_money(reckningTradeMoney);//店铺显示为正数，admin为负数
			adminSettlement.setBalance_record(CurrencyUtil.add(admin_balance,
					CurrencyUtil.add(bank_handling_charge,reckningTradeMoney)));
			
			StoreMember storeMember = storeMemberManager.getStoreMemberByStoreId(reckoning.getStore_id());
			adminSettlement.setMember_id(storeMember.getMember_id());
			Store store = storeManager.getStore(reckoning.getStore_id());
			adminSettlement.setSn(sn);
			adminSettlement.setObject_name(store.getStore_name());
			adminSettlement.setTrade_big_type(AdminSettlementType.BIG_DRAW_MONEY);
			adminSettlement.setTrade_small_type(AdminSettlementType.STORE_DRAW_MONEY);
			adminSettlement.setTrade_time(DateUtil.getDateline());
			daoSupport.insert("es_admin_settlement", adminSettlement);
			daoSupport.execute("update es_store_balance set balance=balance+? where store_id =0",
					CurrencyUtil.add(bank_handling_charge, reckningTradeMoney));
			
		} catch (Exception e) {
			throw e;
		}

	}
	
	

	@Override
	public double getSettlementCountByBigType(int bigType) {
		try{
			String sql = "select sum(trade_money) count from es_admin_settlement eds where  eds.trade_big_type = ?";
			Map map = daoSupport.queryForMap(sql, bigType);
			if(map!=null && map.size()>0){
				return (double)map.get("count");
			}
		}catch(Exception e){
			return 0d;
		}
		return 0d;
	}	

	
	
	@Override
	public double getSettlementCountByTypeAndDate(int bigType, long startTime, long endTime) {
		try {
			String sql = "select sum(trade_money)  count from es_admin_settlement where trade_time >= ? and trade_time<= ? and trade_big_type=?";
			Map map = daoSupport.queryForMap(sql, startTime, endTime,bigType);
			if(map!=null&&map.size()>0){
				return (double)map.get("count");
			}
			return 0d;
		} catch (Exception e) {
			return 0d;
		}
	}

	
	
	@Override
	public Map getSettlementCountByBigType(Map map) {
		try{
			if(map == null){
				return Collections.EMPTY_MAP;
			}
			String bigTypes =(String)map.get("bigTypes");
			StringBuffer sql = new StringBuffer();
			String keyword = (String) map.get("keyword");
			String trade_big_type = (String) map.get("trade_big_type");
			String sn = (String) map.get("sn");
			String object_name = (String) map.get("object_name");
			String start_time = (String) map.get("start_time");
			String end_time = (String) map.get("end_time");
			sql.append("select count(*) number,sum(trade_money) trade_money from es_admin_settlement where 1=1 ");
			if(!StringUtil.isNull(bigTypes)){
				sql.append(" and trade_big_type in ("+(String)map.get("bigTypes")+")");
			}
			if (!StringUtil.isNull(keyword)) {
				sql.append(" and sn like '%" + keyword + "%'");
			}
			if (!StringUtil.isNull(trade_big_type)) {
				sql.append(" and trade_big_type = " + trade_big_type);
			}
			if (!StringUtil.isNull(object_name)) {
				sql.append(" and object_name like  '%" + object_name + "%'");
			}
			if (!StringUtil.isNull(sn)) {
				sql.append(" and sn  like '%" + sn + "%'");
			}
			if (!StringUtil.isNull(start_time)) {
				long time = DateUtil.getDateline(start_time);
				sql.append(" and trade_time >= " + time);
			}
			if (!StringUtil.isNull(end_time)) {
				end_time += " 23:59:59";
				long time = DateUtil.getDateline(end_time, "yyyy-MM-dd hh:mm:ss");
				sql.append(" and trade_time <= " + time);
			}
			Map return_map = daoSupport.queryForMap(sql.toString());
			return return_map;
		}catch(Exception e){
			e.printStackTrace();
			return Collections.EMPTY_MAP;
		}
	}
	
	

	@Override
	public List<Map> getExcelData() {
		try{
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			StringBuffer sql = new StringBuffer();
			String searchKeyword = request.getParameter("searchKeyword");
			String sn = request.getParameter("sn");
			String start_time = request.getParameter("start_time");
			String end_time = request.getParameter("end_time");
			String trade_big_type = request.getParameter("trade_big_type");
			String object_name = request.getParameter("object_name");
			sql.append("SELECT"
					+ "	eas.*,o.service_store_name,er.trade_status,er.pay_type,o.need_pay_money,o.gain,ebt.type_money,o.paymoney,o.payment_name,erd.level_name,erd.service_time"
					+ " ,o.order_type,erd.handling_charge,erd.service_charge,erd.settlement_money,erd.settlement_time,er.balance,s.bank_account_name,"
					+ " s.bank_account_number,s.weichat_account,s.alipay_account,er.trade_money er_trade_money,er.apply_remarks"
					+ " FROM "
					+ "((("
					+ "		("
					+ "			es_admin_settlement eas"
					+ " 		LEFT JOIN es_reckoning er ON eas.sn = er.sn"
					+ "		)"
					+ "		LEFT JOIN es_reckoning_detail erd ON er.reckoning_detail_id =erd.id"
					+ "   )LEFT JOIN es_order o ON o.sn = eas.order_sn"
					+ " )LEFT JOIN (es_member_bonus emb INNER JOIN es_bonus_type ebt ON emb.bonus_type_id=ebt.type_id) ON o.sn = emb.order_sn"
					+ ")LEFT JOIN es_store s ON s.store_id =  er.store_id  where 1=1");
			if(!StringUtil.isNull(searchKeyword)){
				sql.append(" and eas.sn like '%"+searchKeyword+"%'");
			}
			if (!StringUtil.isNull(trade_big_type)) {
				sql.append(" and eas.trade_big_type = " + trade_big_type);
			}
			if (!StringUtil.isNull(object_name)) {
				sql.append(" and eas.object_name like  '%" + object_name + "%'");
			}
			if (!StringUtil.isNull(sn)) {
				sql.append(" and eas.sn  like '%" + sn + "%'");
			}
			if (!StringUtil.isNull(start_time)) {
				long time = DateUtil.getDateline(start_time);
				sql.append(" and eas.trade_time >= " + time);
			}
			if (!StringUtil.isNull(end_time)) {
				end_time += " 23:59:59";
				long time = DateUtil.getDateline(end_time, "yyyy-MM-dd hh:mm:ss");
				sql.append(" and eas.trade_time <= " + time);
			}
			sql.append(" order by eas.trade_time desc");
			List<Map> list = daoSupport.queryForList(sql.toString());
			return list;
		}catch(Exception e){
			e.printStackTrace();
			return Collections.EMPTY_LIST;
		}
	}

	@Override
	public Map getSettlementDetail(String sn) {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT"
					+ "	eas.*, o.service_store_name,er.trade_status,o.need_pay_money,o.gain,o.repair_coin,ebt.type_money,o.paymoney,o.payment_name,erd.level_name,erd.service_time,"
					+ " o.order_type,erd.handling_charge,erd.service_charge,erd.settlement_money,erd.settlement_time,er.balance,er.apply_remarks"
					+ " FROM( ( ("
					+ "				es_admin_settlement eas"
					+ "				LEFT JOIN es_reckoning er ON eas.sn = er.sn"
					+ "			 )"
					+ "			 LEFT JOIN es_reckoning_detail erd ON er.reckoning_detail_id = erd.id"
					+ "		   )"
					+ "		   LEFT JOIN es_order o ON o.sn = eas.order_sn"
					+ "      )"
					+ "		 LEFT JOIN ("
					+ "			es_member_bonus emb"
					+ "			INNER JOIN es_bonus_type ebt ON emb.bonus_type_id = ebt.type_id"
					+ "		 ) ON o.sn = emb.order_sn"
					+ " WHERE eas.sn = ?");
			Map map = daoSupport.queryForMap(sql.toString(), sn);	
			return map;
		}catch(Exception e){
			e.printStackTrace();
			return Collections.EMPTY_MAP;
		}
		
	}

	
	@Override
	public Map getDrowMoneyDetail(String sn) {
		try{
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT	eas.*, er.balance,s.bank_account_name,s.bank_account_number,s.weichat_account,s.alipay_account,er.apply_remarks,er.receipt_file,er.pay_type,er.trade_money er_trade_money  FROM	(es_admin_settlement eas	LEFT JOIN es_reckoning er ON eas.sn = er.sn	)LEFT JOIN es_store s ON s.store_id = er.store_id WHERE	eas.sn = ?");
			Map map = daoSupport.queryForMap(sql.toString(), sn);
			return map;
		}catch(Exception e){
			e.printStackTrace();
			return Collections.EMPTY_MAP;
		}
	}
	
	
	@Override
	public Map getApplyDetail(String sn) {
		try{
			
			StringBuffer sql = new StringBuffer();
			sql.append("select r.*,s.bank_account_name,s.bank_account_number,s.weichat_account,s.alipay_account,s.store_name,sum(r.balance+r.trade_money) before_balance  from es_reckoning r,es_store s where sn = ? and r.store_id = s.store_id ");
			return daoSupport.queryForMap(sql.toString(), sn);
		}catch(Exception e){
			e.printStackTrace();
			return Collections.EMPTY_MAP;
		}
		
		
	}
	
	

	@Override
	public Page listInvoice(int pageNo, int pageSize, Map result) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String today = format.format(new Date());//当天0点
		long todayTime = DateUtil.getDateline(today);
		long yestodayTime = todayTime - (24*3600);
		String store_name = String.valueOf(result.get("store_name"));
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT	s.store_id,	s.store_name,	b.settlement_count,	a.order_count,	b.settlement_money,	esb.balance,	d.no_settlement_money,	e.no_service_money");
		sql.append(" FROM");
		sql.append("	((((es_store s");
		sql.append("	        LEFT JOIN (");
		sql.append("					SELECT count(*) order_count,o.service_store_id FROM es_order o WHERE o.create_time <=? AND o.create_time >= ? GROUP BY o.service_store_id");
		sql.append("			) a ON a.service_store_id = s.store_id");
		sql.append("	    )");
		sql.append("		  LEFT JOIN (");
		sql.append("				SELECT count(*) settlement_count,store_id,SUM(trade_money) settlement_money ");
		sql.append("				FROM	es_reckoning r WHERE");
		sql.append("				r.trade_type = 2");
		sql.append("				AND r.trade_time <=?");
		sql.append("				AND r.trade_time >= ?	");
		sql.append("				GROUP BY store_id	");
		sql.append("		  ) b ON s.store_id = b.store_id");
		sql.append("	   )LEFT JOIN es_store_balance esb ON esb.store_id = s.store_id");
		sql.append("      )LEFT JOIN (");
		sql.append("	      SELECT	sum(er.trade_money) no_settlement_money,	er.store_id FROM es_reckoning er WHERE");
		sql.append("		  er.trade_type = 2 AND er.trade_status = 3");
		sql.append("		  GROUP BY er.store_id");
		sql.append("		) d ON s.store_id = d.store_id");
		sql.append("     )");
		sql.append("LEFT JOIN (");
		sql.append("	SELECT 	service_store_id,sum(need_pay_money) no_service_money");
		sql.append("	FROM es_order WHERE");
		sql.append("	pay_status = 2	AND (`status` = 2 OR `status` = 5)");
		sql.append("	GROUP BY service_store_id");
		sql.append(") e ON s.store_id = e.service_store_id");
		if(!StringUtil.isNull(store_name)){
			sql.append(" where s.store_name like '%").append(store_name).append("%'");
		}
		sql.append(" GROUP BY 	s.store_id");
		Page page = daoSupport.queryForPage(sql.toString(), pageNo, pageSize,todayTime,yestodayTime,todayTime,yestodayTime);
		return page;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IReckoningManager getReckoningManager() {
		return reckoningManager;
	}

	public void setReckoningManager(IReckoningManager reckoningManager) {
		this.reckoningManager = reckoningManager;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

}
