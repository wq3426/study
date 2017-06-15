package com.enation.app.base.core.service;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.framework.database.Page;

/**
 * 会员管理接口
 * @author kingapex
 *2010-4-30上午10:07:35
 */
public interface IMemberManager {
	
	/**
	 * 添加会员
	 * 
	 * @param member
	 * @return 0：用户名已存在，1：添加成功
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int add(Member member);
	
	
	/**
	 * 会员注册 
	 * @param member
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public int register(Member member);

	
	
	/**
	 * 某个会员邮件注册验证成功
	 * 此方法会更新为验证成功，并激发验证成功事件
	 * @param 会员实体
	 *  
	 */
	public void checkEmailSuccess(Member member);
	
	
	
	
	/**
	 * 检测用户名是否存在
	 * 
	 * @param name
	 * @return 存在返回1，否则返回0
	 */
	public int checkname(String name);
	
	/**
	 * 检测邮箱是否存在
	 * 
	 * @param name
	 * @return 存在返回1，否则返回0
	 */
	public int checkemail(String email);

	/**
	 * 修改会员信息
	 * 
	 * @param member
	 * @return
	 */
	public Member edit(Member member);

	/**
	 * 根据会员id获取会员信息
	 * 
	 * @param member_id
	 * @return
	 */
	public Member get(Integer member_id);

	/**
	 * 删除会员
	 * 
	 * @param id
	 */
	public void delete(Integer[] id);

	/**
	 * 根据用户名称取用户信息
	 * 
	 * @param uname
	 * @return 如果没有找到返回null
	 */
	public Member getMemberByUname(String uname);
	
	/**
	 * 根据邮箱取用户信息
	 * @param email
	 * @return
	 */
	public Member getMemberByEmail(String email);

	/**
	 * 根据手机取用户信息
	 * @param mobile
	 * @return
	 */
	public Member getMemberByMobile(String mobile);
	
	
	/**
	 * 修改当前登录会员的密码
	 * 
	 * @param password
	 */
	public void updatePassword(String mobile,String password);
	
	
	
	/**
	 * 更新某用户的密码
	 * @param memberid
	 * @param password
	 */
	public void updatePassword(Integer memberid,String password);
	
	/**
	 * 找回密码使用code
	 * @param code
	 */
	public void updateFindCode(Integer memberid,String code);
	
	
	/**
	 * 增加预存款
	 */
	public void addMoney(Integer memberid,Double num);
	
	
	
	/**
	 * 减少预存款
	 * @param memberid
	 * @param num
	 */
	public void cutMoney(Integer memberid,Double num);
	
	
	
	
	/**
	 * 会员登录 
	 * @param username 用户名
	 * @param password 密码
	 * @return 1:成功, 0：失败
	 */
	@Transactional(propagation = Propagation.REQUIRED) 
	public int login(String username,String password);
	/**
	 * 会员登录 
	 * @param username 用户名
	 * @param password 密码
	 * @return 1:成功, 0：失败
	 */
	@Transactional(propagation = Propagation.REQUIRED) 
	public int loginWithCookie(String username, String password);
	
	/**
	 * 会员注销
	 */
	public void logout();
	
	
	
	/**
	 * 管理员以会员身份登录
	 * @param username 要登录的会员名称
	 * @return 0登录失败，无此会员
	 * @throws  RuntimeException 无权操作
	 */
	public int loginbysys(String username);
	
	
	/**
	 * 更新某个会员的等级
	 * @param memberid
	 * @param lvid
	 */
	public void updateLv(int memberid,int lvid);
	
	/**
	 * 会员搜索
	 * @param keyword
	 * @param lvid
	 * @return
	 */
	public List<Member> search(Map memberMap);

	/**
	 * 会员搜索 带分页
	 * @param memberMap
	 * @param page
	 * @param pageSize
	 * @param other
	 * @return
	 */
	public Page searchMember(Map memberMap,Integer page,Integer pageSize,String other,String order);
	
	/**
	 * 会员搜索 无店铺会员
	 * @param memberMap
	 * @param page
	 * @param pageSize
	 * @param other
	 * @return
	 */
	public Page searchMemberNoShop(Map memberMap,Integer page,Integer pageSize,String other,String order);
	
	/**
	 * 检测手机号
	 * @param phone
	 * @return
	 */
	public int checkMobile(String phone);
	 

	/**
	 * 批量修改用户状态
	 * @param memeberids
	 */
	public void updateStatus(Integer[] memeberids,Integer status);
	/**
	 * 查询店铺会员
	 * @param memeberids
	 */
	public Page MemberList(Integer page,Integer pageSize,Integer storeid, Map map);
	
	/**
	 * 查询解约店铺会员
	 * @param memeberids
	 */
	public Page MemberListBreak(Integer page,Integer pageSize,Integer storeid);
	
	/**
	 * 
	 * 保养 
	 * @param price
	 * @param time
	 * @param detail
	 * @param distance
	 */
	public void maintain(double price,long time,String detail,int  distance,int member_id);
	
	
	/**
	 * 保险
	 * @param insuranceName
	 * @param price
	 * @param outtime
	 */
	public void insurance(String insuranceName,double price,Long outtime,int member_id);

	
	/**
	 * 修改用户信息
	 * @param userObj
	 * @return
	 */
	public List updateUserInfo(JSONObject userObj);


