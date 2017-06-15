package com.dhl.tools.service;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dhl.tools.domain.Material;
import com.dhl.tools.domain.MaterialExample;
import com.dhl.tools.domain.MaterialExample.Criteria;
import com.dhl.tools.domain.WareHouse;
import com.dhl.tools.mapper.MaterialMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 货位推荐 Created by liuso on 2017/5/12.
 */
@Slf4j
@Service
public class CargoLocationRecommendService {

	@Autowired
	private MaterialMapper materialMapper;

	/**
	 * 查询物料推荐货位信息
	 * 
	 * @param material
	 * @param page
	 * @param size
	 * @return
	 */
	public PageInfo<Material> findPage(Material material, Integer page, Integer size) {

		// 获取第1页，10条内容，默认查询总数count
		PageHelper.startPage(page == null ? 1 : page, size == null ? 10 : size);

		MaterialExample example = new MaterialExample();
		example.setOrderByClause("createDate desc,batchSerialNumber asc");
		Criteria criteria = example.createCriteria().andWareHouseIdEqualTo(material.getWareHouseId());
		if (StringUtils.isNoneBlank(material.getCode())) {
			criteria.andCodeLike("%" + material.getCode() + "%");
		}
		if (null != material.getCargoLocationTypeId()) {
			criteria.andOptimalLocationTypeLike("%" + material.getOptimalLocationType() + "%");
			criteria.andCargoLocationTypeIdEqualTo(material.getCargoLocationTypeId());
		}

		List<Material> list = materialMapper.selectByExample(example);
		// 用PageInfo对结果进行包装
		return new PageInfo<Material>(list);
	}

	/**
	 * 导出物料推荐信息
	 *
	 * @param response
	 *            响应
	 * @param wareHouse
	 *            用户当前操作仓库
	 */
	public void exportData(HttpServletResponse response, WareHouse wareHouse, Material mater) {
		if (null == wareHouse || null == wareHouse.getId()) {
			log.error("导出物料推荐信息失败，仓库编号为空");
			return;
		}

		// 导出Excel
		try (XSSFWorkbook wb = new XSSFWorkbook(); OutputStream os = response.getOutputStream()) {
			// 创建工作薄
			XSSFSheet sheet = wb.createSheet();

			// 创建标题列
			XSSFRow header = sheet.createRow(0);
			header.createCell(0).setCellValue("物料编号");
			header.createCell(1).setCellValue("货位");
			header.createCell(2).setCellValue("拣货频率");
			header.createCell(3).setCellValue("原货位分值");
			header.createCell(4).setCellValue("推荐货位");
			header.createCell(5).setCellValue("推荐货位分值");

			// 查询物料信息
			MaterialExample example = new MaterialExample();
			Criteria criteria = example.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
			// 按照查询条件导出
			if (StringUtils.isNoneBlank(mater.getCode())) {
				criteria.andCodeLike("%" + mater.getCode() + "%");
			}
			if (null != mater.getCargoLocationTypeId()) {
				criteria.andCargoLocationTypeIdEqualTo(mater.getCargoLocationTypeId());
			}

			List<Material> materialList = this.materialMapper.selectByExample(example);

			// 填充数据列
			for (int i = 0; i < materialList.size(); i++) {
				Material material = materialList.get(i);
				if (null != material) {
					XSSFRow row = sheet.createRow(i + 1);
					row.createCell(0).setCellValue(material.getCode());
					row.createCell(1).setCellValue(material.getCargoLocationCode());
					row.createCell(3).setCellValue((null != material.getCargoLocationScore()
							? material.getCargoLocationScore().toString() : ""));
					row.createCell(2).setCellValue(
							(null != material.getPickUpRate() ? material.getPickUpRate().toString() : ""));
					row.createCell(4).setCellValue(material.getRecommendedLocationCode());
					row.createCell(5).setCellValue(null != material.getRecommendedLocationScore()
							? material.getRecommendedLocationScore().toString() : "");
				}
			}

			// 初始化文件名
			String filename = "Material_" + new Date().getTime() + ".xlsx";

			// 设置文件头
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

			// 写出excel
			wb.write(os);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("导出物料推荐信息失败，{}", e.getMessage());
		}
	}
}
