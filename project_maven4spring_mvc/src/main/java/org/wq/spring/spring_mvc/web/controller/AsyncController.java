package org.wq.spring.spring_mvc.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;
import org.wq.spring.spring_mvc.web.service.PushService;

/**
 * servlet3.0+ 异步(async)支持 (跨浏览器的)
 * 该异步任务的实现是通过控制器从另外一个线程返回一个DeferredResult，这里的DeferredResult是从pushService中获取的
 * 
 * @author wq3426
 *
 */
@Controller
public class AsyncController {
	@Autowired // 1 pushService中设置定时任务，定时更新DeferredResult
	PushService pushService;

	@RequestMapping("/defer")
	public @ResponseBody DeferredResult<String> deferredCall(){// 2 返回给客户端DeferredResult
		return pushService.getAsyncUpdate();
	}
}
