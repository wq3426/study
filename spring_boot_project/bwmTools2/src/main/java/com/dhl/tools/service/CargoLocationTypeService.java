package com.dhl.tools.service;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dhl.tools.domain.CargoLocationType;
import com.dhl.tools.domain.CargoLocationTypeExample;
import com.dhl.tools.domain.CargoLocationTypeExample.Criteria;
import com.dhl.tools.domain.MessageInfo;
import com.dhl.tools.domain.WareHouse;
import com.dhl.tools.exception.ImportDataException;
import com.dhl.tools.mapper.CargoLocationTypeMapper;
import com.dhl.tools.mapper.CommonMapper;
import com.dhl.tools.security.MyUserDetails;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 货位类型业务处理 Created by liuso on 2017/4/12.
 */
@Slf4j
@Service
public class CargoLocationTypeService {

	@Autowired
	private CargoLocationTypeMapper cargoLocationTypeMapper;

	@Autowired
	private CargoLocationService cargoLocationService;

	@Autowired
	private CommonMapper commonMapper;

	@Transactional
	public boolean add(CargoLocationType cargoLocationType) {

		cargoLocationType.setExtend(cargoLocationType.getExtend() == null ? false : cargoLocationType.getExtend());

		Double frontIncrease = cargoLocationType.getFrontIncrease();
		if (null == frontIncrease) {
			frontIncrease = 0d;
			cargoLocationType.setFrontIncrease(frontIncrease);
		}
		Double backIncrease = cargoLocationType.getBackIncrease();
		if (null == backIncrease) {
			backIncrease = 0d;
			cargoLocationType.setBackIncrease(backIncrease);
		}
		Double leftIncrease = cargoLocationType.getLeftIncrease();
		if (null == leftIncrease) {
			leftIncrease = 0d;
			cargoLocationType.setLeftIncrease(leftIncrease);
		}
		Double rightIncrease = cargoLocationType.getRightIncrease();
		if (null == rightIncrease) {
			rightIncrease = 0d;
			cargoLocationType.setRightIncrease(rightIncrease);
		}

		// 设置扩展后的总长度
		cargoLocationType.setIncreaseLength(cargoLocationType.getLength() + leftIncrease + rightIncrease);
		// 设置扩展后的总宽度
		cargoLocationType.setIncreaseWidth(cargoLocationType.getWidth() + frontIncrease + backIncrease);

		cargoLocationType.setTotal(0);
		cargoLocationType.setUseCount(0);
		cargoLocationType.setBatchNum(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		int result = cargoLocationTypeMapper.insertSelective(cargoLocationType);
		return result > 0;
	}

	@Transactional
	public boolean delete(Integer id) {

		this.commonMapper.updateMaterialLocationTypeDataByLocationTypeId(id);

		// 重置物料表货位类型值为NULL
		this.commonMapper.updateMaterialLocationTypeByLocationTypeId(id);

		// 删除货位
		this.cargoLocationService.deleteByTypeId(id);
		int result = cargoLocationTypeMapper.deleteByPrimaryKey(id);
		return result > 0;
	}

	@Transactional
	public boolean update(CargoLocationType cargoLocationType) {

		cargoLocationType.setExtend(cargoLocationType.getExtend() == null ? false : cargoLocationType.getExtend());

		Double frontIncrease = cargoLocationType.getFrontIncrease();
		if (null == frontIncrease) {
			frontIncrease = 0d;
			cargoLocationType.setFrontIncrease(frontIncrease);
		}
		Double backIncrease = cargoLocationType.getBackIncrease();
		if (null == backIncrease) {
			backIncrease = 0d;
			cargoLocationType.setBackIncrease(backIncrease);
		}
		Double leftIncrease = cargoLocationType.getLeftIncrease();
		if (null == leftIncrease) {
			leftIncrease = 0d;
			cargoLocationType.setLeftIncrease(leftIncrease);
		}
		Double rightIncrease = cargoLocationType.getRightIncrease();
		if (null == rightIncrease) {
			rightIncrease = 0d;
			cargoLocationType.setRightIncrease(rightIncrease);
		}

		// 设置扩展后的总长度
		cargoLocationType.setIncreaseLength(cargoLocationType.getLength() + leftIncrease + rightIncrease);
		// 设置扩展后的总宽度
		cargoLocationType.setIncreaseWidth(cargoLocationType.getWidth() + frontIncrease + backIncrease);

		int result = cargoLocationTypeMapper.updateByPrimaryKeySelective(cargoLocationType);
		return result > 0;
	}

	public CargoLocationType findById(Integer id) {
		return cargoLocationTypeMapper.selectByPrimaryKey(id);
	}

	public PageInfo<CargoLocationType> findPage(CargoLocationType cargoLocationType, Integer page, Integer size) {
		// 获取第1页，10条内容，默认查询总数count
		PageHelper.startPage(page == null ? 1 : page, size == null ? 10 : size);

		CargoLocationTypeExample example = new CargoLocationTypeExample();
		example.setOrderByClause("createDate desc,batchSerialNumber asc");
		Criteria criteria = example.createCriteria().andWareHouseIdEqualTo(cargoLocationType.getWareHouseId());
		if (StringUtils.isNotEmpty(cargoLocationType.getCode())) {
			criteria.andCodeLike("%" + cargoLocationType.getCode() + "%");
		}

		List<CargoLocationType> list = cargoLocationTypeMapper.selectByExample(example);
		// 用PageInfo对结果进行包装
		return new PageInfo<CargoLocationType>(list);
	}

	public List<CargoLocationType> findList(int wareHouseId) {
		CargoLocationTypeExample example = new CargoLocationTypeExample();
		example.createCriteria().andWareHouseIdEqualTo(wareHouseId);
		return cargoLocationTypeMapper.selectByExample(example);
	}

	/**
	 * 判断货位类型编码是否存在
	 *
	 * @param id
	 *            主键
	 * @param code
	 *            货位类型
	 * 
	 * @return
	 */
	public boolean findCodeIsExist(Integer id, String code, int wareHouseId) {
		CargoLocationTypeExample cargoLocationTypeExample = new CargoLocationTypeExample();
		if (0 != id) {
			cargoLocationTypeExample.createCriteria().andIdNotEqualTo(id).andCodeEqualTo(code)
					.andWareHouseIdEqualTo(wareHouseId);
		} else {
			cargoLocationTypeExample.createCriteria().andCodeEqualTo(code).andWareHouseIdEqualTo(wareHouseId);
		}
		return this.cargoLocationTypeMapper.countByExample(cargoLocationTypeExample) > 0;
	}

	/**
	 * 导入货位类型信息
	 *
	 * @param file
	 *            excel文件
	 * @return
	 */
	@Transactional
	public MessageInfo importData(WareHouse wareHouse, String username, MultipartFile file) throws Exception {
		MessageInfo messageInfo = new MessageInfo();
		messageInfo.setSuccess(false);
		if (null == file || file.isEmpty()) {
			log.info("导入货位类型数据失败，文件内容为空");
			messageInfo.setMsg("文件内容为空");
			return messageInfo;
		}
		try (InputStream is = file.getInputStream(); XSSFWorkbook wb = new XSSFWorkbook(is)) {
			// 默认获取第一个工作表
			XSSFSheet sheet = wb.getSheetAt(0);
			if (null != sheet) {
				// 获取工作表的总行数
				int rowCount = sheet.getPhysicalNumberOfRows();
				if (rowCount > 0) {

					// 标题列
					XSSFRow titleRow = sheet.getRow(0);
					if (9 != titleRow.getPhysicalNumberOfCells()) {
						log.info("导入货位类型数据失败，标题列数不对");
						messageInfo.setMsg("导入货位类型数据失败，请检查标题列数");
						return messageInfo;
					}

					// 导入时间
					Date importDate = new Date();
					// 创建批次号
					String batchNum = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
					// 为保证本次导入的数据不重复
					Set<String> cargoLocationCodeSet = new HashSet<String>();

					// 循环处理货位类型信息
					for (int i = 1; i < rowCount; i++) {
						// 获取行
						XSSFRow row = sheet.getRow(i);
						if (null != row) {

							// 获取类型名称
							String name = row.getCell(0, CREATE_NULL_AS_BLANK).getStringCellValue();

							if (StringUtils.isEmpty(name)) {
								throw new ImportDataException("第" + (i + 1) + "行货位类型编号为空");
							}

							// 检测编号是否已存在
							CargoLocationTypeExample example = new CargoLocationTypeExample();
							example.createCriteria().andCodeEqualTo(name).andWareHouseIdEqualTo(wareHouse.getId());
							List<CargoLocationType> list = this.cargoLocationTypeMapper.selectByExample(example);

							CargoLocationType cargoLocationType;
							if (null != list && list.size() > 0) {
								cargoLocationType = list.get(0);
							} else {
								cargoLocationType = new CargoLocationType();
							}

							cargoLocationType.setCode(name);

							// 设置批次号
							cargoLocationType.setBatchNum(batchNum);

							// 是否为托盘
							String palletStr = row.getCell(1, CREATE_NULL_AS_BLANK).getStringCellValue();
							if (StringUtils.isEmpty(palletStr)) {
								log.info("导入货位类型，托盘类型设置错误 {} 请设置是或否", palletStr);
								throw new ImportDataException("第" + (i + 1) + "行，托盘类型设置错误，内容为空");
							}
							if (!palletStr.equals("是") && !palletStr.equals("否")) {
								log.info("导入货位类型，托盘类型设置错误 {} 请设置是或否", palletStr);
								throw new ImportDataException("第" + (i + 1) + "行，托盘类型设置错误，请设置是或否");
							}
							cargoLocationType.setPallet(palletStr.equals("是"));

							cargoLocationType.setLength(row.getCell(2, CREATE_NULL_AS_BLANK).getNumericCellValue());
							if (cargoLocationType.getLength() <= 0) {
								log.info("导入货位类型，货位类型长度 {} 必须大于0", cargoLocationType.getLength());
								throw new ImportDataException("第" + (i + 1) + "行，货位类型长度必须大于0");
							}
							cargoLocationType.setWidth(row.getCell(3, CREATE_NULL_AS_BLANK).getNumericCellValue());
							if (cargoLocationType.getWidth() <= 0) {
								log.info("导入货位类型，货位类型宽度 {} 必须大于0", cargoLocationType.getWidth());
								throw new ImportDataException("第" + (i + 1) + "行，货位类型宽度必须大于0");
							}
							cargoLocationType.setHeight(row.getCell(4, CREATE_NULL_AS_BLANK).getNumericCellValue());
							if (cargoLocationType.getHeight() <= 0) {
								log.info("导入货位类型，货位类型高度 {} 必须大于0", cargoLocationType.getHeight());
								throw new ImportDataException("第" + (i + 1) + "行，货位类型高度必须大于0");
							}

							if (!cargoLocationType.getPallet()) {
								Double leftIncrease = row.getCell(5, CREATE_NULL_AS_BLANK).getNumericCellValue();
								if (leftIncrease < 0) {
									log.info("导入货位类型，货位类型左边扩展长度 {} 必须大于等于0", leftIncrease);
									throw new ImportDataException("第" + (i + 1) + "行，货位类型左边扩展长度必须大于等于0");
								}
								cargoLocationType.setLeftIncrease(leftIncrease);

								Double rightIncrease = row.getCell(6, CREATE_NULL_AS_BLANK).getNumericCellValue();
								if (rightIncrease < 0) {
									log.info("导入货位类型，货位类型右边扩展长度 {} 必须大于等于0", rightIncrease);
									throw new ImportDataException("第" + (i + 1) + "行，货位类型右边扩展长度必须大于等于0");
								}
								cargoLocationType.setRightIncrease(rightIncrease);

								Double frontIncrease = row.getCell(7, CREATE_NULL_AS_BLANK).getNumericCellValue();
								if (frontIncrease < 0) {
									log.info("导入货位类型，货位类型前边扩展长度 {} 必须大于等于0", frontIncrease);
									throw new ImportDataException("第" + (i + 1) + "行，货位类型前边扩展长度必须大于等于0");
								}
								cargoLocationType.setFrontIncrease(frontIncrease);

								Double backIncrease = row.getCell(8, CREATE_NULL_AS_BLANK).getNumericCellValue();
								if (backIncrease < 0) {
									log.info("导入货位类型，货位类型后边扩展长度 {} 必须大于等于0", backIncrease);
									throw new ImportDataException("第" + (i + 1) + "行，货位类型后边扩展长度必须大于等于0");
								}
								cargoLocationType.setBackIncrease(backIncrease);
							} else {
								cargoLocationType.setLeftIncrease(0d);
								cargoLocationType.setRightIncrease(0d);
								cargoLocationType.setFrontIncrease(0d);
								cargoLocationType.setBackIncrease(0d);
							}

							if (cargoLocationType.getPallet() || (0 == cargoLocationType.getLeftIncrease()
									&& 0 == cargoLocationType.getRightIncrease()
									&& 0 == cargoLocationType.getFrontIncrease()
									&& 0 == cargoLocationType.getBackIncrease())) {
								cargoLocationType.setExtend(false);
							} else {
								cargoLocationType.setExtend(true);

								// 设置扩展后的总长度
								cargoLocationType.setIncreaseLength(cargoLocationType.getLength()
										+ cargoLocationType.getLeftIncrease() + cargoLocationType.getRightIncrease());
								// 设置扩展后的总宽度
								cargoLocationType.setIncreaseWidth(cargoLocationType.getWidth()
										+ cargoLocationType.getFrontIncrease() + cargoLocationType.getBackIncrease());
							}

							// 设置所属仓库
							cargoLocationType.setWareHouseId(wareHouse.getId());
							cargoLocationType.setCreateDate(importDate);
							cargoLocationType.setBatchSerialNumber(i);
							// 根据是否含有主键来进行插入或更新
							if (null != cargoLocationType.getId()) {
								// 更新货位类型
								this.cargoLocationTypeMapper.updateByPrimaryKeySelective(cargoLocationType);
							} else {
								// 保证本次导入的货位类型数据唯一
								if (!cargoLocationCodeSet.contains(name)) {
									// 添加货位类型
									cargoLocationType.setTotal(0);
									cargoLocationType.setUseCount(0);
									cargoLocationType.setCreateBy(username);
									this.cargoLocationTypeMapper.insert(cargoLocationType);
									cargoLocationCodeSet.add(name);
								}
							}
						}
					}

					// 查询所有非本批次的货位类型
					CargoLocationTypeExample cargoLocationTypeExample = new CargoLocationTypeExample();
					cargoLocationTypeExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId())
							.andBatchNumNotEqualTo(batchNum);
					List<CargoLocationType> cargoLocationTypeList = this.cargoLocationTypeMapper
							.selectByExample(cargoLocationTypeExample);

					// 循环删除货位类型
					for (CargoLocationType cargoLocationType : cargoLocationTypeList) {
						if (null != cargoLocationType) {
							// 根据货位类型删除货位，并设置货位所对应的物料的货位信息为空
							this.cargoLocationService.deleteByTypeId(cargoLocationType.getId());
							// 根据主键删除货位类型
							this.cargoLocationTypeMapper.deleteByPrimaryKey(cargoLocationType.getId());
						}
					}
				}
			}
		}
		messageInfo.setSuccess(true);
		return messageInfo;
	}

