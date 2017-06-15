package com.enation.app.b2b2c.core.service.saleType;

import java.util.List;
import java.util.Map;

import com.enation.app.base.core.model.SaleFree;
import com.enation.framework.database.Page;

/**
 * 营销管理免费模板维护
 * @author LiFenLong
 *
 */
public interface ISaleFreeManager {

	/**
	 * 获取营销管理免费模板列表
	 * @param pageNo
	 * @param pageSize 
	 * @param saleFreemap 
	 * @return Page
	 */
	public Page saleFreeList(Map saleFreemap,  int pageNo,int pageSize);
	/**
	 * 添加营销管理免费模板
	 * @throws Exception 
	 */
	public void addSaleFree(SaleFree saleFree) throws Exception;
	/**
	 * 获取一个营销管理免费模板
	 * @param levelId
	 */
	public SaleFree getSaleFree(Integer id);
	/**
	 * 修改营销管理免费模板
	 * @param no 
	 * @param integer 
	 */
	
	public void editSaleFree(SaleFree saleFree);
	/**
	 * 删除营销管理免费模板
	 * @param Id
	 * @throws Exception 
	 */
	public void delSaleFree(Integer id);
	/**
	 * @param pageSize 
	 * @param pageNo
	 * @param map 
	 * @description 获取营销管理免费申请列表
	 * @date 2016年8月29日 下午3:45:28
	 * @return
	 * @return Page
	 */
	public Page saleAuditList(Map map, int pageNo,int pageSize);
	/**
	 * @description 营销管理免费申请通过
	 * @date 2016年8月29日 下午6:47:33
	 * @param id
	 * @param typeId 
	 * @param storeId 
	 * @param auditNum 
	 * @return void
	 */
	public void audit_pass(Integer id, String storeId, String typeId, Integer auditNum);
	/**
	 * @description 营销管理免费申请未通过
	 * @date 2016年8月29日 下午7:50:19
	 * @param sign
	 * @param id
	 * @return void
	 */
	public void audit_fail(Integer sign, Integer id);
	/**
	 * @description 获取优惠券收费列表
	 * @date 2016年8月30日 上午11:52:15
	 * @param saleType
	 * @param store_level
	 * @param isFree 
	 * @return
	 * @return SaleFree
	 */
	public List<SaleFree> getSaleFreeByTypeId(int saleType, Integer store_level, int isFree);
	/**
	 * @description 店铺申请营销类型为typeId，数量为auditNum
	 * @date 2016年8月30日 下午4:06:24
	 * @param store_id
	 * @param typeId
	 * @param auditNum
	 * @return void
	 */
	public void addSaleAudit(Integer store_id, String typeId, Integer auditNum);

}
