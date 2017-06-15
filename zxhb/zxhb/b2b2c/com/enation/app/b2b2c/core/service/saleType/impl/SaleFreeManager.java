package com.enation.app.b2b2c.core.service.saleType.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.service.saleType.ISaleFreeManager;
import com.enation.app.base.core.model.SaleFree;
import com.enation.app.base.core.model.StoreCost;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
@Component
public class SaleFreeManager extends BaseSupport implements ISaleFreeManager{

	@Override
	public Page saleFreeList(Map saleFreeMap, int pageNo, int pageSize) {
		String sql = createTempSql(saleFreeMap);
		return this.daoSupport.queryForPage(sql, pageNo, pageSize);
	}
	/**
	 * 生成查询sql
	 * @param map
	 * @param sortField
	 * @param sortType
	 * @return
	 */
	private String  createTempSql(Map map){
		Integer stype = (Integer) map.get("stype");
		String keyword = (String) map.get("keyword");
		String typeId =  (String) map.get("typeId");
		Integer storeLevel = (Integer) map.get("storeLevel");
		Integer isFree = (Integer) map.get("isFree");
		StringBuffer sql =new StringBuffer();
 
		sql.append("SELECT esf.*,est.type_name,esl.level_name FROM es_sale_free esf,es_sale_type est,es_store_level esl "
				 + "WHERE esf.type_id=est.type_id AND esf.level_id=esl.level_id "); //查询营销模板数据
		
		
		if(stype!=null && keyword!=null){			
			if(stype == 0){
				sql.append(" and (est.type_name like '"+keyword+"%'");
				sql.append(" or esl.level_name like '"+keyword+"%')");
			}
		}
		
		if(typeId != null&& !StringUtil.isEmpty(typeId)){
			sql.append(" and est.type_id = "+typeId);
		}
		
		if(storeLevel!=null){
			sql.append(" and esl.level_id = "+storeLevel);
		}
		if(isFree!=null){
			sql.append(" and esf.isFree = "+isFree);
		}
		sql.append(" order by esf.type_id");
		return sql.toString();
	}
	
	@Override
	public void addSaleFree(SaleFree saleFree) throws Exception {
		String sql = "SELECT * FROM es_sale_free WHERE type_id="+saleFree.getType_id()+" AND level_id="+saleFree.getLevel_id()+" AND limit_date = "+saleFree.getLimit_date()+
					 " AND isFree ="+saleFree.getIsFree()+" AND use_num = "+saleFree.getUse_num()+" AND price = "+saleFree.getPrice();
		List saleFreeList = this.daoSupport.queryForList(sql);
		if(saleFreeList.isEmpty()){
			this.daoSupport.insert("es_sale_free", saleFree);
		}else{
			throw new Exception();
		}
	}

	@Override
	public SaleFree getSaleFree(Integer id) {
		String sql="SELECT esf.*,est.type_name,esl.level_name FROM es_sale_free esf,es_sale_type est,es_store_level esl WHERE esf.type_id=est.type_id AND esf.level_id=esl.level_id AND esf.id=?";
		return (SaleFree) this.baseDaoSupport.queryForObject(sql,SaleFree.class, id);
	}

	@Override
	public void editSaleFree(SaleFree saleFree) {
		if(saleFree.getLimit_date() == null){
			saleFree.setLimit_date("0");
		}
		if(saleFree.getPrice()== null){
			saleFree.setPrice(0.00);
		}
		String sql = "update es_sale_free set type_id = "+saleFree.getType_id()+",use_num = "+saleFree.getUse_num()+",level_id = "+saleFree.getLevel_id()+
					 ",limit_date = "+saleFree.getLimit_date()+",price = "+saleFree.getPrice()+",isFree = "+saleFree.getIsFree()+ ",isInitia = "+saleFree.getIsInitia()+" where id ="+saleFree.getId(); 
		this.daoSupport.execute(sql);
	}

	@Override
	public void delSaleFree(Integer id) {
		String sql="DELETE from es_sale_free WHERE id=?";
		this.daoSupport.execute(sql, id);
	}

	@Override
	public Page saleAuditList(Map saleFreeMap, int pageNo, int pageSize) {
		String sql = createAuditSql(saleFreeMap);
		return this.daoSupport.queryForPage(sql, pageNo, pageSize);	
	}
	/**
	 * 生成查询sql
	 * @param map
	 * @param sortField
	 * @param sortType
	 * @return
	 */
	private String  createAuditSql(Map map){
		Integer stype = (Integer) map.get("stype");
		String keyword = (String) map.get("keyword");
		String typeId =  (String) map.get("typeId");
		String storeName =  (String) map.get("storeName");
		Integer storeLevel = (Integer) map.get("storeLevel");
		String auditStatus = (String) map.get("auditStatus");
		StringBuffer sql =new StringBuffer();
		
		sql.append("SELECT es.store_name,es.disabled,esl.level_name,est.type_name, esa.*  FROM es_store es ,es_store_level esl,es_sale_audit esa ,es_sale_type est"+ 
				  " WHERE esa.store_id = es.store_id AND es.store_level  = esl.level_id AND esa.sale_type_id = est.type_id"); //查询营销模板数据
		
		
		if(stype!=null && keyword!=null){			
			if(stype == 0){
				sql.append(" and (es.store_name like '"+keyword+"%'");
				sql.append(" or est.type_name like '"+keyword+"%')");
			}
		}
		
		if(typeId != null&& !StringUtil.isEmpty(typeId)){
			sql.append(" and est.type_id = "+typeId);
		}
		
		if(storeName != null&& !StringUtil.isEmpty(storeName)){
			sql.append(" and es.store_name like '"+storeName+"%'");
		}
		
		if(storeLevel!=null){
			sql.append(" and esl.level_id = "+storeLevel);
		}
		
		if(auditStatus != null&& !StringUtil.isEmpty(auditStatus)){
			sql.append(" and esa.audit_status = "+auditStatus);
		}
		return sql.toString();
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void audit_pass(Integer id,String storeId,String typeId,Integer auditNum) {
		String sql = "select * from es_store_cost where store_id = "+storeId+" and type_id = "+typeId+" and isFree = 0";
		StoreCost storeCost = (StoreCost) this.baseDaoSupport.queryForObject(sql, StoreCost.class);
		this.daoSupport.execute("update  es_sale_audit set audit_status = 1 where id=?",id);
		this.daoSupport.execute("update  es_store_cost set surp_num = "+(storeCost.getSurp_num()+auditNum)+ " where id = ?",storeCost.getId());
	}

	@Override
	public void audit_fail(Integer sign, Integer id) {
		this.daoSupport.execute("update  es_sale_audit set audit_status = 2 where id=?",id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SaleFree> getSaleFreeByTypeId(int saleType, Integer store_level,int isFree) {
		String sql;
		if(isFree == 0){
			sql = "select * from  es_sale_free where type_id = ? and level_id = ? and isFree = ? and  limit_date = '1' and isInitia='1' and price = 0 order by use_num";
		}else{
			sql = "select * from  es_sale_free where type_id = ? and level_id = ? and isFree = ?";
		}
		return this.baseDaoSupport.queryForList(sql,saleType,store_level,isFree);
	}

	@Override
	public void addSaleAudit(Integer store_id, String typeId, Integer auditNum) {
		// TODO Auto-generated method stub
		String sql = "insert into es_sale_audit(store_id,sale_type_id,audit_num,audit_status) values("+store_id+","+typeId+","+auditNum+","+'0'+")";
		this.baseDaoSupport.execute(sql);
	}

}
