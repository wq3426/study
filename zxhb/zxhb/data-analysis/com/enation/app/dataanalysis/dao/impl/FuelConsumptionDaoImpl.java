package com.enation.app.dataanalysis.dao.impl;

import java.util.List;

import com.enation.app.dataanalysis.dao.interfaces.IFuelConsumptionDao;
import com.enation.app.dataanalysis.request.vo.FuelConsumptionRankingReqVO;
import com.enation.app.dataanalysis.response.vo.RankingRespVO;
import com.enation.app.fastdfsclient.sync.JdbcDaoSupport;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.GetBean;
import com.enation.framework.util.StringUtil;


/**
 * @Description 油耗业务DAO实现类
 *
 * @createTime 2016年9月8日 下午5:42:48
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */

public class FuelConsumptionDaoImpl implements IFuelConsumptionDao {

	@SuppressWarnings("rawtypes")
	private JdbcDaoSupport daoSupport = GetBean.getBeanByClass(JdbcDaoSupport.class);
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<RankingRespVO> queryFuelConsumptionRanking(
				FuelConsumptionRankingReqVO fuelConsumptionRankingReqVO)  throws Exception{
		
		//获取请求参数
		String brand = fuelConsumptionRankingReqVO.getBrand();
		String cardriveregion = fuelConsumptionRankingReqVO.getCardriveregion();
		String dataItemType = fuelConsumptionRankingReqVO.getDataItemType();
		int gender = fuelConsumptionRankingReqVO.getGender();
		
		//图片路径转换
		String fs = EopSetting.FILE_STORE_PREFIX;
		String server = SystemSetting.getStatic_server_domain();
		
		//拼装sql
		StringBuffer sql = new StringBuffer();
		sql.append(
			" SELECT                                            "+
			" 	t1.likecount,                                   "+
			" 	t1.carplate,                                    "+
			" 	t2.brand,                                       "+
			" 	replace(t3.face,'"+fs+"','"+server+"') as face, "+
			" 	t3.nickname,                                    "+
			" 	t4."+dataItemType+" as income_consumption       "+
			" FROM                                              "+
			" 	es_carinfo t1,                                  "+
			" 	es_carmodels t2,                                "+
			" 	es_member t3,                                   "+
			" 	es_consumption_statistics t4                    "+
			" WHERE 1=1                                         "+
			" AND t1.carmodelid = t2.id                         "+
			" AND t1.carowner = t3.username                     "+
			" AND t3.rankingflag = '1'                          "+ //排名开启
			" AND t1.carplate = t4.carplate                     ");
		
		if(!StringUtil.isEmpty(brand)) {
			sql.append("AND t2.brand like '%" + brand +"%'");
		}
		if(!StringUtil.isEmpty(cardriveregion)) {
			sql.append("AND t1.cardriveregion like '%" + cardriveregion +"%'");
		}
		if(gender == 0 || gender == 1) {
			sql.append("AND t3.sex = " + gender);
		}
		sql.append(" ORDER BY t4."+dataItemType+"");
		
		List<RankingRespVO> consumptionRankingList = daoSupport.queryForList(sql.toString(),RankingRespVO.class);
		return consumptionRankingList;
	}

}
