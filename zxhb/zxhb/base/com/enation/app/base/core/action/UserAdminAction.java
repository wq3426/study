package com.enation.app.base.core.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.user.AdminUserPluginBundle;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.base.core.service.auth.IPermissionManager;
import com.enation.app.base.core.service.auth.IRoleManager;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.StringUtil;

/**
 * 站点管理员管理
 * @author kingapex
 * 2010-11-5下午04:28:22新增角色管理
 * @author LiFenLong 2014-4-2;4.0改版
 * @author Kanon 2015-12-16 version 1.1 添加注释
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("userAdmin")
@Results({
	@Result(name="success", type="freemarker", location="/core/admin/user/useradmin.html"), 
	@Result(name="add", type="freemarker", location="/core/admin/user/addUserAdmin.html"), 
	@Result(name="edit", type="freemarker", location="/core/admin/user/editUserAdmin.html"),
	@Result(name="editPassword", type="freemarker", location="/core/admin/user/editUserPassword.html")
})
public class UserAdminAction extends WWAction {
	private AdminUserPluginBundle adminUserPluginBundle;
	private IAdminUserManager adminUserManager;
	private IRoleManager roleManager;
	private IPermissionManager permissionManager;
	private AdminUser adminUser;
	private Integer id;
	private List roleList;
	private List userRoles;
	private int[] roleids;
	private List userList;
	private String newPassword; //新密码
	private String updatePwd;//是否修改密码
	private List<String> htmlList;
	
	/***
	 * 跳转至站点管理员列表
	 * @return
	 */
	public String list() {
		return SUCCESS;
	}
	/**
	 * 获取站点管理员JSON列表
	 * @param userList 管理员列表
	 * @return 站点管理员JSON列表
	 */
	public String listJson(){
		userList= this.adminUserManager.list();
		this.showGridJson(userList);
		return this.JSON_MESSAGE;
	}

	/***
	 * 跳转添加管理员
	 * @param roleList 角色列表
	 * @return
	 */
	public String add() throws Exception {
		roleList = roleManager.list();
		return "add";
	}
	
	/**
	 * 新增管理员
	 * @param adminUser 管理员
	 * @param roleids 选择角色列表
	 * @return 新增状态
	 */
	public String addSave() throws Exception {
		try{
			//判断是否管理员重名
			boolean flag = this.adminUserManager.is_exist(adminUser.getUsername());
			if(flag){
				this.showErrorJson("用户名已存在!");
			}else{
				adminUser.setRoleids(roleids);
				adminUserManager.add(adminUser);
				this.showSuccessJson("新增管理员成功");
			}
		 } catch (RuntimeException e) {
			 this.showErrorJson("新增管理员失败");
			 logger.error("新增管理员失败", e);
		 }	
		return this.JSON_MESSAGE;
	}

	/**
	 * 跳转至管理员修改页面
	 * @param id 管理员Id
	 * @param roleList 角色列表
	 * @param userRoles 管理员角色
	 * @param adminUser 管理员
	 * @param htmlList 修改html
	 * @return 
	 */
	public String edit() throws Exception {
		
		roleList = roleManager.list();
		this.userRoles =permissionManager.getUserRoles(id);
		adminUser = this.adminUserManager.get(id);
		this.htmlList = this.adminUserPluginBundle.getInputHtml(adminUser);
		return "edit";
	}

	/***
	 * 管理员修改
	 * @param updatePwd 是否修改密码
	 * @param adminUser 管理员
	 * @param newPassword 修改后密码
	 * @param roleids 管理员角色列表
	 * @return 修改状态
	 */
	public String editSave() throws Exception {
		
		//判断是否为演示站点
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		
		try {
			//判断是否修改密码
			if(updatePwd!=null){
				adminUser.setPassword(newPassword);
			}
			adminUser.setRoleids(roleids);
			this.adminUserManager.edit(adminUser);
			this.showSuccessJson("修改管理员成功");
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error(e,e.fillInStackTrace());
			this.showErrorJson("修改管理员失败");
		}

		return this.JSON_MESSAGE;
	}
	

	/**
	 * 删除管理员
	 * @param id 管理员Id
	 * @return 修改状态
	 */
	public String delete() throws Exception {
		
		//判断是否为演示站点
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
			return JSON_MESSAGE;
		}
		
		try {
			this.adminUserManager.delete(id);
			this.showSuccessJson("管理员删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("管理员删除失败");
			logger.error("管理员删除失败", e);
		}

		return this.JSON_MESSAGE;
	}

	/**
	 * 跳转至修改密码页面
	 * @return
	 */
	public String editPassword()  {
		return "editPassword";
	}
	
	/**
	 * 修改密码逻辑
	 * @return
	 */
	public String savePassword(){
		try{
			adminUser = UserConext.getCurrentAdminUser();
			HttpServletRequest request = this.getRequest();
			String oldPassword = request.getParameter("oldPassword");
			String newPassword1 = request.getParameter("newPassword1");
			String newPassword2 = request.getParameter("newPassword2");
			if(StringUtil.isNull(oldPassword)){
				showErrorJson("原密码为空！");
				return JSON_MESSAGE;
			}
			if(!StringUtil.md5(oldPassword).equals(adminUser.getPassword())){
				showErrorJson("原密码输入不正确！");
				return JSON_MESSAGE;
			}
			if(StringUtil.isNull(newPassword1)||StringUtil.isNull(newPassword2)){
				showErrorJson("俩次密码输入不一致！");
				return JSON_MESSAGE;
			}
			adminUser.setPassword(StringUtil.md5(newPassword1));
			this.adminUserManager.editPassword(adminUser);
			this.showSuccessJson("修改密码成功");
		}catch(Exception e){
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	//get set
	public AdminUserPluginBundle getAdminUserPluginBundle() {
		return adminUserPluginBundle;
	}
	public void setAdminUserPluginBundle(AdminUserPluginBundle adminUserPluginBundle) {
		this.adminUserPluginBundle = adminUserPluginBundle;
	}
	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}
	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}
	public IRoleManager getRoleManager() {
		return roleManager;
	}
	public void setRoleManager(IRoleManager roleManager) {
		this.roleManager = roleManager;
	}
	public IPermissionManager getPermissionManager() {
		return permissionManager;
	}
	public void setPermissionManager(IPermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}
	public AdminUser getAdminUser() {
		return adminUser;
	}
	public void setAdminUser(AdminUser adminUser) {
		this.adminUser = adminUser;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public List getRoleList() {
		return roleList;
	}
	public void setRoleList(List roleList) {
		this.roleList = roleList;
	}
	public List getUserRoles() {
		return userRoles;
	}
	public void setUserRoles(List userRoles) {
		this.userRoles = userRoles;
	}
	public int[] getRoleids() {
		return roleids;
	}
	public void setRoleids(int[] roleids) {
		this.roleids = roleids;
	}
	public List getUserList() {
		return userList;
	}
	public void setUserList(List userList) {
		this.userList = userList;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getUpdatePwd() {
		return updatePwd;
	}
	public void setUpdatePwd(String updatePwd) {
		this.updatePwd = updatePwd;
	}
	public List<String> getHtmlList() {
		return htmlList;
	}
	public void setHtmlList(List<String> htmlList) {
		this.htmlList = htmlList;
	}
}
