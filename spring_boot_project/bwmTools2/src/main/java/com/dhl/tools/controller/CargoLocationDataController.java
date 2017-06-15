package com.dhl.tools.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dhl.tools.domain.CargoLocationData;
import com.dhl.tools.domain.ParamConfigForm;
import com.dhl.tools.security.MyUserDetails;
import com.dhl.tools.service.CargoLocationDataService;
import com.github.pagehelper.PageInfo;

/**
 * Created by liuso on 2017/5/8.
 */
@RestController
@RequestMapping("cargo-location-data")
public class CargoLocationDataController {

	@Autowired
	private CargoLocationDataService cargoLocationDataService;

	/**
	 * 批量更新货位配置数据
	 *
	 * @param form
	 *            批量数据表单
	 */
	@PatchMapping
	public void update(ParamConfigForm form) {
		this.cargoLocationDataService.update(form);
	}

	/**
	 * 设置参数
	 *
	 * @param parentId
	 *            父主键
	 * @param form
	 *            级联设置子节点信息
	 */
	@PatchMapping("setParam")
	public void setParam(String parentId, ParamConfigForm form, @AuthenticationPrincipal MyUserDetails currentUser) {
		this.cargoLocationDataService.setParam(currentUser.getCurrentWareHouse(), parentId, form);
	}

	/**
	 * 分页查询
	 *
	 * @param cargoLocationData
	 *            货位节点数据
	 * @param page
	 *            页码 从1开始
	 * @param size
	 *            数量
	 * @return
	 */
	@GetMapping
	public PageInfo<CargoLocationData> findPage(CargoLocationData cargoLocationData, Integer page, Integer size,
			@AuthenticationPrincipal MyUserDetails currentUser) {
		cargoLocationData.setWareHouseId(currentUser.getCurrentWareHouse().getId());
		return this.cargoLocationDataService.findPage(cargoLocationData, page, size);
	}

	/**
	 * 查询子节点数据
	 *
	 * @param cargoLocationData
	 *            货位节点数据
	 * @return
	 */
	@GetMapping("findListByParentId")
	public List<CargoLocationData> findListByParentId(CargoLocationData cargoLocationData,
			@AuthenticationPrincipal MyUserDetails currentUser) {
		cargoLocationData.setWareHouseId(currentUser.getCurrentWareHouse().getId());
		return this.cargoLocationDataService.findListByParentId(cargoLocationData);
	}

	/**
	 * 同步参数
	 * 
	 * @param referId
	 *            参考id
	 * @param syncId
	 *            被同步的id数组
	 * @param configTypeIds需要级联子节点类型id数组
	 */
	@PatchMapping("syncParam")
	public void syncParam(@AuthenticationPrincipal MyUserDetails currentUser, String referId, String syncIds,
			Integer[] configTypeIds) {
		String[] syncIdArray = syncIds.split(",");
		this.cargoLocationDataService.syncParam(currentUser.getCurrentWareHouse(), referId, syncIdArray, configTypeIds);
	}
}
