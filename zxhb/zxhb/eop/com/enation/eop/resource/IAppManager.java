package com.enation.eop.resource;

import java.util.List;

import com.enation.eop.resource.model.EopApp;

/**
 * @Description 应用管理
 *
 * @createTime 2016年9月7日 下午1:06:59
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public interface IAppManager {

	/**
	 * @description 添加一个应用
	 * @date 2016年9月7日 下午1:07:33
	 * @param app
	 */
	public void add(EopApp app);

	/**
	 * @description 获取所有应用列表
	 * @date 2016年9月7日 下午1:07:43
	 * @return List<EopApp>
	 */
	public List<EopApp> list();

	/**
	 * @description 获取某个应用
	 * @date 2016年9月7日 下午1:07:53
	 * @param appid
	 * @return EopApp
	 */
	public EopApp get(String appid);

}
