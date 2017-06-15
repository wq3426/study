/**
 * 版权：Copyright (C) 2016  中安信博（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：设备api  
 * 修改人：
 * 修改时间：2016-03-14
 * 修改内容: 
 */
package com.enation.app.shop.core.action.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.CarInfoVin;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.CarModel;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.service.ICarInfoManager;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.utils.RegularExpressionUtils;
import com.enation.eop.processor.CarVinContent;
import com.enation.eop.processor.MobileMessageHttpSend;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.ValidateUtils;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

import cn.jiguang.commom.utils.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/**
 * 车辆信息api
 * 
 * 实体类：CarInfo
 * @author 
 * @date 2016-04-4
 * @version v0.1
 * @since 
 */
//curl -H 'Content-Type:application/json' -X PUT -d '[{"timestamp":"20160311"}, {"carNo": "888888"}]' 'http://localhost:8080/api/mobile/device!updateJSONArray.do'

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("carinfo")
@SuppressWarnings("serial")
public class CarInfoApiAction extends WWAction {
	private ICarInfoManager carInfoManager;
	private IProductManager productManager ;
	private ICartManager cartManager;
	/**
	 * 获取预计保费及最大收益
	 * @return
	 */
	public String getEstimatedInsureFeeAndMaxGain(){
		String json = "";
		try {
			HttpServletRequest request = getRequest();
			int carModelID = Integer.parseInt(request.getParameter("carmodelid"));
			String carUseCharacter = request.getParameter("carusecharacter");
			long carFirstBuyTime = Long.parseLong(request.getParameter("carfirstbuytime"));
			String InsureSet = request.getParameter("insuresetid");
			json = carInfoManager.getEstimatedInsureFeeAndMaxGain(carModelID, carUseCharacter, carFirstBuyTime, InsureSet);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.json = "{\"result\":1,\"data\":"+ json + "}";
		return JSON_MESSAGE;
	}
	
	/**
	 * 根据设备列表，返回未绑定的设备列表
	 * 入参：{"obds":["54:4A:16:35:68:9E"],"carowner":"18601970740"}
	 * @return
	 */
	public String getUnbondedObds(){
		try {
			List<String> obdList = new ArrayList<String>();

			String obds = null;
			try {
				obds = getBody(getRequest());
				this.logger.debug("obds: " + obds);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (obds != null) {
				org.json.JSONObject tempObj = new org.json.JSONObject(obds);
				String carowner = tempObj.getString("carowner");
				org.json.JSONArray jArray = tempObj.getJSONArray("obds");
				for(int i = 0; i < jArray.length(); i++) {
					obdList.add(((String)jArray.get(i)).toUpperCase());
				}
				List bondedList = carInfoManager.queryBondedObds(obdList, carowner);
				JSONArray array = JSONArray.fromObject(bondedList);
				for(int j = 0; j < array.size(); j++){
					net.sf.json.JSONObject obj = array.getJSONObject(j);
					String addr = obj.getString("obdmacaddr");
					for(int i = 0; i < obdList.size(); i++) {
						if(obdList.get(i).equalsIgnoreCase(addr)){
							obdList.remove(i);
						}
					}
				}
			}
			this.json = "{\"result\":1,\"data\":"+JSONArray.fromObject(obdList).toString()+"}";
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 获取指定用户下的车辆列表信息
	 * @return
	 */
	public String getCarInfoList(){
		try {
			String carOwner = getRequest().getParameter("carowner");
			JSONArray carInfoArray = carInfoManager.getCarInfoList(carOwner);
			Member member = UserConext.getCurrentMember();
			if(carInfoArray != null){
				for(int i=0; i<carInfoArray.size(); i++){
					JSONObject obj = carInfoArray.getJSONObject(i);
					Integer cartCount = cartManager.countItemNum(obj.getString("carplate"), member.getMember_id(), null);
					obj.put("cartCount", cartCount);//用户购物车数量
				}
				this.json = "{\"result\":1,\"data\":"+carInfoArray.toString()+"}";
			} else {
				this.json = "{\"result\":0,\"message\":"+"该用户名下没有车辆"+"}";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 根据车牌号获取车辆信息
	 * @return
	 */
	public String getCarInfoByCarplate(){
		try {
			String carplate = getRequest().getParameter("carplate");
			carplate = new String(carplate.getBytes("iso-8859-1"), "utf-8");
			JSONArray array = carInfoManager.getCarInfoMobile(carplate.toUpperCase());
			Member member = UserConext.getCurrentMember();
			if(array.size() > 0){
				JSONObject obj = array.getJSONObject(0);
				Integer cartCount = cartManager.countItemNum(obj.getString("carplate"), member.getMember_id(), null);
				obj.put("cartCount", cartCount);//用户购物车数量
				this.json = "{\"result\":1,\"data\":"+obj.toString()+"}";
			}else {
				this.json = "{\"result\":0,\"message\":"+"该车辆不存在"+"}";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 获取车辆的obdmacaddr
	 * @return
	 */
	public String getCarObdMacAddr(){
		try {
			String carplate = getRequest().getParameter("carplate");
			List list = carInfoManager.getCarInfoByCarplate(carplate);
			if(list.size() > 0){
				JSONObject obj = JSONObject.fromObject(list.get(0));
				String obdmacAddr = obj.getString("obdmacaddr").trim();
				if(!"".equals(obdmacAddr)){
					JSONObject returnObj = new JSONObject();
					returnObj.put("obdmacaddr", obdmacAddr);
					this.json = "{\"result\":1,\"data\":"+returnObj.toString()+"}";
				}else{
					this.json = "{\"result\":0,\"message\":"+"该车辆还未绑定obd"+"}";
				}
			}else {
				this.json = "{\"result\":0,\"message\":"+"该车辆还未绑定obd"+"}";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 增加或者更新车辆信息
	 * @return
	 */
	public String updateCarInfo(){
		String carInfo = new String();
		try {
			HttpServletRequest request = getRequest();
			try{
				carInfo = getBody(request);
				this.logger.error("updateCarInfo:" + carInfo);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			JSONArray carInfoArray = carInfoManager.updateCarInfo(carInfo);
			if(carInfoArray.size() > 0){
				Member member = UserConext.getCurrentMember();
				for(int i=0; i< carInfoArray.size(); i++){
					JSONObject obj = carInfoArray.getJSONObject(i);
					Integer cartCount = cartManager.countItemNum(obj.getString("carplate"), member.getMember_id(), null);
					obj.put("cartCount", cartCount);//用户购物车数量
				}
				this.json = "{\"result\":1,\"data\":"+carInfoArray.toString()+"}";
			} else {
				this.json = "{\"result\":0,\"message\":"+"添加/修改失败"+"}";
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("updateCarInfo has errors", e);
			this.showErrorJson(e.getMessage());
		}
	
		return JSON_MESSAGE;
	}
	
	/**
	 * 根据车牌号获取保险订单的product_id
	 * 入参：
	 * 	carplate 车牌号
	 *  type 1 保险 2 保养
	 * @return
	 */
	public String getProductIdOfInsureOrRepair4s(){
		String carPlate = getRequest().getParameter("carplate");
		String type = getRequest().getParameter("type");
		try {
			Product product = productManager.getProductByCarplateAndType(carPlate, type);
			if(product != null){
				JSONObject obj = new JSONObject();
				obj.put("product_id", product.getProduct_id());
				this.json = "{\"result\":1,\"data\":"+obj.toString()+"}";
			} else {
				this.json = "{\"result\":0,\"message\":"+"没有获取到保险或保养套餐的product_id"+"}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("getProductIdOfInsureOrRepair4s has errors", e);
			this.showErrorJson(e.getMessage());
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 变更车辆的保险套餐、保养套餐
	 * @return
	 */
	public String updateInsureAndRepair4sInfo(){
		String carPlate = getRequest().getParameter("carplate");
		String insuranceId = getRequest().getParameter("insuresetid");
		String repair4sStoreId = getRequest().getParameter("repair4sstoreid");
		String insureestimatedfee = getRequest().getParameter("insureestimatedfee");
		String insureestimatedmaxgain = getRequest().getParameter("insureestimatedmaxgain");
		try {
			JSONObject obj = carInfoManager.updateInsureAndRepair4sInfo(carPlate, insuranceId, repair4sStoreId, insureestimatedfee, insureestimatedmaxgain);
			if(obj != null){
				this.json = "{\"result\":1,\"data\":"+obj.toString()+"}";
			} else {
				this.json = "{\"result\":0,\"message\":\"变更套餐失败\"}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("updateInsurceAndRepair4sInfo has errors", e);
			this.showErrorJson(e.getMessage());
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 更改签约4s店
	 * @date 2016年9月27日 下午3:28:13
	 * @return
	 */
	public String updateContractRepair4sStoreInfo(){
		try {
			JSONObject obj = new JSONObject();
			
			String carplate = getRequest().getParameter("carplate");
			String repair4sstoreid = getRequest().getParameter("repair4sstoreid");
			String repairlastmile = getRequest().getParameter("repairlastmile");
			String repairlasttime = getRequest().getParameter("repairlasttime");
			
			String msg = "";
			if("".equals(carplate) || "".equals(repair4sstoreid)){
				msg = "车牌号或签约4s店id为空，请检查";
			}
			if("".equals(repairlastmile)){
				msg = "上次保养里程为空，请填写上次保养里程";
			}
			
			if(!"".equals(msg)){
				obj.put("result", 1);
				obj.put("message", msg);
				
				this.json = obj.toString();
			}else{
				obj = carInfoManager.updateContractRepair4sStoreInfo(carplate, repair4sstoreid, repairlastmile, repairlasttime);
				
				this.json = obj.toString();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，获取数据失败,错误信息："+e.getMessage());
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * web后台：解除车辆的obd绑定
	 * @return
	 */
	public String unboundObdByCarplate(){
		String car_id = getRequest().getParameter("car_id");
		try {
			boolean flag = carInfoManager.unboundObdByCarplate(car_id);
			if(flag){
				this.json = "{\"result\":1,\"message\": \"车辆解绑obd成功\" }";
			} else {
				this.json = "{\"result\":0,\"message\": \"车辆解绑obd失败\" }";
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("unboundObdByCarplate has errors", e);
			this.showErrorJson(e.getMessage());
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 根据车牌号更新车辆保养信息
	 * 入参：
	 * 	carplate 车牌号
	 *  repairlastmile 上次保养里程
	 *  repairlasttime 上次保养时间
	 *  repairlastprice 上次保养价格
	 *  repair_source 上次保养地点
	 * @return
	 */
	public String updateCarRepairInfo(){
		String carplate = getRequest().getParameter("carplate");
		String repairlastmile = getRequest().getParameter("repairlastmile");
		String repairlasttime = getRequest().getParameter("repairlasttime");
		String repairlastprice = getRequest().getParameter("repairlastprice");
		String repair_source = getRequest().getParameter("repair_source");
		try {
			boolean flag = carInfoManager.updateCarRepairInfo(carplate, repairlastmile, repairlasttime, repairlastprice, repair_source);
			if(flag){
				this.json = "{\"result\":1,\"message\":\"更新成功\"}";
			} else {
				this.json = "{\"result\":0,\"message\":\"更新失败\"}";
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("updateCarRepairInfo has errors", e);
			this.showErrorJson(e.getMessage());
		}
		return JSON_MESSAGE;
	}
	
	/***
	 * @description 车型保养手册
	 * @date 2016年9月2日 下午8:53:00
	 * @return
	 */
	public String getCarRepairManual(){
		try {
			String carplate = getRequest().getParameter("carplate");
			
			JSONObject obj = carInfoManager.getCarRepairManual(carplate);
			
			this.json = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorJson("请求出错");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 添加车辆，校验odb是否合法，是否已经被其他车辆绑定  20位
	 * @date 2016年10月21日 下午5:16:37
	 * @return
	 */
	public String obdIsBoundable(){
		try {
			JSONObject obj = new JSONObject();
			String msg = "";
			
			String obdmacaddr = getRequest().getParameter("obdmacaddr");
			
			if(StringUtil.isNull(obdmacaddr)){
				msg = "OBD编码为空，请重新输入";
			}else if(obdmacaddr.length() != 20){
				msg = "您输入的OBD编码格式错误，请输入20位OBD编码";
			}else if(obdmacaddr.toString().contains(":") || !obdmacaddr.toUpperCase().contains("JT")){
				msg = "您输入的obd编码格式有问题：包含：或不是JT开头";
			}else{
				obj = carInfoManager.verifyObdMacAddr(obdmacaddr);
			}
			
			if(!"".equals(msg)){
				obj.put("result", 0);
				obj.put("message", msg);
			}
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，校验obd失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 添加/修改车辆信息
	 * @date 2016年9月13日 下午10:02:39
	 * @return
	 */
	public String inputCarInfo(){
		String carInfo = new String();
		String msg = "";
		try {
			HttpServletRequest request = getRequest();
			carInfo = getBody(request);
			if(StringUtil.isNull(carInfo)){
				msg = "车辆信息为空，请检查";
			}else if(carInfo.indexOf("{") < 0 || carInfo.indexOf("}") < 0){
				msg = "车辆信息应该为json字符串，请检查";
			}else{
				org.json.JSONObject carObj = new org.json.JSONObject(carInfo);
				String carplate = carObj.getString("carplate");
				String carowner = carObj.getString("carowner");

				if("".equals(carplate)){
					msg = "车牌号为空，请检查";
				}else if(!RegularExpressionUtils.matches(carplate, RegularExpressionUtils.carplatePattern)){
					msg = "车牌号格式不正确，请检查";
				}else if("".equals(carowner)){
					msg = "车辆所有人为空，请检查";
				}else if(carObj.has("obdmacaddr")){
					String obdmacaddr = carObj.getString("obdmacaddr");
					
					if(StringUtil.isNull(obdmacaddr)){
						msg = "OBD编码为空，请检查";
					}else if(obdmacaddr.length() != 20){
						msg = "您输入的OBD编码格式错误，请输入20位OBD编码, 以JT开头, 不能包含':'";
					}else if(obdmacaddr.toString().contains(":") || !obdmacaddr.toUpperCase().contains("JT")){
						msg = "您输入的obd编码格式有问题：包含：或不是JT开头";
					}else{
						JSONObject verifyObj = carInfoManager.verifyObdMacAddr(obdmacaddr);
						
						int result = verifyObj.getInt("result");
						if(result == 0){
							msg = verifyObj.getString("message");
						}
					}
				}else if(carObj.has("repairlastmile")){
					String repairlastmile = carObj.getString("repairlastmile");
					if(StringUtil.isNull(repairlastmile)){
						msg = "上次保养里程为空，请检查";
					}else{
						boolean match = RegularExpressionUtils.matches(repairlastmile, RegularExpressionUtils.intPattern);
						if(!match){
							msg = "请输入正整数的上次保养里程";
						}else if(CarModel.max_mile < Long.valueOf(repairlastmile)){
							msg = "超出上次保养里程最大值：" + CarModel.max_mile;
						}
					}
				}else if(carObj.has("repairlasttime")){
					String repairlasttime = carObj.getString("repairlasttime");
					if(StringUtil.isNull(repairlasttime)){
						msg = "上次保养时间为空，请检查";
					}else{
						boolean match = RegularExpressionUtils.matches(repairlasttime, RegularExpressionUtils.intPattern);
						if(!match){
							msg = "请输入正整数的上次保养时间";
						}
					}
				}else{
					List carList = carInfoManager.getCarInfoByCarplate(carplate);
					if(carList.size() > 0){
						JSONObject carinfo = JSONObject.fromObject(carList.get(0));
						String carCarowner = carinfo.getString("carowner");
						if(!carowner.equals(carCarowner)){
							msg = "该车牌号的车辆已经存在，您不能重复添加";
						}
					}
				}
			}
			
			if(!"".equals(msg)){
				this.json = "{\"result\":0,\"message\":"+msg+"}";
			}else{
				JSONArray carInfoArray = carInfoManager.inputCarInfo(carInfo);
				if(carInfoArray.size() > 0){
					Member member = UserConext.getCurrentMember();
					for(int i=0; i<carInfoArray.size(); i++){
						JSONObject obj = carInfoArray.getJSONObject(i);
						Integer cartCount = cartManager.countItemNum(obj.getString("carplate"), member.getMember_id(), null);
						obj.put("cartCount", cartCount);//用户购物车数量
					}
					this.json = "{\"result\":1,\"data\":"+carInfoArray.toString()+"}";
				} else {
					this.json = "{\"result\":0,\"message\":"+"添加/修改失败"+"}";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.logger.error("updateCarInfo has errors", e);
			this.showErrorJson("服务器错误，添加车辆失败");
		}
	
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取车辆信息
	 * @date 2016年9月22日 下午12:06:30
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String getCarDetailInfo() {
		//获取请求数据
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String vin = request.getParameter("vin");
		
		//校验请求参数
		if(StringUtils.isEmpty(vin)) {
			this.showErrorJson("vin值不能为空");
			return JSON_MESSAGE;
		}
		if(vin.length() != 17) {
			this.showErrorJson("vin值必须是17位的字符串");
			return JSON_MESSAGE;
		}
		
		//根据vin查询数据库中的车辆信息
		CarInfoVin carInfoVin = carInfoManager.queryCarInfoVin(vin);
		if(carInfoVin == null) {
			try {
				//调用第三方数据解析接口
				String reqContent = "key=" + CarVinContent.key + "&vin=" + vin;
				String jsonMessage = MobileMessageHttpSend.postSend(CarVinContent.reqUrl,reqContent);
				
				//解析数据
				Map<String, Object> message = JsonUtil.toMap(jsonMessage);
				if(ValidateUtils.isEmpty(message)) {
					logger.info("第三方服务接口为返回任何信息");
					this.showErrorJson("第三方服务解析数据失败");
					return JSON_MESSAGE;
				}
				
				int error_code = Integer.parseInt(String.valueOf(message.get("error_code")));
				String reason = (String) message.get("reason");
				Map<String, Object> resultMap = (Map<String, Object>) message.get("result");
				if(error_code != 0) {
					logger.info(reason);
					this.showErrorJson(reason);
					return JSON_MESSAGE;
				}
				if(ValidateUtils.isEmpty(resultMap)) {
					logger.info("第三方接口返回结果为空");
					this.showErrorJson("未查到任何数据");
					return JSON_MESSAGE;
				}
				
				String vinnf = (String) resultMap.get("VINNF");
				String cjmc = (String) resultMap.get("CJMC");
				String pp = (String) resultMap.get("PP");
				String cx = (String) resultMap.get("CX");
				String pl = (String) resultMap.get("PL");
				String fdjxh = (String) resultMap.get("FDJXH");
				String bsqlx = (String) resultMap.get("BSQLX");
				String dws = (String) resultMap.get("DWS");
				String pfbz = (String) resultMap.get("PFBZ");
				String cldm = (String) resultMap.get("CLDM");
				String ssnf = (String) resultMap.get("SSNF");
				String tcnf = (String) resultMap.get("TCNF");
				String zdjg = (String) resultMap.get("ZDJG");
				String ssyf = (String) resultMap.get("SSYF");
				String scnf = (String) resultMap.get("SCNF");
				String nk = (String) resultMap.get("NK");
				String cxi = (String) resultMap.get("CXI");
				String xsmc = (String) resultMap.get("XSMC");
				String cllx = (String) resultMap.get("CLLX");
				String jb = (String) resultMap.get("JB");
				String csxs = (String) resultMap.get("CSXS");
				String cms = (String) resultMap.get("CMS");
				String zws = (String) resultMap.get("ZWS");
				String gl = (String) resultMap.get("GL");
				String rylx = (String) resultMap.get("RYLX");
				String bsqms = (String) resultMap.get("BSQMS");
				String rybh = (String) resultMap.get("RYBH");
				String qdfs = (String) resultMap.get("QDFS");
				String fdjgs = (String) resultMap.get("FDJGS");
				String levelId = (String) resultMap.get("LevelId");
				
				//封装数据
				carInfoVin = new CarInfoVin(); 
				carInfoVin.setCjmc(cjmc);
				carInfoVin.setPp(pp);
				carInfoVin.setCx(cx);
				carInfoVin.setPl(pl);
				carInfoVin.setFdjxh(fdjxh);
				carInfoVin.setBsqlx(bsqlx);
				carInfoVin.setDws(dws);
				carInfoVin.setVin(vin);
				carInfoVin.setVinnf(vinnf);
				carInfoVin.setSsyf(ssyf);
				carInfoVin.setPfbz(pfbz);
				carInfoVin.setSsnf(ssnf);
				carInfoVin.setZws(zws);
				carInfoVin.setRybh(rybh);
				carInfoVin.setBsqms(bsqms);
				carInfoVin.setZdjg(zdjg);
				carInfoVin.setQdfs(qdfs);
				carInfoVin.setCsxs(csxs);
				carInfoVin.setRylx(rylx);
				carInfoVin.setCxi(cxi);
				carInfoVin.setCldm(cldm);
				carInfoVin.setScnf(scnf);
				carInfoVin.setNk(nk);
				carInfoVin.setXsmc(xsmc);
				carInfoVin.setJb(jb);
				carInfoVin.setTcnf(tcnf);
				carInfoVin.setCllx(cllx);
				carInfoVin.setGl(gl);
				carInfoVin.setFdjgs(fdjgs);
				carInfoVin.setCms(cms);
				carInfoVin.setLevelid(levelId);
				
				//保存数据到数据库
				int carInfoVinId = carInfoManager.saveCarInfoVin(carInfoVin);
				if(carInfoVinId == 0) {
					this.showErrorJson("接口数据保存失败");
				}
				
				//调用方法
				CarModel carModel = carInfoManager.getCarmodelInfo(carInfoVin);
				
				if(ValidateUtils.isEmpty(carModel)) {
					this.showErrorJson("没有查到任何数据");
				} else {
					//更新carInfoVin
					int carModelId = carModel.getId();
					carInfoManager.updateCarInfoVin(carInfoVinId, carModelId);
					
					//响应数据
					String respContent = JSONObject.fromObject(carModel).toString();
					this.showSuccessJson("请求成功", respContent);
				}
			} catch (Exception e) {
				logger.info("第三方服务接口调用失败");
				this.showErrorJson("第三方服务解析数据失败");
				e.printStackTrace();
			}
			
		} else { //从数据库中获取数据
			CarModel carModel = carInfoManager.getCarmodelInfo(carInfoVin);
			if(ValidateUtils.isEmpty(carModel)) {
				//响应数据
				this.showSuccessJson("请求成功,没有查到任何数据");
			} else {
				//响应数据
				String respContent = JSONObject.fromObject(carModel).toString();
				this.showSuccessJson("请求成功", respContent);
			}
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取车辆收益使用历史记录
	 * @date 2016年9月23日 上午10:02:58
	 * @return
	 */
	public String getCarHodometerIncomeHistory(){
		try {
			String carplate = getRequest().getParameter("carplate");
			String pageNo = getRequest().getParameter("pageNo");
			String pageSize = getRequest().getParameter("pageSize");
			int startIndex = getStartIndex(pageNo, pageSize);
			
			JSONObject obj = carInfoManager.getCarHodometerIncomeHistory(carplate, startIndex, pageSize);
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，获取数据失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取用户过往签约4s店列表
	 * @date 2016年9月24日 下午3:22:09
	 * @return
	 */
	public String getCarSignStoreList(){
		try {
			String carplate = getRequest().getParameter("carplate");

			JSONObject obj = carInfoManager.getCarSignStoreList(carplate);
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，获取数据失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取车辆保养币使用历史记录
	 * @date 2016年9月24日 下午1:14:48
	 * @return
	 */
	public String getCarRepairCoinHistory(){
		try {
			String carplate = getRequest().getParameter("carplate");
			String store_id = getRequest().getParameter("store_id");
			String pageNo = getRequest().getParameter("pageNo");
			String pageSize = getRequest().getParameter("pageSize");
			int startIndex = getStartIndex(pageNo, pageSize);

			JSONObject obj = carInfoManager.getCarRepairCoinHistory(carplate, store_id, startIndex, pageSize);
			
			this.json = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorJson("服务器错误，获取数据失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取车辆位置信息  经纬度坐标  时间 车辆品牌图片 品牌  车系
	 * @date 2016年10月14日 下午3:16:22
	 * @return
	 */
	public String getCarPositionInfo(){
		try {
			String carplate = getRequest().getParameter("carplate");
			
			JSONObject resultObj = new JSONObject();
			if(StringUtil.isNull(carplate)){
				resultObj.put("result", 0);
				resultObj.put("message", "车牌号为空，请检查");
			}else{
				resultObj = carInfoManager.getCarPositionInfo(carplate);
			}
			
			this.json = resultObj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，获取车辆位置信息失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取车辆错误信息  品牌  车系
	 * @date 2016年10月14日 下午3:16:22
	 * @return
	 */
	public String getCarErrorCodeInfo(){
		try {
			String carplate = getRequest().getParameter("carplate");
			
			JSONObject resultObj = new JSONObject();
			if(StringUtil.isNull(carplate)){
				resultObj.put("result", 0);
				resultObj.put("message", "车牌号为空，请检查");
			}else{
				resultObj = carInfoManager.getCarErrorCodeInfo(carplate);
			}
			this.json = resultObj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，获取车辆错误信息失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/*
	 * ==========================================================================================
	 * GETTER AND SETTER
	 */
	public ICarInfoManager getCarInfoManager() {
		return carInfoManager;
	}
	public void setCarInfoManager(ICarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}
	public IProductManager getProductManager() {
		return productManager;
	}
	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}
	public ICartManager getCartManager() {
		return cartManager;
	}
	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}
	
}
