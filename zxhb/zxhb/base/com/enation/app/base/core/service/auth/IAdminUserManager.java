package com.enation.app.base.core.service.auth;

import java.util.List;
import java.util.Map;

import com.enation.eop.resource.model.AdminUser;
import com.enation.framework.database.Page;

import net.sf.json.JSONObject;

/**
 * 管理员管理接口
 * @author kingapex
 * 2010-11-7下午05:49:12
 */
public interface IAdminUserManager {
	


	/**
	 * 为当前站点添加一个管理员
	 * @param adminUser
	 * @return 添加的管理员id
	 */
	public Integer add(AdminUser adminUser);
	
	
	
	/**
	 * 为某个站点添加管理员
	 * @param userid
	 * @param siteid
	 * @param adminUser
	 * @return 添加的管理员id
	 */
	public Integer add(int userid,int siteid,AdminUser adminUser);
	
	
	
	
	
	/**
	 * 管理员
	 * @param username
	 * @param password 此处为未经过md5加密的密码
	 * @return 返回登录成功的用户id
	 * @throws RuntimeException 登录失败抛出此异常，登录失败原因可通过getMessage()方法获取
	 */
	public int  login(String username,String password);
	
 
	
	
	/**
	 * 系统登录
	 * @param username
	 * @param password 此处为未经过md5加密的密码
	 * @return 返回登录成功的用户id
	 * @throws RuntimeException 登录失败抛出此异常，登录失败原因可通过getMessage()方法获取
	 */
	public int loginBySys(String username, String password) ;
	
	
	
	/**
	 * 读取管理员信息
	 * @param id
	 * @return
	 */
	public AdminUser get(Integer id);
	
	
	/**
	 * 修改管理员信息 
	 * @param eopUserAdmin
	 */
	public void edit(AdminUser eopUserAdmin);
	
	
	/**
	 * 删除管理员
	 * @param id
	 * @throws RuntimeException  当删除最后一个管理员时
	 */
	public void delete(Integer id);
	
	
	/**
	 * 检查是否为最后一个管理员
	 * @return 
	 */
	public int checkLast();
 
  
	/**
	 * 读取此站点所有管理员
	 * @return
	 */
	public List list( ) ;
	
	
	/**
	 * 读取某站点的所有的管理员
	 * @param userid
	 * @param siteid
	 * @return
	 */
	public List<Map> list(Integer userid,Integer siteid);
	
	
	/**
	 * 读取某个角色的所有管理员
	 * @return
	 */
	public List<AdminUser> listByRoleId(int roleid);
	
	
	
	
	/**
	 * 清除本站点的所有管理员
	 * 一般安装所用
	 */
	public void clean();
	
	/**
	 * 根据用户名查询是否存在
	 * @param username
	 */
	public boolean is_exist(String username);


	/**
	 * 添加版本记录到数据库表
	 * @param vNumber
	 * @param fsAppUrl
	 */
	public void addAppUpdateRecord(String vNumber, String fsAppUrl);


	/**
	 * 校验app是否需要升级
	 * @param versionNo
	 * @return
	 */
	public JSONObject checkAppVersion(String versionNo);

	/**
	 * 获取app列表
	 * @param keyMap
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page searchApp(Map keyMap, int page, int pageSize);

	/**
	 * 删除app记录
	 * @param app_id
	 */
	public void deleteApp(Integer[] app_id);
	
	/**
	 * 指定发布的版本，并更新apk到主页二维码扫描得到的安装目录
	 * @param release_app_id
	 * @return
	 */
	public JSONObject releaseApp(Integer release_app_id);


	/**
	 * 获取所有的版本号字符串数组
	 * @return
	 */
	public String getVersionNoArray();



	/** @description 修改密码
	 * @date 2016年10月27日 下午5:42:11
	 * @param adminUser
	 * @return void
	 */
	public void editPassword(AdminUser adminUser);

}
