package com.dhl.tools.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.dhl.tools.domain.CargoLocation;
import com.dhl.tools.domain.CargoLocationData;
import com.dhl.tools.domain.Material;

public class XmlUtil {

	/**
	 * 拼接导入货位xml
	 * 
	 * @param cargoLocationList
	 * @return
	 */
	public static String getLocationStrByList(List<CargoLocation> cargoLocationList) {

		Element rootElement = DocumentHelper.createElement("cargoLocationList");

		for (CargoLocation cargoLocation : cargoLocationList) {

			Element element = rootElement.addElement("cargoLocation");

			element.addAttribute("code", cargoLocation.getCode());
			element.addAttribute("wareHouseId", cargoLocation.getWareHouseId().toString());
			element.addAttribute("typeId", cargoLocation.getTypeId().toString());
			element.addAttribute("createBy", cargoLocation.getCreateBy());
			element.addAttribute("batchNum", cargoLocation.getBatchNum());
			element.addAttribute("batchSerialNumber", cargoLocation.getBatchSerialNumber().toString());
			element.addAttribute("createDate",
					DateFormatUtils.format(cargoLocation.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
		}
		return rootElement.asXML();
	}

	/**
	 * 拼接更新货位xml
	 * 
	 * @param cargoLocationList
	 * @return
	 */
	public static String getUpdateLocationStrByList(List<CargoLocation> cargoLocationList) {

		Element rootElement = DocumentHelper.createElement("cargoLocationList");

		for (CargoLocation cargoLocation : cargoLocationList) {

			Element element = rootElement.addElement("cargoLocation");

			element.addAttribute("id", cargoLocation.getId().toString());
			element.addAttribute("typeId", cargoLocation.getTypeId().toString());
			element.addAttribute("batchNum", cargoLocation.getBatchNum());
			element.addAttribute("batchSerialNumber", cargoLocation.getBatchSerialNumber().toString());
			element.addAttribute("createDate",
					DateFormatUtils.format(cargoLocation.getCreateDate(), "yyyy-MM-dd HH:mm:ss"));
		}
		return rootElement.asXML();
	}

	/**
	 * 拼接货位拆分数据xml
	 * 
	 * @param cargoLocationDataList
	 * @return
	 */
	public static String getLocationDataStrByList(List<CargoLocationData> cargoLocationDataList) {

		Element rootElement = DocumentHelper.createElement("cargoLocationDataList");

		for (CargoLocationData cargoLocationData : cargoLocationDataList) {

			Element element = rootElement.addElement("data");

			element.addAttribute("id", cargoLocationData.getId());
			element.addAttribute("code", cargoLocationData.getCode());
			element.addAttribute("cacheCode", cargoLocationData.getCacheCode());
			element.addAttribute("parentId", cargoLocationData.getParentId());
			element.addAttribute("wareHouseId", cargoLocationData.getWareHouseId().toString());
			element.addAttribute("configId", cargoLocationData.getConfigId().toString());
			element.addAttribute("configType", cargoLocationData.getConfigType().toString());
		}
		return rootElement.asXML();
	}

	/**
	 * 拼接更新货位参数的xml
	 * 
	 * @param cargoLocationDataList
	 * @return
	 */
	public static String getUpdateLocationDataStrByList(List<CargoLocationData> cargoLocationDataList) {

		Element rootElement = DocumentHelper.createElement("cargoLocationDataList");

		for (CargoLocationData cargoLocationData : cargoLocationDataList) {

			Element element = rootElement.addElement("data");

			element.addAttribute("id", cargoLocationData.getId());
			element.addAttribute("primaryPriority", cargoLocationData.getPrimaryPriority() == null ? null
					: cargoLocationData.getPrimaryPriority().toString());
			element.addAttribute("standbyPriority", cargoLocationData.getStandbyPriority() == null ? null
					: cargoLocationData.getStandbyPriority().toString());
			element.addAttribute("distance",
					cargoLocationData.getDistance() == null ? null : cargoLocationData.getDistance().toString());
			element.addAttribute("pickTool",
					cargoLocationData.getPickTool() == null ? null : cargoLocationData.getPickTool().toString());
		}
		return rootElement.asXML();
	}

	/**
	 * 拼接更新货位分值的xml
	 * 
	 * @param cargoLocationList
	 * @return
	 */
	public static String getUpdateScoreLocationStrByList(List<CargoLocation> cargoLocationList) {

		Element rootElement = DocumentHelper.createElement("cargoLocationList");

		for (CargoLocation cargoLocation : cargoLocationList) {

			Element element = rootElement.addElement("cargoLocation");

			element.addAttribute("id", cargoLocation.getId().toString());
			element.addAttribute("score", cargoLocation.getScore().toString());
		}
		return rootElement.asXML();
	}

	/**
	 * 拼接导入物料的xml
	 * 
	 * @param materialList
	 * @return
	 */
	public static String getInsertMaterialStrByList(List<Material> materialList) {

		Element rootElement = DocumentHelper.createElement("materialList");

		for (Material material : materialList) {

			Element element = rootElement.addElement("material");

			element.addAttribute("code", material.getCode());
			element.addAttribute("lengths", material.getLength().toString());
			element.addAttribute("width", material.getWidth().toString());
			element.addAttribute("height", material.getHeight().toString());
			element.addAttribute("maxStore", material.getMaxStore().toString());
			element.addAttribute("locationId",
					material.getCargoLocationId() == null ? null : String.valueOf(material.getCargoLocationId()));
			element.addAttribute("locationCode", material.getCargoLocationCode());
			element.addAttribute("typeId",
					material.getCargoLocationTypeId() == null ? null : material.getCargoLocationTypeId().toString());
			element.addAttribute("wareHouseId", material.getWareHouseId().toString());
			element.addAttribute("batchNum", material.getBatchNum());
			element.addAttribute("serialNumber", material.getBatchSerialNumber().toString());
			element.addAttribute("faceUp", material.getFaceUp().toString());
			element.addAttribute("createBy", material.getCreateBy());
		}
		return rootElement.asXML();
	}

	/**
	 * 拼接物料推荐货位类型的xml
	 * 
	 * @param cargoLocationList
	 * @return
	 */
	public static String getMaterialRecommendStrByList(List<Material> materialList) {

		Element rootElement = DocumentHelper.createElement("materialList");

		for (Material material : materialList) {

			Element element = rootElement.addElement("material");

			element.addAttribute("id", material.getId().toString());
			element.addAttribute("optimalType", replaceSingleQuotes(material.getOptimalLocationType()));
			element.addAttribute("optimalPlacement", material.getOptimalPlacement());
			element.addAttribute("recommendType", replaceSingleQuotes(material.getRecommendedLocationType()));
			element.addAttribute("recommendPlacement", material.getRecommendedPlacement());
			element.addAttribute("allType", replaceSingleQuotes(material.getAllAppropriateLibraryTypes()));
			element.addAttribute("allExtendType", replaceSingleQuotes(material.getExtendAllAppropriateLibraryTypes()));
		}
		return rootElement.asXML();
	}

	/**
	 * 拼接物料推荐货位的xml
	 * 
	 * @param materialList
	 * @return
	 */
	public static String getMaterialRecommendCargoLocationStrByList(List<Material> materialList) {

		Element rootElement = DocumentHelper.createElement("materialList");

		for (Material material : materialList) {

			Element element = rootElement.addElement("material");

			element.addAttribute("id", material.getId().toString());
			element.addAttribute("locationId", material.getRecommendedLocationId() == null ? null
					: material.getRecommendedLocationId().toString());
			element.addAttribute("locationCode", material.getRecommendedLocationCode());
			element.addAttribute("locationScore", material.getRecommendedLocationScore() == null ? null
					: material.getRecommendedLocationScore().toString());
		}
		return rootElement.asXML();
	}

	/**
	 * 拼接物料导入拣货频率的xml
	 * 
	 * @param materialList
	 * @return
	 */
	public static String getMaterialPickToolFreqStrByList(List<Material> materialList) {

		Element rootElement = DocumentHelper.createElement("materialList");

		for (Material material : materialList) {

			Element element = rootElement.addElement("material");

			element.addAttribute("id", material.getId().toString());
			element.addAttribute("locationId",
					material.getCargoLocationId() == null ? null : material.getCargoLocationId().toString());
			element.addAttribute("locationCode", material.getCargoLocationCode());
			element.addAttribute("locationScore",
					material.getCargoLocationScore() == null ? null : material.getCargoLocationScore().toString());
			element.addAttribute("typeId",
					material.getCargoLocationTypeId() == null ? null : material.getCargoLocationTypeId().toString());
			element.addAttribute("pickUpRate", material.getPickUpRate().toString());
			element.addAttribute("serialNumber", material.getBatchSerialNumber().toString());
		}
		return rootElement.asXML();
	}

	/**
	 * 拼接物料更新库存的xml
	 * 
	 * @param materialList
	 * @return
	 */
	public static String getMaterialStockStrByList(List<Material> materialList) {

		Element rootElement = DocumentHelper.createElement("materialList");

		for (Material material : materialList) {

			Element element = rootElement.addElement("material");

			element.addAttribute("id", material.getId().toString());
			element.addAttribute("locationId",
					material.getCargoLocationId() == null ? null : material.getCargoLocationId().toString());
			element.addAttribute("typeId",
					material.getCargoLocationTypeId() == null ? null : material.getCargoLocationTypeId().toString());
			element.addAttribute("serialNumber", material.getBatchSerialNumber().toString());
		}
		return rootElement.asXML();
	}

	/**
	 * 替换单引号为双引号
	 * 
	 * @param str
	 * @return
	 */
	private static String replaceSingleQuotes(String str) {
		if (StringUtils.isNoneBlank(str)) {
			return str.replaceAll("'", "''");
		}
		return null;
	}
}
