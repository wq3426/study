package com.enation.app.b2b2c.communication.server.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.enation.app.b2b2c.communication.server.TCPProtocol;
import com.enation.app.b2b2c.communication.service.MessageHandler;
import com.enation.app.b2b2c.communication.service.MessageParse;
import com.enation.framework.util.FileUtil;

public class TCPProtocolImpl implements TCPProtocol {
	
	protected final Logger logger = Logger.getLogger(getClass());
	
	private int bufferSize;
	
	private MessageHandler messageHandler =  MessageHandler.getInstance();
	
	//保存正在链接Client的客户端socket
	private static Map <String , SocketChannel> clientSocketMap = new ConcurrentHashMap<String,SocketChannel>();
		
	
	  public TCPProtocolImpl(int bufferSize){
	    this.bufferSize=bufferSize;
	  }

	  public void handleAccept(SelectionKey key) throws IOException {
	    SocketChannel clientChannel=((ServerSocketChannel)key.channel()).accept();
	    byte[] activities = {0x4a,0x54,0x41,0x30,0x30,0x30,0x30,0x31,0x30,0x37,0x32,0x32,0x30,0x30,0x30,0x32,0x0,0x0,0x0,0x0,0x2,0x16,0x55,0x23,0x1,0x0,0x55,0x55,0x55,0x55,0x55,0x55,0x2,0xffffff8f,0x1c};
	    ByteBuffer buffer = ByteBuffer.wrap(activities);
	    clientChannel.write(buffer);
	    clientChannel.configureBlocking(false);
	    clientChannel.register(key.selector(), SelectionKey.OP_READ,ByteBuffer.allocate(bufferSize));
	  }

