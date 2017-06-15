package com.enation.app.b2b2c.core.model.reckoning;

import java.util.LinkedHashMap;
import java.util.Map;

import com.hp.hpl.sparta.xpath.ThisNodeTest;
import com.oreilly.servlet.multipart.MacBinaryDecoderOutputStream;

public enum ReckoningTradeStatus {
	no_settle_accounts("未结算", 1), yet_settle_accounts("已结算", 2), disposing("处理中", 3), yet_dispose("已到账", 4);
	private String name;
	private int index;

	// 构造方法
	private ReckoningTradeStatus(String name, int index) {
		this.name = name;
		this.index = index;
	}

	public static String getName(int index) {
		for (ReckoningTradeStatus b : ReckoningTradeStatus.values()) {
			if (b.getIndex() == index) {
				return b.name;
			}
		}
		return null;
	}

	public static Map getTradeStatusMap() {
		Map map = new LinkedHashMap();
		for (ReckoningTradeStatus b : ReckoningTradeStatus.values()) {
			map.put(getName(b.index), b.index);
		}
		return map;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public static Map getApplyStatus() {
		Map map = new  LinkedHashMap();
		map.put(disposing.name,disposing.index);
		map.put(yet_dispose.name,yet_dispose.index);
		return map;
	}

}
