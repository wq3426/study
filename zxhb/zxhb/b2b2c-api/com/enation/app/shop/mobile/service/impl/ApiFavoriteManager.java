package com.enation.app.shop.mobile.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Favorite;
import com.enation.app.shop.mobile.service.IApiFavoriteManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;

/**
 * Created by Dawei on 4/29/15.
 */
@Component
public class ApiFavoriteManager extends BaseSupport<Favorite> implements IApiFavoriteManager {

   
	@Override
    public Favorite get(int favorite_id) {
        String sql = "SELECT * FROM favorite WHERE favorite_id=?";
        return baseDaoSupport.queryForObject(sql, Favorite.class, favorite_id);
    }

    
	@Override
    public Favorite get(int goodsid, int memberid){
        String sql = "SELECT * FROM favorite WHERE goods_id=? AND member_id=?";
        List<Favorite> favoriteList = baseDaoSupport.queryForList(sql, Favorite.class, goodsid, memberid);
        if(favoriteList.size() > 0){
            return favoriteList.get(0);
        }
        return null;
    }

   
	@Override
    public void delete(int goodsid, int memberid){
        baseDaoSupport.execute("DELETE FROM favorite WHERE goods_id=? AND member_id=?", goodsid, memberid);
    }


   
	@Override
    public boolean isFavorited(int goodsid, int memeberid){
        return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM favorite WHERE goods_id=? AND member_id=?", goodsid,memeberid) > 0;
    }
    
	@Override
    public void add(Integer goodsid, int memberid) {
        String sql = "INSERT INTO favorite(member_id,goods_id,favorite_time) VALUES(?,?,?)";
        baseDaoSupport.execute(sql, memberid, goodsid, com.enation.framework.util.DateUtil.getDateline());
    }
	
	@Override
	public Page list(int memberid, int pageNo, int pageSize) {
		String sql = "select g.*, f.favorite_id from " + this.getTableName("favorite")
				+ " f left join " + this.getTableName("goods")
				+ " g on g.goods_id = f.goods_id";
		sql += " where f.member_id = ? order by f.favorite_id";
		Page page = this.daoSupport.queryForPage(sql, pageNo, pageSize,memberid);
		return page;
	}
	
	
	@Override
	public void delete(int favorite_id) {
		this.baseDaoSupport.execute("delete from favorite where favorite_id = ?", favorite_id);
	}

	@Override
	public void add(Integer goodsid) {
		Member member = UserConext.getCurrentMember();
		Favorite favorite = new Favorite();
		favorite.setGoods_id(goodsid);
		favorite.setMember_id(member.getMember_id());
		this.baseDaoSupport.insert("favorite", favorite);
	}

}
