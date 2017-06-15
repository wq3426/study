package com.enation.app.base.core.service.auth.impl;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.AppVersion;
import com.enation.app.base.core.model.AuthAction;
import com.enation.app.base.core.plugin.user.AdminUserPluginBundle;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;
import com.enation.framework.database.Page;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 管理员管理实现
 * 
 * @author kingapex 2010-11-5下午06:49:02
 */
public class AdminUserManagerImpl extends BaseSupport<AdminUser> implements	IAdminUserManager {
	private AdminUserPluginBundle adminUserPluginBundle;
	private IPermissionManager permissionManager;

	public void clean() {
		this.baseDaoSupport.execute("truncate table adminuser");
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public Integer add(AdminUser adminUser) {
		adminUser.setPassword(StringUtil.md5(adminUser.getPassword()));
		// 添加管理员
		this.baseDaoSupport.insert("adminuser", adminUser);
		int userid = this.baseDaoSupport.getLastId("adminuser");

		// 给用户赋予角色
		permissionManager.giveRolesToUser(userid, adminUser.getRoleids());
		this.adminUserPluginBundle.onAdd(userid);
		return userid;
	}

	/**
	 * 为某个站点添加管理员
	 * 
	 * @param userid
	 * @param siteid
	 * @param adminUser
	 * @return 添加的管理员id
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer add(int userid, int siteid, AdminUser adminUser) {
		adminUser.setState(1);
		this.baseDaoSupport.insert("adminuser", adminUser);
		return this.baseDaoSupport.getLastId("adminuser");
	}

	public int checkLast() {
		int count = this.baseDaoSupport.queryForInt("select count(0) from adminuser");
		return count;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer id) {
		// 如果只有一个管理员，则抛出异常
		if (this.checkLast() == 1) {
			throw new RuntimeException("必须最少保留一个管理员");
		}

		// 清除用户角色
		permissionManager.cleanUserRoles(id);

		// 删除用户基本信息
		this.baseDaoSupport.execute("delete from adminuser where userid=?", id);
		this.adminUserPluginBundle.onDelete(id);
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void edit(AdminUser adminUser) {
		// 给用户赋予角色
		permissionManager.giveRolesToUser(adminUser.getUserid(), adminUser.getRoleids());

		// 修改用户基本信息
		if (!StringUtil.isEmpty(adminUser.getPassword()))
			adminUser.setPassword(StringUtil.md5(adminUser.getPassword()));
		int userId = adminUser.getUserid();
		adminUser.setUserid(null); // 不设置为空，SQLServer更新出错
		this.baseDaoSupport.update("adminuser", adminUser, "userid=" + userId);
		this.adminUserPluginBundle.onEdit(userId);
	}

	public AdminUser get(Integer id) {
		return this.baseDaoSupport.queryForObject("select * from adminuser where userid=?", AdminUser.class, id);
	}

	public List list() {
		return this.baseDaoSupport.queryForList("select * from adminuser order by dateline", AdminUser.class);
	}

	public List<Map> list(Integer userid, Integer siteid) {
		String sql = "select * from es_adminuser_" + userid + "_" + siteid;
		return this.daoSupport.queryForList(sql);
	}

	public List<AdminUser> listByRoleId(int roleid) {
		String sql = "select u.* from " + this.getTableName("adminuser")
				+ " u ," + this.getTableName("user_role")
				+ " ur where ur.userid=u.userid and ur.roleid=?";
		return this.daoSupport.queryForList(sql, AdminUser.class, roleid);
	}

	/**
	 * 管理员登录
	 * 
	 * @param username
	 * @param password
	 *            未经过md5加密的密码
	 * @return 登录成功返回管理员
	 * @throws RuntimeException
	 *             当登录失败时抛出此异常，登录失败的原因可通过getMessage()方法获取
	 */
	public int login(String username, String password) {
		return this.loginBySys(username, StringUtil.md5(password));
	}

	/**
	 * 系统登录
	 * 
	 * @param username
	 * @param password
	 *            此处为未经过md5加密的密码
	 * @return 返回登录成功的用户id
	 * @throws RuntimeException
	 *             登录失败抛出此异常，登录失败原因可通过getMessage()方法获取
	 */
	public int loginBySys(String username, String password) {
		String sql = "select * from adminuser where username=?";
		List<AdminUser> userList = this.baseDaoSupport.queryForList(sql, AdminUser.class, username);
		if (userList == null || userList.size() == 0) {
			throw new RuntimeException("此用户不存在");
		}
		AdminUser user = userList.get(0);

		if (!password.equals(user.getPassword())) {
			throw new RuntimeException("密码错误");
		}

		if (user.getState() == 0) {
			throw new RuntimeException("此用户已经被禁用");
		}

	 

		// 读取此用户的权限点，并设置给当前用户
		List<AuthAction> authList = this.permissionManager.getUesrAct(user.getUserid());
		user.setAuthList(authList);
 

		// 记录session信息
		WebSessionContext sessonContext = ThreadContextHolder.getSessionContext();
		sessonContext.setAttribute(UserConext.CURRENT_ADMINUSER_KEY, user);
		this.adminUserPluginBundle.onLogin(user);
		return user.getUserid();
	}

	@Override
	public boolean is_exist(String username) {
		boolean flag = false;
		int i =  this.daoSupport.queryForInt("select count(0) from es_adminuser where username=?", username);
		if(i!=0){
			flag=true;
		}
		return flag;
	}

	@Override
	public String getVersionNoArray() {
		try {
			String sql = "select version_no from es_app_record";
			List list = daoSupport.queryForList(sql);
			String versionNoArray = "";
			if(list.size() > 0){
				JSONArray array = JSONArray.fromObject(list);
				for(int i=0; i<array.size(); i++){
					versionNoArray += array.getJSONObject(i).getString("version_no") + ",";
				}
				versionNoArray = versionNoArray.substring(0, versionNoArray.lastIndexOf(","));
			}
			return versionNoArray;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void addAppUpdateRecord(String vNumber, String fsAppUrl) {
		try {
			String sql = "select count(*) count, release_flag from es_app_record where version_no=?";
			List list = daoSupport.queryForList(sql, vNumber);
			JSONObject obj = JSONObject.fromObject(list.get(0));
			int count = obj.getInt("count");
			if(count == 0){
				sql = "insert into es_app_record set version_no=?, app_url=?, create_time=?, update_time=?";
				daoSupport.execute(sql, vNumber, fsAppUrl, new Date().getTime(), new Date().getTime());
			}
			/**
			if(count > 0){
				sql = "update es_app_record set app_url=?, update_time=? where version_no=?";
				daoSupport.execute(sql, fsAppUrl, new Date().getTime(), vNumber);
				int release_flag = obj.getInt("release_flag");
				if(release_flag == 1){//如果是发布版本，同时更新上传的文件到二维码指向的目录
//					String filePath = SystemSetting.getFile_path();
					String filePath = SystemSetting.getStatic_server_path();
					int system_type = SystemSetting.getSystemType();//操作系统类型
					if(system_type == 2){//linux系统
						String sourceFilePath = filePath + fsAppUrl.replace(EopSetting.FILE_STORE_PREFIX, "");
						File file = new File(sourceFilePath);
						String desFilePath = "/var/lib/tomcat8-release/webapps/yingjia/fckfile/yingjia.apk";
//						String desFilePath = "/var/lib/tomcat8-6066/webapps/yingjia/fckfile/yingjia.apk";
						FileUtil.delete(desFilePath);
						FileUtil.createFile(file, desFilePath);
					}else if(system_type == 1){//windows系统，本地测试
						String sourceFilePath = filePath + fsAppUrl.replace(EopSetting.FILE_STORE_PREFIX, "");
						File file = new File(sourceFilePath);
						String desFilePath = "G:/files/app_file/yingjia.apk";
						FileUtil.delete(desFilePath);
						FileUtil.createFile(file, desFilePath);
					}
				}
			}else{
				sql = "insert into es_app_record set version_no=?, app_url=?, create_time=?, update_time=?";
				daoSupport.execute(sql, vNumber, fsAppUrl, new Date().getTime(), new Date().getTime());
			}
			**/
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	@Override
	public JSONObject checkAppVersion(String versionNo) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "select version_no, app_url from es_app_record where release_flag=1";
			List list = daoSupport.queryForList(sql);
			if(list.size() > 0){
				obj = JSONObject.fromObject(list.get(0));
				String release_version_no = obj.getString("version_no");
				Integer appVersion = Integer.valueOf(versionNo);
				Integer serverVersion = Integer.valueOf(release_version_no);
				if(!versionNo.trim().equals(release_version_no.trim()) && serverVersion > appVersion){//需要更新
					obj.put("flag", true);
				}else{
					obj.put("flag", false);
				}
			}else{
				obj.put("flag", false);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}

	@Override
	public Page searchApp(Map keyMap, int page, int pageSize) {
		String sql = "select * from es_app_record where release_flag=1";
		List list = daoSupport.queryForList(sql);
		
		String keyword = (String) keyMap.get("keyword");
		
		sql = "select * from app_record where 1=1 and release_flag=0";
		if(keyword !=null && !StringUtil.isEmpty(keyword)){
			sql+=" and version_no like '%"+keyword+"%'";
		}
		sql+=" order by version_no desc";
		Page webpage = this.baseDaoSupport.queryForPage(sql, page, pageSize);
		list.addAll((List)webpage.getResult());
		webpage.setResult(list);
		return webpage;
	}
	
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void deleteApp(Integer[] app_id) {
		try {
			if (app_id == null || app_id.equals("")) return;
			String id_str = StringUtil.arrayToString(app_id, ",");
			String filePath = SystemSetting.getFile_path();
//			String filePath = SystemSetting.getStatic_server_path();
			
			//删除上传的app文件
			String sql = "select app_url from es_app_record where app_id in (" + id_str+ ")";
			List list = daoSupport.queryForList(sql);
			JSONArray urlArray = JSONArray.fromObject(list);
			for(int i=0; i<urlArray.size(); i++){
				String app_url = urlArray.getJSONObject(i).getString("app_url").replace(EopSetting.FILE_STORE_PREFIX, "").replace("/files", "");
				String file = filePath + app_url.substring(0, app_url.lastIndexOf("/"));
				FileUtil.delete(file);
			}
			
			//删除app上传记录
			sql = "delete from app_record where app_id in (" + id_str+ ")";
			this.baseDaoSupport.execute(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	public JSONObject releaseApp(Integer release_app_id) {
		try {
			//重置发布状态
			String sql = "update es_app_record set release_flag=0";
			daoSupport.execute(sql);
			//指定发布版本
			sql = "update es_app_record set release_flag=1 where app_id=?";
			daoSupport.execute(sql, release_app_id);
			//获取发布的app版本文件，复制文件到首页app二维码扫描指向的目录
			sql = "select app_id, version_no, app_url from es_app_record where release_flag=1";
			List list = daoSupport.queryForList(sql);
			JSONObject obj = JSONObject.fromObject(list.get(0));
			String app_url = obj.getString("app_url");
			String filePath = SystemSetting.getFile_path();
//			String filePath = SystemSetting.getStatic_server_path();
			int system_type = SystemSetting.getSystemType();//操作系统类型
			if(system_type == 2){//linux系统
				String sourceFilePath = filePath + app_url.replace(EopSetting.FILE_STORE_PREFIX, "").replace("/files", "");
				File file = new File(sourceFilePath);
				String desFilePath = System.getProperty("catalina.home") + "/webapps/yingjia/fckfile/yingjia.apk";
				FileUtil.delete(desFilePath);
				FileUtil.createFile(file, desFilePath);
			}else if(system_type == 1){//windows系统，本地测试
				String sourceFilePath = filePath + app_url.replace(EopSetting.FILE_STORE_PREFIX, "").replace("/files", "");
				File file = new File(sourceFilePath);
				String desFilePath = System.getProperty("catalina.home") + "/webapps/yingjia.apk";
				FileUtil.delete(desFilePath);
				FileUtil.createFile(file, desFilePath);
			}
			
			return obj;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void editPassword(AdminUser adminUser) {
		  try{  
			  daoSupport.execute("update es_adminuser set password=? where userid = ?", adminUser.getPassword() ,adminUser.getUserid());
	      }catch(Exception e){
	        	 throw e; 
	      }
	}
		

	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}

	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}

	public AdminUserPluginBundle getAdminUserPluginBundle() {
		return adminUserPluginBundle;
	}

	public void setAdminUserPluginBundle(AdminUserPluginBundle adminUserPluginBundle) {
		this.adminUserPluginBundle = adminUserPluginBundle;
	}
}
