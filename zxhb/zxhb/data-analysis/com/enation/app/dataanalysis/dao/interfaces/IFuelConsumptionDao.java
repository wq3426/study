package com.enation.app.dataanalysis.dao.interfaces;

import java.util.List;

import com.enation.app.dataanalysis.exception.DataAnalysisException;
import com.enation.app.dataanalysis.request.vo.FuelConsumptionRankingReqVO;
import com.enation.app.dataanalysis.response.vo.RankingRespVO;

/**
 * @Description 油耗业务DAO接口
 *
 * @createTime 2016年9月8日 下午5:34:02
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public interface IFuelConsumptionDao {

	/**
	 * @description 查询油耗排名
	 * @date 2016年9月8日 下午5:34:57
	 * @param fuelConsumptionRankingReqVO
	 * @return FuelConsumptionRankingRespVO
	 * @throws DataAnalysisException 
	 */
	public List<RankingRespVO> queryFuelConsumptionRanking(FuelConsumptionRankingReqVO fuelConsumptionRankingReqVO) throws Exception;


	


}
