package com.dhl.tools.controller;

import com.dhl.tools.security.MyUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Created by liuso on 2017/4/10.
 */
@Controller
public class CommonController {

	@GetMapping(value = { "/" })
	public RedirectView home(@AuthenticationPrincipal MyUserDetails currentUser) {
		String url = "/login";
		if (null != currentUser) {
			// 当访问/home时，转发到的URL
			url = "/home";
		}
		return new RedirectView(url);
	}
}
