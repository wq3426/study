package com.enation.app.shop.core.action.backend;

import java.util.ArrayList;
import java.util.Date;
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

import com.enation.app.shop.core.model.Depot;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.SellBackChild;
import com.enation.app.shop.core.model.SellBackGoodsList;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.model.SellBackStatus;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;
/**
 * 退货管理Action
 * @author fenlongli
 *
 */
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("sellBack")
@Results({
	@Result(name="add", type="freemarker", location="/shop/admin/orderReport/add_sellback.html"),
	@Result(name="auth", type="freemarker", location="/shop/admin/orderReport/auth_sellback.html"),
	@Result(name="payment", type="freemarker", location="/shop/admin/orderReport/payment_sellback.html"),
	@Result(name="returned", type="freemarker", location="/shop/admin/orderReport/returned_sellback.html"),
	@Result(name="list", type="freemarker", location="/shop/admin/orderReport/sellback_list.html"),
	@Result(name="packDetail",type="freemarker", location="/shop/admin/orderReport/packDetail.html")//整箱详细   
})
public class SellBackAction extends WWAction {
	private ISellBackManager sellBackManager;
	private IPaymentManager paymentManager;
	private IOrderManager orderManager;
	private ILogiManager logiManager;
	private Integer orderId;
	private Order orderinfo;
	private List orderItem;
	private String tradesn;
	private SellBackList sellBackList;
	private Integer goodsId[];
	private String goodsName[];//退货商品名
	private Integer goodsNum[];//申请退货数量
	private String goodsRemark[];//退货商品备注
	private Double goodsPrice[];
	private Integer payNum[];//购买数量
	private Integer[] returnNum;//申请退货数
	private Integer[] oldStorageNum;//已入库数量
	private Integer storageNum[];//入库商品数量
	private Integer status;
	private Integer id;
	private List goodsList;
	private List logList;
	private String cancelRemark;//取消退货备注
	private String keyword;
	private Integer gid[];
	private IDepotManager depotManager;
	private Integer depotid;
	private Integer ctype;
	private Integer productId[];
	private String seller_remark;
	private String depot_name;
	private List<Depot> depotlist;
	private List paymentList;
	
