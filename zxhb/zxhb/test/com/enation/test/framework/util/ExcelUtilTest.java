package com.enation.test.framework.util;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.enation.app.shop.core.model.CarModel;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.util.ExcelUtil;
import com.enation.framework.util.ExcelUtils;

public class ExcelUtilTest {
	
	@Test
	public void test(){
		ExcelUtil excelUtil = new ExcelUtil();
		excelUtil.openModal("d:/access.xls");
		excelUtil.writeStringToCell(1, 1, "test");
		excelUtil.writeToFile("d:/a.xls");
	}
	
	public static void main(String[] args) {
//		String fileName = "G:/车型表.xlsx";
//		
//		ExcelUtils eu = new ExcelUtils();
//		List<List<String>> list = eu.read(fileName);
//        List<String> titleObject = list.get(0);
//		int captialIndex = titleObject.indexOf("首字母");
//		int brandIndex = titleObject.indexOf("品牌");
//		int brandimageIndex = titleObject.indexOf("Logo");
//		int modelIndex = titleObject.indexOf("车型名称");
//		int modelimageIndex = titleObject.indexOf("图片路径");
//		int seriesIndex = titleObject.indexOf("车系");
//		int nkIndex = titleObject.indexOf("年款");
//		int salesnameIndex = titleObject.indexOf("车型名称");
//		int seatsIndex = titleObject.indexOf("座位数(个)");
//		int dischargeIndex = titleObject.indexOf("排量");
//		int priceIndex = titleObject.indexOf("厂商指导价(元)");
//		
//		List<CarModel> carmodelList = new ArrayList<CarModel>();
//		for(int i=1; i<2; i++){
//			CarModel carmodel = new CarModel();
//			List<String> tempObj = list.get(i);
//			carmodel.setCapital(tempObj.get(captialIndex));
//			carmodel.setBrand(tempObj.get(brandIndex));
//			String brandImage = tempObj.get(brandimageIndex).replace("\"", "'").replace("\\", "/");
//			brandImage = brandImage.substring(brandImage.indexOf("a/")+1, brandImage.lastIndexOf("'"));
//			carmodel.setBrandimage(brandImage);
//			carmodel.setModel(tempObj.get(modelIndex));
//			String modelImage = tempObj.get(modelimageIndex).replace("\"", "'").replace("\\", "/");
//			modelImage = modelImage.substring(modelImage.indexOf("a/")+1, modelImage.lastIndexOf("'"));
//			carmodel.setModelimage(modelImage);
//			carmodel.setSeries(tempObj.get(seriesIndex));
//			carmodel.setNk(tempObj.get(nkIndex));
//			carmodel.setSales_name(tempObj.get(salesnameIndex));
//			carmodel.setSeats(Integer.valueOf(tempObj.get(seatsIndex)));
//			carmodel.setDischarge(Double.valueOf(tempObj.get(dischargeIndex).replace("L", "")));
//			carmodel.setPrice(Double.valueOf(tempObj.get(priceIndex).replace("万", "")));
//			
//			System.out.println(carmodel);
//		}
		
//		String s = "=HYPERLINK('.\\car_show_image_qichezhijia\\M\\马自达\\马自达.jpg')".replace("\\", "/");
//		s = EopSetting.FILE_STORE_PREFIX + "/files/" + s.substring(s.indexOf("a/")+2, s.lastIndexOf("'"));
//		System.out.println("s   "+s);
	}
}
