package com.enation.app.shop.core.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.app.shop.core.model.MemberComment;
import com.enation.app.shop.core.plugin.goods.GoodsCommentsBundle;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
/**
 * 
 * @author LiFenLong 2014-4-1;4.0版本改造，修改delete方法
 *
 */
public class MemberCommentManager extends BaseSupport<MemberComment> implements IMemberCommentManager{

	private GoodsCommentsBundle goodsCommentsBundle;
	
	@Override
	public void add(MemberComment memberComment) {
//		this.daoSupport.execute("INSERT INTO es_member_comment(goods_id, member_id,content,img,dateline,ip,reply,replytime,status,type,replystatus,grade) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)",
//				memberComment.getGoods_id(),memberComment.getMember_id(),memberComment.getContent(),memberComment.getImg(),memberComment.getDateline(),
//				memberComment.getIp(),"",0,0,memberComment.getType(),0,memberComment.getGrade());	
		
		//触发增加评论前事件
		this.goodsCommentsBundle.onGoodsCommentsBeforeAdd(memberComment);
		this.baseDaoSupport.insert("member_comment", memberComment);
		
		//触发增加评论后事件
		this.goodsCommentsBundle.onGoodsCommentsAfterAdd(memberComment);

	}

	@Override
	public Page getGoodsComments(int goods_id, int page, int pageSize, int type, Integer grade) {
		//System.out.println("SELECT m.lv_id,m.sex,m.uname,m.face,l.name as levelname,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id LEFT JOIN " + this.getTableName("member_lv") + " l ON m.lv_id=l.lv_id " +
		//		"WHERE c.goods_id=? AND c.type=? AND c.status=1 ORDER BY c.comment_id DESC");
		String sql = "SELECT m.lv_id,m.sex,m.uname,m.face,l.name as levelname,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id LEFT JOIN " + this.getTableName("member_lv") + " l ON m.lv_id=l.lv_id " +
				"WHERE c.goods_id=? AND c.type=? AND c.status=1";
		if(grade != null){
			sql += " and c.grade = "+grade+"";
		}
		sql += " ORDER BY c.comment_id DESC";
		return this.daoSupport.queryForPage(sql, page, pageSize, goods_id, type);
	}
	
	@Override
	public Page getGoodsComments(int goods_id, int page, int pageSize, int type) {
		//System.out.println("SELECT m.lv_id,m.sex,m.uname,m.face,l.name as levelname,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id LEFT JOIN " + this.getTableName("member_lv") + " l ON m.lv_id=l.lv_id " +
		//		"WHERE c.goods_id=? AND c.type=? AND c.status=1 ORDER BY c.comment_id DESC");
		return this.daoSupport.queryForPage("SELECT m.lv_id,m.sex,m.uname,m.face,l.name as levelname,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id LEFT JOIN " + this.getTableName("member_lv") + " l ON m.lv_id=l.lv_id " +
				"WHERE c.goods_id=? AND c.type=? AND c.status=1 ORDER BY c.comment_id DESC", page, pageSize, goods_id, type);
	}
	
	public int getGoodsGrade(int goods_id){
		int sumGrade = this.baseDaoSupport.queryForInt("SELECT SUM(grade) FROM member_comment WHERE status=1 AND goods_id=? AND type=1", goods_id);
		int total = this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE status=1 AND goods_id=? AND type=1", goods_id);
		if(sumGrade != 0 && total != 0){
			return (sumGrade/total);
		}else{
			return 0;
		}
	}
	
	public Page getAllComments(int page, int pageSize, int type){
		String sql="SELECT m.username muname,g.name gname,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("goods") + " g ON c.goods_id=g.goods_id LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id " +
		"WHERE c.type=? ORDER BY c.comment_id DESC";
		//System.out.println(sql);
		return this.daoSupport.queryForPage(sql, page, pageSize, type);
	}

	@Override
	public MemberComment get(int comment_id) {
		return this.baseDaoSupport.queryForObject("SELECT * FROM member_comment WHERE comment_id=?", MemberComment.class, comment_id);
	}

