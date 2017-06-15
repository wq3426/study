package com.enation.app.shop.core.service.impl;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.HotGoods;
import com.enation.app.shop.core.service.IProductStoreManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.database.IntegerMapper;

/**
 * 产品库存管理
 * @author kingapex
 *2014-1-1下午4:29:41
 */
public class ProductStoreManager implements IProductStoreManager {
	private IDaoSupport daoSupport;
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void decreaseEnable(int goodsid, int productid, int depotid,int num) {
		
		
		/**
		 * 多店系统不维护分仓库存
		 */
		/*if(!"b2b2c".equals(EopSetting.PRODUCT)){
			this.daoSupport.execute("update es_product_store set enable_store=enable_store-? where depotid=? and productid=?", num,depotid,productid);
		}*/
		this.daoSupport.execute("update es_product_store set enable_store=enable_store-? where depotid=? and productid=?", num,depotid,productid);
		
		this.daoSupport.execute("update es_product set enable_store=enable_store-? where product_id=?", num,productid);
		this.daoSupport.execute("update es_goods set enable_store=enable_store-? where goods_id=?", num,goodsid);

		int enable_store = this.daoSupport.queryForInt("select enable_store from es_goods where goods_id=?", goodsid);
		/*if(enable_store==0){//商品下架，热门商品自动下架
			this.daoSupport.execute("update es_goods set market_enable=? where goods_id=?", 0,goodsid);
			
			String sql1="SELECT ehg.* FROM es_hot_goods ehg,es_goods eg WHERE ehg.goods_sn=eg.sn AND ehg.store_id = eg.store_id AND eg.goods_id = ?"; 
			List<HotGoods> goodsList=this.daoSupport.queryForList(sql1,HotGoods.class,goodsid);
			if(!goodsList.isEmpty() || goodsList.size() > 0){
				int storeId = 0;
				for(HotGoods hotGoods : goodsList){
					int id =hotGoods.getId();
					storeId=hotGoods.getStore_id();
					String sql2="delete from es_hot_goods where id = ?";
					this.daoSupport.execute(sql2, id);
				}
				String sql3 = "SELECT * FROM es_hot_goods WHERE store_id = ? ORDER BY site";
				List<HotGoods> hotGoodsList = this.daoSupport.queryForList(sql3,storeId);
				for(int i=0;i<hotGoodsList.size();i++){
					net.sf.json.JSONObject hotGoodsInfo = net.sf.json.JSONObject.fromObject(hotGoodsList.get(i));
					String sql4 = "update  es_hot_goods set site = ? where id = ?";
					this.daoSupport.execute(sql4,i+1,hotGoodsInfo.getInt("id"));
				}
			}
		}*/
		
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void increaseEnable(int goodsid, int productid, int depotid,int num) {
		try{
			/**
			 * 多店系统不维护分仓库存
			 */
			if("b2b2c".equals(EopSetting.PRODUCT)){
				if(this.checkExists(goodsid, depotid)){
					this.daoSupport.execute("update es_product_store set enable_store =enable_store+? where goodsid=? and depotid=?", num,goodsid,depotid);
				}else{
					this.daoSupport.execute("insert into es_product_store(goodsid,productid,depotid,enable_store)values(?,?,?,?)",goodsid,productid, depotid,num);
		
				}
			}
			
			this.daoSupport.execute("update es_product set enable_store=enable_store+? where product_id=?", num,productid);
			this.daoSupport.execute("update es_goods set enable_store=enable_store+? where goods_id=?", num,goodsid);
		}catch(Exception e){
			throw e;
		}
		/*this.daoSupport.execute("update es_goods set market_enable=? where goods_id=?", 1,goodsid);//自动上架
*/		
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void decreaseStroe(int goodsid, int productid, int depotid,int num) {
		
		/**
		 * 多店铺不负责分仓库存
		 */
		if(!"b2b2c".equals(EopSetting.PRODUCT)){
			this.daoSupport.execute("update es_product_store set store=store-? where depotid=? and productid=?", num,depotid,productid);
		}
		
		this.daoSupport.execute("update es_product set store=store-? where product_id=?", num,productid);
		this.daoSupport.execute("update es_goods set store=store-? where goods_id=?", num,goodsid);
		
		
		
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void increaseStroe(int goodsid, int productid, int depotid,int num) {
		
		/**
		 * 多店系统不维护分仓库存
		 */
		if(!"b2b2c".equals(EopSetting.PRODUCT)){
			if(this.checkExists(goodsid, depotid)){
				this.daoSupport.execute("update es_product_store set enable_store=enable_store+?,store =store+? where goodsid=? and depotid=?", num,num,goodsid,depotid);
			}else{
				this.daoSupport.execute("insert into es_product_store(goodsid,productid,depotid,store,enable_store)values(?,?,?,?,?)",goodsid,productid, depotid,num,num);
	
			}
		}
		
		this.daoSupport.execute("update es_product set enable_store=enable_store+?, store=store+?  where product_id=?", num, num,productid);
		this.daoSupport.execute("update es_goods set enable_store=enable_store+?,store=store+?  where goods_id=?", num, num,goodsid);
		this.daoSupport.execute("update es_goods set market_enable=? where goods_id=?", 1,goodsid);//自动上架
		
	}


	/**
	 * 查询某个仓库的商品库存是否存在
	 * @param goodsid
	 * @param depotid
	 * @return
	 */
	private boolean checkExists(int goodsid,int depotid){
		int count = this.daoSupport.queryForInt("select count(0) from es_product_store where goodsid=? and depotid=?", goodsid,depotid) ;
		return count==0?false:true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IProductStoreManager#getStroe(int, int)
	 */
	@Override
	public int getEnableStroe(int goodsid, int depotid) {
		String sql ="select enable_store from es_product_store where goodsid=? and depotid=?";
		List<Integer> storeList  = this.daoSupport.queryForList(sql, new IntegerMapper(),goodsid,depotid);
		int store=0;
		if(!storeList.isEmpty()){
			store=storeList.get(0);
		}
		return store;
	}

	
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	
 

}
