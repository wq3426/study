package com.enation.app.b2b2c.core.service.goods;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.HotGoods;
import com.enation.eop.sdk.database.BaseSupport;

/**
 * 实现类
 * @author Sylow
 * @version v1.0, 2016年2月28日
 * @since v5.2
 */
@Component
public class HotGoodsManager extends BaseSupport<HotGoods> implements IHotGoodsManager {

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IHotGoodsManager#add(com.enation.app.shop.core.model.HotGoods)
	 */
	@Override
	public void add(HotGoods hotGoods) {
		
		if (hotGoods != null) {
			this.daoSupport.insert("es_hot_goods", hotGoods);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IHotGoodsManager#delByStoreId(int)
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delByStoreId(int storeId,int id,int site) {
		String sql0="SELECT * FROM es_hot_goods WHERE store_id = ? and site > ? ORDER BY site";
		List<HotGoods> hotGoodsList=this.daoSupport.queryForList(sql0, HotGoods.class, storeId,site);
		//查询该热门商品的商品编号
		String sql1 = "SELECT ehg.goods_sn FROM es_hot_goods ehg WHERE store_id = "+storeId+" and id = "+id;
		String sn = this.daoSupport.queryForString(sql1);
		//先删除里面的数据 再修改对应的位置
		String sql2 = "DELETE FROM es_hot_goods WHERE store_id = ? and id = ?";
		this.daoSupport.execute(sql2, storeId,id);
		String sql3 = "update es_goods set hot_goods = 0 where store_id = ? and sn = ?";
		this.daoSupport.execute(sql3, storeId,sn);
		if(hotGoodsList.size() > 0){
			for(int i=0;i<hotGoodsList.size();i++){
				net.sf.json.JSONObject hotGoodsInfo = net.sf.json.JSONObject.fromObject(hotGoodsList.get(i));
				int site1=hotGoodsInfo.getInt("site");
				String sql = "update  es_hot_goods set site = ? where id = ?";
				this.baseDaoSupport.execute(sql,site1-1,hotGoodsInfo.getInt("id"));
			}
		}
	}
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateSite(int storeId, int id, int site, String flag) {
		if(flag != null){
			if(flag.equals("up")){
				String sql0="SELECT * FROM es_hot_goods WHERE store_id = ? and site < ? ORDER BY site desc";
				List<HotGoods> hotGoodsList=this.daoSupport.queryForList(sql0, HotGoods.class, storeId,site);
				
				String sql1 = "update  es_hot_goods set site = ? where id = ? and store_id = ?";
				this.daoSupport.execute(sql1,site-1,id,storeId);
				if(hotGoodsList.size() > 0){
					net.sf.json.JSONObject hotGoodsInfo = net.sf.json.JSONObject.fromObject(hotGoodsList.get(0));
					int site1=hotGoodsInfo.getInt("site");
					String sql = "update  es_hot_goods set site = ? where id = ?";
					this.baseDaoSupport.execute(sql,site1+1,hotGoodsInfo.getInt("id"));
				}
			}else if(flag.equals("down")){
				String sql0="SELECT * FROM es_hot_goods WHERE store_id = ? and site > ? ORDER BY site";
				List<HotGoods> hotGoodsList=this.daoSupport.queryForList(sql0, HotGoods.class, storeId,site);
				
				String sql1 = "update  es_hot_goods set site = ? where id = ? and store_id = ?";
				this.daoSupport.execute(sql1,site+1,id,storeId);
				if(hotGoodsList.size() > 0){
					net.sf.json.JSONObject hotGoodsInfo = net.sf.json.JSONObject.fromObject(hotGoodsList.get(0));
					int site1=hotGoodsInfo.getInt("site");
					String sql = "update  es_hot_goods set site = ? where id = ?";
					this.baseDaoSupport.execute(sql,site1-1,hotGoodsInfo.getInt("id"));
				}
			}else if(flag.equals("top")){
				String sql0="SELECT * FROM es_hot_goods WHERE store_id = ? and site < ? ORDER BY site";
				List<HotGoods> hotGoodsList=this.daoSupport.queryForList(sql0, HotGoods.class, storeId,site);
				//先修改数据 再第一个位置
				String sql1 = "update  es_hot_goods set site = 1 where id = ? and store_id = ?";
				this.daoSupport.execute(sql1,id,storeId);
				if(hotGoodsList.size() > 0){
					for(int i=0;i<hotGoodsList.size();i++){
						net.sf.json.JSONObject hotGoodsInfo = net.sf.json.JSONObject.fromObject(hotGoodsList.get(i));
						int site1=hotGoodsInfo.getInt("site");
						String sql = "update  es_hot_goods set site = ? where id = ?";
						this.baseDaoSupport.execute(sql,site1+1,hotGoodsInfo.getInt("id"));
					}
				}
			}
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IHotGoodsManager#list(int)
	 */
	@Override
	public List<HotGoods> list(int storeId) {
		String sql = "select ehg.*,eg.hot_goods from es_hot_goods ehg left join es_goods eg "
					+"on ehg.goods_sn=eg.sn where eg.hot_goods =1 and ehg.store_id = ?  order by ehg.site";
		
		return this.daoSupport.queryForList(sql, HotGoods.class, storeId);
	}

}
