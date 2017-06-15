package com.enation.eop.resource.impl;

import java.util.List;

import com.enation.eop.resource.IAppManager;
import com.enation.eop.resource.model.EopApp;
import com.enation.framework.database.IDaoSupport;

/**
 * @Description 应用管理
 *
 * @createTime 2016年9月7日 下午1:12:35
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public class AppManagerImpl implements IAppManager {

	private IDaoSupport<EopApp> daoSupport;

	@Override
	public EopApp get(String appid) {
		String sql = "select * from eop_app where id=?";
		return this.daoSupport.queryForObject(sql, EopApp.class, appid);
	}

	@Override
	public List<EopApp> list() {
		String sql = "select * from eop_app";
		return this.daoSupport.queryForList(sql, EopApp.class);
	}

	@Override
	public void add(EopApp app) {
		this.daoSupport.insert("eop_app", app);
	}
	
	
	
	
	/*
	 * -------------------------------------------------------------------
	 * GETTER AND SETTER
	 */
	public IDaoSupport<EopApp> getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport<EopApp> daoSupport) {
		this.daoSupport = daoSupport;
	}

}
