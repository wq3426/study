package com.enation.app.base.core.action;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Role;
import com.enation.app.base.core.service.auth.IAuthActionManager;
import com.enation.app.base.core.service.auth.IRoleManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;

/**
 * 角色管理
 * @author kingapex
 * 2010-11-4下午05:25:48
 * @author LiFenLong 2014-4-2;4.0版本改造
 * @author Kanon 2015-11-16 version 1.1 添加注释
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/core/admin")
@Action("role")
@Results({
	@Result(name="list", type="freemarker", location="/core/admin/auth/rolelist.html"),
	@Result(name="edit", type="freemarker", location="/core/admin/auth/role_edit.html"), 
	@Result(name="add", type="freemarker", location="/core/admin/auth/role_add.html") 
})
public class RoleAction extends WWAction {
	
	private IRoleManager roleManager;
	private IAuthActionManager authActionManager;
	
	private List roleList;
	private List authList;
	private int roleid;
	private Role role;
	private int[] acts;
	private int isEdit;
	
	
	/**
	 * 跳转到角色列表
	 * @return 角色列表
	 */
	public String list(){
		return "list";
	}
	
	/**
	 * 角色JSON列表
	 * @param roleList 角色列表
	 * @return 角色JSON列表
	 */
	public String listJson(){
		roleList = roleManager.list();
		this.showGridJson(roleList);
		return this.JSON_MESSAGE;
	}
	
	
	/**
	 * 跳转到角色添加页面
	 * @param authList 权限点列表
	 * @return 角色添加页面
	 */
	public String add(){
		authList = authActionManager.list();
		return "add";
	}
	
	
	/**
	 * 跳转到角色修改页面
	 * @param authList 权限点列表
	 * @param isEdit 是否为修改
	 * @param role 角色信息，同时读取此角色权限
	 * @param roleid 角色Id
	 * @return 角色修改页面
	 */
	public String edit(){
		authList = authActionManager.list();
		isEdit= 1;
		this.role = this.roleManager.get(roleid);
		return "edit";
	}
	
	/**
	 * 角色保存添加
	 * @param role 角色
	 * @param acts 权限点列表
	 * @return 新增角色状态
	 */
	public String saveAdd(){
		try {
			this.roleManager.add(role, acts);
			this.showSuccessJson("新增角色成功");
		} catch (Exception e) {
			this.showErrorJson("新增角色失败");
			logger.error("新增角色失败", e);
		}
		return this.JSON_MESSAGE;
	}
	
	
	/**
	 * 保存修改
	 * @param role 角色
	 * @param acts 权限点列表
	 * @return 修改角色状态
	 */
	public String saveEdit(){
		try {
			this.roleManager.edit(role, acts);
			this.showSuccessJson("角色修改成功");
		} catch (Exception e) {
			this.showErrorJson("角色修改失败");
			logger.error("角色修改失败", e);
		}		
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 删除角色
	 * @param roleid 角色Id
	 * @return 删除角色状态
	 */
	public String delete(){
		
		if(EopSetting.IS_DEMO_SITE){
			if(roleid<=5){
				this.showErrorJson("抱歉，当前为演示站点，以不能修改这些示例数据，请下载安装包在本地体验这些功能！");
				return JSON_MESSAGE;
			}
		}
		
		try {
			this.roleManager.delete(roleid);
			this.showSuccessJson("角色删除成功");
		} catch (Exception e) {
			this.showErrorJson("角色删除失败");
			logger.error("角色删除失败", e);
		}		
		return this.JSON_MESSAGE;
	}
	
	//get set
	public IRoleManager getRoleManager() {
		return roleManager;
	}
	public void setRoleManager(IRoleManager roleManager) {
		this.roleManager = roleManager;
	}

	public IAuthActionManager getAuthActionManager() {
		return authActionManager;
	}

	public void setAuthActionManager(IAuthActionManager authActionManager) {
		this.authActionManager = authActionManager;
	}

	public List getRoleList() {
		return roleList;
	}

	public void setRoleList(List roleList) {
		this.roleList = roleList;
	}

	public List getAuthList() {
		return authList;
	}

	public void setAuthList(List authList) {
		this.authList = authList;
	}

	public int getRoleid() {
		return roleid;
	}

	public void setRoleid(int roleid) {
		this.roleid = roleid;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public int[] getActs() {
		return acts;
	}
	public void setActs(int[] acts) {
		this.acts = acts;
	}

	public int getIsEdit() {
		return isEdit;
	}

	public void setIsEdit(int isEdit) {
		this.isEdit = isEdit;
	}
}
