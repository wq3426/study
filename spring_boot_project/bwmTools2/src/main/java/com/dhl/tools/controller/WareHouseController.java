package com.dhl.tools.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.dhl.tools.domain.WareHouse;
import com.dhl.tools.security.MyUserDetails;
import com.dhl.tools.service.WareHouseService;

/**
 * 仓库管理 Created by liuso on 2017/4/10.
 */
@RestController
@RequestMapping("ware-house")
public class WareHouseController {

	@Autowired
	private WareHouseService wareHouseService;

	/**
	 * 添加仓库
	 * 
	 * @param wareHouse
	 *            仓库信息
	 * @return
	 */
	@PutMapping
	public boolean insert(WareHouse wareHouse, Integer[] typeIds, Integer[] lengths, String[] alias) {
		return this.wareHouseService.add(wareHouse, typeIds, lengths, alias);
	}

	/**
	 * 根据主键删除
	 * 
	 * @param id
	 *            主键
	 */
	@DeleteMapping("{id}")
	public boolean remove(@PathVariable Integer id) {
		return this.wareHouseService.delete(id);
	}

	/**
	 * 更新仓库
	 * 
	 * @param wareHouse
	 *            仓库信息
	 * @return
	 */
	@PatchMapping
	public boolean update(WareHouse wareHouse, Integer[] typeIds, Integer[] lengths, String[] alias,
			@AuthenticationPrincipal MyUserDetails currentUser) {
		boolean updateFlag = this.wareHouseService.update(wareHouse, typeIds, lengths, alias);
		// 如果更新的是当前用户登录的仓库，则同步缓存中数据
		if (Objects.equals(wareHouse.getId(), currentUser.getCurrentWareHouse().getId())) {
			currentUser.setCurrentWareHouse(wareHouse);
			currentUser.setCurrentWareHouseCode(wareHouse.getCode());
		}
		return updateFlag;
	}

	/**
	 * 根据主键查询
	 * 
	 * @param id
	 *            主键
	 * @return
	 */
	@GetMapping("{id}")
	public JSONObject findById(@PathVariable Integer id) {
		return this.wareHouseService.findById(id);
	}

	/**
	 * 分页查询
	 *
	 * @param code
	 *            仓库编码
	 * @param page
	 *            页码 从1开始
	 * @param size
	 *            数量
	 * @return
	 */
	@GetMapping
	public JSONObject findPage(String code, Integer page, Integer size) {
		return this.wareHouseService.findPage(code, page, size);
	}

	/**
	 * 查询所有
	 *
	 * @return
	 */
	@GetMapping("list")
	public List<WareHouse> findAll() {
		return this.wareHouseService.findList();
	}

	/**
	 * 判断仓库编码是否存在
	 *
	 * @return
	 */
	@GetMapping("findCodeIsExist")
	public Map<String, Boolean> findCodeIsExist(Integer id, String code) {
		Map<String, Boolean> validMap = new HashMap<String, Boolean>();
		validMap.put("valid", !this.wareHouseService.findCodeIsExist(id, code));
		return validMap;
	}
}
