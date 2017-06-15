package com.enation.app.base.jPush.util;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enation.app.base.jPush.JPushClient;
import com.enation.app.base.jPush.JPushSetting;
import com.enation.app.base.jPush.Servlet.PushServlet;
import com.enation.app.base.jPush.push.PushResult;
import com.enation.app.base.jPush.push.model.PushPayload;
import com.enation.app.base.jPush.push.model.SMS;
import com.enation.app.base.jPush.push.model.notification.IosAlert;

import cn.jiguang.commom.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import net.sf.json.JSONObject;

public class JPushUtil {

	protected static final Logger LOG = LoggerFactory.getLogger(PushServlet.class);

	public static void main(String args[]) {
//		SendPushToAllAliasAndAlertAndExtra("15201415929","艳欣你个臭不要脸滴","001","油耗排序");
		JSONObject dataObj = new JSONObject();
		dataObj.put("brand", "本田");
		dataObj.put("series", "艾力绅");
		dataObj.put("brandimage", "http://123.57.57.145/mall/statics/files/carmodel/honda.jpg");
		JSONObject gpsinfo = new JSONObject();
		gpsinfo.put("latitude", "121.127313628");
		gpsinfo.put("longitude", "121.435932");
		gpsinfo.put("gps_time", "2016-10-17 12:04");
		dataObj.put("gpsinfo", gpsinfo);
		SendPushToAllTagAndMsgAndExtra("京N77E65", dataObj.toString(), "", "");
	}

	/**
	 * @description 构建推送对象：内容为 ALERT 的通知,附加字段map(type,extra)
	 * @date 2016年8月24日 下午3:14:39
	 * @return void
	 */
	public static void SendPushAlert(String platform, String alert,String type,String extra) {
		ClientConfig clientConfig = ClientConfig.getInstance();
		JPushClient jpushClient = new JPushClient(JPushSetting.masterSecret, JPushSetting.appKey, null, clientConfig);
		PushPayload payload = null;
		if (platform.equals(JPushSetting.ALL)) {// 构建推送对象：所有平台，所有设备，内容为 ALERT
												// 的通知,附加字段map(type,extra)

			payload = PushServlet.buildPushObject_all_all_alert(alert,type,extra);

		} else if (platform.equals(JPushSetting.ANDROID)) {// 构建推送对象：android平台，所有设备，内容为
															// ALERT 的通知,附加字段map(type,extra)

			payload = PushServlet.buildPushObject_all_android_alert(alert,type,extra);

		} else if (platform.equals(JPushSetting.IOS)) {// 构建推送对象：ios平台，所有设备，内容为
														// ALERT 的通知,附加字段map(type,extra)

			payload = PushServlet.buildPushObject_all_ios_alert(alert,type,extra);

		} else if (platform.equals(JPushSetting.WINPHONE)) {// 构建推送对象：winphone平台，所有设备，内容为
															// ALERT 的通知,附加字段map(type,extra)

			payload = PushServlet.buildPushObject_all_winphone_alert(alert,type,extra);

		} else if (platform.equals(JPushSetting.ANDROID_IOS)) {// 构建推送对象：android和ios平台，所有设备，内容为
																// ALERT 的通知,附加字段map(type,extra)

			payload = PushServlet.buildPushObject_all_android_ios_alert(alert,type,extra);

		} else if (platform.equals(JPushSetting.ANDROID_WINPHONE)) {// 构建推送对象：android和winphone平台，所有设备，内容为
																	// ALERT 的通知,附加字段map(type,extra)

			payload = PushServlet.buildPushObject_all_android_winphone_alert(alert,type,extra);

		} else if (platform.equals(JPushSetting.IOS_WINPHONE)) {// 构建推送对象：ios和winphone平台，所有设备，内容为
																// ALERT 的通知,附加字段map(type,extra)

			payload = PushServlet.buildPushObject_all_ios_winphone_alert(alert,type,extra);

		}
		try {
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("服务器返回结果 - " + result);

		} catch (APIConnectionException e) {
			LOG.error("连接错误，请稍后重试 ", e);

		} catch (APIRequestException e) {
			LOG.error("JPush服务器错误响应", e);
			LOG.info("HTTP 状态码: " + e.getStatus());
			LOG.info("错误码: " + e.getErrorCode());
			LOG.info("错误信息: " + e.getErrorMessage());
			LOG.info("信息ID: " + e.getMsgId());
		}

	}

