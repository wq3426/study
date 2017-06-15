package com.enation.app.dataanalysis.exception;

/**
 * @Description 数据分析模块自定义异常
 *
 * @createTime 2016年9月8日 下午7:21:33
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public class DataAnalysisException extends Exception{

	private static final long serialVersionUID = 1L;
	
	/**
	 * @date 2016年9月8日 下午7:22:50
	 * 无参构造函数
	 */
	public DataAnalysisException(){};
	
	/**
	 * @date 2016年9月8日 下午7:23:12
	 * @param msg
	 */
	public DataAnalysisException(String msg){
		super(msg);
	}
}
