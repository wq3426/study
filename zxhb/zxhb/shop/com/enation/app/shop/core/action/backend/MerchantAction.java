package com.enation.app.shop.core.action.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MerchantInfo;
import com.enation.app.base.core.model.Store;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.IMerchantManager;
import com.enation.app.shop.core.plugin.member.MemberPluginBundle;
import com.enation.app.shop.core.service.store.IStoreShopManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * @Description admin----商户管理
 *
 * @createTime 2016年8月30日 下午5:09:33
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/b2b2c/admin")
@Action("merchant")
@Results({
	@Result(name="list_merchant_store", type="freemarker", location="/shop/admin/merchant/merchant_list_store.html"),	
	@Result(name="register", type="freemarker", location="/shop/admin/merchant/register.html"),
	@Result(name="register_admin", type="freemarker", location="/shop/admin/merchant/register_admin.html"),
	@Result(name="edit", type="freemarker", location="/shop/admin/merchant/merchant_edit.html")	
})
public class MerchantAction extends WWAction {
	
	private static final long serialVersionUID = 1L;
	
	private IMerchantManager merchantManager;
	private MemberPluginBundle memberPluginBundle;
	private IMemberManager memberManager;
	private IStoreShopManager storeShopManager;
	private List<MerchantInfo> merchantList;
	private Integer[] id;
	private MerchantInfo merchantInfo;
	
	/**
	 * @description 跳转至商户列表
	 * @date 2016年8月30日 下午4:26:54
	 * @return String 
	 */
	public String merchantListStore() {	
		merchantList = merchantManager.queryMerchantInfoList();
		return "list_merchant_store";
	}

	/**
	 * @description 查询商户信息（分页）
	 * @date 2016年8月26日 下午2:52:15
	 * @return String
	 */
	public String merchantInfoListJson() {
		
		//获取基础数据
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String page = request.getParameter("page")==null?"1": request.getParameter("page");
		String searchKeyword = request.getParameter("searchKeyword");
		String companyName = request.getParameter("companyName");
		String brand = request.getParameter("brand");
		String contactName = request.getParameter("contactName");
		String contactPhone = request.getParameter("contactPhone");
		String email = request.getParameter("email");
		String address = request.getParameter("address");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		
		int pageSize = this.getPageSize();;//每页显示的条数
		int pageNum = Integer.parseInt(page);
		Map<String,String> conditions = new HashMap<>();
		conditions.put("searchKeyword", searchKeyword);
		conditions.put("companyName", companyName);
		conditions.put("brand", brand);
		conditions.put("email", email);
		conditions.put("contactName", contactName);
		conditions.put("contactPhone", contactPhone);
		conditions.put("address", address);
		conditions.put("startTime", startTime);
		conditions.put("endTime", endTime);
		
		//查询商户信息List
		Page merchantInfo = merchantManager.queryMerchantInfo(pageNum, pageSize, conditions);
		
		//响应数据
        this.showGridJson(merchantInfo);
		return JSON_MESSAGE;	
	}
	
