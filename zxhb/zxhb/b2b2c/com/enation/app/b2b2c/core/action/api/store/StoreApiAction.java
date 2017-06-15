package com.enation.app.b2b2c.core.action.api.store;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.enation.app.b2b2c.core.model.RepairTimeRegion;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.zxhb.OrderDetail;
import com.enation.app.b2b2c.core.model.zxhb.OrderStatus;
import com.enation.app.b2b2c.core.model.zxhb.OrderUser;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IZxhbOrderManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.Store;
import com.enation.app.shop.component.payment.plugin.alipay.web.config.AlipayConfig;
import com.enation.app.shop.component.payment.plugin.alipay.web.util.AlipayNotify;
import com.enation.app.shop.component.payment.plugin.alipay.web.util.AlipaySubmit;
import com.enation.app.shop.component.payment.plugin.wpay.HttpUtil;
import com.enation.app.shop.component.payment.plugin.wpay.PayCommonUtil;
import com.enation.app.shop.component.payment.plugin.wpay.PayConfigUtil;
import com.enation.app.shop.core.utils.RegularExpressionUtils;
import com.enation.eop.processor.MobileContent;
import com.enation.eop.processor.MobileMessageHttpSend;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.KdniaoTrackQueryAPI;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.eop.sdk.utils.ValidateUtils;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

import cn.jiguang.commom.utils.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
/**
 * 店铺管理API
 * @author LiFenLong
 *
 */
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("storeApi")
@InterceptorRef(value="apiRightStack",params={"apiRightInterceptor.excludeMethods","checkStoreName,checkIdNumber"})
@Results({
	 @Result(name="region_edit",type="freemarker", location="/themes/b2b2cv2/store/apply/edit_insert_time.html"),
	 @Result(name="reApply_edit",type="freemarker", location="/themes/b2b2cv2/store/apply/reApply2.html"),
	 @Result(name="order_list",type="freemarker", location="/themes/b2b2cv2/cardorder/order_list.html"),
	 @Result(name="order_list_app",type="freemarker", location="/themes/b2b2cv2/cardorder/order_list_app.html"),
	 @Result(name="wish",type="freemarker", location="/themes/b2b2cv2/cardorder/wish.html"),
	 @Result(name="wish_app",type="freemarker", location="/themes/b2b2cv2/cardorder/wish_app.html")
})
public class StoreApiAction extends WWAction{
	private Store store;
	
	private File id_img;
	private File license_img;
	private String id_imgFileName;
	private String license_imgFileName;
	private String id_number;
	
	private String fsid_img;
	private String fslicense_img;
	private String status_id_img;
	private String status_license_img;
	
	private String logo;
	private String storeName;
	
	private Integer store_id;
	private Integer store_auth;
	private Integer name_auth;
	private IStoreManager storeManager;
	private IStoreMemberManager storeMemberManager;
	private String description;
	private RepairTimeRegion repairTimeRegion;//保养时间段设置对象
	
	private File data ;//上传的文件
 	private String dataFileName;//上传的文件名
    private String dataContentType; // 上传的文件类型
    private String fileReason;
    
    private JSONObject dataObj;//数据json对象
    
    private Map orderMap;
    
    private IZxhbOrderManager zxhbOrderManager;
	
