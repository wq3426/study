package com.dhl.tools.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dhl.tools.domain.*;
import com.dhl.tools.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by liuso on 2017/4/10.
 */
@Slf4j
@Service
public class WareHouseService {

	@Autowired
	private WareHouseMapper wareHouseMapper;

	@Autowired
	private WareHouseConfigMapper wareHouseConfigMapper;

	@Autowired
	private CargoLocationTypeMapper cargoLocationTypeMapper;

	@Autowired
	private CargoLocationMapper cargoLocationMapper;

	@Autowired
	private MaterialMapper materialMapper;

	@Autowired
	private RoleInfoWareHouseMapper roleInfoWareHouseMapper;

	@Autowired
	private CargoLocationDataMapper cargoLocationDataMapper;

	@Autowired
	private MaterialCargoLocationTypeDataMapper materialCargoLocationTypeDataMapper;

	/**
	 * 添加仓库
	 * 
	 * @param wareHouse
	 *            编码
	 * @return
	 */
	@Transactional
	public boolean add(WareHouse wareHouse, Integer[] typeIds, Integer[] lengths, String[] alias) {
		if (StringUtils.isEmpty(wareHouse.getCode())) {
			log.info("添加仓库失败，编码为空");
			return false;
		}

		WareHouseExample wareHouseExample = new WareHouseExample();
		wareHouseExample.createCriteria().andCodeEqualTo(wareHouse.getCode());
		if (this.wareHouseMapper.countByExample(wareHouseExample) > 0) {
			log.info("添加仓库失败，编码已存在");
			return false;
		}

		// 检测类型主键和编码长度数组长度是否一致
		if (typeIds.length != lengths.length) {
			return false;
		}

		// 货位编码长度
		int fullCodeLength = 0;
		for (Integer length : lengths) {
			fullCodeLength += length;
		}

		wareHouse.setFullCodeLength(fullCodeLength);

		// 获取添加结果
		int result = this.wareHouseMapper.insert(wareHouse);
		if (result == 1) {
			for (int i = 0; i < typeIds.length; i++) {
				WareHouseConfig config = new WareHouseConfig();
				config.setWareHouseId(wareHouse.getId());
				config.setWareHouseTypeId(typeIds[i]);
				config.setLength(lengths[i]);
				config.setRank(i + 1);
				if (null != alias[i]) {
					config.setWareHouseTypeAlias(alias[i]);
				}
				this.wareHouseConfigMapper.insert(config);
			}
		}
		return result > 0;
	}

	/**
	 * 根据主键删除
	 * 
	 * @param id
	 *            主键
	 * @return
	 */
	@Transactional
	public boolean delete(Integer id) {
		if (null == id) {
			log.info("删除仓库失败，主键为空");
			return false;
		}

		MaterialCargoLocationTypeDataExample materialCargoLocationTypeDataExample = new MaterialCargoLocationTypeDataExample();
		materialCargoLocationTypeDataExample.createCriteria().andWareHouseIdEqualTo(id);
		materialCargoLocationTypeDataMapper.deleteByExample(materialCargoLocationTypeDataExample);

		// 删除物料
		MaterialExample materialExample = new MaterialExample();
		materialExample.createCriteria().andWareHouseIdEqualTo(id);
		this.materialMapper.deleteByExample(materialExample);

		// 删除货位
		CargoLocationExample cargoLocationExample = new CargoLocationExample();
		cargoLocationExample.createCriteria().andWareHouseIdEqualTo(id);
		this.cargoLocationMapper.deleteByExample(cargoLocationExample);

		// 删除货位类型
		CargoLocationTypeExample cargoLocationTypeExample = new CargoLocationTypeExample();
		cargoLocationTypeExample.createCriteria().andWareHouseIdEqualTo(id);
		this.cargoLocationTypeMapper.deleteByExample(cargoLocationTypeExample);

		// 删除角色与仓库的关系
		RoleInfoWareHouseExample roleInfoWareHouseExample = new RoleInfoWareHouseExample();
		roleInfoWareHouseExample.createCriteria().andWareHouseIdEqualTo(id);
		this.roleInfoWareHouseMapper.deleteByExample(roleInfoWareHouseExample);

		// 删除货位拆分的信息
		CargoLocationDataExample cargoLocationDataExample = new CargoLocationDataExample();
		cargoLocationDataExample.createCriteria().andWareHouseIdEqualTo(id);
		this.cargoLocationDataMapper.deleteByExample(cargoLocationDataExample);

		// 删除仓库级别配置信息
		WareHouseConfigExample wareHouseConfigExample = new WareHouseConfigExample();
		wareHouseConfigExample.createCriteria().andWareHouseIdEqualTo(id);
		this.wareHouseConfigMapper.deleteByExample(wareHouseConfigExample);

		// 调用删除
		this.wareHouseMapper.deleteByPrimaryKey(id);
		return true;
	}