	/**
	 * @description 构建推送对象：platform平台，内容为 ALERT 的通知，消息为msg,附加字段map(type,extra)
	 * @date 2016年8月25日 上午11:04:53
	 * @param alert
	 * @param msg
	 * @return void
	 */
	public static void SendPushAlertAndMsg(String platform, String alert, String msg,String type,String extra) {
		JPushClient jpushClient = new JPushClient(JPushSetting.masterSecret, JPushSetting.appKey);
		PushPayload payload = null;
		if (platform.equals(JPushSetting.ALL)) {// 构建推送对象：所有平台，所有设备，内容为 ALERT
												// 的通知,附加字段map(type,extra)

			payload = PushServlet.buildPushObject_all_all_alert_msg(alert,msg,type,extra);

		} else if (platform.equals(JPushSetting.ANDROID)) {// 构建推送对象：android平台，所有设备，内容为
															// ALERT 的通知,消息为msg,附加字段map(type,extra)

			payload = PushServlet.buildPushObject_all_android_alert_msg(alert,msg,type,extra);

		} else if (platform.equals(JPushSetting.IOS)) {// 构建推送对象：ios平台，所有设备，内容为
														// ALERT 的通知,消息为msg,附加字段map(type,extra)

			payload = PushServlet.buildPushObject_all_ios_alert_msg(alert,msg,type,extra);

		} else if (platform.equals(JPushSetting.WINPHONE)) {// 构建推送对象：winphone平台，所有设备，内容为
															// ALERT 的通知,消息为msg,附加字段map(type,extra)

			payload = PushServlet.buildPushObject_all_winphone_alert_msg(alert,msg,type,extra);

		} else if (platform.equals(JPushSetting.ANDROID_IOS)) {// 构建推送对象：android和ios平台，所有设备，内容为
																// ALERT的通知,消息为msg,附加字段map(type,extra)

			payload = PushServlet.buildPushObject_all_android_ios_alert_msg(alert,msg,type,extra);

		} else if (platform.equals(JPushSetting.ANDROID_WINPHONE)) {// 构建推送对象：android和winphone平台，所有设备，内容为
																	// ALERT的通知,消息为msg,附加字段map(type,extra)

			payload = PushServlet.buildPushObject_all_android_winphone_alert_msg(alert,msg,type,extra);

		} else if (platform.equals(JPushSetting.IOS_WINPHONE)) {// 构建推送对象：ios和winphone平台，所有设备，内容为
																// ALERT 的通知,消息为msg,附加字段map(type,extra)

			payload = PushServlet.buildPushObject_all_ios_winphone_alert_msg(alert,msg,type,extra);

		}
		try {
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("服务器返回结果 - " + result);

		} catch (APIConnectionException e) {
			LOG.error("连接错误，请稍后重试 ", e);

		} catch (APIRequestException e) {
			LOG.error("JPush服务器错误响应", e);
			LOG.info("HTTP 状态码: " + e.getStatus());
			LOG.info("错误码: " + e.getErrorCode());
			LOG.info("错误信息: " + e.getErrorMessage());
			LOG.info("信息ID: " + e.getMsgId());
		}

	}

	/**
	 * @description 所有平台，推送目标是别名为 "alias"，通知内容为 ALERT,附加字段map(type,extra)
	 * @date 2016年8月23日 下午2:52:27
	 * @return
	 * @return PushPayload
	 */
	public static void SendPushAliasAndAlert(String alias, String alert,String type,String extra) {
		JPushClient jpushClient = new JPushClient(JPushSetting.masterSecret, JPushSetting.appKey);
		// 构建推送对象：所有平台，推送目标是别名为 "alias"， 通知内容为 ALERT。
		PushPayload	payload = PushServlet.buildPushObject_all_alias_alert(alias,alert,type,extra);
		try {
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("服务器返回结果 - " + result);

		} catch (APIConnectionException e) {
			LOG.error("连接错误，请稍后重试 ", e);

		} catch (APIRequestException e) {
			LOG.error("JPush服务器错误响应", e);
			LOG.info("HTTP 状态码: " + e.getStatus());
			LOG.info("错误码: " + e.getErrorCode());
			LOG.info("错误信息: " + e.getErrorMessage());
			LOG.info("信息ID: " + e.getMsgId());
		}

	}

