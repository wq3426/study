package com.enation.app.b2b2c.core.pluin.goods;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.enation.framework.plugin.AutoRegisterPluginsBundle;
import com.enation.framework.plugin.IPlugin;

/**
 * 店铺商品插件桩
 * @author fenlongli
 * @date 2015-6-10 上午10:01:58
 */
@Component
public class StoreGoodsPluginBundle extends AutoRegisterPluginsBundle{

	@Override
	public String getName() {
		
		return "店铺商品插件桩";
	}
	/**
	 * 获取店铺商品的选项卡
	 * @return
	 */
	public List getTabList(){
		List<IPlugin> plugins = this.getPlugins();
		
		List list=new ArrayList(); 
		if (plugins != null) {
			for (IPlugin plugin : plugins) {
				if(plugin instanceof IStoreGoodsTabShowEvent ){
					IStoreGoodsTabShowEvent event  = (IStoreGoodsTabShowEvent)plugin;
					list.add( event.getTabInfo());
				}
			}
		}
		return list;
	}

}
