package com.enation.app.b2b2c.core.service.goods.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.goods.StoreGoods;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsCatManager;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.plugin.goods.GoodsPluginBundle;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
@Component
public class StoreGoodsManager extends BaseSupport implements IStoreGoodsManager{
	private IStoreMemberManager storeMemberManager;
	private GoodsPluginBundle goodsPluginBundle;
	private IGoodsCatManager goodsCatManager;
	private IProductManager productManager;
	private IStoreGoodsCatManager storeGoodsCatManager;
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#storeGoodsList(java.lang.Integer, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Page storeGoodsList(Integer pageNo,Integer pageSize,Map map) {
		Integer store_id=Integer.valueOf(map.get("store_id").toString());
		Integer disable=Integer.valueOf(map.get("disable")+"");
		String store_cat=String.valueOf(map.get("store_cat"));
		String goodsName=String.valueOf(map.get("goodsName"));
		String market_enable=String.valueOf(map.get("market_enable"));
		
		StringBuffer sql=new StringBuffer("SELECT g.*,c.store_cat_name from es_goods g LEFT JOIN es_store_cat c ON g.store_cat_id=c.store_cat_id where g.store_id="+store_id +" and  g.disabled="+disable);
		
		if(!StringUtil.isEmpty(store_cat)&&!StringUtil.equals(store_cat, "null")&&!StringUtil.equals(store_cat, "0")){
			//根据店铺分类ID获取分类的父ID add by DMRain 2016-1-19
			Integer pId = this.storeGoodsCatManager.is_children(Integer.parseInt(store_cat));
			
			//如果店铺分类父ID为0，证明要查询的分类为父分类下的所有子分类和父分类本身	add by DMRain 2016-1-19
			if(pId == 0){
				sql.append(" and (c.store_cat_pid="+store_cat+" or g.store_cat_id="+store_cat+")");
			}else{
				sql.append(" and g.store_cat_id="+store_cat);
			}
		}
		
		if(!StringUtil.isEmpty(goodsName)&&!StringUtil.equals(goodsName, "null")){
			sql.append(" and ((g.name like '%"+goodsName+"%') or (g.sn like '%"+goodsName+"%') )");
		}
		if(!StringUtil.isEmpty(market_enable)&&!StringUtil.equals(market_enable, "null")){
			if(!market_enable.equals("99")){
				sql.append(" and g.market_enable="+market_enable);
			}
		}
		
		sql.append(" order by g.create_time desc");
		
