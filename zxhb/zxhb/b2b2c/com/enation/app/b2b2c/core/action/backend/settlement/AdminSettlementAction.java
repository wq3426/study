package com.enation.app.b2b2c.core.action.backend.settlement;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.reckoning.Reckoning;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeStatus;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeType;
import com.enation.app.b2b2c.core.model.settlement.AdminSettlement;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.b2b2c.core.service.settlement.impl.IAdminSettlementManager;
import com.enation.app.shop.core.service.AdminSettlementType;
import com.enation.app.shop.core.service.OrderType;
import com.enation.eop.processor.core.Request;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.eop.processor.facade.SsoProcessor;
import com.enation.eop.resource.model.AdminUser;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

import edu.emory.mathcs.backport.java.util.TreeMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 清分结算管理
 * 
 * @author huashixin
 *
 */
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({ @Result(name = "applyList", type = "freemarker", location = "/b2b2c/admin/settlement/apply_list.html"),
		@Result(name = "settlementList", type = "freemarker", location = "/b2b2c/admin/settlement/admin_settlement_list.html"),
		@Result(name = "apply_detail", type = "freemarker", location = "/b2b2c/admin/settlement/apply_confirm.html"),
		@Result(name = "settlement_info", type = "freemarker", location = "/b2b2c/admin/settlement/settlement_info.html") ,
		@Result(name = "settlement_detail", type = "freemarker", location = "/b2b2c/admin/settlement/settlement_detail.html") })
@Action("adminSettlement")
public class AdminSettlementAction extends WWAction {

	private IReckoningManager reckoningManager;

	private IAdminSettlementManager adminSettlementManager;

	private IStoreOrderManager storeOrderManager;

	private String sn; // 流水号

	private Map applyList;

	private Map applyDraw;

	private Map settlement_big_types;

	private Map settlementList;

	private Map settlementInfo;
	
	private Map settlementDetail;
	
	private Map drawMoneyDetail;
	
	private Reckoning reckoning;
	
	private Map applyDetail;
	
	private String trade_big_type;

	public String applyList() {
		applyDraw = ReckoningTradeStatus.getApplyStatus();
		return "applyList";
	}

	public String applyListJson() {
		applyList = new HashMap();
		Page page = adminSettlementManager.apply_list(applyList, this.getPage(), this.getPageSize(), this.getSort(),
				this.getOrder());
		JSONArray jsonArray = JSONArray.fromObject(page.getResult());
		for(int i = 0 ; i < jsonArray.size() ; i++){
			JSONObject obj= jsonArray.getJSONObject(i);
			if(StringUtil.isNull(obj.getString("pay_type"))){//兼容之前为空为银行
				obj.put("account", obj.get("bank_account_number"));
			}else if(obj.getString("pay_type").equals("weichat")){
				obj.put("account", obj.get("weichat_account"));
			}else if(obj.getString("pay_type").equals("bank")){
				obj.put("account", obj.get("bank_account_number"));
			}else if(obj.getString("pay_type").equals("alipay")){
				obj.put("account", obj.get("alipay_account"));
			}
		}
		page.setResult(jsonArray.toString());
		this.showGridJson(page);
		return JSON_MESSAGE;
	}

	public String settlementList() {
		settlement_big_types = AdminSettlementType.getAdminSettlementBigTypeMap();
		return "settlementList";
	}

	public String settlementListJson() {
		settlementList = new HashMap();
		HttpServletRequest request = getRequest();
		settlementList.put("keyword", request.getParameter("keyword"));
		settlementList.put("trade_big_type", request.getParameter("trade_big_type"));
		settlementList.put("sn", request.getParameter("sn"));
		settlementList.put("start_time", request.getParameter("start_time"));
		settlementList.put("end_time", request.getParameter("end_time"));
		settlementList.put("object_name", request.getParameter("object_name"));
		Page page = adminSettlementManager.getSettlementList(settlementList, this.getPage(), this.getPageSize(),
				this.getSort(), this.getOrder());
		this.showGridJson(page);
		JSONObject object = JSONObject.fromObject(json);
		//支出多少钱和笔数
		String bigTypes = AdminSettlementType.BIG_BUY_SERVICE +","+AdminSettlementType.BIG_STORE_BUY_SERVICE;//收入类型
		settlementList.put("bigTypes",bigTypes);
		Map map = adminSettlementManager.getSettlementCountByBigType(settlementList);
		if(map!=null && !map.isEmpty()){
			object.put("income_number", map.get("number"));
			object.put("income_money", map.get("trade_money"));
		}
		//支出多少钱和笔数
		bigTypes = AdminSettlementType.BIG_DRAW_MONEY +","+AdminSettlementType.BIG_REFUND;//支出
		settlementList.put("bigTypes",bigTypes);
		map = adminSettlementManager.getSettlementCountByBigType(settlementList);
		if(map!=null && !map.isEmpty()){
			object.put("expend_number", map.get("number"));
			object.put("expend_money", map.get("trade_money"));
		}
		json = object.toString();
		return JSON_MESSAGE;
	}