	/**
	 * 查看用户名是否重复
	 * @param storeName 店铺名称,String
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String checkStoreName(){
		if(this.storeManager.checkStoreName(storeName)){
			this.showErrorJson("店铺名称重复");
		}else{
			this.showSuccessJson("店铺名称可以使用");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 申请开店
	 * @param store 店铺信息,Store
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String apply(){
		try {
			if(null==storeMemberManager.getStoreMember()){
				this.showErrorJson("您没有登录不能申请开店");
			}else if(!storeManager.checkStore()){
				//添加店铺地址
				store=this.getStoreInfo();
				//暂时先将店铺等级定为默认等级，以后版本升级更改此处
				store.setStore_level(1);
				storeManager.apply(store);
				this.showSuccessJson("申请成功,请等待审核");
			}else{
				this.showErrorJson("您已经申请过了，请不要重复申请");
			}
		} catch (Exception e) {
			this.logger.error("申请失败:"+e);
			e.printStackTrace();
			this.showErrorJson("申请失败");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 重试申请开店
	 * @param store 店铺信息,Store
	 * @return
	 */
	public String reApply(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		try {
			if(null==storeMemberManager.getStoreMember()){
				this.showErrorJson("您没有登录不能申请开店");
			}else {
				//添加店铺地址
				String store_province = request.getParameter("store_province");
				String store_province_id = request.getParameter("store_province_id");
				String store_city = request.getParameter("store_city");
				String store_city_id = request.getParameter("store_city_id");
				String store_region_id = request.getParameter("store_region_id");
				String store_region = request.getParameter("store_region");
				store.setStore_provinceid(StringUtil.isEmpty(store_province_id) ? 0 : Integer.valueOf(store_province_id));
				store.setStore_province(store_province);
				store.setStore_cityid(StringUtil.isEmpty(store_city_id) ? 0 : Integer.valueOf(store_city_id));
				store.setStore_city(store_city);
				store.setStore_regionid(StringUtil.isEmpty(store_region_id) ? 0 : Integer.valueOf(store_region_id));
				store.setStore_region(store_region);
				//暂时先将店铺等级定为默认等级，以后版本升级更改此处
				store.setStore_id(store_id);
				store.setStore_level(1);
				store.setSettlement_period(7);//设置结算周期默认为7天
				store.setDisabled(1);//因项目进度先默认不审核，后期修改
				storeManager.reApply(store);
				this.showSuccessJson("店铺信息设置成功");
			}
		} catch (Exception e) {
			this.logger.error("申请失败:"+e);
			e.printStackTrace();
			this.showErrorJson("申请失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 获取店铺信息
	 * @param store
	 * @return Store
	 */
	private Store getStoreInfo(){
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Store store=new Store();
		store.setStore_name(request.getParameter("store_name"));		//店铺名称
		store.setZip(request.getParameter("zip"));					//邮编
		store.setTel(request.getParameter("tel"));					//联系方式
		store.setId_number(request.getParameter("id_number"));		//申请人身份证号
		store.setId_img(request.getParameter("store_id_img"));				//身份证证明
		store.setLicense_img(request.getParameter("store_license_img") );	//营业执照证明
		store.setName_auth(Integer.parseInt(request.getParameter("store_name_auth").toString()));	//身份证图片路径	
		store.setStore_auth(Integer.parseInt(request.getParameter("store_store_auth").toString()));	//营业执照图片路径
		//店铺地址信息
		store.setStore_provinceid(Integer.parseInt(request.getParameter("store_province_id").toString()));	//店铺省ID
		store.setStore_cityid(Integer.parseInt(request.getParameter("store_city_id").toString()));			//店铺市ID
		store.setStore_regionid(Integer.parseInt(request.getParameter("store_region_id").toString()));		//店铺区ID
		
		store.setStore_province(request.getParameter("store_province"));	//店铺省
		store.setStore_city(request.getParameter("store_city"));			//店铺市
		store.setStore_region(request.getParameter("store_region"));		//店铺区
		store.setAttr(request.getParameter("attr"));						//店铺详细地址
		//店铺银行信息
		store.setBank_account_name(request.getParameter("bank_account_name")); 		//银行开户名   
		store.setBank_account_number(request.getParameter("bank_account_number")); 	//公司银行账号
		store.setBank_name(request.getParameter("bank_name")); 						//开户银行支行名称
		store.setBank_code(request.getParameter("bank_code")); 						//支行联行号
		
//		store.setBank_provinceid(Integer.parseInt(request.getParameter("bank_province_id").toString())); //开户银行所在省Id
//		store.setBank_cityid(Integer.parseInt(request.getParameter("bank_city_id").toString()));		  //开户银行所在市Id
//		store.setBank_regionid(Integer.parseInt(request.getParameter("bank_region_id").toString()));    //开户银行所在区Id
		
		store.setBank_province(request.getParameter("bank_province"));	//开户银行所在省
		store.setBank_city(request.getParameter("bank_city"));			//开户银行所在市
		store.setBank_region(request.getParameter("bank_region"));		//开户银行所在区
		
		//店铺佣金
		store.setCommission(0.0);
		
		//判断是否含有小区
		if(request.getParameter("community_id")!=null){
			store.setCommunity_id(Integer.parseInt(request.getParameter("community_id")));
			store.setCommunity(request.getParameter("community"));
		}
		return store;
	}
	/**
	 * 修改店铺设置
	 * @param store 店铺信息,Store
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String edit(){
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			Map map=new HashMap();
			map.put("store_id",request.getParameter("store_id"));
			map.put("attr", request.getParameter("attr"));
			map.put("zip", request.getParameter("zip"));
			map.put("tel", request.getParameter("tel"));
			map.put("qq", request.getParameter("qq"));
			map.put("description", description);              //修改详细得不到富文本编辑器得不到图片src信息。  whj 2015-06-18
			map.put("store_logo",request.getParameter("store_logo"));
			map.put("store_banner", request.getParameter("store_banner"));
			map.put("store_provinceid", Integer.parseInt(request.getParameter("province_id").toString()));
			map.put("store_cityid", Integer.parseInt(request.getParameter("city_id").toString()));
			map.put("store_regionid", Integer.parseInt(request.getParameter("region_id").toString()));
			map.put("store_province", request.getParameter("province"));
			map.put("store_city", request.getParameter("city"));
			map.put("store_region", request.getParameter("region"));
			this.storeManager.editStore(map);
			this.showSuccessJson("修改店铺信息成功");
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("修改店铺信息失败");
			this.logger.error("修改店铺信息失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 检测身份证
	 * @param id_number 身份证号
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String checkIdNumber(){
		int result=storeManager.checkIdNumber(id_number);
		if(result==0){
			this.showSuccessJson("身份证可以使用！");
		}else{
			this.showErrorJson("身份证已经存在！");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 修改店铺Logo
	 * @param logo Logo,String
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String editStoreLogo(){
		try {
			storeManager.editStoreOnekey("store_logo",logo);
			this.showSuccessJson("店铺Logo修改成功");
		} catch (Exception e) {
			this.logger.error("修改店铺Logo失败:"+e);
			this.showErrorJson("店铺Logo修改失败");
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 提交店铺认证信息
	 * @param store_id 店铺Id,Integer
	 * @param fsid_img 身份证图片,String
	 * @param fslicense_img 营业执照图片,String
	 * @param store_auth 店铺认证,Integer
	 * @param name_auth 店主认证,Integer
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String store_auth(){
		try {
			storeManager.saveStoreLicense(store_id, fsid_img, fslicense_img, store_auth, name_auth);
			this.showSuccessJson("提交成功，等待审核");
		} catch (Exception e) {
			this.showErrorJson("提交失败，请重试");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 初始化店铺信息
	 * @return
	 */
	public String initStoreInfo(){
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			Map map=new HashMap();
			int memberId = UserConext.getCurrentMember().getMember_id();
			Store store = this.storeManager.getStoreByMember(memberId);
			map.put("store_id", store.getStore_id());
			map.put("store_name",request.getParameter("store_name"));
			map.put("customer_phone",request.getParameter("customer_phone"));
			map.put("after_phone",request.getParameter("after_phone"));
			map.put("store_desc",request.getParameter("store_desc"));
			this.storeManager.editStore(map);
			this.showSuccessJson("修改成功");
		}catch(RuntimeException e){
			e.printStackTrace();
			this.showErrorJson("发生错误:" + e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * @description 添加保养时间段
	 * @date 2016年8月31日 下午7:36:55
	 * @return String
	 */
	public String addRepairTimeRegion(){
		JSONObject obj = new JSONObject();
		try {
			int store_id = ((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id();
			repairTimeRegion.setStore_id(store_id);
			if(!storeManager.isExistRegion(repairTimeRegion)){
				obj = storeManager.addRepairTimeRegion(repairTimeRegion);
			}else{
				obj.put("result", 0);
				obj.put("message", "该时间段已存在，请勿重复添加");
			}
			
			this.json = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorJson("添加失败，服务器出现错误，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 保养时间段修改
	 * @date 2016年8月31日 下午8:24:32
	 * @return String
	 */
	public String editRepairTimeRegion(){
		try {
			String id = getRequest().getParameter("id");
			repairTimeRegion = storeManager.getRepairTimeRegion(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "region_edit";
	}
	
	/**
	 * @description 修改保养时间段
	 * @date 2016年8月31日 下午8:51:56
	 * @return String
	 */
	public String editRepairTimeRegionSave(){
		try {
			String repair_time_range = getRequest().getParameter("repair_time_range");
			JSONObject obj = storeManager.editRepairTimeRegionSave(repairTimeRegion, repair_time_range);
			
			this.json = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorJson("修改失败，服务器出现错误，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 删除保养时间段
	 * @date 2016年8月31日 下午8:52:27
	 * @return String
	 */
	public String delRepairTimeRegion(){
		try {
			String id = getRequest().getParameter("id");
			JSONObject obj = storeManager.delRepairTimeRegion(id);
			
			this.json = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorJson("删除失败，服务器出现错误，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 店铺保养项目价格导入
	 * @date 2016年9月1日 上午11:57:57
	 * @return String
	 */
	public String importStoreRepairItem(){
		try {
			int store_id = ((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id();
			
			JSONObject returnObj = storeManager.importStoreRepairItem(data, dataFileName, store_id);
			
			this.json = returnObj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorJson("出现错误，导入失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 店铺保养项目价格修改
	 * @date 2016年9月6日 下午6:27:18
	 * @return String
	 */
	public String editStoreRepairItem(){
		try {
			String id = getRequest().getParameter("id");
			String item_price = getRequest().getParameter("item_price");
			String repair_price = getRequest().getParameter("repair_price");

			JSONObject obj = storeManager.editStoreRepairItem(item_price, repair_price, id);
			
			this.json = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorJson("服务器错误，修改失败");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 删除店铺保养项
	 * @date 2016年9月2日 下午5:02:48
	 * @return String
	 */
	public String delStoreRepairItem(){
		try {
			String id = getRequest().getParameter("id");
			JSONObject obj = storeManager.delStoreRepairItem(id);
			
			this.json = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorJson("删除失败，服务器出现错误，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 店铺保险设置
	 * @date 2016年9月17日 下午12:56:59
	 * @return String
	 */
	public String updateStoreInsureCompanyInfo(){
		try {
			String company_ids = getRequest().getParameter("company_ids");
			String insure_income_discount = getRequest().getParameter("insure_income_discount");
			int store_id = ((StoreMember)ThreadContextHolder.getSessionContext().getAttribute(IStoreMemberManager.CURRENT_STORE_MEMBER_KEY)).getStore_id();
			
			JSONObject obj = storeManager.updateStoreInsureCompanyInfo(store_id, company_ids, insure_income_discount);
			
			this.json = obj.toString();
		} catch (Exception e) {
			e.printStackTrace();
			showErrorJson("保存设置失败，服务器出现错误，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}

	/**
	 * @description 校验提现原密码是否正确
	 * @date 2016年9月24日 下午4:19:07
	 * @return String
	 */
	public String checkCashingoutPassword() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String oldPassword = request.getParameter("oldPassword");//原密码
		Member member = storeMemberManager.getStoreMember();
		int storeId = member.getStore_id();
		if(StringUtils.isEmpty(oldPassword)) {
			this.showErrorJson("原密码不能为空");
			return JSON_MESSAGE;
		}
		
		//检验原密码是否正确
		oldPassword = StringUtil.md5(oldPassword);
		String originalPassword = storeManager.queryCashingoutPasswordByMemberName(storeId);
		if(originalPassword.equals(oldPassword)) {
			this.showSuccessJson("原密码正确");
		} else {
			this.showErrorJson("原密码错误");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 根据原密码修改提现密码
	 * @date 2016年9月14日 下午5:35:59
	 * @return String
	 */
	public String changeCashingoutPassword() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Member member = storeMemberManager.getStoreMember();
		String oldPassword = request.getParameter("oldPassword");//原密码
		String newPassword = request.getParameter("newPassword");//新密码
		int storeId = member.getStore_id();
		
		try {
			//数据校验
			if(StringUtil.isEmpty(oldPassword)) {
				this.showErrorJson("原密码不能为空！");
				return JSON_MESSAGE;
			}	
			if(StringUtil.isEmpty(newPassword)) {
				this.showErrorJson("新密码不能为空！");
				return JSON_MESSAGE;
			}
			if(newPassword.equals(oldPassword)) {
				this.showErrorJson("新密码与旧密码一样，请重新填写新密码");
				return JSON_MESSAGE;
			}
			if (newPassword.length() < 6 || newPassword.length() > 16) {
				this.showErrorJson("新密码的长度为4-20个字符！");
				return JSON_MESSAGE;
			}
				
			//更新密码
			newPassword = StringUtil.md5(newPassword);
			int result = storeManager.updateCashingoutPassword(newPassword,storeId);
			if(result ==1) {
				this.showSuccessJson("尊敬的盈驾客户，您已成功修改【提现密码】，请勿泄漏，如有疑问请致电客服电话：010-64860393。【盈驾】");
			} else {
				this.showErrorJson("提现密码修改失败");
			}
			
		} catch (RuntimeException e) {
			this.showErrorJson(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			this.showErrorJson("提现密码修改失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 发送验证码
	 * @date 2016年9月20日 下午7:56:32
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String sendCheckCode() {
		try {
			//获取提现人手机号码
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String mobile = request.getParameter("mobile");
			if(StringUtils.isEmpty(mobile)) {
				this.showErrorJson("手机号码不能为空");
			}
			
			//生成验证码
			String mobileCode=""+(int)((Math.random()*9+1)*100000);
			ThreadContextHolder.getSessionContext().setAttribute("mobileCode", mobileCode);
			ThreadContextHolder.getSessionContext().setAttribute("mobile", mobile);
			
			String content =  MobileMessageHttpSend.paraTo16(
					"尊敬的盈驾客户，（"+mobileCode+"）为您的（设置提现密码）验证码，请于1分钟内填写。"+
					"（工作人员不会向您索取信息，请勿泄漏，以免造成账户或资金损失）"+
					"如非本人操作请忽略或致电客服电话：010-64860393。【盈驾】"
					);
			
			//boolean result = this.storeManager.checkCashingouterPhoneIsExist(mobile);
			//if(result){
				//this.showErrorJson("该手机号已被使用！");
			//} else {
				String strSmsParam = "reg=" + MobileContent.strReg + "&pwd=" + MobileContent.strPwd + "&sourceadd=" + MobileContent.strSourceAdd + "&phone=" + mobile + "&content=" + content;
				MobileMessageHttpSend.postSend(MobileContent.strSmsUrl,strSmsParam);
				this.showSuccessJson("发送成功");
			//}
		} catch (RuntimeException e) {
			this.showErrorJson("发送失败");
		} catch (Exception e) {
			this.showErrorJson("发送失败");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 保存店铺设置
	 * @date 2016年9月20日 下午7:37:39
	 * @return String
	 */
	public String reApply_save() {

		//获取用户信息
		Member member = storeMemberManager.getStoreMember();
		if(ValidateUtils.isEmpty(member)) {
			this.showErrorJson("您没有登录不能申请开店");
			return JSON_MESSAGE;
		}
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String store_name = request.getParameter("store_name");
		String store_province = request.getParameter("store_province");
		String store_city = request.getParameter("store_city");
		String store_region = request.getParameter("store_region");
		String store_province_id = request.getParameter("store_province_id");
		String store_city_id = request.getParameter("store_city_id");
		String store_region_id = request.getParameter("store_region_id");
		String attr = request.getParameter("attr");
		String zip = request.getParameter("zip");
		String customer_phone = request.getParameter("customer_phone");
		String contacts_name = request.getParameter("contacts_name");
		String tel = request.getParameter("tel");
		String office_phone = request.getParameter("office_phone");
		String email = request.getParameter("email");
		String corporation = request.getParameter("corporation");
		String corporation_id_number = request.getParameter("corporation_id_number");
		String bussiness_license_number = request.getParameter("bussiness_license_number");
		int brand = Integer.parseInt(request.getParameter("brand"));
		String bank_account_name = request.getParameter("bank_account_name");
		String bank_account_number = request.getParameter("bank_account_number");
		String bank_name = request.getParameter("bank_name");
		String bank_code = request.getParameter("bank_code");
		String bank_province = request.getParameter("bank_province");
		String bank_city = request.getParameter("bank_city");
		String bank_region = request.getParameter("bank_region");
		String bank_province_id = request.getParameter("bank_province_id");
		String bank_city_id = request.getParameter("bank_city_id");
		String bank_region_id = request.getParameter("bank_region_id");
		String bank_attr = request.getParameter("bank_attr");
		String alipay_account = request.getParameter("alipay_account");
		String weichat_account = request.getParameter("weichat_account");
		String cashingout_password = request.getParameter("cashingout_password");
		String cashingouter_phone = request.getParameter("cashingouter_phone");
		String checkCode = request.getParameter("checkCode");
		String store_logo = request.getParameter("store_logo");
		String id_img = request.getParameter("contacts_id_img");
		String contact_idimg_back = request.getParameter("contact_idimg_back");
		String corporation_id_img = request.getParameter("corporation_img");
		String corporate_idimg_back = request.getParameter("corporate_idimg_back");
		String license_img = request.getParameter("licenseImg");
		String bank_license_img = request.getParameter("bank_license_img");
		String user_password = request.getParameter("user_password");
		
		if(StringUtils.isEmpty(user_password)) {
			this.showErrorJson("登录密码不能为空");
			return JSON_MESSAGE;
		}
		if(StringUtils.isEmpty(cashingout_password)) {
			this.showErrorJson("提现密码不能为空");
			return JSON_MESSAGE;
		}
		if(StringUtils.isEmpty(cashingouter_phone)) {
			this.showErrorJson("提现人手机不能为空");
			return JSON_MESSAGE;
		}
		if(StringUtils.isEmpty(checkCode)) {
			this.showErrorJson("验证码不能为空");
			return JSON_MESSAGE;
		}
		
		//从session中获取验证码
		String mobileCode = (String) ThreadContextHolder.getSessionContext().getAttribute("mobileCode");
		String mobile = (String) ThreadContextHolder.getSessionContext().getAttribute("mobile");
		
		//检验验证码是否正确
		if(!cashingouter_phone.equals(mobile)) {
			this.showErrorJson("手机号码与获取验证码的手机号码不一致");
			return JSON_MESSAGE;
		}
		if(!checkCode.equals(mobileCode)) {
			this.showErrorJson("验证码错误，请重新获取验证码");
			return JSON_MESSAGE;
		}

		//获取品牌名称
		String brand_name = storeManager.queryBrandNameByBrandId(brand);
		
		//封装数据
		Store store = new Store();
		int storeId = member.getStore_id();
		store.setStore_id(storeId);
		store.setStore_level(1);
		store.setStore_city(store_city);
		store.setAttr(attr);
		store.setBank_code(bank_code);
		store.setBank_city(bank_city);
		store.setZip(zip);
		store.setStore_name(store_name);
		store.setBank_name(bank_name);
		store.setTel(tel);
		store.setBank_account_number(bank_account_number);
		store.setStore_provinceid(StringUtil.isEmpty(store_province_id) ? 0 : Integer.valueOf(store_province_id));
		store.setBank_account_name(bank_account_name);
		store.setStore_province(store_province);
		store.setStore_regionid(StringUtil.isEmpty(store_region_id) ? 0 : Integer.valueOf(store_region_id));
		store.setStore_cityid(StringUtil.isEmpty(store_city_id) ? 0 : Integer.valueOf(store_city_id));
		store.setSettlement_period(7);
		store.setBank_region(bank_region);
		store.setStore_region(store_region);
		store.setBank_province(bank_province);
		store.setBrand_id(brand);
		store.setMember_name(member.getUsername());
		store.setContacts_name(contacts_name);
		store.setBank_provinceid(StringUtil.isEmpty(bank_province_id) ? 0 : Integer.valueOf(bank_province_id));
		store.setBank_cityid(StringUtil.isEmpty(bank_city_id) ? 0 : Integer.valueOf(bank_city_id));
		store.setBank_regionid(StringUtil.isEmpty(bank_region_id) ? 0 : Integer.valueOf(bank_region_id));
		store.setBank_attr(bank_attr);
		store.setCustomer_phone(customer_phone);
		store.setAlipay_account(alipay_account);
		store.setCorporation(corporation);
		store.setCorporation_id_number(corporation_id_number);
		store.setBussiness_license_number(bussiness_license_number);
		store.setWeichat_account(weichat_account);
		store.setAuditstatus("1");
		store.setCashingout_password(StringUtil.md5(cashingout_password));
		store.setCashingouter_phone(cashingouter_phone);
		store.setOffice_phone(office_phone);
		store.setEmail(email);
		store.setMember_id(member.getMember_id());
		store.setStore_logo(store_logo);
		store.setId_img(id_img);
		store.setContact_idimg_back(contact_idimg_back);
		store.setCorporation_id_img(corporation_id_img);
		store.setCorporate_idimg_back(corporate_idimg_back);
		store.setLicense_img(license_img);
		store.setBrand_name(brand_name);
		store.setBank_license_img(bank_license_img);
		if(StringUtil.isEmpty(store.getId_img())){
			store.setId_img(null);
			store.setName_auth(0);
		}if(StringUtil.isEmpty(store.getLicense_img())){
			store.setLicense_img(null);
			store.setStore_auth(0);
		}
		
		try {
			//更新登录密码
			user_password = StringUtil.md5(user_password);
			storeMemberManager.updateUserLoginPassword(user_password, storeId);
			
			//保存数据
			int result = storeManager.updateStoreInfo(store);
			
			if(result ==1 ) {
				this.showSuccessJson("尊敬的盈驾客户，您已成功注册，请牢记您的帐号、密码。如有疑问请致电客服电话：010-64860393。【盈驾】");
			} else {
				this.showErrorJson("店铺信息设置失败");
			}
		} catch (Exception e) {
			this.showErrorJson("店铺信息设置失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 校验提现人手机号码是否正确
	 * @date 2016年9月21日 下午3:23:42
	 * @return String
	 */
	public String checkCashingouterPhoneIsCorrect() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Member member = storeMemberManager.getStoreMember();
		int storeId = member.getStore_id();
		String cashingouterPhone = request.getParameter("phone");
		boolean result = storeManager.checkCashingouterPhoneIsCorrect(cashingouterPhone, storeId);
		if(result) {
			this.showSuccessJson("手机号码正确");
		} else {
			this.showErrorJson("当前手机号码与已设置的提现密码不一致，如需修改请联系【中安】");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 为修改提现密码发送验证码
	 * @date 2016年9月21日 下午7:32:22
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String sendCheckCodeOfModify() {
		try {
			//获取提现人手机号码
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			//Member member = storeMemberManager.getStoreMember();
			String mobile = request.getParameter("mobile");
			//int storeId = member.getStore_id();
			
			//生成验证码
			String mobileCode=""+(int)((Math.random()*9+1)*100000);
			ThreadContextHolder.getSessionContext().setAttribute("mobileCode", mobileCode);
			ThreadContextHolder.getSessionContext().setAttribute("mobile", mobile);
			
			String content =  MobileMessageHttpSend.paraTo16(
					" 尊敬的盈驾客户，（"+mobileCode+"）为您的（修改提现密码）验证码，请于1分钟内填写。"+
					"（工作人员不会向您索取信息，请勿泄漏，以免造成账户或资金损失）"+
					"如非本人操作请忽略或致电客服电话：010-64860393。【盈驾】"
					);
			
			//boolean result = storeManager.checkCashingouterPhoneIsCorrect(mobile, storeId);
			//if(result) {
				String strSmsParam = "reg=" + MobileContent.strReg + "&pwd=" + MobileContent.strPwd + "&sourceadd=" + MobileContent.strSourceAdd + "&phone=" + mobile + "&content=" + content;
				MobileMessageHttpSend.postSend(MobileContent.strSmsUrl,strSmsParam);
				this.showSuccessJson("发送成功");
			//} else {
				//this.showErrorJson("发送失败");
			//}
			
		} catch (RuntimeException e) {
			this.showErrorJson("发送失败");
		} catch (Exception e) {
			this.showErrorJson("发送失败");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取纪念卡预约验证码
	 * @date 2016年12月7日 下午6:02:03
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String sendOrderCheckCode() {
		try {
			//获取提现人手机号码
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			//Member member = storeMemberManager.getStoreMember();
			String mobile = request.getParameter("mobile");
			//int storeId = member.getStore_id();

			//生成验证码
			String mobileCode=""+(int)((Math.random()*9+1)*100000);
			ThreadContextHolder.getSessionContext().setAttribute("orderMobileCode", mobileCode);
			ThreadContextHolder.getSessionContext().setAttribute("orderMobile", mobile);
			
			String content =  MobileMessageHttpSend.paraTo16(
			" 尊敬的客户，欢迎订购西藏交通一卡通纪念卡，（"+mobileCode+"）为您的（订购）验证码，请于1分钟内填写。"+
			" 工作人员不会向您索取信息，请勿泄漏。【中信海博】"
			);
			
			//boolean result = storeManager.checkCashingouterPhoneIsCorrect(mobile, storeId);
			//if(result) {
				String strSmsParam = "reg=" + MobileContent.strReg + "&pwd=" + MobileContent.strPwd + "&sourceadd=" + MobileContent.strSourceAdd + "&phone=" + mobile + "&content=" + content;
				MobileMessageHttpSend.postSend(MobileContent.strSmsUrl,strSmsParam);
				this.showSuccessJson("发送成功");
			//} else {
				//this.showErrorJson("发送失败");
			//}
			
		} catch (RuntimeException e) {
			this.showErrorJson("发送失败");
		} catch (Exception e) {
			this.showErrorJson("发送失败");
		}
		return JSON_MESSAGE;
	}
	
	@SuppressWarnings("unchecked")
	public String createUserAndOrder() {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String goods_id = request.getParameter("goods_id");
			String gg_spec_value_id = request.getParameter("gg_spec_value_id");
			String ta_spec_value_id = request.getParameter("ta_spec_value_id");
			String contactName = request.getParameter("contactName");
			String contactPhone = request.getParameter("contactPhone");
			String checkCode = request.getParameter("checkCode");
			String order_count = request.getParameter("order_count");
			String address = request.getParameter("address");
			String referee_code  = request.getParameter("referee_code");
			
			if(StringUtil.isNull(contactName)){
				showErrorJson("联系人姓名为空，请检查");
				return JSON_MESSAGE;
			}
			if(StringUtil.isNull(contactPhone)){
				showErrorJson("手机号为空，请检查");
				return JSON_MESSAGE;
			}
			if(StringUtil.isNull(checkCode)){
				showErrorJson("验证码为空，请检查");
				return JSON_MESSAGE;
			}
			if(StringUtil.isNull(address)){
				showErrorJson("请填写邮寄地址");
				return JSON_MESSAGE;
			}
			
			String mobile = (String) ThreadContextHolder.getSessionContext().getAttribute("orderMobile");
			String mobileCode = (String) ThreadContextHolder.getSessionContext().getAttribute("orderMobileCode");
			
			
			if(!StringUtil.isNull(mobile) && !StringUtil.isNull(mobileCode) && mobile.equals(contactPhone) && mobileCode.equals(checkCode)){//验证通过，注册用户，同时生成预约订单
				OrderUser user = new OrderUser();
				user.setUser_telephone(contactPhone);
				user.setRegister_time(new Date().getTime());
				user.setPassword(StringUtil.md5("123456"));
				
				OrderDetail order_detail = new OrderDetail();
				order_detail.setGoods_id(Integer.valueOf(goods_id));
				order_detail.setGg_spec_value_id(Integer.valueOf(gg_spec_value_id));
				order_detail.setTa_spec_value_id(Integer.valueOf(ta_spec_value_id));
				order_detail.setOrder_count(Integer.valueOf(order_count));
				order_detail.setReceive_user(contactName);
				order_detail.setAddress(address);
				order_detail.setStatus(OrderStatus.NOT_PAY);
				order_detail.setCreate_time(new Date().getTime());
				order_detail.setReferee_code(referee_code);
				
				JSONObject obj = storeMemberManager.createUserAndOrder(user, order_detail);
				
				this.json = obj.toString();
			}else{
				showErrorJson("验证码不正确，请重新获取验证码后再次预约");
				return JSON_MESSAGE;
			}
		} catch (RuntimeException e) {
			this.showErrorJson("预约失败，请稍后再试");
			e.printStackTrace();
		} catch (Exception e) {
			this.showErrorJson("预约失败，请稍后再试");
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取预约订单列表
	 * @date 2016年12月13日 下午4:46:51
	 * @return
	 */
	public String getCardOrderList(){
		try {
			String mobile = getRequest().getParameter("mobile");

			if(StringUtil.isNull(mobile)){
				showErrorJson("手机号为空，请检查");
				return JSON_MESSAGE;
			}
			if(!RegularExpressionUtils.matches(mobile, RegularExpressionUtils.mobilePattern)){
				showErrorJson("手机号格式不正确，请重新输入");
				return JSON_MESSAGE;
			}
			
			JSONObject obj = storeMemberManager.getCardOrderList(mobile);
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			this.showErrorJson("获取订单列表失败，请稍后再试");
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 修改订单收货地址
	 * @date 2016年12月20日 上午10:42:14
	 * @return
	 */
	public String editZxOrderAddress(){
		try {
			String order_id = getRequest().getParameter("order_id");
			String address = getRequest().getParameter("address");
			
			JSONObject obj = storeMemberManager.editZxOrderAddress(order_id, address);
			
			this.json = obj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 订单支付
	 * @date 2016年12月20日 下午4:38:16
	 * @return
	 */
	public String payOrder(){
		try {
			String access_type = getRequest().getParameter("access_type");
			
			String return_url = AlipayConfig.return_url;
			
			if(access_type.equals("2")){
				return_url = "http://www.trans-it.cn:6066/zxhb/api/b2b2c/storeApi!payReturnForAppPage.do";
			}
			
			//商户订单号，商户网站订单系统中唯一订单号，必填
	        String out_trade_no = new String(getRequest().getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

	        //订单名称，必填
	        String subject = getRequest().getParameter("subject");
	        
	        //付款金额，必填
	        String total_fee = new String(getRequest().getParameter("total_fee").getBytes("ISO-8859-1"),"UTF-8");

	        //商品描述，可空
	        String body = new String(getRequest().getParameter("body").getBytes("ISO-8859-1"),"UTF-8");
	        
	        //把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("service", AlipayConfig.service);
	        sParaTemp.put("partner", AlipayConfig.partner);
	        sParaTemp.put("seller_id", AlipayConfig.seller_id);
	        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
			sParaTemp.put("payment_type", AlipayConfig.payment_type);
			sParaTemp.put("notify_url", AlipayConfig.notify_url);
			sParaTemp.put("return_url", return_url);
			sParaTemp.put("anti_phishing_key", AlipayConfig.anti_phishing_key);
			sParaTemp.put("exter_invoke_ip", AlipayConfig.exter_invoke_ip);
			sParaTemp.put("out_trade_no", out_trade_no);
			sParaTemp.put("subject", subject);
			sParaTemp.put("total_fee", total_fee);
			sParaTemp.put("body", body);
			
			//建立请求
			String sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"post","确认");
	        
			this.json = sHtmlText;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 支付成功后的异步回调服务器方法
	 * @date 2016年12月20日 下午7:39:03
	 * @return
	 */
	public String payNotifyAsync(){
		try {
			//获取支付宝POST过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
			Map requestParams = getRequest().getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
				params.put(name, valueStr);
			}
			
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			//商户订单号
			String out_trade_no = new String(getRequest().getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

			//支付宝交易号
			String trade_no = new String(getRequest().getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

			//交易状态
			String trade_status = new String(getRequest().getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

			if(AlipayNotify.verify(params)){//验证成功
				//////////////////////////////////////////////////////////////////////////////////////////
				//请在这里加上商户的业务逻辑程序代码

				//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				
				if(trade_status.equals("TRADE_FINISHED")){
					//判断该笔订单是否在商户网站中已经做过处理
						//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
						//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
						//如果有做过处理，不执行商户的业务程序
						
					//注意：
					//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
				} else if (trade_status.equals("TRADE_SUCCESS")){
					//判断该笔订单是否在商户网站中已经做过处理
						//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
						//请务必判断请求时的total_fee、seller_id与通知时获取的total_fee、seller_id为一致的
						//如果有做过处理，不执行商户的业务程序
					
					//更新订单状态
					storeMemberManager.updateOrderStatus(OrderStatus.PAY, trade_no, "1", out_trade_no);
						
					//注意：
					//付款完成后，支付宝系统发送该交易状态通知
				}

				//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
					
				return "success";	//请不要修改或删除

				//////////////////////////////////////////////////////////////////////////////////////////
			}else{//验证失败
				return "fail";
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return "fail";
		}
	}
	
	/**
	 * @description 支付成功后，前端页面调用的return_url
	 * @date 2016年12月20日 下午8:59:32
	 * @return
	 */
	public String payReturn(){
		try {
			//获取支付宝GET过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
			Map requestParams = getRequest().getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
				params.put(name, valueStr);
			}
			
			//商户订单号
			String out_trade_no = new String(getRequest().getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

			//支付宝交易号
			String trade_no = new String(getRequest().getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

			//交易状态
			String trade_status = new String(getRequest().getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
			
			//计算得出通知验证结果
			boolean verify_result = AlipayNotify.verify(params);
			
			if(verify_result){//验证成功
				//////////////////////////////////////////////////////////////////////////////////////////
				//请在这里加上商户的业务逻辑程序代码

				//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				if(trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")){
					//判断该笔订单是否在商户网站中已经做过处理
						//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
						//如果有做过处理，不执行商户的业务程序
					
					dataObj = storeMemberManager.getCardOrderInfo(out_trade_no);

					if(dataObj !=  null && dataObj.getInt("status") != 1){
						//更新订单状态
						storeMemberManager.updateOrderStatus(OrderStatus.PAY, trade_no, "1", out_trade_no);
					}
				}
				
				dataObj.put("pay_status", "success");
			}else{
				dataObj.put("pay_status", "fail");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "wish";
	}
	
	/**
	 * @description 手机页面支付后跳转
	 * @date 2016年12月21日 下午3:53:51
	 * @return
	 */
	public String payReturnForAppPage(){
		try {
			//获取支付宝GET过来反馈信息
			Map<String,String> params = new HashMap<String,String>();
			Map requestParams = getRequest().getParameterMap();
			for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String[] values = (String[]) requestParams.get(name);
				String valueStr = "";
				for (int i = 0; i < values.length; i++) {
					valueStr = (i == values.length - 1) ? valueStr + values[i]
							: valueStr + values[i] + ",";
				}
				//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
				//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
				params.put(name, valueStr);
			}
			
			//商户订单号
			String out_trade_no = new String(getRequest().getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

			//支付宝交易号
			String trade_no = new String(getRequest().getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

			//交易状态
			String trade_status = new String(getRequest().getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
			
			//计算得出通知验证结果
			boolean verify_result = AlipayNotify.verify(params);
			
			if(verify_result){//验证成功
				//////////////////////////////////////////////////////////////////////////////////////////
				//请在这里加上商户的业务逻辑程序代码

				//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				if(trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")){
					//判断该笔订单是否在商户网站中已经做过处理
						//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
						//如果有做过处理，不执行商户的业务程序
					
					dataObj = storeMemberManager.getCardOrderInfo(out_trade_no);

					if(dataObj !=  null && dataObj.getInt("status") != 1){
						//更新订单状态
						storeMemberManager.updateOrderStatus(OrderStatus.PAY, trade_no, "1", out_trade_no);
					}
				}
				
				dataObj.put("pay_status", "success");
			}else{
				dataObj.put("pay_status", "fail");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "wish_app";
	}
	
	/**
	 * @description 微信支付
	 * @date 2017年1月5日 下午2:37:56
	 * @return
	 */
	public String payNotifyByWechat(){
		try {
			HttpServletRequest request = getRequest();
			HttpServletResponse response = getResponse();
			System.out.println("-------payNotifyByWechat--------");
			
			// 读取xml
			InputStream inputStream;
			StringBuffer sb = new StringBuffer();
			inputStream = request.getInputStream();
			String s;
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ((s = in.readLine()) != null) {
				sb.append(s);
			}
			in.close();
			inputStream.close();
			
			SortedMap<Object, Object> packageParams = PayCommonUtil.xmlConvertToMap(sb.toString());
			logger.info(packageParams); 
			
			// 账号信息
			String key = PayConfigUtil.API_KEY; // key
			
			String resXml="";//反馈给微信服务器
			// 验签
			if (PayCommonUtil.isTenpaySign("UTF-8", packageParams, key)) {
				//appid openid mch_id is_subscribe nonce_str product_id sign
				
				//统一下单
				String openid = (String)packageParams.get("openid");
	            String product_id = (String)packageParams.get("product_id");
	            
	            //解析product_id，计算价格等
	            dataObj = storeMemberManager.getCardOrderInfo(product_id);
				System.out.println("order_price   "+dataObj.getDouble("total_price")*100);
				
	    		String out_trade_no = product_id; // 订单号  
	    		String order_price = "1"; // 价格   注意：价格的单位是分  
//	    		String order_price = ""+dataObj.getDouble("total_price")*100;
//	    		order_price = order_price.substring(0, order_price.indexOf("."));
	            String body = "中信订单";   // 商品名称  这里设置为product_id
	            String attach = "XXX店"; //附加数据
	            
	    		String nonce_str0 = PayCommonUtil.getNonce_str();
	    		
	            // 获取发起电脑 ip  
	            String spbill_create_ip = PayConfigUtil.CREATE_IP;    
	            String trade_type = "NATIVE"; 
	    		
	            
	            SortedMap<Object,Object> unifiedParams = new TreeMap<Object,Object>();  
	            unifiedParams.put("appid", PayConfigUtil.APP_ID); // 必须
	            unifiedParams.put("mch_id", PayConfigUtil.MCH_ID); // 必须
	            unifiedParams.put("out_trade_no", out_trade_no); // 必须
	            unifiedParams.put("product_id", product_id);
	            unifiedParams.put("body", body); // 必须
	            unifiedParams.put("attach", attach);
	            unifiedParams.put("total_fee", order_price);  // 必须 
	            unifiedParams.put("nonce_str", nonce_str0);  // 必须
	            unifiedParams.put("spbill_create_ip", spbill_create_ip); // 必须 
	            unifiedParams.put("trade_type", trade_type); // 必须  
	            unifiedParams.put("openid", openid);  
	            unifiedParams.put("notify_url", PayConfigUtil.NOTIFY_URL);//异步通知url
	            
	            String sign0 = PayCommonUtil.createSign("UTF-8", unifiedParams,key);  
	            unifiedParams.put("sign", sign0); //签名
	            
	            String requestXML = PayCommonUtil.getRequestXml(unifiedParams);  
	            logger.info(requestXML);
	            //统一下单接口
	            String rXml = HttpUtil.postData(PayConfigUtil.UFDODER_URL, requestXML);  
	            
	            //统一下单响应
	            SortedMap<Object, Object> reParams = PayCommonUtil.xmlConvertToMap(rXml);
	    		logger.info(reParams); 
	    		
	    		//验签
	    		if (PayCommonUtil.isTenpaySign("UTF-8", reParams, key)) {
	    			// 统一下单返回的参数
	    			String prepay_id = (String)reParams.get("prepay_id");//交易会话标识  2小时内有效
	    			
	        		String nonce_str1 = PayCommonUtil.getNonce_str();
	    			
	    			SortedMap<Object,Object> resParams = new TreeMap<Object,Object>();  
	    			resParams.put("return_code", "SUCCESS"); // 必须
	    			resParams.put("return_msg", "OK");
	    			resParams.put("appid", PayConfigUtil.APP_ID); // 必须
	    			resParams.put("mch_id", PayConfigUtil.MCH_ID);
	    			resParams.put("nonce_str", nonce_str1); // 必须
	    			resParams.put("prepay_id", prepay_id); // 必须
	    			resParams.put("result_code", "SUCCESS"); // 必须
	    			resParams.put("err_code_des", "OK");
	    			
	    			String sign1 = PayCommonUtil.createSign("UTF-8", resParams,key);  
	    			resParams.put("sign", sign1); //签名
	    			
	    			resXml = PayCommonUtil.getRequestXml(resParams);
	    			logger.info(resXml);
	                
	    		}else{
	    			logger.info("签名验证错误");
	    			resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"  
	                        + "<return_msg><![CDATA[签名验证错误]]></return_msg>" + "</xml> "; 
	    		}
	            
			}else{
				logger.info("签名验证错误");
				resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"  
	                    + "<return_msg><![CDATA[签名验证错误]]></return_msg>" + "</xml> "; 
			}

			//------------------------------  
	        //处理业务完毕  
	        //------------------------------  
	        BufferedOutputStream out = new BufferedOutputStream(  
	                response.getOutputStream());  
	        out.write(resXml.getBytes());  
	        out.flush();  
	        out.close(); 
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 微信支付后台回调方法
	 * @date 2017年1月5日 下午2:47:08
	 * @return
	 */
	public String payReNotifyByWechat(){
		try {
			HttpServletRequest request = getRequest();
			HttpServletResponse response = getResponse();
			
			System.out.println("-------payReNotifyByWechat--------");
			
			// 读取参数
			InputStream inputStream;
			StringBuffer sb = new StringBuffer();
			inputStream = request.getInputStream();
			String s;
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ((s = in.readLine()) != null) {
				sb.append(s);
			}
			in.close();
			inputStream.close();

			SortedMap<Object, Object> packageParams = PayCommonUtil.xmlConvertToMap(sb.toString());
			logger.info(packageParams);

			// 账号信息
			String key = PayConfigUtil.API_KEY; // key

			String resXml = ""; // 反馈给微信服务器
			// 判断签名是否正确
			if (PayCommonUtil.isTenpaySign("UTF-8", packageParams, key)) {
				// ------------------------------
				// 处理业务开始
				// ------------------------------
				if ("SUCCESS".equals((String) packageParams.get("result_code"))) {
					// 这里是支付成功
					////////// 执行自己的业务逻辑////////////////
					String mch_id = (String) packageParams.get("mch_id");
					String openid = (String) packageParams.get("openid");
					String is_subscribe = (String) packageParams.get("is_subscribe");
					String out_trade_no = (String) packageParams.get("out_trade_no");

					String total_fee = (String) packageParams.get("total_fee");

					logger.info("mch_id:" + mch_id);
					logger.info("openid:" + openid);
					logger.info("is_subscribe:" + is_subscribe);
					logger.info("out_trade_no:" + out_trade_no);
					logger.info("total_fee:" + total_fee);

					////////// 执行自己的业务逻辑////////////////
					//更新订单状态
					storeMemberManager.updateOrderStatus(OrderStatus.PAY, out_trade_no, "2", out_trade_no);

					logger.info("支付成功");
					// 通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
					resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
							+ "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

				} else {
					logger.info("支付失败,错误信息：" + packageParams.get("err_code"));
					resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
							+ "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
				}

			} else {
				logger.info("签名验证错误");
				resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"  
	                    + "<return_msg><![CDATA[签名验证错误]]></return_msg>" + "</xml> "; 
			}

			// ------------------------------
			// 处理业务完毕
			// ------------------------------
			BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
			out.write(resXml.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 查询订单支付状态
	 * @date 2017年1月5日 下午4:19:55
	 * @return
	 */
	public String getOrderStatus(){
		try {
			String order_sn = getRequest().getParameter("order_sn");
			
			dataObj = storeMemberManager.getCardOrderInfo(order_sn);

			this.json = dataObj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 获取订单物流信息
	 * @date 2016年12月27日 上午11:54:40
	 * @return
	 */
	public String getTransInfo(){
		try {
			String order_sn = getRequest().getParameter("order_sn");
			
			JSONObject returnObj = new JSONObject();
			JSONObject trans_info = null;
			
			orderMap = zxhbOrderManager.getSpecificsBySn(order_sn);
			orderMap.put("spec_image", UploadUtil.replacePath((String)orderMap.get("spec_image")));
			orderMap.put("create_time_date",DateUtil.toString((long)orderMap.get("create_time"), "yyyy-MM-dd hh:mm:ss"));
			String shipping_type = (String)orderMap.get("shipping_type");
			String shipping_no = (String)orderMap.get("shipping_no");
			if(!StringUtil.isNull(shipping_type)&&!StringUtil.isNull(shipping_no) ){
				KdniaoTrackQueryAPI kdniaoTrackQueryAPI = new KdniaoTrackQueryAPI();
				String result = kdniaoTrackQueryAPI.getOrderTracesByJson(shipping_type, shipping_no);
				JSONObject shippingResult = JSONObject.fromObject(result);
				boolean success = (boolean)shippingResult.get("Success");
				if(success){//成功
					orderMap.put("shippingList",shippingResult.getJSONArray("Traces"));
					JSONArray shippingResultArray = shippingResult.getJSONArray("Traces");
					trans_info = shippingResultArray.getJSONObject(shippingResultArray.size()-1);
					
					returnObj.put("result", 1);
					returnObj.put("data", trans_info);
				}else{
					returnObj.put("result", 0);
					returnObj.put("message", "没有查询到物流信息");
				}
			}else{
				returnObj.put("result", 0);
				returnObj.put("message", "没有物流信息");
			}
			
			this.json = returnObj.toString();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("获取物流信息失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 保存新年心愿
	 * @date 2016年12月31日 下午4:39:10
	 * @return
	 */
	public String saveWishes(){
		try {
			String wisher = getRequest().getParameter("wisher");
			String wisher_telephone = getRequest().getParameter("wisher_telephone");
			String wish_items = getRequest().getParameter("wish_items");
			String order_sn = getRequest().getParameter("order_sn");

			storeMemberManager.saveWishes(wisher, wisher_telephone, wish_items);
			
			dataObj = storeMemberManager.getCardOrderInfo(order_sn);
			
			showSuccessJson("许愿成功！感谢您的配合，我们一定竭尽全力助您达成新年愿望！");
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			showErrorJson("服务器错误，请稍后再试");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 提现密码找回
	 * @date 2016年9月21日 下午2:54:15
	 * @return String
	 */
	public String cashingoutPasswordGetBack() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Member member = storeMemberManager.getStoreMember();
		int storeId = member.getStore_id();
		String phone = request.getParameter("phone");
		String checkCode = request.getParameter("checkCode");
		String forget_NewPassword = request.getParameter("forget_NewPassword");
		
		if(StringUtils.isEmpty(phone)){
			this.showErrorJson("手机号码不能为空");
			return JSON_MESSAGE;
		}
		if(StringUtils.isEmpty(checkCode)){
			this.showErrorJson("验证码不能为空");
			return JSON_MESSAGE;
		}
		if(StringUtils.isEmpty(forget_NewPassword)){
			this.showErrorJson("新密码不能为空");
			return JSON_MESSAGE;
		}
		
		//从session中获取验证码
		String mobileCode = (String) ThreadContextHolder.getSessionContext().getAttribute("mobileCode");
		String mobile = (String) ThreadContextHolder.getSessionContext().getAttribute("mobile");
		
		//检验验证码是否正确
		if(!phone.equals(mobile)) {
			this.showErrorJson("手机号码与获取验证码的手机号码不一致");
			return JSON_MESSAGE;
		}
		if(!checkCode.equals(mobileCode)) {
			this.showErrorJson("验证码错误，请重新获取验证码");
			return JSON_MESSAGE;
		}
		
		//保存新密码
		Store store = new Store();
		store.setStore_id(storeId);
		store.setCashingout_password(StringUtil.md5(forget_NewPassword));
		store.setCashingouter_phone(phone);
		
		try {
			//保存数据
			int result = storeManager.modifyCashingoutPassword(store);
			if(result ==1 ) {
				this.showSuccessJson("尊敬的盈驾客户，您已成功修改【提现密码】，请勿泄漏，如有疑问请致电客服电话：010-64860393。【盈驾】");
			} else {
				this.showErrorJson("提现密码修改失败");
			}
		} catch (Exception e) {
			this.showErrorJson("提现密码修改失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	/*
	 * -----------------------------------------店铺信息修改----------------------------------------
	 */
	/**
	 * @description 修改商家信息--模块1
	 * @date 2016年9月22日 上午1:06:14
	 * @return String
	 */
	public String editStore_1() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Member member = storeMemberManager.getStoreMember();
		int storeId = member.getStore_id();
		String store_name = request.getParameter("store_name");
		String store_province = request.getParameter("store_province");
		String store_city = request.getParameter("store_city");
		String store_region = request.getParameter("store_region");
		String store_province_id = request.getParameter("store_province_id");
		String store_city_id = request.getParameter("store_city_id");
		String store_region_id = request.getParameter("store_region_id");
		String attr = request.getParameter("attr");
		String zip = request.getParameter("zip");
		String customer_phone = request.getParameter("customer_phone");
		String store_logo = request.getParameter("store_logo");
		
		if(StringUtils.isEmpty(store_name)) {
			this.showErrorJson("店铺名称不能为空");
			return JSON_MESSAGE; 
		}
		
		Store store = new Store();
		store.setStore_id(storeId);
		store.setStore_name(store_name);
		store.setStore_province(store_province);
		store.setStore_city(store_city);
		store.setStore_region(store_region);
		store.setStore_regionid(StringUtil.isEmpty(store_region_id) ? 0 : Integer.valueOf(store_region_id));
		store.setStore_cityid(StringUtil.isEmpty(store_city_id) ? 0 : Integer.valueOf(store_city_id));
		store.setStore_provinceid(StringUtil.isEmpty(store_province_id) ? 0 : Integer.valueOf(store_province_id));
		store.setAttr(attr);
		store.setZip(zip);
		store.setCustomer_phone(customer_phone);
		if(StringUtils.isNotEmpty(store_logo)){
			store.setStore_logo(store_logo);
		}
		
		try {
			int result = storeManager.updateStore(store);
			if(result == 1) {
				this.showSuccessJson("信息修改成功");
			} else {
				this.showErrorJson("信息修改失败");
			}
		} catch (Exception e) {
			this.showErrorJson("信息修改失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE; 
	}
	
	/**
	 * @description 修改商家信息--模块2
	 * @date 2016年9月22日 上午1:06:56
	 * @return String
	 */
	public String editStore_2() {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			Member member = storeMemberManager.getStoreMember();
			int storeId = member.getStore_id();
			String contacts_name = request.getParameter("contacts_name");
			String tel = request.getParameter("tel");
			String office_phone = request.getParameter("office_phone");
			String email = request.getParameter("email");
			String id_img = request.getParameter("contacts_id_img");
			String contact_idimg_back = request.getParameter("contact_idimg_back");
				
			Store store = new Store();
			store.setStore_id(storeId);
			store.setContacts_name(contacts_name);
			store.setTel(tel);
			store.setOffice_phone(office_phone);
			store.setEmail(email);
			if(StringUtils.isNotEmpty(id_img)){
				store.setId_img(id_img);
			}
			if(StringUtils.isNotEmpty(contact_idimg_back)){
				store.setContact_idimg_back(contact_idimg_back);
			}
			
			int result = storeManager.updateStore(store);
			if(result == 1) {
				this.showSuccessJson("信息修改成功");
			} else {
				this.showErrorJson("信息修改失败");
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSON_MESSAGE; 
	}
	
	/**
	 * @description 修改商家信息--模块3
	 * @date 2016年9月22日 上午1:06:56
	 * @return String
	 */
	public String editStore_3() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Member member = storeMemberManager.getStoreMember();
		int storeId = member.getStore_id();
		String corporation = request.getParameter("corporation");
		String corporation_id_number = request.getParameter("corporation_id_number");
		String bussiness_license_number = request.getParameter("bussiness_license_number");
		String corporation_id_img = request.getParameter("corporation_img");
		String license_img = request.getParameter("licenseImg");
		int brand_id = Integer.parseInt(request.getParameter("brand"));
		
		//获取品牌名称
		String brand_name = storeManager.queryBrandNameByBrandId(brand_id);
			
		Store store = new Store();
		store.setStore_id(storeId);
		store.setCorporation(corporation);
		store.setCorporation_id_number(corporation_id_number);
		store.setBussiness_license_number(bussiness_license_number);
		store.setBrand_id(brand_id);
		store.setBrand_name(brand_name);
		if(StringUtils.isNotEmpty(license_img)){
			store.setLicense_img(license_img);
		}
		if(StringUtils.isNotEmpty(corporation_id_img)){
			store.setCorporation_id_img(corporation_id_img);
		}
		
		try {
			int result = storeManager.updateStore(store);
			if(result == 1) {
				this.showSuccessJson("信息修改成功");
			} else {
				this.showErrorJson("信息修改失败");
			}
		} catch (Exception e) {
			this.showErrorJson("信息修改失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE; 
	}
	
	/**
	 * @description 修改商家信息--模块4
	 * @date 2016年9月22日 上午1:06:56
	 * @return String
	 */
	public String editStore_4() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Member member = storeMemberManager.getStoreMember();
		int storeId = member.getStore_id();
		String bank_account_name = request.getParameter("bank_account_name");
		String bank_account_number = request.getParameter("bank_account_number");
		String bank_name = request.getParameter("bank_name");
		String bank_attr = request.getParameter("bank_attr");
		String alipay_account = request.getParameter("alipay_account");
		String weichat_account = request.getParameter("weichat_account");
		String bank_license_img = request.getParameter("bank_license_img");
			
		Store store = new Store();
		store.setStore_id(storeId);
		store.setBank_account_name(bank_account_name);
		store.setBank_account_number(bank_account_number);
		store.setBank_attr(bank_attr);
		store.setAlipay_account(alipay_account);
		store.setWeichat_account(weichat_account);
		store.setBank_name(bank_name);
		store.setAuditstatus("1");//待审核
		if(StringUtils.isNotEmpty(bank_license_img)) {
			store.setBank_license_img(bank_license_img);
		}
		
		try {
			int result = storeManager.updateStore(store);
			if(result == 1) {
				this.showSuccessJson("信息修改成功");
			} else {
				this.showErrorJson("信息修改失败");
			}
		} catch (Exception e) {
			this.showErrorJson("信息修改失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE; 
	}
	
	/**
	 * @description 修改商家信息--模块5
	 * @date 2016年9月22日 上午1:22:37
	 * @return
	 */
	public String editStore_5() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		Member member = storeMemberManager.getStoreMember();
		int storeId = member.getStore_id();
		String cashingouter_phone = request.getParameter("cashingouter_phone");
		String checkCode = request.getParameter("checkCode");
		
		//从session中获取验证码
		String mobileCode = (String) ThreadContextHolder.getSessionContext().getAttribute("mobileCode");
		String mobile = (String) ThreadContextHolder.getSessionContext().getAttribute("mobile");
		
		//检验验证码是否正确
		if(!cashingouter_phone.equals(mobile)) {
			this.showErrorJson("手机号码与获取验证码的手机号码不一致");
			return JSON_MESSAGE;
		}
		if(!checkCode.equals(mobileCode)) {
			this.showErrorJson("验证码错误，请重新获取验证码");
			return JSON_MESSAGE;
		}
		
		Store store = new Store();
		store.setStore_id(storeId);
		store.setCashingouter_phone(cashingouter_phone);
		
		try {
			int result = storeManager.updateStore(store);
			if(result == 1) {
				this.showSuccessJson("信息修改成功");
			} else {
				this.showErrorJson("信息修改失败");
			}
		} catch (Exception e) {
			this.showErrorJson("信息修改失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE; 
	}	
	/*
	 * -----------------------------------------店铺信息编辑----------------------------------------
	 */
	
	/**
	 * @description 查询店铺审核结果
	 * @date 2016年10月26日 下午5:29:09
	 * @return String
	 */
	public String queryStoreAuditResult() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		int store_id = Integer.parseInt(request.getParameter("store_id"));
		Map<String,String> auditResult = storeManager.queryStoreAuditResult(store_id);
		if(ValidateUtils.isEmpty(auditResult)) {
			this.showErrorJson("未查到该店铺的审核信息");
		} else {
			this.showSuccessJson("店铺审核结果查询成功", JsonUtil.MapToJson(auditResult));
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 查询审核驳回理由
	 * @date 2016年10月26日 下午7:56:38
	 * @return String
	 */
	public String queryStoreAuditReason() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		int store_id = Integer.parseInt(request.getParameter("store_id"));
		try {
			fileReason = storeManager.queryStoreAuditReason(store_id);
			if(StringUtils.isEmpty(fileReason)) {
				fileReason = "原因不明";
			}
			this.showSuccessJson(fileReason);
		} catch (Exception e) {
			this.showSuccessJson(fileReason);
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * ================================================================================
	 * GETTER AND SETTER
	 */
	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getLogo() {
		return logo;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	public IStoreManager getStoreManager() {
		return storeManager;
	}
	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
	public File getId_img() {
		return id_img;
	}
	public void setId_img(File id_img) {
		this.id_img = id_img;
	}
	public File getLicense_img() {
		return license_img;
	}
	public void setLicense_img(File license_img) {
		this.license_img = license_img;
	}
	public String getId_imgFileName() {
		return id_imgFileName;
	}
	public void setId_imgFileName(String id_imgFileName) {
		this.id_imgFileName = id_imgFileName;
	}
	public String getLicense_imgFileName() {
		return license_imgFileName;
	}
	public void setLicense_imgFileName(String license_imgFileName) {
		this.license_imgFileName = license_imgFileName;
	}
	public String getId_number() {
		return id_number;
	}
	public void setId_number(String id_number) {
		this.id_number = id_number;
	}
	public String getFsid_img() {
		return fsid_img;
	}
	public void setFsid_img(String fsid_img) {
		this.fsid_img = fsid_img;
	}
	public String getFslicense_img() {
		return fslicense_img;
	}
	public void setFslicense_img(String fslicense_img) {
		this.fslicense_img = fslicense_img;
	}
	public String getStatus_id_img() {
		return status_id_img;
	}
	public void setStatus_id_img(String status_id_img) {
		this.status_id_img = status_id_img;
	}
	public String getStatus_license_img() {
		return status_license_img;
	}
	public void setStatus_license_img(String status_license_img) {
		this.status_license_img = status_license_img;
	}

	public Integer getStore_id() {
		return store_id;
	}

	public void setStore_id(Integer store_id) {
		this.store_id = store_id;
	}

	public Integer getStore_auth() {
		return store_auth;
	}

	public void setStore_auth(Integer store_auth) {
		this.store_auth = store_auth;
	}

	public Integer getName_auth() {
		return name_auth;
	}

	public void setName_auth(Integer name_auth) {
		this.name_auth = name_auth;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RepairTimeRegion getRepairTimeRegion() {
		return repairTimeRegion;
	}

	public void setRepairTimeRegion(RepairTimeRegion repairTimeRegion) {
		this.repairTimeRegion = repairTimeRegion;
	}

	public File getData() {
		return data;
	}

	public void setData(File data) {
		this.data = data;
	}

	public String getDataFileName() {
		return dataFileName;
	}

	public void setDataFileName(String dataFileName) {
		this.dataFileName = dataFileName;
	}

	public String getDataContentType() {
		return dataContentType;
	}

	public void setDataContentType(String dataContentType) {
		this.dataContentType = dataContentType;
	}

	public String getFileReason() {
		return fileReason;
	}

	public void setFileReason(String fileReason) {
		this.fileReason = fileReason;
	}

	public JSONObject getDataObj() {
		return dataObj;
	}

	public void setDataObj(JSONObject dataObj) {
		this.dataObj = dataObj;
	}

	public Map getOrderMap() {
		return orderMap;
	}

	public void setOrderMap(Map orderMap) {
		this.orderMap = orderMap;
	}

	public IZxhbOrderManager getZxhbOrderManager() {
		return zxhbOrderManager;
	}

	public void setZxhbOrderManager(IZxhbOrderManager zxhbOrderManager) {
		this.zxhbOrderManager = zxhbOrderManager;
	}

}