	/**
	 * 修改用户头像
	 * @param username
	 * @param imgUrl
	 */
	public void updateUserFace(String username, String imgUrl);

	
	/**
	 * 查询解约用户
	 * @param parseInt
	 * @param pageSize
	 * @param store_id
	 * @param map
	 * @return
	 */
	public Page discontractMemberList(Integer page,Integer pageSize,Integer storeid, Map map);


	/**
	 * 用户车辆保险管理列表
	 * @param parseInt
	 * @param pageSize
	 * @param store_id
	 * @param map
	 * @return
	 */
	public Page MemberInsureList(Integer page,Integer pageSize,Integer storeid, Map map);

	/**
	 * 用户保养管理列表
	 * @param parseInt
	 * @param pageSize
	 * @param store_id
	 * @param map
	 * @return
	 */
	public Page MemberRepairList(Integer page,Integer pageSize,Integer storeid, Map map);

	/**
	 * 店铺签约用户数据统计
	 * @param store_id 
	 * @return
	 */
	public net.sf.json.JSONObject getUserContractInfo(Integer store_id);


	public List<Member> getRepairMemberByStoreId(Map result);

	/**
	 * @description 根據用戶ID查詢用戶密碼
	 * @date 2016年9月14日 下午4:08:54
	 * @param memberId
	 * @return String
	 * @throws Exception 
	 */
	public String queryMemberPasswordByUsername(String username) throws Exception;

	/**
	 * @description 修改用户密码
	 * @date 2016年9月14日 下午4:36:24
	 * @param newPassword
	 * @param memberId
	 * @return int
	 * @throws Exception 
	 */
	public int updateMemberPassword(String newPassword, String username) throws Exception;

	/**
	 * @description 更新排名flag
	 * @date 2016年9月24日 上午11:22:40
	 * @param userName
	 * @param rankingFlag
	 * @throws Exception 
	 */
	public void updateRankingFlag(String userName, String rankingFlag) throws Exception;

	/**
	 * @description 校验原手势锁是否正确
	 * @date 2016年9月24日 上午11:23:33
	 * @param userName
	 * @param oldGestureLock
	 * @return boolean
	 */
	public boolean checkOldGestureLockIsCorrect(String userName, String oldGestureLock);

	/**
	 * @description 更新手势锁
	 * @date 2016年9月24日 上午11:23:38
	 * @param userName
	 * @param newGestureLock
	 * @throws Exception 
	 */
	public void updateGestureLock(String userName, String newGestureLock) throws Exception;

	/**
	 * @description 查询会员信息
	 * @date 2016年10月17日 下午3:40:41
	 * @param pageNum
	 * @param pageSize
	 * @param conditions
	 * @return Page
	 */
	public Page queryMemberInfoByPage(int pageNum, int pageSize, Map<String, String> conditions);

	/**
	 * @description 查询会员的车辆信息
	 * @date 2016年10月18日 下午5:27:00
	 * @param memberId
	 * @return List
	 */
	public List<Map> queryMemberCarInfo(int memberId);

	/**
	 * @description 查询车辆使用性质 
	 * @date 2016年10月19日 下午5:31:44
	 * @return List<Map>
	 */
	public List<Map> queryCarUseType();

	/**
	 * @description 更新车辆信息
	 * @date 2016年10月19日 下午7:52:51
	 * @param carinfoid
	 * @param params
	 * @throws Exception 
	 */
	public void updateCarInfoAndCarModel(int carinfoid, int carmodelid, int brand_id, Map<String, String> params) throws Exception;

	/**
	 * @description 查询品牌名称
	 * @date 2016年10月19日 下午8:21:59
	 * @param brand_id
	 * @return String
	 */
	public String queryBrandName(int brand_id);

	/**
	 * @description 查询品牌列表
	 * @date 2016年10月19日 下午8:28:32
	 * @return List<Map>
	 */
	public List<Map> queryBrandList();

	/**
	 * @description 
	 * @date 2016年10月20日 下午2:18:10
	 * @param carplate
	 * @return Page
	 */
	public Page queryCarBonusDetail(int pageNum, int pageSize, Map<String, String> conditions);

	/**
	 * @description 查询车辆奖励明细
	 * @date 2016年10月20日 下午2:17:38
	 * @param carplate
	 * @return Page
	 */
	public Page queryCarIncomeDetail(int pageNum, int pageSize, Map<String, String> conditions);

	/**
	 * @description 根据车牌号查询车辆信息
	 * @date 2016年10月21日 下午6:19:24
	 * @param carplate
	 * @return Map
	 */
	public Map queryCarInfoByCarplate(String carplate);

	/**
	 * @description 查询优惠券数量
	 * @date 2016年10月21日 下午9:01:54
	 * @param carplate
	 * @return int
	 */
	public int queryBonusCount(String carplate);

	/**
	 * @description 查询车系、年款列表
	 * @date 2016年10月24日 上午10:12:27
	 * @param key
	 * @param args
	 * @return List<String> 
	 */
	public List<String> queryCarSeriesOrNKList(String key, Object... args);

}