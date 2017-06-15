package com.enation.test.communication;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.enation.app.b2b2c.communication.service.MessageParse;
import com.enation.test.communication.client.TCPClient;


public class FileReadTest {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		File file = new File("e:\\20161025-1.txt");//Text文件
		String server = "server <--0x4a,0x54,0x41";
		BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
		String s = null;
		int index = 0;
		while((s = br.readLine())!= null){//使用readLine方法，一次读一行
			index++;
			if (s.contains(server)){
				String str = s.replaceAll("server <--", "").replaceAll("0x1b,0x1d", "0x1b").replaceAll("0x1b,0x1e","0x1c");
				String[] strList = str.split(",");
				byte[] byt = new byte[strList.length];
				for(int i = 0; i< strList.length;i++){
					byt[i] = (byte)Integer.parseInt(strList[i].replaceAll("ffffff", "").replaceAll("0x", ""),16);
				}
				MessageParse pack = new MessageParse();
				pack.handlerMessage(byt);
			}
		}
	}
	
	public static void fileRead(TCPClient client) throws IOException, InterruptedException{
		File file = new File("e:\\20161025-3.txt");//Text文件
		String server = "server <--0x4a,0x54,0x41";
		BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
		String s = null;
		int index = 0;
		long time = 0l;
		while((s = br.readLine())!= null){//使用readLine方法，一次读一行
			index++;
			if (s.contains(server)){
				String str = s.replaceAll("server <--", "");
				String[] strList = str.split(",");
				byte[] byt = new byte[strList.length];
				for(int i = 0; i< strList.length;i++){
					byt[i] = (byte)Integer.parseInt(strList[i].replaceAll("ffffff", "").replaceAll("0x", ""),16);
				}
				System.out.println("MessageParse.getTime(byt)="+MessageParse.getTime(byt));
				System.out.println("MessageParse.getTime(byt)-time="+(MessageParse.getTime(byt)-time));
				time = MessageParse.getTime(byt);
				client.sendMsg(byt);
				Thread.sleep(200);
			}
		}
	}
	
	public static byte[] hexStringToBytes(String hexString) {  
	    if (hexString == null || hexString.equals("")) {  
	        return null;  
	    }  
	    hexString = hexString.toUpperCase();  
	    int length = hexString.length() / 2;  
	    char[] hexChars = hexString.toCharArray();  
	    byte[] d = new byte[length];  
	    for (int i = 0; i < length; i++) {  
	        int pos = i * 2;  
	        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));  
	    }  
	    return d;  
	}  
	 private static byte charToByte(char c) {  
		    return (byte) "0123456789ABCDEF".indexOf(c);  
	}  
}
