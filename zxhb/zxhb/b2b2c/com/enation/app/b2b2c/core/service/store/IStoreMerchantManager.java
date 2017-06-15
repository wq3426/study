package com.enation.app.b2b2c.core.service.store;

import com.enation.app.base.core.model.MerchantInfo;
/**
 * @Description 
 *
 * @createTime 2016年8月26日 下午3:57:34
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public interface IStoreMerchantManager {

	/**
	 * @description 检验公司名称是否重复
	 * @date 2016年9月3日 下午4:10:19
	 * @param companyName
	 * @return int
	 */
	public int checkCompanyNameIsRepeat(String companyName);
	
	/**
	 * @description 校验邮箱是否重复
	 * @date 2016年9月3日 下午4:10:06
	 * @param email
	 * @return int
	 */
	public int checkEmailIsRepeat(String email);
	
	/**
	 * @description 保存商户信息
	 * @date 2016年8月26日 下午3:08:07
	 * @param merchantInfo
	 * @return int
	 */
	public int saveMerchantInfo(MerchantInfo merchantInfo);

	


}
