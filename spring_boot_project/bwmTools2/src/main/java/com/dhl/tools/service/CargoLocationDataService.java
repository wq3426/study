package com.dhl.tools.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dhl.tools.domain.CargoLocation;
import com.dhl.tools.domain.CargoLocationData;
import com.dhl.tools.domain.CargoLocationDataExample;
import com.dhl.tools.domain.CargoLocationDataExample.Criteria;
import com.dhl.tools.domain.CargoLocationExample;
import com.dhl.tools.domain.CargoLocationType;
import com.dhl.tools.domain.CargoLocationTypeExample;
import com.dhl.tools.domain.MessageInfo;
import com.dhl.tools.domain.ParamConfigForm;
import com.dhl.tools.domain.WareHouse;
import com.dhl.tools.domain.WareHouseConfig;
import com.dhl.tools.domain.WareHouseConfigExample;
import com.dhl.tools.mapper.CargoLocationDataMapper;
import com.dhl.tools.mapper.CargoLocationMapper;
import com.dhl.tools.mapper.CargoLocationTypeMapper;
import com.dhl.tools.mapper.CommonMapper;
import com.dhl.tools.mapper.WareHouseConfigMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * 编号解析 Created by liuso on 2017/5/4.
 */
@Slf4j
@Service
public class CargoLocationDataService {

	@Autowired
	private WareHouseConfigMapper wareHouseConfigMapper;

	@Autowired
	private CargoLocationDataMapper cargoLocationDataMapper;

	@Autowired
	private CargoLocationMapper cargoLocationMapper;

	@Autowired
	private CargoLocationTypeMapper cargoLocationTypeMapper;

	@Autowired
	private CommonMapper commonMapper;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * 解析货位编号
	 *
	 * @param wareHouse
	 *            仓库
	 * @param cargoLocationList
	 *            货位列表
	 */
	/*
	 * @Transactional void process(final WareHouse wareHouse,
	 * List<CargoLocation> cargoLocationList) { // 检测仓库是否为空 if (null ==
	 * wareHouse) { log.info("仓库为空"); return; }
	 * 
	 * if (null == wareHouse.getId()) { log.info("仓库主键为空"); return; }
	 * 
	 * // 初始化查询条件 WareHouseConfigExample wareHouseConfigExample = new
	 * WareHouseConfigExample(); // 查询仓库主键为wareHouseId的仓库配置信息
	 * wareHouseConfigExample.createCriteria().andWareHouseIdEqualTo(wareHouse.
	 * getId()); // 根据排行正序排列 wareHouseConfigExample.setOrderByClause("rank asc"
	 * );
	 * 
	 * // 获取仓库配置信息 List<WareHouseConfig> wareHouseConfigList =
	 * this.wareHouseConfigMapper.selectByExample(wareHouseConfigExample);
	 * 
	 * // 如果集合为空，不进行处理 if (CollectionUtils.isEmpty(wareHouseConfigList)) {
	 * log.info("仓库未进行配置，主键：[]", wareHouse.getId()); return; }
	 * 
	 * // 缓存货位计算基础数据 CargoLocationDataExample cargoLocationDataExample = new
	 * CargoLocationDataExample();
	 * cargoLocationDataExample.createCriteria().andWareHouseIdEqualTo(wareHouse
	 * .getId()); List<CargoLocationData> cargoLocationDataList =
	 * this.cargoLocationDataMapper .selectByExample(cargoLocationDataExample);
	 * Map<String, CargoLocationData> cargoLocationDataMap = new HashMap<>();
	 * for (CargoLocationData cargoLocationData : cargoLocationDataList) {
	 * String k = cargoLocationData.getCacheCode(); if
	 * (!cargoLocationDataMap.containsKey(k)) { cargoLocationDataMap.put(k,
	 * cargoLocationData); } }
	 * 
	 * // 初始化待添加数据列表 List<CargoLocationData> insertList = new ArrayList<>();
	 * 
	 * // 构建待添加数据 for (CargoLocation item : cargoLocationList) { String
	 * cargoLocationCode = item.getCode();
	 * 
	 * // 检测货位编号是否为空 if (StringUtils.isEmpty(cargoLocationCode)) {
	 * log.info("货位编号为空"); return; }
	 * 
	 * // 初始化父主键，当配置类型发生变化时，这个值将被重新初始化为NULL String parentId = null; // 初始化开始索引
	 * int startIndex = 0;
	 * 
	 * // 处理货位类型数据，当第一次循环时 for (WareHouseConfig wareHouseConfig :
	 * wareHouseConfigList) {
	 * 
	 * // 设置结束索引 int endIndex = startIndex + wareHouseConfig.getLength();
	 * 
	 * // 获取编码 String cacheCode = cargoLocationCode.substring(0, endIndex);
	 * 
	 * CargoLocationData cargoLocationData = null;
	 * 
	 * if (!cargoLocationDataMap.containsKey(cacheCode)) {
	 * 
	 * cargoLocationData = new CargoLocationData(); // 设置主键
	 * cargoLocationData.setId(UUID.randomUUID().toString());
	 * 
	 * // 设置父主键 cargoLocationData.setParentId(parentId); // 设置编码
	 * cargoLocationData.setCode(cargoLocationCode.substring(startIndex,
	 * endIndex)); // 设置缓存编码 cargoLocationData.setCacheCode(cacheCode); //
	 * 设置配置类型主键 cargoLocationData.setConfigId(wareHouseConfig.getId()); //
	 * 设置配置类型
	 * cargoLocationData.setConfigType(wareHouseConfig.getWareHouseTypeId()); //
	 * 设置仓库主键 cargoLocationData.setWareHouseId(wareHouse.getId());
	 * 
	 * cargoLocationDataMap.put(cacheCode, cargoLocationData);
	 * 
	 * insertList.add(cargoLocationData); } else { cargoLocationData =
	 * cargoLocationDataMap.get(cacheCode); } // 设置父主键 parentId =
	 * cargoLocationData.getId();
	 * 
	 * // 开始索引后移 startIndex = endIndex; } }
	 * 
	 * // 添加数据 for (CargoLocationData cargoLocationData : insertList) {
	 * this.cargoLocationDataMapper.insertSelective(cargoLocationData); } }
	 */

