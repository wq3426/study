package com.dhl.tools.exception;

/**
 * 导入数据异常
 * 
 * @author sunjitao
 *
 */
public class ImportDataException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7580180171047384626L;

	public ImportDataException(String message) {
		super(message);
	}
}
