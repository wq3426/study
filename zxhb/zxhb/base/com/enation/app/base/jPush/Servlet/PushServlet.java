package com.enation.app.base.jPush.Servlet;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enation.app.base.jPush.JPushClient;
import com.enation.app.base.jPush.JPushSetting;
import com.enation.app.base.jPush.push.PushResult;
import com.enation.app.base.jPush.push.model.Message;
import com.enation.app.base.jPush.push.model.Options;
import com.enation.app.base.jPush.push.model.Platform;
import com.enation.app.base.jPush.push.model.PushPayload;
import com.enation.app.base.jPush.push.model.audience.Audience;
import com.enation.app.base.jPush.push.model.audience.AudienceTarget;
import com.enation.app.base.jPush.push.model.notification.AndroidNotification;
import com.enation.app.base.jPush.push.model.notification.IosNotification;
import com.enation.app.base.jPush.push.model.notification.Notification;
import com.enation.app.base.jPush.push.model.notification.WinphoneNotification;
import com.google.gson.JsonObject;

import cn.jiguang.commom.ClientConfig;
import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;

public class PushServlet {
    protected static final Logger LOG = LoggerFactory.getLogger(PushServlet.class);

    // demo App defined in resources/jpush-api.conf 
	public static final String TITLE = "盈驾";//通知标题 	
    public static final String ALERT = "艳欣这个臭不要脸滴！";//通知内容
    public static final String MSG_CONTENT = "Test from API Example - msgContent";//消息内容本身
    public static final String REGISTRATION_ID = "0900e8d85ef";//注册ID
    public static final String TAG = "tag_api";//标签

	public static void main(String[] args) {
//        testSendPushWithCustomConfig();
//        testSendIosAlert();
		testSendPush();
	}
	
	
	public static void testSendPush() {
	    // HttpProxy proxy = new HttpProxy("localhost", 3128);
	    // Can use this https proxy: https://github.com/Exa-Networks/exaproxy
		ClientConfig clientConfig = ClientConfig.getInstance();
        JPushClient jpushClient = new JPushClient(JPushSetting.masterSecret, JPushSetting.appKey, null, clientConfig);
        
        // For push, all you need do is to build PushPayload object.
        PushPayload payload = buildPushObject_all_all_alert(ALERT,"001","qweqweqw");
        
        try {
            PushResult result = jpushClient.sendPush(payload);
            LOG.info("Got result - " + result);
            
        } catch (APIConnectionException e) {
            LOG.error("Connection error. Should retry later. ", e);
            
        } catch (APIRequestException e) {
            LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
            LOG.info("Msg ID: " + e.getMsgId());
        }
	}
	/**
	 * @description 快捷地构建推送对象：所有平台，所有设备，内容为 ALERT 的通知。
	 * @date 2016年8月23日 下午2:52:16
	 * @return
	 * @return PushPayload
	 */
	public static PushPayload buildPushObject_all_all_alert(String alert,String type,String extraValue) {
	    return PushPayload.newBuilder()
	    		.setPlatform(Platform.all())
	    		.setAudience(Audience.all())
	    		.setNotification(Notification.newBuilder()
	    				.setAlert(alert)
	    				.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(WinphoneNotification.newBuilder()
                				.addExtra(type,extraValue).build())
	    				.build())
	    		.build();
	}
	
