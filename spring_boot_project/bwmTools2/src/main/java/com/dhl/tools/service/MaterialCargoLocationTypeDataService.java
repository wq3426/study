package com.dhl.tools.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dhl.tools.domain.CargoLocationType;
import com.dhl.tools.domain.Material;
import com.dhl.tools.domain.MaterialCargoLocationTypeData;
import com.dhl.tools.domain.WareHouse;

/**
 * 主要用来处理计算物料适合的货位类型和摆放方式 Created by liuso on 2017/4/19.
 */
@Service
@Transactional
public class MaterialCargoLocationTypeDataService {
	//
	// private static final int INSERT_SIZE = 100;
	//
	// @Autowired
	// private MaterialCargoLocationTypeDataMapper
	// materialCargoLocationTypeDataMapper;
	//
	// @Autowired
	// private CargoLocationTypeMapper cargoLocationTypeMapper;
	//
	// @Autowired
	// private CommonMapper commonMapper;

	/**
	 * 计算物料所有合适货位
	 *
	 * @param material
	 *            物料
	 * @return
	 */
	@Transactional
	public List<MaterialCargoLocationTypeData> calculateTheOptimalPlacement(
			List<CargoLocationType> cargoLocationTypeList, WareHouse wareHouse, Material material) {

		// 清理数据
		// MaterialCargoLocationTypeDataExample example = new
		// MaterialCargoLocationTypeDataExample();
		// example.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
		// this.materialCargoLocationTypeDataMapper.deleteByExample(example);

		List<MaterialCargoLocationTypeData> list = new ArrayList<MaterialCargoLocationTypeData>();

		// 交换长宽高
		list.addAll(calculate(cargoLocationTypeList, wareHouse, material, material.getLength(), material.getWidth(),
				material.getHeight()));
		list.addAll(calculate(cargoLocationTypeList, wareHouse, material, material.getWidth(), material.getLength(),
				material.getHeight()));

		// 非正面朝上
		if (null == material.getFaceUp() || !material.getFaceUp()) {
			list.addAll(calculate(cargoLocationTypeList, wareHouse, material, material.getLength(),
					material.getHeight(), material.getWidth()));
			list.addAll(calculate(cargoLocationTypeList, wareHouse, material, material.getWidth(), material.getHeight(),
					material.getLength()));
			list.addAll(calculate(cargoLocationTypeList, wareHouse, material, material.getHeight(), material.getWidth(),
					material.getLength()));
			list.addAll(calculate(cargoLocationTypeList, wareHouse, material, material.getHeight(),
					material.getLength(), material.getWidth()));
		}

		// 扩展类型交换长宽高
		list.addAll(calculateExtend(cargoLocationTypeList, wareHouse, material, material.getLength(),
				material.getWidth(), material.getHeight()));
		list.addAll(calculateExtend(cargoLocationTypeList, wareHouse, material, material.getWidth(),
				material.getLength(), material.getHeight()));

		// 非正面朝上
		if (null == material.getFaceUp() || !material.getFaceUp()) {
			list.addAll(calculateExtend(cargoLocationTypeList, wareHouse, material, material.getLength(),
					material.getHeight(), material.getWidth()));
			list.addAll(calculateExtend(cargoLocationTypeList, wareHouse, material, material.getWidth(),
					material.getHeight(), material.getLength()));
			list.addAll(calculateExtend(cargoLocationTypeList, wareHouse, material, material.getHeight(),
					material.getWidth(), material.getLength()));
			list.addAll(calculateExtend(cargoLocationTypeList, wareHouse, material, material.getHeight(),
					material.getLength(), material.getWidth()));
		}

		if (CollectionUtils.isNotEmpty(list)) {

			// 计算出每种货位类型存放最多物料的摆放位置
			Map<String, MaterialCargoLocationTypeData> map = new HashMap<String, MaterialCargoLocationTypeData>();
			for (MaterialCargoLocationTypeData data : list) {
				String code = data.getCargoLocationTypeCode();
				if (map.containsKey(code)) {
					MaterialCargoLocationTypeData old = map.get(code);
					if (old.getStoreCount() < data.getStoreCount()) {
						map.put(code, data);
					}
				} else {
					map.put(code, data);
				}
			}

			list = new ArrayList<MaterialCargoLocationTypeData>(map.values());

			Collections.sort(list, new Comparator<MaterialCargoLocationTypeData>() {
				@Override
				public int compare(MaterialCargoLocationTypeData o1, MaterialCargoLocationTypeData o2) {

					// 按照利用率降序，如果利用率相同则体积升序
					if (o1.getUsageRate() > o2.getUsageRate()) {
						return -1;
					} else if (o1.getUsageRate() < o2.getUsageRate()) {
						return 1;
					} else {
						if (o1.getCargoLocationTypeVolume() < o2.getCargoLocationTypeVolume()) {
							return -1;
						} else if (o1.getCargoLocationTypeVolume() > o2.getCargoLocationTypeVolume()) {
							return 1;
						} else {
							return 0;
						}
					}
				}
			});

			// 分配批量插入数据库，数据库对长度有限制
			// for (int i = 0, size = list.size(), len = size / INSERT_SIZE; i
			// <= len; i++) {
			// int end = (i == len ? size : (i + 1) * INSERT_SIZE);
			// this.commonMapper.batchInsertTypeData(list.subList(i *
			// INSERT_SIZE, end));
			// }
		}
		return list;
	}

