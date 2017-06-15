package com.enation.app.dataanalysis.factory;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.enation.app.dataanalysis.dao.impl.FuelConsumptionDaoImpl;
import com.enation.app.dataanalysis.dao.impl.IncomeDaoImpl;
import com.enation.app.dataanalysis.dao.interfaces.IFuelConsumptionDao;
import com.enation.app.dataanalysis.dao.interfaces.IIncomeDao;
import com.enation.app.dataanalysis.enums.BusinessEnum;
import com.enation.app.dataanalysis.exception.DataAnalysisException;
import com.enation.app.dataanalysis.request.vo.BusinessReqVO;
import com.enation.app.dataanalysis.request.vo.FuelConsumptionRankingReqVO;
import com.enation.app.dataanalysis.request.vo.IncomeRankingReqVO;


/**
 * @Description 业务Bean工厂
 *
 * @createTime 2016年9月8日 下午5:05:03
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
@Component
public class BusinessBeanFactory {

	/**
	 * @description 根据业务枚举创建对应的处理类
	 * @date 2016年9月8日 下午5:39:24
	 * @param businessEnum	业务枚举
	 * @return Object
	 */
	public static Object getInstance(BusinessEnum businessEnum){	
		switch (businessEnum) {
			case 油耗排名:
				IFuelConsumptionDao FuelConsumptionRanking = new FuelConsumptionDaoImpl();
				return FuelConsumptionRanking;
			case 收益排名:
				IIncomeDao incomeRanking = new IncomeDaoImpl();
				return incomeRanking;
			default:
				return null;
		}	
	}
	
	/**
	 * @description 根据枚举将JSON数据分装成对应的请求VO
	 * @date 2016年9月9日 上午11:31:09
	 * @param businessEnum	业务枚举
	 * @param reqContent	请求数据（JSON）
	 * @return BusinessReqVO
	 */
	public static BusinessReqVO getReqVO(BusinessEnum businessEnum, String reqContent) throws DataAnalysisException{	
		BusinessReqVO businessReqVO = new BusinessReqVO();
		switch (businessEnum) {
			case 油耗排名:
				try {
					FuelConsumptionRankingReqVO fuelConsumptionRankingReqVO = JSON.parseObject(reqContent, FuelConsumptionRankingReqVO.class);             
					businessReqVO.setFuelConsumptionrankingReqVO(fuelConsumptionRankingReqVO);
					return businessReqVO;
				} catch (Exception e) {
					e.getStackTrace();
					throw new DataAnalysisException("请求参数错误");
				}
			case 收益排名:
				try {
					IncomeRankingReqVO incomeRankingReqVO = JSON.parseObject(reqContent, IncomeRankingReqVO.class);             
					businessReqVO.setIncomeRankingReqVO(incomeRankingReqVO);
					return businessReqVO;
				} catch (Exception e) {
					e.getStackTrace();
					throw new DataAnalysisException("请求参数错误");
				}
			default:
				return null;
		}	
	}
	
	
}
