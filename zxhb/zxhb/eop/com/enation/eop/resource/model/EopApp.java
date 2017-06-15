package com.enation.eop.resource.model;

import java.io.Serializable;
import java.util.List;

import com.enation.framework.database.NotDbField;
import com.enation.framework.database.PrimaryKeyField;

/**
 * @Description 
 *
 * @createTime 2016年9月7日 下午1:04:09
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public class EopApp implements Serializable {

	private static final long serialVersionUID = 9088172178237139695L;

	private Integer id;
	private String appid;
	private String app_name;
	private String author;
	private String descript;
	private int deployment;  	// 0:本地；1：远程
	private String path;    	// 对本地是目录，对远程是地址
	private String installuri; 	// 安装地址
	private String version;
	private List<String> updateLogList;// 应用的更新日志项
	private String authorizationcode; // 授权码
	
	
	
	/*
	 * -----------------------------------------------------------
	 * GETTER AND SETTER
	 */
	public String getAppid() {
		return appid;
	}
	
	public void setAppid(String appid) {
		this.appid = appid;
	}

	@PrimaryKeyField
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String appName) {
		app_name = appName;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getDescript() {
		return descript;
	}

	public void setDescript(String descript) {
		this.descript = descript;
	}

	public int getDeployment() {
		return deployment;
	}

	public void setDeployment(int deployment) {
		this.deployment = deployment;
	}

	public String getAuthorizationcode() {
		return authorizationcode;
	}
	
	public void setAuthorizationcode(String authorizationcode) {
		this.authorizationcode = authorizationcode;
	}

	public String getInstalluri() {
		return installuri;
	}

	public void setInstalluri(String installuri) {
		this.installuri = installuri;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@NotDbField
	public List<String> getUpdateLogList() {
		return updateLogList;
	}

	public void setUpdateLogList(List<String> updateLogList) {
		this.updateLogList = updateLogList;
	}

}