	/**
	 * @description 目标标签为 "tag" 的设备，通知 ALERT,附加字段map(type,extra)
	 * @date 2016年8月26日 上午10:37:29
	 * @param tag
	 * @param alert
	 * @param title
	 * @return void
	 */
	public static void SendPushTagAndAlert(String tag, String alert,String type,String extra) {
		JPushClient jpushClient = new JPushClient(JPushSetting.masterSecret, JPushSetting.appKey);
		// 构建推送对象：所有平台，推送目标是标签为 "tag"的设备"，通知内容为 ALERT。
		PushPayload	payload = PushServlet.buildPushObject_all_all_tag_alert(tag,alert,type,extra);
		try {
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("服务器返回结果 - " + result);

		} catch (APIConnectionException e) {
			LOG.error("连接错误，请稍后重试 ", e);

		} catch (APIRequestException e) {
			LOG.error("JPush服务器错误响应", e);
			LOG.info("HTTP 状态码: " + e.getStatus());
			LOG.info("错误码: " + e.getErrorCode());
			LOG.info("错误信息: " + e.getErrorMessage());
			LOG.info("信息ID: " + e.getMsgId());
		}

	}

	/**
	 * @description 所有平台，目标标签为 "tag" 的设备，内容是 Android 通知 ALERT，并且标题为
	 *              TITLE,附件字段是key/value是key/extraValue
	 * @date 2016年8月23日 下午2:52:49
	 * @return
	 * @return PushPayload
	 */
	public static void SendPushToAllAndTagAndTitle(String tag, String alert, String title,String type,String extra) {
		JPushClient jpushClient = new JPushClient(JPushSetting.masterSecret, JPushSetting.appKey);
		PushPayload payload = PushServlet.buildPushObject_all_tag_alertWithTitle(tag, alert, title,type,extra);
		try {
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("服务器返回结果 - " + result);

		} catch (APIConnectionException e) {
			LOG.error("连接错误，请稍后重试 ", e);

		} catch (APIRequestException e) {
			LOG.error("JPush服务器错误响应", e);
			LOG.info("HTTP 状态码: " + e.getStatus());
			LOG.info("错误码: " + e.getErrorCode());
			LOG.info("错误信息: " + e.getErrorMessage());
			LOG.info("信息ID: " + e.getMsgId());
		}

	}
	
	/**
	 * @description 所有平台，推送目标是别名为 "alias"， 通知是alert,附件字段是key/value是key/extraValue
	 * @date 2016年8月27日 上午11:13:05
	 * @param tag
	 * @param alert
	 * @param extraValue
	 * @return void
	 */
	public static void SendPushToAllAliasAndAlertAndExtra(String alias,String alert,String type,String extraValue) {
		JPushClient jpushClient = new JPushClient(JPushSetting.masterSecret, JPushSetting.appKey);
		PushPayload payload = PushServlet.buildPushObject_all_all_alias_extra(alias,alert,type,extraValue);
		try {
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("服务器返回结果 - " + result);

		} catch (APIConnectionException e) {
			LOG.error("连接错误，请稍后重试 ", e);

		} catch (APIRequestException e) {
			LOG.error("JPush服务器错误响应", e);
			LOG.info("HTTP 状态码: " + e.getStatus());
			LOG.info("错误码: " + e.getErrorCode());
			LOG.info("错误信息: " + e.getErrorMessage());
			LOG.info("信息ID: " + e.getMsgId());
		}

	}

	/**
	 * @description 所有平台，推送目标是别名为 "alias"， 通知是alert,消息是msg,附件字段是key/value是key/extraValue
	 * @date 2016年8月27日 上午11:51:28
	 * @param alias
	 * @param alert
	 * @param extraValue
	 * @return void
	 */
	public static void SendPushToAllAliasAndMsgAndExtra(String alias,String msg,String alert,String type,String extraValue) {
		JPushClient jpushClient = new JPushClient(JPushSetting.masterSecret, JPushSetting.appKey);
		PushPayload payload = PushServlet.buildPushObject_all_all_alias_msg_extra(alias,msg,alert,type,extraValue);
		try {
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("服务器返回结果 - " + result);

		} catch (APIConnectionException e) {
			LOG.error("连接错误，请稍后重试 ", e);

		} catch (APIRequestException e) {
			LOG.error("JPush服务器错误响应", e);
			LOG.info("HTTP 状态码: " + e.getStatus());
			LOG.info("错误码: " + e.getErrorCode());
			LOG.info("错误信息: " + e.getErrorMessage());
			LOG.info("信息ID: " + e.getMsgId());
		}

	}
	
