package com.dhl.tools.service;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.FutureTask;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dhl.tools.domain.CargoLocation;
import com.dhl.tools.domain.CargoLocationData;
import com.dhl.tools.domain.CargoLocationExample;
import com.dhl.tools.domain.CargoLocationExample.Criteria;
import com.dhl.tools.domain.CargoLocationType;
import com.dhl.tools.domain.CargoLocationTypeExample;
import com.dhl.tools.domain.Material;
import com.dhl.tools.domain.MaterialExample;
import com.dhl.tools.domain.MessageInfo;
import com.dhl.tools.domain.WareHouse;
import com.dhl.tools.mapper.CargoLocationMapper;
import com.dhl.tools.mapper.CargoLocationTypeMapper;
import com.dhl.tools.mapper.CommonMapper;
import com.dhl.tools.mapper.MaterialMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 货位类型业务处理 Created by liuso on 2017/4/12.
 */
@Slf4j
@Service
public class CargoLocationService {

	@Autowired
	private CargoLocationMapper cargoLocationMapper;

	@Autowired
	private CargoLocationTypeMapper cargoLocationTypeMapper;

	@Autowired
	private CommonMapper commonMapper;

	@Autowired
	private MaterialMapper materialMapper;

	@Autowired
	private CargoLocationDataService cargoLocationDataService;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional
	public boolean add(CargoLocation cargoLocation) {

		// 更新对应的货位类型数量
		CargoLocationType cargoLocationType = this.cargoLocationTypeMapper
				.selectByPrimaryKey(cargoLocation.getTypeId());
		cargoLocationType.setTotal(cargoLocationType.getTotal() + 1);
		this.cargoLocationTypeMapper.updateByPrimaryKeySelective(cargoLocationType);
		cargoLocation.setBatchNum(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		int result = cargoLocationMapper.insert(cargoLocation);
		return result > 0;
	}

	@Transactional
	public boolean delete(Integer id) {

		this.commonMapper.updateMaterialLocationByLocationId(id);
		this.commonMapper.updateMaterialRecommendLocationByLocationId(id);

		// 更新对应的货位类型数量
		CargoLocation cargoLocation = this.findById(id);
		CargoLocationType cargoLocationType = this.cargoLocationTypeMapper
				.selectByPrimaryKey(cargoLocation.getTypeId());
		if (null != cargoLocationType) {
			cargoLocationType.setTotal(cargoLocationType.getTotal() - 1);
			this.cargoLocationTypeMapper.updateByPrimaryKeySelective(cargoLocationType);
		}

		int result = cargoLocationMapper.deleteByPrimaryKey(id);
		return result > 0;
	}

	@Transactional
	public void deleteByTypeId(int typeId) {
		CargoLocationExample cargoLocationExample = new CargoLocationExample();
		cargoLocationExample.createCriteria().andTypeIdEqualTo(typeId);

		List<CargoLocation> list = this.cargoLocationMapper.selectByExample(cargoLocationExample);
		if (CollectionUtils.isNotEmpty(list)) {
			for (CargoLocation cargoLocation : list) {
				this.commonMapper.updateMaterialLocationByLocationId(cargoLocation.getId());
				this.commonMapper.updateMaterialRecommendLocationByLocationId(cargoLocation.getId());
				cargoLocationMapper.deleteByPrimaryKey(cargoLocation.getId());
			}
		}
	}

	@Transactional
	public boolean update(CargoLocation cargoLocation) {
		CargoLocation old = this.cargoLocationMapper.selectByPrimaryKey(cargoLocation.getId());
		// 货位类型发生了改变，要更新货位类型对应的总数量
		if (!Objects.equals(old.getTypeId(), cargoLocation.getTypeId())) {

			// 查看当前货位是否已被物料所占用
			MaterialExample example = new MaterialExample();
			example.createCriteria().andCargoLocationIdEqualTo(cargoLocation.getId());
			List<Material> list = this.materialMapper.selectByExample(example);

			boolean useFlag = CollectionUtils.isNotEmpty(list);

			// 原来的货位类型总数量减一
			CargoLocationType cargoLocationType = this.cargoLocationTypeMapper.selectByPrimaryKey(old.getTypeId());
			if (null != cargoLocationType) {
				cargoLocationType.setTotal(cargoLocationType.getTotal() - 1);
				if (useFlag) {
					cargoLocationType.setUseCount(cargoLocationType.getUseCount() - 1);
				}
				this.cargoLocationTypeMapper.updateByPrimaryKeySelective(cargoLocationType);
			}
			// 新的货位类型总数量加一
			cargoLocationType = this.cargoLocationTypeMapper.selectByPrimaryKey(cargoLocation.getTypeId());
			if (null != cargoLocationType) {
				cargoLocationType.setTotal(cargoLocationType.getTotal() + 1);
				if (useFlag) {
					cargoLocationType.setUseCount(cargoLocationType.getUseCount() + 1);
				}
				this.cargoLocationTypeMapper.updateByPrimaryKeySelective(cargoLocationType);
			}
		}
		int result = cargoLocationMapper.updateByPrimaryKeySelective(cargoLocation);
		return result > 0;
	}

	public CargoLocation findById(Integer id) {
		return cargoLocationMapper.selectByPrimaryKey(id);
	}

	public PageInfo<Map<String, Object>> findPage(CargoLocation cargoLocation, Integer page, Integer size) {
		// 获取第1页，10条内容，默认查询总数count
		PageHelper.startPage(page == null ? 1 : page, size == null ? 10 : size);

		List<Map<String, Object>> list = this.commonMapper.findCargoLocationList(cargoLocation);

		// 用PageInfo对结果进行包装
		return new PageInfo<Map<String, Object>>(list);
	}

	public List<CargoLocation> findList(CargoLocation cargoLocation) {
		CargoLocationExample example = new CargoLocationExample();
		Criteria criteria = example.createCriteria().andWareHouseIdEqualTo(cargoLocation.getWareHouseId());
		if (null != cargoLocation.getTypeId() && 0 == cargoLocation.getTypeId()) {
			criteria.andTypeIdEqualTo(cargoLocation.getTypeId());
		}
		return cargoLocationMapper.selectByExample(example);
	}

	/**
	 * 导入货位信息
	 *
	 * @param file
	 *            excel文件
	 * @return
	 */
	@Transactional
	public MessageInfo importData(WareHouse currentWareHouse, String username, MultipartFile file) throws Exception {
		MessageInfo messageInfo = new MessageInfo();
		messageInfo.setSuccess(false);
		if (file.isEmpty()) {
			log.info("导入货位数据失败，文件内容为空");
			messageInfo.setMsg("文件内容为空");
			return messageInfo;
		}

		try (XSSFWorkbook wb = new XSSFWorkbook(file.getInputStream())) {
			// 默认获取第一个工作表
			XSSFSheet sheet = wb.getSheetAt(0);
			if (null != sheet) {
				// 获取工作表的总行数
				int rowCount = sheet.getPhysicalNumberOfRows();
				if (rowCount > 0) {

					// 标题列
					XSSFRow titleRow = sheet.getRow(0);
					if (2 != titleRow.getPhysicalNumberOfCells()) {
						log.info("导入货位数据失败，标题列数不对");
						messageInfo.setMsg("导入货位数据失败，请检查标题列数");
						return messageInfo;
					}

					// 缓存当前仓库下的货位类型
					CargoLocationTypeExample cargoLocationTypeExample = new CargoLocationTypeExample();
					cargoLocationTypeExample.createCriteria().andWareHouseIdEqualTo(currentWareHouse.getId());
					List<CargoLocationType> cargoLocationTypeList = this.cargoLocationTypeMapper
							.selectByExample(cargoLocationTypeExample);

					Map<String, CargoLocationType> cargoLocationTypeMap = new HashMap<>();
					for (CargoLocationType cargoLocationType : cargoLocationTypeList) {
						String k = currentWareHouse.getCode() + cargoLocationType.getCode();
						if (!cargoLocationTypeMap.containsKey(k)) {
							cargoLocationTypeMap.put(k, cargoLocationType);
						}
					}

					// 缓存当前仓库下的货位信息
					CargoLocationExample example = new CargoLocationExample();
					example.createCriteria().andWareHouseIdEqualTo(currentWareHouse.getId());
					List<CargoLocation> cargoLocationList = this.cargoLocationMapper.selectByExample(example);

					Map<String, CargoLocation> cargoLocationMap = new HashMap<>();
					for (CargoLocation cargoLocation : cargoLocationList) {
						String k = currentWareHouse.getCode() + cargoLocation.getCode();
						if (!cargoLocationMap.containsKey(k)) {
							cargoLocationMap.put(k, cargoLocation);
						}
					}

					// 初始化批次号
					String batchNum = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
					// 导入时间
					Date importDate = new Date();

					// 初始化待添加列表
					List<CargoLocation> importList = new ArrayList<>();
					// 初始化更新列表
					List<CargoLocation> updateList = new ArrayList<>();

					// 防止导入重复的数据
					Set<String> codeSet = new HashSet<>();

					// 处理单元格
					for (int i = 1; i < rowCount; i++) {
						// 获取行
						XSSFRow row = sheet.getRow(i);
						if (null != row) {

							// 获取货位名称
							String code = row.getCell(0, CREATE_NULL_AS_BLANK).getStringCellValue();
							if (StringUtils.isBlank(code)) {
								messageInfo.setMsg("导入货位数据失败，第" + (i + 1) + "行货位编号为空");
								return messageInfo;
							}

							// 获取仓库配置的货位编码长度
							Integer fullCodeLength = currentWareHouse.getFullCodeLength();
							if (null != fullCodeLength && fullCodeLength != code.length()) {
								messageInfo.setMsg("导入货位数据失败，第" + (i + 1) + "行货位编号长度有误，应为" + fullCodeLength);
								return messageInfo;
							}

							// 检测货位类型是否已被导入
							String typeCode = row.getCell(1, CREATE_NULL_AS_BLANK).getStringCellValue();
							if (StringUtils.isBlank(typeCode)) {
								messageInfo.setMsg("导入货位数据失败，第" + (i + 1) + "行货位类型编号为空");
								return messageInfo;
							}

							// 从缓存中获取货位类型
							CargoLocationType cargoLocationType = cargoLocationTypeMap
									.get(currentWareHouse.getCode() + typeCode);
							if (null == cargoLocationType) {
								messageInfo.setMsg("导入货位数据失败，第" + (i + 1) + "行货位类型不存在");
								return messageInfo;
							}

							// 从缓存中获取货位
							CargoLocation cargoLocation = cargoLocationMap.get(currentWareHouse.getCode() + code);
							if (null == cargoLocation) {
								cargoLocation = new CargoLocation();
							}

							// 设置批次号
							cargoLocation.setBatchNum(batchNum);
							// 设置编号
							cargoLocation.setCode(code);

							// 设置货位类型
							cargoLocation.setTypeId(cargoLocationType.getId());

							// 设置所属仓库
							cargoLocation.setWareHouseId(currentWareHouse.getId());
							cargoLocation.setCreateDate(importDate);
							cargoLocation.setBatchSerialNumber(i);

							// 根据是否含有主键来进行插入或更新
							if (null != cargoLocation.getId()) {
								cargoLocation.setLastModifiedBy(username);
								cargoLocation.setLastModifiedDate(new Date());
								updateList.add(cargoLocation);
							} else {
								// 添加货位
								if (!codeSet.contains(code)) {
									codeSet.add(code);
									cargoLocation.setCreateBy(username);
									importList.add(cargoLocation);
								}
							}
						}
					}

					// 对货位进行拆分
					FutureTask<List<CargoLocationData>> futureTask = this.cargoLocationDataService
							.processCargoLocationCode(currentWareHouse, importList);

					// 调用存储过程插入货位数据
					if (CollectionUtils.isNotEmpty(importList)) {
						long start = System.currentTimeMillis();
						log.debug("开始调用存储过程sp_uploadValidateCargoLocation保存货位");
						this.jdbcTemplate.execute("{call sp_uploadValidateCargoLocation('"
								+ XmlUtil.getLocationStrByList(importList) + "')}");
						log.debug("调用存储过程sp_uploadValidateCargoLocation,存储{}个货位，耗时{}毫秒", importList.size(),
								System.currentTimeMillis() - start);
					}

					// 更新货位信息
					if (CollectionUtils.isNotEmpty(updateList)) {
						long start = System.currentTimeMillis();
						log.debug("开始调用存储过程sp_updateValidateCargoLocation更新货位");
						this.jdbcTemplate.execute("{call sp_updateValidateCargoLocation('"
								+ XmlUtil.getUpdateLocationStrByList(updateList) + "')}");
						log.debug("调用存储过程sp_updateValidateCargoLocation,更新{}个货位，耗时{}毫秒", importList.size(),
								System.currentTimeMillis() - start);
					}

					// 取得拆分的结果进行插入操作
					List<CargoLocationData> addCargoLocationDataList = futureTask.get();
					if (CollectionUtils.isNotEmpty(addCargoLocationDataList)) {
						long start = System.currentTimeMillis();
						log.debug("开始调用存储过程sp_uploadValidateCargoLocation_Data保存货位数据");
						this.jdbcTemplate.execute("{call sp_uploadValidateCargoLocation_Data('"
								+ XmlUtil.getLocationDataStrByList(addCargoLocationDataList) + "')}");
						log.debug("调用存储过程sp_uploadValidateCargoLocation_Data,存储{}个货位数据，耗时{}毫秒",
								addCargoLocationDataList.size(), System.currentTimeMillis() - start);
					}

					// 将非本批次号的物料所属货位和推荐货位置为null
					this.commonMapper.updateMaterialLocationByLocationBatchNum(currentWareHouse.getId(), batchNum);
					this.commonMapper.updateMaterialRecommendLocationByLocationBatchNum(currentWareHouse.getId(),
							batchNum);

					// 删除所有非本批次的货位
					CargoLocationExample cargoLocationExample = new CargoLocationExample();
					cargoLocationExample.createCriteria().andBatchNumNotEqualTo(batchNum)
							.andWareHouseIdEqualTo(currentWareHouse.getId());
					this.cargoLocationMapper.deleteByExample(cargoLocationExample);

					// 统计货位类型的总数量数量
					List<Map<String, Integer>> useCountMap = this.commonMapper
							.selectLocationTypeTotalCount(currentWareHouse.getId());
					for (Map<String, Integer> map : useCountMap) {
						CargoLocationType cargoLocationType = new CargoLocationType();
						cargoLocationType.setId(map.get("id"));
						cargoLocationType.setTotal(map.get("count"));

						this.cargoLocationTypeMapper.updateByPrimaryKeySelective(cargoLocationType);
					}
				}
			}
			messageInfo.setSuccess(true);
			return messageInfo;
		}
	}

	/**
	 * 判断货位编码是否存在
	 *
	 * @param id
	 *            主键
	 * @param code
	 *            货位类型
	 * 
	 * @return
	 */
	public boolean findCodeIsExist(Integer id, String code, int wareHouseId) {
		CargoLocationExample cargoLocationExample = new CargoLocationExample();
		if (0 != id) {
			cargoLocationExample.createCriteria().andIdNotEqualTo(id).andCodeEqualTo(code)
					.andWareHouseIdEqualTo(wareHouseId);
		} else {
			cargoLocationExample.createCriteria().andCodeEqualTo(code).andWareHouseIdEqualTo(wareHouseId);
		}
		return this.cargoLocationMapper.countByExample(cargoLocationExample) > 0;
	}
}
