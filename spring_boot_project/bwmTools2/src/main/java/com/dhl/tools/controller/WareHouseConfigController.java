package com.dhl.tools.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dhl.tools.domain.WareHouseConfig;
import com.dhl.tools.security.MyUserDetails;
import com.dhl.tools.service.WareHouseConfigService;
import com.github.pagehelper.PageInfo;

/**
 * Created by liuso on 2017/4/29.
 */
@RestController
@RequestMapping("ware-house-config")
public class WareHouseConfigController {

	@Autowired
	private WareHouseConfigService wareHouseConfigService;

	/**
	 * 更新仓库配置信息
	 *
	 * @param wareHouseConfig
	 *            仓库配置信息
	 */
	@PatchMapping
	boolean update(WareHouseConfig wareHouseConfig) {
		return this.wareHouseConfigService.update(wareHouseConfig);
	}

	/**
	 * 分页查询仓库配置信息
	 *
	 * @param configTypeId
	 *            仓库类型主键
	 * @param page
	 *            页码
	 * @param size
	 *            数量
	 * @return 分页信息
	 */
	@GetMapping
	PageInfo<WareHouseConfig> findByPage(Integer configTypeId, Integer page, Integer size) {
		return this.wareHouseConfigService.findByPage(configTypeId, page, size);
	}

	/**
	 * 查询当前仓库配置信息
	 *
	 */
	@GetMapping("list")
	List<WareHouseConfig> findByList(@AuthenticationPrincipal MyUserDetails currentUser) {
		return this.wareHouseConfigService.findByList(currentUser.getCurrentWareHouse().getId());
	}
}
