package com.enation.app.fastdfsclient.sync;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @Description 同步文件到服务器--service
 *
 * @createTime 2016年8月23日 下午3:21:19
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
@Service
public class SyncService {
	
	@SuppressWarnings("rawtypes")
	@Autowired
	private JdbcDaoSupport daoSupport;
	
	
	/**
	 * @description 初始化要操作的表和字段名称
	 * @date 2016年8月23日 下午3:21:38
	 * @return List<String[]>
	 */
	public List<String[]> init(){
		
		List<String[]> relationList = new ArrayList<>();
		
		String[] appRecord = {"es_app_record","app_id","app_url"};
		String[] store_logo = {"es_store","store_id","store_logo"};
		String[] store_img = {"es_store","store_id","id_img"};
		String[] member = {"es_member","member_id","face"};
		String[] hodometer = {"es_hodometer","id","gps_imgurl"};
		String[] carmodels_brandimage = {"es_carmodels","id","brandimage"};
		String[] carmodels_modelimage = {"es_carmodels","id","modelimage"};
		String[] brand = {"es_brand","brand_id","logo"};
		String[] carinfo = {"es_carinfo","id","carmodelimage"};
		String[] insurances = {"es_insurances","id","companyimage"};
		String[] goods_thumbnail = {"es_goods","goods_id","thumbnail"};
		String[] goods_big = {"es_goods","goods_id","big"};
		String[] goods_small = {"es_goods","goods_id","small"};
		String[] goods_original = {"es_goods","goods_id","original"};
		String[] adv = {"es_adv","aid","atturl"};
		String[] appMessage = {"es_app_message","id","image"};
		
		relationList.add(appRecord);
		relationList.add(store_logo);
		relationList.add(store_img);
		relationList.add(member);
		relationList.add(hodometer);
		relationList.add(carmodels_brandimage);
		relationList.add(carmodels_modelimage);
		relationList.add(brand);
		relationList.add(carinfo);
		relationList.add(insurances);
		relationList.add(goods_thumbnail);
		relationList.add(goods_big);
		relationList.add(goods_small);
		relationList.add(goods_original);
		relationList.add(adv);
		relationList.add(appMessage);
		
		return relationList;
	}
	
	/**
	 * @description 查询数据库中的图片URl
	 * @date 2016年8月23日 下午3:22:29
	 * @param tableName
	 * @param id_name
	 * @param url_name
	 * @return List<Map<String,Object>>
	 */
	@SuppressWarnings({ "unchecked" })
	public List<Map<String,Object>> queryImageUrl(String tableName, String  id_name, String url_name){
				
		String sql = 
			"SELECT             			"+
			"	t."+id_name+",  			"+
	        "   t."+url_name+"   			"+
			"FROM               			"+
			"	"+tableName+" t    			";
	
		List<Map<String,Object>> ImageUrlList = daoSupport.queryForList(sql.toString());		
		return ImageUrlList; 
	}

	/**
	 * @description 更新数据
	 * @date 2016年8月23日 下午3:22:54
	 * @param updateMap
	 * @param tableName
	 * @param id_name
	 * @param url_name
	 * @return int
	 */
	public int updateImageUrl(Map<Integer, String> updateMap, String tableName, 
			String id_name, String url_name) {
		
		int resultCount = 0;
		for (Map.Entry<Integer, String> map : updateMap.entrySet()) {
			int id = map.getKey();
			String imageUrl = map.getValue();
			
			String sql=                 
				" UPDATE "+tableName+" t        	"+
				" SET t."+url_name+"='"+imageUrl+"' "+
				" WHERE t."+id_name+"="+id+"        ";
			
			daoSupport.execute(sql);
			resultCount++;
		}
		return resultCount;
	}
	
	

}
