package com.dhl.tools.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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

import com.dhl.tools.domain.CargoLocationType;
import com.dhl.tools.domain.MessageInfo;
import com.dhl.tools.exception.ImportDataException;
import com.dhl.tools.security.MyUserDetails;
import com.dhl.tools.service.CargoLocationTypeService;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 货位类型 Created by liuso on 2017/4/12.
 */
@Slf4j
@RestController
@RequestMapping("cargo-location-type")
public class CargoLocationTypeController {

	@Autowired
	private CargoLocationTypeService cargoLocationTypeService;

	/**
	 * 添加货位类型
	 *
	 * @param cargoLocationType
	 *            货位类型信息
	 * @return
	 */
	@PutMapping
	public boolean insert(CargoLocationType cargoLocationType, @AuthenticationPrincipal MyUserDetails currentUser) {
		cargoLocationType.setWareHouseId(currentUser.getCurrentWareHouse().getId());
		return this.cargoLocationTypeService.add(cargoLocationType);
	}

	/**
	 * 根据主键删除
	 *
	 * @param id
	 *            主键
	 */
	@DeleteMapping("{id}")
	public boolean remove(@PathVariable Integer id) {
		return this.cargoLocationTypeService.delete(id);
	}

	/**
	 * 更新货位类型
	 *
	 * @param cargoLocationType
	 *            货位类型信息
	 * @return
	 */
	@PatchMapping
	public boolean update(CargoLocationType cargoLocationType, @AuthenticationPrincipal MyUserDetails currentUser) {
		cargoLocationType.setWareHouseId(currentUser.getCurrentWareHouse().getId());
		return this.cargoLocationTypeService.update(cargoLocationType);
	}

	/**
	 * 根据主键查询
	 *
	 * @param id
	 *            主键
	 * @return
	 */
	@GetMapping("{id}")
	public CargoLocationType findById(@PathVariable Integer id) {
		return this.cargoLocationTypeService.findById(id);
	}

	/**
	 * 分页查询
	 *
	 * @param cargoLocationType
	 *            货位类型
	 * @param page
	 *            页码 从1开始
	 * @param size
	 *            数量
	 * @return
	 */
	@GetMapping
	public PageInfo<CargoLocationType> findPage(CargoLocationType cargoLocationType, Integer page, Integer size,
			@AuthenticationPrincipal MyUserDetails currentUser) {
		cargoLocationType.setWareHouseId(currentUser.getCurrentWareHouse().getId());
		return this.cargoLocationTypeService.findPage(cargoLocationType, page, size);
	}

	/**
	 * 查询所有
	 *
	 * @return
	 */
	@GetMapping("list")
	public List<CargoLocationType> findAll(@AuthenticationPrincipal MyUserDetails currentUser) {
		return this.cargoLocationTypeService.findList(currentUser.getCurrentWareHouse().getId());
	}

	/**
	 * 导入货位类型信息
	 *
	 * @param file
	 *            excel文件
	 * @param currentUser
	 *            当前登录用户
	 * 
	 * @return
	 */
	@PostMapping("import")
	public MessageInfo importData(MultipartFile file, @AuthenticationPrincipal MyUserDetails currentUser) {
		MessageInfo messageInfo = new MessageInfo();
		try {
			messageInfo = this.cargoLocationTypeService.importData(currentUser.getCurrentWareHouse(),
					currentUser.getUsername(), file);
		} catch (ImportDataException exception) {
			messageInfo.setMsg(exception.getMessage());
		} catch (Exception e) {
			log.info("导入货位类型异常:", e);
			messageInfo.setMsg("导入货位类型异常，请重试!");
		}
		return messageInfo;
	}

	/**
	 * 导出货位类型信息
	 *
	 * @param currentUser
	 *            当前登录用户
	 * @param response
	 *            响应信息
	 */
	@GetMapping("export")
	public void exportData(@AuthenticationPrincipal MyUserDetails currentUser, HttpServletResponse response) {
		this.cargoLocationTypeService.exportData(currentUser, response);
	}

	/**
	 * 判断货位类型编码是否存在
	 * 
	 * @param id
	 *            货位类型主键id
	 * @param code
	 *            货位类型编码
	 *
	 * @return
	 */
	@GetMapping("findCodeIsExist")
	public Map<String, Boolean> findCodeIsExist(Integer id, String code,
			@AuthenticationPrincipal MyUserDetails currentUser) {
		Map<String, Boolean> validMap = new HashMap<String, Boolean>();
		validMap.put("valid",
				!this.cargoLocationTypeService.findCodeIsExist(id, code, currentUser.getCurrentWareHouse().getId()));
		return validMap;
	}
}
