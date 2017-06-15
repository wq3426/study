package com.dhl.tools.controller;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dhl.tools.domain.Material;
import com.dhl.tools.domain.MessageInfo;
import com.dhl.tools.exception.ImportDataException;
import com.dhl.tools.security.MyUserDetails;
import com.dhl.tools.service.CargoLocationRecommendService;
import com.dhl.tools.service.MaterialService;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by sunjt on 2017/5/11.
 */
@Slf4j
@RestController
@RequestMapping("cargo-location-recommend")
public class CargoLocationRecommendController {

	@Autowired
	private MaterialService materialService;

	@Autowired
	private CargoLocationRecommendService cargoLocationRecommendService;

	/**
	 * 导入物料拣货频率
	 *
	 * @param file
	 *            excel文件
	 * @param obligate
	 *            货位预留值
	 * @return
	 */
	@PostMapping("import/{reservePercentage}")
	public MessageInfo importData(MultipartFile file, @PathVariable("reservePercentage") String reservePercentage,
			@AuthenticationPrincipal MyUserDetails currentUser) {
		MessageInfo messageInfo = new MessageInfo();
		try {
			return this.materialService.importPickFreq(currentUser.getCurrentWareHouse(), file,
					this.getDoubleReservePercentage(reservePercentage));
		} catch (ImportDataException exception) {
			messageInfo.setMsg(exception.getMessage());
		} catch (Exception e) {
			log.info("导入物料异常:", e);
			messageInfo.setMsg("导入物料异常，请重试!");
		}
		return messageInfo;
	}

	private double getDoubleReservePercentage(String reservePercentage) {
		double d = 0d;
		if (StringUtils.isNoneBlank(reservePercentage)) {
			try {
				d = Double.parseDouble(reservePercentage);
			} catch (Exception e) {
			}
		}
		return d;
	}

	/**
	 * 分页查询
	 *
	 * @param material
	 *            物料
	 * @param page
	 *            页码 从1开始
	 * @param size
	 *            数量
	 * @return
	 */
	@GetMapping
	public PageInfo<Material> findPage(Material material, Integer page, Integer size,
			@AuthenticationPrincipal MyUserDetails currentUser) {
		material.setWareHouseId(currentUser.getCurrentWareHouse().getId());
		return this.cargoLocationRecommendService.findPage(material, page, size);
	}

	/**
	 * 重新推荐货位
	 *
	 * @param currentUser
	 *            当前登录用户
	 * @return
	 */
	@GetMapping("recommend")
	public MessageInfo recommend(@AuthenticationPrincipal MyUserDetails currentUser, String reservePercentage) {
		MessageInfo messageInfo = new MessageInfo();
		try {
			messageInfo = this.materialService.recommendCargoLocation(
					this.getDoubleReservePercentage(reservePercentage), currentUser.getCurrentWareHouse());
		} catch (Exception e) {
			log.error("重新推荐货位异常：", e);
			messageInfo.setMsg("重新推荐货位异常，请重试");
		}
		return messageInfo;
	}

	/**
	 * 导出物料推荐货位数据
	 * 
	 * @param response
	 *            响应
	 * @param currentUser
	 *            当前登录用户
	 */
	@GetMapping("export")
	public void exportData(HttpServletResponse response, Material material,
			@AuthenticationPrincipal MyUserDetails currentUser) {
		this.cargoLocationRecommendService.exportData(response, currentUser.getCurrentWareHouse(), material);
	}
}
