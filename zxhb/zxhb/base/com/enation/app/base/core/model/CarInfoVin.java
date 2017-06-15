package com.enation.app.base.core.model;

/**
 * @Description vin第三方数据解析--汽车信息实体
 *
 * @createTime 2016年9月22日 上午11:54:00
 *
 * @author <a href="mailto:gezhengcui@trans-it.cn">gezhengcui</a>
 */
public class CarInfoVin {

	private int id;
	private String vin;		//17位车辆识别码
	private int carmodel_id;		
	private String vinnf;	//VIN对应的年份，可为具体的年份和“未查到年代”两种
	private String cjmc	;	//厂家名称
	private String pp	;	//品牌
	private String cx	;	//车型
	private String pl	;	//排量
	private String fdjxh;	//发动机型号
	private String bsqlx;	//变速器类型
	private String dws	;	//	档位数
	private String pfbz	;	//排放标准
	private String cldm	;	//车型代码
	private String ssnf	;	//上市年份
	private String tcnf	;	//停产年份
	private String zdjg	;	//指导价格
	private String ssyf	;	//上市月份
	private String scnf	;	//生产年份
	private String nk	;	//年款
	private String cxi	;	//车系
	private String xsmc	;	//销售名称
	private String cllx	;	//车辆类型
	private String jb	;	//车辆级别
	private String csxs	;	//车身形式
	private String cms	;	//车门数
	private String zws	;	//座位数
	private String gl	;	//发动机最大功率(kW)
	private String rylx	;	//燃油类型
	private String bsqms;	//变速箱描述
	private String rybh	;	//燃油标号
	private String qdfs	;	//驱动方式
	private String fdjgs;	//缸数
	private String levelid;	//A保养周期反馈用到的标示id
	
	
	/*
	 * ================================================================================
	 * GETTER AND SETTER
	 */
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getVin() {
		return vin;
	}
	public void setVin(String vin) {
		this.vin = vin;
	}
	public String getVinnf() {
		return vinnf;
	}
	public void setVinnf(String vinnf) {
		this.vinnf = vinnf;
	}
	public String getCjmc() {
		return cjmc;
	}
	public void setCjmc(String cjmc) {
		this.cjmc = cjmc;
	}
	public String getPp() {
		return pp;
	}
	public void setPp(String pp) {
		this.pp = pp;
	}
	public String getCx() {
		return cx;
	}
	public void setCx(String cx) {
		this.cx = cx;
	}
	public String getPl() {
		return pl;
	}
	public void setPl(String pl) {
		this.pl = pl;
	}
	public String getFdjxh() {
		return fdjxh;
	}
	public void setFdjxh(String fdjxh) {
		this.fdjxh = fdjxh;
	}
	public String getBsqlx() {
		return bsqlx;
	}
	public void setBsqlx(String bsqlx) {
		this.bsqlx = bsqlx;
	}
	public String getDws() {
		return dws;
	}
	public void setDws(String dws) {
		this.dws = dws;
	}
	public String getPfbz() {
		return pfbz;
	}
	public void setPfbz(String pfbz) {
		this.pfbz = pfbz;
	}
	public String getCldm() {
		return cldm;
	}
	public void setCldm(String cldm) {
		this.cldm = cldm;
	}
	public String getSsnf() {
		return ssnf;
	}
	public void setSsnf(String ssnf) {
		this.ssnf = ssnf;
	}
	public String getTcnf() {
		return tcnf;
	}
	public void setTcnf(String tcnf) {
		this.tcnf = tcnf;
	}
	public String getZdjg() {
		return zdjg;
	}
	public void setZdjg(String zdjg) {
		this.zdjg = zdjg;
	}
	public String getSsyf() {
		return ssyf;
	}
	public void setSsyf(String ssyf) {
		this.ssyf = ssyf;
	}
	public String getScnf() {
		return scnf;
	}
	public void setScnf(String scnf) {
		this.scnf = scnf;
	}
	public String getNk() {
		return nk;
	}
	public void setNk(String nk) {
		this.nk = nk;
	}
	public String getCxi() {
		return cxi;
	}
	public void setCxi(String cxi) {
		this.cxi = cxi;
	}
	public String getXsmc() {
		return xsmc;
	}
	public void setXsmc(String xsmc) {
		this.xsmc = xsmc;
	}
	public String getCllx() {
		return cllx;
	}
	public void setCllx(String cllx) {
		this.cllx = cllx;
	}
	public String getJb() {
		return jb;
	}
	public void setJb(String jb) {
		this.jb = jb;
	}
	public String getCsxs() {
		return csxs;
	}
	public void setCsxs(String csxs) {
		this.csxs = csxs;
	}
	public String getCms() {
		return cms;
	}
	public void setCms(String cms) {
		this.cms = cms;
	}
	public String getZws() {
		return zws;
	}
	public void setZws(String zws) {
		this.zws = zws;
	}
	public String getGl() {
		return gl;
	}
	public void setGl(String gl) {
		this.gl = gl;
	}
	public String getRylx() {
		return rylx;
	}
	public void setRylx(String rylx) {
		this.rylx = rylx;
	}
	public String getBsqms() {
		return bsqms;
	}
	public void setBsqms(String bsqms) {
		this.bsqms = bsqms;
	}
	public String getRybh() {
		return rybh;
	}
	public void setRybh(String rybh) {
		this.rybh = rybh;
	}
	public String getQdfs() {
		return qdfs;
	}
	public void setQdfs(String qdfs) {
		this.qdfs = qdfs;
	}
	public String getFdjgs() {
		return fdjgs;
	}
	public void setFdjgs(String fdjgs) {
		this.fdjgs = fdjgs;
	}
	public String getLevelid() {
		return levelid;
	}
	public void setLevelid(String levelid) {
		this.levelid = levelid;
	}
	public int getCarmodel_id() {
		return carmodel_id;
	}
	public void setCarmodel_id(int carmodel_id) {
		this.carmodel_id = carmodel_id;
	}
	@Override
	public String toString() {
		return "CarInfoVin [id=" + id + ", vin=" + vin + ", carmodel_id=" + carmodel_id + ", vinnf=" + vinnf + ", cjmc="
				+ cjmc + ", pp=" + pp + ", cx=" + cx + ", pl=" + pl + ", fdjxh=" + fdjxh + ", bsqlx=" + bsqlx + ", dws="
				+ dws + ", pfbz=" + pfbz + ", cldm=" + cldm + ", ssnf=" + ssnf + ", tcnf=" + tcnf + ", zdjg=" + zdjg
				+ ", ssyf=" + ssyf + ", scnf=" + scnf + ", nk=" + nk + ", cxi=" + cxi + ", xsmc=" + xsmc + ", cllx="
				+ cllx + ", jb=" + jb + ", csxs=" + csxs + ", cms=" + cms + ", zws=" + zws + ", gl=" + gl + ", rylx="
				+ rylx + ", bsqms=" + bsqms + ", rybh=" + rybh + ", qdfs=" + qdfs + ", fdjgs=" + fdjgs + ", levelid="
				+ levelid + "]";
	}
	
	
	
	
	
	

}
