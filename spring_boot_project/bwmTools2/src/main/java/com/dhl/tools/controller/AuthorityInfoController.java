package com.dhl.tools.controller;

import com.dhl.tools.domain.AuthorityInfo;
import com.dhl.tools.service.AuthorityInfoService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限
 * Created by liuso on 2017/4/12.
 */
@RestController
@RequestMapping("authority")
public class AuthorityInfoController {

    @Autowired
    private AuthorityInfoService authorityInfoService;

    /**
     * 添加权限
     *
     * @param authorityInfo 权限信息
     * @return
     */
    @PutMapping
    public boolean insert(AuthorityInfo authorityInfo) {
        return this.authorityInfoService.add(authorityInfo);
    }

    /**
     * 根据主键删除
     *
     * @param id 主键
     */
    @DeleteMapping("{id}")
    public boolean remove(@PathVariable Integer id) {
        return this.authorityInfoService.delete(id);
    }

    /**
     * 更新权限
     *
     * @param authorityInfo 权限信息
     * @return
     */
    @PatchMapping
    public boolean update(AuthorityInfo authorityInfo) {
        return this.authorityInfoService.update(authorityInfo);
    }

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return
     */
    @GetMapping("{id}")
    public AuthorityInfo findById(@PathVariable Integer id) {
        return this.authorityInfoService.findById(id);
    }

    /**
     * 分页查询
     *
     * @param authorityInfo 权限
     * @param page          页码 从1开始
     * @param size          数量
     * @return
     */
    @GetMapping
    public PageInfo<AuthorityInfo> findPage(AuthorityInfo authorityInfo, Integer page, Integer size) {
        return this.authorityInfoService.findPage(authorityInfo, page, size);
    }

    /**
     * 查询所有
     *
     * @return
     */
    @GetMapping("list")
    public List<AuthorityInfo> findAll() {
        return this.authorityInfoService.findList();
    }
}
