package com.enation.app.b2b2c.core.model.reckoning;

import java.util.LinkedHashMap;
import java.util.Map;

public enum ReckoningTradeType {
	draw_money("提现",1),settle_accounts("账单结算",2),other("其他",3),service_buy("营销服务",4);
	private String name;
    private int index;
    // 构造方法
    private ReckoningTradeType(String name, int index) {
        this.name = name;
        this.index = index;
    }
    public static String getName(int index) {  
        for (ReckoningTradeType b : ReckoningTradeType.values()) {  
            if (b.getIndex() == index) {  
                return b.name;  
            }  
        }  
        return null;  
    } 
    
    public static Map getTradeTypeMap(){
    	Map map  = new LinkedHashMap();
    	for (ReckoningTradeType b : ReckoningTradeType.values()) {  
    		map.put(getName(b.index),b.index );
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
}
