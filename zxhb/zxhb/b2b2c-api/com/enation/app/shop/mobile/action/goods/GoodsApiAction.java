/**
 * 版权：Copyright (C) 2015  易族智汇（北京）科技有限公司.
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的.
 * 描述：商品api  
 * 修改人：Sylow  
 * 修改时间：2015-08-22
 * 修改内容：增加获得商品标签api
 */
package com.enation.app.shop.mobile.action.goods;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.StoreBonus;
import com.enation.app.b2b2c.core.service.IStorePromotionManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.Store;
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.core.model.Attribute;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.Specification;
import com.enation.app.shop.core.model.support.ParamGroup;
import com.enation.app.shop.core.service.GoodsTypeUtil;
import com.enation.app.shop.core.service.ICarInfoManager;
import com.enation.app.shop.core.service.IFavoriteManager;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IGoodsSearchManager;
import com.enation.app.shop.core.service.IGoodsTypeManager;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.utils.UrlUtils;
import com.enation.app.shop.mobile.model.ApiGoods;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;
import com.enation.framework.util.TestUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 商品api
 * 
 * @author Dawei
 * @date 2015-07-15
 * @version v1.1 2015-08-22
 * @since v1.0
 */
//http://localhost:8080/api/mobile/goods!listByTag.do?tagid=3&goodsnum=1
@SuppressWarnings("serial")
@Component("mobileGoodsApiAction")
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/mobile")
@Action("goods")
public class GoodsApiAction extends WWAction {

	private IGoodsManager goodsManager;
	private IGoodsGalleryManager goodsGalleryManager;
	private IProductManager productManager;
	private IMemberCommentManager memberCommentManager;
	private IFavoriteManager favoriteManager;
	private IGoodsSearchManager goodsSearchManager;
	private IGoodsTypeManager goodsTypeManager;
	private IStoreManager storeManager;
	private ICarInfoManager carInfoManager;
	private IGoodsCatManager goodsCatManager;
	private IStorePromotionManager storePromotionManager;
	private final int PAGE_SIZE = 20;
	private final int ZHONGANSTORE = 1;
	private String carplate;//车牌号
	//protected final Logger logger = Logger.getLogger(getClass());

