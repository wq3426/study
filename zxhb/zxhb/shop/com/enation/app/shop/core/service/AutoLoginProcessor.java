package com.enation.app.shop.core.service;

import java.net.URLDecoder;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.eop.IEopProcessor;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.EncryptionUtil1;
import com.enation.framework.util.HttpUtil;

public class AutoLoginProcessor implements IEopProcessor {

	private IMemberManager memberManager;


	public boolean process() {
		try {
			HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
			String url = httpRequest.getRequestURI();
			if (url != null) {
				url = url.toLowerCase();
				if (url.endsWith("/") || url.endsWith(".html")
						|| url.endsWith(".do")) {
					Member member = UserConext.getCurrentMember();
					if (member == null) {
						String cookieValue = HttpUtil.getCookieValue(
								ThreadContextHolder.getHttpRequest(), "JavaShopUser");
						if (cookieValue != null && !cookieValue.equals("")) {
							cookieValue = URLDecoder.decode(cookieValue,
									"UTF-8");
							cookieValue = EncryptionUtil1.authcode(cookieValue,
									"DECODE", "", 0);
							if (cookieValue != null && !cookieValue.equals("")) {

								Map map = (Map) JSONObject.toBean(
										JSONObject.fromObject(cookieValue),
										Map.class);
								if (map != null) {
									String username = map.get("username")
											.toString();
									String password = map.get("password")
											.toString();
									int result = memberManager.loginWithCookie(username,
											password);
									if(result != 1){
										HttpUtil.addCookie(ThreadContextHolder.getHttpResponse(), "JavaShopUser","", 0);
									}
								}

							}
						}
					}
				}
			}
		} catch (Exception ex) {
		}
		return true;
	}

	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}

	public static void main(String[] args) {
		// String str =
		// "{username:\"hhyear@163.com\",password:\"256c6da2403dcda7aae0df34337359e8\"}";
		// String str1 = EncryptionUtil.authcode(str,"ENCODE","",0);
		// //System.out.println(str1);
		String str1 = "db21SPFxwCWgshcLqsIxFzS9YeEusB/qzRdC1OKk2R47uLdLCuai1BPUMh5xNJhSkwuu1v09po2qNuLPsWjLg/+p4aaeZZ70LyCEGwcwMZGuHCY9zmKDv1sXBZKQ6OXjFV04MQ";
		//System.out.println(EncryptionUtil1.authcode(str1, "DECODE", "", 0));
	}

}
