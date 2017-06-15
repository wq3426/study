package com.dhl.tools.auditing;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.dhl.tools.domain.AuthorityInfo;
import com.dhl.tools.domain.CargoLocation;
import com.dhl.tools.domain.CargoLocationType;
import com.dhl.tools.domain.Material;
import com.dhl.tools.domain.RoleInfo;
import com.dhl.tools.domain.UserInfo;
import com.dhl.tools.domain.WareHouse;
import com.dhl.tools.security.MyUserDetails;

/**
 * 切入添加方法，设置添加人和添加时间 Created by liuso on 2017/4/16.
 */
@Slf4j
@Aspect
@Service
public class AuditingInsertService {

	@Pointcut("execution(public * com.dhl.tools.service.*.add(..))")
	public void auditingAdd() {

	}

	@Before("auditingAdd()")
	public void doBefore(JoinPoint joinPoint) {
		// 获取当前登录用户
		SecurityContext context = SecurityContextHolder.getContext();
		Object principal = context.getAuthentication().getPrincipal();
		MyUserDetails userDetails = null;
		if (null != principal) {
			userDetails = (MyUserDetails) principal;
		}

		// 获取参数，默认取第一个
		Object[] objects = joinPoint.getArgs();
		Object param = null;
		if (null != objects) {
			param = objects[0];
		}

		if (null != userDetails && null != param) {

			if (param instanceof AuthorityInfo) {
				AuthorityInfo item = (AuthorityInfo) param;
				// 设置创建人
				item.setCreateBy(userDetails.getUsername());
				// 设置创建时间
				item.setCreateDate(new Date());
			} else if (param instanceof CargoLocation) {
				CargoLocation item = (CargoLocation) param;

				// 设置创建人
				item.setCreateBy(userDetails.getUsername());
				// 设置创建时间
				item.setCreateDate(new Date());
			} else if (param instanceof CargoLocationType) {
				CargoLocationType item = (CargoLocationType) param;

				// 设置创建人
				item.setCreateBy(userDetails.getUsername());
				// 设置创建时间
				item.setCreateDate(new Date());
			} else if (param instanceof Material) {
				Material item = (Material) param;

				// 设置创建人
				item.setCreateBy(userDetails.getUsername());
				// 设置创建时间
				item.setCreateDate(new Date());
			} else if (param instanceof RoleInfo) {
				RoleInfo item = (RoleInfo) param;

				// 设置创建人
				item.setCreateBy(userDetails.getUsername());
				// 设置创建时间
				item.setCreateDate(new Date());
			} else if (param instanceof UserInfo) {
				UserInfo item = (UserInfo) param;

				// 设置创建人
				item.setCreateBy(userDetails.getUsername());
				// 设置创建时间
				item.setCreateDate(new Date());
			} else if (param instanceof WareHouse) {
				WareHouse item = (WareHouse) param;

				// 设置创建人
				item.setCreateBy(userDetails.getUsername());
				// 设置创建时间
				item.setCreateDate(new Date());
			}
		}
	}

	@AfterReturning(pointcut = "auditingAdd()", returning = "ret")
	public void doAfter(Object ret) {
		log.debug("返回信息：{}", ret);
	}
}