	 /* 测试代码
	  * public void handleRead(SelectionKey key)  {
		try{
		    // 获得与客户端通信的信道
		    SocketChannel clientChannel=(SocketChannel)key.channel();
		    
		    // 得到并清空缓冲区
		    ByteBuffer buffer=(ByteBuffer)key.attachment();
		    buffer.clear();
		    
		    // 读取信息获得读取的字节数
		    long bytesRead=clientChannel.read(buffer);
		    
		    if(bytesRead==-1){
		      // 没有读取到内容的情况
		      clientChannel.close();
		    }
		    else{
		      // 将缓冲区准备为数据传出状态
		      buffer.flip();
		      
		      // 将字节转化为为UTF-16的字符串   
		      String receivedString=Charset.forName("UTF-16").newDecoder().decode(buffer).toString();
		      // 控制台打印出来
		      System.out.println("接收到来自"+clientChannel.socket().getRemoteSocketAddress()+"的信息:"+receivedString);
		      String sn = "";
		      String dest_sn = "";
		      String message = "";
		      if(!StringUtil.isNull(receivedString)){//处理得到sn标记
		    	  JSONObject obj = JSONObject.fromObject(receivedString);
		    	  sn = obj.getString("sn");
		    	  dest_sn = obj.getString("dest_sn");
		    	  message = obj.getString("message");
		      }
		      if(StringUtil.isNull(dest_sn)){//判断当前sn为盈驾还是为OBD
		    	  System.out.println("这里为OBD上传数据");
		    	  //解析message报文数据更新到数据库
		    	  
		      }else{
		    	  System.out.println("这里为给OBD发指令");
		    	  //解析盈驾指令数据发送给OBD
		    	  SocketChannel ObdSocketChannel = clientSocketMap.get(dest_sn);
		    	  if(ObdSocketChannel!=null){//存在则发送报文	
		    		  handleWrite(ObdSocketChannel, message, dest_sn);
		    	  }else{//不存在则把map中的sn删除
		    		  if(clientSocketMap.containsKey(dest_sn)){
		    			  clientSocketMap.remove(dest_sn);
		    		  }
		    	  }
		      }
		      if(!clientSocketMap.containsKey(sn)){
		    	  clientSocketMap.put(sn,clientChannel);
		      }
		      clientSocketMap.put(sn,clientChannel);//每次读数据都存一次，避免sn是新链接
		      // 设置为下一次读取或是写入做准备
		      key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
		    }
		}catch(Exception e){
			e.printStackTrace();
		}
	  }*/
	  
	  
	  public void handleRead(SelectionKey key)  {
			try{
				
			    // 获得与客户端通信的信道
			    SocketChannel clientChannel=(SocketChannel)key.channel();
			    
			    // 得到并清空缓冲区
			    ByteBuffer buffer=(ByteBuffer)key.attachment();
			    buffer.clear();
			    
			    // 读取信息获得读取的字节数
			    long bytesRead=clientChannel.read(buffer);
			    
			    if(bytesRead==-1){
			      // 没有读取到内容的情况
			      clientChannel.close();
			    }
			    else{
			      // 将缓冲区准备为数据传出状态
			      buffer.flip();
			      /*// 将字节转化为为UTF-16的字符串   
			      String receivedString=Charset.forName("UTF-16").newDecoder().decode(buffer).toString();
			      // 控制台打印出来
			      System.out.println("接收到来自"+clientChannel.socket().getRemoteSocketAddress()+"的信息:"+receivedString);
			     */
			      //调用解析数据接口传入buffer参数，返回数据报文，里面逻辑判断是否为obd,则返回给OBD回复的信息,
			      //如果为盈架,将命令写给OBD
			      
			      MessageParse messageParse = new MessageParse();
			      byte[] bytes = new byte[buffer.remaining()];
			      buffer.get(bytes, 0, bytes.length);
			      String inMessage = "server <--";
			      for(int i = 0 ; i < bytes.length;i++){
			    	  inMessage += "0x"+Integer.toHexString(bytes[i])+",";
			      }
			      System.out.println(inMessage);
			      System.out.println("serverTime = " + System.currentTimeMillis());
			      //将1b1d 转化为1b,1b1e 转化为1c
			      String hex = bytesToHexString(bytes);
			      String filePath = "/home/obdMessage/obdlog_"+new SimpleDateFormat("yyyyMMdd").format(new Date())+".txt";
			      FileUtil.appendWrite(filePath, inMessage);
			      FileUtil.appendWrite(filePath, "serverTime = " + System.currentTimeMillis());
			      
			      hex = hex.replaceAll("1b1d", "1b").replaceAll("1b1e", "1c");
			      bytes = hexStringToBytes(hex);
			      String deviceId =  messageParse.getDeviceId(bytes);
			      System.out.println("connection deviceId is : " + deviceId);
			      FileUtil.appendWrite(filePath, "connection deviceId is : " + deviceId);
			      if(!deviceId.equals("0000000000000000")){//判断当前dev_id为OBD
			    	  if(!"null".equals(deviceId)){
			    		  //数据处理
				    	  messageHandler.handler(bytes);
				    	  //应答报文
				    	  Byte[] responseBytes = messageParse.makeServerToObdAck(messageParse.getFId(bytes),(byte)0x30);
				    	  handleWrite(clientChannel,responseBytes);
				    	  //处理OBD报文逻辑$
			    	  }
			      }else{
			    	  //yingjia发送报文,解析报文数据,得到OBDDevice_id
			    	  String obdDeviceId = getObdDevideId(bytes);
//			    	  System.out.println(obdDeviceId);
			    	  //解析盈驾指令数据发送给OBD
			    	  if(!"null".equals(obdDeviceId)){
				    	  SocketChannel obdSocketChannel = clientSocketMap.get(obdDeviceId);
				    	  if(obdSocketChannel!=null){//存在则发送报文
				    			messageHandler.sendToObdActivities(this,obdSocketChannel,obdDeviceId,bytes);
				    	  }else{//不存在则把map中的sn删除
				    		  System.out.println("the obdDeviceId no exits!");
				    		  if(clientSocketMap.containsKey(deviceId)){
				    			  clientSocketMap.remove(deviceId);
				    		  }
				    	  }
			    	  }
			      }
			      if(!deviceId.equals("null") && deviceId.length() == 16 && deviceId.contains("JTA")){
			    	  clientSocketMap.put(deviceId,clientChannel );
			      }
			      // 设置为下一次读取或是写入做准备
			      key.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			    }
			}catch(Exception e){
				e.printStackTrace();
			}
	  
	  }
	  
	  
	  public void handleWrite(SelectionKey key) throws IOException {
	  }
	  
