package com.enation.app.dataanalysis.dao.interfaces;

import java.util.List;

import com.enation.app.dataanalysis.request.vo.IncomeRankingReqVO;
import com.enation.app.dataanalysis.response.vo.RankingRespVO;

/**
 * @Description 收益排名DAO--接口
 *
 * @createTime 2016年9月12日 下午5:05:32
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public interface IIncomeDao {

	/**
	 * @description 查询收益排名
	 * @date 2016年9月12日 下午5:06:15
	 * @param fuelConsumptionRankingReqVO
	 * @return
	 * @throws Exception
	 */
	public List<RankingRespVO> queryIncomeRanking(IncomeRankingReqVO incomeRankingReqVO) throws Exception;
}
