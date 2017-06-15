package com.enation.app.b2b2c.core.tag.goods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.spec.service.ISpecManager;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.Specification;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;
/**
 * 店铺商品规格Tag
 * @author LiFenLong
 *
 */
@Component
public class StoreGoodsSpecTag extends BaseFreeMarkerTag{
	private ISpecManager specManager;
	private IGoodsCatManager goodsCatManager;
	private IGoodsManager goodsManager;
	
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		
		List<Specification> specList = null;
		int goods_id = NumberUtils.toInt(request.getParameter("goods_id"), 0);
		if(goods_id > 0){
			Map goods = goodsManager.get(goods_id);
			if(goods == null || !goods.containsKey("type_id")){
				return new ArrayList<Specification>();
			}
			int type_id = NumberUtils.toInt(goods.get("type_id").toString(), 0);
			specList = this.specManager.listSpecAndValueByType(type_id);
		}else{		
			int cat_id = NumberUtils.toInt(request.getParameter("catid"),0);
			Cat goodsCat = goodsCatManager.getById(cat_id);
			if(goodsCat == null || goodsCat.getType_id() == null){
				specList = new ArrayList<Specification>();
			}else{
				specList = this.specManager.listSpecAndValueByType(goodsCat.getType_id());
			}
		}
		return specList;
	}
	public ISpecManager getSpecManager() {
		return specManager;
	}
	public void setSpecManager(ISpecManager specManager) {
		this.specManager = specManager;
	}
	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}
	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	
	
}
