package com.enation.app.b2b2c.core.action.backend.store;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreLevelManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.Store;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

import cn.jiguang.commom.utils.StringUtils;
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({
	 @Result(name="store_list",type="freemarker", location="/b2b2c/admin/store/store_list.html"),
	 @Result(name="audit_list",type="freemarker", location="/b2b2c/admin/store/audit_list.html"),
	 @Result(name="license_list",type="freemarker", location="/b2b2c/admin/store/license_list.html"),
	 @Result(name="disStore_list",type="freemarker", location="/b2b2c/admin/store/disStore_list.html"),
	 @Result(name="edit",type="freemarker", location="/b2b2c/admin/store/store_edit.html"),
	 @Result(name="add",type="freemarker", location="/b2b2c/admin/store/store_add.html"),
	 @Result(name="opt",type="freemarker", location="/b2b2c/admin/store/opt_member.html"),
	 @Result(name="pass",type="freemarker", location="/b2b2c/admin/store/pass.html"),
	 @Result(name="auth_list",type="freemarker", location="/b2b2c/admin/store/auth_list.html")
})
@Action("store")
/**
 * 店铺管理
 * @author LiFenLong
 *
 */
public class StoreAction extends WWAction{
	private IStoreLevelManager storeLevelManager;
	private IStoreManager storeManager;
	private IStoreMemberManager storeMemberManager;
	private IMemberManager memberManager;
	private Map other;
	private Integer disabled; 
	private Integer storeId;
	private Store store;
	private List level_list;
	private List brand_list;
	
	private Integer member_id;
	private Integer pass;
	private Integer name_auth;
	private Integer store_auth;
	private String storeName;
	private String name;
	private String uname;
	private String password;
	private Integer assign_password;
	
	private Double commission;
	/**
	 * 店铺列表
	 * @return
	 */
	public String store_list(){
		return "store_list";
	}
	
	/**
	 * 开店申请
	 * @return
	 */
	public String audit_list(){
		return "audit_list";
	}
	
	/**
	 * 店铺认证审核列表
	 * @return
	 */
	public String license_list(){
		return "license_list";
	}
	
	/**
	 * 禁用店铺列表
	 * @return
	 */
	public String disStore_list(){
		return "disStore_list";
	}
	
	/**
	 * 审核店铺
	 * @return
	 */
	public String pass(){
		store= this.storeManager.getStore(storeId);
		if(store.getName_auth()==2){
			store.setId_img(UploadUtil.replacePath(store.getId_img()));
		}
		if(store.getStore_auth()==2){
			store.setLicense_img( UploadUtil.replacePath(store.getLicense_img()));
		}
		return "pass";
	}
	