	/**
	 * 计算符合条件的货位类型的摆放方式，摆放数量和使用率
	 *
	 * @param length
	 *            长度
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param material
	 *            结果集合
	 */
	public List<MaterialCargoLocationTypeData> calculate(List<CargoLocationType> cargoLocationTypeList,
			WareHouse wareHouse, Material material, Double length, Double width, Double height) {

		List<MaterialCargoLocationTypeData> typeDataList = new ArrayList<MaterialCargoLocationTypeData>();

		// // 查询长、宽、高要求的货位类型
		// CargoLocationTypeExample cargoLocationTypeExample = new
		// CargoLocationTypeExample();
		//
		// // 货位类型长宽高符合要求
		// cargoLocationTypeExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId()).andExtendEqualTo(false)
		// .andLengthGreaterThanOrEqualTo(length).andWidthGreaterThanOrEqualTo(width)
		// .andHeightGreaterThanOrEqualTo(height).andCodeNotEqualTo(wareHouse.getStallType());
		//
		// List<CargoLocationType> cargoLocationTypes =
		// this.cargoLocationTypeMapper
		// .selectByExample(cargoLocationTypeExample);

		List<CargoLocationType> cargoLocationTypes = this.getAppropriateLocationTypeList(cargoLocationTypeList, length,
				width, height);

		if (null != cargoLocationTypes && cargoLocationTypes.size() > 0) {

			for (CargoLocationType cargoLocationType : cargoLocationTypes) {

				// 如果是非托盘类型，物料的宽度加上臂长必须大于货位的宽度，否则人够不着
				if (!cargoLocationType.getPallet()) {
					if ((width + MaterialService.ARM_LENGTH) < cargoLocationType.getWidth()) {
						continue;
					}
				}

				// 获取货位长度相对物料长度有效倍数
				int lengthMultiple = Double.valueOf(cargoLocationType.getLength() / length).intValue();
				int widthMultiple = Double.valueOf(cargoLocationType.getWidth() / width).intValue();
				int heightMultiple = Double.valueOf(cargoLocationType.getHeight() / height).intValue();

				// 计算可存放物料数量
				int count = lengthMultiple * widthMultiple * heightMultiple;

				// 如果是非托盘，一个货位必须能放下最大存放数量的物料
				if (!cargoLocationType.getPallet() && count < material.getMaxStore()) {
					continue;
				}

				// 初始化物料货位类型数据
				MaterialCargoLocationTypeData materialCargoLocationTypeData = new MaterialCargoLocationTypeData();

				// 设置仓库信息
				materialCargoLocationTypeData.setWareHouseId(wareHouse.getId());
				materialCargoLocationTypeData.setWareHouseCode(wareHouse.getCode());

				// 设置货位类型
				materialCargoLocationTypeData.setCargoLocationTypeId(cargoLocationType.getId());
				materialCargoLocationTypeData.setCargoLocationTypeCode(cargoLocationType.getCode());
				materialCargoLocationTypeData.setCargoLocationTypeLength(cargoLocationType.getLength());
				materialCargoLocationTypeData.setCargoLocationTypeWidth(cargoLocationType.getWidth());
				materialCargoLocationTypeData.setCargoLocationTypeHeight(cargoLocationType.getHeight());
				materialCargoLocationTypeData.setCargoLocationTypeExtend(cargoLocationType.getExtend());
				materialCargoLocationTypeData.setCargoLocationTypePallet(cargoLocationType.getPallet());
				Integer total = cargoLocationType.getTotal();
				Integer usage = cargoLocationType.getUseCount();
				materialCargoLocationTypeData
						.setCargoLocationTypeCanUseCount((total == null ? 0 : total) - (usage == null ? 0 : usage));

				// 设置物料信息
				materialCargoLocationTypeData.setMaterialId(material.getId());
				materialCargoLocationTypeData.setMaterialCode(material.getCode());
				materialCargoLocationTypeData.setMaterialMaxStore(material.getMaxStore());
				materialCargoLocationTypeData.setMaterialLength(length);
				materialCargoLocationTypeData.setMaterialWidth(width);
				materialCargoLocationTypeData.setMaterialHeight(height);
				materialCargoLocationTypeData.setPlacement(
						StringUtils.join(new Integer[] { lengthMultiple, widthMultiple, heightMultiple }, "*"));

				// 一个货位类型可存放物料数量
				materialCargoLocationTypeData.setStoreCount(count);

				// 设置货位类型的体积
				double volume = cargoLocationType.getLength() * cargoLocationType.getWidth()
						* cargoLocationType.getHeight();
				materialCargoLocationTypeData
						.setCargoLocationTypeVolume(new BigDecimal(volume).setScale(5, RoundingMode.UP).doubleValue());

				// 利用率=物料的数量/该货位类型能放下的数量
				double usageRate = material.getMaxStore() / Double.valueOf(count);
				materialCargoLocationTypeData
						.setUsageRate(new BigDecimal(usageRate).setScale(5, RoundingMode.UP).doubleValue());

				// 添加数据
				typeDataList.add(materialCargoLocationTypeData);
			}
		}
		return typeDataList;
	}

