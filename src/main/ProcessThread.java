
import java.util.ArrayList;
import java.util.HashMap;

import io.netty.channel.ChannelHandlerContext;

public class ProcessThread {
	
	public static HashMap<String, ChannelHandlerContext> clients;

	static DB Db;
	public static String strMsg = "";
	public static String _del=" ";
	public static String _endSendDel="";
	static int n_txtName = 1;
	static boolean isPrint = true;
	public ProcessThread( ){
		Db = new DB();
		
	}
	
	protected synchronized void SendMsg(ChannelHandlerContext ctx, String msg) {
		// ByteBuf buffer = Unpooled.buffer(1023);
		// byte[] str = msg.getBytes();
		System.out.println("보내는 메시지: " + msg);
		ctx.write(msg+"\n");
		ctx.flush();
		// buffer.writeBytes(msg.getBytes());
		// ctx.writeAndFlush(buffer);
	}
	public synchronized static void SendMessage(ChannelHandlerContext sc,String message)
	{
		System.out.println("보내는 메시지: " + message);
		sc.write(message+"\n");
		sc.flush();
	}
	
	public synchronized static void getBookSummary(String Msg[], ChannelHandlerContext sc){
		String query = Db.getBookSummary(Msg, sc);
		SendMessage(sc, query);
	}
	public synchronized static void selectBookRankingAll(String Msg[], ChannelHandlerContext sc) throws InterruptedException{
		ArrayList<String> query = Db.selectbookrankingall(Msg, sc);
		for(int i=0; i<query.size(); i++){
			SendMessage(sc, query.get(i));
			Thread.sleep(150);
			
		}
		Thread.sleep(200);
		SendMessage(sc, "end");
	}
	
	public synchronized static void selectReadListById(String Msg[], ChannelHandlerContext sc) throws InterruptedException{
		ArrayList<String> query = Db.selectReadListById(Msg, sc);
		for(int i=0; i<query.size(); i++){
			SendMessage(sc, query.get(i));
			Thread.sleep(150);
		}
		Thread.sleep(200);
		SendMessage(sc, "end"); 
	}
	
	public synchronized static void selectRecommendListById(String Msg[], ChannelHandlerContext sc) throws InterruptedException{
		ArrayList<String> query = Db.selectRecommendList(Msg, sc);
		for(int i=0; i<query.size(); i++){
			SendMessage(sc, query.get(i));
			Thread.sleep(150);
		}
		Thread.sleep(200);
		SendMessage(sc, "end");
	}
	
	public synchronized static void LogIn(String Msg[], ChannelHandlerContext sc){
		String query = Db.LogIn(Msg, sc);
		SendMessage(sc, query);
	}
	
	public synchronized static void Register(String Msg[], ChannelHandlerContext sc){
		String query = Db.Register(Msg, sc);
		SendMessage(sc, query);
	}
	public synchronized static void CheckAllBook(String Msg[], ChannelHandlerContext sc) throws InterruptedException{
		ArrayList<String> query = Db.CheckAllBook(Msg, sc);
		for(int i=0; i<query.size(); i++){
			SendMessage(sc, query.get(i));
			Thread.sleep(150);
		}
		Thread.sleep(200);
		SendMessage(sc, "end");
	}
	
	public synchronized static void EachRankAll(String Msg[], ChannelHandlerContext sc) throws InterruptedException{
		ArrayList<String> query = Db.selectRecommendList(Msg, sc);
		for(int i=0; i<query.size(); i++){
			SendMessage(sc, query.get(i));
			Thread.sleep(150);
		}
		Thread.sleep(200);
		SendMessage(sc, "end");
	}
	public synchronized static void CountMember(String Msg[], ChannelHandlerContext sc){
		String query = Db.CountMember(Msg, sc);
		SendMessage(sc, query);
	}
	public synchronized static void readCheck(String Msg[], ChannelHandlerContext sc){
		String query = Db.readCheck(Msg, sc);
	}
}