	public String settlementInfo() {
		settlementInfo = new HashMap();
		// admin账户余额
		double admin_balance = adminSettlementManager.getAdminBalance();
		settlementInfo.put("admin_balance", admin_balance);
		// 所有已支付的订单总额
		double pay_order_count = adminSettlementManager.getOrderPayCount();
		settlementInfo.put("pay_order_count", pay_order_count);
		// 已结算和手续费
		Map allMap = adminSettlementManager.getSettlementAllCount();
		settlementInfo.put("all_handling_charge_count", allMap.get("handling_charge_count"));
		// 已提现总额
		double yet_draw_money = adminSettlementManager.getSettlementCountByBigType(AdminSettlementType.BIG_DRAW_MONEY);
		settlementInfo.put("yet_draw_money", yet_draw_money);
		/*
		 * //待结算金额 double noSettlementCount=
		 * adminSettlementManager.getSettlementCountByType(ReckoningTradeType.
		 * settle_accounts.getIndex(),
		 * ReckoningTradeStatus.no_settle_accounts.getIndex(), null);
		 * settlementInfo.put("noSettlementCount", noSettlementCount); //应付账款
		 * double account_payable = 0;
		 */

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String start = simpleDateFormat.format(new Date());
		String end = start + " 23:59:59";
		long startTime = DateUtil.getDateline(start);
		long endTime = DateUtil.getDateline(end, "yyyy-MM-dd HH:mm:ss");
		// 本日收入
		Map map = adminSettlementManager.getSettlementCountByDate(startTime, endTime);
		settlementInfo.put("today_income_money", map.get("income_count"));
		settlementInfo.put("today_handling_charge_count", map.get("handling_charge_count"));
		// 支出
		double today_pay_count = adminSettlementManager
				.getSettlementCountByTypeAndDate(AdminSettlementType.BIG_DRAW_MONEY, startTime, endTime);
		settlementInfo.put("today_pay_count", today_pay_count);
		settlementInfo.put("today_apply_count", today_pay_count);
		settlementInfo.put("today_refund_count", 0d);

		// 本月收入
		Long[] month = DateUtil.getCurrentMonth();
		map = adminSettlementManager.getSettlementCountByDate(month[0], month[1]);
		settlementInfo.put("month_income_money", map.get("income_count"));
		settlementInfo.put("month_handling_charge_count", map.get("handling_charge_count"));
		// 支出
		double month_pay_count = adminSettlementManager
				.getSettlementCountByTypeAndDate(AdminSettlementType.BIG_DRAW_MONEY, month[0], month[1]);
		settlementInfo.put("month_apply_count", month_pay_count);
		settlementInfo.put("month_pay_count", month_pay_count);
		settlementInfo.put("month_refund_count", 0d);

		return "settlement_info";
	}

	public String apply_detail() {
		String sn = getRequest().getParameter("sn");
		applyDetail = adminSettlementManager.getApplyDetail(sn);
		return "apply_detail";
	}

	public String apply_comfirm() {
		try {
			String receipt_file = getRequest().getParameter("receipt_file");
			String sn = getRequest().getParameter("sn");
			if (StringUtil.isNull(receipt_file)) {
				showErrorJson("上传图片才能保存");
				return json;
			}
			adminSettlementManager.addApply(sn);// 提交汇款单
			showSuccessJson("成功");
		} catch (Exception e) {
			showErrorJson(e.getMessage());
		}
		return JSON_MESSAGE;
	}
	
