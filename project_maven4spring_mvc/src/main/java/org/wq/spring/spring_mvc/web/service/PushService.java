package org.wq.spring.spring_mvc.web.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

@Service
public class PushService {
	private DeferredResult<String> deferredResult;// 1 在PushService中产生DeferredResult给控制器使用
	
	public DeferredResult<String> getAsyncUpdate(){// 2
		deferredResult = new DeferredResult<String>();
		return deferredResult;
	}
	
	@Scheduled(fixedDelay = 5000)// 3 定时更新deferredResult，每5s更新一次
	public void refresh(){
		if(deferredResult != null){
			deferredResult.setResult(new Long(System.currentTimeMillis()).toString());
		}
	}
}
