package com.enation.app.shop.core.action.backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.model.PointHistory;
import com.enation.app.shop.core.plugin.member.MemberPluginBundle;
import com.enation.app.shop.core.service.IAdvanceLogsManager;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IPointHistoryManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.resource.IUserManager;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.eop.sdk.utils.ValidateUtils;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

import cn.jiguang.commom.utils.StringUtils;
/**
 * 会员管理Action
 * @author LiFenLong 2014-4-1;4.0版本改造   
 *
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("member")
@Results({
	@Result(name="add_lv", type="freemarker", location="/shop/admin/member/lv_add.html"),
	@Result(name="edit_lv", type="freemarker", location="/shop/admin/member/lv_edit.html"),
	@Result(name="list_lv", type="freemarker", location="/shop/admin/member/lv_list.html"),
	@Result(name="add_member", type="freemarker", location="/shop/admin/member/member_add.html"),
	@Result(name="edit_member", type="freemarker", location="/shop/admin/member/member_edit.html"),
	@Result(name="list_member", type="freemarker", location="/shop/admin/member/member_list.html"),
	@Result(name="incomeDetail", type="freemarker", location="/shop/admin/member/car_income_list.html"),
	@Result(name="bonusDetail", type="freemarker", location="/shop/admin/member/car_bonus_list.html"),
	@Result(name="carInfoEdit", type="freemarker", location="/shop/admin/member/member_car_edit.html"),
	@Result(name="list_member_store", type="freemarker", location="/shop/admin/member/member_list_store.html"), 
	@Result(name="detail", type="freemarker", location="/shop/admin/member/member_detail.html"),
	@Result(name="cardetail", type="freemarker", location="/shop/admin/member/member_car_detail.html"),
	@Result(name="base",  location="/shop/admin/member/member_base.jsp"),
	@Result(name="edit",  location="/shop/admin/>member/member_edit.jsp"),
	@Result(name="orderLog",  location="/shop/admin/member/member_orderLog.jsp"),
	@Result(name="editPoint", location="/shop/admin/member/member_editPoint.jsp"),
	@Result(name="pointLog",  location="/shop/admin/member/member_pointLog.jsp"),
	@Result(name="advance",   location="/shop/admin/member/member_advance.jsp"),
	@Result(name="comments",  location="/shop/admin/member/member_comments.jsp"),
	@Result(name="remark",  location="/shop/admin/member/member_remark.jsp"),
	@Result(name="changeMemberPassword",  location="/shop/admin/member/member_change_password.html"),
	@Result(name="syslogin",  location="/shop/admin/member/syslogin.jsp")
})
@SuppressWarnings({ "rawtypes", "unchecked", "serial","static-access" })
public class MemberAction extends WWAction {

	private IMemberManager memberManager;
	private IMemberLvManager memberLvManager;
	private IRegionsManager regionsManager;
 
	private IPointHistoryManager pointHistoryManager;
	private IAdvanceLogsManager advanceLogsManager;
	private IMemberCommentManager memberCommentManager;
	private MemberPluginBundle memberPluginBundle; 
	
	private IUserManager userManager ;
	private Member member;
	private MemberLv lv;
	private String birthday;
	private Integer[] lv_id;
	private Integer memberId;
	private Integer[] member_id;
	private List lvlist;
	private List provinceList;
	private List cityList;
	private List regionList;
 
	private List listPointHistory;
	private List listAdvanceLogs;
	private List listComments;
	private int point;
	private int pointtype; //积分类型
	private Double modify_advance;
	private String modify_memo;
	private String object_type;
	private String name;
	private String uname;
	private String mobile;
	private String email;
	private String carplate;
	private Integer sex=2;
	private Integer lvId;
	private List catDiscountList;
	private int[] cat_ids;
	private int[] cat_discounts;
	private Map memberMap;
	private Map carInfo;
	private String start_time;
	private String end_time;
	private Integer stype;
	private String keyword;
	private Integer province_id;
	private Integer city_id;
	private Integer region_id;
	private int isshopkeeper;
	private List<Map> memberCarInfoList;
	private List<Map> carUsetypeList;
	private List<Map> brand_List;
	private List<Map> incomeDetailList;
	private List<Map> bonusDetailList;
	private List<String> seriesList;
	private List<String> nianKuanList;
	private double memberCarTotalgain;
	private int bonusCount;

	//详细页面插件返回的数据 
	//protected Map<Integer,String> pluginTabs;
	//protected Map<Integer,String> pluginHtmls;
	
	private Map statusMap;
	private String status_Json;
	
	/**
	 * 跳转至添加会员等级页面
	 * @return 添加会员等级页面
	 */
	public String add_lv() {
		return "add_lv";
	}
	
	/**
	 * 跳转至修改会员等级页面
	 * @param lvId 会员等级Id
	 * @param lv 会员等级
	 * @return 修改会员等级页面
	 */
	public String edit_lv() {
		lv = this.memberLvManager.get(lvId);
		return "edit_lv";
	}
	
	/**
	 * 跳转至会员等级列表
	 * @return 会员等级列表
	 */
	public String list_lv() {
		return "list_lv";
	}
	
	/**
	 * 获取会员等级列表Json
	 * @return 会员等级列表Json
	 */
	public String list_lvJson() {
		this.webpage = memberLvManager.list(this.getSort(), this.getPage(), this.getPageSize());
		this.showGridJson(webpage);
		return JSON_MESSAGE;
	}
	
	/**
	 * 添加会员等级
	 * @param lv 会员等级,MemberLv
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveAddLv() {
		memberLvManager.add(lv);
		this.showSuccessJson("会员等级添加成功");
		return JSON_MESSAGE;
	}
	/**
	 * 修改会员等级
	 * @param lv 会员等级,MemberLv
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveEditLv() {
		
		try{
			memberLvManager.edit(lv);
			this.showSuccessJson("会员等级修改成功");
		}catch (Exception e) {
			this.showErrorJson("非法参数");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 删除会员等级
	 * @param lv_id,会员等级Id,Integer
	 * @return  result
	 * result 1.操作成功.0.操作失败
	 */
	public String deletelv() {
		try {
			this.memberLvManager.delete(lv_id);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败："+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	/**
	 * 跳转至添加会员页面
	 * @param lvlist  会员等级列表,List
	 * @return 添加会员页面
	 */
	public String add_member() {
		if(lvlist==null){			
			lvlist = this.memberLvManager.list();
		}
		provinceList = this.regionsManager.listProvince();
		return "add_member";
	}
	/**
	 * 跳转至修改会员页面
	 * @param memberId 会员Id,Integer
	 * @param member 会员,Member
	 * @param lvlist 会员等级列表,List
	 * @return 修改会员页面
	 */
	public String edit_member() {
		member = this.memberManager.get(memberId);
		if(lvlist==null){			
			lvlist = this.memberLvManager.list();
		}
		return "edit_member";
	}
	/**
	 * 跳转至会员列表
	 * @param lvlist 会员等级列表,List
	 * @return 会员列表
	 */
	public String memberlist() {
		return "list_member";
	}
	
	/**
	 * 跳转至会员列表
	 * @param lvlist 会员等级列表,List
	 * @return 会员列表
	 */
	public String memberlistStore() {
		lvlist = this.memberLvManager.list();
		return "list_member_store";
	}
	
	/**
	 * @description 显示会员列表
	 * @date 2016年10月17日 下午3:26:17
	 * @return String
	 */
	public String memberlistJson() {
		
		//获取基础数据
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String page = request.getParameter("page")==null?"1": request.getParameter("page");
		String searchType = request.getParameter("searchType");
		String searchKeyword = request.getParameter("keyword");
		String start_time_login = request.getParameter("start_time_login");
		String end_time_login = request.getParameter("end_time_login");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String username = request.getParameter("uname");
		String nickname = request.getParameter("nickname");
		String email = request.getParameter("email");
		
		int pageSize= this.getPageSize();
		int pageNum = Integer.parseInt(page);
		Map<String,String> conditions = new HashMap<>();
		conditions.put("searchType", searchType);
		conditions.put("searchKeyword", searchKeyword);
		conditions.put("start_time", start_time);
		conditions.put("end_time", end_time);
		conditions.put("start_time_login", start_time_login);
		conditions.put("end_time_login", end_time_login);
		conditions.put("email", email);
		conditions.put("username", username);
		conditions.put("nickname", nickname);
			
		//查询商户信息List
		Page memberInfo = null;
		try {
			memberInfo = memberManager.queryMemberInfoByPage(pageNum, pageSize, conditions);
		} catch (Exception e) {
			e.getStackTrace();
		}
		
		//响应数据
        this.showGridJson(memberInfo);
		return JSON_MESSAGE;
	} 
	
	/**
	 * 修改会员
	 * @param birthday 生日,String
	 * @param oldMember 修改前会员,Member
	 * @param member 修改后会员,Member
	 * @param province 省份,String
	 * @param city 城市,String
	 * @param region 地区,String
	 * @param province_id 省份Id,Integer
	 * @param city_id 城市Id,Integer
	 * @param region_id 地区Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String saveEditMember() {
			long birth = DateUtil.getDateline(birthday);	//将生日日期转换为秒数
			
			//如果生日日期小于当前日期，则允许修改操作，否则不允许	修改人：DMRain 2015-12-17
			if(birth < DateUtil.getDateline()){
				try {
					member.setBirthday(birth);
					
					Member oldMember = this.memberManager.get(member.getMember_id());
					
					HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
					String province = request.getParameter("province");
					String city = request.getParameter("city");
					String region = request.getParameter("region");
					
					String province_id = request.getParameter("province_id");
					String city_id = request.getParameter("city_id");
					String region_id = request.getParameter("region_id");
					
					if(!StringUtil.isEmpty(province)){
						oldMember.setProvince(province);
					}
					if(!StringUtil.isEmpty(province)){
						oldMember.setProvince(city);
					}
					if(!StringUtil.isEmpty(province)){
						oldMember.setProvince(region);
					}
					
					if(!StringUtil.isEmpty(province_id)){
						oldMember.setProvince_id(StringUtil.toInt(province_id,true));
					}
					
					if(!StringUtil.isEmpty(city_id)){
						oldMember.setCity_id(StringUtil.toInt(city_id,true));
					}
					
					if(!StringUtil.isEmpty(province_id)){
						oldMember.setRegion_id(StringUtil.toInt(region_id,true));
					}
					if(!StringUtil.isEmpty(member.getPassword())){
						oldMember.setPassword(StringUtil.md5(member.getPassword()));
					}
					oldMember.setName(member.getName());
					oldMember.setSex(member.getSex());
					oldMember.setBirthday(member.getBirthday());
					oldMember.setEmail(member.getEmail());
					oldMember.setTel(member.getTel());
					oldMember.setMobile(member.getMobile());
					oldMember.setLv_id(member.getLv_id());
					oldMember.setZip(member.getZip());
					oldMember.setAddress(member.getAddress());
					oldMember.setQq(member.getQq());
					oldMember.setMsn(member.getMsn());
					oldMember.setPw_answer(member.getPw_answer());
					oldMember.setPw_question(member.getPw_question());
					this.memberManager.edit(oldMember);
					this.showSuccessJson("修改成功");
				} catch (Exception e) {
					this.showErrorJson("修改失败");
				}
			}else{
				this.showErrorJson("生日不可以大于当前日期！");
			}
		return this.JSON_MESSAGE;

	}
	/**
	 * 删除会员
	 * @param member_id 会员Id,Integer
	 * @return result
	 * result 1.操作成功.0.操作失败
	 */
	public String delete() {
		try {
			this.memberManager.delete(member_id);
			this.showSuccessJson("删除成功");
		} catch (RuntimeException e) {
			this.showErrorJson("删除失败"+e.getMessage());
		}
		return this.JSON_MESSAGE;
	}

	
	/**
	 * 跳转至会员详细页面
	 * @param memberId 会员Id,Integer
	 * @param member 会员,Member
	 * @param pluginTabs tab列表,List<Map>
	 * @param pluginHtmls tab页Html内容,List<Map>
	 * @return 会员详细页面
	 */
	public String detail() {
		
		//this.member = this.memberManager.get(memberId);
		//pluginTabs = this.memberPluginBundle.getTabList(member);
		//pluginHtmls=this.memberPluginBundle.getDetailHtml(member);
		return "detail";
	}
	
	public String editPoint() {
		member = this.memberManager.get(memberId);
		return "editPoint";
	}

	public String editSavePoint() {
		member = this.memberManager.get(memberId);
		member.setPoint(member.getPoint() + point);
		PointHistory pointHistory = new PointHistory();
		pointHistory.setMember_id(memberId);
		pointHistory.setOperator("管理员");
		pointHistory.setPoint(point);
		pointHistory.setReason("管理员手工修改");
		pointHistory.setTime(DateUtil.getDateline());
		try {
			memberManager.edit(member);
			pointHistoryManager.addPointHistory(pointHistory);
			this.showSuccessJson("会员积分修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败");
			e.printStackTrace();
		}
		return this.JSON_MESSAGE;
	}

	public String pointLog() {
		listPointHistory = pointHistoryManager.listPointHistory(memberId,pointtype);
		member = this.memberManager.get(memberId);
		return "pointLog";
	}

	public String advance() {
		member = this.memberManager.get(memberId);
		listAdvanceLogs = this.advanceLogsManager
				.listAdvanceLogsByMemberId(memberId);
		return "advance";
	}

	public String comments() {
		Page page = memberCommentManager.getMemberComments(1, 100, StringUtil.toInt(object_type), memberId);
		if(page != null){
			listComments = (List)page.getResult();
		}
		return "comments";
	}

	/**
	 * 保存添加会员
	 * @author xulipeng
	 * @param member 会员,Member
	 * @param province 省份,String
	 * @param city 城市,String
	 * @param region 地区,String
	 * @param province_id  省份Id,Integer
	 * @param city_id 城市Id,Integer
	 * @param region_id 地区Id,Integer
	 * @param birthday 生日,String
	 * @return result
	 * 2014年4月1日18:22:50
	 */
	public String saveMember() {
		if(StringUtil.isEmpty(member.getUsername())){
			this.showErrorJson("用户名为空!");
			return JSON_MESSAGE;
		}
		int result = memberManager.checkname(member.getUsername());
		if (result == 1) {
			this.showErrorJson("用户名已存在");
			return JSON_MESSAGE;
		}
		if (member != null) {
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			String province = request.getParameter("province");
			String city = request.getParameter("city");
			String region = request.getParameter("region");
			
			String province_id = request.getParameter("province_id");
			String city_id = request.getParameter("city_id");
			String region_id = request.getParameter("region_id");
			
			
			member.setProvince(province);
			member.setCity(city);
			member.setRegion(region);
			
			if(!StringUtil.isEmpty(province_id)){
				member.setProvince_id( StringUtil.toInt(province_id,true));
			}
			
			if(!StringUtil.isEmpty(city_id)){
				member.setCity_id(StringUtil.toInt(city_id,true));
			}
			
			if(!StringUtil.isEmpty(province_id)){
				member.setRegion_id(StringUtil.toInt(region_id,true));
			}
			
			member.setBirthday(DateUtil.getDateline(birthday));
			member.setPassword(member.getPassword());
			member.setRegtime(DateUtil.getDateline());// lzf add
			memberManager.add(member);
			this.showSuccessJson("保存会员成功",member.getMember_id());
			
		} 
		return JSON_MESSAGE;
	}
	
	/**
	 * 获取订单状态的json
	 * @param OrderStatus 订单状态
	 * @return
	 */
	private Map getStatusJson(){
		Map orderStatus = new  HashMap();
		orderStatus.put(""+OrderStatus.ORDER_NOT_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_PAY));
		orderStatus.put(""+OrderStatus.ORDER_NOT_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_NOT_CONFIRM));
		orderStatus.put(""+OrderStatus.ORDER_PAY_CONFIRM, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY_CONFIRM));
		orderStatus.put(""+OrderStatus.ORDER_ALLOCATION_YES, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ALLOCATION_YES));
