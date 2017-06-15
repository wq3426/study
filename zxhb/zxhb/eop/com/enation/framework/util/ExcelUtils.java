package com.enation.framework.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * @Description Excel导入导出
 *
 * @createTime 2016年8月18日 下午2:30:23
 *
 * @author <a href="mailto:wangqiang@trans-it.cn">wangqiang</a>
 */
public class ExcelUtils {

	private int totalRows = 0; //总行数

	private int totalCells = 0; //总列数

	private String errorInfo; //错误信息

    /**
     * @description 得到总行数
     * @date 2016年8月18日 下午2:33:34
     * @return
     */
	public int getTotalRows() {
		return totalRows;
	}

	/**
	 * @description 得到总列数
	 * @date 2016年8月18日 下午2:34:04
	 * @return
	 */
	public int getTotalCells() {
		return totalCells;
	}

    /**
     * @description 得到错误信息
     * @date 2016年8月18日 下午2:34:25
     * @return
     */
	public String getErrorInfo() {

		return errorInfo;

	}

    /**
     * @description 验证excel文件
     * @date 2016年8月18日 下午2:34:42
     * @param filePath
     * @return
     */
	public boolean validateExcel(String filePath) {

		/** 检查文件名是否为空或者是否是Excel格式的文件 */
		if (filePath == null || !(isExcel2003(filePath) || isExcel2007(filePath))) {
			errorInfo = "文件名不是excel格式";
			return false;
		}

		/** 检查文件是否存在 */
		File file = new File(filePath);

		if (file == null || !file.exists()) {
			errorInfo = "文件不存在";
			return false;
		}

		return true;
	}
	
