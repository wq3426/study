package com.dhl.tools.domain;

/**
 * 权限类型
 * Created by liuso on 2017/4/11.
 */
public enum AuthorityType {

    // 创建仓库
    WAREHOUSE_CREATE,
    // 删除仓库
    WAREHOUSE_DELETE,
    // 更新仓库
    WAREHOUSE_UPDATE,
    // 查询仓库
    WAREHOUSE_QUERY,

    // 创建货位类型
    CARGO_LOCATION_TYPE_CREATE,
    // 删除货位类型
    CARGO_LOCATION_TYPE_DELETE,
    // 更新货位类型
    CARGO_LOCATION_TYPE_UPDATE,
    // 查询货位类型
    CARGO_LOCATION_TYPE_QUERY,

    // 创建货位
    CARGO_LOCATION_CREATE,
    // 删除货位
    CARGO_LOCATION_DELETE,
    // 更新货位
    CARGO_LOCATION_UPDATE,
    // 查询货位
    CARGO_LOCATION_QUERY,

    // 创建物料
    MATERIAL_CREATE,
    // 删除物料
    MATERIAL_DELETE,
    // 更新物料
    MATERIAL_UPDATE,
    // 查询物料
    MATERIAL_QUERY,

    // 创建用户
    USER_CREATE,
    // 删除用户
    USER_DELETE,
    // 更新用户
    USER_UPDATE,
    // 查询用户
    USER_QUERY,

    // 创建角色
    ROLE_CREATE,
    // 删除角色
    ROLE_DELETE,
    // 更新角色
    ROLE_UPDATE,
    // 查询角色
    ROLE_QUERY
}