	public String settlementDetail(){
		try{
			HttpServletRequest request = this.getRequest();
			String sn = request.getParameter("sn");
			trade_big_type = request.getParameter("trade_big_type");
			if(!StringUtil.isNull(trade_big_type)){
				if(Integer.parseInt(trade_big_type)==AdminSettlementType.BIG_BUY_SERVICE){
					settlementDetail = adminSettlementManager.getSettlementDetail(sn);
					if(settlementDetail!=null && !settlementDetail.isEmpty()){
						String order_type_text = settlementDetail.get("order_type")==null?"--":AdminSettlementType.getAppBuyTypeText((int)settlementDetail.get("order_type"));
						settlementDetail.put("order_type_text", order_type_text);
						if(settlementDetail.get("trade_status")==null ){
							settlementDetail.put("handling_charge", "--");
							settlementDetail.put("service_charge", "--");
							settlementDetail.put("settlement_money", "--");
						}
					}
				}else if (Integer.parseInt(trade_big_type) == AdminSettlementType.BIG_DRAW_MONEY){
					drawMoneyDetail = adminSettlementManager.getDrowMoneyDetail(sn);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "settlement_detail";
	}
	
	/**
	 * 
	 */
	public void downExcel(){
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			HttpServletResponse response = ThreadContextHolder.getHttpResponse();
			AdminUser currentAdminUser = UserConext.getCurrentAdminUser();
			// session中获取会员信息,判断用户是否登陆
			if (currentAdminUser == null) {
				try {
					response.sendRedirect("login.html");
				} catch (IOException e) {
					throw new UrlNotFoundException();
				}
			}
			
			List<Map> settlementList = adminSettlementManager.getExcelData();

			
			HSSFWorkbook wb = new HSSFWorkbook();
			//总的列出来
			String paramsName = "流水号;交易时间;交易对象;交易大类;交易状态;交易金额;交易费用;到账金额;账户余额";
			HSSFSheet allSheet = wb.createSheet("账单明细");
			HSSFRow  allRow = allSheet.createRow(0);
			String[] paramsNames = paramsName.split(";");
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
			for (int i = 0; i < paramsNames.length; i++) {
				HSSFCell cell = allRow.createCell(i);
				cell.setCellValue(paramsNames[i]);
				cell.setCellStyle(style);
			}
			
			paramsName = "流水号;交易时间;交易对象;交易大类;交易状态;交易金额;交易费用;到账金额;账户余额;订单号;支付宝流水号;结算对象;订单总价;安全奖励抵扣;优惠券抵扣;实际支付;支付方式;经销商等级;服务时间;交易小类;手续费;服务费;结算金额;到账时间";
			paramsNames = paramsName.split(";");
			HSSFSheet buyServiceSheet = wb.createSheet("用户购买服务");
			HSSFRow buyServiceRow = buyServiceSheet.createRow(0);
			
			for (int i = 0; i < paramsNames.length; i++) {
				HSSFCell cell = buyServiceRow.createCell(i);
				cell.setCellValue(paramsNames[i]);
				cell.setCellStyle(style);
			}
			
			paramsName = "流水号;交易时间;交易对象;交易大类;交易状态;交易金额;交易费用;到账金额;账户余额;店铺账户余额;支付方式;支付账号;提现金额;处理时间;备注";
			HSSFSheet drawMoneySheet = wb.createSheet("经销商提现");
			HSSFRow  drawMoneyRow = drawMoneySheet.createRow(0);
			paramsNames = paramsName.split(";");
			for (int i = 0; i < paramsNames.length; i++) {
				HSSFCell cell = drawMoneyRow.createCell(i);
				cell.setCellValue(paramsNames[i]);
				cell.setCellStyle(style);
			}
			
		
			
			
			if (settlementList != null && !settlementList.isEmpty()) {
				int buyServicerowowNum = 1;
				int drawMoneyRowNum = 1;
				int allRowNum = 1;
				for (int i = 0; i < settlementList.size(); i++) {
					Map map = settlementList.get(i);
					allRow = allSheet.createRow(allRowNum++);
					allRow.createCell(0).setCellValue(map.get("sn").toString());
					allRow.createCell(1).setCellValue(DateUtil.toString((long)map.get("trade_time"), "yyyy-MM-dd HH:mm:ss"));
					allRow.createCell(2).setCellValue(map.get("object_name")==null?"":map.get("object_name").toString());
					allRow.createCell(3).setCellValue(AdminSettlementType.getAdminSettlementTypeText((int)map.get("trade_big_type")));
					allRow.createCell(4).setCellValue(map.get("trade_status")==null?"未服务":ReckoningTradeStatus.getName((int)map.get("trade_status")));
					allRow.createCell(5).setCellValue((double)map.get("trade_money"));
					allRow.createCell(6).setCellValue((double)map.get("bank_handling_charge"));
					allRow.createCell(7).setCellValue((double)(map.get("real_settlement_money")==null?0d:map.get("real_settlement_money")));
					allRow.createCell(8).setCellValue((double)map.get("balance_record"));
					
					
					if((int)map.get("trade_big_type") == AdminSettlementType.BIG_BUY_SERVICE){//购买服务
						buyServiceRow = buyServiceSheet.createRow(buyServicerowowNum ++);
						buyServiceRow.createCell(0).setCellValue(map.get("sn").toString());
						buyServiceRow.createCell(1).setCellValue(DateUtil.toString((long)map.get("trade_time"), "yyyy-MM-dd HH:mm:ss"));
						buyServiceRow.createCell(2).setCellValue(map.get("object_name")==null?"":map.get("object_name").toString());
						buyServiceRow.createCell(3).setCellValue(AdminSettlementType.getAdminSettlementTypeText((int)map.get("trade_big_type")));
						buyServiceRow.createCell(4).setCellValue(map.get("trade_status")==null?"未服务":ReckoningTradeStatus.getName((int)map.get("trade_status")));
						buyServiceRow.createCell(5).setCellValue((double)map.get("trade_money"));
						buyServiceRow.createCell(6).setCellValue((double)map.get("bank_handling_charge"));
						buyServiceRow.createCell(7).setCellValue((double)(map.get("real_settlement_money")==null?0d:map.get("real_settlement_money")));
						buyServiceRow.createCell(8).setCellValue((double)map.get("balance_record"));
						buyServiceRow.createCell(9).setCellValue((String)map.get("order_sn"));
						buyServiceRow.createCell(10).setCellValue((String)(map.get("pay_sn")==null?"--":map.get("pay_sn")));
						buyServiceRow.createCell(11).setCellValue((String)map.get("service_store_name"));
						buyServiceRow.createCell(12).setCellValue((double)map.get("need_pay_money"));
						buyServiceRow.createCell(13).setCellValue((double)map.get("gain"));
						buyServiceRow.createCell(14).setCellValue((double)map.get("type_money"));
						buyServiceRow.createCell(15).setCellValue((double)map.get("paymoney"));
						buyServiceRow.createCell(16).setCellValue((String)map.get("payment_name"));
						buyServiceRow.createCell(17).setCellValue((String)(map.get("level_name")==null?"--":map.get("level_name")));
						buyServiceRow.createCell(18).setCellValue( map.get("service_time")==null?"--":DateUtil.toString((long)map.get("trade_time"), "yyyy-MM-dd"));
						buyServiceRow.createCell(19).setCellValue( map.get("order_type")==null?"--":AdminSettlementType.getAppBuyTypeText((int)map.get("order_type")));
						if(map.get("trade_status")==null || map.get("trade_status") == ""){
							buyServiceRow.createCell(20).setCellValue("--");
							buyServiceRow.createCell(21).setCellValue("--");
							buyServiceRow.createCell(22).setCellValue("--");
						}else{
							buyServiceRow.createCell(20).setCellValue( map.get("handling_charge")==null?"--":String.valueOf(map.get("handling_charge")));
							buyServiceRow.createCell(21).setCellValue( map.get("service_charge")==null?"--":String.valueOf(map.get("service_charge")));
							buyServiceRow.createCell(22).setCellValue( map.get("settlement_money")==null?"--":String.valueOf(map.get("settlement_money")));
						}
						buyServiceRow.createCell(23).setCellValue( map.get("settlement_time")==null?"--":DateUtil.toString((long)map.get("settlement_time"), "yyyy-MM-dd"));
					}else if((int)map.get("trade_big_type") == AdminSettlementType.BIG_DRAW_MONEY){
						drawMoneyRow = drawMoneySheet.createRow(drawMoneyRowNum ++);
						drawMoneyRow.createCell(0).setCellValue(map.get("sn").toString());
						drawMoneyRow.createCell(1).setCellValue(DateUtil.toString((long)map.get("trade_time"), "yyyy-MM-dd HH:mm:ss"));
						drawMoneyRow.createCell(2).setCellValue(map.get("object_name")==null?"":map.get("object_name").toString());
						drawMoneyRow.createCell(3).setCellValue(AdminSettlementType.getAdminSettlementTypeText((int)map.get("trade_big_type")));
						drawMoneyRow.createCell(4).setCellValue(map.get("trade_status")==null?"--":ReckoningTradeStatus.getName((int)map.get("trade_status")));
						drawMoneyRow.createCell(5).setCellValue((double)map.get("trade_money"));
						drawMoneyRow.createCell(6).setCellValue((double)map.get("bank_handling_charge"));
						drawMoneyRow.createCell(7).setCellValue(0);
						drawMoneyRow.createCell(8).setCellValue((double)map.get("balance_record"));
						drawMoneyRow.createCell(9).setCellValue((double)map.get("balance"));
						
						String pay_type = (String)map.get("pay_type");
						if(StringUtil.isNull(pay_type)||pay_type.equals("bank")){
							drawMoneyRow.createCell(10).setCellValue((String)map.get("bank_account_name"));
							drawMoneyRow.createCell(11).setCellValue((String)map.get("bank_account_number"));
						}else if(pay_type.equals("weichat")){
							drawMoneyRow.createCell(10).setCellValue("微信支付");
							drawMoneyRow.createCell(11).setCellValue((String)map.get("weichat_account"));
						}else if(pay_type.equals("alipay")){
							drawMoneyRow.createCell(10).setCellValue("支付宝支付");
							drawMoneyRow.createCell(11).setCellValue((String)map.get("alipay_account"));
						}
						drawMoneyRow.createCell(12).setCellValue((double)map.get("er_trade_money"));
						drawMoneyRow.createCell(13).setCellValue(DateUtil.toString((long)map.get("trade_time"), "yyyy-MM-dd HH:mm:ss"));
						drawMoneyRow.createCell(14).setCellValue(map.get("apply_remarks")==null?"--":String.valueOf(map.get("apply_remarks")));
					}
				}
			}
			String filename = "账单明细" + DateUtil.toString(new Date(), "yyyyMMddhhmmss") + ".xls";
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("gb2312"),"iso-8859-1"));
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			wb.write(response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public IReckoningManager getReckoningManager() {
		return reckoningManager;
	}

	public void setReckoningManager(IReckoningManager reckoningManager) {
		this.reckoningManager = reckoningManager;
	}

	public Map getSettlementList() {
		return settlementList;
	}

	public void setSettlementList(Map settlementList) {
		this.settlementList = settlementList;
	}

	public Map getApplyDraw() {
		return applyDraw;
	}

	public void setApplyDraw(Map applyDraw) {
		this.applyDraw = applyDraw;
	}

	public IAdminSettlementManager getAdminSettlementManager() {
		return adminSettlementManager;
	}

	public void setAdminSettlementManager(IAdminSettlementManager adminSettlementManager) {
		this.adminSettlementManager = adminSettlementManager;
	}

	public Map getApplyList() {
		return applyList;
	}

	public void setApplyList(Map applyList) {
		this.applyList = applyList;
	}

	public Map getSettlement_big_types() {
		return settlement_big_types;
	}

	public void setSettlement_big_types(Map settlement_big_types) {
		this.settlement_big_types = settlement_big_types;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public Map getSettlementInfo() {
		return settlementInfo;
	}

	public void setSettlementInfo(Map settlementInfo) {
		this.settlementInfo = settlementInfo;
	}

	public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}

	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}

	public Reckoning getReckoning() {
		return reckoning;
	}

	public void setReckoning(Reckoning reckoning) {
		this.reckoning = reckoning;
	}

	public Map getSettlementDetail() {
		return settlementDetail;
	}

	public void setSettlementDetail(Map settlementDetail) {
		this.settlementDetail = settlementDetail;
	}

	public Map getDrawMoneyDetail() {
		return drawMoneyDetail;
	}

	public void setDrawMoneyDetail(Map drawMoneyDetail) {
		this.drawMoneyDetail = drawMoneyDetail;
	}

	public String getTrade_big_type() {
		return trade_big_type;
	}

	public void setTrade_big_type(String trade_big_type) {
		this.trade_big_type = trade_big_type;
	}

	public Map getApplyDetail() {
		return applyDetail;
	}

	public void setApplyDetail(Map applyDetail) {
		this.applyDetail = applyDetail;
	}
	
}
