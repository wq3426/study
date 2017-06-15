package com.enation.app.b2b2c.core.service.order.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.order.IB2B2cOrderReportManager;
import com.enation.app.shop.core.model.PaymentLogType;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
@Component
public class B2B2cOrderReportManager extends BaseSupport implements IB2B2cOrderReportManager {

	@Override
	public Page listPayment(Map map, int pageNo, int pageSize, String order) {
		String sql = createTempSql(map);
		return this.daoSupport.queryForPage(sql, pageNo, pageSize);	
	}
	@SuppressWarnings("unused")
	private String  createTempSql(Map map){
		
		Integer stype = (Integer) map.get("stype");
		String keyword = (String) map.get("keyword");
		String start_time = (String) map.get("start_time");
		String end_time = (String) map.get("end_time");
		Integer status = (Integer) map.get("status");
		String sn = (String) map.get("sn");
		Integer paystatus = (Integer) map.get("paystatus");
		Integer payment_id = (Integer) map.get("payment_id");
		
		String sql = "select l.*,o.store_name as store_name from es_payment_logs l INNER JOIN es_order o ON o.sn=l.order_sn  where o.parent_id is NOT NULL  and l.payment_id>0 and l.type="+PaymentLogType.receivable.getValue();
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql+=" and o.sn like '%"+keyword+"%'";
				sql+=" or o.ship_name like '%"+keyword+"%'";
			}
		}
		
		if(sn!=null && !StringUtil.isEmpty(sn)){
			sql+=" and o.sn like '%"+sn+"%'";
		}
		
		if(paystatus!=null){
			sql+=" and o.status="+paystatus;
		}
		
		if(payment_id!=null){
			sql+=" and o.order_id in(select order_id from es_order where payment_id="+payment_id+")";
		}
		
		if(start_time!=null&&!StringUtil.isEmpty(start_time)){			
			long stime = com.enation.framework.util.DateUtil.getDateline(start_time+" 00:00:00");
			sql+=" and o.create_time>"+stime;
		}
		if(end_time!=null&&!StringUtil.isEmpty(end_time)){			
			long etime = com.enation.framework.util.DateUtil.getDateline(end_time +" 23:59:59", "yyyy-MM-dd HH:mm:ss");
			sql+=" and o.create_time<"+(etime*1000);
		}
		
		sql += " order by o.payment_id desc";
		return sql;
	}
	@Override
	public Page listRefund(int pageNo, int pageSize, String order,Map map) {
		
		String keyword = (String) map.get("keyword");
		StringBuffer sql =new StringBuffer("select * from es_sellback_list ");
		//判断关键字是否为空
		if(!StringUtil.isEmpty(keyword)){
			sql.append(" WHERE tradeno like '%"+keyword+"%' or sndto like '%"+keyword+"%'");
		}
		sql.append(" order by id desc ");
		Page webpage = this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
		return webpage;
	}

}
