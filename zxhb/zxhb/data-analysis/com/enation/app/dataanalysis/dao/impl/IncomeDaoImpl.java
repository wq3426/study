package com.enation.app.dataanalysis.dao.impl;

import java.util.List;

import com.enation.app.dataanalysis.dao.interfaces.IIncomeDao;
import com.enation.app.dataanalysis.request.vo.IncomeRankingReqVO;
import com.enation.app.dataanalysis.response.vo.RankingRespVO;
import com.enation.app.fastdfsclient.sync.JdbcDaoSupport;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.DateUtils;
import com.enation.eop.sdk.utils.GetBean;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;


/**
 * @Description 油耗业务DAO实现类
 *
 * @createTime 2016年9月8日 下午5:42:48
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */

public class IncomeDaoImpl implements IIncomeDao {

	@SuppressWarnings("rawtypes")
	private JdbcDaoSupport daoSupport = GetBean.getBeanByClass(JdbcDaoSupport.class);

	@SuppressWarnings("unchecked")
	@Override
	public List<RankingRespVO> queryIncomeRanking(IncomeRankingReqVO incomeRankingReqVO) throws Exception {
	
		//获取请求参数
		String brand = incomeRankingReqVO.getBrand();
		String cardriveregion = incomeRankingReqVO.getCardriveregion();
		int gender = incomeRankingReqVO.getGender();
		String starttime = incomeRankingReqVO.getStarttime();
		String endtime = incomeRankingReqVO.getEndtime();
		
		//处理时间
		long _starttime = 0L;
		long _endtime = 0L; 
		if(StringUtil.isEmpty(starttime)) {
			String newtime = DateUtils.getNowDate()+ " 00:00:00";
			_starttime = DateUtil.getDateline(newtime) * 1000;
		} else {
			_starttime = DateUtil.getDateline(starttime) * 1000;
		}
		if(StringUtil.isEmpty(endtime)) {
			String newtime = DateUtils.getNowDate()+ " 23:59:59";
			_endtime = DateUtil.getDateline(newtime) * 1000;
		} else {
			endtime = endtime + " 23:59:59";
			_endtime = DateUtil.getDateline(endtime, "yyyy-MM-dd HH:mm:ss") * 1000;
		}
		
		//图片路径转换
		String fs = EopSetting.FILE_STORE_PREFIX;
		String server = SystemSetting.getStatic_server_domain();
				
		//拼装SQL
		StringBuffer sql = new StringBuffer();
		sql.append(
			" SELECT		                                                                                                               "+				
			" 	t1.likecount,                                                                                                              "+      
			" 	t1.carplate,                                                                                                               "+      
			" 	t2.brand,                                                                                                                  "+      
			" 	replace(t3.face, '"+fs+"','"+server+"') as face,                                                                           "+      
			" 	t3.nickname,                                                                                                               "+      
			" 	IFNULL((SUM(IFNULL(t4.insuregain,0)) + SUM(IFNULL(t4.repairgain,0))),0) AS income_consumption                              "+      
			" FROM                                                                                                                         "+      
			" 	es_carmodels t2,                                                                                                           "+      
			" 	es_member t3,                                                                                                              "+      
			" 	es_carinfo t1 LEFT JOIN (SELECT * FROM es_hodometer hm WHERE hm.starttime >= "+_starttime+" AND hm.starttime <= "+_endtime+") t4 "+      
			" 		ON t1.carplate = t4.carplate                                                                                           "+      
			" WHERE                                                                                                                        "+      
			" 	1 = 1                                                                                                                      "+      
			" AND t1.carmodelid = t2.id                                                                                                    "+      
			" AND t3.rankingflag = '1'                                                                                                     "+  //排名开启    
			" AND t1.carowner = t3.username                                                                                                ");     
		                                                                                                                                        
		if(!StringUtil.isEmpty(brand)) {                                                                                                        
			sql.append(" AND t2.brand like '%" + brand +"%'");                                                                                   
		}                                                                                                                                        
		                                                                                                                                         
		if(!StringUtil.isEmpty(cardriveregion)) {                                                                                                
			sql.append(" AND t1.cardriveregion like '%" + cardriveregion +"%'");
		}
		
		if(gender == 0 || gender == 1) {
			sql.append(" AND t3.sex = " + gender);
		}
		
		sql.append(
				" GROUP BY t1.likecount,t1.carplate,t2.brand, t3.face,t3.nickname" +
				" ORDER BY income_consumption DESC");
		
		List<RankingRespVO> incomeRankingList = daoSupport.queryForList(sql.toString(),RankingRespVO.class);
		return incomeRankingList;
	}
	
	
	

}
