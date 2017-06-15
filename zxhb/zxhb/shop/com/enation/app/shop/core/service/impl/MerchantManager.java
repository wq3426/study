package com.enation.app.shop.core.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MerchantInfo;
import com.enation.app.base.core.model.Store;
import com.enation.app.base.core.service.IMerchantManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * @Description 商户管理实现类
 *
 * @createTime 2016年8月30日 下午4:58:31
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
@Component
public class MerchantManager extends BaseSupport<MerchantInfo> implements IMerchantManager{

	@Override
	public List<MerchantInfo> queryMerchantInfoList() {
		String sql = 
				" SELECT                             "+  
				" 	t.id,                            "+  
				" 	t.company_name,                  "+  
				" 	t.brand,                         "+  
				" 	t.email,                         "+  
				" 	t.contact_name,                  "+  
				" 	t.contact_phone,                 "+  
				" 	t.address,                       "+  
				" 	t.create_time                    "+  
				" FROM                               "+  
				" 	es_store_merchantinfo t          "+  
				" WHERE 1=1                          "+
			    " order BY t.create_time DESC        ";
		
		List<MerchantInfo> merchantList = this.baseDaoSupport.queryForList(sql, MerchantInfo.class);
		return merchantList;
	}

	@Override
	public Page queryMerchantInfo(int pageNum, int pageSize, Map<String, String> conditions) {
		
		//拼装sql
		StringBuffer sql = new StringBuffer();
		sql.append(
			" SELECT                                                "+
			"   t.id,                                               "+
			"   t.company_name,                                     "+
			"   t.brand,                                            "+
			"   t.contact_name,                                     "+
			"   t.contact_phone,                                    "+
			"   t.email,                                            "+
			"   t.address,                                          "+
			"   CASE                                                "+
			"     WHEN t.signstatus = 0 THEN '未注册'                "+
			"     WHEN t.signstatus = 1 THEN '已注册'                "+
			"   END as signstatus,                                  "+
			"   t.create_time                                       "+
			" FROM                                                  "+
			" 	es_store_merchantinfo t                             "+
			" WHERE 1=1                                             "+
			" AND t.delflag = '0'                                   "
				);
		
		//获取简单查询条件
		String keyword = conditions.get("searchKeyword");
		if(!StringUtils.isEmpty(keyword)) {
			keyword = keyword.trim();
			sql.append(" AND ( t.company_name like '%" + keyword + "%'     "+
					      " OR t.brand like '%" + keyword + "%'            "+
					      " OR t.contact_phone like '%" + keyword + "%'    "+
					      " OR t.contact_name like '%" + keyword + "%' )   ");
			
			sql.append (" order by t.create_time desc");
			Page merchantInfoPage = this.daoSupport.queryForPage(sql.toString(),pageNum, pageSize, MerchantInfo.class);
			return merchantInfoPage;
		}
		
		//获取复杂查询条件
		String companyName = conditions.get("companyName");
		String brand = conditions.get("brand");
		String contactName = conditions.get("contactName");
		String contactPhone = conditions.get("contactPhone");
		String email = conditions.get("email");
		String address = conditions.get("address");
		String startTime = conditions.get("startTime");
		String endTime = conditions.get("endTime");
		
		if(!StringUtil.isEmpty(companyName)) {
			sql.append(" AND t.company_name like '%" + companyName.trim() + "%'");
		}
		if(!StringUtil.isEmpty(brand)) {
			sql.append(" AND t.brand like '%" + brand.trim() + "%'");
		}
		if(!StringUtil.isEmpty(contactName)) {
			sql.append(" AND t.contact_name like '%" + contactName.trim() + "%'");
		}
		if(!StringUtil.isEmpty(contactPhone)) {
			sql.append(" AND t.contact_phone = '" + contactPhone.trim() + "'");
		}
		if(!StringUtil.isEmpty(email)) {
			sql.append(" AND t.email = '" + email.trim() + "'");
		}
		if(!StringUtil.isEmpty(address)) {
			sql.append(" AND t.address = '" + address.trim() + "'");
		}
		if(!StringUtil.isEmpty(startTime)) {
			sql.append(" AND t.create_time >="+DateUtil.getDateline(startTime.trim()));
		}
		if(!StringUtil.isEmpty(endTime)) {
			endTime = endTime.trim() + " 23:59:59";
			sql.append(" AND t.create_time <="+DateUtil.getDateline(endTime, "yyyy-MM-dd HH:mm:ss"));
		}
		
		sql.append (" order by t.create_time desc");
		Page merchantInfoPage = this.daoSupport.queryForPage(sql.toString(),pageNum, pageSize, MerchantInfo.class);
		return merchantInfoPage;
	}

