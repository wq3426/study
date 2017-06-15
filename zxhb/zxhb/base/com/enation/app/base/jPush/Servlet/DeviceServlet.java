package com.enation.app.base.jPush.Servlet;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enation.app.base.jPush.JPushClient;
import com.enation.app.base.jPush.JPushSetting;
import com.enation.app.base.jPush.device.OnlineStatus;
import com.enation.app.base.jPush.device.TagAliasResult;

import cn.jiguang.common.resp.APIConnectionException;
import cn.jiguang.common.resp.APIRequestException;
import cn.jiguang.common.resp.DefaultResult;

public class DeviceServlet {
	protected static final Logger LOG = LoggerFactory.getLogger(DeviceServlet.class);

	public static final String REGISTRATION_ID1 = "0900e8d85ef";
	public static final String REGISTRATION_ID2 = "0a04ad7d8b4";
	
	private static JPushClient jpushClient = new JPushClient(JPushSetting.masterSecret, JPushSetting.appKey);

	public static void main(String[] args) {
//		testGetDeviceTagAlias();
//		testGetUserOnlineStatus();
		testBindMobile();
	}
	
	public static void testGetDeviceTagAlias() {
		try {
			TagAliasResult result = jpushClient.getDeviceTagAlias(REGISTRATION_ID1);
			
			LOG.info(result.alias);
			LOG.info(result.tags.toString());
			
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);
			
		} catch (APIRequestException e) {
			LOG.error("Error response from JPush server. Should review and fix it. ", e);
            LOG.info("HTTP Status: " + e.getStatus());
            LOG.info("Error Code: " + e.getErrorCode());
            LOG.info("Error Message: " + e.getErrorMessage());
		}
	}

	public static void testGetUserOnlineStatus() {
		try {
			Map<String, OnlineStatus> result =  jpushClient.getUserOnlineStatus(REGISTRATION_ID1,REGISTRATION_ID2);

			LOG.info(result.get(REGISTRATION_ID1).toString());
			LOG.info(result.get(REGISTRATION_ID2).toString());
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			LOG.error("Error response from JPush server. Should review and fix it. ", e);
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
		}
	}

	public static void testBindMobile() {
		try {
			DefaultResult result =  jpushClient.bindMobile(REGISTRATION_ID1, "15810479073");
			LOG.info("Got result " + result);
		} catch (APIConnectionException e) {
			LOG.error("Connection error. Should retry later. ", e);
		} catch (APIRequestException e) {
			LOG.error("Error response from JPush server. Should review and fix it. ", e);
			LOG.info("HTTP Status: " + e.getStatus());
			LOG.info("Error Code: " + e.getErrorCode());
			LOG.info("Error Message: " + e.getErrorMessage());
		}
	}
	
}


