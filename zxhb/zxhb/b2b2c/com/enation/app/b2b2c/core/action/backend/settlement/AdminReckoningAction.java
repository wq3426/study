package com.enation.app.b2b2c.core.action.backend.settlement;

import java.io.IOException;
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
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeStatus;
import com.enation.app.b2b2c.core.model.reckoning.ReckoningTradeType;
import com.enation.app.b2b2c.core.service.reckoning.IReckoningManager;
import com.enation.app.shop.core.service.AdminSettlementType;
import com.enation.app.shop.core.service.OrderType;
import com.enation.eop.processor.core.Request;
import com.enation.eop.processor.core.UrlNotFoundException;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;

import freemarker.core.ReturnInstruction.Return;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


/** @Description ADMIN后台显示4S店的结算信息
 *
 * @createTime 2016年9月14日 下午2:24:17
 *
 * @author <a href="mailto:huashixin@trans-it.cn">huashixin</a>
 */
/** @Description (描述类是干嘛用的)
 *
 * @createTime 2016年9月20日 上午11:44:12
 *
 * @author <a href="mailto:huashixin@trans-it.cn">huashixin</a>
 */
@Component
@ParentPackage("eop_default")
@Namespace("/b2b2c/admin")
@Results({ @Result(name = "thisPeriodList", type = "freemarker", location = "/b2b2c/admin/settlement/admin_thisPeriod.html")
		  ,@Result(name = "settledList", type = "freemarker", location = "/b2b2c/admin/settlement/admin_settled.html")
		  ,@Result(name = "transactionList", type = "freemarker", location = "/b2b2c/admin/settlement/admin_transactionDetail.html")
		  ,@Result(name = "transactionDetail", type = "freemarker", location = "/b2b2c/admin/settlement/admin_reckoning_detail.html")
		  ,@Result(name = "settledDownExcelInfo", type = "freemarker", location = "/b2b2c/admin/settlement/admin_settlement_down.html")
})
@Action("adminReckoning")
public class AdminReckoningAction extends WWAction {
	
	private IReckoningManager reckoningManager;
	
	private int store_id;
	
	private Map result;
	
	private Map tradeTypeMap;
	
	private String store_name;
	
	public String settledList(){
		return "settledList";
	}
	
	public String settledListJson(){
		try{
			result = new HashMap();
			HttpServletRequest request = this.getRequest();
			String order_type = request.getParameter("order_type");
			String userInfo = request.getParameter("userInfo");
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			String sn = request.getParameter("sn");
			result.put("sn", sn);
			result.put("order_type", order_type);
			result.put("startTime",startTime);
			result.put("userInfo", userInfo);
			result.put("endTime", endTime);
			result.put("store_id", store_id);
			int pageSize=10;
			String pageNo = request.getParameter("pageNo")==null?"1": request.getParameter("pageNo");
			Page page = reckoningManager.getSettledList(Integer.parseInt(pageNo), pageSize,store_id, result);
			// 获取4S店收入
			result.put("pageType", "settled");
			Map income = reckoningManager.getReckoningByIncome(result);
			// 获取4S店支出
			Map pay = reckoningManager.getReckoningByPay(result);
			page.setCurrentPageNo(Integer.parseInt(pageNo));
			JSONObject object = JSONObject.fromObject(page);
			object.put("income_money",income.get("income") );
			object.put("income_number", income.get("income_count"));
			object.put("expend_money",pay.get("pay"));
			object.put("expend_number",pay.get("pay_count"));
			this.json = "{\"result\" : \"1\",\"data\" : " + object.toString() +"}"; 
		
		}catch(Exception e){
			 e.printStackTrace();
		}
		return JSON_MESSAGE;
	}
	