	/**
	 * @description 所有平台，推送目标是别名为 "tag"， 消息是msg,附件字段是key/value是key/extraValue
	 * @date 2016年10月17日 上午11:38:54
	 * @param tag
	 * @param msg
	 * @param type
	 * @param extraValue
	 */
	public static void SendPushToAllTagAndMsgAndExtra(String tag,String msg,String type,String extraValue) {
		JPushClient jpushClient = new JPushClient(JPushSetting.masterSecret, JPushSetting.appKey);
		PushPayload payload = PushServlet.buildPushObject_all_all_tag_msg(tag, msg, type, extraValue);
		try {
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("服务器返回结果 - " + result);

		} catch (APIConnectionException e) {
			LOG.error("连接错误，请稍后重试 ", e);

		} catch (APIRequestException e) {
			LOG.error("JPush服务器错误响应", e);
			LOG.info("HTTP 状态码: " + e.getStatus());
			LOG.info("错误码: " + e.getErrorCode());
			LOG.info("错误信息: " + e.getErrorMessage());
			LOG.info("信息ID: " + e.getMsgId());
		}
	}
	
	
	/**
	 * @description 所有平台，目标便签是"tag",通知是alert,附件字段是key/value是key/extraValue
	 * @date 2016年8月27日 上午11:10:53
	 * @param tag 
	 * @param alert
	 * @param title
	 * @return void
	 */
	public static void SendPushToAllTagAndAlertAndExtra(String tag,String alert,String type,String extraValue) {
		JPushClient jpushClient = new JPushClient(JPushSetting.masterSecret, JPushSetting.appKey);
		PushPayload payload = PushServlet.buildPushObject_all_all_tag_extra(tag, alert,type,extraValue);
		try {
			PushResult result = jpushClient.sendPush(payload);
			LOG.info("服务器返回结果 - " + result);

		} catch (APIConnectionException e) {
			LOG.error("连接错误，请稍后重试 ", e);

		} catch (APIRequestException e) {
			LOG.error("JPush服务器错误响应", e);
			LOG.info("HTTP 状态码: " + e.getStatus());
			LOG.info("错误码: " + e.getErrorCode());
			LOG.info("错误信息: " + e.getErrorMessage());
			LOG.info("信息ID: " + e.getMsgId());
		}

	}

	
	/**
	 * @description 发送一个Android短信内容消息与别名。
	 * @date 2016年8月24日 下午3:41:08
	 * @return void
	 */
	public static void SendWithSMS() {
		JPushClient jpushClient = new JPushClient(JPushSetting.masterSecret, JPushSetting.appKey);
		try {
			SMS sms = SMS.content("Test SMS", 10);// 短信内容和延迟时间。如果是null,短信不工作,不影响功能。
			// 设置消息标题，消息内容，别名列表
			PushResult result = jpushClient.sendAndroidMessageWithAlias("Test SMS", "test sms", sms, "alias1");
			LOG.info("服务器返回结果 - " + result);
		} catch (APIConnectionException e) {
			LOG.error("连接错误，请稍后重试 ", e);
		} catch (APIRequestException e) {
			LOG.error("JPush服务器错误响应", e);
			LOG.info("HTTP 状态码: " + e.getStatus());
			LOG.info("错误码: " + e.getErrorCode());
			LOG.info("错误信息: " + e.getErrorMessage());
			LOG.info("信息ID: " + e.getMsgId());
		}
	}

	/**
	 * @description 发送一个iOS通知与别名。
	 * @date 2016年8月24日 下午3:59:02
	 * @return void
	 */
	public static void SendIosAlert() {
		JPushClient jpushClient = new JPushClient(JPushSetting.masterSecret, JPushSetting.appKey);

		IosAlert alert = IosAlert.newBuilder().setTitleAndBody("test alert", "test ios alert json")
				.setActionLocKey("PLAY").build();
		try {
			PushResult result = jpushClient.sendIosNotificationWithAlias(alert, new HashMap<String, String>(),
					"alias1");
			LOG.info("服务器返回结果 - " + result);
		} catch (APIConnectionException e) {
			LOG.error("连接错误，请稍后重试 ", e);
		} catch (APIRequestException e) {
			LOG.error("JPush服务器错误响应", e);
			LOG.info("HTTP 状态码: " + e.getStatus());
			LOG.info("错误码: " + e.getErrorCode());
			LOG.info("错误信息: " + e.getErrorMessage());
			LOG.info("信息ID: " + e.getMsgId());
		}
	}
}
