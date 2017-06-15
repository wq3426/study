package com.enation.app.shop.component.bonus.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.component.bonus.model.BonusType;
import com.enation.app.shop.component.bonus.service.IBonusTypeManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;


/**
 * 红包类型管理
 * @author kingapex
 *2013-8-13下午3:10:21
 */
@Component
public class BonusTypeManager extends BaseSupport  implements IBonusTypeManager {

	@Override
	public void add(BonusType bronusType) { 
		this.baseDaoSupport.insert("bonus_type", bronusType);

	}

	@Override
	public void update(BonusType bronusType) {
		this.baseDaoSupport.update("bonus_type", bronusType," type_id="+bronusType.getType_id());
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void delete(Integer[] bonusTypeId) {
		
		for(int typeid:bonusTypeId){
			this.baseDaoSupport.execute("delete from member_bonus where bonus_type_id=?", typeid);
			this.baseDaoSupport.execute("delete from bonus_type where type_id=?",typeid);
		}
	}

	@Override
	public Page list(int page, int pageSize) {
		String sql ="select * from bonus_type order by type_id desc";
		return this.baseDaoSupport.queryForPage(sql, page, pageSize, BonusType.class);
	}

	@Override
	public BonusType get(int typeid) {
		String sql ="select * from bonus_type  where type_id =?";
		return (BonusType) this.baseDaoSupport.queryForObject(sql, BonusType.class, typeid);
	}

}
