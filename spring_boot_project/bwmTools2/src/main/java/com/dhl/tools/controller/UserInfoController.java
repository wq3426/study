package com.dhl.tools.controller;

import com.alibaba.fastjson.JSONObject;
import com.dhl.tools.domain.RoleInfo;
import com.dhl.tools.domain.UserInfo;
import com.dhl.tools.security.MyUserDetails;
import com.dhl.tools.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户 Created by liuso on 2017/4/12.
 */
@Slf4j
@RestController
@RequestMapping("user")
public class UserInfoController {

	@Autowired
	private UserInfoService userInfoService;

	/**
	 * 添加用户
	 *
	 * @param userInfo
	 *            用户信息
	 * @return
	 */
	@PutMapping
	public boolean insert(UserInfo userInfo) {
		return this.userInfoService.add(userInfo);
	}

	/**
	 * 根据主键删除
	 *
	 * @param id
	 *            主键
	 */
	@DeleteMapping("{id}")
	public boolean remove(@PathVariable Integer id) {
		return this.userInfoService.delete(id);
	}

	/**
	 * 更新用户
	 *
	 * @param userInfo
	 *            用户信息
	 * @return
	 */
	@PatchMapping
	public boolean update(UserInfo userInfo) {
		return this.userInfoService.update(userInfo);
	}

	/**
	 * 根据主键查询
	 *
	 * @param id
	 *            主键
	 * @return
	 */
	@GetMapping("{id}")
	public UserInfo findById(@PathVariable Integer id) {
		return this.userInfoService.findById(id);
	}

	/**
	 * 分页查询
	 *
	 * @param userInfo
	 *            用户
	 * @param page
	 *            页码 从1开始
	 * @param size
	 *            数量
	 * @return
	 */
	@GetMapping
	public JSONObject findPage(UserInfo userInfo, Integer page, Integer size) {
		return this.userInfoService.findPage(userInfo, page, size);
	}

	/**
	 * 查询所有
	 *
	 * @return
	 */
	@GetMapping("list")
	public List<UserInfo> findAll() {
		return this.userInfoService.findList();
	}

	/**
	 * 判断用户工号是否存在
	 * 
	 * @param id
	 *            用户主键id
	 * @param username
	 *            用户工号
	 *
	 * @return
	 */
	@GetMapping("findUserNameIsExist")
	public Map<String, Boolean> findUserNameIsExist(Integer id, String username) {
		Map<String, Boolean> validMap = new HashMap<String, Boolean>();
		validMap.put("valid", !this.userInfoService.findUserNameIsExist(id, username));
		return validMap;
	}

	/**
	 * 根据用户Id获取已分配的角色信息
	 *
	 * @return
	 */
	@GetMapping("selectRoleInfoByUserId/{userId}")
	public List<RoleInfo> selectRoleInfoByUserId(@PathVariable Integer userId) {
		return this.userInfoService.selectRoleInfoByUserId(userId);
	}

	/**
	 * 更新用户的角色信息
	 *
	 * @param userId
	 *            用户id
	 * @param roleId
	 *            角色id数组
	 * @return
	 */
	@PatchMapping("updateUserRole")
	public boolean updateUserRole(Integer userId, String roleId) {
		try {
			this.userInfoService.addUserRole(userId, roleId.split(","));
			return true;
		} catch (Exception e) {
			log.info("更新用户的角色信息异常：", e);
		}
		return false;
	}

	/**
	 * 更换登录用户当前操作仓库
	 *
	 * @param currentUser 登录用户
	 * @param code        仓库编号
	 * @return
	 */
	@PostMapping("change-ware-house")
	public boolean changeWareHouse(@AuthenticationPrincipal MyUserDetails currentUser, String code) {
		return this.userInfoService.changeWareHouse(currentUser, code);
	}

	/**
	 * 获取当前登录用户信息
	 *
	 * @param currentUser 当前登录用户
	 * @return
	 */
	@GetMapping("profile")
	public MyUserDetails profile(@AuthenticationPrincipal MyUserDetails currentUser) {
		return currentUser;
	}
}