	  /** @description 发送数据给obd
	 * @date 2016年9月24日 下午6:07:28
	 * @param socketChannel
	 * @param msg
	 * @throws IOException
	 * @return void
	 */
	  public void handleWrite(SocketChannel socketChannel,String msg,String dest_sn) throws IOException {
		  try{
			  ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes("UTF-16"));
			  int writeStatus = socketChannel.write(buffer);
		  }catch(Exception e){//如果发送报文失败，有可能channel未连接
			  e.printStackTrace();
			  socketChannel.close();
			  clientSocketMap.remove(dest_sn);//移除
		  }
	  }
	  
	  /** @description 发送报文给OBD
	 * @date 2016年9月27日 下午7:55:35
	 * @param socketChannel
	 * @param buffer
	 * @throws IOException
	 * @return void
	 */
	public void handleWrite(SocketChannel socketChannel,byte[] message) throws IOException {
		  try{
			  String str ="server--->";
			  for(byte b : message){
				 str +="0x"+Integer.toHexString(b)+",";
			  }
			  System.out.println(str);
			  ByteBuffer buffer = ByteBuffer.wrap(message);
			  int writeStatus = socketChannel.write(buffer);
			  System.out.println("write_Status -->" +writeStatus);
		  }catch(Exception e){//如果发送报文失败，有可能channel未连接
			  e.printStackTrace();
			  socketChannel.close();
			  clientSocketMap.remove("");//移除
		  }
	}
	
	
	  /** @description 发送报文给OBD
		 * @date 2016年9月27日 下午7:55:35
		 * @param socketChannel
		 * @param buffer
		 * @throws IOException
		 * @return void
		 */
		public void handleWrite(SocketChannel socketChannel,Byte[] message) throws IOException {
			  try{
				  byte[] messageBytes = new byte[message.length];
				  String outMessage = "server --> " ;
				  for(int i = 0 ; i < message.length ; i++){
					  messageBytes[i] = message[i];
					  outMessage += "0x"+Integer.toHexString(message[i])+",";
				  }
				  System.out.println(outMessage);
				  ByteBuffer buffer = ByteBuffer.wrap(messageBytes);
				  int writeStatus = socketChannel.write(buffer);
				  System.out.println("write_Status -->" +writeStatus);
			  }catch(Exception e){
				  e.printStackTrace();
			  }
		}
		
		/** @description 获取yingjia给OBD发送的OBDdevideId
		 * @date 2016年9月29日 上午10:51:59
		 * @param str
		 * @return
		 * @return String
		 */
		public String getObdDevideId(byte[] str){
			  StringBuilder sb = new StringBuilder();
			  if(str.length > 25){
				  for( int i=16; i<=31; i++){
					  sb.append((char)str[i]);
				  }
			  }else{
				  sb.append("null");
			  }
			  return sb.toString();
		}
		
		/** 
		 * Convert hex string to byte[] 
		 * @param hexString the hex string 
		 * @return byte[] 
		 */  
		public static String bytesToHexString(byte[] src){  
		    StringBuilder stringBuilder = new StringBuilder("");  
		    if (src == null || src.length <= 0) {  
		        return null;  
		    }  
		    for (int i = 0; i < src.length; i++) {  
		        int v = src[i] & 0xFF;  
		        String hv = Integer.toHexString(v);  
		        if (hv.length() < 2) {  
		            stringBuilder.append(0);  
		        }  
		        stringBuilder.append(hv);  
		    }  
		    return stringBuilder.toString();  
		}  
		
		/** 
		 * Convert hex string to byte[] 
		 * @param hexString the hex string 
		 * @return byte[] 
		 */  
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
