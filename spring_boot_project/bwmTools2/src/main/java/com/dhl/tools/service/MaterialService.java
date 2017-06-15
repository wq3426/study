package com.dhl.tools.service;

import static org.apache.poi.ss.usermodel.Row.MissingCellPolicy.CREATE_NULL_AS_BLANK;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.dhl.tools.domain.CargoLocation;
import com.dhl.tools.domain.CargoLocationExample;
import com.dhl.tools.domain.CargoLocationType;
import com.dhl.tools.domain.CargoLocationTypeExample;
import com.dhl.tools.domain.Material;
import com.dhl.tools.domain.MaterialCargoLocationTypeData;
import com.dhl.tools.domain.MaterialCargoLocationTypeDataExample;
import com.dhl.tools.domain.MaterialExample;
import com.dhl.tools.domain.MaterialExample.Criteria;
import com.dhl.tools.domain.MessageInfo;
import com.dhl.tools.domain.WareHouse;
import com.dhl.tools.mapper.CargoLocationMapper;
import com.dhl.tools.mapper.CargoLocationTypeMapper;
import com.dhl.tools.mapper.CommonMapper;
import com.dhl.tools.mapper.MaterialCargoLocationTypeDataMapper;
import com.dhl.tools.mapper.MaterialMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户业务处理 Created by liuso on 2017/4/12.
 */
@Slf4j
@Service
public class MaterialService {

	// 默认手臂的长度为0.7m
	public static final double ARM_LENGTH = 0.7d;

	DataFormatter formatter = new DataFormatter();
	@Autowired
	private MaterialMapper materialMapper;
	@Autowired
	private CargoLocationTypeMapper cargoLocationTypeMapper;
	@Autowired
	private CargoLocationMapper cargoLocationMapper;
	@Autowired
	private MaterialCargoLocationTypeDataService materialCargoLocationTypeDataService;
	@Autowired
	private CommonMapper commonMapper;
	@Autowired
	private MaterialCargoLocationTypeDataMapper materialCargoLocationTypeDataMapper;
	@Autowired
	private CargoLocationTypeService cargoLocationTypeService;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Transactional
	public boolean add(Material material, WareHouse wareHouse) throws Exception {

		if (null != material.getCargoLocationId()) {
			CargoLocation cargoLocation = this.cargoLocationMapper.selectByPrimaryKey(material.getCargoLocationId());
			material.setCargoLocationCode(cargoLocation.getCode());
		}

		int result = materialMapper.insert(material);
		if (result > 0) {
			// 更新货位类型的使用数量
			if (null != material.getCargoLocationId()) {
				this.cargoLocationTypeService.updateUseCount(material.getCargoLocationTypeId());
			}
			MaterialExample example = new MaterialExample();
			example.createCriteria().andWareHouseIdEqualTo(wareHouse.getId()).andCodeEqualTo(material.getCode());
			// 推荐货位
			this.processCargoLocationTypeData(this.materialMapper.selectByExample(example), wareHouse);
		}
		return result > 0;
	}

	@Transactional
	public boolean delete(Integer id) {

		Material material = this.materialMapper.selectByPrimaryKey(id);

		// 清理推荐货位类型缓存
		MaterialCargoLocationTypeDataExample example = new MaterialCargoLocationTypeDataExample();
		example.createCriteria().andMaterialIdEqualTo(id);
		materialCargoLocationTypeDataMapper.deleteByExample(example);

		// 删除物料
		int result = materialMapper.deleteByPrimaryKey(id);

		// 更新货位类型使用数量
		if (null != material && null != material.getCargoLocationId()) {
			this.upateLoationTypeUseCountByLocationId(material.getCargoLocationId());
		}
		return result > 0;
	}

	@Transactional
	public boolean update(Material material, WareHouse wareHouse) throws Exception {

		Material oldMaterial = this.materialMapper.selectByPrimaryKey(material.getId());

		if (null != material.getCargoLocationId()) {
			CargoLocation cargoLocation = this.cargoLocationMapper.selectByPrimaryKey(material.getCargoLocationId());
			material.setCargoLocationCode(cargoLocation.getCode());
		}
		int result = materialMapper.updateByPrimaryKeySelective(material);

		// 更新货位类型使用数量
		if (oldMaterial.getCargoLocationId() != null && material.getCargoLocationId() != null) {
			if (oldMaterial.getCargoLocationId() != material.getCargoLocationId()) {
				this.upateLoationTypeUseCountByLocationId(oldMaterial.getCargoLocationId());
				this.upateLoationTypeUseCountByLocationId(material.getCargoLocationId());
			}
		} else if (oldMaterial.getCargoLocationId() != null) {
			this.upateLoationTypeUseCountByLocationId(oldMaterial.getCargoLocationId());
		} else if (material.getCargoLocationId() != null) {
			this.upateLoationTypeUseCountByLocationId(material.getCargoLocationId());
		}
		// 重新推荐货位
		this.processCargoLocationTypeData(Arrays.asList(material), wareHouse);
		return result > 0;
	}

	/**
	 * 根据货位id更新货位类型的使用数量
	 * 
	 * @param cargoLocationId
	 */
	@Transactional
	private void upateLoationTypeUseCountByLocationId(Integer cargoLocationId) {
		CargoLocation cargoLocation = this.cargoLocationMapper.selectByPrimaryKey(cargoLocationId);
		this.cargoLocationTypeService.updateUseCount(cargoLocation.getTypeId());
	}

	public Material findById(Integer id) {
		return materialMapper.selectByPrimaryKey(id);
	}

