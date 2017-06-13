package com.wq.spring_boot.domain;

import java.nio.charset.Charset;

import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * http的编码配置，通过类型安全配置实现
 * @author wq3426
 *
 */
@ConfigurationProperties(prefix="spring.http.encoding")
public class HttpEncodingProperties {
	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	
	private Charset charset = DEFAULT_CHARSET;
	
	private boolean force = true;

	public Charset getCharset() {
		return charset;
	}

	public void setCharset(Charset charset) {
		this.charset = charset;
	}

	public boolean isForce() {
		return force;
	}

	public void setForce(boolean force) {
		this.force = force;
	}
}
