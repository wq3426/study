package com.dhl.tools.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 视图配置 Created by liuso on 2017/4/11.
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {

		// 设置主界面
		registry.addViewController("/home").setViewName("home");

		// 设置登录界面
		registry.addViewController("/login").setViewName("login");

		// 设置仓库界面
		registry.addViewController("/ware-house-manage").setViewName("database/warehouseManage");

		// 设置货位类型界面
		registry.addViewController("/cargoLocationType-manage").setViewName("database/cargoLocationTypeManage");

		// 设置物料管理界面
		registry.addViewController("/material-manage").setViewName("database/materialManage");

		// 设置用户管理界面
		registry.addViewController("/user-manage").setViewName("right/userManage");

		// 设置角色管理界面
		registry.addViewController("/role-manage").setViewName("right/roleManage");

		// 设置货位管理界面
		registry.addViewController("/cargoLocation-manage").setViewName("database/cargoLocationManage");

		// 设置仓库配置界面
		registry.addViewController("/ware-house-config-manage").setViewName("database/warehouseConfig");

		// 设置货位推荐配置界面
		registry.addViewController("/cargoLocation-recommend").setViewName("database/cargoLocationRecommend");
	}
}
