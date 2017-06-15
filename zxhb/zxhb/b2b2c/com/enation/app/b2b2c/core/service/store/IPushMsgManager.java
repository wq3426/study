package com.enation.app.b2b2c.core.service.store;

import java.util.List;
import java.util.Map;

import com.enation.app.b2b2c.core.model.store.PushMsg;
import com.enation.app.b2b2c.core.model.store.PushMsgType;
import com.enation.framework.database.Page;

/**
 * @Description 消息推送Manager
 *
 * @createTime 2016年8月29日 上午9:52:46
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
public interface IPushMsgManager {

	/**
	 * @description 消息拉取
	 * @date 2016年10月26日 下午3:00:32
	 * @param other
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page pushMsg_list(Map other, int page, int pageSize);
	
	/**
	 * @description 新增消息
	 * @date 2016年10月26日 下午5:32:35
	 * @param pushMsg
	 * @return void
	 */
	public void addPushMsg(PushMsg pushMsg);
	
	/**
	 * @description 消息修改
	 * @date 2016年10月26日 下午6:06:18
	 * @param id
	 * @return
	 * @return PushMsg
	 */
	public PushMsg getPushMsgDetail(Integer id);

	/**
	 * @description 消息修改保存
	 * @date 2016年10月26日 下午6:06:18
	 * @param id
	 * @return
	 * @return PushMsg
	 */
	public void updatePushMsg(PushMsg pushMsg);

	/**
	 * @description 删除消息
	 * @date 2016年10月26日 下午7:12:43
	 * @param id
	 * @return void
	 */
	public void delPushMsg(Integer id);
	/**
	 * @description 消息推送或者撤回
	 * @date 2016年10月31日 上午10:18:23
	 * @param pushMsg
	 * @return void
	 */
	public void edit(PushMsg pushMsg);

	/**
	 * @description 消息类型
	 * @date 2016年11月1日 下午5:16:10
	 * @return
	 * @return Page
	 */
	public List<PushMsgType> pushMsgType_list();

	/**
	 * @description 新增消息类型
	 * @date 2016年11月1日 下午5:35:28
	 * @param pushMsgType
	 * @return void
	 */
	public void addPushMsgType(PushMsgType pushMsgType);

	/**
	 * @description 根据memberId获取消息
	 * @date 2016年11月2日 下午12:53:35
	 * @param member_id
	 * @return
	 * @return List
	 */
	public List getPushMsgByMemberId(Integer storeId);

	/**
	 * @description 获取未读取消息的总数
	 * @date 2016年11月7日 下午3:03:28
	 * @param store_id
	 * @return
	 * @return Integer
	 */
	public Integer getCountPushMsgByMemberId(Integer store_id);
	
}
