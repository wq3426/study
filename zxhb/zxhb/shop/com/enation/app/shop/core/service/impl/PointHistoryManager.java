package com.enation.app.shop.core.service.impl;

import java.util.List;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.PointHistory;
import com.enation.app.shop.core.service.IPointHistoryManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;

/**
 * 会员积分日志
 * 
 * @author lzf<br/>
 *         2010-3-22 上午11:27:23<br/>
 *         version 1.0<br/>
 */
public class PointHistoryManager extends BaseSupport implements
		IPointHistoryManager {

	
	public Page pagePointHistory(int pageNo, int pageSize,int pointType) {
		Member member = UserConext.getCurrentMember();

		String sql = "select * from point_history where member_id = ? and point_type=? order by time desc";

		Page webpage = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize,
				member.getMember_id(),pointType);
		return webpage;
	}

	public Page pagePointHistory(int pageNo, int pageSize) {
		Member member = UserConext.getCurrentMember();

		String sql = "select * from point_history where member_id = ? order by time desc";

		Page webpage = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize,
				member.getMember_id());
		return webpage;
	}
	
	
	public Long getConsumePoint(int pointType) {
		Member member = UserConext.getCurrentMember();
		Long result = this.baseDaoSupport
				.queryForLong(
						"select SUM(point) from point_history where  member_id = ?  and  type=0  and point_type=?",
						member.getMember_id(),pointType);
		return result;
	}

	
	public Long getGainedPoint(int pointType) {
		Member member = UserConext.getCurrentMember();
		Long result = this.baseDaoSupport.queryForLong(
						"select SUM(point) from point_history where    member_id = ? and  type=1  and point_type=?",
						member.getMember_id(),pointType);
		return result;
	}

	
	public void addPointHistory(PointHistory pointHistory) {
		this.baseDaoSupport.insert("point_history", pointHistory);
	}

	
	public List<PointHistory> listPointHistory(int member_id,int pointtype) {
		String sql = "select * from point_history where member_id = ? and point_type = ? order by time desc";
		List list = this.baseDaoSupport.queryForList(sql,PointHistory.class, member_id, pointtype);
		return list;
	}
	
	/**
	 * 冻结积分列表(当前会员）
	 * 
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	public Page pagePointFreeze(int pageNo, int pageSize){
		Member member = UserConext.getCurrentMember();
		String sql = "select * from freeze_point where memberid = ? order by id desc";
		Page webpage = this.baseDaoSupport.queryForPage(sql, pageNo, pageSize,
				member.getMember_id());
		return webpage;
	}

}
