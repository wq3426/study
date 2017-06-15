package com.dhl.tools.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dhl.tools.domain.AuthorityInfo;
import com.dhl.tools.domain.AuthorityInfoExample;
import com.dhl.tools.mapper.AuthorityInfoMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * 权限业务处理
 * Created by liuso on 2017/4/12.
 */
@Service
public class AuthorityInfoService {

    @Autowired
    private AuthorityInfoMapper authorityInfoMapper;
    
    @Transactional
    public boolean add(AuthorityInfo authorityInfo) {
        int result = authorityInfoMapper.insert(authorityInfo);
        return result > 0;
    }
    
    @Transactional
    public boolean delete(Integer id) {
        int result = authorityInfoMapper.deleteByPrimaryKey(id);
        return result > 0;
    }
    
    @Transactional
    public boolean update(AuthorityInfo authorityInfo) {
        int result = authorityInfoMapper.updateByPrimaryKeySelective(authorityInfo);
        return result > 0;
    }

    public AuthorityInfo findById(Integer id) {
        return authorityInfoMapper.selectByPrimaryKey(id);
    }

    public PageInfo<AuthorityInfo> findPage(AuthorityInfo authorityInfo, Integer page, Integer size) {
        //获取第1页，10条内容，默认查询总数count
        PageHelper.startPage(page == null ? 1 : page, size == null ? 10 : size);

        AuthorityInfoExample example = new AuthorityInfoExample();

        List<AuthorityInfo> list = authorityInfoMapper.selectByExample(example);
        //用PageInfo对结果进行包装
        return new PageInfo<>(list);
    }

    public List<AuthorityInfo> findList() {
        return authorityInfoMapper.selectByExample(null);
    }
}
