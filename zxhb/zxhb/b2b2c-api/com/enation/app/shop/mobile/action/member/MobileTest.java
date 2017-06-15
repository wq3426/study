package com.enation.app.shop.mobile.action.member;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.enation.app.b2b2c.core.action.api.member.MemberApiAction;
import com.enation.eop.processor.MobileContent;
import com.enation.eop.processor.MobileMessageHttpSend;
import com.enation.framework.context.webcontext.ThreadContextHolder;

public class MobileTest {
	public static int num;
	public static int MathRodm(){
		num=(int)((Math.random()*9+1)*100000);
		return num;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		MemberApiAction aa= new MemberApiAction();
		aa.sendCode();
		//以下为所需的参数，测试时请修改,中文请先转为16进制再发送
		String strReg = "101100-WEB-HUAX-175580";   //注册号（由华兴软通提供）
        String strPwd = "ZUAAKFFT";                 //密码（由华兴软通提供）
        String strSourceAdd = "";                   //子通道号，可为空（预留参数一般为空）
        String strTim = MobileMessageHttpSend.paraTo16("2016-4-25 11:03:00"); //定时发送时间
        String strPhone = "18010496419";//手机号码，多个手机号用半角逗号分开，最多1000个
        String strContent = MobileMessageHttpSend.paraTo16("您好!感谢您注册盈驾,验证码为"+MathRodm()+
        									 "请勿转发。"+"【中安信博】");       //短信内容
        String strUname = MobileMessageHttpSend.paraTo16("华兴"); //用户名，不可为空
        String strMobile = "18010496419";            //手机号，不可为空
        String strRegPhone = "01065688262";             //座机，不可为空
        String strFax = "01065685318";               //传真，不可为空
        String strEmail = "hxrt@stongnet.com";       //电子邮件，不可为空
        String strPostcode = "100080";               //邮编，不可为空
        String strCompany = MobileMessageHttpSend.paraTo16("华兴软通");    //公司名称，不可为空
        String strAddress = MobileMessageHttpSend.paraTo16("天阳ja");//公司地址，不可为空
        
        String strNewPwd = "BBBBBBBB";
        
        //以下参数为服务器URL,以及发到服务器的参数，不用修改
        String strRegUrl = "http://www.stongnet.com/sdkhttp/reg.aspx";
        String strBalanceUrl = "http://www.stongnet.com/sdkhttp/getbalance.aspx";
        String strSmsUrl = "http://www.stongnet.com/sdkhttp/sendsms.aspx";
        String strSchSmsUrl = "http://www.stongnet.com/sdkhttp/sendschsms.aspx";
        String strStatusUrl = "http://www.stongnet.com/sdkhttp/getmtreport.aspx";
        String strUpPwdUrl = "http://www.stongnet.com/sdkhttp/uptpwd.aspx";
        String strRegParam = "reg=" + strReg + "&pwd=" + strPwd + "&uname=" + strUname + "&mobile=" + strMobile + "&phone=" + strRegPhone + "&fax=" + strFax + 
                             "&email=" + strEmail + "&postcode=" + strPostcode + 
                             "&company=" + strCompany + "&address=" + strAddress;
        String strBalanceParam = "reg=" + strReg + "&pwd=" + strPwd;
        String strSmsParam = "reg=" + strReg + "&pwd=" + strPwd + "&sourceadd=" + strSourceAdd + "&phone=" + strPhone + "&content=" + strContent;;
        String strSchSmsParam = "reg=" + strReg + "&pwd=" + strPwd + "&sourceadd=" + strSourceAdd + "&tim=" + strTim + "&phone=" + strPhone + "&content=" + strContent;;
        String strStatusParam = "reg=" + strReg + "&pwd=" + strPwd;
        String strUpPwdParam = "reg=" + strReg + "&pwd=" + strPwd + "&newpwd=" + strNewPwd;
        
        String strRes  = new String();
        
        //以下为HTTP接口主要方法，测试时请打开对应注释进行测试
        //注册
        //strRes = HttpSend.postSend(strRegUrl, strRegParam);
        
        //查询余额
       // strRes = HttpSend.postSend(strBalanceUrl, strBalanceParam);
        
        //发送短信
        strRes = MobileMessageHttpSend.postSend(strSmsUrl, strSmsParam);
        
        //定时短信
        //strRes = HttpSend.postSend(strSchSmsUrl, strSchSmsParam);
        
        //状态报告
        //strRes = HttpSend.postSend(strStatusUrl, strStatusParam);
        
        //修改密码
       // strRes = HttpSend.postSend(strUpPwdUrl, strUpPwdParam);
       //System.out.println(MathRodm());
//       System.out.println(strRes);
       //System.exit(0);
	}
	
	@Test
	public void testSendMessage() {
		String mobile = "18010496419";
		String mobileCode=""+(int)((Math.random()*9+1)*100000);
		System.out.println(mobileCode);
		try {
			String content =  MobileMessageHttpSend.paraTo16("您好!感谢您注册盈驾,验证码为"+mobileCode+"请勿转发。"+"【中安信博】");
			String strSmsParam = "reg=" + MobileContent.strReg + "&pwd=" + MobileContent.strPwd + "&sourceadd=" + MobileContent.strSourceAdd + "&phone=" + mobile + "&content=" + content;
			MobileMessageHttpSend.postSend(MobileContent.strSmsUrl,strSmsParam);
			System.out.println("发送成功");
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}       //短信内容
	}

}
