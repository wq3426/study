/**
 * 版权：Copyright (C) 2016  中安信博（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：设备api  
 * 修改人：
 * 修改时间：2016-03-14
 * 修改内容: 
 */
package com.enation.app.shop.core.action.api;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.shop.core.service.I4sStoresManager;
import com.enation.app.shop.core.service.ICarInfoManager;
import com.enation.app.shop.core.utils.RegularExpressionUtils;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;



/**
 * 4s店信息api
 * 
 * @author 
 * @date 2016-03-14
 * @version v0.1
 * @since         
 */
//curl -X GET 'http://localhost:8080/mall/api/mobile/4sstores!get4sStoresInfo.do'

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("4sstores")
@SuppressWarnings("serial")
public class C4SstoresApiAction extends WWAction {
	private I4sStoresManager i4sStoresManager;
	private ICarInfoManager carInfoManager;
	
	/**
	 * 获取4s店信息列表
	 * 提供分页
	 * @return
	 */
	public String get4sStoresInfo(){
		try{
			//获取输入参数
			String carmodel_id = getRequest().getParameter("carmodel_id");
			String page = getRequest().getParameter("page");
			String store_cityid = getRequest().getParameter("region_id");
			String repair4sstoreid = getRequest().getParameter("repair4sstoreid");
			
			//输入校验
			String msg = "";
			Pattern pattern = Pattern.compile("^\\d+$");
			if(StringUtil.isNull(carmodel_id)){
				msg = "车型id为空，请检查";
			}else{
				boolean match = pattern.matcher(carmodel_id).matches();
				if(!match){
					msg = "请输入正整数的车型id";
				}
			}
			if(!StringUtil.isNull(repair4sstoreid)){
				boolean match = pattern.matcher(repair4sstoreid).matches();
				if(!match){
					msg = "请输入正整数的签约店铺id";
				}
			}
			if(StringUtil.isNull(page)){
				page = "1";
			}else{
				boolean match = pattern.matcher(page).matches();
				if(!match){
					msg = "请输入正整数的页码";
				}
			}
			if(!StringUtil.isNull(store_cityid)){
				boolean match = pattern.matcher(store_cityid).matches();
				if(!match){
					msg = "请输入正整数的店铺城市id";
				}
			}
			
			if(!StringUtil.isNull(msg)){
				this.json = "{\"result\":0,\"message\":"+msg+"}";
			}else{
				int pageSize = 10;
				Page i4sStoresPage = i4sStoresManager.get4sStoresInfoPage(Integer.parseInt(page), pageSize, carmodel_id, store_cityid, repair4sstoreid);
				if(!StringUtil.isNull(repair4sstoreid) && Integer.parseInt(page)==1){//第一页时获取当前店铺信息放在list第一位
					JSONArray belongStoreArray = i4sStoresManager.get4sStoreById(Integer.parseInt(repair4sstoreid));
					belongStoreArray.addAll((List)i4sStoresPage.getResult());
					i4sStoresPage.setResult(belongStoreArray.toString());
				}
				i4sStoresPage.setCurrentPageNo(Integer.parseInt(page));
				JSONObject resultObj = JSONObject.fromObject(i4sStoresPage);
				String result = UploadUtil.replacePath(resultObj.toString());
				resultObj = JSONObject.fromObject(result);
				this.json = "{\"result\":1,\"data\":"+resultObj+"}";
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}

	
	/**
	 * 获取保养的签约和非签约的4s店信息
	 * 入参：
	 * carplate 车牌号
	 * 返参：name telephone address image mkprice price securitygain repairestimatedmaxgain nextmile  repaircontent
	 * @return
	 */
	public String get4sStoresRepairInfo(){
		String carplate = getRequest().getParameter("carplate");
		String page = getRequest().getParameter("page");
		String store_cityid = getRequest().getParameter("region_id");
		String repair4sstoreid = getRequest().getParameter("repair4sstoreid");
		page = (page == null || page.equals("")) ? "1" : page;
		
		JSONObject obj = i4sStoresManager.getRepairInfoByCar(Integer.parseInt(page),carplate,store_cityid,repair4sstoreid);
		
		this.json = "{\"result\":1,\"data\":"+obj.toString()+"}";
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取保养服务店铺列表
	 * @date 2016年9月2日 下午3:24:17
	 * @return
	 */
	public String getCarRepairStoreList(){
		try {
			String carplate = getRequest().getParameter("carplate");
			String page = getRequest().getParameter("page");
			String store_cityid = getRequest().getParameter("region_id");
			String repair4sstoreid = getRequest().getParameter("repair4sstoreid");
			
			//输入校验
			String msg = "";
			Pattern pattern = Pattern.compile("^\\d+$");
			if(StringUtil.isNull(carplate)){
				msg = "车牌号为空，请检查";
			}else if(carInfoManager.getCarInfoByCarplate(carplate).size() == 0){
				msg = "车牌号不存在，请检查";
			}
			if(StringUtil.isNull(repair4sstoreid)){
				msg = "签约店铺id为空，请检查";
			}else{
				boolean match = pattern.matcher(repair4sstoreid).matches();
				if(!match){
					msg = "请输入正整数的店铺id";
				}
			}
			if(StringUtil.isNull(page)){
				page = "1";
			}else{
				boolean match = pattern.matcher(page).matches();
				if(!match){
					msg = "请输入正整数的页码";
				}
			}
			if(!StringUtil.isNull(store_cityid)){
				boolean match = pattern.matcher(store_cityid).matches();
				if(!match){
					msg = "请输入正整数的店铺城市id";
				}
			}
			
			if(!StringUtil.isNull(msg)){
				this.json = "{\"result\":0,\"message\":"+msg+"}";
			}else{
				JSONObject obj = i4sStoresManager.getCarRepairStoreList(Integer.parseInt(page), carplate, store_cityid, repair4sstoreid);
				
				this.json = "{\"result\":1,\"data\":"+obj.toString()+"}";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * 4s店详情,显示评价信息
	 * @return
	 */
	public String getDetail(){
		try{
			HttpServletRequest request = this.getRequest();
			String page = request.getParameter("page");
			String store_id = request.getParameter("store_id");
			if(StringUtil.isNull(store_id)){
				showErrorJson("参数错误");
				return this.json;
			}
			page = (page == null || page.equals("")) ? "1" : page;
			int pageSize = 10;
			Page commentPage =  i4sStoresManager.listComment(Integer.parseInt(page),pageSize,Integer.parseInt(store_id));
			commentPage.setCurrentPageNo(Integer.parseInt(page));
			String commentList= UploadUtil.replacePath(JSONObject.fromObject(commentPage).toString());
			Map map= i4sStoresManager.get4sGrades(Integer.parseInt(store_id));
			JSONObject obj =  JSONObject.fromObject(commentList);
			if(map!=null && map.size() >0){
				obj.put("count_service_grade", map.get("count_service_grade"));
				obj.put("count_goods_grade", map.get("count_goods_grade"));
			}else{
				obj.put("count_service_grade", -1);
				obj.put("count_goods_grade", -1);
			}
			this.json = "{result : 1,data : " + obj + "}";
		}catch(Exception e){
			e.printStackTrace();
			this.showErrorJson("获取数据错误");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取店铺保养服务详情
	 * @date 2016年9月2日 下午5:26:37
	 * @return
	 */
	public String getStoreRepairDetail(){
		try {
			String store_id = getRequest().getParameter("store_id");
			String carplate = getRequest().getParameter("carplate");
			String order_time = getRequest().getParameter("order_time");
		
			JSONObject obj = i4sStoresManager.getStoreRepairDetail(store_id, carplate, order_time);
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("解析出错");
		}
		
		return JSON_MESSAGE;
	}
	
	public I4sStoresManager getI4sStoresManager() {
		return i4sStoresManager;
	}

	public void setI4sStoresManager(I4sStoresManager i4sStoresManager) {
		this.i4sStoresManager = i4sStoresManager;
	}

	public ICarInfoManager getCarInfoManager() {
		return carInfoManager;
	}

	public void setCarInfoManager(ICarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}
}
