package com.enation.app.shop.core.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.Brand;
import com.enation.app.shop.core.model.CarModel;
import com.enation.app.shop.core.model.CarmodelRepairItem;
import com.enation.app.shop.core.model.RepairItem;
import com.enation.app.shop.core.service.ICarManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.database.Page;
import com.enation.framework.util.ExcelUtils;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 车辆信息管理
 * 
 * @author kingapex 2010-1-13下午12:07:07
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CarManager extends BaseSupport implements ICarManager {

	@Override
	public List getCarBrandList() {
		String sql = "SELECT DISTINCT(brand), capital, replace(brandimage,?,?) brandimage FROM "+ getTableName("carmodels")+" c ORDER BY c.capital";
		List list = this.daoSupport.queryForList(sql, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_domain());
		this.logger.debug("carBrandList:" + JSONArray.fromObject(list).toString());
		return list;
	}

	@Override
	public List getCarSeries(String brandName) {
		String sql = "SELECT DISTINCT(series), type FROM es_carmodels c WHERE brand=? ORDER BY c.type";
		List seriesList = this.baseDaoSupport.queryForList(sql, brandName);
		this.logger.debug("carSeries:" + JSONArray.fromObject(seriesList).toString());
		return seriesList;
	}

	@Override
	public List getCarModelsList(String brand, String type, String series) {
		String sql = "SELECT id, model, replace(modelimage,?,?) modelimage FROM es_carmodels WHERE brand=? AND TYPE=? AND series=?";
		List carmodelsList = this.daoSupport.queryForList(sql, EopSetting.FILE_STORE_PREFIX, SystemSetting.getStatic_server_domain(), brand, type, series);
		this.logger.debug("carModels:" + JSONArray.fromObject(carmodelsList).toString());
		return carmodelsList;
	}

	@Override
	public Page getListByPage(Map keyMap, int page, int pageSize, String sort, String order) {
		StringBuilder sql = new StringBuilder();
		sql.append("select id, capital, brand, type, series, nk, sales_name, seats, discharge, IF(gearboxtype=1, '手动', '自动') gearboxtype, price, repairinterval, repairintervaltime from es_carmodels where 1=1");
		
		Integer stype = (Integer) keyMap.get("stype");
		String keyword = (String) keyMap.get("keyword");
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql.append(" and (brand like '%"+keyword+"%'");
				sql.append(" or sales_name like '%"+keyword+"%')");
			}
		}
		
		sql.append(" order by "+sort+" "+order);
		
		Page webPage = this.baseDaoSupport.queryForPage(sql.toString(), page, pageSize);
		
		return webPage;
	}
	
	@Override
	public List<Brand> getCarBrandSelectList() {
		String sql = "SELECT NAME, brand_id FROM es_brand WHERE TYPE=0";
		List<Brand> brandList = daoSupport.queryForList(sql, Brand.class);
		
		return brandList;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject importExcelData(File data, String dataFileName) throws Exception {
		JSONObject obj = new JSONObject();
		try {
			Map<String, Integer> map = new HashMap<>();
			List<Brand> brandList = getCarBrandSelectList();
			if(brandList.size() > 0){
				for(Brand b : brandList){
					map.put(b.getName(), b.getBrand_id());
				}
			}
			ExcelUtils eu = new ExcelUtils();
			List<List<String>> list = eu.read(new FileInputStream(data), ExcelUtils.isExcel2003(dataFileName));
			if(list.size() > 0){
				List<String> titleObject = list.get(0);
				int capitalIndex = titleObject.indexOf("首字母");
				int brandIndex = titleObject.indexOf("品牌");
				int brandimageIndex = titleObject.indexOf("Logo");
				int modelIndex = titleObject.indexOf("车型名称");
				int modelimageIndex = titleObject.indexOf("图片路径");
				int seriesIndex = titleObject.indexOf("车系");
				int nkIndex = titleObject.indexOf("年款");
				int salesnameIndex = titleObject.indexOf("车型名称");
				int seatsIndex = titleObject.indexOf("座位数(个)");
				int dischargeIndex = titleObject.indexOf("排量");
				int priceIndex = titleObject.indexOf("厂商指导价(元)");
				int gearboxIndex = titleObject.indexOf("变速箱");
				
				String reduplicationData = "";
				
				if(capitalIndex >= 0 && brandIndex >= 0 && salesnameIndex >= 0 && seriesIndex >= 0 && nkIndex >= 0 && seatsIndex >= 0 && dischargeIndex >= 0 && priceIndex >= 0){
					for(int i=1; i<list.size(); i++){
						CarModel carmodel = new CarModel();
						List<String> tempObj = list.get(i);
						carmodel.setCapital(tempObj.get(capitalIndex));
						String brand = tempObj.get(brandIndex);
						carmodel.setBrand(brand);
						if(map.containsKey(brand)){
							carmodel.setBrand_id(map.get(brand));
						}
						String brandImage = tempObj.get(brandimageIndex).replace("\\", "/");
						brandImage = EopSetting.FILE_STORE_PREFIX + "/files/" + brandImage;
						carmodel.setBrandimage(brandImage);
						carmodel.setModel(tempObj.get(modelIndex));
						String modelImage = tempObj.get(modelimageIndex).replace("\\", "/");
						modelImage = EopSetting.FILE_STORE_PREFIX + "/files/" + modelImage;
						carmodel.setModelimage(modelImage);
						String series = tempObj.get(seriesIndex);
						String sales_name = tempObj.get(salesnameIndex);
						if(series.indexOf("进口") != -1 || series.indexOf("海外") != -1 || sales_name.indexOf("进口") != -1 || sales_name.indexOf("海外") != -1){
							carmodel.setType("进口");
						}else{
							carmodel.setType("国产");
						}
						String gearboxtype = tempObj.get(gearboxIndex);
						if(gearboxtype.indexOf("手动") > 0){
							carmodel.setGearboxtype(1);
						}else{
							carmodel.setGearboxtype(2);
						}
						carmodel.setSeries(series);
						carmodel.setNk(tempObj.get(nkIndex));
						carmodel.setSales_name(sales_name);
						carmodel.setSeats("".equals(tempObj.get(seatsIndex)) ? 0 : Integer.valueOf(tempObj.get(seatsIndex)));
						carmodel.setDischarge(tempObj.get(dischargeIndex));
						carmodel.setPrice("".equals(tempObj.get(priceIndex)) ? 0.0 : Double.valueOf(tempObj.get(priceIndex).replace("万", "")));
						
						if(isExist(carmodel)){//如果车型已经存在，则不再重复添加
							if(i == 1){
								reduplicationData += i;
							}else{
								reduplicationData += "," + i;
							}
							continue;
						}

						daoSupport.insert("es_carmodels", carmodel);
					}
					
					obj.put("result", 1);
					if("".equals(reduplicationData)){
						obj.put("message", "导入成功");
					}else{
						obj.put("message", "导入成功,但有重复数据，行数为："+reduplicationData);
					}
				}else{
					obj.put("result", 0);
					obj.put("message", "您选择的excel文件内容不是车型相关数据，请检查后重新导入");
				}
			}else{
				obj.put("result", 0);
				obj.put("message", "文件内容为空，请检查");
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw e1;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public JSONObject addCarmodel(CarModel carmodel, String carmodel_type, File data, String dataFileName) throws Exception {
		JSONObject obj = new JSONObject();
		try {
			carmodel.setCapital(carmodel.getCapital().toUpperCase());
			carmodel.setModel(carmodel.getSales_name());
			String sql = "select name, logo from es_brand where brand_id=?";
			List<String> brandlist = daoSupport.queryForList(sql, carmodel.getBrand_id());
			if(brandlist.size() > 0){
				JSONObject brandObj = JSONObject.fromObject(brandlist.get(0));
				carmodel.setBrand(brandObj.getString("name"));
				carmodel.setBrandimage(brandObj.getString("logo"));
			}
			if("1".equals(carmodel_type)){
				carmodel.setType("国产");
			}else if("2".equals(carmodel_type)){
				carmodel.setType("进口");
			}
			String folder = SystemSetting.carmodel_image_savedir +"/"+ carmodel.getCapital() +"/"+ carmodel.getBrand();
			String imagepath = UploadUtil.uploadFile(data, dataFileName, folder);
			carmodel.setModelimage(imagepath);
			
			daoSupport.insert("es_carmodels", carmodel);
		
			obj.put("result", 1);
			obj.put("message", "添加车型成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		
		return obj;
	}

	@Override
	public boolean isExist(CarModel carmodel) {
		try {
			carmodel.setCapital(carmodel.getCapital().toUpperCase());
			carmodel.setModel(carmodel.getSales_name());
			String sql = "select name, logo from es_brand where brand_id=?";
			List<String> brandlist = daoSupport.queryForList(sql, carmodel.getBrand_id());
			if(brandlist.size() > 0){
				JSONObject brandObj = JSONObject.fromObject(brandlist.get(0));
				carmodel.setBrand(brandObj.getString("name"));
				carmodel.setBrandimage(brandObj.getString("logo"));
			}
			sql = "select count(*) from es_carmodels where brand_id=? and series=? and nk=? and sales_name=?";
			int count = daoSupport.queryForInt(sql, carmodel.getBrand_id(), carmodel.getSeries(), carmodel.getNk(), carmodel.getSales_name());
			if(count > 0){
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public CarModel getCarmodel(String id) {
		try {
			//String sql = "SELECT id, capital, brand, brand_id, TYPE, series, brandimage, IFNULL(model,'') model, modelimage, nk, sales_name, seats, discharge, price, IFNULL(repairinterval,0) repairinterval, IFNULL(repairintervaltime, 0) repairintervaltime FROM es_carmodels where id=?";
			String sql = "select * from es_carmodels where id=?";
			List<CarModel> list = daoSupport.queryForList(sql, CarModel.class, id);
			
			if(list.size() > 0){
				return list.get(0);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject editSave(CarModel carmodel, String carmodel_type, File data, String dataFileName) throws Exception {
		JSONObject obj = new JSONObject();
		String new_modelImagePath = "";
		String old_modelImagePath = "";
		try {
			if("1".equals(carmodel_type)){
				carmodel.setType("国产");
			}else if("2".equals(carmodel_type)){
				carmodel.setType("进口");
			}
			if(data != null){
				old_modelImagePath = SystemSetting.getFile_path() + carmodel.getModelimage().replace(EopSetting.FILE_STORE_PREFIX+"/files", "");
				String folder = SystemSetting.carmodel_image_savedir +"/"+ carmodel.getCapital() +"/"+ carmodel.getBrand();
				new_modelImagePath = UploadUtil.uploadFile(data, dataFileName, folder);
				carmodel.setModelimage(new_modelImagePath);//设置上传的新图片路径
				new_modelImagePath = SystemSetting.getFile_path() + new_modelImagePath.replace(EopSetting.FILE_STORE_PREFIX+"/files", "");
			}
			daoSupport.update("es_carmodels", carmodel, "id="+carmodel.getId());
			
			FileUtil.delete(old_modelImagePath);//修改成功，删除旧的图片
			
			obj.put("result", 1);
			obj.put("message", "修改成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			FileUtil.delete(new_modelImagePath);//修改失败，删除新上传的图片
			throw e;
		}
		return obj;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject delete(Integer[] id) throws Exception {
		JSONObject obj = new JSONObject();
		try {
			String ids = StringUtil.arrayToString(id, ",");
			String sql = "select modelimage from es_carmodels where id in ("+ ids +")";
			List modelimageList = daoSupport.queryForList(sql);
			
			sql = "delete from es_carmodels where id in ("+ ids +")";
			daoSupport.execute(sql);
			
			//删除记录成功，删除车型图片
			if(modelimageList.size() > 0){
				JSONArray fsArray = JSONArray.fromObject(modelimageList);
				for(int i=0; i<fsArray.size(); i++){
					JSONObject tmp = fsArray.getJSONObject(i);
					String fsPath = tmp.getString("modelimage");
					String imagePath = SystemSetting.getFile_path() + fsPath.replace(EopSetting.FILE_STORE_PREFIX+"/files", "");
					FileUtil.delete(imagePath);
				}
			}
			
			obj.put("result", 1);
			obj.put("message", "删除记录成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	public Page getRepairItemListByPage(Map keyMap, int page, int pageSize, String sort, String order) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT id, itemname, sort, IF(is_necessary, '是', '否') is_necessary FROM es_repair_items WHERE 1=1");
		
		String keyword = (String) keyMap.get("keyword");
		
		if(keyword!=null){			
			sql.append(" and (itemname like '%"+keyword+"%')");
//			sql.append(" or sales_name like '%"+keyword+"%')");
		}
		
		sql.append(" order by "+sort+" "+order);
		
		Page webPage = this.baseDaoSupport.queryForPage(sql.toString(), page, pageSize);
		
		return webPage;
	}

	@Override
	public boolean isExistRepairItem(String item_name) {
		try {
			String sql = "select count(*) from es_repair_items where itemname='"+ item_name +"'";
			int count = daoSupport.queryForInt(sql);
			if(count > 0){
				return true;
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject addRepairItem(RepairItem repairItem) {
		JSONObject obj = new JSONObject();
		try {
			daoSupport.insert("es_repair_items", repairItem);
			
			obj.put("result", 1);
			obj.put("message", "添加成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	public RepairItem getRepairItem(String id) {
		try {
			String sql = "select * from es_repair_items where id=?";
			return (RepairItem) daoSupport.queryForObject(sql, RepairItem.class, id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject editRepairItemSave(RepairItem repairItem) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "update es_repair_items set itemname=?, is_necessary=?, sort=? where id=?";
			daoSupport.execute(sql, repairItem.getItemname(), repairItem.getIs_necessary(), repairItem.getSort(), repairItem.getId());
			
			obj.put("result", 1);
			obj.put("message", "修改成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject deleteRepairItem(Integer[] id) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "delete from es_repair_items where id in ("+ StringUtil.arrayToString(id, ",") +")";
			daoSupport.execute(sql);
			
			obj.put("result", 1);
			obj.put("message", "删除成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}
	
	public Integer getRepairItemId(String itemname){
		String sql = "select id from es_repair_items where itemname=?";
		return daoSupport.queryForInt(sql, itemname);
	}
	
	public long[] getIntervalAndIntervalTime(String temp){
		long[] array = new long[2];
		if(!StringUtil.isEmpty(temp)){
			if(temp.indexOf(";") > 0){
				String[] tempArray = temp.split(";");
				array[0] = Long.valueOf(tempArray[0]);
				array[1] = Long.valueOf(tempArray[1]);
			}
		}
		return array;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject importCarRepairItemExcelData(File data, String dataFileName) {
		JSONObject obj = new JSONObject();
		try {
			Map<String, Integer> map = new HashMap<>();
			List<Brand> brandList = getCarBrandSelectList();
			if(brandList.size() > 0){
				for(Brand b : brandList){
					map.put(b.getName(), b.getBrand_id());
				}
			}
			ExcelUtils eu = new ExcelUtils();
			List<List<String>> list = eu.read(new FileInputStream(data), ExcelUtils.isExcel2003(dataFileName));
			if(list.size() > 0){
				int oilId = getRepairItemId("机油");
				int oilfilterId = getRepairItemId("机油滤清器");
				int airfilterId = getRepairItemId("空气滤清器");
				int fuelfilterId = getRepairItemId("燃油滤清器");
				int cabinfilterId = getRepairItemId("空调滤清器");
				int sparkplugId = getRepairItemId("火花塞");
				int brakeoilId = getRepairItemId("刹车油");
				int transoilId = getRepairItemId("变速箱油");
				int coolingliquidId = getRepairItemId("防冻冷却液");
				int cvvtId = getRepairItemId("正时系统");
				
				List<String> titleObject = list.get(0);
				int capitalIndex = titleObject.indexOf("首字母");
				int brandIndex = titleObject.indexOf("品牌");
				int seriesIndex = titleObject.indexOf("车系");
				int dischargeIndex = titleObject.indexOf("排量");
				int nkIndex = titleObject.indexOf("年份");
				
				int oilIndex = titleObject.indexOf("更换机油");
				int oilfilterIndex = titleObject.indexOf("更换机油滤清器");
				int airfilterIndex = titleObject.indexOf("更换空气滤清器");
				int fuelfilterIndex = titleObject.indexOf("更换燃油滤清器");
				int cabinfilterIndex = titleObject.indexOf("更换空调滤清器");
				int sparkplugIndex = titleObject.indexOf("更换火花塞");
				int brakeoilIndex = titleObject.indexOf("更换刹车油");
				int manualtransoilIndex = titleObject.indexOf("更换手动挡变速箱油");
				int autotransoilIndex = titleObject.indexOf("更换自动挡变速箱油");
				int coolingliquidIndex = titleObject.indexOf("更换防冻冷却液");
				int cvvtIndex = titleObject.indexOf("更换正时系统");
				
				if(capitalIndex >= 0 && brandIndex >= 0 && seriesIndex >= 0 && nkIndex >= 0 && dischargeIndex >= 0 && oilIndex >= 0 && oilfilterIndex >= 0){
					String sql = "select id from es_carmodels where capital=? and brand=? and series=? and nk=? and discharge=?";
					for(int i=1; i<list.size(); i++){
						List<String> tempObj = list.get(i);
						String capital= tempObj.get(capitalIndex);
						String brand = tempObj.get(brandIndex);
						String series = tempObj.get(seriesIndex);
						String nk = tempObj.get(nkIndex);
						String discharge = tempObj.get(dischargeIndex);
						long[] oilArray = getIntervalAndIntervalTime(tempObj.get(oilIndex));
						long[] oilfilterArray = getIntervalAndIntervalTime(tempObj.get(oilfilterIndex));
						long[] airfilterArray = getIntervalAndIntervalTime(tempObj.get(airfilterIndex));
						long[] fuelfilterArray = getIntervalAndIntervalTime(tempObj.get(fuelfilterIndex));
						long[] cabinfilterArray = getIntervalAndIntervalTime(tempObj.get(cabinfilterIndex));
						long[] sparkplugArray = getIntervalAndIntervalTime(tempObj.get(sparkplugIndex));
						long[] brakeoilArray = getIntervalAndIntervalTime(tempObj.get(brakeoilIndex));
						long[] transoilArray = new long[2];
						long[] coolingliquidArray = getIntervalAndIntervalTime(tempObj.get(coolingliquidIndex));
						long[] cvvtArray = getIntervalAndIntervalTime(tempObj.get(cvvtIndex));
						List carmodellist = daoSupport.queryForList(sql, capital, brand, series, nk, discharge);
                        if(carmodellist.size() > 0){
                        	JSONArray carmodelidArray = JSONArray.fromObject(carmodellist);
    						List<Integer> idList = new ArrayList<>();
    						for(int a=0; a<carmodelidArray.size(); a++){
    							idList.add(carmodelidArray.getJSONObject(a).getInt("id"));
    						}
    						Object[] idArray = idList.toArray();
    						String carmodelIds = StringUtil.arrayToString(idArray, ",");
    						String sql0 = "update es_carmodels set repairinterval=?, repairintervaltime=? where id in("+ carmodelIds +")";
    						daoSupport.execute(sql0, oilArray[0], oilArray[1]);
    			            String sql1 = "select count(*) from es_carmodel_repair_items where carmodel_id=? and repair_item_id=?";
    			            String sql2 = "update es_carmodel_repair_items set repair_interval=?, repair_interval_time=? where carmodel_id=? and repair_item_id=?";
    						String sql3 = "insert into es_carmodel_repair_items set repair_interval=?, repair_interval_time=?, carmodel_id=?, repair_item_id=?";
    						
    						for(int j=0; j<idArray.length; j++){
    							CarModel carmodel = getCarmodel(idArray[j]+"");
    							if(carmodel.getGearboxtype() == 1){
    								transoilArray = getIntervalAndIntervalTime(tempObj.get(manualtransoilIndex));
    							}else{
    								transoilArray = getIntervalAndIntervalTime(tempObj.get(autotransoilIndex));
    							}
    							
    							int oilCount = daoSupport.queryForInt(sql1, idArray[j], oilId);
    							int oilfilterCount = daoSupport.queryForInt(sql1, idArray[j], oilfilterId);
    							int airfilterCount = daoSupport.queryForInt(sql1, idArray[j], airfilterId);
    							int fuelfilterCount = daoSupport.queryForInt(sql1, idArray[j], fuelfilterId);
    							int cabinfilterCount = daoSupport.queryForInt(sql1, idArray[j], cabinfilterId);
    							int sparkplugCount = daoSupport.queryForInt(sql1, idArray[j], sparkplugId);
    							int brakeoilCount = daoSupport.queryForInt(sql1, idArray[j], brakeoilId);
    							int transoilCount = daoSupport.queryForInt(sql1, idArray[j], transoilId);
    							int coolingliquidCount = daoSupport.queryForInt(sql1, idArray[j], coolingliquidId);
    							int cvvtCount = daoSupport.queryForInt(sql1, idArray[j], cvvtId);

    							if(oilCount > 0){
    								daoSupport.execute(sql2, oilArray[0], oilArray[1], idArray[j], oilId);
    							}else{
    								daoSupport.execute(sql3, oilArray[0], oilArray[1], idArray[j], oilId);
    							}
    							if(oilfilterCount > 0){
    								daoSupport.execute(sql2, oilfilterArray[0], oilfilterArray[1], idArray[j], oilfilterId);
    							}else{
    								daoSupport.execute(sql3, oilfilterArray[0], oilfilterArray[1], idArray[j], oilfilterId);
    							}
    							if(airfilterCount > 0){
    								daoSupport.execute(sql2, airfilterArray[0], airfilterArray[1], idArray[j], airfilterId);
    							}else{
    								daoSupport.execute(sql3, airfilterArray[0], airfilterArray[1], idArray[j], airfilterId);
    							}
    							if(fuelfilterCount > 0){
    								daoSupport.execute(sql2, fuelfilterArray[0], fuelfilterArray[1], idArray[j], fuelfilterId);
    							}else{
    								daoSupport.execute(sql3, fuelfilterArray[0], fuelfilterArray[1], idArray[j], fuelfilterId);
    							}
    							if(cabinfilterCount > 0){
    								daoSupport.execute(sql2, cabinfilterArray[0], cabinfilterArray[1], idArray[j], cabinfilterId);
    							}else{
    								daoSupport.execute(sql3, cabinfilterArray[0], cabinfilterArray[1], idArray[j], cabinfilterId);
    							}
    							if(sparkplugCount > 0){
    								daoSupport.execute(sql2, sparkplugArray[0], sparkplugArray[1], idArray[j], sparkplugId);
    							}else{
    								daoSupport.execute(sql3, sparkplugArray[0], sparkplugArray[1], idArray[j], sparkplugId);
    							}
    							if(brakeoilCount > 0){
    								daoSupport.execute(sql2, brakeoilArray[0], brakeoilArray[1], idArray[j], brakeoilId);
    							}else{
    								daoSupport.execute(sql3, brakeoilArray[0], brakeoilArray[1], idArray[j], brakeoilId);
    							}
    							if(transoilCount > 0){
    								daoSupport.execute(sql2, transoilArray[0], transoilArray[1], idArray[j], transoilId);
    							}else{
    								daoSupport.execute(sql3, transoilArray[0], transoilArray[1], idArray[j], transoilId);
    							}
    							if(coolingliquidCount > 0){
    								daoSupport.execute(sql2, coolingliquidArray[0], coolingliquidArray[1], idArray[j], coolingliquidId);
    							}else{
    								daoSupport.execute(sql3, coolingliquidArray[0], coolingliquidArray[1], idArray[j], coolingliquidId);
    							}
    							if(cvvtCount > 0){
    								daoSupport.execute(sql2, cvvtArray[0], cvvtArray[1], idArray[j], cvvtId);
    							}else{
    								daoSupport.execute(sql3, cvvtArray[0], cvvtArray[1], idArray[j], cvvtId);
    							}
    						}
                        }
					}
					
					obj.put("result", 1);
					obj.put("message", "导入成功");
				}else{
					obj.put("result", 0);
					obj.put("message", "您选择的excel文件内容不是车型保养相关数据，请检查后重新导入");
				}
			}else{
				obj.put("result", 0);
				obj.put("message", "文件内容为空，请检查");
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	public Page getCarmodelRepairItemListByPage(Map keyMap, int page, int pageSize, String sort, String order) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT t1.id, t2.`capital`, t2.`brand`, t2.`type`, t2.`series`, t2.`nk`, t2.`sales_name`, IF(gearboxtype=1, '手动', '自动') gearboxtype, t3.`itemname`, t1.`repair_interval`, t1.`repair_interval_time` FROM es_carmodel_repair_items t1, es_carmodels t2, es_repair_items t3 WHERE t1.`carmodel_id`=t2.`id` AND t1.`repair_item_id`=t3.`id`");
		
		Integer stype = (Integer) keyMap.get("stype");
		String keyword = (String) keyMap.get("keyword");
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql.append(" and (brand like '%"+keyword+"%'");
				sql.append(" or sales_name like '%"+keyword+"%')");
			}
		}
		
		sql.append(" order by "+sort+" "+order);
		
		Page webPage = this.baseDaoSupport.queryForPage(sql.toString(), page, pageSize);
		
		return webPage;
	}

	@Override
	public List<String> getCarmodelRelateList(String key, Object... args) {
		String sql = "";
		List<String> resultList = new ArrayList<>();
		if("brand_id".equals(key)){
			sql = "select distinct(type) from es_carmodels where brand_id=?";
			resultList = daoSupport.queryForList(sql, args[0]);
		}
		if("type".equals(key)){
			sql = "select distinct(series) from es_carmodels where brand_id=? and type=?";
			resultList = daoSupport.queryForList(sql, args[0], args[1]);
		}
		if("series".equals(key)){
			sql = "select distinct(nk) from es_carmodels where brand_id=? and type=? and series=?";
			resultList = daoSupport.queryForList(sql, args[0], args[1], args[2]);
		}
		if("nk".equals(key)){
			sql = "SELECT DISTINCT(sales_name) FROM es_carmodels WHERE brand_id=? AND TYPE=? AND series=? AND nk=?";
			resultList = daoSupport.queryForList(sql, args[0], args[1], args[2], args[3]);
		}
		if("sales_name".equals(key)){
			sql = "SELECT id FROM es_carmodels WHERE brand_id=? AND TYPE=? AND series=? AND nk=? AND sales_name=?";
			resultList = daoSupport.queryForList(sql, args[0], args[1], args[2], args[3], args[4]);
		}
		return resultList;
	}

	@Override
	public List<CarmodelRepairItem> getCarmodelRepairItemList() {
		String sql = "SELECT id, itemname FROM es_repair_items ORDER BY sort";
		return daoSupport.queryForList(sql, CarmodelRepairItem.class);
	}

	@Override
	public boolean isExistCarmodelRepairItem(CarmodelRepairItem carmodelRepairItem) {
		String sql = "select count(*) from es_carmodel_repair_items where carmodel_id=? and repair_item_id=?";
		int count = daoSupport.queryForInt(sql, carmodelRepairItem.getCarmodel_id(), carmodelRepairItem.getRepair_item_id());
		if(count > 0){
			return true;
		}
		return false;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject addCarmodelRepairItem(CarmodelRepairItem carmodelRepairItem) {
		JSONObject obj = new JSONObject();
		try {
			daoSupport.insert("es_carmodel_repair_items", carmodelRepairItem);
			
			obj.put("result", 1);
			obj.put("message", "添加成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	public CarmodelRepairItem getCarmodelRepairItem(String id) {
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT t1.id, t2.`brand`, t2.`type`, t2.`series`, t2.`nk`, t2.`sales_name`, t3.`itemname`, t1.`repair_interval`, t1.`repair_interval_time` ");
			sql.append("FROM es_carmodel_repair_items t1, es_carmodels t2, es_repair_items t3 ");
			sql.append("WHERE t1.`carmodel_id`=t2.`id` AND t1.`repair_item_id`=t3.`id` ");
			sql.append("AND t1.id=?");
			List<CarmodelRepairItem> list = daoSupport.queryForList(sql.toString(), CarmodelRepairItem.class, id);
			
			if(list.size() > 0){
				return list.get(0);
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject editCarmodelRepairItemSave(CarmodelRepairItem carmodelRepairItem) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "update es_carmodel_repair_items set repair_interval=?, repair_interval_time=? where id=?";
			daoSupport.execute(sql, carmodelRepairItem.getRepair_interval(), carmodelRepairItem.getRepair_interval_time(), carmodelRepairItem.getId());
			
			obj.put("result", 1);
			obj.put("message", "修改成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}

	@Override
	@Transactional(propagation=Propagation.REQUIRED)
	public JSONObject deleteCarmodelRepairItem(Integer[] id) {
		JSONObject obj = new JSONObject();
		try {
			String sql = "delete from es_carmodel_repair_items where id in ("+ StringUtil.arrayToString(id, ",") +")";
			daoSupport.execute(sql);
			
			obj.put("result", 1);
			obj.put("message", "删除成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			throw e;
		}
		return obj;
	}
}