	@Override
	public int merchantDelete(Integer[] merchantIds) {
		if (merchantIds == null || merchantIds.equals("")) {
			return 2;
		}
		String id_str = StringUtil.arrayToString(merchantIds, ",");
		String sql = "delete from es_store_merchantinfo where id in (" + id_str + ")";
		this.baseDaoSupport.execute(sql);
		return 1;
	}

	@Override
	public MerchantInfo queryMerchantInfo(int merchantId) {
		String sql = 
				" SELECT                                                "+
				"   t.id,                                               "+
				"   t.company_name,                                     "+
				"   t.brand,                                            "+
				"   t.contact_name,                                     "+
				"   t.contact_phone,                                    "+
				"   t.email,                                            "+
				"   t.address,                                          "+
				"   t.signstatus                                        "+
				" FROM                                                  "+
				" 	es_store_merchantinfo t                             "+
				" WHERE 1=1                                             "+
				" AND t.id = '"+merchantId+"'                           ";
		
        List<MerchantInfo> list = this.baseDaoSupport.queryForList(sql,MerchantInfo.class);
		
		return (MerchantInfo) list.get(0);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveMerchantEdit(MerchantInfo merchantInfo) {
		
		//获取数据
		int merchantId = merchantInfo.getId();
		String company_name = merchantInfo.getCompany_name();
		String brand = merchantInfo.getBrand();
		String contact_name = merchantInfo.getContact_name();
		String contact_phone = merchantInfo.getContact_phone();
		String email = merchantInfo.getEmail();
		String address = merchantInfo.getAddress();
		
		//sql
		String sql =                                             
				" UPDATE es_store_merchantinfo t                  "+
				" SET                                             "+
				"   t.company_name = '"+company_name+"',          "+
				"   t.brand = '"+brand+"',                        "+
				"   t.contact_name = '"+contact_name+"',          "+
				"   t.contact_phone = '"+contact_phone+"',        "+
				"   t.email = '"+email+"',                        "+
				"   t.address = '"+address+"'                     "+
				" WHERE 1=1                                       "+
				" AND t.id = "+merchantId+"                       ";
		
		//更新数据
		baseDaoSupport.execute(sql);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int checkEmailIsRepeat(String email) {
		String sql = 
				" SELECT                                  "+
				" 	t.member_id                           "+
				" FROM                                    "+
				" 	es_member t                           "+
				" WHERE 1=1                               "+
				" AND t.username = '"+email+"'            ";
			
		List<Map> memberId = this.daoSupport.queryForList(sql);
		return memberId.isEmpty() ? 0 : 1;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int saveMemberRegisterInfo(Member member) {
		baseDaoSupport.insert("es_member", member);
		int memberid = this.baseDaoSupport.getLastId("member");
		return memberid;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int updateMember(Map<String, Integer> map,int memberId) {
		daoSupport.update("es_member", map, "member_id="+memberId);
		return 1;
	}

	@Override
	public boolean checkEmailIsExist(String email) {
		String sql = 
				" SELECT                                  "+
				" 	t.id                                  "+
				" FROM                                    "+
				" 	es_store_merchantinfo t               "+
				" WHERE 1=1                               "+
				" AND t.email = '"+email+"'               ";
			
		List<Map> merchantId = this.daoSupport.queryForList(sql);
		return merchantId.isEmpty() ? false : true;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int updateMerchantStatus(String email) {
		String sql = 
				" UPDATE es_store_merchantinfo t              "+
				" SET                                         "+
				"   t.signstatus = '1'                        "+ 
				" WHERE 1=1                                   "+
				" AND t.email = '"+ email +"'                 ";
		this.daoSupport.execute(sql);
		return 1;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int opeanStore(Store store) {
		baseDaoSupport.insert("es_store", store);
		int storeId = this.baseDaoSupport.getLastId("store");
		return storeId;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int initStoreBalance(int storeId) {
		double banlance = 0D;
		String sql = "INSERT INTO es_store_balance VALUES ("+storeId+", "+banlance+")";
		baseDaoSupport.execute(sql);
		return 1;
	}


	

}