	/**
	 * 多线程拆分货位编码
	 * 
	 * @param wareHouse
	 * @param cargoLocationList
	 * @return
	 * @throws Exception
	 */
	public FutureTask<List<CargoLocationData>> processCargoLocationCode(final WareHouse wareHouse,
			final List<CargoLocation> cargoLocationList) throws Exception {

		// 缓存货位计算基础数据
		CargoLocationDataExample cargoLocationDataExample = new CargoLocationDataExample();
		cargoLocationDataExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
		List<CargoLocationData> cargoLocationDataList = this.cargoLocationDataMapper
				.selectByExample(cargoLocationDataExample);
		final Set<String> cacheCodeSet = new HashSet<>();
		for (CargoLocationData cargoLocationData : cargoLocationDataList) {
			cacheCodeSet.add(cargoLocationData.getCacheCode());
		}

		// 初始化查询条件
		WareHouseConfigExample wareHouseConfigExample = new WareHouseConfigExample();
		// 查询仓库主键为wareHouseId的仓库配置信息
		wareHouseConfigExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
		// 根据排行正序排列
		wareHouseConfigExample.setOrderByClause("rank asc");

		// 获取仓库配置信息
		final List<WareHouseConfig> wareHouseConfigList = this.wareHouseConfigMapper
				.selectByExample(wareHouseConfigExample);

		FutureTask<List<CargoLocationData>> future = new FutureTask<>(new Callable<List<CargoLocationData>>() {
			@Override
			public List<CargoLocationData> call() throws Exception {
				long start = System.currentTimeMillis();
				// 去掉数据库中已经存在
				List<CargoLocationData> cargoLocationDatas = splitCargoLocationCode(wareHouse, cargoLocationList,
						wareHouseConfigList);
				if (CollectionUtils.isNotEmpty(cargoLocationDatas)) {
					for (Iterator<CargoLocationData> iterator = cargoLocationDatas.iterator(); iterator.hasNext();) {
						if (cacheCodeSet.contains(iterator.next().getCacheCode())) {
							iterator.remove();
						}
					}
				}
				log.debug("拆分货位数据耗时{}毫秒", System.currentTimeMillis() - start);
				return cargoLocationDatas;
			}
		});
		new Thread(future).start();
		return future;
	}

	private List<CargoLocationData> splitCargoLocationCode(WareHouse wareHouse, List<CargoLocation> cargoLocationList,
			final List<WareHouseConfig> wareHouseConfigList) throws Exception {

		List<CargoLocationData> cargoLocationDataList = new ArrayList<>();

		// 如果集合为空，不进行处理
		if (CollectionUtils.isEmpty(wareHouseConfigList)) {
			log.info("仓库未进行配置，主键：[]", wareHouse.getId());
			return null;
		}

		int fristTypeLength = wareHouseConfigList.get(0).getLength();

		// 按照第一级分组
		final Map<String, List<CargoLocation>> cacheMap = new HashMap<>();
		for (CargoLocation cargoLocation : cargoLocationList) {
			String fristCode = cargoLocation.getCode().substring(0, fristTypeLength);
			List<CargoLocation> cargoLocations = null;
			if (cacheMap.containsKey(fristCode)) {
				cargoLocations = cacheMap.get(fristCode);
			} else {
				cargoLocations = new ArrayList<>();
				cacheMap.put(fristCode, cargoLocations);
			}
			cargoLocations.add(cargoLocation);
		}

		ExecutorService threadPool = Executors.newCachedThreadPool();
		try {

			CompletionService<List<CargoLocationData>> cs = new ExecutorCompletionService<List<CargoLocationData>>(
					threadPool);

			for (final String key : cacheMap.keySet()) {
				cs.submit(new Callable<List<CargoLocationData>>() {
					public List<CargoLocationData> call() throws Exception {
						return splitCargoLocationCode(wareHouseConfigList, cacheMap.get(key));
					}
				});
			}
			// 等待每个线程都执行完
			for (int i = 0; i < cacheMap.size(); i++) {
				cargoLocationDataList.addAll(cs.take().get());
			}
		} finally {
			threadPool.shutdown();
		}
		return cargoLocationDataList;
	}

	private List<CargoLocationData> splitCargoLocationCode(List<WareHouseConfig> wareHouseConfigList,
			List<CargoLocation> cargoLocations) {

		List<CargoLocationData> cargoLocationDataList = new ArrayList<>();

		Map<String, CargoLocationData> cargoLocationDataMap = new HashMap<>();

		for (CargoLocation cargoLocation : cargoLocations) {

			String cargoLocationCode = cargoLocation.getCode();

			// 初始化父主键，当配置类型发生变化时，这个值将被重新初始化为NULL
			String parentId = null;
			// 初始化开始索引
			int startIndex = 0;

			// 处理货位类型数据，当第一次循环时
			for (WareHouseConfig wareHouseConfig : wareHouseConfigList) {

				// 设置结束索引
				int endIndex = startIndex + wareHouseConfig.getLength();

				// 获取编码
				String cacheCode = cargoLocationCode.substring(0, endIndex);

				CargoLocationData cargoLocationData = null;

				if (!cargoLocationDataMap.containsKey(cacheCode)) {

					cargoLocationData = new CargoLocationData();
					// 设置主键
					cargoLocationData.setId(UUID.randomUUID().toString());

					// 设置父主键
					cargoLocationData.setParentId(parentId);
					// 设置编码
					cargoLocationData.setCode(cargoLocationCode.substring(startIndex, endIndex));
					// 设置缓存编码
					cargoLocationData.setCacheCode(cacheCode);
					// 设置配置类型主键
					cargoLocationData.setConfigId(wareHouseConfig.getId());
					// 设置配置类型
					cargoLocationData.setConfigType(wareHouseConfig.getWareHouseTypeId());
					// 设置仓库主键
					cargoLocationData.setWareHouseId(wareHouseConfig.getWareHouseId());

					cargoLocationDataMap.put(cacheCode, cargoLocationData);

					cargoLocationDataList.add(cargoLocationData);
				} else {
					cargoLocationData = cargoLocationDataMap.get(cacheCode);
				}
				// 设置父主键
				parentId = cargoLocationData.getId();
				// 开始索引后移
				startIndex = endIndex;
			}
		}
		return cargoLocationDataList;
	}

