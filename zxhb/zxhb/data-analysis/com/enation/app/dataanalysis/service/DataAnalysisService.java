package com.enation.app.dataanalysis.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.enation.app.dataanalysis.dao.interfaces.IFuelConsumptionDao;
import com.enation.app.dataanalysis.dao.interfaces.IIncomeDao;
import com.enation.app.dataanalysis.enums.BusinessEnum;
import com.enation.app.dataanalysis.exception.DataAnalysisException;
import com.enation.app.dataanalysis.factory.BusinessBeanFactory;
import com.enation.app.dataanalysis.request.vo.BusinessReqVO;
import com.enation.app.dataanalysis.request.vo.FuelConsumptionRankingReqVO;
import com.enation.app.dataanalysis.request.vo.IncomeRankingReqVO;
import com.enation.app.dataanalysis.response.vo.RankingRespVO;
import com.enation.framework.util.JsonUtil;

/**
 * @Description 数据分析模块service
 *
 * @createTime 2016年9月8日 下午5:49:45
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
@Service
public class DataAnalysisService {
	
	@Autowired
	private UpdateConsumptionDataService updateConsumptionDataService;
	/**
	 * @description 业务执行方法
	 * @date 2016年9月8日 下午7:23:46
	 * @param businessEnum	业务枚举
	 * @param BusinessReqVO	请求VO
	 * @return String 
	 * @throws Exception
	 */
	public String invokeBusinessMethod(BusinessEnum businessEnum, BusinessReqVO businessReqVO) throws Exception {
		
		//接口响应数据
		String respContent ="";
		
		//根据枚举获取对应业务DAO的实现类
		Object daoInstance = BusinessBeanFactory.getInstance(businessEnum);
		
		switch (businessEnum) {
		case 油耗排名:	
			//updateConsumptionDataService.handleAllCarConsumptionData();// 更新数据(定时已处理)
			IFuelConsumptionDao fuelConsumptionDao = (IFuelConsumptionDao)daoInstance;
			respContent = handleFuelConsumptionRanking(fuelConsumptionDao, businessReqVO);//调用处理方法
			break;
		case 收益排名:			
			IIncomeDao incomeDao = (IIncomeDao)daoInstance;
			respContent = handleIncomeRanking(incomeDao, businessReqVO);//调用处理方法
			break;
		default:
			throw new DataAnalysisException("业务编码错误");
		}
		
		return respContent;
	}
	
	/**
	 * @description 处理油耗排名
	 * @date 2016年9月10日 上午10:22:20
	 * @param fuelConsumptionRanking	DAO
	 * @param businessReqVO
	 * @return String
	 * @throws DataAnalysisException 
	 */
	protected String handleFuelConsumptionRanking(IFuelConsumptionDao fuelConsumptionDao, 
			BusinessReqVO businessReqVO) throws Exception {
		
		//转换请求VO
		FuelConsumptionRankingReqVO fuelConsumptionRankingReqVO = businessReqVO.getFuelConsumptionrankingReqVO();
		
		//业务处理
		List<RankingRespVO> consumptionRankingList = fuelConsumptionDao.queryFuelConsumptionRanking(fuelConsumptionRankingReqVO);
		
		//返回结果（json）
		return JsonUtil.ListToJson(consumptionRankingList);
	} 
	
	/**
	 * @description 处理收益排名
	 * @date 2016年9月12日 下午5:12:38
	 * @param incomeDao
	 * @param businessReqVO
	 * @return	String
	 * @throws Exception
	 */
	protected String handleIncomeRanking(IIncomeDao incomeDao, 
			BusinessReqVO businessReqVO) throws Exception {
		
		//转换请求VO
		IncomeRankingReqVO incomeRankingReqVO = businessReqVO.getIncomeRankingReqVO();
		
		//业务处理
		List<RankingRespVO> incomeRankingList = incomeDao.queryIncomeRanking(incomeRankingReqVO);
		
		//返回结果（json）
		return JsonUtil.ListToJson(incomeRankingList);
	} 
	
}
