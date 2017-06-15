package com.dhl.tools.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dhl.tools.domain.CargoLocation;
import com.dhl.tools.domain.MessageInfo;
import com.dhl.tools.exception.ImportDataException;
import com.dhl.tools.security.MyUserDetails;
import com.dhl.tools.service.CargoLocationDataService;
import com.dhl.tools.service.CargoLocationService;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 货位 Created by liuso on 2017/4/12.
 */
@Slf4j
@RestController
@RequestMapping("cargo-location")
public class CargoLocationController {

	@Autowired
	private CargoLocationService cargoLocationService;

	@Autowired
	private CargoLocationDataService cargoLocationDataService;

	/**
	 * 添加货位
	 *
	 * @param cargoLocation
	 *            货位信息
	 * @return
	 */
	@PutMapping
	public boolean insert(CargoLocation cargoLocation, @AuthenticationPrincipal MyUserDetails currentUser) {
		cargoLocation.setWareHouseId(currentUser.getCurrentWareHouse().getId());
		return this.cargoLocationService.add(cargoLocation);
	}

	/**
	 * 根据主键删除
	 *
	 * @param id
	 *            主键
	 */
	@DeleteMapping("{id}")
	public boolean remove(@PathVariable Integer id) {
		return this.cargoLocationService.delete(id);
	}

	/**
	 * 更新货位
	 *
	 * @param cargoLocation
	 *            货位信息
	 * @return
	 */
	@PatchMapping
	public boolean update(CargoLocation cargoLocation, @AuthenticationPrincipal MyUserDetails currentUser) {
		cargoLocation.setWareHouseId(currentUser.getCurrentWareHouse().getId());
		return this.cargoLocationService.update(cargoLocation);
	}

	/**
	 * 根据主键查询
	 *
	 * @param id
	 *            主键
	 * @return
	 */
	@GetMapping("{id}")
	public CargoLocation findById(@PathVariable Integer id) {
		return this.cargoLocationService.findById(id);
	}

	/**
	 * 分页查询
	 *
	 * @param cargoLocation
	 *            货位
	 * @param page
	 *            页码 从1开始
	 * @param size
	 *            数量
	 * @return
	 */
	@GetMapping
	public PageInfo<Map<String, Object>> findPage(CargoLocation cargoLocation, Integer page, Integer size,
			@AuthenticationPrincipal MyUserDetails currentUser) {
		cargoLocation.setWareHouseId(currentUser.getCurrentWareHouse().getId());
		return this.cargoLocationService.findPage(cargoLocation, page, size);
	}

	/**
	 * 查询所有
	 *
	 * @return
	 */
	@GetMapping("list")
	public List<CargoLocation> findAll(CargoLocation cargoLocation,
			@AuthenticationPrincipal MyUserDetails currentUser) {
		cargoLocation.setWareHouseId(currentUser.getCurrentWareHouse().getId());
		return this.cargoLocationService.findList(cargoLocation);
	}

	/**
	 * 导入货位信息
	 *
	 * @param file
	 *            excel文件
	 * @return
	 */
	@PostMapping("import")
	public MessageInfo importData(MultipartFile file, @AuthenticationPrincipal MyUserDetails currentUser) {
		MessageInfo messageInfo = new MessageInfo();
		try {
			messageInfo = this.cargoLocationService.importData(currentUser.getCurrentWareHouse(),
					currentUser.getUsername(), file);
		} catch (ImportDataException exception) {
			messageInfo.setMsg(exception.getMessage());
		} catch (Exception e) {
			log.error("导入货位异常:", e);
			messageInfo.setMsg("导入货位异常，请重试!");
		}
		return messageInfo;
	}

	/**
	 * 判断货位编码是否存在
	 * 
	 * @param id
	 *            货位主键id
	 * @param code
	 *            货位编码
	 *
	 * @return
	 */
	@GetMapping("findCodeIsExist")
	public Map<String, Boolean> findCodeIsExist(Integer id, String code,
			@AuthenticationPrincipal MyUserDetails currentUser) {
		Map<String, Boolean> validMap = new HashMap<String, Boolean>();
		validMap.put("valid",
				!this.cargoLocationService.findCodeIsExist(id, code, currentUser.getCurrentWareHouse().getId()));
		return validMap;
	}

	/**
	 * 计算货位分值
	 *
	 * @return
	 */
	@GetMapping("calculationCargoLocationScore")
	public MessageInfo calculationCargoLocationScore(@AuthenticationPrincipal MyUserDetails currentUser) {
		MessageInfo messageInfo = new MessageInfo();
		try {
			messageInfo = this.cargoLocationDataService
					.asynCalculationCargoLocationScore(currentUser.getCurrentWareHouse());
		} catch (Exception e) {
			log.info("计算货位分值异常：", e);
			messageInfo.setMsg("计算货位分值异常");
		}
		return messageInfo;
	}
}