	/**
	 * 批量更新货位配置数据
	 *
	 */
	@Transactional
	public void update(ParamConfigForm form) {
		if (null != form.getId() && form.getId().length > 0) {
			Integer configTypeId = form.getConfigTypeId()[0];
			for (int i = 0, size = form.getId().length; i < size; i++) {
				CargoLocationData cargoLocationData = this.cargoLocationDataMapper.selectByPrimaryKey(form.getId()[i]);
				cargoLocationData.setPrimaryPriority(form.getChildPrimaryPriority()[i]);
				cargoLocationData.setStandbyPriority(form.getChildStandbyPriority()[i]);
				// 区域和通道更新距离
				if (1 == configTypeId || 2 == configTypeId) {
					cargoLocationData.setDistance(form.getChildDistance()[i]);
				} // 层级更新拣货工具
				else if (4 == configTypeId) {
					cargoLocationData.setPickTool(form.getChildPickTool()[i]);
				}
				this.cargoLocationDataMapper.updateByPrimaryKey(cargoLocationData);
			}
		}
	}

	/**
	 * 设置参数配置
	 * 
	 * @param wareHouse
	 *            当前仓库
	 * @param parentId
	 *            父节点ID
	 * @param form
	 *            节点
	 */
	@Transactional
	public void setParam(WareHouse wareHouse, String parentId, ParamConfigForm form) {

		List<CargoLocationData> resultList = new ArrayList<>();

		// 选中的节点
		Integer[] childChecked = form.getChildChecked();

		if (null != childChecked && childChecked.length > 0) {

			// 父节点缓存code
			String rootCacheCode = "";
			if (StringUtils.isNoneBlank(parentId)) {
				rootCacheCode = this.cargoLocationDataMapper.selectByPrimaryKey(parentId).getCacheCode();
			}

			Map<Integer, Integer> map = this.getConfigLengthMap(wareHouse.getId());

			for (Integer index : childChecked) {

				Integer configTypeId = form.getConfigTypeId()[index];

				CargoLocationDataExample example = new CargoLocationDataExample();
				example.setOrderByClause("cacheCode asc,code asc");
				example.createCriteria().andWareHouseIdEqualTo(wareHouse.getId()).andCacheCodeLike(rootCacheCode + "%")
						.andConfigTypeEqualTo(configTypeId);

				List<CargoLocationData> list = this.cargoLocationDataMapper.selectByExample(example);
				if (CollectionUtils.isNotEmpty(list)) {

					// 按照父节点cacheCode分组
					Map<String, List<CargoLocationData>> cargoLocationDataMap = new HashMap<>();
					for (CargoLocationData cargoLocationData : list) {
						String cacheCode = cargoLocationData.getCacheCode();
						// 父节点cacheCode等于当前cacheCode减去该级别的长度
						String parentCacheCode = cacheCode.substring(0,
								cacheCode.length() - MapUtils.getInteger(map, cargoLocationData.getConfigType(), 0));
						if (!cargoLocationDataMap.containsKey(parentCacheCode)) {
							cargoLocationDataMap.put(parentCacheCode, new ArrayList<CargoLocationData>());
						}
						cargoLocationDataMap.get(parentCacheCode).add(cargoLocationData);
					}

					// 拣货工具
					Integer pickTool = form.getChildPickTool()[index];

					// 距离
					Double distance = form.getChildDistance()[index];

					// 给每一组设置参数
					for (String cacheCode : cargoLocationDataMap.keySet()) {

						List<CargoLocationData> dataList = cargoLocationDataMap.get(cacheCode);

						// 主优先级
						List<Integer> priorityList = this.getPriority(dataList.size(),
								form.getChildPrimaryPriority()[index]);

						// 次优先级
						List<Integer> standbyPriority = this.getPriority(list.size(),
								form.getChildStandbyPriority()[index]);

						for (int i = 0, size = dataList.size(); i < size; i++) {

							CargoLocationData cargoLocationData = dataList.get(i);

							// 优先级、次优先级、拣货工具、距离
							cargoLocationData.setPrimaryPriority(priorityList.get(i));
							cargoLocationData.setStandbyPriority(standbyPriority.get(i));
							cargoLocationData.setPickTool(pickTool);
							cargoLocationData.setDistance(distance);

							resultList.add(cargoLocationData);
						}
					}
				}
			}
		}
		// 汇总更新,方便以后改成批量更新
		// for (CargoLocationData cargoLocationData : resultList) {
		// this.cargoLocationDataMapper.updateByPrimaryKey(cargoLocationData);
		// }
		if (CollectionUtils.isNotEmpty(resultList)) {
			this.jdbcTemplate.execute("{call sp_updateValidateCargoLocation_Data('"
					+ XmlUtil.getUpdateLocationDataStrByList(resultList) + "')}");
		}
	}

