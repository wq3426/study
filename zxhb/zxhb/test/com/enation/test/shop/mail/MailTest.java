package com.enation.test.shop.mail;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.junit.Test;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.enation.framework.test.SpringTestSupport;

public class MailTest extends SpringTestSupport {

	@Test
	public static void test() {
		JavaMailSender mailSender = SpringTestSupport.getBean("mailSender");
		MimeMessage message = mailSender.createMimeMessage();
		try {
			// use the true flag to indicate you need a multipart message
			MimeMessageHelper helper = new MimeMessageHelper(message, true,"UTF-8");
			helper.setFrom("yunbingsen521@126.com");
			helper.setSubject("test1111111");
			helper.setTo("kingapex@126.com");
			helper.setText("<html><body><span style='color:red'>test</span></body></html>", true);
			mailSender.send(message);
		} catch (MailException ex) {
			// simply log it and go on...
			System.err.println(ex.getMessage());
		} catch (MessagingException e) {
			System.err.println(e.getMessage());
		}
	}
}
