package com.enation.app.dataanalysis.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.dataanalysis.enums.BusinessEnum;
import com.enation.app.dataanalysis.exception.DataAnalysisException;
import com.enation.app.dataanalysis.factory.BusinessBeanFactory;
import com.enation.app.dataanalysis.request.vo.BusinessReqVO;
import com.enation.app.dataanalysis.service.DataAnalysisService;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

/**
 * @Description 数据分析模块 公共action
 *
 * @createTime 2016年9月9日 上午11:39:03
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */

@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/data/analysis")
@Action("dataAnalysisAction")
public class DataAnalysisAction extends WWAction {
	
	private static final long serialVersionUID = 1L;
	
	@Autowired
	private DataAnalysisService dataAnalysisService;
	
	
	
	/**
	 * @description 数据分析
	 * @date 2016年9月9日 上午11:47:40
	 * @return String
	 */
	public String analyse() {
		
		//获取请求参数
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String businessCode = request.getParameter("businessCode");
		String param = request.getParameter("param");
		
		//数据校验
		if(StringUtil.isEmpty(businessCode)) {
			this.showErrorJson("业务编码不能为空");
			return JSON_MESSAGE;
		}
		if(StringUtil.isEmpty(param)) {
			this.showErrorJson("请求参数不能为空");
			return JSON_MESSAGE;
		}
		
		try {
			//获取枚举类型
			BusinessEnum businessEnum = BusinessEnum.getEnumByBusinessCode(businessCode);
			logger.info("----------处理数据分析【"+businessEnum+"】开始--------------------");
			
			//封装请求VO
			BusinessReqVO businessReqVO = BusinessBeanFactory.getReqVO(businessEnum, param);
		
			//调用业务处理方法
			String respContent = dataAnalysisService.invokeBusinessMethod(businessEnum, businessReqVO);
			
			logger.info("----------处理数据分析【"+businessEnum+"】结束--------------------");
			this.showSuccessJson("请求成功", respContent);
		} catch (DataAnalysisException e) {
			e.getStackTrace();
			this.showErrorJson(e.getMessage());
			
		} catch (Exception e) {
			e.getStackTrace();
			this.showErrorJson("系统异常");
		}
		
		return JSON_MESSAGE;
	}


}
	
	
	
	
	
	