	@Override
	public void update(MemberComment memberComment) {
//		this.baseDaoSupport.execute("UPDATE member_comment SET goods_id=?, member_id=?,content=?,img=?,dateline=?,ip=?,reply=?,replytime=?,status=?,type=?,replystatus=?,grade=? WHERE comment_id=?",
//				memberComment.getGoods_id(),memberComment.getMember_id(),memberComment.getContent(),memberComment.getImg(),memberComment.getDateline(),memberComment.getIp(),memberComment.getReply(),
//				memberComment.getReplytime(),memberComment.getStatus(),memberComment.getType(),memberComment.getReplystatus(),memberComment.getGrade(),memberComment.getComment_id());
		this.baseDaoSupport.update("member_comment", memberComment, "comment_id="+memberComment.getComment_id());
		if(memberComment.getStatus()==1){
			String updatesql = "update es_goods set grade=grade+1 where goods_id=?";
			this.daoSupport.execute(updatesql,memberComment.getGoods_id());
		}
	}

	
	public Map statistics(int goodsid){
		String sql="select grade,count(grade) num from es_member_comment c where c.goods_id =? GROUP BY c.grade ";
		List<Map> gradeList =this.daoSupport.queryForList(sql, goodsid);
		Map gradeMap  = new HashMap();
		for(Map grade:gradeList){
			Object gradeValue =grade.get("grade");
			long num =Long.parseLong(grade.get("num").toString().trim());
			gradeMap.put("grade_"+gradeValue, num);
		}
		return gradeMap;
	}
	
	@Override
	public int getGoodsCommentsCount(int goods_id) {
		return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE goods_id=? AND status=1 AND type=1", goods_id);
	}

	@Override
	public void delete(Integer[] comment_id) {
		if (comment_id== null || comment_id.equals(""))
			return;
		String id_str = StringUtil.arrayToString(comment_id, ",");
		String sql = "DELETE FROM member_comment where comment_id in (" + id_str + ")";
		this.baseDaoSupport.execute(sql);
		
	}

	@Override
	public Page getMemberComments(int page, int pageSize, int type,
			int member_id) {
		return this.daoSupport.queryForPage("SELECT g.name,g.cat_id, c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("goods") + " g ON c.goods_id=g.goods_id " +
				"WHERE c.type=? AND c.member_id=? ORDER BY c.comment_id DESC", page, pageSize, type, member_id);
	}

	@Override
	public int getMemberCommentTotal(int member_id, int type) {
		return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE member_id=? AND type=?", member_id, type);
	}

	@Override
	public Page getCommentsByStatus(int page, int pageSize, int type, int status) {
		return this.daoSupport.queryForPage("SELECT m.uname,g.name,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("goods") + " g ON c.goods_id=g.goods_id LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id " +
				"WHERE c.type=? and c.status = ? ORDER BY c.comment_id DESC", page, pageSize, type,status);
	}

	@Override
	/**
	 * @author LiFenLong
	 */
	public void deletealone(int comment_id) {
		
		this.baseDaoSupport.execute("DELETE FROM member_comment WHERE comment_id=?", comment_id);
	}
	
	/**
	 * 根据商品id获取商品评论总数
	 * @param goods_id
	 * @return
	 */
	@Override
	public int getCommentsCount(int goods_id) {
        return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE goods_id=? AND status=1 AND type=1", goods_id);
    }
	
	@Override
    public int getCommentsCount(int goods_id, int grade){
        return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE goods_id=? AND status=1 AND type=1 AND grade >= ?", goods_id, grade);
    }

	/**
	 * 根据评分、商品ID或者各个评论的数量
	 * @param count  数量
	 * @param goods_id  商品ID
	 * @param count    评论数量
	 * whj 2015-10-15
	 * @return
	 */
	@Override
	public int getCommentsGradeCount(int goods_id, int grade){
        return this.baseDaoSupport.queryForInt("SELECT COUNT(0) FROM member_comment WHERE goods_id=? AND status=1 AND type=1 AND grade=?", goods_id, grade);
    }
	
	
	
	@Override
	public Page getGoodsCommentsGradeList(int goods_id, int page, int pageSize, int type, int grade) {
		return this.daoSupport.queryForPage("SELECT m.lv_id,m.sex,m.uname,m.face,l.name as levelname,c.* FROM " + this.getTableName("member_comment") + " c LEFT JOIN " + this.getTableName("member") + " m ON c.member_id=m.member_id LEFT JOIN " + this.getTableName("member_lv") + " l ON m.lv_id=l.lv_id " +
				"WHERE c.goods_id=? AND c.type=? AND c.grade=? AND c.status=1 ORDER BY c.comment_id DESC", page, pageSize, goods_id, type, grade);
	}

	public GoodsCommentsBundle getGoodsCommentsBundle() {
		return goodsCommentsBundle;
	}

	public void setGoodsCommentsBundle(GoodsCommentsBundle goodsCommentsBundle) {
		this.goodsCommentsBundle = goodsCommentsBundle;
	}
}