	private Map<Integer, Integer> getConfigLengthMap(Integer warehouseId) {
		Map<Integer, Integer> map = new HashMap<>();

		WareHouseConfigExample example = new WareHouseConfigExample();
		example.setOrderByClause("rank asc");
		example.createCriteria().andWareHouseIdEqualTo(warehouseId);

		List<WareHouseConfig> list = this.wareHouseConfigMapper.selectByExample(example);
		if (CollectionUtils.isNotEmpty(list)) {
			for (WareHouseConfig wareHouseConfig : list) {
				map.put(wareHouseConfig.getWareHouseTypeId(), wareHouseConfig.getLength());
			}
		}
		return map;
	}

	// 根据排序方式计算优先级的值
	private List<Integer> getPriority(int size, Integer sort) {
		List<Integer> priorityList = new ArrayList<>(size);

		if (null != sort) {
			// 升序和降序
			if (1 == sort || 2 == sort) {
				for (int i = 1; i <= size; i++) {
					priorityList.add(i);
				}
			}
			// 升序奇偶相同和降序奇偶相同
			else if (3 == sort || 4 == sort) {
				for (int i = 0; i < size; i++) {
					priorityList.add(i / 2 + 1);
				}
			}
			// 相同
			else if (6 == sort) {
				for (int i = 0; i < size; i++) {
					priorityList.add(1);
				}
			}
			// 降序进行数据反转
			if (2 == sort || 4 == sort) {
				Collections.reverse(priorityList);
			}
		}
		// 没有选择或者选择自定义
		if (priorityList.isEmpty()) {
			for (int i = 0; i < size; i++) {
				priorityList.add(null);
			}
		}
		return priorityList;
	}

