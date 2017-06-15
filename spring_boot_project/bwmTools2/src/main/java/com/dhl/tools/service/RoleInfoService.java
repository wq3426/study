package com.dhl.tools.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dhl.tools.domain.*;
import com.dhl.tools.mapper.CommonMapper;
import com.dhl.tools.mapper.RoleInfoAuthorityInfoMapper;
import com.dhl.tools.mapper.RoleInfoMapper;
import com.dhl.tools.mapper.RoleInfoWareHouseMapper;
import com.dhl.tools.mapper.UserInfoRoleInfoMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 角色业务处理 Created by liuso on 2017/4/12.
 */
@Service
public class RoleInfoService {

	@Autowired
	private RoleInfoMapper roleInfoMapper;

	@Autowired
	private RoleInfoWareHouseMapper roleInfoWareHouseMapper;

	@Autowired
	private CommonMapper commonMapper;

	@Autowired
	private RoleInfoAuthorityInfoMapper roleInfoAuthorityInfoMapper;

	@Autowired
	private UserInfoRoleInfoMapper userInfoRoleInfoMapper;
	
	@Transactional
	public boolean add(RoleInfo roleInfo) {
		int result = roleInfoMapper.insert(roleInfo);
		return result > 0;
	}
	
	@Transactional
	public boolean delete(Integer id) {

		UserInfoRoleInfoExample userInfoRoleInfoExample = new UserInfoRoleInfoExample();
		userInfoRoleInfoExample.createCriteria().andRoleIdEqualTo(id);
		this.userInfoRoleInfoMapper.deleteByExample(userInfoRoleInfoExample);

		RoleInfoAuthorityInfoExample roleInfoAuthorityInfoExample = new RoleInfoAuthorityInfoExample();
		roleInfoAuthorityInfoExample.createCriteria().andRoleIdEqualTo(id);
		this.roleInfoAuthorityInfoMapper.deleteByExample(roleInfoAuthorityInfoExample);

		RoleInfoWareHouseExample roleInfoWareHouseExample = new RoleInfoWareHouseExample();
		roleInfoWareHouseExample.createCriteria().andRoleIdEqualTo(id);
		this.roleInfoWareHouseMapper.deleteByExample(roleInfoWareHouseExample);

		int result = roleInfoMapper.deleteByPrimaryKey(id);
		return result > 0;
	}
	
	@Transactional
	public boolean update(RoleInfo roleInfo) {
		int result = roleInfoMapper.updateByPrimaryKeySelective(roleInfo);
		return result > 0;
	}

	public RoleInfo findById(Integer id) {
		return roleInfoMapper.selectByPrimaryKey(id);
	}

	public JSONObject findPage(RoleInfo roleInfo, Integer page, Integer size) {
		// 获取第1页，10条内容，默认查询总数count
		PageHelper.startPage(page == null ? 1 : page, size == null ? 10 : size);

		RoleInfoExample example = new RoleInfoExample();
		example.setOrderByClause("createDate desc");
		if (StringUtils.isNotBlank(roleInfo.getName())) {
			example.createCriteria().andNameLike("%" + roleInfo.getName() + "%");
		}

		PageInfo<RoleInfo> pageInfo = new PageInfo<RoleInfo>(roleInfoMapper.selectByExample(example));

		JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(pageInfo));
		JSONArray array = jsonObject.getJSONArray("list");
		if (null != array && array.size() > 0) {
			for (int i = 0; i < array.size(); i++) {
				JSONObject roleJoson = array.getJSONObject(i);
				if (null != roleJoson) {
					roleJoson.put("wareHouseList",
							this.commonMapper.selectWareHouseByRoleId(roleJoson.getInteger("id")));
				}
			}
		}
		return jsonObject;
	}

	public List<RoleInfo> findList() {
		return roleInfoMapper.selectByExample(null);
	}

	public boolean findNameIsExist(Integer id, String name) {
		RoleInfoExample roleInfoExample = new RoleInfoExample();
		if (0 != id) {
			roleInfoExample.createCriteria().andIdNotEqualTo(id).andNameEqualTo(name);
		} else {
			roleInfoExample.createCriteria().andNameEqualTo(name);
		}
		return this.roleInfoMapper.countByExample(roleInfoExample) > 0;
	}
	
	@Transactional
	public void updateRoleWareHouse(int roleId, String[] warehouseIdArray) {

		// 删除之前分配的仓库信息
		RoleInfoWareHouseExample reInfoWareHouseExample = new RoleInfoWareHouseExample();
		reInfoWareHouseExample.createCriteria().andRoleIdEqualTo(roleId);
		this.roleInfoWareHouseMapper.deleteByExample(reInfoWareHouseExample);

		// 添加分配的仓库信息
		RoleInfoWareHouse roleInfoWareHouse = new RoleInfoWareHouse();
		roleInfoWareHouse.setRoleId(roleId);
		for (String warehouseId : warehouseIdArray) {
			roleInfoWareHouse.setWareHouseId(Integer.parseInt(warehouseId));
			this.roleInfoWareHouseMapper.insert(roleInfoWareHouse);
		}
	}
	
	@Transactional
	public void updateRoleRight(int roleId, String[] rightIdArray) {

		// 删除之前分配的权限信息
		RoleInfoAuthorityInfoExample example = new RoleInfoAuthorityInfoExample();
		example.createCriteria().andRoleIdEqualTo(roleId);
		this.roleInfoAuthorityInfoMapper.deleteByExample(example);

		// 添加分配的权限信息
		RoleInfoAuthorityInfo roleInfoAuthorityInfo = new RoleInfoAuthorityInfo();
		roleInfoAuthorityInfo.setRoleId(roleId);
		for (String rightId : rightIdArray) {
			roleInfoAuthorityInfo.setAuthorityId(Integer.parseInt(rightId));
			roleInfoAuthorityInfoMapper.insert(roleInfoAuthorityInfo);
		}
	}

	public List<WareHouse> selectWareHouseByRoleId(int roleId) {
		return this.commonMapper.selectWareHouseByRoleId(roleId);
	}

	public List<LoadTree> getRightTree(String roleId) {
		return this.commonMapper.getRightTree(roleId);
	}
}
