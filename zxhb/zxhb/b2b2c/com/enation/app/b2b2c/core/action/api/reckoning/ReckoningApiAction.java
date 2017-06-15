 package com.enation.app.b2b2c.core.action.api.reckoning;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.reckoning.Reckoning;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeStatus;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeType;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.b2b2c.core.service.settlement.impl.IAdminSettlementManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Store;
import com.enation.app.shop.core.service.AdminSettlementType;
import com.enation.app.shop.core.service.OrderType;
import com.enation.app.shop.core.utils.SerailNumberUtils;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * Descriptiion:结算API
 * 
 * @author xinzai
 * @date 2016年7月15日上午11:11:09
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/reckoning")
@Action("storeReckoning")
@InterceptorRef(value = "apiRightStack")
public class ReckoningApiAction extends WWAction {

	private IStoreMemberManager storeMemberManager;

	private IReckoningManager reckoningManager;

	private IStoreManager storeManager;

	private IAdminSettlementManager adminSettlementManager;
	
	@Transactional(propagation = Propagation.REQUIRED)
	public String drawMoneyApply() {// 提现申请
		HttpServletRequest request = this.getRequest();
		String password = request.getParameter("password");
		String apply_remarks = request.getParameter("apply_remarks");
		StoreMember member = storeMemberManager.getStoreMember();
		Store store = storeManager.getStore(member.getStore_id());
		if(StringUtil.isNull(password)){
			showErrorJson("提现密码为空！");
			return JSON_MESSAGE;
		}
		password = com.enation.framework.util.StringUtil.md5(password);
		if(!password.equals(store.getCashingout_password())){
			showErrorJson("提现密码不正确！");
			return JSON_MESSAGE;
		}
		String payType = request.getParameter("payType");
		double balance = storeManager.getBalance(member.getStore_id());
		if (balance == 0d) {
			showErrorJson("提现金额为零或不存在");
			return JSON_MESSAGE;
		}
		DecimalFormat format = new DecimalFormat("0.00");
		double trade_money = Double.valueOf(format.format(Double.valueOf(balance)));
		
		//提现为全部提款
		try {
			Reckoning reckoning = new Reckoning();
			reckoning.setStore_id(member.getStore_id());
			reckoning.setTrade_money(trade_money);
			reckoning.setApply_remarks(apply_remarks);
			int sequence = adminSettlementManager.countTodayBigTypeNum(ReckoningTradeType.draw_money.getIndex());
			String sn = SerailNumberUtils.getSerailNumber(AdminSettlementType.BIG_DRAW_MONEY,
					AdminSettlementType.STORE_DRAW_MONEY, sequence);
			reckoning.setSn(sn);
			reckoning.setTrade_time(DateUtil.getDateline());
			reckoning.setTrade_status(ReckoningTradeStatus.disposing.getIndex());
			reckoning.setTrade_type(ReckoningTradeType.draw_money.getIndex());
			reckoning.setPay_type(payType);
			// 更新4s店铺余额,将本期已结算金额列入本次提现的周期中
			Double store_balance = CurrencyUtil.add(balance, -trade_money);
			//查询该店铺已经提现多少期了
			int  stage_no =  reckoningManager.countDrawMoneyByStoreId(member.getStore_id());
			stage_no=stage_no + 1;
			storeManager.updateBalance(String.valueOf(stage_no),-trade_money, member.getStore_id());
			reckoning.setBalance(store_balance);
			reckoning.setStage_no(String.valueOf(stage_no));
			reckoningManager.add(reckoning);
			showSuccessJson("申请成功");
			
		} catch (Exception e) {
			e.printStackTrace();
			showErrorJson("提现申请出错");
		}
		return JSON_MESSAGE;
	}