	public PageInfo<Material> findPage(Material material, Integer page, Integer size) {
		// 获取第1页，10条内容，默认查询总数count
		PageHelper.startPage(page == null ? 1 : page, size == null ? 10 : size);

		MaterialExample example = new MaterialExample();
		example.setOrderByClause("createDate desc,batchSerialNumber asc");
		Criteria criteria = example.createCriteria().andWareHouseIdEqualTo(material.getWareHouseId());
		if (StringUtils.isNoneBlank(material.getCode())) {
			criteria.andCodeLike("%" + material.getCode() + "%");
		}
		if (StringUtils.isNoneBlank(material.getOptimalLocationType())) {
			criteria.andOptimalLocationTypeLike("%" + material.getOptimalLocationType() + "%");
		}
		if (StringUtils.isNoneBlank(material.getRecommendedLocationType())) {
			criteria.andRecommendedLocationTypeLike("%" + material.getRecommendedLocationType() + "%");
		}

		List<Material> list = materialMapper.selectByExample(example);
		// 用PageInfo对结果进行包装
		return new PageInfo<Material>(list);
	}

	public List<Material> findList() {
		return materialMapper.selectByExample(null);
	}

	/**
	 * 判断物料编码是否存在
	 *
	 * @param id
	 * @param code
	 * @param wareHouseId
	 * @return
	 */
	public boolean findCodeIsExist(Integer id, String code, int wareHouseId) {
		MaterialExample materialExample = new MaterialExample();
		if (0 != id) {
			materialExample.createCriteria().andIdNotEqualTo(id).andCodeEqualTo(code)
					.andWareHouseIdEqualTo(wareHouseId);
		} else {
			materialExample.createCriteria().andCodeEqualTo(code).andWareHouseIdEqualTo(wareHouseId);
		}
		return this.materialMapper.countByExample(materialExample) > 0;
	}

	public List<Material> findList(int wareHouseId) {
		MaterialExample materialExample = new MaterialExample();
		materialExample.createCriteria().andWareHouseIdEqualTo(wareHouseId);
		return materialMapper.selectByExample(materialExample);
	}