	private List orderItemDetail;	//整箱详情
	private Integer itemId;
	private String packDetailJson;
	private List return_child_list;	//子项详情
	private OrderPluginBundle orderPluginBundle;
	private IDaoSupport daoSupport;
	/**
	 * 退货申请列表
	 */
	public String list(){
		return "list";
	}
	public String listJson(){
		this.webpage = sellBackManager.list(this.getPage(), this.getPageSize(),status);
		this.showGridJson(this.webpage);
		return this.JSON_MESSAGE;
	}
	//订单号查询
	public String searchSn(){
		orderinfo = orderManager.get(orderId);//订单详细
		int num = this.sellBackManager.searchSn(orderinfo.getSn());
		//System.out.println(num);
		//提交过返回success 因为只有success能传递id
		if(num>0){
			this.showSuccessJson("订单已提交过退货申请",num);
		}else{
			this.showErrorJson("");
		}
		return JSON_MESSAGE;
	}
	/**
	 * 退货搜索
	 * @return
	 */
	public String search(){
		this.webpage = sellBackManager.search(keyword,this.getPage(), this.getPageSize());
		return "list";
	}
	/**
	 * 新建退货申请
	 */
	public String add(){
		orderinfo = orderManager.get(orderId);//订单详细
		orderItem = orderManager.getOrderItem(orderId);
		depotlist=depotManager.list();//仓库列表
		paymentList=paymentManager.list(); //支付方式列表
		return "add";
	}
	/**
	 * 审核退货申请
	 * @author fenlongli
	 * @return
	 */
	public String auth(){
		try{
			sellBackList = this.sellBackManager.get(id);//退货详细
			orderinfo = orderManager.get(sellBackList.getOrdersn());//订单详细
			goodsList = this.sellBackManager.getGoodsList(id,sellBackList.getOrdersn());//退货商品列表
			depot_name=depotManager.get(sellBackList.getDepotid()).getName();
		
			return_child_list = new ArrayList();
			for (int i = 0; i < goodsList.size(); i++) {
				Map map = (Map) goodsList.get(i);
				if(Integer.parseInt(map.get("return_type").toString())==1){
					List list = this.sellBackManager.getSellbackChilds(orderinfo.getOrder_id(),Integer.parseInt(map.get("goodsId").toString()));
					if (list != null) {
						return_child_list.addAll(list);
					}
				}
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return "auth";
	}
	/**
	 * 审核退货申请
	 * @param status 审核状态
	 * @param id 退货单ID
	 * @param seller_remark 审核备注
	 * @return
	 */
	public String saveAuth(){
		try {
			this.sellBackManager.editStatus(status,id,seller_remark);
			this.showSuccessJson("操作成功");
		} catch (Exception e) {
			this.showErrorJson("操作失败");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 退货入库
	 * @return
	 */
	public String returned(){
		
		sellBackList = this.sellBackManager.get(id);// 退货详细
		orderinfo = orderManager.get(sellBackList.getOrdersn());// 订单详细
		goodsList = this.sellBackManager.getGoodsList(id,sellBackList.getOrdersn());// 退货商品列表
		logList = this.sellBackManager.sellBackLogList(id);// 退货操作日志
		depot_name = depotManager.get(sellBackList.getDepotid()).getName();
		
		return_child_list = new ArrayList();
		for (int i = 0; i < goodsList.size(); i++) {
			Map map = (Map) goodsList.get(i);
			if(Integer.parseInt(map.get("return_type").toString())==1){
				List list = this.sellBackManager.getSellbackChilds(orderinfo.getOrder_id(),Integer.parseInt(map.get("goodsId").toString()));
				if (list != null) {
					return_child_list.addAll(list);
				}
			}
			
		}
		
		return "returned";
	}	
	
	/**
	 * 财务结算
	 * @return
	 */
	public String payment(){
		sellBackList = this.sellBackManager.get(id);//退货详细
		orderinfo = orderManager.get(sellBackList.getOrdersn());//订单详细
		goodsList = this.sellBackManager.getGoodsList(id,sellBackList.getOrdersn());// 退货商品列表
		//goodsList = this.sellBackManager.getGoodsList(id);//退货商品列表
		logList = this.sellBackManager.sellBackLogList(id);//退货操作日志
		depot_name=depotManager.get(sellBackList.getDepotid()).getName();
		
		return_child_list = new ArrayList();
		for (int i = 0; i < goodsList.size(); i++) {
			Map map = (Map) goodsList.get(i);
			if(Integer.parseInt(map.get("return_type").toString())==1){
				List list = this.sellBackManager.getSellbackChilds(orderinfo.getOrder_id(),Integer.parseInt(map.get("goods_id").toString()));
				if (list != null) {
					return_child_list.addAll(list);
				}
			}
		}
		return "payment";
	}
	
	/**
	 * 保存退货申请
	 * @return
	 */
	public String save(){
		String goodslist ="";
		//查找用户选中的goodsid对应的数据
		if(goodsId!=null){
			for(int i=0;i<goodsId.length;i++){
				for(int j=0;j<gid.length;j++){
					if(goodsId[i].intValue()==gid[j].intValue()){
						HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
						String isPack = request.getParameter("isPack_" + goodsId[i].intValue());
						//如果该商品是一个整箱  则把箱内的商品 提取出来
						if("1".equals(isPack)){
							goodslist = goodslist + goodsName[j];
						}else{
							goodslist = goodslist + goodsName[j] + "(" + goodsNum[j] +") ";
						}
					}
				}
			}
		}
		try{
			//Logi logi = logiManager.getLogiById(sellBackList.getLogi_id());
			sellBackList.setGoodslist(goodslist);
			SellBackList sellback = this.sellBackManager.get(sellBackList.getTradeno());
			Order order = orderManager.get(orderId);
			if(order.getShipping_area()!=null || !StringUtil.isEmpty(order.getShipping_area()) || order.getShipping_area().trim().equals("暂空")){
				sellBackList.setAdr(order.getShip_addr());
			}else{
				String adr[]=order.getShipping_area().split("-"); 
				sellBackList.setAdr(adr[0] + adr[1] + adr[2] + order.getShip_addr());
			}
			sellBackList.setRegtime(DateUtil.getDateline());
			sellBackList.setRegoperator(UserConext.getCurrentAdminUser().getUsername());
			sellBackList.setTel(order.getShip_tel());
			sellBackList.setZip(order.getShip_zip());
			sellBackList.setTradestatus(status);
			sellBackList.setDepotid(depotid);
			//保存退货单
			Integer sid = this.sellBackManager.save(sellBackList);
			
			if(sellback!=null){
				SellBackList sellbacklist = this.sellBackManager.get(sellBackList.getTradeno());
				this.sellBackManager.saveLog(sellbacklist.getId(), SellBackStatus.valueOf(sellbacklist.getTradestatus()), "");
			}
			
			/**
			 * 关于整箱退货代码修改   Start
			 * 冯兴隆 2015-07-14
			 */
			List<Map<String,Object>> list = JsonUtil.toList(packDetailJson);
			if (goodsId != null) {
				for (int i = 0; i < goodsId.length; i++) {
					int nowGoodsId = goodsId[i];
					for (int j = 0; j < gid.length; j++) {
						//判断商品是否被选中
						if (goodsId[i].intValue() == gid[j].intValue()) {
							//获取退货详情
							SellBackGoodsList sellBackGoods = this.sellBackManager.getSellBackGoods(sid, nowGoodsId);
							if (sellBackGoods != null) {
								this.editGoodsList(goodsNum[j], sid,nowGoodsId, sellBackList.getSeller_remark(), null);
							} else {
								this.saveGoodsList(nowGoodsId, goodsNum[j],
										goodsPrice[j], payNum[j], sid,
										sellBackList.getSeller_remark(), null,
										productId[i]);
							}
							HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
							String isPack = request.getParameter("isPack_" + nowGoodsId);
							//如果该商品是一个整箱  则把箱内的商品 提取出来
							if("1".equals(isPack)){
								String isAll = request.getParameter("is_all_" + nowGoodsId);		//是否是全部退货
								
								//如果是全部退货 则自动算出数量
								if("0".equals(isAll)){
									List<Map> listGoods = sellBackManager.list(nowGoodsId);
									for(Map map : listGoods){
										int tempGoodsId = Integer.parseInt(map.get("rel_goods_id").toString());
										Map childGoodsInfo = this.sellBackManager.getPackInfo(nowGoodsId, tempGoodsId);
										int pkgNum = Integer.parseInt(childGoodsInfo.get("pkgnum").toString());
										int tempNum = goodsNum[j] * pkgNum;
										//int tempNum =  goodsNum[j];
										this.saveOrUpdateSellbackChild(orderId, tempGoodsId, nowGoodsId, tempNum);
									}
								}else{
									for(Map<String,Object> map : list){
										int packGoodsId = Integer.parseInt(map.get("packGoodsId").toString());
										//如果该条数据是该整箱中的商品
										if(nowGoodsId == packGoodsId){
											int tempGoodsId = Integer.parseInt(map.get("goodsId").toString());
											int tempNum = Integer.parseInt(map.get("num").toString());
											this.saveOrUpdateSellbackChild(orderId, tempGoodsId, nowGoodsId, tempNum);
										}
									}
								}
								
							}
						}
					}
				}
			}
			/**
			 * 关于整箱退货代码修改   End
			 * 冯兴隆 2015-07-14
			 */
			this.showSuccessJson("操作成功！",sid);
		}catch(Exception e){
			//e.printStackTrace();
			this.showErrorJson("操作失败:" + e.getMessage());
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 判断json数组转换的list中 是否含有该商品Id  相当于判断该商品是不是一个整箱
	 * @param goodsId
	 * @param list
	 * @return	boolean 
	 */
	/*private boolean isHaveGoods(int goodsId,List<Map<String,Object>> list){
		boolean result = false;
		for(Map map : list){
			int packGoodsId = Integer.parseInt(map.get("packGoodsId").toString());
			if(goodsId == packGoodsId){
				result = true;
				break;
			}
		}
		return result;
	}*/
	
	/**
	 * 新增或修改 整箱内的退货子项详情
	 * @param orderId
	 * @param goodsId
	 * @param parentId
	 * @param returnNum
	 * @param storageNum
	 */
	private void saveOrUpdateSellbackChild(int orderId,int goodsId,int parentId,int returnNum){
		SellBackChild sellBackChild = this.sellBackManager.getSellbackChild(orderId, goodsId);
		if(sellBackChild != null){
			this.sellBackManager.updateSellbackChild(orderId, goodsId, returnNum,0);
		}else{
			this.sellBackManager.saveSellbackChild(orderId, goodsId, parentId, returnNum);
		}
	}
	

	/**
	 * 退货申请入库
	 * @return
	 */
	public String update() {
		
		SellBackList sellback = this.sellBackManager.get(id);
		orderinfo = orderManager.get(sellback.getOrdersn());// 订单详细
		
		String goodslist = "";
		status = 2;// 先假设全部入库
		
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		String json = request.getParameter("storageDetail");
		List<Map<String,Object>> list = JsonUtil.toList(json);
		
		if (goodsId != null) {
			for (int i = 0; i < goodsId.length; i++) {
				for (int j = 0; j < gid.length; j++) {

					int rnum = this.returnNum[i]; // 申请退货的数量
					int osnum = this.oldStorageNum[i];// 已入库的数量

					if (goodsId[i].intValue() == gid[j].intValue()) {
						/**
						 * 关于整箱退货入库代码修改   Start
						 * 冯兴隆 2015-07-16
						 */
						int nowGoodsId = goodsId[i];
						SellBackGoodsList sellBackGoods = this.sellBackManager.getSellBackGoods(id, goodsId[i]);
						if(sellBackGoods.getReturn_type() != 1){
							int snum = this.storageNum[j];// 本次入库的数量
	
							if (snum + osnum > rnum) {
								this.showErrorJson("入库数量不能大于退货数量");
								return JSON_MESSAGE;
							}
							// 还有部分入库的情况
							if (snum + osnum < rnum) {
								status = 5;
							}
	
							goodslist = goodslist + goodsName[j] + "("+ rnum + ") ";
							
							if (sellBackGoods != null) {
								this.editGoodsList(null, id,nowGoodsId,sellBackGoods.getGoods_remark(), snum + osnum);
							}
						}else{
							goodslist = goodslist + goodsName[j];
							List<Map> listChilds = this.sellBackManager.getSellbackChilds(orderinfo.getOrder_id(),nowGoodsId);
							for(Map child : listChilds){
								int tempONum =  Integer.parseInt(child.get("storage_num").toString());		//已入库数量
								int tempRNum = Integer.parseInt(child.get("return_num").toString());		// 申请退货的数量
								int parentId = Integer.parseInt(child.get("parent_id").toString());			//所属整箱商品id
								int childGoodsId = Integer.parseInt(child.get("goods_id").toString());
								for(Map<String,Object> map : list){
									
									int tempGoodsId = Integer.parseInt(map.get("goodsId").toString());
									int tempParentId = Integer.parseInt(map.get("parentId").toString());
									if(childGoodsId == tempGoodsId && parentId == tempParentId){
										int tempSNum = Integer.parseInt(map.get("num").toString());		//本次入库数量
										
										if (tempSNum + tempONum > tempRNum) {
											this.showErrorJson("入库数量不能大于退货数量");
											return JSON_MESSAGE;
										}
										// 还有部分入库的情况
										if (tempSNum + tempONum < tempRNum) {
											status = 5;
										}
										this.sellBackManager.updateSellbackChild(orderinfo.getOrder_id(), childGoodsId, tempRNum,tempSNum + tempONum);
									}
								}
								
							}
						}
						/**
						 * 关于整箱退货入库代码修改   End
						 * 冯兴隆 2015-07-16
						 */
					}
				}
			}
		}
		
		try {
			sellback.setGoodslist(goodslist);
			sellback.setWarehouse_remark(sellBackList.getWarehouse_remark());
			sellback.setTradestatus(status);
			this.sellBackManager.save(sellback);
			if (status == 2 || status == 5) {
				this.sellBackManager.saveLog(sellback.getId(),
						SellBackStatus.valueOf(sellback.getTradestatus()), "");
			}
			this.showSuccessJson("操作成功！");
		} catch (Exception e) {
			e.printStackTrace();
			this.showErrorJson("操作失败 ！");
		}
		return JSON_MESSAGE;
	}

	/**
	 * 财务结算
	 * @return
	 */
	public String savePayment(){
		try{
			this.sellBackManager.closePayable(id, sellBackList.getFinance_remark(), "财务退款："+sellBackList.getAlltotal_pay()+"元",sellBackList.getAlltotal_pay());
			this.showSuccessJson("操作成功！");
		}catch(Exception e){
			this.showErrorJson("操作失败！");
		}
		return this.JSON_MESSAGE;
	}
	
 
	
	/**
	 * 保存退货商品
	 * @param tradeno
	 */
	public Integer saveGoodsList(Integer goodsid,Integer goodsnum,Double price,Integer paynum,Integer id,String remark,Integer storageNum,Integer productid){
		SellBackGoodsList sellBackGoods = new SellBackGoodsList();
		if(storageNum!=null){
			sellBackGoods.setStorage_num(storageNum);
			sellBackGoods.setReturn_num(storageNum);
		}
		if(goodsnum==null){
			sellBackGoods.setReturn_num(0);
		}else{
			sellBackGoods.setReturn_num(goodsnum);
		}
		sellBackGoods.setGoods_id(goodsid);
		sellBackGoods.setPrice(price);
		sellBackGoods.setRecid(id);
		sellBackGoods.setShip_num(paynum);
		sellBackGoods.setGoods_remark(remark);
		sellBackGoods.setProduct_id(productid);
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		String isPack = request.getParameter("isPack_" + goodsid);
		try{
			//如果商品为全部退货或者为单只产品
			if(isPack.equals("1")){
				String return_type = request.getParameter("is_all_" + goodsid);		//是否是全部退货
				if("0".equals(return_type)){
					sellBackGoods.setReturn_type(0);
				}else{
					sellBackGoods.setReturn_type(1);
					
				}
			}else{
				sellBackGoods.setReturn_type(0);
			}
		}catch(RuntimeException e){
			e.printStackTrace();
		}
		
		
		Integer sid = this.sellBackManager.saveGoodsList(sellBackGoods);
		return sid;
	}
	 
	
	/**
	 * 编辑退货商品
	 */
	public void editGoodsList(Integer goodsNum,Integer recid,Integer goodsid,String remark,Integer storageNum){
		if(goodsNum!=null){
			if(goodsNum>0){
				Map map = new HashMap();
				map.put("recid", recid);
				map.put("goods_id", goodsid);
				map.put("return_num", goodsNum);
				map.put("goods_remark", remark);
				this.sellBackManager.editGoodsNum(map);
			}else{
				this.sellBackManager.delGoods(recid, goodsid);
			}
		}
		if(storageNum!=null)
			this.sellBackManager.editStorageNum(recid,goodsid,storageNum);//修改入库数量
	}
	
	/**
	 * 取消退货
	 * @return
	 */
	public String cancel(){
		try {
			SellBackList sellbacklist = null;
			status = ctype;
			if (id != null) {
				if (status == 0 || status == 1) {
					sellbacklist = this.sellBackManager.get(id);
				} else {
					this.showErrorJson("该退货单的商品已入库，不能取消退货！");
					return this.JSON_MESSAGE;
				}
			} else {
				if (sellBackList.getTradeno() != null) {
					if (status == 0 || status == 1) {
						sellbacklist = this.sellBackManager.get(sellBackList.getTradeno());
					} else {
						this.showErrorJson("该退货单的商品已入库，不能取消退货！");
						return this.JSON_MESSAGE;
					}
				}
			}
			if (sellbacklist != null) {
				sellbacklist.setTradestatus(4);// 取消
				this.sellBackManager.save(sellbacklist);
				this.sellBackManager.saveLog(sellbacklist.getId(),
						SellBackStatus.valueOf(sellbacklist.getTradestatus()),
						"取消退货，原因：" + cancelRemark);
				orderinfo = orderManager.get(sellbacklist.getOrdersn());// 订单详细
				this.sellBackManager.delSellerBackChilds(orderinfo.getOrder_id());
				this.showSuccessJson("取消退货成功！");
			} else {
				this.showSuccessJson("操作成功！");
			}
		} catch (Exception e) {
			this.showErrorJson("取消退货失败！");
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 获取整箱详细
	 * @return
	 */
	public String packDetail(){
		orderItemDetail = orderManager.getOrderItemDetail(itemId);
		return "packDetail";
	}
	
	/**
	 * 创建退货单号
	 */
	public String createSn() {
		Date now = new Date();
		String sn = com.enation.framework.util.DateUtil.toString(now,
				"yyMMddhhmmss");
		return sn;
	}
	public ISellBackManager getSellBackManager() {
		return sellBackManager;
	}
	public void setSellBackManager(ISellBackManager sellBackManager) {
		this.sellBackManager = sellBackManager;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public ILogiManager getLogiManager() {
		return logiManager;
	}
	public void setLogiManager(ILogiManager logiManager) {
		this.logiManager = logiManager;
	}
	public Integer getOrderId() {
		return orderId;
	}
	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}
	public Order getOrderinfo() {
		return orderinfo;
	}
	public void setOrderinfo(Order orderinfo) {
		this.orderinfo = orderinfo;
	}
	public List getOrderItem() {
		return orderItem;
	}
	public void setOrderItem(List orderItem) {
		this.orderItem = orderItem;
	}
	public String getTradesn() {
		return tradesn;
	}
	public void setTradesn(String tradesn) {
		this.tradesn = tradesn;
	}
	public SellBackList getSellBackList() {
		return sellBackList;
	}
	public void setSellBackList(SellBackList sellBackList) {
		this.sellBackList = sellBackList;
	}
	public Integer[] getGoodsId() {
		return goodsId;
	}
	public void setGoodsId(Integer[] goodsId) {
		this.goodsId = goodsId;
	}
	public String[] getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String[] goodsName) {
		this.goodsName = goodsName;
	}
	public Integer[] getGoodsNum() {
		return goodsNum;
	}
	public void setGoodsNum(Integer[] goodsNum) {
		this.goodsNum = goodsNum;
	}
	public String[] getGoodsRemark() {
		return goodsRemark;
	}
	public void setGoodsRemark(String[] goodsRemark) {
		this.goodsRemark = goodsRemark;
	}
	public Double[] getGoodsPrice() {
		return goodsPrice;
	}
	public void setGoodsPrice(Double[] goodsPrice) {
		this.goodsPrice = goodsPrice;
	}
	public Integer[] getPayNum() {
		return payNum;
	}
	public void setPayNum(Integer[] payNum) {
		this.payNum = payNum;
	}
	public Integer[] getReturnNum() {
		return returnNum;
	}
	public void setReturnNum(Integer[] returnNum) {
		this.returnNum = returnNum;
	}
	public Integer[] getOldStorageNum() {
		return oldStorageNum;
	}
	public void setOldStorageNum(Integer[] oldStorageNum) {
		this.oldStorageNum = oldStorageNum;
	}
	public Integer[] getStorageNum() {
		return storageNum;
	}
	public void setStorageNum(Integer[] storageNum) {
		this.storageNum = storageNum;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public List getGoodsList() {
		return goodsList;
	}
	public void setGoodsList(List goodsList) {
		this.goodsList = goodsList;
	}
	public List getLogList() {
		return logList;
	}
	public void setLogList(List logList) {
		this.logList = logList;
	}
	public String getCancelRemark() {
		return cancelRemark;
	}
	public void setCancelRemark(String cancelRemark) {
		this.cancelRemark = cancelRemark;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	public Integer[] getGid() {
		return gid;
	}
	public void setGid(Integer[] gid) {
		this.gid = gid;
	}
	public IDepotManager getDepotManager() {
		return depotManager;
	}
	public void setDepotManager(IDepotManager depotManager) {
		this.depotManager = depotManager;
	}
	public Integer getDepotid() {
		return depotid;
	}
	public void setDepotid(Integer depotid) {
		this.depotid = depotid;
	}
	public Integer getCtype() {
		return ctype;
	}
	public void setCtype(Integer ctype) {
		this.ctype = ctype;
	}
	public Integer[] getProductId() {
		return productId;
	}
	public void setProductId(Integer[] productId) {
		this.productId = productId;
	}
	public String getSeller_remark() {
		return seller_remark;
	}
	public void setSeller_remark(String seller_remark) {
		this.seller_remark = seller_remark;
	}
	public String getDepot_name() {
		return depot_name;
	}
	public void setDepot_name(String depot_name) {
		this.depot_name = depot_name;
	}
	public List<Depot> getDepotlist() {
		return depotlist;
	}
	public void setDepotlist(List<Depot> depotlist) {
		this.depotlist = depotlist;
	}
	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}
	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}
	public List getPaymentList() {
		return paymentList;
	}
	public void setPaymentList(List paymentList) {
		this.paymentList = paymentList;
	}
	public List getOrderItemDetail() {
		return orderItemDetail;
	}
	public void setOrderItemDetail(List orderItemDetail) {
		this.orderItemDetail = orderItemDetail;
	}
	public Integer getItemId() {
		return itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public String getPackDetailJson() {
		return packDetailJson;
	}
	public void setPackDetailJson(String packDetailJson) {
		this.packDetailJson = packDetailJson;
	}
	public List getReturn_child_list() {
		return return_child_list;
	}
	public void setReturn_child_list(List return_child_list) {
		this.return_child_list = return_child_list;
	}
	public OrderPluginBundle getOrderPluginBundle() {
		return orderPluginBundle;
	}
	public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
		this.orderPluginBundle = orderPluginBundle;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	
}