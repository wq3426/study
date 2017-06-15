package com.enation.app.base.core.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Regions;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.app.base.core.service.dbsolution.IDBSolution;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.database.IntegerMapper;

/**
 * 地区管理
 * @author kingapex
 * 2010-7-18下午08:12:03
 */
public class RegionsManager extends BaseSupport<Regions> implements IRegionsManager {

	public List listCity(int province_id) {
		List list = this.baseDaoSupport.queryForList("select * from regions where region_grade = 2 and p_region_id = ?", Regions.class, province_id);
		list = list == null ? new ArrayList() : list;
		return list;
	}
	
	public List listProvince() {
		List list = this.baseDaoSupport.queryForList("select * from regions where region_grade = 1", Regions.class);
		list = list == null ? new ArrayList() : list;
		return list;
	}
	
	public List listRegion(int city_id) {
		List list = this.baseDaoSupport.queryForList("select * from regions where region_grade = 3 and p_region_id = ?", Regions.class, city_id);
		list = list == null ? new ArrayList() : list;
		return list;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.base.core.service.IRegionsManager#listChildrenAsyn(java.lang.Integer)
	 */
	public List listChildrenAsyn(Integer regionid) {
		
		String sql = "select reg.*,tt.t_num as totle_num from es_regions reg left join "
				+ " (select r.p_region_id r_pid ,count(r.region_id) as t_num from es_regions r where r.p_region_id in "
				+ " (select re.region_id from es_regions re where re.p_region_id=? ) GROUP BY r.p_region_id) tt on reg.region_id = tt.r_pid "
				+ " where reg.p_region_id=? ";
		
		 List<Map> regionlist = this.daoSupport.queryForList(sql, regionid,regionid);

		 for(Map map :regionlist){
			if(map.get("totle_num")==null){		//判断某一个分类下的子分类数量 null赋值为0
				map.put("totle_num", 0);
			}
			int totle_num = Integer.parseInt(map.get("totle_num").toString());
			 if(totle_num!=0){		//判断某一个分类下的子分类数量 不为0 则是(easyui)文件夹并且是闭合状态。
				 map.put("state", "closed");
			 }
		 }
		return regionlist;
		
	}
	
	public List listChildren(Integer regionid) {
		StringBuffer sql  = new StringBuffer();
		sql.append("select * from  es_regions ");
		List<Regions> list = this.daoSupport.queryForList(sql.toString(),Regions.class);
		List<Regions> reglist = new ArrayList<Regions>();
		for(Regions map:list){
			if(map.getP_region_id().equals(regionid)){
				List<Regions> children =this.getChildren(list, map.getRegion_id());
				map.setChildren(children);
				int i = this.baseDaoSupport.queryForInt("select count(0) from es_regions where p_region_id="+map.getRegion_id());
				if(i!=0){
					map.setState("closed");
				}
				reglist.add(map);
			}
		}		
		return reglist;
	}
	private List<Regions> getChildren(List<Regions> list,Integer id){
		List<Regions> children = new ArrayList<Regions>();
		for (Regions map : list) {
			if(map.getP_region_id().equals(id)){				
				map.setChildren(this.getChildren(list, map.getRegion_id()));
				children.add(map);
			}
		}
		return children;
	}
	
	@SuppressWarnings("rawtypes")
	public List listChildren(String regionid) {
		
		if(regionid==null || regionid.equals("")) return new ArrayList();
		StringBuffer sql  = new StringBuffer();
		sql.append("select * from  ");
		sql.append(this.getTableName("regions"));
		sql.append(" c");
		sql.append("  where c. p_region_id in("+regionid+")  order by region_id");
		List list = this.daoSupport.queryForList(sql.toString(),new IntegerMapper());
		return list;
	}
	
	public List listChildrenByid(Integer regionid) {
		StringBuffer sql  = new StringBuffer();
		sql.append("select c.region_id,c.local_name,c.region_grade,c.p_region_id,count(s.region_id) as childnum,c.zipcode,c.cod  from  ");

		sql.append(this.getTableName("regions"));
		sql.append(" c");
		
		sql.append(" left join ");
		sql.append(this.getTableName("regions"));
		sql.append(" s");
		
		sql.append(" on s.p_region_id = c.region_id  where c.p_region_id=? group by c.region_id,c.local_name,c.region_grade,c.p_region_id,c.zipcode,c.cod order by region_id");
		List list = this.daoSupport.queryForList(sql.toString(),regionid);
		return list;
	}
	
	@SuppressWarnings("rawtypes")
	public String getChildrenJson(Integer regionid) {
		List list  = this.listChildren(regionid);
		JSONArray jsonArray = JSONArray.fromObject( list );  
		return jsonArray.toString();
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(Regions regions) {
		this.baseDaoSupport.insert("regions", regions);
		String region_path = "";
		int region_id = this.baseDaoSupport.getLastId("regions");
		if(regions.getP_region_id()!=null&&regions.getP_region_id() != 0){
			Regions p = get(regions.getP_region_id());
			region_path = p.getRegion_path() + region_id + ",";
		}else{
			region_path = "," + region_id + ",";
		}
		regions = get(region_id);
		regions.setRegion_path(region_path);
		update(regions);
	}

	public void delete(int regionId) {
		this.baseDaoSupport.execute("delete from regions where region_path like '%,"+regionId+",%'");
		
	}
	
	public Regions get(int regionId) {
		return this.baseDaoSupport.queryForObject("select * from regions where region_id = ?", Regions.class, regionId);
	}
	
	public Regions getByName(String name) {
		try{
			List<Regions> list = this.baseDaoSupport.queryForList("select * from regions where local_name = ?", Regions.class, name  );
			if(list== null || list.isEmpty()) {
				return null;
			}
			return list.get(0);
		}catch(RuntimeException e){
			return null;
		}
	}

	public void update(Regions regions) {
		this.baseDaoSupport.update("regions", regions, "region_id="+regions.getRegion_id());
		
	}

	@Transactional(propagation = Propagation.REQUIRED) 
	public void reset() {
		Connection conn = DBSolutionFactory.getConnection(null);
		try {
			Statement state = conn.createStatement();
			state.execute("truncate table " + this.getTableName("regions"));	//	先清空表中数据
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String xmlFile = "file:com/enation/app/base/city.xml";
			DBSolutionFactory.dbImport(xmlFile, "es_");
		/*
		IDBSolution dbsolution = DBSolutionFactory.getDBSolution();
		dbsolution.setPrefix("es_");
		dbsolution.setConnection(conn);
		boolean result = dbsolution.dbImport();
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		*/
	}

	public Regions[] get(String area) {
		//这里什么也不做
		return null;
	}

	@Override
	public List get4sStoreRegion() {
		List list = new ArrayList<>();
		try {
			String sql = "SELECT c.region_id, SUBSTRING_INDEX(c.local_name,'市',1) local_name FROM es_regions c INNER JOIN es_store s ON c.region_id=s.store_cityid WHERE c.region_grade=2 GROUP BY c.region_id, c.local_name";
			list = daoSupport.queryForList(sql);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return list;
	}
	
}