	/**
	 * 同步参数
	 * 
	 * @param wareHouse
	 *            当前仓库
	 * @param referId
	 *            参考id
	 * @param syncIds
	 *            被同步的id数组
	 * @param configTypeIds
	 *            需要级联子节点类型id数组
	 */
	@Transactional
	public void syncParam(WareHouse wareHouse, String referId, String[] syncIds, Integer[] configTypeIds) {

		if (null != configTypeIds && configTypeIds.length > 0) {

			WareHouseConfigExample example = new WareHouseConfigExample();
			example.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
			example.setOrderByClause("rank asc");

			List<WareHouseConfig> list = this.wareHouseConfigMapper.selectByExample(example);
			if (CollectionUtils.isEmpty(list)) {
				log.info("同步参数，该仓库还未配置级别信息");
				return;
			}

			// 参考对象
			CargoLocationData cargoLocationData = this.cargoLocationDataMapper.selectByPrimaryKey(referId);

			int start = this.getStartIndex(list, cargoLocationData.getConfigType());

			// 把参考的所有级别信息缓存起来
			Map<String, CargoLocationData> referMap = new HashMap<>();
			for (int configTypeId : configTypeIds) {

				CargoLocationDataExample cargoLocationDataExample = new CargoLocationDataExample();
				cargoLocationDataExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId())
						.andConfigTypeEqualTo(configTypeId).andCacheCodeLike(cargoLocationData.getCacheCode() + "_%");
				List<CargoLocationData> dataList = this.cargoLocationDataMapper
						.selectByExample(cargoLocationDataExample);
				for (CargoLocationData data : dataList) {
					String cacheCode = data.getCacheCode().substring(start);
					referMap.put(cacheCode, data);
				}
			}
			List<CargoLocationData> updateList = new ArrayList<CargoLocationData>();
			// 迭代需要同步的级别
			for (String syncId : syncIds) {

				cargoLocationData = this.cargoLocationDataMapper.selectByPrimaryKey(syncId);

				// 查询需要同步的子级别
				for (int configTypeId : configTypeIds) {

					CargoLocationDataExample cargoLocationDataExample = new CargoLocationDataExample();
					cargoLocationDataExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId())
							.andConfigTypeEqualTo(configTypeId)
							.andCacheCodeLike(cargoLocationData.getCacheCode() + "_%");
					List<CargoLocationData> dataList = this.cargoLocationDataMapper
							.selectByExample(cargoLocationDataExample);

					if (CollectionUtils.isNotEmpty(dataList)) {

						for (CargoLocationData data : dataList) {
							String cacheCode = data.getCacheCode().substring(start);
							if (referMap.containsKey(cacheCode)) {

								// 获取同级别相同编码的对象
								CargoLocationData referData = referMap.get(cacheCode);

								// 设置需要同步的参数:优先级、次优先级、距离和拣货工具
								data.setPrimaryPriority(referData.getPrimaryPriority());
								data.setStandbyPriority(referData.getStandbyPriority());
								data.setDistance(referData.getDistance());
								data.setPickTool(referData.getPickTool());

								updateList.add(data);
							}
						}
					}
				}
			}
			// for (CargoLocationData data : updateList) {
			// this.cargoLocationDataMapper.updateByPrimaryKey(data);
			// }
			if (CollectionUtils.isNotEmpty(updateList)) {
				this.jdbcTemplate.execute("{call sp_updateValidateCargoLocation_Data('"
						+ XmlUtil.getUpdateLocationDataStrByList(updateList) + "')}");
			}
		}
	}

	private int getStartIndex(List<WareHouseConfig> list, int configTypeId) {
		int index = 0;
		for (WareHouseConfig config : list) {
			index += config.getLength();
			if (config.getWareHouseTypeId() == configTypeId) {
				break;
			}
		}
		return index;
	}

	/**
	 * 计算货位分值
	 */
	@Transactional
	public MessageInfo calculationCargoLocationScore(WareHouse wareHouse) {

		MessageInfo messageInfo = new MessageInfo();

		// 查询当前仓库未设置配置的数量
		Integer count = this.commonMapper.selectNotSetCount(wareHouse.getId());
		if (count > 0) {
			log.info("当前仓库的配置信息还未全部设置，请到仓库配置页面进行检查");
			messageInfo.setMsg("当前仓库的配置信息还未全部设置，请到仓库配置页面进行检查");
			return messageInfo;
		}

		// 查询当前仓库所属货位信息
		CargoLocationExample cargoLocationExample = new CargoLocationExample();
		cargoLocationExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
		List<CargoLocation> cargoLocationList = this.cargoLocationMapper.selectByExample(cargoLocationExample);
		if (null == cargoLocationList || cargoLocationList.size() == 0) {
			log.info("计算货位分值失败，货位数据为空");
			messageInfo.setMsg("计算货位分值失败，货位数据为空");
			return messageInfo;
		}

		// 查询当前仓库配置信息
		WareHouseConfigExample wareHouseConfigExample = new WareHouseConfigExample();
		wareHouseConfigExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
		wareHouseConfigExample.setOrderByClause("rank asc");
		List<WareHouseConfig> wareHouseConfigList = this.wareHouseConfigMapper.selectByExample(wareHouseConfigExample);
		if (null == wareHouseConfigList || wareHouseConfigList.size() == 0) {
			log.info("计算货位分值失败，仓库配置数据为空");
			messageInfo.setMsg("计算货位分值失败，仓库配置数据为空");
			return messageInfo;
		}
		Map<Integer, WareHouseConfig> wareHouseConfigMap = new HashMap<>();
		for (WareHouseConfig wareHouseConfig : wareHouseConfigList) {
			wareHouseConfigMap.put(wareHouseConfig.getId(), wareHouseConfig);
		}

		// 查询所有货位类型信息
		CargoLocationTypeExample cargoLocationTypeExample = new CargoLocationTypeExample();
		cargoLocationTypeExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
		List<CargoLocationType> cargoLocationTypeList = this.cargoLocationTypeMapper
				.selectByExample(cargoLocationTypeExample);
		if (null == cargoLocationTypeList || cargoLocationTypeList.size() == 0) {
			log.info("计算货位分值失败，货位类型数据为空");
			messageInfo.setMsg("计算货位分值失败，货位类型数据为空");
			return messageInfo;
		}
		Map<Integer, CargoLocationType> cargoLocationTypeMap = new HashMap<>();
		for (CargoLocationType cargoLocationType : cargoLocationTypeList) {
			cargoLocationTypeMap.put(cargoLocationType.getId(), cargoLocationType);
		}

		// 查询货位的计算数据
		CargoLocationDataExample cargoLocationDataExample = new CargoLocationDataExample();
		cargoLocationDataExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());

		List<CargoLocationData> cargoLocationDataList = this.cargoLocationDataMapper
				.selectByExample(cargoLocationDataExample);
		if (null == cargoLocationDataList || cargoLocationDataList.size() == 0) {
			log.info("计算货位分值失败，货位计算数据为空");
			messageInfo.setMsg("计算货位分值失败，货位计算数据为空");
			return messageInfo;
		}
		Map<String, CargoLocationData> cargoLocationDataMap = new HashMap<>();
		for (CargoLocationData cargoLocationData : cargoLocationDataList) {
			cargoLocationDataMap.put(cargoLocationData.getCacheCode(), cargoLocationData);
		}

		// 初始化待更新分数的货位
		List<CargoLocation> updateList = new ArrayList<>();

		// 循环计算每个货位的分数
		for (CargoLocation cargoLocation : cargoLocationList) {
			// 货位编码
			String cargoLocationCode = cargoLocation.getCode();

			// 距离
			int distance = 0;
			// 层
			int floor = 0;
			// 行驶速度
			Double driverSpeed = 1d;
			// 抬举速度
			Double handsUpSpeed = 1d;

			// 初始化开始索引
			int startIndex = 0;

			// 处理货位类型数据，当第一次循环时
			for (WareHouseConfig wareHouseConfig : wareHouseConfigList) {
				// 设置结束索引
				int endIndex = startIndex + wareHouseConfig.getLength();

				// 获取编码
				String cacheCode = cargoLocationCode.substring(0, endIndex);

				// 检测货位类型数据是否已被添加
				if (cargoLocationDataMap.containsKey(cacheCode)) {
					// 获取当前缓存编码所属的货位计算数据
					CargoLocationData cargoLocationData = cargoLocationDataMap.get(cacheCode);

					CargoLocationDataExample example;
					List<CargoLocationData> list;
					Criteria criteria;
					switch (cargoLocationData.getConfigType()) {
					case 1: // 区域
						// 计算距离区域的距离
						example = new CargoLocationDataExample();
						criteria = example.createCriteria()
								.andStandbyPriorityEqualTo(cargoLocationData.getStandbyPriority())
								.andPrimaryPriorityLessThan(cargoLocationData.getPrimaryPriority())
								.andConfigTypeEqualTo(1) // 匹配类型为区域
								.andWareHouseIdEqualTo(cargoLocation.getWareHouseId());

						if (StringUtils.isEmpty(cargoLocationData.getParentId())) {
							criteria.andParentIdIsNull();
						} else {
							criteria.andParentIdEqualTo(cargoLocationData.getParentId());
						}

						list = this.cargoLocationDataMapper.selectByExample(example);
						for (CargoLocationData item : list) {
							distance += item.getDistance() == null ? 0d : item.getDistance();
						}
						break;
					case 2: // 通道
						// 计算距离通道的距离
						example = new CargoLocationDataExample();
						criteria = example.createCriteria()
								.andStandbyPriorityEqualTo(cargoLocationData.getStandbyPriority())
								.andPrimaryPriorityLessThan(cargoLocationData.getPrimaryPriority())
								.andConfigTypeEqualTo(2) // 匹配类型为通道
								.andWareHouseIdEqualTo(cargoLocation.getWareHouseId());

						if (StringUtils.isEmpty(cargoLocationData.getParentId())) {
							criteria.andParentIdIsNull();
						} else {
							criteria.andParentIdEqualTo(cargoLocationData.getParentId());
						}

						list = this.cargoLocationDataMapper.selectByExample(example);
						for (CargoLocationData item : list) {
							distance += item.getDistance() == null ? 0d : item.getDistance();
						}
						break;
					case 3: // 排货架
						// 计算距离排货架的距离
						example = new CargoLocationDataExample();
						criteria = example.createCriteria()
								.andStandbyPriorityEqualTo(cargoLocationData.getStandbyPriority())
								.andPrimaryPriorityLessThan(cargoLocationData.getPrimaryPriority())
								.andConfigTypeEqualTo(3) // 匹配类型为排货架
								.andWareHouseIdEqualTo(cargoLocation.getWareHouseId());
						if (StringUtils.isEmpty(cargoLocationData.getParentId())) {
							criteria.andParentIdIsNull();
						} else {
							criteria.andParentIdEqualTo(cargoLocationData.getParentId());
						}

						list = this.cargoLocationDataMapper.selectByExample(example);

						distance += list.size() * cargoLocationTypeMap.get(cargoLocation.getTypeId()).getLength();
						break;
					case 4: // 层
						floor = cargoLocationData.getPrimaryPriority() - 1;
						// 根据捡货工具设置捡货速度
						// 1 人
						// 2 叉车
						// 3 捡货车
						if (null != cargoLocationData.getPickTool()) {
							switch (cargoLocationData.getPickTool()) {
							case 1:
								// 设置行驶速度
								driverSpeed = wareHouse.getPersonDrivingSpeed();
								handsUpSpeed = wareHouse.getPersonPickUpSpeed();
								break;
							case 2:
								// 设置行驶速度
								driverSpeed = wareHouse.getForkliftDrivingSpeed();
								handsUpSpeed = wareHouse.getForkliftPickUpSpeed();
								break;
							case 3:
								// 设置行驶速度
								driverSpeed = wareHouse.getPickTruckDrivingSpeed();
								handsUpSpeed = wareHouse.getPickTuckPickUpSpeed();
								break;
							}
						}
						break;
					case 5: // 货位
						distance += cargoLocationData.getPrimaryPriority() - 1;
						break;
					case 6: // 进深
						distance += cargoLocationData.getPrimaryPriority() - 1;
						break;
					}
				}
				// 开始索引后移
				startIndex = endIndex;
			}

			// 计算分值
			double result = distance / driverSpeed + floor / handsUpSpeed;
			log.info("货位分值计算结果：货位主键[{}]，分值[{}]", cargoLocation.getId(), result);

			// 设置分值(用一个比较大值进去分值，这样分值越大货位越好)
			cargoLocation.setScore(1000 - new BigDecimal(result).setScale(2, RoundingMode.UP).doubleValue());

			// 添加到待更新列表
			updateList.add(cargoLocation);
		}

		// 更新货位分值
		for (CargoLocation cargoLocation : updateList) {
			this.cargoLocationMapper.updateByPrimaryKeySelective(cargoLocation);
		}
		messageInfo.setSuccess(true);
		return messageInfo;
	}

	/**
	 * 多线程对货位进行分值计算
	 * 
	 * @param wareHouse
	 * @return
	 * @throws Exception
	 */
	public MessageInfo asynCalculationCargoLocationScore(final WareHouse wareHouse) throws Exception {

		MessageInfo messageInfo = new MessageInfo();

		// 查询当前仓库未设置配置的数量
		Integer count = this.commonMapper.selectNotSetCount(wareHouse.getId());
		if (count > 0) {
			log.info("当前仓库的配置信息还未全部设置，请到仓库配置页面进行检查");
			messageInfo.setMsg("当前仓库的配置信息还未全部设置，请到仓库配置页面进行检查");
			return messageInfo;
		}

		// 查询当前仓库配置信息
		WareHouseConfigExample wareHouseConfigExample = new WareHouseConfigExample();
		wareHouseConfigExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
		wareHouseConfigExample.setOrderByClause("rank asc");
		final List<WareHouseConfig> wareHouseConfigList = this.wareHouseConfigMapper
				.selectByExample(wareHouseConfigExample);
		if (null == wareHouseConfigList || wareHouseConfigList.size() == 0) {
			log.info("计算货位分值失败，仓库配置数据为空");
			messageInfo.setMsg("计算货位分值失败，仓库配置数据为空");
			return messageInfo;
		}
		Map<Integer, WareHouseConfig> wareHouseConfigMap = new HashMap<>();
		for (WareHouseConfig wareHouseConfig : wareHouseConfigList) {
			wareHouseConfigMap.put(wareHouseConfig.getId(), wareHouseConfig);
		}

		// 查询所有货位类型信息
		CargoLocationTypeExample cargoLocationTypeExample = new CargoLocationTypeExample();
		cargoLocationTypeExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
		List<CargoLocationType> cargoLocationTypeList = this.cargoLocationTypeMapper
				.selectByExample(cargoLocationTypeExample);
		if (null == cargoLocationTypeList || cargoLocationTypeList.size() == 0) {
			log.info("计算货位分值失败，货位类型数据为空");
			messageInfo.setMsg("计算货位分值失败，货位类型数据为空");
			return messageInfo;
		}
		final Map<Integer, CargoLocationType> cargoLocationTypeMap = new HashMap<>();
		for (CargoLocationType cargoLocationType : cargoLocationTypeList) {
			cargoLocationTypeMap.put(cargoLocationType.getId(), cargoLocationType);
		}

		// 第一级别的长度
		int firstLength = wareHouseConfigList.get(0).getLength();
		// 第一级别的类型
		int firstTypeId = wareHouseConfigList.get(0).getWareHouseTypeId();

		// 查询当前仓库所属货位信息
		CargoLocationExample cargoLocationExample = new CargoLocationExample();
		cargoLocationExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
		List<CargoLocation> cargoLocationList = this.cargoLocationMapper.selectByExample(cargoLocationExample);
		if (null == cargoLocationList || cargoLocationList.size() == 0) {
			log.info("计算货位分值失败，货位数据为空");
			messageInfo.setMsg("计算货位分值失败，货位数据为空");
			return messageInfo;
		}

		// 按照第一级别对货位进行分组
		final Map<String, List<CargoLocation>> cargoLocationMap = new HashMap<>();
		for (CargoLocation cargoLocation : cargoLocationList) {
			String fristCode = cargoLocation.getCode().substring(0, firstLength);
			if (!cargoLocationMap.containsKey(fristCode)) {
				cargoLocationMap.put(fristCode, new ArrayList<CargoLocation>());
			}
			cargoLocationMap.get(fristCode).add(cargoLocation);
		}

		// 查询货位的计算数据
		CargoLocationDataExample cargoLocationDataExample = new CargoLocationDataExample();
		cargoLocationDataExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());

		List<CargoLocationData> cargoLocationDataList = this.cargoLocationDataMapper
				.selectByExample(cargoLocationDataExample);
		if (null == cargoLocationDataList || cargoLocationDataList.size() == 0) {
			log.info("计算货位分值失败，货位计算数据为空");
			messageInfo.setMsg("计算货位分值失败，货位计算数据为空");
			return messageInfo;
		}

		// 缓存第一级别的信息
		final List<CargoLocationData> rootTypeDataList = new ArrayList<>();
		// 按照第一级别对货位配置进行分组
		final Map<String, List<CargoLocationData>> cargoLocationDataMap = new HashMap<>();
		for (CargoLocationData cargoLocationData : cargoLocationDataList) {
			if (firstTypeId == cargoLocationData.getConfigType()) {
				rootTypeDataList.add(cargoLocationData);
			}
			String key = cargoLocationData.getCacheCode().substring(0, firstLength);
			if (!cargoLocationDataMap.containsKey(key)) {
				cargoLocationDataMap.put(key, new ArrayList<CargoLocationData>());
			}
			cargoLocationDataMap.get(key).add(cargoLocationData);
		}

		List<CargoLocation> updateCargoLocationList = new ArrayList<>();
		ExecutorService threadPool = Executors.newCachedThreadPool();
		try {

			CompletionService<List<CargoLocation>> cs = new ExecutorCompletionService<List<CargoLocation>>(threadPool);
			// 每一组分配一个线程进行货位分值的计算
			for (final String key : cargoLocationMap.keySet()) {
				cs.submit(new Callable<List<CargoLocation>>() {
					public List<CargoLocation> call() throws Exception {
						return calculationCargoLocationScore(wareHouse, cargoLocationTypeMap, cargoLocationMap.get(key),
								cargoLocationDataMap.get(key), wareHouseConfigList, rootTypeDataList);
					}
				});
			}
			// 等待每个线程都执行完
			for (int i = 0; i < cargoLocationMap.size(); i++) {
				updateCargoLocationList.addAll(cs.take().get());
			}

			// for (CargoLocation cargoLocation : updateCargoLocationList) {
			// this.cargoLocationMapper.updateByPrimaryKeySelective(cargoLocation);
			// }
			if (CollectionUtils.isNotEmpty(updateCargoLocationList)) {
				this.jdbcTemplate.execute("{call sp_updateValidateCargoLocationScore('"
						+ XmlUtil.getUpdateScoreLocationStrByList(updateCargoLocationList) + "')}");
			}
		} finally {
			threadPool.shutdown();
		}

		messageInfo.setSuccess(true);
		return messageInfo;
	}

	private List<CargoLocation> calculationCargoLocationScore(WareHouse wareHouse,
			Map<Integer, CargoLocationType> cargoLocationTypeMap, List<CargoLocation> cargoLocationList,
			List<CargoLocationData> cargoLocationDataList, List<WareHouseConfig> wareHouseConfigList,
			List<CargoLocationData> firstCargoLocationDataList) {

		// 初始化待更新分数的货位
		List<CargoLocation> updateList = new ArrayList<>();

		if (CollectionUtils.isEmpty(cargoLocationDataList) || CollectionUtils.isEmpty(cargoLocationList)) {
			return updateList;
		}

		// 把通道、区域、排货架按照父节点分组缓存起来
		Map<String, List<CargoLocationData>> typeMap = new HashMap<>();
		Map<String, CargoLocationData> cacheCodeMap = new HashMap<>();
		for (CargoLocationData cargoLocationData : cargoLocationDataList) {
			int configType = cargoLocationData.getConfigType();
			String parentId = cargoLocationData.getParentId();
			// 区域、通道和排货架缓存起来
			if (1 == configType || 2 == configType || 3 == configType) {
				if (!typeMap.containsKey(parentId)) {
					typeMap.put(parentId, new ArrayList<CargoLocationData>());
				}
				typeMap.get(parentId).add(cargoLocationData);
			}
			cacheCodeMap.put(cargoLocationData.getCacheCode(), cargoLocationData);
		}
		if (typeMap.containsKey(null)) {
			typeMap.put(null, firstCargoLocationDataList);
		}

		// 循环计算每个货位的分数
		for (CargoLocation cargoLocation : cargoLocationList) {

			// 货位编码
			String cargoLocationCode = cargoLocation.getCode();

			// 距离
			int distance = 0;
			// 层
			int floor = 0;
			// 行驶速度
			Double driverSpeed = 1d;
			// 抬举速度
			Double handsUpSpeed = 1d;

			// 初始化开始索引
			int startIndex = 0;

			// 处理货位类型数据，当第一次循环时
			for (WareHouseConfig wareHouseConfig : wareHouseConfigList) {

				// 设置结束索引
				int endIndex = startIndex + wareHouseConfig.getLength();

				// 获取编码
				String cacheCode = cargoLocationCode.substring(0, endIndex);

				// 检测货位类型数据是否已被添加
				if (cacheCodeMap.containsKey(cacheCode)) {

					// 获取当前缓存编码所属的货位计算数据
					CargoLocationData cargoLocationData = cacheCodeMap.get(cacheCode);

					List<CargoLocationData> list;
					switch (cargoLocationData.getConfigType()) {
					case 1: // 区域
						// 计算距离区域的距离
						list = this.findCargoLocationDataByParentIdAndPriority(typeMap, cargoLocationData.getParentId(),
								cargoLocationData.getPrimaryPriority(), cargoLocationData.getStandbyPriority());
						for (CargoLocationData item : list) {
							distance += item.getDistance() == null ? 0d : item.getDistance();
						}
						break;
					case 2: // 通道
						// 计算距离通道的距离
						list = this.findCargoLocationDataByParentIdAndPriority(typeMap, cargoLocationData.getParentId(),
								cargoLocationData.getPrimaryPriority(), cargoLocationData.getStandbyPriority());
						for (CargoLocationData item : list) {
							distance += item.getDistance() == null ? 0d : item.getDistance();
						}
						break;
					case 3: // 排货架
						// 计算距离排货架的距离
						list = this.findCargoLocationDataByParentIdAndPriority(typeMap, cargoLocationData.getParentId(),
								cargoLocationData.getPrimaryPriority(), cargoLocationData.getStandbyPriority());
						distance += list.size() * cargoLocationTypeMap.get(cargoLocation.getTypeId()).getLength();
						break;
					case 4: // 层
						floor = cargoLocationData.getPrimaryPriority() - 1;
						// 根据捡货工具设置捡货速度
						// 1 人
						// 2 叉车
						// 3 捡货车
						if (null != cargoLocationData.getPickTool()) {
							switch (cargoLocationData.getPickTool()) {
							case 1:
								// 设置行驶速度
								driverSpeed = wareHouse.getPersonDrivingSpeed();
								handsUpSpeed = wareHouse.getPersonPickUpSpeed();
								break;
							case 2:
								// 设置行驶速度
								driverSpeed = wareHouse.getForkliftDrivingSpeed();
								handsUpSpeed = wareHouse.getForkliftPickUpSpeed();
								break;
							case 3:
								// 设置行驶速度
								driverSpeed = wareHouse.getPickTruckDrivingSpeed();
								handsUpSpeed = wareHouse.getPickTuckPickUpSpeed();
								break;
							}
						}
						break;
					case 5: // 货位
						distance += cargoLocationData.getPrimaryPriority() - 1;
						break;
					case 6: // 进深
						distance += cargoLocationData.getPrimaryPriority() - 1;
						break;
					}
				}
				// 开始索引后移
				startIndex = endIndex;
			}

			// 计算分值
			double result = distance / driverSpeed + floor / handsUpSpeed;
			log.info("货位分值计算结果：货位主键[{}]，分值[{}]", cargoLocation.getId(), result);

			// 设置分值(用一个比较大值进去分值，这样分值越大货位越好)
			cargoLocation.setScore(1000 - new BigDecimal(result).setScale(2, RoundingMode.UP).doubleValue());

			// 添加到待更新列表
			updateList.add(cargoLocation);
		}
		return updateList;
	}

	private List<CargoLocationData> findCargoLocationDataByParentIdAndPriority(
			Map<String, List<CargoLocationData>> typeMap, String parentId, Integer primaryPriority,
			Integer standbyPriority) {
		List<CargoLocationData> resultDataList = new ArrayList<>();
		List<CargoLocationData> cacheDataList = typeMap.get(parentId);
		if (CollectionUtils.isNotEmpty(cacheDataList)) {
			for (CargoLocationData cargoLocationData : cacheDataList) {
				if (standbyPriority == cargoLocationData.getStandbyPriority()
						&& primaryPriority > cargoLocationData.getPrimaryPriority()) {
					resultDataList.add(cargoLocationData);
				}
			}
		}
		return resultDataList;
	}

	public PageInfo<CargoLocationData> findPage(CargoLocationData cargoLocationData, Integer page, Integer size) {

		// 获取第1页，10条内容，默认查询总数count
		PageHelper.startPage(page == null ? 1 : page, size == null ? 10 : size);

		CargoLocationDataExample example = new CargoLocationDataExample();
		example.setOrderByClause("code asc");
		Criteria criteria = example.createCriteria().andWareHouseIdEqualTo(cargoLocationData.getWareHouseId());
		if (StringUtils.isNotEmpty(cargoLocationData.getParentId())) {
			criteria.andParentIdEqualTo(cargoLocationData.getParentId());
		}
		criteria.andConfigTypeEqualTo(cargoLocationData.getConfigType());

		return new PageInfo<CargoLocationData>(this.cargoLocationDataMapper.selectByExample(example));
	}

	public List<CargoLocationData> findListByParentId(CargoLocationData cargoLocationData) {

		CargoLocationDataExample example = new CargoLocationDataExample();
		example.setOrderByClause("code asc");
		Criteria criteria = example.createCriteria().andWareHouseIdEqualTo(cargoLocationData.getWareHouseId());
		if (StringUtils.isNotEmpty(cargoLocationData.getParentId())) {
			criteria.andParentIdEqualTo(cargoLocationData.getParentId());
		} else {
			criteria.andParentIdIsNull();
		}
		return this.cargoLocationDataMapper.selectByExample(example);
	}
}