	/**
	 * 计算符合条件的扩展货位类型的摆放方式，摆放数量和使用率
	 *
	 * @param length
	 *            长度
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param material
	 *            结果集合
	 */
	public List<MaterialCargoLocationTypeData> calculateExtend(List<CargoLocationType> cargoLocationTypeList,
			WareHouse wareHouse, Material material, Double length, Double width, Double height) {

		List<MaterialCargoLocationTypeData> typeDataList = new ArrayList<MaterialCargoLocationTypeData>();

		// 查询长、宽、高要求的货位类型
		// CargoLocationTypeExample cargoLocationTypeExample = new
		// CargoLocationTypeExample();
		// // 货位类型长宽高符合要求
		// cargoLocationTypeExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId()).andExtendEqualTo(true)
		// .andIncreaseLengthGreaterThanOrEqualTo(length).andIncreaseWidthGreaterThanOrEqualTo(width)
		// .andHeightGreaterThanOrEqualTo(height).andCodeNotEqualTo(wareHouse.getStallType());
		//
		// List<CargoLocationType> cargoLocationTypes =
		// this.cargoLocationTypeMapper
		// .selectByExample(cargoLocationTypeExample);

		List<CargoLocationType> cargoLocationTypes = this.getAppropriateExtendLocationTypeList(cargoLocationTypeList,
				height, width, height);

		if (null != cargoLocationTypes && cargoLocationTypes.size() > 0) {
			for (CargoLocationType cargoLocationType : cargoLocationTypes) {

				// 获取货位长度相对物料长度有效倍数
				int lengthMultiple = 0, widthMultiple = 0, heightMultiple = 0;

				// 1、左右前后都扩展
				if (cargoLocationType.getIncreaseLength().doubleValue() != cargoLocationType.getLength().doubleValue()
						&& cargoLocationType.getIncreaseWidth().doubleValue() != cargoLocationType.getWidth()
								.doubleValue()) {
					// 物料的长度在货位类型扩展和非扩展长度之间，并且物料的深度在货位类型扩展和非扩展宽度之间
					if (length > cargoLocationType.getLength() && length < cargoLocationType.getIncreaseLength()
							&& width > cargoLocationType.getWidth() && width < cargoLocationType.getIncreaseWidth()) {
						lengthMultiple = 1;
						widthMultiple = 1;
						heightMultiple = Double.valueOf(cargoLocationType.getHeight() / height).intValue();
					}
				}
				// 2、左右扩展
				else if (cargoLocationType.getIncreaseLength().doubleValue() != cargoLocationType.getLength()
						.doubleValue()) {
					if ((width + MaterialService.ARM_LENGTH) > cargoLocationType.getWidth()
							&& length > cargoLocationType.getLength()
							&& length < cargoLocationType.getIncreaseLength()) {
						lengthMultiple = 1;
						widthMultiple = Double.valueOf(cargoLocationType.getWidth() / width).intValue();
						heightMultiple = Double.valueOf(cargoLocationType.getHeight() / height).intValue();
					}
				}
				// 3、前后扩展
				else if (cargoLocationType.getIncreaseWidth().doubleValue() != cargoLocationType.getWidth()
						.doubleValue()) {
					if (width > cargoLocationType.getWidth() && width < cargoLocationType.getIncreaseWidth()) {
						lengthMultiple = Double.valueOf(cargoLocationType.getLength() / length).intValue();
						widthMultiple = 1;
						heightMultiple = Double.valueOf(cargoLocationType.getHeight() / height).intValue();
					}
				} else {
					continue;
				}

				// 计算可存放物料数量
				int count = lengthMultiple * widthMultiple * heightMultiple;

				if (0 == count) {
					continue;
				}

				// 扩展类型一个货位必须能放下最大存放数量的物料
				if (count < material.getMaxStore().intValue()) {
					continue;
				}

				// 初始化物料货位类型数据
				MaterialCargoLocationTypeData materialCargoLocationTypeData = new MaterialCargoLocationTypeData();

				// 设置仓库信息
				materialCargoLocationTypeData.setWareHouseId(wareHouse.getId());
				materialCargoLocationTypeData.setWareHouseCode(wareHouse.getCode());

				// 设置货位类型
				materialCargoLocationTypeData.setCargoLocationTypeId(cargoLocationType.getId());
				materialCargoLocationTypeData.setCargoLocationTypeCode(cargoLocationType.getCode());
				materialCargoLocationTypeData.setCargoLocationTypeLength(cargoLocationType.getIncreaseLength());
				materialCargoLocationTypeData.setCargoLocationTypeWidth(cargoLocationType.getIncreaseWidth());

				materialCargoLocationTypeData.setCargoLocationTypeHeight(cargoLocationType.getHeight());
				materialCargoLocationTypeData.setCargoLocationTypeExtend(cargoLocationType.getExtend());
				materialCargoLocationTypeData.setCargoLocationTypePallet(cargoLocationType.getPallet());
				Integer total = cargoLocationType.getTotal();
				Integer usage = cargoLocationType.getUseCount();
				materialCargoLocationTypeData
						.setCargoLocationTypeCanUseCount((total == null ? 0 : total) - (usage == null ? 0 : usage));

				// 设置物料信息
				materialCargoLocationTypeData.setMaterialId(material.getId());
				materialCargoLocationTypeData.setMaterialCode(material.getCode());
				materialCargoLocationTypeData.setMaterialMaxStore(material.getMaxStore());
				materialCargoLocationTypeData.setMaterialLength(length);
				materialCargoLocationTypeData.setMaterialWidth(width);
				materialCargoLocationTypeData.setMaterialHeight(height);
				materialCargoLocationTypeData.setPlacement(
						StringUtils.join(new Integer[] { lengthMultiple, widthMultiple, heightMultiple }, "*"));

				// 一个货位类型可存放物料数量
				materialCargoLocationTypeData.setStoreCount(count);

				// 设置货位类型的体积
				double volume = cargoLocationType.getIncreaseLength() * cargoLocationType.getIncreaseWidth()
						* cargoLocationType.getHeight();
				materialCargoLocationTypeData
						.setCargoLocationTypeVolume(new BigDecimal(volume).setScale(5, RoundingMode.UP).doubleValue());

				// 设置使用率
				double usageRate = material.getMaxStore() / Double.valueOf(count);
				materialCargoLocationTypeData
						.setUsageRate(new BigDecimal(usageRate).setScale(5, RoundingMode.UP).doubleValue());

				// 添加数据
				typeDataList.add(materialCargoLocationTypeData);
			}
		}
		return typeDataList;
	}

