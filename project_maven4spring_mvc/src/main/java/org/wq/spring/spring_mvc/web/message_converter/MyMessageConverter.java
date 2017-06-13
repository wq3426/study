package org.wq.spring.spring_mvc.web.message_converter;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;
import org.wq.spring.spring_mvc.domain.DemoObj;

/**
 * 1.自定义的HttpMessageConverter  继承AbstractHttpMessageConverter类来实现自定义的HttpMessageConverter
 * 
 * @author wq3426
 *
 */
public class MyMessageConverter extends AbstractHttpMessageConverter<DemoObj> {// 1

	/**
	 * 2.新建自定义的媒体类型 application/x-wq
	 */
	public MyMessageConverter() {
		super(new MediaType("application", "x-wq", Charset.forName("UTF-8")));// 2
	}

	/**
	 * 3.表明本HttpMessageConverter只处理DemoObj这个类
	 */
	@Override
	protected boolean supports(Class<?> clazz) {
		return DemoObj.class.isAssignableFrom(clazz);// 3
	}

	/**
	 * 4.重写readInternal()方法，处理请求的数据，此处处理由"-"隔开的数据并将其转换为DemoObj对象
	 */
	@Override
	protected DemoObj readInternal(Class<? extends DemoObj> clazz, HttpInputMessage inputMessage) 
			throws IOException, HttpMessageNotReadableException {// 4
		String temp = StreamUtils.copyToString(inputMessage.getBody(), Charset.forName("UTF-8"));
		String[] tempArr = temp.split("-");
		return new DemoObj(new Long(tempArr[0]), tempArr[1]);
	}

	/**
	 * 5.处理如何输出数据到response
	 */
	@Override
	protected void writeInternal(DemoObj obj, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {// 5
		String out = "hello:" + obj.getId() + "-" + obj.getName();
		outputMessage.getBody().write(out.getBytes());
	}

}