	/**
	 * 导入数据
	 *
	 * @param wareHouse
	 *            当前用户操作仓库
	 * @param file
	 *            excel文件
	 * @return
	 */
	@Transactional
	public MessageInfo importData(WareHouse wareHouse, String username, MultipartFile file) throws Exception {

		MessageInfo messageInfo = new MessageInfo();
		messageInfo.setSuccess(false);
		if (null == file || file.isEmpty()) {
			log.info("导入物料数据失败，文件内容为空");
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
					if (6 != titleRow.getPhysicalNumberOfCells()) {
						log.info("导入物料数据失败，标题列数不对");
						messageInfo.setMsg("导入物料数据失败，请检查标题列数");
						return messageInfo;
					}

					// 导入时间
					Date importDate = new Date();
					// 创建批次号
					String batchNum = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

					// 缓存当前仓库中的货位信息
					CargoLocationExample cargoLocationExample = new CargoLocationExample();
					cargoLocationExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
					List<CargoLocation> cargoLocationList = this.cargoLocationMapper
							.selectByExample(cargoLocationExample);

					Map<String, CargoLocation> cargoLocationMap = new HashMap<>();
					for (CargoLocation cargoLocation : cargoLocationList) {
						if (!cargoLocationMap.containsKey(cargoLocation.getCode())) {
							cargoLocationMap.put(cargoLocation.getCode(), cargoLocation);
						}
					}

					// 缓存当前仓库中的物料信息
					MaterialExample materialExample = new MaterialExample();
					materialExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
					List<Material> materialList = this.materialMapper.selectByExample(materialExample);

					Map<String, Material> materialMap = new HashMap<>();
					for (Material material : materialList) {
						if (!materialMap.containsKey(material.getCode())) {
							materialMap.put(material.getCode(), material);
						}
					}

					List<Material> insertList = new ArrayList<>();
					List<Material> updateList = new ArrayList<>();

					// 为保证本次导入的物料编码数据不重复
					Set<String> materialCodeSet = new HashSet<String>();

					for (int i = 1; i < rowCount; i++) {
						// 获取行
						XSSFRow row = sheet.getRow(i);
						if (null != row) {
							// 获取编号
							String code = formatter.formatCellValue(row.getCell(0));

							if (StringUtils.isEmpty(code)) {
								messageInfo.setMsg("第" + i + "行物料编号为空");
								return messageInfo;
							}

							double length = row.getCell(1, CREATE_NULL_AS_BLANK).getNumericCellValue();
							if (length <= 0) {
								log.info("导入物料，物料长度 {} 必须大于0", length);
								messageInfo.setMsg("第" + (i + 1) + "行物料长度必须大于0");
								return messageInfo;
							}
							double width = row.getCell(2, CREATE_NULL_AS_BLANK).getNumericCellValue();
							if (width <= 0) {
								log.info("导入物料，物料深度 {} 必须大于0", width);
								messageInfo.setMsg("第" + (i + 1) + "行物料深度必须大于0");
								return messageInfo;
							}
							double height = row.getCell(3, CREATE_NULL_AS_BLANK).getNumericCellValue();
							if (height <= 0) {
								log.info("导入物料，物料高度 {} 必须大于0", height);
								messageInfo.setMsg("第" + (i + 1) + "行物料高度必须大于0");
								return messageInfo;
							}
							Double maxStore = row.getCell(4, CREATE_NULL_AS_BLANK).getNumericCellValue();
							if (maxStore <= 0) {
								log.info("导入物料，物料最大存放 {} 必须大于0", maxStore);
								messageInfo.setMsg("第" + (i + 1) + "行物料最大存放必须大于0");
								return messageInfo;
							}

							// 检测编号是否已存在
							Material material = materialMap.get(code);
							if (null == material) {
								material = new Material();
							} else {
								// 如果物料的长宽高、最大存放没有变化则不更新
								if (length == material.getLength() && width == material.getWidth()
										&& height == material.getHeight()
										&& maxStore.intValue() == material.getMaxStore()) {
									continue;
								}
							}

							material.setCode(code);
							material.setLength(length);
							material.setWidth(width);
							material.setHeight(height);
							material.setMaxStore(maxStore.intValue());

							// 货位编码
							String locationCode = formatter.formatCellValue(row.getCell(5));
							if (StringUtils.isNoneBlank(locationCode)) {
								if (!cargoLocationMap.containsKey(locationCode)) {
									log.info("导入物料，货位编号 {} 不存在", locationCode);
									messageInfo.setMsg("第" + (i + 1) + "行货位编号" + locationCode + "不存在");
									return messageInfo;
								}
								CargoLocation cargoLocation = cargoLocationMap.get(locationCode);
								material.setCargoLocationId(cargoLocation.getId());
								material.setCargoLocationCode(locationCode);
								material.setCargoLocationTypeId(cargoLocation.getTypeId());
							}

							// 设置所属仓库
							material.setWareHouseId(wareHouse.getId());
							material.setCreateDate(importDate);
							material.setBatchNum(batchNum);
							material.setBatchSerialNumber(i);

							// 根据是否含有主键来进行插入或更新
							if (null != material.getId()) {
								// 更新物料
								updateList.add(material);
							} else {
								if (!materialCodeSet.contains(code)) {
									// 添加物料
									material.setFaceUp(true);
									material.setCreateBy(username);
									insertList.add(material);
									materialCodeSet.add(code);
								}
							}
						}
					}

					// 新增
					if (CollectionUtils.isNotEmpty(insertList)) {
						this.jdbcTemplate.execute("{call sp_uploadValidateMaterial('"
								+ XmlUtil.getInsertMaterialStrByList(insertList) + "')}");
					}

					// 更新
					for (Material material : updateList) {
						this.materialMapper.updateByPrimaryKeySelective(material);
					}

					// 更新货位类型的使用数量
					List<Map<String, Integer>> useCountMap = this.commonMapper
							.selectLocationTypeUseCount(wareHouse.getId());
					for (Map<String, Integer> map : useCountMap) {
						CargoLocationType cargoLocationType = new CargoLocationType();
						cargoLocationType.setId(map.get("typeId"));
						cargoLocationType.setUseCount(map.get("count"));

						this.cargoLocationTypeMapper.updateByPrimaryKeySelective(cargoLocationType);
					}

					// 为物料重新推荐货位类型
					this.processCargoLocationTypeData(null, wareHouse);
				}
			}
			messageInfo.setSuccess(true);
			return messageInfo;
		}
	}

	/**
	 * 更新库存
	 *
	 * @param wareHouse
	 *            当前用户操作仓库
	 * @param file
	 *            excel文件
	 * @return
	 */
	@Transactional
	public MessageInfo updateData(WareHouse wareHouse, MultipartFile file) throws Exception {
		MessageInfo messageInfo = new MessageInfo();
		messageInfo.setSuccess(false);
		if (null == file || file.isEmpty()) {
			log.info("更新库存失败，文件内容为空");
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

					// 导入时间
					Date importDate = new Date();

					// 缓存当前仓库中的货位信息
					CargoLocationExample cargoLocationExample = new CargoLocationExample();
					cargoLocationExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
					List<CargoLocation> cargoLocationList = this.cargoLocationMapper
							.selectByExample(cargoLocationExample);

					Map<String, CargoLocation> cargoLocationMap = new HashMap<>();
					for (CargoLocation cargoLocation : cargoLocationList) {
						if (!cargoLocationMap.containsKey(cargoLocation.getCode())) {
							cargoLocationMap.put(cargoLocation.getCode(), cargoLocation);
						}
					}

					// 缓存当前仓库中的物料信息
					MaterialExample materialExample = new MaterialExample();
					materialExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
					List<Material> materialList = this.materialMapper.selectByExample(materialExample);

					Map<String, Material> materialMap = new HashMap<>();
					for (Material material : materialList) {
						if (!materialMap.containsKey(material.getCode())) {
							materialMap.put(material.getCode(), material);
						}
					}

					// 更新库存列表
					List<Material> updateMaterialList = new ArrayList<>();

					for (int i = 1; i < rowCount; i++) {
						// 获取行
						XSSFRow row = sheet.getRow(i);
						if (null != row) {
							// 获取物料编号
							String materialCode = formatter.formatCellValue(row.getCell(0));

							// 检测编号是否已存在
							Material material = materialMap.get(materialCode);
							if (null == material) {
								log.info("更新库存失败，物料编号 {} 不存在", materialCode);
								messageInfo.setMsg("第" + (i + 1) + "行，物料编号" + materialCode + "不存在");
								return messageInfo;
							}

							// 获取货位编号
							String cargoLocationCode = formatter.formatCellValue(row.getCell(1));

							// 检测货位编号是否已存在
							CargoLocation cargoLocation = cargoLocationMap.get(cargoLocationCode);
							if (null == cargoLocation) {
								log.info("更新库存失败，货位编号 {} 不存在", cargoLocationCode);
								messageInfo.setMsg("第" + (i + 1) + "行，货位编号" + cargoLocationCode + "不存在");
								return messageInfo;
							}

							// 设置所属货位
							material.setCargoLocationId(cargoLocation.getId());
							material.setCargoLocationTypeId(cargoLocation.getTypeId());
							material.setCreateDate(importDate);
							material.setBatchSerialNumber(i);

							updateMaterialList.add(material);
						}
					}

					// 更新物料
					if (CollectionUtils.isNotEmpty(updateMaterialList)) {
						this.jdbcTemplate.execute("{call sp_updateMaterialStock('"
								+ XmlUtil.getMaterialStockStrByList(updateMaterialList) + "')}");
					}

					// 统计物料所使用货位数量
					List<Map<String, Integer>> useCountMap = this.commonMapper
							.selectLocationTypeUseCount(wareHouse.getId());
					for (Map<String, Integer> map : useCountMap) {
						CargoLocationType cargoLocationType = new CargoLocationType();
						cargoLocationType.setId(map.get("typeId"));
						cargoLocationType.setUseCount(map.get("count"));

						this.cargoLocationTypeMapper.updateByPrimaryKeySelective(cargoLocationType);
					}

					// 为物料重新推荐货位类型
					processCargoLocationTypeData(null, wareHouse);
				}
			}
			messageInfo.setSuccess(true);
			return messageInfo;
		}
	}

	/**
	 * 为物料重新推荐货位类型信息
	 *
	 * @param wareHouse
	 *            当前仓库
	 */
	@Transactional
	public MessageInfo processCargoLocationTypeData(List<Material> materialList, WareHouse wareHouse) throws Exception {

		MessageInfo messageInfo = new MessageInfo();
		messageInfo.setSuccess(false);
		if (null == wareHouse) {
			messageInfo.setMsg("用户当前仓库为空");
			return messageInfo;
		}
		// 统计物料所使用的货位
		if (null == materialList) {
			MaterialExample example = new MaterialExample();
			example.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
			materialList = this.materialMapper.selectByExample(example);
		}

		if (null != materialList && materialList.size() > 0) {

			// 查询当前仓库下面除地堆类型外的所有货位类型
			CargoLocationTypeExample cargoLocationTypeExample = new CargoLocationTypeExample();
			cargoLocationTypeExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId())
					.andCodeNotEqualTo(wareHouse.getStallType());
			List<CargoLocationType> cargoLocationTypes = this.cargoLocationTypeMapper
					.selectByExample(cargoLocationTypeExample);

			this.batchRecommendCargoLocationType(wareHouse, materialList, cargoLocationTypes);

			// 更新物料信息
			if (CollectionUtils.isNotEmpty(materialList)) {
				this.jdbcTemplate.execute("{call sp_updateMaterialRecommend('"
						+ XmlUtil.getMaterialRecommendStrByList(materialList) + "')}");
			}
		}
		messageInfo.setSuccess(true);
		return messageInfo;
	}

	/**
	 * 多线程执行任务
	 * 
	 * @param wareHouse
	 * @param materials
	 * @param cargoLocationTypes
	 * @throws Exception
	 */
	private void batchRecommendCargoLocationType(final WareHouse wareHouse, List<Material> materials,
			final List<CargoLocationType> cargoLocationTypes) throws Exception {

		ExecutorService threadPool = Executors.newCachedThreadPool();

		try {
			CompletionService<Integer> cs = new ExecutorCompletionService<Integer>(threadPool);

			int len = Double.valueOf(Math.ceil(materials.size() / 100.0)).intValue();

			for (int i = 0; i < len; i++) {

				int end = (i == (len - 1) ? materials.size() : (i + 1) * 100);
				final List<Material> list = materials.subList(i * 100, end);

				cs.submit(new Callable<Integer>() {
					public Integer call() throws Exception {
						for (Material material : list) {
							recommendCargoLocationType(wareHouse, material, cargoLocationTypes);
						}
						return list.size();
					}
				});
			}
			// 等待每个线程都执行完
			for (int i = 0; i < len; i++) {
				cs.take().get();
			}
		} finally {
			threadPool.shutdown();
		}
	}

	/**
	 * 推荐货位类型
	 * 
	 * @param wareHouse
	 * @param material
	 * @param cargoLocationTypes
	 */
	private void recommendCargoLocationType(WareHouse wareHouse, Material material,
			List<CargoLocationType> cargoLocationTypes) {

		if (null != material) {

			// 计算所有合适货位
			// this.materialCargoLocationTypeDataService.calculateTheOptimalPlacement(wareHouse,
			// material);

			// 非扩展
			List<String> idSet = new ArrayList<String>();
			// 扩展
			List<String> extendsIdSet = new ArrayList<>();

			// 获取非扩展合适货位类型(如果利用率相同则选择货位类型体积小的)
			// List<MaterialCargoLocationTypeData>
			// allAppropriateLibraryTypes = this.commonMapper
			// .selectAllAppropriateLibraryTypes(wareHouse.getId(),
			// material.getId());

			List<MaterialCargoLocationTypeData> allAppropriateLibraryTypes = this.materialCargoLocationTypeDataService
					.calculateTheOptimalPlacement(cargoLocationTypes, wareHouse, material);

			if (CollectionUtils.isNotEmpty(allAppropriateLibraryTypes)) {

				for (MaterialCargoLocationTypeData materialCargoLocationTypeData : allAppropriateLibraryTypes) {

					if (null != materialCargoLocationTypeData) {

						String cargoLocationTypeCode = materialCargoLocationTypeData.getCargoLocationTypeCode();

						// 非扩展
						if (!materialCargoLocationTypeData.getCargoLocationTypeExtend()) {

							if (materialCargoLocationTypeData.getCargoLocationTypePallet()) {
								// 如果是托盘类型需要显示能用几个货位存放，显示格式如：P1(2)
								// 使用货位的数量
								int useCount = Double.valueOf(
										Math.ceil(Double.valueOf(materialCargoLocationTypeData.getMaterialMaxStore())
												/ materialCargoLocationTypeData.getStoreCount()))
										.intValue();
								idSet.add(cargoLocationTypeCode + "(" + useCount + ")");
							} else {
								idSet.add(cargoLocationTypeCode);
							}
						} // 扩展
						else {
							extendsIdSet.add(cargoLocationTypeCode);
						}
					}
				}
			}

			// 获取扩展合适货位类型
			// List<MaterialCargoLocationTypeData>
			// extendAllAppropriateLibraryTypes = this.commonMapper
			// .selectExtendAllAppropriateLibraryTypes(wareHouse.getId(),
			// material.getId());
			//
			// if (null != extendAllAppropriateLibraryTypes &&
			// extendAllAppropriateLibraryTypes.size() > 0) {
			// for (MaterialCargoLocationTypeData
			// materialCargoLocationTypeData :
			// extendAllAppropriateLibraryTypes) {
			// if (null != materialCargoLocationTypeData) {
			// extendsIdSet.add(materialCargoLocationTypeData.getCargoLocationTypeCode());
			// }
			// }
			// }

			material.setAllAppropriateLibraryTypes("");
			if (idSet.size() > 0) {
				material.setAllAppropriateLibraryTypes(StringUtils.join(idSet, ","));
			}
			material.setExtendAllAppropriateLibraryTypes("");
			if (extendsIdSet.size() > 0) {
				material.setExtendAllAppropriateLibraryTypes(StringUtils.join(extendsIdSet, ","));
			}

			// 最优推荐货位类型
			// MaterialCargoLocationTypeData optimalLocationTypeData
			// =
			// this.getOptimalCargoLocationTypeData(
			// allAppropriateLibraryTypes,
			// extendAllAppropriateLibraryTypes);

			MaterialCargoLocationTypeData optimalLocationTypeData = null;
			if (CollectionUtils.isNotEmpty(allAppropriateLibraryTypes)) {
				optimalLocationTypeData = allAppropriateLibraryTypes.get(0);
			}
			if (null != optimalLocationTypeData) {
				material.setOptimalLocationType(optimalLocationTypeData.getCargoLocationTypeCode() + "("
						+ this.getUsageRate(optimalLocationTypeData) + ")");
				material.setOptimalPlacement(optimalLocationTypeData.getPlacement());
			} else {
				material.setOptimalLocationType("");
				material.setOptimalPlacement("");
			}

			material.setRecommendedPlacement("");
			material.setRecommendedLocationType("");

			// 推荐货位
			MaterialCargoLocationTypeData recommendedCargoLocationType = this
					.getRecommendCargoLocationTypeData(allAppropriateLibraryTypes);
			if (null != recommendedCargoLocationType) {
				material.setRecommendedLocationType(recommendedCargoLocationType.getCargoLocationTypeCode() + "("
						+ this.getUsageRate(recommendedCargoLocationType) + ")");
				material.setRecommendedPlacement(recommendedCargoLocationType.getPlacement());
			}
			// this.materialMapper.updateByPrimaryKeySelective(material);
		}
	}

	/**
	 * 计算利用率
	 * 
	 * @param data
	 * @return
	 */
	private double getUsageRate(MaterialCargoLocationTypeData data) {
		return new BigDecimal(Double.valueOf(data.getMaterialMaxStore()) / data.getStoreCount())
				.setScale(2, RoundingMode.UP).doubleValue();
	}

	/**
	 * 从合适获取中获取最优货位，取两种类型排名第一的利用率高的
	 * 
	 * @param allAppropriateLibraryTypes
	 * @param extendAllAppropriateLibraryTypes
	 * @return
	 */
	// private MaterialCargoLocationTypeData getOptimalCargoLocationTypeData(
	// List<MaterialCargoLocationTypeData> allAppropriateLibraryTypes,
	// List<MaterialCargoLocationTypeData> extendAllAppropriateLibraryTypes) {
	//
	// if (CollectionUtils.isNotEmpty(allAppropriateLibraryTypes)
	// && CollectionUtils.isNotEmpty(extendAllAppropriateLibraryTypes)) {
	// Double useRate1 = allAppropriateLibraryTypes.get(0).getUsageRate();
	// Double useRate2 = extendAllAppropriateLibraryTypes.get(0).getUsageRate();
	// if (useRate1 >= useRate2) {
	// return allAppropriateLibraryTypes.get(0);
	// } else {
	// return extendAllAppropriateLibraryTypes.get(0);
	// }
	// } else if (CollectionUtils.isNotEmpty(allAppropriateLibraryTypes)) {
	// return allAppropriateLibraryTypes.get(0);
	// } else if (CollectionUtils.isNotEmpty(extendAllAppropriateLibraryTypes))
	// {
	// return extendAllAppropriateLibraryTypes.get(0);
	// }
	// return null;
	// }

	/**
	 * 获取推荐货位类型
	 * 
	 * @return
	 */
	private MaterialCargoLocationTypeData getRecommendCargoLocationTypeData(
			List<MaterialCargoLocationTypeData> optimalLocationTypeList) {

		// 查询满足条件的货位类型,并按照利用率从大到小排序，如果利用率一样，则
		// List<MaterialCargoLocationTypeData> optimalLocationTypeList =
		// this.commonMapper
		// .selectRecommendedLocationType(wareHouseId, materialId);

		if (CollectionUtils.isNotEmpty(optimalLocationTypeList)) {

			for (MaterialCargoLocationTypeData materialCargoLocationTypeData : optimalLocationTypeList) {

				// 存储最大存放的物料需要的货位数量
				int useCount = Double
						.valueOf(Math.ceil(Double.valueOf(materialCargoLocationTypeData.getMaterialMaxStore())
								/ materialCargoLocationTypeData.getStoreCount()))
						.intValue();
				// 货位未使用的数量大于存放物料的数量
				if (materialCargoLocationTypeData.getCargoLocationTypeCanUseCount() >= useCount) {
					return materialCargoLocationTypeData;
				}
			}
		}
		return null;
	}

	/**
	 * 导出物料信息
	 *
	 * @param response
	 *            响应
	 * @param wareHouse
	 *            用户当前操作仓库
	 */
	public void exportData(HttpServletResponse response, WareHouse wareHouse, Material mater) {
		if (null == wareHouse || null == wareHouse.getId()) {
			log.info("导出物料信息失败，仓库编号为空");
			return;
		}

		// 导出Excel
		try (XSSFWorkbook wb = new XSSFWorkbook(); OutputStream os = response.getOutputStream()) {
			// 创建工作薄
			XSSFSheet sheet = wb.createSheet();

			// 创建标题列
			XSSFRow header = sheet.createRow(0);
			header.createCell(0).setCellValue("料号");
			header.createCell(1).setCellValue("长深高(m)");
			header.createCell(2).setCellValue("最大数量");
			header.createCell(3).setCellValue("合适库位类型(非扩展)");
			header.createCell(4).setCellValue("合适库位类型(扩展)");
			header.createCell(5).setCellValue("推荐货位类型");
			header.createCell(6).setCellValue("推荐摆放(长深高)");
			header.createCell(7).setCellValue("最优货位类型");
			header.createCell(8).setCellValue("推荐摆放(长深高)");

			// 查询物料信息
			MaterialExample example = new MaterialExample();
			Criteria criteria = example.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
			// 按照查询条件导出
			if (StringUtils.isNoneBlank(mater.getCode())) {
				criteria.andCodeLike("%" + mater.getCode() + "%");
			}
			if (StringUtils.isNoneBlank(mater.getOptimalLocationType())) {
				criteria.andOptimalLocationTypeLike("%" + mater.getOptimalLocationType() + "%");
			}
			if (StringUtils.isNoneBlank(mater.getRecommendedLocationType())) {
				criteria.andRecommendedLocationTypeLike("%" + mater.getRecommendedLocationType() + "%");
			}

			List<Material> materialList = this.materialMapper.selectByExample(example);

			// 填充数据列
			for (int i = 0; i < materialList.size(); i++) {
				Material material = materialList.get(i);
				if (null != material) {
					XSSFRow row = sheet.createRow(i + 1);
					row.createCell(0).setCellValue(material.getCode());
					row.createCell(1).setCellValue(StringUtils.join(
							new Double[] { material.getLength(), material.getWidth(), material.getHeight() }, "*"));
					row.createCell(2).setCellValue(material.getMaxStore());
					row.createCell(3).setCellValue(material.getAllAppropriateLibraryTypes());
					row.createCell(4).setCellValue(material.getExtendAllAppropriateLibraryTypes());
					row.createCell(5).setCellValue(material.getRecommendedLocationType());
					row.createCell(6).setCellValue(material.getRecommendedPlacement());
					row.createCell(7).setCellValue(material.getOptimalLocationType());
					row.createCell(8).setCellValue(material.getOptimalPlacement());
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
			log.error("导出物料信息失败，{}", e.getMessage());
		}
	}

	/**
	 * 导入拣货频率
	 *
	 * @param wareHouse
	 *            当前用户操作仓库
	 * @param file
	 *            excel文件
	 * @return
	 */
	@Transactional
	public MessageInfo importPickFreq(WareHouse wareHouse, MultipartFile file, double reservePercentage)
			throws Exception {
		MessageInfo messageInfo = new MessageInfo();
		messageInfo.setSuccess(false);
		if (null == file || file.isEmpty()) {
			log.info("导入拣货频率失败，文件内容为空");
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

					// 导入时间
					Date importDate = new Date();

					// 缓存当前仓库中的货位类型信息
					CargoLocationTypeExample cargoLocationTypeExample = new CargoLocationTypeExample();
					cargoLocationTypeExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
					List<CargoLocationType> cargoLocationTypeList = this.cargoLocationTypeMapper
							.selectByExample(cargoLocationTypeExample);

					Map<String, CargoLocationType> cargoLocationTypeMap = new HashMap<>();
					for (CargoLocationType cargoLocationType : cargoLocationTypeList) {
						if (!cargoLocationTypeMap.containsKey(cargoLocationType.getCode())) {
							cargoLocationTypeMap.put(cargoLocationType.getCode(), cargoLocationType);
						}
					}

					// 缓存当前仓库中的货位信息
					CargoLocationExample cargoLocationExample = new CargoLocationExample();
					cargoLocationExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
					List<CargoLocation> cargoLocationList = this.cargoLocationMapper
							.selectByExample(cargoLocationExample);

					Map<String, CargoLocation> cargoLocationMap = new HashMap<>();
					for (CargoLocation cargoLocation : cargoLocationList) {
						if (!cargoLocationMap.containsKey(cargoLocation.getCode())) {
							cargoLocationMap.put(cargoLocation.getCode(), cargoLocation);
						}
					}

					// 缓存当前仓库中的物料信息
					MaterialExample materialExample = new MaterialExample();
					materialExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
					List<Material> materialList = this.materialMapper.selectByExample(materialExample);

					Map<String, Material> materialMap = new HashMap<>();
					for (Material material : materialList) {
						if (!materialMap.containsKey(material.getCode())) {
							materialMap.put(material.getCode(), material);
						}
					}

					List<Material> updateMaterialList = new ArrayList<>();

					for (int i = 1; i < rowCount; i++) {
						// 获取行
						XSSFRow row = sheet.getRow(i);
						if (null != row) {
							// 获取物料编号
							String materialCode = formatter.formatCellValue(row.getCell(0));

							// 检测编号是否已存在
							Material material = materialMap.get(materialCode);
							if (null == material) {
								log.info("导入拣货频率失败，物料编号 {} 不存在", materialCode);
								messageInfo.setMsg("第" + (i + 1) + "行，物料编号" + materialCode + "不存在");
								return messageInfo;
							}

							// 获取货位编号
							String cargoLocationCode = formatter.formatCellValue(row.getCell(1));

							if (StringUtils.isNoneBlank(cargoLocationCode)) {
								// 检测货位编号是否已存在
								CargoLocation cargoLocation = cargoLocationMap.get(cargoLocationCode);
								if (null == cargoLocation) {
									log.info("导入拣货频率失败，货位编号 {} 不存在", cargoLocationCode);
									messageInfo.setMsg("第" + (i + 1) + "行，货位编号" + cargoLocationCode + "不存在");
									return messageInfo;
								}
								// 设置所属货位id和编码
								material.setCargoLocationId(cargoLocation.getId());
								material.setCargoLocationCode(cargoLocationCode);
								material.setCargoLocationScore(cargoLocation.getScore());
								material.setCargoLocationTypeId(cargoLocation.getTypeId());
							}

							// 获取货位类型编号
							String cargoLocationTypeCode = formatter.formatCellValue(row.getCell(2));

							// 检测货位编号是否已存在
							CargoLocationType cargoLocationType = cargoLocationTypeMap.get(cargoLocationTypeCode);
							if (null == cargoLocationType) {
								log.info("导入拣货频率失败，货位编号 {} 不存在", cargoLocationCode);
								messageInfo.setMsg("第" + (i + 1) + "行，货位编号" + cargoLocationCode + "不存在");
								return messageInfo;
							}
							// 设置货位类型,以货位编码对应的货位类型为主，防止物料对应的货位和货位类型两列不一致
							if (StringUtils.isNoneBlank(cargoLocationCode)) {
								material.setCargoLocationTypeId(cargoLocationType.getId());
							}

							// 设置拣货频率
							Double b = row.getCell(3, CREATE_NULL_AS_BLANK).getNumericCellValue();
							material.setPickUpRate(b.intValue());

							material.setCreateDate(importDate);
							material.setBatchSerialNumber(i);

							updateMaterialList.add(material);
						}
					}

					// for (Material material : updateMaterialList) {
					// this.materialMapper.updateByPrimaryKeySelective(material);
					// }

					if (CollectionUtils.isNotEmpty(updateMaterialList)) {
						this.jdbcTemplate.execute("{call sp_updateMaterialPickRate('"
								+ XmlUtil.getMaterialPickToolFreqStrByList(updateMaterialList) + "')}");
					}

					// 统计物料所使用货位数量
					List<Map<String, Integer>> useCountMap = this.commonMapper
							.selectLocationTypeUseCount(wareHouse.getId());
					for (Map<String, Integer> map : useCountMap) {
						CargoLocationType cargoLocationType = new CargoLocationType();
						cargoLocationType.setId(map.get("typeId"));
						cargoLocationType.setUseCount(map.get("count"));

						this.cargoLocationTypeMapper.updateByPrimaryKeySelective(cargoLocationType);
					}
					// 推荐货位
					this.recommendCargoLocation(reservePercentage, wareHouse);
				}
			}
			messageInfo.setSuccess(true);
			return messageInfo;
		}
	}

	/**
	 * 推荐物料货位
	 *
	 * @param reservePercentage
	 *            预留百分比
	 * @param currentWareHouse
	 *            当前仓库
	 * @return
	 */
	public MessageInfo recommendCargoLocation(final Double reservePercentage, WareHouse currentWareHouse)
			throws Exception {

		MessageInfo messageInfo = new MessageInfo();
		messageInfo.setSuccess(false);
		if (null == currentWareHouse) {
			messageInfo.setMsg("用户当前仓库为空");
			return messageInfo;
		}

		// 查询当前仓库的货位
		CargoLocationExample cargoLocationExample = new CargoLocationExample();
		cargoLocationExample.createCriteria().andWareHouseIdEqualTo(currentWareHouse.getId());
		cargoLocationExample.setOrderByClause("score desc");// 按照分值升序排序
		List<CargoLocation> cargoLocationList = this.cargoLocationMapper.selectByExample(cargoLocationExample);

		// 根据类型对货位进行分组
		final Map<Integer, List<CargoLocation>> cargoLocationGroupMap = new HashMap<>();
		for (CargoLocation cargoLocation : cargoLocationList) {
			// 将货位根据类型进行分组
			Integer groupId = cargoLocation.getTypeId(); // 货位类型编号
			List<CargoLocation> tempList; // 初始化货位集合
			if (!cargoLocationGroupMap.containsKey(groupId)) { // 如果分组不存在，则进行创建
				tempList = new ArrayList<>();
				cargoLocationGroupMap.put(groupId, tempList);
			} else {
				tempList = cargoLocationGroupMap.get(groupId);
			}
			tempList.add(cargoLocation);
		}

		// 查询当前仓库的物料
		MaterialExample materialExample = new MaterialExample();
		materialExample.setOrderByClause("pickUpRate desc");// 按照拣货频率降序
		materialExample.createCriteria().andWareHouseIdEqualTo(currentWareHouse.getId());
		List<Material> materialList = this.materialMapper.selectByExample(materialExample);
		// 初始化物料分组Map
		final Map<Integer, List<Material>> materialGroupMap = new HashMap<>();
		for (Material material : materialList) {
			Integer groupId = material.getCargoLocationTypeId(); // 货位类型编号
			if (null != groupId) {
				List<Material> tempList; // 初始化物料集合
				if (!materialGroupMap.containsKey(groupId)) { // 如果分组不存在，则进行创建
					tempList = new ArrayList<>();
					materialGroupMap.put(groupId, tempList);
				} else {
					tempList = materialGroupMap.get(groupId);
				}
				tempList.add(material);
			}
		}

		ExecutorService threadPool = Executors.newCachedThreadPool();

		try {
			CompletionService<List<Material>> cs = new ExecutorCompletionService<List<Material>>(threadPool);

			// 解析物料推荐货位
			for (Map.Entry<Integer, List<Material>> entry : materialGroupMap.entrySet()) {

				// 按照货位类型为一个单元，每个单元生成一个线程进行货位推荐运算
				final Integer key = entry.getKey();

				cs.submit(new Callable<List<Material>>() {
					public List<Material> call() throws Exception {
						return recommendCargoLocationStore(reservePercentage, cargoLocationGroupMap.get(key),
								materialGroupMap.get(key));
					}
				});
			}
			// 货位推荐结果
			List<Material> allMaterialList = new ArrayList<>();
			// 等待每个线程都执行完
			for (int i = 0; i < materialGroupMap.size(); i++) {
				List<Material> list = cs.take().get();
				if (null != list) {
					allMaterialList.addAll(list);
				}
			}

			// 更新物料推荐货位信息
			// for (Material material : allMaterialList) {
			// this.materialMapper.updateByPrimaryKeySelective(material);
			// }
			if (CollectionUtils.isNotEmpty(allMaterialList)) {
				this.jdbcTemplate.execute("{call sp_updateMaterialRecommendLocation('"
						+ XmlUtil.getMaterialRecommendCargoLocationStrByList(allMaterialList) + "')}");
			}
		} finally {
			threadPool.shutdown();
		}
		messageInfo.setSuccess(true);
		return messageInfo;
	}

	private List<Material> recommendCargoLocationStore(double reservePercentage, List<CargoLocation> cargoLocationList,
			List<Material> tempList) {

		// 实际预留值，放在F1 上面的物料总数量/F1 类型的货位总数量
		Double materialCount = (double) tempList.size(); // 获取货位类型下物料总数量

		// 没有对应货位类型的货位则不能推荐
		if (CollectionUtils.isEmpty(cargoLocationList)) {
			return null;
		}
		Double cargoLocationCount = (double) cargoLocationList.size(); // 获取货位类型下的货位数量

		// 如果物料的数量大于货位的数量，或者货位的数量少于5个，或者用户选择不预留，则不用考虑预留值
		if (materialCount >= cargoLocationCount || cargoLocationCount < 5 || reservePercentage == 0) {
			// 设置物料推荐货位
			for (int i = 0; i < cargoLocationList.size(); i++) {
				CargoLocation cargoLocation = cargoLocationList.get(i);

				// 如果货位数量大于物料数量，则终止循环
				if (i >= tempList.size()) {
					break;
				}

				// 获取物料信息
				Material material = tempList.get(i);
				if (null != material) {
					material.setRecommendedLocationId(cargoLocation.getId());
					material.setRecommendedLocationCode(cargoLocation.getCode());
					material.setRecommendedLocationScore(cargoLocation.getScore());
				}
			}
		} else {
			// 计算实际预留值
			double result = 1 - (materialCount / cargoLocationCount);

			// 用户按照%输入的，转化成小数
			reservePercentage = reservePercentage / 100;

			// 如果用户输入的值大于实际预留值，则以实际预留值计算，否则以用户填写的为准
			double tempReservePercentage = reservePercentage > result ? result : reservePercentage;

			// 按照货位预留设置物料推荐货位
			recommendCargoLocationStoreByReserve(tempReservePercentage, cargoLocationList, tempList);
		}
		return tempList;
	}

	private void recommendCargoLocationStoreByReserve(double reservePercentage, List<CargoLocation> cargoLocationList,
			List<Material> materialList) {
		// 将货位列表分为5个
		if (reservePercentage > 0) {
			// 如果货位数量大于或者等于5
			if (cargoLocationList.size() >= 5) {
				// 获取每组数据大小
				int groupSize = cargoLocationList.size() / 5;

				// 计算每组数据量
				Map<Integer, Integer> dataMap = new HashMap<>();
				for (int i = 0; i < 5; i++) {
					dataMap.put(i, groupSize);
				}

				// 计算剩余货位
				int count = cargoLocationList.size() - 5 * groupSize;
				for (int i = 0; i < count; i++) {
					dataMap.put(i, dataMap.get(i) + 1);
				}

				// 按指定数量填充数组
				// 初始化分组list
				CopyOnWriteArrayList<CopyOnWriteArrayList<CargoLocation>> cargoLocationGroupList = new CopyOnWriteArrayList<>();
				int startIndex = 0;
				for (int i = 0; i < 5; i++) {
					// 获取指定索引的数组长度
					int endIndex = startIndex + dataMap.get(i);
					cargoLocationGroupList
							.add(new CopyOnWriteArrayList<>(cargoLocationList.subList(startIndex, endIndex)));
					startIndex = endIndex;
				}

				// 初始化所有可用货位列表
				List<CargoLocation> allCargoLocationList = new ArrayList<>();

				// 初始化预留货位
				Map<Integer, CopyOnWriteArrayList<CargoLocation>> reserveMap = new HashMap<>();

				// 根据预留百分比，清理存放数量
				for (int i = 0; i < 5; i++) {
					// 计算保留的个数
					int result = Double.valueOf(Math.ceil(dataMap.get(i) * reservePercentage)).intValue();

					// 获取第i组的List
					List<CargoLocation> tempList = cargoLocationGroupList.get(i);

					// 设置每组保留的货位
					CopyOnWriteArrayList<CargoLocation> reserveList = new CopyOnWriteArrayList<>(
							tempList.subList(tempList.size() - result, tempList.size()));
					reserveMap.put(i, reserveList);

					// 根据保留的个数，从集合中移除元素
					tempList.removeAll(reserveList);

					// 添加不含预留的货位到可用货位列表
					allCargoLocationList.addAll(tempList);
				}

				// 开始分配货位
				for (int i = 0; i < materialList.size(); i++) {
					Material material = materialList.get(i);
					CargoLocation cargoLocation;
					// 判断是否有可用货位
					if (i < allCargoLocationList.size()) {
						cargoLocation = allCargoLocationList.get(i);

					} else {
						// 从可用货位列表中取出倒数materialList.size() -i个,
						// 对应的索引为5-(materialList.size() -i),
						// 又因为key是从1到5，而不是0到4，所以再加1
						cargoLocation = reserveMap.get(5 - (materialList.size() - i) + 1).get(0);
					}

					if (null != cargoLocation) {
						// 设置物料推荐货位主键
						material.setRecommendedLocationId(cargoLocation.getId());
						// 设置物料推荐货位编码
						material.setRecommendedLocationCode(cargoLocation.getCode());
						// 设置物料推荐货位分数
						material.setRecommendedLocationScore(cargoLocation.getScore());
					}
				}
			}
		}
	}
}