	/**
	 * 根据标签获得商品列表
	 * 
	 * @author Sylow
	 * @param <b>catid</b>:分类id.int型，必填项
	 * @param <b>tagid</b>:标签id，int型，必填项
	 * @param <b>goodsnum</b>:数量，int型，必填项
	 * @return 返回json串 <br />
	 *         <b>result</b>: 1表示添加成功0表示失败 ，int型 <br />
	 *         <b>message</b>: 提示信息 <br />
	 *         <b>data</b>: 商品列表数据
	 */
	@SuppressWarnings("rawtypes")
	public String listByTag() {
		try {
			HttpServletRequest request = getRequest();
			String catid = (String) request.getParameter("catid");
			String tagid = (String) request.getParameter("tagid");
			String goodsnum = (String) request.getParameter("goodsnum");
			//logger.error("log4j:" + " " + tagid + " " + goodsnum);
			
			if (catid == null || catid.equals("")) {
				String uri = ThreadContextHolder.getHttpRequest().getServletPath();
				catid = UrlUtils.getParamStringValue(uri, "cat");
			}
			List<Map> goodsList = goodsManager.listByCat(tagid, catid, goodsnum);
//			for(Map goods:goodsList){
//				goods.put("original", UploadUtil.replacePath((String)goods.get("original")));
//				goods.put("big", UploadUtil.replacePath((String)goods.get("big")));
//				goods.put("small", UploadUtil.replacePath((String)goods.get("small")));
//				goods.put("thumbnail", UploadUtil.replacePath((String)goods.get("thumbnail")));
//			}
			this.json = JsonMessageUtil.getListJson(goodsList);

		} catch (RuntimeException e) {
			this.logger.error("获取商品列表出错", e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 商品列表
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String list() {
		try {
//			HttpServletRequest request = getRequest();
//		
//			int page = NumberUtils.toInt(request.getParameter("page"), 1);
//			Page webpage = goodsSearchManager.search(page, PAGE_SIZE);
			List<ApiGoods> goodsList = new ArrayList<ApiGoods>();
			//List list = (List) webpage.getResult();
			List<Map> list = goodsManager.list();
			for (int i = 0; i < list.size(); i++) {
				Map map = (Map) list.get(i);
//				map.put("original", UploadUtil.replacePath((String)map.get("original")));
//				map.put("big", UploadUtil.replacePath((String)map.get("big")));
//				map.put("small", UploadUtil.replacePath((String)map.get("small")));
//				map.put("thumbnail", UploadUtil.replacePath((String)map.get("thumbnail")));
				ApiGoods goods = new ApiGoods();
				BeanUtils.populate(goods, map);
				goodsList.add(goods);
				
			}
			this.json = JsonMessageUtil.getListJson(goodsList);

		} catch (RuntimeException e) {
			this.logger.error("获取商品列表出错", e);
			this.showErrorJson(e.getMessage());

		} catch (IllegalAccessException e) {
			this.logger.error("获取商品列表出错", e);
			this.showErrorJson(e.getMessage());

		} catch (InvocationTargetException e) {
			this.logger.error("获取商品列表出错", e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	/**
	 * 搜索商品
	 * @author Sylow
	 * @param <b>catid</b>:分类id.int型,可为空
	 * @param <b>goods_name</b>:商品名称,String,可为空
	 * @param <b>sort</b>:排序名.String,可为空
	 * @param <b>order</b>:正序倒序关键字,String,可为空
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String search(){
		try {
			HttpServletRequest request = getRequest();
			Object catId =  request.getParameter("catid");
			Object storeId =  request.getParameter("store_id");
			String goodsName = (String) request.getParameter("goods_name");
			String sort = (String) request.getParameter("sort");
			String order = (String) request.getParameter("order");
			
			//默认值
			if (order == null || "".equals(sort)) {
				order = "desc";
			}
			if (sort == null ) {
				sort = "";
			}
			
			Map<String,Object> param = new HashMap<String,Object>();
			if(catId != null) {
				param.put("catid", Integer.parseInt(catId.toString()));
			}
			if (storeId != null) {
				param.put("store_id", Integer.parseInt(storeId.toString()));
			}
			
			param.put("goods_name", goodsName);
			List<Goods> goodsList = goodsManager.searchGoods(param, sort, order);
			for(Goods goods:goodsList){
				goods.setOriginal( UploadUtil.replacePath((String)goods.getOriginal()));
				goods.setBig(UploadUtil.replacePath((String)goods.getBig()));
				goods.setSmall(UploadUtil.replacePath((String)goods.getSmall()));
				goods.setThumbnail(UploadUtil.replacePath((String)goods.getThumbnail()));
			}
			this.json = JsonMessageUtil.getListJson(goodsList);
		} catch(RuntimeException e) {
			TestUtil.print(e);
			this.logger.error("搜索商品出错", e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}

	/**
	 * 商品详细
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String detail() {
		try {
			HttpServletRequest request = getRequest();
			int goods_id = NumberUtils.toInt(request.getParameter("id"), 0);
			String carplate = request.getParameter("carplate");
			Double totalgain = 0d; //获取当前车的安全出行奖励;
			if(!StringUtils.isEmpty(carplate)){
				List carinfoList = carInfoManager.getCarInfoByCarplate(carplate);
				if(carinfoList != null && carinfoList.size() > 0){
					net.sf.json.JSONObject carInfo = net.sf.json.JSONObject.fromObject(carinfoList.get(0));
					totalgain = carInfo.getDouble("totalgain");
				}
			}
			totalgain = totalgain<0d?0d:totalgain;
			
			Map<String, Object> productMap;

			Product product = productManager.getByGoodsId(goods_id);
			productMap = BeanUtils.describe(product);
			
			//Goods goods = goodsManager.getGoods(goods_id);
			Map goods = goodsManager.get(goods_id);
			productMap.put("thumbnail", goods.get("thumbnail"));
			productMap.put("intro", goods.get("intro"));
			productMap.put("market_enable",goods.get("market_enable")); //是否上架
			
			int commentCount = memberCommentManager.getCommentsCount(goods_id);
			int goodCommentCount = memberCommentManager.getCommentsCount(goods_id, 3);
			productMap.put("comment_count", commentCount);
			if (commentCount > 0) {
				java.text.NumberFormat percentFormat = java.text.NumberFormat.getPercentInstance();
				percentFormat.setMaximumFractionDigits(0); // 最大小数位数
				percentFormat.setMaximumIntegerDigits(2);// 最大整数位数
				percentFormat.setMinimumFractionDigits(0); // 最小小数位数
				percentFormat.setMinimumIntegerDigits(1);// 最小整数位数
				productMap.put("comment_percent", percentFormat.format((float) (goodCommentCount / 1)));
			} else {
				productMap.put("comment_percent", "100%");
			}

			// 是否已收藏
			Member member = UserConext.getCurrentMember();

			if (member == null) {
				productMap.put("favorited", false);
			} else {
				productMap.put("favorited", favoriteManager.isFavorited(goods_id, member.getMember_id()));
			}
			
			//商品相册
			List<GoodsGallery> galleryList = goodsGalleryManager.list(goods_id);
			if (galleryList == null || galleryList.size() == 0) {
				String img = SystemSetting.getDefault_img_url();
				GoodsGallery gallery = new GoodsGallery();
				gallery.setSmall(img);
				gallery.setBig(img);
				gallery.setThumbnail(img);
				gallery.setTiny(img);
				gallery.setOriginal(img);
				gallery.setIsdefault(1);
				galleryList.add(gallery);
			}
			productMap.put("imgList", galleryList);
			
			
			//2015-08-28  规格在第一版本直接显示在商品页里  _  by  _ Sylow
			List<Product> productList = this.productManager.list(goods_id);
			if (("" + goods.get("have_spec")).equals("0")) {
//				productMap.put("productid", productList.get(0).getProduct_id());// 商品的货品id
				productMap.put("productList", productList);// 商品的货品列表
			} else {
				List<Specification> specList = this.productManager.listSpecs(goods_id);
				productMap.put("specList", specList);// 可选的商品规格数据列表（goods的所有spec列表）
				productMap.put("productList", productList);// 商品的货品列表
			}
			productMap.put("have_spec", goods.get("have_spec") == null ? 0 : goods.get("have_spec"));// 是否有规格
			productMap.put("store_id", goods.get("store_id")); 
			Store store = storeManager.getStore((Integer) goods.get("store_id"));
			productMap.put("store_name", store.getStore_name());
			productMap.put("brand_name",goods.get("brand_name")==null?"无":goods.get("brand_name"));
			productMap.put("totalgain",totalgain);
			productMap.put("rewards_limit",goods.get("rewards_limit")==null?0.0d:goods.get("rewards_limit"));
			this.json = UploadUtil.replacePath(JsonMessageUtil.getObjectJson(productMap));//替换图片链接
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取商品详情出错", e);

		} catch (IllegalAccessException e) {
			e.printStackTrace();
			this.logger.error("获取商品详情出错", e);
			this.showErrorJson(e.getMessage());

		} catch (InvocationTargetException e) {
			e.printStackTrace();
			this.logger.error("获取商品详情出错", e);
			this.showErrorJson(e.getMessage());

		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			this.logger.error("获取商品详情出错", e);
			this.showErrorJson(e.getMessage());
		} catch (Exception e){
			e.printStackTrace();
		}
		return JSON_MESSAGE;
	}

	/**
	 * 商品相册
	 * 
	 * @return
	 */
	public String gallery() {
		try {
			HttpServletRequest request = getRequest();
			int goods_id = NumberUtils.toInt(request.getParameter("id"), 0);

			List<GoodsGallery> galleryList = this.goodsGalleryManager.list(goods_id);
			if (galleryList == null || galleryList.size() == 0) {
				String img = SystemSetting.getDefault_img_url();
				GoodsGallery gallery = new GoodsGallery();
				gallery.setSmall(img);
				gallery.setBig(img);
				gallery.setThumbnail(img);
				gallery.setTiny(img);
				gallery.setOriginal(img);
				gallery.setIsdefault(1);
				galleryList.add(gallery);
			}
			this.json = JsonMessageUtil.getListJson(galleryList);
		} catch (RuntimeException e) {
			this.logger.error("获取商品相册出错", e);
			this.showErrorJson(e.getMessage());

		}

		return JSON_MESSAGE;
	}

	/**
	 * 商品规格
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String spec() {
		try {

			HttpServletRequest request = getRequest();
			int goods_id = NumberUtils.toInt(request.getParameter("id"), 0);

			Map goods = goodsManager.get(goods_id);

			List<Product> productList = this.productManager.list(goods_id);
			Map data = new HashMap();
			if (("" + goods.get("have_spec")).equals("0")) {
				data.put("productid", productList.get(0).getProduct_id());// 商品的货品id
				data.put("productList", productList);// 商品的货品列表
			} else {
				List<Specification> specList = this.productManager.listSpecs(goods_id);
				data.put("specList", specList);// 商品规格数据列表
				data.put("productList", productList);// 商品的货品列表
			}
			data.put("have_spec", goods.get("have_spec") == null ? 0 : goods.get("have_spec"));// 是否有规格

			this.json = JsonMessageUtil.getObjectJson(data);
		} catch (RuntimeException e) {
			this.logger.error("获取商品规格出错", e);
			this.showErrorJson(e.getMessage());
		}
		return JSON_MESSAGE;
	}

	/**
	 * 商品评论
	 * 
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String comment() {
		try {
			HttpServletRequest request = getRequest();
			int goods_id = NumberUtils.toInt(request.getParameter("id"), 0);
			int type = NumberUtils.toInt(request.getParameter("type"), 1);
			int page = NumberUtils.toInt(request.getParameter("page"), 1);

			Page pageData = memberCommentManager.getGoodsComments(goods_id, page, PAGE_SIZE, type);
			List list = (List) pageData.getResult();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) list.get(i);
				if (map.containsKey("face") && map.get("face") != null) {
					map.put("face", com.enation.eop.sdk.utils.UploadUtil.replacePath(map.get("face").toString()));
				}
			}
			this.json = JsonMessageUtil.getListJson(list);
		} catch (RuntimeException e) {
			this.logger.error("获取商品评论出错", e);
			this.showErrorJson(e.getMessage());
		}
		return JSON_MESSAGE;
	}

	/**
	 * 商品属性
	 * 
	 * @return
	 */
	public String attrList() {
		try {
			HttpServletRequest request = getRequest();
			Integer goodsid = NumberUtils.toInt(request.getParameter("id"), 0);
			Map goodsmap =this.goodsManager.get(goodsid);
			Integer typeid = (Integer) goodsmap.get("type_id");
			
			List<Attribute> list = this.goodsTypeManager.getAttrListByTypeId(typeid);
			List attrList = new ArrayList();
			
			int i=1;
			for(Attribute attribute:list){
				Map attrmap = new HashMap();
				if(attribute.getType()==3){
					String[] s = attribute.getOptionAr();
					String p = (String) goodsmap.get("p"+i);
					Integer num=0;
					if(!StringUtil.isEmpty(p)){
						num=Integer.parseInt(p);
					}
					attrmap.put("attrName", attribute.getName());
					attrmap.put("attrValue", s[num]);
				}else if(attribute.getType()==6){
					attrmap.put("attrName", attribute.getName());
					String value=goodsmap.get("p"+i).toString().replace("#", ",").substring(1);
					attrmap.put("attrValue",value);
				}else{
					attrmap.put("attrName", attribute.getName());
					attrmap.put("attrValue", goodsmap.get("p"+i));
				}
				attrList.add(attrmap);
				i++;
			}
			this.json = JsonMessageUtil.getListJson(attrList);
		} catch (RuntimeException e) {
			this.logger.error("获取商品相册出错", e);
			this.showErrorJson(e.getMessage());

		}

		return JSON_MESSAGE;
	}
	
	/**
	 * 商品参数
	 * @return
	 */
	public String parameList() {
		try {
			HttpServletRequest request = getRequest();
			Integer goodsid = NumberUtils.toInt(request.getParameter("id"), 0);
			Map goodsmap =this.goodsManager.get(goodsid);
			String goodParams  =(String)goodsmap.get("params");
			Map result = new HashMap();
			if(goodParams!=null && !goodParams.equals("")){
				ParamGroup[] paramList =GoodsTypeUtil.converFormString(goodParams);
				
				result.put("paramListss", paramList);
		
				
				if(paramList!=null && paramList.length>0) {
					result.put("hasParam", true);
				} else {
					result.put("hasParam", false);
				}
			}else{
				result.put("hasParam", false);
			}
			if(result.get("paramListss") == null){
				this.json = "{result :1,data:[{paramListss:[]}]}";
			}else{	
				this.json = JsonMessageUtil.getObjectJson(result);
			}
		} catch (RuntimeException e) {
			this.logger.error("获取商品相册出错", e);
			this.showErrorJson(e.getMessage());

		}

		return JSON_MESSAGE;
	}
	
	/**
	 * 根据用户绑定的店铺显示店铺商品
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String listByStore(){
		HttpServletRequest request = this.getRequest();
		try {
			String cat_id = request.getParameter("cat_id");
			List carInfoList=carInfoManager.getCarInfoByCarplate(carplate);
			String orderByParam = request.getParameter("orderByParam");
			String store_id = request.getParameter("store_id");
			String sort = request.getParameter("sort");
			String keyWord = request.getParameter("keyWord");
			String associate = request.getParameter("associate");
			Member member = UserConext.getCurrentMember();//获取当前用户
			String sift_prices = request.getParameter("sift_price");
			List<Map> zhonganHotgoodsList = Collections.emptyList();
			List<Map> zhonganList = Collections.emptyList();
			List<Map> hotgoodsList = Collections.emptyList();
			List<Map> store4sList  = Collections.emptyList();
			
			if(StringUtil.isNull(store_id)||Integer.parseInt(store_id )== ZHONGANSTORE){
				 zhonganHotgoodsList = goodsManager.getHotgoodsListByStoreId(ZHONGANSTORE,cat_id,keyWord);
				 zhonganList = goodsManager.getGoodsListByStoreId(ZHONGANSTORE,cat_id,keyWord,orderByParam,sort,sift_prices);
			}
			List<StoreBonus> storeBonus = null;
 			if(member != null && carInfoList != null && carInfoList.size()>0 ){
				//如果用户绑定店铺则获取店铺商品
 				net.sf.json.JSONObject carInfo = net.sf.json.JSONObject.fromObject(carInfoList.get(0));
 				if(StringUtil.isNull(store_id) || Integer.parseInt(store_id)!=ZHONGANSTORE){
 					hotgoodsList = goodsManager.getHotgoodsListByStoreId(carInfo.getInt("repair4sstoreid"),cat_id,keyWord);
 					store4sList = goodsManager.getGoodsListByStoreId(carInfo.getInt("repair4sstoreid"),cat_id,keyWord,orderByParam,sort,sift_prices);
 				}
 				storeBonus = storePromotionManager.getBonusByStoreId(carInfo.getInt("repair4sstoreid"),member);
 			}
 			//过滤普通商品中的热门商品
 			if(!StringUtil.isNull(orderByParam)||!StringUtil.isNull(sort)||!StringUtil.isNull(sift_prices)||!StringUtil.isNull(keyWord)){
 				hotgoodsList = store4sList;
 				zhonganHotgoodsList = zhonganList;
 			}else{//用排序后的列表
 				store4sList.removeAll(hotgoodsList);
 				hotgoodsList.addAll(store4sList);
 				zhonganList.removeAll(zhonganHotgoodsList);
 				zhonganHotgoodsList.addAll(zhonganList);
 			}
 			List<Map> list = new LinkedList<Map>();
 			//UI界面需要中安和4s店size相同显示
			List<ApiGoods> GoodsList = new ArrayList<ApiGoods>();
 			if(hotgoodsList.isEmpty() || zhonganHotgoodsList.isEmpty()){ //当某个店铺出现没有商品的情况
 				list.addAll(zhonganHotgoodsList);
 				list.addAll(hotgoodsList);
 			}else if(hotgoodsList.size() > 0 &&  zhonganHotgoodsList.size()>0){//中安和4s都有的情况,交替添加
 				int sizeCount = hotgoodsList.size() + zhonganHotgoodsList.size();
 				int flag  = 0;
 				int index = 0;
 				while(flag != sizeCount){//
 					if(index < zhonganHotgoodsList.size()){
 						list.add(zhonganHotgoodsList.get(index));
 						flag++;
 					}
 					if(index < hotgoodsList.size()){
 						list.add(hotgoodsList.get(index));
 						flag++;
 					}
 					index++;
 				}
 			}
 			for (int i = 0; i < list.size(); i++) {
					Map map = (Map) list.get(i);
					ApiGoods goods = new ApiGoods();
					BeanUtils.populate(goods, map);
					GoodsList.add(goods);
 			}
 			if(StringUtil.toInt(associate,0)==1){//搜索词联想给三个	
				if(GoodsList!=null&&GoodsList.size() > 5){
					GoodsList = GoodsList.subList(0, 5);
				}
 			}
 			JSONObject data = new JSONObject();
 			data.put("goods_list", GoodsList);
			if (StringUtil.isNull(cat_id)) {//获取商品类型
				 List<Cat> cats = goodsCatManager.getAllParent();
				 data.put("cats", cats);
			}
		
			//价格区间
			JSONArray jsonArray = new JSONArray();
			jsonArray.add("0-199");
			jsonArray.add("200-499");
			jsonArray.add("500-999");
			jsonArray.add("1000-1999");
			data.put("sift_prices", jsonArray);
			if(storeBonus!=null && storeBonus.size()>0){
				data.put("isCoupon", 1);
			}else{
				data.put("isCoupon", 0);
			}
			this.json =   "{\"result\":1,\"data\":"+UploadUtil.replacePath(data.toString())+"}";
		} catch (RuntimeException e) {
			e.printStackTrace();
			this.logger.error("获取商品列表出错", e);
			this.showErrorJson(e.getMessage());

		} catch (IllegalAccessException e) {
			e.printStackTrace();
			this.logger.error("获取商品列表出错", e);
			this.showErrorJson(e.getMessage());

		} catch (InvocationTargetException e) {
			e.printStackTrace();
			this.logger.error("获取商品列表出错", e);
			this.showErrorJson(e.getMessage());
		} catch(Exception e){ 
			e.printStackTrace();
		}
		return WWAction.JSON_MESSAGE;
	}
	
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	public IGoodsGalleryManager getGoodsGalleryManager() {
		return goodsGalleryManager;
	}

	public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
		this.goodsGalleryManager = goodsGalleryManager;
	}

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	public IMemberCommentManager getMemberCommentManager() {
		return memberCommentManager;
	}

	public void setMemberCommentManager(IMemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}

	public IFavoriteManager getFavoriteManager() {
		return favoriteManager;
	}

	public void setFavoriteManager(IFavoriteManager favoriteManager) {
		this.favoriteManager = favoriteManager;
	}

	public IGoodsSearchManager getGoodsSearchManager() {
		return goodsSearchManager;
	}

	public void setGoodsSearchManager(IGoodsSearchManager goodsSearchManager) {
		this.goodsSearchManager = goodsSearchManager;
	}

	public IGoodsTypeManager getGoodsTypeManager() {
		return goodsTypeManager;
	}

	public void setGoodsTypeManager(IGoodsTypeManager goodsTypeManager) {
		this.goodsTypeManager = goodsTypeManager;
	}

	public IStoreManager getStoreManager() {
		return storeManager;
	}

	public void setStoreManager(IStoreManager storeManager) {
		this.storeManager = storeManager;
	}

	public ICarInfoManager getCarInfoManager() {
		return carInfoManager;
	}

	public void setCarInfoManager(ICarInfoManager carInfoManager) {
		this.carInfoManager = carInfoManager;
	}

	public String getCarplate() {
		return carplate;
	}

	public void setCarplate(String carplate) {
		this.carplate = carplate;
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

	public IStorePromotionManager getStorePromotionManager() {
		return storePromotionManager;
	}

	public void setStorePromotionManager(IStorePromotionManager storePromotionManager) {
		this.storePromotionManager = storePromotionManager;
	}
	
	
	


}
