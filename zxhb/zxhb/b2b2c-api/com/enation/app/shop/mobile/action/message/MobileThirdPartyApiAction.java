/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：购物车api  
 * 修改人：  wanghongjun
 * 修改时间：2015-08-31
 * 修改内容：增加商家信息、商家商品等。
 */
package com.enation.app.shop.mobile.action.message;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.TrafficRestriction;
import com.enation.app.shop.core.service.ICarInfoManager;
import com.enation.eop.processor.MobileMessageHttpSend;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.StringUtil;

/**
 * 商家api
 * 
 * @author wanghongjun
 * @version v1.1 2015-08-31
 * @since v1.0
 */
@SuppressWarnings("serial")
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("thirdparty")
public class MobileThirdPartyApiAction extends WWAction {
	
	private ICarInfoManager carInfoManager;
	
	/** @description 返回限行数据
	 * @date 2016年9月26日 下午8:26:19
	 * @return
	 * @return String
	 */
	public String  showTrafficRestriction(){
		try{
			HttpServletRequest request = this.getRequest();
			String city = request.getParameter("city");
			Date nowDate = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String limit_date = dateFormat.format(nowDate);
			TrafficRestriction trafficRestriction = carInfoManager.getTrafficRestriction(city,limit_date);
			if(trafficRestriction!=null){
				json = trafficRestriction.getResult();
			}else{
				String url  = "http://v.juhe.cn/xianxing/index";
				String key = "6ffeca20d90e2835c4e5ffaaead7b32e";
				String param = "key="+key+"&city="+city;
				String result = MobileMessageHttpSend.getSend(url, param);
				trafficRestriction  = new TrafficRestriction();
				trafficRestriction.setCity(city);
				trafficRestriction.setLimit_date(limit_date);
				trafficRestriction.setResult(result);
				carInfoManager.addTrafficRestriction(trafficRestriction);
				json = result;
			}
		}catch(Exception e){
			e.printStackTrace();
			showErrorJson("获取限行数据失败");
		}
		return JSON_MESSAGE;
	}
	
	public String  trafficRestrictionCityList(){
		try{
			String path = StringUtil.getRootPath("/config/apiutils.properties");
			Properties props = new Properties();
			//创建写入
				InputStream in  = new FileInputStream( new File(path));
				props.load(in);
				json = (String)props.get("cityLimitList");
		}catch(Exception e){
			e.printStackTrace();
			showErrorJson("获取限行城市列表失败");
		}
			return JSON_MESSAGE;
	}
	
	
	public ICarInfoManager getCarInfoManager() {
		return carInfoManager;
	}

	public void setCarInfoManager(ICarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}
	
	
}
