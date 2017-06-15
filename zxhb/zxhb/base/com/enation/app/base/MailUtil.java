package com.enation.app.base;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.james.mime4j.message.BodyPart;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.enation.framework.jms.EmailModel;

public class MailUtil {

    public static String sendEmail(Integer StoreId, String goodsCatName, String newBrand){
    	 JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();  
    	  
         // 设定mail server  
         senderImpl.setHost("smtp.ym.163.com");  
   
         // 建立邮件消息,发送简单邮件和html邮件的区别  
         MimeMessage mailMessage = senderImpl.createMimeMessage();  
         MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage);  
   
         // 设置收件人，寄件人  
         try {
			messageHelper.setTo("yunying@trans-it.cn");
			messageHelper.setFrom("yunbingsen@trans-it.cn");  
			messageHelper.setSubject("测试HTML邮件！");  
			//1：发送简单文本
			mailMessage.setSubject("新增品牌邮件发送");  
			mailMessage.setText("店铺编号为"+StoreId+"的店铺，增加商品分类名："+goodsCatName+"的商品分类，申请新增品牌："+newBrand);  
			  //2： true 表示启动HTML格式的邮件  
	        // messageHelper.setText(  
	                        // "<html><head></head><body><h1>hello!!spring html Mail</h1></body></html>",  
	                        // true);  
	         //3：邮件中嵌套图片
	        /*messageHelper.setText(  
	                 "<html><head></head><body><h1>hello!!spring image html mail</h1>"  
	                         + "<img src=\"cid:aaa\"/></body></html>", true);  
	         FileSystemResource img = new FileSystemResource(new File("g:/123.jpg"));  
	         messageHelper.addInline("aaa", img);  */
	         //4： true 表示启动HTML格式的邮件  
	         /*messageHelper.setText(  
	                 "<html><head></head><body><h1>你好：附件中有学习资料！</h1></body></html>",  
	                 true);  
	         FileSystemResource file = new FileSystemResource(  
	                 new File("g:/test.rar"));  
	         messageHelper.addAttachment("test.rar", file);  */
	         
	         senderImpl.setUsername("yunbingsen@trans-it.cn"); // 根据自己的情况,设置username  
	         senderImpl.setPassword("xiaosen0416"); // 根据自己的情况, 设置password  
	         Properties prop = new Properties();  
	         prop.put("mail.smtp.auth", "false"); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确  
	         prop.put("mail.smtp.timeout", "25000");  
	         senderImpl.setJavaMailProperties(prop);  
	         // 发送邮件  
	         senderImpl.send(mailMessage);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "1";
		}
		return "0";  
    }

	public static String sendEmailForFindPassword(EmailModel emailModel) {
		 JavaMailSenderImpl senderImpl = new JavaMailSenderImpl();  
		 StringBuilder builder = new StringBuilder(); 
		 builder.append("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" </head><body>"); 
		 builder.append("<table width='832' border='0' align='left' cellpadding='0' cellspacing='0' style='margin-left:25px;font-size:12px;'>");
		 builder.append("<tr><td height='60' style='border-bottom:1px solid #c8c8c8;'><a href='' target='_blank'><image src='"+emailModel.getData().get("logo")+"' border='0' /></a></td></tr>");
		 builder.append("<tr><td height='10'></td></tr>");
		 builder.append("<tr><td height='30' style='line-height:30px;'>亲爱的"+emailModel.getData().get("username")+":</td></tr>");
		 builder.append("<tr><td height='30' align='left' style='line-height:30px;'>这是一封【"+emailModel.getData().get("sitename")+"】发送的验证邮件，请您在1个小时内重置密码。如果您记得您在【"+emailModel.getData().get("sitename")+"】的密码，请忽略这封邮件。</td></tr>");
		 builder.append("<tr><td height='15' align='left' style='line-height:30px;'></td></tr>");
		 builder.append("<tr><td height='40' align='left' style='line-height:30px; font-size:14px; font-weight:bold;'><a href='"+emailModel.getData().get("checkurl")+"' style='color:#da6d00; text-decoration:underline;'>点此修改您的密码</a></td></tr>");
		 builder.append("<tr><td height='14' align='left' style='line-height:14px; color:#474747;'>如果您无法点击此链接，请将下面的链接地址复制到您的浏览器地址栏中，打开页面即可</td></tr>");
		 builder.append("<tr><td height='24' align='left' style='line-height:14px; color:#7d7d7d;'><a href='"+emailModel.getData().get("checkurl")+"' target='_blank' style='color:#da6d00;'>"+emailModel.getData().get("checkurl")+"</a></td></tr>");
		 builder.append("<tr><td height='30' align='left' style='line-height:30px;'>本邮件为【"+emailModel.getData().get("sitename")+"】系统发出，请勿直接回复。</td></tr>");
		 builder.append("<tr><td height='30' align='left' style='line-height:30px; font-size:14px; font-weight:bold; color:#3d3d3d;'>感谢您的信任与支持，祝您购物愉快！</td></tr>");
		 builder.append("</table>");
		 builder.append("</body></html>"); 
         // 设定mail server  
         senderImpl.setHost("smtp.ym.163.com");  
   
         // 建立邮件消息,发送简单邮件和html邮件的区别  
         MimeMessage mailMessage = senderImpl.createMimeMessage();  
         MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage);  
         // 设置收件人，寄件人  
         try {
			messageHelper.setTo(emailModel.getEmail());
			InternetAddress Addressia=new InternetAddress("yunying@trans-it.cn","盈驾商城","UTF-8");
			messageHelper.setFrom(Addressia);  
			//1：发送简单文本
			mailMessage.setSubject(emailModel.getTitle());  
			//mailMessage.setText(builder.toString());  
			// 后面的bodypart将加入到此处创建的multipart中  
			Multipart mp = new MimeMultipart();  
			MimeBodyPart mbp = new MimeBodyPart();  
			mbp.setContent(builder.toString(), "text/html;charset=UTF-8");// 将信件内容加入到BodyPart对象中  
			mp.addBodyPart(mbp); // 将含有信件内容的BodyPart加入到MimeMulitipart对象中  
			  
			// multipart加入到信件  
			mailMessage.setContent(mp);  
	        senderImpl.setUsername("yunying@trans-it.cn"); // 根据自己的情况,设置username  
	        senderImpl.setPassword("yunying"); // 根据自己的情况, 设置password  
	        Properties prop = new Properties();  
	        prop.put("mail.smtp.auth", "false"); // 将这个参数设为true，让服务器进行认证,认证用户名和密码是否正确  
	        prop.put("mail.smtp.timeout", "25000");  
	        senderImpl.setJavaMailProperties(prop);  
	         // 发送邮件  
	        senderImpl.send(mailMessage);
		} catch (MessagingException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "1";
		}
		return "0";  
	}
}