	public String thisPeriodList(){
		return "thisPeriodList";
	}
	
	
	public String thisPeriodListJson(){
		try{
			result = new HashMap();
			String order_type = this.getRequest().getParameter("order_type");
			String userInfo = this.getRequest().getParameter("userInfo");
			String startTime = this.getRequest().getParameter("startTime");
			String endTime = this.getRequest().getParameter("endTime");
			String sn = this.getRequest().getParameter("sn");
			result.put("sn", sn);
			result.put("order_type", order_type);
			result.put("startTime",startTime);
			result.put("userInfo", userInfo);
			result.put("endTime", endTime);
			result.put("store_id", store_id);
			Page page = reckoningManager.getThisPeriodReckoning(this.getPage(), this.getPageSize(),store_id, result);
			// 获取4S店收入
			result.put("pageType", "thisPeriod");
			Map income = reckoningManager.getReckoningByIncome(result);
			// 获取4S店支出
			Map pay = reckoningManager.getReckoningByPay(result);
			this.showGridJson(page);
			JSONObject object = JSONObject.fromObject(json);
			object.put("income_money",income.get("income") );
			object.put("income_number", income.get("income_count"));
			object.put("expend_money",pay.get("pay"));
			object.put("expend_number",pay.get("pay_count"));
			json = object.toString();
		}catch(Exception e){
			 e.printStackTrace();
		}
		return JSON_MESSAGE;
	}

	
	public String transactionList(){
		tradeTypeMap = ReckoningTradeType.getTradeTypeMap();
		return "transactionList";
	}
	
	
	public String transactionListJson(){
		HttpServletRequest request = this.getRequest();
		result = new HashMap();
		String trade_type = request.getParameter("trade_type");
		String trade_status = request.getParameter("trade_status");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		String sn = request.getParameter("sn");
		result.put("trade_type", trade_type);
		result.put("trade_status", trade_status);
		result.put("startTime", startTime);
		result.put("endTime", endTime);
		result.put("pageType","transactionDetail");
		result.put("sn",sn);
		Page reckoningList = reckoningManager.getReckoningList(this.getPage(), this.getPageSize(),store_id, result);
		//获取4S店收入
		Map income = reckoningManager.getReckoningByIncome(result);
		//获取4S店支出
		Map pay = reckoningManager.getReckoningByPay(result);
	
		this.showGridJson(reckoningList);
		JSONObject object = JSONObject.fromObject(json);
		object.put("income_money",income.get("income"));
		object.put("income_number",income.get("income_count"));
		object.put("expend_money",pay.get("pay"));
		object.put("expend_number",pay.get("pay_count"));
		json=object.toString();
		return JSON_MESSAGE;
	}
	
	
	public String transactionDetail(){
		try{
			HttpServletRequest request = this.getRequest();
			String id = request.getParameter("id");
			result = new HashMap();
			result = reckoningManager.getTransactionDetailById(id);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "transactionDetail";
	}
	
	public void downloadThisPeriodExcel() {
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			HttpServletResponse response = ThreadContextHolder.getHttpResponse();
			Map result = new HashMap();
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
	
	
	public void downTransactionDeatilExcel(){
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			HttpServletResponse response = ThreadContextHolder.getHttpResponse();
			String trade_type = request.getParameter("trade_type");
			String trade_status = request.getParameter("trade_status");
			String startTime = request.getParameter("startTime");
			String endTime = request.getParameter("endTime");
			String sn = request.getParameter("sn");
			List<Map> reckoningList = reckoningManager.getReckonings(store_id, trade_type, trade_status, startTime,
					endTime,sn);

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
	
	
	/** @description admin往期下载时显示页面
	 * @date 2016年9月20日 上午11:44:14
	 * @return
	 * @return String
	 */
	public String settledDownExcelInfo(){
		try{
			List list= reckoningManager.getSettledStageNoList(store_id);
			result = new HashMap();
			result.put("settledList", list);
		}catch(Exception e){
			e.printStackTrace();
		}
		return "settledDownExcelInfo";
	}
	
	
	public void settledDownExcel(){
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			HttpServletResponse response = ThreadContextHolder.getHttpResponse();
			String stage_no = request.getParameter("stage_no");
			Map result = new HashMap();
			result.put("stage_no", stage_no);
			List<Map> settledList = reckoningManager.getSettledListByNo(store_id, result);

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
	
	public IReckoningManager getReckoningManager() {
		return reckoningManager;
	}


	public void setReckoningManager(IReckoningManager reckoningManager) {
		this.reckoningManager = reckoningManager;
	}


	public int getStore_id() {
		return store_id;
	}


	public void setStore_id(int store_id) {
		this.store_id = store_id;
	}


	public Map getResult() {
		return result;
	}


	public void setResult(Map result) {
		this.result = result;
	}

	public Map getTradeTypeMap() {
		return tradeTypeMap;
	}

	public void setTradeTypeMap(Map tradeTypeMap) {
		this.tradeTypeMap = tradeTypeMap;
	}

	public String getStore_name() {
		return store_name;
	}

	public void setStore_name(String store_name) {
		this.store_name = store_name;
	}
	
	
}