	public void downloadExcel() {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			HttpServletResponse response = ThreadContextHolder.getHttpResponse();
			StoreMember member = storeMemberManager.getStoreMember();
			// session中获取会员信息,判断用户是否登陆
			if (member == null) {
				try {
					response.sendRedirect("login.html");
				} catch (IOException e) {
					throw new UrlNotFoundException();
				}
			}
			String trade_type = request.getParameter("trade_type");
			String trade_status = request.getParameter("trade_status");
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			int store_id = member.getStore_id();
			List<Map> reckoningList = reckoningManager.getReckonings(store_id, trade_type, trade_status, startTime,
					endTime,"");

			String paramsName = "流水号;交易时间;交易类型;交易状态;交易金额;账户余额;订单编号;服务时间;服务总价;安全奖励抵扣;优惠券抵扣;实际支付;服务类型;手续费;服务费;结算金额;结算时间;开户银行;银行卡号;提款处理时间;备注";
			String[] paramsNames = paramsName.split(";");
			
			
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("账单明细");
			HSSFRow row = sheet.createRow((int) 0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
			for (int i = 0; i < paramsNames.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellValue(paramsNames[i]);
				cell.setCellStyle(style);
			}
			if (reckoningList != null && !reckoningList.isEmpty()) {
				for (int i = 0; i < reckoningList.size(); i++) {
					row = sheet.createRow((int) i + 1);
					Map map = reckoningList.get(i);
					row.createCell(0).setCellValue(map.get("sn").toString());
					row.createCell(1)
							.setCellValue(DateUtil.toString((Long) map.get("trade_time"), "yyyy-MM-dd HH:mm:ss"));
					row.createCell(2).setCellValue(ReckoningTradeType.getName((int) map.get("trade_type")));
					row.createCell(3).setCellValue(ReckoningTradeStatus.getName((int) map.get("trade_status")));
					row.createCell(4).setCellValue((double)(map.get("trade_money")==null?0d:map.get("trade_money")));
					row.createCell(5).setCellValue((double)(map.get("balance")==null?0d:map.get("balance")));
					if( (int)map.get("trade_type") == 2){
						row.createCell(6).setCellValue((String)(map.get("order_sn")==null?"--":map.get("order_sn")));
						row.createCell(7).setCellValue(map.get("service_time")==null?"--":DateUtil.toString((long)map.get("service_time"), "yyyy-MM-dd"));
						row.createCell(8).setCellValue((double)(map.get("order_price")==null?0d:map.get("order_price")));
						row.createCell(9).setCellValue((double)(map.get("use_gain")==null?0d:map.get("use_gain")));
						row.createCell(10).setCellValue((double)(map.get("use_coupon")==null?0d:map.get("use_coupon")));
						row.createCell(11).setCellValue((double)(map.get("paymoney")==null?0d:map.get("paymoney")));
						row.createCell(12).setCellValue(map.get("order_type")==null?"":OrderType.getOrderTypeTest((int)map.get("order_type")));
						row.createCell(13).setCellValue((double)(map.get("handling_charge")==null?0d:map.get("handling_charge")));
						row.createCell(14).setCellValue((double)(map.get("service_charge")==null?0d:map.get("service_charge")));
						row.createCell(15).setCellValue((double)(map.get("settlement_money")==null?0d:map.get("settlement_money")));
						row.createCell(16).setCellValue(map.get("settlement_time")==null?"--":DateUtil.toString((long)map.get("settlement_time"), "yyyy-MM-dd"));
						row.createCell(19).setCellValue("--");
					}else if((int)map.get("trade_type") == 1){
						row.createCell(6).setCellValue("--");
						row.createCell(7).setCellValue("--");
						row.createCell(8).setCellValue("--");
						row.createCell(9).setCellValue("--");
						row.createCell(10).setCellValue("--");
						row.createCell(11).setCellValue("--");
						row.createCell(12).setCellValue("--");
						row.createCell(13).setCellValue("--");
						row.createCell(14).setCellValue("--");
						row.createCell(15).setCellValue("--");
						row.createCell(16).setCellValue("--");
						row.createCell(19).setCellValue((String)(map.get("deal_time")==null?"--":DateUtil.toString((Long) map.get("deal_time"), "yyyy-MM-dd HH:mm:ss")));
					}
					row.createCell(17).setCellValue((String)(map.get("bank_account_name")==null?"--":map.get("bank_account_name")));
					row.createCell(18).setCellValue((String)(map.get("bank_account_number")==null?"--":map.get("bank_account_number")));
					row.createCell(20).setCellValue((String)(map.get("apply_remarks")==null?"--":map.get("apply_remarks")));
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

	public void downloadSettledExcel() {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			HttpServletResponse response = ThreadContextHolder.getHttpResponse();
			StoreMember member = storeMemberManager.getStoreMember();
			// session中获取会员信息,判断用户是否登陆
			if (member == null) {
				try {
					response.sendRedirect("login.html");
				} catch (IOException e) {
					throw new UrlNotFoundException();
				}
			}
			String stage_no = request.getParameter("stage_no");
			Map result = new HashMap();
			result.put("stage_no", stage_no);
			List<Map> settledList = reckoningManager.getSettledListByNo(member.getStore_id(), result);

			String paramsName = "流水号;订单编号;付款时间;服务类型;实付金额;手续费;服务费;结算金额;用户全名;用户名";
			String[] paramsNames = paramsName.split(";");
			
			
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("往期账单明细");
			HSSFRow row = sheet.createRow((int) 0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
			for (int i = 0; i < paramsNames.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellValue(paramsNames[i]);
				cell.setCellStyle(style);
			}
			if (settledList != null && !settledList.isEmpty()) {
				for (int i = 0; i < settledList.size(); i++) {
					row = sheet.createRow((int) i + 1);
					Map map = settledList.get(i);
					int trade_type = (int)map.get("trade_type");
					int trade_status = (int)map.get("trade_status");
					if(trade_type == ReckoningTradeType.draw_money.getIndex()){
						row.createCell(0).setCellValue(DateUtil.toString((long)map.get("r_trade_time"), "yyyy-MM-dd HH:mm:ss"));
						row.createCell(1).setCellValue(ReckoningTradeStatus.getName(trade_status)+ " : " + (double)map.get("trade_money") );
						row.createCell(2).setCellValue("期号  : " + String.valueOf(map.get("stage_no")));
					}else if (trade_type == ReckoningTradeType.settle_accounts.getIndex()) {
						row.createCell(0).setCellValue((String)map.get("sn"));
						row.createCell(1).setCellValue((String)map.get("order_sn"));
						row.createCell(2).setCellValue(DateUtil.toString((map.get("trade_time")==null?0:(long)map.get("trade_time")), "yyyy-MM-dd HH:mm:ss"));
						row.createCell(3).setCellValue(OrderType.getOrderTypeTest((int)map.get("order_type")));
						row.createCell(4).setCellValue((double)map.get("paymoney"));
						row.createCell(5).setCellValue((double)map.get("handling_charge"));
						row.createCell(6).setCellValue((double)map.get("service_charge"));
						row.createCell(7).setCellValue((double)map.get("settlement_money"));
						row.createCell(8).setCellValue((String)map.get("fullname"));
						row.createCell(9).setCellValue((String)map.get("username"));
					}
				
				}
			}
			String filename = "往期账单明细" + DateUtil.toString(new Date(), "yyyyMMddhhmmss") + ".xls";
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("gb2312"),"iso-8859-1"));
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			wb.write(response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void downloadThisPeriodExcel() {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			HttpServletResponse response = ThreadContextHolder.getHttpResponse();
			StoreMember member = storeMemberManager.getStoreMember();
			// session中获取会员信息,判断用户是否登陆
			if (member == null) {
				try {
					response.sendRedirect("login.html");
				} catch (IOException e) {
					throw new UrlNotFoundException();
				}
			}
		/*	String trade_type = request.getParameter("trade_type");
			String trade_status = request.getParameter("trade_status");
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");*/
			Map result = new HashMap();
			int store_id = member.getStore_id();
			List<Map> reckoningList = reckoningManager.getThisPeriodList(store_id,result);

			String paramsName = "流水号;订单编号;付款时间;服务类型;实付金额;结算金额;手续费;服务费;用户全名;用户名";
			String[] paramsNames = paramsName.split(";");
			HSSFWorkbook wb = new HSSFWorkbook();
			HSSFSheet sheet = wb.createSheet("本期账单明细");
			HSSFRow row = sheet.createRow((int) 0);
			HSSFCellStyle style = wb.createCellStyle();
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式
			for (int i = 0; i < paramsNames.length; i++) {
				HSSFCell cell = row.createCell(i);
				cell.setCellValue(paramsNames[i]);
				cell.setCellStyle(style);
			}
			if (reckoningList != null && !reckoningList.isEmpty()) {
				for (int i = 0; i < reckoningList.size(); i++) {
					row = sheet.createRow((int) i + 1);
					Map map = reckoningList.get(i);
					row.createCell(0).setCellValue(map.get("sn").toString());
					row.createCell(1).setCellValue(map.get("order_sn").toString());
					String trade_time = "--";
					if(map.get("trade_time") != null){
						trade_time = DateUtil.toString((long)map.get("trade_time"), "yyyy-MM-dd HH:mm:ss");
					}
					row.createCell(2).setCellValue(trade_time);
					row.createCell(3).setCellValue(OrderType.getOrderTypeTest((int)map.get("order_type")));				
					row.createCell(4).setCellValue((Double)map.get("paymoney"));				
					row.createCell(5).setCellValue((Double)map.get("settlement_money"));				
					row.createCell(6).setCellValue((Double)map.get("handling_charge"));				
					row.createCell(7).setCellValue((Double)map.get("service_charge"));			
					row.createCell(8).setCellValue((String)map.get("fullname"));			
					row.createCell(9).setCellValue((String)map.get("username"));			
				}
			}
			String filename = "本期账单明细" + DateUtil.toString(new Date(), "yyyyMMddhhmmss") + ".xls";
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes("gb2312"),"iso-8859-1"));
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			wb.write(response.getOutputStream());
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public IReckoningManager getReckoningManager() {
		return reckoningManager;
	}

	public void setReckoningManager(IReckoningManager reckoningManager) {
		this.reckoningManager = reckoningManager;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}

	public IAdminSettlementManager getAdminSettlementManager() {
		return adminSettlementManager;
	}

	public void setAdminSettlementManager(IAdminSettlementManager adminSettlementManager) {
		this.adminSettlementManager = adminSettlementManager;
	}

	

}
