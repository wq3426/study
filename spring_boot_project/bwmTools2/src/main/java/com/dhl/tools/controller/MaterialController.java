package com.dhl.tools.controller;

import com.dhl.tools.domain.Material;
import com.dhl.tools.domain.MessageInfo;
import com.dhl.tools.exception.ImportDataException;
import com.dhl.tools.security.MyUserDetails;
import com.dhl.tools.service.MaterialService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物料 Created by liuso on 2017/4/12.
 */
@Slf4j
@RestController
@RequestMapping("material")
public class MaterialController {

	@Autowired
	private MaterialService materialService;

	/**
	 * 添加物料
	 *
	 * @param material
	 *            物料信息
	 * @return
	 */
	@PutMapping
	public boolean insert(Material material, @AuthenticationPrincipal MyUserDetails currentUser) {
		material.setWareHouseId(currentUser.getCurrentWareHouse().getId());
		try {
			return this.materialService.add(material, currentUser.getCurrentWareHouse());
		} catch (Exception e) {
			log.error("添加物料异常：", e);
		}
		return false;
	}

	/**
	 * 根据主键删除
	 *
	 * @param id
	 *            主键
	 */
	@DeleteMapping("{id}")
	public boolean remove(@PathVariable Integer id) {
		return this.materialService.delete(id);
	}

	/**
	 * 更新物料
	 *
	 * @param material
	 *            物料信息
	 * @return
	 */
	@PatchMapping
	public boolean update(Material material, @AuthenticationPrincipal MyUserDetails currentUser) {
		material.setWareHouseId(currentUser.getCurrentWareHouse().getId());
		try {
			return this.materialService.update(material, currentUser.getCurrentWareHouse());
		} catch (Exception e) {
			log.error("更新物料异常：", e);
		}
		return false;
	}

	/**
	 * 根据主键查询
	 *
	 * @param id
	 *            主键
	 * @return
	 */
	@GetMapping("{id}")
	public Material findById(@PathVariable Integer id) {
		return this.materialService.findById(id);
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
		return this.materialService.findPage(material, page, size);
	}

	/**
	 * 查询所有
	 *
	 * @return
	 */
	@GetMapping("list")
	public List<Material> findAll(@AuthenticationPrincipal MyUserDetails currentUser) {
		return this.materialService.findList(currentUser.getCurrentWareHouse().getId());
	}

	/**
	 * 导入数据
	 *
	 * @param file
	 *            excel文件
	 * @return
	 */
	@PostMapping("import")
	public MessageInfo importData(MultipartFile file, @AuthenticationPrincipal MyUserDetails currentUser) {
		MessageInfo messageInfo = new MessageInfo();
		try {
			messageInfo = this.materialService.importData(currentUser.getCurrentWareHouse(), currentUser.getUsername(),
					file);
		} catch (ImportDataException exception) {
			messageInfo.setMsg(exception.getMessage());
		} catch (Exception e) {
			log.info("导入物料异常:", e);
			messageInfo.setMsg("导入物料异常，请重试!");
		}
		return messageInfo;

	}

	/**
	 * 导出数据
	 * 
	 * @param response
	 *            响应
	 * @param currentUser
	 *            当前登录用户
	 */
	@GetMapping("export")
	public void exportData(HttpServletResponse response, Material material,
			@AuthenticationPrincipal MyUserDetails currentUser) {
		this.materialService.exportData(response, currentUser.getCurrentWareHouse(), material);
	}

	/**
	 * 更新库存
	 *
	 * @param file
	 *            excel文件
	 * @return
	 */
	@PostMapping("update")
	public MessageInfo updateData(MultipartFile file, @AuthenticationPrincipal MyUserDetails currentUser) {
		MessageInfo messageInfo = new MessageInfo();
		try {
			messageInfo = this.materialService.updateData(currentUser.getCurrentWareHouse(), file);
		} catch (Exception e) {
			log.error("重新推荐物料货位类型异常：", e);
			messageInfo.setMsg("更新库存异常，请重试！");
		}
		return messageInfo;
	}

	/**
	 * 重新推荐物料货位类型
	 *
	 * @param currentUser
	 *            当前登录用户
	 * @return
	 */
	@GetMapping("recommend")
	public MessageInfo recommend(@AuthenticationPrincipal MyUserDetails currentUser) {
		MessageInfo messageInfo = new MessageInfo();
		try {
			messageInfo = this.materialService.processCargoLocationTypeData(null, currentUser.getCurrentWareHouse());
		} catch (Exception e) {
			log.error("重新推荐物料货位类型异常：", e);
			messageInfo.setMsg("重新推荐货位异常，请重试");
		}
		return messageInfo;
	}

	/**
	 * 推荐物料货位
	 *
	 * @param currentUser
	 *            当前登录用户
	 * @return
	 */
	@GetMapping("recommend-cargo-location")
	public MessageInfo recommendCargoLocation(@AuthenticationPrincipal MyUserDetails currentUser,
			Double reservePercentage) {
		MessageInfo messageInfo = new MessageInfo();
		try {
			messageInfo = this.materialService.recommendCargoLocation(reservePercentage,
					currentUser.getCurrentWareHouse());
		} catch (Exception e) {
			log.error("推荐物料货位异常：", e);
			messageInfo.setMsg("推荐货位异常，请重新推荐");
		}
		return messageInfo;
	}

	/**
	 * 判断物料编码是否存在
	 * 
	 * @param id
	 * @param code
	 * @param currentUser
	 * @return
	 */
	@GetMapping("findCodeIsExist")
	public Map<String, Boolean> findCodeIsExist(Integer id, String code,
			@AuthenticationPrincipal MyUserDetails currentUser) {
		Map<String, Boolean> validMap = new HashMap<String, Boolean>();
		validMap.put("valid",
				!this.materialService.findCodeIsExist(id, code, currentUser.getCurrentWareHouse().getId()));
		return validMap;
	}
}