	/**
	 * 导出货位类型
	 *
	 * @param currentUser
	 *            当前登录用户
	 */
	public void exportData(MyUserDetails currentUser, HttpServletResponse response) {
		// 导出Excel
		try (XSSFWorkbook wb = new XSSFWorkbook(); OutputStream os = response.getOutputStream()) {
			// 创建工作薄
			XSSFSheet sheet = wb.createSheet();

			// 创建标题列
			XSSFRow header = sheet.createRow(0);
			header.createCell(0).setCellValue("货位类型编号");
			header.createCell(1).setCellValue("是否托盘");
			header.createCell(2).setCellValue("长(m)");
			header.createCell(3).setCellValue("宽(m)");
			header.createCell(4).setCellValue("高(m)");
			header.createCell(5).setCellValue("左(m)");
			header.createCell(6).setCellValue("右(m)");
			header.createCell(7).setCellValue("前(m)");
			header.createCell(8).setCellValue("后(m)");

			// 查询货位类型信息
			CargoLocationTypeExample example = new CargoLocationTypeExample();
			example.setOrderByClause("createDate desc,batchSerialNumber asc");
			example.createCriteria().andWareHouseIdEqualTo(currentUser.getCurrentWareHouse().getId());
			List<CargoLocationType> list = this.cargoLocationTypeMapper.selectByExample(example);

			// 填充数据列
			for (int i = 0; i < list.size(); i++) {
				CargoLocationType cargoLocationType = list.get(i);
				if (null != cargoLocationType) {
					XSSFRow row = sheet.createRow(i + 1);
					row.createCell(0).setCellValue(cargoLocationType.getCode());
					row.createCell(1).setCellValue(cargoLocationType.getPallet() ? "是" : "否");
					row.createCell(2).setCellValue(cargoLocationType.getLength());
					row.createCell(3).setCellValue(cargoLocationType.getWidth());
					row.createCell(4).setCellValue(cargoLocationType.getHeight());
					row.createCell(5).setCellValue(cargoLocationType.getLeftIncrease());
					row.createCell(6).setCellValue(cargoLocationType.getRightIncrease());
					row.createCell(7).setCellValue(cargoLocationType.getFrontIncrease());
					row.createCell(8).setCellValue(cargoLocationType.getBackIncrease());
				}
			}

			// 初始化文件名
			String filename = "CargoLocationType_" + new Date().getTime() + ".xlsx";

			// 设置文件头
			response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

			// 写出excel
			wb.write(os);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("导出货位类型信息失败，{}", e.getMessage());
		}
	}

	/**
	 * 更新使用数量
	 * 
	 * @param id
	 * @return
	 */
	@Transactional
	public int updateUseCount(Integer id) {
		CargoLocationType cargoLocationType = this.cargoLocationTypeMapper.selectByPrimaryKey(id);
		cargoLocationType.setId(id);
		cargoLocationType.setUseCount(this.commonMapper.selectLocationTypeUseCountById(id));
		return this.cargoLocationTypeMapper.updateByPrimaryKey(cargoLocationType);
	}
}
