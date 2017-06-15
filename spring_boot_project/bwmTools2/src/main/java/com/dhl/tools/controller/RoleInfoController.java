package com.dhl.tools.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.dhl.tools.domain.LoadTree;
import com.dhl.tools.domain.RoleInfo;
import com.dhl.tools.domain.WareHouse;
import com.dhl.tools.service.RoleInfoService;

import lombok.extern.slf4j.Slf4j;

/**
 * 角色 Created by liuso on 2017/4/12.
 */
@Slf4j
@RestController
@RequestMapping("role")
public class RoleInfoController {

	@Autowired
	private RoleInfoService roleInfoService;

	/**
	 * 添加角色
	 *
	 * @param roleInfo
	 *            角色信息
	 * @return
	 */
	@PutMapping
	public boolean insert(RoleInfo roleInfo) {
		return this.roleInfoService.add(roleInfo);
	}

	/**
	 * 根据主键删除
	 *
	 * @param id
	 *            主键
	 */
	@DeleteMapping("{id}")
	public boolean remove(@PathVariable Integer id) {
		return this.roleInfoService.delete(id);
	}

	/**
	 * 更新角色
	 *
	 * @param roleInfo
	 *            角色信息
	 * @return
	 */
	@PatchMapping
	public boolean update(RoleInfo roleInfo) {
		return this.roleInfoService.update(roleInfo);
	}

	/**
	 * 根据主键查询
	 *
	 * @param id
	 *            主键
	 * @return
	 */
	@GetMapping("{id}")
	public RoleInfo findById(@PathVariable Integer id) {
		return this.roleInfoService.findById(id);
	}

	/**
	 * 分页查询
	 *
	 * @param roleInfo
	 *            角色
	 * @param page
	 *            页码 从1开始
	 * @param size
	 *            数量
	 * @return
	 */
	@GetMapping
	public JSONObject findPage(RoleInfo roleInfo, Integer page, Integer size) {
		return this.roleInfoService.findPage(roleInfo, page, size);
	}

	/**
	 * 查询所有
	 *
	 * @return
	 */
	@GetMapping("list")
	public List<RoleInfo> findAll() {
		return this.roleInfoService.findList();
	}

	/**
	 * 判断角色名称是否存在
	 *
	 * @return
	 */
	@GetMapping("findNameIsExist")
	public Map<String, Boolean> findNameIsExist(Integer id, String name) {
		Map<String, Boolean> validMap = new HashMap<String, Boolean>();
		validMap.put("valid", !this.roleInfoService.findNameIsExist(id, name));
		return validMap;
	}

	/**
	 * 更新角色的仓库信息
	 *
	 * @param roleId
	 *            角色id
	 * @param warehouseId
	 *            仓库id数组
	 * @return
	 */
	@PatchMapping("updateRoleWareHouse")
	public boolean updateRoleWareHouse(Integer roleId, String warehouseId) {
		try {
			this.roleInfoService.updateRoleWareHouse(roleId, warehouseId.split(","));
			return true;
		} catch (Exception e) {
			log.error("更新角色的仓库信息异常：", e);
		}
		return false;
	}

	/**
	 * 根据角色获取已分配的仓库信息
	 *
	 * @return
	 */
	@GetMapping("selectWareHouseByRoleId/{roleId}")
	public List<WareHouse> selectWareHouseByRoleId(@PathVariable Integer roleId) {
		return this.roleInfoService.selectWareHouseByRoleId(roleId);
	}

	/**
	 * 根据角色加载权限树
	 *
	 * @return
	 */
	@GetMapping("loadTree/{roleId}")
	public List<LoadTree> loadTree(@PathVariable String roleId) {
		return this.roleInfoService.getRightTree(roleId);
	}

	/**
	 * 更新角色的权限信息
	 *
	 * @param roleId
	 *            角色id
	 * @param rightId
	 *            权限id数组
	 * @return
	 */
	@PatchMapping("updateRoleRight")
	public boolean updateRoleRight(Integer roleId, String rightId) {
		try {
			this.roleInfoService.updateRoleRight(roleId, rightId.split(","));
			return true;
		} catch (Exception e) {
			log.info("更新角色的权限信息异常：", e);
		}
		return false;
	}
}
