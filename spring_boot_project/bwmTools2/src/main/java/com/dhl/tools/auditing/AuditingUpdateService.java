package com.dhl.tools.auditing;

import com.dhl.tools.domain.CargoLocation;
import com.dhl.tools.domain.CargoLocationType;
import com.dhl.tools.domain.Material;
import com.dhl.tools.domain.RoleInfo;
import com.dhl.tools.domain.UserInfo;
import com.dhl.tools.domain.WareHouse;
import com.dhl.tools.security.MyUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 切入更新方法，设置修改人和修改时间 Created by liuso on 2017/4/16.
 */
@Slf4j
@Aspect
@Service
public class AuditingUpdateService {

	@Pointcut("execution(public * com.dhl.tools.service.*.update(..))")
	public void auditingUpdate() {

	}

	@Before("auditingUpdate()")
	public void doBefore(JoinPoint joinPoint) {
		// 获取参数，默认取第一个
		Object[] objects = joinPoint.getArgs();
		if (null != objects && null != objects[0]) {
			// 获取当前登录用户
			SecurityContext context = SecurityContextHolder.getContext();
			Object principal = context.getAuthentication().getPrincipal();
			if (null != principal) {

				MyUserDetails userDetails = (MyUserDetails) principal;

				Object obj = objects[0];

				// 设置修改人和修改时间
				if (obj instanceof WareHouse) {
					WareHouse wareHouse = (WareHouse) obj;
					wareHouse.setLastModifiedBy(userDetails.getUsername());
					wareHouse.setLastModifiedDate(new Date());
				} else if (obj instanceof Material) {
					Material material = (Material) obj;
					material.setLastModifiedBy(userDetails.getUsername());
					material.setLastModifiedDate(new Date());
				} else if (obj instanceof CargoLocationType) {
					CargoLocationType cargoLocationType = (CargoLocationType) obj;
					cargoLocationType.setLastModifiedBy(userDetails.getUsername());
					cargoLocationType.setLastModifiedDate(new Date());
				} else if (obj instanceof CargoLocation) {
					CargoLocation cargoLocation = (CargoLocation) obj;
					cargoLocation.setLastModifiedBy(userDetails.getUsername());
					cargoLocation.setLastModifiedDate(new Date());
				} else if (obj instanceof UserInfo) {
					UserInfo userInfo = (UserInfo) obj;
					userInfo.setLastModifiedBy(userDetails.getUsername());
					userInfo.setLastModifiedDate(new Date());
				} else if (obj instanceof RoleInfo) {
					RoleInfo roleInfo = (RoleInfo) obj;
					roleInfo.setLastModifiedBy(userDetails.getUsername());
					roleInfo.setLastModifiedDate(new Date());
				}
			}
		}
	}

	@Around("auditingUpdate()")
	public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
		log.debug("Around is running");
		return joinPoint.proceed();
	}

	@AfterReturning(pointcut = "auditingUpdate()", returning = "ret")
	public void doAfter(Object ret) {
		log.debug("返回信息：{}", ret);
	}
}
