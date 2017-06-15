package com.dhl.tools.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dhl.tools.domain.*;
import com.dhl.tools.mapper.CommonMapper;
import com.dhl.tools.mapper.UserInfoMapper;
import com.dhl.tools.mapper.UserInfoRoleInfoMapper;
import com.dhl.tools.mapper.WareHouseMapper;
import com.dhl.tools.security.MyUserDetails;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户业务处理 Created by liuso on 2017/4/12.
 */
@Slf4j
@Service
public class UserInfoService {

	@Autowired
	private UserInfoMapper userInfoMapper;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private UserInfoRoleInfoMapper userInfoRoleInfoMapper;

	@Autowired
	private CommonMapper commonMapper;

	@Autowired
	private WareHouseMapper wareHouseMapper;

	@Transactional
	public boolean add(UserInfo userInfo) {
		// 校验账号
		if (StringUtils.isEmpty(userInfo.getUsername())) {
			log.info("添加用户失败，账号为空");
			return false;
		}

		// 校验密码
		if (StringUtils.isEmpty(userInfo.getPassword())) {
			log.info("添加用户失败，密码为空");
			return false;
		}

		// 加密密码
		String password = passwordEncoder.encode(userInfo.getPassword());
		userInfo.setPassword(password);
		int result = userInfoMapper.insert(userInfo);
		return result > 0;
	}

	@Transactional
	public boolean delete(Integer id) {
		UserInfoRoleInfoExample example = new UserInfoRoleInfoExample();
		example.createCriteria().andUserIdEqualTo(id);
		this.userInfoRoleInfoMapper.deleteByExample(example);

		int result = userInfoMapper.deleteByPrimaryKey(id);
		return result > 0;
	}

	@Transactional
	public boolean update(UserInfo userInfo) {
		userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
		int result = userInfoMapper.updateByPrimaryKeySelective(userInfo);
		return result > 0;
	}

	public UserInfo findById(Integer id) {
		return userInfoMapper.selectByPrimaryKey(id);
	}

	public JSONObject findPage(UserInfo userInfo, Integer page, Integer size) {
		// 获取第1页，10条内容，默认查询总数count
		PageHelper.startPage(page == null ? 1 : page, size == null ? 10 : size);

		UserInfoExample example = new UserInfoExample();
		example.setOrderByClause("createDate desc");
		if (StringUtils.isNotEmpty(userInfo.getUsername())) {
			example.createCriteria().andUsernameLike("%" + userInfo.getUsername() + "%");
		}

		PageInfo<UserInfo> pageInfo = new PageInfo<>(userInfoMapper.selectByExample(example));

		JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(pageInfo));
		JSONArray array = jsonObject.getJSONArray("list");
		if (null != array && array.size() > 0) {
			for (int i = 0; i < array.size(); i++) {
				JSONObject userJoson = array.getJSONObject(i);
				if (null != userJoson) {
					userJoson.put("roleInfoList", this.commonMapper.selectRoleInfoByUserId(userJoson.getInteger("id")));
				}
			}
		}
		return jsonObject;
	}

	public List<UserInfo> findList() {
		return userInfoMapper.selectByExample(null);
	}

	/**
	 * 判断用户工号是否存在
	 *
	 */
	public boolean findUserNameIsExist(Integer id, String userName) {
		UserInfoExample userInfoExample = new UserInfoExample();
		if (0 != id) {
			userInfoExample.createCriteria().andIdNotEqualTo(id).andUsernameEqualTo(userName);
		} else {
			userInfoExample.createCriteria().andUsernameEqualTo(userName);
		}
		return this.userInfoMapper.countByExample(userInfoExample) > 0;
	}

	public List<RoleInfo> selectRoleInfoByUserId(int userId) {
		return this.commonMapper.selectRoleInfoByUserId(userId);
	}

	@Transactional
	public void addUserRole(int userId, String[] roleIdArray) {

		// 删除之前分配的角色信息
		UserInfoRoleInfoExample userInfoRoleInfoExample = new UserInfoRoleInfoExample();
		userInfoRoleInfoExample.createCriteria().andUserIdEqualTo(userId);
		this.userInfoRoleInfoMapper.deleteByExample(userInfoRoleInfoExample);

		// 添加分配的角色信息
		UserInfoRoleInfo userInfoRoleInfo = new UserInfoRoleInfo();
		userInfoRoleInfo.setUserId(userId);
		for (String roleId : roleIdArray) {
			userInfoRoleInfo.setRoleId(Integer.parseInt(roleId));
			this.userInfoRoleInfoMapper.insert(userInfoRoleInfo);
		}
	}

	/**
	 * 更换登录用户当前操作仓库
	 *
	 * @param currentUser
	 *            登录用户
	 * @param code
	 *            仓库编号
	 * @return
	 */
	public boolean changeWareHouse(MyUserDetails currentUser, String code) {
		if (!currentUser.getCurrentWareHouseCode().equals(code)) {
			WareHouseExample wareHouseExample = new WareHouseExample();
			wareHouseExample.createCriteria().andCodeEqualTo(code);

			List<WareHouse> list = this.wareHouseMapper.selectByExample(wareHouseExample);
			if (CollectionUtils.isNotEmpty(list)) {
				currentUser.setCurrentWareHouse(list.get(0));
			}
			currentUser.setCurrentWareHouseCode(code);
		}
		return true;
	}
}