//		orderStatus.put(""+OrderStatus.ORDER_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SHIP));
//		orderStatus.put(""+OrderStatus.ORDER_ROG, OrderStatus.getOrderStatusText(OrderStatus.ORDER_ROG));
		orderStatus.put(""+OrderStatus.ORDER_SERVECE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_SERVECE));
		orderStatus.put(""+OrderStatus.ORDER_APPRAISE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_APPRAISE));
		orderStatus.put(""+OrderStatus.ORDER_CANCEL_SHIP, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_SHIP));
		orderStatus.put(""+OrderStatus.ORDER_COMPLETE, OrderStatus.getOrderStatusText(OrderStatus.ORDER_COMPLETE));
		orderStatus.put(""+OrderStatus.ORDER_CANCEL_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCEL_PAY));
		orderStatus.put(""+OrderStatus.ORDER_CANCELLATION, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CANCELLATION));
		orderStatus.put(""+OrderStatus.ORDER_CHANGED, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGED));
		orderStatus.put(""+OrderStatus.ORDER_CHANGE_APPLY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_CHANGE_APPLY));
		orderStatus.put(""+OrderStatus.ORDER_RETURN_APPLY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_RETURN_APPLY));
		orderStatus.put(""+OrderStatus.ORDER_PAY, OrderStatus.getOrderStatusText(OrderStatus.ORDER_PAY));
		return orderStatus;
	}
	
	
	//修改会员备注
	public String editRemark(){
		
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String modify_memo = request.getParameter("modify_memo");
		int memberid  = StringUtil.toInt(request.getParameter("memberid"),true);
		Member member = this.memberManager.get(memberid);
		member.setRemark(modify_memo);
		try {
			memberManager.edit(member);
			this.showSuccessJson("会员备注修改成功");
		} catch (Exception e) {
			this.logger.error("修改会员备注",e);
			this.showErrorJson("会员备注修改失败");
		}
		
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 显示会员详情页面
	 * @date 2016年10月18日 下午3:34:40
	 * @return String
	 */
	public String memberDetail() {
		member = memberManager.get(memberId);
		String face = member.getFace();
		
		//图片路径转换
		if(StringUtils.isNotEmpty(face)) {
			face = UploadUtil.replacePath(face);
			member.setFace(face);
		}
		return "detail";
	}
	
	/**
	 * @description 显示会员汽车详情页面
	 * @date 2016年10月18日 下午3:46:23
	 * @return String
	 */
	public String memberCarDetail() {
		//查询车辆信息
		memberCarInfoList = memberManager.queryMemberCarInfo(memberId);
		if(ValidateUtils.isNotEmpty(memberCarInfoList)) {
			for (Map<String,Object> map : memberCarInfoList) {
				String carplate = (String) map.get("carplate");
				if(StringUtils.isEmpty(carplate)) {
					map.put("bonusCount", 0);
					continue;
				}
				carplate = carplate.toLowerCase();//车牌号转为小写
				//查询优惠券数量
				bonusCount = memberManager.queryBonusCount(carplate);
				map.put("bonusCount", bonusCount);
			}
		}
		return "cardetail";
	}
	
	/**
	 * @description 保存车辆修改数据
	 * @date 2016年10月18日 下午6:12:34
	 * @return String
	 */
	public String saveCarInfo() {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String carinfoid_String = request.getParameter("carinfoid");
			String carmodelid_String = request.getParameter("carmodelid");
			String carplate = request.getParameter("carplate");
			int brand_id = Integer.parseInt(request.getParameter("brandId"));
			String series = request.getParameter("series");
			String nk = request.getParameter("nk");
			String carengineno = request.getParameter("carengineno");
			String carvin = request.getParameter("carvin");
			String car_use_type_String = request.getParameter("car_use_type");
			
			int carinfoid = Integer.parseInt(carinfoid_String);
			int carmodelid = Integer.parseInt(carmodelid_String);	
			if(StringUtils.isEmpty(carplate)) {
				this.showErrorJson("车牌号不能为空");
				return JSON_MESSAGE;
			}
			carplate = carplate.toLowerCase();//车牌号转小写保存
			//查询品牌名称
			String brand = memberManager.queryBrandName(brand_id);
			
			Map params = new HashMap<>();
		
			//更新carinfo表中的信息
			params.put("carplate", carplate);
			params.put("carvin", carvin);
			params.put("carengineno", carengineno);
			params.put("car_use_type_String", car_use_type_String);
			//更新carmodels表中的信息
			params.put("brand", brand);
			params.put("series", series);
			params.put("nk", nk);
			memberManager.updateCarInfoAndCarModel(carinfoid, carmodelid, brand_id, params);
									
			this.showSuccessJson("保存成功");
		} catch (Exception e) {
			this.showErrorJson("保存失败");
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 显示车辆奖励详情页面
	 * @date 2016年10月20日 下午2:07:19
	 * @return String
	 */
	public String showCarIncomeDetail() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		carplate = request.getParameter("carplate");
		return "incomeDetail";
	}
	
	/**
	 * @description 显示车辆优惠券详情页面
	 * @date 2016年10月20日 下午2:08:31
	 * @return String
	 */
	public String showCarBonusDetail() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		carplate = request.getParameter("carplate");
		return "bonusDetail";
	}
	
	/**
	 * @description 奖励信息列表
	 * @date 2016年10月20日 下午2:49:23
	 * @return String
	 */
	public String carIncomeDetailListJson() {	
		//获取基础数据
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String page = request.getParameter("page")==null?"1": request.getParameter("page");
		String carplate = request.getParameter("carplate").toLowerCase();
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		
		int pageSize= this.getPageSize();
		int pageNum = Integer.parseInt(page);
		Map<String,String> conditions = new HashMap<>();
		
		conditions.put("start_time", start_time);
		conditions.put("end_time", end_time);		
		conditions.put("carplate", carplate);		
			
		//查询收益详情List
		Page incomeInfo = null;
		try {
			incomeInfo = memberManager.queryCarIncomeDetail(pageNum, pageSize, conditions);
		} catch (Exception e) {
			e.getStackTrace();
		}
		
		//响应数据
        this.showGridJson(incomeInfo);
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 查询车辆优惠券详情
	 * @date 2016年10月20日 下午2:46:57
	 * @return String
	 */
	public String carBonusDetailListJson() {
		//获取基础数据
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String page = request.getParameter("page")==null?"1": request.getParameter("page");
		String carplate = request.getParameter("carplate").toLowerCase();
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");

		int pageSize= this.getPageSize();
		int pageNum = Integer.parseInt(page);
		Map<String,String> conditions = new HashMap<>();
		conditions.put("carplate", carplate);
		conditions.put("start_time", start_time);
		conditions.put("end_time", end_time);
		
		//查询商户信息List
		Page bonusInfo = null;
		try {
			bonusInfo = memberManager.queryCarBonusDetail(pageNum, pageSize, conditions);
		} catch (Exception e) {
			e.getStackTrace();
		}
		
		//响应数据
        this.showGridJson(bonusInfo);
		return JSON_MESSAGE;
	}
	
	/**
	 * @description 车辆信息--修改数据回显
	 * @date 2016年10月21日 上午10:59:15
	 * @return String
	 */
	public String showCarInfoEdit() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		carplate = request.getParameter("carplate");
		
		//查询品牌列表
		brand_List = memberManager.queryBrandList();
		
		//查询车辆使用性质
		carUsetypeList = memberManager.queryCarUseType();
		
		//根据车牌号查询车辆信息
		carInfo = memberManager.queryCarInfoByCarplate(carplate);
		return "carInfoEdit";
	}
		
	/**
	 * @description 查询车系、年款列表
	 * @date 2016年10月22日 下午4:08:24
	 * @return String
	 */
	public String getCarSeriesOrNKList() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String brand_id = request.getParameter("brand_id");
		String series = request.getParameter("series");
		
		//根据品牌id查询车系列表
		if(!StringUtils.isEmpty(brand_id)) {
			seriesList = memberManager.queryCarSeriesOrNKList("brand_id",brand_id);
			this.showSuccessJson("查询成功",JsonUtil.ListToJson(seriesList));
		}
			
		//根据品牌、类型获取车系列表
		if(!StringUtil.isEmpty(brand_id) && !StringUtil.isEmpty(series)){
			nianKuanList = memberManager.queryCarSeriesOrNKList("series", brand_id, series);
			this.showSuccessJson("查询成功",JsonUtil.ListToJson(nianKuanList));
		}
		return JSON_MESSAGE;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * ===========================================================================
	 * GETTER AND SETTER
	 */
	public MemberLv getLv() {
		return lv;
	}

	public void setLv(MemberLv lv) {
		this.lv = lv;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public IMemberManager getMemberManager() {
		return memberManager;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public Integer[] getLv_id() {
		return lv_id;
	}

	public void setLv_id(Integer[] lv_id) {
		this.lv_id = lv_id;
	}

	public Integer getLvId() {
		return lvId;
	}

	public void setLvId(Integer lvId) {
		this.lvId = lvId;
	}

	public List getLvlist() {
		return lvlist;
	}

	public void setLvlist(List lvlist) {
		this.lvlist = lvlist;
	}

	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}

	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}

	public List getProvinceList() {
		return provinceList;
	}

	public void setProvinceList(List provinceList) {
		this.provinceList = provinceList;
	}

	public List getCityList() {
		return cityList;
	}

	public void setCityList(List cityList) {
		this.cityList = cityList;
	}

	public List getRegionList() {
		return regionList;
	}

	public void setRegionList(List regionList) {
		this.regionList = regionList;
	}

 

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public IPointHistoryManager getPointHistoryManager() {
		return pointHistoryManager;
	}

	public void setPointHistoryManager(IPointHistoryManager pointHistoryManager) {
		this.pointHistoryManager = pointHistoryManager;
	}

	public List getListPointHistory() {
		return listPointHistory;
	}

	public void setListPointHistory(List listPointHistory) {
		this.listPointHistory = listPointHistory;
	}

	public IAdvanceLogsManager getAdvanceLogsManager() {
		return advanceLogsManager;
	}

	public void setAdvanceLogsManager(IAdvanceLogsManager advanceLogsManager) {
		this.advanceLogsManager = advanceLogsManager;
	}

	public List getListAdvanceLogs() {
		return listAdvanceLogs;
	}

	public void setListAdvanceLogs(List listAdvanceLogs) {
		this.listAdvanceLogs = listAdvanceLogs;
	}

	public Double getModify_advance() {
		return modify_advance;
	}

	public void setModify_advance(Double modifyAdvance) {
		modify_advance = modifyAdvance;
	}

	public String getModify_memo() {
		return modify_memo;
	}

	public void setModify_memo(String modifyMemo) {
		modify_memo = modifyMemo;
	}

	public List getListComments() {
		return listComments;
	}

	public void setListComments(List listComments) {
		this.listComments = listComments;
	}

	public String getObject_type() {
		return object_type;
	}

	public void setObject_type(String objectType) {
		object_type = objectType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUname() {
		return uname;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}
 
	public IUserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(IUserManager userManager) {
		this.userManager = userManager;
	}

	public List getCatDiscountList() {
		return catDiscountList;
	}

	public void setCatDiscountList(List catDiscountList) {
		this.catDiscountList = catDiscountList;
	}

	public int[] getCat_ids() {
		return cat_ids;
	}

	public void setCat_ids(int[] cat_ids) {
		this.cat_ids = cat_ids;
	}

	public int[] getCat_discounts() {
		return cat_discounts;
	}

	public void setCat_discounts(int[] cat_discounts) {
		this.cat_discounts = cat_discounts;
	}

	public void setMemberCommentManager(IMemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}

	public int getPointtype() {
		return pointtype;
	}

	public void setPointtype(int pointtype) {
		this.pointtype = pointtype;
	}

	/*public Map<Integer, String> getPluginTabs() {
		return pluginTabs;
	}

	public void setPluginTabs(Map<Integer, String> pluginTabs) {
		this.pluginTabs = pluginTabs;
	}

	public Map<Integer, String> getPluginHtmls() {
		return pluginHtmls;
	}

	public void setPluginHtmls(Map<Integer, String> pluginHtmls) {
		this.pluginHtmls = pluginHtmls;
	}*/

	public MemberPluginBundle getMemberPluginBundle() {
		return memberPluginBundle;
	}

	public void setMemberPluginBundle(MemberPluginBundle memberPluginBundle) {
		this.memberPluginBundle = memberPluginBundle;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer[] getMember_id() {
		return member_id;
	}

	public void setMember_id(Integer[] member_id) {
		this.member_id = member_id;
	}
 
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getSex() {
		return sex;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public Map getMemberMap() {
		return memberMap;
	}

	public void setMemberMap(Map memberMap) {
		this.memberMap = memberMap;
	}

	public IMemberCommentManager getMemberCommentManager() {
		return memberCommentManager;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public double getMemberCarTotalgain() {
		return memberCarTotalgain;
	}

	public void setMemberCarTotalgain(double memberCarTotalgain) {
		this.memberCarTotalgain = memberCarTotalgain;
	}

	public Integer getStype() {
		return stype;
	}

	public void setStype(Integer stype) {
		this.stype = stype;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getProvince_id() {
		return province_id;
	}

	public void setProvince_id(Integer province_id) {
		this.province_id = province_id;
	}

	public Integer getCity_id() {
		return city_id;
	}

	public void setCity_id(Integer city_id) {
		this.city_id = city_id;
	}

	public Integer getRegion_id() {
		return region_id;
	}

	public void setRegion_id(Integer region_id) {
		this.region_id = region_id;
	}

	public Map getStatusMap() {
		return statusMap;
	}

	public void setStatusMap(Map statusMap) {
		this.statusMap = statusMap;
	}

	public String getStatus_Json() {
		return status_Json;
	}

	public void setStatus_Json(String status_Json) {
		this.status_Json = status_Json;
	}

	public int getIsshopkeeper() {
		return isshopkeeper;
	}
	public void setIsshopkeeper(int isshopkeeper) {
		this.isshopkeeper = isshopkeeper;
	}

	public List<Map> getMemberCarInfoList() {
		return memberCarInfoList;
	}

	public void setMemberCarInfoList(List<Map> memberCarInfoList) {
		this.memberCarInfoList = memberCarInfoList;
	}

	public List<Map> getCarUsetypeList() {
		return carUsetypeList;
	}

	public void setCarUsetypeList(List<Map> carUsetypeList) {
		this.carUsetypeList = carUsetypeList;
	}

	public List<Map> getBrand_List() {
		return brand_List;
	}

	public void setBrand_List(List<Map> brand_List) {
		this.brand_List = brand_List;
	}

	public List<Map> getIncomeDetailList() {
		return incomeDetailList;
	}

	public void setIncomeDetailList(List<Map> incomeDetailList) {
		this.incomeDetailList = incomeDetailList;
	}

	public List<Map> getBonusDetailList() {
		return bonusDetailList;
	}

	public void setBonusDetailList(List<Map> bonusDetailList) {
		this.bonusDetailList = bonusDetailList;
	}

	public String getCarplate() {
		return carplate;
	}

	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}

	public int getBonusCount() {
		return bonusCount;
	}

	public void setBonusCount(int bonusCount) {
		this.bonusCount = bonusCount;
	}

	public Map getCarInfo() {
		return carInfo;
	}

	public void setCarInfo(Map carInfo) {
		this.carInfo = carInfo;
	}

	public List<String> getSeriesList() {
		return seriesList;
	}

	public void setSeriesList(List<String> seriesList) {
		this.seriesList = seriesList;
	}

	public List<String> getNianKuanList() {
		return nianKuanList;
	}

	public void setNianKuanList(List<String> nianKuanList) {
		this.nianKuanList = nianKuanList;
	}

	
	

	
	
	
}