	// 获取所有非扩展合适的货位类型
	private List<CargoLocationType> getAppropriateLocationTypeList(List<CargoLocationType> cargoLocationTypes,
			double length, double width, double height) {
		List<CargoLocationType> cargoLocationTypeList = new ArrayList<CargoLocationType>();
		if (CollectionUtils.isNotEmpty(cargoLocationTypes)) {
			for (CargoLocationType cargoLocationType : cargoLocationTypes) {
				if (!cargoLocationType.getExtend() && cargoLocationType.getLength().doubleValue() >= length
						&& cargoLocationType.getWidth().doubleValue() >= width
						&& cargoLocationType.getHeight().doubleValue() >= height) {
					cargoLocationTypeList.add(cargoLocationType);
				}
			}
		}
		return cargoLocationTypeList;
	}

	// 获取所有扩展合适的货位类型
	private List<CargoLocationType> getAppropriateExtendLocationTypeList(List<CargoLocationType> cargoLocationTypes,
			double length, double width, double height) {

		List<CargoLocationType> cargoLocationTypeList = new ArrayList<CargoLocationType>();
		if (CollectionUtils.isNotEmpty(cargoLocationTypes)) {
			for (CargoLocationType cargoLocationType : cargoLocationTypes) {
				if (cargoLocationType.getExtend() && cargoLocationType.getIncreaseLength().doubleValue() >= length
						&& cargoLocationType.getIncreaseWidth().doubleValue() >= width
						&& cargoLocationType.getHeight().doubleValue() >= height) {
					cargoLocationTypeList.add(cargoLocationType);
				}
			}
		}
		return cargoLocationTypeList;
	}
}
