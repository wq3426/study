package com.enation.app.base.core.service;

import java.util.List;
import java.util.Map;


import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MerchantInfo;
import com.enation.app.base.core.model.Store;
import com.enation.framework.database.Page;

/**
 * @Description 商户管理接口
 *
 * @createTime 2016年8月30日 下午4:57:23
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public interface IMerchantManager {

	/**
	 * @description 查询商户信息列表
	 * @date 2016年8月26日 下午4:53:07
	 * @return List<MerchantInfo>
	 */
	public List<MerchantInfo> queryMerchantInfoList();

	/**
	 * @description 查询商户信息列表（带条件分页查询）
	 * @date 2016年8月31日 上午11:34:09
	 * @param pageNum
	 * @param pageSize
	 * @param conditions
	 * @return Page
	 */
	
	public Page queryMerchantInfo(int pageNum, int pageSize, Map<String, String> conditions);

	/**
	 * @description 删除商户信息（逻辑删除）
	 * @date 2016年9月2日 下午4:42:00
	 * @param merchantId
	 * @return int
	 */
	public int merchantDelete(Integer[] merchantId);

	/**
	 * @description 根据id查询商户信息
	 * @date 2016年9月3日 下午7:34:24
	 * @param merchantId
	 * @return MerchantInfo
	 */
	public MerchantInfo queryMerchantInfo(int merchantId);

	/**
	 * @description 更新商家信息
	 * @date 2016年9月5日 上午10:12:18
	 * @param merchantInfo
	 * @return void
	 */
	public void saveMerchantEdit(MerchantInfo merchantInfo);

	/**
	 * @description 校验注册邮箱是否重复
	 * @date 2016年9月5日 下午4:43:57
	 * @param email
	 * @return int
	 */
	public int checkEmailIsRepeat(String email);

	/**
	 * @description 商家注册
	 * @date 2016年9月5日 下午5:23:32
	 * @param member
	 * @return int
	 */
	public int saveMemberRegisterInfo(Member member);

	/**
	 * @description 更新用户店铺信息
	 * @date 2016年9月14日 下午2:37:22
	 * @param map
	 * @param memberId 
	 * @return int
	 */
	public int updateMember(Map<String, Integer> map, int memberId);	

	/**
	 * @description 校验商户邮箱是否存在
	 * @date 2016年9月14日 下午3:14:01
	 * @param email
	 */
	public boolean checkEmailIsExist(String email);

	/**
	 * @description 将商户状态更新为已注册
	 * @date 2016年9月14日 下午3:19:56
	 * @param email
	 * @return
	 */
	public int updateMerchantStatus(String email);

	/**
	 * @description 注册完成自动开店
	 * @date 2016年9月14日 下午2:30:28
	 * @param store
	 * @return int
	 */
	public int opeanStore(Store store);
	
	/**
	 * @description 
	 * @date 2016年9月14日 下午2:51:30
	 * @param storeId
	 * @return int
	 */
	public int initStoreBalance(int storeId);
	
}