	/**
	 * @description 获取工作簿数量
	 * @date 2016年9月1日 上午10:19:08
	 * @param inputStream
	 * @param isExcel2003
	 * @return
	 */
	public int getSheetNumber(InputStream inputStream, boolean isExcel2003){
		int count = 0;
		try {
			/** 根据版本选择创建Workbook的方式 */
			Workbook wb = null;

			if (isExcel2003) {
				wb = new HSSFWorkbook(inputStream);
			} else {
				wb = new XSSFWorkbook(inputStream);
			}
			count = wb.getNumberOfSheets();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * @description 根据列的值获取所在行的索引
	 * @date 2016年9月1日 下午4:16:22
	 * @return
	 */
	public int getRowIndexOfCellvalue(InputStream inputStream, boolean isExcel2003, int sheetIndex, String cellStringValue){
		int rowIndex = 0;
		try {
			/** 根据版本选择创建Workbook的方式 */
			Workbook wb = null;

			if (isExcel2003) {
				wb = new HSSFWorkbook(inputStream);
			} else {
				wb = new XSSFWorkbook(inputStream);
			}
			
			/** 得到第一个shell */
			Sheet sheet = wb.getSheetAt(sheetIndex);		

			/** 得到Excel的行数 */
			this.totalRows = sheet.getPhysicalNumberOfRows();

			/** 得到Excel的列数 */
			if (this.totalRows >= 1 && sheet.getRow(0) != null) {
				this.totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
			}

			/** 循环Excel的行 */
	outer: for (int r = 0; r < this.totalRows; r++) {
				Row row = sheet.getRow(r);

				if (row == null) {
					continue;
				}

				List<String> rowList = new ArrayList<String>();

				/** 循环Excel的列 */
				for (int c = 0; c < this.getTotalCells(); c++) {
					Cell cell = row.getCell(c);

					String cellValue = "";

					if (null != cell) {
						// 以下是判断数据的类型
						switch (cell.getCellType()) {
						    case HSSFCell.CELL_TYPE_NUMERIC: // 数字
							     cellValue = cell.getNumericCellValue() + "";
							     break;

						    case HSSFCell.CELL_TYPE_STRING: // 字符串
							     cellValue = cell.getStringCellValue();
							     break;

						    case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
							     cellValue = cell.getBooleanCellValue() + "";
							     break;

						    case HSSFCell.CELL_TYPE_FORMULA: // 公式
							     cellValue = cell.getCellFormula() + "";
							     break;

						    case HSSFCell.CELL_TYPE_BLANK: // 空值
							     cellValue = "";
							     break;

							     case HSSFCell.CELL_TYPE_ERROR: // 故障
							     cellValue = "非法字符";
							     break;

						    default:
							     cellValue = "未知类型";
							     break;
						}
					}

					if(cellStringValue.equals(cellValue)){
						rowIndex = r;
						break outer;
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rowIndex;
	}

	/**
	 * @description 根据文件名读取excel文件
	 * @date 2016年8月18日 下午2:35:49
	 * @param filePath
	 * @return
	 */
	public List<List<String>> read(String filePath) {

		List<List<String>> dataList = new ArrayList<List<String>>();

		InputStream is = null;

		try {
			/** 验证文件是否合法 */
			if (!validateExcel(filePath)) {
//				System.out.println(errorInfo);
				return null;
			}

			/** 判断文件的类型，是2003还是2007 */
			boolean isExcel2003 = true;
			
			if (isExcel2007(filePath)) {
				isExcel2003 = false;
			}

			/** 调用本类提供的根据流读取的方法 */
			File file = new File(filePath);

			is = new FileInputStream(file);

			dataList = read(is, isExcel2003);

			is.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					is = null;
					e.printStackTrace();
				}
			}
		}

		/** 返回最后读取的结果 */
		return dataList;
	}

	/**
	 * @description 根据流读取Excel文件
	 * @date 2016年8月18日 下午2:37:34
	 * @param inputStream
	 * @param isExcel2003
	 * @return
	 */
	public List<List<String>> read(InputStream inputStream, boolean isExcel2003) {
		List<List<String>> dataList = null;
		
		try {
			/** 根据版本选择创建Workbook的方式 */
			Workbook wb = null;

			if (isExcel2003) {
				wb = new HSSFWorkbook(inputStream);
			} else {
				wb = new XSSFWorkbook(inputStream);
			}
			dataList = read(wb, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dataList;
	}
	
	/**
	 * @description 按sheet索引读取excel文件
	 * @date 2016年9月1日 上午10:24:25
	 * @param inputStream
	 * @param isExcel2003
	 * @param sheetIndex
	 * @return
	 */
	public List<List<String>> read(InputStream inputStream, boolean isExcel2003, int sheetIndex) {
		List<List<String>> dataList = null;
		
		try {
			/** 根据版本选择创建Workbook的方式 */
			Workbook wb = null;

			if (isExcel2003) {
				wb = new HSSFWorkbook(inputStream);
			} else {
				wb = new XSSFWorkbook(inputStream);
			}
			dataList = read(wb, sheetIndex);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return dataList;
	}

    /**
     * @description 读取数据
     * @date 2016年8月18日 下午2:38:22
     * @param wb
     * @return
     */
	private List<List<String>> read(Workbook wb, int sheetIndex) {
		List<List<String>> dataList = new ArrayList<List<String>>();

		/** 得到第一个shell */
		Sheet sheet = wb.getSheetAt(sheetIndex);		

		/** 得到Excel的行数 */
		this.totalRows = sheet.getPhysicalNumberOfRows();

		/** 得到Excel的列数 */
		if (this.totalRows >= 1 && sheet.getRow(0) != null) {
			this.totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
		}

		/** 循环Excel的行 */
		for (int r = 0; r < this.totalRows; r++) {
			Row row = sheet.getRow(r);

			if (row == null) {
				continue;
			}

			List<String> rowList = new ArrayList<String>();

			/** 循环Excel的列 */
			for (int c = 0; c < this.getTotalCells(); c++) {
				Cell cell = row.getCell(c);

				String cellValue = "";

				if (null != cell) {
					// 以下是判断数据的类型
					switch (cell.getCellType()) {
					    case HSSFCell.CELL_TYPE_NUMERIC: // 数字
						     cellValue = cell.getNumericCellValue() + "";
						     break;

					    case HSSFCell.CELL_TYPE_STRING: // 字符串
						     cellValue = cell.getStringCellValue();
						     break;

					    case HSSFCell.CELL_TYPE_BOOLEAN: // Boolean
						     cellValue = cell.getBooleanCellValue() + "";
						     break;

					    case HSSFCell.CELL_TYPE_FORMULA: // 公式
						     cellValue = cell.getCellFormula() + "";
						     break;

					    case HSSFCell.CELL_TYPE_BLANK: // 空值
						     cellValue = "";
						     break;

						     case HSSFCell.CELL_TYPE_ERROR: // 故障
						     cellValue = "非法字符";
						     break;

					    default:
						     cellValue = "未知类型";
						     break;
					}
				}

				rowList.add(cellValue);
			}

			/** 保存第r行的第c列 */
			dataList.add(rowList);
		}

		return dataList;
	}
	
    /**
     * @description 是否是2003的excel，返回true是2003
     * @date 2016年8月18日 下午2:43:01
     * @param filePath
     * @return
     */
	public static boolean isExcel2003(String filePath) {
		
		return filePath.matches("^.+\\.(?i)(xls)$");
	}

    /**
     * @description 是否是2007的excel，返回true是2007
     * @date 2016年8月18日 下午2:43:36
     * @param filePath
     * @return
     */
	public static boolean isExcel2007(String filePath) {

		return filePath.matches("^.+\\.(?i)(xlsx)$");
	}
	
}