	/**
	 * @description 快捷地构建推送对象：android平台，所有设备，内容为 ALERT 的通知。
	 * @date 2016年8月23日 下午2:52:16
	 * @return
	 * @return PushPayload
	 */
	public static PushPayload buildPushObject_all_android_alert(String alert,String type,String extraValue) {
	    return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
                		.setAlert(alert)
                		.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.build())
                .build();
	}
	
	/**
	 * @description 快捷地构建推送对象：ios平台，所有设备，内容为 ALERT 的通知。
	 * @date 2016年8月23日 下午2:52:16
	 * @return
	 * @return PushPayload
	 */
	public static PushPayload buildPushObject_all_ios_alert(String alert,String type,String extraValue) {
	    return PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
                		.setAlert(alert)
                		.addPlatformNotification(IosNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.build())
                .build();
	}
	
	/**
	 * @description  快捷地构建推送对象：winphone平台，所有设备，内容为 ALERT 的通知。
	 * @date 2016年8月26日 下午3:48:38
	 * @param ALERT
	 * @return
	 * @return PushPayload
	 */
	public static PushPayload buildPushObject_all_winphone_alert(String alert,String type,String extraValue) {
	    return PushPayload.newBuilder()
                .setPlatform(Platform.winphone())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
                		.setAlert(alert)
                		.addPlatformNotification(WinphoneNotification.newBuilder()
                				.addExtra(type, extraValue).build())
                		.build())
                .build();
	}
	
	/**
	 * @description 快捷地构建推送对象：android和ios平台，所有设备，内容为 ALERT 的通知。
	 * @date 2016年8月26日 下午3:46:15
	 * @param ALERT
	 * @return
	 * @return PushPayload
	 */
	public static PushPayload buildPushObject_all_android_ios_alert(String alert,String type,String extraValue) {
	    return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
                		.setAlert(alert)
                		.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type, extraValue).build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.addExtra(type, extraValue).build())
                		.build())
                .build();
	}
	
	/**
	 * @description 快捷地构建推送对象：android和winphone平台，所有设备，内容为 ALERT 的通知。
	 * @date 2016年8月26日 下午3:47:20
	 * @param ALERT
	 * @return
	 * @return PushPayload
	 */
	public static PushPayload buildPushObject_all_android_winphone_alert(String alert,String type,String extraValue) {
	    return PushPayload.newBuilder()
                .setPlatform(Platform.android_winphone())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
                		.setAlert(alert)
                		.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type, extraValue).build())
                		.addPlatformNotification(WinphoneNotification.newBuilder()
                				.addExtra(type, extraValue).build())
                		.build())
                .build();
	}
	
	/**
	 * @description 快捷地构建推送对象：ios和winphone平台，所有设备，内容为 ALERT 的通知。
	 * @date 2016年8月26日 下午3:48:02
	 * @param ALERT
	 * @return
	 * @return PushPayload
	 */
	public static PushPayload buildPushObject_all_ios_winphone_alert(String alert,String type,String extraValue) {
	    return PushPayload.newBuilder()
                .setPlatform(Platform.ios_winphone())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
                		.setAlert(alert)
                		.addPlatformNotification(IosNotification.newBuilder()
                				.addExtra(type, extraValue).build())
                		.addPlatformNotification(WinphoneNotification.newBuilder()
                				.addExtra(type, extraValue).build())
                		.build())
                .build();
	}
	
	/**
	 * @description 快捷地构建推送对象：所有平台，所有设备，内容为 ALERT 的通知，消息为msg。
	 * @date 2016年8月26日 下午4:00:10
	 * @param ALERT
	 * @param msg
	 * @return
	 * @return PushPayload
	 */
	public static PushPayload buildPushObject_all_all_alert_msg(String alert,String msg,String type,String extraValue) {
	    return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
	    				.setAlert(alert)
	    				.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(WinphoneNotification.newBuilder()
                				.addExtra(type,extraValue).build())
	    				.build())
                .setMessage(Message.content(msg))
                .build();
	}
	
	/**
	 * @description 快捷地构建推送对象：android平台，所有设备，内容为 ALERT 的通知，消息为msg。
	 * @date 2016年8月25日 上午11:02:31
	 * @param ALERT
	 * @param msg
	 * @return
	 * @return PushPayload
	 */
	public static PushPayload buildPushObject_all_android_alert_msg(String alert,String msg,String type,String extraValue) {
	    return PushPayload.newBuilder()
                .setPlatform(Platform.android())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
	    				.setAlert(alert)
	    				.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type,extraValue).build())
	    				.build())
                .setMessage(Message.content(msg))
                .build();
	}
	
	/**
	 * @description 快捷地构建推送对象：ios平台，所有设备，内容为 ALERT 的通知，消息为msg。
	 * @date 2016年8月26日 下午4:03:29
	 * @param ALERT
	 * @param msg
	 * @return
	 * @return PushPayload
	 */
	public static PushPayload buildPushObject_all_ios_alert_msg(String alert,String msg,String type,String extraValue) {
	    return PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
	    				.setAlert(alert)
                		.addPlatformNotification(IosNotification.newBuilder()
                				.addExtra(type,extraValue).build())
	    				.build())
                .setMessage(Message.content(msg))
                .build();
	}
	
	/**
	 * @description 快捷地构建推送对象：winphone平台，所有设备，内容为 ALERT 的通知，消息为msg。
	 * @date 2016年8月26日 下午4:03:40
	 * @param ALERT
	 * @param msg
	 * @return
	 * @return PushPayload
	 */
	public static PushPayload buildPushObject_all_winphone_alert_msg(String alert,String msg,String type,String extraValue) {
	    return PushPayload.newBuilder()
                .setPlatform(Platform.winphone())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
	    				.setAlert(alert)
                		.addPlatformNotification(WinphoneNotification.newBuilder()
                				.addExtra(type,extraValue).build())
	    				.build())
                .setMessage(Message.content(msg))
                .build();
	}
	
	/**
	 * @description 快捷地构建推送对象：android和ios平台，所有设备，内容为 ALERT 的通知，消息为msg。
	 * @date 2016年8月26日 下午4:03:45
	 * @param ALERT
	 * @param msg
	 * @return
	 * @return PushPayload
	 */
	public static PushPayload buildPushObject_all_android_ios_alert_msg(String alert,String msg,String type,String extraValue) {
	    return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
	    				.setAlert(alert)
	    				.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.addExtra(type,extraValue).build())
	    				.build())
                .setMessage(Message.content(msg))
                .build();
	}
	
	/**
	 * @description 快捷地构建推送对象：android和winphone平台，所有设备，内容为 ALERT 的通知，消息为msg。
	 * @date 2016年8月26日 下午4:03:50
	 * @param ALERT
	 * @param msg
	 * @return
	 * @return PushPayload
	 */
	public static PushPayload buildPushObject_all_android_winphone_alert_msg(String alert,String msg,String type,String extraValue) {
	    return PushPayload.newBuilder()
                .setPlatform(Platform.android_winphone())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
	    				.setAlert(alert)
	    				.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(WinphoneNotification.newBuilder()
                				.addExtra(type,extraValue).build())
	    				.build())
                .setMessage(Message.content(msg))
                .build();
	}
	
	/**
	 * @description 快捷地构建推送对象：ios和winphone平台，所有设备，内容为 ALERT 的通知，消息为msg。
	 * @date 2016年8月26日 下午4:03:56
	 * @param ALERT
	 * @param msg
	 * @return
	 * @return PushPayload
	 */
	public static PushPayload buildPushObject_all_ios_winphone_alert_msg(String alert,String msg,String type,String extraValue) {
	    return PushPayload.newBuilder()
                .setPlatform(Platform.ios_winphone())
                .setAudience(Audience.all())
                .setNotification(Notification.newBuilder()
	    				.setAlert(alert)
                		.addPlatformNotification(IosNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(WinphoneNotification.newBuilder()
                				.addExtra(type,extraValue).build())
	    				.build())
                .setMessage(Message.content(msg))
                .build();
	}
	
	
	/**
	 * @description 所有平台，推送目标是别名为 "alias"，通知内容为 ALERT,附加字段map(type,extra)
	 * @date 2016年8月23日 下午2:52:27
	 * @return
	 * @return PushPayload
	 */
    public static PushPayload buildPushObject_all_alias_alert(String alias,String alert,String type,String extraValue) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder()
	    				.setAlert(alert)
	    				.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(WinphoneNotification.newBuilder()
                				.addExtra(type,extraValue).build())
	    				.build())
                .build();
    }
    
    /**
     * @description 所有平台，标签是"tag",通知内容为 ALERT。 
     * @date 2016年8月26日 下午4:38:24
     * @param tag
     * @param alert
     * @return
     * @return PushPayload
     */
    public static PushPayload buildPushObject_all_all_tag_alert(String tag,String alert,String type,String extraValue) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.tag(tag))
                .setNotification(Notification.newBuilder()
	    				.setAlert(alert)
	    				.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(WinphoneNotification.newBuilder()
                				.addExtra(type,extraValue).build())
	    				.build())
                .build();
    }
    
    /**
     * @description 所有平台，标签是"tag",通知内容为 message。 
     * @date 2016年10月17日 上午11:22:12
     * @param tag
     * @param msg
     * @param type
     * @param extraValue
     * @return
     */
    public static PushPayload buildPushObject_all_all_tag_msg(String tag,String msg,String type,String extraValue) {
    	return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.tag(tag))
                .setMessage(Message.newBuilder().setMsgContent(msg).addExtra(type, extraValue).build())
                .build();
    }
    
    
    /**
     * @description 所有平台，目标标签为 "tag" 的设备，内容 通知 ALERT，并且标题为 TITLE。
     * @date 2016年8月23日 下午2:52:49
     * @return
     * @return PushPayload
     */
    public static PushPayload buildPushObject_all_tag_alertWithTitle(String tag,String alert,String title,String type,String extraValue) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.tag(tag))
                .setNotification(Notification.newBuilder()
                		.setAlert(alert)
	    				.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type,extraValue).setTitle(title).build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(WinphoneNotification.newBuilder()
                				.addExtra(type,extraValue).setTitle(title).build())
	    				.build())
                .build();
    }
    
    /**
     * @description 平台是 Android和ios，目标标签为 "tag" 的设备，
     * 				内容是 Android 通知 alert，并且标题为title。
     * 				内容是 ios 通知 alert，并且标题为 title,应用角标为1,附加字段key/value=extra_key/extraValue
     * @date 2016年8月23日 下午2:59:24
     * @return
     * @return PushPayload
     */
    public static PushPayload buildPushObject_android_and_ios(String tag,String alert,String title,String type,String extraValue) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.tag(tag))
                .setNotification(Notification.newBuilder()
                		.setAlert(alert)
                		.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type, extraValue).setTitle(title).build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.incrBadge(1)//应用角标
                				.addExtra(type, extraValue).build())
                		.build())
                .build();
    }

    /**
     * @description 所有平台，目标别名是"alias",通知是alert,附件字段是key/value是key/extraValue
     * @date 2016年8月27日 上午11:19:26
     * @param alias
     * @param alert
     * @param extraValue
     * @return
     * @return PushPayload
     */
    public static PushPayload buildPushObject_all_all_alias_extra(String alias,String alert,String type,String extraValue) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.newBuilder()
                		.setAlert(alert)
                		.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.addExtra(type, extraValue).build())
                		.addPlatformNotification(WinphoneNotification.newBuilder()
                				.addExtra(type, extraValue).build())
                		.build())
                .build();
    }
    
    /**
     * @description  所有平台，目标别名是"alias",通知是alert,消息是msg,附件字段是key/value是key/extraValue
     * @date 2016年8月27日 上午11:44:25
     * @param alias
     * @param msg
     * @param alert
     * @param extraValue
     * @return
     * @return PushPayload
     */
    public static PushPayload buildPushObject_all_all_alias_msg_extra(String alias,String msg,String alert,String type,String extraValue) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(alias))
                .setMessage(Message.newBuilder().setMsgContent(msg).build())
                .setNotification(Notification.newBuilder()
                		.setAlert(alert)
                		.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.addExtra(type, extraValue).build())
                		.addPlatformNotification(WinphoneNotification.newBuilder()
                				.addExtra(type, extraValue).build())
                		.build())
                .build();
    }
    
    /**
     * @description 所有平台，目标便签是"tag",通知是alert,附件字段是key/value是key/extraValue
     * @date 2016年8月27日 上午10:59:48
     * @param tag
     * @param alert
     * @param title 
     * @param extraValue
     * @return
     * @return PushPayload
     */
    public static PushPayload buildPushObject_all_all_tag_extra(String tag,String alert,String type,String extraValue) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.tag(tag))
                .setNotification(Notification.newBuilder()
                		.setAlert(alert)
                		.addPlatformNotification(AndroidNotification.newBuilder()
                				.addExtra(type,extraValue).build())
                		.addPlatformNotification(IosNotification.newBuilder()
                				.addExtra(type, extraValue).build())
                		.addPlatformNotification(WinphoneNotification.newBuilder()
                				.addExtra(type, extraValue).build())
                		.build())
                .build();
    }
    
    public static void buildPushObject_with_extra() {

        JsonObject jsonExtra = new JsonObject();
        jsonExtra.addProperty("extra1", 1);
        jsonExtra.addProperty("extra2", false);

        Map<String, String> extras = new HashMap<String, String>();
        extras.put("extra_1", "val1");
        extras.put("extra_2", "val2");

        PushPayload payload = PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.tag("tag1"))
                .setNotification(Notification.newBuilder()
                        .setAlert("alert content")
                        .addPlatformNotification(AndroidNotification.newBuilder()
                                .setTitle("Android Title")
                                .addExtras(extras)
                                .addExtra("booleanExtra", false)
                                .addExtra("numberExtra", 1)
                                .addExtra("jsonExtra", jsonExtra)
                                .build())
                        .addPlatformNotification(IosNotification.newBuilder()
                                .incrBadge(1)
                                .addExtra("extra_key", "extra_value").build())
                        .build())
                .build();

        System.out.println(payload.toJSON());
    }
    /**
     * @description  构建推送对象：平台是 iOS，推送标签是 "tag1", "tag_all" 的并集，
     * 			             推送内容同时包括通知与消息 - 通知信息是 ALERT，角标数字为 5，通知声音为 "happy"，
     *               并且附加字段 from = "JPush"；消息内容是 MSG_CONTENT。通知是 APNs 推送通道的，消息是 JPush 应用内消息通道的。
     *               APNs 的推送环境是“生产”（如果不显式设置的话，Library 会默认指定为开发）

     * @date 2016年8月23日 下午3:05:08
     * @return
     * @return PushPayload
     */
    
    public static PushPayload buildPushObject_ios_tagAnd_alertWithExtrasAndMessage() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.ios())
                .setAudience(Audience.tag_and("tag1", "tag_all"))
                .setNotification(Notification.newBuilder()
                        .addPlatformNotification(IosNotification.newBuilder()
                                .setAlert(ALERT)
                                .setBadge(5)
                                .setSound("happy")
                                .addExtra("from", "JPush")
                                .build())
                        .build())
                 .setMessage(Message.content(MSG_CONTENT))
                 .setOptions(Options.newBuilder()
                         .setApnsProduction(true)
                         .build())
                 .build();
    }
    
    public static PushPayload buildPushObject_ios_audienceMore_messageWithExtras() {
        return PushPayload.newBuilder()
                .setPlatform(Platform.android_ios())
                .setAudience(Audience.newBuilder()
                        .addAudienceTarget(AudienceTarget.tag("tag1", "tag2"))
                        .addAudienceTarget(AudienceTarget.alias("alias1", "alias2"))
                        .build())
                .setMessage(Message.newBuilder()
                        .setMsgContent(MSG_CONTENT)
                        .addExtra("from", "JPush")
                        .build())
                .build();
    }


}