	/**
	 * @description 删除商户信息（逻辑删除）
	 * @date 2016年9月2日 下午4:31:18
	 * @return String
	 */
	public String merchantDelete(){
		
		try {
			//删除商户信息
			int deleteResult = merchantManager.merchantDelete(id);
			if(deleteResult == 1) {
				this.showSuccessJson("删除成功");
			} else if(deleteResult == 2) {
				this.showErrorJson("请选择要删除的商家信息");
			} else {
				this.showErrorJson("删除失败");
			}
		} catch (Exception e) {
			logger.info("商户信息删除失败");
			this.showErrorJson("删除失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 修改商户信息
	 * @date 2016年9月3日 下午7:40:33
	 * @return String
	 */
	public String merchantEdit(){
		//获取基础数据
	    HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	    int merchantId = Integer.parseInt(request.getParameter("merchantId"));
	    
	    merchantInfo = merchantManager.queryMerchantInfo(merchantId);
		merchantList = merchantManager.queryMerchantInfoList();
		return "edit";
	}
	
	/**
	 * @description 保存修改的信息
	 * @date 2016年9月5日 下午4:33:12
	 * @return String
	 */
	public String saveMerchantEdit(){
		
		//获取基础数据
	    HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	    int merchantId = Integer.parseInt(request.getParameter("merchantId"));
	    String company_name = request.getParameter("company_name");
	    String brand = request.getParameter("brand");
	    String contact_name = request.getParameter("contact_name");
	    String contact_phone = request.getParameter("contact_phone");
	    String email = request.getParameter("email");
	    String address = request.getParameter("address");
	    
	    //校验公司名称和邮箱是否为空
  		if(StringUtils.isEmpty(company_name)) {
  			this.showErrorJson("公司名称不能为空！");
  			return JSON_MESSAGE;
  		}
  		if(StringUtils.isEmpty(email)) {
  			this.showErrorJson("邮箱不能为空！");
  			return JSON_MESSAGE;
  		}
  		
  		//封装数据
	    MerchantInfo merchantInfo = new MerchantInfo();
	    merchantInfo.setId(merchantId);
	    merchantInfo.setCompany_name(company_name);
	    merchantInfo.setBrand(brand);
	    merchantInfo.setContact_name(contact_name);
	    merchantInfo.setContact_phone(contact_phone);
	    merchantInfo.setEmail(email);
	    merchantInfo.setAddress(address);
	    
	    try {
			//更新数据
			merchantManager.saveMerchantEdit(merchantInfo);
			this.showSuccessJson("修改成功");		   
		} catch (Exception e) {
			logger.info("商户信息修改失败");
			this.showErrorJson("商户信息修改失败");
			e.printStackTrace();
		}
	    return JSON_MESSAGE;
	}
	
	/**
	 * @description 校验注册邮箱是否重复
	 * @date 2016年9月5日 下午4:41:16
	 * @return String
	 */
	public String checkEmailIsRepeat() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String email = request.getParameter("email");
		if(StringUtils.isEmpty(email)) {
			this.showErrorJson("邮箱不能为空！");
			return JSON_MESSAGE;
		}
		
		//校验邮箱是否重复
	    int result = merchantManager.checkEmailIsRepeat(email);
	    if(result==0) {
			this.showSuccessJson("邮箱可以使用！");
		} else {
			this.showErrorJson("该邮箱已被注册！");
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 显示注册页面
	 * @date 2016年9月5日 下午6:16:51
	 * @return String
	 */
	public String showMermberRegister() {
		//获取基础数据
	    HttpServletRequest request = ThreadContextHolder.getHttpRequest();
	    int merchantId = Integer.parseInt(request.getParameter("merchantId"));
	    
	    merchantInfo = merchantManager.queryMerchantInfo(merchantId);
		merchantList = merchantManager.queryMerchantInfoList();
		return "register";
	}
	
	/**
	 * @description 商家注册
	 * @date 2016年9月5日 下午5:34:04
	 * @return String
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public String mermberRegister() {
		
		//获取数据
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String registerip = request.getRemoteAddr();
		
		//校验注册邮箱和密码是否为空，密码长度是否符合要求
		if(StringUtil.isEmpty(email)) {
			this.showErrorJson("注册邮箱不能为空！");
			return JSON_MESSAGE;
		}
		if(StringUtil.isEmpty(password)) {
			this.showErrorJson("注册密码不能为空！");
			return JSON_MESSAGE;
		}			
		if (password.length() < 6 || password.length() > 16) {
			this.showErrorJson("密码的长度为4-20个字符！");
			return JSON_MESSAGE;
		}
		
		//检验注册邮箱是否重复
		int checkResult = merchantManager.checkEmailIsRepeat(email);
	    if(!(checkResult==0)) {
			this.showErrorJson("该邮箱已被注册！");
			return JSON_MESSAGE;
	    }
	    
		//封装数据
		Member member = new Member();
		member.setUsername(email);
		member.setName(email);
		member.setEmail(email);
		member.setPassword(StringUtil.md5(password));
		member.setLv_id(1);				 	
		member.setRegisterip(registerip);	
		member.setNickname(email);			
		member.setPoint(0);
		member.setAdvance(0D);
		member.setRegtime(DateUtil.getDateline());	
		member.setLastlogin(DateUtil.getDateline());	
		member.setLogincount(0);			
		member.setMp(0);
		member.setFace("");
		member.setMidentity(0);
		member.setSex(1);
		member.setIs_store(1);
		
		try {
			//保存数据
			int memberId = merchantManager.saveMemberRegisterInfo(member);	
			
			//自动开店
			Store store = new Store();
			store.setMember_id(memberId);
			store.setMember_name(member.getName());
			store.setDisabled(1);
			store.setEmail(email);
			store.setStore_provinceid(0);
			store.setStore_cityid(0);
			store.setStore_regionid(0);
			store.setStore_auth(0);
			store.setName_auth(0);
			store.setStore_level(1);
			store.setAuditstatus("0");
			store.setBrand_id(0);
			store.setCreate_time(DateUtil.getDateline());
			int storeId = merchantManager.opeanStore(store);
			
			//更新用户表
			Map<String,Integer> map = new HashMap<>();
			map.put("is_store", 1);
			map.put("store_id", storeId);
			int updateMemberresult = merchantManager.updateMember(map, memberId);
			if(updateMemberresult !=1) {
				this.showErrorJson("自动开店失败");
				return JSON_MESSAGE;
			}
			
			//设置店铺余额
			int initStoreBalanceResult = merchantManager.initStoreBalance(storeId);
			if(initStoreBalanceResult !=1) {
				this.showErrorJson("初始化店铺余额失败");
				return JSON_MESSAGE;
			}
			
			//更改商户表状态为已注册
			boolean emailIsExist = merchantManager.checkEmailIsExist(email);
			if(emailIsExist) {
				merchantManager.updateMerchantStatus(email);
			}
			//发送营销免费使用
			store.setStore_id(storeId);
			storeShopManager.addStoreUseData(store);
			
			this.showSuccessJson("注册成功！");
			
		} catch (Exception e) {
			logger.error("商户注册失败");
			this.showErrorJson("注册失败！");
			e.printStackTrace();
		} finally {
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 
	 * @date 2016年9月14日 上午11:35:18
	 * @return
	 */
	public String showMermberRegisterAdmin() {
		
		return "register_admin";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * ===================================================================================
	 * getter and setter
	 */
	public IMerchantManager getMerchantManager() {
		return merchantManager;
	}
	public void setMerchantManager(IMerchantManager merchantManager) {
		this.merchantManager = merchantManager;
	}
	public List<MerchantInfo> getMerchantList() {
		return merchantList;
	}
	public void setMerchantList(List<MerchantInfo> merchantList) {
		this.merchantList = merchantList;
	}
	public Integer[] getId() {
		return id;
	}
	public void setId(Integer[] id) {
		this.id = id;
	}
	public MerchantInfo getMerchantInfo() {
		return merchantInfo;
	}
	public void setMerchantInfo(MerchantInfo merchantInfo) {
		this.merchantInfo = merchantInfo;
	}
	public MemberPluginBundle getMemberPluginBundle() {
		return memberPluginBundle;
	}
	public void setMemberPluginBundle(MemberPluginBundle memberPluginBundle) {
		this.memberPluginBundle = memberPluginBundle;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public IStoreShopManager getStoreShopManager() {
		return storeShopManager;
	}

	public void setStoreShopManager(IStoreShopManager storeShopManager) {
		this.storeShopManager = storeShopManager;
	}
	
}
