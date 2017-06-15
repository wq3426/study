package com.enation.app.b2b2c.core.service.saleType.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.saleType.ISaleAuditManager;
import com.enation.app.base.core.model.SaleFree;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.DateUtil;
import com.enation.framework.database.Page;
@Component
public class SaleAuditManager extends BaseSupport implements ISaleAuditManager{

	@Override
	public void addSaleAudit(Integer store_id,String typeId, SaleFree saleFree,int auditStatus) {
		// TODO Auto-generated method stub
		long currenTime = System.currentTimeMillis();
		String sql = "insert into es_sale_audit(store_id,sale_type_id,audit_num,limit_date,apply_date,audit_status,isFree) values("
					  +store_id+","+typeId+","+saleFree.getUse_num()+","+saleFree.getLimit_date() +","+currenTime/1000+",'"+auditStatus+"',"+saleFree.getIsFree()+")";
		this.baseDaoSupport.execute(sql);
	}

	@Override
	public Page getAllAudit(int pageNo, int pageSize, Map map, Integer store_id) {
		// TODO Auto-generated method stub
		String saleType=String.valueOf(map.get("saleType")); 
		String status= String.valueOf(map.get("status")); 
		String isFree = String.valueOf(map.get("isFree")); 
		String startTime = String.valueOf(map.get("startTime"));
		String endTime = String.valueOf(map.get("endTime"));
		
		StringBuffer sql =new StringBuffer(" SELECT esa.*,type_name FROM es_sale_audit esa,es_sale_type est WHERE esa.sale_type_id = est.type_id AND store_id =  "+ store_id);
		

		if(!saleType.isEmpty()&&!saleType.equals("null")){
			sql.append(" AND esa.sale_type_id = "+ saleType);
		}
		
		if(!status.isEmpty()&&!status.equals("null")){
			sql.append(" AND esa.audit_status = "+ status);
		}
		
		if(!isFree.isEmpty()&&!isFree.equals("null")){
			sql.append(" AND esa.isFree = "+ isFree);
		}
		
		if(!startTime.isEmpty()&&!startTime.equals("null")){
			Long sTime = DateUtil.toLong(startTime);
			sql.append(" AND esa.apply_date >="+ sTime);
		}
		
		if(!endTime.isEmpty()&&!endTime.equals("null")){
			Long eTime = DateUtil.toLong(endTime);
			sql.append(" AND esa.apply_date <="+eTime);
		}
		
		sql.append(" order by esa.apply_date desc");
		Page rpage =  this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
		
		return rpage;

	}


}