	/**
	 * @description 显示店铺列表
	 * @date 2016年10月9日 上午11:25:24
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	public String store_listJson(){
		other = new HashMap<>();
		other.put("disabled", disabled);
		other.put("name", storeName);
		
		Page store_list = storeManager.store_list(other,disabled,this.getPage(),this.getPageSize());
		this.showGridJson(store_list);
		return JSON_MESSAGE;
	}
	
	/**
	 * 审核通过
	 * @return
	 */
	public String audit_pass(){
		try {
			storeManager.audit_pass(member_id, storeId, pass, name_auth, store_auth,commission);
			this.showSuccessJson("操作成功");
		} catch (Exception e) {
			this.showErrorJson("审核失败");
			this.logger.error("操作失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 禁用店铺
	 * @return
	 */
	public String disStore(){
		if(EopSetting.IS_DEMO_SITE){
			this.showErrorJson(EopSetting.DEMO_SITE_TIP);
			return this.JSON_MESSAGE;
		}
		
		try {
			storeManager.disStore(storeId);
			this.showSuccessJson("店铺关闭成功");
		} catch (Exception e) {
			this.showErrorJson("店铺关闭失败");
			this.logger.error("店铺关闭失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 店铺恢复使用
	 * @return
	 */
	public String useStore(){
		try {
			storeManager.useStore(storeId);
			this.showSuccessJson("店铺恢复使用成功");
		} catch (Exception e) {
			this.showErrorJson("店铺恢复使用失败");
			this.logger.error("店铺恢复使用失败"+e);
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 添加店铺
	 * @return
	 */
	public String save(){
		try {
			store=new Store();
			store = this.assign();
			this.storeManager.save(store);
			this.showSuccessJson("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("保存失败");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 注册店铺
	 * @return
	 */
	public String registStore() {
		try {
			store = new Store();
			store = this.assign(); // 设置商店信息
			Member member = this.assignMem(); // 设置会员信息
//			member.setPassword("123456"); // 会员默认密码
			
			// 注册商店,同时注册会员
			this.storeManager.registStore(store, member);
			
			this.showSuccessJson("保存成功！");
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("保存失败");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 显示修改店铺页面
	 * @date 2016年10月10日 下午4:40:23
	 * @return
	 */
	public String edit(){
		store = this.storeManager.getStore(storeId);
		
		//图片路径转换
		String id_img = store.getId_img();
		String store_logo = store.getStore_logo();
		String bank_license_img = store.getBank_license_img();
		String license_img = store.getLicense_img();
		String corporation_id_img = store.getCorporation_id_img();
		String contact_idimg_back = store.getContact_idimg_back();
		String corporate_idimg_back = store.getCorporate_idimg_back();
		
		if(StringUtils.isNotEmpty(bank_license_img)) {
			bank_license_img = UploadUtil.replacePath(bank_license_img);
			store.setBank_license_img(bank_license_img);
		}
		if(StringUtils.isNotEmpty(store_logo)) {
			store_logo = UploadUtil.replacePath(store_logo);
			store.setStore_logo(store_logo);
		}
		if(StringUtils.isNotEmpty(id_img)) {
			id_img = UploadUtil.replacePath(id_img);
			store.setId_img(id_img);
		}
		if(StringUtils.isNotEmpty(corporation_id_img)) {
			corporation_id_img = UploadUtil.replacePath(corporation_id_img);
			store.setCorporation_id_img(corporation_id_img);
		}
		if(StringUtils.isNotEmpty(license_img)) {
			license_img = UploadUtil.replacePath(license_img);
			store.setLicense_img(license_img);
		}
		if(StringUtils.isNotEmpty(contact_idimg_back)) {
			contact_idimg_back = UploadUtil.replacePath(contact_idimg_back);
			store.setContact_idimg_back(contact_idimg_back);
		}
		if(StringUtils.isNotEmpty(corporate_idimg_back)) {
			corporate_idimg_back = UploadUtil.replacePath(corporate_idimg_back);
			store.setCorporate_idimg_back(corporate_idimg_back);
		}
			
		level_list = storeLevelManager.storeLevelList();
		brand_list = storeManager.queryBrandList();
		return "edit";
	}
	/**
	 * 修改店铺信息
	 * @return
	 */
	public String saveEdit() {
		try {
			store = this.storeManager.getStore(storeId);
			Integer disable = store.getDisabled();
			Integer storeLevel = store.getStore_level();
			//store = this.assign();
			
			//处理请求数据
			store = this.assign_other();
			
			//判断店铺状态 更改店铺状态
			if(disable != store.getDisabled()){
				if(store.getDisabled() == 1){
					storeManager.useStore(storeId);
				} else {
					storeManager.disStore(storeId);
				}
			}
			//判断店铺等级是否为0，如果为0直接在统计表中添加数据，
			//不为0的话，直接修改店铺等级，然后自动任务会在月底根据等级发放对应的免费条数/栏位
			if(storeLevel != store.getStore_level()) {
				storeManager.editStoreUseData(store);
			}
			this.storeManager.editStoreInfo(store);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败，请稍后重试！");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 修改店铺信息--处理请求数据
	 * @date 2016年10月10日 上午9:46:07
	 * @return Store
	 */
	private Store assign_other() {
		HttpServletRequest request = this.getRequest();
		int store_id = Integer.parseInt(request.getParameter("storeId"));
		String store_name = request.getParameter("store_name");
		String store_logo = request.getParameter("store_logo");
		int store_province_id = Integer.parseInt(request.getParameter("store_province_id").toString());
		int store_city_id = Integer.parseInt(request.getParameter("store_city_id").toString());
		int store_region_id = Integer.parseInt(request.getParameter("store_region_id").toString());
		String store_province = request.getParameter("store_province");
		String store_city = request.getParameter("store_city");
		String store_region = request.getParameter("store_region");
		String attr = request.getParameter("attr");
		String zip = request.getParameter("zip");
		String customer_phone = request.getParameter("customer_phone");
		String contacts_name = request.getParameter("contacts_name");
		String tel = request.getParameter("tel");
		String office_phone = request.getParameter("office_phone");
		String email = request.getParameter("email");
		String id_img = request.getParameter("id_img");
		String corporation = request.getParameter("corporation");
		String corporation_id_number = request.getParameter("corporation_id_number");
		String corporation_id_img = request.getParameter("corporation_id_img");
		String bussiness_license_number = request.getParameter("bussiness_license_number");
		String license_img = request.getParameter("license_img");
		int brandId = Integer.parseInt(request.getParameter("brandId"));
		String bank_account_name = request.getParameter("bank_account_name");
		String bank_account_number = request.getParameter("bank_account_number");
		String bank_name = request.getParameter("bank_name");
		String bank_license_img = request.getParameter("bank_license_img");
		String alipay_account = request.getParameter("alipay_account");
		String weichat_account = request.getParameter("weichat_account");
		String cashingouter_phone = request.getParameter("cashingouter_phone");
		int store_level = Integer.parseInt(request.getParameter("store_level"));
		int disabled = Integer.parseInt(request.getParameter("disabled"));
		String auditstatus = request.getParameter("auditstatus");
		int settlement_period = Integer.parseInt(request.getParameter("settlement_period"));
		double discountcontract = Double.valueOf(request.getParameter("discountcontract"));
		double discountnoncontract = Double.valueOf(request.getParameter("discountnoncontract"));
		String contact_idimg_back = request.getParameter("contact_idimg_back");
		String corporate_idimg_back = request.getParameter("corporate_idimg_back");
		String docker = request.getParameter("docker");
		String docker_job_number = request.getParameter("docker_job_number");
		String docker_tel = request.getParameter("docker_tel");
		
		//图片路径转换为虚拟路径
		store_logo = UploadUtil.replacePathTo(store_logo);
		id_img = UploadUtil.replacePathTo(id_img);
		license_img = UploadUtil.replacePathTo(license_img);
		bank_license_img = UploadUtil.replacePathTo(bank_license_img);
		corporation_id_img = UploadUtil.replacePathTo(corporation_id_img);
		corporate_idimg_back = UploadUtil.replacePathTo(corporate_idimg_back);
		contact_idimg_back = UploadUtil.replacePathTo(contact_idimg_back);
		
		//查询品牌名称
		String brand_name = storeManager.queryBrandNameByBrandId(brandId);
		
		store.setStore_id(store_id);
		store.setLicense_img(license_img);
		store.setId_img(id_img);
		store.setSettlement_period(settlement_period);
		store.setStore_province(store_province);
		store.setStore_cityid(store_city_id);
		store.setBank_account_name(bank_account_name);
		store.setBank_account_number(bank_account_number);
		store.setStore_region(store_region);
		store.setStore_level(store_level);
		store.setStore_regionid(store_region_id);
		store.setStore_provinceid(store_province_id);
		store.setDisabled(disabled);
		store.setBank_name(bank_name);
		store.setTel(tel);
		store.setZip(zip);
		store.setStore_name(store_name);
		store.setStore_city(store_city);
		store.setAttr(attr);
		store.setDiscountnoncontract(discountnoncontract);
		store.setDiscountcontract(discountcontract);
		store.setEmail(email);
		store.setStore_logo(store_logo);
		store.setBrand_name(brand_name);
		store.setCashingouter_phone(cashingouter_phone);
		store.setAuditstatus(auditstatus);
		store.setCorporation(corporation);
		store.setBank_license_img(bank_license_img);
		store.setCorporation_id_img(corporation_id_img);
		store.setCustomer_phone(customer_phone);
		store.setOffice_phone(office_phone);
		store.setCorporation_id_number(corporation_id_number);
		store.setAlipay_account(alipay_account);
		store.setBussiness_license_number(bussiness_license_number);
		store.setWeichat_account(weichat_account);
		store.setContacts_name(contacts_name);
		store.setContact_idimg_back(contact_idimg_back);
		store.setCorporate_idimg_back(corporate_idimg_back);
		store.setDocker(docker);
		store.setDocker_job_number(docker_job_number);
		store.setDocker_tel(docker_tel);
		
		return store;
	}
	/**
	 * 获取会员账号信息
	 * @return
	 */
	private Member assignMem(){
		HttpServletRequest request = this.getRequest();
		Member member = new Member();
		member.setUsername(request.getParameter("member_name"));
		member.setPassword(request.getParameter("mem_pwd"));
		return member;
	}
	/**
	 * 获取店铺信息
	 * @return
	 */
	private Store assign(){
		HttpServletRequest request = this.getRequest();

		store.setMember_name(request.getParameter("member_name"));
		store.setId_number(request.getParameter("id_number"));
		store.setStore_name(request.getParameter("store_name"));
		
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
		
		store.setBank_provinceid(Integer.parseInt(request.getParameter("bank_province_id").toString())); //开户银行所在省Id
		store.setBank_cityid(Integer.parseInt(request.getParameter("bank_city_id").toString()));		  //开户银行所在市Id
		store.setBank_regionid(Integer.parseInt(request.getParameter("bank_region_id").toString()));    //开户银行所在区Id
		
		store.setBank_province(request.getParameter("bank_province"));	//开户银行所在省
		store.setBank_city(request.getParameter("bank_city"));			//开户银行所在市
		store.setBank_region(request.getParameter("bank_region"));		//开户银行所在区
 		
		store.setAttr(request.getParameter("attr"));
		store.setZip(request.getParameter("zip"));
		store.setTel(request.getParameter("tel"));
		
		store.setCommission(commission);
		store.setStore_level(Integer.parseInt(request.getParameter("store_level")));//店铺等级
		store.setDisabled(Integer.valueOf(request.getParameter("disabled")));
		
		store.setId_img(request.getParameter("id_img"));				// 身份证照片
		store.setLicense_img(request.getParameter("license_img"));		// 营业执照照片
		store.setSettlement_period(Integer.parseInt(request.getParameter("settlement_period")));
		store.setDiscountcontract(Double.valueOf(request.getParameter("discountcontract")));
		store.setDiscountnoncontract(Double.valueOf(request.getParameter("discountnoncontract")));
		
		//判断是否含有小区
		if(request.getParameter("community_id")!=null){
			store.setCommunity_id(Integer.parseInt(request.getParameter("community_id")));
			store.setCommunity(request.getParameter("community"));
		}
		return store;
	}
	
	/**
	 * 新增店铺验证用户
	 * @return
	 */
	public String opt(){
		return "opt";
	}
	/**
	 * 验证用户 
	 * @param uname 会员名称
	 * @param password 密码
	 * @param assign_password 是否验证密码 
	 * @return
	 */
	public String optMember(){
		try {
			StoreMember storeMember= storeMemberManager.getMember(uname);
			//检测是否为新添加的会员
			if(storeMember.getIs_store()==null){
				this.showSuccessJson(uname);
				return this.JSON_MESSAGE;
			}
			//判断用户是否已经拥有店铺
			if(storeMember.getIs_store()==1){
				this.showErrorJson("会员已拥有店铺");
				return this.JSON_MESSAGE;
			}
			//验证会员密码
			if(assign_password!=null&&assign_password==1){
				if(!storeMember.getPassword().equals(StringUtil.md5(password))){
					this.showErrorJson("密码不正确");
					return this.JSON_MESSAGE;
				}
			}
			if(storeMember.getIs_store()==-1){
				this.showSuccessJson(uname);
			}else{
				this.showSuccessJson("2");
			}
		} catch (Exception e) {
			this.showErrorJson("没有此用户");
		}
		return this.JSON_MESSAGE;
		
	}
	public String add(){
//		level_list=storeLevelManager.storeLevelList(); 
		return "add";
	}
	/**
	 * 跳转到申请信息页面
	 * @return
	 */
	public String auth_list(){
		return "auth_list";
	}
	public String auth_listJson(){
		this.showGridJson(storeManager.auth_list(other, disabled, this.getPage(), this.getPageSize()));
		return this.JSON_MESSAGE;
	}
	/**
	 * 审核店铺认证
	 * @param storeId 店铺Id
	 * @param name_auth 店主认证
	 * @param store_auth 店铺认证
	 */
	public String auth_pass(){
		try{
			storeManager.auth_pass(storeId, name_auth, store_auth);
			this.showSuccessJson("操作成功");
		}catch(Exception e){
			this.showErrorJson("操作失败");
			this.logger.error("审核店铺认证失败:"+e);
		}
		return this.JSON_MESSAGE;
	}
	
	

	public String sysLogin(){
		try{ 
			int r  = this.memberManager.loginbysys(name);
			// 如果成功登陆
			if(r==1){
//				EopUser user  = this.userManager.get(name);
//				if(user!=null){
//					WebSessionContext<EopUser> sessonContext = ThreadContextHolder
//					.getSessionContext();	
//					sessonContext.setAttribute(IUserManager.USER_SESSION_KEY, user);
//				}
//				return "syslogin";
				storeMemberManager.edit(storeMemberManager.getMember(UserConext.getCurrentMember().getUsername()));
			}
			this.json="<script>location.href = '"+SystemSetting.getContext_path()+"/store/business_center.html?menu=store_index'</script>";
		}catch(RuntimeException e){
			 e.printStackTrace();
		}
		
		return this.JSON_MESSAGE;
	}
	
	
	
	public IStoreLevelManager getStoreLevelManager() {
		return storeLevelManager;
	}
	public void setStoreLevelManager(IStoreLevelManager storeLevelManager) {
		this.storeLevelManager = storeLevelManager;
	}
	public IStoreManager getStoreManager() {
		return storeManager;
	}
	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}
	public Map getOther() {
		return other;
	}
	public void setOther(Map other) {
		this.other = other;
	}
	public Integer getDisabled() {
		return disabled;
	}
	public void setDisabled(Integer disabled) {
		this.disabled = disabled;
	}
	public Integer getStoreId() {
		return storeId;
	}
	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}
	public Store getStore() {
		return store;
	}
	public void setStore(Store store) {
		this.store = store;
	}
	public List getLevel_list() {
		return level_list;
	}
	public void setLevel_list(List level_list) {
		this.level_list = level_list;
	}
	public Integer getMember_id() {
		return member_id;
	}
	public void setMember_id(Integer member_id) {
		this.member_id = member_id;
	}
	public Integer getPass() {
		return pass;
	}
	public void setPass(Integer pass) {
		this.pass = pass;
	}
	public Integer getName_auth() {
		return name_auth;
	}
	public void setName_auth(Integer name_auth) {
		this.name_auth = name_auth;
	}
	public Integer getStore_auth() {
		return store_auth;
	}
	public void setStore_auth(Integer store_auth) {
		this.store_auth = store_auth;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getAssign_password() {
		return assign_password;
	}
	public void setAssign_password(Integer assign_password) {
		this.assign_password = assign_password;
	}
	public Double getCommission() {
		return commission;
	}
	public void setCommission(Double commission) {
		this.commission = commission;
	}
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public List getBrand_list() {
		return brand_list;
	}

	public void setBrand_list(List brand_list) {
		this.brand_list = brand_list;
	}
	
}
