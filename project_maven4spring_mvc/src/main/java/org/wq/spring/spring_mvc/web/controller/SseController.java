package org.wq.spring.spring_mvc.web.controller;

import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 基于SSE(Server Send Event)实现的服务器端推送技术 (需要新式浏览器的支持)
 * 
 * 【早期的解决方案】：使用Ajax向服务器轮询，但这种方式频率不好控制，所以大大增加了服务端的压力。
 * 
 * 【服务器端推送技术】：当客户端向服务端发送请求，服务端会抓住这个请求不放，等有数据更新的时候才返回给客户端，当客户端接收到消息后，
 *                再向服务端发送请求，周而复始。
 *                这种方式的好处是减少了服务器接收的请求数量，大大减少了服务器的压力。
 * 【WebSocket】：另一种解决方案：WebSocket 双向通信的技术
 * @author wq3426
 *
 */
@Controller
public class SseController {
	
	/**
	 * 1."text/event-stream" 该媒体类型是服务器端SSE的支持，本例演示每隔5秒向浏览器推送随机消息
	 * @return
	 */
	@RequestMapping(value="/push", produces="text/event-stream")// 1
	public @ResponseBody String push(){
		Random r = new Random();
		
		int i = r.nextInt();
		System.out.println("i-----"+i);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "data:Testing 1,2,3" + i + "\n\n";
	}
}
