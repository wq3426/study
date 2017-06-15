package com.enation.app.b2b2c.communication.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class TCPClient {

      // 信道选择器
	  private Selector selector;
	  // 与服务器通信的信道
	  private SocketChannel socketChannel;
	  // 要连接的服务器Ip地址
	  private String hostIp;
		
	  // 要连接的远程服务器在监听的端口
	  private int hostListenningPort;

	  private static volatile TCPClient tcpClient = null;
	  
	  public static TCPClient getInstance(){
		  if(tcpClient == null){
			  synchronized (TCPClient.class) {
				if(tcpClient == null){
					tcpClient = new TCPClient();
				}
			  }
		  }
		  return tcpClient;
	  }
	  

	  /**
	   * 初始化
	   * @throws IOException
	   */
	  public void initialize(String hostIp,int hostListenningPort) {
		try{
			// 打开监听信道并设置为非阻塞模式
			socketChannel=SocketChannel.open(new InetSocketAddress(hostIp, hostListenningPort));
			System.out.println("message : the  yingjia openSocketChannel success");
		}catch(IOException e){
			System.out.println("error : the  yingjia socketChannel IOException");
		}
	    
	/*    socketChannel.configureBlocking(false);
	    // 打开并注册选择器到信道
	    selector = Selector.open();
	    socketChannel.register(selector, SelectionKey.OP_READ);
	    
	    new TCPClientReadThread(selector);*/
	  }
	  
	  /**
	   * 发送字符串到服务器
	   * @param message
	   * @throws IOException
	   */
	  public void sendMsg(String message) throws IOException{
		  ByteBuffer writeBuffer=ByteBuffer.wrap(message.getBytes("UTF-16"));
		  socketChannel.write(writeBuffer);
	  }
	  public void sendMsg(byte[] message) throws IOException{
		  ByteBuffer writeBuffer=ByteBuffer.wrap(message);
		  socketChannel.write(writeBuffer);
	  }
	  
	  
    public Selector getSelector() {
		return selector;
	}

	public void setSelector(Selector selector) {
		this.selector = selector;
	}

	public SocketChannel getSocketChannel() {
		return socketChannel;
	}

	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public String getHostIp() {
		return hostIp;
	}

	public void setHostIp(String hostIp) {
		this.hostIp = hostIp;
	}

	public int getHostListenningPort() {
		return hostListenningPort;
	}

	public void setHostListenningPort(int hostListenningPort) {
		this.hostListenningPort = hostListenningPort;
	}

	public static void main(String[] args) throws IOException{
			byte[] message = {0x4a,0x54,0x41,0x30,0x30,0x30,0x30,0x31,0x31,0x31,0x38,0x34,0x30,0x30,0x30,0x32,0x12,0x01,(byte)0xe2,(byte)0xfe,0x63,0x02,0x16,0x55,0x2e,0x18,0x31,0x41,0x31,0x4a,0x43,0x35,0x34,0x34,0x34,0x52,0x37,0x32,0x35,0x32,0x33,0x36,0x37,0x31,0x30,0x34,0x33,0x31,0x30,0x00,0x04,(byte)0xec,0x1c};
			/*String msg = "1";
			while(msg!="exit"){
				Scanner scanner = new Scanner(System.in);
				System.out.println("输入你要发的数据包");
				msg =scanner.nextLine();
				if(msg.equals("exit")){
					client.socketChannel.close();
				}
				client.sendMsg("{\"sn\":\"111\",\"message\":\"hello小森\",\"dest_sn\":\"222\"}");
//				client.sendMsg("{\"sn\":\"222\",\"message\":\""+msg+"\",\"dest_sn\":\"111\"}");
			}*/
//			client.sendMsg("{\"sn\":\"111\",\"message\":\"hello小森\",\"dest_sn\":\"222\"}");
	  }
}
	
	
	
