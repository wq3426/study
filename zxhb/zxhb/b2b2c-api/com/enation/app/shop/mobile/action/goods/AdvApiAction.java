/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：广告api  
 * 修改人：Sylow  
 * 修改时间：2015-08-22
 * 修改内容：增加获得广告列表api
 */
package com.enation.app.shop.mobile.action.goods;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.AdColumn;
import com.enation.app.base.core.model.Adv;
import com.enation.app.base.core.service.IAdColumnManager;
import com.enation.app.base.core.service.IAdvManager;
import com.enation.app.shop.core.model.CarInfo;
import com.enation.app.shop.core.service.ICarInfoManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 广告位api
 * 提供广告相关api
 * @author Sylow
 * @version v1.0
 * @since v1.0
 */
@SuppressWarnings("serial")
@Component("mobileAdvApiAction")
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("adv")
public class AdvApiAction extends WWAction {

	private IAdvManager advManager;
	private ICarInfoManager  carInfoManager;
	private String carplate;
	private long advid;

	/**
	 * @param acid
	 *            广告位id
	 * @return Map广告信息数据，其中key结构为 adDetails:广告位详细信息 {@link AdColumn}
	 *         advList:广告列表 {@link Adv}
	 */
	@SuppressWarnings("unchecked")
	public String advList() {
		Map<String, Object> data = new HashMap<String,Object>();
		List<Adv> advList = null;
		int storeId ;
		try {
			List<CarInfo> carInfoList=carInfoManager.getCarInfoByCarplate(carplate);
			if(carInfoList.size()>0){
				net.sf.json.JSONObject carInfo = net.sf.json.JSONObject.fromObject(carInfoList.get(0));
				storeId = carInfo.getInt("repair4sstoreid");
				advList=advManager.getStoreAdvListForMobile(storeId);
			}else{
				advList=advManager.getStoreAdvListForMobile(1);
			}
			data.put("advList", advList);// 广告列表
			this.json = JsonMessageUtil.getObjectJson(data);
		} catch (RuntimeException e) {
			if (this.logger.isDebugEnabled()) {
				this.logger.error(e.getStackTrace());
			}
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 根据某个广告id获取广告信息
	 * 
	 * @param advid
	 * 
	 * @return result:1调用成功 0调用失败 data: Adv对象json
	 * 
	 */
	public String getOneAdv() {

		try {

			Adv adv = advManager.getAdvDetail(advid);
			this.json = JsonMessageUtil.getObjectJson(adv);

		} catch (Exception e) {
			this.logger.error("获取某个广告出错", e);
			this.showErrorJson(e.getMessage());
		}

		return WWAction.JSON_MESSAGE;
	}
	
	public IAdvManager getAdvManager() {
		return advManager;
	}

	public void setAdvManager(IAdvManager advManager) {
		this.advManager = advManager;
	}

	public long getAdvid() {
		return advid;
	}

	public void setAdvid(long advid) {
		this.advid = advid;
	}

	public ICarInfoManager getCarInfoManager() {
		return carInfoManager;
	}

	public void setCarInfoManager(ICarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}

	public String getCarplate() {
		return carplate;
	}

	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}
}
