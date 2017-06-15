package com.dhl.tools.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.dhl.tools.domain.AuthorityInfo;
import com.dhl.tools.domain.UserInfo;
import com.dhl.tools.domain.WareHouse;
import com.dhl.tools.domain.WareHouseType;

/**
 * 用户权限信息 Created by liuso on 2017/4/15.
 */
public class MyUserDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8058799520703913300L;

	/**
	 * 用户信息
	 */
	private UserInfo userInfo;

	/**
	 * 当前仓库编号
	 */
	private String currentWareHouseCode;

	/**
	 * 当前仓库
	 */
	private WareHouse currentWareHouse;

	/**
	 * 所属仓库
	 */
	private List<WareHouse> wareHouseList;

	/**
	 * 权限列表
	 */
	private List<AuthorityInfo> authorityInfoList;

	/**
	 * 仓库级别配置信息
	 */
	private List<WareHouseType> wareHouseTypeList;

	public MyUserDetails(UserInfo userInfo, List<WareHouse> wareHouseList, List<AuthorityInfo> authorityInfoList,
			List<WareHouseType> wareHouseTypeList) {
		this.userInfo = userInfo;
		this.wareHouseList = wareHouseList;
		this.authorityInfoList = authorityInfoList;
		this.currentWareHouseCode = wareHouseList.get(0).getCode();
		this.currentWareHouse = wareHouseList.get(0);
		this.wareHouseTypeList = wareHouseTypeList;
	}

	public String getCurrentWareHouseCode() {
		return currentWareHouseCode;
	}

	public void setCurrentWareHouseCode(String currentWareHouseCode) {
		this.currentWareHouseCode = currentWareHouseCode;
	}

	public WareHouse getCurrentWareHouse() {
		return currentWareHouse;
	}

	public void setCurrentWareHouse(WareHouse currentWareHouse) {
		this.currentWareHouse = currentWareHouse;
	}

	public List<WareHouse> getWareHouseList() {
		return wareHouseList;
	}

	public void setWareHouseList(List<WareHouse> wareHouseList) {
		this.wareHouseList = wareHouseList;
	}

	public List<WareHouseType> getWareHouseTypeList() {
		return wareHouseTypeList;
	}

	public void setWareHouseTypeList(List<WareHouseType> wareHouseTypeList) {
		this.wareHouseTypeList = wareHouseTypeList;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<String> roleList = new HashSet<>();
		for (AuthorityInfo authorityInfo : this.authorityInfoList) {
			roleList.add(authorityInfo.getCode());
		}
		return AuthorityUtils.createAuthorityList(roleList.toArray(new String[] {}));
	}

	@Override
	public String getPassword() {
		return this.userInfo.getPassword();
	}

	@Override
	public String getUsername() {
		return this.userInfo.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}
