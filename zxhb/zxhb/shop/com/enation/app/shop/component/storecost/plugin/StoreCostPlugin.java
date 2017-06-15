package com.enation.app.shop.component.storecost.plugin;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.SaleTypeSetting;
import com.enation.app.base.core.model.SaleFree;
import com.enation.app.base.core.model.StoreCost;
import com.enation.app.base.core.plugin.job.IEveryMinutesExecuteEvent;
import com.enation.app.base.core.plugin.job.IEveryMonthExecuteEvent;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;

/**
 * 
 *自动任务每个月修改免费发放的营销类型信息 
 * @author yunbs
 *
 */
@Component
@Scope("prototype")
public class StoreCostPlugin extends AutoRegisterPlugin  implements IEveryMonthExecuteEvent{
	private IDaoSupport daoSupport;
	
	
	@Override
	public void everyMonth() {
		String sqlSale = "select * from es_sale_free where isFree=0 AND isInitia = 0 AND type_id in("+SaleTypeSetting.ADV_PUBLISH_NUM_TYPE+","+
				SaleTypeSetting.APP_MESPUBLISH_NUM_TYPE+","+SaleTypeSetting.BONUS_NUM_TYPE+")";
		List<SaleFree> saleFreeList = this.daoSupport.queryForList(sqlSale,SaleFree.class);
		if(!saleFreeList.isEmpty()){
			for(SaleFree saleFree : saleFreeList){
				Long[] currentMonth = DateUtil.getCurrentLastMonth(2);
				String sql1 = "UPDATE es_store_cost SET valid_start_date = "+currentMonth[0]*1000+",valid_end_date = "+currentMonth[1]*1000+
						",used_num = 0, surp_num = "+saleFree.getUse_num()+" WHERE type_id = "+saleFree.getType_id()+" AND level_id = "+saleFree.getLevel_id()+
						" AND isFree = 0 ";
				this.daoSupport.execute(sql1);
			}
		}
			
	}


	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	
}
