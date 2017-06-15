package com.enation.app.b2b2c.core.model;

/**
 * @Description 消息推送类型
 *
 * @createTime 2016年11月2日 下午2:36:12
 *
 * @author yunbs;a href="mailto:yunbs@trans-it.cn">yunbs</a>
 */
public abstract class PushMsgTypeContent {
		
	public final static Integer PINGTAI_MSG = 1;//平台公告
	public final static Integer SYSTEM_MSG = 2;//系统消息
	public final static Integer ORDER_MSG = 3;//订单处理
	public final static Integer CARERROR_CODE = 4;//车辆故障

}
