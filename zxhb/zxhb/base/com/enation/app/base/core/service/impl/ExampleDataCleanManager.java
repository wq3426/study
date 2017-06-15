/**
 * 
 */
package com.enation.app.base.core.service.impl;

import com.enation.app.base.core.service.IExampleDataCleanManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;
import com.enation.framework.database.IDaoSupport;

/**
 * 
 * 示例数据清除管理
 * @author kingapex
 *2015-6-3
 */
public class ExampleDataCleanManager implements IExampleDataCleanManager {
	
	private IDaoSupport  daoSupport;
 
	
	
	/* (non-Javadoc)
	 * @see com.enation.app.base.core.service.IExampleDataCleanManager#clean(java.lang.String[])
	 */
	@Override
	public void clean(String[] moudels) {
		
		
		if( this.hasMoudel("goods", moudels)){
			this.cleanGoods();
			this.cleanOrder();
		}
		
		if( this.hasMoudel("order", moudels)){
			this.cleanOrder();
		}
		
		if( this.hasMoudel("member", moudels)){
			this.cleanMember();
			this.cleanOrder();
		}
		
		
		if( this.hasMoudel("goodscat", moudels)){
			this.cleanGoodsCat();
		}
		
		
		if( this.hasMoudel("goodstype", moudels)){
			this.cleanGoodsType();
			this.cleanGoodsCat();
			this.cleanGoods();
		}
		
	
		if( this.hasMoudel("brand", moudels)){
			this.cleanBrand();
		}
		
		if( this.hasMoudel("store", moudels)){
			this.cleanStore();
			this.cleanOrder();
		}
		
		
	}
 
	/**
	 * 检测某个模块是否存在
	 * @param m
	 * @param moudels
	 * @return
	 */
	private boolean hasMoudel(String m,String[] moudels){
		for (String s : moudels) {
			if(s.equals(m)){
				return true;
			}
		}
		
		return false;
	}
	
	
	
	/***
	 * 清空商品
	 */
	private void cleanGoods(){
		
		String sql ="TRUNCATE table es_goods";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_goods_gallery";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_product";
		this.daoSupport.execute(sql);
		

		sql ="TRUNCATE table es_product_store";
		this.daoSupport.execute(sql);
		
		
		sql ="TRUNCATE table es_store_log";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_tag_rel";
		this.daoSupport.execute(sql);
		
		
	}
	
	
	
	
	/**
	 * 清空订单
	 */
	private void cleanOrder(){
	
		String sql ="TRUNCATE table es_order";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_order_items";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_order_log";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_order_meta";
		this.daoSupport.execute(sql);
	}
	
	
	/**
	 * 清空商品分类
	 */
	private void cleanGoodsCat(){
		String sql ="TRUNCATE table es_goods_cat";
		this.daoSupport.execute(sql);
		ICache cache = CacheFactory.getCache("goods_cat");
		cache.remove("goods_cat_0");
	}

	
	/**
	 * 清空商品类型
	 */
	private void cleanGoodsType(){
		String sql ="TRUNCATE table es_goods_type";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_type_brand";
		this.daoSupport.execute(sql);
	}

	
	/**
	 * 清除品牌
	 */
	private void cleanBrand(){
		String sql ="TRUNCATE table es_brand";
		this.daoSupport.execute(sql);
		
	}
	
	
	
	
	/**
	 * 清除店铺
	 */
	private void cleanStore(){
		String sql ="TRUNCATE table es_store";
		this.daoSupport.execute(sql);
		
		/**
		 * 清空店铺表后需要再插入一条自营店铺的数据，以防止自营店某些功能报错
		 */
		sql = "insert into es_store values (1,'平台自营',null,1,34,452,'我的地址','101601','13333333333',1,1,'pingtaiziying','133333333333333333',null,null,1,1421329391,null,null,null,0,0,0,0.00,0.00,0.00,0.00,0,0,0,'北京','三河','燕郊',3,null,null,0.00,'开户名','62323233233','1212131','121212',1,34,452,'北京','三河','燕郊',null,null,null,null)";
		this.daoSupport.execute(sql);
		
		sql = "TRUNCATE table es_tags";
		this.daoSupport.execute(sql);
		
		/**
		 * 清除标签表数据后，需插入以下数据防止平台某些功能报错
		 */
		if(EopSetting.PRODUCT.equals("b2c")){
			sql = "insert into es_tags values (1,'特别推荐',0,null,null,null),(2,'猜你喜欢',0,null,null,null),(3,'新品上市',0,null,null,null),(4,'疯狂抢购',0,null,null,null),(5,'热卖排行',0,1,null,'hot'),(6,'新品推荐',0,1,null,'new'),(7,'推荐商品',0,1,null,'recommend')";
		}else{
			sql = "insert into es_tags values (1,'特别推荐',0,null,null,null,null),(2,'猜你喜欢',0,null,null,null,null),(3,'新品上市',0,null,null,null,null),(4,'疯狂抢购',0,null,null,null,null),(5,'热卖排行',0,1,null,null,'hot'),(6,'新品推荐',0,1,null,null,'new'),(7,'推荐商品',0,1,null,null,'recommend')";
		}
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_store_silde";
		this.daoSupport.execute(sql);
		
		/**
		 * 清除es_store_silde数据后，需要插入以下数据，以防止自营店店铺报错
		 */
		sql = "insert into es_store_silde values (1,1,null,'fs://images/s_side.jpg'),(2,1,null,'fs://images/s_side.jpg'),(3,1,null,'fs://images/s_side.jpg'),(4,1,null,'fs://images/s_side.jpg'),(5,1,null,'fs://images/s_side.jpg')";
		this.daoSupport.execute(sql);
		
	}
	
	/**
	 * 清空会员
	 */
	private void cleanMember(){
		String sql ="TRUNCATE table es_member";
		this.daoSupport.execute(sql);
		
		/**
		 * 清空会员表数据后，需插入以下一条数据用以关联自营店店铺
		 */
		sql = "insert into es_member values (1,null,0,1,'pingtaiziying','2459931371@qq.com','49315942650d43f880d745710c742240',1421329021,null,null,'pingtaiziying',1,null,0.00,0,0,0,null,null,null,null,null,'',null,20,20,null,null,null,1436884739,0,3,0,'0:0:0:0:0:0:0:1',0,null,0,null,'','pingtaiziying',0,1,1)";
		this.daoSupport.execute(sql);
		
		sql ="TRUNCATE table es_member_address";
		this.daoSupport.execute(sql);
		
	}
	
	
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}


	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}


 
	

}
