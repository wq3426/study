package com.enation.app.b2b2c.core.service.store.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.service.store.IStoreMerchantManager;
import com.enation.app.base.core.model.MerchantInfo;
import com.enation.eop.sdk.database.BaseSupport;

/**
 * @Description 商户信息管理
 *
 * @createTime 2016年8月25日 下午6:06:59
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
@Component
public class StoreMerchantManager extends BaseSupport<MerchantInfo> implements IStoreMerchantManager{

	@SuppressWarnings("rawtypes")
	@Override
	public int checkCompanyNameIsRepeat(String companyName) {
		String sql = 
			" SELECT                                  "+
			" 	t.id                                  "+
			" FROM                                    "+
			" 	es_store_merchantinfo t               "+
			" WHERE 1=1                               "+
			" AND t.delflag = '0'  					  "+
			" AND t.company_name = '"+companyName+"'  ";
		
		List<Map> merchantId = this.daoSupport.queryForList(sql);
		return merchantId.isEmpty() ? 0 : 1;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public int checkEmailIsRepeat(String email) {
		String sql = 
				" SELECT                                  "+
				" 	t.id                                  "+
				" FROM                                    "+
				" 	es_store_merchantinfo t               "+
				" WHERE 1=1                               "+
				" AND t.delflag = '0'  					  "+
				" AND t.email = '"+email+"'               ";
			
		List<Map> merchantId = this.daoSupport.queryForList(sql);
		return merchantId.isEmpty() ? 0 : 1;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public int saveMerchantInfo(MerchantInfo merchantInfo) {
		this.daoSupport.insert("es_store_merchantinfo", merchantInfo);	
		return 1;
	}

	
	

}
