package com.dhl.tools.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dhl.tools.domain.WareHouseConfig;
import com.dhl.tools.domain.WareHouseConfigExample;
import com.dhl.tools.mapper.WareHouseConfigMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * Created by liuso on 2017/4/29.
 */
@Service
public class WareHouseConfigService {

	@Autowired
	private WareHouseConfigMapper wareHouseConfigMapper;

	/**
	 * 更新仓库配置信息
	 *
	 * @param wareHouseConfig
	 *            仓库配置信息
	 */
	@Transactional
	public boolean update(WareHouseConfig wareHouseConfig) {
		int result = this.wareHouseConfigMapper.updateByPrimaryKeySelective(wareHouseConfig);
		return result > 0;
	}

	/**
	 * 分页查询仓库配置信息
	 *
	 * @param configTypeId
	 *            仓库类型主键
	 * @param page
	 *            页码
	 * @param size
	 *            数量
	 * @return 分页信息
	 */
	public PageInfo<WareHouseConfig> findByPage(Integer configTypeId, Integer page, Integer size) {
		WareHouseConfigExample example = new WareHouseConfigExample();
		if (null != configTypeId) {
			// 查询类型主键大于当前值的数据
			// 因为配置类型是按照从区域到层进行配置的，主键是自增固定的，所以，可以取代rank数值使用
			example.createCriteria().andWareHouseTypeIdEqualTo(configTypeId + 1);
		}
		PageHelper.startPage(page == null ? 1 : page, size == null ? 10 : size);
		List<WareHouseConfig> list = this.wareHouseConfigMapper.selectByExample(example);
		return new PageInfo<>(list);
	}

	/**
	 * 查询仓库配置信息
	 *
	 * @param configTypeId
	 *            仓库类型主键
	 * 
	 * @return 仓库配置信息
	 */
	public List<WareHouseConfig> findByList(int wareHouseId) {
		WareHouseConfigExample example = new WareHouseConfigExample();
		example.setOrderByClause("rank asc");
		example.createCriteria().andWareHouseIdEqualTo(wareHouseId);
		return this.wareHouseConfigMapper.selectByExample(example);
	}
}
