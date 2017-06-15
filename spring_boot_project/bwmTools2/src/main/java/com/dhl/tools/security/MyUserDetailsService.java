package com.dhl.tools.security;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dhl.tools.domain.AuthorityInfo;
import com.dhl.tools.domain.AuthorityInfoExample;
import com.dhl.tools.domain.RoleInfoAuthorityInfo;
import com.dhl.tools.domain.RoleInfoAuthorityInfoExample;
import com.dhl.tools.domain.RoleInfoWareHouse;
import com.dhl.tools.domain.RoleInfoWareHouseExample;
import com.dhl.tools.domain.UserInfo;
import com.dhl.tools.domain.UserInfoExample;
import com.dhl.tools.domain.UserInfoRoleInfo;
import com.dhl.tools.domain.UserInfoRoleInfoExample;
import com.dhl.tools.domain.WareHouse;
import com.dhl.tools.domain.WareHouseExample;
import com.dhl.tools.domain.WareHouseTypeExample;
import com.dhl.tools.mapper.AuthorityInfoMapper;
import com.dhl.tools.mapper.RoleInfoAuthorityInfoMapper;
import com.dhl.tools.mapper.RoleInfoWareHouseMapper;
import com.dhl.tools.mapper.UserInfoMapper;
import com.dhl.tools.mapper.UserInfoRoleInfoMapper;
import com.dhl.tools.mapper.WareHouseMapper;
import com.dhl.tools.mapper.WareHouseTypeMapper;

/**
 * Created by liuso on 2017/4/15.
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	private UserInfoMapper userInfoMapper;

	@Autowired
	private WareHouseMapper wareHouseMapper;

	@Autowired
	private AuthorityInfoMapper authorityInfoMapper;

	@Autowired
	private UserInfoRoleInfoMapper userInfoRoleInfoMapper;

	@Autowired
	private RoleInfoWareHouseMapper roleInfoWareHouseMapper;

	@Autowired
	private RoleInfoAuthorityInfoMapper roleInfoAuthorityInfoMapper;

	@Autowired
	private WareHouseTypeMapper wareHouseTypeMapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserInfoExample example = new UserInfoExample();
		example.createCriteria().andUsernameEqualTo(username);
		List<UserInfo> list = this.userInfoMapper.selectByExample(example);
		if (null != list && list.size() > 0) {
			UserInfo userInfo = list.get(0);
			if (null != userInfo) {
				// 获取用户所属角色
				UserInfoRoleInfoExample userInfoRoleInfoExample = new UserInfoRoleInfoExample();
				userInfoRoleInfoExample.createCriteria().andUserIdEqualTo(userInfo.getId());
				List<UserInfoRoleInfo> userInfoRoleInfoList = userInfoRoleInfoMapper
						.selectByExample(userInfoRoleInfoExample);
				if (null != userInfoRoleInfoList && userInfoRoleInfoList.size() > 0) {
					List<AuthorityInfo> authorityInfoList = new ArrayList<>();
					Set<Integer> wareHouseIdSet = new HashSet<Integer>();
					for (UserInfoRoleInfo userInfoRoleInfo : userInfoRoleInfoList) {
						if (null != userInfoRoleInfo && null != userInfoRoleInfo.getRoleId()) {
							// 获取角色所属仓库
							RoleInfoWareHouseExample roleInfoWareHouseExample = new RoleInfoWareHouseExample();
							roleInfoWareHouseExample.createCriteria().andRoleIdEqualTo(userInfoRoleInfo.getRoleId());
							List<RoleInfoWareHouse> roleInfoWareHouseList = this.roleInfoWareHouseMapper
									.selectByExample(roleInfoWareHouseExample);
							if (null != roleInfoWareHouseList && roleInfoWareHouseList.size() > 0) {
								for (RoleInfoWareHouse roleInfoWareHouse : roleInfoWareHouseList) {
									if (null != roleInfoWareHouse && null != roleInfoWareHouse.getWareHouseId()) {
										wareHouseIdSet.add(roleInfoWareHouse.getWareHouseId());
									}
								}
							}

							// 获取角色所属权限
							RoleInfoAuthorityInfoExample roleInfoAuthorityInfoExample = new RoleInfoAuthorityInfoExample();
							roleInfoAuthorityInfoExample.createCriteria()
									.andRoleIdEqualTo(userInfoRoleInfo.getRoleId());
							List<RoleInfoAuthorityInfo> roleInfoAuthorityInfoList = this.roleInfoAuthorityInfoMapper
									.selectByExample(roleInfoAuthorityInfoExample);
							if (null != roleInfoAuthorityInfoList && roleInfoAuthorityInfoList.size() > 0) {
								List<Integer> authorityIdList = new ArrayList<>();
								for (RoleInfoAuthorityInfo roleInfoAuthorityInfo : roleInfoAuthorityInfoList) {
									if (null != roleInfoAuthorityInfo
											&& null != roleInfoAuthorityInfo.getAuthorityId()) {
										authorityIdList.add(roleInfoAuthorityInfo.getAuthorityId());
									}
								}

								if (authorityIdList.size() > 0) {
									AuthorityInfoExample authorityInfoExample = new AuthorityInfoExample();
									authorityInfoExample.createCriteria().andIdIn(authorityIdList);
									List<AuthorityInfo> tempAuthorityList = this.authorityInfoMapper
											.selectByExample(authorityInfoExample);
									if (null != tempAuthorityList && tempAuthorityList.size() > 0) {
										authorityInfoList.addAll(tempAuthorityList);
									}
								}
							}
						}
					}
					List<WareHouse> wareHouseList = new ArrayList<WareHouse>();
					if (!wareHouseIdSet.isEmpty()) {
						WareHouseExample wareHouseExample = new WareHouseExample();
						wareHouseExample.setOrderByClause("createDate desc");
						wareHouseExample.createCriteria().andIdIn(new ArrayList<Integer>(wareHouseIdSet));
						wareHouseList = this.wareHouseMapper.selectByExample(wareHouseExample);
					}

					WareHouseTypeExample wareHouseTypeExample = new WareHouseTypeExample();
					wareHouseTypeExample.setOrderByClause("rank asc");

					return new MyUserDetails(userInfo, wareHouseList, authorityInfoList,
							this.wareHouseTypeMapper.selectByExample(wareHouseTypeExample));
				}
			}
		}
		return null;
	}
}