	/**
	 * 更新仓库
	 * 
	 * @param wareHouse
	 *            编码
	 * @return
	 */
	@Transactional
	public boolean update(WareHouse wareHouse, Integer[] typeIds, Integer[] lengths, String[] alias) {
		if (wareHouse.getId() == null) {
			log.info("更新仓库失败，主键为空");
			return false;
		}

		// 检测类型主键和编码长度数组长度是否一致
		if (typeIds.length != lengths.length) {
			return false;
		}

		// 货位编码长度
		int fullCodeLength = 0;
		for (Integer length : lengths) {
			fullCodeLength += length;
		}
		wareHouse.setFullCodeLength(fullCodeLength);

		// 更新仓库
		this.wareHouseMapper.updateByPrimaryKeySelective(wareHouse);

		WareHouseConfigExample wareHouseConfigExample = new WareHouseConfigExample();
		wareHouseConfigExample.createCriteria().andWareHouseIdEqualTo(wareHouse.getId());
		wareHouseConfigExample.setOrderByClause("rank asc");
		List<WareHouseConfig> wareHouseConfigList = this.wareHouseConfigMapper.selectByExample(wareHouseConfigExample);

		for (int i = 0; i < typeIds.length; i++) {
			if (i >= wareHouseConfigList.size()) {
				WareHouseConfig config = new WareHouseConfig();
				config.setWareHouseId(wareHouse.getId());
				config.setWareHouseTypeId(typeIds[i]);
				config.setLength(lengths[i]);
				if (null != alias[i]) {
					config.setWareHouseTypeAlias(alias[i]);
				}
				this.wareHouseConfigMapper.insert(config);
			} else {
				WareHouseConfig config = wareHouseConfigList.get(i);
				config.setWareHouseTypeId(typeIds[i]);
				config.setLength(lengths[i]);
				if (null != alias[i]) {
					config.setWareHouseTypeAlias(alias[i]);
				}

				this.wareHouseConfigMapper.updateByPrimaryKeySelective(config);
			}
		}

		// 检测仓库配置级别是否比原来少
		if (typeIds.length < wareHouseConfigList.size()) {
			List<Integer> idList = Arrays.asList(typeIds);
			for (WareHouseConfig wareHouseConfig : wareHouseConfigList) {
				Integer id = wareHouseConfig.getWareHouseTypeId();
				if (!idList.contains(id)) {
					CargoLocationDataExample cargoLocationDataExample = new CargoLocationDataExample();
					cargoLocationDataExample.createCriteria().andWareHouseIdEqualTo(wareHouseConfig.getWareHouseId())
							.andConfigIdEqualTo(wareHouseConfig.getId());
					List<CargoLocationData> cargoLocationDataList = this.cargoLocationDataMapper
							.selectByExample(cargoLocationDataExample);

					for (CargoLocationData cargoLocationData : cargoLocationDataList) {
						cargoLocationData.setConfigId(null);
						cargoLocationDataMapper.updateByPrimaryKey(cargoLocationData);
					}

					this.wareHouseConfigMapper.deleteByPrimaryKey(wareHouseConfig.getId());
				}
			}
		}

		return true;
	}

	/**
	 * 根据主键查询
	 *
	 * @param id
	 *            主键
	 * @return
	 */
	public JSONObject findById(Integer id) {
		WareHouse wareHouse = this.wareHouseMapper.selectByPrimaryKey(id);
		JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(wareHouse));
		// 查询仓库等级配置
		WareHouseConfigExample example = new WareHouseConfigExample();
		example.setOrderByClause("rank asc");
		example.createCriteria().andWareHouseIdEqualTo(id);
		jsonObject.put("config", this.wareHouseConfigMapper.selectByExample(example));
		return jsonObject;
	}

	/**
	 * 判断仓库编码是否存在
	 *
	 * @param id
	 *            主键
	 * @param code
	 *            仓库编码
	 * 
	 * @return
	 */
	public boolean findCodeIsExist(Integer id, String code) {
		WareHouseExample wareHouseExample = new WareHouseExample();
		wareHouseExample.setOrderByClause("createDate desc");
		if (0 != id) {
			wareHouseExample.createCriteria().andCodeEqualTo(code).andIdNotEqualTo(id);
		} else {
			wareHouseExample.createCriteria().andCodeEqualTo(code);
		}
		return this.wareHouseMapper.countByExample(wareHouseExample) > 0;
	}

	/**
	 * 查询所有
	 * 
	 * @return
	 */
	public List<WareHouse> findList() {
		return wareHouseMapper.selectByExample(null);
	}

	/**
	 * 分页查询
	 *
	 * @param code
	 * @param page
	 *            页码 从1开始
	 * @param size
	 *            数量
	 * @return
	 */
	public JSONObject findPage(String code, Integer page, Integer size) {
		// 获取第1页，10条内容，默认查询总数count
		PageHelper.startPage(page == null ? 1 : page, size == null ? 10 : size);

		WareHouseExample example = new WareHouseExample();
		example.setOrderByClause("createDate desc");
		if (!StringUtils.isEmpty(code)) {
			example.createCriteria().andCodeLike("%" + code + "%");
		}
		JSONObject jsonObject = JSON
				.parseObject(JSON.toJSONString(new PageInfo<WareHouse>(wareHouseMapper.selectByExample(example))));
		JSONArray array = jsonObject.getJSONArray("list");
		if (null != array && array.size() > 0) {
			for (int i = 0; i < array.size(); i++) {
				JSONObject wareHouseJoson = array.getJSONObject(i);
				if (null != wareHouseJoson) {
					wareHouseJoson.put("wareHousetype", getAliasStr(wareHouseJoson.getInteger("id")));
				}
			}
		}
		return jsonObject;
	}

	// 获取仓库的配置的级别名称
	private String getAliasStr(int wareHouseId) {
		WareHouseConfigExample example = new WareHouseConfigExample();
		example.setOrderByClause("rank asc");
		example.createCriteria().andWareHouseIdEqualTo(wareHouseId);

		List<String> typeList = new ArrayList<String>();
		List<WareHouseConfig> list = this.wareHouseConfigMapper.selectByExample(example);
		for (WareHouseConfig config : list) {
			typeList.add(config.getWareHouseTypeAlias());
		}
		if (!typeList.isEmpty()) {
			return org.apache.commons.lang3.StringUtils.join(typeList.toArray(new String[0]), "-");
		}
		return "";
	}
}
