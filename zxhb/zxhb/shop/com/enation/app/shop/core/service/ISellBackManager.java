package com.enation.app.shop.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.SellBackChild;
import com.enation.app.shop.core.model.SellBackGoodsList;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.model.SellBackStatus;
import com.enation.framework.database.Page;

public interface ISellBackManager {
	/**
	 * 分页显示退货单列表
	 * @param status
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page list(int page,int pageSize,Integer status);
	
	/**
	 * 分页显示退货搜索
	 * @param keyword
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page search(String keyword,int page,int pageSize);
	
	/**
	 * 获取退货单详细信息
	 * @param tradeno
	 * @return
	 */
	public SellBackList get(String tradeno);
	public SellBackList get(Integer id);
	
	/**
	 * 保存退货单
	 * @param data
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer save(SellBackList data);
	
	 
	
	/**
	 * 保存退货商品
	 * @param data
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public Integer saveGoodsList(SellBackGoodsList data);
	
	/**
	 * 根据退货单id及商品id获取该退货商品详细
	 * @param id
	 * @param goodsid
	 * @return
	 */
	public SellBackGoodsList getSellBackGoods(Integer id,Integer goodsid);
	
	/**
	 * 获取该退货id的商品列表
	 * @param id
	 * @return SellBackGoodsList
	 */
	public List getGoodsList(Integer id,String sn);
	public List getGoodsList(Integer id);
	
 
	/**
	 * 保存会员账户日志
	 * @param log
	 */
	public void saveAccountLog(Map log);
	
	
	/**
	 * 获取退货单id
	 * @param tradeno
	 * @return
	 */
	public Integer getRecid(String tradeno);
	
	/**
	 * 修改退货商品数量
	 * @param recid
	 * @param goodsid
	 */
	public void editGoodsNum(Map data);
	
	/**
	 * 修改入库数量
	 * @param id
	 * @param goodsid
	 * @param num
	 */
	public void editStorageNum(Integer id,Integer goodsid,Integer num);
	
	/**
	 * 修改套餐商品中的子项库存
	 * @param orderId
	 * @param parentId
	 * @param goods_id
	 * @param num
	 */
	public void editChildStorageNum(Integer orderId,Integer parentId,Integer goods_id, Integer num);
	/**
	 * 删除退货商品表中的商品
	 * @param id
	 * @param goodsid
	 */
	public void delGoods(Integer id,Integer goodsid);
	
	/**
	 * 获取该退货id的操作日志
	 * @param id
	 * @return
	 */
	public List sellBackLogList(Integer id);
	
	
	/**
	 * 库存退货入库
	 */
	public void syncStore(SellBackList sellback);
	
	
	/**
	 * 财务结算
	 * @param backid 退货单id
	 * @param finance_remark 财务备注
	 * @param logdetail 日志详细
	 * @param alltotal_pay 退款金额
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void closePayable(int backid,String finance_remark,String logdetail,Double alltotal_pay);
	
	/**
	 * 记录退货操作日志
	 * @param id 退货单id
	 * @param status 状态
	 * @param logdetail 日志描述
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveLog(Integer id,SellBackStatus status,String logdetail);
	/**
	 * 记录退货操作日志
	 * @param id 退货单id
	 * @param status 状态
	 * @param logdetail 日志描述
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveLog(Integer id,SellBackStatus status,String logdetail,String memberName);
	
	/**
	 * 申请退货时查询是否已申请过
	 * @param sn
	 * @return
	 */
	public int searchSn(String sn);
	/**
	 * 根据会员获取退货申请列表
	 * @param member_id 会员ID
	 * @param page 分页
	 * @param pageSize 每页显示数量
	 * @return
	 */
	public Page list(Integer member_id,int page,int pageSize);
	/**
	 * 修改退货单状态
	 * @param status 状态
	 * @param id 退货单ID
	 * @param seller_remark 备注
	 */
	public void editStatus(Integer status,Integer id,String seller_remark);
	
	/**
	 * 保存退货整箱子项
	 * @param orderId	订单id
	 * @param goodsId	商品id
	 * @param parentId	整箱商品 的id
	 * @param returnNum	退货数量
	 * @param storageNum	购买数量
	 */
	public void saveSellbackChild(int orderId,int goodsId,int parentId,int returnNum);
	
	/**
	 * 修改退货整箱子项	
	 * @param orderId 订单id
	 * @param goodsId 商品id
	 * @param returnNum 退货数量
	 */
	public void updateSellbackChild(int orderId,int goodsId,int returnNum,int storageNum);
	
	/**
	 * 获取订单内整箱内的子项
	 * @param orderId 订单id
	 * @param goodsId 商品id
	 * @param returnNum
	 */
	public SellBackChild getSellbackChild(int orderId,int goodsId);
	
	/**
	 * 获取订单内一个整箱内的所有子项
	 * @param orderId
	 * @param parentGoodsId
	 * @return
	 */
	public List getSellbackChilds(int orderId ,int parentGoodsId);

	/**
	 * 根据订单id 清空退货子项表
	 * @param orderId
	 */
	public void delSellerBackChilds(int orderId);
	
	/**
	 * 获取一个整箱商品中的商品list(捆绑插件暂时移植到核心类里)
	 * @param goods_id
	 * @return
	 */
	public List<Map> list(int goods_id);

	/**
	 * 获取一个套餐中的子项详情(捆绑插件暂时移植到核心类里)
	 * @param goodsId
	 * @param relGoodsId
	 * @return
	 */
	public Map getPackInfo(int goodsId,int relGoodsId);
	/**
	 * 判断一个货品是否是套餐(捆绑插件暂时移植到核心类里)
	 * @param productid
	 * @return
	 */
	public int isPack(int productid);
	
}
