package com.enation.app.dataanalysis.enums;

import com.enation.app.dataanalysis.exception.DataAnalysisException;

public enum BusinessEnum {
	
	油耗排名("consumption"),
	收益排名("income");
	
	private String businessCode;//业务编码
	BusinessEnum(String businessCode){
		this.businessCode = businessCode;
	}
	
	/**
	 * @description GETTER 获取业务编码
	 * @date 2016年9月8日 下午4:34:52
	 * @return String
	 */
	public String getBusinessCode() {
		return businessCode;
	}
	
	/**
	 * @description 
	 * @date 2016年9月8日 下午5:18:18
	 * @param businessCode
	 * @return BusinessEnum
	 * @throws DataAnalysisException  
	 */
	public static BusinessEnum getEnumByBusinessCode(String businessCode) throws DataAnalysisException {
		switch (businessCode) {
		case "consumption":
			return BusinessEnum.油耗排名;
		case "income":
			return BusinessEnum.收益排名;
		default:
			throw new DataAnalysisException("业务编码错误");
		}
	} 
	
}