		Page page=daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
		
		
		List list = (List) page.getResult(); 
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map2 = (Map<String, Object>) list.get(i);
			if (map2.containsKey("thumbnail") && map2.get("thumbnail") != null) {
				String pic  =UploadUtil.replacePath(map2.get("thumbnail").toString()); 
				map2.put("thumbnail", pic);
			}
		}
		page.setResult(list);
		return page;
	}
	@Override
	public List<Map> getStoreGoodsList(Map map) {
		// TODO Auto-generated method stub
		Integer store_id=Integer.valueOf(map.get("store_id").toString());
		Integer disable=Integer.valueOf(map.get("disable")+"");
		String store_cat=String.valueOf(map.get("store_cat"));
		String goodsName=String.valueOf(map.get("goodsName"));
		String market_enable=String.valueOf(map.get("market_enable"));
		
		StringBuffer sql=new StringBuffer("SELECT g.*,FROM_UNIXTIME(g.create_time,'%Y-%m-%d') create_time,c.store_cat_name from es_goods g LEFT JOIN es_store_cat c ON g.store_cat_id=c.store_cat_id where g.store_id="+store_id +" and  g.disabled="+disable);
		
		if(!StringUtil.isEmpty(store_cat)&&!StringUtil.equals(store_cat, "null")&&!StringUtil.equals(store_cat, "0")){
			//根据店铺分类ID获取分类的父ID add by DMRain 2016-1-19
			Integer pId = this.storeGoodsCatManager.is_children(Integer.parseInt(store_cat));
			
			//如果店铺分类父ID为0，证明要查询的分类为父分类下的所有子分类和父分类本身	add by DMRain 2016-1-19
			if(pId == 0){
				sql.append(" and (c.store_cat_pid="+store_cat+" or g.store_cat_id="+store_cat+")");
			}else{
				sql.append(" and g.store_cat_id="+store_cat);
			}
		}
		
		if(!StringUtil.isEmpty(goodsName)&&!StringUtil.equals(goodsName, "null")){
			sql.append(" and ((g.name like '%"+goodsName+"%') or (g.sn like '%"+goodsName+"%') )");
		}
		if(!StringUtil.isEmpty(market_enable)&&!StringUtil.equals(market_enable, "null")){
			if(!market_enable.equals("99")){
				sql.append(" and g.market_enable="+market_enable);
			}
		}
		
		sql.append(" order by g.create_time desc");
		
		List list = this.daoSupport.queryForList(sql.toString());
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map2 = (Map<String, Object>) list.get(i);
			if (map2.containsKey("thumbnail") && map2.get("thumbnail") != null) {
				String pic  =UploadUtil.replacePath(map2.get("thumbnail").toString()); 
				map2.put("thumbnail", pic);
			}
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#storeGoodsList(int, java.util.Map)
	 */
	@Override
	public List<Map> storeGoodsList(int storeid, Map map) {
		
		StringBuffer sql=new StringBuffer("SELECT g.* from es_goods g where g.store_id="+storeid +" and  g.disabled=0");
		String store_catid=String.valueOf(map.get("store_catid"));
		String keyword=String.valueOf(map.get("keyword"));
		if(!StringUtil.isEmpty(store_catid) && !"0".equals(store_catid)){ //按店铺分类搜索
			sql.append(" and g.store_cat_id="+store_catid);
		}
		
		if(!StringUtil.isEmpty(keyword) ){
			sql.append(" and ((g.name like '%"+keyword+"%') or (g.sn like '%"+keyword+"%') )");
		}
		return this.daoSupport.queryForList(sql.toString());
	}
	
	
	protected Map po2Map(Object po) {
		Map poMap = new HashMap();
		Map map = new HashMap();
		try {
			map = BeanUtils.describe(po);
		} catch (Exception ex) {
		}
		Object[] keyArray = map.keySet().toArray();
		for (int i = 0; i < keyArray.length; i++) {
			String str = keyArray[i].toString();
			if (str != null && !str.equals("class")) {
				if (map.get(str) != null) {
					poMap.put(str, map.get(str));
				}
			}
		}
		return poMap;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#b2b2cGoodsList(java.lang.Integer, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Page b2b2cGoodsList(Integer pageNo, Integer pageSize, Map map) {
		String keyword=(String) (map.get("namekeyword")==null?"":map.get("namekeyword"));
		String cat_id=(String) (map.get("cat_id")==null?"":map.get("cat_id"));
		String search_type=(String) (map.get("search_type")==null?"":map.get("search_type")); //0:默认、1:销量、2:价格
		StringBuffer sql=new StringBuffer("select g.*,s.store_name as store_name,s.qq as qq from es_goods g inner join es_store s on s.store_id=g.store_id INNER JOIN es_brand b ON b.brand_id=g.brand_id  where s.disabled=1 and g.disabled=0 and g.market_enable=1");
			
		if(!StringUtil.isEmpty(keyword)){
			sql.append("  and ((g.name like '%"+keyword+"%') or ( g.sn like '%"+keyword+"%') or(b.name like '%"+keyword+"%'))");
		}
		if (!StringUtil.isEmpty(cat_id) && cat_id!="0") {
			Cat cat = this.goodsCatManager.getById(Integer.parseInt(cat_id));
			sql.append(" and  g.cat_id in(select c.cat_id from es_goods_cat c where c.cat_path like '" + cat.getCat_path()+ "%')");
		}
		if(!StringUtil.isEmpty(search_type)){
			if(search_type.equals("1")){
				sql.append(" order by buy_num desc");
			}else if(search_type.equals("2")){
				sql.append(" order by price desc");
			}else{
				sql.append(" order by goods_id desc");
			}
		}
		//System.out.println(sql.toString());
		return this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#store_searchGoodsList(java.lang.Integer, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Page store_searchGoodsList(Integer page, Integer pageSize, Map params) {
		Integer storeid = (Integer) params.get("storeid");
		String keyword = (String) params.get("keyword");
		String start_price = (String) params.get("start_price");
		String end_price = (String) params.get("end_price");
		Integer key = (Integer) params.get("key");
		String order = (String) params.get("order");
		Integer cat_id = (Integer) params.get("stc_id");
		
		StringBuffer sql=new StringBuffer("select * from es_goods g where disabled=0 and market_enable=1 and store_id="+storeid);
		
		if(!StringUtil.isEmpty(keyword)){
			sql.append("  and g.name like '%"+keyword+"%' ");
		}
		
		if(!StringUtil.isEmpty(start_price)){
			sql.append(" and price>="+ Double.valueOf(start_price));
		}
		if(!StringUtil.isEmpty(end_price)){
 			sql.append(" and price<="+ Double.valueOf(end_price));
		}
		
		if (cat_id!=null && cat_id!=0) {
			
			List<Map> list  =this.daoSupport.queryForList("select store_cat_id from es_store_cat where store_cat_pid=?", cat_id);
			String cat_str=cat_id+",";
			for (Map map : list) {
				cat_str = map.get("store_cat_id").toString()+",";
			}
			sql.append(" and  g.store_cat_id in("+cat_str.substring(0, cat_str.length()-1)+")");
		}
		
		if(key!=null){
			if(key==1){			//1:新品
				sql.append(" order by goods_id "+order);
			}else if(key==2){	//2:价格
				sql.append(" order by price "+order);
			}else if(key==3){	//3:销量
				sql.append(" order by buy_num "+order);
			}else if(key==4){	//4:收藏
				sql.append(" order by goods_id "+order);
			}else if(key==5){	//5:人气
				sql.append(" order by goods_id "+order);
			}
		}
		 
		Page webpage = this.baseDaoSupport.queryForPage(sql.toString(), page, pageSize);
		return webpage;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#saveGoodsStore(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveGoodsStore(Integer storeid,Integer goods_id, Integer storeNum) {
		Product product=productManager.getByGoodsId(goods_id);
		Integer productid=product.getProduct_id();
		
				
		if (storeid == 0) { // 新库存
			this.daoSupport.execute("insert into es_product_store(goodsid,productid,depotid,store,enable_store)values(?,?,?,?,?)", goods_id, productid, 1, storeNum,storeNum);
		}else{
			//如果 现有可用库存小于修改后的库存 现有可用库存为准 
			/*Map nowStore = this.daoSupport.queryForMap("select enable_store,store from es_product_store where goodsid = ?", goods_id);
			 
			if(Integer.parseInt(nowStore.get("store").toString())-Integer.parseInt((String) nowStore.get("enable_store").toString()) > 0){
				storeNum=Integer.parseInt(nowStore.get("store").toString())-Integer.parseInt(nowStore.get("enable_store").toString());
			} */
			// 更新库存
			this.daoSupport.execute("update es_product_store set store=store-enable_store+?,enable_store=? where goodsid=?", storeNum,storeNum, goods_id);
		}
		
		
		
		
		this.daoSupport.execute("update es_goods set store=store-enable_store+?, enable_store=? where goods_id=?", storeNum,storeNum, goods_id);
		this.daoSupport.execute("update es_product set store=store-enable_store+?, enable_store=? where product_id=?", storeNum,storeNum, productid);
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveGoodsSpecStore(Integer[] store_id,Integer goods_id, Integer[] storeNum,Integer[] product_id){
		for(int i= 0;i<store_id.length ;i ++){
			if(store_id[i] == 0) { //新库存
				this.daoSupport.execute("insert into es_product_store(goodsid,productid,depotid,store,enable_store)values(?,?,?,?,?)", goods_id, product_id[i], 1, storeNum[i],storeNum[i]);
			}else{ //更新库存  
				
				//如果 现有可用库存小于修改后的库存 现有可用库存为准 
				/*Map nowStore = this.daoSupport.queryForMap("select enable_store,store from es_product_store where storeid = ?", store_id[i]);
				
				if(Integer.parseInt(nowStore.get("enable_store").toString())-Integer.parseInt((String) nowStore.get("store").toString())+storeNum[i]<0){
					storeNum[i]=Integer.parseInt(nowStore.get("store").toString())-Integer.parseInt(nowStore.get("enable_store").toString());
				}*/
				
				this.daoSupport.execute("update es_product_store set store=store-enable_store+?,enable_store=? where storeid=?", storeNum[i],storeNum[i], store_id[i]);
			}
			//更新某个货品的总库存 
			this.daoSupport.execute("update es_product set store=store-enable_store+?,enable_store=? where product_id=?", storeNum[i],storeNum[i], product_id[i]);
		}
		//更新商品总库存
		this.daoSupport.execute("update "+this.getTableName("goods")+" set store=(select sum(store) from "+this.getTableName("product_store")+" where goodsid=?),enable_store=(select sum(enable_store) from "+this.getTableName("product_store")+" where goodsid=?) where goods_id=? ", goods_id,goods_id,goods_id);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#transactionList(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List transactionList(Integer pageNo, Integer pageSize,
			Integer goods_id) {
		String sql="select * from  es_transaction_record where goods_id=? order by record_id";
		return  daoSupport.queryForListPage(sql, pageNo, pageSize, goods_id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#transactionCount(java.lang.Integer)
	 */
	@Override
	public int transactionCount(Integer goods_id) {
		String sql="select count(0) from  es_transaction_record where goods_id=? ";
		return	this.daoSupport.queryForInt(sql, goods_id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#getGoods(java.lang.Integer)
	 */
	@Override
	public StoreGoods getGoods(Integer goods_id) {
		String sql  = "select * from es_goods where goods_id=?";
		StoreGoods goods = (StoreGoods) this.baseDaoSupport.queryForObject(sql, StoreGoods.class, goods_id);
		return goods;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#getStoreGoodsNum(int)
	 */
	@Override
	public int getStoreGoodsNum(int struts) {
		StoreMember member  = storeMemberManager.getStoreMember();
		StringBuffer sql=new StringBuffer("SELECT count(goods_id) from es_goods where store_id=? and  disabled=0 and market_enable=?");
		return this.daoSupport.queryForInt(sql.toString(), member.getStore_id(),struts);
	}
	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	@Override
	public Map getGoodsStore(Integer goods_id) {
		 List<Map> list= this.daoSupport.queryForList("select * from es_product_store where goodsid=?", goods_id);
		 if(list.size()>0){
			 return list.get(0);
		 }else{
			 return null;
		 }
	}
	
	@Override
	public List getGoodsSpecStore(Integer goods_id){
		List<Map> list= this.daoSupport.queryForList("select * from es_product_store where goodsid=?", goods_id);
		if(list.size()>0){
			return list;
		}else{
			return new ArrayList();
		}
	}
	
	@Override
	public void addStoreGoodsComment(Integer goods_id) {

		String sql="update es_goods set comment_num=comment_num+1 where goods_id="+goods_id;
		this.daoSupport.execute(sql);
	}
	
	@Override
	public void editStoreGoodsGrade(Integer goods_id){
		int gradeAvg = this.getGoodsGradeAvg(goods_id);
		String sql = "update es_goods set grade = ? where goods_id = ?";
		this.daoSupport.execute(sql, gradeAvg, goods_id);
	}
	
	/**
	 * 根据商品id获取商品评分的平均值
	 * @param goods_id 商品id
	 * @return gradeAvg 商品评分的平均值
	 */
	private int getGoodsGradeAvg(Integer goods_id){
		int gradeAvg;
		String sql = "select avg(grade) from es_member_comment where goods_id = ? and type = 1";
		gradeAvg = this.daoSupport.queryForInt(sql, goods_id);
		return gradeAvg;
	}
	
	@Override
	public JSONObject getZxCardGoodsInfo() {
		JSONObject obj = new JSONObject();
		try {
			//查询商品信息
			String sql = "SELECT goods_id, NAME, intro, type_id FROM es_goods WHERE store_id=1 AND cat_id=53";
			List list = daoSupport.queryForList(sql);
			if(list.size() > 0){
				JSONObject goodsObj = JSONObject.fromObject(list.get(0));
				String intro = goodsObj.getString("intro").replace("<p>", "").replace("</p>", "");
				goodsObj.put("intro", intro);
				obj.put("goods", goodsObj);
				
				int select_spec_id = 0;//选中的属性id
				
				//查询规格属性集合
				sql = "SELECT spec_id, spec_name FROM es_specification WHERE spec_name='规格'";
				List ggList = daoSupport.queryForList(sql);
				if(ggList.size() > 0){
					JSONObject ggObject = JSONObject.fromObject(ggList.get(0));
					int spec_id = ggObject.getInt("spec_id");
					
					//查询属性值集合
					String specSql = "SELECT * FROM es_spec_values WHERE spec_id=? ORDER BY spec_value_id";
					List specValueList = daoSupport.queryForList(specSql, spec_id);
					if(specValueList.size() > 0){
						ggObject.put("spec_value_list", JSONArray.fromObject(specValueList));
						select_spec_id = JSONArray.fromObject(specValueList).getJSONObject(0).getInt("spec_value_id");
					}
					
					obj.put("ggSpec", ggObject);
				}
				
				//查询图案属性集合
				String select_card_price = "0";
				sql = "SELECT spec_id, spec_name FROM es_specification WHERE spec_name='图案'";
				List taList = daoSupport.queryForList(sql);
				if(taList.size() > 0){
					JSONObject taObject = JSONObject.fromObject(taList.get(0));
					int spec_id = taObject.getInt("spec_id");
					
					//查询属性值集合
//					String specSql = "SELECT * FROM es_spec_values WHERE spec_id=? "
//							       + "AND spec_value_id IN (SELECT spec_value_id FROM spec_value_id_level WHERE parent_spec_value_id=?) "
//							       + "ORDER BY spec_value_id";
					String specSql = "SELECT t1.*, t2.card_price FROM es_spec_values t1, spec_value_id_level t2 WHERE t1.spec_value_id=t2.spec_value_id "
							       + "AND t1.spec_id=? AND t2.parent_spec_value_id=? "
							       + "ORDER BY t1.spec_value_id";
					List specValueList = daoSupport.queryForList(specSql, spec_id, select_spec_id);
					if(specValueList.size() > 0){
						JSONArray specArray = new JSONArray();
						JSONArray taSpecArray = JSONArray.fromObject(specValueList);
						for(int i=0; i<taSpecArray.size(); i++){
							JSONObject specObj = JSONObject.fromObject(taSpecArray.get(i));
							if(i == 0){
								select_card_price = specObj.getString("card_price");
							}
							specObj.put("spec_image", UploadUtil.replacePath(specObj.getString("spec_image")));
							specArray.add(specObj);
						}
						taObject.put("spec_value_list", specArray);
					}

					obj.put("taSpec", taObject);
					select_card_price = Double.valueOf(select_card_price) > 1 ? select_card_price.substring(0, select_card_price.indexOf(".")) : select_card_price;
					obj.put("card_price", select_card_price);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}
	
	@Override
	public JSONObject getTaSpecList(String gg_spec_value_id) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "SELECT t1.*, t2.card_price FROM es_spec_values t1, spec_value_id_level t2 WHERE t1.spec_value_id=t2.spec_value_id "
					   + "AND t2.parent_spec_value_id=? "
					   + "ORDER BY t1.spec_value_id";
			List list = daoSupport.queryForList(sql, gg_spec_value_id);
			if(list.size() > 0){
				obj.put("result", 1);
				JSONArray array = JSONArray.fromObject(list);
				JSONArray newArray = new JSONArray();
				for(int i=0; i<array.size(); i++){
					JSONObject specObj = array.getJSONObject(i);
					specObj.put("spec_image", UploadUtil.replacePath(specObj.getString("spec_image")));
					newArray.add(specObj);
				}
				obj.put("taList", newArray);
			}else{
				obj.put("result", 0);
				obj.put("message", "该规格下没有图案");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}
	
	@Override
	public JSONObject getTaPrice(String gg_spec_value_id, String ta_spec_value_id) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "SELECT t1.*, t2.card_price FROM es_spec_values t1, spec_value_id_level t2 WHERE t1.spec_value_id=t2.spec_value_id "
					   + "AND t1.spec_value_id=? AND t2.parent_spec_value_id=? "
					   + "ORDER BY t1.spec_value_id";
			List ta_spec_List = daoSupport.queryForList(sql, ta_spec_value_id, gg_spec_value_id);
			if(ta_spec_List.size() > 0){
				JSONObject specObj = JSONObject.fromObject(ta_spec_List.get(0));
				String spec_image = UploadUtil.replacePath(specObj.getString("spec_image"));
				specObj.put("spec_image", spec_image);
				obj.put("result", 1);
				obj.put("specObj", specObj);
			}else{
				obj.put("result", 0);
				obj.put("message", "没有查询到记录");
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}
	
	@Override
	public JSONObject getZxCardGoodsInfo(String goods_id, String gg_spec_value_id, String ta_spec_value_id) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "SELECT goods_id, NAME, intro FROM es_goods WHERE goods_id=?";
			List goodsList = daoSupport.queryForList(sql, goods_id);
			if(goodsList.size() > 0){
				JSONObject goodsObj = JSONObject.fromObject(goodsList.get(0));
				obj.put("goods", goodsObj);
				
				//根据规格属性值id获取规格属性值value
				sql = "SELECT spec_value FROM es_spec_values WHERE spec_value_id="+gg_spec_value_id;
				String gg_value = daoSupport.queryForString(sql);
				obj.put("gg_value", gg_value);
				
				//根据图案属性值id获取规格属性值value
				sql = "SELECT t1.*, t2.card_price FROM es_spec_values t1, spec_value_id_level t2 WHERE t1.spec_value_id=t2.spec_value_id "
					+ "AND t1.spec_value_id=? AND t2.parent_spec_value_id=?";
				List ta_spec_list = daoSupport.queryForList(sql, ta_spec_value_id, gg_spec_value_id);
				if(ta_spec_list.size() > 0){
					JSONObject taObj = JSONObject.fromObject(ta_spec_list.get(0));
					String ta_value = taObj.getString("spec_value");
					String ta_image = UploadUtil.replacePath(taObj.getString("spec_image"));
					String card_price = taObj.getString("card_price");
					
					obj.put("ta_value", ta_value);
					obj.put("ta_image", ta_image);
					card_price = Double.valueOf(card_price) > 1 ? card_price.substring(0, card_price.indexOf(".")) : card_price;
					obj.put("card_price", card_price);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}
	
	@Override
	public JSONObject getZxOrderInfo(String mobile, String order_id) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "SELECT user_id, user_telephone FROM order_user WHERE user_telephone=?";
			List user_list = daoSupport.queryForList(sql, mobile);
			if(user_list.size() > 0){
				JSONObject user_obj = JSONObject.fromObject(user_list.get(0));
				String user_telephone = user_obj.getString("user_telephone");
				user_telephone = user_telephone.replace(user_telephone.substring(4, 8), "****");
				
				sql = "SELECT *, FROM_UNIXTIME(create_time/1000, '%Y-%m-%d') order_date FROM order_detail WHERE order_id=?";
				List order_list = daoSupport.queryForList(sql, order_id);
				if(order_list.size() > 0){
					JSONObject order_obj = JSONObject.fromObject(order_list.get(0));
					order_obj.put("user_telephone", user_telephone);
					
					int goods_id = order_obj.getInt("goods_id");
					int gg_spec_value_id = order_obj.getInt("gg_spec_value_id");
					int ta_spec_value_id = order_obj.getInt("ta_spec_value_id");
					
					String goodsSql = "SELECT NAME, intro FROM es_goods WHERE goods_id=?";
					List goods_list = daoSupport.queryForList(goodsSql, goods_id);
					if(goods_list.size() > 0){
						JSONObject goods_obj = JSONObject.fromObject(goods_list.get(0));
						String goods_name = goods_obj.getString("name");
						String intro = goods_obj.getString("intro");
						intro = intro.replace("<p>", "").replace("</p>", "");
						order_obj.put("goods_name", goods_name);
						order_obj.put("intro", intro);
					}
					String gg_spec_sql = "SELECT spec_value FROM es_spec_values WHERE spec_value_id="+gg_spec_value_id;
					String gg_spec_value = daoSupport.queryForString(gg_spec_sql);
					
					order_obj.put("gg_spec_value", gg_spec_value);
					
					String ta_spec_sql = "SELECT spec_value, REPLACE(spec_image, ?, ?) spec_image FROM es_spec_values WHERE spec_value_id="+ta_spec_value_id;
					List ta_spec_list = daoSupport.queryForList(ta_spec_sql, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_domain());
					if(ta_spec_list.size() > 0){
						JSONObject ta_obj = JSONObject.fromObject(ta_spec_list.get(0));
						String ta_spec_value = ta_obj.getString("spec_value");
						String ta_spec_image = ta_obj.getString("spec_image");
						
						order_obj.put("ta_spec_value", ta_spec_value);
						order_obj.put("ta_spec_image", ta_spec_image);
					}

					obj.put("order_info", order_obj);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return obj;
	}
	
	public GoodsPluginBundle getGoodsPluginBundle() {
		return goodsPluginBundle;
	}

	public void setGoodsPluginBundle(GoodsPluginBundle goodsPluginBundle) {
		this.goodsPluginBundle = goodsPluginBundle;
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}


	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}

	public IStoreGoodsCatManager getStoreGoodsCatManager() {
		return storeGoodsCatManager;
	}

	public void setStoreGoodsCatManager(IStoreGoodsCatManager storeGoodsCatManager) {
		this.storeGoodsCatManager = storeGoodsCatManager;
	}

}
